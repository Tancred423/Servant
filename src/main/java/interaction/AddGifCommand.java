// Author: Tancred423 (https://github.com/Tancred423)
package interaction;

import moderation.guild.Guild;
import net.dv8tion.jda.core.Permission;
import servant.*;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;

public class AddGifCommand extends Command {
    public AddGifCommand() {
        this.name = "addgif";
        this.aliases = new String[]{"addjif"};
        this.help = "Adds gif for interactions.";
        this.category = new Category("Interaction");
        this.arguments = "<interaction> <gifUrl>";
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = true;
        this.cooldown = 0;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[0];
    }

    @Override
    protected void execute(CommandEvent event) {
        var prefix = Servant.config.getDefaultPrefix();
        // Usage
        if (event.getArgs().isEmpty()) {
            try {
                var description = "This is a command to add gifs for the interaction commands.";

                var usage = "**Add a gif**\n" +
                        "Command: `" + prefix + name + " [interaction] [gifUrl]`\n" +
                        "Example: `" + prefix + name + " slap https://i.imgur.com/bbXmAx2.gif`\n";

                var hint = "Image url has to be a [direct link](https://www.urbandictionary.com/define.php?term=direct%20link).";

                event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            }
            return;
        }

        var args = event.getArgs().split(" ");
        if (args.length < 2) {
            event.reply("2 arguments are needed.\n" +
                    "... [interaction] [gifUrl]");
            return;
        }

        var interaction = args[0];
        var gifUrl = args[1];

        // Prevent SQL injection.
        if (!interaction.matches("[a-zA-Z]+")) {
            event.reply("Invalid interaction.");
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
            event.reply("Not a valid gif url. It has to be a direct link!");
            return;
        }

        try {
            var connection = Database.getConnection();
            var insert = connection.prepareStatement("INSERT INTO interaction (interaction,gif) VALUES (?,?)");
            insert.setString(1, interaction);
            insert.setString(2, gifUrl);
            insert.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
           return;
        }

        event.reactSuccess();

        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }
}
