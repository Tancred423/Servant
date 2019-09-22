// Author: Tancred423 (https://github.com/Tancred423)
package utilities;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.awt.*;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MessageHandler {
    public void sendAndExpire(MessageChannel channel, Message message, long cooldown) {
        channel.sendMessage(message).queue(sentMessage -> new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                sentMessage.delete().queue();
            }
        }, cooldown));
    }

    public void sendEmbed(MessageChannel channel, Color color, String authorName, String authorUrl, String authorIconUrl, String title, String thumbUrl, String description, Map<String, Map.Entry<String, Boolean>> fields, String imageUrl, String footerText, String footerIconUrl) {
        var eb  = new EmbedBuilder();
        if (color       != null) eb.setColor(color);
        if (authorName  != null) eb.setAuthor(authorName, authorUrl, authorIconUrl);
        if (title       != null) eb.setTitle(title);
        if (thumbUrl    != null) eb.setThumbnail(thumbUrl);
        if (description != null) eb.setDescription(description);

        if (fields      != null)
            for (Map.Entry<String, Map.Entry<String, Boolean>> entry : fields.entrySet())
                if (entry.getKey() != null)
                    eb.addField(entry.getKey(), entry.getValue().getKey(), entry.getValue().getValue());

        if (imageUrl    != null) eb.setImage(imageUrl);
        if (footerText  != null) eb.setFooter(footerText, footerIconUrl);

        channel.sendMessage(eb.build()).queue();
    }

    public void reactAchievement(Message message) {
        var emote = Emote.getEmoji("achievement");
        if (emote != null) message.addReaction(emote).queue();
        else message.addReaction(Emote.getEmoji("achievement")).queue();
    }
}
