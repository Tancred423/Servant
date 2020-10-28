package listeners;

import files.language.LanguageHandler;
import plugins.moderation.voicelobby.VoiceLobbyHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.Console;
import utilities.Constants;
import utilities.ImageUtil;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class GuildVoiceJoinListener extends ListenerAdapter {
    // This event will be thrown if a user joins a voice channel.
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
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
            if (myGuild.pluginIsEnabled("voicelobby") && myGuild.categoryIsEnabled("moderation"))
                processVoiceLobby(event, guild, user, myGuild, member, channel, lang);

            // Log
            if (myGuild.pluginIsEnabled("log") && myGuild.categoryIsEnabled("moderation") && myGuild.logIsEnabled("vc_join")) {
                var logChannel = guild.getTextChannelById(myGuild.getLogChannelId());
                if (logChannel != null) {
                    logChannel.sendMessage(new EmbedBuilder()
                            .setColor(myGuildOwnerUser.getColor())
                            .setTitle(LanguageHandler.get(lang, "log_vc_join_title"))
                            .addField(LanguageHandler.get(lang, "user"), event.getMember().getUser().getName() + "#" + event.getMember().getUser().getDiscriminator() + "\n" + event.getMember().getUser().getId(), true)
                            .addField(LanguageHandler.get(lang, "vc"), event.getChannelJoined().getName() + "\n" + event.getChannelJoined().getId(), false)
                            .setFooter(LanguageHandler.get(lang, "log_at"), ImageUtil.getUrl(jda, "clock"))
                            .setTimestamp(Instant.now())
                            .build()
                    ).queue();
                }
            }
        }, Servant.fixedThreadPool);
    }

    private static void processVoiceLobby(GuildVoiceJoinEvent event, net.dv8tion.jda.api.entities.Guild guild, User user, MyGuild myGuild, Member member, VoiceChannel lobby, String lang) {
        var channels = myGuild.getVoiceLobbies();
        if (channels.contains(lobby.getIdLong())) {
            lobby.createCopy().queue(tmpVc -> {
                new VoiceLobbyHandler(event.getJDA(), guild.getIdLong(), tmpVc.getIdLong()).setActive();
                tmpVc.getManager()
                        .setParent(lobby.getParent())
                        .setName(VoiceLobbyHandler.getVoiceLobbyName(member, lang))
                        .queue(parentAndName ->
                                guild.modifyVoiceChannelPositions().selectPosition(tmpVc).moveTo(lobby.getPosition() + 1).queue(position ->
                                        guild.moveVoiceMember(member, tmpVc).queue()));

                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                if (tmpVc.getMembers().size() == 0)
                                    tmpVc.delete().queue(s -> {
                                    }, f -> {
                                    });
                            }
                        }, 3000
                );
            }, f -> Console.log("Missing permission to copy voice channel! Guild: " + guild.getName() + " (" + guild.getIdLong() + ")"));
        }
    }
}
