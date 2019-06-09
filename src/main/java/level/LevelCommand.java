package level;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import servant.Guild;
import servant.Log;
import utilities.Parser;
import utilities.StringFormat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CommandInfo(
        name = {"Level", "Lvl", "Experience", "Exp"},
        description = "Check your level or the current guild's leaderboard!",
        usage = "level [blank, \"leaderboard\" or mention]"
)
@Author("Tancred")
public class LevelCommand extends Command {
    public LevelCommand() {
        this.name = "level";
        this.aliases = new String[]{"lvl", "experience", "exp"};
        this.help = "check your level or the current guild's leaderboard";
        this.category = new Category("Level");
        this.arguments = "[blank, \"leaderboard\" or mention]";
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        User author = event.getAuthor();
        servant.User internalAuthor;
        try {
            internalAuthor = new servant.User(author.getIdLong());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(true);
            return;
        }

        String arg = event.getArgs();

        if (arg.isEmpty()) {
            // Show own level.
            int currentExp;
            try {
                currentExp = internalAuthor.getExp(event.getGuild().getIdLong());
            } catch (SQLException e) {
                new Log(e, event, name).sendLogSqlCommandEvent(true);
                return;
            }
            int currentLevel = Parser.getLevelFromExp(currentExp);
            int neededExp = Parser.getLevelExp(currentLevel);
            int currentExpOnThisLevel = currentExp - Parser.getTotalLevelExp(currentLevel - 1);

            event.reply(author.getAsMention() + "'s current level: " + currentLevel + " (" + currentExpOnThisLevel + "/" + neededExp + " XP)");
        } else {
            String[] args = arg.split(" ");
            List<String> aliasLeaderboard = new ArrayList<>() {{
                add("leaderboard");
                add("leaderboards");
                add("lb");
                add("lbs");
            }};

            if (aliasLeaderboard.contains(args[0].toLowerCase())) {
                // Show leaderboard.
                Guild internalGuild = new Guild(event.getGuild().getIdLong());
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
            } else if (Parser.hasMentionedUser(event.getMessage())) {
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
                int currentExp;
                try {
                    currentExp = internalMentioned.getExp(event.getGuild().getIdLong());
                } catch (SQLException e) {
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
                    return;
                }
                int currentLevel = Parser.getLevelFromExp(currentExp);
                int neededExp = Parser.getLevelExp(currentLevel);
                int currentExpOnThisLevel = currentExp - Parser.getTotalLevelExp(currentLevel - 1);

                event.reply(mentioned.getAsMention() + "'s current level: " + currentLevel + " (" + currentExpOnThisLevel + "/" + neededExp + " XP)");
            } else {
                event.reply("Invalid argument.\n" +
                        "Leave it blank to view your own level, mention someone to view their level or type \"leaderboard\" to check the current guild's leaderboard.");
            }
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
