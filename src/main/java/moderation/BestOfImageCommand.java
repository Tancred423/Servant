// Author: Tancred423 (https://github.com/Tancred423)
package moderation;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.guild.Server;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import servant.Servant;
import utilities.Constants;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class BestOfImageCommand extends Command {
    public BestOfImageCommand() {
        this.name = "bestofimage";
        this.aliases = new String[] { "image" };
        this.help = "Image rating and best of.";
        this.category = new Category("Moderation");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.GUILD;
        this.userPermissions = new Permission[] { Permission.MANAGE_CHANNEL };
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);
        var p = GuildHandler.getPrefix(event);
        var supportGuild = event.getJDA().getGuildById(Servant.config.getSupportGuildId());
        if (supportGuild == null) return;
        var usageEmote = event.getGuild().getEmotes().isEmpty() ?
                supportGuild.getEmotes().get(0) :
                event.getGuild().getEmotes().get(0);

        if (event.getArgs().isEmpty()) {
            var description = LanguageHandler.get(lang, "bestofimage_description");
            var usage = String.format(LanguageHandler.get(lang, "bestof_usage"),
                    p, name, p, name, p, name, usageEmote.getAsMention(), p, name, p, name, p, name, p, name, "%", p, name, "%", p, name);
            var hint = LanguageHandler.get(lang, "bestof_hint");
            event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            return;
        }

        var args = event.getArgs().split(" ");
        var guild = event.getGuild();
        var server = new Server(guild);
        var user = event.getAuthor();
        var master = new Master(user);

        var eb = new EmbedBuilder();
        eb.setColor(master.getColor());

        if (args[0].equalsIgnoreCase("show") || args[0].equalsIgnoreCase("sh")) {
            var emote = server.getBestOfImageEmote();
            var emoji = server.getBestOfImageEmoji();
            var number = server.getBestOfImageNumber();
            var percentage = server.getBestOfImagePercentage();
            var channel = server.getBestOfImageChannel();

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
            server.setBestOfImageChannel(mentionedChannel.getIdLong());
            event.reactSuccess();
            return;
        }

        // Number
        if (args[0].matches("[0-9]+")) {
            try {
                var mentionedNumber = Integer.parseInt(args[0]);
                server.setBestOfImageNumber(mentionedNumber);
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
                server.setBestOfImagePercentage(mentionedPercentage);
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
                if (mentionedEmote.getGuild() != null)
                    server.setBestOfImageEmote(mentionedEmote.getGuild().getIdLong(), mentionedEmote.getIdLong());
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
            server.setBestOfImageEmoji(mentionedEmoji);
            event.reactSuccess();
        }, failure -> {
            event.reply(LanguageHandler.get(lang, "bestof_invalidemoji"));
            event.reactError();
        });
    }
}
