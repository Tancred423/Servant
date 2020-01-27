// Author: Tancred423 (https://github.com/Tancred423)
package moderation.joinleave;

import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.api.Permission;
import owner.blacklist.Blacklist;
import servant.Servant;
import utilities.Constants;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.concurrent.CompletableFuture;

public class LeaveMessageCommand extends Command {
    public LeaveMessageCommand() {
        this.name = "leavemessage";
        this.aliases = new String[] { "leavemsg" };
        this.help = "Description of leave message.";
        this.category = new Category("Moderation");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.GUILD;
        this.userPermissions = new Permission[] { Permission.MANAGE_CHANNEL };
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                if (!Toggle.isEnabled(event, name)) return;
                if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

                var author = event.getAuthor();
                var guild = event.getGuild();
                var internalGuild = new moderation.guild.Guild(guild.getIdLong());

                if (event.getArgs().isEmpty()) internalGuild.setLeaveMessage("empty", guild, author);
                else internalGuild.setLeaveMessage(event.getArgs(), guild, author);

                event.reactSuccess();

                // Statistics.
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
                if (event.getGuild() != null)
                    new moderation.guild.Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Servant.threadPool);
    }
}
