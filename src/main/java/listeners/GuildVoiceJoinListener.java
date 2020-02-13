package listeners;

import moderation.guild.Server;
import moderation.toggle.Toggle;
import moderation.voicelobby.VoiceLobby;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import owner.blacklist.Blacklist;
import servant.Servant;

import java.util.concurrent.CompletableFuture;

public class GuildVoiceJoinListener extends ListenerAdapter {
    // This event will be thrown if a user joins a voice channel.
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        var guild = event.getGuild();
        var member = event.getMember();
        var user = member.getUser();

        /* Certain conditions must meet, so this event is allowed to be executed:
         * 1.   Ignore any request from the Discord Bot List as this big guild
         *      invoke a lot of events, but never use this bot actively.
         * 2.   Ignore any request from bots to prevent infinite loops.
         * 3.   Ignore any request from blacklisted users and guilds.
         */
        if (guild.getIdLong() == 264445053596991498L) return; // Discord Bot List
        if (user.isBot()) return;
        if (Blacklist.isBlacklisted(guild, user)) return;

        CompletableFuture.runAsync(() -> {
            var channel = event.getChannelJoined();
            var server = new Server(guild);
            var lang = server.getLanguage();

            // Voice Lobby
            if (Toggle.isEnabled(event, "voicelobby")) {
                processVoiceLobby(event, guild, user, server, member, channel, lang);
            }
        }, Servant.fixedThreadPool);
    }

    private static void processVoiceLobby(GuildVoiceJoinEvent event, net.dv8tion.jda.api.entities.Guild guild, User user, Server server, Member member, VoiceChannel channel, String lang) {
        var channels = server.getVoiceLobbies();
        if (channels.contains(channel.getIdLong())) {
            channel.createCopy().queue(newChannel -> {
                new VoiceLobby(event.getJDA(), guild.getIdLong(), newChannel.getIdLong()).setActive();
                newChannel.getManager().setParent(channel.getParent()).queue(
                        parent -> newChannel.getManager().setName(VoiceLobby.getVoiceLobbyName(member, lang)).queue(
                                name -> guild.modifyVoiceChannelPositions().selectPosition(newChannel).moveTo(channel.getPosition() + 1).queue(
                                        position -> {
                                            try {
                                                guild.moveVoiceMember(member, newChannel).queue();
                                            } catch (Exception ignored) { }
                                        }
                                )
                        ),
                        failure -> System.out.println("Couldn't create new voice channel. User: " + user.getName() + "#" + user.getDiscriminator() + " (" + user.getIdLong() + ") Guild: " + guild.getName() + " (" + guild.getIdLong() + ")")
                );

                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                if (newChannel.getMembers().size() == 0)
                                    newChannel.delete().queue(s -> {}, f -> {});
                            }
                        }, 3000
                );
            });
        }
    }
}
