// Author: Tancred423 (https://github.com/Tancred423)
package moderation;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import owner.blacklist.Blacklist;
import servant.Log;
import utilities.*;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ServerSetupCommand extends Command {
    private final EventWaiter waiter;
    private String accept = "✅";
    private String decline = "❌";

    public ServerSetupCommand(EventWaiter waiter) {
        this.name = "serversetup";
        this.aliases = new String[]{"setup"};
        this.help = "Initial server setup wizard.";
        this.category = new Category("Moderation");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.GUILD;
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};

        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, name)) return;
            if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

            var lang = LanguageHandler.getLanguage(event, name);
            var p = GuildHandler.getPrefix(event, name);

            var author = event.getAuthor();
            var channel = event.getChannel();
            channel.sendMessage(LanguageHandler.get(lang, "setupwizard_introduction")).queue(message -> {
                message.addReaction(accept).queue();
                message.addReaction(decline).queue();

                waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                        e -> e.getUser().equals(author)
                                && (e.getReactionEmote().getName().equals(accept)
                                || e.getReactionEmote().getName().equals(decline)),
                        e -> {
                            if (e.getReactionEmote().getName().equals(accept)) {
                                message.clearReactions().queue();
                                processLanguage(channel, author, message, event, lang, p, false, new Guild(event.getGuild().getIdLong()));
                            } else {
                                message.delete().queue();
                                event.reactWarning();
                            }
                        }, 15, TimeUnit.MINUTES, () -> timeout(event, message, lang));
            });

            // Statistics.
            try {
                new moderation.user.User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
                new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
            }
        });
    }

    // Timeout
    private void timeout(CommandEvent event, Message botMessage, String lang) {
        event.reactWarning();
        botMessage.delete().queue();
        event.reply(LanguageHandler.get(lang, "setupwizard_timeout"));
    }

    // Language
    private void processLanguage(MessageChannel channel, User author, Message previous, CommandEvent event, String lang, String p, boolean repeated, Guild internalGuild) {
        previous.editMessage(repeated ? LanguageHandler.get(lang, "setupwizard_language_repeated") :
                LanguageHandler.get(lang, "setupwizard_language")).queue(
                message -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                        e -> e.getAuthor().equals(author)
                                && e.getChannel().equals(channel),
                        e -> {
                            var language = e.getMessage().getContentRaw();
                            e.getMessage().delete().queue();
                            if (Parser.isValidLanguage(language)) {
                                try {
                                    internalGuild.setLanguage(language);
                                } catch (SQLException ex) {
                                    new Log(ex, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                                }processPrefix(channel, author, message, event, lang, p, false, internalGuild);
                            }
                            else processLanguage(channel, author, previous, event, lang, p, true, internalGuild);
                        }, 15, TimeUnit.MINUTES, () -> timeout(event, message, lang)));
    }

    // Prefix
    private void processPrefix(MessageChannel channel, User author, Message previous, CommandEvent event, String lang, String p, boolean repeated, Guild internalGuild) {
        previous.editMessage(repeated ? String.format(LanguageHandler.get(lang, "setupwizard_prefix_repeated"), p) :
                String.format(LanguageHandler.get(lang, "setupwizard_prefix"), accept, p)).queue(
                message -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                        e -> e.getAuthor().equals(author)
                                && e.getChannel().equals(channel),
                        e -> {
                            var prefix = e.getMessage().getContentRaw();
                            e.getMessage().delete().queue();
                            if (Parser.isValidPrefix(prefix)) {
                                try {
                                    internalGuild.setPrefix(prefix);
                                } catch (SQLException ex) {
                                    new Log(ex, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                                }processOffset(channel, author, message, event, lang, false, internalGuild);
                            }
                            else processPrefix(channel, author, previous, event, lang, p, true, internalGuild);
                        }, 15, TimeUnit.MINUTES, () -> timeout(event, message, lang)));
    }

    // Offset
    private void processOffset(MessageChannel channel, User author, Message previous, CommandEvent event, String lang, boolean repeated, Guild internalGuild) {
        previous.editMessage(repeated ? LanguageHandler.get(lang, "setupwizard_offset_repeated") :
                String.format(LanguageHandler.get(lang, "setupwizard_offset"), accept)).queue(
                message -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                        e -> e.getAuthor().equals(author)
                                && e.getChannel().equals(channel),
                        e -> {
                            var offset = e.getMessage().getContentRaw();
                            e.getMessage().delete().queue();
                            try {
                                if (Parser.isValidOffset(offset)) {
                                    try {
                                        internalGuild.setOffset(offset);
                                    } catch (SQLException ex) {
                                        new Log(ex, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                                    }
                                    processFinish(message, event, lang);
                                } else processOffset(channel, author, previous, event, lang, true, internalGuild);
                            } catch (NumberFormatException ex) {
                                processOffset(channel, author, previous, event, lang, true, internalGuild);
                            }
                        }, 15, TimeUnit.MINUTES, () -> timeout(event, message, lang)));
    }

    // Finish
    private void processFinish(Message previous, CommandEvent event, String lang) {
        previous.editMessage(LanguageHandler.get(lang, "setupwizard_done")).queue();
        event.reactSuccess();
    }
}
