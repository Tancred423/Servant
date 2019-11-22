// Author: Tancred423 (https://github.com/Tancred423)
package moderation.lobby;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class VoiceLobbyListener extends ListenerAdapter {
    private String getLobbyName(Member member, String lang) {
        return "⤷ " + member.getEffectiveName() +
                (member.getEffectiveName().toLowerCase().endsWith("s") ?
                        LanguageHandler.get(lang, "voicelobby_apostrophe") :
                        LanguageHandler.get(lang, "voicelobby_apostropge_s")) + " Lobby"; }

    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "voicelobby")) return;

            var guild = event.getGuild();
            var member = event.getMember();
            var user = member.getUser();
            var channel = event.getChannelJoined();
            var internalGuild = new Guild(guild.getIdLong());
            var channels = internalGuild.getLobbies(guild, user);
            var active = new LinkedList<VoiceChannel>();
            var lang = internalGuild.getLanguage(guild, user);

            if (channels.contains(channel.getIdLong())) {
                channel.createCopy().queue(newChannel ->
                        newChannel.getManager().setName(getLobbyName(member, lang)).queue(name ->
                                newChannel.getManager().setParent(channel.getParent()).queue(parent ->
                                        guild.modifyVoiceChannelPositions().selectPosition(newChannel).moveTo(channel.getPosition() + 1).queue(position ->
                                                guild.moveVoiceMember(member, newChannel).queue(move -> {
                                                    internalGuild.setActiveLobby(newChannel.getIdLong(), guild, user);

                                                    try {
                                                        TimeUnit.MILLISECONDS.sleep(2000);
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }

                                                    var activeIds = internalGuild.getActiveLobbies(guild, user);

                                                    for (var activeId : activeIds) {
                                                        if (event.getJDA().getVoiceChannelById(activeId) != null)
                                                            active.add(event.getJDA().getVoiceChannelById(activeId));
                                                        else internalGuild.unsetActiveLobby(activeId, guild, user);
                                                    }

                                                    for (int i = 0; i < active.size(); i++) {
                                                        if (active.get(i).getMembers().size() == 0) {
                                                            var vc = event.getJDA().getVoiceChannelById(active.get(i).getIdLong());
                                                            if (vc != null) {
                                                                vc.delete().queue();
                                                                active.remove(active.get(i));
                                                            }
                                                        }
                                                    }
                                                })
                                        )
                                )
                        ), failure -> System.out.println("Couldn't create new voice channel. User: " + user.getName() + "#" + user.getDiscriminator() + " (" + user.getIdLong() + ") Guild: " + guild.getName() + " (" + guild.getIdLong() + ")")
                );
            }
        });
    }

    public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "voicelobby")) return;

            // Join
            var guild = event.getGuild();
            var member = event.getMember();
            var user = member.getUser();
            var joinedChannel = event.getChannelJoined();
            var internalGuild = new Guild(guild.getIdLong());
            var channels = internalGuild.getLobbies(guild, user);
            var active = new LinkedList<VoiceChannel>();
            var lang = internalGuild.getLanguage(guild, user);

            if (channels.contains(joinedChannel.getIdLong())) {
                joinedChannel.createCopy().queue(newChannel ->
                        newChannel.getManager().setName(getLobbyName(member, lang)).queue(name ->
                                newChannel.getManager().setParent(joinedChannel.getParent()).queue(parent ->
                                        guild.modifyVoiceChannelPositions().selectPosition(newChannel).moveTo(joinedChannel.getPosition() + 1).queue(position ->
                                                guild.moveVoiceMember(member, newChannel).queue(move -> {
                                                    internalGuild.setActiveLobby(newChannel.getIdLong(), guild, user);

                                                    try {
                                                        TimeUnit.MILLISECONDS.sleep(2000);
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }

                                                    var activeIds = internalGuild.getActiveLobbies(guild, user);

                                                    for (var activeId : activeIds) {
                                                        if (event.getJDA().getVoiceChannelById(activeId) != null)
                                                            active.add(event.getJDA().getVoiceChannelById(activeId));
                                                        else internalGuild.unsetActiveLobby(activeId, guild, user);
                                                    }

                                                    for (int i = 0; i < active.size(); i++) {
                                                        if (active.get(i).getMembers().size() == 0) {
                                                            var vc = event.getJDA().getVoiceChannelById(active.get(i).getIdLong());
                                                            if (vc != null) {
                                                                vc.delete().queue();
                                                                active.remove(active.get(i));
                                                            }
                                                        }
                                                    }
                                                })
                                        )
                                )
                        ), failure -> System.out.println("Couldn't create new voice channel. User: " + user.getName() + "#" + user.getDiscriminator() + " (" + user.getIdLong() + ") Guild: " + guild.getName() + " (" + guild.getIdLong() + ")")
                );
            }

            // Leave
            var leftChannel = event.getChannelLeft();

            if (active.contains(leftChannel) && leftChannel.getMembers().size() == 0) {
                leftChannel.delete().queue();
                active.remove(leftChannel);
            }
        });
    }

    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
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
                channel.delete().queue();
                active.remove(channel);
            }
        });

    }

    public void onVoiceChannelDelete(@NotNull VoiceChannelDeleteEvent event) {
        CompletableFuture.runAsync(() -> {
            var guild = event.getGuild();
            var owner = guild.getOwner();
            var internalGuild = new Guild(guild.getIdLong());
            var activeIds = internalGuild.getActiveLobbies(guild, owner == null ? null : owner.getUser());
            var active = new LinkedList<VoiceChannel>();

            for (var activeId : activeIds) {
                if (event.getJDA().getVoiceChannelById(activeId) != null) active.add(event.getJDA().getVoiceChannelById(activeId));
                else internalGuild.unsetActiveLobby(activeId, guild, owner == null ? null : owner.getUser());
            }

            if (active.contains(event.getChannel())) {
                active.remove(event.getChannel());
                new Guild(event.getGuild().getIdLong()).unsetLobby(event.getChannel().getIdLong(), event.getGuild(), owner == null ? null : owner.getUser());
            }
        });
    }
}
