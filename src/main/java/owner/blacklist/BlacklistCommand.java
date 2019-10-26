// Author: Tancred423 (https://github.com/Tancred423)
package owner.blacklist;

import net.dv8tion.jda.core.Permission;
import utilities.Constants;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class BlacklistCommand extends Command {
    public BlacklistCommand() {
        this.name = "blacklist";
        this.aliases = new String[0];
        this.help = "Blacklists guilds or users";
        this.category = new Category("Owner");
        this.arguments = null;
        this.hidden = true;
        this.guildOnly = false;
        this.ownerCommand = true;
        this.cooldown = Constants.OWNER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[0];
    }

    @Override
    protected void execute(CommandEvent event) {
//        var lang = LanguageHandler.getLanguage(event, name);
//
//        if (event.getArgs().isEmpty()) {
//            event.replyError(LanguageHandler.get(lang, "blacklist_missingid"));
//            return;
//        }
//
//        try {
//            if (event.getArgs().equals("show") || event.getArgs().equals("sh")) {
//                var ids = await(Blacklist.getBlacklistedIdsAsync());
//                var sb = new StringBuilder();
//                for (var id : ids) sb.append(id).append("\n");
//                event.reply(ids.isEmpty() ? LanguageHandler.get(lang, "blacklist_empty") : sb.toString());
//            } else {
//                if (Parser.isValidId(event.getArgs())) {
//                    var id = Long.parseLong(event.getArgs());
//                    if (await(Blacklist.isBlacklistedAsync(id))) await(Blacklist.unsetBlacklistAsync(id, event));
//                    else {
//                        await(Blacklist.setBlacklistAsync(id, event));
//                        var blacklistedGuild = event.getJDA().getGuildById(id);
//                        if (blacklistedGuild != null) blacklistedGuild.leave().queue();
//                    }
//                    event.reactSuccess();
//                } else event.reactError();
//            }
//        } catch (SQLException e) {
//            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
//        }
    }
}
