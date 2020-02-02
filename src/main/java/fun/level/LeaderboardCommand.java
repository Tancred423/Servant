// Author: Tancred423 (https://github.com/Tancred423)
package fun.level;

import files.language.LanguageHandler;
import moderation.guild.Server;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import utilities.Parser;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.ArrayList;

public class LeaderboardCommand extends Command {
    public LeaderboardCommand() {
        this.name = "leaderboard";
        this.aliases = new String[] { "levelranking" };
        this.help = "Displays the level ranking of the current server.";
        this.category = new Category("Fun");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = 0;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[0];
    }

    @Override
    protected void execute(CommandEvent event) {
        var guild = event.getGuild();
        var server = new Server(guild);
        var user = event.getAuthor();
        var master = new Master(user);
        var lang = master.getLanguage();

        var leaderboard = server.getLeaderboard();

        var eb = new EmbedBuilder()
                .setColor(master.getColor())
                .setAuthor(String.format(
                        LanguageHandler.get(lang, "leaderboard_title"),
                        guild.getName(),
                        guild.getName().endsWith("s") ? LanguageHandler.get(lang, "apostrophe") : LanguageHandler.get(lang, "apostrophe_s")
                ), null, guild.getIconUrl());

        var sb = new StringBuilder();
        var fieldValues = new ArrayList<String>();

        var ranking = 1; // Ranking placement
        for (var entry : leaderboard.entrySet()) {
            /* Max length for a field is 1024. 100 is the max length of one row.
             * In case this list is getting too long, we will make a new one.
             */
            if (sb.length() >= 1024 - 100) {
                fieldValues.add(sb.toString());
                sb = new StringBuilder();
            }

            var member = guild.getMemberById(entry.getKey());
            if (member != null) {
                var currentExp = entry.getValue();
                var currentLevel = Parser.getLevelFromExp(currentExp);
                var neededExp = Parser.getLevelExp(currentLevel);
                var currentExpOnThisLevel = currentExp - Parser.getTotalLevelExp(currentLevel - 1);
                sb.append(ranking).append(". ").append(member.getAsMention()).append(": ").append(LanguageHandler.get(lang, "profile_level")).append(" ").append(currentLevel).append(" (").append(currentExpOnThisLevel).append("/").append(neededExp).append(")\n");
                ranking++;
            }
        }

        fieldValues.add(sb.toString());

        var i = 0;
        for (var fieldValue : fieldValues) {
            if (i == 24) break; // Max 25 fields
            eb.addField(" ", fieldValue, true);
            i++;
        }

        event.reply(eb.build());
    }
}
