// Author: Tancred423 (https://github.com/Tancred423)
package information;

import files.language.LanguageHandler;
import moderation.user.User;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import servant.Servant;
import utilities.Constants;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.examples.doc.Author;

import java.awt.*;
import java.sql.SQLException;

@Author("John Grosh (jagrosh)")
public class PatreonCommand extends Command {
    public PatreonCommand() {
        this.name = "patreon";
        this.aliases = new String[]{"donation"};
        this.help = "Support me <3";
        this.category = new Category("Information");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event, name);
        var eb = new EmbedBuilder();

        try {
            eb.setColor(new User(event.getAuthor().getIdLong()).getColor());
        } catch (SQLException e) {
            eb.setColor(Color.decode(Servant.config.getDefaultColorCode()));
        }
        eb.setAuthor(LanguageHandler.get(lang, "patreon_supportserver"), null, "https://i.imgur.com/rCnhGKA.jpg"); // Patreon Icon
        eb.setDescription(LanguageHandler.get(lang, "patreon_description"));
        eb.addField("1. Patreon", LanguageHandler.get(lang, "patreon_subscription"), false);
        eb.addField("$1+/month", LanguageHandler.get(lang, "patreon_$1"), true);
        eb.addField("$3+/month", LanguageHandler.get(lang, "patreon_$3"), true);
        eb.addField("$5+/month", LanguageHandler.get(lang, "patreon_$5"), true);
        eb.addField("$10+/month", LanguageHandler.get(lang, "patreon_$10"), true);
        eb.addField("2. Donation", LanguageHandler.get(lang, "patreon_donation"), false);

        eb.setFooter(LanguageHandler.get(lang, "patreon_thanks"), event.getSelfUser().getAvatarUrl());
        event.reply(eb.build());
    }
}
