// Modified by: Tancred423 (https://github.com/Tancred423)
package information;

import files.language.LanguageHandler;
import moderation.guild.Server;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import servant.Servant;
import utilities.Constants;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.examples.doc.Author;

import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Author("John Grosh (jagrosh)")
public class ServerInfoCommand extends Command {
    public ServerInfoCommand() {
        this.name = "serverinfo";
        this.aliases = new String[]{"guildinfo"};
        this.help = "Shows info about the server.";
        this.category = new Category("Information");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);
        processInfo(event, lang);
    }

    private void processInfo(CommandEvent event, String lang) {
        var user = event.getAuthor();
        var master = new Master(user);
        var guild = event.getGuild();
        var server = new Server(guild);

        var vanityUrl = guild.getVanityUrl();

        var name = guild.getName();
        var id = guild.getIdLong();
        var icon = guild.getIconUrl();
        var splash = guild.getSplashUrl();

        var owner = guild.getOwner();
        if (owner == null) return;

        var textChannelCount = guild.getTextChannels().size();
        var voiceChannelCount = guild.getVoiceChannels().size();
        var memberCount = guild.getMembers().size();
        var roleCount = guild.getRoles().size();
        var categoryCount = guild.getCategories().size();
        var emoteCount = guild.getEmotes().size();

        var afkChannel = guild.getAfkChannel();
        var afkTimeout = guild.getAfkTimeout().getSeconds();

        var explicitContentLevel = guild.getExplicitContentLevel();
        var verificationLevel = guild.getVerificationLevel();
        var mfaLevel = guild.getRequiredMFALevel();

        var region = guild.getRegionRaw();
        var systemChannel = guild.getSystemChannel(); // New member announcement.

        OffsetDateTime timestamp;
        try {
            timestamp = OffsetDateTime.now(ZoneId.of(server.getOffset()));
        } catch (DateTimeException e) {
            timestamp = OffsetDateTime.now(ZoneId.of(Servant.config.getDefaultOffset()));
        }

        var eb = new EmbedBuilder()
                .setColor(master.getColor())
                .setAuthor(String.format(LanguageHandler.get(lang, "serverinfo_owner"), owner.getEffectiveName()), null, owner.getUser().getEffectiveAvatarUrl())
                .setThumbnail(icon)
                .setTitle(String.format(LanguageHandler.get(lang, "serverinfo_name"), name, id))
                .setImage(splash)
                .setFooter(String.format(LanguageHandler.get(lang, "serverinfo_region"), region), null)
                .setTimestamp(timestamp);

        // Counts
        eb.addField(LanguageHandler.get(lang, "serverinfo_textcount"), String.valueOf(textChannelCount), true);
        eb.addField(LanguageHandler.get(lang, "serverinfo_voicecount"), String.valueOf(voiceChannelCount), true);
        eb.addField(LanguageHandler.get(lang, "serverinfo_membercount"), String.valueOf(memberCount), true);
        eb.addField(LanguageHandler.get(lang, "serverinfo_rolecount"), String.valueOf(roleCount), true);
        eb.addField(LanguageHandler.get(lang, "serverinfo_categorycount"), String.valueOf(categoryCount), true);
        eb.addField(LanguageHandler.get(lang, "serverinfo_emotecount"), String.valueOf(emoteCount), true);

        // Misc
        eb.addField(LanguageHandler.get(lang, "serverinfo_afktimeout"), String.format(LanguageHandler.get(lang, "serverinfo_timeout"), afkTimeout), true);
        eb.addField(LanguageHandler.get(lang, "serverinfo_afkchannel"), afkChannel == null ? LanguageHandler.get(lang, "serverinfo_noafkchannel") : afkChannel.getName(), true);
        eb.addField(LanguageHandler.get(lang, "serverinfo_systemchannel"), systemChannel == null ? LanguageHandler.get(lang, "serverinfo_nosystemchannel") : systemChannel.getAsMention(), true);
        eb.addField(LanguageHandler.get(lang, "serverinfo_vanity"), vanityUrl == null ? LanguageHandler.get(lang, "serverinfo_novanity") : vanityUrl, true);

        // Level
        eb.addField(LanguageHandler.get(lang, "serverinfo_mfa"), mfaLevel.name(), true);
        eb.addField(LanguageHandler.get(lang, "serverinfo_explicit"), explicitContentLevel.getDescription(), true);
        eb.addField(LanguageHandler.get(lang, "serverinfo_verification"), "**" + getVerificationName(verificationLevel.name(), lang) + "**\n" +
                getVerificationDescription(verificationLevel.name(), lang), false);

        // Bot Settings
        eb.addField("", String.format(LanguageHandler.get(lang, "serverinfo_botsettings"), event.getSelfUser().getName()), false);
        eb.addField(LanguageHandler.get(lang, "serverinfo_prefix"), server.getPrefix(), true);
        eb.addField(LanguageHandler.get(lang, "serverinfo_offset"), server.getOffset().equalsIgnoreCase("z") ? "UTC" : server.getOffset(), true);
        eb.addField(LanguageHandler.get(lang, "serverinfo_language"), server.getLanguage(), true);
        var birthdayChannel = guild.getTextChannelById(server.getBirthdayChannelId());
        eb.addField(LanguageHandler.get(lang, "serverinfo_bdaychannel"), birthdayChannel == null ?
                LanguageHandler.get(lang, "serverinfo_nobdaychannel") : birthdayChannel.getAsMention(), true);
        var roleIntegerEntry = server.getAutorole();
        eb.addField(LanguageHandler.get(lang, "serverinfo_autorole"), roleIntegerEntry == null ?
                LanguageHandler.get(lang, "serverinfo_noautorole") :
                String.format(LanguageHandler.get(lang, "serverinfo_autorole_value"), roleIntegerEntry.getKey().getAsMention(), roleIntegerEntry.getValue()), true);
        var streamChannel = guild.getTextChannelById(server.getStreamChannelId());
        var streamRole = guild.getRoleById(server.getStreamingRoleId());
        eb.addField(LanguageHandler.get(lang, "serverinfo_livestream"),
                (streamChannel == null ? LanguageHandler.get(lang, "serverinfo_nolivestream_channel") : streamChannel.getAsMention()) + "\n" +
                        (streamRole == null ? LanguageHandler.get(lang, "serverinfo_nolivestream_role") : streamRole.getAsMention()) + "\n" +
                        (server.isStreamerMode() ? LanguageHandler.get(lang, "serverinfo_streamermode") : LanguageHandler.get(lang, "serverinfo_publicmode")), true);

        var lobbies = server.getLobbies();
        var sb = new StringBuilder();
        if (!lobbies.isEmpty())
            for (Long lobbyId : lobbies) {
                var lobbyChannel = guild.getVoiceChannelById(lobbyId);
                if (lobbyChannel != null) sb.append(lobbyChannel.getName()).append("\n");
            }
        eb.addField(LanguageHandler.get(lang, "serverinfo_voicelobbies"), sb.toString().isEmpty() ?
                LanguageHandler.get(lang, "serverinfo_novoicelobbies") : sb.toString(), true);

        var mediaOnlyChannels = server.getMediaOnlyChannels();
        sb = new StringBuilder();
        if (mediaOnlyChannels != null)
            for (var mediaChannel : mediaOnlyChannels) {
                var mediaTextChannel = guild.getTextChannelById(mediaChannel.getIdLong());
                if (mediaTextChannel != null) sb.append(mediaTextChannel.getAsMention()).append("\n");
            }
        eb.addField(LanguageHandler.get(lang, "serverinfo_mediaonlychannels"), sb.toString().isEmpty() ?
                LanguageHandler.get(lang, "serverinfo_nomediaonlychannels") : sb.toString(), true);

        var internalChannel = server.getJoinNotifierChannel();
        var joinNotifierChannel = internalChannel == null ? null : guild.getTextChannelById(internalChannel.getIdLong());
        eb.addField(LanguageHandler.get(lang, "serverinfo_join"), joinNotifierChannel == null ?
                LanguageHandler.get(lang, "serverinfo_nojoin") :
                joinNotifierChannel.getAsMention(), true);

        internalChannel = server.getLeaveNotifierChannel();
        var leaveNotifierChannel = internalChannel == null ? null : guild.getTextChannelById(internalChannel.getIdLong());
        eb.addField(LanguageHandler.get(lang, "serverinfo_leave"), leaveNotifierChannel == null ?
                LanguageHandler.get(lang, "serverinfo_noleave") :
                leaveNotifierChannel.getAsMention(), true);

        event.reply(eb.build());
    }

    private String getVerificationName(String key, String lang) {
        switch (key.toLowerCase()) {
            case "none":
                return LanguageHandler.get(lang, "serverinfo_none");
            case "low":
                return LanguageHandler.get(lang, "serverinfo_low");
            case "medium":
                return LanguageHandler.get(lang, "serverinfo_medium");
            case "high":
                return LanguageHandler.get(lang, "serverinfo_high");
            case "very_high":
                return LanguageHandler.get(lang, "serverinfo_veryhigh");
            default:
                return "";
        }
    }

    private String getVerificationDescription(String key, String lang) {
        switch (key.toLowerCase()) {
            case "none":
                return LanguageHandler.get(lang, "serverinfo_none_desc");
            case "low":
                return LanguageHandler.get(lang, "serverinfo_low_desc");
            case "medium":
                return LanguageHandler.get(lang, "serverinfo_medium_desc");
            case "high":
                return LanguageHandler.get(lang, "serverinfo_high_desc");
            case "very_high":
                return LanguageHandler.get(lang, "serverinfo_veryhigh_desc");
            default:
                return "";
        }
    }
}
