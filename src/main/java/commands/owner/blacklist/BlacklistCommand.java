// Author: Tancred423 (https://github.com/Tancred423)
package commands.owner.blacklist;

import files.language.LanguageHandler;
import servant.MyGuild;
import servant.MyUser;
import net.dv8tion.jda.api.Permission;
import servant.Servant;
import utilities.Constants;
import utilities.Parser;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.concurrent.CompletableFuture;

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
        this.modCommand = false;
        this.cooldown = Constants.OWNER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                var lang = LanguageHandler.getLanguage(event);

                if (event.getArgs().isEmpty()) {
                    event.replyError(LanguageHandler.get(lang, "blacklist_missingid"));
                    return;
                }

                var jda = event.getJDA();

                if (event.getArgs().equals("show") || event.getArgs().equals("sh")) {
                    var blacklist = new Blacklist(jda);
                    var ids = blacklist.getIds();
                    var sb = new StringBuilder();
                    for (var id : ids) sb.append(id).append("\n");
                    event.reply(ids.isEmpty() ? LanguageHandler.get(lang, "blacklist_empty") : sb.toString());
                } else {
                    if (Parser.isValidId(event.getArgs())) {
                        var id = Long.parseLong(event.getArgs());
                        var guildToChange = jda.getGuildById(id);
                        var userToChange = jda.getUserById(id);

                        if (guildToChange != null) {
                            var serverToChange = new MyGuild(guildToChange);
                            if (serverToChange.isBlacklisted()) serverToChange.unsetBlacklist();
                            else serverToChange.setBlacklist();
                            event.reactSuccess();
                        } else if (userToChange != null) {
                            var masterToChange = new MyUser(userToChange);
                            if (masterToChange.isBlacklisted()) masterToChange.unsetBlacklist();
                            else masterToChange.setBlacklist();
                            event.reactSuccess();
                        } else {
                            event.reactWarning();
                        }
                    } else event.reactError();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Servant.fixedThreadPool);
    }
}
