package listeners;

import files.language.LanguageHandler;
import plugins.moderation.voicelobby.VoiceLobbyHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.Constants;
import utilities.ImageUtil;

import java.time.Instant;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

public class GuildVoiceMoveListener extends ListenerAdapter {
    // This event will be thrown if a user moves to a voice channel from a vc.
    public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent event) {
        var member = event.getMember();
        var user = member.getUser();
        var myUser = new MyUser(user);
        var guild = event.getGuild();
        var myGuild = new MyGuild(guild);
        var guildOwner = guild.getOwner();
        if (guildOwner == null) return; // To eliminate errors. Will never occur.
        var myGuildOwnerUser = new MyUser(guildOwner.getUser());
        var jda = event.getJDA();
        var lang = myGuild.getLanguageCode();
        var channel = event.getChannelJoined();

        // Blacklist
        if (guild.getIdLong() == Constants.DISCORD_BOT_LIST_ID) return;
        if (guildOwner.getUser().isBot()) return;
        if (myGuild.isBlacklisted() || myUser.isBlacklisted()) return;

        CompletableFuture.runAsync(() -> {
            // Voice Lobby
            if (myGuild.pluginIsEnabled("voicelobby") && myGuild.categoryIsEnabled("moderation")) {
                processVoiceLobby(event, guild, myGuild, user, member, lang);
            }

            // Log
            if (myGuild.pluginIsEnabled("log") && myGuild.categoryIsEnabled("moderation") && myGuild.logIsEnabled("vc_move")) {
                var logChannel = guild.getTextChannelById(myGuild.getLogChannelId());
                if (logChannel != null) {
                    logChannel.sendMessage(new EmbedBuilder()
                            .setColor(myGuildOwnerUser.getColor())
                            .setTitle(LanguageHandler.get(lang, "log_vc_move_title"))
                            .addField(LanguageHandler.get(lang, "user"), event.getMember().getUser().getName() + "#" + event.getMember().getUser().getDiscriminator(), true)
                            .addField(LanguageHandler.get(lang, "user_id"), event.getMember().getUser().getId(), true)
                            .addField(LanguageHandler.get(lang, "vc"), event.getChannelLeft().getName() + " → " + event.getChannelJoined().getName(), false)
                            .addField(LanguageHandler.get(lang, "vc_id"), event.getChannelLeft().getId() + " → " + event.getChannelJoined().getId(), false)
                            .setFooter(LanguageHandler.get(lang, "log_at"), ImageUtil.getUrl(jda, "clock"))
                            .setTimestamp(Instant.now())
                            .build()
                    ).queue();
                }
            }
        }, Servant.fixedThreadPool);
    }

    private static void processVoiceLobby(GuildVoiceMoveEvent event, net.dv8tion.jda.api.entities.Guild guild, MyGuild myGuild, User user, Member member, String lang) {
        var actives = new LinkedList<VoiceChannel>();
        var activeIds = VoiceLobbyHandler.getActive(event.getJDA());
        for (var activeId : activeIds)
            if (event.getJDA().getVoiceChannelById(activeId) != null)
                actives.add(event.getJDA().getVoiceChannelById(activeId));
            else
                new VoiceLobbyHandler(event.getJDA(), guild.getIdLong(), activeId).unsetActive();

        var channels = myGuild.getVoiceLobbies();
        var joinedChannel = event.getChannelJoined();
        if (channels.contains(joinedChannel.getIdLong())) {
            // Join
            joinedChannel.createCopy().queue(newChannel -> {
                new VoiceLobbyHandler(event.getJDA(), guild.getIdLong(), newChannel.getIdLong()).setActive();
                newChannel.getManager().setParent(joinedChannel.getParent()).queue(
                        parent -> newChannel.getManager().setName(VoiceLobbyHandler.getVoiceLobbyName(member, lang)).queue(
                                name -> guild.modifyVoiceChannelPositions().selectPosition(newChannel).moveTo(joinedChannel.getPosition() + 1).queue(
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

        // Leave
        var leftChannel = event.getChannelLeft();
        if (actives.contains(leftChannel) && leftChannel.getMembers().size() == 0) {
            leftChannel.delete().queue(s -> {}, f -> {});
            actives.remove(leftChannel);
        }
    }
}
