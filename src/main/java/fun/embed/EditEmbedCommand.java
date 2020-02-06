// Author: Tancred423 (https://github.com/Tancred423)
package fun.embed;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import utilities.Constants;
import utilities.MessageUtil;
import utilities.Parser;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class EditEmbedCommand extends Command {
    private final EventWaiter waiter;
    private String accept = "✅";
    private String decline = "❌";

    public EditEmbedCommand(EventWaiter waiter) {
        this.name = "editembed";
        this.aliases = new String[0];
        this.help = "Edit an embed of the bot.";
        this.category = new Category("Fun");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[] { Permission.MESSAGE_MANAGE };
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_MANAGE
        };

        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent event) {
        var user = event.getAuthor();
        var master = new Master(user);

        var lang = LanguageHandler.getLanguage(event);
        var p = GuildHandler.getPrefix(event);

        if (event.getArgs().isEmpty()) {
            var description = String.format(LanguageHandler.get(lang, "editembed_description"), event.getSelfUser().getName());
            var usage = String.format(LanguageHandler.get(lang, "editembed_usage"), p, name, p, name);
            var hint = LanguageHandler.get(lang, "editembed_hint");
            event.reply(MessageUtil.createUsageEmbed(name, user, description, ownerCommand, userPermissions, aliases, usage, hint));
            return;
        }

        if (event.getMessage().getMentionedChannels().isEmpty()) {
            event.reactError();
            event.reply(String.format(LanguageHandler.get(lang, "editembed_missing_channel"), p));
            return;
        }

        if (event.getArgs().split(" ").length < 2) {
            event.reactError();
            event.reply(String.format(LanguageHandler.get(lang, "editembed_missing_message_id"), p));
            return;
        }

        var mentionedChannel = event.getMessage().getMentionedChannels().get(0);
        var messageId = event.getArgs().split(" ")[1];

        if (!Parser.isValidMessageId(mentionedChannel, messageId)) {
            event.reactError();
            event.reply(String.format(LanguageHandler.get(lang, "editembed_invalid_message_id"), p));
            return;
        }

        if (!mentionedChannel.canTalk(Objects.requireNonNull(event.getGuild().getMember(event.getAuthor())))) {
            event.replyError(LanguageHandler.get(lang, "editembed_missingpermission"));
            return;
        }

        mentionedChannel.retrieveMessageById(messageId).queue(embedMessage -> {
            if (!embedMessage.getAuthor().equals(event.getSelfUser())) {
                event.reactError();
                event.reply(LanguageHandler.get(lang, "editembed_notbyme"));
                return;
            }

            if (embedMessage.getEmbeds().isEmpty()) {
                event.reactError();
                event.reply(LanguageHandler.get(lang, "editembed_noembed"));
                return;
            }

            var messageEmbed = embedMessage.getEmbeds().get(0);
            var eb = new EmbedBuilder();

            eb.setColor(master.getColor());
            if (messageEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
            else eb.setAuthor(messageEmbed.getAuthor().getName(), messageEmbed.getAuthor().getUrl(), messageEmbed.getAuthor().getIconUrl());
            eb.setThumbnail(messageEmbed.getThumbnail() == null ? null : messageEmbed.getThumbnail().getUrl());
            eb.setTitle(messageEmbed.getTitle(), messageEmbed.getUrl());
            eb.setDescription(messageEmbed.getDescription());
            List<MessageEmbed.Field> fields = messageEmbed.getFields();
            for (MessageEmbed.Field field : fields)
                eb.addField(field.getName(), field.getValue(), field.isInline());
            eb.setImage(messageEmbed.getImage() == null ? null : messageEmbed.getImage().getUrl());
            if (messageEmbed.getFooter() == null) eb.setFooter(null, null);
            else eb.setFooter(messageEmbed.getFooter().getText(), messageEmbed.getFooter().getIconUrl());
            eb.setTimestamp(messageEmbed.getTimestamp());


            var channel = event.getChannel();
            channel.sendMessage(eb.build()).queue(message -> {
                var embedUser = new EmbedUser(message, message.getEmbeds().get(0));
                processIntroduction(channel, user, embedUser, event, embedMessage, lang);
            });
        }, failure -> {
            event.reply(LanguageHandler.get(lang, "editembed_notfound"));
            event.reactError();
        });
    }

    // Timeout
    private void timeout(Message botMessage, Message embedMessage, CommandEvent event, String lang) {
        event.reactWarning();
        botMessage.delete().queue();
        embedMessage.delete().queue();
        event.reply(LanguageHandler.get(lang, "embed_timeout"));
    }

    // Introduction
    private void processIntroduction(MessageChannel channel, User author, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        channel.sendMessage(String.format(LanguageHandler.get(lang, "editembed_introduction"), event.getSelfUser().getName())).queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept)) {
                            e.getReaction().removeReaction(author).queue();
                            processAuthorUsage(channel, author, message, embedUser, event, edit, lang);
                        } else {
                            embedUser.getMessage().delete().queue();
                            message.delete().queue();
                            event.reactWarning();
                        }
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    // Author
    private void processAuthorUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        previous.editMessage(LanguageHandler.get(lang, "embed_authorline_q")).queue(
                message -> waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                        e -> e.getUser().equals(author)
                                && (e.getReactionEmote().getName().equals(accept)
                                || e.getReactionEmote().getName().equals(decline)),
                        e -> {
                            if (e.getReactionEmote().getName().equals(accept))
                                processAuthorName(channel, author, message, embedUser, event, edit, lang);
                            else processNoAuthorLine(channel, author, message, embedUser, event, edit, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processAuthorName(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage(LanguageHandler.get(lang, "embed_authorname_i")).queue(
                message -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                        e -> e.getAuthor().equals(author)
                                && e.getChannel().equals(channel),
                        e -> {
                            var authorName = e.getMessage().getContentRaw();
                            e.getMessage().delete().queue();
                            var ogMessage = embedUser.getMessage();
                            var ogEmbed = embedUser.getEmbed();
                            var eb = new EmbedBuilder();
                            eb.setColor(ogEmbed.getColor());
                            var ogAuthor = ogEmbed.getAuthor();
                            eb.setAuthor(authorName, ogAuthor == null ? null : ogAuthor.getUrl(), ogAuthor == null ? null : ogAuthor.getIconUrl());
                            eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));
                            eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
                            eb.setDescription(ogEmbed.getDescription());
                            var fields = ogEmbed.getFields();
                            for (var field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());
                            eb.setImage(ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl());
                            if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
                            else
                                eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processAuthorUrlUsage(channel, author, message, embedUser, event, edit, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processAuthorUrlUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage(LanguageHandler.get(lang, "embed_authorurl_q")).queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept))
                            processAuthorUrl(channel, author, message, embedUser, event, false, edit, lang);
                        else processNoAuthorUrl(channel, author, message, embedUser, event, edit, lang);
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processAuthorUrl(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, boolean repeated, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage((repeated ? LanguageHandler.get(lang, "embed_authorurl_i_fail") : LanguageHandler.get(lang, "embed_authorurl_i"))).queue(
                message -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                        e -> e.getAuthor().equals(author)
                                && e.getChannel().equals(channel),
                        e -> {
                            var authorUrl = e.getMessage().getContentRaw();
                            e.getMessage().delete().queue();

                            // Redo if user didn't provide valid url.
                            if (!Parser.isValidUrl(authorUrl)) {
                                processAuthorUrl(channel, author, previous, embedUser, event, true, edit, lang);
                                return;
                            }

                            var ogMessage = embedUser.getMessage();
                            var ogEmbed = embedUser.getEmbed();
                            var eb = new EmbedBuilder();
                            eb.setColor(ogEmbed.getColor());
                            var ogAuthor = ogEmbed.getAuthor();
                            eb.setAuthor(ogAuthor == null ? "" : ogAuthor.getName(), authorUrl, ogAuthor == null ? null : ogAuthor.getIconUrl());
                            eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));
                            eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
                            eb.setDescription(ogEmbed.getDescription());
                            var fields = ogEmbed.getFields();
                            for (var field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());
                            eb.setImage(ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl());
                            if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
                            else
                                eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processAuthorIconUrlUsage(channel, author, message, embedUser, event, edit, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processNoAuthorUrl(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());
        var ogAuthor = ogEmbed.getAuthor();
        eb.setAuthor(ogAuthor == null ? "" : ogAuthor.getName(), null, ogAuthor == null ? null : ogAuthor.getIconUrl());
        eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));
        eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
        eb.setDescription(ogEmbed.getDescription());
        var fields = ogEmbed.getFields();
        for (var field : fields)
            eb.addField(field.getName(), field.getValue(), field.isInline());
        eb.setImage(ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl());
        if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
        else
            eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
        eb.setTimestamp(ogEmbed.getTimestamp());

        ogMessage.editMessage(eb.build()).queue();
        embedUser.setEmbed(eb.build());
        processAuthorIconUrlUsage(channel, author, previous, embedUser, event, edit, lang);
    }

    private void processAuthorIconUrlUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage(LanguageHandler.get(lang, "embed_authoricon_q")).queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept))
                            processAuthorIconUrl(channel, author, message, embedUser, event, false, edit, lang);
                        else processNoAuthorIconUrl(channel, author, message, embedUser, event, edit, lang);
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processAuthorIconUrl(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, boolean repeated, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage((repeated ? LanguageHandler.get(lang, "embed_authoricon_i_fail") : LanguageHandler.get(lang, "embed_authoricon_i"))).queue(
                message -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                        e -> e.getAuthor().equals(author)
                                && e.getChannel().equals(channel),
                        e -> {
                            var authorIconUrl = e.getMessage().getContentRaw();
                            e.getMessage().delete().queue();

                            // Redo if user didn't provide valid direct url.
                            if (!Parser.isValidDirectUrl(authorIconUrl)) {
                                processAuthorIconUrl(channel, author, message, embedUser, event, true, edit, lang);
                                return;
                            }

                            var ogMessage = embedUser.getMessage();
                            var ogEmbed = embedUser.getEmbed();
                            var eb = new EmbedBuilder();
                            eb.setColor(ogEmbed.getColor());
                            var ogAuthor = ogEmbed.getAuthor();
                            eb.setAuthor(ogAuthor == null ? "" : ogAuthor.getName(), ogAuthor == null ? "" : ogAuthor.getUrl(), authorIconUrl);
                            eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));
                            eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
                            eb.setDescription(ogEmbed.getDescription());
                            var fields = ogEmbed.getFields();
                            for (var field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());
                            eb.setImage(ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl());
                            if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
                            else
                                eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processThumbnailUsage(channel, author, message, embedUser, event, edit, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processNoAuthorIconUrl(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());
        var ogAuthor = ogEmbed.getAuthor();
        eb.setAuthor(ogAuthor == null ? "" : ogAuthor.getName(), ogAuthor == null ? "" : ogAuthor.getUrl(), null);
        eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));
        eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
        eb.setDescription(ogEmbed.getDescription());
        var fields = ogEmbed.getFields();
        for (var field : fields)
            eb.addField(field.getName(), field.getValue(), field.isInline());
        eb.setImage(ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl());
        if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
        else
            eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
        eb.setTimestamp(ogEmbed.getTimestamp());

        ogMessage.editMessage(eb.build()).queue();
        embedUser.setEmbed(eb.build());
        processThumbnailUsage(channel, author, previous, embedUser, event, edit, lang);
    }

    private void processNoAuthorLine(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());
        eb.setAuthor(null, null, null);
        eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));
        eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
        eb.setDescription(ogEmbed.getDescription());
        var fields = ogEmbed.getFields();
        for (var field : fields)
            eb.addField(field.getName(), field.getValue(), field.isInline());
        eb.setImage(ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl());
        if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
        else
            eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
        eb.setTimestamp(ogEmbed.getTimestamp());

        ogMessage.editMessage(eb.build()).queue();
        embedUser.setEmbed(eb.build());
        processThumbnailUsage(channel, author, previous, embedUser, event, edit, lang);
    }

    // Thumbnail
    private void processThumbnailUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage(LanguageHandler.get(lang, "embed_thumbnail_q")).queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept))
                            processThumbnail(channel, author, message, embedUser, event, false, edit, lang);
                        else processNoThumbnail(channel, author, message, embedUser, event, edit, lang);
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processThumbnail(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, boolean repeated, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage((repeated ? LanguageHandler.get(lang, "embed_thumbnail_i_fail") : LanguageHandler.get(lang, "embed_thumbnail_i"))).queue(
                message -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                        e -> e.getAuthor().equals(author)
                                && e.getChannel().equals(channel),
                        e -> {
                            var thumbnailUrl = e.getMessage().getContentRaw();
                            e.getMessage().delete().queue();

                            // Redo if user didn't provide valid direct url.
                            if (!Parser.isValidDirectUrl(thumbnailUrl)) {
                                processThumbnail(channel, author, message, embedUser, event, true, edit, lang);
                                return;
                            }

                            var ogMessage = embedUser.getMessage();
                            var ogEmbed = embedUser.getEmbed();
                            var eb = new EmbedBuilder();
                            eb.setColor(ogEmbed.getColor());

                            if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
                            else
                                eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

                            eb.setThumbnail(thumbnailUrl);
                            eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
                            eb.setDescription(ogEmbed.getDescription());
                            var fields = ogEmbed.getFields();
                            for (var field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());
                            eb.setImage(ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl());
                            if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
                            else
                                eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processTitleUsage(channel, author, message, embedUser, event, edit, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processNoThumbnail(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());

        if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
        else
            eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

        eb.setThumbnail(null);
        eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
        eb.setDescription(ogEmbed.getDescription());
        var fields = ogEmbed.getFields();
        for (var field : fields)
            eb.addField(field.getName(), field.getValue(), field.isInline());
        eb.setImage(ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl());
        if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
        else
            eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
        eb.setTimestamp(ogEmbed.getTimestamp());

        ogMessage.editMessage(eb.build()).queue();
        embedUser.setEmbed(eb.build());
        processTitleUsage(channel, author, previous, embedUser, event, edit, lang);
    }

    // Title
    private void processTitleUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage(LanguageHandler.get(lang, "embed_title_q")).queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept))
                            processTitle(channel, author, message, embedUser, event, edit, lang);
                        else processNoTitle(channel, author, message, embedUser, event, edit, lang);
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processTitle(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage(LanguageHandler.get(lang, "embed_title_i")).queue(
                message -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                        e -> e.getAuthor().equals(author)
                                && e.getChannel().equals(channel),
                        e -> {
                            var title = e.getMessage().getContentRaw();
                            e.getMessage().delete().queue();
                            var ogMessage = embedUser.getMessage();
                            var ogEmbed = embedUser.getEmbed();
                            var eb = new EmbedBuilder();
                            eb.setColor(ogEmbed.getColor());

                            if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
                            else
                                eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

                            eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

                            eb.setTitle(title, ogEmbed.getUrl());
                            eb.setDescription(ogEmbed.getDescription());
                            var fields = ogEmbed.getFields();
                            for (var field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());
                            eb.setImage(ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl());
                            if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
                            else
                                eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processTitleUrlUsage(channel, author, message, embedUser, event, edit, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processTitleUrlUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage(LanguageHandler.get(lang, "embed_url_q")).queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept))
                            processTitleUrl(channel, author, message, embedUser, event, false, edit, lang);
                        else processNoTitleUrl(channel, author, message, embedUser, event, edit, lang);
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processTitleUrl(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, boolean repeated, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage((repeated ? LanguageHandler.get(lang, "embed_url_i_fail") : LanguageHandler.get(lang, "embed_url_i"))).queue(
                message -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                        e -> e.getAuthor().equals(author)
                                && e.getChannel().equals(channel),
                        e -> {
                            var titleUrl = e.getMessage().getContentRaw();
                            e.getMessage().delete().queue();

                            // Redo if user didn't provide valid url.
                            if (!Parser.isValidUrl(titleUrl)) {
                                processTitleUrl(channel, author, message, embedUser, event, true, edit, lang);
                                return;
                            }

                            var ogMessage = embedUser.getMessage();
                            var ogEmbed = embedUser.getEmbed();
                            var eb = new EmbedBuilder();
                            eb.setColor(ogEmbed.getColor());

                            if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
                            else
                                eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

                            eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

                            eb.setTitle(ogEmbed.getTitle(), titleUrl);
                            eb.setDescription(ogEmbed.getDescription());
                            var fields = ogEmbed.getFields();
                            for (var field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());
                            eb.setImage(ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl());
                            if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
                            else
                                eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processDescriptionUsage(channel, author, message, embedUser, event, edit, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processNoTitleUrl(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());

        if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
        else
            eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

        eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

        eb.setTitle(ogEmbed.getTitle(), null);
        eb.setDescription(ogEmbed.getDescription());
        var fields = ogEmbed.getFields();
        for (var field : fields)
            eb.addField(field.getName(), field.getValue(), field.isInline());
        eb.setImage(ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl());
        if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
        else
            eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
        eb.setTimestamp(ogEmbed.getTimestamp());

        ogMessage.editMessage(eb.build()).queue();
        embedUser.setEmbed(eb.build());
        processDescriptionUsage(channel, author, previous, embedUser, event, edit, lang);
    }

    private void processNoTitle(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());

        if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
        else
            eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

        eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

        eb.setTitle(null, null);
        eb.setDescription(ogEmbed.getDescription());
        var fields = ogEmbed.getFields();
        for (var field : fields)
            eb.addField(field.getName(), field.getValue(), field.isInline());
        eb.setImage(ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl());
        if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
        else
            eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
        eb.setTimestamp(ogEmbed.getTimestamp());

        ogMessage.editMessage(eb.build()).queue();
        embedUser.setEmbed(eb.build());
        processDescriptionUsage(channel, author, previous, embedUser, event, edit, lang);
    }

    // Description
    private void processDescriptionUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage(LanguageHandler.get(lang, "embed_description_q")).queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept))
                            processDescription(channel, author, message, embedUser, event, edit, lang);
                        else processNoDescription(channel, author, message, embedUser, event, edit, lang);
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processDescription(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage(LanguageHandler.get(lang, "embed_description_i")).queue(
                message -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                        e -> e.getAuthor().equals(author)
                                && e.getChannel().equals(channel),
                        e -> {
                            var description = e.getMessage().getContentRaw();
                            e.getMessage().delete().queue();
                            var ogMessage = embedUser.getMessage();
                            var ogEmbed = embedUser.getEmbed();
                            var eb = new EmbedBuilder();
                            eb.setColor(ogEmbed.getColor());

                            if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
                            else
                                eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

                            eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

                            eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
                            eb.setDescription(description);
                            var fields = ogEmbed.getFields();
                            for (var field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());
                            eb.setImage(ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl());
                            if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
                            else
                                eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processFieldUsage(channel, author, message, embedUser, event, edit, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processNoDescription(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());

        if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
        else
            eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

        eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

        eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
        eb.setDescription(null);
        var fields = ogEmbed.getFields();
        for (var field : fields)
            eb.addField(field.getName(), field.getValue(), field.isInline());
        eb.setImage(ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl());
        if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
        else
            eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
        eb.setTimestamp(ogEmbed.getTimestamp());

        ogMessage.editMessage(eb.build()).queue();
        embedUser.setEmbed(eb.build());
        if (fields.isEmpty()) processFieldUsage(channel, author, previous, embedUser, event, edit, lang);
        else processFieldDeleteUsage(channel, author, previous, embedUser, event, edit, lang);
    }

    // Fields
    private void processFieldDeleteUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage(LanguageHandler.get(lang, "embed_field_remove_q")).queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept))
                            processFieldRemove(channel, author, message, embedUser, event, edit, lang);
                        else processFieldUsage(channel, author, message, embedUser, event, edit, lang);
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processFieldRemove(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());

        if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
        else eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

        eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

        eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
        eb.setDescription(ogEmbed.getDescription());
        // No fields
        if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
        else
            eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
        eb.setTimestamp(ogEmbed.getTimestamp());

        ogMessage.editMessage(eb.build()).queue();
        embedUser.setEmbed(eb.build());
        processFieldUsage(channel, author, previous, embedUser, event, edit, lang);
    }

    private void processFieldUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage(LanguageHandler.get(lang, "embed_field_q")).queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept))
                            processFieldName(channel, author, message, embedUser, event, edit, lang);
                        else processNoMoreFields(channel, author, message, embedUser, event, edit, lang);
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processFieldName(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage(LanguageHandler.get(lang, "embed_field_name_i")).queue(
                message -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                        e -> e.getAuthor().equals(author)
                                && e.getChannel().equals(channel),
                        e -> {
                            var fieldName = e.getMessage().getContentRaw();
                            e.getMessage().delete().queue();
                            var ogMessage = embedUser.getMessage();
                            var ogEmbed = embedUser.getEmbed();
                            var eb = new EmbedBuilder();
                            eb.setColor(ogEmbed.getColor());
                            if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
                            else
                                eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());
                            eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));
                            eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
                            eb.setDescription(ogEmbed.getDescription());

                            embedUser.setFieldCounter(embedUser.getFieldCounter() + 1);

                            var fields = ogEmbed.getFields();
                            if (!fields.isEmpty()) {
                                var field0 = fields.get(0);
                                if (field0.getName() != null && field0.getName().equalsIgnoreCase(LanguageHandler.get(lang, "createembed_field_name_inline")))
                                    fields = new ArrayList<>();
                            }

                            for (MessageEmbed.Field field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());

                            eb.addField(fieldName, "", false);

                            eb.setImage(ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl());
                            if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
                            else
                                eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processFieldValue(channel, author, message, embedUser, event, edit, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processFieldValue(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage(LanguageHandler.get(lang, "embed_field_value_i")).queue(
                message -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                        e -> e.getAuthor().equals(author)
                                && e.getChannel().equals(channel),
                        e -> {
                            var fieldValue = e.getMessage().getContentRaw();
                            e.getMessage().delete().queue();
                            var ogMessage = embedUser.getMessage();
                            var ogEmbed = embedUser.getEmbed();
                            var eb = new EmbedBuilder();
                            eb.setColor(ogEmbed.getColor());
                            if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
                            else
                                eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());
                            eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));
                            eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
                            eb.setDescription(ogEmbed.getDescription());

                            var fields = ogEmbed.getFields();
                            for (int i = 0; i < fields.size(); i++) {
                                if (i == fields.size() - 1)
                                    eb.addField(fields.get(i).getName(), fieldValue, fields.get(i).isInline());
                                else
                                    eb.addField(fields.get(i).getName(), fields.get(i).getValue(), fields.get(i).isInline());
                            }

                            eb.setImage(ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl());
                            if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
                            else
                                eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processFieldInline(channel, author, message, embedUser, event, edit, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processFieldInline(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage(LanguageHandler.get(lang, "embed_field_inline_i")).queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        var fieldInline = e.getReactionEmote().getName().equals(accept);
                        var ogMessage = embedUser.getMessage();
                        var ogEmbed = embedUser.getEmbed();
                        var eb = new EmbedBuilder();
                        eb.setColor(ogEmbed.getColor());
                        if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
                        else
                            eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());
                        eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));
                        eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
                        eb.setDescription(ogEmbed.getDescription());

                        var fields = ogEmbed.getFields();
                        for (int i = 0; i < fields.size(); i++) {
                            if (i == fields.size() - 1)
                                eb.addField(fields.get(i).getName(), fields.get(i).getValue(), fieldInline);
                            else
                                eb.addField(fields.get(i).getName(), fields.get(i).getValue(), fields.get(i).isInline());
                        }

                        eb.setImage(ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl());
                        if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
                        else
                            eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
                        eb.setTimestamp(ogEmbed.getTimestamp());

                        ogMessage.editMessage(eb.build()).queue();
                        embedUser.setEmbed(eb.build());
                        if (embedUser.getFieldCounter() == 25)
                            processImageUsage(channel, author, message, embedUser, event, edit, lang);
                        else processFieldUsage(channel, author, message, embedUser, event, edit, lang);
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processNoMoreFields(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());

        var fields = ogEmbed.getFields();
        if (!fields.isEmpty()) {
            var field0 = fields.get(0);
            if (field0.getName() != null && field0.getName().equalsIgnoreCase(LanguageHandler.get(lang, "createembed_field_name_inline")))
                fields = new ArrayList<>();
        }

        if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
        else
            eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

        eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

        eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
        eb.setDescription(ogEmbed.getDescription());
        for (MessageEmbed.Field field : fields) eb.addField(field.getName(), field.getValue(), field.isInline());
        eb.setImage(ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl());
        if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
        else
            eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
        eb.setTimestamp(ogEmbed.getTimestamp());

        ogMessage.editMessage(eb.build()).queue();
        embedUser.setEmbed(eb.build());
        processImageUsage(channel, author, previous, embedUser, event, edit, lang);
    }

    // Image
    private void processImageUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage(LanguageHandler.get(lang, "embed_image_q")).queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept))
                            processImage(channel, author, message, embedUser, event, false, edit, lang);
                        else processNoImage(channel, author, message, embedUser, event, edit, lang);
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processImage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, boolean repeated, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage((repeated ? LanguageHandler.get(lang, "embed_image_i_fail") : LanguageHandler.get(lang, "embed_image_i"))).queue(
                message -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                        e -> e.getAuthor().equals(author)
                                && e.getChannel().equals(channel),
                        e -> {
                            var imageUrl = e.getMessage().getContentRaw();
                            e.getMessage().delete().queue();

                            // Redo if user didn't provide valid direct url.
                            if (!Parser.isValidDirectUrl(imageUrl)) {
                                processImage(channel, author, message, embedUser, event, true, edit, lang);
                                return;
                            }

                            var ogMessage = embedUser.getMessage();
                            var ogEmbed = embedUser.getEmbed();
                            var eb = new EmbedBuilder();
                            eb.setColor(ogEmbed.getColor());

                            if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
                            else
                                eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

                            eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

                            eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
                            eb.setDescription(ogEmbed.getDescription());
                            var fields = ogEmbed.getFields();
                            for (var field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());
                            eb.setImage(imageUrl);
                            if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
                            else
                                eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processFooterUsage(channel, author, message, embedUser, event, edit, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processNoImage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());

        if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
        else
            eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

        eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

        eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
        eb.setDescription(ogEmbed.getDescription());
        var fields = ogEmbed.getFields();
        for (var field : fields)
            eb.addField(field.getName(), field.getValue(), field.isInline());
        eb.setImage(null);
        if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
        else
            eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
        eb.setTimestamp(ogEmbed.getTimestamp());

        ogMessage.editMessage(eb.build()).queue();
        embedUser.setEmbed(eb.build());
        processFooterUsage(channel, author, previous, embedUser, event, edit, lang);
    }

    // Footer
    private void processFooterUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage(LanguageHandler.get(lang, "embed_footer_q")).queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept))
                            processFooterText(channel, author, message, embedUser, event, edit, lang);
                        else processNoFooter(channel, author, message, embedUser, event, edit, lang);
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processFooterText(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage(LanguageHandler.get(lang, "embed_footer_text_i")).queue(
                message -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                        e -> e.getAuthor().equals(author)
                                && e.getChannel().equals(channel),
                        e -> {
                            var footerText = e.getMessage().getContentRaw();
                            e.getMessage().delete().queue();
                            var ogMessage = embedUser.getMessage();
                            var ogEmbed = embedUser.getEmbed();
                            var eb = new EmbedBuilder();
                            eb.setColor(ogEmbed.getColor());

                            if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
                            else
                                eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

                            eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

                            eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
                            eb.setDescription(ogEmbed.getDescription());
                            var fields = ogEmbed.getFields();
                            for (var field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());
                            eb.setImage((ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl()));
                            eb.setFooter(footerText, (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processFooterIconUrlUsage(channel, author, message, embedUser, event, edit, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processFooterIconUrlUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage(LanguageHandler.get(lang, "embed_footer_icon_q")).queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept))
                            processFooterIconUrl(channel, author, message, embedUser, event, false, edit, lang);
                        else processNoFooterIconUrl(channel, author, message, embedUser, event, edit, lang);
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processFooterIconUrl(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, boolean repeated, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage((repeated ? LanguageHandler.get(lang, "embed_footer_icon_i_fail") : LanguageHandler.get(lang, "embed_footer_icon_i"))).queue(
                message -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                        e -> e.getAuthor().equals(author)
                                && e.getChannel().equals(channel),
                        e -> {
                            var footerIconUrl = e.getMessage().getContentRaw();
                            e.getMessage().delete().queue();

                            // Redo if user didn't provide valid direct url.
                            if (!Parser.isValidDirectUrl(footerIconUrl)) {
                                processFooterIconUrl(channel, author, message, embedUser, event, true, edit, lang);
                                return;
                            }

                            var ogMessage = embedUser.getMessage();
                            var ogEmbed = embedUser.getEmbed();
                            var eb = new EmbedBuilder();
                            eb.setColor(ogEmbed.getColor());

                            if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
                            else
                                eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

                            eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

                            eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
                            eb.setDescription(ogEmbed.getDescription());
                            var fields = ogEmbed.getFields();
                            for (var field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());
                            eb.setImage((ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl()));
                            var footer = ogEmbed.getFooter();
                            eb.setFooter(footer == null ? "" : footer.getText(), footerIconUrl);
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processTimestampUsage(channel, author, message, embedUser, event, edit, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processNoFooterIconUrl(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());

        if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
        else
            eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

        eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

        eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
        eb.setDescription(ogEmbed.getDescription());
        var fields = ogEmbed.getFields();
        for (var field : fields)
            eb.addField(field.getName(), field.getValue(), field.isInline());
        eb.setImage((ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl()));
        var footer = ogEmbed.getFooter();
        eb.setFooter(footer == null ? "" : footer.getText(), null);
        eb.setTimestamp(ogEmbed.getTimestamp());

        ogMessage.editMessage(eb.build()).queue();
        embedUser.setEmbed(eb.build());
        processTimestampUsage(channel, author, previous, embedUser, event, edit, lang);
    }

    private void processNoFooter(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());

        if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
        else
            eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

        eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

        eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
        eb.setDescription(ogEmbed.getDescription());
        var fields = ogEmbed.getFields();
        for (var field : fields)
            eb.addField(field.getName(), field.getValue(), field.isInline());
        eb.setImage((ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl()));
        eb.setFooter(null, null);
        eb.setTimestamp(ogEmbed.getTimestamp());

        try {
            ogMessage.editMessage(eb.build()).queue();
        } catch (IllegalArgumentException e) {
            event.reactError();
            ogMessage.delete().queue();
            previous.clearReactions().queue();
            previous.editMessage(LanguageHandler.get(lang, "embed_empty")).queue();
            return;
        }
        embedUser.setEmbed(eb.build());
        processTimestampUsage(channel, author, previous, embedUser, event, edit, lang);
    }

    // Timestamp
    private void processTimestampUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage(LanguageHandler.get(lang, "embed_timestamp_q")).queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept))
                            processTimestamp(channel, author, message, embedUser, event, false, edit, lang);
                        else processNoTimestamp(author, message, embedUser, event, edit, lang);
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processTimestamp(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, boolean repeated, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage((repeated ? LanguageHandler.get(lang, "embed_timestamp_i_fail") : LanguageHandler.get(lang, "embed_timestamp_i"))).queue(
                message -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                        e -> e.getAuthor().equals(author)
                                && e.getChannel().equals(channel),
                        e -> {
                            var timestamp = e.getMessage().getContentRaw();
                            e.getMessage().delete().queue();

                            // Redo if user didn't provide valid direct url.
                            if (!Parser.isValidDateTime(timestamp) && !timestamp.equalsIgnoreCase("now")) {
                                processTimestamp(channel, author, message, embedUser, event, true, edit, lang);
                                return;
                            }

                            var ogMessage = embedUser.getMessage();
                            var ogEmbed = embedUser.getEmbed();
                            var eb = new EmbedBuilder();
                            eb.setColor(ogEmbed.getColor());

                            if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
                            else
                                eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

                            eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

                            eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
                            eb.setDescription(ogEmbed.getDescription());
                            List<MessageEmbed.Field> fields = ogEmbed.getFields();
                            for (var field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());
                            eb.setImage((ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl()));
                            if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
                            else
                                eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
                            eb.setTimestamp((timestamp.equalsIgnoreCase("now") ?
                                    OffsetDateTime.now() :
                                    OffsetDateTime.of(
                                            LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                                            ZoneOffset.of(embedUser.getOffset().equals("00:00") ? "Z" : embedUser.getOffset())
                                    )));

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processFinishUsage(author, message, embedUser, event, edit, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processNoTimestamp(User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());

        if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
        else
            eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

        eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

        eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
        eb.setDescription(ogEmbed.getDescription());
        List<MessageEmbed.Field> fields = ogEmbed.getFields();
        for (var field : fields)
            eb.addField(field.getName(), field.getValue(), field.isInline());
        eb.setImage((ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl()));
        if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
        else
            eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
        eb.setTimestamp(null);

        ogMessage.editMessage(eb.build()).queue();
        embedUser.setEmbed(eb.build());
        processFinishUsage(author, previous, embedUser, event, edit, lang);
    }

    // Finish
    private void processFinishUsage(User author, Message previous, EmbedUser embedUser, CommandEvent event, Message edit, String lang) {
        previous.clearReactions().queue();
        previous.editMessage(LanguageHandler.get(lang, "editembed_confirm")).queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept))
                            processFinish(message, embedUser, event, edit);
                        else
                            processNoFinish(event, message, embedUser.getMessage());
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processFinish(Message previous, EmbedUser embedUser, CommandEvent event, Message edit) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());

        if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
        else
            eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());
        eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));
        eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
        eb.setDescription(ogEmbed.getDescription());
        List<MessageEmbed.Field> fields = ogEmbed.getFields();
        for (var field : fields)
            eb.addField(field.getName(), field.getValue(), field.isInline());
        eb.setImage((ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl()));
        if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
        else
            eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
        eb.setTimestamp(ogEmbed.getTimestamp());

        edit.editMessage(eb.build()).queue();

        previous.delete().queue();
        ogMessage.delete().queue();
        event.reactSuccess();
    }

    private void processNoFinish(CommandEvent event, Message botMessage, Message embedMessage) {
        event.reactError();
        botMessage.delete().queue();
        embedMessage.delete().queue();
    }
}
