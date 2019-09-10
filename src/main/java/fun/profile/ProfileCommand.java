// Author: Tancred423 (https://github.com/Tancred423)
package fun.profile;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import moderation.guild.Guild;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import patreon.PatreonHandler;
import servant.Log;
import servant.Servant;
import utilities.*;
import utilities.Image;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.awt.*;
import java.sql.SQLException;
import java.util.Map;

public class ProfileCommand extends Command {
    public ProfileCommand() {
        this.name = "profile";
        this.aliases = new String[0];
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
        var internalGuild = new Guild(guild.getIdLong());
        var profileUser = (event.getMessage().getMentionedMembers().isEmpty() ? author : event.getMessage().getMentionedMembers().get(0).getUser());
        var internalProfileUser = new User(profileUser.getIdLong());

        try {
            // Level
            // Level Percentage Bar
            var currentExp = internalProfileUser.getExp(guild.getIdLong());
            var currentLevel = Parser.getLevelFromExp(currentExp);
            var neededExp = Parser.getLevelExp(currentLevel);
            var currentExpOnThisLevel = currentExp - Parser.getTotalLevelExp(currentLevel - 1);

            // Achievements
            Map<String, Integer> achievements;
            achievements = internalProfileUser.getAchievements();
            var achievementBuilder = new StringBuilder();
            achievementBuilder.append("AP: ").append(internalProfileUser.getTotelAP()).append("\n")
                    .append("```c\n").append(StringFormat.fillWithWhitespace(LanguageHandler.get(lang, "profile_name"), 32))
                    .append(" ")
                    .append(StringFormat.pushWithWhitespace(LanguageHandler.get(lang, "profile_ap"), 5))
                    .append("\n");
            achievementBuilder.append("-".repeat(32)).append(" ").append("-".repeat(5)).append("\n");
            for (Map.Entry<String, Integer> achievement : achievements.entrySet())
                achievementBuilder.append(StringFormat.fillWithWhitespace(Achievement.getFancyName(achievement.getKey(), lang), 32))
                        .append(" ")
                        .append(StringFormat.pushWithWhitespace(String.valueOf(achievement.getValue()), 5))
                        .append("\n");
            achievementBuilder.append("```");
            if (achievements.isEmpty()) achievementBuilder = new StringBuilder().append(LanguageHandler.get(lang, "profile_noachievements"));

            // Most used command
            Map<String, Integer> mostUsedFeature = internalProfileUser.getMostUsedFeature();
            String text;
            if (mostUsedFeature.isEmpty()) text = LanguageHandler.get(lang, "profile_nocommands");
            else {
                Map.Entry<String, Integer> entry = mostUsedFeature.entrySet().iterator().next();
                text = String.format(LanguageHandler.get(lang, "profile_mostused_value"), p, entry.getKey(), entry.getValue());
            }

            // Baguette
            var baguette = internalProfileUser.getBaguette().entrySet().iterator().hasNext() ? internalProfileUser.getBaguette().entrySet().iterator().next() : null;

            // Description
            var desc = JsonReader.readJsonFromUrl("https://complimentr.com/api").getString("compliment");
            desc = desc.substring(0, 1).toUpperCase() + desc.substring(1).toLowerCase() + ".";

            var eb = new EmbedBuilder();
            try {
                eb.setColor(internalAuthor.getColor());
            } catch (SQLException e) {
                eb.setColor(Color.decode(Servant.config.getDefaultColorCode()));
            }
            eb.setAuthor(profileUser.getName() + "#" + profileUser.getDiscriminator(), null, guild.getIconUrl());
            eb.setThumbnail(profileUser.getEffectiveAvatarUrl());
            eb.setDescription(desc);
            eb.addField(LanguageHandler.get(lang, "profile_level"), currentLevel + " (" + currentExpOnThisLevel + "/" + neededExp + ")\n" +
                   String.format(LanguageHandler.get(lang, "profile_rank"),  internalGuild.getUserRank(profileUser.getIdLong())), false);
            eb.addField(LanguageHandler.get(lang, "profile_mostused"), text, true);
            eb.addField(LanguageHandler.get(lang, "profile_baguettecounter"), baguette == null ?
                    LanguageHandler.get(lang, "profile_nobaguette") :
                    String.format(LanguageHandler.get(lang, "profile_baguette"), baguette.getKey(), baguette.getValue()), true);
            eb.addField(LanguageHandler.get(lang, "profile_achievements"), achievementBuilder.toString(), false);
            eb.setImage(Image.getImageUrl(PatreonHandler.getPatreonRank(profileUser)));
            eb.setFooter(profileUser.equals(author) ?
                    String.format(LanguageHandler.get(lang, "profile_footer1"), p, name) :
                    String.format(LanguageHandler.get(lang, "profile_footer2"), p, name),
                    event.getSelfUser().getEffectiveAvatarUrl());
            event.reply(eb.build());
        } catch (Exception e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
        }

        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }
}
