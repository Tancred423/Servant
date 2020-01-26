// Modified by: Tancred423 (https://github.com/Tancred423)
package information;

import files.language.LanguageHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import owner.blacklist.Blacklist;
import servant.Servant;
import utilities.Constants;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.examples.doc.Author;

import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;

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
        CompletableFuture.runAsync(() -> {
            try {
                if (!Toggle.isEnabled(event, name)) return;
                if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

                var lang = LanguageHandler.getLanguage(event);
                var guild = event.getGuild();
                var author = event.getAuthor();

                processInfo(event, guild, guild.getVanityUrl(), lang);

                // Statistics.
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
                new moderation.guild.Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Servant.threadPool);
    }

    private void processInfo(CommandEvent event, Guild guild, String vanityUrl, String lang) {
        var author = event.getAuthor();

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

        var eb = new EmbedBuilder();
        eb.setColor(new User(event.getAuthor().getIdLong()).getColor(guild, author));
        eb.setAuthor(String.format(LanguageHandler.get(lang, "serverinfo_owner"), owner.getEffectiveName()), null, owner.getUser().getEffectiveAvatarUrl());
        eb.setThumbnail(icon);
        eb.setTitle(String.format(LanguageHandler.get(lang, "serverinfo_name"), name, id));
        eb.setImage(splash);
        eb.setFooter(String.format(LanguageHandler.get(lang, "serverinfo_region"), region), null);
        try {
            eb.setTimestamp(OffsetDateTime.now(ZoneId.of(new moderation.guild.Guild(event.getGuild().getIdLong()).getOffset(guild, author))));
        } catch (DateTimeException e) {
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
        var internalGuild = new moderation.guild.Guild(guild.getIdLong());
        eb.addField(LanguageHandler.get(lang, "serverinfo_prefix"), internalGuild.getPrefix(guild, author), true);
        eb.addField(LanguageHandler.get(lang, "serverinfo_offset"), internalGuild.getOffset(guild, author).equalsIgnoreCase("z") ? "UTC" : internalGuild.getOffset(guild, author), true);
        eb.addField(LanguageHandler.get(lang, "serverinfo_language"), internalGuild.getLanguage(guild, author), true);
        var birthdayChannel = guild.getTextChannelById(internalGuild.getBirthdayChannelId(guild, author));
        eb.addField(LanguageHandler.get(lang, "serverinfo_bdaychannel"), birthdayChannel == null ?
                LanguageHandler.get(lang, "serverinfo_nobdaychannel") : birthdayChannel.getAsMention(), true);
        var roleIntegerEntry = internalGuild.getAutorole(guild, author);
        eb.addField(LanguageHandler.get(lang, "serverinfo_autorole"), roleIntegerEntry == null ?
                LanguageHandler.get(lang, "serverinfo_noautorole") :
                String.format(LanguageHandler.get(lang, "serverinfo_autorole_value"), roleIntegerEntry.getKey().getAsMention(), roleIntegerEntry.getValue()), true);
        var streamChannel = guild.getTextChannelById(internalGuild.getStreamChannelId(guild, author));
        var streamRole = guild.getRoleById(internalGuild.getStreamingRoleId(guild, author));
        eb.addField(LanguageHandler.get(lang, "serverinfo_livestream"),
                (streamChannel == null ? LanguageHandler.get(lang, "serverinfo_nolivestream_channel") : streamChannel.getAsMention()) + "\n" +
                        (streamRole == null ? LanguageHandler.get(lang, "serverinfo_nolivestream_role") : streamRole.getAsMention()) + "\n" +
                        (internalGuild.isStreamerMode(guild, author) ? LanguageHandler.get(lang, "serverinfo_streamermode") : LanguageHandler.get(lang, "serverinfo_publicmode")), true);

        var lobbies = internalGuild.getLobbies(guild, author);
        var sb = new StringBuilder();
        if (!lobbies.isEmpty())
            for (Long lobbyId : lobbies) {
                var lobbyChannel = guild.getVoiceChannelById(lobbyId);
                if (lobbyChannel != null) sb.append(lobbyChannel.getName()).append("\n");
            }
        eb.addField(LanguageHandler.get(lang, "serverinfo_voicelobbies"), sb.toString().isEmpty() ?
                LanguageHandler.get(lang, "serverinfo_novoicelobbies") : sb.toString(), true);

        var mediaOnlyChannels = internalGuild.getMediaOnlyChannels(guild, author);
        sb = new StringBuilder();
        if (mediaOnlyChannels != null)
            for (var mediaChannel : mediaOnlyChannels) {
                var mediaTextChannel = guild.getTextChannelById(mediaChannel.getIdLong());
                if (mediaTextChannel != null) sb.append(mediaTextChannel.getAsMention()).append("\n");
            }
        eb.addField(LanguageHandler.get(lang, "serverinfo_mediaonlychannels"), sb.toString().isEmpty() ?
                LanguageHandler.get(lang, "serverinfo_nomediaonlychannels") : sb.toString(), true);

        var internalChannel = internalGuild.getJoinNotifierChannel(guild, author);
        var joinNotifierChannel = internalChannel == null ? null : guild.getTextChannelById(internalChannel.getIdLong());
        eb.addField(LanguageHandler.get(lang, "serverinfo_join"), joinNotifierChannel == null ?
                LanguageHandler.get(lang, "serverinfo_nojoin") :
                joinNotifierChannel.getAsMention(), true);

        internalChannel = internalGuild.getLeaveNotifierChannel(guild, author);
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
