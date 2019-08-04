package freeToAll.embed;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import servant.Log;
import servant.Servant;
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
import java.util.concurrent.TimeUnit;

public class CreateEmbedCommand extends Command {
    private final EventWaiter waiter;
    private String accept = "✅";
    private String decline = "❌";

    public CreateEmbedCommand(EventWaiter waiter) {
        this.name = "createembed";
        this.aliases = new String[]{"ce", "embed"};
        this.help = "Create an embed.";
        this.category = new Category("Free to all");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = 900; // 15 minutes
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};

        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent event) {
        var author = event.getAuthor();
        var internalAuthor = new servant.User(author.getIdLong());
        var eb = new EmbedBuilder();

        try {
            eb.setColor(internalAuthor.getColor());
        } catch (SQLException e) {
            eb.setColor(Color.decode(Servant.config.getDefaultColorCode()));
        }
        eb.setAuthor("Author name (can point to URL)", "https://google.com/", author.getAvatarUrl());
        eb.setThumbnail("https://i.imgur.com/wAcLhfY.png");
        eb.setTitle("Title (can point to URL)", "https://google.com/");
        eb.setDescription("Description\n" +
                "The title will be white if it's not a hyperlink.\n" +
                "Any image URL's have to be direct links.\n" +
                "The timestamp is not part of the footer text, but a standalone date and time.");
        eb.addField("Inline field name", "Field value", true);
        eb.addField("Inline field name", "Up to 3 in a line.", true);
        eb.addField("Inline field name", "You can have up to 25 fields.", true);
        eb.addField("Non-inline field name", "Non-inline fields take the while width of the embed.", false);
        eb.setImage("https://i.imgur.com/9G46UQx.png");
        eb.setFooter("Footer text", event.getSelfUser().getAvatarUrl());
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
            processIntroduction(channel, author, embedUser, event);
        });
    }

    // Timeout
    private void timeout(Message botMessage, Message embedMessage, CommandEvent event) {
        event.reactWarning();
        botMessage.delete().queue();
        embedMessage.delete().queue();
        event.reply("This configuration timed out.");
    }

    // Introduction
    private void processIntroduction(MessageChannel channel, User author, EmbedUser embedUser, CommandEvent event) {
        channel.sendMessage("With this command, you can create your own embed.\n" +
                "- You cannot create an empty embed.\n" +
                "- The embed but not be longer than 6000 characters in total.\n" +
                "- Everytime you have to click a reacton or write an answer, you have a time limit of 15 minutes.\n" +
                "Are you prepared?").queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept)) {
                            e.getReaction().removeReaction(author).queue();
                            processAuthorUsage(channel, author, message, embedUser, event);
                        } else {
                            embedUser.getMessage().delete().queue();
                            message.delete().queue();
                            event.reactWarning();
                        }
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event));
        });
    }

    // Author
    private void processAuthorUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
        previous.editMessage("Alright! Do you want to use an author line?").queue(
                message -> waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                        e -> e.getUser().equals(author)
                                && (e.getReactionEmote().getName().equals(accept)
                                || e.getReactionEmote().getName().equals(decline)),
                        e -> {
                            if (e.getReactionEmote().getName().equals(accept))
                                processAuthorName(channel, author, message, embedUser, event);
                            else processNoAuthorLine(channel, author, message, embedUser, event);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event)));
    }

    private void processAuthorName(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
        previous.clearReactions().queue();
        previous.editMessage("Please provide the **author name**:").queue(
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
                            processAuthorUrlUsage(channel, author, message, embedUser, event);
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, embedUser.getMessage(), event)));
    }

    private void processAuthorUrlUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
        previous.clearReactions().queue();
        previous.editMessage("Do you want to use an **author url** (not the icon)?").queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept))
                            processAuthorUrl(channel, author, message, embedUser, event, false);
                        else processNoAuthorUrl(channel, author, message, embedUser, event);
                    }, 15, TimeUnit.MINUTES, () -> channel.sendMessage("Timeout.").queue());
        });
    }

    private void processAuthorUrl(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, boolean repeated) {
        previous.clearReactions().queue();
        previous.editMessage((repeated ? "Your input is invalid. Please provide a valid url:" : "Please provide the **author url** (not the icon url!):")).queue(
                message -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                        e -> e.getAuthor().equals(author)
                                && e.getChannel().equals(channel),
                        e -> {
                            var authorUrl = e.getMessage().getContentRaw();
                            e.getMessage().delete().queue();

                            // Redo if user didn't provide valid url.
                            if (!Parser.isValidUrl(authorUrl)) {
                                processAuthorUrl(channel, author, previous, embedUser, event, true);
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
                            processAuthorIconUrlUsage(channel, author, message, embedUser, event);
                        }, 15, TimeUnit.MINUTES, () -> channel.sendMessage("Timeout.").queue()));
    }

    private void processNoAuthorUrl(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());
        eb.setAuthor(ogEmbed.getAuthor().getName(), null, ogEmbed.getAuthor().getIconUrl());
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
        processAuthorIconUrlUsage(channel, author, previous, embedUser, event);
    }

    private void processAuthorIconUrlUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
        previous.clearReactions().queue();
        previous.editMessage("Do you want to use an **author icon**?").queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept))
                            processAuthorIconUrl(channel, author, message, embedUser, event, false);
                        else processNoAuthorIconUrl(channel, author, message, embedUser, event);
                    }, 15, TimeUnit.MINUTES, () -> channel.sendMessage("Timeout.").queue());
        });
    }

    private void processAuthorIconUrl(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, boolean repeated) {
        previous.clearReactions().queue();
        previous.editMessage((repeated ? "Your input is invalid. Please provide a valid direct url:" : "Please provide the **author icon url** (direct link!):")).queue(
                message -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                        e -> e.getAuthor().equals(author)
                                && e.getChannel().equals(channel),
                        e -> {
                            var authorIconUrl = e.getMessage().getContentRaw();
                            e.getMessage().delete().queue();

                            // Redo if user didn't provide valid direct url.
                            if (!Parser.isValidDirectUrl(authorIconUrl)) {
                                processAuthorIconUrl(channel, author, message, embedUser, event, true);
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
                            processThumbnailUsage(channel, author, message, embedUser, event);
                        }, 15, TimeUnit.MINUTES, () -> channel.sendMessage("Timeout.").queue()));
    }

    private void processNoAuthorIconUrl(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
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
        processThumbnailUsage(channel, author, previous, embedUser, event);
    }

    private void processNoAuthorLine(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
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
        processThumbnailUsage(channel, author, previous, embedUser, event);
    }

    // Thumbnail
    private void processThumbnailUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
        previous.clearReactions().queue();
        previous.editMessage("Do you want to use a thumbnail?").queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept))
                            processThumbnail(channel, author, message, embedUser, event, false);
                        else processNoThumbnail(channel, author, message, embedUser, event);
                    }, 15, TimeUnit.MINUTES, () -> channel.sendMessage("Timeout.").queue());
        });
    }

    private void processThumbnail(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, boolean repeated) {
        previous.clearReactions().queue();
        previous.editMessage((repeated ? "Your input is invalid. Please provide a valid direct url:" : "Please provide the **thumbnail url** (direct link!):")).queue(
                message -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                        e -> e.getAuthor().equals(author)
                                && e.getChannel().equals(channel),
                        e -> {
                            var thumbnailUrl = e.getMessage().getContentRaw();
                            e.getMessage().delete().queue();

                            // Redo if user didn't provide valid direct url.
                            if (!Parser.isValidDirectUrl(thumbnailUrl)) {
                                processThumbnail(channel, author, message, embedUser, event, true);
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
                            processTitleUsage(channel, author, message, embedUser, event);
                        }, 15, TimeUnit.MINUTES, () -> channel.sendMessage("Timeout.").queue()));
    }

    private void processNoThumbnail(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
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
        processTitleUsage(channel, author, previous, embedUser, event);
    }

    // Title
    private void processTitleUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
        previous.clearReactions().queue();
        previous.editMessage("Do you want to use a title?").queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept))
                            processTitle(channel, author, message, embedUser, event);
                        else processNoTitle(channel, author, message, embedUser, event);
                    }, 15, TimeUnit.MINUTES, () -> channel.sendMessage("Timeout.").queue());
        });
    }

    private void processTitle(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
        previous.clearReactions().queue();
        previous.editMessage("Please provide the **title**:").queue(
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
                            processTitleUrlUsage(channel, author, message, embedUser, event);
                        }, 15, TimeUnit.MINUTES, () -> channel.sendMessage("Timeout.").queue()));
    }

    private void processTitleUrlUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
        previous.clearReactions().queue();
        previous.editMessage("Do you want to use a title URL?").queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept))
                            processTitleUrl(channel, author, message, embedUser, event, false);
                        else processNoTitleUrl(channel, author, message, embedUser, event);
                    }, 15, TimeUnit.MINUTES, () -> channel.sendMessage("Timeout.").queue());
        });
    }

    private void processTitleUrl(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, boolean repeated) {
        previous.clearReactions().queue();
        previous.editMessage((repeated ? "Your input is invalid. Please provide a valid url:" : "Please provide the **title url**:")).queue(
                message -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                        e -> e.getAuthor().equals(author)
                                && e.getChannel().equals(channel),
                        e -> {
                            var titleUrl = e.getMessage().getContentRaw();
                            e.getMessage().delete().queue();

                            // Redo if user didn't provide valid url.
                            if (!Parser.isValidUrl(titleUrl)) {
                                processTitleUrl(channel, author, message, embedUser, event, true);
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
                            processDescriptionUsage(channel, author, message, embedUser, event);
                        }, 15, TimeUnit.MINUTES, () -> channel.sendMessage("Timeout.").queue()));
    }

    private void processNoTitleUrl(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
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
        processDescriptionUsage(channel, author, previous, embedUser, event);
    }

    private void processNoTitle(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
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
        processDescriptionUsage(channel, author, previous, embedUser, event);
    }

    // Description
    private void processDescriptionUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
        previous.clearReactions().queue();
        previous.editMessage("Do you want to use a description?").queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept))
                            processDescription(channel, author, message, embedUser, event);
                        else processNoDescription(channel, author, message, embedUser, event);
                    }, 15, TimeUnit.MINUTES, () -> channel.sendMessage("Timeout.").queue());
        });
    }

    private void processDescription(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
        previous.clearReactions().queue();
        previous.editMessage("Please provide the **description**:").queue(
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
                            processFieldUsage(channel, author, message, embedUser, event);
                        }, 15, TimeUnit.MINUTES, () -> channel.sendMessage("Timeout.").queue()));
    }

    private void processNoDescription(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
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
        processFieldUsage(channel, author, previous, embedUser, event);
    }

    // Fields
    private void processFieldUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
        previous.clearReactions().queue();
        previous.editMessage("Do you want to add a field?").queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept))
                            processFieldName(channel, author, message, embedUser, event);
                        else processNoMoreFields(channel, author, message, embedUser, event);
                    }, 15, TimeUnit.MINUTES, () -> channel.sendMessage("Timeout.").queue());
        });
    }

    private void processFieldName(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
        previous.clearReactions().queue();
        previous.editMessage("Please provide the **field name**:").queue(
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
                            if (!fields.isEmpty() && fields.get(0).getName().equalsIgnoreCase("inline field name")) fields = new ArrayList<>();

                            for (MessageEmbed.Field field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());

                            eb.addField(fieldName, "", false);

                            eb.setImage(ogEmbed.getImage().getUrl());
                            eb.setFooter(ogEmbed.getFooter().getText(), ogEmbed.getFooter().getIconUrl());
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processFieldValue(channel, author, message, embedUser, event);
                        }, 15, TimeUnit.MINUTES, () -> channel.sendMessage("Timeout.").queue()));
    }

    private void processFieldValue(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
        previous.clearReactions().queue();
        previous.editMessage("Please provide the **field value**:").queue(
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
                            processFieldInline(channel, author, message, embedUser, event);
                        }, 15, TimeUnit.MINUTES, () -> channel.sendMessage("Timeout.").queue()));
    }

    private void processFieldInline(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
        previous.clearReactions().queue();
        previous.editMessage("Should this field be inline?").queue(message -> {
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
                        if (embedUser.getFieldCounter() == 25) processImageUsage(channel, author, message, embedUser, event);
                        else processFieldUsage(channel, author, message, embedUser, event);
                    }, 15, TimeUnit.MINUTES, () -> channel.sendMessage("Timeout.").queue());
        });
    }

    private void processNoMoreFields(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
        var ogMessage = embedUser.getMessage();
        var ogEmbed = embedUser.getEmbed();
        var eb = new EmbedBuilder();
        eb.setColor(ogEmbed.getColor());

        List<MessageEmbed.Field> fields = ogEmbed.getFields();
        if (!fields.isEmpty() && fields.get(0).getName().equalsIgnoreCase("inline field name")) fields = new ArrayList<>();

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
        processImageUsage(channel, author, previous, embedUser, event);
    }

    // Image
    private void processImageUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
        previous.clearReactions().queue();
        previous.editMessage("Do you want to use an image?").queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept))
                            processImage(channel, author, message, embedUser, event, false);
                        else processNoImage(channel, author, message, embedUser, event);
                    }, 15, TimeUnit.MINUTES, () -> channel.sendMessage("Timeout.").queue());
        });
    }

    private void processImage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, boolean repeated) {
        previous.clearReactions().queue();
        previous.editMessage((repeated ? "Your input is invalid. Please provide a valid direct url:" : "Please provide the **image url** (direct link!):")).queue(
                message -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                        e -> e.getAuthor().equals(author)
                                && e.getChannel().equals(channel),
                        e -> {
                            var imageUrl = e.getMessage().getContentRaw();
                            e.getMessage().delete().queue();

                            // Redo if user didn't provide valid direct url.
                            if (!Parser.isValidDirectUrl(imageUrl)) {
                                processImage(channel, author, message, embedUser, event, true);
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
                            processFooterUsage(channel, author, message, embedUser, event);
                        }, 15, TimeUnit.MINUTES, () -> channel.sendMessage("Timeout.").queue()));
    }

    private void processNoImage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
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
        processFooterUsage(channel, author, previous, embedUser, event);
    }

    // Footer
    private void processFooterUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
        previous.clearReactions().queue();
        previous.editMessage("Do you want to use a footer?").queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept))
                            processFooterText(channel, author, message, embedUser, event);
                        else processNoFooter(channel, author, message, embedUser, event);
                    }, 15, TimeUnit.MINUTES, () -> channel.sendMessage("Timeout.").queue());
        });
    }

    private void processFooterText(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
        previous.clearReactions().queue();
        previous.editMessage("Please provide the **footer text**:").queue(
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
                            for (var field : fields)
                                eb.addField(field.getName(), field.getValue(), field.isInline());
                            eb.setImage((ogEmbed.getImage() == null ? null : ogEmbed.getImage().getUrl()));
                            eb.setFooter(footerText, ogEmbed.getFooter().getIconUrl());
                            eb.setTimestamp(ogEmbed.getTimestamp());

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processFooterIconUrlUsage(channel, author, message, embedUser, event);
                        }, 15, TimeUnit.MINUTES, () -> channel.sendMessage("Timeout.").queue()));
    }

    private void processFooterIconUrlUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
        previous.clearReactions().queue();
        previous.editMessage("Do you want to use a footer icon?").queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept))
                            processFooterIconUrl(channel, author, message, embedUser, event, false);
                        else processNoFooterIconUrl(channel, author, message, embedUser, event);
                    }, 15, TimeUnit.MINUTES, () -> channel.sendMessage("Timeout.").queue());
        });
    }

    private void processFooterIconUrl(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, boolean repeated) {
        previous.clearReactions().queue();
        previous.editMessage((repeated ? "Your input is invalid. Please provide a valid direct url:" : "Please provide the **footer icon url** (direct link!):")).queue(
                message -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                        e -> e.getAuthor().equals(author)
                                && e.getChannel().equals(channel),
                        e -> {
                            var footerIconUrl = e.getMessage().getContentRaw();
                            e.getMessage().delete().queue();

                            // Redo if user didn't provide valid direct url.
                            if (!Parser.isValidDirectUrl(footerIconUrl)) {
                                processFooterIconUrl(channel, author, message, embedUser, event, true);
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
                            processTimestampUsage(channel, author, message, embedUser, event);
                        }, 15, TimeUnit.MINUTES, () -> channel.sendMessage("Timeout.").queue()));
    }

    private void processNoFooterIconUrl(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
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
        processTimestampUsage(channel, author, previous, embedUser, event);
    }

    private void processNoFooter(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
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
            previous.editMessage("Either the embed is empty or it has over 6000 characters.\n" +
                    "Both is not allowed!").queue();
            return;
        }
        embedUser.setEmbed(eb.build());
        processTimestampUsage(channel, author, previous, embedUser, event);
    }

    // Timestamp
    private void processTimestampUsage(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
        previous.clearReactions().queue();
        previous.editMessage("Do you want to use a timestamp?").queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept))
                            processTimestamp(channel, author, message, embedUser, event, false);
                        else processNoTimestamp(channel, author, message, embedUser, event);
                    }, 15, TimeUnit.MINUTES, () -> channel.sendMessage("Timeout.").queue());
        });
    }

    private void processTimestamp(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event, boolean repeated) {
        previous.clearReactions().queue();
        previous.editMessage((repeated ?  "Your input is invalid.\nYou either can say `now` or provide a date and time like this: `yyyy-MM-dd HH:mm` (uses guild timezone).\nTry again:" :
                "Please provide a timestamp.\nYou either can say `now` or provide a date and time like this: `yyyy-MM-dd HH:mm` (uses guild timezone):")).queue(
                message -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                        e -> e.getAuthor().equals(author)
                                && e.getChannel().equals(channel),
                        e -> {
                            var timestamp = e.getMessage().getContentRaw();
                            e.getMessage().delete().queue();

                            // Redo if user didn't provide valid direct url.
                            if (!Parser.isValidDateTime(timestamp)) {
                                processTimestamp(channel, author, message, embedUser, event, true);
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
                                            ZoneOffset.of(embedUser.getOffset())
                                    )));

                            ogMessage.editMessage(eb.build()).queue();
                            embedUser.setEmbed(eb.build());
                            processFinish(channel, author, message, embedUser, event);
                        }, 15, TimeUnit.MINUTES, () -> channel.sendMessage("Timeout.").queue()));
    }

    private void processNoTimestamp(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
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
        processFinish(channel, author, previous, embedUser, event);
    }

    // Finish
    private void processFinish(MessageChannel channel, User author, Message previous, EmbedUser embedUser, CommandEvent event) {
        previous.clearReactions().queue();
        previous.editMessage("We're done! Please mention a text channel to post this embed in (e.g. #channel):").queue(
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
                        }, 15, TimeUnit.MINUTES, () -> channel.sendMessage("Timeout.").queue()));
    }
}
