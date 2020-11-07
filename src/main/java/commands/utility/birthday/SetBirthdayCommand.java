package commands.utility.birthday;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.Permission;
import servant.MyGuild;
import servant.MyUser;
import utilities.Constants;
import utilities.MessageUtil;
import utilities.Parser;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class SetBirthdayCommand extends Command {
    public SetBirthdayCommand() {
        this.name = "setbirthday";
        this.aliases = new String[]{"setbday", "addbirthday", "addbday"};
        this.help = "Set/Update your birthday";
        this.category = new Category("Utility");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[]{
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var jda = event.getJDA();
        var guild = event.getGuild();
        var myGuild = new MyGuild(guild);
        var user = event.getAuthor();
        var myUser = new MyUser(user);

        var lang = LanguageHandler.getLanguage(event);
        var p = myGuild.getPrefix();

        var birthday = event.getArgs();

        if (birthday.isEmpty()) {
            var description = LanguageHandler.get(lang, "setbirthday_description");
            var usage = String.format(LanguageHandler.get(lang, "setbirthday_usage"), p, name, p, name);
            var hint = String.format(LanguageHandler.get(lang, "setbirthday_hint"), Constants.WEBSITE_DASHBOARD);
            event.reply(MessageUtil.createUsageEmbed(name, user, lang, description, aliases, usage, hint));
            return;
        }

        if (!Parser.isValidDate(birthday)) {
            event.replyWarning(String.format(LanguageHandler.get(lang, "setbirthday_invalid"), event.getArgs()));
        } else {
            myUser.setBirthday(birthday);
            myUser.addBirthdayGuild(guild.getIdLong());

            event.replySuccess(String.format(LanguageHandler.get(lang, "setbirthday_success"), birthday, guild.getName()));
        }
    }
}
