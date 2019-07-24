package moderation.guild;

import com.jagrosh.jdautilities.command.GuildSettingsProvider;
import net.dv8tion.jda.core.entities.Guild;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class GuildSettings implements GuildSettingsProvider {
    private Collection<String> prefixes = new ArrayList<>();

    GuildSettings(Guild guild) throws SQLException {
        String prefix = new servant.Guild(guild.getIdLong()).getPrefix();
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
