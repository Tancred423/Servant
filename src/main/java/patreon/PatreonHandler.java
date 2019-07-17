package patreon;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import servant.Servant;

public class PatreonHandler {
    private static Guild g = Servant.jda.getGuildById(436925371577925642L);

    public static boolean isDonator(User user) {
        if (is$1Patron(user)) return true;
        return g.getMemberById(user.getIdLong()).getRoles().contains(g.getRoleById(489738762838867969L));
    }

    // Example: $10 Patron is also a $1 Patron!
    public static boolean is$1Patron(User user) {
        if (is$3Patron(user)) return true;
        return g.getMemberById(user.getIdLong()).getRoles().contains(g.getRoleById(502472440455233547L));
    }

    public static boolean is$3Patron(User user) {
        if (is$5Patron(user)) return true;
        return g.getMemberById(user.getIdLong()).getRoles().contains(g.getRoleById(502472546600353796L));
    }

    public static boolean is$5Patron(User user) {
        if (is$10Patron(user)) return true;
        return g.getMemberById(user.getIdLong()).getRoles().contains(g.getRoleById(502472823638458380L));
    }

    public static boolean is$10Patron(User user) {
        return g.getMemberById(user.getIdLong()).getRoles().contains(g.getRoleById(502472869234868224L));
    }

    public static void sendWarning(MessageChannel channel, String rank) {
        String message = "You have to be a %s to use this feature!";
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
