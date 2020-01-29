// Author: Tancred423 (https://github.com/Tancred423)
package fun.level;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import servant.Log;
import utilities.Constants;
import utilities.Time;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import javax.imageio.ImageIO;
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
        this.help = "Your or mentioned user's profile.";
        this.category = new Category("Fun");
        this.arguments = "[optional @user]";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    protected void execute(CommandEvent event) {
        var user = event.getAuthor();
        var profileUser = (event.getMessage().getMentionedMembers().isEmpty() ? user : event.getMessage().getMentionedMembers().get(0).getUser());
        var profileMaster = new Master(profileUser);

        var lang = LanguageHandler.getLanguage(event);
        var p = GuildHandler.getPrefix(event);

        event.getChannel().sendTyping().queue();
        event.getMessage().addReaction("\uD83D\uDD51").queue();

        var path = "profile_tmp/";

        var image = new File(path + OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond() + "_" + ThreadLocalRandom.current().nextInt(100) + ".png");

        try {
            var profileImage = new ProfileImage(profileUser, event.getGuild(), lang).generateImage();
            if (profileImage == null) {
                System.out.println("error null");
                return;
            }
            ImageIO.write(profileImage, "png", image);
        } catch (IOException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            return;
        }

        var eb = new EmbedBuilder()
                .setColor(profileMaster.getColor())
                .setImage("attachment://" + image.getName() + ".png")
                .setFooter(profileUser.equals(user) ?
                                String.format(LanguageHandler.get(lang, "profile_footer1"), p, name) :
                                String.format(LanguageHandler.get(lang, "profile_footer2"), p, name),
                        event.getSelfUser().getEffectiveAvatarUrl());

        event.getChannel().sendFile(image, image.getName() + ".png").embed(eb.build()).queue(m -> {
            event.getMessage().clearReactions().queue();
        });

        // Delete File.
        new Timer().schedule(Time.wrap(image::delete), 10 * 1000);
    }
}
