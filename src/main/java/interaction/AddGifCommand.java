package interaction;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import servant.Database;
import servant.Log;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@CommandInfo(
        name = {"AddGif"},
        description = "Add a gif for an interaction command.",
        usage = "addgif [interaction] [gifUrl]",
        requirements = {"Gif has to be a direct link!"}
)
@Author("Tancred")
public class AddGifCommand extends Command {
    public AddGifCommand() {
        this.name = "AddGif";
        this.aliases = new String[]{"AddJif"};
        this.help = "adds a gif for the interaction commands";
        this.category = new Category("Owner");
        this.arguments = "[interaction] [gifUrl]";
        this.guildOnly = false;
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(" ");
        if (args.length < 2) {
            event.reply("2 arguments are needed.\n" +
                    "... [interaction] [gifUrl]");
            return;
        }

        String interaction = args[0];
        String gifUrl = args[1];

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
            new Log(e, event, name).sendLogHttp();
            return;
        }
        String contentType = c.getContentType();
        if (!contentType.equals("image/gif")) {
            event.reply("Not a valid gif url. It has to be a direct link!");
            return;
        }

        try {
            Connection connection = Database.getConnection();
            PreparedStatement insert = connection.prepareStatement("INSERT INTO interaction (interaction,gif) VALUES (?,?)");
            insert.setString(1, interaction);
            insert.setString(2, gifUrl);
            insert.executeUpdate();
            connection.close();
        } catch (SQLException e) {
           new Log(e, event, name).sendLogSQL();
           return;
        }

        event.reactSuccess();
    }
}
