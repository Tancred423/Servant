// Author: Tancred423 (https://github.com/Tancred423)
package freeToAll.level;

import net.dv8tion.jda.core.Permission;
import moderation.guild.Guild;
import servant.Log;
import servant.Servant;
import utilities.Parser;
import utilities.StringFormat;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;
import java.util.Map;

public class LevelCommand extends Command {
    public LevelCommand() {
        this.name = "level";
        this.aliases = new String[]{"lvl", "experience", "exp"};
        this.help = "Check levels.";
        this.category = new Category("Free to all");
        this.arguments = "[show|@user|leaderboard]";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = 5;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[0];
    }

    @Override
    protected void execute(CommandEvent event) {
        // Enabled?
        try {
            if (!new Guild(event.getGuild().getIdLong()).getToggleStatus("level")) return;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            return;
        }

        var author = event.getAuthor();
        var internalAuthor = new servant.User(author.getIdLong());
        var guild = event.getGuild();
        var internalGuild = new Guild(guild.getIdLong());
        var prefix = Servant.config.getDefaultPrefix();
        // Usage
        if (event.getArgs().isEmpty()) {
            try {
                var description = "You are leveling up by being active in chat.\n" +
                        "You can look up your or someone else's level or you can view the current guild's leaderboard.";

                var usage = "**Show your own current level**\n" +
                        "Command: `" + prefix + name + " show`\n" +
                        "\n" +
                        "**Show someone else's current level**\n" +
                        "Command: `" + prefix + name + " [@user]`\n" +
                        "Example: `" + prefix + name + " @Servant`\n" +
                        "\n" +
                        "**Showing guild's current leaderboard**\n" +
                        "Command: `" + prefix + name + " leaderboard`";

                var hint = "You will get 15 - 25 exp inclusively per message (" + Servant.config.getExpCdMillis() + "ms CD)\n" +
                        "Aliases for `leaderboard`: `leaderboards`, `lb` and `lbs`.";

                event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            } catch (SQLException e) {
                new Log(e, guild, author, name, event).sendLog(true);
            }
            return;
        }

        var arg = event.getArgs();
        int currentExp;
        int currentLevel;
        int neededExp;
        int currentExpOnThisLevel;

        switch (arg.toLowerCase()) {
            case "show":
            case "sh":
                try {
                    currentExp = internalAuthor.getExp(event.getGuild().getIdLong());
                } catch (SQLException e) {
                    new Log(e, guild, author, name, event).sendLog(true);
                    return;
                }
                currentLevel = Parser.getLevelFromExp(currentExp);
                neededExp = Parser.getLevelExp(currentLevel);
                currentExpOnThisLevel = currentExp - Parser.getTotalLevelExp(currentLevel - 1);

                event.reply(author.getAsMention() + (event.getMember().getNickname().endsWith("s") ? "'" : "'s") + " current level: " + currentLevel + " (" + currentExpOnThisLevel + "/" + neededExp + " XP)");
                break;

            case "leaderboard":
            case "leaderboards":
            case "lb":
            case "lbs":
                Map<Long, Integer> userExp;
                try {
                    userExp = internalGuild.getLeaderboard();
                } catch (SQLException e) {
                    new Log(e, guild, author, name, event).sendLog(true);
                    return;
                }
                if (userExp == null) {
                    event.reply("Leaderboard is empty");
                    return;
                }

                var leaderboard = new StringBuilder();
                leaderboard
                        .append("```c\n")
                        .append("Name                             Level     EXP\n")
                        .append("-------------------------------- ----- -------\n");

                for (Map.Entry<Long, Integer> entry : userExp.entrySet())
                    leaderboard
                            .append(StringFormat.fillWithWhitespace(event.getJDA().getUserById(entry.getKey()).getName(), 32))
                            .append(" ")
                            .append(StringFormat.pushWithWhitespace(String.valueOf(Parser.getLevelFromExp(entry.getValue())), 5))
                            .append(" ")
                            .append(StringFormat.pushWithWhitespace(entry.getValue().toString(), 7))
                            .append("\n");

                leaderboard.append("```");

                event.reply(leaderboard.toString());
                break;

            default:
                if (Parser.hasMentionedUser(event.getMessage())) {
                    // Show mentioned user's level.
                    var message = event.getMessage();
                    var mentioned = message.getMentionedMembers().get(0).getUser();

                    if (mentioned.isBot()) {
                        event.reply("Bots cannot collect experience.");
                        return;
                    }

                    var internalMentioned = new servant.User(mentioned.getIdLong());
                    try {
                        currentExp = internalMentioned.getExp(event.getGuild().getIdLong());
                    } catch (SQLException e) {
                        new Log(e, guild, author, name, event).sendLog(true);
                        return;
                    }
                    currentLevel = Parser.getLevelFromExp(currentExp);
                    neededExp = Parser.getLevelExp(currentLevel);
                    currentExpOnThisLevel = currentExp - Parser.getTotalLevelExp(currentLevel - 1);

                    event.reply(mentioned.getAsMention() + "'s current level: " + currentLevel + " (" + currentExpOnThisLevel + "/" + neededExp + " XP)");
                } else {
                    event.reply("Invalid argument.\n" +
                            "Type `show` to view your own level, mention someone to view their level or type \"leaderboard\" to check the current guild's leaderboard.");
                }
                break;
        }

        // Statistics.
        try {
            new servant.User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }
}
