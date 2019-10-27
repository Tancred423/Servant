// Author: Tancred423 (https://github.com/Tancred423)
package fun.embed;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import servant.Log;
import servant.Servant;
import utilities.Constants;
import utilities.Parser;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CreateEmbedCommand extends Command {
    private final EventWaiter waiter;
    private String accept = "✅";
    private String decline = "❌";

    public CreateEmbedCommand(EventWaiter waiter) {
        this.name = "createembed";
        this.aliases = new String[]{"embed"};
        this.help = "Create an embed.";
        this.category = new Category("Fun");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_MANAGE};

        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, name)) return;

            var author = event.getAuthor();
            var internalAuthor = new moderation.user.User(author.getIdLong());
            var eb = new EmbedBuilder();

            var lang = LanguageHandler.getLanguage(event, name);

            try {
                eb.setColor(internalAuthor.getColor());
            } catch (SQLException e) {
                eb.setColor(Color.decode(Servant.config.getDefaultColorCode()));
            }
            eb.setAuthor(LanguageHandler.get(lang, "createembed_author_name"), "https://google.com/", author.getAvatarUrl());
            eb.setThumbnail("https://i.imgur.com/wAcLhfY.png");
            eb.setTitle(LanguageHandler.get(lang, "createembed_title"), "https://google.com/");
            eb.setDescription(LanguageHandler.get(lang, "createembed_description"));
            eb.addField(LanguageHandler.get(lang, "createembed_field_name_inline"), LanguageHandler.get(lang, "createembed_field_value1"), true);
            eb.addField(LanguageHandler.get(lang, "createembed_field_name_inline"), LanguageHandler.get(lang, "createembed_field_value2"), true);
            eb.addField(LanguageHandler.get(lang, "createembed_field_name_inline"), LanguageHandler.get(lang, "createembed_field_value3"), true);
            eb.addField(LanguageHandler.get(lang, "createembed_field_name_noninline"), LanguageHandler.get(lang, "createembed_field_value_noninline"), false);
            eb.setImage("https://i.imgur.com/9G46UQx.png");
            eb.setFooter(LanguageHandler.get(lang, "createembed_footer"), event.getSelfUser().getAvatarUrl());
            eb.setTimestamp(OffsetDateTime.now());


            var channel = event.getChannel();
            channel.sendMessage(eb.build()).queue(message -> {
                EmbedUser embedUser;
                try {
                    embedUser = new EmbedUser(message, message.getEmbeds().get(0));
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), author, name, event).sendLog(true);
                    return;
                }
                processIntroduction(channel, author, embedUser, event, lang);
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
    private void timeout(Message botMessage, Message embedMessage, CommandEvent event, String lang) {
        event.reactWarning();
        botMessage.delete().queue();
        embedMessage.delete().queue();
        event.reply(LanguageHandler.get(lang, "embed_timeout"));
    }

    // Introduction
    private void processIntroduction(MessageChannel channel, User author, EmbedUser embedUser, CommandEvent event, String lang) {
        channel.sendMessage(LanguageHandler.get(lang, "createembed_introduction")).queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept)) {
                            e.getReaction().removeReaction(author).queue();
                            processAuthorUsage(channel, author, message, embedUser, event, lang);
                        } else {
                            embedUser.getMessage().delete().queue();
                            message.delete().queue();
                            event.reactWarning();
                        }
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    // Author
    private void processAuthorUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
        previous.editMessage(LanguageHandler.get(lang, "embed_authorline_q")).queue(
                message -> waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                        e -> e.getUser().equals(author)
                                && (e.getReactionEmote().getName().equals(accept)
                                || e.getReactionEmote().getName().equals(decline)),
                        e -> {
                            if (e.getReactionEmote().getName().equals(accept))
                                processAuthorName(channel, author, message, embedUser, event, lang);
                            else processNoAuthorLine(channel, author, message, embedUser, event, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processAuthorName(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
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
                            eb.setAuthor(authorName, ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());
                            eb.setThumbnail(ogEmbed.getThumbnail().getUrl());
                            eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
                            eb.setDescription(ogEmbed.getDescription());
                            List<MessageEmbed.Field> fields = ogEmbed.getFields();
                            for (var field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());
                            eb.setImage(ogEmbed.getImage().getUrl());
                            eb.setFooter(ogEmbed.getFooter().getText(), ogEmbed.getFooter().getIconUrl());
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processAuthorUrlUsage(channel, author, message, embedUser, event, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processAuthorUrlUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
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
                            processAuthorUrl(channel, author, message, embedUser, event, false, lang);
                        else processNoAuthorUrl(channel, author, message, embedUser, event, lang);
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processAuthorUrl(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, boolean repeated, String lang) {
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
                                processAuthorUrl(channel, author, previous, embedUser, event, true, lang);
                                return;
                            }

                            var ogMessage = embedUser.getMessage();
                            var ogEmbed = embedUser.getEmbed();
                            var eb = new EmbedBuilder();
                            eb.setColor(ogEmbed.getColor());
                            eb.setAuthor(ogEmbed.getAuthor().getName(), authorUrl, ogEmbed.getAuthor().getIconUrl());
                            eb.setThumbnail(ogEmbed.getThumbnail().getUrl());
                            eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
                            eb.setDescription(ogEmbed.getDescription());
                            List<MessageEmbed.Field> fields = ogEmbed.getFields();
                            for (var field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());
                            eb.setImage(ogEmbed.getImage().getUrl());
                            eb.setFooter(ogEmbed.getFooter().getText(), ogEmbed.getFooter().getIconUrl());
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processAuthorIconUrlUsage(channel, author, message, embedUser, event, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processNoAuthorUrl(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());
        eb.setAuthor(ogEmbed.getAuthor().getName(), null, ogEmbed.getAuthor().getIconUrl());
        eb.setThumbnail(ogEmbed.getThumbnail().getUrl());
        eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
        eb.setDescription(ogEmbed.getDescription());
        List<MessageEmbed.Field> fields = ogEmbed.getFields();
        for (var field : fields) eb.addField(field.getName(), field.getValue(), field.isInline());
        eb.setImage(ogEmbed.getImage().getUrl());
        eb.setFooter(ogEmbed.getFooter().getText(), ogEmbed.getFooter().getIconUrl());
        eb.setTimestamp(ogEmbed.getTimestamp());

        ogMessage.editMessage(eb.build()).queue();
        embedUser.setEmbed(eb.build());
        processAuthorIconUrlUsage(channel, author, previous, embedUser, event, lang);
    }

    private void processAuthorIconUrlUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
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
                            processAuthorIconUrl(channel, author, message, embedUser, event, false, lang);
                        else processNoAuthorIconUrl(channel, author, message, embedUser, event, lang);
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processAuthorIconUrl(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, boolean repeated, String lang) {
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
                                processAuthorIconUrl(channel, author, message, embedUser, event, true, lang);
                                return;
                            }

                            var ogMessage = embedUser.getMessage();
                            var ogEmbed = embedUser.getEmbed();
                            var eb = new EmbedBuilder();
                            eb.setColor(ogEmbed.getColor());
                            eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), authorIconUrl);
                            eb.setThumbnail(ogEmbed.getThumbnail().getUrl());
                            eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
                            eb.setDescription(ogEmbed.getDescription());
                            List<MessageEmbed.Field> fields = ogEmbed.getFields();
                            for (var field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());
                            eb.setImage(ogEmbed.getImage().getUrl());
                            eb.setFooter(ogEmbed.getFooter().getText(), ogEmbed.getFooter().getIconUrl());
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processThumbnailUsage(channel, author, message, embedUser, event, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processNoAuthorIconUrl(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());
        eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), null);
        eb.setThumbnail(ogEmbed.getThumbnail().getUrl());
        eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
        eb.setDescription(ogEmbed.getDescription());
        List<MessageEmbed.Field> fields = ogEmbed.getFields();
        for (var field : fields)
            eb.addField(field.getName(), field.getValue(), field.isInline());
        eb.setImage(ogEmbed.getImage().getUrl());
        eb.setFooter(ogEmbed.getFooter().getText(), ogEmbed.getFooter().getIconUrl());
        eb.setTimestamp(ogEmbed.getTimestamp());

        ogMessage.editMessage(eb.build()).queue();
        embedUser.setEmbed(eb.build());
        processThumbnailUsage(channel, author, previous, embedUser, event, lang);
    }

    private void processNoAuthorLine(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());
        eb.setAuthor(null, null, null);
        eb.setThumbnail(ogEmbed.getThumbnail().getUrl());
        eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
        eb.setDescription(ogEmbed.getDescription());
        List<MessageEmbed.Field> fields = ogEmbed.getFields();
        for (var field : fields)
            eb.addField(field.getName(), field.getValue(), field.isInline());
        eb.setImage(ogEmbed.getImage().getUrl());
        eb.setFooter(ogEmbed.getFooter().getText(), ogEmbed.getFooter().getIconUrl());
        eb.setTimestamp(ogEmbed.getTimestamp());

        ogMessage.editMessage(eb.build()).queue();
        embedUser.setEmbed(eb.build());
        processThumbnailUsage(channel, author, previous, embedUser, event, lang);
    }

    // Thumbnail
    private void processThumbnailUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
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
                            processThumbnail(channel, author, message, embedUser, event, false, lang);
                        else processNoThumbnail(channel, author, message, embedUser, event, lang);
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processThumbnail(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, boolean repeated, String lang) {
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
                                processThumbnail(channel, author, message, embedUser, event, true, lang);
                                return;
                            }

                            var ogMessage = embedUser.getMessage();
                            var ogEmbed = embedUser.getEmbed();
                            var eb = new EmbedBuilder();
                            eb.setColor(ogEmbed.getColor());

                            if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
                            else eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

                            eb.setThumbnail(thumbnailUrl);
                            eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
                            eb.setDescription(ogEmbed.getDescription());
                            List<MessageEmbed.Field> fields = ogEmbed.getFields();
                            for (var field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());
                            eb.setImage(ogEmbed.getImage().getUrl());
                            eb.setFooter(ogEmbed.getFooter().getText(), ogEmbed.getFooter().getIconUrl());
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processTitleUsage(channel, author, message, embedUser, event, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processNoThumbnail(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());

        if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
        else eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

        eb.setThumbnail(null);
        eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
        eb.setDescription(ogEmbed.getDescription());
        List<MessageEmbed.Field> fields = ogEmbed.getFields();
        for (var field : fields)
            eb.addField(field.getName(), field.getValue(), field.isInline());
        eb.setImage(ogEmbed.getImage().getUrl());
        eb.setFooter(ogEmbed.getFooter().getText(), ogEmbed.getFooter().getIconUrl());
        eb.setTimestamp(ogEmbed.getTimestamp());

        ogMessage.editMessage(eb.build()).queue();
        embedUser.setEmbed(eb.build());
        processTitleUsage(channel, author, previous, embedUser, event, lang);
    }

    // Title
    private void processTitleUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
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
                            processTitle(channel, author, message, embedUser, event, lang);
                        else processNoTitle(channel, author, message, embedUser, event, lang);
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processTitle(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
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
                            else eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

                            eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

                            eb.setTitle(title, ogEmbed.getUrl());
                            eb.setDescription(ogEmbed.getDescription());
                            List<MessageEmbed.Field> fields = ogEmbed.getFields();
                            for (var field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());
                            eb.setImage(ogEmbed.getImage().getUrl());
                            eb.setFooter(ogEmbed.getFooter().getText(), ogEmbed.getFooter().getIconUrl());
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processTitleUrlUsage(channel, author, message, embedUser, event, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processTitleUrlUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
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
                            processTitleUrl(channel, author, message, embedUser, event, false, lang);
                        else processNoTitleUrl(channel, author, message, embedUser, event, lang);
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processTitleUrl(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, boolean repeated, String lang) {
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
                                processTitleUrl(channel, author, message, embedUser, event, true, lang);
                                return;
                            }

                            var ogMessage = embedUser.getMessage();
                            var ogEmbed = embedUser.getEmbed();
                            var eb = new EmbedBuilder();
                            eb.setColor(ogEmbed.getColor());

                            if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
                            else eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

                            eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

                            eb.setTitle(ogEmbed.getTitle(), titleUrl);
                            eb.setDescription(ogEmbed.getDescription());
                            List<MessageEmbed.Field> fields = ogEmbed.getFields();
                            for (var field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());
                            eb.setImage(ogEmbed.getImage().getUrl());
                            eb.setFooter(ogEmbed.getFooter().getText(), ogEmbed.getFooter().getIconUrl());
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processDescriptionUsage(channel, author, message, embedUser, event, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processNoTitleUrl(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());

        if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
        else eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

        eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

        eb.setTitle(ogEmbed.getTitle(), null);
        eb.setDescription(ogEmbed.getDescription());
        List<MessageEmbed.Field> fields = ogEmbed.getFields();
        for (var field : fields)
            eb.addField(field.getName(), field.getValue(), field.isInline());
        eb.setImage(ogEmbed.getImage().getUrl());
        eb.setFooter(ogEmbed.getFooter().getText(), ogEmbed.getFooter().getIconUrl());
        eb.setTimestamp(ogEmbed.getTimestamp());

        ogMessage.editMessage(eb.build()).queue();
        embedUser.setEmbed(eb.build());
        processDescriptionUsage(channel, author, previous, embedUser, event, lang);
    }

    private void processNoTitle(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());

        if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
        else eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

        eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

        eb.setTitle(null, null);
        eb.setDescription(ogEmbed.getDescription());
        List<MessageEmbed.Field> fields = ogEmbed.getFields();
        for (var field : fields)
            eb.addField(field.getName(), field.getValue(), field.isInline());
        eb.setImage(ogEmbed.getImage().getUrl());
        eb.setFooter(ogEmbed.getFooter().getText(), ogEmbed.getFooter().getIconUrl());
        eb.setTimestamp(ogEmbed.getTimestamp());

        ogMessage.editMessage(eb.build()).queue();
        embedUser.setEmbed(eb.build());
        processDescriptionUsage(channel, author, previous, embedUser, event, lang);
    }

    // Description
    private void processDescriptionUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
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
                            processDescription(channel, author, message, embedUser, event, lang);
                        else processNoDescription(channel, author, message, embedUser, event, lang);
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processDescription(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
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
                            else eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

                            eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

                            eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
                            eb.setDescription(description);
                            List<MessageEmbed.Field> fields = ogEmbed.getFields();
                            for (var field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());
                            eb.setImage(ogEmbed.getImage().getUrl());
                            eb.setFooter(ogEmbed.getFooter().getText(), ogEmbed.getFooter().getIconUrl());
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processFieldUsage(channel, author, message, embedUser, event, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processNoDescription(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());

        if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
        else eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

        eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

        eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
        eb.setDescription(null);
        List<MessageEmbed.Field> fields = ogEmbed.getFields();
        for (var field : fields)
            eb.addField(field.getName(), field.getValue(), field.isInline());
        eb.setImage(ogEmbed.getImage().getUrl());
        eb.setFooter(ogEmbed.getFooter().getText(), ogEmbed.getFooter().getIconUrl());
        eb.setTimestamp(ogEmbed.getTimestamp());

        ogMessage.editMessage(eb.build()).queue();
        embedUser.setEmbed(eb.build());
        processFieldUsage(channel, author, previous, embedUser, event, lang);
    }

    // Fields
    private void processFieldUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
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
                            processFieldName(channel, author, message, embedUser, event, lang);
                        else processNoMoreFields(channel, author, message, embedUser, event, lang);
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processFieldName(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
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
                            else eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());
                            eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));
                            eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
                            eb.setDescription(ogEmbed.getDescription());

                            embedUser.setFieldCounter(embedUser.getFieldCounter() + 1);

                            List<MessageEmbed.Field> fields = ogEmbed.getFields();
                            if (fields == null) fields = new ArrayList<>();
                            if (!fields.isEmpty() && fields.get(0).getName().equalsIgnoreCase(LanguageHandler.get(lang, "createembed_field_name_inline"))) fields = new ArrayList<>();

                            for (MessageEmbed.Field field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());

                            eb.addField(fieldName, "", false);

                            eb.setImage(ogEmbed.getImage().getUrl());
                            eb.setFooter(ogEmbed.getFooter().getText(), ogEmbed.getFooter().getIconUrl());
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processFieldValue(channel, author, message, embedUser, event, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processFieldValue(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
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
                            else eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());
                            eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));
                            eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
                            eb.setDescription(ogEmbed.getDescription());

                            List<MessageEmbed.Field> fields = ogEmbed.getFields();
                            for (int i = 0; i < fields.size(); i++) {
                                if (i == fields.size() - 1) eb.addField(fields.get(i).getName(), fieldValue, fields.get(i).isInline());
                                else eb.addField(fields.get(i).getName(), fields.get(i).getValue(), fields.get(i).isInline());
                            }

                            eb.setImage(ogEmbed.getImage().getUrl());
                            eb.setFooter(ogEmbed.getFooter().getText(), ogEmbed.getFooter().getIconUrl());
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processFieldInline(channel, author, message, embedUser, event, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processFieldInline(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
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
                        else eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());
                        eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));
                        eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
                        eb.setDescription(ogEmbed.getDescription());

                        List<MessageEmbed.Field> fields = ogEmbed.getFields();
                        for (int i = 0; i < fields.size(); i++) {
                            if (i == fields.size() - 1) eb.addField(fields.get(i).getName(), fields.get(i).getValue(), fieldInline);
                            else eb.addField(fields.get(i).getName(), fields.get(i).getValue(), fields.get(i).isInline());
                        }

                        eb.setImage(ogEmbed.getImage().getUrl());
                        eb.setFooter(ogEmbed.getFooter().getText(), ogEmbed.getFooter().getIconUrl());
                        eb.setTimestamp(ogEmbed.getTimestamp());

                        ogMessage.editMessage(eb.build()).queue();
                        embedUser.setEmbed(eb.build());
                        if (embedUser.getFieldCounter() == 25) processImageUsage(channel, author, message, embedUser, event, lang);
                        else processFieldUsage(channel, author, message, embedUser, event, lang);
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processNoMoreFields(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());

        List<MessageEmbed.Field> fields = ogEmbed.getFields();
        if (!fields.isEmpty() && fields.get(0).getName().equalsIgnoreCase(LanguageHandler.get(lang, "createembed_field_name_inline"))) fields = new ArrayList<>();

        if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
        else eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

        eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

        eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
        eb.setDescription(ogEmbed.getDescription());
        for (MessageEmbed.Field field : fields) eb.addField(field.getName(), field.getValue(), field.isInline());
        eb.setImage(ogEmbed.getImage().getUrl());
        eb.setFooter(ogEmbed.getFooter().getText(), ogEmbed.getFooter().getIconUrl());
        eb.setTimestamp(ogEmbed.getTimestamp());

        ogMessage.editMessage(eb.build()).queue();
        embedUser.setEmbed(eb.build());
        processImageUsage(channel, author, previous, embedUser, event, lang);
    }

    // Image
    private void processImageUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
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
                            processImage(channel, author, message, embedUser, event, false, lang);
                        else processNoImage(channel, author, message, embedUser, event, lang);
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processImage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, boolean repeated, String lang) {
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
                                processImage(channel, author, message, embedUser, event, true, lang);
                                return;
                            }

                            var ogMessage = embedUser.getMessage();
                            var ogEmbed = embedUser.getEmbed();
                            var eb = new EmbedBuilder();
                            eb.setColor(ogEmbed.getColor());

                            if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
                            else eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

                            eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

                            eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
                            eb.setDescription(ogEmbed.getDescription());
                            List<MessageEmbed.Field> fields = ogEmbed.getFields();
                            for (var field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());
                            eb.setImage(imageUrl);
                            eb.setFooter(ogEmbed.getFooter().getText(), ogEmbed.getFooter().getIconUrl());
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processFooterUsage(channel, author, message, embedUser, event, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processNoImage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());

        if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
        else eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

        eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

        eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
        eb.setDescription(ogEmbed.getDescription());
        List<MessageEmbed.Field> fields = ogEmbed.getFields();
        for (var field : fields)
            eb.addField(field.getName(), field.getValue(), field.isInline());
        eb.setImage(null);
        eb.setFooter(ogEmbed.getFooter().getText(), ogEmbed.getFooter().getIconUrl());
        eb.setTimestamp(ogEmbed.getTimestamp());

        ogMessage.editMessage(eb.build()).queue();
        embedUser.setEmbed(eb.build());
        processFooterUsage(channel, author, previous, embedUser, event, lang);
    }

    // Footer
    private void processFooterUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
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
                            processFooterText(channel, author, message, embedUser, event, lang);
                        else processNoFooter(channel, author, message, embedUser, event, lang);
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processFooterText(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
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
                            else eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

                            eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

                            eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
                            eb.setDescription(ogEmbed.getDescription());
                            List<MessageEmbed.Field> fields = ogEmbed.getFields();
                            for (var field : fields) eb.addField(field.getName(), field.getValue(), field.isInline());
                            eb.setImage((ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl()));
                            eb.setFooter(footerText, ogEmbed.getFooter().getIconUrl());
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processFooterIconUrlUsage(channel, author, message, embedUser, event, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processFooterIconUrlUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
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
                            processFooterIconUrl(channel, author, message, embedUser, event, false, lang);
                        else processNoFooterIconUrl(channel, author, message, embedUser, event, lang);
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processFooterIconUrl(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, boolean repeated, String lang) {
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
                                processFooterIconUrl(channel, author, message, embedUser, event, true, lang);
                                return;
                            }

                            var ogMessage = embedUser.getMessage();
                            var ogEmbed = embedUser.getEmbed();
                            var eb = new EmbedBuilder();
                            eb.setColor(ogEmbed.getColor());

                            if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
                            else eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

                            eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

                            eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
                            eb.setDescription(ogEmbed.getDescription());
                            List<MessageEmbed.Field> fields = ogEmbed.getFields();
                            for (var field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());
                            eb.setImage((ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl()));
                            eb.setFooter(ogEmbed.getFooter().getText(), footerIconUrl);
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processTimestampUsage(channel, author, message, embedUser, event, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processNoFooterIconUrl(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());

        if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
        else eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

        eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

        eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
        eb.setDescription(ogEmbed.getDescription());
        List<MessageEmbed.Field> fields = ogEmbed.getFields();
        for (var field : fields)
            eb.addField(field.getName(), field.getValue(), field.isInline());
        eb.setImage((ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl()));
        eb.setFooter(ogEmbed.getFooter().getText(), null);
        eb.setTimestamp(ogEmbed.getTimestamp());

        ogMessage.editMessage(eb.build()).queue();
        embedUser.setEmbed(eb.build());
        processTimestampUsage(channel, author, previous, embedUser, event, lang);
    }

    private void processNoFooter(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());

        if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
        else eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

        eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

        eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
        eb.setDescription(ogEmbed.getDescription());
        List<MessageEmbed.Field> fields = ogEmbed.getFields();
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
        processTimestampUsage(channel, author, previous, embedUser, event, lang);
    }

    // Timestamp
    private void processTimestampUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
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
                            processTimestamp(channel, author, message, embedUser, event, false, lang);
                        else processNoTimestamp(channel, author, message, embedUser, event, lang);
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang));
        });
    }

    private void processTimestamp(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, boolean repeated, String lang) {
        previous.clearReactions().queue();
        previous.editMessage((repeated ?  LanguageHandler.get(lang, "embed_timestamp_i_fail") : LanguageHandler.get(lang, "embed_timestamp_i"))).queue(
                message -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                        e -> e.getAuthor().equals(author)
                                && e.getChannel().equals(channel),
                        e -> {
                            var timestamp = e.getMessage().getContentRaw();
                            e.getMessage().delete().queue();

                            // Redo if user didn't provide valid direct url.
                            if (!Parser.isValidDateTime(timestamp) && !timestamp.equalsIgnoreCase("now")) {
                                processTimestamp(channel, author, message, embedUser, event, true, lang);
                                return;
                            }

                            var ogMessage = embedUser.getMessage();
                            var ogEmbed = embedUser.getEmbed();
                            var eb = new EmbedBuilder();
                            eb.setColor(ogEmbed.getColor());

                            if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
                            else eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

                            eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

                            eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
                            eb.setDescription(ogEmbed.getDescription());
                            List<MessageEmbed.Field> fields = ogEmbed.getFields();
                            for (var field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());
                            eb.setImage((ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl()));
                            if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
                            else eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
                            eb.setTimestamp((timestamp.equalsIgnoreCase("now") ?
                                    OffsetDateTime.now() :
                                    OffsetDateTime.of(
                                            LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                                            ZoneOffset.of(embedUser.getOffset().equals("00:00") ? "Z" : embedUser.getOffset())
                                    )));

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processFinish(channel, author, message, embedUser, event, lang);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }

    private void processNoTimestamp(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());

        if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
        else eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

        eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

        eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
        eb.setDescription(ogEmbed.getDescription());
        List<MessageEmbed.Field> fields = ogEmbed.getFields();
        for (var field : fields)
            eb.addField(field.getName(), field.getValue(), field.isInline());
        eb.setImage((ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl()));
        if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
        else eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
        eb.setTimestamp(null);

        ogMessage.editMessage(eb.build()).queue();
        embedUser.setEmbed(eb.build());
        processFinish(channel, author, previous, embedUser, event, lang);
    }

    // Finish
    private void processFinish(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, String lang) {
        previous.clearReactions().queue();
        previous.editMessage(LanguageHandler.get(lang, "createembed_done")).queue(
                message -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                        e -> e.getAuthor().equals(author)
                                && e.getChannel().equals(channel),
                        e -> {
                            var destinationChannel = e.getMessage().getMentionedChannels().get(0);
                            e.getMessage().delete().queue();
                            var ogMessage = embedUser.getMessage();
                            var ogEmbed = embedUser.getEmbed();
                            var eb = new EmbedBuilder();
                            eb.setColor(ogEmbed.getColor());

                            if (ogEmbed.getAuthor() == null) eb.setAuthor(null, null, null);
                            else eb.setAuthor(ogEmbed.getAuthor().getName(), ogEmbed.getAuthor().getUrl(), ogEmbed.getAuthor().getIconUrl());

                            eb.setThumbnail((ogEmbed.getThumbnail() == null ? null : ogEmbed.getThumbnail().getUrl()));

                            eb.setTitle(ogEmbed.getTitle(), ogEmbed.getUrl());
                            eb.setDescription(ogEmbed.getDescription());
                            List<MessageEmbed.Field> fields = ogEmbed.getFields();
                            for (var field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());
                            eb.setImage((ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl()));
                            if (ogEmbed.getFooter() == null) eb.setFooter(null, null);
                            else eb.setFooter((ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getText()), (ogEmbed.getFooter() == null ? null : ogEmbed.getFooter().getIconUrl()));
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            destinationChannel.sendMessage(eb.build()).queue();
                            message.delete().queue();
                            ogMessage.delete().queue();
                            event.reactSuccess();
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event, lang)));
    }
}
