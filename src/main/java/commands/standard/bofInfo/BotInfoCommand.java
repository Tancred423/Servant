// Author: John Grosh (jagrosh)
// Modified by: Tancred423 (https://github.com/Tancred423)
package commands.standard.bofInfo;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import servant.MyUser;
import utilities.Constants;
import utilities.ImageUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.awt.*;

public class BotInfoCommand extends Command {
    public BotInfoCommand() {
        this.name = "botinfo";
        this.aliases = new String[] { "about" };
        this.help = "All information about Servant";
        this.category = new Category("Standard");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.modCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };
    }
    
    @Override
    protected void execute(CommandEvent event) {
        var jda = event.getJDA();
        var lang = LanguageHandler.getLanguage(event);
        var user = event.getAuthor();
        var myUser = new MyUser(user);
        var selfUser = event.getSelfUser();

        var shardManager = event.getJDA().getShardManager();
        var totalGuilds = 0;
        if (shardManager == null) totalGuilds = event.getJDA().getGuilds().size();
        else totalGuilds = shardManager.getGuilds().size();

        event.reply(new EmbedBuilder()
                .setColor(Color.decode(myUser.getColorCode()))
                .setTitle(String.format(LanguageHandler.get(lang, "botinfo_authorname"), selfUser.getName()))
                .setThumbnail(selfUser.getAvatarUrl() + "?size=2048")
                .setDescription(String.format(LanguageHandler.get(lang, "botinfo_description"), selfUser.getName(), Constants.WEBSITE_HELP, Constants.SUPPORT, Constants.WEBSITE))
                .addField(LanguageHandler.get(lang, "botinfo_links_name"), String.format(LanguageHandler.get(lang, "botinfo_links_value"), Constants.WEBSITE, Constants.INVITE, Constants.SUPPORT, Constants.TOPGG), false)
                .addField(LanguageHandler.get(lang, "botinfo_stats"), totalGuilds + " Servers\nShard " + (jda.getShardInfo().getShardId() + 1) + "/" + jda.getShardInfo().getShardTotal(), true)
                .addField(LanguageHandler.get(lang, "botinfo_shard"), jda.getUsers().size() + " Users\n" + jda.getGuilds().size() + " Servers", true)
                .addField(LanguageHandler.get(lang, "botinfo_channels"), jda.getTextChannels().size() + " Text Channels\n" + jda.getVoiceChannels().size() + " Voice Channels", true)
                .setFooter(LanguageHandler.get(lang, "botinfo_restart"), ImageUtil.getUrl(jda, "clock"))
                .setTimestamp(event.getClient().getStartTime())
                .build()
        );
    }
}
