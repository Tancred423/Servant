// Author: Tancred423 (https://github.com/Tancred423)
package moderation.toggle;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.guild.Server;
import net.dv8tion.jda.api.Permission;
import utilities.Constants;
import utilities.MessageUtil;
import utilities.NameAliasUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class ToggleCommand extends Command {
    public ToggleCommand() {
        this.name = "toggle";
        this.aliases = new String[0];
        this.help = "Toggles bot's features.";
        this.category = new Command.Category("Moderation");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.GUILD;
        this.userPermissions = new Permission[] { Permission.MANAGE_SERVER };
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);
        var p = GuildHandler.getPrefix(event);

        if (event.getArgs().isEmpty()) {
            var description = String.format(LanguageHandler.get(lang, "toggle_description"), p, name);
            var usage = String.format(LanguageHandler.get(lang, "toggle_usage"), p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name);
            var hint = LanguageHandler.get(lang, "toggle_hint");

            event.reply(MessageUtil.createUsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint));
            return;
        }

        var validFeatures = NameAliasUtil.getValidToggles();

        var args = event.getArgs().split(" ");
        if (args.length < 2) {
            event.reply(LanguageHandler.get(lang, "toggle_args"));
            return;
        }

        var feature = NameAliasUtil.getToggleAlias(args[0]);
        // Has to be improved. Redundant text.
        if (feature == null) {
            event.reply(LanguageHandler.get(lang, "toggle_invalid_feature"));
            return;
        } else if (!validFeatures.contains(feature) && !feature.equals("all")) {
            event.reply(LanguageHandler.get(lang, "toggle_invalid_feature"));
            return;
        }

        var arg1 = args[1].toLowerCase();
        if (!arg1.equals("on") && !arg1.equals("off") && !arg1.equals("status") && !arg1.equals("show")) {
            event.reply(LanguageHandler.get(lang, "toggle_invalid_argument"));
            return;
        }

        var guild = event.getGuild();
        var server = new Server(guild);

        if (arg1.equals("show") || arg1.equals("status")) {
            if (feature.equals("all")) {
                // Toggle Status All Features
                var stringBuilder = new StringBuilder();
                for (var validFeature : validFeatures) {
                    var toggleStatus = server.getToggleStatus(validFeature);
                    stringBuilder.append(validFeature).append(": ").append(toggleStatus ? "on" : "off").append("\n");
                }
                event.reply(stringBuilder.toString());
            } else {
                // Toggle Status Single Feature
                var toggleStatus = server.getToggleStatus(feature);
                event.reply(feature + "'s status: " + (toggleStatus ? "on" : "off"));
            }
            return;
        }

        var statusBool = arg1.equals("on");

        if (feature.equals("all"))
            for (var validFeature : validFeatures)
                server.setToggleStatus(validFeature, statusBool);
        else server.setToggleStatus(feature, statusBool);
        event.reactSuccess();
    }
}
