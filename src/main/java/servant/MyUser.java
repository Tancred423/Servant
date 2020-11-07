// Author: Tancred423 (https://github.com/Tancred423)
package servant;

import commands.fun.baguette.Baguette;
import commands.fun.tictactoe.TicTacToeStatistic;
import commands.interaction.Interaction;
import files.language.LanguageHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import utilities.Constants;
import utilities.Parser;

import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import static servant.Database.closeQuietly;

public class MyUser {
    private final User user;
    private final long userId;
    private final JDA jda;

    public MyUser(User user) {
        this.user = user;
        this.userId = user.getIdLong();
        this.jda = user.getJDA();
    }

    public User getUser() {
        return user;
    }

    public long getUserId() {
        return userId;
    }

    public JDA getJDA() {
        return jda;
    }

    // User
    private boolean userHasEntry() {
        Connection connection = null;
        var userHasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT user_id " +
                            "FROM users " +
                            "WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            userHasEntry = resultSet.next();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#userHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return userHasEntry;
    }

    // Prefix
    public String getPrefix() {
        Connection connection = null;
        var prefix = Servant.config.getDefaultPrefix();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT prefix " +
                            "FROM users " +
                            "WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) prefix = resultSet.getString("prefix");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#getPrefix"));
        } finally {
            closeQuietly(connection);
        }

        if (prefix.trim().isEmpty()) prefix = Servant.config.getDefaultPrefix();

        return prefix;
    }

    // LanguageCode
    public String getLanguageCode() {
        Connection connection = null;
        var languageCode = Servant.config.getDefaultLanguage();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT language_code " +
                            "FROM users " +
                            "WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) languageCode = resultSet.getString("language_code");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#getLanguageCode"));
        } finally {
            closeQuietly(connection);
        }

        return languageCode;
    }

    // ColorCode
    public String getColorCode() {
        Connection connection = null;
        var colorCode = Servant.config.getDefaultColorCode();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT color_code " +
                            "FROM users " +
                            "WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) colorCode = resultSet.getString("color_code");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#getColorCode"));
        } finally {
            closeQuietly(connection);
        }

        return colorCode;
    }

    public Color getColor() {
        Connection connection = null;
        var colorCode = Servant.config.getDefaultColorCode();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT color_code " +
                            "FROM users " +
                            "WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) colorCode = resultSet.getString("color_code");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#getColorCode"));
        } finally {
            closeQuietly(connection);
        }

        return Color.decode(colorCode);
    }

    // Bio
    public String getBio() {
        Connection connection = null;
        String bio = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT bio " +
                            "FROM users " +
                            "WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) bio = resultSet.getString("bio");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#getBio"));
        } finally {
            closeQuietly(connection);
        }

        return bio;
    }

    // Birthday
    public Date getBirthday() {
        Connection connection = null;
        Date birthday = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT birthday " +
                            "FROM users " +
                            "WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) birthday = resultSet.getDate("birthday");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#getBirthday"));
        } finally {
            closeQuietly(connection);
        }

        return birthday;
    }

    public void setBirthday(String birthday) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (userHasEntry()) {
                var update = connection.prepareStatement(
                        "UPDATE users " +
                                "SET birthday=? " +
                                "WHERE user_id=?");
                update.setString(1, birthday);
                update.setLong(2, userId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement(
                        "INSERT INTO users (user_id,prefix,language_code,color_code,bio,birthday,profile_bg_id) " +
                                "VALUES (?,?,?,?,?,?,?)");
                insert.setLong(1, userId);
                insert.setString(2, "");
                insert.setString(3, Servant.config.getDefaultLanguage());
                insert.setString(4, Servant.config.getDefaultColorCode().replace("0x", "#"));
                insert.setString(5, "");
                insert.setString(6, birthday);
                insert.setInt(7, 1);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#setBirthday"));
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean isBirthdayGuild(long guildId) {
        Connection connection = null;
        var isBirthdayGuild = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT id " +
                            "FROM user_birthday_guilds " +
                            "WHERE user_id=? " +
                            "AND guild_id=?");
            select.setLong(1, userId);
            select.setLong(2, guildId);
            var resultSet = select.executeQuery();
            isBirthdayGuild = resultSet.next();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#getBirthdayGuild"));
        } finally {
            closeQuietly(connection);
        }

        return isBirthdayGuild;
    }

    public void addBirthdayGuild(long guildId) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (!isBirthdayGuild(guildId)) {
                var insert = connection.prepareStatement(
                        "INSERT INTO user_birthday_guilds (user_id,guild_id) " +
                                "VALUES (?,?)");
                insert.setLong(1, userId);
                insert.setLong(2, guildId);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#addBirthdayGuild"));
        } finally {
            closeQuietly(connection);
        }
    }

    // Creator
    public boolean isCreator() {
        return String.valueOf(userId).equals(Servant.config.getBotOwnerId());
    }

    // Supporter
    public boolean isSupporter() {
        var isSupporter = false;

        var sm = jda.getShardManager();
        if (sm != null) {
            var sk = sm.getGuildById(Constants.SERVANTS_KINGDOM_ID);
            if (sk != null) {
                var member = sk.getMemberById(userId);
                if (member != null) {
                    var roles = member.getRoles();
                    for (var role : roles)
                        if (role.getIdLong() == Constants.SUPPORTER_ROLE_ID)
                            isSupporter = true;
                }
            }
        }

        return isSupporter;
    }

    // Background Image
    public String getProfileBgImageUrl() {
        Connection connection = null;
        String url = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT p.image_url " +
                            "FROM users AS u " +
                            "INNER JOIN const_profile_images AS p " +
                            "ON u.profile_bg_id=p.id " +
                            "WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) url = resultSet.getString("image_url");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#getProfileBgImageUrl"));
        } finally {
            closeQuietly(connection);
        }

        if (url == null || url.isEmpty()) {
            try {
                connection = Servant.db.getHikari().getConnection();
                var select = connection.prepareStatement(
                        "SELECT image_url " +
                                "FROM const_profile_images " +
                                "WHERE id=?");
                select.setInt(1, 1);
                var resultSet = select.executeQuery();
                if (resultSet.next()) url = resultSet.getString("image_url");
            } catch (SQLException e) {
                Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#getProfileBgImageUrl"));
            } finally {
                closeQuietly(connection);
            }
        }

        return url;
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
                            "WHERE user_or_guild_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) isBlacklisted = true;
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#isBlacklisted"));
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
                                "VALUES (?)");
                insert.setLong(1, userId);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#setBlacklist"));
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
                                "WHERE user_or_guild_id=?");
                delete.setLong(1, userId);
                delete.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#unsetBlacklist"));
        } finally {
            closeQuietly(connection);
        }
    }

    // Achievement
    public LinkedHashMap<String, Integer> getAchievements() {
        Connection connection = null;
        var achievements = new LinkedHashMap<String, Integer>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT a.name, a.ap " +
                            "FROM user_achievements AS u " +
                            "INNER JOIN const_achievements AS a " +
                            "ON u.achievement_id=a.id " +
                            "WHERE u.user_id=? " +
                            "ORDER BY a.ap DESC");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) {
                do achievements.put(resultSet.getString("name"), resultSet.getInt("ap"));
                while (resultSet.next());
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#getAchievements"));
        } finally {
            closeQuietly(connection);
        }

        return achievements;
    }

    public LinkedList<String> getLevelAchievements() {
        Connection connection = null;
        var achievements = new LinkedList<String>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT a.name " +
                            "FROM user_achievements AS u " +
                            "INNER JOIN const_achievements AS a " +
                            "ON u.achievement_id=a.id " +
                            "WHERE u.user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            while (resultSet.next()) {
                var achievement = resultSet.getString("name");
                if (achievement.startsWith("level")) achievements.add(achievement);
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#getLevelAchievements"));
        } finally {
            closeQuietly(connection);
        }

        return achievements;
    }

    public boolean hasAchievement(String achievement) {
        Connection connection = null;
        var hasAchievement = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT u.id " +
                            "FROM user_achievements AS u " +
                            "INNER JOIN const_achievements AS a " +
                            "ON u.achievement_id=a.id " +
                            "WHERE u.user_id=? " +
                            "AND a.name=?");
            select.setLong(1, userId);
            select.setString(2, achievement.toLowerCase());
            var resultSet = select.executeQuery();
            hasAchievement = resultSet.next();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#hasAchievement"));
        } finally {
            closeQuietly(connection);
        }

        return hasAchievement;
    }

    private int getAchievementIdByName(String name) {
        Connection connection = null;
        var achievementId = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT id " +
                            "FROM const_achievements " +
                            "WHERE name=?");
            select.setString(1, name);
            var resultSet = select.executeQuery();
            if (resultSet.next()) achievementId = resultSet.getInt("id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#getAchievementIdByName"));
        } finally {
            closeQuietly(connection);
        }

        return achievementId;
    }

    public void setAchievement(String achievement) {
        Connection connection = null;

        try {
            if (!hasAchievement(achievement)) {
                connection = Servant.db.getHikari().getConnection();
                var insert = connection.prepareStatement(
                        "INSERT INTO user_achievements (user_id,achievement_id) " +
                                "VALUES (?,?)");
                insert.setLong(1, userId);
                insert.setInt(2, getAchievementIdByName(achievement));
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#setAchievement"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void unsetAchievement(String achievement) {
        Connection connection = null;

        try {
            if (hasAchievement(achievement)) {
                connection = Servant.db.getHikari().getConnection();
                var delete = connection.prepareStatement(
                        "DELETE FROM user_achievements " +
                                "WHERE user_id=? " +
                                "AND achievement_id=?");
                delete.setLong(1, userId);
                delete.setInt(2, getAchievementIdByName(achievement));
                delete.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#unsetAchievement"));
        } finally {
            closeQuietly(connection);
        }
    }

    public int getTotalAP() {
        Connection connection = null;
        var ap = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT a.ap " +
                            "FROM user_achievements AS u " +
                            "INNER JOIN const_achievements AS a " +
                            "ON u.achievement_id=a.id " +
                            "WHERE u.user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) do ap += resultSet.getInt("ap"); while (resultSet.next());
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#getTotalAP"));
        } finally {
            closeQuietly(connection);
        }

        return ap;
    }

    // EXP
    public int getLevelTotalExp(long guildId) {
        Connection connection = null;
        var exp = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT * " +
                            "FROM user_exp " +
                            "WHERE user_id=? " +
                            "AND guild_id=?");
            select.setLong(1, userId);
            select.setLong(2, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) exp = resultSet.getInt("exp");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#getExp"));
        } finally {
            closeQuietly(connection);
        }

        return exp;
    }

    public int getLevel(long guildId) {
        return Parser.getLevelFromExp(getLevelTotalExp(guildId));
    }

    public int getLevelNeededExp(long guildId) {
        var currentLevel = getLevel(guildId);
        return Parser.getLevelExp(currentLevel);
    }

    public int getLevelCurrentExp(long guildId) {
        var exp = getLevelTotalExp(guildId);
        var level = getLevel(guildId);
        return exp - Parser.getTotalLevelExp(level - 1);
    }

    public float getLevelPercent(long guildId) {
        var currentExp = getLevelCurrentExp(guildId);
        var neededExp = getLevelNeededExp(guildId);
        return ((float) currentExp) / neededExp;
    }

    public void setExp(long guildId, int exp) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (getLevelTotalExp(guildId) == 0) {
                var insert = connection.prepareStatement(
                        "INSERT INTO user_exp(user_id,guild_id,exp) " +
                                "VALUES(?,?,?)");
                insert.setLong(1, userId);
                insert.setLong(2, guildId);
                insert.setInt(3, exp);
                insert.executeUpdate();
            } else {
                var currentExp = getLevelTotalExp(guildId);
                exp += currentExp;
                var update = connection.prepareStatement(
                        "UPDATE user_exp " +
                                "SET exp=? " +
                                "WHERE user_id=? " +
                                "AND guild_id=?");
                update.setInt(1, exp);
                update.setLong(2, userId);
                update.setLong(3, guildId);
                update.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#setExp"));
        } finally {
            closeQuietly(connection);
        }
    }

    // Gratulated
    public boolean wasGratulated(long guildId) {
        Connection connection = null;
        var wasGratulated = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT id " +
                            "FROM tmp_birthday_gratulated " +
                            "WHERE user_id=? " +
                            "AND guild_id=?");
            select.setLong(1, userId);
            select.setLong(2, guildId);
            var resultSet = select.executeQuery();
            wasGratulated = resultSet.next();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#wasGratulated"));
        } finally {
            closeQuietly(connection);
        }

        return wasGratulated;
    }

    public void setGratulated(long guildId) {
        Connection connection = null;

        if (!wasGratulated(guildId)) {
            try {
                connection = Servant.db.getHikari().getConnection();
                var insert = connection.prepareStatement(
                        "INSERT INTO tmp_birthday_gratulated (user_id,guild_id) " +
                                "VALUES(?,?)");
                insert.setLong(1, userId);
                insert.setLong(2, guildId);
                insert.executeUpdate();
            } catch (SQLException e) {
                Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#setGratulated"));
            } finally {
                closeQuietly(connection);
            }
        }
    }

    public void unsetGratulated(long guildId) {
        Connection connection = null;

        if (wasGratulated(guildId)) {
            try {
                connection = Servant.db.getHikari().getConnection();
                var delete = connection.prepareStatement(
                        "DELETE FROM tmp_birthday_gratulated " +
                                "WHERE user_id=? " +
                                "AND guild_id=?");
                delete.setLong(1, userId);
                delete.setLong(2, guildId);
                delete.executeUpdate();
            } catch (SQLException e) {
                Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#setGratulated"));
            } finally {
                closeQuietly(connection);
            }
        }
    }

    // Command Counts
    public LinkedHashMap<String, Integer> getCommandCounts() {
        Connection connection = null;
        var commandCounts = new LinkedHashMap<String, Integer>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT c.name, u.count " +
                            "FROM user_command_counts AS u " +
                            "INNER JOIN const_commands AS c " +
                            "ON u.command_id=c.id " +
                            "WHERE u.user_id=? " +
                            "ORDER BY u.count DESC");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            while (resultSet.next()) {
                var command = resultSet.getString("name");
                var myCommand = new MyCommand(jda, command);
                if (!myCommand.isOwnerCommand())
                    commandCounts.put(command, resultSet.getInt("count"));
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#getCommandCounts"));
        } finally {
            closeQuietly(connection);
        }

        return commandCounts;
    }

    public int getCommandsTotalCount() {
        var totalCount = 0;
        var commandCounts = getCommandCounts();
        for (var commandCount : commandCounts.entrySet()) totalCount += commandCount.getValue();
        return totalCount;
    }

    private int getCommandCount(int commandId) {
        Connection connection = null;
        var featureCount = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT count " +
                            "FROM user_command_counts " +
                            "WHERE user_id=? " +
                            "AND command_id=?");
            select.setLong(1, userId);
            select.setInt(2, commandId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) featureCount = resultSet.getInt("count");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#getCommandCount"));
        } finally {
            closeQuietly(connection);
        }

        return featureCount;
    }

    private boolean userCommandCountsHasEntry(int commandId) {
        Connection connection = null;
        var userCommandCountsHasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT id " +
                            "FROM user_command_counts " +
                            "WHERE user_id=? " +
                            "AND command_id=?");
            select.setLong(1, userId);
            select.setInt(2, commandId);
            var resultSet = select.executeQuery();
            userCommandCountsHasEntry = resultSet.next();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#userCommandCountsHasEntry"));
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
            if (userCommandCountsHasEntry(myCommand.getId())) {
                var count = getCommandCount(myCommand.getId());
                var update = connection.prepareStatement(
                        "UPDATE user_command_counts " +
                                "SET count=? " +
                                "WHERE user_id=? " +
                                "AND command_id=?");
                update.setInt(1, count + 1);
                update.setLong(2, userId);
                update.setInt(3, myCommand.getId());
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement(
                        "INSERT INTO user_command_counts (user_id,command_id,count) " +
                                "VALUES (?,?,?)");
                insert.setLong(1, userId);
                insert.setInt(2, myCommand.getId());
                insert.setInt(3, 1);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#incrementCommandCount"));
        } finally {
            closeQuietly(connection);
        }
    }

    public String getFavoriteAnimal(String lang) {
        Connection connection = null;
        String favoriteAnimal = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT c.name, u.count " +
                            "FROM user_command_counts AS u " +
                            "INNER JOIN const_commands AS c " +
                            "ON u.command_id=c.id " +
                            "WHERE u.user_id=? " +
                            "AND ( " +
                            "c.name='bird' " +
                            "OR c.name='cat' " +
                            "OR c.name='dog' " +
                            "OR c.name='fennec' " +
                            "OR c.name='fox' " +
                            "OR c.name='frog' " +
                            "OR c.name='koala' " +
                            "OR c.name='panda' " +
                            "OR c.name='redpanda' " +
                            "OR c.name='sloth' " +
                            "OR c.name='wolf' ) " +
                            "ORDER BY u.count DESC");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) {
                if (resultSet.getInt("count") == 0)
                    favoriteAnimal = LanguageHandler.get(lang, "profile_nofavourite");
                else
                    favoriteAnimal = LanguageHandler.get(lang, "profile_animal_" + resultSet.getString("name"));
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#getFavoriteAnimal"));
        } finally {
            closeQuietly(connection);
        }

        return favoriteAnimal;
    }

    // InteractionCounts
    public ArrayList<Interaction> getInteractionCounts() {
        Connection connection = null;
        var interactions = new ArrayList<Interaction>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT i.name, u.shared, u.received " +
                            "FROM user_interaction_counts AS u " +
                            "INNER JOIN const_interactions AS i " +
                            "ON u.interaction_id=i.id " +
                            "WHERE u.user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.next())
                do interactions.add(new Interaction(
                        resultSet.getString("name"),
                        resultSet.getInt("shared"),
                        resultSet.getInt("received")
                ));
                while (resultSet.next());
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#getInteractionCounts"));
        } finally {
            closeQuietly(connection);
        }

        return interactions;
    }

    public int getInteractionCount(String interaction, boolean isShared) {
        Connection connection = null;
        var commandCount = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT " + (isShared ? "shared" : "received") + " " +
                            "FROM user_interaction_counts AS u " +
                            "INNER JOIN const_interactions AS i " +
                            "ON u.interaction_id=i.id " +
                            "WHERE u.user_id=? " +
                            "AND i.name=?");
            select.setLong(1, userId);
            select.setString(2, interaction.toLowerCase());
            var resultSet = select.executeQuery();
            if (resultSet.next()) commandCount = resultSet.getInt(isShared ? "shared" : "received");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#getInteractionCount"));
        } finally {
            closeQuietly(connection);
        }

        return commandCount;
    }

    public boolean userInteractionCountsHasEntry(String interaction) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT u.shared, u.received " +
                            "FROM user_interaction_counts AS u " +
                            "INNER JOIN const_interactions AS i " +
                            "ON u.interaction_id = i.id " +
                            "WHERE u.user_id=? " +
                            "AND i.name=?");
            select.setLong(1, userId);
            select.setString(2, interaction.toLowerCase());
            var resultSet = select.executeQuery();
            if (resultSet.next())
                if (resultSet.getInt("shared") != 0 || resultSet.getInt("received") != 0)
                    hasEntry = true;
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#userInteractionCountsHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    private int getInteractionIdByName(String name) {
        Connection connection = null;
        var interactionId = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT id " +
                            "FROM const_interactions " +
                            "WHERE name=?");
            select.setString(1, name);
            var resultSet = select.executeQuery();
            if (resultSet.next()) interactionId = resultSet.getInt("id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#getInteractionIdByName"));
        } finally {
            closeQuietly(connection);
        }

        return interactionId;
    }

    public void incrementInteractionCount(String interaction, boolean isShared) {
        Connection connection = null;
        var count = getInteractionCount(interaction, isShared);
        var interactionId = getInteractionIdByName(interaction);

        try {
            connection = Servant.db.getHikari().getConnection();
            if (userInteractionCountsHasEntry(interaction)) {
                var update = connection.prepareStatement(
                        "UPDATE user_interaction_counts " +
                                "SET " + (isShared ? "shared" : "received") + "=? " +
                                "WHERE user_id=? " +
                                "AND interaction_id=?");
                update.setInt(1, count + 1);
                update.setLong(2, userId);
                update.setInt(3, interactionId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement(
                        "INSERT INTO user_interaction_counts (user_id,interaction_id,shared,received) " +
                                "VALUES (?,?,?,?)");
                insert.setLong(1, userId);
                insert.setInt(2, interactionId);
                if (isShared) {
                    insert.setInt(3, 1);
                    insert.setInt(4, 0);
                } else {
                    insert.setInt(3, 0);
                    insert.setInt(4, 1);
                }
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#incrementInteractionCount"));
        } finally {
            closeQuietly(connection);
        }
    }

    // Baguette
    public Baguette getBaguette() {
        Connection connection = null;
        Baguette baguette = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT size, counter " +
                            "FROM user_baguettes " +
                            "WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) baguette = new Baguette(resultSet.getInt("size"), resultSet.getInt("counter"));
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#getBaguette"));
        } finally {
            closeQuietly(connection);
        }

        return baguette;
    }

    private boolean userBaguettesHasEntry() {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT user_id " +
                            "FROM user_baguettes " +
                            "WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.next();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#userBaguettesHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public void setBaguette(int baguetteSize, int sizeCounter) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (userBaguettesHasEntry()) {
                var update = connection.prepareStatement(
                        "UPDATE user_baguettes " +
                                "SET size=?, counter=? " +
                                "WHERE user_id=?");
                update.setInt(1, baguetteSize);
                update.setInt(2, sizeCounter);
                update.setLong(3, userId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement(
                        "INSERT INTO user_baguettes (user_id,size,counter) " +
                                "VALUES (?,?,?)");
                insert.setLong(1, userId);
                insert.setInt(2, baguetteSize);
                insert.setInt(3, sizeCounter);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#setBaguette"));
        } finally {
            closeQuietly(connection);
        }
    }

    // Livestream
    public boolean isStreamer(Guild guild) {
        var member = guild.getMemberById(userId);
        if (member != null) {
            var memberRoles = member.getRoles();
            var streamerRoleIds = new MyGuild(guild).getStreamerRoleIds();
            for (var memberRole : memberRoles) if (streamerRoleIds.contains(memberRole.getIdLong())) return true;
        }

        return false;
    }

    // TicTacToe
    public TicTacToeStatistic getTttStatistic() {
        Connection connection = null;
        TicTacToeStatistic tttStatistics = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT wins, draws, loses " +
                            "FROM user_ttt_statistics " +
                            "WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.next())
                tttStatistics = new TicTacToeStatistic(userId, resultSet.getInt("wins"), resultSet.getInt("draws"), resultSet.getInt("loses"));
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#getTttStatistic"));
        } finally {
            closeQuietly(connection);
        }

        return tttStatistics;
    }

    private boolean tttStatisticsHasEntry() {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT user_id " +
                            "FROM user_ttt_statistics " +
                            "WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.next();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#tttStatisticsHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public void incrementTttWin() {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (tttStatisticsHasEntry()) {
                var update = connection.prepareStatement(
                        "UPDATE user_ttt_statistics " +
                                "SET wins=? " +
                                "WHERE user_id=?");
                update.setInt(1, getTttStatistic().getWins() + 1);
                update.setLong(2, userId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement(
                        "INSERT INTO user_ttt_statistics (user_id,wins,draws,loses) " +
                                "VALUES (?,?,?,?)");
                insert.setLong(1, userId);
                insert.setInt(2, 1);
                insert.setInt(3, 0);
                insert.setInt(4, 0);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#incrementTttWin"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void incrementTttDraw() {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (tttStatisticsHasEntry()) {
                var update = connection.prepareStatement(
                        "UPDATE user_ttt_statistics " +
                                "SET draws=? " +
                                "WHERE user_id=?");
                update.setInt(1, getTttStatistic().getDraws() + 1);
                update.setLong(2, userId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement(
                        "INSERT INTO user_ttt_statistics (user_id,wins,draws,loses) " +
                                "VALUES (?,?,?,?)");
                insert.setLong(1, userId);
                insert.setInt(2, 0);
                insert.setInt(3, 1);
                insert.setInt(4, 0);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#incrementTttDraw"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void incrementTttLose() {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (tttStatisticsHasEntry()) {
                var update = connection.prepareStatement(
                        "UPDATE user_ttt_statistics " +
                                "SET loses=? " +
                                "WHERE user_id=?");
                update.setInt(1, getTttStatistic().getLoses() + 1);
                update.setLong(2, userId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement(
                        "INSERT INTO user_ttt_statistics (user_id,wins,draws,loses) " +
                                "VALUES (?,?,?,?)");
                insert.setLong(1, userId);
                insert.setInt(2, 0);
                insert.setInt(3, 0);
                insert.setInt(4, 1);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyUser#incrementTttLose"));
        } finally {
            closeQuietly(connection);
        }
    }
}
