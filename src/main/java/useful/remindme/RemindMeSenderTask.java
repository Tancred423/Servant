package useful.remindme;

import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import utilities.ImageUtil;

public class RemindMeSenderTask implements Runnable {
    private JDA jda;
    private RemindMe remindMe;

    public RemindMeSenderTask(JDA jda, RemindMe remindMe) {
        this.jda = jda;
        this.remindMe = remindMe;
    }

    @Override
    public void run() {
        var userToDm = jda.getUserById(remindMe.getUserId());

        if (userToDm != null) {
            var master = new Master(userToDm);
            userToDm.openPrivateChannel().queue(channel -> channel.sendMessage(new EmbedBuilder()
                    .setColor(master.getColor())
                    .setAuthor("Requested RemindMe", null, ImageUtil.getImageUrl("clock", null, null))
                    .setDescription(remindMe.getTopic())
                    .build()
            ).queue(message -> {
                master.unsetRemindMe(remindMe.getAiNumber());
            }, failure -> {}), failure -> {});
        }
    }
}
