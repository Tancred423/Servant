/*
 * Copyright 2016-2018 John Grosh (jagrosh) & Kaidan Gustave (TheMonitorLizard)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Modified by Tancred (https://github.com/Tancred423)
// Modification: onMessageReceived -> Better check for guild specific prefixes.
// Also better help message via embeds.
package zJdaUtilsLib.com.jagrosh.jdautilities.command.impl;

import commands.owner.blacklist.Blacklist;
import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.Console;
import utilities.Constants;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.*;
import zJdaUtilsLib.com.jagrosh.jdautilities.commons.utils.FixedSizeCache;
import zJdaUtilsLib.com.jagrosh.jdautilities.commons.utils.SafeIdUtil;

import java.awt.*;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommandClientImpl implements CommandClient, EventListener {
    private static final Logger LOG = LoggerFactory.getLogger(CommandClient.class);
    private static final int INDEX_LIMIT = 20;
    private static final String DEFAULT_PREFIX = "@mention";

    private final OffsetDateTime start;
    private final Activity activity;
    private final OnlineStatus status;
    private final String ownerId;
    private final String[] coOwnerIds;
    private final String prefix;
    private final String altprefix;
    private final String serverInvite;
    private final HashMap<String, Integer> commandIndex;
    private final ArrayList<Command> commands;
    private final String success;
    private final String warning;
    private final String error;
    private final String carbonKey;
    private final String botsKey;
    private final String botsOrgKey;
    private final HashMap<String,OffsetDateTime> cooldowns;
    private final HashMap<String,Integer> uses;
    private final FixedSizeCache<Long, Set<Message>> linkMap;
    private final boolean useHelp;
    private final Consumer<CommandEvent> helpConsumer;
    private final String helpWord;
    private final String helpWorldAlias;
    private final ScheduledExecutorService executor;
    private final AnnotatedModuleCompiler compiler;
    private final GuildSettingsManager manager;

    private String textPrefix;
    private CommandListener listener = null;
    private int totalGuilds;

    public CommandClientImpl(String ownerId, String[] coOwnerIds, String prefix, String altprefix, Activity activity, OnlineStatus status, String serverInvite,
                             String success, String warning, String error, String carbonKey, String botsKey, String botsOrgKey, ArrayList<Command> commands,
                             boolean useHelp, Consumer<CommandEvent> helpConsumer, String helpWord, ScheduledExecutorService executor, int linkedCacheSize,
                             AnnotatedModuleCompiler compiler, GuildSettingsManager manager) {
        Checks.check(ownerId != null, "Owner ID was set null or not set! Please provide an User ID to register as the owner!");

        if(!SafeIdUtil.checkId(ownerId))
            LOG.warn(String.format("The provided Owner ID (%s) was found unsafe! Make sure ID is a non-negative long!", ownerId));

        if(coOwnerIds!=null) for (String coOwnerId : coOwnerIds)
            if (!SafeIdUtil.checkId(coOwnerId))
                LOG.warn(String.format("The provided CoOwner ID (%s) was found unsafe! Make sure ID is a non-negative long!", coOwnerId));

        this.start = OffsetDateTime.now();

        this.ownerId = ownerId;
        this.coOwnerIds = coOwnerIds;
        this.prefix = prefix==null || prefix.isEmpty() ? DEFAULT_PREFIX : prefix;
        this.altprefix = altprefix==null || altprefix.isEmpty() ? null : altprefix;
        this.textPrefix = prefix;
        this.activity = activity;
        this.status = status;
        this.serverInvite = serverInvite;
        this.success = success==null ? "": success;
        this.warning = warning==null ? "": warning;
        this.error = error==null ? "": error;
        this.carbonKey = carbonKey;
        this.botsKey = botsKey;
        this.botsOrgKey = botsOrgKey;
        this.commandIndex = new HashMap<>();
        this.commands = new ArrayList<>();
        this.cooldowns = new HashMap<>();
        this.uses = new HashMap<>();
        this.linkMap = linkedCacheSize>0 ? new FixedSizeCache<>(linkedCacheSize) : null;
        this.useHelp = useHelp;
        this.helpWord = helpWord==null ? "help" : helpWord;
        this.helpWorldAlias = "h";
        this.executor = executor==null ? Executors.newSingleThreadScheduledExecutor() : executor;
        this.compiler = compiler;
        this.manager = manager;
        this.helpConsumer = (helpConsumer == null) ? (event) -> {
            System.out.println("[" + OffsetDateTime.now(ZoneId.of(Constants.LOG_OFFSET)).toString().replaceAll("T", " ").substring(0, 19) + "] " +
                    "Command executed: " + event.getMessage().getContentDisplay() + ". " +
                    "Guild: " + (event.getGuild() == null ? "DM" : event.getGuild().getName() + " (" + event.getGuild().getIdLong() + ")") + ". " +
                    "User: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + " (" + event.getAuthor().getIdLong() + ").");

            var eb = new EmbedBuilder();
            eb.setColor(Color.decode(new MyUser(event.getAuthor()).getColorCode()));
            var g = event.getJDA().getGuildById(436925371577925642L);
            eb.setThumbnail(g == null ? null : g.getIconUrl());
            eb.setAuthor(event.getSelfUser().getName() + " Commands\n", null, event.getSelfUser().getAvatarUrl());
            eb.setDescription("Type a command to get detailed help, e.g. `" + textPrefix + "avatar`");

            StringBuilder builder = new StringBuilder();
            Command.Category category;
            Command.Category previousCategory = null;

            for (Command command : commands) {
                if (!command.isHidden() && (!command.isOwnerCommand() || event.isOwner())) {
                    category = command.getCategory();

                    if (!Objects.equals(category, previousCategory)) {
                        // New category
                        if (!builder.toString().isBlank()) {
                            eb.addField((previousCategory == null ? "Standard Commands" : previousCategory.getName()), builder.toString(), false);
                            builder = new StringBuilder();
                        }
                    }

                    var userPrefix = event.getGuild() == null ?
                            new MyUser(event.getAuthor()).getPrefix() :
                            new MyGuild(event.getGuild()).getPrefix();

                    builder.append("\n`").append(userPrefix).append(prefix == null ? " " : "").append(command.getName())
                            .append(command.getArguments() == null ? "`" : " " + command.getArguments() + "`")
                            .append(" - ").append(command.getHelp());
                    previousCategory = category;
                }
            }

            if (!builder.toString().isBlank()) {
                eb.addField((previousCategory == null ? "Standard Commands" : previousCategory.getName()), builder.toString(), false);
            }

            User owner = event.getJDA().getUserById(ownerId);
            if (owner != null)
                eb.setFooter("For additional help, contact " + owner.getName() + "#" + owner.getDiscriminator() +
                        (serverInvite != null ? " or join " + serverInvite : ""), owner.getAvatarUrl());

            if (event.isFromType(ChannelType.TEXT)) {
                var botMember = event.getGuild().getMemberById(event.getJDA().getSelfUser().getIdLong());
                if (botMember != null && botMember.hasPermission(Permission.MESSAGE_ADD_REACTION)) event.reactSuccess();
            }

            event.replyInDm(eb.build(), unused -> {
            }, t -> event.replyWarning(LanguageHandler.get(new MyUser(event.getAuthor()).getLanguageCode(), "blocking_dm")));
        } : helpConsumer;

        // Load commands
        for(Command command : commands) addCommand(command);
    }

    @Override
    public void setListener(CommandListener listener)
    {
        this.listener = listener;
    }

    @Override
    public CommandListener getListener()
    {
        return listener;
    }

    @Override
    public List<Command> getCommands()
    {
        return commands;
    }

    @Override
    public OffsetDateTime getStartTime()
    {
        return start;
    }

    @Override
    public OffsetDateTime getCooldown(String name)
    {
        return cooldowns.get(name);
    }

    @Override
    public int getRemainingCooldown(String name) {
        if(cooldowns.containsKey(name)) {
            int time = (int)OffsetDateTime.now().until(cooldowns.get(name), ChronoUnit.SECONDS);
            if(time<=0) {
                cooldowns.remove(name);
                return 0;
            }
            return time;
        }
        return 0;
    }

    @Override
    public void applyCooldown(String name, int seconds) {
        cooldowns.put(name, OffsetDateTime.now().plusSeconds(seconds));
    }

    @Override
    public void cleanCooldowns() {
        OffsetDateTime now = OffsetDateTime.now();
        cooldowns.keySet().stream().filter((str) -> (cooldowns.get(str).isBefore(now)))
                .collect(Collectors.toList()).forEach(cooldowns::remove);
    }

    @Override
    public int getCommandUses(Command command) {
    	return getCommandUses(command.getName());
    }

    @Override
    public int getCommandUses(String name)
    {
    	return uses.getOrDefault(name, 0);
    }

    @Override
    public void addCommand(Command command)
    {
        addCommand(command, commands.size());
    }

    @Override
    public void addCommand(Command command, int index) {
        if(index>commands.size() || index<0)
            throw new ArrayIndexOutOfBoundsException("Index specified is invalid: ["+index+"/"+commands.size()+"]");
        String name = command.getName();
        synchronized(commandIndex) {
            if(commandIndex.containsKey(name))
                throw new IllegalArgumentException("Command added has a name or alias that has already been indexed: \""+name+"\"!");
            for(String alias : command.getAliases()) {
                if(commandIndex.containsKey(alias))
                    throw new IllegalArgumentException("Command added has a name or alias that has already been indexed: \""+alias+"\"!");
                commandIndex.put(alias, index);
            }
            commandIndex.put(name, index);
            if(index<commands.size()) {
                commandIndex.keySet().stream().filter(key -> commandIndex.get(key)>index).collect(Collectors.toList())
                            .forEach(key -> commandIndex.put(key, commandIndex.get(key)+1));
            }
        }
        commands.add(index,command);
    }

    @Override
    public void removeCommand(String name) {
        if(!commandIndex.containsKey(name))
            throw new IllegalArgumentException("Name provided is not indexed: \"" + name + "\"!");
        int targetIndex = commandIndex.remove(name);
        if(commandIndex.containsValue(targetIndex)) {
            commandIndex.keySet().stream().filter(key -> commandIndex.get(key) == targetIndex)
                        .collect(Collectors.toList()).forEach(commandIndex::remove);
        }
        commandIndex.keySet().stream().filter(key -> commandIndex.get(key)>targetIndex).collect(Collectors.toList())
                .forEach(key -> commandIndex.put(key, commandIndex.get(key)-1));
        commands.remove(targetIndex);
    }

    @Override
    public void addAnnotatedModule(Object module)
    {
        compiler.compile(module).forEach(this::addCommand);
    }

    @Override
    public void addAnnotatedModule(Object module, Function<Command, Integer> mapFunction) {
        compiler.compile(module).forEach(command -> addCommand(command, mapFunction.apply(command)));
    }

    @Override
    public String getOwnerId()
    {
        return ownerId;
    }

    @Override
    public long getOwnerIdLong()
    {
        return Long.parseLong(ownerId);
    }

    @Override
    public String[] getCoOwnerIds()
    {
    	return coOwnerIds;
    }

    @Override
    public long[] getCoOwnerIdsLong() {
        // Thought about using java.util.Arrays#setAll(T[], IntFunction<T>)
        // here, but as it turns out it's actually the same thing as this but
        // it throws an error if null. Go figure.
        if(coOwnerIds==null)
            return null;
        long[] ids = new long[coOwnerIds.length];
        for(int i = 0; i<ids.length; i++)
            ids[i] = Long.parseLong(coOwnerIds[i]);
        return ids;
    }

    @Override
    public String getSuccess()
    {
        return success;
    }

    @Override
    public String getWarning()
    {
        return warning;
    }

    @Override
    public String getError()
    {
        return error;
    }

    @Override
    public ScheduledExecutorService getScheduleExecutor()
    {
        return executor;
    }
    
    @Override
    public String getServerInvite()
    {
        return serverInvite;
    }

    @Override
    public String getPrefix()
    {
        return prefix;
    }

    @Override
    public String getAltPrefix()
    {
        return altprefix;
    }

    @Override
    public String getTextualPrefix()
    {
        return textPrefix;
    }

    @Override
    public int getTotalGuilds()
    {
        return totalGuilds;
    }

    @Override
    public String getHelpWord()
    {
        return helpWord;
    }

    @Override
    public boolean usesLinkedDeletion() {
        return linkMap != null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S> S getSettingsFor(Guild guild) {
        if (manager==null)
            return null;
        return (S) manager.getSettings(guild);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <M extends GuildSettingsManager> M getSettingsManager() {
        return (M) manager;
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof MessageReceivedEvent) onMessageReceived((MessageReceivedEvent)event);
        else if (event instanceof GuildMessageDeleteEvent && usesLinkedDeletion()) onMessageDelete((GuildMessageDeleteEvent) event);
        else if (event instanceof ReadyEvent) onReady((ReadyEvent)event);
        else if (event instanceof ShutdownEvent) {
            GuildSettingsManager<?> manager = getSettingsManager();
            if (manager != null)
                manager.shutdown();
            executor.shutdown();
        }
    }

    private void onReady(ReadyEvent event) {
        if (!event.getJDA().getSelfUser().isBot()) {
            LOG.error("JDA-Utilities does not support CLIENT accounts.");
            event.getJDA().shutdown();
            return;
        }

        textPrefix = prefix.equals(DEFAULT_PREFIX) ? "@"+event.getJDA().getSelfUser().getName()+" " : prefix;

        event.getJDA().getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing(String.format(LanguageHandler.get(Servant.config.getDefaultLanguage(), "presence_0"),
                Constants.VERSION, Servant.config.getDefaultPrefix())));

        // Start SettingsManager if necessary
        GuildSettingsManager<?> manager = getSettingsManager();
        if(manager != null) manager.init();

//        sendStats(event.getJDA());
    }

    private void onMessageReceived(MessageReceivedEvent event) {
        // No bots, Nothing from Discord Bot List and No Blacklisted Guilds or Users
        if (event.getAuthor().isBot()) return;
        if (event.isFromGuild() && event.getGuild().getIdLong() == 264445053596991498L) return;
        if (Blacklist.isBlacklisted(event.isFromGuild() ? event.getGuild() : null, event.getAuthor())) return;

        String[] parts = null;
        var rawContent = event.getMessage().getContentRaw();

        var settings = event.isFromType(ChannelType.TEXT)? provideSettings(event.getGuild()) : null;
        if (settings != null) if (settings.getPrefixes() != null && settings.getPrefixes().isEmpty()) settings = null;

        if (settings == null) {
            var userPrefix = new MyUser(event.getAuthor()).getPrefix();
            // Check for default prefix.
            if(rawContent.toLowerCase().startsWith(userPrefix.toLowerCase()))
                parts = splitOnPrefixLength(rawContent, userPrefix.length());
            // Check for alternate prefix
            if(parts == null && altprefix != null && rawContent.toLowerCase().startsWith(altprefix.toLowerCase()))
                parts = splitOnPrefixLength(rawContent, altprefix.length());
        } else {
            // Check for guild specific prefix.
            var prefixes = settings.getPrefixes();
            if(prefixes != null) {
                for(var prefix : prefixes)
                    if (parts == null && rawContent.toLowerCase().startsWith(prefix.toLowerCase()))
                        parts = splitOnPrefixLength(rawContent, prefix.length());
            }
        }

        if (parts == null) {
            var contentRaw = event.getMessage().getContentRaw();
            var selfUserId = event.getJDA().getSelfUser().getIdLong();
            if (contentRaw.startsWith("<@!" + selfUserId + ">") || contentRaw.startsWith("<@" + selfUserId + ">")) {
                parts = splitOnMention(event.getMessage().getContentRaw(), event.getJDA().getSelfUser().getIdLong(), contentRaw.startsWith("<@!"));
            }
        }

        if (parts != null) { // Starts with valid prefix.
                if (event.isFromType(ChannelType.PRIVATE) || event.getTextChannel().canTalk()) {
                var name = parts[0].toLowerCase();
                var args = parts[1] == null ? "" : parts[1];
                final Command command; // this will be null if it's not a command
                if (commands.size() < INDEX_LIMIT + 1)
                    command = commands.stream().filter(cmd -> cmd.isCommandFor(name)).findAny().orElse(null);
                else {
                    synchronized (commandIndex) {
                        int i = commandIndex.getOrDefault(name, -1);
                        command = i != -1 ? commands.get(i) : null;
                    }
                }

                if (command != null) {
                    // Execute Command
                    CompletableFuture.runAsync(() -> {
                        try {
                            var cevent = new CommandEvent(event, args, this);

                            if (event.isFromGuild()) {
                                var myGuild = new MyGuild(event.getGuild());
                                if (myGuild.commandIsEnabled(command.getName()) && myGuild.categoryIsEnabled(command.getCategory().getName()))
                                    startCommand(event, cevent, command, myGuild, name);

                            } else startCommand(event, cevent, command, null, name);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, command.getName().equals("profile") ? Servant.cachedThreadPool : Servant.fixedThreadPool);
                    return;
                }
            }
        }

        if (listener != null) listener.onNonCommandMessage(event);
    }

    private void startCommand(MessageReceivedEvent event, CommandEvent cevent, Command command, MyGuild myGuild, String name) {
        Console.logCmd(event, false);

        if (listener != null) listener.onCommand(cevent, command);
        uses.put(command.getName(), uses.getOrDefault(command.getName(), 0) + 1);
        command.run(cevent, command);

        if (event.isFromGuild()) {
            if (myGuild.featureIsEnabled("cmddeletion")) {
                Servant.myDeletedMessageCache.put(event.getMessageIdLong(), "");
                if (!name.equals("clear")) event.getMessage().delete().queue(s -> {}, f -> {});
            }
        }
    }

    private void onMessageDelete(GuildMessageDeleteEvent event) {
        if (event.getGuild().getIdLong() == 264445053596991498L) return; // Discord Bot List
        // We don't need to cover whether or not this client usesLinkedDeletion() because
        // that is checked in onEvent(Event) before this is even called.
        synchronized(linkMap) {
            if(linkMap.contains(event.getMessageIdLong())) {
                Set<Message> messages = linkMap.get(event.getMessageIdLong());
                if(messages.size()>1 && event.getGuild().getSelfMember()
                        .hasPermission(event.getChannel(), Permission.MESSAGE_MANAGE))
                    event.getChannel().deleteMessages(messages).queue(unused -> {}, ignored -> {});
                else if(messages.size()>0)
                    messages.forEach(m -> m.delete().queue(unused -> {}, ignored -> {}));
            }
        }
    }

    public GuildSettingsProvider provideSettings(Guild guild) {
        Object settings = getSettingsFor(guild);
        if(settings instanceof GuildSettingsProvider)
            return (GuildSettingsProvider)settings;
        else
            return null;
    }

    private static String[] splitOnPrefixLength(String rawContent, int length) {
        return Arrays.copyOf(rawContent.substring(length).trim().split("\\s+", 2), 2);
    }

    private static String[] splitOnMention(String rawContent, long botId, boolean hasExclamationPoint) {
        return Arrays.copyOf(rawContent.replaceAll((hasExclamationPoint ? "<@!" : "<@") + botId + ">", "").trim().split("\\s+", 2), 2);
    }

    public void linkIds(long callId, Message message)
    {
        // We don't use linked deletion, so we don't do anything.
        if(!usesLinkedDeletion()) return;

        synchronized(linkMap) {
            Set<Message> stored = linkMap.get(callId);
            if(stored != null) stored.add(message);
            else {
                stored = new HashSet<>();
                stored.add(message);
                linkMap.add(callId, stored);
            }
        }
    }
}
