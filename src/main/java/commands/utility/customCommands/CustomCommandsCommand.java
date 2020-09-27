package commands.utility.customCommands;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import servant.MyGuild;
import servant.MyUser;
import utilities.Constants;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class CustomCommandsCommand extends Command {
    public CustomCommandsCommand() {
        this.name = "customcommands";
        this.aliases = new String[] { "customcommand" };
        this.help = "Lists all available custom commands";
        this.category = new Category("Utility");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.modCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var guild = event.getGuild();
        var myGuild = new MyGuild(guild);
        var prefix = myGuild.getPrefix();
        var customCommands = myGuild.getCustomCommands();
        var lang = myGuild.getLanguageCode();

        var eb = new EmbedBuilder()
                .setColor(new MyUser(event.getAuthor()).getColor())
                .setAuthor(String.format(LanguageHandler.get(lang, "customcommands_title"), guild.getName()), null, guild.getIconUrl())
                .setFooter(LanguageHandler.get(lang, "customcommands_dashboard"));

        if (customCommands.isEmpty()) {
            eb.setDescription(LanguageHandler.get(lang, "customcommands_empty"));
        } else {
            var list = new StringBuilder();
            for (var customCommand : customCommands) {
                if (list.toString().length() + 100 > 1024) {
                    eb.addField("", list.toString(), false);
                    list = new StringBuilder();
                }
                list.append("`").append(prefix).append(customCommand).append("`\n");
            }
            eb.addField("", list.toString(), false);
        }

        event.reply(eb.build());
    }
}
