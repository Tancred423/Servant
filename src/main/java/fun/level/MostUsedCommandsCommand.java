// Author: Tancred423 (https://github.com/Tancred423)
package fun.level;

import files.language.LanguageHandler;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import utilities.ImageUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.ArrayList;

public class MostUsedCommandsCommand extends Command {
    public MostUsedCommandsCommand() {
        this.name = "mostusedcommands";
        this.aliases = new String[] { "mostusedcommand", "muc" };
        this.help = "Displays a detailed list of your most used commands.";
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

        var featureEmotes = ImageUtil.getFeatureEmotes(jda);
        var featuresMap = master.getFeatureCounts();

        var eb = new EmbedBuilder()
                .setColor(master.getColor())
                .setAuthor(String.format(
                        LanguageHandler.get(lang, "mostusedcommands_title"),
                        user.getName(),
                        user.getName().endsWith("s") ? LanguageHandler.get(lang, "apostrophe") : LanguageHandler.get(lang, "apostrophe_s")
                        ), null, user.getEffectiveAvatarUrl())
                .setDescription(LanguageHandler.get(lang, "profile_total_muc") + ": " + master.getTotalFeatureCount());

        var sb = new StringBuilder();
        var fieldValues = new ArrayList<String>();

        for (var entry : featuresMap.entrySet()) {
            /* Max length for a field is 1024. 100 is the max length of one row.
             * In case this list is getting too long, we will make a new one.
             */
            if (sb.length() >= 1024 - 100) {
                fieldValues.add(sb.toString());
                sb = new StringBuilder();
            }

            var emote = featureEmotes.get(entry.getKey());
            if (emote != null) sb.append(emote.getAsMention()).append(" ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" ").append(LanguageHandler.get(lang, "times")).append("\n");
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