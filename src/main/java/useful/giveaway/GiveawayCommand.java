// Author: Tancred423 (https://github.com/Tancred423)
package useful.giveaway;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import owner.blacklist.Blacklist;
import servant.Servant;
import utilities.Constants;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.concurrent.CompletableFuture;

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
        CompletableFuture.runAsync(() -> {
            try {
                if (!Toggle.isEnabled(event, name)) return;
                if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

                var lang = LanguageHandler.getLanguage(event);
                var p = GuildHandler.getPrefix(event);

                var guild = event.getGuild();
                var author = event.getAuthor();

                if (event.getArgs().isEmpty()) {
                    var description = LanguageHandler.get(lang, "giveaway_description");
                    var usage = String.format(LanguageHandler.get(lang, "giveaway_usage"), p, p, p, p, p, p, p);
                    var hint = String.format(LanguageHandler.get(lang, "giveaway_hint"), p);
                    event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
                    return;
                }

                var args = event.getArgs().trim().split(" ");
                var message = event.getMessage();

                if (args[0].equalsIgnoreCase("list")) {
                    var currentGiveaways = Giveaway.getCurrentGiveaways(event.getJDA(), message, lang, guild, author);
                    var eb = new EmbedBuilder();
                    eb.setColor(new User(message.getAuthor().getIdLong()).getColor(guild, author));
                    eb.setAuthor(LanguageHandler.get(lang, "giveaway_current"), null, message.getGuild().getIconUrl());
                    eb.setDescription(currentGiveaways);
                    event.reply(eb.build());
                } else {
                    if (args[0].startsWith("\"")) Giveaway.startGiveaway(event, args, lang);
                    else Giveaway.sendWrongArgumentError(message, lang);
                }

                // Statistics.
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
                new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Servant.cpuPool);
    }
}
