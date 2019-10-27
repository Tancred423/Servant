// Modified by: Tancred423 (https://github.com/Tancred423)
package owner;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import net.dv8tion.jda.core.Permission;
import servant.Log;
import moderation.user.User;
import utilities.Constants;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import zJdaUtilsLib.com.jagrosh.jdautilities.examples.doc.Author;
import zJdaUtilsLib.com.jagrosh.jdautilities.menu.Paginator;

@Author("John Grosh (jagrosh)")
public class ServerlistCommand extends Command {
    private final Paginator.Builder pbuilder;
    public ServerlistCommand(EventWaiter waiter) {
        this.name = "serverlist";
        this.aliases = new String[]{"guildlist"};
        this.help = "Servers the bot is on.";
        this.category = new Category("Owner");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = true;
        this.cooldown = Constants.OWNER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION};

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
            int page = 1;
            String lang = LanguageHandler.getLanguage(event, name);
            if (lang.equals("invalid")) return;

            if (!event.getArgs().isEmpty()) {
                try {
                    page = Integer.parseInt(event.getArgs());
                } catch(NumberFormatException e) {
                    event.reply(LanguageHandler.get(lang, "guildlist_integer"));
                    return;
                }
            }

            pbuilder.clearItems();
            event.getJDA().getGuilds().stream()
                    .map(g -> "**" + g.getName() + "** (ID:" + g.getId() + ") ~ " + g.getMembers().size() + " " + LanguageHandler.get(lang, "guildlist_members"))
                    .forEach(pbuilder::addItems);

            Paginator p;
            try {
                p = pbuilder.setColor(new User(event.getAuthor().getIdLong()).getColor())
                        .setText(String.format(LanguageHandler.get(lang, "guildlist_connected"), event.getSelfUser().getName()) +
                                (event.getJDA().getShardInfo() == null ? ":" : "(Shard ID " + event.getJDA().getShardInfo().getShardId() + "):"))
                        .setUsers(event.getAuthor())
                        .build();
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                return;
            }

            p.paginate(event.getChannel(), page);

            // Statistics.
            try {
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
                if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
            }
        });
    }
}
