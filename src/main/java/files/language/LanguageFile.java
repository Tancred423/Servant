// Author: Tancred423 (https://github.com/Tancred423)
package files.language;

import nu.studer.java.util.OrderedProperties;

import java.io.*;

public class LanguageFile {
    static void createDefaultEN_GB() throws IOException {
        OrderedProperties en_gb = new OrderedProperties();
        OutputStream os = new FileOutputStream(System.getProperty("user.dir") + "/resources/lang/en_gb.ini");

        // General
        en_gb.setProperty("permission", ":x: You must have the %s permission in this Guild to use that!");
        en_gb.setProperty("blocking_dm", "Help cannot be sent because you are blocking Direct Messages.");
        en_gb.setProperty("invalid_mention", "Invalid mention.");
        en_gb.setProperty("current_prefix", "Current prefix: %s");

        // Features
        /// Achievement
        en_gb.setProperty("achievement_excalibur", "Excalibur");
        en_gb.setProperty("achievement_level10", "Level 10");
        en_gb.setProperty("achievement_level20", "Level 20");
        en_gb.setProperty("achievement_level30", "Level 30");
        en_gb.setProperty("achievement_level40", "Level 40");
        en_gb.setProperty("achievement_level50", "Level 50");
        en_gb.setProperty("achievement_level60", "Level 60");
        en_gb.setProperty("achievement_level69", "Nice Level");
        en_gb.setProperty("achievement_level70", "Level 70");
        en_gb.setProperty("achievement_level80", "Level 80");
        en_gb.setProperty("achievement_level90", "Level 90");
        en_gb.setProperty("achievement_level100", "Level 100");
        en_gb.setProperty("achievement_love42", "Found the answer.");
        en_gb.setProperty("achievement_love69", "Nice Love");

        /// Invite
        en_gb.setProperty("invite_author", "%s, at your service!");
        en_gb.setProperty("invite_description", "Thank you for choosing me to assist you and your server.\n" +
                "I have a lot of features. Most of them are enabled by default but some of them are not.\n" +
                "Type `%sguild togglestatus` to check the status of all available features.\n" +
                "Then you can enable/disable the features to your desire.\n" +
                "\n" +
                "To get started, I recommend you to use my `%shelp` command.\n" +
                "To get detailed help, you simply can type the command name without any arguments. E.g. `%savatar`\n" +
                "\n" +
                "If you need further help, you can join my support server or contact my creator directly.\n" +
                "Support Server: [Click to join](https://%s)\n" +
                "Creator Name: %s#%s\n" +
                "Email: `servant@tanc.red`\n" +
                "\n" +
                "Have fun!");
        en_gb.setProperty("invite_footer", "You are receiving this message, because someone invited me to your guild (%s).");

        /// Kick
        en_gb.setProperty("kick_author", "Farewell!");
        en_gb.setProperty("kick_description", "It's very sad to hear, that you don't need me anymore.\n" +
                "If there is anything to improve, I am always open for feedback.\n" +
                "\n" +
                "To submit feedback, you can join my support server or contact my creator directly.\n" +
                "Support Server: [Click to join](https://%s)\n" +
                "Creator Name: %s#%s\n" +
                "Email: `servant@tanc.red`\n");
        en_gb.setProperty("kick_footer", "You are receiving this message, because someone kicked me from your guild (%s) or your guild was deleted.");

        /// Patreon Handler
        en_gb.setProperty("patreon_warning", "You have to be a %s to use this feature!");

        /// Presence
        en_gb.setProperty("presence_0", "v%s | %shelp");
        en_gb.setProperty("presence_1", "%s users | %shelp");
        en_gb.setProperty("presence_2", "%s servers | %shelp");
        en_gb.setProperty("presence_3", "%spatreon | %shelp");

        // Owner
        /// Add Gif
        en_gb.setProperty("addgif_description", "This is a command to add gifs for the interaction commands.");
        en_gb.setProperty("addgif_usage", "**Add a gif**\n" +
                "Command: `%s%s [interaction] [gifUrl]`\n" +
                "Example: `%s%s slap https://i.imgur.com/bbXmAx2.gif`\n");
        en_gb.setProperty("addgif_hint", "Image url has to be a [direct link](https://www.urbandictionary.com/define.php?term=direct%20link).");
        en_gb.setProperty("addgif_args", "2 arguments are needed.\n... [interaction] [gifUrl]");
        en_gb.setProperty("addgif_interaction", "Invalid interaction.");
        en_gb.setProperty("addgif_direct_link", "Not a valid gif url. It has to be a direct link!");

        /// Server List
        en_gb.setProperty("guildlist_members", "Members");
        en_gb.setProperty("guildlist_connected", " Servers that **%s** is connected to");

        // Moderation
        /// Auto Role
        en_gb.setProperty("autorole_description", "This role will be automatically given to any new member.");
        en_gb.setProperty("autorole_usage", "**Setting up an autorole**\n" +
                "Command: `%s%s set [@role] [optinal delay]`\n" +
                "Example 1: `%s%s set @Member`\n" +
                "Example 2: `%s%s set @Member 10` - Role after 10 minutes.\n" +
                "\n" +
                "**Unsetting the autorole**\n" +
                "Command: `%s%s unset`\n" +
                "\n" +
                "**Showing current autorole**\n" +
                "Command: `%s%s show`");
        en_gb.setProperty("autorole_hint", "Pinging the role on setup, will also ping anyone with this role.\n" +
                "Execute the command in a hidden channel to prevent pinging many people.");
        en_gb.setProperty("autorole_no_role", "You did not provide a role.");
        en_gb.setProperty("autorole_missing", "No autorole was set.");
        en_gb.setProperty("autorole_no_current", "There is no current autorole.");
        en_gb.setProperty("autorole_current", "The current autorole is: %s (%s) with a delay of %s minutes.");
        en_gb.setProperty("autorole_first_arg", "Invalid first argument.\nEither `set`, `unset` or `show`");

        /// Birthday
        en_gb.setProperty("birthday_description", "Manage the birthdays on this server:\n" +
                "- Members can add their birthday to the list.\n" +
                "- Members can remove their birthday from the list.\n" +
                "- Members can create a one-time list of all birthdays of this server.\n" +
                "- Moderators can set up a notification channel, where %s will post a Happy-Birthday-Message.\n" +
                "- Moderators can create a list of all birthdays of this server, which will be updated every day.");
        en_gb.setProperty("birthday_usage", "**Set notification channel**\n" +
                "Command: `%s%s #channel`\n" +
                "**Unset notification channel**\n" +
                "Command: `%s%s unsetchannel`\n" +
                "**Create an auto updating birthday list**\n" +
                "Command: `%s%s updatelist`\n" +
                "**Set birthday**\n" +
                "Command: `%s%s yyyy-MM-dd`\n" +
                "Example: `%s%s 1990-12-31`\n" +
                "**Unset birthday**\n" +
                "Command: `%s%s unsetbirthday`\n" +
                "**Create a non-updating birthday list**\n" +
                "Command: `%s%s list`");
        en_gb.setProperty("birthday_hint", "The commands have different required permissions.\n" +
                "See description:\n" +
                "- \"Members\": Everyone\n" +
                "- \"Moderators\": Manage Channels");
        en_gb.setProperty("birthday_countdown", "Countdown");
        en_gb.setProperty("birthday_countdown_value", "in %s days");
        en_gb.setProperty("birthday_date", "Date");
        en_gb.setProperty("birthday_name", "Name");
        en_gb.setProperty("birthday_missing", "No birthdays were set.");
        en_gb.setProperty("birthday_guild", "%s birthdays");
        en_gb.setProperty("birthday_as_of", "As of");
        en_gb.setProperty("birthday_gratulation", "Happy Birthday %s!");
        en_gb.setProperty("birthday_not_set", "You never set a birthday.");
        en_gb.setProperty("birthday_invalid", "Invalid argument.\n" +
                "Mods can...\n" +
                "... mention a channel to set up the notification channel.\n" +
                "... type `unsetchannel` to unset the notification channel.\n" +
                "... type `updatelist` to create a list of all birthdays, that will update itself every day.\n" +
                "Users can...\n" +
                "... enter their birthday (yyyy-MM-dd) to enter the birthday list.\n" +
                "... type `unsetbirthday` to leave the birthday list.\n" +
                "... type `list` to create a list of all birthdays, that shows the current state, but does not update itself.");

        /// Best of's
        en_gb.setProperty("bestof_usage", "**Set up vote-emote/emoji**\n" +
                "Command: `%s%s [emoji or emote]`\n" +
                "Example (Emoji): `%s%s ⭐`\n" +
                "Example (Emote): `%s%s` %s\n" +
                "\n" +
                "**Set up best of channel**\n" +
                "Command: `%s%s #channel`\n" +
                "\n" +
                "**Set up a number of people that have to vote**\n" +
                "Command: `%s%s [number]`\n" +
                "Example: `%s%s 10`\n" +
                "\n" +
                "**Set up a percentage of online members that have to vote**\n" +
                "Command: `%s%s [percentage]%s`\n" +
                "Example: `%s%s 50%s`\n" +
                "\n" +
                "**Show current set up**\n" +
                "Command: `%s%s show`");
        en_gb.setProperty("bestof_hint", "Setting up both number and percentage has a big advantage:\n" +
                "- As your sever is growing it is nice to have a percentage based amount of online members that have to vote.\n" +
                "- But at night, there may be just a few members who could abuse this, so you can set up a minimum number of votes.");
        en_gb.setProperty("bestof_emote", "Emote/Emoji");
        en_gb.setProperty("bestof_noemote", "No emote/emoji set");
        en_gb.setProperty("bestof_number", "Number");
        en_gb.setProperty("bestof_nonumber", "No number set");
        en_gb.setProperty("bestof_percentage", "Percentage");
        en_gb.setProperty("bestof_nopercentage", "No percentage set");
        en_gb.setProperty("bestof_channel", "Channel");
        en_gb.setProperty("bestof_nochannel", "No channel set");
        en_gb.setProperty("bestof_numbertoobig", "This number is way too big!");
        en_gb.setProperty("bestof_invalidpercentage", "This percentage is invalid.");
        en_gb.setProperty("bestof_invalidemote", "I cannot find that emote! Make sure I have access to it!");
        en_gb.setProperty("bestof_invalidemoji", "Invalid emoji, master!");
        en_gb.setProperty("bestof_jump", "Click here to jump to original message.");
        en_gb.setProperty("bestof_footer", "%s votes | #%s");

        /// Best of Image
        en_gb.setProperty("bestofimage_description", "Users can vote images that will be posted in a best of channel.\n" +
                "You can set up the vote-emote/emoji, the best of channel and the amount of people that have to vote (fix number and/or percentage of online users).");
        en_gb.setProperty("bestofimage_setup", "Best Of Image Setup");

        /// Best of Quote
        en_gb.setProperty("bestofquote_description", "Users can vote messages that will be posted in a best of channel.\n" +
                "You can set up the vote-emote/emoji, the best of channel and the amount of people that have to vote (fix number and/or percentage of online users).");
        en_gb.setProperty("bestofquote_setup", "Best Of Quote Setup");

        /// Clear
        en_gb.setProperty("clear_description", "Deletes up to 100 messages.\n" +
                "Can delete user specific messages from the past 100 messages.\n" +
                "Messages older than two weeks cannot be deleted because of Discord's restrictions.");
        en_gb.setProperty("clear_usage", "**Delete some messages**\n" +
                "Command: `%s%s [1 - 100 OR @user]`\n" +
                "Example 1: `%s%s 50`\n" +
                "Example 2: `%s%s @name");
        en_gb.setProperty("clear_hint", "The range is inclusively, so you can also delete just 1 or a total of 100 messages.");
        en_gb.setProperty("clear_input", "You only can put in numbers or a user mention!");
        en_gb.setProperty("clear_sub_one", "Input cannot be lower than 1.");
        en_gb.setProperty("clear_cleared", "%s messages cleared");

        /// Join + Leave general
        en_gb.setProperty("joinleave_nochannel_mention", "No channel was mentioned.");
        en_gb.setProperty("joinleave_unset_fail", "No channel was set yet.");
        en_gb.setProperty("joinleave_nochannel_set", "No channel is set.");
        en_gb.setProperty("joinleave_current", "Current channel: %s");
        en_gb.setProperty("joinleave_firstarg", "Either `set`, `unset` or `show`");

        /// Join
        en_gb.setProperty("join_description", "The bot will post a notification once a user joins the server.");
        en_gb.setProperty("join_usage", "**Setting up a join notification channel**\n" +
                "Command: `%s%s set [#channel]`\n" +
                "Example: `%s%s set #welcome`\n" +
                "\n" +
                "**Unsetting this channel**\n" +
                "Command: `%s%s unset`\n" +
                "\n" +
                "**Showing current notification channel**\n" +
                "Command: `%s%s show`");
        en_gb.setProperty("join_hint", "Shows a message like \"Name#1234 just joined GuildName!\"");
        en_gb.setProperty("join_author", "Welcome %s#%s to %s");
        en_gb.setProperty("join_embeddescription", "Enjoy your stay!");
        en_gb.setProperty("join_footer", "Joined at");

        /// Leave
        en_gb.setProperty("leave_description", "The bot will post a notification once a user leaves the server.");
        en_gb.setProperty("leave_usage", "**Setting up a leave notification channel**\n" +
                "Command: `%s%s set [#channel]`\n" +
                "Example: `%s%s set #welcome`\n" +
                "\n" +
                "**Unsetting this channel**\n" +
                "Command: `%s%s unset`\n" +
                "\n" +
                "**Showing current notification channel**\n" +
                "Command: `%s%s show`");
        en_gb.setProperty("leave_hint", "Shows a message like \"Name#1234 just left GuildName!\"");
        en_gb.setProperty("leave_left", "%s#%s just left %s!");
        en_gb.setProperty("leave_author", "Farewell %s#%s");
        en_gb.setProperty("leave_embeddescription", "We are sorry to see you go!");
        en_gb.setProperty("leave_footer", "Left at");

        /// Level Role
        en_gb.setProperty("levelrole_description", "Set up roles that are assigned if a member hits a certain level.");
        en_gb.setProperty("levelrole_usage", "**Set up a role**\n" +
                "- Command: `%s%s set [level] @role`\n" +
                "- Example: `%s%s set 10 @SuperMember`\n" +
                "\n" +
                "**Unset a role**\n" +
                "- Command: `%s%s unset [level] @role`\n" +
                "- Example: `%s%s unset 10 @SuperMember`\n" +
                "\n" +
                "**Show current rank roles**\n" +
                "- Command: `%s%s show`\n" +
                "\n" +
                "**Refresh**\n" +
                "- Command: `%s%s refresh`");
        en_gb.setProperty("levelrole_hint", "You can set up multiple roles for one level.\n" +
                "With refresh, you can reassign the roles to the members in case they somehow didn't get it. This does not remove any roles.");
        en_gb.setProperty("levelrole_missing", "You have to provide a level and mention a role.");
        en_gb.setProperty("levelrole_invalidlevel", "Invalid level.");
        en_gb.setProperty("levelrole_levelrole", "Level Role");
        en_gb.setProperty("levelrole_empty", "No level roles set up.");
        en_gb.setProperty("levelrole_current", "Current Level Roles");
        en_gb.setProperty("levelrole_alreadyset", "This role was already set for this level.");
        en_gb.setProperty("levelrole_role_singular", "You also gained following role:");
        en_gb.setProperty("levelrole_role_plural", "You also gained following roles:");
        en_gb.setProperty("levelrole_levelup", "LEVEL UP");

        /// Livestream
        en_gb.setProperty("livestream_description", "You can set up streamers, one stream notification channel and one streamer role.\n" +
                "Once a streamer goes online, a notification **with** @everyone will be posted and the streamer will receive the set role.\n" +
                "If you toggle the streamer mode `off`, a notification **without** @everyone will be posted and the member will receive the set role.");
        en_gb.setProperty("livestream_usage", "**(Un)setting a streamer**\n" +
                "Set: `%s%s set @user`\n" +
                "Unset: `%s%s unset @user`\n" +
                "\n" +
                "**(Un)setting the notification channel**\n" +
                "Set: `%s%s set #channel`\n" +
                "Unset: `%s%s unset #channel`\n" +
                "\n" +
                "**(Un)settings the streaming role**\n" +
                "Set: `%s%s set @role`\n" +
                "Unset: `%s%s unset @role`\n" +
                "\n" +
                "**Toggle streamer mode**\n" +
                "Command: `%s%s toggle`\n" +
                "\n" +
                "**Showing current stream settings**\n" +
                "Command: `%s%s show`");
        en_gb.setProperty("livestream_hint", "There can be multiple streamers but only one notification channel.\n" +
                "Streamer Mode: Only set up streamers will get notifications and roles.\n" +
                "Public Mode: Anyone will get notifications and roles.");
        en_gb.setProperty("livestream_missingmention", "You didn't mention a channel, user nor role.");
        en_gb.setProperty("livestream_toomanymentions", "You mentioned too much. One at a time!");
        en_gb.setProperty("livestream_nochannel", "There was no channel set.");
        en_gb.setProperty("livestream_nostreamer", "This user is not a streamer.");
        en_gb.setProperty("livestream_norole", "This role was not set.");
        en_gb.setProperty("livestream_firstarg", "Either `set`, `unset` or `show`");
        en_gb.setProperty("livestream_settings", "Livetream Settings");
        en_gb.setProperty("livestream_notificationchannel", "Notification Channel");
        en_gb.setProperty("livestream_role", "Livestreaming Role");
        en_gb.setProperty("livestream_mode", "Streamer Mode");
        en_gb.setProperty("livestream_streamers", "Streamers");
        en_gb.setProperty("livestream_nochannelset", "No channel set");
        en_gb.setProperty("livestream_noroleset", "No role set");
        en_gb.setProperty("livestream_streamermode", "Streamer Mode");
        en_gb.setProperty("livestream_publicmode", "Public Mode");
        en_gb.setProperty("livestream_nostreamersset", "No streamers set");
        en_gb.setProperty("livestream_announcement_title", "Livestream!");
        en_gb.setProperty("livestream_announcement", "%s just went live on [Twitch (click me)](%s)!");
        en_gb.setProperty("livestream_announcement_game", "Streaming %s");

        /// Media Only Channel
        en_gb.setProperty("mediaonlychannel_description", "If a member writes a normal message into a text channel that is marked as mediaonlychannel, the message will be removed and a warning will be posted.\n" +
                "Members only can post links or upload files.\n" +
                "This can be very handy for e.g. a memes channel.");
        en_gb.setProperty("mediaonlychannel_usage", "**Setting up an media only channel**\n" +
                "Command: `%s%s set [#channel]`\n" +
                "Example: `%s%s set #images`\n" +
                "\n" +
                "**Unsetting an media only channel**\n" +
                "Command: `%s%sunset [#channel]`\n" +
                "Example: `%s%s unset #images`\n" +
                "\n" +
                "**Showing current media only channels**\n" +
                "Command: `%s%s show`");
        en_gb.setProperty("mediaonlychannel_hint", "You can have multiple media only channels.");
        en_gb.setProperty("mediaonlychannel_missingmention", "You did not provide a channel mention.");
        en_gb.setProperty("mediaonlychannel_invalidchannel", "The given channel is invalid.");
        en_gb.setProperty("mediaonlychannel_unset_fail", "This channel was not set as an media only channel.");
        en_gb.setProperty("mediaonlychannel_nochannels", "There are no media only channels.");
        en_gb.setProperty("mediaonlychannel_firstarg", "Invalid first argument.\nEither `set`, `unset` or `show`");
        en_gb.setProperty("mediaonlychannel_warning", "%s, this is a media only channel!\n" +
                "You are allowed to:\n" +
                "- Send upload files with an optional description.\n" +
                "- Post a valid url with an optional description.\n" +
                "*This message will be deleted in 30 seconds.*");

        /// Reaction Role
        en_gb.setProperty("reactionrole_description", "You can add reaction to a message via this command. Once a member clicks on the corresponding reaction, he will get the designated role\n" +
                "This allows you easy role management via reactions.");
        en_gb.setProperty("reactionrole_usage", "**Set up a reaction to manage user roles**\n" +
                "Command: `%s%s set [#channel | channel ID] [message ID] [emoji/emote] [@role | role ID]`\n" +
                "Example: %s%s set #test-channel 999999999999999999 %s @role\n" +
                "\n" +
                "**Unset a reaction role**\n" +
                "Command: `%s%s unset [#channel | channel ID] [message Id] [emoji/emote]`\n" +
                "Example: %s%s unset #test-channel 999999999999999999 %s");
        en_gb.setProperty("reactionrole_hint", "**How to get ID's:**\n" +
                "Role: `\\@role` - THIS ALSO PINGS. Maybe do this is a non public channel.\n" +
                "Channels: \n" +
                "1. Activate Discord Developer Mode: User Settings → Appearance → ADVANCED → Developer Mode\n" +
                "2. Rightclick a text channel → Copy ID");
        en_gb.setProperty("reactionrole_toofewargs", "Too few arguments. You have to declare the channel ID, message ID, an emoji or emote and a role mention or role ID behind `set`.");
        en_gb.setProperty("reactionrole_alreadyset", "This emoji or emote was already set. Unset first if you want to update the emoji or emote.");
        en_gb.setProperty("reactionrole_notset", "This emoji or emote was not set.");
        en_gb.setProperty("reactionrole_firstarg", "Either `set` or `unset` a reaction.");
        en_gb.setProperty("reactionrole_insufficient", "Insufficient permissions or problem with hierarchy.");

        /// Role
        en_gb.setProperty("role_description", "Assing or remove roles from members.");
        en_gb.setProperty("role_usage", "Command: `%s%s @user [roleName]`\n" +
                "Example: `%s%s @name member`");
        en_gb.setProperty("role_hint", "I will remove the role if the member already has it and provide it if the member doesn't have it already.");
        en_gb.setProperty("role_missing", "Missing role name.");
        en_gb.setProperty("role_notfound", "Sorry, master! I couldn't find a role with that name.");

        /// Server
        en_gb.setProperty("server_description", "With this command you can personalize the bot to your guild's desire.");
        en_gb.setProperty("server_usage", "**Setting an offset**\n" +
                "Command: `%s%s set offset [offset]`\n" +
                "Example: `%s%s set offset +01:00`\n" +
                "\n" +
                "**Unsetting the offset**\n" +
                "Command: `%s%s unset offset`\n" +
                "\n" +
                "**Setting an server specific prefix**\n" +
                "Command: `%s%s set prefix [prefix]`\n" +
                "Example: `%s%s set prefix -`\n" +
                "\n" +
                "**Unsettings the prefix**\n" +
                "Command: `%s%s unset prefix`\n" +
                "\n" +
                "**Setting a language**\n" +
                "Command: `%s%s set language [language code]`\n" +
                "Example: `%s%s set language de_de`\n" +
                "\n" +
                "**Unsettings the language**\n" +
                "Command: `%s%s unset language`\n" +
                "\n" +
                "**Show your current settings**\n" +
                "Command: `%s%s show`");
        en_gb.setProperty("server_hint", "Unsetting an offset will just remove your custom offset and you will use the default offset (%s) again.\n" +
                "Offset always adds on UTC.\n" +
                "Unsettings the prefix, recovers the default bot prefix (%s)\n\n" +
                "Current available languages:\n" +
                "- en_gb - British English\n" +
                "- de_de - German (Deutsch)");
        en_gb.setProperty("server_args_set", "To set a setting, there have to be 3 arguments.\n" +
                "... set [setting] [value]");
        en_gb.setProperty("server_offset", "Invalid offset.");
        en_gb.setProperty("server_prefix", "Invalid prefix.");
        en_gb.setProperty("server_invalidsetting", "This setting does not exist.");
        en_gb.setProperty("server_args_unset", "To unset a setting, there have to be 2 arguments.\n" +
                "... unset [setting]");
        en_gb.setProperty("server_offset_text", "Offset");
        en_gb.setProperty("server_prefix_text", "Prefix");
        en_gb.setProperty("server_language_text", "Language");
        en_gb.setProperty("server_settings", "Guild Settings");
        en_gb.setProperty("server_firstarg", "The first argument has to be either `set`, `unset` or `show`.");

        /// Server Setup
        en_gb.setProperty("setupwizard_introduction", "Welcome to the server setup wizard!\n" +
                "After doing this, you have all (3) base settings set up and we are ready to go!\n" +
                "On each question you have 15 minutes to answer, otherwise this setup will time out.\n" +
                "Do you want to start?");
        en_gb.setProperty("setupwizard_timeout", "The setup wizard timed out.");
        en_gb.setProperty("setupwizard_language", "Please state in what language you want me to speak in.\n" +
                "Currently available languages:\n" +
                "   \u200B⤷ `en_gb` - British English\n" +
                "   \u200B⤷ `de_de` - German (Deutsch)\n" +
                "**Provide the language code:**");
        en_gb.setProperty("setupwizard_language_repeated", "This language is not available.\n" +
                "Currently available languages:\n" +
                "   \u200B⤷ `en_gb` - British English\n" +
                "   \u200B⤷ `de_de` - German (Deutsch)\n" +
                "**Try again:**");
        en_gb.setProperty("setupwizard_prefix", "%s Language set.\n" +
                "Please provide a prefix you want to use.\n" +
                "If you don't want to change the prefix, just type `%s`:");
        en_gb.setProperty("setupwizard_prefix_repeated", "This prefix is not suitable.\n" +
                "If you don't want to change the prefix, just type `%s`\n" +
                "Try again:");
        en_gb.setProperty("setupwizard_offset", "%s Prefix set.\n" +
                "Please provide an offset to UTC to represent your timezone.\n" +
                "The format is: `+HH:mm` or `-HH:mm`. E.g. `+01:00`\n" +
                "To use UTC/GMT, just type `00:00`:");
        en_gb.setProperty("setupwizard_offset_repeated", "This offset is invalid.\n" +
                "The format is: `+HH:mm` or `-HH:mm`. E.g. `+01:00`\n" +
                "To use UTC/GMT, just type `00:00`\n" +
                "Try again:");
        en_gb.setProperty("setupwizard_done", "We are done :)");

        /// Toggle
        en_gb.setProperty("toggle_description", "You can toggle almost every feature on or off.\n" +
                "The level-feature is off by default in case you want to use it.\n" +
                "To check what features are toggleable, just use `%s%s all show`");
        en_gb.setProperty("toggle_usage", "**Manage one feautre**\n" +
                "Command: `%s%s [feature] [on|off|show]`\n" +
                "Example 1: `%s%s level on`\n" +
                "Example 2: `%s%s level off`\n" +
                "Example 3: `%s%s level show`\n" +
                "\n" +
                "**Manage all features**\n" +
                "Command: `%s%s all [on|off|show]`\n" +
                "Example 1: `%s%s all on`\n" +
                "Example 2: `%s%s all off`\n" +
                "Example 3: `%s%s all show`\n");
        en_gb.setProperty("toggle_hint", "Be careful with toggling all features on or off, as you may delete your perfect setup.\n" +
                "You may write `everything` instead of `all`.\n" +
                "You may write `status` instead of `show`.\n" +
                "Show will only show the current status without changing any values.");
        en_gb.setProperty("toggle_args", "Too few arguments.\n" +
                "toggle [feature] [on|off|show]\n" +
                "e.g.: toggle level off");
        en_gb.setProperty("toggle_invalid_feature", "Invalid feature.");
        en_gb.setProperty("toggle_invalid_argument", "Argument has to be `on`, `off` or `show`.");

        /// User
        en_gb.setProperty("user_description", "With this command you can personalize the bot to your desire.");
        en_gb.setProperty("user_usage", "**Setting an embed colour**\n" +
                "Command: `%s%s set colour [color code]`\n" +
                "Example: `%s%s set colour #FFFFFF`\n" +
                "\n" +
                "**Unsetting the embed colour**\n" +
                "Command: `%s%s unset colour`\n" +
                "\n" +
                "**Setting an offset**\n" +
                "Command: `%s%s set offset [offset]`\n" +
                "Example: `%s%s set offset +02:00`\n" +
                "\n" +
                "**Unsetting the offset**\n" +
                "Command: `%s%s unset offset`\n" +
                "\n" +
                "**Setting a prefix**\n" +
                "Command: `%s%s set prefix [prefix]`\n" +
                "Example: `%s%s set prefix -`\n" +
                "\n" +
                "**Unsetting the prefix**\n" +
                "Command: `%s%s unset prefix`\n" +
                "\n" +
                "**Setting a language**\n" +
                "Command: `%s%s set language [language code]`\n" +
                "Example: `%s%s set language de_de`\n" +
                "\n" +
                "**Unsetting the language**\n" +
                "Command: `%s%s unset language`\n" +
                "\n" +
                "**Hide yourself from stream highlighting**\n" +
                "Command: `%s%s streamhide [opt. guild ID]`\n" +
                "\n" +
                "**Show your current settings**\n" +
                "Command: `%s%s show`");
        en_gb.setProperty("user_hint", "You can set a custom embed colour. ($10 Patron)\n" +
                "An embed colour is the colour you can see right know on the left of this text field thingy.\n" +
                "Settings like offset, prefix and language only work in DM's. For server settings please refer to `%sserver`." +
                "Available languages:\n" +
                "- en_gb - British English\n" +
                "- de_de - German (Deutsch)\n" +
                "You can hide yourself from being highlighted with streaming role while streaming (server specific).");
        en_gb.setProperty("user_streamhide_description", "You have to use this in a guild to specify what guild you want to be muted on.\n" +
                "If you want to do this secretly, you can also provide a guild ID:\n" +
                "Command: `%s%s streamhide [guild ID]`\n" +
                "Example: `%s%s streamhide 99999999999999999`");
        en_gb.setProperty("user_streamhide_hidden", "You are now hidden!");
        en_gb.setProperty("user_streamhide_visible", "You are now visible!");
        en_gb.setProperty("user_args_set", "To set a setting, there have to be 3 arguments.\n" +
                "... set [setting] [value]");
        en_gb.setProperty("user_invalidcolor", "The given colour code is invalid.");
        en_gb.setProperty("user_invalidsetting", "This setting does not exist.");
        en_gb.setProperty("user_args_unset", "To unset a setting, there have to be 2 arguments.\n" +
                        "... unset [setting]");
        en_gb.setProperty("user_unset_fail", "Nothing to unset.");
        en_gb.setProperty("user_color_text", "Color");
        en_gb.setProperty("user_prefix_text", "Prefix");
        en_gb.setProperty("user_offset_text", "Offset");
        en_gb.setProperty("user_language_text", "Language");
        en_gb.setProperty("user_noservers", "No servers");
        en_gb.setProperty("user_streamhideservers", "Stream Hide Servers");
        en_gb.setProperty("user_settings", "User Settings");
        en_gb.setProperty("user_firstarg", "The first argument has to be either `set`, `unset` or `show`.");

        /// Voice Lobby
        en_gb.setProperty("voicelobby_description", "If a member joins an voice channel that is marked as lobby, a copy of this voice channel will be made.\n" +
                "Then the member will be moved into this new voice channel.\n" +
                "Once everyone left the new channel, it will be deleted automatically.\n" +
                "This will save you a lot of space from unused voice channels.");
        en_gb.setProperty("voicelobby_usage", "**Set a voice channel lobby**\n" +
                "Command: `%s%s set [Voice Channel ID]`\n" +
                "Example: `%s%s set 999999999999999999`\n" +
                "\n" +
                "**Unset a voice channel lobby**\n" +
                "Command: `%s%s unset [Voice Channel ID]`\n" +
                "Example: `%s%s unset 999999999999999999`\n" +
                "\n" +
                "**Show current voice channel lobbies**\n" +
                "Command: `%s%s show`");
        en_gb.setProperty("voicelobby_hint", "**How to get ID's:**\n" +
                "1. Activate Discord Developer Mode: User Settings → Appearance → ADVANCED → Developer Mode\n" +
                "2. Rightclick voice channel → Copy ID");
        en_gb.setProperty("voicelobby_missingid", "Too few arguments. Please provide a voice channel ID.");
        en_gb.setProperty("voicelobby_invalidid", "Invalid voice channel ID.");
        en_gb.setProperty("voicelobby_unset_fail", "Nothing to unset.");
        en_gb.setProperty("voicelobby_noneset", "No lobbies set!");
        en_gb.setProperty("voicelobby_current", "Current Lobbies");
        en_gb.setProperty("voicelobby_footer", "Type `%slobby` to get help.");
        en_gb.setProperty("voicelobby_firstarg", "Invalid argument. Either `set`, `unset`, `show` or `toggletext`.");
        en_gb.setProperty("voicelobby_apostrophe", "\'");
        en_gb.setProperty("voicelobby_apostropge_s", "\'s");

        // Information
        /// Bot Info
        en_gb.setProperty("botinfo_authorname", "All about %s!");
        en_gb.setProperty("botinfo_or", "or");
        en_gb.setProperty("botinfo_please", "Please");
        en_gb.setProperty("botinfo_moderationtools", "Moderation Tools");
        en_gb.setProperty("botinfo_informativecommands", "Informative Commands");
        en_gb.setProperty("botinfo_usefulfeatures", "Useful Features");
        en_gb.setProperty("botinfo_funcommands", "Fun Commands");
        en_gb.setProperty("botinfo_introduction", "Hello! I am **%s**");
        en_gb.setProperty("botinfo_written", "was written in Java");
        en_gb.setProperty("botinfo_owned", "am owned");

        en_gb.setProperty("botinfo_description", "Hello, master! I am %s, your multifuntional bot.\n" +
                "I was written in Java by %s using JDA-Applications's [Commands Extension](%s) (%s) and the [JDA library](%s) (%s).\n" +
                "Type `%shelp` to see my commands!");
        en_gb.setProperty("botinfo_join", "Join my server [`here`](%s)");
        en_gb.setProperty("botinfo_invite", "or [`invite`](%s) me to your server!");
        en_gb.setProperty("botinfo_features", "I provide:");
        en_gb.setProperty("botinfo_stats", "Statistics");
        en_gb.setProperty("botinfo_users", "Users");
        en_gb.setProperty("botinfo_channels", "Channels");
        en_gb.setProperty("botinfo_shard", "This shard");
        en_gb.setProperty("botinfo_restart", "Last restart");

        /// Patreon
        en_gb.setProperty("patreon_supportserver", "Support Servant");
        en_gb.setProperty("patreon_description", "You can support Servant in two different ways:");
        en_gb.setProperty("patreon_subscription", "You can become a patron and support Servant with a monthly payment.\n" +
                "Each patreon rank will have its own embed colour and profile image.\n" +
                "[Click here to get to the Patreon page.](https://www.patreon.com/tancred)");
        en_gb.setProperty("patreon_$1", "Colour: Silver");
        en_gb.setProperty("patreon_$3", "Colour: Gold");
        en_gb.setProperty("patreon_$5", "Colour: Platinum");
        en_gb.setProperty("patreon_$10", "Colour: Diamond\n" +
                "Also, you can change your colour to **whatever** colour you want **whenever** you want!\n\u200B");
        en_gb.setProperty("patreon_donation", "You can send Servant a donation.\n" +
                "[Click here to get to the PayPal.me donation page.](https://www.paypal.me/servantdiscord)\n" +
                "- Add your discord name and numbers into the donation message so I can see where the donation comes from.\n" +
                "- If you have donated at least $5 in total:\n" +
                "... you will get a special role on the Servant Support Server for lifetime.\n" +
                "... your embeds will have the colour bronze showing everyone your big support.\n" +
                "... your profile will get a new image.");
        en_gb.setProperty("patreon_thanks", "Thanks to every supporter <3");

        /// Server Info
        en_gb.setProperty("serverinfo_owner", "Owner: %s");
        en_gb.setProperty("serverinfo_name", "%s (ID: %s)");
        en_gb.setProperty("serverinfo_region", "Region: %s");
        en_gb.setProperty("serverinfo_textcount", "Text Channel Count");
        en_gb.setProperty("serverinfo_voicecount", "Voice Channel Count");
        en_gb.setProperty("serverinfo_membercount", "Member Count");
        en_gb.setProperty("serverinfo_rolecount", "Role Count");
        en_gb.setProperty("serverinfo_categorycount", "Category Count");
        en_gb.setProperty("serverinfo_emotecount", "Emote Count");
        en_gb.setProperty("serverinfo_afktimeout", "AFK Timeout");
        en_gb.setProperty("serverinfo_timeout", "%s seconds");
        en_gb.setProperty("serverinfo_afkchannel", "AFK Channel");
        en_gb.setProperty("serverinfo_noafkchannel", "No AFK channel");
        en_gb.setProperty("serverinfo_systemchannel", "System Channel");
        en_gb.setProperty("serverinfo_nosystemchannel", "No system channel");
        en_gb.setProperty("serverinfo_vanity", "Vanity Url");
        en_gb.setProperty("serverinfo_novanity", "No vanity url");
        en_gb.setProperty("serverinfo_mfa", "MFA Level");
        en_gb.setProperty("serverinfo_explicit", "Explicit Content Level");
        en_gb.setProperty("serverinfo_verification", "Verification Level");
        en_gb.setProperty("serverinfo_botsettings", "**%s Settings:**");
        en_gb.setProperty("serverinfo_prefix", "Prefix");
        en_gb.setProperty("serverinfo_offset", "Offset");
        en_gb.setProperty("serverinfo_language", "Language");
        en_gb.setProperty("serverinfo_bdaychannel", "Birthday Channel");
        en_gb.setProperty("serverinfo_nobdaychannel", "No birthday channel");
        en_gb.setProperty("serverinfo_autorole", "Auto Role");
        en_gb.setProperty("serverinfo_noautorole", "No Auto Role");
        en_gb.setProperty("serverinfo_autorole_value", "%s\nAfter %s minutes");
        en_gb.setProperty("serverinfo_livestream", "Livestream");
        en_gb.setProperty("serverinfo_nolivestream_channel", "No livestream channel");
        en_gb.setProperty("serverinfo_nolivestream_role", "No livestream role");
        en_gb.setProperty("serverinfo_streamermode", "Streamer Mode");
        en_gb.setProperty("serverinfo_publicmode", "Public Mode");
        en_gb.setProperty("serverinfo_voicelobbies", "Voice Lobbies");
        en_gb.setProperty("serverinfo_novoicelobbies", "No Voice Lobbies");
        en_gb.setProperty("serverinfo_mediaonlychannels", "Media Only Channels");
        en_gb.setProperty("serverinfo_nomediaonlychannels", "No Media Only Channels");
        en_gb.setProperty("serverinfo_join", "Join Notification Channel");
        en_gb.setProperty("serverinfo_nojoin", "No join notification channel");
        en_gb.setProperty("serverinfo_leave", "Leave Notification Channel");
        en_gb.setProperty("serverinfo_noleave", "No leave notification channel");
        en_gb.setProperty("serverinfo_none", "None");
        en_gb.setProperty("serverinfo_low", "Low");
        en_gb.setProperty("serverinfo_medium", "Medium");
        en_gb.setProperty("serverinfo_high", "(╯°□°)╯︵ ┻━┻");
        en_gb.setProperty("serverinfo_veryhigh", "┻━┻ ミヽ(ಠ益ಠ)ノ彡┻━┻");
        en_gb.setProperty("serverinfo_none_desc", "Unrestricted");
        en_gb.setProperty("serverinfo_low_desc", "Must have a verified email on their Discord account.");
        en_gb.setProperty("serverinfo_medium_desc", "Must have a verified email on their Discord account.\n" +
                "Must also be registered on Discord for longer than 5 Minutes.");
        en_gb.setProperty("serverinfo_high_desc", "Must have a verified email on their Discord account.\n" +
                "Must also be registered on Discord for longer than 5 minutes.\n" +
                "Must also be a member of this server for longer than 10 minutes.");
        en_gb.setProperty("serverinfo_veryhigh_desc", "Must have a verified email on their Discord account.\n" +
                "Must also be registered on Discord for longer than 5 minutes.\n" +
                "Must also be a member of this server for longer than 10 minutes.\n" +
                "Must have a verified phone on their Discordd account.");

        /// Ping

        // Useful
        /// Alarm
        en_gb.setProperty("alarm_description", "Set up an alarm to a specific time.\n" +
                "The date and time will take your timezone into account.\n" +
                "If you didn't set one up, it will use UTC.\n" +
                "To set one up, use the `%suser` command.");
        en_gb.setProperty("alarm_usage", "Command: `%s%s [time]`\n" +
                "Example: `%s%s 3d`");
        en_gb.setProperty("alarm_hint", "- Time formats: d = days, h = hours, m = minutes\n" +
                "   \u200B⤷ Also, you can use as many time-arguments as you want\n" +
                "   \u200B⤷ Example: `%s%s 2d 12h 36m 1d`\n" +
                "   \u200B⤷ Time result: `2d` + `12h` + `36m` + `1d` = 3 days, 12 hours, 36 minutes\n" +
                "- Minutes are rounded. If you put in `1m`, you will be notifies on the **next** minute.");
        en_gb.setProperty("alarm_invalidtime", "Invalid Time.");
        en_gb.setProperty("alarm_wrongargument", "Wrong argument");
        en_gb.setProperty("alarm_messedupargs", "You messed up your arguments.");
        en_gb.setProperty("alarm_alreadyset", "You already have an alarm at that time!");
        en_gb.setProperty("alarm_remind", "Hi master, here is your requested alarm.");

        /// Giveaway
        en_gb.setProperty("giveaway_description", "- Start a giveaway that draws a given amount of people as winners after a given time.\n" +
                "- List all running giveaways of the current server.");
        en_gb.setProperty("giveaway_usage", "- Start giveaway: `%sgiveaway \"[prize name]\" [amount of winners] [time]`\n" +
                "   \u200B⤷ Example: `%sgiveaway \"100 Cookies\" 1 12h`\n" +
                "- List giveaways: `%sgiveaway list`");
        en_gb.setProperty("giveaway_hint", "- Time formats: d = days, h = hours, m = minutes\n" +
                "   \u200B⤷ Also, you can use as many time-arguments as you want\n" +
                "   \u200B⤷ Example: `%sgiveaway \"100 cookies\" 1 2d 12h 36m 1d`\n" +
                "   \u200B⤷ Time result: `2d` + `12h` + `36m` + `1d` = 3 days, 12 hours, 36 minutes");
        en_gb.setProperty("giveaway_current", "Current giveaways on this guild");
        en_gb.setProperty("giveaway_days", "%s days");
        en_gb.setProperty("giveaway_hours", "%s hours");
        en_gb.setProperty("giveaway_minutes", "%s minutes");
        en_gb.setProperty("giveaway_from", "Giveaway from %s!");
        en_gb.setProperty("giveaway_endsat", "Ends at");
        en_gb.setProperty("giveaway_endedat", "Ended at");
        en_gb.setProperty("giveaway_messageid", "**Message ID:**");
        en_gb.setProperty("giveaway_prize", "**Prize:**");
        en_gb.setProperty("giveaway_noreactions", "Sorry, I can't find any reactions!");
        en_gb.setProperty("giveaway_invalidtime", "Invalid Time.");
        en_gb.setProperty("giveaway_wrongargument", "Wrong argument");
        en_gb.setProperty("giveaway_nocurrent", "There are no giveaways running!");
        en_gb.setProperty("giveaway_invalidwinneramount", "Invalid amount of winners. Only numbers!");
        en_gb.setProperty("giveaway_messedupargs", "You messed up your arguments.");
        en_gb.setProperty("giveaway_zerowinners", "That makes no sense. There has to be at least one winner.");
        en_gb.setProperty("giveaway_emptyprize", "You cannot leave the prize empty.");
        en_gb.setProperty("giveaway_description_running", "Prize: **%s**\n" +
                "Amount of winners: **%s**\n" +
                "Time remaining: **%s**\n" +
                "React with %s to enter the giveaway!");
        en_gb.setProperty("giveaway_description_end", "Prize: **%s**\n" +
                "Amount of winners: **%s**\n\n" +
                "The winners are:\n" +
                "%s\n" +
                "Congratulations!");
        en_gb.setProperty("giveaway_description_nowinner", "Prize: **%s**\n" +
                "Amount of winners: **%s**\n\n" +
                "Nobody participated. Therefore, nobody won.");

        /// Reminder
        en_gb.setProperty("reminder_description", "Set up a reminder to a specific date and time.\n" +
                "The date and time will take your timezone into account.\n" +
                "If you didn't set one up, it will use UTC.\n" +
                "To set one up, use the `%suser` command.\n" +
                "You can add a topic about what you want to be reminded.");
        en_gb.setProperty("reminder_usage", "Command: `%s%s yyyy-MM-dd HH:mm [topic]`\n" +
                "Example: `%s%s 2020-01-01 00:00 Happy New Year`");
        en_gb.setProperty("reminder_hint", "You cannot set reminders in the past.");
        en_gb.setProperty("reminder_missingargs", "Missing arguments! Please add a date and a time.");
        en_gb.setProperty("reminder_past", "You cannot set up reminders in the past!");
        en_gb.setProperty("reminder_invalidtopic", "Unsuitable topic");
        en_gb.setProperty("reminder_success", "Added. Your message was removed for privacy.");
        en_gb.setProperty("reminder_fail", "You already have a reminder at this time.");
        en_gb.setProperty("reminder_invalidinput", "Invalid input.");
        en_gb.setProperty("reminder_remind_notopic", "Hi master, I should remind you at this time.");
        en_gb.setProperty("reminder_remind_topic", "Hi master, I should remind you at this time about:\n**%s**");

        /// Timezone
        en_gb.setProperty("timezone_description", "Convert a date and time from one timezone to another");
        en_gb.setProperty("timezone_usage", "Command: `%s%s yyyy-MM-dd HH:mm [current timezone] [target timezone]`\n" +
                "Example: `%s%s 2019-01-01 22:00 PST CET`");
        en_gb.setProperty("timezone_hint", "This command uses the 24 hour system.");
        en_gb.setProperty("timezone_missingargs", "Missing arguments.");
        en_gb.setProperty("timezone_conversion", "Timezone Conversion");
        en_gb.setProperty("timezone_input", "Input");
        en_gb.setProperty("timezone_output", "Output");
        en_gb.setProperty("timezone_invalidzone_start", "Invalid starting timezone.");
        en_gb.setProperty("timezone_invalidzone_target", "Invalid target timezone.");
        en_gb.setProperty("timezone_invalid", "Invalid input. Check your formatting.");

        // Votes
        en_gb.setProperty("votes_emote_fail", "It's not your fault, master! I cannot find the correct emotes. I'll let my mediator know...");
        en_gb.setProperty("votes_active", "This vote is active.");
        en_gb.setProperty("votes_inactive", "This vote has ended.");

        /// Quickvote
        en_gb.setProperty("quickvote_started", "%s started a quickvote!");
        en_gb.setProperty("quickvote_ended", "%s has ended the quickvote!");
        en_gb.setProperty("quickvote_emote_dm", "Greetings mediator! I was not able to pull of a quickvote as I am missing the following emotes:");
        en_gb.setProperty("quickvote_missing_db", "Greetings mediator! I couldn't remove a succesful quickvote ending from the database.");

        /// Vote
        en_gb.setProperty("vote_description", "Create a vote with up to 10 custom answers.");
        en_gb.setProperty("vote_usage", "Command: `%s%s [question]/[answer1]/(...)/[answer10]`\n" +
                "Example: `%s%s When do you have time?/Mon/Tue/Wed/Thu/Fri/Sat/Sun`");
        en_gb.setProperty("vote_hint", "After executing this command, you will be asked if you want to allow multiple answers.");
        en_gb.setProperty("vote_amount", "Invalid amount of arguments. There has to be at least one answer to your question and a maximum of 10 answers.");
        en_gb.setProperty("vote_timeout", "Timeout! You didn't react on my question.");
        en_gb.setProperty("vote_started", "%s started a vote!");
        en_gb.setProperty("vote_ended", "%s has ended the vote!");
        en_gb.setProperty("vote_missing_db", "Greetings mediator! I couldn't remove a succesful vote ending from the database.");

        // Fun
        /// Avatar
        en_gb.setProperty("avatar_description", "Steal someone's avatar.");
        en_gb.setProperty("avatar_usage", "Command: `%s%s @user`\n");
        en_gb.setProperty("avatar_stolen", "%s just stole %s's avatar!");

        /// Baguette
        en_gb.setProperty("baguette_jackpot", "JACKPOT! Now you're cool.");

        /// Bird

        /// Cat

        /// Coin Flip
        en_gb.setProperty("coinflip_head", "Head!");
        en_gb.setProperty("coinflip_tail", "Tail!");

        /// Embed (for both create and edit)
        en_gb.setProperty("embed_timeout", "This configuration timed out.");
        en_gb.setProperty("embed_authorline_q", "Alright! Do you want to use an author line?");
        en_gb.setProperty("embed_authorname_i", "Please provide the **author name**:");
        en_gb.setProperty("embed_authorurl_q", "Do you want to use an **author url** (not the icon)?");
        en_gb.setProperty("embed_authorurl_i", "Please provide the **author url** (not the icon url!):");
        en_gb.setProperty("embed_authorurl_i_fail", "Your input is invalid. Please provide a valid url:");
        en_gb.setProperty("embed_authoricon_q", "Do you want to use an **author icon**?");
        en_gb.setProperty("embed_authoricon_i", "Please provide the **author icon url** (direct link!):");
        en_gb.setProperty("embed_authoricon_i_fail", "Your input is invalid. Please provide a valid direct url:");
        en_gb.setProperty("embed_thumbnail_q", "Do you want to use a thumbnail?");
        en_gb.setProperty("embed_thumbnail_i", "Please provide the **thumbnail url** (direct link!):");
        en_gb.setProperty("embed_thumbnail_i_fail", "Your input is invalid. Please provide a valid direct url:");
        en_gb.setProperty("embed_title_q", "Do you want to use a title?");
        en_gb.setProperty("embed_title_i", "Please provide the **title**:");
        en_gb.setProperty("embed_url_q", "Do you want to use a title URL?");
        en_gb.setProperty("embed_url_i", "Please provide the **title url**:");
        en_gb.setProperty("embed_url_i_fail", "Your input is invalid. Please provide a valid url:");
        en_gb.setProperty("embed_description_q", "Do you want to use a description?");
        en_gb.setProperty("embed_description_i", "Please provide the **description**:");
        en_gb.setProperty("embed_field_q", "Do you want to add a field?");
        en_gb.setProperty("embed_field_name_i", "Please provide the **field name**:");
        en_gb.setProperty("embed_field_value_i", "Please provide the **field value**:");
        en_gb.setProperty("embed_field_inline_i", "Should this field be inline?");
        en_gb.setProperty("embed_image_q", "Do you want to use an image?");
        en_gb.setProperty("embed_image_i", "Please provide the **image url** (direct link!):");
        en_gb.setProperty("embed_image_i_fail", "Your input is invalid. Please provide a valid direct url:");
        en_gb.setProperty("embed_footer_q", "Do you want to use a footer?");
        en_gb.setProperty("embed_footer_text_i", "Please provide the **footer text**:");
        en_gb.setProperty("embed_footer_icon_q", "Do you want to use a footer icon?");
        en_gb.setProperty("embed_footer_icon_i", "Please provide the **footer icon url** (direct link!):");
        en_gb.setProperty("embed_footer_icon_i_fail", "Your input is invalid. Please provide a valid direct url:");
        en_gb.setProperty("embed_empty", "Either the embed is empty or it has over 6000 characters.\nBoth is not allowed!");
        en_gb.setProperty("embed_timestamp_q", "Do you want to use a timestamp?");
        en_gb.setProperty("embed_timestamp_i", "Please provide a timestamp.\nYou either can say `now` or provide a date and time like this: `yyyy-MM-dd HH:mm` (uses guild timezone):");
        en_gb.setProperty("embed_timestamp_i_fail", "Your input is invalid.\nYou either can say `now` or provide a date and time like this: `yyyy-MM-dd HH:mm` (uses guild timezone).\nTry again:");

        //// Create Embed + Edit Embed
        en_gb.setProperty("createembed_introduction", "With this command, you can create your own embed.\n" +
                "- You cannot create an empty embed.\n" +
                "- The embed but not be longer than 6000 characters in total.\n" +
                "- Everytime you have to click a reacton or write an answer, you have a time limit of 15 minutes.\n" +
                "Are you prepared?");
        en_gb.setProperty("createembed_author_name", "Author name (can point to URL)");
        en_gb.setProperty("createembed_title", "Title (can point to URL)");
        en_gb.setProperty("createembed_descriptiopn", "Description\n" +
                "The title will be white if it's not a hyperlink.\n" +
                "Any image URL's have to be direct links.\n" +
                "The timestamp is not part of the footer text, but a standalone date and time.");
        en_gb.setProperty("createembed_field_name_inline", "Inline field name"); // dis
        en_gb.setProperty("createembed_field_value1", "Field value");
        en_gb.setProperty("createembed_field_value2", "Up to 3 in a line.");
        en_gb.setProperty("createembed_field_value3", "You can have up to 25 fields.");
        en_gb.setProperty("createembed_field_name_noninline", "Non-inline field name");
        en_gb.setProperty("createembed_field_value_noninline", "Non-inline fields take the while width of the embed.");
        en_gb.setProperty("createembed_footer", "Footer text");
        en_gb.setProperty("createembed_done", "We're done! Please mention a text channel to post this embed in (e.g. #channel):");

        //// Edit Embed
        en_gb.setProperty("editembed_description", "Edit an embed that was made by %s.");
        en_gb.setProperty("editembed_usage", "Command: `%s%s #channel [message ID of the embed]`\n" +
                "Example: `%s%s #info 999999999999999999`");
        en_gb.setProperty("editembed_hint", "**How to get ID's:**\n" +
                "1. Activate Discord Developer Mode: User Settings → Appearance → ADVANCED → Developer Mode\n" +
                "2. Hover over message → Menu buttons at the right → Copy ID");
        en_gb.setProperty("editembed_missing_channel", "You have to mention a channel. Check `%seditembed` for help.`");
        en_gb.setProperty("editembed_missing_message_id", "You have to provide a message ID. Check `%seditembed for help.`");
        en_gb.setProperty("editembed_invalid_message_id", "The provided message ID is invalid. Check `%seditembed for help.`");
        en_gb.setProperty("editembed_notbyme", "This is not a message made by me.");
        en_gb.setProperty("editembed_noembed", "I cannot find an embed for this message.");
        en_gb.setProperty("editembed_introduction", "With this command, you can edit an embed from %s.\n" +
                "- You cannot create an empty embed.\n" +
                "- The embed but not be longer than 6000 characters in total.\n" +
                "- Everytime you have to click a reacton or write an answer, you have a time limit of 15 minutes.\n" +
                "Are you prepared?");
        en_gb.setProperty("editembed_confirm", "We're done! Please confirm you want to overwrite the old embed.");
        en_gb.setProperty("editembed_notfound", "I cannot find this message, master!");

        /// Dog

        /// Flip
        en_gb.setProperty("flip_description", "Flip a user.");
        en_gb.setProperty("flip_usage", "Command: `%s%s @user`");
        en_gb.setProperty("flip_hint", "Check out `%sunflip @user`");

        /// Level
        en_gb.setProperty("level_leaderboard_empty", "Leaderboard is empty");
        en_gb.setProperty("level_leaderboard_authorname", "%s Leaderboard");
        en_gb.setProperty("level_leaderboard_footer", "Also try \"%slevel\" and \"%slevel @user\"");
        en_gb.setProperty("level_level", "Level");
        en_gb.setProperty("level_rank", "Rank");
        en_gb.setProperty("level_up", "%s just reached level %s! \uD83C\uDF89");
        en_gb.setProperty("level_footer", "Also try %s and \"%slevel leaderboard\"!");

        /// Love
        en_gb.setProperty("love_description","Ship two people or just one with themselves.");
        en_gb.setProperty("love_usage", "Command: `%s%s @user1 @user2`\n" +
                "Command: `%s%s @user`");
        en_gb.setProperty("love_hint", "You only need to mention a person once, if you want to ship them with themselves.");
        en_gb.setProperty("love_self_100", "Damn! Straight to the fap!");
        en_gb.setProperty("love_self_90", "Pretty self confident, don't you think?");
        en_gb.setProperty("love_self_80", "So narcissistic...");
        en_gb.setProperty("love_self_70", "You love yourself more than others love you.");
        en_gb.setProperty("love_self_69", "Nice.");
        en_gb.setProperty("love_self_60", "Seems like you are accepting yourself.");
        en_gb.setProperty("love_self_50", "You seem to be undecided if you like yourself or not.");
        en_gb.setProperty("love_self_42", "You found the answer.");
        en_gb.setProperty("love_self_40", "Now, you can look into the mirror with pride.");
        en_gb.setProperty("love_self_30", "A bit unsecure, but I'm sure you can handle it.");
        en_gb.setProperty("love_self_20", "You are doing great. Build some self confidence!");
        en_gb.setProperty("love_self_10", "Believe in yourself!");
        en_gb.setProperty("love_self_0", "Thats tough. We still love you <3");
        en_gb.setProperty("love_noself_100", "Damn! Thats a match!");
        en_gb.setProperty("love_noself_90", "Get up and invite them for a dinner.");
        en_gb.setProperty("love_noself_80", "You sure, you don't wanna date?");
        en_gb.setProperty("love_noself_70", "I call a sis-/bromance.");
        en_gb.setProperty("love_noself_69", "Nice.");
        en_gb.setProperty("love_noself_60", "There is a chance.");
        en_gb.setProperty("love_noself_50", "I bet you can be friends. :)");
        en_gb.setProperty("love_noself_42", "You found the answer.");
        en_gb.setProperty("love_noself_40", "At least you are trying.");
        en_gb.setProperty("love_noself_30", "I think this won't work out.");
        en_gb.setProperty("love_noself_20", "At least a bit, amirite.");
        en_gb.setProperty("love_noself_10", "Dats pretty low, tho.");
        en_gb.setProperty("love_noself_0", "Well, that won't work out.");
        en_gb.setProperty("love_fallback", "Urgh!");

        /// Music

        /// Profile
        en_gb.setProperty("profile_name", "Name");
        en_gb.setProperty("profile_ap", "AP");
        en_gb.setProperty("profile_amount", "Amount");
        en_gb.setProperty("profile_noachievements", "No achievements");
        en_gb.setProperty("profile_nocommands", "No commands were used yet");
        en_gb.setProperty("profile_level", "Level");
        en_gb.setProperty("profile_rank",  "Rank #%s");
        en_gb.setProperty("profile_mostused", "Most used commands");
        en_gb.setProperty("profile_achievements", "Achievements");
        en_gb.setProperty("profile_footer1", "Also try \"%s%s @user\"");
        en_gb.setProperty("profile_footer2", "Also try \"%s%s\"");
        en_gb.setProperty("profile_baguettecounter", "Baguette Statistics");
        en_gb.setProperty("profile_nobaguette", "No baguette yet");
        en_gb.setProperty("profile_baguette", "Biggest baguette: %s (%s times)");

        /// Unflip
        en_gb.setProperty("unflip_description", "Unlip a user.");
        en_gb.setProperty("unflip_usage", "Command: `%s%s @user`");
        en_gb.setProperty("unflip_hint", "Check out `%sflip @user`");

        // Interaction
        en_gb.setProperty("interaction_description", "Interaction commands are like reactions, but way better.\n" +
                "Share your feelings or cookies with other people.");
        en_gb.setProperty("interaction_usage", "**%s%s%s someone**\n" +
                "Command: `%s%s [@user]`\n" +
                "Example: `%s%s @Servant`");
        en_gb.setProperty("interaction_usage_dab", "**%s%s on someone**\n" +
                "Command: `%s%s [@user]`\n" +
                "Example: `%s%s @Servant`");


        en_gb.store(os,
                "Project: Servant\n" +
                        "Author: Tancred#0001\n" +
                        "GitHub: https://github.com/Tancred423/Servant");
        os.close();
    }

    static void createDefaultDE_DE() throws IOException {
        OrderedProperties de_de = new OrderedProperties();
        OutputStream os = new FileOutputStream(System.getProperty("user.dir") + "/resources/lang/de_de.ini");

        de_de.store(os,
                "Project: Servant\n" +
                        "Author: Tancred#0001\n" +
                        "GitHub: https://github.com/Tancred423/Servant");
        os.close();
    }

    public static String get(String lang, String key) throws IOException {
        OrderedProperties languageFile = new OrderedProperties();
        String configPath = System.getProperty("user.dir") + "/resources/lang/" + lang + ".ini";
        InputStream is = new FileInputStream(configPath);
        languageFile.load(is);
        return languageFile.getProperty(key);
    }
}
