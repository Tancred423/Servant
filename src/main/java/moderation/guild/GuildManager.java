// Author: Tancred423 (https://github.com/Tancred423)
package moderation.guild;

import net.dv8tion.jda.api.entities.Guild;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.GuildSettingsManager;

import javax.annotation.Nullable;

public class GuildManager implements GuildSettingsManager<GuildSettings> {
    @Nullable
    @Override
    public GuildSettings getSettings(Guild guild) {
        return new GuildSettings(guild);
    }

    @Override
    public void init() {

    }

    @Override
    public void shutdown() {

    }
}
