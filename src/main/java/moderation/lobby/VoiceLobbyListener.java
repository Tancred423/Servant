// Author: Tancred423 (https://github.com/Tancred423)
package moderation.lobby;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class VoiceLobbyListener extends ListenerAdapter {
    private String getLobbyName(Member member, String lang) {
        return "⤷ " + member.getEffectiveName() +
                (member.getEffectiveName().toLowerCase().endsWith("s") ?
                        LanguageHandler.get(lang, "voicelobby_apostrophe") :
                        LanguageHandler.get(lang, "voicelobby_apostropge_s")) + " Lobby"; }

    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "voicelobby")) return;

            var guild = event.getGuild();
            moderation.guild.Guild internalGuild;
            var member = event.getMember();
            var user = member.getUser();
            var channel = event.getChannelJoined();
            internalGuild = new Guild(guild.getIdLong());
            var channels = internalGuild.getLobbies(guild, user);

            if (channels.contains(channel.getIdLong())) {
                VoiceChannel newChannel;
                newChannel = (VoiceChannel) guild.getController().createVoiceChannel(getLobbyName(member, new Guild(event.getGuild().getIdLong()).getLanguage(guild, user)))
                        .setBitrate(Math.min(channel.getBitrate(), 96000))
                        .setUserlimit(channel.getUserLimit())
                        .complete();

                if (channel.getParent() != null) newChannel.getManager().setParent(channel.getParent()).complete();

                guild.getController().modifyVoiceChannelPositions().selectPosition(newChannel).moveTo(channel.getPosition() + 1).complete();
                guild.getController().moveVoiceMember(member, newChannel).complete();
                internalGuild.setActiveLobby(newChannel.getIdLong(), guild, user);
            }

            try {
                TimeUnit.MILLISECONDS.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            var activeIds = internalGuild.getActiveLobbies(guild, user);
            var active = new LinkedList<VoiceChannel>();

            for (var activeId : activeIds) {
                if (event.getJDA().getVoiceChannelById(activeId) != null) active.add(event.getJDA().getVoiceChannelById(activeId));
                else internalGuild.unsetActiveLobby(activeId, guild, user);
            }

            for (int i = 0; i < active.size(); i++) {
                if (active.get(i).getMembers().size() == 0) {
                    event.getJDA().getVoiceChannelById(active.get(i).getIdLong()).delete().complete();
                    active.remove(active.get(i));
                }
            }
        });
    }

    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "voicelobby")) return;

            // Join
            var guild = event.getGuild();
            moderation.guild.Guild internalGuild;
            var member = event.getMember();
            var user = member.getUser();
            var joinedChannel = event.getChannelJoined();
            internalGuild = new Guild(guild.getIdLong());
            var channels = internalGuild.getLobbies(guild, user);


            if (channels.contains(joinedChannel.getIdLong())) {
                VoiceChannel newChannel;
                newChannel = (VoiceChannel) guild.getController().createVoiceChannel(getLobbyName(member, new Guild(event.getGuild().getIdLong()).getLanguage(guild, user)))
                        .setBitrate(Math.min(joinedChannel.getBitrate(), 96000))
                        .setUserlimit(joinedChannel.getUserLimit())
                        .complete();

                if (joinedChannel.getParent() != null)
                    newChannel.getManager().setParent(joinedChannel.getParent()).complete();

                guild.getController().modifyVoiceChannelPositions().selectPosition(newChannel).moveTo(joinedChannel.getPosition() + 1).complete();
                guild.getController().moveVoiceMember(member, newChannel).complete();
                internalGuild.setActiveLobby(newChannel.getIdLong(), guild, user);
            }

            try {
                TimeUnit.MILLISECONDS.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            var activeIds = internalGuild.getActiveLobbies(guild, user);
            var active = new LinkedList<VoiceChannel>();

            for (var activeId : activeIds) {
                if (event.getJDA().getVoiceChannelById(activeId) != null) active.add(event.getJDA().getVoiceChannelById(activeId));
                else internalGuild.unsetActiveLobby(activeId, guild, user);
            }

            for (int i = 0; i < active.size(); i++) {
                if (active.get(i).getMembers().size() == 0) {
                    event.getJDA().getVoiceChannelById(active.get(i).getIdLong()).delete().complete();
                    active.remove(active.get(i));
                }
            }

            // Leave
            var leftChannel = event.getChannelLeft();

            if (active.contains(leftChannel) && leftChannel.getMembers().size() == 0) {
                active.remove(leftChannel);
                leftChannel.delete().complete();
            }
        });
    }

    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "voicelobby")) return;

            var channel = event.getChannelLeft();

            var user = event.getMember().getUser();
            var guild = event.getGuild();
            var internalGuild = new Guild(guild.getIdLong());
            var activeIds = internalGuild.getActiveLobbies(guild, user);
            var active = new LinkedList<VoiceChannel>();

            for (var activeId : activeIds) {
                if (event.getJDA().getVoiceChannelById(activeId) != null) active.add(event.getJDA().getVoiceChannelById(activeId));
                else internalGuild.unsetActiveLobby(activeId, guild, user);
            }

            if (active.contains(channel) && channel.getMembers().size() == 0) {
                active.remove(channel);
                channel.delete().complete();
            }
        });

    }

    public void onVoiceChannelDelete(VoiceChannelDeleteEvent event) {
        CompletableFuture.runAsync(() -> {
            var guild = event.getGuild();
            var user = guild.getOwner().getUser();
            var internalGuild = new Guild(guild.getIdLong());
            var activeIds = internalGuild.getActiveLobbies(guild, user);
            var active = new LinkedList<VoiceChannel>();

            for (var activeId : activeIds) {
                if (event.getJDA().getVoiceChannelById(activeId) != null) active.add(event.getJDA().getVoiceChannelById(activeId));
                else internalGuild.unsetActiveLobby(activeId, guild, user);
            }

            if (active.contains(event.getChannel())) {
                active.remove(event.getChannel());
                new Guild(event.getGuild().getIdLong()).unsetLobby(event.getChannel().getIdLong(), event.getGuild(), event.getGuild().getOwner().getUser());
            }
        });
    }
}
