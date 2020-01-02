// Author: Tancred423 (https://github.com/Tancred423)
package files.language;

import nu.studer.java.util.OrderedProperties;

import java.io.*;

public class LanguageFile {
    static void createDefaultEN_GB() throws IOException {
        var en_gb = new OrderedProperties();
        var os = new FileOutputStream(System.getProperty("user.dir") + "/resources/lang/en_gb.ini");

        // General
        en_gb.setProperty("permission", ":x: You must have the %s permission in this server to use that!");
        en_gb.setProperty("blocking_dm", "Help cannot be sent because you are blocking Direct Messages.");
        en_gb.setProperty("invalid_mention", "Invalid mention.");
        en_gb.setProperty("current_prefix", "Current prefix: %s");
        en_gb.setProperty("unknown_message", "Unknown message.");

        // Features
        /// Achievement
        en_gb.setProperty("achievement_excalibur", "Excalibur");
        en_gb.setProperty("achievement_unlimited_blade_works", "Unlimited Blade Works");
        en_gb.setProperty("achievement_gae_bolg", "The Legend of Lancer");
        en_gb.setProperty("achievement_level10", "Level 10");
        en_gb.setProperty("achievement_level20", "Level 20");
        en_gb.setProperty("achievement_level30", "Level 30");
        en_gb.setProperty("achievement_level40", "Level 40");
        en_gb.setProperty("achievement_level50", "Level 50");
        en_gb.setProperty("achievement_level60", "Level 60");
        en_gb.setProperty("achievement_nicelevel", "Nice Level");
        en_gb.setProperty("achievement_level70", "Level 70");
        en_gb.setProperty("achievement_level80", "Level 80");
        en_gb.setProperty("achievement_level90", "Level 90");
        en_gb.setProperty("achievement_level100", "Level 100");
        en_gb.setProperty("achievement_love42", "Found the answer");
        en_gb.setProperty("achievement_love69", "Nice Love");
        en_gb.setProperty("achievement_kind", "Being kind to %s");
        en_gb.setProperty("achievement_navi", "Hey Listen!");
        en_gb.setProperty("achievement_deusvult", "DEUS VULT!");
        en_gb.setProperty("achievement_fiteme", "Fite me!!!");
        en_gb.setProperty("achievement_xmas", "Xmas Time");

        /// Invite
        en_gb.setProperty("invite_author", "%s, at your service!");
        en_gb.setProperty("invite_description", "Thank you for choosing me to assist you and your server.\n" +
                "I have a lot of features. Most of them are enabled by default but some of them are not.\n" +
                "Type `%stoggle all show` to check the status of all available features.\n" +
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
        en_gb.setProperty("invite_footer", "You are receiving this message, because someone invited me to your server (%s).");

        /// Kick
        en_gb.setProperty("kick_author", "Farewell!");
        en_gb.setProperty("kick_description", "It's very sad to hear, that you don't need me anymore.\n" +
                "If there is anything to improve, I am always open for feedback.\n" +
                "\n" +
                "To submit feedback, you can join my support server or contact my creator directly.\n" +
                "Support Server: [Click to join](https://%s)\n" +
                "Creator Name: %s#%s\n" +
                "Email: `servant@tanc.red`\n");
        en_gb.setProperty("kick_footer", "You are receiving this message, because someone kicked me from your server (%s) or your server was deleted.");

        /// Patreon Handler
        en_gb.setProperty("patreon_warning", "You have to be a %s to use this feature!");

        /// Presence
        en_gb.setProperty("presence_0", "v%s | %shelp");
        en_gb.setProperty("presence_1", "%s masters | %shelp");
        en_gb.setProperty("presence_2", "%s servers | %shelp");
        en_gb.setProperty("presence_3", "%spatreon | %shelp");
        en_gb.setProperty("presence_4", "https://servant.tanc.red");

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

        /// Blacklist
        en_gb.setProperty("blacklist_missingid", "Add an ID");
        en_gb.setProperty("blacklist_empty", "No blacklisted ID's.");

        /// Server List
        en_gb.setProperty("guildlist_members", "Members");
        en_gb.setProperty("guildlist_connected", " Servers that **%s** is connected to");
        en_gb.setProperty("blacklist_empty", "No blacklisted ID's.");

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
                "- Moderators can create a list of all birthdays of this server, which will be updated regulary.\n" +
                "- Moderators can add or remove %s's birthday.");
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
                "Command: `%s%s list`\n" +
                "**Add/Remove %s's birthday**\n" +
                "Command: `%s%s %s`");
        en_gb.setProperty("birthday_hint", "The commands have different required permissions.\n" +
                "See description:\n" +
                "- \"Members\": No permission required\n" +
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
        en_gb.setProperty("clear_invalid", "Your input is invalid. Try a smaller number.");

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
        en_gb.setProperty("join_hint", "Shows a message like \"Name#1234 just joined Servername!\"");
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
        en_gb.setProperty("leave_hint", "Shows a message like \"Name#1234 just left Servername!\"");
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
                "**Show current levelroles**\n" +
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
        en_gb.setProperty("level_missingpermission_embed", "I don't have the permission to create embeds (Embed Links), that's why you see the budget level up announcement.");
        en_gb.setProperty("level_hierarchy", "Couldn't add role \"%s\", because they are higher than me in hierarchy.");

        /// Livestream
        en_gb.setProperty("livestream_description", "You can set up streamers, one stream notification channel and one streamer role.\n" +
                "Once a streamer goes online, a notification **with** @here will be posted and the streamer will receive the set role.\n" +
                "If you toggle the streamer mode `off`, a notification **without** @here will be posted and the member will receive the set role.");
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
        en_gb.setProperty("livestream_hint", "Streamer Mode: Only set up streamers will get notifications and roles.\n" +
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
        en_gb.setProperty("mediaonlychannel_alreadyset", "This channel was already set.");

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
                "Possibility 1: Post in chat: `\\@role`, `\\#channel`, etc. - THIS ALSO PINGS. Maybe do this is a non public channel.\n" +
                "Possibility 2:\n" +
                "- Discord Settings → Appearance → ADVANCED → Enable Developer Mode\n" +
                "- Rightclick on channel, role, etc. → Copy ID");
        en_gb.setProperty("reactionrole_toofewargs", "Too few arguments. You have to declare the channel ID, message ID, an emoji or emote and a role mention or role ID behind `set`.");
        en_gb.setProperty("reactionrole_alreadyset", "This emoji or emote was already set. Unset first if you want to update the emoji or emote.");
        en_gb.setProperty("reactionrole_notset", "This emoji or emote was not set.");
        en_gb.setProperty("reactionrole_firstarg", "Either `set` or `unset` a reaction.");
        en_gb.setProperty("reactionrole_insufficient", "Insufficient permissions or problem with hierarchy.");
        en_gb.setProperty("reactionrole_invalidmessageid", "Invalid message ID.");

        /// Role
        en_gb.setProperty("role_description", "Assing or remove roles from members.");
        en_gb.setProperty("role_usage", "Command: `%s%s @user [roleName]`\n" +
                "Example: `%s%s @name member`");
        en_gb.setProperty("role_hint", "I will remove the role if the member already has it and provide it if the member doesn't have it already.");
        en_gb.setProperty("role_missing", "Missing role name.");
        en_gb.setProperty("role_notfound", "Sorry, master! I couldn't find a role with that name.");
        en_gb.setProperty("role_missingrolename", "Please also provide the role name.");
        en_gb.setProperty("role_cantinteract", "I cannot interact with that user! (Check permissions)");

        /// Server
        en_gb.setProperty("server_description", "With this command you can personalize the bot to your server's desire.");
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
                "- en_gb - English (British)\n" +
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
        en_gb.setProperty("server_settings", "Server Settings");
        en_gb.setProperty("server_firstarg", "The first argument has to be either `set`, `unset` or `show`.");

        /// Server Setup
        en_gb.setProperty("setupwizard_introduction", "Welcome to the server setup wizard!\n" +
                "After doing this, you have all (3) base settings set up and we are ready to go!\n" +
                "On each question you have 15 minutes to answer, otherwise this setup will time out.\n" +
                "Do you want to start?");
        en_gb.setProperty("setupwizard_timeout", "The setup wizard timed out.");
        en_gb.setProperty("setupwizard_language", "Please state in what language you want me to speak in.\n" +
                "Currently available languages:\n" +
                "   \u200B⤷ `en_gb` - English (British)\n" +
                "   \u200B⤷ `de_de` - German (Deutsch)\n" +
                "**Provide the language code:**");
        en_gb.setProperty("setupwizard_language_repeated", "This language is not available.\n" +
                "Currently available languages:\n" +
                "   \u200B⤷ `en_gb` - English (British)\n" +
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
        en_gb.setProperty("user_usage", "**Setting an offset**\n" +
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
                "Command: `%s%s streamhide [opt. server ID]`\n" +
                "\n" +
                "**Setting an embed colour**\n" +
                "Command: `%s%s set colour [color code]`\n" +
                "Example: `%s%s set colour #FFFFFF`\n" +
                "\n" +
                "**Unsetting the embed colour**\n" +
                "Command: `%s%s unset colour`\n" +
                "\n" +
                "**Show your current settings**\n" +
                "Command: `%s%s show`");
        en_gb.setProperty("user_hint", "You can set a custom embed colour. ($10 Patron)\n" +
                "An embed colour is the colour you can see right know on the left of this text field thingy.\n" +
                "Settings like offset, prefix and language only work in DM's. For server settings please refer to `%sserver`." +
                "Available languages:\n" +
                "- en_gb - English (British)\n" +
                "- de_de - German (Deutsch)\n" +
                "You can hide yourself from being highlighted with streaming role while streaming (server specific).");
        en_gb.setProperty("user_streamhide_description", "You have to use this in a server to specify what server you want to be muted on.\n" +
                "If you want to do this secretly, you can also provide a server ID:\n" +
                "Command: `%s%s streamhide [server ID]`\n" +
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
        en_gb.setProperty("voicelobby_already_set", "This channel was already set as an voice lobby.");

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
        en_gb.setProperty("patreon_description", "You can support Servant in three different ways:");
        en_gb.setProperty("patreon_patreontitle", "Patreon");
        en_gb.setProperty("patreon_subscription", "You can become a patron and support Servant with a monthly payment.\n" +
                "Each patreon rank will have its own embed colour and profile image.\n" +
                "[Click here to get to the Patreon page.](https://www.patreon.com/tancred)");
        en_gb.setProperty("patreon_$1", "✓ Caster Profile Image\n✓ Orange Embeds");
        en_gb.setProperty("patreon_$3", "✓ Lancer Profile Image\n✓ Yellow Embeds");
        en_gb.setProperty("patreon_$5", "✓ Archer Profile Image\n✓ Green Embeds");
        en_gb.setProperty("patreon_$10", "✓ Saber Profile Image\n✓ Blue Embeds\n✓ Custom Colour Embeds");
        en_gb.setProperty("patreon_donationtitle", "Donation");
        en_gb.setProperty("patreon_donation", "You can send Servant a donation.\n" +
                "Add your Discord Name#1234 into the donation message so I can see who sent the donation.\n" +
                "[Click here to get to the PayPal.me donation page.](https://www.paypal.me/servantdiscord)\n");
        en_gb.setProperty("patreon_donation_$5", "✓ Assassin Profile Image\n✓ Red Embeds");
        en_gb.setProperty("patreon_serverboosttitle", "Server Boost");
        en_gb.setProperty("patreon_serverboost", "You can boost Servant's Kingdom with Discord Nitro to unlock new Discord server perks.\n" +
                "[Click here to join Servant's Kingdom](https://discord.gg/4GpaH5V)\n" +
                "✓ Berserker Profile Image\n" +
                "✓ Pink Embeds");
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

        // Useful
        /// Alarm
        en_gb.setProperty("alarm_description", "Set up an alarm to a specific time.\n" +
                "The date and time will take your timezone into account.\n" +
                "If you didn't set one up, it will use UTC.\n" +
                "To set one up, use the `%suser` command.");
        en_gb.setProperty("alarm_usage", "Command: `%s%s \"Optional Title\" [time]`\n" +
                "Example 1: `%s%s \"Pat Servant\" 3d 2h 1m`\n" +
                "Example 2: `%s%s 5m");
        en_gb.setProperty("alarm_hint", "- Time formats: d = days, h = hours, m = minutes\n" +
                "   \u200B⤷ Also, you can use as many time-arguments as you want\n" +
                "   \u200B⤷ Example: `%s%s \"Pat Servant\" 2d 12h 36m 1d`\n" +
                "   \u200B⤷ Time result: `2d` + `12h` + `36m` + `1d` = 3 days, 12 hours, 36 minutes\n" +
                "- Minutes are rounded. If you put in `1m`, you will be notifies on the **next** minute.\n" +
                "- Seconds are not supported.");
        en_gb.setProperty("alarm_invalidtime", "Invalid Time.");
        en_gb.setProperty("alarm_wrongargument", "Wrong argument");
        en_gb.setProperty("alarm_messedupargs", "You messed up your arguments.");
        en_gb.setProperty("alarm_alreadyset", "You already have an alarm at that time!");
        en_gb.setProperty("alarm_remind", "Hi master, here is your requested alarm.");
        en_gb.setProperty("alarm_invalidtitle", "Invalid Title: No ending quotation mark.");
        en_gb.setProperty("alarm_titlelength", "Invalid Title Length: The title must not be longer than 256 characters.");
        en_gb.setProperty("alarm_missingtime", "Missing Time: You have to add time arguments.");
        en_gb.setProperty("alarm_toobig", "Invalid Time: The given value is too big.");

        /// Giveaway
        en_gb.setProperty("giveaway_description", "- Start a giveaway that draws a given amount of people as winners after a given time.\n" +
                "- List all running giveaways of the current server.");
        en_gb.setProperty("giveaway_usage", "- Start giveaway: `%sgiveaway \"[prize name]\" [amount of winners] [time]`\n" +
                "   \u200B⤷ Example: `%sgiveaway \"100 Cookies\" 1 12h`\n" +
                "- List giveaways: `%sgiveaway list`");
        en_gb.setProperty("giveaway_hint", "- Time formats: d = days, h = hours, m = minutes\n" +
                "   \u200B⤷ Also, you can use as many time-arguments as you want\n" +
                "   \u200B⤷ Example: `%sgiveaway \"100 cookies\" 1 2d 12h 36m 1d`\n" +
                "   \u200B⤷ Time result: `2d` + `12h` + `36m` + `1d` = 3 days, 12 hours, 36 minutes\n" +
                "- Seconds are not supported.");
        en_gb.setProperty("giveaway_current", "Current giveaways on this server");
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
        en_gb.setProperty("reminder_success_dm", "Added.");
        en_gb.setProperty("reminder_fail", "You already have a reminder at this time.");
        en_gb.setProperty("reminder_invalidinput", "Invalid input.");
        en_gb.setProperty("reminder_remind_notopic", "Hi master, I should remind you at this time.");
        en_gb.setProperty("reminder_remind_topic", "Hi master, I should remind you at this time about:\n**%s**");

        /// Signup
        en_gb.setProperty("signup_description", "Let people sign up for an event (e.g. a raid).\n" +
                "The signup will be completed if either of the following happens:\n" +
                "1. The given amount of people have signed up (reacted).\n" +
                "2. The given expiring date (or the default one if not given) was reached (see last hint).\n" +
                "3. The signup creator clicked the :x: reaction.");
        en_gb.setProperty("signup_usage", "Command: `%s%s \"Title\" [amount of people] [event date and time]`\n" +
                "Example 1: `%s%s \"Holy Grail War\" 10 2019-12-31 23:30`\n" +
                "Example 2: `%s%s 10 2019-12-31 23:30`\n" +
                "Example 3: `%s%s \"Holy Grail War\" 10`\n" +
                "Example 4: `%s%s 10`");
        en_gb.setProperty("signup_hint", "- The amount of people that are allowed to sign up has to be within 1 and 100.\n" +
                "- The event date and time has to be within the next 4 weeks.\n" +
                "- If you don't set the event date and time, the signup will expire in 4 weeks.\n" +
                "- The event date and time will use the server's offset. You can check it via `!serverinfo` -> Servant Settings -> Offset\n" +
                "- If you set an event date, the signup will expire 30 minutes earlier, so you have time to organize the group.");
        en_gb.setProperty("signup_invalidtitle", "Invalid Title: No ending quotation mark.");
        en_gb.setProperty("signup_titlelength", "Invalid Title Length: The title must not be longer than 256 characters.");
        en_gb.setProperty("signup_invalidamount", "Invalid Amount: No amount of participants was found.");
        en_gb.setProperty("signup_invalidamountrange", "Invalid Amount Size: The amount has to be within 1 and 100 participants.");
        en_gb.setProperty("signup_invalidamountparse", "Invalid Amount Parse: %s");
        en_gb.setProperty("signup_missingamount", "Missing Amount: You have to add an amount of allowed participants.");
        en_gb.setProperty("signup_invaliddate", "Invalid Date and Time");
        en_gb.setProperty("signup_invaliddatedistance", "Invalid Date and Time: The given date and time must not be farther away than 4 weeks and it must not be in the past.\n" +
                "Keep in mind the custom event date will be processed 30 min earlier, so the next 30 minutes will be considered to be the \"past\".");
        en_gb.setProperty("signup_invaliddateday", "Invalid Date and Time: The given date does not exist. (e.g. Feb 31)");
        en_gb.setProperty("signup_embedtitle_empty", "Sign up");
        en_gb.setProperty("signup_embedtitle_notempty", "Sign up for %s");
        en_gb.setProperty("signup_embeddescription", "Click on %s to participate.\n" +
                "Remove said reaction if you have changed your mind.\n\n" +
                "%s people can participate!" +
                "%s");
        en_gb.setProperty("signup_embeddescription_custom", "\nThe signup will close 30 minutes prior to the scheduled event.");
        en_gb.setProperty("signup_embeddescriptionend", "%s people could participate!\n\n" +
                "These are the participants:");
        en_gb.setProperty("signup_nobody", "Nobody signed up");
        en_gb.setProperty("signup_timeout", "Times out at");
        en_gb.setProperty("signup_event", "Event at");
        en_gb.setProperty("signup_timeout_finish", "Ended at");

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
        en_gb.setProperty("votes_active", "Ends at");
        en_gb.setProperty("votes_inactive", "This poll has ended.");

        /// Quickvote
        en_gb.setProperty("quickvote_started", "%s started a quickpoll!");
        en_gb.setProperty("quickvote_ended", "%s has ended the quickpoll!");
        en_gb.setProperty("quickvote_missing_db", "Greetings mediator! I couldn't remove a succesful quickvote ending from the database.");

        /// Vote
        en_gb.setProperty("vote_description", "Create a poll with up to 10 custom answers.");
        en_gb.setProperty("vote_usage", "Command: `%s%s [question]/[answer1]/(...)/[answer10]`\n" +
                "Example: `%s%s When do you have time?/Mon/Tue/Wed/Thu/Fri/Sat/Sun`");
        en_gb.setProperty("vote_hint", "After executing this command, you will be asked if you want to allow multiple answers.");
        en_gb.setProperty("vote_amount", "Invalid amount of arguments. There has to be at least one answer to your question and a maximum of 10 answers.");
        en_gb.setProperty("vote_timeout", "Timeout! You didn't react on my question.");
        en_gb.setProperty("vote_started", "%s started a poll!");
        en_gb.setProperty("vote_ended", "%s has ended the poll!");
        en_gb.setProperty("vote_missing_db", "Greetings mediator! I couldn't remove a succesful vote ending from the database.");
        en_gb.setProperty("vote_multiple", "You can select multiple answers.");
        en_gb.setProperty("vote_single", "You can only pick one answer.");

        // Fun
        /// Avatar
        en_gb.setProperty("avatar_description", "Steal someone's avatar.");
        en_gb.setProperty("avatar_usage", "Command: `%s%s @user`\n");
        en_gb.setProperty("avatar_stolen", "%s just stole %s's avatar!");

        /// Baguette
        en_gb.setProperty("baguette_49", "Unlucky");
        en_gb.setProperty("baguette_50", "JACKPOT! Now you're cool.");

        /// Bio
        en_gb.setProperty("bio_maxlength", "Bio max length is 50.");

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
        en_gb.setProperty("embed_timestamp_i", "Please provide a timestamp.\nYou either can say `now` or provide a date and time like this: `yyyy-MM-dd HH:mm` (uses server timezone):");
        en_gb.setProperty("embed_timestamp_i_fail", "Your input is invalid.\nYou either can say `now` or provide a date and time like this: `yyyy-MM-dd HH:mm` (uses server timezone).\nTry again:");

        //// Create Embed
        en_gb.setProperty("createembed_introduction", "With this command, you can create your own embed.\n" +
                "- You cannot create an empty embed.\n" +
                "- The embed but not be longer than 6000 characters in total.\n" +
                "- Everytime you have to click a reacton or write an answer, you have a time limit of 15 minutes.\n" +
                "Are you prepared?");
        en_gb.setProperty("createembed_author_name", "Author name (can point to URL)");
        en_gb.setProperty("createembed_title", "Title (can point to URL)");
        en_gb.setProperty("createembed_description", "Description\n" +
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
        en_gb.setProperty("createembed_done_repeated", "Invalid input! You either didn't mention a channel (e.g. #channel) or you are not allowed to write in the given channel.\nTry again:");

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
        en_gb.setProperty("editembed_missingpermission", "You need to be able to write in the given channel to access embeds.");
        en_gb.setProperty("embed_field_remove_q", "Do you want to remove the current fields?");

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

        /// Profile
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
        en_gb.setProperty("profile_total_muc", "Total commands used");
        en_gb.setProperty("profile_total_ap", "Total AP");

        // Random
        en_gb.setProperty("random_empty", "I couldn't find anything with this keyword.");

        /// Unflip
        en_gb.setProperty("unflip_description", "Unlip a user.");
        en_gb.setProperty("unflip_usage", "Command: `%s%s @user`");
        en_gb.setProperty("unflip_hint", "Check out `%sflip @user`");

        // Interaction
        en_gb.setProperty("interaction_description", "Interaction commands are like reactions, but way better.\n" +
                "Share your feelings or cookies with other people.");
        en_gb.setProperty("interaction_usage", "**%s%s someone**\n" +
                "Command: `%s%s [@user]`\n" +
                "Example: `%s%s @Servant`");
        en_gb.setProperty("interaction_usage_on", "**%s%s on someone**\n" +
                "Command: `%s%s [@user]`\n" +
                "Example: `%s%s @Servant`");


        en_gb.store(os,
                "Project: Servant\n" +
                        "Author: Tancred#0001\n" +
                        "GitHub: https://github.com/Tancred423/Servant");
        os.close();
    }

    static void createDefaultDE_DE() throws IOException {
        var de_de = new OrderedProperties();
        var os = new FileOutputStream(System.getProperty("user.dir") + "/resources/lang/de_de.ini");

        // General
        de_de.setProperty("permission", ":x: Du brauchst in diesem Server die Berechtigung %s, um dies zu tun!");
        de_de.setProperty("blocking_dm", "Die Hilfe konnte nicht gesendet werden, da Du private Nachrichten blockierst.");
        de_de.setProperty("invalid_mention", "Ungültige Erwähnung.");
        de_de.setProperty("current_prefix", "Aktueller Prefix: %s");
        de_de.setProperty("unknown_message", "Unbekannte Nachricht.");

        // Features
        /// Achievement
        de_de.setProperty("achievement_excalibur", "Excalibur");
        de_de.setProperty("achievement_unlimited_blade_works", "Unlimited Blade Works");
        de_de.setProperty("achievement_gae_bolg", "Die Legende des Lancer");
        de_de.setProperty("achievement_level10", "Level 10");
        de_de.setProperty("achievement_level20", "Level 20");
        de_de.setProperty("achievement_level30", "Level 30");
        de_de.setProperty("achievement_level40", "Level 40");
        de_de.setProperty("achievement_level50", "Level 50");
        de_de.setProperty("achievement_level60", "Level 60");
        de_de.setProperty("achievement_nicelevel", "Nice Level");
        de_de.setProperty("achievement_level70", "Level 70");
        de_de.setProperty("achievement_level80", "Level 80");
        de_de.setProperty("achievement_level90", "Level 90");
        de_de.setProperty("achievement_level100", "Level 100");
        de_de.setProperty("achievement_love42", "Fand die Antwort");
        de_de.setProperty("achievement_love69", "Nice Liebe");
        de_de.setProperty("achievement_kind", "War nett zu %s");
        de_de.setProperty("achievement_navi", "Hey Listen!");
        de_de.setProperty("achievement_deusvult", "DEUS VULT!");
        de_de.setProperty("achievement_fiteme", "Fite me!!!");
        de_de.setProperty("achievement_xmas", "Xmas Time");

        /// Invite
        de_de.setProperty("invite_author", "%s, zu Ihren Diensten!");
        de_de.setProperty("invite_description", "Danke Dir, dass Du mich gewählt hast, Dich und Deinen Server zu unterstützen.\n" +
                "Ich habe eine Menge Funktionen. Die meisten davon sind standardmäßig aktiviert, aber einige sind es nicht.\n" +
                "Schreibe `%stoggle all show`, um den Status aller Funktionen nachzusehen.\n" +
                "Danach kannst Du die Features nach belieben ein- bzw. ausschalten.\n" +
                "\n" +
                "Für den Start empfehle ich Dir den `%shelp` Befehl zu benutzen.\n" +
                "Um eine detailierte Hilfe zu einem Befehl zu bekommen, gebe diesen einfach ohne Argumente ein. Z. B. `%savatar`\n" +
                "\n" +
                "Falls Du weitere Hilfe benötigst, kannst Du meinem Hilfsserver beitreten oder meinen Ersteller privat anschreiben.\n" +
                "Hilfsserver: [Klicke um beizutreten](https://%s)\n" +
                "Name des Erstellers: %s#%s\n" +
                "E-Mail: `servant@tanc.red`\n" +
                "\n" +
                "Viel Spaß!");
        de_de.setProperty("invite_footer", "Du hast diese Nachricht erhalten, da jemand mich zu Deinem Server (%s) eingeladen hat.");

        /// Kick
        de_de.setProperty("kick_author", "Lebewohl!");
        de_de.setProperty("kick_description", "Es ist sehr traurig zu hören, dass Du mich nicht mehr benötigst.\n" +
                "Falls es irgendetwas gibt, das ich an mir verbessern kann, lasse es mich bitte wissen.\n" +
                "\n" +
                "Um eine Rückmeldung zu geben kannst Du dem Hilfsserver beitreten oder meinen Erschaffer privat schreiben.\n" +
                "Hilfsserver: [Click to join](https://%s)\n" +
                "Name des Erschaffers: %s#%s\n" +
                "E-Mail: `servant@tanc.red`\n");
        de_de.setProperty("kick_footer", "Du hast diese Nachricht erhalten, da jemand mich von deinem Server (%s) gekickt hat oder dieser gelöscht wurde.");

        /// Patreon Handler
        de_de.setProperty("patreon_warning", "Du musst ein %s sein, um diese Funktion nutzen zu können!");

        /// Presence
        de_de.setProperty("presence_0", "v%s | %shelp");
        de_de.setProperty("presence_1", "%s Meister | %shelp");
        de_de.setProperty("presence_2", "%s Server | %shelp");
        de_de.setProperty("presence_3", "%spatreon | %shelp");
        de_de.setProperty("presence_4", "https://servant.tanc.red");

        // Owner
        /// Add Gif
        de_de.setProperty("addgif_description", "Dies ist ein Befehl, um GIFs zu den Interaktion-Befehlen hinzuzufügen.");
        de_de.setProperty("addgif_usage", "**Füge ein GIF hinzu**\n" +
                "Befehl: `%s%s [interaktion] [GIF URL]`\n" +
                "Beispiel: `%s%s slap https://i.imgur.com/bbXmAx2.gif`\n");
        de_de.setProperty("addgif_hint", "Bild URL muss ein [direct link](https://www.urbandictionary.com/define.php?term=direct%20link) sein.");
        de_de.setProperty("addgif_args", "2 Arugmente sind nötig.\n... [interaktion] [GIF URL]");
        de_de.setProperty("addgif_interaction", "Ungültige Interaktion.");
        de_de.setProperty("addgif_direct_link", "Keine gültige GIF URL. Es muss ein direct link sein!");

        /// Blacklist
        de_de.setProperty("blacklist_missingid", "Add an ID");
        de_de.setProperty("blacklist_empty", "No blacklisted ID's.");

        /// Server List
        de_de.setProperty("guildlist_members", "Mitglieder");
        de_de.setProperty("guildlist_connected", "Server, die mit **%s** verbunden sind");

        // Moderation
        /// Auto Role
        de_de.setProperty("autorole_description", "Diese Rolle wird automatisch neuen Mitgliedern gegeben.");
        de_de.setProperty("autorole_usage", "**Stelle eine neue autorole ein**\n" +
                "Befehl: `%s%s set [@role] [optinal delay]`\n" +
                "Beispiel 1: `%s%s set @Mitglied`\n" +
                "Beispiel 2: `%s%s set @Mitglied 10` - Rolle nach 10 Minuten.\n" +
                "\n" +
                "**Setze die autorole zurück**\n" +
                "Befehl: `%s%s unset`\n" +
                "\n" +
                "**Zeige die aktuelle autorole an**\n" +
                "Befehl: `%s%s show`");
        de_de.setProperty("autorole_hint", "Die Rolle beim Einstellen zu erwähnen, erwähnt auch jedes Mitglied mit dieser Rolle.\n" +
                "Führe diesen Befehl in einem verstecktem Kanal aus, um das Erwähen vieler Mitglieder zu verhindern.");
        de_de.setProperty("autorole_no_role", "Du hast keine Rolle erwähnt.");
        de_de.setProperty("autorole_missing", "Es wurde noch keine autorole gesetzt.");
        de_de.setProperty("autorole_no_current", "Es gibt aktuell keine autorole.");
        de_de.setProperty("autorole_current", "Die aktuelle autorole ist: %s (%s) mit einer Verzögerung von %s Minuten.");
        de_de.setProperty("autorole_first_arg", "Ungültiges erstes Argument.\nEntweder `set`, `unset` oder `show`");

        /// Birthday
        de_de.setProperty("birthday_description", "Verwalte die Geburtstage auf diesem Server:\n" +
                "- Mitglieder können ihren Geburtstag der Liste hinzufügen.\n" +
                "- Mitglieder können ihren Geburstag von der Liste enfernen.\n" +
                "- Mitglieder können eine einmalige Liste aller Geburtstage des Servers erstellen.\n" +
                "- Moderatoren können einen Benachrichtungskanal festlegen, in welchem %s den entsprechenden Mitgliedern gratulieren wird.\n" +
                "- Moderatoren können eine Liste aller Geburtstage des Servers erstellen, welche sich regelmäßig aktualisiert.\n" +
                "- Moderatoren können %ss Geburtstag hinuzfügen oder entfernen.");
        de_de.setProperty("birthday_usage", "**Stelle einen Benachrichtungskanal ein**\n" +
                "Befehl: `%s%s #kanal`\n" +
                "**Setze den Benachrichtungskanal zurück**\n" +
                "Befehl: `%s%s unsetchannel`\n" +
                "**Erstelle eine automatisch aktualisierende Geburtstagsliste**\n" +
                "Befehl: `%s%s updatelist`\n" +
                "**Füge Deinen Geburtstag hinzu**\n" +
                "Befehl: `%s%s yyyy-MM-dd`\n" +
                "Beispiel: `%s%s 1990-12-31`\n" +
                "**Entferne Deinen Geburtstag**\n" +
                "Befehl: `%s%s unsetbirthday`\n" +
                "**Erstelle eine einmalige Geburtstagsliste**\n" +
                "Befehl: `%s%s list`\n" +
                "**Füge/Entferne %ss Geburtstag hinzu**\n" +
                "Befehl: `%s%s %s`");
        de_de.setProperty("birthday_hint", "Die Befehle haben unterschiedliche benötigte Berechtigungen.\n" +
                "Siehe Beschreibung:\n" +
                "- \"Mitglieder\": Jeder\n" +
                "- \"Moderatoren\": Kanäle verwalten");
        de_de.setProperty("birthday_countdown", "Countdown");
        de_de.setProperty("birthday_countdown_value", "in %s Tagen");
        de_de.setProperty("birthday_date", "Datum");
        de_de.setProperty("birthday_name", "Name");
        de_de.setProperty("birthday_missing", "Keine Geburtstage vorhanden.");
        de_de.setProperty("birthday_guild", "%s Geburtstage.");
        de_de.setProperty("birthday_as_of", "Stand");
        de_de.setProperty("birthday_gratulation", "Herzlichen Glückwunsch zum Geburtstag %s!");
        de_de.setProperty("birthday_not_set", "Du hattest noch keinen Geburtstag gesetzt.");
        de_de.setProperty("birthday_invalid", "Ungültiges Argument.\n" +
                "Mods können...\n" +
                "... einen Kanal erwähnen, um diesen als Benachrichtungskanal einzustellen.\n" +
                "... `unsetchannel` schreiben, um den Benachrichtungskanal zurückzusetzen.\n" +
                "... `updatelist` schreiben, um eine automatisch regelmäßig aktualisierende Geburtstagsliste zu erzeugen.\n" +
                "Jeder kann...\n" +
                "... ihren Geburtstag (yyyy-MM-dd) der Liste hinzufügen.\n" +
                "... `unsetbirthday` schreiben, um ihren Geburtstag aus der Liste enfernen zu lassen.\n" +
                "... `list` schreiben, um eine einmalige Geburtstagsliste zu bekommen.");

        /// Best of's
        de_de.setProperty("bestof_usage", "**Richte das Abstimmungs-Emote/Emoji ein**\n" +
                "Befehl: `%s%s [Emoji oder Emote]`\n" +
                "Beispiel (Emoji): `%s%s ⭐`\n" +
                "Beispiel (Emote): `%s%s` %s\n" +
                "\n" +
                "**Richte einen Best-of-Kanal ein**\n" +
                "Befehl: `%s%s #kanal`\n" +
                "\n" +
                "**Richte ein, wie viele Leute abstimmen müssen**\n" +
                "Befehl: `%s%s [Nummer]`\n" +
                "Beispiel: `%s%s 10`\n" +
                "\n" +
                "**Richte ein, wie viel Prozent der Online-Nutzer abstimmen müssen**\n" +
                "Befehl: `%s%s [Prozentsatz]%s`\n" +
                "Beispiel: `%s%s 50%s`\n" +
                "\n" +
                "**Zeige das aktuelle Setup an**\n" +
                "Befehl: `%s%s show`");
        de_de.setProperty("bestof_hint", "Sowohl eine Nummer als auch einen Prozentsatz einzurichten hat einen entscheidenen Vorteil:\n" +
                "- Während dein Server wächst, ist es vorteilhaft einen Prozentsatz festzulegen, damit sich die Anzahl der Leute, die abstimmen müssen, automatisch anpasst.\n" +
                "- Allerdings sind während der Nacht eventuell viel weniger Leute online und die Prozentzahl könnte missbraucht werden. Daher kannst Du eine Nummer festlegen, die immer erreicht werden muss.");
        de_de.setProperty("bestof_emote", "Emote/Emoji");
        de_de.setProperty("bestof_noemote", "Kein Emote/Emoji gesetzt");
        de_de.setProperty("bestof_number", "Nummer");
        de_de.setProperty("bestof_nonumber", "Keine Nummer gesetzt");
        de_de.setProperty("bestof_percentage", "Prozentsatz");
        de_de.setProperty("bestof_nopercentage", "Kein Prozentsatz gesetzt");
        de_de.setProperty("bestof_channel", "Kanal");
        de_de.setProperty("bestof_nochannel", "Kein Kanal gesetzt");
        de_de.setProperty("bestof_numbertoobig", "Dise Number ist viel zu groß!");
        de_de.setProperty("bestof_invalidpercentage", "Dieser Prozentsatz ist ungültig.");
        de_de.setProperty("bestof_invalidemote", "Ich kann dieses Emote nicht finden! Stelle sicher, dass ich darauf Zugriff habe!");
        de_de.setProperty("bestof_invalidemoji", "Ungültiges Emoji, Meister!");
        de_de.setProperty("bestof_jump", "Drücke hier, um zur orginalen Nachricht zu gelangen.");
        de_de.setProperty("bestof_footer", "%s Stimmen | #%s");

        /// Best of Image
        de_de.setProperty("bestofimage_description", "Nutzer können Bildern ihre Stimme geben, sodass dieses eventuell im Best-of-Kanal gepostet wird.\n" +
                "Du kannst das Abstimmungs-Emote/Emoji, den Best-of-Kanal und die Anzahl an Leute, die abstimmen müssen numerisch und prozentual einstellen.");
        de_de.setProperty("bestofimage_setup", "Best Of Image Setup");

        /// Best of Quote
        de_de.setProperty("bestofquote_description", "Nutzer können Nachrichten ihre Stimme geben, sodass diese eventuell im Best-of-Kanal gepostet wird.\n" +
                "Du kannst das Abstimmungs-Emote/Emoji, den Best-of-Kanal und die Anzahl an Leute, die abstimmen müssen numerisch und prozentual einstellen.");
        de_de.setProperty("bestofquote_setup", "Best Of Quote Setup");

        /// Clear
        de_de.setProperty("clear_description", "Löscht bis zu 100 Nachrichten.\n" +
                "Kann nutzerspezifische Nachrichten der letzten 100 Nachrichten löschen.\n" +
                "Nachrichten, die älter sind als zwei Wochen, können aufgrund von Discords Beschränkungen nicht automatisiert gelöscht werden.");
        de_de.setProperty("clear_usage", "**Lösche einige Nachrichten**\n" +
                "Befehl: `%s%s [1 - 100 ODER @Nutzer]`\n" +
                "Beispiel 1: `%s%s 50`\n" +
                "Beispiel 2: `%s%s @Name");
        de_de.setProperty("clear_hint", "Die Anzahl der Nachrichten ist inklusiv, d. h. Du kannst nur eine Nachricht oder auch ganze 100 löschen.");
        de_de.setProperty("clear_input", "Du kannst nur eine Nummer oder eine Nutzererwähnung angeben!");
        de_de.setProperty("clear_sub_one", "Die Eingabe darf nicht niedriger als 1 sein.");
        de_de.setProperty("clear_cleared", "%s Nachrichten gelöscht");
        de_de.setProperty("clear_invalid", "Deine Eingabe ist ungültig. Versuche es mit einer kleineren Zahl.");

        /// Join + Leave general
        de_de.setProperty("joinleave_nochannel_mention", "Kein Kanal wurde erwähnt.");
        de_de.setProperty("joinleave_unset_fail", "Kein Kanal wurde bisher gesetzt.");
        de_de.setProperty("joinleave_nochannel_set", "Kein Kanal wurde gesetzt.");
        de_de.setProperty("joinleave_current", "Aktueller Kanal: %s");
        de_de.setProperty("joinleave_firstarg", "Entweder `set`, `unset` oder `show`");

        /// Join
        de_de.setProperty("join_description", "Der Bot wird eine Benachrichtigung posten, sobald ein Nutzer den Server betreten hat.");
        de_de.setProperty("join_usage", "**Einrichten des Beitrittsbenachrichtigungskanals**\n" +
                "Befehl: `%s%s set [#kanal]`\n" +
                "Beispiel: `%s%s set #willkommen`\n" +
                "\n" +
                "**Zurücksetzen des Kanals**\n" +
                "Befehl: `%s%s unset`\n" +
                "\n" +
                "**Anzeigen des aktuellen Kanals**\n" +
                "Befehl: `%s%s show`");
        de_de.setProperty("join_author", "Willkommen %s#%s zu %s");
        de_de.setProperty("join_embeddescription", "Genieße deinen Aufenthalt!");
        de_de.setProperty("join_footer", "Trat bei am");

        /// Leave
        de_de.setProperty("leave_description", "Der Bot wird eine Benachrichtung posten, sobald ein Nutzer den Server verlassen hat.");
        de_de.setProperty("leave_usage", "**Einrichten des Austrittsbenachrichtungskanals**\n" +
                "Befehl: `%s%s set [#kanal]`\n" +
                "Beispiel: `%s%s set #willkommen`\n" +
                "\n" +
                "**Zurücksetzen des Kanals**\n" +
                "Befehl: `%s%s unset`\n" +
                "\n" +
                "**Anzeigen des aktuellen Kanals**\n" +
                "Befehl: `%s%s show`");
        de_de.setProperty("leave_left", "%s#%s verließ %s!");
        de_de.setProperty("leave_author", "Lebewohl %s#%s");
        de_de.setProperty("leave_embeddescription", "Es ist schade, Dich gehen zu sehen!");
        de_de.setProperty("leave_footer", "Trat aus am");

        /// Level Role
        de_de.setProperty("levelrole_description", "Richte Rollen ein, die einem Mitglied zugeteilt werden, sobald dieser ein bestimmtes Level erreicht.");
        de_de.setProperty("levelrole_usage", "**Richte eine Rolle ein**\n" +
                "- Befehl: `%s%s set [Level] @Rolle`\n" +
                "- Beispiel: `%s%s set 10 @SuperMitglied`\n" +
                "\n" +
                "**Setze eine Rolle zurück**\n" +
                "- Befehl: `%s%s unset [Level] @Rolle`\n" +
                "- Beispiel: `%s%s unset 10 @SuperMitglied`\n" +
                "\n" +
                "**Zeige die aktuellen levelroles an**\n" +
                "- Befehl: `%s%s show`\n" +
                "\n" +
                "**Aktualisieren**\n" +
                "- Befehl: `%s%s refresh`");
        de_de.setProperty("levelrole_hint", "Du kannst mehrere Rollen einem Level zuweisen.\n" +
                "Mit refresh kannst du den Bot alle Mitglieder überprüfen lassen und die eventuell fehlenden Levelroles hinzufügen, sofern ein Mitglied diese nicht bekommen hat oder eine neue Levelrolle hinzugefügt wurde, welche unter dem Level eines Mitglieds ist. Hierbei werden keine Rollen entfernt.");
        de_de.setProperty("levelrole_missing", "Du musst ein Level und eine Rollenerwähnung angeben.");
        de_de.setProperty("levelrole_invalidlevel", "Ungültiges Level.");
        de_de.setProperty("levelrole_levelrole", "Levelrolle");
        de_de.setProperty("levelrole_empty", "Keine Levelrollen gesetzt.");
        de_de.setProperty("levelrole_current", "Aktuelle Levelrollen");
        de_de.setProperty("levelrole_alreadyset", "Diese Rolle wurde bereits für dieses Level gesetzt.");
        de_de.setProperty("levelrole_role_singular", "Du hast zusätzlich folgende Rolle erhalten:");
        de_de.setProperty("levelrole_role_plural", "Du hast zusätzelich folgende Rollen erhalten:");
        de_de.setProperty("levelrole_levelup", "LEVEL UP");

        /// Livestream
        de_de.setProperty("livestream_description", "Du kannst mehrere Streamer, einen Benachrichtigungskanal und eine Streamerrolle festlegen.\n" +
                "Sobald ein Streamer online geht, wird eine Benachrichtigung **mit** @here gepostet und der Streamer bekommt die gesetzte Rolle.\n" +
                "Falls Du den Streamer-Modus `off` togglest, wird eine Benachtigung **ohne** @here gepostet und das Mitglied bekommt die gesetzte Rolle.");
        de_de.setProperty("livestream_usage", "**Hinzufügen/Löschen eines Streamers**\n" +
                "Hinzufügen: `%s%s set @nutzer`\n" +
                "Löschen: `%s%s unset @nutzer`\n" +
                "\n" +
                "**(Zurück)setzen des Benachrichtigungskanals**\n" +
                "Setzen: `%s%s set #kanal`\n" +
                "Zurücksetzen: `%s%s unset #kanal`\n" +
                "\n" +
                "**(Zurück)setzen der Streamer Rolle**\n" +
                "Setzen: `%s%s set @rolle`\n" +
                "Zurücksetzen: `%s%s unset @rolle`\n" +
                "\n" +
                "**Toggle Streamer-Mode**\n" +
                "Befehl: `%s%s toggle`\n" +
                "\n" +
                "**Zeige die aktuellen Steameinstellungen an**\n" +
                "Befehl: `%s%s show`");
        de_de.setProperty("livestream_hint", "Streamer-Modus: Nur gesetze Streamer lösen eine Benachrichtigung aus und erhalten eine Rolle.\n" +
                "Öffentlicher Modus: Jeder löst eine Benachrichtigung aus und erhält eine Rolle.");
        de_de.setProperty("livestream_missingmention", "Du kannst keinen Kanal, User oder Rolle erwähnt..");
        de_de.setProperty("livestream_toomanymentions", "Du hast zu viel erwähnt. Eins nach dem anderen!");
        de_de.setProperty("livestream_nochannel", "Es wurde kein Kanal gesetzt.");
        de_de.setProperty("livestream_nostreamer", "Dieser Nutzer ist kein Streamer.");
        de_de.setProperty("livestream_norole", "Diese Rolle wurde nicht gesetzt.");
        de_de.setProperty("livestream_firstarg", "Entweder `set`, `unset` oder `show`");
        de_de.setProperty("livestream_settings", "Livetream Einstellungen");
        de_de.setProperty("livestream_notificationchannel", "Benachrichtigungskanal");
        de_de.setProperty("livestream_role", "Livestreaming Rolle");
        de_de.setProperty("livestream_mode", "Streamer-Modus");
        de_de.setProperty("livestream_streamers", "Streamer");
        de_de.setProperty("livestream_nochannelset", "Kein Kanal gesetzt");
        de_de.setProperty("livestream_noroleset", "Keine Rolle gesetzt");
        de_de.setProperty("livestream_publicmode", "Öffentlicher Modus");
        de_de.setProperty("livestream_nostreamersset", "Keine Streamer gesetzt");
        de_de.setProperty("livestream_announcement_title", "Livestream!");
        de_de.setProperty("livestream_announcement", "%s ging gerade auf [Twitch (click me)](%s) live!");
        de_de.setProperty("livestream_announcement_game", "Streamt %s");

        /// Media Only Channel
        de_de.setProperty("mediaonlychannel_description", "Falls ein Mitglied eine normale Nachricht in einen Textkanal, der als MediaOnlyChannel markiert ist, schreibt, wird die Nachricht gelöscht und eine Warnung wird gesendet.\n" +
                "Mitglieder können nur Links posten oder Dateien hochladen.\n" +
                "Dies kann sehr nützlich sein für z.B. einen Meme Kanal.");
        de_de.setProperty("mediaonlychannel_usage", "**Richte den MediaOnlyChannel ein**\n" +
                "Befehl: `%s%s set [#kanal]`\n" +
                "Beispiel: `%s%s set #memes`\n" +
                "\n" +
                "**Zurücksetzen eines MediaOnlyChannels**\n" +
                "Befehl: `%s%sunset [#kanal]`\n" +
                "Beispiel: `%s%s unset #memes`\n" +
                "\n" +
                "**Zeige die aktuellen MediaOnlychannel Einstellungen an**\n" +
                "Befehl: `%s%s show`");
        de_de.setProperty("mediaonlychannel_hint", "Du kannst mehrere MediaOnlyChannel haben.");
        de_de.setProperty("mediaonlychannel_missingmention", "Du hast keinen Kanal erwähnt.");
        de_de.setProperty("mediaonlychannel_invalidchannel", "Der gegebene Kanal ist ungültig.");
        de_de.setProperty("mediaonlychannel_unset_fail", "Dieser Kanal war nicht als MediaOnlyChannel gesetzt.");
        de_de.setProperty("mediaonlychannel_nochannels", "Es existieren keine MediaOnlyChannel.");
        de_de.setProperty("mediaonlychannel_firstarg", "Ungültiges erstes Argument.\n" +
                "Entweder `set`, `unset` oder `show`");
        de_de.setProperty("mediaonlychannel_warning", "%s, dies ist ein MediaOnlyChannel!\n" +
                "Du darfst:\n" +
                "- Dateien mit einer optionalen Beschreibung hochladen.\n" +
                "- Einen gültigen Link mit einer optionalen Beschreibung senden.\n" +
                "*Diese Nachricht wird in 30 Sekunden gelöscht.*");
        de_de.setProperty("mediaonlychannel_alreadyset", "Dieser Kanal wurde bereits gesetzt.");

        /// Reaction Role
        de_de.setProperty("reactionrole_description", "Mit diesem Befehl kannst Du Reaktionen an eine Nachricht binden. Sobald ein Mitglied auf eine dieser Reaktionen drückt, erhält der die festgelegte Rolle.\n" +
                "Dies erlaubt Dir ein einfaches Rollenverwaltungssystem via Reaktionen.");
        de_de.setProperty("reactionrole_usage", "**Richte eine Reaktion zur Verwaltung der Rollen ein**\n" +
                "Befehl: `%s%s set [#kanal | Kanal-ID] [Nachrichten-ID] [emoji/emote] [@rolle | Rollen-ID]`\n" +
                "Beispiel: %s%s set #test-kanal 999999999999999999 %s @rolle\n" +
                "\n" +
                "**Setze eine Reaktion zurück**\n" +
                "Befehl: `%s%s unset [#kanal | Kanal-ID] [Nachrichten-ID] [emoji/emote]`\n" +
                "Beispiel: %s%s unset #test-kanal 999999999999999999 %s");
        de_de.setProperty("reactionrole_hint", "**Wie man IDs erhält:**\n" +
                "Möglichkeit 1: Im Chat posten: `\\@rolle`, `\\#kanal`, etc. - DIES WIRD DIE ROLLE AUCH ANPINGEN. Vielleicht solltest Du das in keinem öffentlichen Kanal senden.\n" +
                "Möglichkeit 2:\n" +
                "- Discord Einstellungen → Erscheinungsbild → ERWEITERT → Entwicklermodus anschalten\n" +
                "- Rechtsklick auf Kanal, Rolle, etc. → ID kopieren");
        de_de.setProperty("reactionrole_toofewargs", "Zu wenige Argumente. Du musst die Kanal-ID, Nachrichten-ID, ein Emoji oder Emote und eine Rollenerwähnung oder Rollen-ID hinter `set` angeben.");
        de_de.setProperty("reactionrole_alreadyset", "Dieses Emoji oder Emote wurde bereits gesetzt. Setze das erste zurück falls du das Emoji oder Emote aktualisieren möchtest.");
        de_de.setProperty("reactionrole_notset", "Dieses Emoji oder Emote wurde nicht gesetzt.");
        de_de.setProperty("reactionrole_firstarg", "Entweder `set` oder `unset` eine Reaktion.");
        de_de.setProperty("reactionrole_insufficient", "Ungenügende Berechtigungen oder ein Problem mit der Hierarchie.");
        de_de.setProperty("reactionrole_invalidmessageid", "Ungültige Nachrichten ID.");

        /// Role
        de_de.setProperty("role_description", "Orde Mitgliedern neuen Rollen zu oder nehme ihnen diese weg.");
        de_de.setProperty("role_usage", "Befehl: `%s%s @user [Rollenname]`\n" +
                "Beispiel: `%s%s @name Mitglied`");
        de_de.setProperty("role_hint", "Ich werde die Rolle entfernen falls das Mitglied sie bereits hatte und sie ihm geben falls nicht.");
        de_de.setProperty("role_missing", "Fehlender Rollenname.");
        de_de.setProperty("role_notfound", "Verzeichung, Meister! Ich konnte keine Rolle mit diesem Namen finden.");
        de_de.setProperty("role_missingrolename", "Bitte gebe auch den Rollennamen an.");
        de_de.setProperty("role_cantinteract", "Ich kann nicht mit diesem User interagieren. (Überprüfe die Berechtigungen)");

        /// Server
        de_de.setProperty("server_description", "Mit diesem Befehl kannst Du den Bot nach deinen Wünschen für diesen Server einrichten.");
        de_de.setProperty("server_usage", "**Richte einen Offset ein**\n" +
                "Befehl: `%s%s set offset [offset]`\n" +
                "Beispiel: `%s%s set offset +01:00`\n" +
                "\n" +
                "**Setze den Offset zurück**\n" +
                "Befehl: `%s%s unset offset`\n" +
                "\n" +
                "**Richte einen serverspezifischen Prefix ein**\n" +
                "Befehl: `%s%s set prefix [prefix]`\n" +
                "Beispiel: `%s%s set prefix -`\n" +
                "\n" +
                "**Setze den Prefix zurück**\n" +
                "Befehl: `%s%s unset prefix`\n" +
                "\n" +
                "**Setlle eine Sprache ein**\n" +
                "Befehl: `%s%s set language [Sprachenschlüssel]`\n" +
                "Beispiel: `%s%s set language de_de`\n" +
                "\n" +
                "**Setze die Sprache zurück**\n" +
                "Befehl: `%s%s unset language`\n" +
                "\n" +
                "**Zeige deine aktuellen Einstellungen**\n" +
                "Befehl: `%s%s show`");
        de_de.setProperty("server_hint", "Sofern du den Offset zurücksetzt, wird lediglich Dein eingesteller zurückgesetzt und der Standardoffset (%s) wird wieder genutzt.\n" +
                "Offsets nehmen immer UTC als Basis.\n" +
                "Wenn du den Prefix zurücksetzt, wird der Defaultprefix (%s) genutzt\n\n" +
                "Aktuell verfügbare Sprachen:\n" +
                "- en_gb - English (British)\n" +
                "- de_de - German (Deutsch)");
        de_de.setProperty("server_args_set", "Um eine Einstellung einzurichten, müssen 3 Argumente mitgegeben werden.\n" +
                "... set [Einstellung] [Wert]");
        de_de.setProperty("server_offset", "Ungültiger Offset.");
        de_de.setProperty("server_prefix", "Ungültiger Prefix.");
        de_de.setProperty("server_invalidsetting", "Diese Einstellung existiert nicht.");
        de_de.setProperty("server_args_unset", "Um eine Einstellung zurückuzsetzen, müssen 2 Argumente mitgegeben werden.\n" +
                "... unset [Einstellung]");
        de_de.setProperty("server_offset_text", "Offset");
        de_de.setProperty("server_prefix_text", "Prefix");
        de_de.setProperty("server_language_text", "Sprache");
        de_de.setProperty("server_settings", "Server Einstellungen");
        de_de.setProperty("server_firstarg", "Das erste Arugment muss entweder `set`, `unset` oder `show` sein.");

        /// Server Setup
        de_de.setProperty("setupwizard_introduction", "Willkommen zu dem Server Setup Wizard!\n" +
                "Nachdem wir dies abgeschlossen haben, wirst Du alle (3) Grundeinstellungen eingerichtet haben und wir sind bereit loszulegen!\n" +
                "Bei jeder frage hast Du 15 Minuten zu antworten, andererseits wird das Setup unterbrechen.\n" +
                "Möchtest Du starten?");
        de_de.setProperty("setupwizard_timeout", "Der Setup Wizard wurde unterbrochen.");
        de_de.setProperty("setupwizard_language", "Bitte gebe an in welche Sprache Du mich sprechen lassen willst.\n" +
                "Aktuell verfügbare Sprachen:\n" +
                "   \u200B⤷ `en_gb` - English (British)\n" +
                "   \u200B⤷ `de_de` - German (Deutsch)\n" +
                "**Gebe den Sprachenschlüssel an:**");
        de_de.setProperty("setupwizard_language_repeated", "Diese Sprache ist nicht verfügbar.\n" +
                "Aktuell verfügbare Sprachen:\n" +
                "   \u200B⤷ `en_gb` - English (British)\n" +
                "   \u200B⤷ `de_de` - German (Deutsch)\n" +
                "**Versuche es erneut:**");
        de_de.setProperty("setupwizard_prefix", "%s Sprache wurde gesetzt.\n" +
                "Bitte gebe einen Prefix an, den du Nutzen möchtest.\n" +
                "Falls du diesen nicht ändern möchtest, schreibe einfach `%s`:");
        de_de.setProperty("setupwizard_prefix_repeated", "Dieser Prefix ist nicht geeignet.\n" +
                "Falls du diesen nicht ändern möchtest, schreibe einfach `%s`\n" +
                "**Versuche es erneut:**");
        de_de.setProperty("setupwizard_offset", "%s Prefix wurde gesetzt.\n" +
                "Bitte gebe einen Offset zu UTC an, um deine Zeitzone einzustellen.\n" +
                "Das Format lautet: `+HH:mm` oder `-HH:mm`. Z.B. `+01:00`\n" +
                "Um UTC/GMT zu benutzen, schreibe einfach `00:00`:");
        de_de.setProperty("setupwizard_offset_repeated", "Dieses Offset ist ungültig.\n" +
                "Das Format lautet: `+HH:mm` oder `-HH:mm`. Z.B. `+01:00`\n" +
                "Um UTC/GMT zu benutzen, schreibe einfach `00:00`\n" +
                "**Versuche es erneut:**");
        de_de.setProperty("setupwizard_done", "Wir sind fertig :)");

        /// Toggle
        de_de.setProperty("toggle_description", "You can toggle almost every feature on or off.\n" +
                "The level-feature is off by default in case you want to use it.\n" +
                "To check what features are toggleable, just use `%s%s all show`");
        de_de.setProperty("toggle_usage", "**Manage one feautre**\n" +
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
        de_de.setProperty("toggle_hint", "Be careful with toggling all features on or off, as you may delete your perfect setup.\n" +
                "You may write `everything` instead of `all`.\n" +
                "You may write `status` instead of `show`.\n" +
                "Show will only show the current status without changing any values.");
        de_de.setProperty("toggle_args", "Too few arguments.\n" +
                "toggle [feature] [on|off|show]\n" +
                "e.g.: toggle level off");
        de_de.setProperty("toggle_invalid_feature", "Invalid feature.");
        de_de.setProperty("toggle_invalid_argument", "Argument has to be `on`, `off` or `show`.");

        /// User
        de_de.setProperty("user_description", "With this command you can personalize the bot to your desire.");
        de_de.setProperty("user_usage", "**Setting an offset**\n" +
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
                "Command: `%s%s streamhide [opt. server ID]`\n" +
                "\n" +
                "**Setting an embed colour**\n" +
                "Command: `%s%s set colour [color code]`\n" +
                "Example: `%s%s set colour #FFFFFF`\n" +
                "\n" +
                "**Unsetting the embed colour**\n" +
                "Command: `%s%s unset colour`\n" +
                "\n" +
                "**Show your current settings**\n" +
                "Command: `%s%s show`");
        de_de.setProperty("user_hint", "You can set a custom embed colour. ($10 Patron)\n" +
                "An embed colour is the colour you can see right know on the left of this text field thingy.\n" +
                "Settings like offset, prefix and language only work in DM's. For server settings please refer to `%sserver`." +
                "Available languages:\n" +
                "- en_gb - British English\n" +
                "- de_de - German (Deutsch)\n" +
                "You can hide yourself from being highlighted with streaming role while streaming (server specific).");
        de_de.setProperty("user_streamhide_description", "You have to use this in a server to specify what server you want to be muted on.\n" +
                "If you want to do this secretly, you can also provide a server ID:\n" +
                "Command: `%s%s streamhide [server ID]`\n" +
                "Example: `%s%s streamhide 99999999999999999`");
        de_de.setProperty("user_streamhide_hidden", "You are now hidden!");
        de_de.setProperty("user_streamhide_visible", "You are now visible!");
        de_de.setProperty("user_args_set", "To set a setting, there have to be 3 arguments.\n" +
                "... set [setting] [value]");
        de_de.setProperty("user_invalidcolor", "The given colour code is invalid.");
        de_de.setProperty("user_invalidsetting", "This setting does not exist.");
        de_de.setProperty("user_args_unset", "To unset a setting, there have to be 2 arguments.\n" +
                "... unset [setting]");
        de_de.setProperty("user_unset_fail", "Nothing to unset.");
        de_de.setProperty("user_color_text", "Color");
        de_de.setProperty("user_prefix_text", "Prefix");
        de_de.setProperty("user_offset_text", "Offset");
        de_de.setProperty("user_language_text", "Language");
        de_de.setProperty("user_noservers", "No servers");
        de_de.setProperty("user_streamhideservers", "Stream Hide Servers");
        de_de.setProperty("user_settings", "User Settings");
        de_de.setProperty("user_firstarg", "The first argument has to be either `set`, `unset` or `show`.");

        /// Voice Lobby
        de_de.setProperty("voicelobby_description", "If a member joins an voice channel that is marked as lobby, a copy of this voice channel will be made.\n" +
                "Then the member will be moved into this new voice channel.\n" +
                "Once everyone left the new channel, it will be deleted automatically.\n" +
                "This will save you a lot of space from unused voice channels.");
        de_de.setProperty("voicelobby_usage", "**Set a voice channel lobby**\n" +
                "Command: `%s%s set [Voice Channel ID]`\n" +
                "Example: `%s%s set 999999999999999999`\n" +
                "\n" +
                "**Unset a voice channel lobby**\n" +
                "Command: `%s%s unset [Voice Channel ID]`\n" +
                "Example: `%s%s unset 999999999999999999`\n" +
                "\n" +
                "**Show current voice channel lobbies**\n" +
                "Command: `%s%s show`");
        de_de.setProperty("voicelobby_hint", "**How to get ID's:**\n" +
                "1. Activate Discord Developer Mode: User Settings → Appearance → ADVANCED → Developer Mode\n" +
                "2. Rightclick voice channel → Copy ID");
        de_de.setProperty("voicelobby_missingid", "Too few arguments. Please provide a voice channel ID.");
        de_de.setProperty("voicelobby_invalidid", "Invalid voice channel ID.");
        de_de.setProperty("voicelobby_unset_fail", "Nothing to unset.");
        de_de.setProperty("voicelobby_noneset", "No lobbies set!");
        de_de.setProperty("voicelobby_current", "Current Lobbies");
        de_de.setProperty("voicelobby_footer", "Type `%slobby` to get help.");
        de_de.setProperty("voicelobby_firstarg", "Invalid argument. Either `set`, `unset`, `show` or `toggletext`.");
        de_de.setProperty("voicelobby_apostrophe", "\'");
        de_de.setProperty("voicelobby_apostropge_s", "\'s");
        de_de.setProperty("voicelobby_already_set", "This channel was already set as an voice lobby.");

        // Information
        /// Bot Info
        de_de.setProperty("botinfo_authorname", "All about %s!");
        de_de.setProperty("botinfo_or", "or");
        de_de.setProperty("botinfo_please", "Please");
        de_de.setProperty("botinfo_moderationtools", "Moderation Tools");
        de_de.setProperty("botinfo_informativecommands", "Informative Commands");
        de_de.setProperty("botinfo_usefulfeatures", "Useful Features");
        de_de.setProperty("botinfo_funcommands", "Fun Commands");
        de_de.setProperty("botinfo_introduction", "Hello! I am **%s**");
        de_de.setProperty("botinfo_written", "was written in Java");
        de_de.setProperty("botinfo_owned", "am owned");

        de_de.setProperty("botinfo_description", "Hello, master! I am %s, your multifuntional bot.\n" +
                "I was written in Java by %s using JDA-Applications's [Commands Extension](%s) (%s) and the [JDA library](%s) (%s).\n" +
                "Type `%shelp` to see my commands!");
        de_de.setProperty("botinfo_join", "Join my server [`here`](%s)");
        de_de.setProperty("botinfo_invite", "or [`invite`](%s) me to your server!");
        de_de.setProperty("botinfo_features", "I provide:");
        de_de.setProperty("botinfo_stats", "Statistics");
        de_de.setProperty("botinfo_users", "Users");
        de_de.setProperty("botinfo_channels", "Channels");
        de_de.setProperty("botinfo_shard", "This shard");
        de_de.setProperty("botinfo_restart", "Last restart");

        /// Patreon
        de_de.setProperty("patreon_supportserver", "Support Servant");
        de_de.setProperty("patreon_description", "You can support Servant in three different ways:");
        de_de.setProperty("patreon_patreontitle", "Patreon");
        de_de.setProperty("patreon_subscription", "You can become a patron and support Servant with a monthly payment.\n" +
                "Each patreon rank will have its own embed colour and profile image.\n" +
                "[Click here to get to the Patreon page.](https://www.patreon.com/tancred)");
        de_de.setProperty("patreon_$1", "✓ Caster Profile Image\n✓ Orange Embeds");
        de_de.setProperty("patreon_$3", "✓ Lancer Profile Image\n✓ Yellow Embeds");
        de_de.setProperty("patreon_$5", "✓ Archer Profile Image\n✓ Green Embeds");
        de_de.setProperty("patreon_$10", "✓ Saber Profile Image\n✓ Blue Embeds\n✓ Custom Colour Embeds");
        de_de.setProperty("patreon_donationtitle", "Donation");
        de_de.setProperty("patreon_donation", "You can send Servant a donation.\n" +
                "Add your Discord Name#1234 into the donation message so I can see who sent the donation.\n" +
                "[Click here to get to the PayPal.me donation page.](https://www.paypal.me/servantdiscord)\n");
        de_de.setProperty("patreon_donation_$5", "✓ Assassin Profile Image\n✓ Red Embeds");
        de_de.setProperty("patreon_serverboosttitle", "Server Boost");
        de_de.setProperty("patreon_serverboost", "You can boost Servant's Kingdom with Discord Nitro to unlock new Discord server perks.\n" +
                "[Click here to join Servant's Kingdom](https://discord.gg/4GpaH5V)\n" +
                "✓ Berserker Profile Image\n" +
                "✓ Pink Embeds");
        de_de.setProperty("patreon_thanks", "Thanks to every supporter <3");

        /// Server Info
        de_de.setProperty("serverinfo_owner", "Owner: %s");
        de_de.setProperty("serverinfo_name", "%s (ID: %s)");
        de_de.setProperty("serverinfo_region", "Region: %s");
        de_de.setProperty("serverinfo_textcount", "Text Channel Count");
        de_de.setProperty("serverinfo_voicecount", "Voice Channel Count");
        de_de.setProperty("serverinfo_membercount", "Member Count");
        de_de.setProperty("serverinfo_rolecount", "Role Count");
        de_de.setProperty("serverinfo_categorycount", "Category Count");
        de_de.setProperty("serverinfo_emotecount", "Emote Count");
        de_de.setProperty("serverinfo_afktimeout", "AFK Timeout");
        de_de.setProperty("serverinfo_timeout", "%s seconds");
        de_de.setProperty("serverinfo_afkchannel", "AFK Channel");
        de_de.setProperty("serverinfo_noafkchannel", "No AFK channel");
        de_de.setProperty("serverinfo_systemchannel", "System Channel");
        de_de.setProperty("serverinfo_nosystemchannel", "No system channel");
        de_de.setProperty("serverinfo_vanity", "Vanity Url");
        de_de.setProperty("serverinfo_novanity", "No vanity url");
        de_de.setProperty("serverinfo_mfa", "MFA Level");
        de_de.setProperty("serverinfo_explicit", "Explicit Content Level");
        de_de.setProperty("serverinfo_verification", "Verification Level");
        de_de.setProperty("serverinfo_botsettings", "**%s Settings:**");
        de_de.setProperty("serverinfo_prefix", "Prefix");
        de_de.setProperty("serverinfo_offset", "Offset");
        de_de.setProperty("serverinfo_language", "Language");
        de_de.setProperty("serverinfo_bdaychannel", "Birthday Channel");
        de_de.setProperty("serverinfo_nobdaychannel", "No birthday channel");
        de_de.setProperty("serverinfo_autorole", "Auto Role");
        de_de.setProperty("serverinfo_noautorole", "No Auto Role");
        de_de.setProperty("serverinfo_autorole_value", "%s\nAfter %s minutes");
        de_de.setProperty("serverinfo_livestream", "Livestream");
        de_de.setProperty("serverinfo_nolivestream_channel", "No livestream channel");
        de_de.setProperty("serverinfo_nolivestream_role", "No livestream role");
        de_de.setProperty("serverinfo_streamermode", "Streamer Mode");
        de_de.setProperty("serverinfo_publicmode", "Public Mode");
        de_de.setProperty("serverinfo_voicelobbies", "Voice Lobbies");
        de_de.setProperty("serverinfo_novoicelobbies", "No Voice Lobbies");
        de_de.setProperty("serverinfo_mediaonlychannels", "Media Only Channels");
        de_de.setProperty("serverinfo_nomediaonlychannels", "No Media Only Channels");
        de_de.setProperty("serverinfo_join", "Join Notification Channel");
        de_de.setProperty("serverinfo_nojoin", "No join notification channel");
        de_de.setProperty("serverinfo_leave", "Leave Notification Channel");
        de_de.setProperty("serverinfo_noleave", "No leave notification channel");
        de_de.setProperty("serverinfo_none", "None");
        de_de.setProperty("serverinfo_low", "Low");
        de_de.setProperty("serverinfo_medium", "Medium");
        de_de.setProperty("serverinfo_high", "(╯°□°)╯︵ ┻━┻");
        de_de.setProperty("serverinfo_veryhigh", "┻━┻ ミヽ(ಠ益ಠ)ノ彡┻━┻");
        de_de.setProperty("serverinfo_none_desc", "Unrestricted");
        de_de.setProperty("serverinfo_low_desc", "Must have a verified email on their Discord account.");
        de_de.setProperty("serverinfo_medium_desc", "Must have a verified email on their Discord account.\n" +
                "Must also be registered on Discord for longer than 5 Minutes.");
        de_de.setProperty("serverinfo_high_desc", "Must have a verified email on their Discord account.\n" +
                "Must also be registered on Discord for longer than 5 minutes.\n" +
                "Must also be a member of this server for longer than 10 minutes.");
        de_de.setProperty("serverinfo_veryhigh_desc", "Must have a verified email on their Discord account.\n" +
                "Must also be registered on Discord for longer than 5 minutes.\n" +
                "Must also be a member of this server for longer than 10 minutes.\n" +
                "Must have a verified phone on their Discordd account.");

        // Useful
        /// Alarm
        de_de.setProperty("alarm_description", "Set up an alarm to a specific time.\n" +
                "The date and time will take your timezone into account.\n" +
                "If you didn't set one up, it will use UTC.\n" +
                "To set one up, use the `%suser` command.");
        de_de.setProperty("alarm_usage", "Command: `%s%s \"Optional Title\" [time]`\n" +
                "Example 1: `%s%s \"Pat Servant\" 3d 2h 1m`\n" +
                "Example 2: `%s%s 5m");
        de_de.setProperty("alarm_hint", "- Time formats: d = days, h = hours, m = minutes\n" +
                "   \u200B⤷ Also, you can use as many time-arguments as you want\n" +
                "   \u200B⤷ Example: `%s%s \"Pat Servant\" 2d 12h 36m 1d`\n" +
                "   \u200B⤷ Time result: `2d` + `12h` + `36m` + `1d` = 3 days, 12 hours, 36 minutes\n" +
                "- Minutes are rounded. If you put in `1m`, you will be notifies on the **next** minute.\n" +
                "- Seconds are not supported.");
        de_de.setProperty("alarm_invalidtime", "Invalid Time.");
        de_de.setProperty("alarm_wrongargument", "Wrong argument");
        de_de.setProperty("alarm_messedupargs", "You messed up your arguments.");
        de_de.setProperty("alarm_alreadyset", "You already have an alarm at that time!");
        de_de.setProperty("alarm_remind", "Hi master, here is your requested alarm.");
        de_de.setProperty("alarm_invalidtitle", "Invalid Title: No ending quotation mark.");
        de_de.setProperty("alarm_titlelength", "Invalid Title Length: The title must not be longer than 256 characters.");
        de_de.setProperty("alarm_missingtime", "Missing Time: You have to add time arguments.");
        de_de.setProperty("alarm_toobig", "Invalid Time: The given value is too big.");

        /// Giveaway
        de_de.setProperty("giveaway_description", "- Start a giveaway that draws a given amount of people as winners after a given time.\n" +
                "- List all running giveaways of the current server.");
        de_de.setProperty("giveaway_usage", "- Start giveaway: `%sgiveaway \"[prize name]\" [amount of winners] [time]`\n" +
                "   \u200B⤷ Example: `%sgiveaway \"100 Cookies\" 1 12h`\n" +
                "- List giveaways: `%sgiveaway list`");
        de_de.setProperty("giveaway_hint", "- Time formats: d = days, h = hours, m = minutes\n" +
                "   \u200B⤷ Also, you can use as many time-arguments as you want\n" +
                "   \u200B⤷ Example: `%sgiveaway \"100 cookies\" 1 2d 12h 36m 1d`\n" +
                "   \u200B⤷ Time result: `2d` + `12h` + `36m` + `1d` = 3 days, 12 hours, 36 minutes\n" +
                "- Seconds are not supported");
        de_de.setProperty("giveaway_current", "Current giveaways on this server");
        de_de.setProperty("giveaway_days", "%s days");
        de_de.setProperty("giveaway_hours", "%s hours");
        de_de.setProperty("giveaway_minutes", "%s minutes");
        de_de.setProperty("giveaway_from", "Giveaway from %s!");
        de_de.setProperty("giveaway_endsat", "Ends at");
        de_de.setProperty("giveaway_endedat", "Ended at");
        de_de.setProperty("giveaway_messageid", "**Message ID:**");
        de_de.setProperty("giveaway_prize", "**Prize:**");
        de_de.setProperty("giveaway_noreactions", "Sorry, I can't find any reactions!");
        de_de.setProperty("giveaway_invalidtime", "Invalid Time.");
        de_de.setProperty("giveaway_wrongargument", "Wrong argument");
        de_de.setProperty("giveaway_nocurrent", "There are no giveaways running!");
        de_de.setProperty("giveaway_invalidwinneramount", "Invalid amount of winners. Only numbers!");
        de_de.setProperty("giveaway_messedupargs", "You messed up your arguments.");
        de_de.setProperty("giveaway_zerowinners", "That makes no sense. There has to be at least one winner.");
        de_de.setProperty("giveaway_emptyprize", "You cannot leave the prize empty.");
        de_de.setProperty("giveaway_description_running", "Prize: **%s**\n" +
                "Amount of winners: **%s**\n" +
                "Time remaining: **%s**\n" +
                "React with %s to enter the giveaway!");
        de_de.setProperty("giveaway_description_end", "Prize: **%s**\n" +
                "Amount of winners: **%s**\n\n" +
                "The winners are:\n" +
                "%s\n" +
                "Congratulations!");
        de_de.setProperty("giveaway_description_nowinner", "Prize: **%s**\n" +
                "Amount of winners: **%s**\n\n" +
                "Nobody participated. Therefore, nobody won.");

        /// Reminder
        de_de.setProperty("reminder_description", "Set up a reminder to a specific date and time.\n" +
                "The date and time will take your timezone into account.\n" +
                "If you didn't set one up, it will use UTC.\n" +
                "To set one up, use the `%suser` command.\n" +
                "You can add a topic about what you want to be reminded.");
        de_de.setProperty("reminder_usage", "Command: `%s%s yyyy-MM-dd HH:mm [topic]`\n" +
                "Example: `%s%s 2020-01-01 00:00 Happy New Year`");
        de_de.setProperty("reminder_hint", "You cannot set reminders in the past.");
        de_de.setProperty("reminder_missingargs", "Missing arguments! Please add a date and a time.");
        de_de.setProperty("reminder_past", "You cannot set up reminders in the past!");
        de_de.setProperty("reminder_invalidtopic", "Unsuitable topic");
        de_de.setProperty("reminder_success", "Added. Your message was removed for privacy.");
        de_de.setProperty("reminder_success_dm", "Added.");
        de_de.setProperty("reminder_fail", "You already have a reminder at this time.");
        de_de.setProperty("reminder_invalidinput", "Invalid input.");
        de_de.setProperty("reminder_remind_notopic", "Hi master, I should remind you at this time.");
        de_de.setProperty("reminder_remind_topic", "Hi master, I should remind you at this time about:\n**%s**");

        /// Signup
        de_de.setProperty("signup_description", "Let people sign up for an event (e.g. a raid).\n" +
                "The signup will be completed if either of the following happens:\n" +
                "1. The given amount of people have signed up (reacted).\n" +
                "2. The given expiring date (or the default one if not given) was reached (see last hint).\n" +
                "3. The signup creator clicked the :x: reaction.");
        de_de.setProperty("signup_usage", "Command: `%s%s \"Title\" [amount of people] [event date and time]`\n" +
                "Example 1: `%s%s \"Holy Grail War\" 10 2019-12-31 23:30`\n" +
                "Example 2: `%s%s 10 2019-12-31 23:30`\n" +
                "Example 3: `%s%s \"Holy Grail War\" 10`\n" +
                "Example 4: `%s%s 10`");
        de_de.setProperty("signup_hint", "- The amount of people that are allowed to sign up has to be within 1 and 100.\n" +
                "- The event date and time has to be within the next 4 weeks.\n" +
                "- If you don't set the event date and time, the signup will expire in 4 weeks.\n" +
                "- The event date and time will use the server's offset. You can check it via `!serverinfo` -> Servant Settings -> Offset\n" +
                "- If you set an event date, the signup will expire 30 minutes earlier, so you have time to organize the group.");
        de_de.setProperty("signup_invalidtitle", "Invalid Title: No ending quotation mark.");
        de_de.setProperty("signup_titlelength", "Invalid Title Length: The title must not be longer than 256 characters.");
        de_de.setProperty("signup_invalidamount", "Invalid Amount: No amount of participants was found.");
        de_de.setProperty("signup_invalidamountrange", "Invalid Amount Size: The amount has to be within 1 and 100 participants.");
        de_de.setProperty("signup_invalidamountparse", "Invalid Amount Parse: %s");
        de_de.setProperty("signup_missingamount", "Missing Amount: You have to add an amount of allowed participants.");
        de_de.setProperty("signup_invaliddate", "Invalid Date and Time");
        de_de.setProperty("signup_invaliddatedistance", "Invalid Date and Time: The given date and time must not be farther away than 4 weeks and it must not be in the past.\n" +
                "Keep in mind the custom event date will be processed 30 min earlier, so the next 30 minutes will be considered to be the \"past\".");
        de_de.setProperty("signup_invaliddateday", "Invalid Date and Time: The given date does not exist. (e.g. Feb 31)");
        de_de.setProperty("signup_embedtitle_empty", "Sign up");
        de_de.setProperty("signup_embedtitle_notempty", "Sign up for %s");
        de_de.setProperty("signup_embeddescription", "%s people can participate!\n" +
                "%s" +
                "\n" +
                "Click on %s to participate.\n" +
                "Remove said reaction if you have changed your mind.");
        de_de.setProperty("signup_embeddescription_custom", "The signup will close 30 minutes prior to the scheduled event.\n");
        de_de.setProperty("signup_embeddescriptionend", "%s people could participate!\n\n" +
                "These are the participants:");
        de_de.setProperty("signup_nobody", "Nobody signed up");
        de_de.setProperty("signup_timeout", "Times out at");
        de_de.setProperty("signup_event", "Event at");
        de_de.setProperty("signup_timeout_finish", "Ended at");

        /// Timezone
        de_de.setProperty("timezone_description", "Convert a date and time from one timezone to another");
        de_de.setProperty("timezone_usage", "Command: `%s%s yyyy-MM-dd HH:mm [current timezone] [target timezone]`\n" +
                "Example: `%s%s 2019-01-01 22:00 PST CET`");
        de_de.setProperty("timezone_hint", "This command uses the 24 hour system.");
        de_de.setProperty("timezone_missingargs", "Missing arguments.");
        de_de.setProperty("timezone_conversion", "Timezone Conversion");
        de_de.setProperty("timezone_input", "Input");
        de_de.setProperty("timezone_output", "Output");
        de_de.setProperty("timezone_invalidzone_start", "Invalid starting timezone.");
        de_de.setProperty("timezone_invalidzone_target", "Invalid target timezone.");
        de_de.setProperty("timezone_invalid", "Invalid input. Check your formatting.");

        // Votes
        de_de.setProperty("votes_active", "Endet am");
        de_de.setProperty("votes_inactive", "This poll has ended.");

        /// Quickvote
        de_de.setProperty("quickvote_started", "%s started a quickpoll!");
        de_de.setProperty("quickvote_ended", "%s has ended the quickpoll!");
        de_de.setProperty("quickvote_missing_db", "Greetings mediator! I couldn't remove a succesful quickvote ending from the database.");

        /// Vote
        de_de.setProperty("vote_description", "Create a poll with up to 10 custom answers.");
        de_de.setProperty("vote_usage", "Command: `%s%s [question]/[answer1]/(...)/[answer10]`\n" +
                "Example: `%s%s When do you have time?/Mon/Tue/Wed/Thu/Fri/Sat/Sun`");
        de_de.setProperty("vote_hint", "After executing this command, you will be asked if you want to allow multiple answers.");
        de_de.setProperty("vote_amount", "Invalid amount of arguments. There has to be at least one answer to your question and a maximum of 10 answers.");
        de_de.setProperty("vote_timeout", "Timeout! You didn't react on my question.");
        de_de.setProperty("vote_started", "%s started a poll!");
        de_de.setProperty("vote_ended", "%s has ended the poll!");
        de_de.setProperty("vote_missing_db", "Greetings mediator! I couldn't remove a succesful vote ending from the database.");
        de_de.setProperty("vote_multiple", "You can select multiple answers.");
        de_de.setProperty("vote_single", "You can only pick one answer.");

        // Fun
        /// Avatar
        de_de.setProperty("avatar_description", "Steal someone's avatar.");
        de_de.setProperty("avatar_usage", "Command: `%s%s @user`\n");
        de_de.setProperty("avatar_stolen", "%s just stole %s's avatar!");

        /// Baguette
        de_de.setProperty("baguette_49", "Unlucky");
        de_de.setProperty("baguette_50", "JACKPOT! Now you're cool.");

        /// Bio
        de_de.setProperty("bio_maxlength", "Bio max length is 50.");

        /// Coin Flip
        de_de.setProperty("coinflip_head", "Head!");
        de_de.setProperty("coinflip_tail", "Tail!");

        /// Embed (for both create and edit)
        de_de.setProperty("embed_timeout", "This configuration timed out.");
        de_de.setProperty("embed_authorline_q", "Alright! Do you want to use an author line?");
        de_de.setProperty("embed_authorname_i", "Please provide the **author name**:");
        de_de.setProperty("embed_authorurl_q", "Do you want to use an **author url** (not the icon)?");
        de_de.setProperty("embed_authorurl_i", "Please provide the **author url** (not the icon url!):");
        de_de.setProperty("embed_authorurl_i_fail", "Your input is invalid. Please provide a valid url:");
        de_de.setProperty("embed_authoricon_q", "Do you want to use an **author icon**?");
        de_de.setProperty("embed_authoricon_i", "Please provide the **author icon url** (direct link!):");
        de_de.setProperty("embed_authoricon_i_fail", "Your input is invalid. Please provide a valid direct url:");
        de_de.setProperty("embed_thumbnail_q", "Do you want to use a thumbnail?");
        de_de.setProperty("embed_thumbnail_i", "Please provide the **thumbnail url** (direct link!):");
        de_de.setProperty("embed_thumbnail_i_fail", "Your input is invalid. Please provide a valid direct url:");
        de_de.setProperty("embed_title_q", "Do you want to use a title?");
        de_de.setProperty("embed_title_i", "Please provide the **title**:");
        de_de.setProperty("embed_url_q", "Do you want to use a title URL?");
        de_de.setProperty("embed_url_i", "Please provide the **title url**:");
        de_de.setProperty("embed_url_i_fail", "Your input is invalid. Please provide a valid url:");
        de_de.setProperty("embed_description_q", "Do you want to use a description?");
        de_de.setProperty("embed_description_i", "Please provide the **description**:");
        de_de.setProperty("embed_field_q", "Do you want to add a field?");
        de_de.setProperty("embed_field_name_i", "Please provide the **field name**:");
        de_de.setProperty("embed_field_value_i", "Please provide the **field value**:");
        de_de.setProperty("embed_field_inline_i", "Should this field be inline?");
        de_de.setProperty("embed_image_q", "Do you want to use an image?");
        de_de.setProperty("embed_image_i", "Please provide the **image url** (direct link!):");
        de_de.setProperty("embed_image_i_fail", "Your input is invalid. Please provide a valid direct url:");
        de_de.setProperty("embed_footer_q", "Do you want to use a footer?");
        de_de.setProperty("embed_footer_text_i", "Please provide the **footer text**:");
        de_de.setProperty("embed_footer_icon_q", "Do you want to use a footer icon?");
        de_de.setProperty("embed_footer_icon_i", "Please provide the **footer icon url** (direct link!):");
        de_de.setProperty("embed_footer_icon_i_fail", "Your input is invalid. Please provide a valid direct url:");
        de_de.setProperty("embed_empty", "Either the embed is empty or it has over 6000 characters.\nBoth is not allowed!");
        de_de.setProperty("embed_timestamp_q", "Do you want to use a timestamp?");
        de_de.setProperty("embed_timestamp_i", "Please provide a timestamp.\nYou either can say `now` or provide a date and time like this: `yyyy-MM-dd HH:mm` (uses server timezone):");
        de_de.setProperty("embed_timestamp_i_fail", "Your input is invalid.\nYou either can say `now` or provide a date and time like this: `yyyy-MM-dd HH:mm` (uses server timezone).\nTry again:");

        //// Create Embed
        de_de.setProperty("createembed_introduction", "With this command, you can create your own embed.\n" +
                "- You cannot create an empty embed.\n" +
                "- The embed but not be longer than 6000 characters in total.\n" +
                "- Everytime you have to click a reacton or write an answer, you have a time limit of 15 minutes.\n" +
                "Are you prepared?");
        de_de.setProperty("createembed_author_name", "Author name (can point to URL)");
        de_de.setProperty("createembed_title", "Title (can point to URL)");
        de_de.setProperty("createembed_description", "Description\n" +
                "The title will be white if it's not a hyperlink.\n" +
                "Any image URL's have to be direct links.\n" +
                "The timestamp is not part of the footer text, but a standalone date and time.");
        de_de.setProperty("createembed_field_name_inline", "Inline field name"); // dis
        de_de.setProperty("createembed_field_value1", "Field value");
        de_de.setProperty("createembed_field_value2", "Up to 3 in a line.");
        de_de.setProperty("createembed_field_value3", "You can have up to 25 fields.");
        de_de.setProperty("createembed_field_name_noninline", "Non-inline field name");
        de_de.setProperty("createembed_field_value_noninline", "Non-inline fields take the while width of the embed.");
        de_de.setProperty("createembed_footer", "Footer text");
        de_de.setProperty("createembed_done", "We're done! Please mention a text channel to post this embed in (e.g. #channel):");
        de_de.setProperty("createembed_done_repeated", "Invalid input, you have to **mention** a channel (e.g. #channel):");

        //// Edit Embed
        de_de.setProperty("editembed_description", "Edit an embed that was made by %s.");
        de_de.setProperty("editembed_usage", "Command: `%s%s #channel [message ID of the embed]`\n" +
                "Example: `%s%s #info 999999999999999999`");
        de_de.setProperty("editembed_hint", "**How to get ID's:**\n" +
                "1. Activate Discord Developer Mode: User Settings → Appearance → ADVANCED → Developer Mode\n" +
                "2. Hover over message → Menu buttons at the right → Copy ID");
        de_de.setProperty("editembed_missing_channel", "You have to mention a channel. Check `%seditembed` for help.`");
        de_de.setProperty("editembed_missing_message_id", "You have to provide a message ID. Check `%seditembed for help.`");
        de_de.setProperty("editembed_invalid_message_id", "The provided message ID is invalid. Check `%seditembed for help.`");
        de_de.setProperty("editembed_notbyme", "This is not a message made by me.");
        de_de.setProperty("editembed_noembed", "I cannot find an embed for this message.");
        de_de.setProperty("editembed_introduction", "With this command, you can edit an embed from %s.\n" +
                "- You cannot create an empty embed.\n" +
                "- The embed but not be longer than 6000 characters in total.\n" +
                "- Everytime you have to click a reacton or write an answer, you have a time limit of 15 minutes.\n" +
                "Are you prepared?");
        de_de.setProperty("editembed_confirm", "We're done! Please confirm you want to overwrite the old embed.");
        de_de.setProperty("editembed_notfound", "I cannot find this message, master!");
        de_de.setProperty("editembed_missingpermission", "You need to be able to write in the given channel to access embeds.");
        de_de.setProperty("embed_field_remove_q", "Do you want to remove the current fields?");

        /// Flip
        de_de.setProperty("flip_description", "Flip a user.");
        de_de.setProperty("flip_usage", "Command: `%s%s @user`");
        de_de.setProperty("flip_hint", "Check out `%sunflip @user`");

        /// Level
        de_de.setProperty("level_leaderboard_empty", "Leaderboard is empty");
        de_de.setProperty("level_leaderboard_authorname", "%s Leaderboard");
        de_de.setProperty("level_leaderboard_footer", "Also try \"%slevel\" and \"%slevel @user\"");
        de_de.setProperty("level_level", "Level");
        de_de.setProperty("level_rank", "Rank");
        de_de.setProperty("level_up", "%s just reached level %s! \uD83C\uDF89");
        de_de.setProperty("level_footer", "Also try %s and \"%slevel leaderboard\"!");
        de_de.setProperty("level_missingpermission_embed", "I don't have the permission to create embeds (Embed Links), that's why you see the budget level up announcement.");
        de_de.setProperty("level_hierarchy", "Couldn't add role \"%s\", because they are higher than me in hierarchy.");

        /// Love
        de_de.setProperty("love_description","Ship two people or just one with themselves.");
        de_de.setProperty("love_usage", "Command: `%s%s @user1 @user2`\n" +
                "Command: `%s%s @user`");
        de_de.setProperty("love_hint", "You only need to mention a person once, if you want to ship them with themselves.");
        de_de.setProperty("love_self_100", "Damn! Straight to the fap!");
        de_de.setProperty("love_self_90", "Pretty self confident, don't you think?");
        de_de.setProperty("love_self_80", "So narcissistic...");
        de_de.setProperty("love_self_70", "You love yourself more than others love you.");
        de_de.setProperty("love_self_69", "Nice.");
        de_de.setProperty("love_self_60", "Seems like you are accepting yourself.");
        de_de.setProperty("love_self_50", "You seem to be undecided if you like yourself or not.");
        de_de.setProperty("love_self_42", "You found the answer.");
        de_de.setProperty("love_self_40", "Now, you can look into the mirror with pride.");
        de_de.setProperty("love_self_30", "A bit unsecure, but I'm sure you can handle it.");
        de_de.setProperty("love_self_20", "You are doing great. Build some self confidence!");
        de_de.setProperty("love_self_10", "Believe in yourself!");
        de_de.setProperty("love_self_0", "Thats tough. We still love you <3");
        de_de.setProperty("love_noself_100", "Damn! Thats a match!");
        de_de.setProperty("love_noself_90", "Get up and invite them for a dinner.");
        de_de.setProperty("love_noself_80", "You sure, you don't wanna date?");
        de_de.setProperty("love_noself_70", "I call a sis-/bromance.");
        de_de.setProperty("love_noself_69", "Nice.");
        de_de.setProperty("love_noself_60", "There is a chance.");
        de_de.setProperty("love_noself_50", "I bet you can be friends. :)");
        de_de.setProperty("love_noself_42", "You found the answer.");
        de_de.setProperty("love_noself_40", "At least you are trying.");
        de_de.setProperty("love_noself_30", "I think this won't work out.");
        de_de.setProperty("love_noself_20", "At least a bit, amirite.");
        de_de.setProperty("love_noself_10", "Dats pretty low, tho.");
        de_de.setProperty("love_noself_0", "Well, that won't work out.");
        de_de.setProperty("love_fallback", "Urgh!");

        /// Profile
        de_de.setProperty("profile_noachievements", "No achievements");
        de_de.setProperty("profile_nocommands", "No commands were used yet");
        de_de.setProperty("profile_level", "Level");
        de_de.setProperty("profile_rank",  "Rank #%s");
        de_de.setProperty("profile_mostused", "Most used commands");
        de_de.setProperty("profile_achievements", "Achievements");
        de_de.setProperty("profile_footer1", "Also try \"%s%s @user\"");
        de_de.setProperty("profile_footer2", "Also try \"%s%s\"");
        de_de.setProperty("profile_baguettecounter", "Baguette Statistics");
        de_de.setProperty("profile_nobaguette", "No baguette yet");
        de_de.setProperty("profile_baguette", "Biggest baguette: %s (%s times)");
        de_de.setProperty("profile_total_muc", "Total commands used");
        de_de.setProperty("profile_total_ap", "Total AP");

        // Random
        de_de.setProperty("random_empty", "Ich konnte unter diesem Suchwort nichts finden.");

        /// Unflip
        de_de.setProperty("unflip_description", "Unlip a user.");
        de_de.setProperty("unflip_usage", "Command: `%s%s @user`");
        de_de.setProperty("unflip_hint", "Check out `%sflip @user`");

        // Interaction
        de_de.setProperty("interaction_description", "Interaction commands are like reactions, but way better.\n" +
                "Share your feelings or cookies with other people.");
        de_de.setProperty("interaction_usage", "**%s%s someone**\n" +
                "Command: `%s%s [@user]`\n" +
                "Example: `%s%s @Servant`");
        de_de.setProperty("interaction_usage_on", "**%s%s on someone**\n" +
                "Command: `%s%s [@user]`\n" +
                "Example: `%s%s @Servant`");

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
