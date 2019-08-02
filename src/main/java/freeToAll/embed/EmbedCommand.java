// Author: Tancred423 (https://github.com/Tancred423)
package freeToAll.embed;

import moderation.guild.Guild;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import servant.Log;
import servant.Servant;
import utilities.Parser;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EmbedCommand extends Command {
    private EventWaiter waiter;

    public EmbedCommand(EventWaiter waiter) {
        this.name = "embed";
        this.aliases = new String[0];
        this.help = "Creates a customized embed message.";
        this.category = new Category("Free to all");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = 5;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};

        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent event) {
        // Enabled?
        try {
            if (!new Guild(event.getGuild().getIdLong()).getToggleStatus("embed")) return;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }

        List<Long> wizardMessageIds = new ArrayList<>();
        var channel = event.getChannel();
        var embed = new Embed();

        channel.sendMessage("Embed to the embed message configuration wizard. In the following I will ask you to enter stuff. On every question you have 1 hour to answer, otherwise the configuration will be stopped and you will loose all the progress.\n" +
                "To do this, you should know a bit about embeds. If you don't have any experience with embeds, I recommend you to type \"yes\" on the following question.\n" +
                "**Do you want help with the embed layout? Yes/No:**").queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
        var internalGuild = new Guild(event.getGuild().getIdLong());
        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                helpEvent -> helpEvent.getAuthor().equals(event.getAuthor()) && helpEvent.getChannel().equals(event.getChannel()),
                helpEvent -> {
                    var helpMessage = helpEvent.getMessage();
                    wizardMessageIds.add(helpMessage.getIdLong());
                    var answer = helpMessage.getContentDisplay();

                    if (answer.toLowerCase().equals("yes") || answer.toLowerCase().equals("y")) {
                        event.reply("**Author Name:** The title of the embed.\n" +
                                "**Author Url (optional):** Author Name as a hyperlink.\n" +
                                "**Author Icon Url (optional):** Small icon next to Author Name.\n" +
                                "\n" +
                                "**Thumbnail Url (optional):** The bigger image on the right side.\n" +
                                "\n" +
                                "**Title:** The title of the description field.\n" +
                                "**Title Url (optional):** Title as a hyperlink.\n" +
                                "**Description:** The context of the description field.\n" +
                                "\n" +
                                "**Fields:** A field has a title, a description and a flag whether the field should be inline or not.\n" +
                                "- Inline: Fields can be shown next to each other (max. 3 in one row).\n" +
                                "- Non-Inline: Field will take one row on its own.\n" +
                                "\n" +
                                "**Image Url (optional):** The big image on the bottom.\n" +
                                "\n" +
                                "**Footer Text (optional):** The description in the footer.\n" +
                                "**Footer Icon Url (optional):** The icon next to the footer.\n" +
                                "**Timestamp (optional):** A flag whether the current timestamp should be shown in the footer.\n" +
                                "\n" +
                                "**Some further information:**\n" +
                                "- You can have up to 25 fields.\n" +
                                "- Please remember, that the bot also has a character limit. Try to keep the embed clean.\n" +
                                "- You are not allowed to change the color, as this is a patreon feature.\n" +
                                "- The Author Name, Title and Fields also *could* be optional. The embed is not allowed to be empty, meaning:\n" +
                                "--- There has to be a title. Just the Author Name or just the Title is enough.\n" +
                                "--- There has to be a context. Just the description or just one or more fields are enough.\n" +
                                "--- Stuff that is marked with \"(optional)\" are completely optional without any condition.\n" +
                                "\n" +
                                "**To start the configuration, you have to type `" + Servant.config.getDefaultPrefix() + "embed` again and deny the initial question.**\n" +
                                "https://i.stack.imgur.com/HRWHk.png");
                    } else if (answer.toLowerCase().equals("no") || answer.toLowerCase().equals("n")) {
                        // Author Name
                        channel.sendMessage("Please enter an Author Name:\n" +
                                "*Type \"none\" if you don't want to set an Author Name.*")
                                .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                authorNameEvent -> authorNameEvent.getAuthor().equals(event.getAuthor()) && authorNameEvent.getChannel().equals(event.getChannel()),
                                authorNameEvent -> {
                                    var authorNameMessage = authorNameEvent.getMessage();
                                    wizardMessageIds.add(authorNameMessage.getIdLong());
                                    var authorName = authorNameMessage.getContentDisplay();

                                    if (authorName.length() <= 256) {
                                        if (authorName.toLowerCase().equals("none")) embed.setAuthorName(null);
                                        else embed.setAuthorName(authorName);

                                        // Author Url
                                        channel.sendMessage("Please enter an Author Url:\n" +
                                                "*Type \"none\" if you don't want to set an Author Url.*")
                                                .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                                        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                                authorUrlEvent -> authorUrlEvent.getAuthor().equals(event.getAuthor()) && authorUrlEvent.getChannel().equals(event.getChannel()),
                                                authorUrlEvent -> {
                                                    var authorUrlMessage = authorUrlEvent.getMessage();
                                                    wizardMessageIds.add(authorUrlMessage.getIdLong());
                                                    var authorUrl = authorUrlMessage.getContentDisplay();

                                                    if (Parser.isValidUrl(authorUrl) || authorUrl.toLowerCase().equals("none")) {
                                                        if (authorUrl.toLowerCase().equals("none"))
                                                            embed.setAuthorUrl(null);
                                                        else if (embed.getAuthorName() == null)
                                                            embed.setAuthorUrl(null);
                                                        else embed.setAuthorUrl(authorUrl);

                                                        // Author Icon Url
                                                        channel.sendMessage("Please enter an Author Icon Url:\n" +
                                                                "*Type \"none\" if you don't want to set an Author Icon.*")
                                                                .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                                                        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                                                authorIconUrlEvent -> authorIconUrlEvent.getAuthor().equals(event.getAuthor()) && authorIconUrlEvent.getChannel().equals(event.getChannel()),
                                                                authorIconUrlEvent -> {
                                                                    var authorIconUrlMessage = authorIconUrlEvent.getMessage();
                                                                    wizardMessageIds.add(authorIconUrlMessage.getIdLong());
                                                                    var authorIconUrl = authorIconUrlMessage.getContentDisplay();

                                                                    if (Parser.isValidDirectUrl(authorIconUrl) || authorIconUrl.toLowerCase().equals("none")) {
                                                                        if (authorIconUrl.toLowerCase().equals("none"))
                                                                            embed.setAuthorIconUrl(null);
                                                                        else if (embed.getAuthorName() == null)
                                                                            embed.setAuthorIconUrl(null);
                                                                        else embed.setAuthorIconUrl(authorIconUrl);

                                                                        // Thumbnail Url
                                                                        channel.sendMessage("Please enter a Thumbnail Url:\n" +
                                                                                "*Type \"none\" if you don't want to set a Thumbnail.*")
                                                                                .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                                                                        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                                                                thumbnailUrlEvent -> thumbnailUrlEvent.getAuthor().equals(event.getAuthor()) && thumbnailUrlEvent.getChannel().equals(event.getChannel()),
                                                                                thumbnailUrlEvent -> {
                                                                                    var thumnailUrlMessage = thumbnailUrlEvent.getMessage();
                                                                                    wizardMessageIds.add(thumnailUrlMessage.getIdLong());
                                                                                    var thumbnailUrl = thumnailUrlMessage.getContentDisplay();

                                                                                    if (Parser.isValidDirectUrl(thumbnailUrl) || thumbnailUrl.toLowerCase().equals("none")) {
                                                                                        if (thumbnailUrl.toLowerCase().equals("none"))
                                                                                            embed.setThumbnailUrl(null);
                                                                                        else
                                                                                            embed.setThumbnailUrl(thumbnailUrl);

                                                                                        // Title
                                                                                        channel.sendMessage("Please enter a Title:\n" +
                                                                                                "*Type \"none\" if you don't want to set a Title.*")
                                                                                                .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                                                                                        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                                                                                titleEvent -> titleEvent.getAuthor().equals(event.getAuthor()) && titleEvent.getChannel().equals(event.getChannel()),
                                                                                                titleEvent -> {
                                                                                                    var titleMessage = titleEvent.getMessage();
                                                                                                    wizardMessageIds.add(titleMessage.getIdLong());
                                                                                                    var title = titleMessage.getContentDisplay();

                                                                                                    if (title.length() <= 256) {
                                                                                                        if (title.toLowerCase().equals("none"))
                                                                                                            embed.setTitle(null);
                                                                                                        else
                                                                                                            embed.setTitle(title);

                                                                                                        // Title Url
                                                                                                        channel.sendMessage("Please enter a Title Url:\n" +
                                                                                                                "*Type \"none\" if you don't want to set a Title Url.*")
                                                                                                                .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                                                                                                        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                                                                                                titleUrlEvent -> titleUrlEvent.getAuthor().equals(event.getAuthor()) && titleUrlEvent.getChannel().equals(event.getChannel()),
                                                                                                                titleUrlEvent -> {
                                                                                                                    var titleUrlMessage = titleUrlEvent.getMessage();
                                                                                                                    wizardMessageIds.add(titleUrlMessage.getIdLong());
                                                                                                                    var titleUrl = titleUrlMessage.getContentDisplay();

                                                                                                                    if (Parser.isValidUrl(titleUrl) || titleUrl.toLowerCase().equals("none")) {
                                                                                                                        if (titleUrl.toLowerCase().equals("none"))
                                                                                                                            embed.setTitleUrl(null);
                                                                                                                        else if (embed.getTitle() == null)
                                                                                                                            embed.setTitleUrl(null);
                                                                                                                        else
                                                                                                                            embed.setTitleUrl(titleUrl);

                                                                                                                        // Description
                                                                                                                        channel.sendMessage("Please enter a Description:\n" +
                                                                                                                                "*Type \"none\" if you don't want to set a Description.*")
                                                                                                                                .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                                                                                                                        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                                                                                                                descriptionEvent -> descriptionEvent.getAuthor().equals(event.getAuthor()) && descriptionEvent.getChannel().equals(event.getChannel()),
                                                                                                                                descriptionEvent -> {
                                                                                                                                    var descriptionMessage = descriptionEvent.getMessage();
                                                                                                                                    wizardMessageIds.add(descriptionMessage.getIdLong());
                                                                                                                                    var description = descriptionMessage.getContentDisplay();

                                                                                                                                    if (description.length() <= 2048) {
                                                                                                                                        if (description.toLowerCase().equals("none"))
                                                                                                                                            embed.setDescription(null);
                                                                                                                                        else
                                                                                                                                            embed.setDescription(descriptionMessage.getContentRaw());

                                                                                                                                        // Fields

                                                                                                                                        // Image Url
                                                                                                                                        channel.sendMessage("Please enter an Image Url:\n" +
                                                                                                                                                "*Type \"none\" if you don't want to set an Image.*")
                                                                                                                                                .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                                                                                                                                        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                                                                                                                                imageUrlEvent -> imageUrlEvent.getAuthor().equals(event.getAuthor()) && imageUrlEvent.getChannel().equals(event.getChannel()),
                                                                                                                                                imageUrlEvent -> {
                                                                                                                                                    var imageUrlMessage = imageUrlEvent.getMessage();
                                                                                                                                                    wizardMessageIds.add(imageUrlMessage.getIdLong());
                                                                                                                                                    var imageUrl = imageUrlMessage.getContentDisplay();
                                                                                                                                                    if (Parser.isValidDirectUrl(imageUrl) || imageUrl.toLowerCase().equals("none")) {
                                                                                                                                                        if (imageUrl.toLowerCase().equals("none"))
                                                                                                                                                            embed.setImageUrl(null);
                                                                                                                                                        else
                                                                                                                                                            embed.setImageUrl(imageUrl);

                                                                                                                                                        // Footer Text
                                                                                                                                                        channel.sendMessage("Please enter a Footer Text:\n" +
                                                                                                                                                                "*Type \"none\" if you don't want to set a Footer.*")
                                                                                                                                                                .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                                                                                                                                                        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                                                                                                                                                footerTextEvent -> footerTextEvent.getAuthor().equals(event.getAuthor()) && footerTextEvent.getChannel().equals(event.getChannel()),
                                                                                                                                                                footerTextEvent -> {
                                                                                                                                                                    var footerTextMessage = footerTextEvent.getMessage();
                                                                                                                                                                    wizardMessageIds.add(footerTextMessage.getIdLong());
                                                                                                                                                                    var footerText = footerTextMessage.getContentDisplay();

                                                                                                                                                                    if (footerText.length() <= 2048) {
                                                                                                                                                                        if (footerText.toLowerCase().equals("none"))
                                                                                                                                                                            embed.setFooterText(null);
                                                                                                                                                                        else
                                                                                                                                                                            embed.setFooterText(footerText);

                                                                                                                                                                        // Footer Icon Url
                                                                                                                                                                        channel.sendMessage("Please enter a Footer Icon Url:\n" +
                                                                                                                                                                                "*Type \"none\" if you don't want to set a Footer Icon.*")
                                                                                                                                                                                .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                                                                                                                                                                        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                                                                                                                                                                footerIconUrlEvent -> footerIconUrlEvent.getAuthor().equals(event.getAuthor()) && footerIconUrlEvent.getChannel().equals(event.getChannel()),
                                                                                                                                                                                footerIconUrlEvent -> {
                                                                                                                                                                                    var footerIconUrlMessage = footerIconUrlEvent.getMessage();
                                                                                                                                                                                    wizardMessageIds.add(footerIconUrlMessage.getIdLong());
                                                                                                                                                                                    var footerIconUrl = footerIconUrlMessage.getContentDisplay();
                                                                                                                                                                                    if (Parser.isValidDirectUrl(footerIconUrl) || footerIconUrl.toLowerCase().equals("none")) {
                                                                                                                                                                                        if (footerIconUrl.equals("none"))
                                                                                                                                                                                            embed.setFooterIconUrl(null);
                                                                                                                                                                                        else if (embed.getFooterText() == null)
                                                                                                                                                                                            embed.setFooterIconUrl(null);
                                                                                                                                                                                        else
                                                                                                                                                                                            embed.setFooterIconUrl(footerIconUrl);

                                                                                                                                                                                        // Timestamp
                                                                                                                                                                                        channel.sendMessage("Please state whether you want a timestamp to be included in the footer.\n" +
                                                                                                                                                                                                "Add Timestamp? Yes/No:")
                                                                                                                                                                                                .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                                                                                                                                                                                        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                                                                                                                                                                                timestampEvent -> timestampEvent.getAuthor().equals(event.getAuthor()) && timestampEvent.getChannel().equals(event.getChannel()),
                                                                                                                                                                                                timestampEvent -> {
                                                                                                                                                                                                    var timestampMessage = timestampEvent.getMessage();
                                                                                                                                                                                                    wizardMessageIds.add(timestampMessage.getIdLong());
                                                                                                                                                                                                    var hasTimestamp = timestampMessage.getContentDisplay().toLowerCase().equals("yes");
                                                                                                                                                                                                    ZoneId zoneId;
                                                                                                                                                                                                    try {
                                                                                                                                                                                                        zoneId = internalGuild.getOffset() == null ? ZoneId.of("UTC") : ZoneId.of(internalGuild.getOffset());
                                                                                                                                                                                                    } catch (SQLException e) {
                                                                                                                                                                                                        new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                                                                                                                                                                                                        return;
                                                                                                                                                                                                    }
                                                                                                                                                                                                    if (hasTimestamp)
                                                                                                                                                                                                        embed.setTimestamp(OffsetDateTime.now(zoneId));
                                                                                                                                                                                                    else
                                                                                                                                                                                                        embed.setTimestamp(null);

                                                                                                                                                                                                    // Channel
                                                                                                                                                                                                    channel.sendMessage("We are done! Please ping the desired text channel for the embed message:")
                                                                                                                                                                                                            .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                                                                                                                                                                                                    waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                                                                                                                                                                                            channelEvent -> channelEvent.getAuthor().equals(event.getAuthor()) && channelEvent.getChannel().equals(event.getChannel()),
                                                                                                                                                                                                            channelEvent -> {
                                                                                                                                                                                                                var channelMessage = channelEvent.getMessage();
                                                                                                                                                                                                                wizardMessageIds.add(channelMessage.getIdLong());
                                                                                                                                                                                                                if (!channelMessage.getMentionedChannels().isEmpty()) {
                                                                                                                                                                                                                    embed.setMessageChannel(channelMessage.getMentionedChannels().get(0));
                                                                                                                                                                                                                    sendWelcomeMessage(embed, event, wizardMessageIds);
                                                                                                                                                                                                                } else
                                                                                                                                                                                                                    endWithError(wizardMessageIds, event, "[Error] Invalid channel mention.");
                                                                                                                                                                                                            }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't provide a channel mention."));
                                                                                                                                                                                                }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't answer."));
                                                                                                                                                                                    } else
                                                                                                                                                                                        endWithError(wizardMessageIds, event, "[Error] Invalid Footer Icon Url.");
                                                                                                                                                                                }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't provide a Footer Icon Url."));
                                                                                                                                                                    } else
                                                                                                                                                                        endWithError(wizardMessageIds, event, "[Error] The Footer Text was too long! (max. 2048 characters)");
                                                                                                                                                                }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't provide a Footer Text."));
                                                                                                                                                    } else
                                                                                                                                                        endWithError(wizardMessageIds, event, "[Error] Invalid Image Url.");
                                                                                                                                                }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't provide an Image Url."));
                                                                                                                                    } else
                                                                                                                                        endWithError(wizardMessageIds, event, "[Error] The description was too long! (max. 2048 characters)");
                                                                                                                                }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't provide a Description."));
                                                                                                                    } else
                                                                                                                        endWithError(wizardMessageIds, event, "[Error] Invalid Title Url.");
                                                                                                                }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't provide a Title Url."));
                                                                                                    } else
                                                                                                        endWithError(wizardMessageIds, event, "[Error] The Title was too long! (max. 256 characters)");
                                                                                                }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't provide a Title."));
                                                                                    } else
                                                                                        endWithError(wizardMessageIds, event, "[Error] Invalid Thumbnail Url.");
                                                                                }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't provide a Thumbnail Url."));
                                                                    } else
                                                                        endWithError(wizardMessageIds, event, "[Error] Invalid Author Icon Url.");
                                                                }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't provide an Author Icon Url."));
                                                    } else
                                                        endWithError(wizardMessageIds, event, "[Error] Invalid Author Url.");
                                                }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't provide a Author Url."));
                                    } else
                                        endWithError(wizardMessageIds, event, "[Error] The Author Name was too long! (max. 256 characters)");
                                }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't provide an Author Name."));
                    } else event.reply("You had one job.");
                }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't answer me."));

        // Statistics.
        try {
            new servant.User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }

    private void endWithError(List<Long> wizardMessageIds, CommandEvent event, String errorMessage) {
        for (Long message : wizardMessageIds)
            event.getChannel().getMessageById(message).queue(msg -> msg.delete().queue());
        event.reply(errorMessage);
        event.reactError();
    }

    private void sendWelcomeMessage(Embed embed, CommandEvent event, List<Long> wizardMessageIds) {
        for (Long message : wizardMessageIds)
            event.getChannel().getMessageById(message).queue(msg -> msg.delete().queue());

        EmbedBuilder eb = new EmbedBuilder();
        try {
            eb.setColor(new servant.User(event.getAuthor().getIdLong()).getColor());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        eb.setAuthor(embed.getAuthorName(), embed.getAuthorUrl(), embed.getAuthorIconUrl());
        eb.setThumbnail(embed.getThumbnailUrl());
        eb.setTitle(embed.getTitle(), embed.getTitleUrl());
        eb.setDescription(embed.getDescription());
        eb.setImage(embed.getImageUrl());
        eb.setFooter(embed.getFooterText(), embed.getFooterIconUrl());
        eb.setTimestamp(embed.getTimestamp());

        try {
            embed.getMessageChannel().sendMessage(eb.build()).queue();
            event.reactSuccess();
        } catch (Exception e) {
            event.reply("[ERROR] I told you, you cannot leave everything empty...");
            event.reactError();
        }
    }
}
