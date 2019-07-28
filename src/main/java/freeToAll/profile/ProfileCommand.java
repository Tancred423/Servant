package freeToAll.profile;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import moderation.guild.Guild;
import net.dv8tion.jda.core.Permission;
import servant.Log;

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
        this.help = "shows your profile.";
        this.category = new Category("Free to all");
        this.botPermissions = new Permission[]{Permission.MESSAGE_ATTACH_FILES};
        this.guildOnly = true;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendTyping();

        // Enabled?
        try {
            if (!new Guild(event.getGuild().getIdLong()).getToggleStatus("profile")) return;
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }

        // Create File.
        String currentDir   = System.getProperty("user.dir");
        String profileTmpDir = currentDir   + "/profile_tmp" ;
        String imageDir     = profileTmpDir + "/" + OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond() + ".png";

        File profileTmp = new File(profileTmpDir);
        if (!profileTmp.exists()) profileTmp.mkdir();
        File image = new File(imageDir);

        // Create Image.
        try {
            Profile profile = new Profile(event.getAuthor(), event.getGuild());
            ImageIO.write(profile.getImage(), "png", image);
        } catch (IOException IOE) {
            new Log(IOE, event, name).sendLogIOCommandEvent(true);
            return;
        } catch (SQLException sqlE) {
            new Log(sqlE, event, name).sendLogSqlCommandEvent(true);
            return;
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
            return;
        }

        event.getChannel().sendFile(image).queue();

        // Delete File.
        Thread thread = new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(10000); // 10 seconds.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!image.delete()) new Log(event, name).sendLogProfileDelete();
        });

        thread.start();

        // Statistics.
        try {
            new servant.User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }
    }
}
