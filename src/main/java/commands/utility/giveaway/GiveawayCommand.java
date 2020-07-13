// Author: Tancred423 (https://github.com/Tancred423)
package commands.utility.giveaway;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import servant.MyGuild;
import servant.MyUser;
import utilities.Constants;
import utilities.MessageUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.awt.*;
import java.text.ParseException;

public class GiveawayCommand extends Command {
    public GiveawayCommand() {
        this.name = "giveaway";
        this.aliases = new String[0];
        this.help = "Host a giveaway";
        this.category = new Category("Utility");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.modCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);
        var p = new MyGuild(event.getGuild()).getPrefix();

        var guild = event.getGuild();
        var myGuild = new MyGuild(guild);

        if (event.getArgs().isEmpty()) {
            var description = LanguageHandler.get(lang, "giveaway_description");
            var usage = String.format(LanguageHandler.get(lang, "giveaway_usage"), p, p, p, p, p, p, p);
            var hint = String.format(LanguageHandler.get(lang, "giveaway_hint"), p);
            event.reply(MessageUtil.createUsageEmbed(name, event.getAuthor(), lang, description, aliases, usage, hint));
            return;
        }

        var args = event.getArgs().trim().split(" ");
        var message = event.getMessage();

        if (args[0].equalsIgnoreCase("list")) {
            var currentGiveaways = myGuild.getCurrentGiveaways(event.getJDA(), lang);
            var eb = new EmbedBuilder();
            eb.setColor(Color.decode(new MyUser(message.getAuthor()).getColorCode()));
            eb.setAuthor(LanguageHandler.get(lang, "giveaway_current"), null, message.getGuild().getIconUrl());
            eb.setDescription(currentGiveaways);
            event.reply(eb.build());
        } else {
            try {
                Giveaway.startGiveaway(event, lang);
            } catch (ParseException e) {
                event.replyError(e.getMessage());
            }
        }
    }
}
