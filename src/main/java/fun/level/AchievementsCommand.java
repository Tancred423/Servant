// Author: Tancred423 (https://github.com/Tancred423)
package fun.level;

import files.language.LanguageHandler;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import utilities.Achievement;
import utilities.Image;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.ArrayList;

public class AchievementsCommand extends Command {
    public AchievementsCommand() {
        this.name = "achievements";
        this.aliases = new String[] { "achievement" };
        this.help = "Displays a detailed list of your achievements.";
        this.category = new Category("Fun");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.cooldown = 0;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var jda = event.getJDA();
        var user = event.getAuthor();
        var master = new Master(user);
        var message = event.getMessage();
        var lang = master.getLanguage();

        if (message.getMentionedMembers().size() > 0) {
            user = message.getMentionedMembers().get(0).getUser();
            master = new Master(user);
        }

        var achievementEmotes = Image.getAchievementsEmotes(jda);
        var achievementsMap = master.getAchievements();

        var eb = new EmbedBuilder()
                .setColor(master.getColor())
                .setAuthor(String.format(
                        LanguageHandler.get(lang, "achievements_title"),
                        user.getName(),
                        user.getName().endsWith("s") ? LanguageHandler.get(lang, "apostrophe") : LanguageHandler.get(lang, "apostrophe_s")
                        ), null, user.getEffectiveAvatarUrl())
                .setDescription(LanguageHandler.get(lang, "profile_total_ap") + ": " + master.getTotalAP());

        var sb = new StringBuilder();
        var fieldValues = new ArrayList<String>();

        for (var entry : achievementsMap.entrySet()) {
            /* Max length for a field is 1024. 100 is the max length of one row.
             * In case this list is getting too long, we will make a new one.
             */
            if (sb.length() >= 1024 - 100) {
                fieldValues.add(sb.toString());
                sb = new StringBuilder();
            }

            var emote = achievementEmotes.get(entry.getKey());
            if (emote != null) sb.append(emote.getAsMention()).append(" ").append(Achievement.getFancyName(jda, entry.getKey(), lang)).append(": ").append(entry.getValue()).append(" AP\n");
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
