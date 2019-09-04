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
import servant.Log;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;

public class Toggle {
    public static boolean isEnabled(CommandEvent event, String name) {
        try {
            if (event.getGuild() != null) if (!new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus(name)) return false;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
        return true;
    }

    public static boolean isEnabled(GuildMessageReactionAddEvent event, String name) {
        try {
            if (event.getGuild() != null) if (!new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus(name)) return false;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getUser(), name, null).sendLog(false);
        }
        return true;
    }

    public static boolean isEnabled(GuildMemberJoinEvent event, String name) {
        try {
            if (event.getGuild() != null) if (!new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus(name)) return false;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getUser(), name, null).sendLog(false);
        }
        return true;
    }

    public static boolean isEnabled(GuildMemberLeaveEvent event, String name) {
        try {
            if (event.getGuild() != null) if (!new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus(name)) return false;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getUser(), name, null).sendLog(false);
        }
        return true;
    }

    public static boolean isEnabled(GuildVoiceJoinEvent event, String name) {
        try {
            if (event.getGuild() != null) if (!new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus(name)) return false;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getMember().getUser(), name, null).sendLog(false);
        }
        return true;
    }


    public static boolean isEnabled(GuildVoiceMoveEvent event, String name) {
        try {
            if (event.getGuild() != null) if (!new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus(name)) return false;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getMember().getUser(), name, null).sendLog(false);
        }
        return true;
    }

    public static boolean isEnabled(GuildVoiceLeaveEvent event, String name) {
        try {
            if (event.getGuild() != null) if (!new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus(name)) return false;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getMember().getUser(), name, null).sendLog(false);
        }
        return true;
    }

    public static boolean isEnabled(GuildMessageReactionRemoveEvent event, String name) {
        try {
            if (event.getGuild() != null) if (!new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus(name)) return false;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getMember().getUser(), name, null).sendLog(false);
        }
        return true;
    }

    public static boolean isEnabled(GuildMessageReceivedEvent event, String name) {
        try {
            if (event.getGuild() != null) if (!new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus(name)) return false;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getMember().getUser(), name, null).sendLog(false);
        }
        return true;
    }

    public static boolean isEnabled(UserUpdateGameEvent event, String name) {
        try {
            if (event.getGuild() != null) if (!new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus(name)) return false;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getUser(), name, null).sendLog(false);
        }
        return true;
    }
}
