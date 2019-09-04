// Author: Tancred423 (https://github.com/Tancred423)
package information;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.Permission;
import servant.Log;
import servant.Servant;
import moderation.user.User;
import utilities.Constants;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.commons.JDAUtilitiesInfo;
import zJdaUtilsLib.com.jagrosh.jdautilities.examples.doc.Author;

import java.awt.*;
import java.sql.SQLException;

@Author("John Grosh (jagrosh)")
public class BotInfoCommand extends Command {
    private final Permission[] perms;
    private String oauthLink;
    
    public BotInfoCommand() {
        this.name = "botinfo";
        this.aliases = new String[]{"about"};
        this.help = "Shows info about the bot.";
        this.category = new Category("Information");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};

        this.perms = new Permission[]{Permission.ADMINISTRATOR};
    }
    
    @Override
    protected void execute(CommandEvent event) {
        if (!Toggle.isEnabled(event, name)) return;

        var lang = LanguageHandler.getLanguage(event, name);
        if (oauthLink == null) {
            try {
                var info = event.getJDA().asBot().getApplicationInfo().complete();
                oauthLink = info.isBotPublic() ? info.getInviteUrl(0L, perms) : "";
            } catch (Exception e) {
                oauthLink = "";
            }
        }
        var eb = new EmbedBuilder();
        var features = new String[]{LanguageHandler.get(lang, "botinfo_moderationtools"),
                LanguageHandler.get(lang, "botinfo_informativecommands"),
                LanguageHandler.get(lang, "botinfo_usefulfeatures"),
                LanguageHandler.get(lang, "botinfo_funcommands")
        };

        try {
            eb.setColor(new User(event.getAuthor().getIdLong()).getColor());
        } catch (SQLException e) {
            eb.setColor(Color.decode(Servant.config.getDefaultColorCode()));
        }
        eb.setAuthor(String.format(LanguageHandler.get(lang, "botinfo_authorname"), event.getSelfUser().getName()), null, event.getSelfUser().getAvatarUrl());

        boolean inv = !oauthLink.isEmpty();
        var sb = new StringBuilder();
        var description = String.format(LanguageHandler.get(lang, "botinfo_description"),
                event.getSelfUser().getName(),
                event.getJDA().getUserById(event.getClient().getOwnerId()).getName(),
                JDAUtilitiesInfo.GITHUB,
                JDAUtilitiesInfo.VERSION,
                "https://github.com/DV8FromTheWorld/JDA",
                JDAInfo.VERSION,
                event.getClient().getTextualPrefix());
        var invite = String.format(LanguageHandler.get(lang, "botinfo_join"), "https://" + event.getClient().getServerInvite(), oauthLink) +
                (inv ? ", " + String.format(LanguageHandler.get(lang, "botinfo_invite"), oauthLink) : "!");

        sb.append(description).append("\n").append(invite).append("\n\n").append(LanguageHandler.get(lang, "botinfo_features")).append("\n```c");
        String REPLACEMENT_ICON = "+";
        for (String feature : features)
            sb.append("\n").append(event.getClient().getSuccess().startsWith("<") ? REPLACEMENT_ICON : event.getClient().getSuccess()).append(" ").append(feature);
        sb.append(" ```");
        eb.setDescription(sb);

        if (event.getJDA().getShardInfo() == null) {
            eb.addField(LanguageHandler.get(lang, "botinfo_stats"), event.getJDA().getGuilds().size() + " servers\n1 shard", true);
            eb.addField(LanguageHandler.get(lang, "botinfo_users"), event.getJDA().getUsers().size() + " unique\n" + event.getJDA().getGuilds().stream().mapToInt(g -> g.getMembers().size()).sum() + " total", true);
            eb.addField(LanguageHandler.get(lang, "botinfo_channels"), event.getJDA().getTextChannels().size() + " Text\n" + event.getJDA().getVoiceChannels().size() + " Voice", true);
        } else {
            eb.addField(LanguageHandler.get(lang, "botinfo_stats"), (event.getClient()).getTotalGuilds() + " Servers\nShard " + (event.getJDA().getShardInfo().getShardId() + 1) + "/" + event.getJDA().getShardInfo().getShardTotal(), true);
            eb.addField(LanguageHandler.get(lang, "botinfo_shard"), event.getJDA().getUsers().size() + " Users\n" + event.getJDA().getGuilds().size() + " Servers", true);
            eb.addField("", event.getJDA().getTextChannels().size() + " Text Channels\n" + event.getJDA().getVoiceChannels().size() + " Voice Channels", true);
        }
        eb.setFooter(LanguageHandler.get(lang, "botinfo_restart"), null);
        eb.setTimestamp(event.getClient().getStartTime());
        event.reply(eb.build());

        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }
}
