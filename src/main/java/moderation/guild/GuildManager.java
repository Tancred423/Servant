package moderation.guild;

import com.jagrosh.jdautilities.command.GuildSettingsManager;
import net.dv8tion.jda.core.entities.Guild;

import javax.annotation.Nullable;

public class GuildManager implements GuildSettingsManager<GuildSettings> {
    @Nullable
    @Override
    public GuildSettings getSettings(Guild guild) {
        return new GuildSettings();
    }

    @Override
    public void init() {

    }

    @Override
    public void shutdown() {

    }
}
