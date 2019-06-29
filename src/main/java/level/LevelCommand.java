package level;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import servant.Guild;
import servant.Log;
import servant.Servant;
import utilities.Parser;
import utilities.StringFormat;
import utilities.UsageEmbed;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LevelCommand extends Command {
    public LevelCommand() {
        this.name = "level";
        this.aliases = new String[]{"lvl", "experience", "exp"};
        this.help = "check your level or the current guild's leaderboard";
        this.category = new Category("Free to all");
        this.arguments = "[show, @user or \"leaderboard\"]";
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        // Enabled?
        try {
            if (!new servant.Guild(event.getGuild().getIdLong()).getToggleStatus("level")) return;
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlReceiveEvent(false);
            return;
        }

        net.dv8tion.jda.core.entities.Guild guild = event.getGuild();
        servant.Guild internalGuild;
        try {
            internalGuild = new Guild(guild.getIdLong());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(true);
            return;
        }
        String prefix = Servant.config.getDefaultPrefix();
        // Usage
        if (event.getArgs().isEmpty()) {
            try {
                String usage = "**Show your own current level**\n" +
                        "Command: `" + prefix + name + " show`\n" +
                        "\n" +
                        "**Show someone else's current level**\n" +
                        "Command: `" + prefix + name + " [@user]`\n" +
                        "Example: `" + prefix + name + " @Servant`\n" +
                        "\n" +
                        "**Showing guild's current leaderboard**\n" +
                        "Command: `" + prefix + name + " leaderboard`";

                String hint = "You will get 15 - 25 exp inclusively per message (" + Servant.config.getExpCdMillis() + "ms CD)\n" +
                        "Aliases for `leaderboard`: `leaderboards`, `lb` and `lbs`.";

                event.reply(new UsageEmbed(name, event.getAuthor(), ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            } catch (SQLException e) {
                new Log(e, event, name).sendLogSqlCommandEvent(true);
            }
            return;
        }

        User author = event.getAuthor();
        servant.User internalAuthor;
        try {
            internalAuthor = new servant.User(author.getIdLong());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(true);
            return;
        }

        String arg = event.getArgs();
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
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
                    return;
                }
                currentLevel = Parser.getLevelFromExp(currentExp);
                neededExp = Parser.getLevelExp(currentLevel);
                currentExpOnThisLevel = currentExp - Parser.getTotalLevelExp(currentLevel - 1);

                event.reply(author.getAsMention() + "'s current level: " + currentLevel + " (" + currentExpOnThisLevel + "/" + neededExp + " XP)");
                break;

            case "leaderboard":
            case "leaderboards":
            case "lb":
            case "lbs":
                Map<Long, Integer> userExp;
                try {
                    userExp = internalGuild.getLeaderboard();
                } catch (SQLException e) {
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
                    return;
                }
                if (userExp == null) {
                    event.reply("Leaderboard is empty");
                    return;
                }

                StringBuilder leaderboard = new StringBuilder();
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
                    Message message = event.getMessage();
                    User mentioned = message.getMentionedMembers().get(0).getUser();

                    if (mentioned.isBot()) {
                        event.reply("Bots cannot collect experience.");
                        return;
                    }

                    servant.User internalMentioned;
                    try {
                        internalMentioned = new servant.User(mentioned.getIdLong());
                    } catch (SQLException e) {
                        new Log(e, event, name).sendLogSqlCommandEvent(true);
                        return;
                    }
                    try {
                        currentExp = internalMentioned.getExp(event.getGuild().getIdLong());
                    } catch (SQLException e) {
                        new Log(e, event, name).sendLogSqlCommandEvent(true);
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
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }
    }
}
