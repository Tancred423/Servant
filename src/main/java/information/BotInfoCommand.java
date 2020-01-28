// Modified by: Tancred423 (https://github.com/Tancred423)
package information;

import files.language.LanguageHandler;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import utilities.Constants;
import utilities.Image;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.examples.doc.Author;

@Author("John Grosh (jagrosh)")
public class BotInfoCommand extends Command {
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
    }
    
    @Override
    protected void execute(CommandEvent event) {
        var jda = event.getJDA();
        var lang = LanguageHandler.getLanguage(event);
        var guild = event.getGuild();
        var user = event.getAuthor();
        var master = new Master(user);
        var selfUser = event.getSelfUser();

        var shardManager = event.getJDA().getShardManager();
        var totalGuilds = 0;
        if (shardManager == null) totalGuilds = event.getJDA().getGuilds().size();
        else totalGuilds = shardManager.getGuilds().size();

        event.reply(new EmbedBuilder()
                .setColor(master.getColor())
                .setAuthor(String.format(LanguageHandler.get(lang, "botinfo_authorname"), selfUser.getName()), null, selfUser.getAvatarUrl())
                .setDescription(String.format(LanguageHandler.get(lang, "botinfo_description"), selfUser.getName()))
                .addField(LanguageHandler.get(lang, "botinfo_links_name"), LanguageHandler.get(lang, "botinfo_links_value"), false)
                .addField(LanguageHandler.get(lang, "botinfo_stats"), totalGuilds + " Servers\nShard " + (jda.getShardInfo().getShardId() + 1) + "/" + jda.getShardInfo().getShardTotal(), true)
                .addField(LanguageHandler.get(lang, "botinfo_shard"), jda.getUsers().size() + " Users\n" + jda.getGuilds().size() + " Servers", true)
                .addField(LanguageHandler.get(lang, "botinfo_channels"), jda.getTextChannels().size() + " Text Channels\n" + jda.getVoiceChannels().size() + " Voice Channels", true)
                .setFooter(LanguageHandler.get(lang, "botinfo_restart"), Image.getImageUrl("clock", guild, user))
                .setTimestamp(event.getClient().getStartTime())
                .build()
        );
    }
}
