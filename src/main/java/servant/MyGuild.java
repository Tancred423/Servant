// Author: Tancred423 (https://github.com/Tancred423)
package servant;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import utilities.Console;
import utilities.ToggleUtil;

import java.sql.*;
import java.util.*;

import static servant.Database.closeQuietly;

public class MyGuild {
    private final Guild guild;
    private final long guildId;
    private final JDA jda;

    public MyGuild(Guild guild) {
        this.guild = guild;
        this.guildId = guild.getIdLong();
        this.jda = guild.getJDA();
    }

    public Guild getGuild() { return guild; }
    public long getGuildId() { return guildId; }

    // Purge
    public void purge() {
      Console.log("Purging guild... Guild ID: " + guildId);
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();

            // Giveaway
            var delete = connection.prepareStatement("DELETE FROM giveaways WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            delete = connection.prepareStatement("DELETE FROM tmp_giveaway_participants WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            // AutoRole
            delete = connection.prepareStatement("DELETE FROM guild_autoroles WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            // Best of Image
            delete = connection.prepareStatement("DELETE FROM guild_best_of_images WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            delete = connection.prepareStatement("DELETE FROM tmp_best_of_image_bl WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            // Best of Quote
            delete = connection.prepareStatement("DELETE FROM guild_best_of_quotes WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            delete = connection.prepareStatement("DELETE FROM tmp_best_of_quote_bl WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            // Birthday
            delete = connection.prepareStatement("DELETE FROM guild_birthdays WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            delete = connection.prepareStatement("DELETE FROM tmp_birthday_gratulated WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            // Command Counts
            delete = connection.prepareStatement("DELETE FROM guild_command_counts WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            // Disabled Stuff
            delete = connection.prepareStatement("DELETE FROM guild_disabled_categories WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            delete = connection.prepareStatement("DELETE FROM guild_disabled_commands WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            delete = connection.prepareStatement("DELETE FROM guild_disabled_features WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            delete = connection.prepareStatement("DELETE FROM guild_disabled_plugins WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            // Join
            delete = connection.prepareStatement("DELETE FROM guild_joins WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            // Leave
            delete = connection.prepareStatement("DELETE FROM guild_leaves WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            // Level Settings
            delete = connection.prepareStatement("DELETE FROM guild_level WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            // Level Roles
            delete = connection.prepareStatement("DELETE FROM guild_level_roles WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            // Livestream
            delete = connection.prepareStatement("DELETE FROM guild_livestreamers WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            delete = connection.prepareStatement("DELETE FROM guild_livestreams WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            // Log Settings
            delete = connection.prepareStatement("DELETE FROM guild_logs WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            // Media Only Channel
            delete = connection.prepareStatement("DELETE FROM guild_media_only_channels WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            // Mods
            delete = connection.prepareStatement("DELETE FROM guild_mod_roles WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            // Voice Lobby
            delete = connection.prepareStatement("DELETE FROM guild_voice_lobbies WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            delete = connection.prepareStatement("DELETE FROM tmp_voice_lobbies_active WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            // Polls
            delete = connection.prepareStatement("DELETE FROM polls WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            delete = connection.prepareStatement("DELETE FROM tmp_poll_participants WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            // Ratings
            delete = connection.prepareStatement("DELETE FROM ratings WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            delete = connection.prepareStatement("DELETE FROM tmp_rating_participants WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            // Reaction Roles
            delete = connection.prepareStatement("DELETE FROM reaction_roles WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            delete = connection.prepareStatement("DELETE FROM reaction_role_fields WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            delete = connection.prepareStatement("DELETE FROM reaction_role_messages WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            // Remind Me
            delete = connection.prepareStatement("DELETE FROM remind_mes WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            // Signup
            delete = connection.prepareStatement("DELETE FROM signups WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            delete = connection.prepareStatement("DELETE FROM tmp_signup_participants WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();

            // Guild
            delete = connection.prepareStatement("DELETE FROM guilds WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#purge"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void purgeTc(long tcId) {
        Console.log("Purging text channel... Guild ID: " + guildId + " Text Channel ID: " + tcId);
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();

            // Giveaway
            var delete = connection.prepareStatement("DELETE FROM giveaways WHERE guild_id=? AND tc_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.setLong(2, tcId);
            delete.executeUpdate();

            delete = connection.prepareStatement("DELETE FROM tmp_giveaway_participants WHERE guild_id=? AND tc_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.setLong(2, tcId);
            delete.executeUpdate();

            // Best of Image
            delete = connection.prepareStatement("DELETE FROM tmp_best_of_image_bl WHERE guild_id=? AND tc_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.setLong(2, tcId);
            delete.executeUpdate();

            // Best of Quote
            delete = connection.prepareStatement("DELETE FROM tmp_best_of_quote_bl WHERE guild_id=? AND tc_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.setLong(2, tcId);
            delete.executeUpdate();

            // Birthday
            var myTC = new MyTextChannel(jda, guildId, tcId);
            if (myTC.containsBirthdayList()) {
                delete = connection.prepareStatement("UPDATE guild_birthdays SET list_tc_id=?, list_msg_id=?, list_author_id=? WHERE guild_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                delete.setLong(1, 0L);
                delete.setLong(2, 0L);
                delete.setLong(3, 0L);
                delete.setLong(4, guildId);
                delete.executeUpdate();
            }

            if (myTC.isBirthdayAnnouncementTc()) {
                delete = connection.prepareStatement("UPDATE guild_birthdays SET announcement_tc_id=? WHERE guild_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                delete.setLong(1, 0L);
                delete.setLong(2, guildId);
                delete.executeUpdate();
            }

            // Join
            delete = connection.prepareStatement("DELETE FROM guild_joins WHERE guild_id=? AND tc_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.setLong(2, tcId);
            delete.executeUpdate();

            // Leave
            delete = connection.prepareStatement("DELETE FROM guild_leaves WHERE guild_id=? AND tc_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.setLong(2, tcId);
            delete.executeUpdate();

            // Log Settings
            delete = connection.prepareStatement("DELETE FROM guild_logs WHERE guild_id=? AND tc_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.setLong(2, tcId);
            delete.executeUpdate();

            // Media Only Channel
            delete = connection.prepareStatement("DELETE FROM guild_media_only_channels WHERE guild_id=? AND tc_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.setLong(2, tcId);
            delete.executeUpdate();

            // Polls
            delete = connection.prepareStatement("DELETE FROM polls WHERE guild_id=? AND tc_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.setLong(2, tcId);
            delete.executeUpdate();

            delete = connection.prepareStatement("DELETE FROM tmp_poll_participants WHERE guild_id=? AND tc_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.setLong(2, tcId);
            delete.executeUpdate();

            // Ratings
            delete = connection.prepareStatement("DELETE FROM ratings WHERE guild_id=? AND tc_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.setLong(2, tcId);
            delete.executeUpdate();

            delete = connection.prepareStatement("DELETE FROM tmp_rating_participants WHERE guild_id=? AND tc_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.setLong(2, tcId);
            delete.executeUpdate();

            // Reaction Roles
            delete = connection.prepareStatement("DELETE FROM reaction_roles WHERE guild_id=? AND tc_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.setLong(2, tcId);
            delete.executeUpdate();

            delete = connection.prepareStatement("DELETE FROM reaction_role_fields WHERE guild_id=? AND tc_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.setLong(2, tcId);
            delete.executeUpdate();

            delete = connection.prepareStatement("DELETE FROM reaction_role_messages WHERE guild_id=? AND tc_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.setLong(2, tcId);
            delete.executeUpdate();

            // Remind Me
            delete = connection.prepareStatement("DELETE FROM remind_mes WHERE guild_id=? AND tc_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.setLong(2, tcId);
            delete.executeUpdate();

            // Signup
            delete = connection.prepareStatement("DELETE FROM signups WHERE guild_id=? AND tc_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.setLong(2, tcId);
            delete.executeUpdate();

            delete = connection.prepareStatement("DELETE FROM tmp_signup_participants WHERE guild_id=? AND tc_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.setLong(2, tcId);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#purgeTc"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void purgeVc(long vcTc) {
        Console.log("Purging voice channel... Guild ID: " + guildId + " Voice Channel ID: " + vcTc);
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();

            // Voice Lobby
            var delete = connection.prepareStatement("DELETE FROM guild_voice_lobbies WHERE guild_id=? AND vc_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            delete.setLong(1, guildId);
            delete.setLong(2, vcTc);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#purgeVc"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void purgeMsg(long tcId, long msgId) {
        Connection connection = null;

        var myMessage = new MyMessage(jda, guildId, tcId, msgId);

        try {
            connection = Servant.db.getHikari().getConnection();
            PreparedStatement delete;

            // Giveaway
            if (myMessage.isGiveaway()) {
                Console.log("Purging giveaway... Guild ID: " + guildId + " Message ID: " + msgId);

                delete = connection.prepareStatement("DELETE FROM giveaways WHERE guild_id=? AND msg_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                delete.setLong(1, guildId);
                delete.setLong(2, msgId);
                delete.executeUpdate();

                delete = connection.prepareStatement("DELETE FROM tmp_giveaway_participants WHERE guild_id=? AND msg_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                delete.setLong(1, guildId);
                delete.setLong(2, msgId);
                delete.executeUpdate();
            }

            // Birthday
            else if (myMessage.isBirthdayList()) {
                Console.log("Purging birthday list... Guild ID: " + guildId + " Message ID: " + msgId);

                delete = connection.prepareStatement("UPDATE guild_birthdays SET list_tc_id=?, list_msg_id=?, list_author_id=? WHERE guild_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                delete.setLong(1, 0);
                delete.setLong(2, 0);
                delete.setLong(3, 0);
                delete.setLong(4, guildId);
                delete.executeUpdate();
            }

            // Polls
            else if (myMessage.isRadiopoll() || myMessage.isQuickpoll() || myMessage.isCheckpoll()) {
                Console.log("Purging poll... Guild ID: " + guildId + " Message ID: " + msgId);

                delete = connection.prepareStatement("DELETE FROM polls WHERE guild_id=? AND msg_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                delete.setLong(1, guildId);
                delete.setLong(2, msgId);
                delete.executeUpdate();

                delete = connection.prepareStatement("DELETE FROM tmp_poll_participants WHERE guild_id=? AND msg_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                delete.setLong(1, guildId);
                delete.setLong(2, msgId);
                delete.executeUpdate();
            }

            // Ratings
            else if (myMessage.isRating()) {
                Console.log("Purging rating... Guild ID: " + guildId + " Message ID: " + msgId);

                delete = connection.prepareStatement("DELETE FROM ratings WHERE guild_id=? AND msg_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                delete.setLong(1, guildId);
                delete.setLong(2, msgId);
                delete.executeUpdate();

                delete = connection.prepareStatement("DELETE FROM tmp_rating_participants WHERE guild_id=? AND msg_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                delete.setLong(1, guildId);
                delete.setLong(2, msgId);
                delete.executeUpdate();
            }

            // Reaction Roles
            else if (myMessage.isReactionRole()) {
                Console.log("Purging reaction role... Guild ID: " + guildId + " Message ID: " + msgId);

                delete = connection.prepareStatement("DELETE FROM reaction_roles WHERE guild_id=? AND msg_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                delete.setLong(1, guildId);
                delete.setLong(2, msgId);
                delete.executeUpdate();

                delete = connection.prepareStatement("DELETE FROM reaction_role_fields WHERE guild_id=? AND msg_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                delete.setLong(1, guildId);
                delete.setLong(2, msgId);
                delete.executeUpdate();

                delete = connection.prepareStatement("DELETE FROM reaction_role_messages WHERE guild_id=? AND msg_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                delete.setLong(1, guildId);
                delete.setLong(2, msgId);
                delete.executeUpdate();
            }

            // Remind Me
            else if (myMessage.isRemindMe()) {
                Console.log("Purging remindme... Guild ID: " + guildId + " Message ID: " + msgId);

                delete = connection.prepareStatement("DELETE FROM remind_mes WHERE guild_id=? AND msg_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                delete.setLong(1, guildId);
                delete.setLong(2, msgId);
                delete.executeUpdate();
            }

            // Signup
            else if (myMessage.isSignup()) {
                Console.log("Purging signup... Guild ID: " + guildId + " Message ID: " + msgId);

                delete = connection.prepareStatement("DELETE FROM signups WHERE guild_id=? AND msg_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                delete.setLong(1, guildId);
                delete.setLong(2, msgId);
                delete.executeUpdate();

                delete = connection.prepareStatement("DELETE FROM tmp_signup_participants WHERE guild_id=? AND msg_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                delete.setLong(1, guildId);
                delete.setLong(2, msgId);
                delete.executeUpdate();
            }

            // Best of Image
            if (myMessage.isBestOfImageBlacklisted()) {
                Console.log("Purging best of image... Guild ID: " + guildId + " Message ID: " + msgId);

                delete = connection.prepareStatement("DELETE FROM tmp_best_of_image_bl WHERE guild_id=? AND msg_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                delete.setLong(1, guildId);
                delete.setLong(2, msgId);
                delete.executeUpdate();
            }

            // Best of Quote
            if (myMessage.isBestOfQuoteBlacklisted()) {
                Console.log("Purging best of quote... Guild ID: " + guildId + " Message ID: " + msgId);

                delete = connection.prepareStatement("DELETE FROM tmp_best_of_quote_bl WHERE guild_id=? AND msg_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                delete.setLong(1, guildId);
                delete.setLong(2, msgId);
                delete.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#purgeMsg"));
        } finally {
            closeQuietly(connection);
        }
    }

    // Prefix
    public String getPrefix() {
        Connection connection = null;
        var prefix = Servant.config.getDefaultPrefix();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT prefix " +
                            "FROM guilds " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()){
                prefix = resultSet.getString("prefix");
                if (prefix == null || prefix.isEmpty()) prefix = Servant.config.getDefaultPrefix();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getPrefix"));
        } finally {
            closeQuietly(connection);
        }

        return prefix;
    }

    // LanguageCode
    public String getLanguageCode() {
        Connection connection = null;
        String languageCode = Servant.config.getDefaultLanguage();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT l.code " +
                            "FROM guilds AS g " +
                            "INNER JOIN const_languages AS l " +
                            "ON g.language_code = l.code " +
                            "WHERE g.guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) languageCode = resultSet.getString("code");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getLanguageCode"));
        } finally {
            closeQuietly(connection);
        }

        return languageCode;
    }

    // Timezone
    public TimeZone getTimezone() {
        Connection connection = null;
        var timezone = TimeZone.getTimeZone("Etc/UTC");

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT t.timezone " +
                            "FROM guilds AS g " +
                            "INNER JOIN const_timezones AS t " +
                            "ON g.timezone_id = t.id " +
                            "WHERE g.guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) timezone = TimeZone.getTimeZone(resultSet.getString("timezone"));
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getTimezone"));
        } finally {
            closeQuietly(connection);
        }

        return timezone;
    }

    // Servant-Moderators
    public ArrayList<Role> getModRoles() {
        Connection connection = null;
        var modRoles = new ArrayList<Role>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT role_id " +
                            "FROM guild_mod_roles " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                var modRoleId = resultSet.getLong("role_id");
                Role modRole = guild.getRoleById(modRoleId);
                if (modRole == null) unsetModRole(); // Unsetting it because the role doesn't exist anymore.
                else modRoles.add(modRole);
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getModRoles"));
        } finally {
            closeQuietly(connection);
        }

        return modRoles;
    }

    public void unsetModRole() {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var update = connection.prepareStatement(
                    "UPDATE guilds " +
                            "SET mod_role_id=? " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            update.setLong(1, 0L);
            update.setLong(2, guildId);
            update.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#unsetModRole"));
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean isMod(User user) {
        var isMod = false;
        var member = guild.getMemberById(user.getIdLong());

        if (member != null) {
            if (member.isOwner())
                isMod = true;
            else if (member.hasPermission(Permission.ADMINISTRATOR))
                isMod = true;
            else {
                var modRoles = getModRoles();
                var memberRoles = member.getRoles();
                if (modRoles != null) {
                    for (var modRole : modRoles) {
                        if (memberRoles.contains(modRole)) {
                            isMod = true;
                            break;
                        }
                    }
                }
            }
        }

        return isMod;
    }

    // Toggle
    public boolean commandIsEnabled(String command) {
        Connection connection = null;
        var isEnabled = true;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT d.id " +
                            "FROM guild_disabled_commands AS d " +
                            "INNER JOIN const_commands AS c " +
                            "ON d.command_id=c.id " +
                            "WHERE d.guild_id=? AND c.name=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            select.setString(2, command);
            var resultSet = select.executeQuery();
            if (resultSet.first()) isEnabled = false;
            else {
                select = connection.prepareStatement(
                        "SELECT guild_id" + " " +
                                "FROM guilds" + " " +
                                "WHERE guild_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                select.setLong(1, guildId);
                resultSet = select.executeQuery();
                if (!resultSet.first()) isEnabled = ToggleUtil.commandIsEnabledByDefault(command);
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#commandIsEnabled"));
        } finally {
            closeQuietly(connection);
        }

        return isEnabled;
    }

    public boolean featureIsEnabled(String feature) {
        Connection connection = null;
        var isEnabled = true;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT d.id " +
                            "FROM guild_disabled_features AS d " +
                            "INNER JOIN const_features AS f " +
                            "ON d.feature_id=f.id " +
                            "WHERE d.guild_id=? AND f.name=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            select.setString(2, feature);
            var resultSet = select.executeQuery();
            if (resultSet.first()) isEnabled = false;
            else {
                select = connection.prepareStatement(
                        "SELECT guild_id " +
                                "FROM guilds " +
                                "WHERE guild_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                select.setLong(1, guildId);
                resultSet = select.executeQuery();
                if (!resultSet.first()) isEnabled = ToggleUtil.featureIsEnabledByDefault(feature);
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#featureIsEnabled"));
        } finally {
            closeQuietly(connection);
        }

        return isEnabled;
    }

    public boolean categoryIsEnabled(String category) {
        Connection connection = null;
        var isEnabled = true;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT d.id " +
                            "FROM guild_disabled_categories AS d " +
                            "INNER JOIN const_categories AS c " +
                            "ON d.category_id=c.id " +
                            "WHERE d.guild_id=? AND c.name=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            select.setString(2, category);
            var resultSet = select.executeQuery();
            if (resultSet.first()) isEnabled = false;
            else {
                select = connection.prepareStatement(
                        "SELECT guild_id " +
                                "FROM guilds " +
                                "WHERE guild_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                select.setLong(1, guildId);
                resultSet = select.executeQuery();
                if (!resultSet.first()) isEnabled = ToggleUtil.categoryIsEnabledByDefault(category);
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#categoryIsEnabled"));
        } finally {
            closeQuietly(connection);
        }

        return isEnabled;
    }

    public boolean pluginIsEnabled(String plugin) {
        Connection connection = null;
        var isEnabled = true;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT d.id " +
                            "FROM guild_disabled_plugins AS d " +
                            "INNER JOIN const_plugins AS p " +
                            "ON d.plugin_id=p.id " +
                            "WHERE d.guild_id=? AND p.name=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            select.setString(2, plugin);
            var resultSet = select.executeQuery();
            if (resultSet.first()) isEnabled = false;
            else {
                select = connection.prepareStatement(
                        "SELECT guild_id " +
                                "FROM guilds " +
                                "WHERE guild_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                select.setLong(1, guildId);
                resultSet = select.executeQuery();
                if (!resultSet.first()) isEnabled = ToggleUtil.pluginIsEnabledByDefault(plugin);
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#categoryIsEnabled"));
        } finally {
            closeQuietly(connection);
        }

        return isEnabled;
    }

    // Birthday
    private boolean birthdayHasEntry() {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT guild_id " +
                            "FROM guild_birthdays " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#birthdayHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public TextChannel getBirthdayAnnouncementTc() {
        Connection connection = null;
        TextChannel birthdayTc = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT announcement_tc_id " +
                            "FROM guild_birthdays " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                var birthdayTcId = resultSet.getLong("announcement_tc_id");
                birthdayTc = guild.getTextChannelById(birthdayTcId);
                if (birthdayTc == null) unsetBirthdayAnnouncementTc(); // Unsetting it because the role doesn't exist anymore.
            }

        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getBirthdayAnnouncementTc"));
        } finally {
            closeQuietly(connection);
        }

        return birthdayTc;
    }

    public void unsetBirthdayAnnouncementTc() {
        if (birthdayHasEntry()) {
            Connection connection = null;

            try {
                connection = Servant.db.getHikari().getConnection();
                var update = connection.prepareStatement(
                        "UPDATE guild_birthdays " +
                            "SET announcement_tc_id=? " +
                            "WHERE guild_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                update.setLong(1, 0L);
                update.setLong(2, guildId);
                update.executeUpdate();
            } catch (SQLException e) {
                Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#unsetBirthdayAnnouncementTc"));
            } finally {
                closeQuietly(connection);
            }
        }
    }

    public long getBirthdayListTcId() {
        Connection connection = null;
        var channelId = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT list_tc_id " +
                            "FROM guild_birthdays " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) channelId = resultSet.getLong("list_tc_id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getBirthdayListTcId"));
        } finally {
            closeQuietly(connection);
        }

        return channelId;
    }

    public long getBirthdayListMsgId() {
        Connection connection = null;
        var messageId = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT list_msg_id " +
                            "FROM guild_birthdays " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) messageId = resultSet.getLong("list_msg_id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getBirthdayListMsgId"));
        } finally {
            closeQuietly(connection);
        }

        return messageId;
    }

    public long getBirthdayListAuthorId() {
        Connection connection = null;
        var messageId = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();

            var select = connection.prepareStatement(
                    "SELECT list_author_id " +
                            "FROM guild_birthdays " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) messageId = resultSet.getLong("list_author_id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getBirthdayListAuthorId"));
        } finally {
            closeQuietly(connection);
        }

        return messageId;
    }

    public void unsetBirthdayList() {
        if (birthdayHasEntry()) {
            Connection connection = null;

            try {
                connection = Servant.db.getHikari().getConnection();
                var update = connection.prepareStatement(
                        "UPDATE guild_birthdays " +
                            "SET list_tc_id=?, list_msg_id=?, list_author_id=? " +
                            "WHERE guild_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                update.setLong(1, 0L);
                update.setLong(2, 0L);
                update.setLong(3, 0L);
                update.setLong(4, guildId);
                update.executeUpdate();
            } catch (SQLException e) {
                Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#unsetBirthdayList"));
            } finally {
                closeQuietly(connection);
            }
        }
    }

    public boolean usesServantBirthday() {
        Connection connection = null;
        var usesServantBirthday = true;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT servant_bday " +
                            "FROM guild_birthdays " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) usesServantBirthday = resultSet.getBoolean("servant_bday");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#usesServantBirthday"));
        } finally {
            closeQuietly(connection);
        }

        return usesServantBirthday;
    }

    public Map<Long, String> getBirthdays() {
        Connection connection = null;
        var birthdays = new HashMap<Long, String>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT b.user_id, u.birthday " +
                            "FROM user_birthday_guilds AS b " +
                            "INNER JOIN users AS u " +
                            "ON b.user_id = u.user_id " +
                            "WHERE b.guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first())
                do birthdays.put(resultSet.getLong("user_id"), resultSet.getString("birthday"));
                while (resultSet.next());
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getBirthdays"));
        } finally {
            closeQuietly(connection);
        }

        if (usesServantBirthday()) birthdays.put(jda.getSelfUser().getIdLong(), "2018-04-06");

        return birthdays;
    }

    // Blacklist
    public boolean isBlacklisted() {
        Connection connection = null;
        var isBlacklisted = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT * " +
                            "FROM global_blacklist " +
                            "WHERE user_or_guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) isBlacklisted = true;
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#isBlacklisted"));
        } finally {
            closeQuietly(connection);
        }

        return isBlacklisted;
    }

    public void setBlacklist() {
        Connection connection = null;

        try {
            if (!isBlacklisted()) {
                connection = Servant.db.getHikari().getConnection();
                var insert = connection.prepareStatement(
                        "INSERT INTO global_blacklist (user_or_guild_id) " +
                                "VALUES (?)",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                insert.setLong(1, guildId);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#setBlacklist"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void unsetBlacklist() {
        Connection connection = null;

        try {
            if (isBlacklisted()) {
                connection = Servant.db.getHikari().getConnection();
                var delete = connection.prepareStatement(
                        "DELETE FROM global_blacklist " +
                                "WHERE user_or_guild_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                delete.setLong(1, guildId);
                delete.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#unsetBlacklist"));
        } finally {
            closeQuietly(connection);
        }
    }

    // BestOfImage
    public String getBestOfImageEmoji() {
        Connection connection = null;
        var emoji = "";

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT emoji FROM guild_best_of_images " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) emoji = resultSet.getString("emoji");
            if (emoji.isEmpty()) emoji = null;
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getBestOfImageEmoji"));
        } finally {
            closeQuietly(connection);
        }

        return emoji;
    }

    public TextChannel getBestOfImageChannel() {
        Connection connection = null;
        TextChannel channel = null;

        var thisGuild = guild.getJDA().getGuildById(guildId);
        if (thisGuild == null) return null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT tc_id FROM guild_best_of_images " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) channel = thisGuild.getTextChannelById(resultSet.getLong("tc_id"));
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getBestOfImageChannel"));
        } finally {
            closeQuietly(connection);
        }

        if (channel != null && channel.getIdLong() == 0) channel = null;

        return channel;
    }

    public int getBestOfImageMinVotesFlat() {
        Connection connection = null;
        int number = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT min_votes_flat " +
                            "FROM guild_best_of_images " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) number = resultSet.getInt("min_votes_flat");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getBestOfImageMinVotesFlat"));
        } finally {
            closeQuietly(connection);
        }

        return number;
    }

    public int getBestOfImageMinVotesPercent() {
        Connection connection = null;
        int percentage = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT min_votes_percent " +
                            "FROM guild_best_of_images " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) percentage = resultSet.getInt("min_votes_percent");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getBestOfImageMinVotesPercent"));
        } finally {
            closeQuietly(connection);
        }

        return percentage;
    }

    public void addBestOfImageBlacklist(long messageId, long tcId) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (!bestOfImageIsBlacklisted(messageId)) {
                var insert = connection.prepareStatement(
                        "INSERT INTO tmp_best_of_image_bl (msg_id,guild_id,tc_id) " +
                                "VALUES (?,?,?)",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                insert.setLong(1, messageId);
                insert.setLong(2, guildId);
                insert.setLong(3, tcId);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#addBestOfImageBlacklist"));
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean bestOfImageIsBlacklisted(long messageId) {
        Connection connection = null;
        var isBlacklisted = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT * " +
                            "FROM tmp_best_of_image_bl " +
                            "WHERE msg_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, messageId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) isBlacklisted = true;
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#bestOfImageIsBlacklisted"));
        } finally {
            closeQuietly(connection);
        }

        return isBlacklisted;
    }

    // BestOfQuote
    public String getBestOfQuoteEmoji() {
        Connection connection = null;
        var emoji = "";

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT emoji FROM guild_best_of_quotes " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) emoji = resultSet.getString("emoji");
            if (emoji.isEmpty()) emoji = null;
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getBestOfQuoteEmoji"));
        } finally {
            closeQuietly(connection);
        }

        return emoji;
    }

    public TextChannel getBestOfQuoteChannel() {
        Connection connection = null;
        TextChannel channel = null;

        var thisGuild = guild.getJDA().getGuildById(guildId);
        if (thisGuild == null) return null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT tc_id FROM guild_best_of_quotes " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) channel = thisGuild.getTextChannelById(resultSet.getLong("tc_id"));
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getBestOfQuoteChannel"));
        } finally {
            closeQuietly(connection);
        }

        return channel;
    }

    public int getBestOfQuoteMinVotesFlat() {
        Connection connection = null;
        int number = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT min_votes_flat " +
                            "FROM guild_best_of_quotes " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) number = resultSet.getInt("min_votes_flat");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getBestOfQuoteMinVotesFlat"));
        } finally {
            closeQuietly(connection);
        }

        return number;
    }

    public int getBestOfQuoteMinVotesPercent() {
        Connection connection = null;
        int percentage = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT min_votes_percent " +
                            "FROM guild_best_of_quotes " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) percentage = resultSet.getInt("min_votes_percent");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getBestOfQuoteMinVotesPercent"));
        } finally {
            closeQuietly(connection);
        }

        return percentage;
    }

    public void addBestOfQuoteBlacklist(long messageId, long tcId) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (!bestOfQuoteIsBlacklisted(messageId)) {
                var insert = connection.prepareStatement(
                        "INSERT INTO tmp_best_of_quote_bl (msg_id,guild_id,tc_id) " +
                                "VALUES (?)",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                insert.setLong(1, messageId);
                insert.setLong(2, guildId);
                insert.setLong(3, tcId);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#addBestOfQuoteBlacklist"));
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean bestOfQuoteIsBlacklisted(long messageId) {
        Connection connection = null;
        var isBlacklisted = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT * " +
                            "FROM tmp_best_of_quote_bl " +
                            "WHERE msg_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, messageId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) isBlacklisted = true;
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#bestOfQuoteIsBlacklisted"));
        } finally {
            closeQuietly(connection);
        }

        return isBlacklisted;
    }

    // Level
    public float getLevelModifier() {
        Connection connection = null;
        var modifier = 1.00f;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT modifier " +
                            "FROM guild_level " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) modifier = resultSet.getFloat("modifier");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getLevelModifier"));
        } finally {
            closeQuietly(connection);
        }

        return modifier;
    }

    public boolean levelNotificationIsEnabled() {
        Connection connection = null;
        var isEnabled = true;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT notification " +
                            "FROM guild_level " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) isEnabled = resultSet.getBoolean("notification");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#levelNotificationIsEnabled"));
        } finally {
            closeQuietly(connection);
        }

        return isEnabled;
    }

    public List<Long> getLevelRole(int level) {
        Connection connection = null;
        List<Long> roleId = new ArrayList<>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT role_id " +
                            "FROM guild_level_roles " +
                            "WHERE guild_id=? " +
                            "AND level=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            select.setInt(2, level);
            var resultSet = select.executeQuery();
            if (resultSet.first())
                do roleId.add(resultSet.getLong("role_id"));
                while (resultSet.next());
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getLevelRole"));
        } finally {
            closeQuietly(connection);
        }

        return roleId;
    }

    // Voice Lobby
    public List<Long> getVoiceLobbies() {
        Connection connection = null;
        var lobbies = new ArrayList<Long>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT * " +
                            "FROM guild_voice_lobbies " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first())
                do lobbies.add(resultSet.getLong("vc_id"));
                while (resultSet.next());
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getVoiceLobbies"));
        } finally {
            closeQuietly(connection);
        }

        return lobbies;
    }

    // Command Counts
    public LinkedHashMap<String, Integer> getCommandCounts() {
        Connection connection = null;
        var commandCounts = new LinkedHashMap<String, Integer>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT c.name g.count " +
                            "FROM guild_commands_count AS g " +
                            "INNER JOIN commands AS c " +
                            "WHERE g.guild_id=? " +
                            "ORDER BY g.count DESC",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                do {
                    var command = resultSet.getString("name");
                    var myCommand = new MyCommand(jda, command);
                    if (!myCommand.isOwnerCommand())
                        commandCounts.put(command, resultSet.getInt("count"));
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getCommandCounts"));
        } finally {
            closeQuietly(connection);
        }

        return commandCounts;
    }

    private int getCommandCount(int commandId) {
        Connection connection = null;
        var featureCount = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT count " +
                            "FROM guild_command_counts " +
                            "WHERE guild_id=? " +
                            "AND command_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            select.setInt(2, commandId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) featureCount = resultSet.getInt("count");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getCommandCount"));
        } finally {
            closeQuietly(connection);
        }

        return featureCount;
    }

    private boolean guildCommandCountsHasEntry(int commandId) {
        Connection connection = null;
        var userCommandCountsHasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT id " +
                            "FROM guild_command_counts " +
                            "WHERE guild_id=? " +
                            "AND command_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            select.setInt(2, commandId);
            var resultSet = select.executeQuery();
            userCommandCountsHasEntry = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#guildCommandCountsHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return userCommandCountsHasEntry;
    }

    public void incrementCommandCount(String command) {
        command = command.toLowerCase(); // just to be sure
        Connection connection = null;

        var myCommand = new MyCommand(jda, command);
        if (myCommand.getId() == 0) return;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (guildCommandCountsHasEntry(myCommand.getId())) {
                var count = getCommandCount(myCommand.getId());
                var update = connection.prepareStatement(
                        "UPDATE guild_command_counts " +
                                "SET count=? " +
                                "WHERE guild_id=? " +
                                "AND command_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                update.setInt(1, count + 1);
                update.setLong(2, guildId);
                update.setInt(3, myCommand.getId());
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement(
                        "INSERT INTO guild_command_counts (guild_id,command_id,count) " +
                                "VALUES (?,?,?)",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                insert.setLong(1, guildId);
                insert.setInt(2, myCommand.getId());
                insert.setInt(3, 1);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#incrementCommandCount"));
        } finally {
            closeQuietly(connection);
        }
    }

    // Autorole
    public boolean hasAutorole() {
        Connection connection = null;
        var hasAutorole = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT * " +
                            "FROM guild_autoroles " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasAutorole = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#hasAutorole"));
        } finally {
            closeQuietly(connection);
        }

        return hasAutorole;
    }

    public HashMap<Role, Integer> getAutoRoles() {
        Connection connection = null;
        var roles = new HashMap<Role, Integer>();

        var thisGuild = guild.getJDA().getGuildById(guildId);
        if (thisGuild == null) return null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT * " +
                            "FROM guild_autoroles " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                do {
                    var roleId = resultSet.getLong("role_id");
                    var delay = resultSet.getInt("delay");
                    roles.put(thisGuild.getRoleById(roleId), delay);
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getAutorole"));
        } finally {
            closeQuietly(connection);
        }

        if (roles.isEmpty()) return null;
        else return roles;
    }

    // MediaOnlyChannel
    public boolean isMediaOnlyChannel(MessageChannel channel) {
        Connection connection = null;
        var isMediaOnlyChannel = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT * " +
                            "FROM guild_media_only_channels " +
                            "WHERE guild_id=? " +
                            "AND tc_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            select.setLong(2, channel.getIdLong());
            var resultSet = select.executeQuery();
            isMediaOnlyChannel = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#mediaOnlyChannelHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return isMediaOnlyChannel;
    }

    public boolean mediaOnlyChannelNotification() {
        Connection connection = null;
        var notify = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT * " +
                            "FROM guild_disabled_features " +
                            "WHERE guild_id=? " +
                            "AND feature_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            select.setInt(2, 5);
            var resultSet = select.executeQuery();
            notify = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#mediaOnlyChannelHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return !notify;
    }

    // Join
    public TextChannel getJoinTc() {
        Connection connection = null;
        TextChannel tc = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT tc_id " +
                            "FROM guild_joins " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                var tc_id = resultSet.getLong("tc_id");
                if (guild.getTextChannelById(tc_id) != null) tc = guild.getTextChannelById(tc_id);
                else unsetJoinTc(tc_id);
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getJoinTc"));
        } finally {
            closeQuietly(connection);
        }

        return tc;
    }

    public void unsetJoinTc(long tcId) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "UPDATE guild_joins " +
                            "SET tc_id=?" +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, tcId);
            select.setLong(2, guildId);
            select.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#unsetJoinTc"));
        } finally {
            closeQuietly(connection);
        }
    }

    public String getJoinMessage() {
        Connection connection = null;
        String msg = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT msg " +
                            "FROM guild_joins " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) msg = resultSet.getString("msg");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getJoinMessage"));
        } finally {
            closeQuietly(connection);
        }

        return msg;
    }

    // Leave
    public TextChannel getLeaveTc() {
        Connection connection = null;
        TextChannel tc = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT tc_id " +
                            "FROM guild_leaves " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                var tc_id = resultSet.getLong("tc_id");
                if (guild.getTextChannelById(tc_id) != null) tc = guild.getTextChannelById(tc_id);
                else unsetLeaveTc(tc_id);
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getLeaveTc"));
        } finally {
            closeQuietly(connection);
        }

        return tc;
    }

    public void unsetLeaveTc(long tcId) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "UPDATE guild_joins " +
                            "SET tc_id=? " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, tcId);
            select.setLong(2, guildId);
            select.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#unsetLeaveTc"));
        } finally {
            closeQuietly(connection);
        }
    }

    public String getLeaveMessage() {
        Connection connection = null;
        String msg = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT msg " +
                            "FROM guild_leaves " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) msg = resultSet.getString("msg");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getLeaveMessage"));
        } finally {
            closeQuietly(connection);
        }

        return msg;
    }


    // Exp
    public int getUserRank(long userId) {
        Connection connection = null;
        int rank = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT * FROM user_exp " +
                            "WHERE guild_id=? " +
                            "ORDER BY exp DESC",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                rank = 1;
                do {
                    if (resultSet.getLong("user_id") == userId) break;
                    else rank++;
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getUserRank"));
        } finally {
            closeQuietly(connection);
        }

        return rank;
    }

    // Livestream
    public boolean streamIsPublic() {
        Connection connection = null;
        var isPublicMode = true;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT is_public " +
                            "FROM guild_livestreams " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) isPublicMode = resultSet.getBoolean("is_public");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#streamIsPublic"));
        } finally {
            closeQuietly(connection);
        }

        return isPublicMode;
    }

    public long getStreamPingRoleId() {
        Connection connection = null;
        var pingRoleId = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT ping_role_id " +
                            "FROM guild_livestreams " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) pingRoleId = resultSet.getLong("ping_role_id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getStreamPingRoleId"));
        } finally {
            closeQuietly(connection);
        }

        return pingRoleId;
    }

    public long getStreamTcId() {
        Connection connection = null;
        var channelId = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT tc_id " +
                            "FROM guild_livestreams " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) channelId = resultSet.getLong("tc_id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getStreamTcId"));
        } finally {
            closeQuietly(connection);
        }

        return channelId;
    }

    public long getStreamRoleId() {
        Connection connection = null;
        var role = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT role_id " +
                            "FROM guild_livestreams " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) role = resultSet.getLong("role_id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getStreamRoleId"));
        } finally {
            closeQuietly(connection);
        }

        return role;
    }

    public List<Long> getStreamerRoleIds() {
        Connection connection = null;
        var streamerRoles = new ArrayList<Long>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT role_id " +
                            "FROM guild_livestreamers " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) do streamerRoles.add(resultSet.getLong("role_id")); while (resultSet.next());
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getStreamerRoleIds"));
        } finally {
            closeQuietly(connection);
        }

        return streamerRoles;
    }

    // Log
    private boolean logHasEntry() {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT * " +
                            "FROM guild_logs " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#logHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public HashMap<String, Boolean> getLogEvents() {
        Connection connection = null;
        var logSettings = new HashMap<String, Boolean>();

        if (logHasEntry()) {
            try {
                connection = Servant.db.getHikari().getConnection();
                var select = connection.prepareStatement(
                        "SELECT * " +
                                "FROM guild_logs " +
                                "WHERE guild_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                select.setLong(1, guildId);
                var resultSet = select.executeQuery();
                if (resultSet.first()) {
                    logSettings.put("boost_count", resultSet.getBoolean("boost_count"));
                    logSettings.put("member_join", resultSet.getBoolean("member_join"));
                    logSettings.put("member_leave", resultSet.getBoolean("member_leave"));
                    logSettings.put("role_add", resultSet.getBoolean("role_add"));
                    logSettings.put("role_remove", resultSet.getBoolean("role_remove"));
                    logSettings.put("deleted_msgs", resultSet.getBoolean("deleted_msgs"));
                }
            } catch (SQLException e) {
                Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getLogEvents"));
            } finally {
                closeQuietly(connection);
            }
        }

        return logSettings;
    }

    public boolean logIsEnabled(String eventName) {
        Connection connection = null;
        var isEnabled = true;

        if (logHasEntry()) {
            try {
                connection = Servant.db.getHikari().getConnection();
                var select = connection.prepareStatement(
                        "SELECT * " +
                                "FROM guild_logs " +
                                "WHERE guild_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                select.setLong(1, guildId);
                var resultSet = select.executeQuery();
                if (resultSet.first()) isEnabled = resultSet.getBoolean(eventName);
            } catch (SQLException e) {
                Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#logIsEnabled"));
            } finally {
                closeQuietly(connection);
            }
        }

        return isEnabled;
    }

    public long getLogChannelId() {
        Connection connection = null;
        var logChannelId = 0L;

        if (logHasEntry()) {
            try {
                connection = Servant.db.getHikari().getConnection();
                var select = connection.prepareStatement(
                        "SELECT tc_id " +
                                "FROM guild_logs " +
                                "WHERE guild_id=?",
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                select.setLong(1, guildId);
                var resultSet = select.executeQuery();
                if (resultSet.first()) logChannelId = resultSet.getLong("tc_id");
            } catch (SQLException e) {
                Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getLogChannelId"));
            } finally {
                closeQuietly(connection);
            }
        }

        return logChannelId;
    }

    // Giveaways
    public String getCurrentGiveaways(JDA jda, String lang) {
        Connection connection = null;
        var currentGiveaways = LanguageHandler.get(lang, "giveaway_nocurrent");

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT * FROM giveaways " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) currentGiveaways = getRunningGiveaways(resultSet, lang);
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getCurrentGiveaways"));
        } finally {
            closeQuietly(connection);
        }

        return currentGiveaways;
    }

    private String getRunningGiveaways(ResultSet resultSet, String lang) throws SQLException {
        var giveaways = new StringBuilder();
        do {
            var tc = guild.getTextChannelById(resultSet.getLong("tc_id"));
            if (tc == null) continue;
            giveaways.append("- ")
                    .append(tc.getAsMention()).append(": ")
                    .append(LanguageHandler.get(lang, "giveaway_prize")).append(" ").append(resultSet.getString("prize"))
                    .append(" | [").append(LanguageHandler.get(lang, "jump")).append("](").append("https://discordapp.com/channels/").append(guildId).append("/").append(tc.getId()).append("/").append(resultSet.getLong("msg_id")).append(")")
                    .append("\n");
        } while (resultSet.next());

        return giveaways.toString();
    }

    public void insertGiveawayToDb(long tcId, long msgId, long authorId, String prize, Timestamp eventTime, int amountWinners) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var preparedStatement = connection.prepareStatement(
                    "INSERT INTO giveaways(guild_id,tc_id,msg_id,author_id,prize,event_time,amount_winners) " +
                            "VALUES(?,?,?,?,?,?,?)",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setLong(1, guildId);
            preparedStatement.setLong(2, tcId);
            preparedStatement.setLong(3, msgId);
            preparedStatement.setLong(4, authorId);
            preparedStatement.setString(5, prize);
            preparedStatement.setTimestamp(6, eventTime);
            preparedStatement.setInt(7, amountWinners);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#insertGiveawayToDb"));
        } finally {
            closeQuietly(connection);
        }
    }

    public List<String> getCustomCommands() {
        Connection connection = null;
        var customCommands = new ArrayList<String>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT invoke " +
                            "FROM custom_commands " +
                            "WHERE guild_id=? " +
                            "ORDER BY invoke ASC");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            while (resultSet.next()) customCommands.add(resultSet.getString("invoke"));
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyGuild#getCustomCommands"));
        } finally {
            closeQuietly(connection);
        }

        return customCommands;
    }
}
