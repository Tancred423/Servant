package moderation.lobby;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import servant.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LobbyListener extends ListenerAdapter {
    private List<VoiceChannel> active = new ArrayList<>();
    private String getLobbyName(Member member) { return "⤷ " + member.getEffectiveName() + "'s Lobby"; }

    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        // Enabled?
        try {
            if (!new servant.Guild(event.getGuild().getIdLong()).getToggleStatus("lobby")) return;
        } catch (SQLException e) {
            return;
        }

        Guild guild = event.getGuild();
        servant.Guild internalGuild;
        List<Long> channels;
        try {
            internalGuild = new servant.Guild(guild.getIdLong());
            channels = internalGuild.getLobbies();
        } catch (SQLException e) {
            new Log(e, event, "lobby").sendLogSqlGuildVoiceJoinEvent();
            return;
        }
        Member member = event.getMember();
        VoiceChannel channel = event.getChannelJoined();

        if (channels.contains(channel.getIdLong())) {
            // Create copy.
            VoiceChannel newChannel = (VoiceChannel) guild.getController().createVoiceChannel(getLobbyName(member))
                    .setBitrate(channel.getBitrate())
                    .setUserlimit(channel.getUserLimit())
                    .complete();

            if (channel.getParent() != null) newChannel.getManager().setParent(channel.getParent()).complete();
            guild.getController().modifyVoiceChannelPositions().selectPosition(newChannel).moveTo(channel.getPosition() + 1).complete();
            guild.getController().moveVoiceMember(member, newChannel).complete();
            active.add(newChannel);
        }
    }

    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        // Enabled?
        try {
            if (!new servant.Guild(event.getGuild().getIdLong()).getToggleStatus("lobby")) return;
        } catch (SQLException e) {
            return;
        }

        // Join
        Guild guild = event.getGuild();
        servant.Guild internalGuild;
        List<Long> channels;
        try {
            internalGuild = new servant.Guild(guild.getIdLong());
            channels = internalGuild.getLobbies();
        } catch (SQLException e) {
            new Log(e, event, "lobby").sendLogSqlGuildVoiceJoinEvent();
            return;
        }
        Member member = event.getMember();
        VoiceChannel joinedChannel = event.getChannelJoined();

        if (channels.contains(joinedChannel.getIdLong())) {
            // Create copy.
            VoiceChannel newChannel = (VoiceChannel) guild.getController().createVoiceChannel(getLobbyName(member))
                    .setBitrate(joinedChannel.getBitrate())
                    .setUserlimit(joinedChannel.getUserLimit())
                    .complete();

            if (joinedChannel.getParent() != null) newChannel.getManager().setParent(joinedChannel.getParent()).complete();
            guild.getController().modifyVoiceChannelPositions().selectPosition(newChannel).moveTo(joinedChannel.getPosition() + 1).complete();
            guild.getController().moveVoiceMember(member, newChannel).complete();
            active.add(newChannel);
        }

        // Leave
        VoiceChannel leftChannel = event.getChannelLeft();

        if (active.contains(leftChannel) && leftChannel.getMembers().size() == 0) {
            active.remove(leftChannel);
            leftChannel.delete().complete();
        }
    }

    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        // Enabled?
        try {
            if (!new servant.Guild(event.getGuild().getIdLong()).getToggleStatus("lobby")) return;
        } catch (SQLException e) {
            return;
        }

        VoiceChannel channel = event.getChannelLeft();

        if (active.contains(channel) && channel.getMembers().size() == 0) {
            active.remove(channel);
            channel.delete().complete();
        }
    }
}
