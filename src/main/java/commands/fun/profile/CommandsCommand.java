// Author: Tancred423 (https://github.com/Tancred423)
package commands.fun.profile;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import servant.MyGuild;
import servant.MyUser;
import utilities.Constants;
import utilities.ImageUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.awt.*;
import java.util.ArrayList;

public class CommandsCommand extends Command {
    public CommandsCommand() {
        this.name = "commands";
        this.aliases = new String[] { "mostusedcommands", "mostusedcommand", "muc" };
        this.help = "Displays a detailed list of your most used commands";
        this.category = new Category("Fun");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.modCommand = false;
        this.cooldown = 0;
        this.cooldownScope = CooldownScope.USER;
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var jda = event.getJDA();
        var user = event.getAuthor();
        var myUser = new MyUser(user);
        var message = event.getMessage();
        var lang = LanguageHandler.getLanguage(event);
        var prefix = event.getGuild() == null ? myUser.getPrefix() : new MyGuild(event.getGuild()).getPrefix();

        if (event.getGuild() != null && message.getMentionedMembers().size() > 0) {
            user = message.getMentionedMembers().get(0).getUser();
            myUser = new MyUser(user);
        }

        var featureEmotes = ImageUtil.getFeatureEmotes(jda);
        var featuresMap = myUser.getCommandCounts();

        var eb = new EmbedBuilder()
                .setColor(Color.decode(myUser.getColorCode()))
                .setAuthor(String.format(
                        LanguageHandler.get(lang, "commands_title"),
                        user.getName(),
                        user.getName().endsWith("s") ? LanguageHandler.get(lang, "apostrophe") : LanguageHandler.get(lang, "apostrophe_s")
                        ), null, user.getEffectiveAvatarUrl())
                .setDescription(LanguageHandler.get(lang, "commands_used") + ": " + myUser.getCommandsTotalCount());

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
        eb.setFooter(String.format(LanguageHandler.get(lang, "commands_footer"), Constants.WEBSITE_HELP));

        event.reply(eb.build());
    }
}
