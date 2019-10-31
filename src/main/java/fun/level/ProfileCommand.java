// Author: Tancred423 (https://github.com/Tancred423)
package fun.level;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import moderation.guild.Guild;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import owner.blacklist.Blacklist;
import servant.Log;
import utilities.*;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import javax.imageio.ImageIO;
import java.awt.geom.NoninvertibleTransformException;
import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
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
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, name)) return;
            if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;
            var lang = LanguageHandler.getLanguage(event);
            var p = GuildHandler.getPrefix(event);

            event.getChannel().sendTyping().queue();

            var author = event.getAuthor();
            var internalAuthor = new User(author.getIdLong());
            var guild = event.getGuild();
            var profileUser = (event.getMessage().getMentionedMembers().isEmpty() ? author : event.getMessage().getMentionedMembers().get(0).getUser());
            var internalProfileUser = new User(profileUser.getIdLong());

            try {
                // Achievements
                var achievements = internalProfileUser.getAchievements(guild, author);
                var achievementsWithName = new TreeMap<String, Integer>();
                for (var achievement : achievements.entrySet())
                    achievementsWithName.put(Achievement.getFancyName(achievement.getKey(), lang), achievement.getValue());
                achievementsWithName = StringFormat.sortByKey(achievementsWithName);

                var achievementBuilder = new StringBuilder();
                achievementBuilder.append("**AP: ").append(internalProfileUser.getTotelAP(guild, author)).append("**\n")
                        .append("```c\n").append(StringFormat.fillWithWhitespace(LanguageHandler.get(lang, "profile_name"), 23))
                        .append(" ")
                        .append(StringFormat.pushWithWhitespace(LanguageHandler.get(lang, "profile_ap"), 3))
                        .append("\n");
                achievementBuilder.append("-".repeat(23)).append(" ").append("-".repeat(3)).append("\n");
                for (var achievement : achievementsWithName.entrySet())
                    achievementBuilder.append(StringFormat.fillWithWhitespace(Achievement.getFancyName(achievement.getKey(), lang), 23))
                            .append(" ")
                            .append(StringFormat.pushWithWhitespace(String.valueOf(achievement.getValue()), 3))
                            .append("\n");
                achievementBuilder.append("```");
                if (achievementsWithName.isEmpty()) achievementBuilder = new StringBuilder().append(LanguageHandler.get(lang, "profile_noachievements"));

                // Most used command
                var features = internalProfileUser.getTop10MostUsedFeatures(guild, author);
                var top10Features = new StringBuilder();
                if (features.isEmpty()) top10Features.append(LanguageHandler.get(lang, "profile_nocommands"));
                else {
                    top10Features.append("```c\n");
                    top10Features.append(StringFormat.fillWithWhitespace(LanguageHandler.get(lang, "profile_name"), 16)).append(" ").append(StringFormat.pushWithWhitespace(LanguageHandler.get(lang, "profile_amount"), 10)).append("\n");
                    top10Features.append("-".repeat(16)).append(" ").append("-".repeat(10)).append("\n");
                    for (var feature : features.entrySet()) {
                        top10Features.append(StringFormat.fillWithWhitespace(feature.getKey(), 16))
                                .append(" ")
                                .append(StringFormat.pushWithWhitespace(String.valueOf(feature.getValue()), 10))
                                .append("\n");
                    }
                    top10Features.append("```");
                }

                // Baguette
                var baguette = internalProfileUser.getBaguette(guild, author).entrySet().iterator().hasNext() ? internalProfileUser.getBaguette(guild, author).entrySet().iterator().next() : null;

                // Description
                var bio = internalProfileUser.getBio(guild, author);

                // Level
                // Create File.
                var image = new File(OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond() + ".png");

                try {
                    var profile = new LevelImage(profileUser, event.getGuild(), lang);
                    ImageIO.write(profile.getImage(), "png", image);
                } catch (IOException | NoninvertibleTransformException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    return;
                }

                var eb = new EmbedBuilder();
                eb.setColor(internalAuthor.getColor(guild, author));
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
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
        });
    }
}
