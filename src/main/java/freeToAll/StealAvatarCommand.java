package freeToAll;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import servant.Guild;
import servant.Log;
import servant.Servant;
import utilities.MessageHandler;
import utilities.Parser;
import utilities.UsageEmbed;

import java.awt.*;
import java.sql.SQLException;

public class StealAvatarCommand extends Command {
    public StealAvatarCommand() {
        this.name = "avatar";
        this.aliases = new String[]{"ava", "stealavatar", "stealava"};
        this.help = "returns mentioned user's avatar.";
        this.category = new Category("Free to all");
        this.arguments = "@user";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        // Enabled?
        try {
            if (event.getGuild() != null) if (!new servant.Guild(event.getGuild().getIdLong()).getToggleStatus("avatar")) return;
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }

        String prefix = Servant.config.getDefaultPrefix();
        // Usage
        if (event.getArgs().isEmpty()) {
            try {
                String usage = "**Stealing someones avatar**\n" +
                        "Command: `" + prefix + name + " [@user]`\n" +
                        "Example: `" + prefix + name + " @Servant`\n";

                event.reply(new UsageEmbed(name, event.getAuthor(), ownerCommand, userPermissions, aliases, usage, null).getEmbed());
            } catch (SQLException e) {
                new Log(e, event, name).sendLogSqlCommandEvent(true);
            }
            return;
        }

        // Check mentioned user.
        if (!Parser.hasMentionedUser(event.getMessage())) {
            event.reply("Invalid mention.");
            return;
        }

        Message message = event.getMessage();
        MessageChannel channel = message.getChannel();
        User author = message.getAuthor();
        User mentioned = message.getMentionedMembers().get(0).getUser();
        String avatarUrl = mentioned.getAvatarUrl();
        Color color = null;
        try {
            color = new servant.User(author.getIdLong()).getColor();
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(true);
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
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }
    }
}
