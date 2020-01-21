// Author: Tancred423 (https://github.com/Tancred423)
package moderation.bestOfQuote;

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

import java.util.concurrent.CompletableFuture;

public class BestOfQuoteCommand extends Command {
    public BestOfQuoteCommand() {
        this.name = "bestofquote";
        this.aliases = new String[] { "quote" };
        this.help = "Quote rating and best of.";
        this.category = new Category("Moderation");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.GUILD;
        this.userPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION
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
                var supportGuild = event.getJDA().getGuildById(Servant.config.getSupportGuildId());
                if (supportGuild == null) return;
                var usageEmote = event.getGuild().getEmotes().isEmpty() ?
                        supportGuild.getEmotes().get(0) :
                        event.getGuild().getEmotes().get(0);

                if (event.getArgs().isEmpty()) {
                    var description = LanguageHandler.get(lang, "bestofquote_description");
                    var usage = String.format(LanguageHandler.get(lang, "bestof_usage"),
                            p, name, p, name, p, name, usageEmote.getAsMention(), p, name, p, name, p, name, p, name, "%", p, name, "%", p, name);
                    var hint = LanguageHandler.get(lang, "bestof_hint");
                    event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
                    return;
                }

                var args = event.getArgs().split(" ");
                var guild = event.getGuild();
                var internalGuild = new Guild(guild.getIdLong());
                var author = event.getAuthor();
                var internalAuthor = new User(author.getIdLong());

                var eb = new EmbedBuilder();
                eb.setColor(internalAuthor.getColor(guild, author));

                if (args[0].equalsIgnoreCase("show") || args[0].equalsIgnoreCase("sh")) {
                    var emote = internalGuild.getBestOfQuoteEmote(event.getJDA(), guild, author);
                    var emoji = internalGuild.getBestOfQuoteEmoji(guild, author);
                    var number = internalGuild.getBestOfQuoteNumber(guild, author);
                    var percentage = internalGuild.getBestOfQuotePercentage(guild, author);
                    var channel = internalGuild.getBestOfQuoteChannel(guild, author);

                    eb.setAuthor(LanguageHandler.get(lang, "bestofquote_setup"), null, null);
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
                    internalGuild.setBestOfQuoteChannel(mentionedChannel.getIdLong(), guild, author);
                    event.reactSuccess();
                    return;
                }

                // Number
                if (args[0].matches("[0-9]+")) {
                    try {
                        var mentionedNumber = Integer.parseInt(args[0]);
                        internalGuild.setBestOfQuoteNumber(mentionedNumber, guild, author);
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
                        internalGuild.setBestOfQuotePercentage(mentionedPercentage, guild, author);
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
                            internalGuild.setBestOfQuoteEmote(mentionedEmote.getGuild().getIdLong(), mentionedEmote.getIdLong(), guild, author);
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
                    internalGuild.setBestOfQuoteEmoji(mentionedEmoji, guild, author);
                    event.reactSuccess();
                }, failure -> {
                    event.reply(LanguageHandler.get(lang, "bestof_invalidemoji"));
                    event.reactError();
                });

                // Statistics.
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
                new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Servant.threadPool);
    }
}
