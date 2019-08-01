// Author: Tancred423 (https://github.com/Tancred423)
package servant;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class Log {
    private Exception e;
    private Guild guild;
    private User user;
    private String name;
    private CommandEvent commandEvent;

    public Log(Exception e, Guild guild, User user, String featureName, CommandEvent commandEvent) {
        this.e = e;
        this.guild = guild;
        this.user = user;
        this.name = featureName;
        this.commandEvent = commandEvent;
    }

    // Send generic log
    public void sendLog(boolean notifyUser) {
        if (commandEvent != null) commandEvent.reactWarning();
        if (notifyUser && commandEvent != null) commandEvent.reply("Something went wrong, ...!\n" + "A report was sent to the bot owner.");
        if (e != null) e.printStackTrace();
        Servant.jda.getUserById(Servant.config.getBotOwnerId()).openPrivateChannel().queue(privateChannel ->
                privateChannel.sendMessage("```c\n" +
                        "Error\n" +
                        "-----\n" +
                        (guild == null ? "" : "Guild: " + guild.getName() + " (" + guild.getIdLong() + ")\n") +
                        (user == null ? "" : "User: " + user.getName() + " (" + user.getIdLong() + ")\n") +
                        (name == null ? "" : "Command: " + name + "\n") +
                        (e == null ? "" : "Error: " + e.getMessage() + "\n") +
                        "```").queue());
    }
}
