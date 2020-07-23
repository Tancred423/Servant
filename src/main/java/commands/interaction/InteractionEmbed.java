// Author: Tancred423 (https://github.com/Tancred423)
package commands.interaction;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import servant.MyUser;
import utilities.ConsoleLog;
import utilities.EmoteUtil;

import java.awt.*;

class InteractionEmbed {
    private final MessageEmbed embed;

    InteractionEmbed(JDA jda, String name, String emoji, String gif, User author, User mentioned, int authorCount, int mentionedCount, String shared, String received, String lang) {
        var emoteMention = "";
        if (name.equals("highfive")) {
            var highfiveL = EmoteUtil.getEmote(jda, "highfiveL");
            var highfiveR = EmoteUtil.getEmote(jda, "highfiveR");
            if (highfiveL != null && highfiveR != null) emoteMention = highfiveL.getAsMention() + highfiveR.getAsMention();
            else emoteMention = emoji;
        } else {
            var emote = EmoteUtil.getEmote(jda, name);
            if (emote != null) emoteMention = emote.getAsMention();
            else emoteMention = emoji;
        }
        var myUser = new MyUser(author);
        var eb = new EmbedBuilder();
        eb.setColor(Color.decode(myUser.getColorCode()));
        eb.setAuthor(name.substring(0, 1).toUpperCase() + name.substring(1), null, null);
        eb.setDescription("**" + author.getName() + "** " + emoteMention + " **" + mentioned.getName() + "**\n\n" +
                String.format(shared, author.getAsMention(), authorCount) + "\n" +
                String.format(received, mentioned.getAsMention(), mentionedCount));
        System.out.println(ConsoleLog.getTimestamp() + name + " GIF URL: " + gif);
        eb.setImage(gif);
        if (name.equalsIgnoreCase("birthday")) {
            eb.setFooter(LanguageHandler.get(lang, "dashboard_birthday_settings"));
        }

        this.embed = eb.build();
    }

    MessageEmbed getEmbed() {
        return embed;
    }
}
