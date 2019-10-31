// Author: Tancred423 (https://github.com/Tancred423)
package moderation.toggle;

import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateGameEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class Toggle {
    public static boolean isEnabled(CommandEvent event, String name) {
        if (event.getGuild() != null) return new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus(name, event.getGuild(), event.getAuthor());
        return true;
    }

    public static boolean isEnabled(GuildMessageReactionAddEvent event, String name) {
        if (event.getGuild() != null) return new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus(name, event.getGuild(), event.getUser());
        return true;
    }

    public static boolean isEnabled(GuildMemberJoinEvent event, String name) {
        if (event.getGuild() != null) return new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus(name, event.getGuild(), event.getUser());
        return true;
    }

    public static boolean isEnabled(GuildMemberLeaveEvent event, String name) {
        if (event.getGuild() != null) return new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus(name, event.getGuild(), event.getUser());
        return true;
    }

    public static boolean isEnabled(GuildVoiceJoinEvent event, String name) {
        if (event.getGuild() != null) return new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus(name, event.getGuild(), event.getMember().getUser());
        return true;
    }

    public static boolean isEnabled(GuildVoiceMoveEvent event, String name) {
        if (event.getGuild() != null) return new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus(name, event.getGuild(), event.getMember().getUser());
        return true;
    }

    public static boolean isEnabled(GuildVoiceLeaveEvent event, String name) {
        if (event.getGuild() != null) return new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus(name, event.getGuild(), event.getMember().getUser());
        return true;
    }

    public static boolean isEnabled(GuildMessageReactionRemoveEvent event, String name) {
        if (event.getGuild() != null) return new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus(name, event.getGuild(), event.getUser());
        return true;
    }

    public static boolean isEnabled(GuildMessageReceivedEvent event, String name) {
        if (event.getGuild() != null) return new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus(name, event.getGuild(), event.getAuthor());
        return true;
    }

    public static boolean isEnabled(UserUpdateGameEvent event, String name) {
        if (event.getGuild() != null) return new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus(name, event.getGuild(), event.getUser());
        return true;
    }
}
