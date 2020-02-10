// Author: Tancred423 (https://github.com/Tancred423)
package useful.polls;

import files.language.LanguageHandler;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import utilities.Constants;
import utilities.EmoteUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class QuickpollCommand extends Command {
    public QuickpollCommand() {
        this.name = "quickpoll";
        this.aliases = new String[] { "quickvote" };
        this.help = "Smol poll with yes/no.";
        this.category = new Category("Useful");
        this.arguments = "[optional text]";
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
        var guild = event.getGuild();
        var user = event.getAuthor();
        var master = new Master(user);
        var message = event.getMessage();
        var lang = LanguageHandler.getLanguage(event);

        var eb = new EmbedBuilder();
        eb.setColor(master.getColor());
        eb.setAuthor(String.format(LanguageHandler.get(lang, "quickvote_started"), user.getName()), null, user.getEffectiveAvatarUrl());
        eb.setDescription("**" + event.getArgs() + "**");
        eb.appendDescription("\n\n" + String.format(LanguageHandler.get(lang, "votes_end_manually"), user.getAsMention()));
        eb.setFooter(LanguageHandler.get(lang, "votes_active"), event.getJDA().getSelfUser().getAvatarUrl());
        var dateIn7Days = OffsetDateTime.now(ZoneOffset.UTC).plusDays(7).toLocalDateTime();
        eb.setTimestamp(dateIn7Days);

        var upvote = EmoteUtil.getEmoji("upvote");
        var downvote = EmoteUtil.getEmoji("downvote");
        var end = EmoteUtil.getEmoji("end");

        if (upvote == null || downvote == null || end == null) return;

        message.getChannel().sendMessage(eb.build()).queue(sentMessage -> {
            sentMessage.addReaction(upvote).queue();
            sentMessage.addReaction(downvote).queue();
            sentMessage.addReaction(end).queue();
            new Poll(event.getJDA(), lang, sentMessage).set(guild.getIdLong(), sentMessage.getChannel().getIdLong(), user.getIdLong(), "quick", Timestamp.valueOf(dateIn7Days));
        });

        message.delete().queue();
    }
}
