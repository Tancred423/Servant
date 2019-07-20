package moderation.guild;

import com.jagrosh.jdautilities.command.GuildSettingsProvider;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;

public class GuildSettings implements GuildSettingsProvider {
    private Collection<String> prefixes = new ArrayList<>();

    public GuildSettings() {
        prefixes.add("+");
    }

    @Nullable
    @Override
    public Collection<String> getPrefixes() {
        return prefixes;
    }
}
