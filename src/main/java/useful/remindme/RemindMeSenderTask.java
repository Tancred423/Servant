package useful.remindme;

import files.language.LanguageHandler;
import moderation.guild.Server;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import utilities.EmoteUtil;
import utilities.ImageUtil;

public class RemindMeSenderTask implements Runnable {
    private JDA jda;
    private RemindMe remindMe;
    private String lang;

    public RemindMeSenderTask(JDA jda, RemindMe remindMe, String lang) {
        this.jda = jda;
        this.remindMe = remindMe;
        this.lang = lang;
    }

    @Override
    public void run() {
        var guild = jda.getGuildById(remindMe.getGuildId());

        if (guild != null) {
            var server = new Server(guild);
            var channel = guild.getTextChannelById(remindMe.getChannelId());

            if (channel != null) {
                channel.retrieveMessageById(remindMe.getMessageId()).queue(botMessage -> {
                    var author = jda.getUserById(remindMe.getUserId());
                    editBotMessage(jda, author, botMessage, remindMe);
                    sendDm(author, botMessage);

                    var reactions = botMessage.getReactions();
                    for (var reaction : reactions) {
                        var reactionEmote = reaction.getReactionEmote();
                        var upvote = EmoteUtil.getEmoji("upvote");
                        if (reactionEmote.isEmoji() && upvote != null && reactionEmote.getName().equals(upvote)) {
                           reaction.retrieveUsers().queue(usersToDm -> {
                               for (var userToDm : usersToDm)
                                   if (userToDm != author && userToDm != jda.getSelfUser())
                                       sendDm(userToDm, botMessage);
                               botMessage.clearReactions().queue();
                           });
                           break;
                        }
                    }
                }, failure -> {});
            }

            server.unsetRemindMe(remindMe.getAiNumber());
        }
    }

    private void editBotMessage(JDA jda, User user, Message botMessage, RemindMe remindMe) {
        var master = new Master(user);
        botMessage.editMessage(new EmbedBuilder()
                .setColor(master.getColor())
                .setAuthor(String.format(LanguageHandler.get(lang, "remindme_of"), user.getName()), null, user.getEffectiveAvatarUrl())
                .setDescription((remindMe.getTopic().isEmpty() ? "" : String.format(LanguageHandler.get(lang, "remindme_topic"), remindMe.getTopic())) + "\n" +
                       String.format(LanguageHandler.get(lang, "remindme_success"), EmoteUtil.getEmoteMention(jda, "pinged")))
                .build()
        ).queue();
    }

    private void sendDm(User user, Message botMessage) {
        var master = new Master(user);
        user.openPrivateChannel().queue(
                channel -> channel.sendMessage(new EmbedBuilder()
                        .setColor(master.getColor())
                        .setAuthor(LanguageHandler.get(lang, "remindme_remind"), null, ImageUtil.getImageUrl(jda, "clock"))
                        .setDescription((remindMe.getTopic().isEmpty() ? "" : String.format(LanguageHandler.get(lang, "remindme_topic"), remindMe.getTopic()) + "\n") +
                                "[" + LanguageHandler.get(lang, "remindme_jump") + "](" + botMessage.getJumpUrl() + ")")
                        .build()).queue(),
                failure -> {});
    }
}
