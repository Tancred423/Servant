// Author: Tancred423 (https://github.com/Tancred423)
package servant.guild;

import net.dv8tion.jda.api.entities.Guild;
import servant.MyGuild;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.GuildSettingsProvider;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;

public class GuildSettings implements GuildSettingsProvider {
    private final Collection<String> prefixes = new ArrayList<>();

    GuildSettings(Guild guild) {
        var prefix = new MyGuild(guild).getPrefix();
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
