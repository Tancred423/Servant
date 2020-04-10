// Author: Tancred423 (https://github.com/Tancred423)
package utilities;

import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MessageUtil {
    public void sendAndExpire(MessageChannel channel, Message message, long cooldown) {
        channel.sendMessage(message).queue(sentMessage -> new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (sentMessage != null) sentMessage.delete().queue(s -> {}, f -> {});
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
        var emote = EmoteUtil.getEmoji("achievement");
        if (emote != null) message.addReaction(emote).queue(s -> {}, f -> {});
        else {
            var emoji = EmoteUtil.getEmoji("achievement");
            if (emoji != null) message.addReaction(emoji).queue();
        }
    }

    public static MessageEmbed createUsageEmbed(String commandName, User author, String description, boolean ownerCommand, Permission[] permissions, String[] aliases, String usage, String hint) {
        var master = new Master(author);

        var sb = new StringBuilder();
        for (var perm : permissions) sb.append(perm.getName()).append("\n");
        var permission = sb.toString();

        sb = new StringBuilder();
        for (var alias : aliases) sb.append(alias).append("\n");
        var alias = sb.toString();

        return new EmbedBuilder()
                .setColor(master.getColor())
                .setAuthor(commandName.substring(0, 1).toUpperCase() + commandName.substring(1) + " Usage", null, null)
                .setDescription((description == null ? "No description available." : description))
                .addField(permissions.length > 1 ? "Permissions" : "Permission", ownerCommand ? "Bot Owner" : permission.isEmpty() ? "None" : permission, true)
                .addField(aliases.length > 1 ? "Aliases" : "Alias", alias.isEmpty() ? "No aliases available" : alias, true)
                .addField("Usage", usage == null ? "No usage available" : usage, false)
                .addField("Hint", hint == null ? "No hint available" : hint, false)
                .build();
    }
}
