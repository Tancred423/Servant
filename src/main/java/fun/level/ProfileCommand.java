// Author: Tancred423 (https://github.com/Tancred423)
package fun.level;

import files.language.LanguageHandler;
import fun.level.LevelImage;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import moderation.guild.Guild;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import servant.Log;
import servant.Servant;
import utilities.*;
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

public class ProfileCommand extends Command {
    public ProfileCommand() {
        this.name = "profile";
        this.aliases = new String[]{"level"};
        this.help = "Your or mentioned user's profile.";
        this.category = new Category("Fun");
        this.arguments = "[optional @user]";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[]{Permission.MESSAGE_ATTACH_FILES};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!Toggle.isEnabled(event, name)) return;
        var lang = LanguageHandler.getLanguage(event, name);
        var p = GuildHandler.getPrefix(event, name);

        event.getChannel().sendTyping().queue();

        var author = event.getAuthor();
        var internalAuthor = new User(author.getIdLong());
        var guild = event.getGuild();
        var profileUser = (event.getMessage().getMentionedMembers().isEmpty() ? author : event.getMessage().getMentionedMembers().get(0).getUser());
        var internalProfileUser = new User(profileUser.getIdLong());

        try {
            // Achievements
            Map<String, Integer> achievements;
            achievements = StringFormat.sortByValues( internalProfileUser.getAchievements());
            var achievementBuilder = new StringBuilder();
            achievementBuilder.append("**AP: ").append(internalProfileUser.getTotelAP()).append("**\n")
                    .append("```c\n").append(StringFormat.fillWithWhitespace(LanguageHandler.get(lang, "profile_name"), 23))
                    .append(" ")
                    .append(StringFormat.pushWithWhitespace(LanguageHandler.get(lang, "profile_ap"), 3))
                    .append("\n");
            achievementBuilder.append("-".repeat(23)).append(" ").append("-".repeat(3)).append("\n");
            for (Map.Entry<String, Integer> achievement : achievements.entrySet())
                achievementBuilder.append(StringFormat.fillWithWhitespace(Achievement.getFancyName(achievement.getKey(), lang), 23))
                        .append(" ")
                        .append(StringFormat.pushWithWhitespace(String.valueOf(achievement.getValue()), 3))
                        .append("\n");
            achievementBuilder.append("```");
            if (achievements.isEmpty()) achievementBuilder = new StringBuilder().append(LanguageHandler.get(lang, "profile_noachievements"));

            // Most used command
            Map<String, Integer> features = internalProfileUser.getTop10MostUsedFeatures();
            var top10Features = new StringBuilder();
            if (features.isEmpty()) top10Features.append(LanguageHandler.get(lang, "profile_nocommands"));
            else {
                top10Features.append("```c\n");
                top10Features.append(StringFormat.fillWithWhitespace(LanguageHandler.get(lang, "profile_name"), 16)).append(" ").append(StringFormat.pushWithWhitespace(LanguageHandler.get(lang, "profile_amount"), 10)).append("\n");
                top10Features.append("-".repeat(16)).append(" ").append("-".repeat(10)).append("\n");
                for (Map.Entry<String, Integer> feature : features.entrySet()) {
                    top10Features.append(StringFormat.fillWithWhitespace(feature.getKey(), 16))
                            .append(" ")
                            .append(StringFormat.pushWithWhitespace(String.valueOf(feature.getValue()), 10))
                            .append("\n");
                }
                top10Features.append("```");
            }

            // Baguette
            var baguette = internalProfileUser.getBaguette().entrySet().iterator().hasNext() ? internalProfileUser.getBaguette().entrySet().iterator().next() : null;

            // Description
            var bio = internalProfileUser.getBio();

            // Level
            // Create File.
            var image = new File(OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond() + ".png");

            try {
                var profile = new LevelImage(profileUser, event.getGuild(), lang);
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
            eb.setAuthor(profileUser.getName() + "#" + profileUser.getDiscriminator(), null, guild.getIconUrl());
            eb.setThumbnail(profileUser.getEffectiveAvatarUrl());
            eb.setDescription(bio);
            eb.addField(LanguageHandler.get(lang, "profile_baguettecounter"), baguette == null ?
                    LanguageHandler.get(lang, "profile_nobaguette") :
                    String.format(LanguageHandler.get(lang, "profile_baguette"), baguette.getKey(), baguette.getValue()), false);
            eb.addField(LanguageHandler.get(lang, "profile_mostused"), top10Features.toString(), false);
            eb.addField(LanguageHandler.get(lang, "profile_achievements"), achievementBuilder.toString(), false);
            eb.setImage("attachment://" + image.getPath());
            eb.setFooter(profileUser.equals(author) ?
                    String.format(LanguageHandler.get(lang, "profile_footer1"), p, name) :
                    String.format(LanguageHandler.get(lang, "profile_footer2"), p, name),
                    event.getSelfUser().getEffectiveAvatarUrl());

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
        } catch (Exception e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
        }

        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }
}
