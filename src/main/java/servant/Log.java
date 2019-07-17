package servant;

import com.jagrosh.jdautilities.command.CommandEvent;
import config.ConfigFile;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;

public class Log {
    private Exception e;
    private CommandEvent commandEvent;
    private GuildMessageReceivedEvent guildMessageReceivedEvent;
    private MessageReceivedEvent messageReceivedEvent;
    private GuildMemberJoinEvent guildMemberJoinEvent;
    private GuildMemberLeaveEvent guildMemberLeaveEvent;
    private GuildJoinEvent guildJoinEvent;
    private GuildLeaveEvent guildLeaveEvent;
    private GuildMessageReactionAddEvent guildMessageReactionAddEvent;
    private GuildMessageReactionRemoveEvent guildMessageReactionRemoveEvent;
    private ConfigFile config;
    private String name;

    public Log(Exception e, GuildMessageReactionAddEvent guildMessageReactionAddEvent, String commandName) {
        this.e = e;
        this.guildMessageReactionAddEvent = guildMessageReactionAddEvent;
        this.config = Servant.config;
        this.name = commandName;
    }

    public Log(Exception e, GuildMessageReactionRemoveEvent guildMessageReactionRemoveEvent, String commandName) {
        this.e = e;
        this.guildMessageReactionRemoveEvent = guildMessageReactionRemoveEvent;
        this.config = Servant.config;
        this.name = commandName;
    }

    public Log(Exception e, CommandEvent commandEvent, String commandName) {
        this.e = e;
        this.commandEvent = commandEvent;
        this.config = Servant.config;
        this.name = commandName;
    }

    public Log(Exception e, GuildMessageReceivedEvent guildMessageReceivedEvent, String commandName) {
        this.e = e;
        this.guildMessageReceivedEvent = guildMessageReceivedEvent;
        this.config = Servant.config;
        this.name = commandName;
    }

    public Log(Exception e, MessageReceivedEvent messageReceivedEvent, String commandName) {
        this.e = e;
        this.messageReceivedEvent = messageReceivedEvent;
        this.config = Servant.config;
        this.name = commandName;
    }

    public Log(Exception e, GuildMemberJoinEvent guildMemberJoinEvent, String commandName) {
        this.e = e;
        this.guildMemberJoinEvent = guildMemberJoinEvent;
        this.config = Servant.config;
        this.name = commandName;
    }

    public Log(Exception e, GuildMemberLeaveEvent guildMemberLeaveEvent, String commandName) {
        this.e = e;
        this.guildMemberLeaveEvent = guildMemberLeaveEvent;
        this.config = Servant.config;
        this.name = commandName;
    }

    public Log(Exception e, GuildJoinEvent guildJoinEvent, String commandName) {
        this.e = e;
        this.guildJoinEvent = guildJoinEvent;
        this.config = Servant.config;
        this.name = commandName;
    }

