// Author: Tancred423 (https://github.com/Tancred423)
package utilities;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class UsageEmbed {
    private MessageEmbed embed;

    public UsageEmbed(String commandName, User author, String description, boolean ownerCommand, Permission[] permissions, String[] aliases, String usage, String hint) {
        var internalUser = new moderation.user.User(author.getIdLong());

        var stringBuilder = new StringBuilder();
        for (var perm : permissions) stringBuilder.append(perm.getName()).append("\n");
        var permission = stringBuilder.toString();

        stringBuilder = new StringBuilder();
        for (var al : aliases) stringBuilder.append(al).append("\n");
        var alias = stringBuilder.toString();

        var eb = new EmbedBuilder();
        eb.setColor(internalUser.getColor(null, author));
        eb.setAuthor(commandName.substring(0, 1).toUpperCase() + commandName.substring(1) + " Usage", null, null);
        eb.setDescription((description == null ? "No description available." : description));
        eb.addField(permissions.length > 1 ? "Permissions" : "Permission", ownerCommand ? "Bot Owner" : permission.isEmpty() ? "None" : permission, true);
        eb.addField(aliases.length > 1 ? "Aliases" : "Alias", alias.isEmpty() ? "No aliases available" : alias, true);
        eb.addField("Usage", usage == null ? "No usage available" : usage, false);
        eb.addField("Hint", hint == null ? "No hint available" : hint, false);

        this.embed = eb.build();
    }

    public MessageEmbed getEmbed() {
        return embed;
    }
}
