// Author: Tancred423 (https://github.com/Tancred423)
package moderation.livestream;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import owner.blacklist.Blacklist;
import servant.Servant;
import utilities.Constants;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LivestreamCommand extends Command {
    public LivestreamCommand() {
        this.name = "livestream";
        this.aliases = new String[] { "stream" };
        this.help = "Livestream notifications.";
        this.category = new Category("Moderation");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.GUILD;
        this.userPermissions = new Permission[] { Permission.MANAGE_CHANNEL, Permission.MANAGE_ROLES };
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_MENTION_EVERYONE, Permission.MANAGE_ROLES
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                if (!Toggle.isEnabled(event, name)) return;
                if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

                var lang = LanguageHandler.getLanguage(event);
                var p = GuildHandler.getPrefix(event);

                String arg = event.getArgs();
                if (arg.isEmpty()) {
                    var description = LanguageHandler.get(lang, "livestream_description");
                    var usage = String.format(LanguageHandler.get(lang, "livestream_usage"),
                            p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name);
                    var hint = LanguageHandler.get(lang, "livestream_hint");
                    event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
                    return;
                }

                var message = event.getMessage();
                var author = event.getAuthor();
                var guild = event.getGuild();
                var internalGuild = new Guild(guild.getIdLong());
                var args = arg.split(" ");
                switch (args[0].toLowerCase()) {
                    case "toggle":
                    case "t":
                        internalGuild.toggleStreamerMode(guild, author);
                        event.reactSuccess();
                        break;

                    case "set":
                    case "s":
                    case "add":
                    case "a":
                        if (message.getMentionedChannels().isEmpty()
                                && message.getMentionedMembers().isEmpty()
                                && message.getMentionedRoles().isEmpty()) {
                            event.reply(LanguageHandler.get(lang, "livestream_missingmention"));
                            event.reactError();
                        } else if (message.getMentionedMembers().isEmpty() && message.getMentionedRoles().isEmpty()) {
                            internalGuild.setStreamChannel(message.getMentionedChannels().get(0), guild, author);
                            event.reactSuccess();
                        } else if (message.getMentionedChannels().isEmpty() && message.getMentionedRoles().isEmpty()) {
                            internalGuild.setStreamer(message.getMentionedMembers().get(0).getUser().getIdLong(), guild, author);
                            event.reactSuccess();
                        } else if (message.getMentionedMembers().isEmpty() && message.getMentionedChannels().isEmpty()) {
                            internalGuild.setStreamingRole(message.getMentionedRoles().get(0).getIdLong(), guild, author);
                            event.reactSuccess();
                        } else {
                            event.reply(LanguageHandler.get(lang, "livestream_toomanymentions"));
                            event.reactError();
                        }
                        break;

                    case "unset":
                    case "u":
                    case "remove":
                    case "r":
                        if (message.getMentionedChannels().isEmpty()
                                && message.getMentionedMembers().isEmpty()
                                && message.getMentionedRoles().isEmpty()) {
                            event.reply(LanguageHandler.get(lang, "livestream_missingmention"));
                            event.reactError();
                        } else if (message.getMentionedMembers().isEmpty() && message.getMentionedRoles().isEmpty()) {
                            if (internalGuild.unsetStreamChannel(guild, author)) event.reactSuccess();
                            else {
                                event.reply(LanguageHandler.get(lang, "livestream_nochannel"));
                                event.reactWarning();
                            }
                        } else if (message.getMentionedChannels().isEmpty() && message.getMentionedRoles().isEmpty()) {
                            if (internalGuild.unsetStreamer(message.getMentionedMembers().get(0).getUser().getIdLong(), guild, author))
                                event.reactSuccess();
                            else {
                                event.reply(LanguageHandler.get(lang, "livestream_nostreamer"));
                                event.reactWarning();
                            }
                        } else if (message.getMentionedMembers().isEmpty() && message.getMentionedChannels().isEmpty()) {
                            if (internalGuild.unsetStreamingRole(guild, author))
                                event.reactSuccess();
                            else {
                                event.reply(LanguageHandler.get(lang, "livestream_norole"));
                                event.reactWarning();
                            }
                        } else {
                            event.reply(LanguageHandler.get(lang, "livestream_toomanymentions"));
                            event.reactError();
                        }
                        break;

                    case "show":
                    case "sh":
                        var internalUser = new User(event.getAuthor().getIdLong());
                        var eb = new EmbedBuilder();
                        var sb = new StringBuilder();
                        List<Long> streamers = internalGuild.getStreamers(guild, author);

                        long roleId = internalGuild.getStreamingRoleId(guild, author);

                        long channelId = internalGuild.getStreamChannelId(guild, author);
                        for (Long streamer : streamers) {
                            var streamerMember = guild.getMemberById(streamer);
                            if (streamerMember != null) // todo: always null?
                                sb.append(streamerMember.getAsMention()).append("\n");
                        }

                        boolean streamerMode = internalGuild.isStreamerMode(guild, author);

                        eb.setColor(internalUser.getColor(guild, author));
                        eb.setAuthor(LanguageHandler.get(lang, "livestream_settings"), null, guild.getIconUrl());
                        var tc = guild.getTextChannelById(channelId);
                        eb.addField(LanguageHandler.get(lang, "livestream_notificationchannel"),
                                (tc == null ? LanguageHandler.get(lang, "livestream_nochannelset") : tc.getAsMention()), true);
                        var role = guild.getRoleById(roleId);
                        eb.addField(LanguageHandler.get(lang, "livestream_role"),
                                (role == null ? LanguageHandler.get(lang, "livestream_noroleset") : role.getAsMention()), true);
                        eb.addField(LanguageHandler.get(lang, "livestream_mode"),
                                (streamerMode ? LanguageHandler.get(lang, "livestream_mode") : LanguageHandler.get(lang, "livestream_publicmode")), true);
                        eb.addField(LanguageHandler.get(lang, "livestream_streamers"),
                                (streamers.isEmpty() ? LanguageHandler.get(lang, "livestream_nostreamersset") : sb.toString()), false);

                        event.reply(eb.build());
                        break;

                    default:
                        event.reactError();
                        event.reply(LanguageHandler.get(lang, "livestream_firstarg"));
                        break;
                }

                // Statistics.
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
                new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Servant.cpuPool);
    }
}
