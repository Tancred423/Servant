package freeToAll.embed;

import moderation.guild.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.sql.SQLException;

public class EmbedUser {
    private Message message;
    private MessageEmbed embed;
    private String offset;
    private int fieldCounter;

    public EmbedUser(Message message, MessageEmbed embed) throws SQLException {
        this.message = message;
        this.embed = embed;
        this.offset = new Guild(message.getGuild().getIdLong()).getOffset();
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

    public String getOffset() {
        return offset;
    }

    public int getFieldCounter() {
        return fieldCounter;
    }

    public void setFieldCounter(int fieldCounter) {
        this.fieldCounter = fieldCounter;
    }
}
