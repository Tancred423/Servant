package freeToAll;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import servant.Log;
import utilities.MessageHandler;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LoveCommand extends Command {
    public LoveCommand() {
        this.name = "love";
        this.aliases = new String[]{"ship", "uwu"};
        this.help = "detects love percentage between two persons.";
        this.category = new Command.Category("Free to all");
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        // Enabled?
        try {
            if (!new servant.Guild(event.getGuild().getIdLong()).getToggleStatus("love")) return;
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }

        Message message = event.getMessage();
        List<Member> mentioned = message.getMentionedMembers();

        if (mentioned.size() < 1) {
            event.reactError();
            event.reply("You didn't mention enough people");
            return;
        }

        Member first = mentioned.get(0);
        Member second = mentioned.size() == 1 ? mentioned.get(0) : mentioned.get(1);

        User author = event.getAuthor();
        servant.User internalAuthor = new servant.User(author.getIdLong());

        int love = ThreadLocalRandom.current().nextInt(0, 101);
        String bar = getBar(love);
        String quote = getQuote(love);

        try {
            new MessageHandler().sendEmbed(event.getChannel(),
                    internalAuthor.getColor(),
                    quote,
                    null,
                    event.getJDA().getSelfUser().getAvatarUrl(),
                    null,
                    "https://i.imgur.com/BaeIVWa.png", // :tancLove:
                    first.getAsMention() + bar + second.getAsMention(),
                    null,
                    null,
                    null,
                    null
            );
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(true);
        }
    }

    private String getBar(int love) {
        if        (love >= 100) {
            return "▮▮▮▮▮" + love + "%▮▮▮▮▮";
        } else if (love >=  90) {
            return "▮▮▮▮▮" + love + "%▮▮▮▮▯";
        } else if (love >=  80) {
            return "▮▮▮▮▮" + love + "%▮▮▮▯▯";
        } else if (love >=  70) {
            return "▮▮▮▮▮" + love + "%▮▮▯▯▯";
        } else if (love >=  60) {
            return "▮▮▮▮▮" + love + "%▮▯▯▯▯";
        } else if (love >=  50) {
            return "▮▮▮▮▮" + love + "%▯▯▯▯▯";
        } else if (love >=  40) {
            return "▮▮▮▮▯" + love + "%▯▯▯▯▯";
        } else if (love >=  30) {
            return "▮▮▮▯▯" + love + "%▯▯▯▯▯";
        } else if (love >=  20) {
            return "▮▮▯▯▯" + love + "%▯▯▯▯▯";
        } else if (love >=  10) {
            return "▮▯▯▯▯" + love + "%▯▯▯▯▯";
        } else if (love >=   0) {
            return "▯▯▯▯▯" + love + "%▯▯▯▯▯";
        } else return "▯▯▯▯▯error▯▯▯▯▯";
    }

    private String getQuote(int love) {
        if        (love >= 100) {
            return "Damn! Thats a match!";
        } else if (love >=  90) {
            return "Get up and invite them for a dinner.";
        } else if (love >=  80) {
            return "You sure, you don't wanna date?";
        } else if (love >=  70) {
            return "I call a sis-/bromance.";
        } else if (love >=  60) {
            return "There is a chance.";
        } else if (love >=  50) {
            return "I bet you can be friends. :)";
        } else if (love >=  40) {
            return "At least you are trying.";
        } else if (love >=  30) {
            return "Try inviting them to the cinema. 11/10 chance this won't work out.";
        } else if (love >=  20) {
            return "At least a bit, amirite.";
        } else if (love >=  10) {
            return "Dats pretty low, tho.";
        } else if (love >=   0) {
            return "Well, that won't work out.";
        } else return "Urgh!";
    }
}
