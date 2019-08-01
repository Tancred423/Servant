package servant;

import com.jagrosh.jdautilities.command.CommandEvent;
import config.ConfigFile;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateGameEvent;

public class Log {
    private Exception e;
    private CommandEvent commandEvent;
    private GuildMessageReceivedEvent guildMessageReceivedEvent;
    private MessageReceivedEvent messageReceivedEvent;
    private GuildMemberJoinEvent guildMemberJoinEvent;
    private GuildMemberLeaveEvent guildMemberLeaveEvent;
    private GuildMessageReactionAddEvent guildMessageReactionAddEvent;
    private GuildMessageReactionRemoveEvent guildMessageReactionRemoveEvent;
    private GuildVoiceJoinEvent guildVoiceJoinEvent;
    private GuildVoiceMoveEvent guildVoiceMoveEvent;
    private VoiceChannelDeleteEvent voiceChannelDeleteEvent;
    private UserUpdateGameEvent userUpdateGameEvent;
    private ConfigFile config;
    private String name;

    public Log(Exception e, UserUpdateGameEvent userUpdateGameEvent, String commandName) {
        this.e = e;
        this.userUpdateGameEvent = userUpdateGameEvent;
        this.config = Servant.config;
        this.name = commandName;
    }

    public Log(Exception e, GuildVoiceJoinEvent guildVoiceJoinEvent, String commandName) {
        this.e = e;
        this.guildVoiceJoinEvent = guildVoiceJoinEvent;
        this.config = Servant.config;
        this.name = commandName;
    }

    public Log(Exception e, GuildVoiceMoveEvent guildVoiceMoveEvent, String commandName) {
        this.e = e;
        this.guildVoiceMoveEvent = guildVoiceMoveEvent;
        this.config = Servant.config;
        this.name = commandName;
    }

    public Log(Exception e, VoiceChannelDeleteEvent voiceChannelDeleteEvent, String commandName) {
        this.e = e;
        this.voiceChannelDeleteEvent = voiceChannelDeleteEvent;
        this.config = Servant.config;
        this.name = commandName;
    }

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

    public Log(CommandEvent commandEvent, String commandName) {
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

    // userUpdateGameEvent
    public void sendLogSqlUserUpdateGameEvent() {
        e.printStackTrace();
        userUpdateGameEvent.getJDA().getUserById(config.getBotOwnerId()).openPrivateChannel().queue(privateChannel ->
                privateChannel.sendMessage("```c\n" +
                        "Error\n" +
                        "-----\n" +
                        "Guild: " + userUpdateGameEvent.getGuild().getName() + " (" + userUpdateGameEvent.getGuild().getIdLong() + ")\n" +
                        "User: " + userUpdateGameEvent.getUser().getName() + " (" + userUpdateGameEvent.getUser().getIdLong() + ")\n" +
                        "Command: " + name + "\n" +
                        "Error: " + e.getMessage() + "\n" +
                        "```").queue());
    }

    // commandEvent
    public void sendLogSqlCommandEvent(boolean notifyUser) {
        commandEvent.reactWarning();
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
        commandEvent.reactWarning();
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

    public void sendLogIOCommandEvent(boolean notifyUser) {
        commandEvent.reactWarning();
        e.printStackTrace();
        if (notifyUser) commandEvent.reply("Something went wrong with saving data.\n" + "A report was sent to the bot owner.");
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

    public void sendLogProfileDelete() {
        commandEvent.getJDA().getUserById(config.getBotOwnerId()).openPrivateChannel().queue(privateChannel ->
                privateChannel.sendMessage("```c\n" +
                        "Error\n" +
                        "-----\n" +
                        "Guild: " + commandEvent.getGuild().getName() + " (" + commandEvent.getGuild().getIdLong() + ")\n" +
                        "User: " + commandEvent.getAuthor().getName() + " (" + commandEvent.getAuthor().getIdLong() + ")\n" +
                        "Command: " + name + "\n" +
                        "Error: Image was not deleted.\n" +
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

    // guildVoiceJoinEvent
    public void sendLogSqlGuildVoiceJoinEvent() {
        e.printStackTrace();
        guildVoiceJoinEvent.getJDA().getUserById(config.getBotOwnerId()).openPrivateChannel().queue(privateChannel ->
                privateChannel.sendMessage("```c\n" +
                        "Error\n" +
                        "-----\n" +
                        "Guild: " + guildVoiceJoinEvent.getGuild().getName() + " (" + guildVoiceJoinEvent.getGuild().getIdLong() + ")\n" +
                        "User: " + guildVoiceJoinEvent.getGuild().getOwner().getUser().getName() + " (" + guildVoiceJoinEvent.getGuild().getOwner().getUser().getIdLong() + ")\n" +
                        "Command: " + name + "\n" +
                        "Error: " + e.getMessage() + "\n" +
                        "```").queue());
    }

    // guildVoiceMoveEvent
    public void sendLogSqlGuildVoiceMoveEvent() {
        e.printStackTrace();
        guildVoiceMoveEvent.getJDA().getUserById(config.getBotOwnerId()).openPrivateChannel().queue(privateChannel ->
                privateChannel.sendMessage("```c\n" +
                        "Error\n" +
                        "-----\n" +
                        "Guild: " + guildVoiceMoveEvent.getGuild().getName() + " (" + guildVoiceMoveEvent.getGuild().getIdLong() + ")\n" +
                        "User: " + guildVoiceMoveEvent.getGuild().getOwner().getUser().getName() + " (" + guildVoiceMoveEvent.getGuild().getOwner().getUser().getIdLong() + ")\n" +
                        "Command: " + name + "\n" +
                        "Error: " + e.getMessage() + "\n" +
                        "```").queue());
    }

    // voiceChannelDeleteEvent
    public void sendLogSqlVoiceChannelDeleteEvent() {
        e.printStackTrace();
        voiceChannelDeleteEvent.getJDA().getUserById(config.getBotOwnerId()).openPrivateChannel().queue(privateChannel ->
                privateChannel.sendMessage("```c\n" +
                        "Error\n" +
                        "-----\n" +
                        "Guild: " + voiceChannelDeleteEvent.getGuild().getName() + " (" + voiceChannelDeleteEvent.getGuild().getIdLong() + ")\n" +
                        "User: " + voiceChannelDeleteEvent.getGuild().getOwner().getUser().getName() + " (" + voiceChannelDeleteEvent.getGuild().getOwner().getUser().getIdLong() + ")\n" +
                        "Command: " + name + "\n" +
                        "Error: " + e.getMessage() + "\n" +
                        "```").queue());
    }
}
