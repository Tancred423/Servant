// Author: Tancred423 (https://github.com/Tancred423)
package useful.giveaway;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.guild.Server;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import utilities.Constants;
import utilities.MessageUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

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
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);
        var p = GuildHandler.getPrefix(event);

        var guild = event.getGuild();
        var server = new Server(guild);

        if (event.getArgs().isEmpty()) {
            var description = LanguageHandler.get(lang, "giveaway_description");
            var usage = String.format(LanguageHandler.get(lang, "giveaway_usage"), p, p, p, p, p, p, p);
            var hint = String.format(LanguageHandler.get(lang, "giveaway_hint"), p);
            event.reply(MessageUtil.createUsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint));
            return;
        }

        var args = event.getArgs().trim().split(" ");
        var message = event.getMessage();

        if (args[0].equalsIgnoreCase("list")) {
            var currentGiveaways = server.getCurrentGiveaways(event.getJDA(), lang);
            var eb = new EmbedBuilder();
            eb.setColor(new Master(message.getAuthor()).getColor());
            eb.setAuthor(LanguageHandler.get(lang, "giveaway_current"), null, message.getGuild().getIconUrl());
            eb.setDescription(currentGiveaways);
            event.reply(eb.build());
        } else {
            if (args[0].startsWith("\"")) GiveawayHandler.startGiveaway(event, args, lang);
            else GiveawayHandler.sendWrongArgumentError(message, lang);
        }
    }
}
