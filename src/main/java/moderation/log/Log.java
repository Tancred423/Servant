// Author: Tancred423 (https://github.com/Tancred423)
package moderation.log;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import utilities.ImageUtil;

import java.awt.*;
import java.time.OffsetDateTime;

public class Log {
    public static MessageEmbed getLogEmbed(JDA jda, Color color, String title, String description) {
        return new EmbedBuilder()
                .setColor(color)
                .setTitle(title)
                .setDescription(description)
                .setFooter("Logged at", ImageUtil.getImageUrl(jda, "clock"))
                .setTimestamp(OffsetDateTime.now())
                .build();
    }
}
