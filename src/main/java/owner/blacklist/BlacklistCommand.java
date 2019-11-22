// Author: Tancred423 (https://github.com/Tancred423)
package owner.blacklist;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.Permission;
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
        this.cooldown = Constants.OWNER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
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

                var guild = event.getGuild();
                var author = event.getAuthor();

                if (event.getArgs().equals("show") || event.getArgs().equals("sh")) {
                    var ids = Blacklist.getBlacklistedIds(guild, author);
                    var sb = new StringBuilder();
                    for (var id : ids) sb.append(id).append("\n");
                    event.reply(ids.isEmpty() ? LanguageHandler.get(lang, "blacklist_empty") : sb.toString());
                } else {
                    if (Parser.isValidId(event.getArgs())) {
                        var id = Long.parseLong(event.getArgs());
                        if (Blacklist.isBlacklisted(id, guild, author)) Blacklist.unsetBlacklist(id, guild, author);
                        else {
                            Blacklist.setBlacklist(id, guild, author);
                            var blacklistedGuild = event.getJDA().getGuildById(id);
                            if (blacklistedGuild != null) blacklistedGuild.leave().queue();
                        }
                        event.reactSuccess();
                    } else event.reactError();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
