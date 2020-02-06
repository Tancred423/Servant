// Author: Tancred423 (https://github.com/Tancred423)
package servant;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class Log {
    private Exception e;
    private Guild guild;
    private User user;
    private String function;
    private CommandEvent commandEvent;

    public Log(Exception e, Guild guild, User user, String function, CommandEvent commandEvent) {
        this.e = e;
        this.guild = guild;
        this.user = user;
        this.function = function;
        this.commandEvent = commandEvent;
    }

    // Send generic log
    public void sendLog(boolean notifyUser) {
        if (commandEvent != null) commandEvent.reactWarning();
        if (notifyUser && commandEvent != null) commandEvent.reply("Something went wrong, master!\n" + "A report was sent to the bot owner.");
        if (e != null) e.printStackTrace();
        var jda = user == null ? (guild == null ? (commandEvent == null ? null : commandEvent.getJDA()) : guild.getJDA()) : user.getJDA();
        if (jda != null) {
            var botOwner = guild.getJDA().getUserById(Servant.config.getBotOwnerId());
            if (botOwner != null) {
                botOwner.openPrivateChannel().queue(privateChannel ->
                        privateChannel.sendMessage("```c\n" +
                                "Error\n" +
                                "-----\n" +
                                (guild == null ? "" : "Guild: " + guild.getName() + " (" + guild.getIdLong() + ")\n") +
                                (user == null ? "" : "User: " + user.getName() + " (" + user.getIdLong() + ")\n") +
                                (function == null ? "" : "Function: " + function + "\n") +
                                (e == null ? "" : "Error: " + e.getMessage() + "\n") +
                                "```").queue());
            }
        }
    }
}
