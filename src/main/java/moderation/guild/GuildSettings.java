// Author: Tancred423 (https://github.com/Tancred423)
package moderation.guild;

import net.dv8tion.jda.core.entities.Guild;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.GuildSettingsProvider;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class GuildSettings implements GuildSettingsProvider {
    private Collection<String> prefixes = new ArrayList<>();

    GuildSettings(Guild guild) throws SQLException {
        var prefix = new moderation.guild.Guild(guild.getIdLong()).getPrefix();
        if (prefix != null) addPrefixes(prefix);
    }

    private void addPrefixes(String prefix) {
        this.prefixes.add(prefix);
    }

    @Nullable
    @Override
    public Collection<String> getPrefixes() {
        return prefixes;
    }
}
