// Author: Tancred423 (https://github.com/Tancred423)
package patreon;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import servant.Servant;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;

public class PatreonListener extends ListenerAdapter {
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
        CompletableFuture.runAsync(() -> {
            if (event.getMember().getUser().isBot()) return;
            if (event.getGuild().getId().equals(Servant.config.getSupportGuildId())) return;

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
        });
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
        internalGuild = new moderation.guild.Guild(event.getGuild().getIdLong());
        eb.setTimestamp(ZonedDateTime.now(ZoneId.of(internalGuild.getOffset(event.getGuild(), event.getUser()))));

        var guild = event.getJDA().getGuildById(436925371577925642L);
        if (guild != null) {
            var tc = guild.getTextChannelById(502477863757545472L);
            if (tc != null) tc.sendMessage(eb.build()).queue();
        }
    }

    private String getPatreonRoleMention(String rank, JDA jda) {
        var roleMention = "Supporter";
        Role role;
        var guild = jda.getGuildById(436925371577925642L);
        if (guild != null)
            switch (rank) {
                case "donation":
                    role = guild.getRoleById(489738762838867969L);
                    if (role != null) roleMention = role.getAsMention();
                case "$1":
                    role = guild.getRoleById(502472440455233547L);
                    if (role != null) roleMention = role.getAsMention();
                case "$3":
                    role = guild.getRoleById(502472546600353796L);
                    if (role != null) roleMention = role.getAsMention();
                case "$5":
                    role = guild.getRoleById(502472823638458380L);
                    if (role != null) roleMention = role.getAsMention();
                case "$10":
                    role = guild.getRoleById(502472869234868224L);
                    if (role != null) roleMention = role.getAsMention();
            }

        return roleMention;
    }
}
