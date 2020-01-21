// Modified by: Tancred423 (https://github.com/Tancred423)
package information;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import owner.blacklist.Blacklist;
import servant.Servant;
import utilities.Constants;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.commons.JDAUtilitiesInfo;
import zJdaUtilsLib.com.jagrosh.jdautilities.examples.doc.Author;

import java.util.concurrent.CompletableFuture;

@Author("John Grosh (jagrosh)")
public class BotInfoCommand extends Command {
    private final Permission[] perms;
    private String oauthLink;
    
    public BotInfoCommand() {
        this.name = "botinfo";
        this.aliases = new String[] { "about" };
        this.help = "Shows info about the bot.";
        this.category = new Category("Information");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };

        this.perms = new Permission[] {Permission.ADMINISTRATOR};
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

                if (oauthLink == null) {
                    try {
                        var info = event.getJDA().retrieveApplicationInfo().complete();
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

                eb.setColor(new User(event.getAuthor().getIdLong()).getColor(guild, author));
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

                event.getJDA().getShardInfo();
                eb.addField(LanguageHandler.get(lang, "botinfo_stats"), (event.getClient()).getTotalGuilds() + " Servers\nShard " + (event.getJDA().getShardInfo().getShardId() + 1) + "/" + event.getJDA().getShardInfo().getShardTotal(), true);
                eb.addField(LanguageHandler.get(lang, "botinfo_shard"), event.getJDA().getUsers().size() + " Users\n" + event.getJDA().getGuilds().size() + " Servers", true);
                eb.addField("", event.getJDA().getTextChannels().size() + " Text Channels\n" + event.getJDA().getVoiceChannels().size() + " Voice Channels", true);
                eb.setFooter(LanguageHandler.get(lang, "botinfo_restart"), null);
                eb.setTimestamp(event.getClient().getStartTime());
                event.reply(eb.build());

                // Statistics.
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
                if (event.getGuild() != null)
                    new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Servant.threadPool);
    }
}
