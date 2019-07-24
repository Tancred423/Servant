package moderation.guild;

import com.jagrosh.jdautilities.command.GuildSettingsManager;
import net.dv8tion.jda.core.entities.Guild;

import javax.annotation.Nullable;
import java.sql.SQLException;

public class GuildManager implements GuildSettingsManager<GuildSettings> {
    @Nullable
    @Override
    public GuildSettings getSettings(Guild guild) {
        try {
            return new GuildSettings(guild);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void shutdown() {

    }
}
