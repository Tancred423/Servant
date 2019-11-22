// Author: Tancred423 (https://github.com/Tancred423)
package patreon;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class PatreonHandler {
    public static boolean isServerBooster(User user) {
        var g = user.getJDA().getGuildById(436925371577925642L);
        var m = user.getJDA().getGuildById(237575963439923200L);
        if (g == null) return false;

        var isServerBooster = false;

        // Servant's Kingdom Booster
        var members = g.getMembers();
        for (var member : members)
            if (member.getUser().equals(user))
                isServerBooster = member.getRoles().contains(g.getRoleById(639128857747652648L));

        // Mistlock Sanctuary Booster
        if (m != null) {
            members = m.getMembers();
            for (var member : members)
                if (member.getUser().equals(user))
                    isServerBooster = member.getRoles().contains(m.getRoleById(585536192691568681L));
        }

        return isServerBooster;
    }

    public static boolean isDonator(User user) {
        var g = user.getJDA().getGuildById(436925371577925642L);
        if (g == null) return false;

        var members = g.getMembers();
        for (var member : members)
            if (member.getUser().equals(user))
                return member.getRoles().contains(g.getRoleById(489738762838867969L));
        return false;
    }

    // Example: $10 Patron is also a $1 Patron!
    public static boolean is$1Patron(User user) {
        var g = user.getJDA().getGuildById(436925371577925642L);
        if (g == null) return false;

        if (is$3Patron(user)) return true;
        var members = g.getMembers();
        for (var member : members)
            if (member.getUser().equals(user))
                return member.getRoles().contains(g.getRoleById(502472440455233547L));
        return false;
    }

    public static boolean is$3Patron(User user) {
        var g = user.getJDA().getGuildById(436925371577925642L);
        if (g == null) return false;

        if (is$5Patron(user)) return true;
        var members = g.getMembers();
        for (var member : members)
            if (member.getUser().equals(user))
                return member.getRoles().contains(g.getRoleById(502472546600353796L));
        return false;
    }

    public static boolean is$5Patron(User user) {
        var g = user.getJDA().getGuildById(436925371577925642L);
        if (g == null) return false;

        if (is$10Patron(user)) return true;
        var members = g.getMembers();
        for (var member : members)
            if (member.getUser().equals(user))
                return member.getRoles().contains(g.getRoleById(502472823638458380L));
        return false;
    }

    public static boolean is$10Patron(User user) {
        var g = user.getJDA().getGuildById(436925371577925642L);
        if (g == null) return false;

        if (user.equals(g.getJDA().getSelfUser())) return true;
        if (isVIP(user)) return true;
        var members = g.getMembers();
        for (var member : members)
            if (member.getUser().equals(user))
                return member.getRoles().contains(g.getRoleById(502472869234868224L));
        return false;
    }

    public static boolean isVIP(User user) {
        var g = user.getJDA().getGuildById(436925371577925642L);
        if (g == null) return false;

        var members = g.getMembers();
        for (var member : members)
            if (member.getUser().equals(user))
                return member.getRoles().contains(g.getRoleById(510204269568458753L));
        return false;
    }

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
}
