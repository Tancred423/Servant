// Author: Tancred423 (https://github.com/Tancred423)
package commands.moderation.editembed;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import servant.MyGuild;

import java.util.TimeZone;

public class EmbedUser {
    private Message message;
    private MessageEmbed embed;
    private TimeZone timezone;
    private int fieldCounter;

    EmbedUser(Message message, MessageEmbed embed) {
        this.message = message;
        this.embed = embed;
        this.timezone = new MyGuild(message.getGuild()).getTimezone();
        this.fieldCounter = 0;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public MessageEmbed getEmbed() {
        return embed;
    }

    public void setEmbed(MessageEmbed embed) {
        this.embed = embed;
    }

    public TimeZone getTimezone() {
        return timezone;
    }

    int getFieldCounter() {
        return fieldCounter;
    }

    void setFieldCounter(int fieldCounter) {
        this.fieldCounter = fieldCounter;
    }
}