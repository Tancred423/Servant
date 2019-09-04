// Author: Tancred423 (https://github.com/Tancred423)
package fun.level;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import moderation.guild.Guild;
import servant.Log;
import servant.Servant;
import utilities.Parser;
import utilities.StringFormat;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.NoninvertibleTransformException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LevelCommand extends Command {
    public LevelCommand() {
        this.name = "level";
        this.aliases = new String[]{"rank"};
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
        if (!Toggle.isEnabled(event, name)) return;

        var lang = LanguageHandler.getLanguage(event, name);
        var p = GuildHandler.getPrefix(event, name);

        var author = event.getAuthor();
        var internalAuthor = new User(author.getIdLong());
        var guild = event.getGuild();
        var internalGuild = new Guild(guild.getIdLong());

        if (event.getArgs().equalsIgnoreCase("leaderboard") || event.getArgs().equalsIgnoreCase("lb")) {
            Map<Long, Integer> userExp;
            try {
                userExp = internalGuild.getLeaderboard();
            } catch (SQLException e) {
                new Log(e, guild, author, name, event).sendLog(true);
                return;
            }

            if (userExp == null) {
                event.reply(LanguageHandler.get(lang, "level_leaderboard_empty"));
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

            var eb = new EmbedBuilder();
            try {
                eb.setColor(internalAuthor.getColor());
            } catch (SQLException e) {
                eb.setColor(Color.decode(Servant.config.getDefaultColorCode()));
            }
            eb.setAuthor(String.format(LanguageHandler.get(lang, "level_leaderboard_authorname"), guild.getName()), null, guild.getIconUrl());
            eb.setFooter(String.format(LanguageHandler.get(lang, "level_leaderboard_footer"), p, p), event.getSelfUser().getAvatarUrl());
            eb.setDescription(leaderboard.toString());
            event.reply(eb.build());
            return;
        }

        var mentioned = (event.getMessage().getMentionedMembers().isEmpty() ? null : event.getMessage().getMentionedMembers().get(0).getUser());

        // Create File.
        var imageDir = OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond() + ".png";
        var image = new File(imageDir);

        try {
            var profile = new LevelImage((mentioned == null ? author : mentioned), event.getGuild(), lang);
            ImageIO.write(profile.getImage(), "png", image);
        } catch (IOException | SQLException | NoninvertibleTransformException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            return;
        }

        var eb = new EmbedBuilder();
        try {
            eb.setColor(internalAuthor.getColor());
        } catch (SQLException e) {
            eb.setColor(Color.decode(Servant.config.getDefaultColorCode()));
        }
        eb.setImage("attachment://" + image.getPath());
        eb.setFooter(String.format(LanguageHandler.get(lang, "level_footer"), (mentioned == null ? "\"" + p + "level @user\"" : "\"" + p + "level\""), p), event.getSelfUser().getAvatarUrl());
        event.getChannel().sendFile(image, image.getPath()).embed(eb.build()).queue();

        // Delete File.
        var thread = new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(10000); // 10 seconds.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!image.delete()) new Log(null, event.getGuild(), event.getAuthor(), name, null).sendLog(false);
        });

        thread.start();

        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }
}
