package commands.standard.help;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import servant.MyGuild;
import servant.MyUser;
import utilities.Constants;
import utilities.EmoteUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.awt.*;

public class HelpCommand extends Command {
    public HelpCommand() {
        this.name = "help";
        this.aliases = new String[]{"h"};
        this.help = "Help message";
        this.category = new Category("Standard");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.modCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.botPermissions = new Permission[]{
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var jda = event.getJDA();
        var lang = LanguageHandler.getLanguage(event);
        var eb = new EmbedBuilder();
        var myUser = new MyUser(event.getAuthor());
        var prefix = event.getGuild() == null ? myUser.getPrefix() : new MyGuild(event.getGuild()).getPrefix();
        eb.setColor(Color.decode(myUser.getColorCode()));
        var g = event.getJDA().getGuildById(436925371577925642L);
        eb.setThumbnail(g == null ? null : g.getIconUrl());
        eb.setAuthor(event.getSelfUser().getName() + " " + LanguageHandler.get(lang, "help") + "\n", null, event.getSelfUser().getAvatarUrl());

        eb.addField(
                LanguageHandler.get(lang, "help_getting_started"),
                String.format(LanguageHandler.get(lang, "help_getting_started_content"), EmoteUtil.getEmote(jda, "love").getAsMention(), Constants.WEBSITE_FAQ_GETTING_STARTED),
                false
        );

        eb.addField(
                LanguageHandler.get(lang, "commands"),
                String.format(LanguageHandler.get(lang, "help_commands_content"), Constants.WEBSITE_HELP, prefix),
                false
        );

        eb.addField(
                LanguageHandler.get(lang, "help_plugins_dashboard"),
                String.format(LanguageHandler.get(lang, "help_plugins_dashboard_content"), Constants.WEBSITE_DASHBOARD),
                false
        );

        eb.addField(
                LanguageHandler.get(lang, "help_faq"),
                String.format(LanguageHandler.get(lang, "help_faq_content"), Constants.WEBSITE_FAQ, Constants.SUPPORT),
                false
        );

//        if (event.isFromType(ChannelType.TEXT)) {
//            var botMember = event.getGuild().getMemberById(event.getJDA().getSelfUser().getIdLong());
//            if (botMember != null && botMember.hasPermission(Permission.MESSAGE_ADD_REACTION)) event.reactSuccess();
//        }

        event.reply(eb.build());
    }
}
