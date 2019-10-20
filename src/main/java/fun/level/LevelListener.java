// Author: Tancred423 (https://github.com/Tancred423)
package fun.level;

import files.language.LanguageHandler;
import moderation.toggle.Toggle;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import servant.Log;
import servant.Servant;
import utilities.MessageHandler;
import utilities.Parser;

import java.awt.*;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class LevelListener extends ListenerAdapter {
    private static Map<Guild, Map<User, ZonedDateTime>> guildCds = new HashMap<>();

    private static int getLevel(long userId, long guildId) throws SQLException {
        return Parser.getLevelFromExp(new moderation.user.User(userId).getExp(guildId));
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        var author = event.getAuthor();
        var guild = event.getGuild();

        if (author.isBot()) return;

        if (!Toggle.isEnabled(event, "level")) return;

        String lang;
        try {
            lang = new moderation.guild.Guild(event.getGuild().getIdLong()).getLanguage();
        } catch (SQLException e) {
            lang = Servant.config.getDefaultLanguage();
        }

        Map<User, ZonedDateTime> userCd = guildCds.get(guild);

        if (userCd != null) {
            var lastMessage = userCd.get(author);
            if (lastMessage != null) {
                // Check if last message is older than the exp cooldown.
                long difference = Parser.getTimeDifferenceInMillis(lastMessage, ZonedDateTime.now(ZoneOffset.UTC));
                long expCooldown = Integer.parseInt(Servant.config.getExpCdMillis());
                if (difference <= expCooldown) return;
            }
        } else {
            userCd = new HashMap<>();
        }

        userCd.put(author, ZonedDateTime.now(ZoneOffset.UTC));
        guildCds.put(guild, userCd);

        var authorId = author.getIdLong();
        var guildId = guild.getIdLong();

        int currentLevel;
        try {
            currentLevel = getLevel(authorId, guildId);
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), "level", null).sendLog(true);
            return;
        }
        var randomExp = ThreadLocalRandom.current().nextInt(15, 26); // Between 15 and 25 inclusively.
        try {
            new moderation.user.User(authorId).addExp(guildId, randomExp);
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), "level", null).sendLog(true);
            return;
        }
        int updatedLevel;
        try {
            updatedLevel = getLevel(authorId, guildId);
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), "level", null).sendLog(true);
            return;
        }

        if (updatedLevel > currentLevel) {
            try {
                checkForAchievements(updatedLevel, event);
                var sb = new StringBuilder();
                List<String> roles = checkForNewRole(updatedLevel, event, lang);
                if (!roles.isEmpty()) for (var roleName : roles) sb.append(roleName).append("\n");

                if (event.getGuild().getMemberById(event.getJDA().getSelfUser().getIdLong()).hasPermission(Permission.MESSAGE_EMBED_LINKS)) {
                    var eb = new EmbedBuilder();
                    try {
                        eb.setColor(new moderation.user.User(authorId).getColor());
                    } catch (SQLException ex) {
                        eb.setColor(Color.decode(Servant.config.getDefaultColorCode()));
                    }
                    eb.setAuthor(LanguageHandler.get(lang, "levelrole_levelup"), null, null);
                    eb.setThumbnail(author.getEffectiveAvatarUrl());
                    eb.setDescription(String.format(LanguageHandler.get(lang, "level_up"), author.getAsMention(), updatedLevel));
                    if (!roles.isEmpty()) eb.addField(roles.size() == 1 ?
                            LanguageHandler.get(lang, "levelrole_role_singular") :
                            LanguageHandler.get(lang, "levelrole_role_plural"), sb.toString(), false);
                    event.getChannel().sendMessage(eb.build()).queue();
                } else {
                    var mb = new StringBuilder();
                    mb.append("**").append(LanguageHandler.get(lang, "levelrole_levelup")).append("**\n");
                    mb.append(String.format(LanguageHandler.get(lang, "level_up"), author.getAsMention(), updatedLevel)).append("**\n");
                    if (!roles.isEmpty()) {
                        mb.append(roles.size() == 1 ?
                                LanguageHandler.get(lang, "levelrole_role_singular") :
                                LanguageHandler.get(lang, "levelrole_role_plural")).append("\n");
                        mb.append(sb.toString()).append("\n");
                    }
                    mb.append("_").append(LanguageHandler.get(lang, "level_missingpermission_embed")).append("_");
                    event.getChannel().sendMessage(mb.toString()).queue();
                }
            } catch (SQLException e) {
                new Log(e, guild, author, "level", null).sendLog(false);
            }
        }
    }

    private List<String> checkForNewRole(int level, GuildMessageReceivedEvent event, String lang) throws SQLException {
        var guild = event.getGuild();
        var internalGuild = new moderation.guild.Guild(guild.getIdLong());
        var roleIds = internalGuild.getLevelRole(level);
        List<String> roles = new ArrayList<>();
        for (Long roleId : roleIds)
            if (guild.getRoleById(roleId) != null
                    && event.getGuild().getMemberById(event.getJDA().getSelfUser().getIdLong()).hasPermission(Permission.MANAGE_ROLES)) {
                try {
                    guild.getController().addSingleRoleToMember(event.getMember(), guild.getRoleById(roleId)).queue();
                    roles.add(guild.getRoleById(roleId).getName());
                } catch (HierarchyException e) {
                    roles.add(String.format(LanguageHandler.get(lang, "level_hierarchy"), guild.getRoleById(roleId).getName()));
                }
            }
        return roles;
    }

    private void checkForAchievements(int level, GuildMessageReceivedEvent event) throws SQLException {
        var author = event.getAuthor();
        var internalAuthor = new moderation.user.User(author.getIdLong());
        var message = event.getMessage();

        if (level >= 10) {
            if (!internalAuthor.hasAchievement("level10")) {
                internalAuthor.setAchievement("level10", 10);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 20) {
            if (!internalAuthor.hasAchievement("level20")) {
                internalAuthor.setAchievement("level20", 20);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 30) {
            if (!internalAuthor.hasAchievement("level30")) {
                internalAuthor.setAchievement("level30", 30);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 40) {
            if (!internalAuthor.hasAchievement("level40")) {
                internalAuthor.setAchievement("level40", 40);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 50) {
            if (!internalAuthor.hasAchievement("level50")) {
                internalAuthor.setAchievement("level50", 50);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 60) {
            if (!internalAuthor.hasAchievement("level60")) {
                internalAuthor.setAchievement("level60", 60);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 69) {
            if (!internalAuthor.hasAchievement("level69")) {
                internalAuthor.setAchievement("level69", 69);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 70) {
            if (!internalAuthor.hasAchievement("level70")) {
                internalAuthor.setAchievement("level70", 70);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 80) {
            if (!internalAuthor.hasAchievement("level80")) {
                internalAuthor.setAchievement("level80", 80);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 90) {
            if (!internalAuthor.hasAchievement("level90")) {
                internalAuthor.setAchievement("level90", 90);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 100) {
            if (!internalAuthor.hasAchievement("level100")) {
                internalAuthor.setAchievement("level100", 100);
                new MessageHandler().reactAchievement(message);
            }
        }
    }
}
