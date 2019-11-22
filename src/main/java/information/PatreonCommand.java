// Author: Tancred423 (https://github.com/Tancred423)
package information;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.user.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import owner.blacklist.Blacklist;
import utilities.Constants;
import utilities.Image;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.examples.doc.Author;

import java.util.concurrent.CompletableFuture;

@Author("John Grosh (jagrosh)")
public class PatreonCommand extends Command {
    public PatreonCommand() {
        this.name = "patreon";
        this.aliases = new String[] { "donation", "serverboost", "boost" };
        this.help = "Support me <3";
        this.category = new Category("Information");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
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
                if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

                var lang = LanguageHandler.getLanguage(event);
                var guild = event.getGuild();
                var author = event.getAuthor();
                var eb = new EmbedBuilder();

                eb.setColor(new User(event.getAuthor().getIdLong()).getColor(guild, author));
                eb.setAuthor(LanguageHandler.get(lang, "patreon_supportserver"), null, "https://i.imgur.com/rCnhGKA.jpg"); // Patreon Icon
                eb.setDescription(LanguageHandler.get(lang, "patreon_description"));
                eb.setThumbnail(Image.getImageUrl("love", guild, author));
                eb.addField("1. " + LanguageHandler.get(lang, "patreon_patreontitle"), LanguageHandler.get(lang, "patreon_subscription"), false);
                eb.addField("$1+/month", LanguageHandler.get(lang, "patreon_$1"), true);
                eb.addField("$3+/month", LanguageHandler.get(lang, "patreon_$3"), true);
                eb.addField("$5+/month", LanguageHandler.get(lang, "patreon_$5"), true);
                eb.addField("$10+/month", LanguageHandler.get(lang, "patreon_$10"), true);
                eb.addField("2. " + LanguageHandler.get(lang, "patreon_donationtitle"), LanguageHandler.get(lang, "patreon_donation"), false);
                eb.addField("$5+ Donated In Lifetime", LanguageHandler.get(lang, "patreon_donation_$5"), true);
                eb.addField("3. " + LanguageHandler.get(lang, "patreon_serverboosttitle"), LanguageHandler.get(lang, "patreon_serverboost"), false);

                eb.setFooter(LanguageHandler.get(lang, "patreon_thanks"), event.getSelfUser().getAvatarUrl());
                event.reply(eb.build());

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
