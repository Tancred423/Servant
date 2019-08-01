// Author: Tancred423 (https://github.com/Tancred423)
package freeToAll.profile;

import moderation.guild.Guild;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;
import servant.Log;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import javax.imageio.ImageIO;
import java.awt.geom.NoninvertibleTransformException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

public class ProfileCommand extends Command {
    public ProfileCommand() {
        this.name = "profile";
        this.aliases = new String[]{"profil"};
        this.help = "Your or mentioned user's profile.";
        this.category = new Category("Free to all");
        this.arguments = "[optional @user]";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = 5;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[]{Permission.MESSAGE_ATTACH_FILES};
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendTyping().queue();

        // Enabled?
        try {
            if (!new Guild(event.getGuild().getIdLong()).getToggleStatus("profile")) return;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, null).sendLog(false);
        }

        var author = event.getAuthor();
        var mentioned = (event.getMessage().getMentionedMembers().isEmpty() ? null : event.getMessage().getMentionedMembers().get(0).getUser());

        // Create File.
        var currentDir   = System.getProperty("user.dir");
        var profileTmpDir = currentDir   + "/profile_tmp" ;
        var imageDir     = profileTmpDir + "/" + OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond() + ".png";

        var profileTmp = new File(profileTmpDir);
        if (!profileTmp.exists()) profileTmp.mkdir();
        var image = new File(imageDir);

        // Create Image.
        try {
            var profile = new Profile((mentioned == null ? author : mentioned), event.getGuild());
            ImageIO.write(profile.getImage(), "png", image);
        } catch (IOException | SQLException | NoninvertibleTransformException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            return;
        }

        event.getChannel().sendFile(image).queue();

        // Delete File.
        var thread = new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(10000); // 10 seconds.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!image.delete()) new Log(null, event.getGuild(), event.getAuthor(), name, null).sendLog(false);
        });

        thread.start();

        // Statistics.
        try {
            new servant.User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }
}
