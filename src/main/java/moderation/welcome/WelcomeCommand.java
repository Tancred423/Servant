package moderation.welcome;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import utilities.Parser;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WelcomeCommand extends Command {
    private EventWaiter waiter;

    public WelcomeCommand(EventWaiter waiter) {
        this.name = "welcome";
        this.aliases = new String[]{"wlc", "welc", "whalecum"};
        this.help = "creates welcome message | **MANAGE CHANNELS** **MANAGE ROLES**";
        this.category = new Category("Moderation");
        this.arguments = "";
        this.userPermissions = new Permission[]{Permission.MANAGE_CHANNEL, Permission.MANAGE_ROLES};
        this.guildOnly = true;
        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent event) {
        List<Long> wizardMessageIds = new ArrayList<>();
        MessageChannel channel = event.getChannel();
        Welcome welcome = new Welcome();

        channel.sendMessage("Welcome to the welcome-embed-message configuration wizard. In the following I will ask you to enter stuff. On every question you have 1 hour to answer, otherwise the configuration will be stopped and you will loose all the progress.\n" +
                "To do this, you should know a bit about embeds. If you don't have any experience with embeds, I recommend you to type \"yes\" on the following question.\n" +
                "**Do you want help with the embed layout? Yes/No:**").queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
        final servant.Guild internalGuild;
        try {
            internalGuild = new servant.Guild(event.getGuild().getIdLong());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                helpEvent -> helpEvent.getAuthor().equals(event.getAuthor()) && helpEvent.getChannel().equals(event.getChannel()),
                helpEvent -> {
                    Message helpMessage = helpEvent.getMessage();
                    wizardMessageIds.add(helpMessage.getIdLong());
                    String answer = helpMessage.getContentDisplay();

                    if (answer.toLowerCase().equals("yes")) {
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
                                "- In case you want to create the welcome feature and not just an embed, so people can get an \"unlock-role\" by reacting, you also should be able to provide an emoji (e.g. \uD83D\uDC4D) or an custom emote.\n" +
                                "- If you want to use a custom emote, make sure I have access to the server the emote is on.\n" +
                                "- Furthermore you should have the unlock-role ready. In the configuration, you either can ping it or if you don't want to bother people, you also can provide the role ID.\n" +
                                "\n" +
                                "**To start the configuration, you have to type `!welcome` again and deny the initial question.**\n" +
                                "https://i.stack.imgur.com/HRWHk.png");
                    } else {
                        // Author Name
                        channel.sendMessage("Please enter an Author Name:\n" +
                                "*Type \"none\" if you don't want to set an Author Name.*")
                                .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                authorNameEvent -> authorNameEvent.getAuthor().equals(event.getAuthor()) && authorNameEvent.getChannel().equals(event.getChannel()),
                                authorNameEvent -> {
                                    Message authorNameMessage = authorNameEvent.getMessage();
                                    wizardMessageIds.add(authorNameMessage.getIdLong());
                                    String authorName = authorNameMessage.getContentDisplay();

                                    if (authorName.toLowerCase().equals("none")) welcome.setAuthorName(null);
                                    else welcome.setAuthorName(authorName);

                                    // Author Url
                                    channel.sendMessage("Please enter an Author Url:\n" +
                                            "*Type \"none\" if you don't want to set an Author Url.*")
                                            .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                                    waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                            authorUrlEvent -> authorUrlEvent.getAuthor().equals(event.getAuthor()) && authorUrlEvent.getChannel().equals(event.getChannel()),
                                            authorUrlEvent -> {
                                                Message authorUrlMessage = authorUrlEvent.getMessage();
                                                wizardMessageIds.add(authorUrlMessage.getIdLong());
                                                String authorUrl = authorUrlMessage.getContentDisplay();

                                                if (Parser.isValidUrl(authorUrl) || authorUrl.toLowerCase().equals("none")) {
                                                    if (authorUrl.toLowerCase().equals("none")) welcome.setAuthorUrl(null);
                                                    else if (welcome.getAuthorName() == null) welcome.setAuthorUrl(null);
                                                    else welcome.setAuthorUrl(authorUrl);

                                                    // Author Icon Url
                                                    channel.sendMessage("Please enter an Author Icon Url:\n" +
                                                            "*Type \"none\" if you don't want to set an Author Icon.*")
                                                            .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                                                    waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                                            authorIconUrlEvent -> authorIconUrlEvent.getAuthor().equals(event.getAuthor()) && authorIconUrlEvent.getChannel().equals(event.getChannel()),
                                                            authorIconUrlEvent -> {
                                                                Message authorIconUrlMessage = authorIconUrlEvent.getMessage();
                                                                wizardMessageIds.add(authorIconUrlMessage.getIdLong());
                                                                String authorIconUrl = authorIconUrlMessage.getContentDisplay();

                                                                if (Parser.isValidDirectUrl(authorIconUrl) || authorIconUrl.toLowerCase().equals("none")) {
                                                                    if (authorIconUrl.toLowerCase().equals("none")) welcome.setAuthorIconUrl(null);
                                                                    else if (welcome.getAuthorName() == null) welcome.setAuthorIconUrl(null);
                                                                    else welcome.setAuthorIconUrl(authorIconUrl);

                                                                    // Thumbnail Url
                                                                    channel.sendMessage("Please enter a Thumbnail Url:\n" +
                                                                            "*Type \"none\" if you don't want to set a Thumbnail.*")
                                                                            .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                                                                    waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                                                            thumbnailUrlEvent -> thumbnailUrlEvent.getAuthor().equals(event.getAuthor()) && thumbnailUrlEvent.getChannel().equals(event.getChannel()),
                                                                            thumbnailUrlEvent -> {
                                                                                Message thumnailUrlMessage = thumbnailUrlEvent.getMessage();
                                                                                wizardMessageIds.add(thumnailUrlMessage.getIdLong());
                                                                                String thumbnailUrl = thumnailUrlMessage.getContentDisplay();

                                                                                if (Parser.isValidDirectUrl(thumbnailUrl) || thumbnailUrl.toLowerCase().equals("none")) {
                                                                                    if (thumbnailUrl.toLowerCase().equals("none")) welcome.setThumbnailUrl(null);
                                                                                    else welcome.setThumbnailUrl(thumbnailUrl);

                                                                                    // Title
                                                                                    channel.sendMessage("Please enter a Title:\n" +
                                                                                            "*Type \"none\" if you don't want to set a Title.*")
                                                                                            .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                                                                                    waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                                                                            titleEvent -> titleEvent.getAuthor().equals(event.getAuthor()) && titleEvent.getChannel().equals(event.getChannel()),
                                                                                            titleEvent -> {
                                                                                                Message titleMessage = titleEvent.getMessage();
                                                                                                wizardMessageIds.add(titleMessage.getIdLong());
                                                                                                String title = titleMessage.getContentDisplay();

                                                                                                if (title.toLowerCase().equals("none")) welcome.setTitle(null);
                                                                                                else welcome.setTitle(title);

                                                                                                // Title Url
                                                                                                channel.sendMessage("Please enter a Title Url:\n" +
                                                                                                        "*Type \"none\" if you don't want to set a Title Url.*")
                                                                                                        .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                                                                                                waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                                                                                        titleUrlEvent -> titleUrlEvent.getAuthor().equals(event.getAuthor()) && titleUrlEvent.getChannel().equals(event.getChannel()),
                                                                                                        titleUrlEvent -> {
                                                                                                            Message titleUrlMessage = titleUrlEvent.getMessage();
                                                                                                            wizardMessageIds.add(titleUrlMessage.getIdLong());
                                                                                                            String titleUrl = titleUrlMessage.getContentDisplay();

                                                                                                            if (Parser.isValidUrl(titleUrl) || titleUrl.toLowerCase().equals("none")) {
                                                                                                                if (titleUrl.toLowerCase().equals("none")) welcome.setTitleUrl(null);
                                                                                                                else if (welcome.getTitle() == null) welcome.setTitleUrl(null);
                                                                                                                else welcome.setTitleUrl(titleUrl);

                                                                                                                // Description
                                                                                                                channel.sendMessage("Please enter a Description:\n" +
                                                                                                                        "*Type \"none\" if you don't want to set a Description.*")
                                                                                                                        .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                                                                                                                waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                                                                                                        descriptionEvent -> descriptionEvent.getAuthor().equals(event.getAuthor()) && descriptionEvent.getChannel().equals(event.getChannel()),
                                                                                                                        descriptionEvent -> {
                                                                                                                            Message descriptionMessage = descriptionEvent.getMessage();
                                                                                                                            wizardMessageIds.add(descriptionMessage.getIdLong());
                                                                                                                            String description = descriptionMessage.getContentDisplay();

                                                                                                                            if (description.toLowerCase().equals("none")) welcome.setDescription(null);
                                                                                                                            else welcome.setDescription(Parser.parseMentions(description));

                                                                                                                            // Fields

                                                                                                                            // Image Url
                                                                                                                            channel.sendMessage("Please enter an Image Url:\n" +
                                                                                                                                    "*Type \"none\" if you don't want to set an Image.*")
                                                                                                                                    .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                                                                                                                            waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                                                                                                                    imageUrlEvent -> imageUrlEvent.getAuthor().equals(event.getAuthor()) && imageUrlEvent.getChannel().equals(event.getChannel()),
                                                                                                                                    imageUrlEvent -> {
                                                                                                                                        Message imageUrlMessage = imageUrlEvent.getMessage();
                                                                                                                                        wizardMessageIds.add(imageUrlMessage.getIdLong());
                                                                                                                                        String imageUrl = imageUrlMessage.getContentDisplay();
                                                                                                                                        if (Parser.isValidDirectUrl(imageUrl) || imageUrl.toLowerCase().equals("none")) {
                                                                                                                                            if (imageUrl.toLowerCase().equals("none")) welcome.setImageUrl(null);
                                                                                                                                            else welcome.setImageUrl(imageUrl);

                                                                                                                                            // Footer Text
                                                                                                                                            channel.sendMessage("Please enter a Footer Text:\n" +
                                                                                                                                                    "*Type \"none\" if you don't want to set a Footer.*")
                                                                                                                                                    .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                                                                                                                                            waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                                                                                                                                    footerTextEvent -> footerTextEvent.getAuthor().equals(event.getAuthor()) && footerTextEvent.getChannel().equals(event.getChannel()),
                                                                                                                                                    footerTextEvent -> {
                                                                                                                                                        Message footerTextMessage = footerTextEvent.getMessage();
                                                                                                                                                        wizardMessageIds.add(footerTextMessage.getIdLong());
                                                                                                                                                        String footerText = footerTextMessage.getContentDisplay();

                                                                                                                                                        if (footerText.toLowerCase().equals("none")) welcome.setFooterText(null);
                                                                                                                                                        else welcome.setFooterText(footerText);

                                                                                                                                                        // Footer Icon Url
                                                                                                                                                        channel.sendMessage("Please enter a Footer Icon Url:\n" +
                                                                                                                                                                "*Type \"none\" if you don't want to set a Footer Icon.*")
                                                                                                                                                                .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                                                                                                                                                        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                                                                                                                                                footerIconUrlEvent -> footerIconUrlEvent.getAuthor().equals(event.getAuthor()) && footerIconUrlEvent.getChannel().equals(event.getChannel()),
                                                                                                                                                                footerIconUrlEvent -> {
                                                                                                                                                                    Message footerIconUrlMessage = footerIconUrlEvent.getMessage();
                                                                                                                                                                    wizardMessageIds.add(footerIconUrlMessage.getIdLong());
                                                                                                                                                                    String footerIconUrl = footerIconUrlMessage.getContentDisplay();
                                                                                                                                                                    if (Parser.isValidDirectUrl(footerIconUrl) || footerIconUrl.toLowerCase().equals("none")) {
                                                                                                                                                                        if (footerIconUrl.equals("none")) welcome.setFooterIconUrl(null);
                                                                                                                                                                        else if (welcome.getFooterText() == null) welcome.setFooterIconUrl(null);
                                                                                                                                                                        else welcome.setFooterIconUrl(footerIconUrl);

                                                                                                                                                                        // Timestamp
                                                                                                                                                                        channel.sendMessage("Please state whether you want a timestamp to be included in the footer.\n" +
                                                                                                                                                                                "Add Timestamp? Yes/No:")
                                                                                                                                                                                .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                                                                                                                                                                        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                                                                                                                                                                timestampEvent -> timestampEvent.getAuthor().equals(event.getAuthor()) && timestampEvent.getChannel().equals(event.getChannel()),
                                                                                                                                                                                timestampEvent -> {
                                                                                                                                                                                    Message timestampMessage = timestampEvent.getMessage();
                                                                                                                                                                                    wizardMessageIds.add(timestampMessage.getIdLong());
                                                                                                                                                                                    boolean hasTimestamp = timestampMessage.getContentDisplay().toLowerCase().equals("yes");
                                                                                                                                                                                    ZoneId zoneId = internalGuild.getOffset() == null ? ZoneId.of("UTC") : ZoneId.of(internalGuild.getOffset());
                                                                                                                                                                                    if (hasTimestamp) welcome.setTimestamp(OffsetDateTime.now(zoneId));
                                                                                                                                                                                    else welcome.setTimestamp(null);

                                                                                                                                                                                    // Unlock Emote
                                                                                                                                                                                    channel.sendMessage("Please provide an emoji or emote (that I have access to), that should be used to give the unlock role:\n" +
                                                                                                                                                                                            "*Type \"none\" if you don't want to use the unlock feature.*")
                                                                                                                                                                                            .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                                                                                                                                                                                    waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                                                                                                                                                                            unlockEmoteEvent -> unlockEmoteEvent.getAuthor().equals(event.getAuthor()) && unlockEmoteEvent.getChannel().equals(event.getChannel()),
                                                                                                                                                                                            unlockEmoteEvent -> {
                                                                                                                                                                                                Message unlockEmoteMessage = unlockEmoteEvent.getMessage();
                                                                                                                                                                                                wizardMessageIds.add(unlockEmoteMessage.getIdLong());
                                                                                                                                                                                                String emoji;
                                                                                                                                                                                                Emote emote;
                                                                                                                                                                                                if (unlockEmoteMessage.getEmotes().isEmpty()) {
                                                                                                                                                                                                    if (unlockEmoteMessage.getContentDisplay().toLowerCase().equals("none")) {
                                                                                                                                                                                                        welcome.setReactEmoji(null);
                                                                                                                                                                                                        welcome.setReactEmote(null);
                                                                                                                                                                                                    } else {
                                                                                                                                                                                                        emoji = unlockEmoteMessage.getContentDisplay();
                                                                                                                                                                                                        unlockEmoteMessage.addReaction(emoji).queue();
                                                                                                                                                                                                        welcome.setReactEmoji(emoji);
                                                                                                                                                                                                    }
                                                                                                                                                                                                } else {
                                                                                                                                                                                                    emote = unlockEmoteMessage.getEmotes().get(0);
                                                                                                                                                                                                    unlockEmoteMessage.addReaction(emote).queue();
                                                                                                                                                                                                    welcome.setReactEmote(emote);
                                                                                                                                                                                                }

                                                                                                                                                                                                // Unlock Role
                                                                                                                                                                                                channel.sendMessage("Please ping the unlock role or enter its ID:\n" +
                                                                                                                                                                                                        "*Type \"none\" if you don't want to use the unlock feature.*")
                                                                                                                                                                                                        .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                                                                                                                                                                                                waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                                                                                                                                                                                        unlockRoleEvent -> unlockRoleEvent.getAuthor().equals(event.getAuthor()) && unlockRoleEvent.getChannel().equals(event.getChannel()),
                                                                                                                                                                                                        unlockRoleEvent -> {
                                                                                                                                                                                                            Message unlockRoleMessage = unlockRoleEvent.getMessage();
                                                                                                                                                                                                            wizardMessageIds.add(unlockRoleMessage.getIdLong());
                                                                                                                                                                                                            if (unlockRoleMessage.getMentionedRoles().isEmpty()) {
                                                                                                                                                                                                                if (unlockRoleMessage.getContentDisplay().toLowerCase().equals("none")) {
                                                                                                                                                                                                                    welcome.setRole(null);
                                                                                                                                                                                                                    welcome.setReactEmote(null);
                                                                                                                                                                                                                    welcome.setReactEmoji(null);
                                                                                                                                                                                                                } else {
                                                                                                                                                                                                                    long roleId = Long.parseLong(unlockRoleMessage.getContentDisplay());
                                                                                                                                                                                                                    if (welcome.getReactEmoji() == null && welcome.getReactEmote() == null) welcome.setRole(null);
                                                                                                                                                                                                                    else welcome.setRole(event.getGuild().getRoleById(roleId));
                                                                                                                                                                                                                }
                                                                                                                                                                                                            } else {
                                                                                                                                                                                                                if (welcome.getReactEmoji() == null && welcome.getReactEmote() == null) welcome.setRole(null);
                                                                                                                                                                                                                else welcome.setRole(unlockRoleMessage.getMentionedRoles().get(0));
                                                                                                                                                                                                            }

                                                                                                                                                                                                            // Channel
                                                                                                                                                                                                            channel.sendMessage("We are done! Please ping the desired text channel for the welcome message:")
                                                                                                                                                                                                                    .queue(sentMessage -> wizardMessageIds.add(sentMessage.getIdLong()));
                                                                                                                                                                                                            waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                                                                                                                                                                                                    channelEvent -> channelEvent.getAuthor().equals(event.getAuthor()) && channelEvent.getChannel().equals(event.getChannel()),
                                                                                                                                                                                                                    channelEvent -> {
                                                                                                                                                                                                                        Message channelMessage = channelEvent.getMessage();
                                                                                                                                                                                                                        wizardMessageIds.add(channelMessage.getIdLong());
                                                                                                                                                                                                                        if (!channelMessage.getMentionedChannels().isEmpty()) {
                                                                                                                                                                                                                            welcome.setMessageChannel(channelMessage.getMentionedChannels().get(0));
                                                                                                                                                                                                                            sendWelcomeMessage(welcome, event, wizardMessageIds);
                                                                                                                                                                                                                            event.reactSuccess();
                                                                                                                                                                                                                        } else endWithError(wizardMessageIds, event, "[Error] Invalid channel mention.");
                                                                                                                                                                                                                    }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't provide a channel mention."));
                                                                                                                                                                                                        }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't provide a role mention or role ID."));
                                                                                                                                                                                            }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't provide an emoji or emote."));
                                                                                                                                                                                }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't answer."));
                                                                                                                                                                    } else endWithError(wizardMessageIds, event, "[Error] Invalid Footer Icon Url.");
                                                                                                                                                                }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't provide a Footer Icon Url."));
                                                                                                                                                    }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't provide a Footer Text."));
                                                                                                                                        } else endWithError(wizardMessageIds, event, "[Error] Invalid Image Url.");
                                                                                                                                    }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't provide an Image Url."));
                                                                                                                        }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't provide a Description."));
                                                                                                            } else endWithError(wizardMessageIds, event, "[Error] Invalid Title Url.");
                                                                                                        }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't provide a Title Url."));
                                                                                            }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't provide a Title."));
                                                                                } else endWithError(wizardMessageIds, event, "[Error] Invalid Thumbnail Url.");
                                                                            }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't provide a Thumbnail Url."));
                                                                } else endWithError(wizardMessageIds, event, "[Error] Invalid Author Icon Url.");
                                                            }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't provide an Author Icon Url."));
                                                } else endWithError(wizardMessageIds, event, "[Error] Invalid Author Url.");
                                            }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't provide a Author Url."));
                                }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't provide an Author Name."));
                    }
                }, 1, TimeUnit.HOURS, () -> endWithError(wizardMessageIds, event, "[Timeout] You didn't answer me."));
    }

    private void endWithError(List<Long> wizardMessageIds, CommandEvent event, String errorMessage) {
        for (Long message : wizardMessageIds)
            event.getChannel().getMessageById(message).queue(msg -> msg.delete().queue());
        event.reply(errorMessage);
        event.reactError();
    }

    private void sendWelcomeMessage(Welcome welcome, CommandEvent event, List<Long> wizardMessageIds) {
        for (Long message : wizardMessageIds)
            event.getChannel().getMessageById(message).queue(msg -> msg.delete().queue());

        EmbedBuilder eb = new EmbedBuilder();
        try {
            eb.setColor(new servant.User(event.getAuthor().getIdLong()).getColor());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        eb.setAuthor(welcome.getAuthorName(), welcome.getAuthorUrl(), welcome.getAuthorIconUrl());
        eb.setThumbnail(welcome.getThumbnailUrl());
        eb.setTitle(welcome.getTitle(), welcome.getTitleUrl());
        eb.setDescription(welcome.getDescription());
        eb.setImage(welcome.getImageUrl());
        eb.setFooter(welcome.getFooterText(), welcome.getFooterIconUrl());
        eb.setTimestamp(welcome.getTimestamp());

        welcome.getMessageChannel().sendMessage(eb.build()).queue(sentMessage -> {
            String emoji = welcome.getReactEmoji();
            Emote emote = welcome.getReactEmote();
            if (emoji != null) sentMessage.addReaction(emoji).queue();
            else if (emote != null) sentMessage.addReaction(emote).queue();

            // Save message id and emote/emoji in database for Listener.
        });
    }
}
