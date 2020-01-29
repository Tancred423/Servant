// Author: Tancred423 (https://github.com/Tancred423)
package patreon;

import files.language.LanguageHandler;
import moderation.guild.Server;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class PatreonHandler {
    public static void sendWarning(MessageChannel channel, String rank, String lang) {
        var message = LanguageHandler.get(lang, "patreon_warning");
        switch (rank) {
            case "donator":
                channel.sendMessage(String.format(message, "donator")).queue();
                break;
            case "$1":
                channel.sendMessage(String.format(message, "$1 Patron")).queue();
                break;
            case "$3":
                channel.sendMessage(String.format(message, "$3 Patron")).queue();
                break;
            case "$5":
                channel.sendMessage(String.format(message, "$5 Patron")).queue();
                break;
            case "$10":
                channel.sendMessage(String.format(message, "$10 Patron")).queue();
                break;
            default:
                channel.sendMessage(String.format(message, "supporter")).queue();
                break;
        }
    }

    public static void sendPatreonNotification(GuildMemberRoleAddEvent event, String rank) {
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

        Server internalGuild;
        internalGuild = new Server(event.getGuild());
        eb.setTimestamp(ZonedDateTime.now(ZoneId.of(internalGuild.getOffset())));

        var guild = event.getJDA().getGuildById(436925371577925642L);
        if (guild != null) {
            var tc = guild.getTextChannelById(502477863757545472L);
            if (tc != null) tc.sendMessage(eb.build()).queue();
        }
    }

    private static String getPatreonRoleMention(String rank, JDA jda) {
        var roleMention = "Supporter";
        Role role;
        var guild = jda.getGuildById(436925371577925642L);
        if (guild != null)
            switch (rank) {
                case "donation":
                    role = guild.getRoleById(489738762838867969L);
                    if (role != null) roleMention = role.getAsMention();
                    break;
                case "$1":
                    role = guild.getRoleById(502472440455233547L);
                    if (role != null) roleMention = role.getAsMention();
                    break;
                case "$3":
                    role = guild.getRoleById(502472546600353796L);
                    if (role != null) roleMention = role.getAsMention();
                    break;
                case "$5":
                    role = guild.getRoleById(502472823638458380L);
                    if (role != null) roleMention = role.getAsMention();
                    break;
                case "$10":
                    role = guild.getRoleById(502472869234868224L);
                    if (role != null) roleMention = role.getAsMention();
                    break;
            }

        return roleMention;
    }
}
