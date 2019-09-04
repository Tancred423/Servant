// Author: Tancred423 (https://github.com/Tancred423)
package moderation.bestOfImage;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import servant.Log;
import servant.Servant;
import utilities.Constants;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.awt.*;
import java.sql.SQLException;

public class BestOfImageCommand extends Command {
    public BestOfImageCommand() {
        this.name = "bestofimage";
        this.aliases = new String[]{"image"};
        this.help = "Image rating and best of.";
        this.category = new Category("Moderation");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.GUILD;
        this.userPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!Toggle.isEnabled(event, name)) return;

        var lang = LanguageHandler.getLanguage(event, name);
        var p = GuildHandler.getPrefix(event, name);
        var usageEmote = event.getGuild().getEmotes().isEmpty() ?
                event.getJDA().getGuildById(Servant.config.getSupportGuildId()).getEmotes().get(0) :
                event.getGuild().getEmotes().get(0);

        if (event.getArgs().isEmpty()) {
            try {
                var description = LanguageHandler.get(lang, "bestofimage_description");
                var usage = String.format(LanguageHandler.get(lang, "bestof_usage"),
                        p, name, p, name, p, name, usageEmote.getAsMention(), p, name, p, name, p, name, p, name, "%", p, name, "%", p, name);
                var hint = LanguageHandler.get(lang, "bestof_hint");
                event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            }
            return;
        }

        var args = event.getArgs().split(" ");
        var guild = event.getGuild();
        var internalGuild = new Guild(guild.getIdLong());
        var author = event.getAuthor();
        var internalAuthor = new User(author.getIdLong());

        var eb = new EmbedBuilder();
        try {
            eb.setColor(internalAuthor.getColor());
        } catch (SQLException e) {
            eb.setColor(Color.decode(Servant.config.getDefaultColorCode()));
        }

        try {
            if (args[0].equalsIgnoreCase("show") || args[0].equalsIgnoreCase("sh")) {
                var emote = internalGuild.getBestOfImageEmote();
                var emoji = internalGuild.getBestOfImageEmoji();
                var number = internalGuild.getBestOfImageNumber();
                var percentage = internalGuild.getBestOfImagePercentage();
                var channel = internalGuild.getBestOfImageChannel();

                eb.setAuthor(LanguageHandler.get(lang, "bestofimage_setup"), null, null);
                eb.addField(LanguageHandler.get(lang, "bestof_emote"), emote == null ? (emoji == null ? LanguageHandler.get(lang, "bestof_noemote") : emoji) : emote.getAsMention(), true);
                eb.addField(LanguageHandler.get(lang, "bestof_number"), number == 0 ? LanguageHandler.get(lang, "bestof_nonumber") : String.valueOf(number), true);
                eb.addField(LanguageHandler.get(lang, "bestof_percentage"), percentage == 0 ? LanguageHandler.get(lang, "bestof_nopercentage") : percentage + "%", true);
                eb.addField(LanguageHandler.get(lang, "bestof_channel"), channel == null ? LanguageHandler.get(lang, "bestof_nochannel") : channel.getAsMention(), true);

                event.reply(eb.build());
                return;
            }

            // Channel
            var message = event.getMessage();
            if (!message.getMentionedChannels().isEmpty()) {
                var mentionedChannel = message.getMentionedChannels().get(0);
                try {
                    internalGuild.setBestOfImageChannel(mentionedChannel.getIdLong());
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    return;
                }
                event.reactSuccess();
                return;
            }

            // Number
            if (args[0].matches("[0-9]+")) {
                try {
                    var mentionedNumber = Integer.parseInt(args[0]);
                    internalGuild.setBestOfImageNumber(mentionedNumber);
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    return;
                } catch (NumberFormatException ex) {
                    event.reactError();
                    event.reply(LanguageHandler.get(lang, "bestof_numbertoobig"));
                    return;
                }
                event.reactSuccess();
                return;
            }

            // Percentage
            if (args[0].contains("%")) {
                var arg = args[0].replaceAll("%", "");
                if (arg.matches("[0-9]+")) {
                    var mentionedPercentage = Integer.parseInt(arg);
                    if (mentionedPercentage > 100) {
                        event.reply(LanguageHandler.get(lang, "bestof_numbertoobig"));
                        event.reactError();
                        return;
                    }
                    try {
                        internalGuild.setBestOfImagePercentage(mentionedPercentage);
                    } catch (SQLException e) {
                        new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                        return;
                    }
                    event.reactSuccess();
                } else {
                    event.reply(LanguageHandler.get(lang, "bestof_invalidpercentage"));
                    event.reactError();
                }
                return;
            }

            // Emote
            if (!message.getEmotes().isEmpty()) {
                var mentionedEmote = message.getEmotes().get(0);
                try {
                    internalGuild.setBestOfImageEmote(mentionedEmote.getGuild().getIdLong(), mentionedEmote.getIdLong());
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    return;
                } catch (NullPointerException ex) {
                    event.reply(LanguageHandler.get(lang, "bestof_invalidemote"));
                    event.reactError();
                    return;
                }
                event.reactSuccess();
                return;
            }

            // Emoji
            message.addReaction(args[0]).queue(success -> {
                var mentionedEmoji = args[0];
                try {
                    internalGuild.setBestOfImageEmoji(mentionedEmoji);
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    return;
                }
                event.reactSuccess();
            }, failure -> {
                event.reply(LanguageHandler.get(lang, "bestof_invalidemoji"));
                event.reactError();
            });
        } catch (SQLException e) {
            new Log(e, guild, author, name, event).sendLog(true);
        }

        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }
}
