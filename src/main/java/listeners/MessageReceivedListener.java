package listeners;

import files.language.LanguageHandler;
import fun.level.Level;
import moderation.guild.Server;
import moderation.toggle.Toggle;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import owner.blacklist.Blacklist;
import servant.Servant;
import utilities.*;

import java.awt.*;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class MessageReceivedListener extends ListenerAdapter {
    // This event will be thrown if a message was received (Guild + DM).
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        var user = event.getAuthor();

        /* Certain conditions must meet, so this event is allowed to be executed:
         * 1.   Ignore any request from the Discord Bot List as this big guild
         *      invoke a lot of events, but never use this bot actively.
         * 2.   Ignore any request from bots to prevent infinite loops.
         * 3.   Ignore any request from blacklisted users and guilds.
         */
        if (event.isFromGuild() && event.getGuild().getIdLong() == 264445053596991498L) return; // Discord Bot List
        if (user.isBot()) return;
        if (Blacklist.isBlacklisted(event.isFromGuild() ? event.getGuild() : null, user)) return;

        CompletableFuture.runAsync(() -> {
            if (event.getMessage().getContentRaw().equals("<@!" + event.getJDA().getSelfUser().getIdLong() + ">")
                    || event.getMessage().getContentRaw().equals("<@" + event.getJDA().getSelfUser().getIdLong() + ">")) {
                // @Servant - Show current ping
                processPrefix(event, user);
            } else {
                var gotPurged = processMediaOnlyChannel(event, user); // MediaOnlyChannel
                if (!gotPurged) {
                    processEasterEggs(event.getJDA(), event, user); // Easter Eggs
                    processLevel(event, user); // Level
                }
            }

        }, Servant.fixedThreadPool);
    }

    private static void processPrefix(MessageReceivedEvent event, net.dv8tion.jda.api.entities.User user) {
        String prefix;
        String lang;

        if (event.isFromGuild()) {
            var server = new Server(event.getGuild());
            prefix = server.getPrefix();
            lang = server.getLanguage();
        } else {
            var master = new Master(user);
            prefix = master.getPrefix();
            lang = master.getLanguage();
        }

        event.getChannel().sendMessage(String.format(LanguageHandler.get(lang, "current_prefix"), prefix)).queue();
    }

    private static void processEasterEggs(JDA jda, MessageReceivedEvent event, net.dv8tion.jda.api.entities.User user) {
        var master = new Master(user);
        var message = event.getMessage();
        var contentRaw = message.getContentRaw().toLowerCase();
        Color color;
        try {
            color = master.getColor();
        } catch (NullPointerException e) { // Only happens on boot up
            color = Color.decode(Servant.config.getDefaultColorCode());
        }

        switch (contentRaw) {
            case "its name is":
            case "its name's":
                logEasterEggFound(event, contentRaw);
                event.getChannel().sendMessage(new EmbedBuilder().setColor(color).setImage(ImageUtil.getImageUrl(jda, "excalibur")).build()).queue(sentMessage -> {
                    if (!master.hasAchievement("excalibur")) {
                        master.setAchievement("excalibur", 50);
                        new MessageUtil().reactAchievement(message);
                    }
                }, failure -> logEasterEggError(event));
                break;

            case "i am the bone of my sword":
            case "i'm the bone of my sword":
            case "im the bone of my sword":
                logEasterEggFound(event, contentRaw);
                event.getChannel().sendMessage(new EmbedBuilder().setColor(color).setImage(ImageUtil.getImageUrl(jda, "unlimitedbladeworks")).build()).queue(sentMessage -> {
                    if (!master.hasAchievement("unlimited_blade_works")) {
                        master.setAchievement("unlimited_blade_works", 50);
                        new MessageUtil().reactAchievement(message);
                    }
                }, failure -> logEasterEggError(event));
                break;

            case "gae bolg":
                logEasterEggFound(event, contentRaw);
                event.getChannel().sendMessage(new EmbedBuilder().setColor(color).setImage(ImageUtil.getImageUrl(jda, "gaebolg")).build()).queue(sentMessage -> {
                    if (!master.hasAchievement("gae_bolg")) {
                        master.setAchievement("gae_bolg", 50);
                        new MessageUtil().reactAchievement(message);
                    }
                }, failure -> logEasterEggError(event));
                break;

            case "hey listen":
                logEasterEggFound(event, contentRaw);
                event.getChannel().sendMessage(new EmbedBuilder().setColor(color).setImage(ImageUtil.getImageUrl(jda, "heylisten")).build()).queue(sentMessage -> {
                    if (!master.hasAchievement("navi")) {
                        master.setAchievement("navi", 50);
                        new MessageUtil().reactAchievement(message);
                    }
                }, failure -> logEasterEggError(event));
                break;

            case "deus vult":
                logEasterEggFound(event, contentRaw);
                event.getChannel().sendMessage(new EmbedBuilder().setColor(color).setImage(ImageUtil.getImageUrl(jda, "deusvult")).build()).queue(sentMessage -> {
                    if (!master.hasAchievement("deusvult")) {
                        master.setAchievement("deusvult", 10);
                        new MessageUtil().reactAchievement(message);
                    }
                }, failure -> logEasterEggError(event));
                break;

            case "<@!436916794796670977> fite me":
            case "<@!436916794796670977> fight me":
            case "<@436916794796670977> fite me":
            case "<@436916794796670977> fight me":
            case "<@!550309058251456512> fite me":
            case "<@!550309058251456512> fight me":
            case "<@550309058251456512> fite me":
            case "<@550309058251456512> fight me":
                logEasterEggFound(event, contentRaw);
                event.getChannel().sendMessage(new EmbedBuilder().setColor(color).setImage(ImageUtil.getImageUrl(jda, "fiteme")).build()).queue(sentMessage -> {
                    if (!master.hasAchievement("fiteme")) {
                        master.setAchievement("fiteme", 10);
                        new MessageUtil().reactAchievement(message);
                    }
                }, failure -> logEasterEggError(event));
                break;

            case "happy christmas":
            case "happy xmas":
            case "merry christmas":
            case "merry xmas":
                logEasterEggFound(event, contentRaw);
                event.getChannel().sendMessage(EmoteUtil.getEmoteMention(event.getJDA(), "servant_padoru")).queue(sentMessage -> {
                    if (!master.hasAchievement("xmas")) {
                        master.setAchievement("xmas", 10);
                        new MessageUtil().reactAchievement(message);
                    }
                }, failure -> logEasterEggError(event));
                break;

            case "padoru":
                logEasterEggFound(event, contentRaw);
                event.getChannel().sendMessage(new EmbedBuilder().setColor(master.getColor()).setImage(ImageUtil.getImageUrl(event.getJDA(), "padoru")).build()).queue(success -> {
                    if (!master.hasAchievement("padoru")) {
                        master.setAchievement("padoru", 10);
                        new MessageUtil().reactAchievement(message);
                    }
                }, failure -> logEasterEggError(event));
                break;

            case "<@!436916794796670977> thanks":
            case "<@!436916794796670977> thank you":
            case "<@436916794796670977> thank":
            case "<@!550309058251456512> thanks":
            case "<@!550309058251456512> thank you":
            case "<@550309058251456512> thank":
                event.getChannel().sendMessage("You're welcome " + EmoteUtil.getEmoteMention(event.getJDA(), "love")).queue(success -> {
                    if (!master.hasAchievement("kind")) {
                        master.setAchievement("kind", 10);
                        new MessageUtil().reactAchievement(message);
                    }
                });
                break;
        }
    }

    private static void logEasterEggFound(MessageReceivedEvent event, String contentRaw) {
        System.out.println("[" + OffsetDateTime.now(ZoneId.of(Constants.LOG_OFFSET)).toString().replaceAll("T", " ").substring(0, 19) + "] " +
                "Easteregg found: " + contentRaw + ". " +
                "Guild: " + (event.isFromGuild() ? event.getGuild().getName() + " (" + event.getGuild().getIdLong() + "). " : "DM") +
                "User: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + " (" + event.getAuthor().getIdLong() + ").");
    }

    private static void logEasterEggError(MessageReceivedEvent event) {
        System.out.println("Easter egg message couldn't be sent: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + " (" + event.getAuthor().getIdLong() + ")" +
                " Guild: " + (event.isFromGuild() ? event.getGuild().getName() + " (" + event.getGuild().getIdLong() + ")" : "DM"));
    }

    private static boolean processMediaOnlyChannel(MessageReceivedEvent event, net.dv8tion.jda.api.entities.User user) {
        if (Toggle.isEnabled(event, "mediaononlychannel") && event.isFromGuild()) {
            // Is message in mo-channel?
            var channel = event.getChannel();
            var guild = event.getGuild();
            var server = new Server(guild);
            var lang = server.getLanguage();
            if (server.mediaOnlyChannelHasEntry(channel)) {
                // Has to have an attachment or a valid url.
                var validMessage = true;
                var message = event.getMessage();

                var attachments = message.getAttachments();
                if (attachments.isEmpty()) {
                    String url = null;
                    var args = event.getMessage().getContentDisplay().replace("\n", " ").split(" ");
                    for (var arg : args)
                        if (arg.startsWith("http")) url = arg;
                        else if (arg.startsWith("||http") && arg.endsWith("||"))
                            url = arg.substring(2, arg.length() - 2); // killing spoiler tags

                    if (url == null) validMessage = false;
                    else if (!Parser.isValidUrl(url)) validMessage = false;
                }

                if (!validMessage) {
                    // No files nor valid url. -> Delete and inform about mo-channel.
                    event.getMessage().delete().queue();
                    var mb = new MessageBuilder();
                    mb.setContent(String.format(LanguageHandler.get(lang, "mediaonlychannel_warning"), user.getAsMention()));
                    new MessageUtil().sendAndExpire(channel, mb.build(), 30 * 1000); // 30 seconds.
                    return true;
                }
            }
        }

        return false;
    }

    private static void processLevel(MessageReceivedEvent event, net.dv8tion.jda.api.entities.User user) {
        if (Toggle.isEnabled(event, "level") && event.isFromGuild()) {
            var guild = event.getGuild();
            var lang = new Server(event.getGuild()).getLanguage();
            var userCd = Level.guildCds.get(guild);

            if (userCd != null) {
                var lastMessage = userCd.get(user);
                if (lastMessage != null) {
                    // Check if last message is older than the exp cooldown.
                    long difference = Parser.getTimeDifferenceInMillis(lastMessage, ZonedDateTime.now(ZoneOffset.UTC));
                    long expCooldown = Integer.parseInt(Servant.config.getExpCdMillis());
                    if (difference <= expCooldown) return;
                }
            } else {
                userCd = new HashMap<>();
            }

            userCd.put(user, ZonedDateTime.now(ZoneOffset.UTC));
            Level.guildCds.put(guild, userCd);

            var guildId = guild.getIdLong();

            var currentLevel = Level.getLevel(user, guildId);
            var randomExp = ThreadLocalRandom.current().nextInt(15, 25 + 1); // 15 - 25
            new Master(user).setExp(guildId, randomExp);
            var updatedLevel = Level.getLevel(user, guildId);

            if (updatedLevel > currentLevel) {
                Level.checkForAchievements(updatedLevel, event);
                var sb = new StringBuilder();
                var roles = Level.checkForNewRole(updatedLevel, event, lang);

                if (Toggle.isEnabled(event, "levelupmessage")) {
                    if (!roles.isEmpty()) for (var roleName : roles) sb.append(roleName).append("\n");
                    var selfMember = event.getGuild().getMemberById(event.getJDA().getSelfUser().getIdLong());
                    if (selfMember != null && selfMember.hasPermission(Permission.MESSAGE_EMBED_LINKS)) {
                        var eb = new EmbedBuilder();
                        eb.setColor(new Master(user).getColor());
                        eb.setAuthor(LanguageHandler.get(lang, "levelrole_levelup"), null, null);
                        eb.setThumbnail(user.getEffectiveAvatarUrl());
                        eb.setDescription(String.format(LanguageHandler.get(lang, "level_up"), user.getAsMention(), updatedLevel));
                        if (!roles.isEmpty()) eb.addField(roles.size() == 1 ?
                                LanguageHandler.get(lang, "levelrole_role_singular") :
                                LanguageHandler.get(lang, "levelrole_role_plural"), sb.toString(), false);
                        event.getChannel().sendMessage(eb.build()).queue();
                    } else {
                        var mb = new StringBuilder();
                        mb.append("**").append(LanguageHandler.get(lang, "levelrole_levelup")).append("**\n");
                        mb.append(String.format(LanguageHandler.get(lang, "level_up"), user.getAsMention(), updatedLevel)).append("**\n");
                        if (!roles.isEmpty()) {
                            mb.append(roles.size() == 1 ?
                                    LanguageHandler.get(lang, "levelrole_role_singular") :
                                    LanguageHandler.get(lang, "levelrole_role_plural")).append("\n");
                            mb.append(sb.toString()).append("\n");
                        }
                        mb.append("_").append(LanguageHandler.get(lang, "level_missingpermission_embed")).append("_");
                        event.getChannel().sendMessage(mb.toString()).queue();
                    }
                }
            }
        }
    }
}
