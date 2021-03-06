// Modified by: Tancred423 (https://github.com/Tancred423)
package commands.owner;

import files.language.LanguageHandler;
import servant.MyUser;
import net.dv8tion.jda.api.Permission;
import servant.Servant;
import utilities.Constants;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import zJdaUtilsLib.com.jagrosh.jdautilities.examples.doc.Author;
import zJdaUtilsLib.com.jagrosh.jdautilities.menu.Paginator;

import java.awt.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Author("John Grosh (jagrosh)")
public class ServerlistCommand extends Command {
    private final Paginator.Builder pbuilder;
    public ServerlistCommand(EventWaiter waiter) {
        this.name = "serverlist";
        this.aliases = new String[] { "guildlist" };
        this.help = "Servers the bot is on.";
        this.category = new Category("Owner");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = true;
        this.modCommand = false;
        this.cooldown = Constants.OWNER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION
        };

        pbuilder = new Paginator.Builder().setColumns(1)
                .setItemsPerPage(10)
                .showPageNumbers(true)
                .waitOnSinglePage(false)
                .useNumberedItems(false)
                .setFinalAction(m -> {
                    try {
                        m.clearReactions().queue();
                    } catch(Exception ex) {
                        m.delete().queue();
                    }
                })
                .setEventWaiter(waiter)
                .setTimeout(1, TimeUnit.MINUTES);
    }

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                int page = 1;
                String lang = LanguageHandler.getLanguage(event);

                if (!event.getArgs().isEmpty()) {
                    try {
                        page = Integer.parseInt(event.getArgs());
                    } catch (NumberFormatException e) {
                        event.reply(LanguageHandler.get(lang, "serverlist_integer"));
                        return;
                    }
                }

                pbuilder.clearItems();
                event.getJDA().getGuilds().stream()
                        .map(g -> "**" + g.getName() + "** (ID:" + g.getId() + ") ~ " + g.getMembers().size() + " " + LanguageHandler.get(lang, "serverlist_members"))
                        .forEach(pbuilder::addItems);

                Paginator p;
                event.getJDA().getShardInfo();
                p = pbuilder.setColor(Color.decode(new MyUser(event.getAuthor()).getColorCode()))
                        .setText(String.format(LanguageHandler.get(lang, "serverlist_connected"), event.getSelfUser().getName()) + ("(Shard ID " + event.getJDA().getShardInfo().getShardId() + "):"))
                        .setUsers(event.getAuthor())
                        .build();

                p.paginate(event.getChannel(), page);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Servant.fixedThreadPool);
    }
}
