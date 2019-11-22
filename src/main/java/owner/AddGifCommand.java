// Author: Tancred423 (https://github.com/Tancred423)
package owner;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.user.User;
import moderation.guild.Guild;
import net.dv8tion.jda.api.Permission;
import servant.*;
import utilities.Constants;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

import static utilities.DatabaseConn.closeQuietly;

public class AddGifCommand extends Command {
    public AddGifCommand() {
        this.name = "addgif";
        this.aliases = new String[] { "addjif" };
        this.help = "Adds gif for interactions.";
        this.category = new Category("Owner");
        this.arguments = null;
        this.hidden = false;
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
                var p = GuildHandler.getPrefix(event);

                if (event.getArgs().isEmpty()) {
                    var description = LanguageHandler.get(lang, "addgif_description");
                    var usage = String.format(LanguageHandler.get(lang, "addgif_usage"), p, name, p, name);
                    var hint = LanguageHandler.get(lang, "addgif_hint");

                    event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
                    return;
                }

                var args = event.getArgs().split(" ");
                if (args.length < 2) {
                    event.reply(LanguageHandler.get(lang, "addgif_args"));
                    return;
                }

                var interaction = args[0];
                var gifUrl = args[1];

                // Prevent SQL injection.
                if (!interaction.matches("[a-zA-Z]+")) {
                    event.reply(LanguageHandler.get(lang, "addgif_interaction"));
                    return;
                }

                // Check for valid url.
                URL url;
                URLConnection c;
                try {
                    url = new URL(gifUrl);
                    c = url.openConnection();
                } catch (IOException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    return;
                }
                var contentType = c.getContentType();
                if (!contentType.equals("image/gif")) {
                    event.reply(LanguageHandler.get(lang, "addgif_direct_link"));
                    return;
                }

                Connection connection = null;
                var guild = event.getGuild();
                var author = event.getAuthor();

                try {
                    connection = Servant.db.getHikari().getConnection();
                    var insert = connection.prepareStatement("INSERT INTO interaction (interaction,gif) VALUES (?,?)");
                    insert.setString(1, interaction);
                    insert.setString(2, gifUrl);
                    insert.executeUpdate();
                } catch (SQLException e) {
                    new Log(e, guild, author, "addgif", null).sendLog(false);
                } finally {
                    closeQuietly(connection);
                }

                event.reactSuccess();

                // Statistics.
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
                if (event.getGuild() != null)
                    new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
