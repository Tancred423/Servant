// Author: Tancred423 (https://github.com/Tancred423)
package patreon;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class PatreonListener extends ListenerAdapter {
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        if (event.getMember().getUser().isBot()) return;
        if (event.getGuild().getIdLong() != 436925371577925642L) return;

        switch (event.getRoles().get(0).getId()) {
            case "489738762838867969": // Donation
                sendPatreonNotification(event, "donation");
                break;
            case "502472440455233547": // $1
                sendPatreonNotification(event, "$1");
                break;
            case "502472546600353796": // $3
                sendPatreonNotification(event, "$3");
                break;
            case "502472823638458380": // $5
                sendPatreonNotification(event, "$5");
                break;
            case "502472869234868224": // $10
                sendPatreonNotification(event, "$10");
                break;
        }
    }

    private void sendPatreonNotification(GuildMemberRoleAddEvent event, String rank) {
        var eb = new EmbedBuilder();
        var patron = event.getMember().getUser();

        eb.setColor(event.getRoles().get(0).getColor());
        eb.setAuthor(patron.getName() + "#" + patron.getDiscriminator(), null, patron.getAvatarUrl());
        if (rank.equals("donation")) {
            eb.setDescription(patron.getAsMention() + " just donated some money via PayPal.\n" +
                    "They are now a " + getPatreonRoleMention(rank, event.getJDA()));
            eb.setThumbnail("https://i.imgur.com/xlhkWMv.png"); // PayPal Logo
        } else {
            eb.setDescription(patron.getAsMention() + " just pledged " + rank + " via Patreon.\n" +
                    "They are now a " + getPatreonRoleMention(rank, event.getJDA()));
            eb.setThumbnail("https://i.imgur.com/rCnhGKA.jpg"); // Patreon Logo
        }

        moderation.guild.Guild internalGuild;
        try {
            internalGuild = new moderation.guild.Guild(event.getGuild().getIdLong());
            eb.setTimestamp(ZonedDateTime.now(ZoneId.of(internalGuild.getOffset())));
        } catch (SQLException ignored) { }

        event.getJDA().getGuildById(436925371577925642L).getTextChannelById(502477863757545472L).sendMessage(eb.build()).queue();
    }

    private String getPatreonRoleMention(String rank, JDA jda) {
        var guild = jda.getGuildById(436925371577925642L);
        switch (rank) {
            case "donation":
                return guild.getRoleById(489738762838867969L).getAsMention();
            case "$1":
                return guild.getRoleById(502472440455233547L).getAsMention();
            case "$3":
                return guild.getRoleById(502472546600353796L).getAsMention();
            case "$5":
                return guild.getRoleById(502472823638458380L).getAsMention();
            case "$10":
                return guild.getRoleById(502472869234868224L).getAsMention();
            default:
                return "Supporter";
        }
    }
}
