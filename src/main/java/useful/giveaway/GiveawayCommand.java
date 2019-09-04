// Author: Tancred423 (https://github.com/Tancred423)
package useful.giveaway;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import servant.Log;
import servant.Servant;
import utilities.Constants;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.awt.*;
import java.sql.SQLException;

public class GiveawayCommand extends Command {
    public GiveawayCommand() {
        this.name = "giveaway";
        this.aliases = new String[0];
        this.help = "Host a giveaway.";
        this.category = new Category("Useful");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!Toggle.isEnabled(event, name)) return;

        var lang = LanguageHandler.getLanguage(event, name);
        var p = GuildHandler.getPrefix(event, name);

        if (event.getArgs().isEmpty()) {
            try {
                var description = LanguageHandler.get(lang, "giveaway_description");
                var usage = String.format(LanguageHandler.get(lang, "giveaway_usage"), p, p, p, p, p, p, p);
                var hint = String.format(LanguageHandler.get(lang, "giveaway_hint"), p);
                event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            }
            return;
        }

        var args = event.getArgs().trim().split(" ");
        var message = event.getMessage();

        if (args[0].equalsIgnoreCase("list")) {
            try {
                var currentGiveaways = Giveaway.getCurrentGiveaways(message, lang);
                var eb = new EmbedBuilder();
                try {
                    eb.setColor(new moderation.user.User(message.getAuthor().getIdLong()).getColor());
                } catch (SQLException e) {
                    eb.setColor(Color.decode(Servant.config.getDefaultColorCode()));
                }
                eb.setAuthor(LanguageHandler.get(lang, "giveaway_current"), null, message.getGuild().getIconUrl());
                eb.setDescription(currentGiveaways);
                event.reply(eb.build());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            }
        } else {
            if (args[0].startsWith("\"")) Giveaway.startGiveaway(event, args, lang);
            else Giveaway.sendWrongArgumentError(message, lang);
        }

        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }
}
