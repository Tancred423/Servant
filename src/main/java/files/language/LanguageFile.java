// Author: Tancred423 (https://github.com/Tancred423)
package files.language;

import nu.studer.java.util.OrderedProperties;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LanguageFile {
    static void createDefaultEN_GB() throws IOException {
        var en_gb = new OrderedProperties();
        var os = new FileOutputStream(System.getProperty("user.dir") + "/resources/lang/en_gb.ini");

        ///////////////////////////////////// Global Arguments /////////////////////////////////////

        var valid_args = "Valid arguments:\n" +
                "- Days: days, day, d\n" +
                "- Hours: hours, hour, h\n" +
                "- Minutes: minutes, minute, min, m";

        ///////////////////////////////////// General /////////////////////////////////////

        en_gb.setProperty("no_ending_quotation_mark", "No ending quotation mark.");
        en_gb.setProperty("topic_too_long", "Topic is too long.");
        en_gb.setProperty("missing_time_unit", "Time or time unit not provided in time argument.");
        en_gb.setProperty("invalid_char", "Invalid character: '%s'");
        en_gb.setProperty("jump", "Jump");
        en_gb.setProperty("tmp_disabled", "This command has been disabled temporarily due to a bug.");
        en_gb.setProperty("cooldown_warning", "That command is on cooldown for %s more seconds");
        en_gb.setProperty("needed_role", "You must have a role called `%s` to use that!");
        en_gb.setProperty("invalid_channel", "That command cannot be used in this channel!");
        en_gb.setProperty("hastobe_vc", "You must be in a voice channel to use that!");
        en_gb.setProperty("permission_bot", "%s I need the %s permission in this %s!");
        en_gb.setProperty("permission_user", "%s You must have the %s permission in this %s to use that!");
        en_gb.setProperty("forbidden_dm", "This command cannot be used in direct messages.");
        en_gb.setProperty("permission", ":x: You must have the %s permission in this server to use that!");
        en_gb.setProperty("invalid_mention", "Invalid mention.");
        en_gb.setProperty("current_prefix", "Current prefix: %s");
        en_gb.setProperty("unknown_message", "Unknown message.");
        en_gb.setProperty("apostrophe", "'");
        en_gb.setProperty("apostrophe_s", "'s");
        en_gb.setProperty("times", "times");
        en_gb.setProperty("ap", "Achievement Points");
        en_gb.setProperty("commands_used", "Commands Used");
        en_gb.setProperty("commands", "Commands");
        en_gb.setProperty("missing_args", "Missing arguments");
        en_gb.setProperty("missing_time_args", "Missing time arguments");
        en_gb.setProperty("member", "Member");
        en_gb.setProperty("role_s", "Role(s)");
        en_gb.setProperty("author", "Author");
        en_gb.setProperty("msg_id", "Message ID");
        en_gb.setProperty("category_id", "Category ID");
        en_gb.setProperty("emote_id", "Emote ID");
        en_gb.setProperty("emote", "Emote");
        en_gb.setProperty("invite", "Invite");
        en_gb.setProperty("user", "User");
        en_gb.setProperty("user_id", "User ID");
        en_gb.setProperty("vc", "Voice Channel");
        en_gb.setProperty("vc_id", "Voice Channel ID");
        en_gb.setProperty("role_id", "Role ID");
        en_gb.setProperty("tc_id", "Text Channel ID");
        en_gb.setProperty("general_error", "Something went wrong! The bot dev will take care of that ASAP.");
        en_gb.setProperty("description", "Description");
        en_gb.setProperty("usage", "Usage");
        en_gb.setProperty("alias", "Alias");
        en_gb.setProperty("aliases", "Aliases");
        en_gb.setProperty("hint", "Hint");
        en_gb.setProperty("channel", "Channel");
        en_gb.setProperty("guild", "Server");
        en_gb.setProperty("help", "Help");

        // Parser
        en_gb.setProperty("parser_invalid_time_arg", "Invalid time argument. Or maybe did you forget the quotation marks around your topic?");

        ///////////////////////////////////// Commands /////////////////////////////////////

        ////////////////// Dashboard //////////////////

        // Dashboard
        en_gb.setProperty("dashboard_link", "The dashboard can be found here: <%s>");
        en_gb.setProperty("dashboard_discontinued", "This command is discontinued.\n" +
                "This is because you can now do everything this command did in the dashboard: <%s>");
        en_gb.setProperty("dashboard_birthday_settings", "The birthday settings are now on the dashboard!");

        // Discontinued
        en_gb.setProperty("discontinued", "This command is discontinued.");

        // Leaderboard
        en_gb.setProperty("leaderboard_website", "The leaderboard can now be found here: <https://servant.gg/leaderboard/%s>");

        ////////////////// Fun //////////////////

        // Achievements
        en_gb.setProperty("achievements_title", "%s%s Achievements");

        // Avatar
        en_gb.setProperty("avatar_avatar", "Avatar");
        en_gb.setProperty("avatar_self", "%s just stole their own avatar!");
        en_gb.setProperty("avatar_stolen", "%s just stole %s's avatar!");

        // Baguette
        en_gb.setProperty("baguette_49", "Unlucky");
        en_gb.setProperty("baguette_50", "JACKPOT! Now you're cool.");

        // BubbleWrap
        en_gb.setProperty("bubblewrap_title", "Bubble Wrap!");
        en_gb.setProperty("bubblewrap_subtitle", "POP POP POP!");
        en_gb.setProperty("bubblewrap_footer", "Knick knickediknack, uh yeah!");

        // Coinflip
        en_gb.setProperty("coinflip_head", "Head!");
        en_gb.setProperty("coinflip_tail", "Tail!");

        // Commands
        en_gb.setProperty("commands_title", "%s%s commands");
        en_gb.setProperty("commands_footer", "If you've used this command in hope to see a command list, visit the \"Help\" section on the website: %s");

        // Flip
        en_gb.setProperty("flip_description", "Flip a user");
        en_gb.setProperty("flip_usage", "**Flip a user**\n" +
                "Command: `%s%s @user`\n" +
                "\n" +
                "**Unflip a user**\n" +
                "Command: `%s%s @user`");

        // Love
        en_gb.setProperty("love_description", "Ship two people, or just one with themselves.");
        en_gb.setProperty("love_usage", "**Ship yourself with someone**\n" +
                "Command: `%s%s @user`\n" +
                "\n" +
                "**Ship two users**\n" +
                "Command: `%s%s @user1 @user2`");

        en_gb.setProperty("love_self_100", "Damn! Straight to the fap!");
        en_gb.setProperty("love_self_90", "Pretty self confident, don't you think?");
        en_gb.setProperty("love_self_80", "So narcissistic...");
        en_gb.setProperty("love_self_70", "You love yourself more than others love you.");
        en_gb.setProperty("love_self_69", "Nice.");
        en_gb.setProperty("love_self_60", "Seems like you are accepting yourself.");
        en_gb.setProperty("love_self_50", "You seem to be undecided if you like yourself or not.");
        en_gb.setProperty("love_self_42", "You found the answer.");
        en_gb.setProperty("love_self_40", "Now you can look into the mirror with pride.");
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
        en_gb.setProperty("love_noself_30", "I don't think this will work out.");
        en_gb.setProperty("love_noself_20", "At least a bit, amirite.");
        en_gb.setProperty("love_noself_10", "Dats pretty low, tho.");
        en_gb.setProperty("love_noself_0", "Well, that won't work out.");

        en_gb.setProperty("love_fallback", "Urgh!");

        // Profile
        en_gb.setProperty("profile_footer1", "Also try \"%s%s @user\"");
        en_gb.setProperty("profile_footer2", "Also try \"%s%s\"");

        en_gb.setProperty("profile_generalinfo", "General info");
        en_gb.setProperty("profile_commandstats", "Command stats");
        en_gb.setProperty("profile_mostused", "Most used commands");
        en_gb.setProperty("profile_nocommands", "No commands were used yet");
        en_gb.setProperty("profile_achievements", "Achievements");
        en_gb.setProperty("profile_noachievements", "No achievements");

        en_gb.setProperty("profile_level", "Level");
        en_gb.setProperty("profile_rank",  "Rank");

        en_gb.setProperty("profile_baguette", "Biggest Baguette");
        en_gb.setProperty("profile_nobaguette", "No baguette yet");
        en_gb.setProperty("profile_baguette_value", "%s (%s times)");
        en_gb.setProperty("profile_animal", "Favourite Animal");
        en_gb.setProperty("profile_nofavourite", "No Favourite");
        en_gb.setProperty("profile_msi", "Most Shared Interaction");
        en_gb.setProperty("profile_mri", "Most Received Interaction");

        en_gb.setProperty("profile_animal_bird", "Bird");
        en_gb.setProperty("profile_animal_cat", "Cat");
        en_gb.setProperty("profile_animal_dog", "Dog");
        en_gb.setProperty("profile_animal_fennec", "Fennec");
        en_gb.setProperty("profile_animal_fox", "Fox");
        en_gb.setProperty("profile_animal_frog", "Frog");
        en_gb.setProperty("profile_animal_koala", "Koala");
        en_gb.setProperty("profile_animal_panda", "Panda");
        en_gb.setProperty("profile_animal_redpanda", "RedPanda");
        en_gb.setProperty("profile_animal_sloth", "Sloth");
        en_gb.setProperty("profile_animal_wolf", "Wolf");

        en_gb.setProperty("profile_title_creator", "Creator");
        en_gb.setProperty("profile_title_supporter", "Supporter");
        en_gb.setProperty("profile_title_normal", "Master");

        ////////////////// Interaction //////////////////

        en_gb.setProperty("interaction_description_beg", "Beg someone");
        en_gb.setProperty("interaction_description_birthday", "Wish someone a happy birthday");
        en_gb.setProperty("interaction_description_bite", "Bite someone");
        en_gb.setProperty("interaction_description_bully", "Bully someone");
        en_gb.setProperty("interaction_description_cheers", "Raise a toast to someone");
        en_gb.setProperty("interaction_description_cookie", "Give someone a cookie");
        en_gb.setProperty("interaction_description_cop", "Arrest someone");
        en_gb.setProperty("interaction_description_dab", "Dab on someone");
        en_gb.setProperty("interaction_description_flex", "Flex on someone");
        en_gb.setProperty("interaction_description_highfive", "Give someone a high five");
        en_gb.setProperty("interaction_description_hug", "Hug someone");
        en_gb.setProperty("interaction_description_kiss", "Kiss someone");
        en_gb.setProperty("interaction_description_lick", "Lick someone");
        en_gb.setProperty("interaction_description_pat", "Pat someone");
        en_gb.setProperty("interaction_description_poke", "Poke someone");
        en_gb.setProperty("interaction_description_shame", "Shame \uD83D\uDD14 Shame \uD83D\uDD14 Shame \uD83D\uDD14");
        en_gb.setProperty("interaction_usage_shame", "Shame someone");
        en_gb.setProperty("interaction_description_slap", "Slap someone");
        en_gb.setProperty("interaction_description_wave", "Wave to someone");
        en_gb.setProperty("interaction_description_wink", "Wink to someone");
        en_gb.setProperty("interaction_usage", "**%s**\n" +
                "Command: `%s%s [@user]`\n" +
                "Example: `%s%s @Servant`");

        en_gb.setProperty("interaction_shared_beg", "%s begged %s generous people.");
        en_gb.setProperty("interaction_received_beg", "%s was begged by %s filthy leechers.");
        en_gb.setProperty("interaction_shared_birthday", "%s congratulated %s times to a birthday.");
        en_gb.setProperty("interaction_received_birthday", "%s got congratulated on their birthday %s times.");
        en_gb.setProperty("interaction_shared_bite", "%s bit %s people. And all of them deserved it! ò.ó");
        en_gb.setProperty("interaction_received_bite", "%s got bitten by %s crazy people.");
        en_gb.setProperty("interaction_shared_bully", "%s bullied %s poor kids.");
        en_gb.setProperty("interaction_received_bully", "%s got bullied by %s meanies.");
        en_gb.setProperty("interaction_shared_cheers", "%s raised a toast to %s people.");
        en_gb.setProperty("interaction_received_cheers", "%s was toasted by %s people. \uD83C\uDF5E");
        en_gb.setProperty("interaction_shared_cookie", "%s gave a cookie to %s cuties.");
        en_gb.setProperty("interaction_received_cookie", "%s noms their %s. cookie.");
        en_gb.setProperty("interaction_shared_cop", "%s arrested %s loli hunters.");
        en_gb.setProperty("interaction_received_cop", "%s was arrested by %s FBI agents.");
        en_gb.setProperty("interaction_shared_dab", "%s d4bb3d 0n %s n00b5.");
        en_gb.setProperty("interaction_received_dab", "%s sneered at %s Fortnite kiddies.");
        en_gb.setProperty("interaction_shared_flex", "%s flexed on %s weaklings.");
        en_gb.setProperty("interaction_received_flex", "%s got flexed on by %s wanna-be body builders.");
        en_gb.setProperty("interaction_shared_highfive", "%s clapped %s raised hands.");
        en_gb.setProperty("interaction_received_highfive", "%s tried to wave %s times, but got clapped by some weird dude.");
        en_gb.setProperty("interaction_shared_hug", "%s hugged %s comfy peeps.");
        en_gb.setProperty("interaction_received_hug", "%s got hugged by %s cuties.");
        en_gb.setProperty("interaction_shared_kiss", "%s kissed %s random people.");
        en_gb.setProperty("interaction_received_kiss", "%s received %s kisses yet.");
        en_gb.setProperty("interaction_shared_lick", "%s licked %s strangers.");
        en_gb.setProperty("interaction_received_lick", "%s washed their face %s times.");
        en_gb.setProperty("interaction_shared_pat", "%s patted %s catgirls. uwu");
        en_gb.setProperty("interaction_received_pat", "%s tried to explain %s times that they aren't a catgirl.");
        en_gb.setProperty("interaction_shared_poke", "%s poked %s people. (｀ω´)");
        en_gb.setProperty("interaction_received_poke", "%s got annoyed %s times.");
        en_gb.setProperty("interaction_shared_slap", "%s smacked %s meanies.");
        en_gb.setProperty("interaction_received_slap", "%s got bullied %s times... oh wait, wrong command (◯Δ◯∥)");
        en_gb.setProperty("interaction_shared_shame", "%s lead %s sinners through the walk of atonement.");
        en_gb.setProperty("interaction_received_shame", "%s cosplayed Cersei %s times.");
        en_gb.setProperty("interaction_shared_wave", "%s greeted %s lovely derps.");
        en_gb.setProperty("interaction_received_wave", "%s `✓✓Read` %s times.");
        en_gb.setProperty("interaction_shared_wink", "%s winked to %s people.");
        en_gb.setProperty("interaction_received_wink", "%s blushed %s times.");

        ////////////////// Moderation //////////////////

        // Clear
        en_gb.setProperty("clear_description", "Deletes up to 100 messages\n" +
                "Can delete user specific messages from the past 100 messages");
        en_gb.setProperty("clear_usage", "**Delete some messages**\n" +
                "Command: `%s%s [1 - 100 OR @user]`\n" +
                "Example 1: `%s%s 50`\n" +
                "Example 2: `%s%s @Servant`");
        en_gb.setProperty("clear_hint", "The range is inclusively, so you can also delete just 1 or a total of 100 messages.");

        en_gb.setProperty("clear_input", "You only can put in numbers or a user mention!");
        en_gb.setProperty("clear_invalid", "Your input is invalid. Try a smaller number.");
        en_gb.setProperty("clear_sub_one", "Input cannot be lower than 1.");
        en_gb.setProperty("clear_cleared", "%s messages cleared");

        // EditEmbed
        en_gb.setProperty("editembed_description", "Edit an embed message from %s");
        en_gb.setProperty("editembed_usage", "**Edit an embed**\n" +
                "Command: `%s%s [message link]`\n" +
                "Example: `%s%s %s`");
        en_gb.setProperty("editembed_hint", "**How to get the message link:**\n" +
                "- Desktop: Hover over message > Use the 3 dots menu > Copy Message Link\n" +
                "- Android: Press and hold message > Share > Copy\n" +
                "- iOS: Press and hold message > Copy Message Link");

        en_gb.setProperty("editembed_args_length", "Please provide a valid message link. Type `%seditembed` to see all information needed.");
        en_gb.setProperty("editembed_tc_not_found", "The given text channel cannot be found.");
        en_gb.setProperty("editembed_invalid_message_id", "The provided message ID is invalid. Check `%seditembed` for help.");
        en_gb.setProperty("editembed_missingpermission", "You need to be able to write in the given channel to access embeds.");
        en_gb.setProperty("editembed_notbyme", "This is not a message made by me.");
        en_gb.setProperty("editembed_noembed", "I cannot find an embed for this message.");
        en_gb.setProperty("editembed_notfound", "I cannot find this message.");
        en_gb.setProperty("embed_timeout", "This configuration timed out.");
        en_gb.setProperty("editembed_introduction", "With this command, you can edit an embed from %s.\n" +
                "- The embed must not be longer than 6000 characters in total.\n" +
                "- Everytime you have to click a reacton or write an answer, you have a time limit of 15 minutes.\n" +
                "Are you prepared?");
        en_gb.setProperty("embed_field_remove_q", "Do you want to remove the current fields?");
        en_gb.setProperty("embed_authorline_q", "Alright! Do you want to use an **author line**?");
        en_gb.setProperty("embed_authorname_i", "Please provide the **author name**:");
        en_gb.setProperty("embed_authorurl_q", "Do you want to use an **author url** (not the icon)?");
        en_gb.setProperty("embed_authorurl_i", "Please provide the **author url** (not the icon url!):");
        en_gb.setProperty("embed_authorurl_i_fail", "Your input is invalid. Please provide a valid url:");
        en_gb.setProperty("embed_authoricon_q", "Do you want to use an **author icon**?");
        en_gb.setProperty("embed_authoricon_i", "Please provide the **author icon url** (direct link!):");
        en_gb.setProperty("embed_authoricon_i_fail", "Your input is invalid. Please provide a valid direct url:");
        en_gb.setProperty("embed_thumbnail_q", "Do you want to use a **thumbnail**?");
        en_gb.setProperty("embed_thumbnail_i", "Please provide the **thumbnail url** (direct link!):");
        en_gb.setProperty("embed_thumbnail_i_fail", "Your input is invalid. Please provide a valid direct url:");
        en_gb.setProperty("embed_title_q", "Do you want to use a **title**?");
        en_gb.setProperty("embed_title_i", "Please provide the **title**:");
        en_gb.setProperty("embed_url_q", "Do you want to use a **title URL**?");
        en_gb.setProperty("embed_url_i", "Please provide the **title url**:");
        en_gb.setProperty("embed_url_i_fail", "Your input is invalid. Please provide a valid url:");
        en_gb.setProperty("embed_description_q", "Do you want to use a **description**?");
        en_gb.setProperty("embed_description_i", "Please provide the **description**:");
        en_gb.setProperty("embed_field_q", "Do you want to add a **field**?");
        en_gb.setProperty("embed_field_name_i", "Please provide the **field name**:");
        en_gb.setProperty("embed_field_value_i", "Please provide the **field value**:");
        en_gb.setProperty("embed_field_inline_i", "Should this field be inline?");
        en_gb.setProperty("embed_image_q", "Do you want to use an **image**?");
        en_gb.setProperty("embed_image_i", "Please provide the **image url** (direct link!):");
        en_gb.setProperty("embed_image_i_fail", "Your input is invalid. Please provide a valid direct url:");
        en_gb.setProperty("embed_footer_q", "Do you want to use a **footer**?");
        en_gb.setProperty("embed_footer_text_i", "Please provide the **footer text**:");
        en_gb.setProperty("embed_footer_icon_q", "Do you want to use a **footer icon**?");
        en_gb.setProperty("embed_footer_icon_i", "Please provide the **footer icon url** (direct link!):");
        en_gb.setProperty("embed_footer_icon_i_fail", "Your input is invalid. Please provide a valid direct url:");
        en_gb.setProperty("embed_empty", "Either the embed is empty or it has over 6000 characters.\nBoth is not allowed!");
        en_gb.setProperty("embed_timestamp_q", "Do you want to use a **timestamp**?");
        en_gb.setProperty("embed_timestamp_i", "Please provide a **timestamp**.\nYou either can say `now` or provide a date and time like this: `yyyy-MM-dd HH:mm` (uses server offset):");
        en_gb.setProperty("embed_timestamp_i_fail", "Your input is invalid.\nYou either can say `now` or provide a date and time like this: `yyyy-MM-dd HH:mm` (uses server offset).\nTry again:");
        en_gb.setProperty("editembed_confirm", "We're done! Please confirm you want to overwrite the old embed.");

        ////////////////// Owner //////////////////

        // Blacklist
        en_gb.setProperty("blacklist_missingid", "Add an ID");
        en_gb.setProperty("blacklist_empty", "No blacklisted ID's.");

        // Thread
        en_gb.setProperty("thread_title", "Stats for Nerds");
        en_gb.setProperty("thread_stats_k", "General Stats");
        en_gb.setProperty("thread_stats_v", "Available Processors: %s\nCurrent Threads: %s");
        en_gb.setProperty("thread_fixed_k", "Fixed Thread Pool");
        en_gb.setProperty("thread_cached_k", "Cached Thread Pool");
        en_gb.setProperty("thread_scheduled_k", "Scheduled Service");
        en_gb.setProperty("thread_period_k", "Period Service");
        en_gb.setProperty("thread_pool_service_v", "Is Running: %s\nPool Size: %s\nActive Threads: %s\nQueued Tasks: %s\nCompleted Tasks: %s");
        en_gb.setProperty("thread_database_k", "Database");
        en_gb.setProperty("thread_database_v", "Active Connections: %s\nTotal Connections: %s\nMaximum Pool Size: %s");
        en_gb.setProperty("thread_msg_cache_k", "Message Cache");
        en_gb.setProperty("thread_deleted_msg_cache_k", "Deleted Message Cache");
        en_gb.setProperty("thread_cache_v", "Size: %s");

        // ServerList
        en_gb.setProperty("serverlist_integer", "That's not an integer.");
        en_gb.setProperty("serverlist_members", "Members");
        en_gb.setProperty("serverlist_connected", " Servers that **%s** is connected to");

        // Shutdown
        en_gb.setProperty("shutdown_goodnight", "Good Night %s");

        ////////////////// Random //////////////////

        en_gb.setProperty("random_empty", "I couldn't find anything with this keyword.");

        ////////////////// Standard //////////////////

        // BotInfo
        en_gb.setProperty("botinfo_authorname", "%s - Info");
        en_gb.setProperty("botinfo_description", "Greetings master! I am %s!\n" +
                "I will help you to moderate, organize and entertain your Discord server.\n" +
                "\n" +
                "I am fully customizable. Any command, plugin, feature and category can be toggled on or off.\n" +
                "You can find a detailed help [here](%s).\n" +
                "If you have any questions, feel free to join the [support server](%s).\n" +
                "\n" +
                "To get started, take a look into the dashboard. There you can set up everything!\n" +
                "[Simply visit the website and hit the login button!](%s)");
        en_gb.setProperty("botinfo_links_name", "Links");
        en_gb.setProperty("botinfo_links_value", "[Visit the website and dashboard](%s)\n" +
                "[Invite Servant](%s)\n" +
                "[Join Support Server](%s)\n" +
                "[Upvote the bot at top.gg](%s)");
        en_gb.setProperty("botinfo_stats", "Statistics");
        en_gb.setProperty("botinfo_users", "Users");
        en_gb.setProperty("botinfo_channels", "Channels");
        en_gb.setProperty("botinfo_shard", "This shard");
        en_gb.setProperty("botinfo_restart", "Last restart");

        // Help
        en_gb.setProperty("help_getting_started", "How to get started");
        en_gb.setProperty("help_getting_started_content", "First of all, welcome and thank you for using Servant. %s\n" +
                "\n" +
                "[Click here](%s) to see how to get started with the bot.\n" +
                "This link will bring you to the FAQ. You can also find other useful answers there.");

        en_gb.setProperty("help_commands_content", "There are many commands that are divided into categories.\n" +
                "Those categories are `Standard`, `Moderation`, `Utility`, `Fun`, `Interaction` and `Random`.\n" +
                "\n" +
                "You can find all commands on the website in the [Help](%s) section.\n" +
                "You also can get a detailed help for a command here in Discord. Just type the command name like `%stimezone`.");

        en_gb.setProperty("help_plugins_dashboard", "Plugins & Dashboard");
        en_gb.setProperty("help_plugins_dashboard_content", "Plugins can be found in the [dashboard](%s). Those includes e.g. the birthday plugin, reaction roles and the leveling system.\n" +
                "\n" +
                "In the dashboard you also can set up Servant-Moderator roles.\n" +
                "Every member of your server who has at least one of those roles is considered a Servant-Moderator and is able to execute commands from the moderation category as well as **access the dashboard of this server**.\n" +
                "Give out this permission with care. Treat it like the Administrator permission!");

        en_gb.setProperty("help_faq", "FAQ");
        en_gb.setProperty("help_faq_content", "If you have any question, you can check out the [FAQ](%s).\n" +
                "If you cannot find your question there, feel free to join the [support server](%s) and ask there directly.");

        // Patreon
        en_gb.setProperty("supporter_supportservant", "Support Servant");
        en_gb.setProperty("supporter_description", "As a Servant-Supporter you can choose a custom color for embed messages and personalize your profile with a background!\n" +
                "There are three way to become a Servant-Supporter:");
        en_gb.setProperty("supporter_patreontitle", "Patreon: Subscription");
        en_gb.setProperty("supporter_subscription", "Price: Only $1 / month!\n" +
                "Duration: As long as you pledge.\n" +
                "[Patreon page](https://www.patreon.com/tancred)");
        en_gb.setProperty("supporter_donationtitle", "PayPal: Donation");
        en_gb.setProperty("supporter_donation", "Price: Only $5 (in total)\n" +
                "Duration: Lifetime\n" +
                "**Please provide your Discord Name#1234!**\n" +
                "[PayPal.me page](https://www.paypal.me/servantdiscord)");
        en_gb.setProperty("supporter_serverboosttitle", "Support Server: Server Boost");
        en_gb.setProperty("supporter_serverboost", "Price: FREE\n" +
                "Duration: As long as you boost.\n" +
                "[Join Support Server](https://support.servant.gg/)");
        en_gb.setProperty("supporter_thanks", "Thanks to every supporter <3");

        ////////////////// Utility //////////////////

        // Giveaway
        en_gb.setProperty("giveaway_description", "Start a giveaway that draws a given amount of people as winners after a given time.\n" +
                "List all running giveaways of the current server.");
        en_gb.setProperty("giveaway_usage", "**Start giveaway**\n" +
                "Command: `%sgiveaway \"[prize name]\" [time & winners]`\n" +
                "Example: `%sgiveaway \"100 Cookies\" 1w 12h 2w 30 min`\n" +
                "⤷ `1w` + `12h` + `2w` + `30 min` = 3 Winners, 12 Hours, 30 Minutes\n" +
                "\n" +
                "**List giveaways**\n" +
                "Command: `%sgiveaway list`");
        en_gb.setProperty("giveaway_hint", valid_args + "\n- Winners: winners, winner, w");

        en_gb.setProperty("giveaway_emptyprize", "You cannot leave the prize empty.");
        en_gb.setProperty("giveaway_zerowinners", "There has to be at least one winner.");
        en_gb.setProperty("giveaway_30_days", "Giveaway cannot be further in the future than 30 days.");

        en_gb.setProperty("giveaway_title", "Giveaway: %s");
        en_gb.setProperty("giveaway_description_running", "Host: **%s**\n" +
                "Amount of winners: **%s**\n" +
                "React with %s to enter the giveaway!");
        en_gb.setProperty("giveaway_description_end", "Host: **%s**\n" +
                "Amount of winners: **%s**\n\n" +
                "Congratulations to these winners:");
        en_gb.setProperty("giveaway_description_nowinner", "Host: **%s**\n" +
                "Amount of winners: **%s**\n\n" +
                "Nobody participated. Therefore, nobody won.");
        en_gb.setProperty("giveaway_end_manually", "%s, to end this giveaway manually click on ❌");
        en_gb.setProperty("giveaway_endsat", "Ends at");
        en_gb.setProperty("giveaway_endedat", "Ended at");

        en_gb.setProperty("giveaway_current", "Current giveaways on this server");
        en_gb.setProperty("giveaway_nocurrent", "There are no giveaways running!");

        en_gb.setProperty("giveaway_messageid", "**Message ID:**");
        en_gb.setProperty("giveaway_prize", "**Prize:**");

        // Polls
        en_gb.setProperty("poll_ends_at", "Ends at");
        en_gb.setProperty("poll_ended", "This poll has ended.");
        en_gb.setProperty("poll_end_manually", "%s, to end this poll manually click on ❌");
        en_gb.setProperty("poll_30_days", "Poll cannot be further in the future than 30 days.");

        /// Poll
        en_gb.setProperty("poll_description", "Create a poll with up to 10 custom answers");
        en_gb.setProperty("poll_usage", "**Start a poll**\n" +
                "Command: `%s%s \"[question]/[answer1]/(...)/[answer10]\" [time arguments]`\n" +
                "Example: `%s%s \"When do you have time?/Mon/Tue/Wed/Thu/Fri/Sat/Sun\" 12h`");
        en_gb.setProperty("poll_hint", "After executing this command, you will be asked if you want to allow multiple answers.\n\n" + valid_args);

        en_gb.setProperty("poll_questions_answers", "Please provide a question and answers: `%spoll \"Your question? / Answer 1 / Answer 2 / (...) / Answer 10\" [time arguments]`");
        en_gb.setProperty("poll_amount", "Invalid amount of arguments. There has to be at least one answer to your question and a maximum of 10 answers.");
        en_gb.setProperty("poll_multiplechoice_q", "Allow multiple answers?");

        en_gb.setProperty("poll_timeout", "Timeout! You didn't react on my question.");
        en_gb.setProperty("poll_started", "%s started a poll!");
        en_gb.setProperty("poll_multiple", "Multiple Answers: %s");
        en_gb.setProperty("poll_allowed", "Allowed");
        en_gb.setProperty("poll_forbidden", "Forbidden");
        en_gb.setProperty("poll_ended_manually", "%s has ended the poll!");

        /// QuickPoll
        en_gb.setProperty("quickpoll_description", "An uncomplicated poll with %s or %s.");
        en_gb.setProperty("quickpoll_usage", "**Start a quickpoll**\n" +
                "Command: `%s%s \"Your question (optional)\" [time arguments]`\n\n" +
                "Examples 1: `%s%s \"Create a meme channel?\" 15 min`\n" +
                "Examples 2: `%s%s 15 min`");
        en_gb.setProperty("quickpoll_hint", valid_args);

        en_gb.setProperty("quickpoll_started", "%s started a quickpoll!");
        en_gb.setProperty("quickpoll_ended", "%s has ended the quickpoll!");

        // Rate
        en_gb.setProperty("rate_description", "User rating out of 5");
        en_gb.setProperty("rate_usage", "**Start a rating**\n" +
                "Command: `%s%s \"Optional topic\" [time arguments]`\n" +
                "Example 1: `%s%s \"Newest episode\" 10d`\n" +
                "Example 2: `%s%s 10d`");
        en_gb.setProperty("rate_hint", valid_args);

        en_gb.setProperty("rate_30_days", "Rating must not be further in the future than 30 days.");
        en_gb.setProperty("rate_end_manually", "%s, to end this rating manually click on ❌");
        en_gb.setProperty("rate_ends_at", "Ends at");
        en_gb.setProperty("rate_ended", "This rating ended.");
        en_gb.setProperty("rate_title", "Rating!");
        en_gb.setProperty("rate_title_topic", "Rate: %s");

        // RemindMe
        en_gb.setProperty("remindme_description", "Set up an RemindMe to a specific time.\n" +
                "Other members can upvote the RemindMe to also get reminded.\n" +
                "The RemindMe will have a message link.");
        en_gb.setProperty("remindme_usage", "**Start a remind me**\n" +
                "Command: `%s%s \"Optional topic\" [time arguments]`\n\n" +
                "Example 1: `%s%s \"Pat Servant\" 3d 2h 1m`\n" +
                "Example 2: `%s%s 3d 2h 1m`");
        en_gb.setProperty("remindme_hint", "The author will always be reminded, no matter whether they reacted or not.\n\n" + valid_args);

        en_gb.setProperty("remindme_30_days", "RemindMe must not be further in the future than 30 days.");
        en_gb.setProperty("remindme_of", "RemindMe of %s");
        en_gb.setProperty("remindme_topic", "Topic: %s");
        en_gb.setProperty("remindme_also",  "If you also want to be reminded, just click %s");
        en_gb.setProperty("remindme_at", "RemindMe at");
        en_gb.setProperty("remindme_success",  "You've been reminded %s");
        en_gb.setProperty("remindme_remind", "Hi master, here is your requested reminder.");
        en_gb.setProperty("remindme_jump", "Click here to jump to the context.");

        // Signup
        en_gb.setProperty("signup_description", "Easy event organisation by letting users sign up");
        en_gb.setProperty("signup_usage", "**Start a signup**\n" +
                "Command: `%s%s \"Optional topic\" [time & participants]`\n" +
                "Example 1: `%s%s \"Raiding Event\" 3d 2h 1m 10p`\n" +
                "Example 2: `%s%s 3d 2h 1m 10p`\n" +
                "⮡ `3d` + `2h` + `1m` + `10p` = 3 Days, 2 Hours, 1 Minute, 10 Participants");
        en_gb.setProperty("signup_hint", "- The amount of participants has to be within 1 and 100 inclusively.\n\n" + valid_args + "\n- Participants: participants, participant, p");

        en_gb.setProperty("signup_missing_participants", "You have to provide how many people can participate. E.g.: `%ssignup \"Optional Title\" 10p 1d`");
        en_gb.setProperty("signup_30_days", "Signup must not be further in the future than 30 days.");

        en_gb.setProperty("signup_embedtitle", "Sign up");
        en_gb.setProperty("signup_embedtitle_topic", "Sign up for %s");
        en_gb.setProperty("signup_embeddescription", "Click on %s to participate.\n" +
                "%s people can participate!\n\n" +
                "Remove said reaction if you have changed your mind.\n" +
                "%s, to manually end this signup click on ❌");
        en_gb.setProperty("signup_embeddescriptionend", "%s people could participate!\n" +
                "These are the participants:");
        en_gb.setProperty("signup_nobody", "Nobody signed up");
        en_gb.setProperty("signup_event", "Event at");
        en_gb.setProperty("signup_ended", "This signup ended.");

        // Timezone
        en_gb.setProperty("timezone_description", "Convert a date and time from one timezone to another");
        en_gb.setProperty("timezone_usage", "**Convert a date and time to a different timezone**\n" +
                "Command: `%s%s yyyy-mm-dd hh:mm [start timezone] [target timezone]`\n" +
                "Example: `%s%s 2019-01-01 22:00 PST CET`");
        en_gb.setProperty("timezone_hint", "This command uses the 24 hour system.");

        en_gb.setProperty("timezone_missingargs", "Missing arguments.");
        en_gb.setProperty("timezone_invalidzone_start", "Invalid starting timezone.");
        en_gb.setProperty("timezone_invalidzone_target", "Invalid target timezone.");
        en_gb.setProperty("timezone_conversion", "Timezone Conversion");
        en_gb.setProperty("timezone_input", "Input");
        en_gb.setProperty("timezone_output", "Output");
        en_gb.setProperty("timezone_invalid", "Invalid input. Check your formatting.");

        ///////////////////////////////////// Plugins /////////////////////////////////////

        ////////////////// Moderation //////////////////

        // BestOfImage & BestOfQuote
        en_gb.setProperty("bestof_jump", "Click here to jump to the original message.");
        en_gb.setProperty("bestof_footer", "%s votes | #%s");

        // Birthday
        en_gb.setProperty("birthday_gratulation", "Happy Birthday %s!");
        en_gb.setProperty("birthday_countdown", "Countdown");
        en_gb.setProperty("birthday_countdown_value_sin", "in %s day ");
        en_gb.setProperty("birthday_countdown_value", "in %s days");
        en_gb.setProperty("birthday_date", "Date");
        en_gb.setProperty("birthday_name", "Name");
        en_gb.setProperty("birthday_missing", "No birthdays were set.");
        en_gb.setProperty("birthday_guild", "%s birthdays");
        en_gb.setProperty("birthday_howtoadd", "Add your birthday in the [dashboard](%s)!");
        en_gb.setProperty("birthday_as_of", "As of");

        // Giveaway
        en_gb.setProperty("giveaway_dm", "I counted your entry for [this](%s) giveaway!");
        en_gb.setProperty("giveaway_dm_footer", "If there are more than 5000 reactions, the count may look scuffed, but I still counted you!");

        // Join
        en_gb.setProperty("join_author", "Welcome %s to %s");
        en_gb.setProperty("join_embeddescription", "Enjoy your stay!");
        en_gb.setProperty("join_footer", "Joined at");

        // Leave
        en_gb.setProperty("leave_author", "Farewell %s");
        en_gb.setProperty("leave_embeddescription", "We are sorry to see you go!");
        en_gb.setProperty("leave_footer", "Left at");

        // Level (incl. LevelRole)
        en_gb.setProperty("levelrole_levelup", "LEVEL UP");
        en_gb.setProperty("level_up", "%s just reached level %s! \uD83C\uDF89");
        en_gb.setProperty("levelrole_role_singular", "You also gained following role:");
        en_gb.setProperty("levelrole_role_plural", "You also gained following roles:");
        en_gb.setProperty("level_missingpermission_embed", "I don't have the permission to create embeds (Embed Links), that's why you're seeing the budget level up announcement.");
        en_gb.setProperty("level_hierarchy", "Couldn't add one ore more roles, because they are higher than me in hierarchy.");

        // Livestream
        en_gb.setProperty("livestream_announcement_title", "Livestream!");
        en_gb.setProperty("livestream_announcement", "%s just went live on [Twitch (click me)](%s)!");
        en_gb.setProperty("livestream_announcement_game", "Streaming %s");

        // Log
        en_gb.setProperty("log_at", "Logged at");

        en_gb.setProperty("log_category_create_title", "Category created");
        en_gb.setProperty("log_category_delete_title", "Category deleted");
        en_gb.setProperty("log_category_name", "Category Name");

        en_gb.setProperty("log_emote_add_title", "Emote added");
        en_gb.setProperty("log_emote_remove_title", "Emote removed");
        en_gb.setProperty("log_emote_name", "Emote Name");

        en_gb.setProperty("log_user_ban_title", "User banned");
        en_gb.setProperty("log_banned_user", "Banned User");
        en_gb.setProperty("log_banned_user_id", "Banned User ID");

        en_gb.setProperty("log_invite_create_title", "Invite created");
        en_gb.setProperty("log_invite_delete_title", "Invite deleted");
        en_gb.setProperty("log_invite_channel", "To channel");

        en_gb.setProperty("log_user_join_title", "User joined");
        en_gb.setProperty("log_user_leave_title", "User left");

        en_gb.setProperty("log_role_add_title", "User received role(s)");
        en_gb.setProperty("log_role_remove_title", "User got role(s) removed");

        en_gb.setProperty("log_msg_delete_title", "Message deleted");
        en_gb.setProperty("log_msg_update_title", "Message updated");
        en_gb.setProperty("log_msg_too_old_author", "Too old to fetch author name");
        en_gb.setProperty("log_msg_too_old_content", "Too old to fetch previous content");
        en_gb.setProperty("log_msg_old_content", "Old content");
        en_gb.setProperty("log_msg_new_content", "New content");

        en_gb.setProperty("log_user_unban_title", "User unbanned");
        en_gb.setProperty("log_unbanned_user", "Unbanned User");
        en_gb.setProperty("log_unbanned_user_id", "Unbanned User ID");

        en_gb.setProperty("log_boost_count_title", "Boost count updated");
        en_gb.setProperty("log_boost_tier_title", "Boost tier updated");
        en_gb.setProperty("log_old_boost_count", "Old Boost Count");
        en_gb.setProperty("log_new_boost_count", "New Boost Count");
        en_gb.setProperty("log_changing_perks", "Changing Perks");
        en_gb.setProperty("log_emote_slots", "Emote Slots");
        en_gb.setProperty("log_max_bitrate", "Max Bitrate");

        en_gb.setProperty("log_role_create_title", "Role created");
        en_gb.setProperty("log_role_delete_title", "Role deleted");
        en_gb.setProperty("log_role_name", "Role Name");

        en_gb.setProperty("log_tc_create_title", "Text channel created");
        en_gb.setProperty("log_tc_delete_title", "Text channel deleted");
        en_gb.setProperty("log_tc_name", "Text Channel Name");

        en_gb.setProperty("log_vc_join_title", "Voice channel joined");
        en_gb.setProperty("log_vc_move_title", "Voice channel moved");
        en_gb.setProperty("log_vc_leave_title", "Voice channel left");
        en_gb.setProperty("log_vc_create_title", "Voice channel created");
        en_gb.setProperty("log_vc_delete_title", "Voice channel deleted");
        en_gb.setProperty("log_vc_name", "Voice Channel Name");

        // MediaOnlyChannel
        en_gb.setProperty("mediaonlychannel_warning", "%s, this is a media only channel!\n" +
                "You are allowed to:\n" +
                "- Send upload files with an optional description.\n" +
                "- Post a valid url with an optional description.\n" +
                "*This message will be deleted in 30 seconds.*");

        // ReactionRole
        en_gb.setProperty("reactionrole_insufficient", "Insufficient permissions or problem with hierarchy.");

        // RemindMe
        en_gb.setProperty("remindme_dm", "I counted your entry for [this](%s) reminder!");
        en_gb.setProperty("remindme_dm_footer", "If there are more than 5000 reactions, the count may look scuffed, but I still counted you!");

        // VoiceLobby
        en_gb.setProperty("voicelobby_lobby", "Lobby");

        ///////////////////////////////////// Features /////////////////////////////////////

        // Achievement
        en_gb.setProperty("achievement_console", "Console");
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
        en_gb.setProperty("achievement_padoru", "Padoru Padoruu");
        en_gb.setProperty("achievement_arenanet", "ArenaNet");

        // EasterEggs
        en_gb.setProperty("eastereggs_you_are_welcome", "You're welcome %s");

        // Invite
        en_gb.setProperty("invite_author", "%s, at your service!");
        en_gb.setProperty("invite_description", "Thank you for choosing me to assist you and your server.\n" +
                "I have a lot of features. Most of them are enabled by default but some of them are not (e.g. the leveling system).\n" +
                "For the start, I recommend to check out the [website](%s). There you will find a detailed help to all commands as well as a dashboard, where you can set up and toggle everything.\n" +
                "\n" +
                "Support Server: [Click here to join](%s)\n" +
                "\n" +
                "Have fun!");
        en_gb.setProperty("invite_footer", "You are receiving this message, because someone invited me to your server (%s).");

        // Presence
        en_gb.setProperty("presence_0", "v%s | %shelp");
        en_gb.setProperty("presence_1", "%s users | %shelp");
        en_gb.setProperty("presence_2", "%s servers | %shelp");
        en_gb.setProperty("presence_3", "%ssupporter | %shelp");
        en_gb.setProperty("presence_4", "DASHBOARD: %s");
        en_gb.setProperty("presence_5", "#StayAtHome | %shelp");
        en_gb.setProperty("presence_6", "#BLM | %shelp");

        // Supporter
        en_gb.setProperty("supporter_supporter", "supporter");
        en_gb.setProperty("supporter_dono", "%s just donated $5+ via PayPal.\nThey are now a %s!");
        en_gb.setProperty("supporter_patron", "%s just became a Patron.\nThey are now a %s!");
        en_gb.setProperty("supporter_boost", "%s just boosted the server.\nThey are now a %s!");

        // UsageEmbed
        en_gb.setProperty("usageembed_birthday_settings", "The birthday settings are now in the dashboard!");


        en_gb.store(os,
                "Project: Servant\n" +
                        "Author: Tancred#0001\n" +
                        "GitHub: https://github.com/Tancred423/Servant");
        os.close();
    }

    static void createDefaultDE_DE() throws IOException {
        var de_de = new OrderedProperties();
        var os = new FileOutputStream(System.getProperty("user.dir") + "/resources/lang/de_de.ini");

        ///////////////////////////////////// Global Arguments /////////////////////////////////////

        var valid_args = "Gültige Angaben:\n" +
                "- Tage: days, day, d\n" +
                "- Stunden: hours, hour, h\n" +
                "- Minuten: minutes, minute, min, m";

        ///////////////////////////////////// General /////////////////////////////////////

        de_de.setProperty("no_ending_quotation_mark", "Kein endendes Anführungszeichen.");
        de_de.setProperty("topic_too_long", "Titellänge zu lang.");
        de_de.setProperty("missing_time_unit", "Zeitangabe unvollständig. Wert oder Zeiteinheit fehlt.");
        de_de.setProperty("invalid_char", "Ungültiger Buchstabe: '%s'");
        de_de.setProperty("jump", "Sprung");
        de_de.setProperty("tmp_disabled", "Dieser Befehl wurde temporär wegen eines Fehlers deaktiviert.");
        de_de.setProperty("cooldown_warning", "Dieser Befehl hat noch eine Abklingszeit von %s Sekunden");
        de_de.setProperty("invalid_channel", "Dieser Befehl kann in diesem Kanal nicht verwendet werden!");
        de_de.setProperty("hastobe_vc", "Du musst in einem Sprachkanal sein um dies zu tun!");
        de_de.setProperty("permission_bot", "%s Ich brauch die Berechtigung \"%s\" in diesem/r %s!");
        de_de.setProperty("permission_user", "%s Du brauchst die Berechtigung \"%s\" in diesem/r %s um dies zu tun!");
        de_de.setProperty("forbidden_dm", "Dieser Befehl kann nicht in privaten Nachrichten ausgeführt werden.");
        de_de.setProperty("permission", ":x: Du brauchst in diesem Server die Berechtigung %s, um dies zu tun!");
        de_de.setProperty("blocking_dm", "Die Hilfe konnte nicht gesendet werden, da Du private Nachrichten blockierst.");
        de_de.setProperty("invalid_mention", "Ungültige Erwähnung.");
        de_de.setProperty("current_prefix", "Aktueller Prefix: %s");
        de_de.setProperty("unknown_message", "Unbekannte Nachricht.");
        de_de.setProperty("apostrophe", "'");
        de_de.setProperty("apostrophe_s", "s");
        de_de.setProperty("times", "Mal");
        de_de.setProperty("ap", "Erfolgspunkte");
        de_de.setProperty("commands_used", "Genutzte Befehle");
        de_de.setProperty("commands", "Befehle");
        de_de.setProperty("missing_args", "Fehlende Angaben");
        de_de.setProperty("missing_time_args", "Fehlende Zeitangaben");
        de_de.setProperty("member", "Mitglied");
        de_de.setProperty("role_s", "Rolle(n)");
        de_de.setProperty("author", "Autor");
        de_de.setProperty("msg_id", "Nachrichten ID");
        de_de.setProperty("category_id", "Kategorie ID");
        de_de.setProperty("emote_id", "Emote ID");
        de_de.setProperty("emote", "Emote");
        de_de.setProperty("invite", "Einladung");
        de_de.setProperty("user", "Nutzer");
        de_de.setProperty("user_id", "Nutzer ID");
        de_de.setProperty("vc", "Sprachkanal");
        de_de.setProperty("vc_id", "Sprachkanal ID");
        de_de.setProperty("role_id", "Rolle ID");
        de_de.setProperty("tc_id", "Textkanal ID");
        de_de.setProperty("general_error", "Etwas lief schief! Der Entwickler des Bots wird sich so bald wie möglich darum kümmern.");
        de_de.setProperty("description", "Description");
        de_de.setProperty("usage", "Usage");
        de_de.setProperty("alias", "Alias");
        de_de.setProperty("aliases", "Aliases");
        de_de.setProperty("hint", "Hint");
        de_de.setProperty("channel", "Kanal");
        de_de.setProperty("guild", "Server");
        de_de.setProperty("help", "Hilfe");

        // Parser
        de_de.setProperty("parser_invalid_time_arg", "Ungültige Zeitangabe. Oder vielleicht hast Du die Anführungszeichen um das Thema vergessen?");

        ///////////////////////////////////// Commands /////////////////////////////////////

        ////////////////// Dashboard //////////////////

        // Dashboard
        de_de.setProperty("dashboard_link", "Das Dashboard findet man hier: <%s>");
        de_de.setProperty("dashboard_discontinued", "Diese Befehl wurde abgeschaltet.\n" +
                "Dies geschiet, weil Du nun alles, das dieser Befehl gemacht hat, im Dashboard machen kannst: <%s>");
        de_de.setProperty("dashboard_birthday_settings", "Die Geburtstagseinstellungen sind nun im Dashboard!");

        // Discontinued
        de_de.setProperty("discontinued", "Diese Befehl wurde abgeschaltet.");

        // Leaderboard
        de_de.setProperty("leaderboard_website", "Die Rangliste kann nun hier eingesehen werden: <https://servant.gg/leaderboard/%s>");

        ////////////////// Fun //////////////////

        // Avatar
        de_de.setProperty("avatar_avatar", "Avatar");
        de_de.setProperty("avatar_self", "%s sich selbst das Avatar geklaut!");
        de_de.setProperty("avatar_stolen", "%s hat gerade %s Avatar geklaut!");

        // Baguette
        de_de.setProperty("baguette_49", "Mies Brudi");
        de_de.setProperty("baguette_50", "JACKPOT! Jetzt bist du cool.");

        // BubbleWrap
        de_de.setProperty("bubblewrap_title", "Knickerfolie!");
        de_de.setProperty("bubblewrap_subtitle", "Lass es poppen!");
        de_de.setProperty("bubblewrap_footer", "Knick knickediknack, uh yeah!");

        // Coinflip
        de_de.setProperty("coinflip_head", "Kopf!");
        de_de.setProperty("coinflip_tail", "Zahl!");

        // Flip
        de_de.setProperty("flip_description", "Wirf einen Nutzer um.");
        de_de.setProperty("flip_usage", "**Wird einen Nutzer um**\n" +
                "Befehl: `%s%s @Nutzer`\n" +
                "\n" +
                "**Stell einen Nutzer wieder richtig rum hin**\n" +
                "Befehl: `%s%s @Nutzer`");

        // Love
        de_de.setProperty("love_description", "Verkuppel zwei Leute oder auch nur einen mit sich selbst.");
        de_de.setProperty("love_usage", "**Verkuppel Dich mit jemandem**\n" +
                "Befehl: `%s%s @Nutzer`\n" +
                "\n" +
                "**Verkuppel zwei Nutzer**\n" +
                "Befehl: `%s%s @Nutzer1 @Nutzer2`");

        de_de.setProperty("love_self_100", "Verdammt! Direkt zum Fap!");
        de_de.setProperty("love_self_90", "Ziemlich selbstsicher, oder?");
        de_de.setProperty("love_self_80", "So selbstverliebt...");
        de_de.setProperty("love_self_70", "Du liebst Dich selbst mehr als es andere tun.");
        de_de.setProperty("love_self_69", "Nice.");
        de_de.setProperty("love_self_60", "Sieht so aus als würdest Du Dich selbst akzeptieren.");
        de_de.setProperty("love_self_50", "Du scheinst unsicher zu sein ob Du dich magst oder nicht.");
        de_de.setProperty("love_self_42", "Du hast die Antwort gefunden.");
        de_de.setProperty("love_self_40", "Jetzt kannst du in den Spiegel mit stolz gucken.");
        de_de.setProperty("love_self_30", "Ein bisschen unsicher, aber ich bin mir sicher, dass Du dies bewerkstelligen kannst.");
        de_de.setProperty("love_self_20", "Du schlägst Dich gut. Baue ein wenig mehr selbstvertrauen auf!");
        de_de.setProperty("love_self_10", "Glaube an Dich selbst!");
        de_de.setProperty("love_self_0", "Das ist hart. Wir lieben Dich trotzdem <3");

        de_de.setProperty("love_noself_100", "Verdammt! Das passt super!");
        de_de.setProperty("love_noself_90", "Raff Dich auf und lade die Person zum Essen ein.");
        de_de.setProperty("love_noself_80", "Seid ihr euch sicher, dass ihr euch nicht daten wollt?");
        de_de.setProperty("love_noself_70", "Ich sag das ist eine gesunde Geschwisterliebe.");
        de_de.setProperty("love_noself_69", "Nice.");
        de_de.setProperty("love_noself_60", "Es besteht die Chance.");
        de_de.setProperty("love_noself_50", "Ich wette ihr könnt Freunde sein. :)");
        de_de.setProperty("love_noself_42", "Du hast die Antwort gefunden.");
        de_de.setProperty("love_noself_40", "Immerhin bist du stets bemüht.");
        de_de.setProperty("love_noself_30", "Ich denke nicht, dass das klappt...");
        de_de.setProperty("love_noself_20", "Immerhin etwas, wa?");
        de_de.setProperty("love_noself_10", "Das ist ziemlich niedrig...");
        de_de.setProperty("love_noself_0", "Gut, das wird nicht funktionieren.");

        de_de.setProperty("love_fallback", "Urgh!");

        // Achievements
        de_de.setProperty("achievements_title", "%s%s Erfolge");

        // Commands
        de_de.setProperty("commands_title", "%s%s Befehle");
        de_de.setProperty("commands_footer", "Falls du diesen Befehl in der Hoffnung eine Befehlsliste zu finden benutzt hast, besuche die \"Help\" Sektion der Webseite: %s");

        /// Profile
        de_de.setProperty("profile_footer1", "Siehe auch \"%s%s @nutzer\"");
        de_de.setProperty("profile_footer2", "Siehe auch \"%s%s\"");

        de_de.setProperty("profile_generalinfo", "Allgemeine Infos");
        de_de.setProperty("profile_commandstats", "Befehlstatistiken");
        de_de.setProperty("profile_mostused", "Meist genutzte Befehle");
        de_de.setProperty("profile_nocommands", "Bisher keine genutzten Befehle");
        de_de.setProperty("profile_achievements", "Errungenschaften");
        de_de.setProperty("profile_noachievements", "Keine Errungenschaften");

        de_de.setProperty("profile_level", "Stufe");
        de_de.setProperty("profile_rank",  "Rang");

        de_de.setProperty("profile_baguette", "Größtes Baguette");
        de_de.setProperty("profile_nobaguette", "Bisher keine Baguettes");
        de_de.setProperty("profile_baguette_value", "%s (%s mal)");
        de_de.setProperty("profile_animal", "Lieblingstier");
        de_de.setProperty("profile_nofavourite", "Kein Favorit");
        de_de.setProperty("profile_msi", "Meist verteile Interaktion");
        de_de.setProperty("profile_mri", "Meist erhaltene Interaktion");

        de_de.setProperty("profile_animal_bird", "Vogel");
        de_de.setProperty("profile_animal_cat", "Katze");
        de_de.setProperty("profile_animal_dog", "Hund");
        de_de.setProperty("profile_animal_fennec", "Fennek");
        de_de.setProperty("profile_animal_fox", "Fuchs");
        de_de.setProperty("profile_animal_frog", "Frosch");
        de_de.setProperty("profile_animal_koala", "Koala");
        de_de.setProperty("profile_animal_panda", "Panda");
        de_de.setProperty("profile_animal_redpanda", "RedPanda");
        de_de.setProperty("profile_animal_sloth", "Faultier");
        de_de.setProperty("profile_animal_wolf", "Wolf");

        de_de.setProperty("profile_title_creator", "Erschaffer");
        de_de.setProperty("profile_title_supporter", "Unterstützer");
        de_de.setProperty("profile_title_normal", "Meister");

        ////////////////// Interaction //////////////////

        de_de.setProperty("interaction_description_beg", "Bettel jemanden an");
        de_de.setProperty("interaction_description_birthday", "Wünsche jemanden alles Gute zum Geburtstag");
        de_de.setProperty("interaction_description_bite", "Beiße jemanden");
        de_de.setProperty("interaction_description_bully", "Mobbe jemanden");
        de_de.setProperty("interaction_description_cheers", "Proste jemandem zu");
        de_de.setProperty("interaction_description_cookie", "Gib jemanden einen Keks");
        de_de.setProperty("interaction_description_cop", "Nehme jemanden fest");
        de_de.setProperty("interaction_description_dab", "Dab auf jemanden");
        de_de.setProperty("interaction_description_flex", "Flex auf jemanden");
        de_de.setProperty("interaction_description_highfive", "Gib jemandem ein High-Five");
        de_de.setProperty("interaction_description_hug", "Umarme jemanden");
        de_de.setProperty("interaction_description_kiss", "Küsse jemanden");
        de_de.setProperty("interaction_description_lick", "Lecke jemanden ab");
        de_de.setProperty("interaction_description_pat", "Pat jemanden");
        de_de.setProperty("interaction_description_poke", "Stups jemanden an");
        de_de.setProperty("interaction_description_shame", "Schande \uD83D\uDD14 Schande \uD83D\uDD14 Schande \uD83D\uDD14");
        de_de.setProperty("interaction_usage_shame", "Schäme jemanden");
        de_de.setProperty("interaction_description_slap", "Schlage jemanden");
        de_de.setProperty("interaction_description_wave", "Winke zu jemandem");
        de_de.setProperty("interaction_description_wink", "Zwinker zu jemandenm");
        de_de.setProperty("interaction_usage", "**%s**\n" +
                "Befehl: `%s%s [@nutzer]`\n" +
                "Beispiel: `%s%s @Servant`");

        de_de.setProperty("interaction_shared_beg", "%s bettelte %s großzügige Menschen an.");
        de_de.setProperty("interaction_received_beg", "%s wurde von %s schmierigen Bettlern belästigt.");
        de_de.setProperty("interaction_shared_birthday", "%s wünschte %s wundervolle Geburtstage.");
        de_de.setProperty("interaction_received_birthday", "%s wurde am Geburtstag %s Mal beglückwünscht.");
        de_de.setProperty("interaction_shared_bite", "%s biss %s Menschen. Und alle haben es verdient! ò.ó");
        de_de.setProperty("interaction_received_bite", "%s wurde von %s verrückten Menschen gebissen.");
        de_de.setProperty("interaction_shared_bully", "%s mobbte %s arme Kinder.");
        de_de.setProperty("interaction_received_bully", "%s wurde von %s Rabauken gemobbt.");
        de_de.setProperty("interaction_shared_cheers", "%s postete %s Menschen zu.");
        de_de.setProperty("interaction_received_cheers", "%s wurde von %s Menschen zugeprostet.");
        de_de.setProperty("interaction_shared_cookie", "%s gab einen Keks an %s liebe Leute.");
        de_de.setProperty("interaction_received_cookie", "%s nomt den %s. Keks.");
        de_de.setProperty("interaction_shared_cop", "%s brachte %s Loli-Jäger hinter Gitter.");
        de_de.setProperty("interaction_received_cop", "%s wurde von %s FBI Agenten verhaftet.");
        de_de.setProperty("interaction_shared_dab", "%s d4bt3 4uf %s n00b5.");
        de_de.setProperty("interaction_received_dab", "%s schmunzelte über %s Fortnite Kids.");
        de_de.setProperty("interaction_shared_flex", "%s zeigte %s Schwächlingen die Muskeln.");
        de_de.setProperty("interaction_received_flex", "%s wurde von %s Möchtegern-Pumpern angepöbelt.");
        de_de.setProperty("interaction_shared_highfive", "%s schlug in %s erhobenen Händen ein.");
        de_de.setProperty("interaction_received_highfive", "%s versuchte %s Mal zu winken, bekam aber High-Fives von komischen Leuten.");
        de_de.setProperty("interaction_shared_hug", "%s umarmte %s süße Leute.");
        de_de.setProperty("interaction_received_hug", "%s wurde von %s süßen Leuten geknuddelt.");
        de_de.setProperty("interaction_shared_kiss", "%s küsste %s zufällige Leute.");
        de_de.setProperty("interaction_received_kiss", "%s erhielt bisher %s Küsse.");
        de_de.setProperty("interaction_shared_lick", "%s leckte an %s Fremden.");
        de_de.setProperty("interaction_received_lick", "%s hat sich %s Mal das Gesicht gewaschen.");
        de_de.setProperty("interaction_shared_pat", "%s tätschelte den Kopf von %s Catgirls. uwu");
        de_de.setProperty("interaction_received_pat", "%s versuchte %s Mal zu erklären, dass es keine Catgirls gibt.");
        de_de.setProperty("interaction_shared_poke", "%s piekste %s Leute. (｀ω´)");
        de_de.setProperty("interaction_received_poke", "%s wurde von %s Leuten genervt.");
        de_de.setProperty("interaction_shared_slap", "%s schlug %s Rabauken.");
        de_de.setProperty("interaction_received_slap", "%s wurde %s Mal gemobbt... Oh warte, falscher Befehl (◯Δ◯∥)");
        de_de.setProperty("interaction_shared_shame", "%s geleitete %s Sünder durch den Gang der Buße.");
        de_de.setProperty("interaction_received_shame", "%s cosplayte %s Mal Cersei.");
        de_de.setProperty("interaction_shared_wave", "%s grüßte %s liebevolle Derps.");
        de_de.setProperty("interaction_received_wave", "%s `✓✓Gelesen` %s Mal.");
        de_de.setProperty("interaction_shared_wink", "%s zwinkerte %s Leuten zu.");
        de_de.setProperty("interaction_received_wink", "%s errötete %s Mal.");

        ////////////////// Moderation //////////////////

        // Clear
        de_de.setProperty("clear_description", "Löscht bis zu 100 Nachrichten.\n" +
                "Kann nutzerspezifische Nachrichten der letzten 100 Nachrichten löschen.");
        de_de.setProperty("clear_usage", "**Lösche einige Nachrichten**\n" +
                "Befehl: `%s%s [1 - 100 ODER @Nutzer]`\n" +
                "Beispiel 1: `%s%s 50`\n" +
                "Beispiel 2: `%s%s @Servant`");
        de_de.setProperty("clear_hint", "Die Anzahl der Nachrichten ist inklusiv, d. h. Du kannst nur eine Nachricht oder auch ganze 100 löschen.");

        de_de.setProperty("clear_input", "Du kannst nur eine Nummer oder eine Nutzererwähnung angeben!");
        de_de.setProperty("clear_invalid", "Deine Eingabe ist ungültig. Versuche es mit einer kleineren Zahl.");
        de_de.setProperty("clear_sub_one", "Die Eingabe darf nicht niedriger als 1 sein.");
        de_de.setProperty("clear_cleared", "%s Nachrichten gelöscht");

        // EditEmbed
        de_de.setProperty("editembed_description", "Bearbeite ein Embed, das von %s gemacht wurde.");
        de_de.setProperty("editembed_usage", "**Bearbeite ein Embed**\n" +
                "Befehl: `%s%s [Nachrichtenlink]`\n" +
                "Example: `%s%s %s`");
        de_de.setProperty("editembed_hint", "**Wie man Nachtenlinks bekommt:**\n" +
                "- PC: Über der Nachrichten hovern > 3 Punkte Menü > Nachrichtenlink kopieren\n" +
                "- Android: Drücke & halte die Nachricht > Teilen > Kopieren\n" +
                "- iOS: Drücke & halte die Nachricht > Nachrichtenlink kopieren");

        de_de.setProperty("editembed_args_length", "Bitte gib einen gültigen Nachrichtenlink an. Schreibe `%seditembed` um alle nötigen Informationen zu erhalten.");
        de_de.setProperty("editembed_tc_not_found", "Der gegebene Textkanal kann nicht gefunden werden.");
        de_de.setProperty("editembed_invalid_message_id", "Die angegebene Nachrichten ID ist ungültig. Siehe `%seditembed` für Hilfe.");
        de_de.setProperty("editembed_missingpermission", "Du musst die Rechte haben in den gegebenen Kanal schreiben zu dürfen, um die Embeds bedienen zu können.");
        de_de.setProperty("editembed_notbyme", "Diese Nachricht ist nicht von mir.");
        de_de.setProperty("editembed_noembed", "Ich kann kein Embed für diese Nachricht finden.");
        de_de.setProperty("editembed_introduction", "Mit diesem Befehl kannst du Embeds von %s bearbeiten.\n" +
                "- Das Embed darf ingesamt nicht länger als 6000 Zeichen sein.\n" +
                "- Jedes mal wenn du reagieren oder eine Antwort schreiben musst, hast du 15 Minuten dafür Zeit.\n" +
                "Bist du vorbereitet?");
        de_de.setProperty("editembed_notfound", "Ich kann diese Nachricht nicht finden, Meister!");
        de_de.setProperty("embed_field_remove_q", "Möchtest du die aktuellen Felder entfernen?");
        de_de.setProperty("embed_timeout", "Diese Konfiguration ist timeoutet.");
        de_de.setProperty("embed_authorline_q", "Alles klar! Möchtest Du den **Autor-Eintrag** verwenden?");
        de_de.setProperty("embed_authorname_i", "Bitte gebe den **Autornamen** an:");
        de_de.setProperty("embed_authorurl_q", "Möchtest Du die **Autor-URL** (Nicht das Icon) verwenden?");
        de_de.setProperty("embed_authorurl_i", "Bitte gebe die **Autor-URL** (Nicht die Icon-URL!) an:");
        de_de.setProperty("embed_authorurl_i_fail", "Deine Eingabe ist ungültig. Bitte gebe eine gültige URL an:");
        de_de.setProperty("embed_authoricon_q", "Möchtest Du das **Autor-Icon** verwenden?");
        de_de.setProperty("embed_authoricon_i", "Bitte gebe die **Autor-Icon-URL** (Direkt-Link!) an:");
        de_de.setProperty("embed_authoricon_i_fail", "Deine Eingabe ist ungültig. Bitte gebe einen gültigen Direkt-Link an:");
        de_de.setProperty("embed_thumbnail_q", "Möchtest Du das **Thumbnail** benutzen?");
        de_de.setProperty("embed_thumbnail_i", "Bitte gebe eine **Thumbnail-URL** (Direkt-Link!) an:");
        de_de.setProperty("embed_thumbnail_i_fail", "Deine Eingabe ist ungültig. Bitte gebe einen gültigen Direkt-Link an:");
        de_de.setProperty("embed_title_q", "Möchtest Du einen **Titel** verwenden?");
        de_de.setProperty("embed_title_i", "Bitte gebe einen **Titel** an:");
        de_de.setProperty("embed_url_q", "Möchtest du eine **Titel-URL** verwenden?");
        de_de.setProperty("embed_url_i", "Bitte gebe eine **Titel-URL** an:");
        de_de.setProperty("embed_url_i_fail", "Deine Eingabe ist ungültig. Bitte gebe eine gültige URL an:");
        de_de.setProperty("embed_description_q", "Möchtest du eine **Beschreibung** benutzen?");
        de_de.setProperty("embed_description_i", "Bitte gebe eine **Beschreibung** an:");
        de_de.setProperty("embed_field_q", "Möchtest du ein Feld hinzufügen?");
        de_de.setProperty("embed_field_name_i", "Bitte gebe einen **Feldnamen** an:");
        de_de.setProperty("embed_field_value_i", "Bitte gebe eine **Feldbeschreibung** an:");
        de_de.setProperty("embed_field_inline_i", "Soll sich das Feld in eine Zeile einordnen?");
        de_de.setProperty("embed_image_q", "Möchtest du ein **Bild** verwenden?");
        de_de.setProperty("embed_image_i", "Bitte gebe eine **Bild-URL** (Direkt-Link!) an:");
        de_de.setProperty("embed_image_i_fail", "Deine Eingabe ist ungültig. Bitte gebe einen gültigen Direkt-Link an:");
        de_de.setProperty("embed_footer_q", "Möchtest du eine **Fußzeile** verwenden?");
        de_de.setProperty("embed_footer_text_i", "Bitte gebe eine **Fußzeilenbeschreibung** an:");
        de_de.setProperty("embed_footer_icon_q", "Möchtest du ein **Fußzeilen-Icon** verwenden?");
        de_de.setProperty("embed_footer_icon_i", "Bitte gebe eine **Icon-URL** (Direkt-Link!) an:");
        de_de.setProperty("embed_footer_icon_i_fail", "Deine Eingabe ist ungültig. Bitte gebe einen gültigen Direkt-Link an:");
        de_de.setProperty("embed_empty", "Entweder ist das Embed leer, oder es hat über 6000 Zeichen.\nBeides geht leider nicht!");
        de_de.setProperty("embed_timestamp_q", "Möchtest du einen **Zeitstempel** verwenden?");
        de_de.setProperty("embed_timestamp_i", "Bitte gebe einen **Zeitstempel** an\nDu kannst entweder `now` (jetzt) schreiben oder ein Datum und Zeit wie folgt angeben: `yyyy-MM-dd HH:mm` (Benutzt Server Offset):");
        de_de.setProperty("embed_timestamp_i_fail", "Deine Eingabe ist ungültig\nDu kannst entweder `now` (jetzt) schreiben oder ein Datum und Zeit wie folgt angeben: `yyyy-MM-dd HH:mm` (Benutzt Server Offset).\nVersuche es erneut:");
        de_de.setProperty("editembed_confirm", "Wir sind fertig! Bitte bestätige, dass du das alte Embed überschreiben willst.");

        ////////////////// Owner //////////////////

        // Blacklist
        de_de.setProperty("blacklist_missingid", "Füge eine ID hinzu");
        de_de.setProperty("blacklist_empty", "Keine geblacklisteten IDs.");

        // Thread
        de_de.setProperty("thread_title", "Stats für Nerds");
        de_de.setProperty("thread_stats_k", "Generelle Stats");
        de_de.setProperty("thread_stats_v", "Verfügbare Prozessoren: %s\nAktuelle Threads: %s");
        de_de.setProperty("thread_fixed_k", "Fixed Thread Pool");
        de_de.setProperty("thread_cached_k", "Cached Thread Pool");
        de_de.setProperty("thread_scheduled_k", "Scheduled Service");
        de_de.setProperty("thread_period_k", "Period Service");
        de_de.setProperty("thread_pool_service_v", "Läuft: %s\nPool Größe: %s\nAktive Threads: %s\nAufgaben in Warteschlange: %s\nErledigte Aufgaben: %s");
        de_de.setProperty("thread_database_k", "Datenbank");
        de_de.setProperty("thread_database_v", "Aktive Verbindungen: %s\nGesamte Verbindungen: %s\nMaximale Poolgröße: %s");
        de_de.setProperty("thread_msg_cache_k", "Nachrichten Cache");
        de_de.setProperty("thread_deleted_msg_cache_k", "Gelöschte Nachrichten Cache");
        de_de.setProperty("thread_cache_v", "Größe: %s");

        // ServerList
        de_de.setProperty("serverlist_integer", "Das ist kein Integer.");
        de_de.setProperty("serverlist_members", "Mitglieder");
        de_de.setProperty("serverlist_connected", "Server, die mit **%s** verbunden sind");

        // Shutdown
        de_de.setProperty("shutdown_goodnight", "Gute Nacht %s");

        ////////////////// Random //////////////////

        de_de.setProperty("random_empty", "Ich konnte unter diesem Suchwort nichts finden.");

        ////////////////// Standard //////////////////

        // BotInfo
        de_de.setProperty("botinfo_authorname", "%s - Info");
        de_de.setProperty("botinfo_description", "Sei gegrüßt Master! Ich bin %s!\n" +
                "Ich werde Dir dabei helfen Deinen Server zu moderieren, organisieren und unterhalten.\n" +
                "\n" +
                "Ich bin komplett anpassbar. Jeder Befehl, Befehlskategorie, Plugin und Feature kann an- bzw. ausgeschaltet werden.\n" +
                "Du kannst eine detailierte Hilfe [hier](%s) finden.\n" +
                "Falls Du eine Frage hast, kannst Du auch dem [Support Server](%s) beitreten.\n" +
                "\n" +
                "Um zu starten, wirf doch einfach Mal einen Blick ins Dashboard. Dort kannst du alles einstellen!\n" +
                "[Öffne einfach die Webseite und drücke auf Login!](%s)");
        de_de.setProperty("botinfo_links_name", "Links");
        de_de.setProperty("botinfo_links_value", "[Öffne die Webseite und das Dashboard](%s)\n" +
                "[Lade Servant ein](%s)\n" +
                "[Trete dem Support Server bei](%s)\n" +
                "[Upvote den Bot auf top.gg](%s)");
        de_de.setProperty("botinfo_stats", "Statistiken");
        de_de.setProperty("botinfo_users", "Nutzer");
        de_de.setProperty("botinfo_channels", "Kanäle");
        de_de.setProperty("botinfo_shard", "Diese Shard");
        de_de.setProperty("botinfo_restart", "Letzter Neustart");

        // Help
        de_de.setProperty("help_getting_started", "Wie fange ich an");
        de_de.setProperty("help_getting_started_content", "Zu aller erst willkommen und danke, dass du Servant verwendest. %s\n" +
                "\n" +
                "[Klicke hier](%s) um zu sehen, wie du mit Servant anfängst.\n" +
                "Dieser Link bringt dich zum FAQ. Dort kannst du auch weitere nützliche Antworten finden.");

        de_de.setProperty("help_commands_content", "Es gibt viele Befehle. Diese sind in verschiedene Kategories unterteilt.\n" +
                "Diese Kategorien lauten `Standard`, `Moderation`, `Utility`, `Fun`, `Interaction` und `Random`.\n" +
                "\n" +
                "Du kannst alle Befehle in der [Help](%s) Sektion auf der Website finden.\n" +
                "Du kannst auch hier in Discord eine detailierte Hilfe zu einem Befehl erhalten. Schreibe einfach einen Befehl, wie z.B. `%stimezone`.");

        de_de.setProperty("help_plugins_dashboard", "Plugins & Dashboard");
        de_de.setProperty("help_plugins_dashboard_content", "Plugins befinden sich im [Dashboard](%s). Darunter befindet sich z.B. das Birthday-Plugin, Reaction Roles und das Levelsystem.\n" +
                "\n" +
                "Im Dashboard kannst du ebenfalls die Servant-Moderatoren Rollen einstellen.\n" +
                "Jedes Mitglied deines Servers das mindestens eine dieser Rollen hat, ist ein Servant-Moderator und ist in der Lage Befehle der Moderation-Kategorie auszuführen, sowie auf **das Dashboard deines Servers zuzugreifen**.\n" +
                "Verteile dieses Recht mit Bedacht. Behandle es wie die Administrator-Berechtigung!");

        de_de.setProperty("help_faq", "FAQ");
        de_de.setProperty("help_faq_content", "Falls Du irgendwelche Fragen hast, kannst du [FAQ](%s) durchgucken.\n" +
                "Falls Du dort deine Frage nicht finden kannst, kannst Du auch gerne dem [Support Server](%s) beitreten und dort direkt fragen.");

        // Supporter
        de_de.setProperty("supporter_supportservant", "Unterstütze Servant");
        de_de.setProperty("supporter_description", "Als Servant-Supporter kannst Du eine beliebige Farbe für Embed-Nachrichten einstellen und Dein Profil mit einem Hintergrund personalisieren!\n" +
                "Du kannst Servant auf drei unterschiedliche Wege unterstützen:");
        de_de.setProperty("supporter_patreontitle", "Patreon: Abonnement");
        de_de.setProperty("supporter_subscription", "Preis: Nur $1 / Monat!\n" +
                "Laufzeit: Solange du Abonnent bist.\n" +
                "[Patreon](https://www.patreon.com/tancred)");
        de_de.setProperty("supporter_donationtitle", "PayPal: Spende");
        de_de.setProperty("supporter_donation", "Preis: Nur $5 (insgesamt)\n" +
                "Laufzeit: Lebenszeit\n" +
                "**Bitte gib deinen Discord Namen#1234 an!**\n" +
                "[PayPal.me Seite](https://www.paypal.me/servantdiscord)");
        de_de.setProperty("supporter_serverboosttitle", "Support Server: Server Boost");
        de_de.setProperty("supporter_serverboost", "Preis: GRATIS\n" +
                "Laufzeit: Solange du boostest.\n" +
                "[Support Server beitreten](https://support.servant.gg/)");
        de_de.setProperty("supporter_thanks", "Danke an jeden einzelnen Unterstützer <3");

        ////////////////// Utility //////////////////

        // Giveaway
        de_de.setProperty("giveaway_description", "Verantstalte ein Giveaway, das eine gegebene Anzahl an Leuten als Gewinner nach einer gewissen Zeit ermittelt.\n" +
                "Liste alle aktuell laufenden Giveaways auf dem aktuellen Server auf.");
        de_de.setProperty("giveaway_usage", "**Starte ein Giveaway**\n" +
                "Befehl: `%sgiveaway \"[Preisname]\" [Gewinner und Zeit]`\n" +
                "Beispiel: `%sgiveaway \"100 Kekse\" 1w 12h 2w 30 min`\n" +
                "⤷ `1w` + `12h` + `2w` + `30 min` = 3 Gewinner, 12 Stunden, 30 Minuten\n" +
                "\n" +
                "**Liste Giveaways auf**\n" +
                "Befehl: `%sgiveaway list`");
        de_de.setProperty("giveaway_hint", valid_args + "\n- Gewinner: winners, winner, w");

        de_de.setProperty("giveaway_emptyprize", "Du kannst den Preis nicht leer lassen.");
        de_de.setProperty("giveaway_zerowinners", "Dies ergibt keinen Sinn. Es muss mindestens einen Gewinner geben.");
        de_de.setProperty("giveaway_30_days", "Das Giveaway darf nicht weiter in der Zukunft sein als 30 Tage.");

        de_de.setProperty("giveaway_title", "Giveaway: %s");
        de_de.setProperty("giveaway_description_running", "Host: **%s**\n" +
                "Anzahl Gewinner: **%s**\n" +
                "Reagiere mit %s, um am Giveaway teilzunehmen!");
        de_de.setProperty("giveaway_description_end", "Host: **%s**\n" +
                "Anzahl Gewinner: **%s**\n\n" +
                "Herzlichen Glückwunsch an folgende Gewinner:");
        de_de.setProperty("giveaway_description_nowinner", "Host: **%s**\n" +
                "Anzahl Gewinner: **%s**\n\n" +
                "Keiner hat teilgenommen, daher hat auch keiner gewonnen.");
        de_de.setProperty("giveaway_end_manually", "%s, um dieses Giveaway manuell zu beenden, drücke auf ❌");
        de_de.setProperty("giveaway_endsat", "Endet am");
        de_de.setProperty("giveaway_endedat", "Endete am");

        de_de.setProperty("giveaway_current", "Aktuelle Giveaways auf diesem Server");
        de_de.setProperty("giveaway_nocurrent", "Es laufen gerade keine Giveaways!");

        de_de.setProperty("giveaway_messageid", "**Nachrichten-ID:**");
        de_de.setProperty("giveaway_prize", "**Preis:**");

        // Polls
        de_de.setProperty("poll_ends_at", "Endet am");
        de_de.setProperty("poll_ended", "Diese Abstimmung wurde beendet.");
        de_de.setProperty("poll_end_manually", "%s, um diese Umfrage manuell zu beenden, drücke auf ❌");
        de_de.setProperty("poll_30_days", "Die Umfrage darf nicht weiter in der Zukunft sein als 30 Tage.");

        /// Poll
        de_de.setProperty("poll_description", "Erstelle eine Umfrage mit bis zu 10 selbst erstellten Antwortmöglichkeiten.");
        de_de.setProperty("poll_usage", "**Starte eine Umfrage**\n" +
                "Befehl: `%s%s \"[Frage]/[Antwort1]/(...)/[Antwort10]\" [time arguments]`\n" +
                "Beispiel: `%s%s \"Wann hast du Zeit?/Mo/Di/Mi/Do/Fr/Sa/So\" 12h`");
        de_de.setProperty("poll_hint", "Nachdem der Befehl durchgeführt wurde, wirst Du gefragt, ob Du mehrere Antworten zulassen willst.\n\n" + valid_args);

        de_de.setProperty("poll_questions_answers", "Bitte gebe eine Frage und dessen Antworten mit an: `%spoll \"Deine Frage? / Antwort 1 / Antwort 2 / (...) / Antwort 10\" [Zeitangaben]`");
        de_de.setProperty("poll_amount", "Ungültige Anzahl an Angaben. Es muss mindestens eine und maximal 10 Antworten zu Deiner Frage geben.");
        de_de.setProperty("poll_multiplechoice_q", "Erlaube mehrere Antworten?");

        de_de.setProperty("poll_timeout", "Timeout! Du hast nicht auf meine Frage reagiert.");
        de_de.setProperty("poll_started", "%s startete eine Umfrage!");
        de_de.setProperty("poll_multiple", "Mehrere Antworten: %s");
        de_de.setProperty("poll_allowed", "Erlaubt");
        de_de.setProperty("poll_forbidden", "Verboten");
        de_de.setProperty("poll_ended_manually", "%s beendete die Umfrage!");

        /// QuickPoll
        de_de.setProperty("quickpoll_description", "Eine unkomplizierte Umfrage bei der Nutzer mit %s or %s abstimmen können.");
        de_de.setProperty("quickpoll_usage", "**Starte eine Quickpoll-Umfrage**\n" +
                "Befehl: `%s%s \"Optionaler Betreff\" [Zeitangaben]`\n\n" +
                "Beispiel 1: `%s%s \"Neuer Textkanal für Memes?\" 15 min`\n" +
                "Beispiel 2: `%s%s 15 min`");
        de_de.setProperty("quickpoll_hint", valid_args);

        de_de.setProperty("quickpoll_started", "%s hat einen QuickPoll gestartet!");
        de_de.setProperty("quickpoll_ended", "%s hat den QuickPoll beendet!");

        // Rate
        de_de.setProperty("rate_description", "Bewertung von 1 - 5");
        de_de.setProperty("rate_usage", "**Starte eine Bewertung**\n" +
                "Befehl: `%s%s \"Optionales Thema\" [Zeitangaben]`\n" +
                "Beispiel 1: `%s%s \"Neuste Folge\" 10d`\n" +
                "Beispiel 2: `%s%s 10d`");
        de_de.setProperty("rate_hint", valid_args);

        de_de.setProperty("rate_30_days", "Die Bewertung darf nicht weiter in der Zukunft sein als 30 Tage.");
        de_de.setProperty("rate_end_manually", "%s, um diese Bewertung manuell zu beenden, drücke auf ❌");
        de_de.setProperty("rate_ends_at", "Endet am");
        de_de.setProperty("rate_ended", "Diese Bewertung endete.");
        de_de.setProperty("rate_title", "Bewertung!");
        de_de.setProperty("rate_title_topic", "Bewerte: %s");

        // RemindMe
        de_de.setProperty("remindme_description", "Erstelle eine Erinnerung zu einer spezifischen Zeit.\n" +
                "Andere Member können die Erinnerungen upvoten, um auch erinnert zu werden.\n" +
                "Die Erinnerung wird einen Nachrichtenlink haben.");
        de_de.setProperty("remindme_usage", "**Starte eine Erinnerung**\n" +
                "Befehl: `%s%s \"Optionale Beschreibung\" [Zeitangaben]`\n\n" +
                "Beispiel 1: `%s%s \"Servant patten\" 3d 2h 1m`\n" +
                "Beispiel 2: `%s%s 3d 2h 1m`");
        de_de.setProperty("remindme_hint", "Der Befehlautor wird immer erinnert, auch wenn dieser nicht upvotet.\n\n" + valid_args);

        de_de.setProperty("remindme_30_days", "Diese Erinnerung darf nicht weiter in der Zukunft sein als 30 Tage.");
        de_de.setProperty("remindme_of", "Erinnerung von %s");
        de_de.setProperty("remindme_topic", "Thema: %s");
        de_de.setProperty("remindme_also",  "Wenn Du auch erinnert werden möchtest, drücke %s");
        de_de.setProperty("remindme_at", "Erinnerung am");
        de_de.setProperty("remindme_success",  "Du wurdest erinnert %s");
        de_de.setProperty("remindme_remind", "Hi Meister, hier ist deine angeforderte Erinnerung.");
        de_de.setProperty("remindme_jump", "Hier drücken um zum Kontext zu gelangen.");

        // Signup
        de_de.setProperty("signup_description", "Einfache Eventorganisation indem Leute sich anmelden können");
        de_de.setProperty("signup_usage", "**Starte eine Anmeldung**\n" +
                "Command: `%s%s \"Optionaler Titel\" [Zeitangaben und Teilnehmer]`\n" +
                "Beispiel 1: `%s%s \"Raid Event\" 3d 2h 1m 10p`\n" +
                "Beispiel 2: `%s%s 3d 2h 1m 10p`\n" +
                "⮡ `3d` + `2h` + `1m` + `10p` = 3 Tage, 2 Stunden, 1 Minute, 10 Teilnehmer");
        de_de.setProperty("signup_hint", "- Die Anzahl der Teilnehmer muss zwischen 1 und 100 sein.\n\n" + valid_args + "\n- Teilnehmer: participants, participant, p");

        de_de.setProperty("signup_missing_participants", "Du musst angeben, wie viele Leute teilnehmen dürfen. Z.B.: `%ssignup \"Optionale Titel\" 10p 1d`");
        de_de.setProperty("signup_30_days", "Diese Anmeldung darf nicht weiter in der Zukunft sein als 30 Tage.");

        de_de.setProperty("signup_embedtitle", "Anmeldung");
        de_de.setProperty("signup_embedtitle_topic", "Anmeldung für %s");
        de_de.setProperty("signup_embeddescription", "Klicke auf %s um teilzunehmen.\n" +
                "%s Leute können teilnehmen!\n\n" +
                "Entferne besagte Reaktion falls du deine Meinung geändert hast.\n" +
                "%s, um die Anmeldung manuell zu beenden, klicke auf ❌");
        de_de.setProperty("signup_embeddescriptionend", "%s Leute konnten teilnehmen!\n" +
                "Dies sind die Teilnehmer:");
        de_de.setProperty("signup_nobody", "Keiner hat sich angemeldet");
        de_de.setProperty("signup_event", "Event am");
        de_de.setProperty("signup_ended", "Diese Anmeldung ist geschlossen.");

        // Timezone
        de_de.setProperty("timezone_description", "Konvertiert ein Datum und Zeit von einer Zeitzone in eine andere");
        de_de.setProperty("timezone_usage", "**Konvertiere ein Datum und Zeit in eine andere Zeitzone**\n" +
                "Befehl: `%s%s yyyy-mm-dd hh:mm [Start-Zeitzone] [Ziel-Zeitzone]`\n" +
                "Beispiel: `%s%s 2019-01-01 22:00 PST CET`");
        de_de.setProperty("timezone_hint", "Dieser Befehl benutzt das 24 Stunden System.");

        de_de.setProperty("timezone_missingargs", "Fehlende Angaben.");
        de_de.setProperty("timezone_invalidzone_start", "Ungültige Start-Zeitzone.");
        de_de.setProperty("timezone_invalidzone_target", "Ungültige Ziel-Zeitzone.");
        de_de.setProperty("timezone_conversion", "Zeitzonenkonvertierung");
        de_de.setProperty("timezone_input", "Eingabe");
        de_de.setProperty("timezone_output", "Ausgabe");
        de_de.setProperty("timezone_invalid", "Ungültige Eingabe. Überprüfe deine Formatierungen.");

        ///////////////////////////////////// Plugins /////////////////////////////////////

        ////////////////// Moderation //////////////////

        // BestOfImage & BestOfQuote
        de_de.setProperty("bestof_jump", "Drücke hier, um zur orginalen Nachricht zu gelangen.");
        de_de.setProperty("bestof_footer", "%s Stimmen | #%s");

        // Birthday
        de_de.setProperty("birthday_gratulation", "Herzlichen Glückwunsch zum Geburtstag %s!");
        de_de.setProperty("birthday_countdown", "Countdown");
        de_de.setProperty("birthday_countdown_value_sin", "in %s Tag  ");
        de_de.setProperty("birthday_countdown_value", "in %s Tagen");
        de_de.setProperty("birthday_date", "Datum");
        de_de.setProperty("birthday_name", "Name");
        de_de.setProperty("birthday_missing", "Keine Geburtstage vorhanden.");
        de_de.setProperty("birthday_guild", "%s Geburtstage");
        de_de.setProperty("birthday_howtoadd", "Füge deinen Geburtstag im [Dashboard](%s) hinzu!");
        de_de.setProperty("birthday_as_of", "Stand");

        // Giveaway
        de_de.setProperty("giveaway_dm", "Ich habe deine Teilnahme für [dieses](%s) Giveaway gezählt!");
        de_de.setProperty("giveaway_dm_footer", "Falls dort mehr als 5000 Reaktionen vorhanden sind, mag der Counter verbuggt aussehen, aber ich habe dich dennoch mitgezählt!");

        // Join
        de_de.setProperty("join_author", "Willkommen %s zu %s");
        de_de.setProperty("join_embeddescription", "Genieße deinen Aufenthalt!");
        de_de.setProperty("join_footer", "Trat bei am");

        /// Leave
        de_de.setProperty("leave_author", "Lebewohl %s");
        de_de.setProperty("leave_embeddescription", "Es ist schade, Dich gehen zu sehen!");
        de_de.setProperty("leave_footer", "Trat aus am");

        // Level (incl. LevelRole)
        de_de.setProperty("levelrole_levelup", "LEVEL UP");
        de_de.setProperty("level_up", "%s erreichte soeben Level %s! \uD83C\uDF89");
        de_de.setProperty("levelrole_role_singular", "Du hast zusätzlich folgende Rolle erhalten:");
        de_de.setProperty("levelrole_role_plural", "Du hast zusätzelich folgende Rollen erhalten:");
        de_de.setProperty("level_missingpermission_embed", "Ich habe keine Rechte Embeds zu erstellen (Links einbetten). Deshalb siehst du die Sparversion der Level-Up Benachrichtigung.");
        de_de.setProperty("level_hierarchy", "Ich konnte ein oder mehrere Rollen nicht hinzufügen, weil der Nutzer höher in der Hierarchie ist als ich.");

        // Livestream
        de_de.setProperty("livestream_announcement_title", "Livestream!");
        de_de.setProperty("livestream_announcement", "%s ging gerade auf [Twitch (click me)](%s) live!");
        de_de.setProperty("livestream_announcement_game", "Streamt %s");

        // Log
        de_de.setProperty("log_at", "Gelogt am");

        de_de.setProperty("log_category_create_title", "Kategorie erstellt");
        de_de.setProperty("log_category_delete_title", "Kategorie gelöscht");
        de_de.setProperty("log_category_name", "Kategoriename");

        de_de.setProperty("log_emote_add_title", "Emote hinzugefügt");
        de_de.setProperty("log_emote_remove_title", "Emote gelöscht");
        de_de.setProperty("log_emote_name", "Emotename");

        de_de.setProperty("log_user_ban_title", "Nutzer gebannt");
        de_de.setProperty("log_banned_user", "Gebannter Nutzer");
        de_de.setProperty("log_banned_user_id", "Gebannter Nutzer ID");

        de_de.setProperty("log_invite_create_title", "Einladung erstellt");
        de_de.setProperty("log_invite_delete_title", "Einladung gelöscht");
        de_de.setProperty("log_invite_channel", "Zum Kanal");

        de_de.setProperty("log_user_join_title", "Nutzer beigetreten");
        de_de.setProperty("log_user_leave_title", "Nutzer verlassen");

        de_de.setProperty("log_role_add_title", "Nutzer erhielt Rolle(n)");
        de_de.setProperty("log_role_remove_title", "Nutzer hat Rolle(n) verloren");

        de_de.setProperty("log_msg_delete_title", "Nachricht gelöscht");
        de_de.setProperty("log_msg_update_title", "Nachricht editiert");
        de_de.setProperty("log_msg_too_old_author", "Zu alt, um den Autorennamen zu bekommen");
        de_de.setProperty("log_msg_too_old_content", "Zu alt, um den vorherigen Inhalt zu bekommen");
        de_de.setProperty("log_msg_old_content", "Alter Inhalt");
        de_de.setProperty("log_msg_new_content", "Neuer Inhalt");

        de_de.setProperty("log_user_unban_title", "Nutzer entbannt");
        de_de.setProperty("log_unbanned_user", "Entbannter Nutzer");
        de_de.setProperty("log_unbanned_user_id", "Entbannter Nutzer ID");

        de_de.setProperty("log_boost_count_title", "Boost-Anzahl aktualisiert");
        de_de.setProperty("log_boost_tier_title", "Boost-Rang aktualisiert");
        de_de.setProperty("log_old_boost_count", "Alte Boostanzahl");
        de_de.setProperty("log_new_boost_count", "Neue Boostanzahl");
        de_de.setProperty("log_changing_perks", "Ändernde Perks");
        de_de.setProperty("log_emote_slots", "Emote Plätze");
        de_de.setProperty("log_max_bitrate", "Max Bitrate");

        de_de.setProperty("log_role_create_title", "Rolle erstellt");
        de_de.setProperty("log_role_delete_title", "Rolle gelöscht");
        de_de.setProperty("log_role_name", "Rollenname");

        de_de.setProperty("log_tc_create_title", "Textkanal erstellt");
        de_de.setProperty("log_tc_delete_title", "Textkanal deleted");
        de_de.setProperty("log_tc_name", "Textkanalname");

        de_de.setProperty("log_vc_join_title", "Sprachkanal beigetreten");
        de_de.setProperty("log_vc_move_title", "Sprachkanal gewechselt");
        de_de.setProperty("log_vc_leave_title", "Sprachkanal verlassen");
        de_de.setProperty("log_vc_create_title", "Sprachkanal erstellt");
        de_de.setProperty("log_vc_delete_title", "Sprachkanal gelöscht");
        de_de.setProperty("log_vc_name", "Sprachkanalname");

        // MediaOnlyChannel
        de_de.setProperty("mediaonlychannel_warning", "%s, dies ist ein MediaOnly-Kanal!\n" +
                "Du darfst:\n" +
                "- Dateien mit einer optionalen Beschreibung hochladen.\n" +
                "- Einen gültigen Link mit einer optionalen Beschreibung senden.\n" +
                "*Diese Nachricht wird in 30 Sekunden gelöscht.*");

        // ReactionRole
        de_de.setProperty("reactionrole_insufficient", "Ungenügende Berechtigungen oder ein Problem mit der Hierarchie.");

        // RemindMe
        de_de.setProperty("remindme_dm", "Ich habe deine Teilnahme an [dieser](%s) Erinnerung gezählt!");
        de_de.setProperty("remindme_dm_footer", "Falls dort mehr als 5000 Reaktionen vorhanden sind, mag der Counter verbuggt aussehen, aber ich habe dich dennoch mitgezählt!\"");

        // VoiceLobby
        de_de.setProperty("voicelobby_lobby", "Lobby");

        ///////////////////////////////////// Features /////////////////////////////////////

        // Achievement
        de_de.setProperty("achievement_console", "Konsole");
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
        de_de.setProperty("achievement_arenanet", "ArenaNet");

        // EasterEggs
        de_de.setProperty("eastereggs_you_are_welcome", "Sehr gerne, %s");

        // Invite
        de_de.setProperty("invite_author", "%s, zu Ihren Diensten!");
        de_de.setProperty("invite_description", "Danke Dir, dass Du mich gewählt hast, Dich und Deinen Server zu unterstützen.\n" +
                "Ich habe eine Menge Funktionen. Die meisten davon sind standardmäßig aktiviert, aber einige sind es nicht (z.B. das Leveling-System).\n" +
                "\n" +
                "Für den Start empfehle ich Dir, Dir die [Website](%s) anzusehen. Dort gibt es eine ausführliche Hilfe zu allen Befehlen, sowie ein Dashboard, in diesem Du alles mögliche einstellen oder an-/ausschalten kannst.\n" +
                "\n" +
                "Hilfsserver: [Klicke um beizutreten](%s)\n" +
                "\n" +
                "Viel Spaß!");
        de_de.setProperty("invite_footer", "Du hast diese Nachricht erhalten, da jemand mich zu Deinem Server (%s) eingeladen hat.");

        // Presence
        de_de.setProperty("presence_0", "v%s | %shelp");
        de_de.setProperty("presence_1", "%s Nutzer | %shelp");
        de_de.setProperty("presence_2", "%s Server | %shelp");
        de_de.setProperty("presence_3", "%ssupporter | %shelp");
        de_de.setProperty("presence_4", "DASHBOARD: %s");
        de_de.setProperty("presence_5", "#StayAtHome | %shelp");
        de_de.setProperty("presence_6", "#BLM | %shelp");

        // Supporter
        de_de.setProperty("supporter_supporter", "Unterstützer");
        de_de.setProperty("supporter_dono", "%s hat gerade $5+ über PayPal gespendet.\nDu bist jetzt ein %s!");
        de_de.setProperty("supporter_patron", "%s wurde gerade zum Patron.\nDu bist jetzt ein %s!");
        de_de.setProperty("supporter_boost", "%s hat gerade den Server geboostet.\nDu bist jetzt ein %s!");

        // UsageEmbed
        de_de.setProperty("usageembed_birthday_settings", "The birthday settings are now in the dashboard!");

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
