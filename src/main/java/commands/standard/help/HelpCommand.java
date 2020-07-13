package commands.standard.help;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.User;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.Constants;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.awt.*;
import java.util.Objects;

public class HelpCommand extends Command {
    public HelpCommand() {
        this.name = "help";
        this.aliases = new String[] { "h" };
        this.help = "Help message";
        this.category = new Category("Standard");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.modCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);
        var eb = new EmbedBuilder();
        var myUser = new MyUser(event.getAuthor());
        var prefix = event.getGuild() == null ? myUser.getPrefix() : new MyGuild(event.getGuild()).getPrefix();
        eb.setColor(Color.decode(myUser.getColorCode()));
        var g = event.getJDA().getGuildById(436925371577925642L);
        eb.setThumbnail(g == null ? null : g.getIconUrl());
        eb.setAuthor(event.getSelfUser().getName() + " " + LanguageHandler.get(lang, "commands") + "\n", null, event.getSelfUser().getAvatarUrl());
        eb.setDescription(String.format(LanguageHandler.get(lang, "help_detailed"), prefix));

        var builder = new StringBuilder();
        Category category;
        Category previousCategory = null;

        for (var command : Servant.commands) {
            if (!command.isHidden() && (!command.isOwnerCommand() || event.isOwner())) {
                category = command.getCategory();

                if (!Objects.equals(category, previousCategory)) {
                    // New category
                    if (!builder.toString().isBlank()) {
                        eb.addField((previousCategory == null ? LanguageHandler.get(lang, "help_standard") : previousCategory.getName()), builder.toString(), false);
                        builder = new StringBuilder();
                    }
                }

                var userPrefix = event.getGuild() == null ?
                        new MyUser(event.getAuthor()).getPrefix() :
                        new MyGuild(event.getGuild()).getPrefix();

                builder.append("\n`").append(userPrefix).append(prefix == null ? " " : "").append(command.getName())
                        .append(command.getArguments() == null ? "`" : " " + command.getArguments() + "`")
                        .append(" - ").append(command.getHelp());
                previousCategory = category;
            }
        }

        if (!builder.toString().isBlank()) {
            eb.addField((previousCategory == null ? LanguageHandler.get(lang, "help_standard") : previousCategory.getName()), builder.toString(), false);
        }

        User owner = event.getJDA().getUserById(Servant.config.getBotOwnerId());
        if (owner != null)
            eb.setFooter(String.format(LanguageHandler.get(lang, "help_additional"), owner.getName(), owner.getDiscriminator(), Constants.SUPPORT), owner.getAvatarUrl());

        if (event.isFromType(ChannelType.TEXT)) {
            var botMember = event.getGuild().getMemberById(event.getJDA().getSelfUser().getIdLong());
            if (botMember != null && botMember.hasPermission(Permission.MESSAGE_ADD_REACTION)) event.reactSuccess();
        }

        event.replyInDm(eb.build(), unused -> {
        }, t -> event.replyWarning(LanguageHandler.get(new MyUser(event.getAuthor()).getLanguageCode(), "help_blocking_dm")));
    }
}