    public Log(Exception e, GuildLeaveEvent guildLeaveEvent, String commandName) {
        this.e = e;
        this.guildLeaveEvent = guildLeaveEvent;
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

    // guildMessageReceivedEvent
    public void sendLogSqlGuildMessageReceivedEvent(boolean notifyUser) {
        e.printStackTrace();
        if (notifyUser) guildMessageReceivedEvent.getChannel().sendMessage("Something went wrong connecting to the database.\n" + "A report was sent to the bot owner.").queue();
        guildMessageReceivedEvent.getJDA().getUserById(config.getBotOwnerId()).openPrivateChannel().queue(privateChannel ->
                privateChannel.sendMessage("```c\n" +
                        "Error\n" +
                        "-----\n" +
                        "Guild: " + guildMessageReceivedEvent.getGuild().getName() + " (" + guildMessageReceivedEvent.getGuild().getIdLong() + ")\n" +
                        "User: " + guildMessageReceivedEvent.getAuthor().getName() + " (" + guildMessageReceivedEvent.getAuthor().getIdLong() + ")\n" +
                        "Command: " + name + "\n" +
                        "Error: " + e.getMessage() + "\n" +
                        "```").queue());
    }

    // messageReceivedEvent
    public void sendLogSqlMessageReceivedEvent(boolean notifyUser) {
        e.printStackTrace();
        if (notifyUser) messageReceivedEvent.getChannel().sendMessage("Something went wrong connecting to the database.\n" + "A report was sent to the bot owner.").queue();
        messageReceivedEvent.getJDA().getUserById(config.getBotOwnerId()).openPrivateChannel().queue(privateChannel ->
                privateChannel.sendMessage("```c\n" +
                        "Error\n" +
                        "-----\n" +
                        (messageReceivedEvent.getGuild() == null ? "" : "Guild: " + messageReceivedEvent.getGuild().getName() + " (" + messageReceivedEvent.getGuild().getIdLong() + ")\n") +
                        "User: " + messageReceivedEvent.getAuthor().getName() + " (" + messageReceivedEvent.getAuthor().getIdLong() + ")\n" +
                        "Command: " + name + "\n" +
                        "Error: " + e.getMessage() + "\n" +
                        "```").queue());
    }

    // guildMemberJoinEvent
    public void sendLogSqlGuildMemberJoinEvent() {
        e.printStackTrace();
        guildMemberJoinEvent.getJDA().getUserById(config.getBotOwnerId()).openPrivateChannel().queue(privateChannel ->
                privateChannel.sendMessage("```c\n" +
                        "Error\n" +
                        "-----\n" +
                        "Guild: " + guildMemberJoinEvent.getGuild().getName() + " (" + guildMemberJoinEvent.getGuild().getIdLong() + ")\n" +
                        "User: " + guildMemberJoinEvent.getUser().getName() + " (" + guildMemberJoinEvent.getUser().getIdLong() + ")\n" +
                        "Command: " + name + "\n" +
                        "Error: " + e.getMessage() + "\n" +
                        "```").queue());
    }

    // guildMemberLeaveEvent
    public void sendLogSqlGuildMemberLeaveEvent() {
        e.printStackTrace();
        guildMemberLeaveEvent.getJDA().getUserById(config.getBotOwnerId()).openPrivateChannel().queue(privateChannel ->
                privateChannel.sendMessage("```c\n" +
                        "Error\n" +
                        "-----\n" +
                        "Guild: " + guildMemberLeaveEvent.getGuild().getName() + " (" + guildMemberLeaveEvent.getGuild().getIdLong() + ")\n" +
                        "User: " + guildMemberLeaveEvent.getUser().getName() + " (" + guildMemberLeaveEvent.getUser().getIdLong() + ")\n" +
                        "Command: " + name + "\n" +
                        "Error: " + e.getMessage() + "\n" +
                        "```").queue());
    }

    // guildJoinEvent
    public void sendLogSqlGuildJoinEvent() {
        e.printStackTrace();
        guildJoinEvent.getJDA().getUserById(config.getBotOwnerId()).openPrivateChannel().queue(privateChannel ->
                privateChannel.sendMessage("```c\n" +
                        "Error\n" +
                        "-----\n" +
                        "Guild: " + guildJoinEvent.getGuild().getName() + " (" + guildJoinEvent.getGuild().getIdLong() + ")\n" +
                        "User: " + guildJoinEvent.getGuild().getOwner().getUser().getName() + " (" + guildJoinEvent.getGuild().getOwner().getUser().getIdLong() + ")\n" +
                        "Command: " + name + "\n" +
                        "Error: " + e.getMessage() + "\n" +
                        "```").queue());
    }

    // guildJoinEvent
    public void sendLogSqlGuildLeaveEvent() {
        e.printStackTrace();
        guildLeaveEvent.getJDA().getUserById(config.getBotOwnerId()).openPrivateChannel().queue(privateChannel ->
                privateChannel.sendMessage("```c\n" +
                        "Error\n" +
                        "-----\n" +
                        "Guild: " + guildLeaveEvent.getGuild().getName() + " (" + guildLeaveEvent.getGuild().getIdLong() + ")\n" +
                        "User: " + guildLeaveEvent.getGuild().getOwner().getUser().getName() + " (" + guildLeaveEvent.getGuild().getOwner().getUser().getIdLong() + ")\n" +
                        "Command: " + name + "\n" +
                        "Error: " + e.getMessage() + "\n" +
                        "```").queue());
    }

    // guildMessageReactionAddEvent
    public void sendLogSqlGuildMessageReactionAddEvent() {
        e.printStackTrace();
        guildMessageReactionAddEvent.getJDA().getUserById(config.getBotOwnerId()).openPrivateChannel().queue(privateChannel ->
                privateChannel.sendMessage("```c\n" +
                        "Error\n" +
                        "-----\n" +
                        "Guild: " + guildMessageReactionAddEvent.getGuild().getName() + " (" + guildMessageReactionAddEvent.getGuild().getIdLong() + ")\n" +
                        "User: " + guildMessageReactionAddEvent.getGuild().getOwner().getUser().getName() + " (" + guildMessageReactionAddEvent.getGuild().getOwner().getUser().getIdLong() + ")\n" +
                        "Command: " + name + "\n" +
                        "Error: " + e.getMessage() + "\n" +
                        "```").queue());
    }

    // guildMessageReactionRemoveEvent
    public void sendLogSqlGuildMessageReactionRemoveEvent() {
        e.printStackTrace();
        guildMessageReactionRemoveEvent.getJDA().getUserById(config.getBotOwnerId()).openPrivateChannel().queue(privateChannel ->
                privateChannel.sendMessage("```c\n" +
                        "Error\n" +
                        "-----\n" +
                        "Guild: " + guildMessageReactionRemoveEvent.getGuild().getName() + " (" + guildMessageReactionRemoveEvent.getGuild().getIdLong() + ")\n" +
                        "User: " + guildMessageReactionRemoveEvent.getGuild().getOwner().getUser().getName() + " (" + guildMessageReactionRemoveEvent.getGuild().getOwner().getUser().getIdLong() + ")\n" +
                        "Command: " + name + "\n" +
                        "Error: " + e.getMessage() + "\n" +
                        "```").queue());
    }
}
