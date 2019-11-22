// Author: Tancred423 (https://github.com/Tancred423)
package interaction;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

class InteractionEmbed {
    private MessageEmbed embed;

    InteractionEmbed(String commandName, Emote commandEmote, String commandEmoji, String commandGif, User author, User mentioned, int authorCount, int mentionedCount, Guild guild) {
        var internalUser = new moderation.user.User(author.getIdLong());
        var eb = new EmbedBuilder();
        eb.setColor(internalUser.getColor(guild, author));
        eb.setAuthor(commandName.substring(0, 1).toUpperCase() + commandName.substring(1), null, null);
        eb.setDescription("**" + author.getName() + "** " + (commandEmote == null ? commandEmoji : commandEmote.getAsMention()) + " **" + (commandName.toLowerCase().equals("dab") ? "on " : "") +  mentioned.getName() + "**\n\n" +
                author.getAsMention() + "'s shared " + commandName.toLowerCase() + " counter: `" + authorCount + "`\n" +
                mentioned.getAsMention() + "'s received " + commandName.toLowerCase() + " counter: `" + mentionedCount + "`");
        eb.setImage(commandGif);

        this.embed = eb.build();
    }

    MessageEmbed getEmbed() {
        return embed;
    }
}
