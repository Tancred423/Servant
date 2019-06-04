package utilities;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.Timer;
import java.util.TimerTask;

public class MessageHandler {
    public void sendAndExpire(MessageChannel channel, Message message, long cooldown) {
        channel.sendMessage(message).queue(sentMessage -> {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    sentMessage.delete().queue();
                }
            }, cooldown);
        });
    }
}
