// Author: Tancred423 (https://github.com/Tancred423)
package moderation.joinleave;

import moderation.guild.Server;
import net.dv8tion.jda.api.Permission;
import utilities.Constants;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

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
        var guild = event.getGuild();
        var server = new Server(guild);

        if (event.getArgs().isEmpty()) server.setLeaveMessage("empty");
        else server.setLeaveMessage(event.getArgs());

        event.reactSuccess();
    }
}
