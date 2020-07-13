// Author: Tancred423 (https://github.com/Tancred423)
package commands.fun.profile;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import servant.LoggingTask;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.Constants;
import utilities.TimeUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Timer;
import java.util.concurrent.ThreadLocalRandom;

public class ProfileCommand extends Command {
    public ProfileCommand() {
        this.name = "profile";
        this.aliases = new String[] { "level" };
        this.help = "Displays the profile of you or the mentioned user";
        this.category = new Category("Fun");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.modCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES
        };
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    protected void execute(CommandEvent event) {
        var user = event.getAuthor();
        var profileUser = (event.getMessage().getMentionedMembers().isEmpty() ? user : event.getMessage().getMentionedMembers().get(0).getUser());
        var profileMyUser = new MyUser(profileUser);

        var lang = LanguageHandler.getLanguage(event);
        var p = new MyGuild(event.getGuild()).getPrefix();

        var path = "profile_tmp/";

        var image = new File(path + OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond() + "_" + ThreadLocalRandom.current().nextInt(100) + ".png");

        try {
            var profileImage = new ProfileImage(event.getJDA(), profileUser, event.getGuild(), lang).generateImage();
            if (profileImage == null) {
                System.out.println("ProfleCommand#error null");
                return;
            }
            ImageIO.write(profileImage, "png", image);
        } catch (IOException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, event.getJDA(), name, event));
            return;
        }

        var eb = new EmbedBuilder()
                .setColor(Color.decode(profileMyUser.getColorCode()))
                .setImage("attachment://" + image.getName() + ".png")
                .setFooter(profileUser.equals(user) ?
                                String.format(LanguageHandler.get(lang, "profile_footer1"), p, name) :
                                String.format(LanguageHandler.get(lang, "profile_footer2"), p, name),
                        event.getSelfUser().getEffectiveAvatarUrl());

        event.getChannel().sendFile(image, image.getName() + ".png").embed(eb.build()).queue();

        // Delete File.
        new Timer().schedule(TimeUtil.wrap(image::delete), 10 * 1000);
    }
}
