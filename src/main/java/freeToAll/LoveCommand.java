package freeToAll;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import moderation.guild.Guild;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import servant.Log;
import utilities.MessageHandler;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LoveCommand extends Command {
    public LoveCommand() {
        this.name = "love";
        this.aliases = new String[]{"ship", "uwu"};
        this.help = "detects love percentage between two persons";
        this.category = new Command.Category("Free to all");
        this.arguments = "@user1 @user2";
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.cooldown = 5;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void execute(CommandEvent event) {
        // Enabled?
        try {
            if (!new Guild(event.getGuild().getIdLong()).getToggleStatus("love")) return;
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }

        var message = event.getMessage();
        List<Member> mentioned = message.getMentionedMembers();

        if (mentioned.size() < 1) {
            event.reactError();
            event.reply("You didn't mention enough people");
            return;
        }

        var first = mentioned.get(0);
        var second = mentioned.size() == 1 ? mentioned.get(0) : mentioned.get(1);

        var author = event.getAuthor();
        var internalAuthor = new servant.User(author.getIdLong());

        var love = ThreadLocalRandom.current().nextInt(0, 101);
        var bar = getBar(love);
        var quote = getQuote(love, mentioned.size() == 1);
        var shippingName = getShippingName(first, second);

        try {
            new MessageHandler().sendEmbed(event.getChannel(),
                    internalAuthor.getColor(),
                    quote,
                    null,
                    event.getJDA().getSelfUser().getAvatarUrl(),
                    null,
                    "https://i.imgur.com/BaeIVWa.png", // :tancLove:
                    first.getAsMention() + "♥" + second.getAsMention() + "\n" +
                            "\n" +
                            bar,
                    null,
                    null,
                    shippingName,
                    "https://i.imgur.com/JAKcV8F.png"
            );
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(true);
        }

        // Statistics.
        try {
            new servant.User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }
    }

    private String getBar(int love) {
        if        (love >= 100) {
            return "▮▮▮▮▮▮▮▮▮▮ " + love + "%";
        } else if (love >=  90) {
            return "▮▮▮▮▮▮▮▮▮▯ " + love + "%";
        } else if (love >=  80) {
            return "▮▮▮▮▮▮▮▮▯▯ " + love + "%";
        } else if (love >=  70) {
            return "▮▮▮▮▮▮▮▯▯▯ " + love + "%";
        } else if (love >=  60) {
            return "▮▮▮▮▮▮▯▯▯▯ " + love + "%";
        } else if (love >=  50) {
            return "▮▮▮▮▮▯▯▯▯▯ " + love + "%";
        } else if (love >=  40) {
            return "▮▮▮▮▯▯▯▯▯▯ " + love + "%";
        } else if (love >=  30) {
            return "▮▮▮▯▯▯▯▯▯▯ " + love + "%";
        } else if (love >=  20) {
            return "▮▮▯▯▯▯▯▯▯▯ " + love + "%";
        } else if (love >=  10) {
            return "▮▯▯▯▯▯▯▯▯▯ " + love + "%";
        } else if (love >=   0) {
            return "▯▯▯▯▯▯▯▯▯▯ " + love + "%";
        } else return "▯▯▯▯▯▯▯▯▯▯ error";
    }

    private String getQuote(int love, boolean isSelfLove) {
        if (isSelfLove) {
            if (love >= 100) {
                return "Damn! Straight to the fap!";
            } else if (love >= 90) {
                return "Pretty self confident, don't you think?";
            } else if (love >= 80) {
                return "So narcissistic...";
            } else if (love >= 70) {
                return "You love yourself more than others love you.";
            } else if (love >= 60) {
                return "Seems like you are accepting yourself.";
            } else if (love >= 50) {
                return "You seem to be undecided if you like yourself or not.";
            } else if (love >= 40) {
                return "Now, you can look into the mirror with pride.";
            } else if (love >= 30) {
                return "A bit unsecure, but I'm sure you can handle it.";
            } else if (love >= 20) {
                return "You are doing great. Build some self confidence!";
            } else if (love >= 10) {
                return "Believe in yourself!";
            } else if (love >= 0) {
                return "Thats tough. We still love you <3";
            } else return "Urgh!";
        } else {
            if (love >= 100) {
                return "Damn! Thats a match!";
            } else if (love >= 90) {
                return "Get up and invite them for a dinner.";
            } else if (love >= 80) {
                return "You sure, you don't wanna date?";
            } else if (love >= 70) {
                return "I call a sis-/bromance.";
            } else if (love >= 60) {
                return "There is a chance.";
            } else if (love >= 50) {
                return "I bet you can be friends. :)";
            } else if (love >= 40) {
                return "At least you are trying.";
            } else if (love >= 30) {
                return "Try inviting them to the cinema. 11/10 chance this won't work out.";
            } else if (love >= 20) {
                return "At least a bit, amirite.";
            } else if (love >= 10) {
                return "Dats pretty low, tho.";
            } else if (love >= 0) {
                return "Well, that won't work out.";
            } else return "Urgh!";
        }
    }

    private String getShippingName(Member first, Member second) {
        String firstName = first.getUser().getName();
        String secondName = second.getUser().getName();
        return firstName.substring(0, 1).toUpperCase() + firstName.substring(1, firstName.length() / 2).toLowerCase()
                + secondName.substring(secondName.length() / 2).toLowerCase();
    }
}
