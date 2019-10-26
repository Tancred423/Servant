// Author: Tancred423 (https://github.com/Tancred423)
package owner.blacklist;

public class Blacklist {
//    public static boolean isBlacklisted(User user, Guild guild) {
//        var isBlacklisted = false;
//        try {
//            if (await(isBlacklistedAsync(user.getIdLong()))) isBlacklisted = true;
//            if (await(isBlacklistedAsync(guild.getIdLong()))) isBlacklisted = true;
//        } catch (SQLException e) {
//            new Log(e, guild, user, "blacklist", null).sendLog(false);
//        }
//        return isBlacklisted;
//    }
//
//    static CompletableFuture<Boolean> isBlacklistedAsync(long id) throws SQLException {
//        var connection = Database.getConnection();
//        var select = connection.prepareStatement("SELECT * FROM blacklist WHERE id=?");
//        select.setLong(1, id);
//        var resultSet = select.executeQuery();
//        var isBlacklisted = completedFuture(false);
//        if (resultSet.first()) isBlacklisted = completedFuture(true);
//        connection.close();
//        return isBlacklisted;
//    }
//
//    static CompletableFuture<List<Long>> getBlacklistedIdsAsync() throws SQLException {
//        var connection = Database.getConnection();
//        var select = connection.prepareStatement("SELECT * FROM blacklist");
//        var resultSet = select.executeQuery();
//        List<Long> blacklistedIds = new ArrayList<>();
//        if (resultSet.first())
//            do blacklistedIds.add(resultSet.getLong("id")); while (resultSet.next());
//        connection.close();
//        return completedFuture(blacklistedIds);
//    }
//
//    static CompletableFuture<Void> setBlacklistAsync(long id, CommandEvent event) {
//        return CompletableFuture.runAsync(() -> {
//            try {
//                if (!await(isBlacklistedAsync(id))) {
//                    var connection = Database.getConnection();
//                    var insert = connection.prepareStatement("INSERT INTO blacklist (id) VALUES (?)");
//                    insert.setLong(1, id);
//                    insert.executeUpdate();
//                    connection.close();
//                }
//            } catch (SQLException e) {
//                new Log(e, event.getGuild(), event.getAuthor(), "blacklist", event).sendLog(true);
//            }
//        });
//    }
//
//    static CompletableFuture<Void> unsetBlacklistAsync(long id, CommandEvent event) {
//        return CompletableFuture.runAsync(() -> {
//            try {
//                if (await(isBlacklistedAsync(id))) {
//                    var connection = Database.getConnection();
//                    var delete = connection.prepareStatement("DELETE FROM blacklist WHERE id=?");
//                    delete.setLong(1, id);
//                    delete.executeUpdate();
//                    connection.close();
//                }
//            } catch (SQLException e) {
//                new Log(e, event.getGuild(), event.getAuthor(), "blacklist", event).sendLog(true);
//            }
//        });
//    }
}
