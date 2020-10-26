// Author: Tancred423 (https://github.com/Tancred423)
package commands.fun.profile;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import servant.MyGuild;
import servant.MyUser;
import utilities.Constants;
import utilities.EmoteUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class NewProfileCommand extends Command {
    public NewProfileCommand() {
        this.name = "newprofile";
        this.aliases = new String[] { "np" }; // TODO: change for production
        this.help = "Displays the profile of you or the mentioned user";
        this.category = new Category("Fun");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.modCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES
        };
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    protected void execute(CommandEvent event) {
        var jda = event.getJDA();

        var guild = event.getGuild();
        var guildId = guild.getIdLong();
        var myGuild = new MyGuild(guild);

        var user = event.getAuthor();
        var userId = user.getIdLong();
        var member = event.getMember();
        var profileUser = event.getMessage().getMentionedUsers().isEmpty() ? user : event.getMessage().getMentionedUsers().get(0);
        var profileMember = event.getMessage().getMentionedMembers().isEmpty() ? member : event.getMessage().getMentionedMembers().get(0);
        var profileMyUser = new MyUser(profileUser);

        var lang = LanguageHandler.getLanguage(event);
        var p = new MyGuild(event.getGuild()).getPrefix();

        var eb = new EmbedBuilder()
                .setColor(profileMyUser.getColor())
                .setThumbnail(profileUser.getEffectiveAvatarUrl())
                .setAuthor(getSupporterEmoji(profileMyUser) + profileMember.getEffectiveName())
                .setTitle("« " + getTitle(profileMyUser, lang) + " »")
                .setFooter(profileUser.equals(user) ?
                                String.format(LanguageHandler.get(lang, "profile_footer1"), p, name) :
                                String.format(LanguageHandler.get(lang, "profile_footer2"), p, name),
                        event.getSelfUser().getEffectiveAvatarUrl());

        // Level and rank
        var levelEmote = EmoteUtil.getEmote(jda, "level");
        var rankEmote = EmoteUtil.getEmote(jda, "rank");
        var description = new StringBuilder();
        description.append(levelEmote == null ? "" : levelEmote.getAsMention()).append(" Level ").append(profileMyUser.getLevel(guildId)).append("\n");
        description.append(rankEmote == null ? "" : rankEmote.getAsMention()).append(" Rank ").append(myGuild.getUserRank(userId));

        eb.setDescription(description);


        event.reply(eb.build());
    }

    private static String getSupporterEmoji(MyUser myUser) {
        if (myUser.isSupporter()) return "✨ "; // Sparkles
        else return " "; // Nothing
    }

    private static String getTitle(MyUser myUser, String lang) {
        if (myUser.isCreator()) return LanguageHandler.get(lang, "profile_title_creator");
        else if (myUser.isSupporter()) return LanguageHandler.get(lang, "profile_title_supporter");
        else return LanguageHandler.get(lang, "profile_title_normal");
    }
}
