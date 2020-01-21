// Author: Tancred423 (https://github.com/Tancred423)
package useful.giveaway;

import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GiveawayListener extends ListenerAdapter {
    public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
        if (event.getGuild().getIdLong() == 264445053596991498L) return; // Discord Bot List
        var guild = event.getGuild();
        var owner = event.getGuild().getOwner();

        if (Giveaway.isGiveaway(guild.getIdLong(), event.getChannel().getIdLong(), event.getMessageIdLong(), guild, owner == null ? null : owner.getUser()))
            Giveaway.deleteGiveawayFromDb(event.getGuild().getIdLong(), event.getChannel().getIdLong(), event.getMessageIdLong(), event.getGuild(), owner == null ? null : owner.getUser());
    }

    public void onTextChannelDelete(TextChannelDeleteEvent event) {
        if (event.getGuild().getIdLong() == 264445053596991498L) return; // Discord Bot List
        var guild = event.getGuild();
        var owner = event.getGuild().getOwner();

        if (Giveaway.isGiveaway(guild.getIdLong(), event.getChannel().getIdLong(), guild, owner == null ? null : owner.getUser()))
            Giveaway.deleteGiveawayFromDb(event.getGuild().getIdLong(), event.getChannel().getIdLong(), event.getGuild(), owner == null ? null : owner.getUser());
    }

    public void onGuildLeave(GuildLeaveEvent event) {
        var guild = event.getGuild();
        var owner = event.getGuild().getOwner();

        if (Giveaway.isGiveaway(guild.getIdLong(), guild, owner == null ? null : owner.getUser()))
            Giveaway.deleteGiveawayFromDb(event.getGuild().getIdLong(), event.getGuild(), owner == null ? null : owner.getUser());
    }
}
