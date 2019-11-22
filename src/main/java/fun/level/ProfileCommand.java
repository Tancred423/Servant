// Author: Tancred423 (https://github.com/Tancred423)
package fun.level;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import owner.blacklist.Blacklist;
import servant.Log;
import utilities.Constants;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                if (!Toggle.isEnabled(event, name)) return;
                if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;
                var lang = LanguageHandler.getLanguage(event);
                var p = GuildHandler.getPrefix(event);

                event.getChannel().sendTyping().queue();

                var author = event.getAuthor();
                var guild = event.getGuild();
                var profileUser = (event.getMessage().getMentionedMembers().isEmpty() ? author : event.getMessage().getMentionedMembers().get(0).getUser());
                var internalProfileUser = new User(profileUser.getIdLong());

                try {
                    var image = new File(OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond() + ".png");

                    try {
                        var profile = new LevelImage(event, profileUser, event.getGuild(), lang);
                        ImageIO.write(profile.getImage(), "png", image);
                    } catch (IOException e) {
                        new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                        return;
                    }



                    var eb = new EmbedBuilder();
                    eb.setColor(internalProfileUser.getColor(guild, author));
                    eb.setImage("attachment://" + image.getPath());
                    eb.setFooter(profileUser.equals(author) ?
                                    String.format(LanguageHandler.get(lang, "profile_footer1"), p, name) :
                                    String.format(LanguageHandler.get(lang, "profile_footer2"), p, name),
                            event.getSelfUser().getEffectiveAvatarUrl());

                    event.getChannel().sendFile(image, image.getPath()).embed(eb.build()).queue();

                    // Delete File.
                    var thread = new Thread(() -> {
                        try {
                            TimeUnit.MILLISECONDS.sleep(15000); // 15 seconds.
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (!image.delete())
                            new Log(null, event.getGuild(), event.getAuthor(), name, null).sendLog(false);
                    });

                    thread.start();
                } catch (Exception e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                }

                // Statistics.
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
                new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
