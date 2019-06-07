package servant;

import com.jagrosh.jdautilities.command.CommandEvent;
import config.ConfigFile;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class Log {
    private Exception e;
    private CommandEvent commandEvent;
    private GuildMessageReceivedEvent receivedEvent;
    private GuildMemberJoinEvent memberJoinEvent;
    private ConfigFile config;
    private String name;

    public Log(Exception e, CommandEvent commandEvent, String commandName) {
        this.e = e;
        this.commandEvent = commandEvent;
        this.config = Servant.config;
        this.name = commandName;
    }

    public Log(Exception e, GuildMessageReceivedEvent receiveEvent, String commandName) {
        this.e = e;
        this.receivedEvent = receiveEvent;
        this.config = Servant.config;
        this.name = commandName;
    }

    public Log(Exception e, GuildMemberJoinEvent memberJoinEvent, String commandName) {
        this.e = e;
        this.memberJoinEvent = memberJoinEvent;
        this.config = Servant.config;
        this.name = commandName;
    }

    // commandEvent
    public void sendLogSqlCommandEvent(boolean notifyUser) {
        e.printStackTrace();
        if (notifyUser) commandEvent.reply("Something went wrong connecting to the database.\n" + "A report was sent to the bot owner.");
        commandEvent.getJDA().getUserById(config.getBotOwnerId()).openPrivateChannel().queue(privateChannel ->
                privateChannel.sendMessage("```c\n" +
                        "Error\n" +
                        "-----\n" +
                        "Guild: " + commandEvent.getGuild().getName() + " (" + commandEvent.getGuild().getIdLong() + ")\n" +
                        "User: " + commandEvent.getAuthor().getName() + " (" + commandEvent.getAuthor().getIdLong() + ")\n" +
                        "Command: " + name + "\n" +
                        "Error: " + e.getMessage() + "\n" +
                        "```").queue());
    }

    public void sendLogHttpCommandEvent() {
        e.printStackTrace();
        commandEvent.reply("Something went wrong connecting to HTTP.\n" +
                "A report was sent to the bot owner.");
        commandEvent.getJDA().getUserById(config.getBotOwnerId()).openPrivateChannel().queue(privateChannel ->
                privateChannel.sendMessage("```c\n" +
                        "Error\n" +
                        "-----\n" +
                        "Guild: " + commandEvent.getGuild().getName() + " (" + commandEvent.getGuild().getIdLong() + ")\n" +
                        "User: " + commandEvent.getAuthor().getName() + " (" + commandEvent.getAuthor().getIdLong() + ")\n" +
                        "Command: " + name + "\n" +
                        "Error: " + e.getMessage() + "\n" +
                        "```").queue());
    }

    // receiveEvent
    public void sendLogSqlReceiveEvent(boolean notifyUser) {
        e.printStackTrace();
        if (notifyUser) receivedEvent.getChannel().sendMessage("Something went wrong connecting to the database.\n" + "A report was sent to the bot owner.").queue();
        receivedEvent.getJDA().getUserById(config.getBotOwnerId()).openPrivateChannel().queue(privateChannel ->
                privateChannel.sendMessage("```c\n" +
                        "Error\n" +
                        "-----\n" +
                        "Guild: " + receivedEvent.getGuild().getName() + " (" + receivedEvent.getGuild().getIdLong() + ")\n" +
                        "User: " + receivedEvent.getAuthor().getName() + " (" + receivedEvent.getAuthor().getIdLong() + ")\n" +
                        "Command: " + name + "\n" +
                        "Error: " + e.getMessage() + "\n" +
                        "```").queue());
    }

    // memberJoinEvent
    public void sendLogSqlMemberJoinEvent() {
        e.printStackTrace();
        memberJoinEvent.getJDA().getUserById(config.getBotOwnerId()).openPrivateChannel().queue(privateChannel ->
                privateChannel.sendMessage("```c\n" +
                        "Error\n" +
                        "-----\n" +
                        "Guild: " + memberJoinEvent.getGuild().getName() + " (" + memberJoinEvent.getGuild().getIdLong() + ")\n" +
                        "User: " + memberJoinEvent.getUser().getName() + " (" + memberJoinEvent.getUser().getIdLong() + ")\n" +
                        "Command: " + name + "\n" +
                        "Error: " + e.getMessage() + "\n" +
                        "```").queue());
    }
}
