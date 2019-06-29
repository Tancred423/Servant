package utilities;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

import java.sql.SQLException;

public class UsageEmbed {
    private MessageEmbed embed;

    public UsageEmbed(String commandName, User author, boolean ownerCommand, Permission[] permissions, String[] aliases, String usage, String hint) throws SQLException {
        servant.User internalUser = new servant.User(author.getIdLong());

        StringBuilder stringBuilder = new StringBuilder();
        for (Permission perm : permissions) stringBuilder.append(perm.getName()).append("\n");
        String permission = stringBuilder.toString();

        stringBuilder = new StringBuilder();
        for (String al : aliases) stringBuilder.append(al).append("\n");
        String alias = stringBuilder.toString();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(internalUser.getColor());
        eb.setAuthor(commandName.substring(0, 1).toUpperCase() + commandName.substring(1) + " Usage", null, null);
        eb.addField(permissions.length > 1 ? "Permissions" : "Permission", ownerCommand ? "Bot Owner" : permission.isEmpty() ? "None" : permission, false);
        eb.addField(aliases.length > 1 ? "Aliases" : "Alias", alias.isEmpty() ? "No aliases available" : alias, false);
        eb.addField("Usage", usage == null ? "No usage available" : usage, false);
        eb.addField("Hint", hint == null ? "No hint available" : hint, false);

        this.embed = eb.build();
    }

    public MessageEmbed getEmbed() {
        return embed;
    }
}
