// Modified by: Tancred423 (https://github.com/Tancred423)
package information;

import files.language.LanguageHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import servant.Log;
import servant.Servant;
import utilities.Constants;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.examples.doc.Author;

import java.awt.*;
import java.sql.SQLException;
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
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!Toggle.isEnabled(event, name)) return;
        var lang = LanguageHandler.getLanguage(event, name);
        var guild = event.getGuild();

        if (guild.getFeatures().contains("VANITY_URL"))
            guild.getVanityUrl().queue(vanityUrl -> processInfo(event, guild, vanityUrl, lang));
        else processInfo(event, guild, null, lang);

        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            new moderation.guild.Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }

    private void processInfo(CommandEvent event, Guild guild, String vanityUrl, String lang) {
        try {
            var name = guild.getName();
            var id = guild.getIdLong();
            var icon = guild.getIconUrl();
            var splash = guild.getSplashUrl();

            var owner = guild.getOwner();

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

            var eb = new EmbedBuilder();
            try {
                eb.setColor(new User(event.getAuthor().getIdLong()).getColor());
            } catch (SQLException e) {
                eb.setColor(Color.decode(Servant.config.getDefaultColorCode()));
            }
            eb.setAuthor(String.format(LanguageHandler.get(lang, "serverinfo_owner"), owner.getEffectiveName()), null, owner.getUser().getEffectiveAvatarUrl());
            eb.setThumbnail(icon);
            eb.setTitle(String.format(LanguageHandler.get(lang, "serverinfo_name"), name, id));
            eb.setImage(splash);
            eb.setFooter(String.format(LanguageHandler.get(lang, "serverinfo_region"), region), null);
            try {
                eb.setTimestamp(OffsetDateTime.now(ZoneId.of(new moderation.guild.Guild(event.getGuild().getIdLong()).getOffset())));
            } catch (SQLException | DateTimeException e) {
                eb.setTimestamp(OffsetDateTime.now(ZoneId.of(Servant.config.getDefaultOffset())).getOffset());
            }

            // Counts
            eb.addField(LanguageHandler.get(lang, "serverinfo_textcount"), String.valueOf(textChannelCount), true);
            eb.addField(LanguageHandler.get(lang, "serverinfo_voicecount"), String.valueOf(voiceChannelCount), true);
            eb.addField(LanguageHandler.get(lang, "serverinfo_membercount"), String.valueOf(memberCount), true);
            eb.addField(LanguageHandler.get(lang, "serverinfo_rolecount"), String.valueOf(roleCount), true);
            eb.addField(LanguageHandler.get(lang, "serverinfo_categorycount"), String.valueOf(categoryCount), true);
            eb.addField(LanguageHandler.get(lang, "serverinfo_emotecount"), String.valueOf(emoteCount), true);

            // Misc
            eb.addField(LanguageHandler.get(lang, "serverinfo_afktimeout"), String.format(LanguageHandler.get(lang, "serverinfo_timeout"), afkTimeout), true);
            eb.addField(LanguageHandler.get(lang, "serverinfo_afkchannel"), afkChannel == null ? LanguageHandler.get(lang, "serverinfo_noafkchannel") : guild.getAfkChannel().getName(), true);
            eb.addField(LanguageHandler.get(lang, "serverinfo_systemchannel"), systemChannel == null ? LanguageHandler.get(lang, "serverinfo_nosystemchannel") : systemChannel.getAsMention(), true);
            eb.addField(LanguageHandler.get(lang, "serverinfo_vanity"), vanityUrl == null ? LanguageHandler.get(lang, "serverinfo_novanity") : vanityUrl, true);

            // Level
            eb.addField(LanguageHandler.get(lang, "serverinfo_mfa"), mfaLevel.name(), true);
            eb.addField(LanguageHandler.get(lang, "serverinfo_explicit"), explicitContentLevel.getDescription(), true);
            eb.addField(LanguageHandler.get(lang, "serverinfo_verification"), "**" + getVerificationName(verificationLevel.name(), lang) + "**\n" +
                    getVerificationDescription(verificationLevel.name(), lang), false);

            // Bot Settings
            eb.addField("", String.format(LanguageHandler.get(lang, "serverinfo_botsettings"), event.getSelfUser().getName()), false);
            var internalGuild = new moderation.guild.Guild(guild.getIdLong());
            eb.addField(LanguageHandler.get(lang, "serverinfo_prefix"), internalGuild.getPrefix(), true);
            eb.addField(LanguageHandler.get(lang, "serverinfo_offset"), internalGuild.getOffset().equalsIgnoreCase("z") ? "UTC" : internalGuild.getOffset(), true);
            eb.addField(LanguageHandler.get(lang, "serverinfo_language"), internalGuild.getLanguage(), true);
            eb.addField(LanguageHandler.get(lang, "serverinfo_bdaychannel"), internalGuild.getBirthdayChannelId() == 0 ?
                    LanguageHandler.get(lang, "serverinfo_nobdaychannel") :
                    guild.getTextChannelById(internalGuild.getBirthdayChannelId()).getAsMention(), true);
            var roleIntegerEntry = internalGuild.getAutorole().isEmpty() ? null : internalGuild.getAutorole().entrySet().iterator().next();
            eb.addField(LanguageHandler.get(lang, "serverinfo_autorole"), roleIntegerEntry == null ?
                    LanguageHandler.get(lang, "serverinfo_noautorole") :
                    String.format(LanguageHandler.get(lang, "serverinfo_autorole_value"), roleIntegerEntry.getKey().getAsMention(), roleIntegerEntry.getValue()), true);
            eb.addField(LanguageHandler.get(lang, "serverinfo_livestream"),
                    (internalGuild.getStreamChannelId() == 0 ? LanguageHandler.get(lang, "serverinfo_nolivestream_channel") : guild.getTextChannelById(internalGuild.getStreamChannelId()).getAsMention()) + "\n" +
                            (internalGuild.getStreamingRoleId() == 0 ? LanguageHandler.get(lang, "serverinfo_nolivestream_role") : guild.getRoleById(internalGuild.getStreamingRoleId()).getAsMention()) + "\n" +
                            (internalGuild.isStreamerMode() ? LanguageHandler.get(lang, "serverinfo_streamermode") :
                                    LanguageHandler.get(lang, "serverinfo_publicmode")), true);

            var lobbies = internalGuild.getLobbies();
            var sb = new StringBuilder();
            if (!lobbies.isEmpty())
                for (Long lobbyId : lobbies)
                    sb.append(guild.getVoiceChannelById(lobbyId).getName()).append("\n");
            eb.addField(LanguageHandler.get(lang, "serverinfo_voicelobbies"), sb.toString().isEmpty() ?
                    LanguageHandler.get(lang, "serverinfo_novoicelobbies") : sb.toString(), true);

            var mediaonlychannels = internalGuild.getMediaOnlyChannels();
            sb = new StringBuilder();
            if (mediaonlychannels != null)
                for (var mediaChannel : mediaonlychannels)
                    sb.append(guild.getTextChannelById(mediaChannel.getIdLong()).getAsMention()).append("\n");
            eb.addField(LanguageHandler.get(lang, "serverinfo_mediaonlychannels"), sb.toString().isEmpty() ?
                    LanguageHandler.get(lang, "serverinfo_nomediaonlychannels") : sb.toString(), true);

            eb.addField(LanguageHandler.get(lang, "serverinfo_join"), internalGuild.getJoinNotifierChannel() == null ?
                    LanguageHandler.get(lang, "serverinfo_nojoin") :
                    guild.getTextChannelById(internalGuild.getJoinNotifierChannel().getIdLong()).getAsMention(), true);

            eb.addField(LanguageHandler.get(lang, "serverinfo_leave"), internalGuild.getLeaveNotifierChannel() == null ?
                    LanguageHandler.get(lang, "serverinfo_noleave") :
                    guild.getTextChannelById(internalGuild.getLeaveNotifierChannel().getIdLong()).getAsMention(), true);

            event.reply(eb.build());

        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
        }
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
