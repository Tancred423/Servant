// Author: Tancred423 (https://github.com/Tancred423)
package fun.flip;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import servant.Log;
import utilities.Constants;
import utilities.StringFormat;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;

public class FlipCommand extends Command {
    public FlipCommand() {
        this.name = "flip";
        this.aliases = new String[0];
        this.help = "Flip a user.";
        this.category = new Category("Fun");
        this.arguments = "@user";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[]{Permission.NICKNAME_MANAGE};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!Toggle.isEnabled(event, name)) return;

        var message = event.getMessage();
        var lang = LanguageHandler.getLanguage(event, name);
        var p = GuildHandler.getPrefix(event, name);

        if (message.getMentionedMembers().isEmpty()) {
            try {
                var description = LanguageHandler.get(lang, "flip_description");
                var usage = String.format(LanguageHandler.get(lang, "flip_usage"), p, name);
                var hint = String.format(LanguageHandler.get(lang, "flip_hint"), p);
                event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            }
            return;
        }

        var mentioned = message.getMentionedMembers().get(0);
        var effectiveName = mentioned.getEffectiveName();
        var flipped = StringFormat.flipString(effectiveName);
        event.reply("(╯°□°)╯︵ " + flipped);
        if (event.getGuild().getMemberById(event.getSelfUser().getIdLong()).canInteract(mentioned))
            event.getGuild().getController().setNickname(mentioned, flipped).queue();


        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }
}
