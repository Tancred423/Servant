package listeners;

import commands.fun.profile.Level;
import plugins.moderation.customcommands.CustomCommand;
import commands.owner.blacklist.Blacklist;
import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import servant.MyGuild;
import servant.MyMessage;
import servant.MyUser;
import servant.Servant;
import utilities.*;

import java.awt.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class MessageReceivedListener extends ListenerAdapter {
    // This event will be thrown if a message was received (Guild + DM).
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        var author = event.getAuthor();
        var message = event.getMessage();

        // Blacklist
        if ((event.isFromGuild() ? event.getGuild().getIdLong() : 0) == Constants.DISCORD_BOT_LIST_ID) return;
        if (author.isBot()) return;
        if (Blacklist.isBlacklisted(event.isFromGuild() ? event.getGuild() : null, author)) return;

        CompletableFuture.runAsync(() -> {
            var myMessage = new MyMessage(event.getJDA(), event.isFromGuild() ? event.getGuild().getIdLong() : 0, event.isFromGuild() ? event.getTextChannel().getIdLong() : 0, message.getIdLong());
            myMessage.setContent(message.getContentDisplay());
            myMessage.setAuthor(message.getAuthor());

            if (message.getContentRaw().equals("<@!" + event.getJDA().getSelfUser().getIdLong() + ">")
                    || message.getContentRaw().equals("<@" + event.getJDA().getSelfUser().getIdLong() + ">")) {
                processPrefix(event, author); // @Servant - Show current prefix
            } else {
                var gotPurged = false;

                if (event.isFromGuild()) gotPurged = processMediaOnlyChannel(event, author); // MediaOnlyChannel

                if (!gotPurged) {
                    if (event.isFromGuild()) {
                        processCache(message.getIdLong(), myMessage); // Cache
                        processCustomCommands(event, myMessage); // CustomCommands
                        processLevel(event, author); // Level
                    }

                    // For both guild and DM
                    processEasterEggs(event.getJDA(), event, author); // EasterEggs
                }
            }
        }, Servant.fixedThreadPool);
    }

    private static void processCustomCommands(MessageReceivedEvent event, MyMessage myMessage) {
        if (myMessage.isCustomCommand() && myMessage.startsWithPrefix()) {
            var myGuild = new MyGuild(event.getGuild());
            if (myGuild.categoryIsEnabled("moderation") && myGuild.pluginIsEnabled("customcommands"))
                new CustomCommand(event).reply();
        }
    }

    private static void processCache(long messageId, MyMessage myMessage) {
        Servant.myMessageCache.put(messageId, myMessage);
    }

    private static void processPrefix(MessageReceivedEvent event, net.dv8tion.jda.api.entities.User user) {
        String prefix;
        String lang;

        if (event.isFromGuild()) {
            var server = new MyGuild(event.getGuild());
            prefix = server.getPrefix();
            lang = server.getLanguageCode();
        } else {
            var master = new MyUser(user);
            prefix = master.getPrefix();
            lang = master.getLanguageCode();
        }

        event.getChannel().sendMessage(String.format(LanguageHandler.get(lang, "current_prefix"), prefix)).queue();
    }

    private static void processEasterEggs(JDA jda, MessageReceivedEvent event, net.dv8tion.jda.api.entities.User user) {
        var myUser = new MyUser(user);
        var message = event.getMessage();
        var contentRaw = message.getContentRaw().toLowerCase();
        Color color;
        try {
            color = Color.decode(myUser.getColorCode());
        } catch (NullPointerException e) { // Only happens on boot up
            color = Color.decode(Servant.config.getDefaultColorCode());
        }

        if (event.isFromGuild()) {
            var myGuild = new MyGuild(event.getGuild());
            if (!myGuild.featureIsEnabled("eastereggs")) return;

            switch (contentRaw) {
                case "its name is":
                case "its name's":
                    logEasterEggFound(event, contentRaw);
                    event.getChannel().sendMessage(new EmbedBuilder().setColor(color).setImage(ImageUtil.getUrl(jda, "excalibur")).build()).queue(sentMessage -> {
                        if (!myUser.hasAchievement("excalibur")
                                && new MyGuild(event.getGuild()).featureIsEnabled("achievements")) {
                            myUser.setAchievement("excalibur");
                            new MessageUtil().reactAchievement(message);
                        }
                    }, failure -> logEasterEggError(event));
                    break;

                case "i am the bone of my sword":
                case "i'm the bone of my sword":
                case "im the bone of my sword":
                    logEasterEggFound(event, contentRaw);
                    event.getChannel().sendMessage(new EmbedBuilder().setColor(color).setImage(ImageUtil.getUrl(jda, "unlimitedbladeworks")).build()).queue(sentMessage -> {
                        if (!myUser.hasAchievement("unlimited_blade_works") && new MyGuild(event.getGuild()).featureIsEnabled("achievements")) {
                            myUser.setAchievement("unlimited_blade_works");
                            new MessageUtil().reactAchievement(message);
                        }
                    }, failure -> logEasterEggError(event));
                    break;

                case "gae bolg":
                    logEasterEggFound(event, contentRaw);
                    event.getChannel().sendMessage(new EmbedBuilder().setColor(color).setImage(ImageUtil.getUrl(jda, "gaebolg")).build()).queue(sentMessage -> {
                        if (!myUser.hasAchievement("gae_bolg") && new MyGuild(event.getGuild()).featureIsEnabled("achievements")) {
                            myUser.setAchievement("gae_bolg");
                            new MessageUtil().reactAchievement(message);
                        }
                    }, failure -> logEasterEggError(event));
                    break;

                case "hey listen":
                    logEasterEggFound(event, contentRaw);
                    event.getChannel().sendMessage(new EmbedBuilder().setColor(color).setImage(ImageUtil.getUrl(jda, "heylisten")).build()).queue(sentMessage -> {
                        if (!myUser.hasAchievement("navi") && new MyGuild(event.getGuild()).featureIsEnabled("achievements")) {
                            myUser.setAchievement("navi");
                            new MessageUtil().reactAchievement(message);
                        }
                    }, failure -> logEasterEggError(event));
                    break;

                case "deus vult":
                    logEasterEggFound(event, contentRaw);
                    event.getChannel().sendMessage(new EmbedBuilder().setColor(color).setImage(ImageUtil.getUrl(jda, "deusvult")).build()).queue(sentMessage -> {
                        if (!myUser.hasAchievement("deusvult") && new MyGuild(event.getGuild()).featureIsEnabled("achievements")) {
                            myUser.setAchievement("deusvult");
                            new MessageUtil().reactAchievement(message);
                        }
                    }, failure -> logEasterEggError(event));
                    break;

                case "<@!436916794796670977> fite me":
                case "<@436916794796670977> fite me":
                case "<@!436916794796670977> fight me":
                case "<@436916794796670977> fight me":

                case "<@!550309058251456512> fite me":
                case "<@550309058251456512> fite me":
                case "<@!550309058251456512> fight me":
                case "<@550309058251456512> fight me":
                    logEasterEggFound(event, contentRaw);
                    event.getChannel().sendMessage(new EmbedBuilder().setColor(color).setImage(ImageUtil.getUrl(jda, "fiteme")).build()).queue(sentMessage -> {
                        if (!myUser.hasAchievement("fiteme") && new MyGuild(event.getGuild()).featureIsEnabled("achievements")) {
                            myUser.setAchievement("fiteme");
                            new MessageUtil().reactAchievement(message);
                        }
                    }, failure -> logEasterEggError(event));
                    break;

                case "happy christmas":
                case "happy xmas":
                case "merry christmas":
                case "merry xmas":
                    logEasterEggFound(event, contentRaw);
                    event.getChannel().sendMessage(EmoteUtil.getEmote(jda, "padoru").getAsMention()).queue(sentMessage -> {
                        if (!myUser.hasAchievement("xmas") && new MyGuild(event.getGuild()).featureIsEnabled("achievements")) {
                            myUser.setAchievement("xmas");
                            new MessageUtil().reactAchievement(message);
                        }
                    }, failure -> logEasterEggError(event));
                    break;

                case "padoru":
                    logEasterEggFound(event, contentRaw);
                    event.getChannel().sendMessage(new EmbedBuilder().setColor(Color.decode(myUser.getColorCode())).setImage(ImageUtil.getUrl(event.getJDA(), "padoru")).build()).queue(success -> {
                        if (!myUser.hasAchievement("padoru") && new MyGuild(event.getGuild()).featureIsEnabled("achievements")) {
                            myUser.setAchievement("padoru");
                            new MessageUtil().reactAchievement(message);
                        }
                    }, failure -> logEasterEggError(event));
                    break;

                case "<@!436916794796670977> thanks":
                case "<@436916794796670977> thanks":
                case "<@!436916794796670977> thank you":
                case "<@436916794796670977> thank you":

                case "<@!550309058251456512> thanks":
                case "<@550309058251456512> thanks":
                case "<@!550309058251456512> thank you":
                case "<@550309058251456512> thank you":
                    var lang = myGuild.getLanguageCode();
                    event.getChannel().sendMessage(String.format(LanguageHandler.get(lang, "eastereggs_you_are_welcome"), EmoteUtil.getEmote(jda, "love").getAsMention())).queue(success -> {
                        if (!myUser.hasAchievement("kind") && new MyGuild(event.getGuild()).featureIsEnabled("achievements")) {
                            myUser.setAchievement("kind");
                            new MessageUtil().reactAchievement(message);
                        }
                    });
                    break;
            }
        } else {
            if (contentRaw.toLowerCase().startsWith("i found your console")) {
                // Console achievement
                if (!myUser.hasAchievement("console")) {
                    logEasterEggFound(event, event.getMessage().getContentRaw());
                    myUser.setAchievement("console");
                    new MessageUtil().reactAchievement(event.getMessage());
                }
            }
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
        var guild = event.getGuild();
        var myGuild = new MyGuild(guild);

        if (myGuild.pluginIsEnabled("mediaonlychannel") && myGuild.categoryIsEnabled("moderation")) {
            // Is message in mo-channel?
            var channel = event.getChannel();
            var lang = myGuild.getLanguageCode();
            if (myGuild.isMediaOnlyChannel(channel)) {
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

                    if (myGuild.mediaOnlyChannelNotification()) {
                        var mb = new MessageBuilder();
                        mb.setContent(String.format(LanguageHandler.get(lang, "mediaonlychannel_warning"), user.getAsMention()));
                        new MessageUtil().sendAndExpire(channel, mb.build(), 30 * 1000); // 30 seconds.
                    }

                    return true;
                }
            }
        }

        return false;
    }

    private static void processLevel(MessageReceivedEvent event, net.dv8tion.jda.api.entities.User user) {
        var guild = event.getGuild();
        var myGuild = new MyGuild(guild);

        if (myGuild.pluginIsEnabled("level") && myGuild.categoryIsEnabled("moderation")) {
            var lang = new MyGuild(event.getGuild()).getLanguageCode();
            var userCd = Level.guildCds.get(guild);

            if (userCd != null) {
                var lastMessage = userCd.get(user);
                if (lastMessage != null) {
                    // Check if last message is older than the exp cooldown.
                    long difference = Parser.getTimeDifferenceInMillis(lastMessage, Instant.now());
                    long expCooldown = Integer.parseInt(Servant.config.getExpCdMillis());
                    if (difference <= expCooldown) return;
                }
            } else {
                userCd = new HashMap<>();
            }

            userCd.put(user, Instant.now());
            Level.guildCds.put(guild, userCd);

            var guildId = guild.getIdLong();

            var currentLevel = Level.getLevel(user, guildId);
            var randomExp = Math.round(ThreadLocalRandom.current().nextInt(15, 25 + 1) * myGuild.getLevelModifier()); // 15 - 25 * modifier
            new MyUser(user).setExp(guildId, randomExp);
            var updatedLevel = Level.getLevel(user, guildId);

            if (updatedLevel > currentLevel) {
                Level.checkForAchievements(updatedLevel, event);
                var sb = new StringBuilder();
                try {
                    var roles = Level.checkForNewRole(updatedLevel, event, lang);

                    if (myGuild.levelNotificationIsEnabled()) {
                        if (!roles.isEmpty()) for (var roleName : roles) sb.append(roleName).append("\n");
                        var selfMember = event.getGuild().getMemberById(event.getJDA().getSelfUser().getIdLong());
                        if (selfMember != null && selfMember.hasPermission(Permission.MESSAGE_EMBED_LINKS)) {
                            var eb = new EmbedBuilder();
                            eb.setColor(Color.decode(new MyUser(user).getColorCode()));
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
                } catch (HierarchyException e) {
                    event.getChannel().sendMessage("❌" + e.getMessage()).queue();
                }
            }
        }
    }
}
