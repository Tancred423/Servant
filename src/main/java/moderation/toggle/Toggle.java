// Author: Tancred423 (https://github.com/Tancred423)
package moderation.toggle;

import moderation.guild.Server;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class Toggle {
    public static boolean isEnabled(CommandEvent event, String name) {
        if (event.getGuild() == null) return true;
        return new Server(event.getGuild()).getToggleStatus(name);
    }

    public static boolean isEnabled(GuildMessageReactionAddEvent event, String name) {
        return new Server(event.getGuild()).getToggleStatus(name);
    }

    public static boolean isEnabled(GuildMemberJoinEvent event, String name) {
        return new Server(event.getGuild()).getToggleStatus(name);
    }

    public static boolean isEnabled(GuildMemberLeaveEvent event, String name) {
        return new Server(event.getGuild()).getToggleStatus(name);
    }

    public static boolean isEnabled(GuildVoiceJoinEvent event, String name) {
        return new Server(event.getGuild()).getToggleStatus(name);
    }

    public static boolean isEnabled(GuildVoiceMoveEvent event, String name) {
        return new Server(event.getGuild()).getToggleStatus(name);
    }

    public static boolean isEnabled(GuildVoiceLeaveEvent event, String name) {
        return new Server(event.getGuild()).getToggleStatus(name);
    }

    public static boolean isEnabled(GuildMessageReactionRemoveEvent event, String name) {
        return new Server(event.getGuild()).getToggleStatus(name);
    }

    public static boolean isEnabled(GuildMessageReceivedEvent event, String name) {
        return new Server(event.getGuild()).getToggleStatus(name);
    }

    public static boolean isEnabled(MessageReceivedEvent event, String name) {
        return new Server(event.getGuild()).getToggleStatus(name);
    }

    public static boolean isEnabled(UserActivityStartEvent event, String name) {
        return new Server(event.getGuild()).getToggleStatus(name);
    }

    public static boolean isEnabled(UserActivityEndEvent event, String name) {
        return new Server(event.getGuild()).getToggleStatus(name);
    }
}
