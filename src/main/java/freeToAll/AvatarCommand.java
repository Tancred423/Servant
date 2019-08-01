// Author: Tancred423 (https://github.com/Tancred423)
package freeToAll;

import net.dv8tion.jda.core.Permission;
import moderation.guild.Guild;
import servant.Log;
import servant.Servant;
import utilities.MessageHandler;
import utilities.Parser;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.awt.*;
import java.sql.SQLException;

public class AvatarCommand extends Command {
    public AvatarCommand() {
        this.name = "avatar";
        this.aliases = new String[]{"ava", "stealavatar", "stealava"};
        this.help = "Returns mentioned user's avatar.";
        this.category = new Category("Free to all");
        this.arguments = "@user";
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.cooldown = 5;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void execute(CommandEvent event) {
        // Enabled?
        try {
            if (event.getGuild() != null) if (!new Guild(event.getGuild().getIdLong()).getToggleStatus("avatar")) return;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }

        var prefix = Servant.config.getDefaultPrefix();
        // Usage
        if (event.getArgs().isEmpty()) {
            try {
                var description = "Steal someone's avatar.";

                var usage = "Command: `" + prefix + name + " @user`\n";

                event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, null).getEmbed());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            }
            return;
        }

        // Check mentioned user.
        if (!Parser.hasMentionedUser(event.getMessage())) {
            event.reply("Invalid mention.");
            return;
        }

        var message = event.getMessage();
        var channel = message.getChannel();
        var author = message.getAuthor();
        var mentioned = message.getMentionedMembers().get(0).getUser();
        var avatarUrl = mentioned.getAvatarUrl();
        Color color;
        try {
            color = new servant.User(author.getIdLong()).getColor();
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            return;
        }

        new MessageHandler().sendEmbed(
                channel,
                color,
                "Stealing Avatar", null, null,
                null,
                null,
                author.getAsMention() + " just stole " + mentioned.getAsMention() + "'s avatar!",
                null,
                avatarUrl,
                null,
                null
        );

        // Statistics.
        try {
            new servant.User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }
}
