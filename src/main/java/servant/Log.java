package servant;

import com.jagrosh.jdautilities.command.CommandEvent;
import config.ConfigFile;

public class Log {
    private Exception e;
    private CommandEvent event;
    private ConfigFile config;
    private String name;

    public Log(Exception e, CommandEvent event, String commandName) {
        this.e = e;
        this.event = event;
        this.config = Servant.config;
        this.name = commandName;
    }

    public void sendLogSQL() {
        e.printStackTrace();
        event.reply("Something went wrong connecting to the database.\n" +
                "A report was sent to the bot owner.");
        event.getJDA().getUserById(config.getBotOwnerId()).openPrivateChannel().queue(privateChannel ->
                privateChannel.sendMessage("```c\n" +
                        "Error\n" +
                        "-----\n" +
                        "Guild: " + event.getGuild().getName() + " (" + event.getGuild().getIdLong() + ")\n" +
                        "User: " + event.getAuthor().getName() + " (" + event.getAuthor().getIdLong() + ")\n" +
                        "Command: " + name + "\n" +
                        "Error: " + e.getMessage() + "\n" +
                        "```").queue());
    }

    public void sendLogHttp() {
        e.printStackTrace();
        event.reply("Something went wrong connecting to HTTP.\n" +
                "A report was sent to the bot owner.");
        event.getJDA().getUserById(config.getBotOwnerId()).openPrivateChannel().queue(privateChannel ->
                privateChannel.sendMessage("```c\n" +
                        "Error\n" +
                        "-----\n" +
                        "Guild: " + event.getGuild().getName() + " (" + event.getGuild().getIdLong() + ")\n" +
                        "User: " + event.getAuthor().getName() + " (" + event.getAuthor().getIdLong() + ")\n" +
                        "Command: " + name + "\n" +
                        "Error: " + e.getMessage() + "\n" +
                        "```").queue());
    }
}
