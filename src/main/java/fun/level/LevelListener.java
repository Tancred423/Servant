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
import owner.blacklist.Blacklist;
import servant.Servant;
import utilities.MessageHandler;
import utilities.Parser;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class LevelListener extends ListenerAdapter {
    private static Map<Guild, Map<User, ZonedDateTime>> guildCds = new HashMap<>();

    private static int getLevel(long userId, long guildId, Guild guild, User user) {
        return Parser.getLevelFromExp(new moderation.user.User(userId).getExp(guildId, guild, user));
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        CompletableFuture.runAsync(() -> {
            var author = event.getAuthor();
            var guild = event.getGuild();

            if (author.isBot()) return;
            if (!Toggle.isEnabled(event, "level")) return;
            if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

            var lang = new moderation.guild.Guild(event.getGuild().getIdLong()).getLanguage(guild, author);

            var userCd = guildCds.get(guild);

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

            var currentLevel = getLevel(authorId, guildId, guild, author);
            var randomExp = ThreadLocalRandom.current().nextInt(15, 26); // Between 15 and 25 inclusively.
            new moderation.user.User(authorId).setExp(guildId, randomExp, guild, author);
            var updatedLevel = getLevel(authorId, guildId, guild, author);

            if (updatedLevel > currentLevel) {
                checkForAchievements(updatedLevel, event);
                var sb = new StringBuilder();
                var roles = checkForNewRole(updatedLevel, event, lang);
                if (!roles.isEmpty()) for (var roleName : roles) sb.append(roleName).append("\n");

                if (event.getGuild().getMemberById(event.getJDA().getSelfUser().getIdLong()).hasPermission(Permission.MESSAGE_EMBED_LINKS)) {
                    var eb = new EmbedBuilder();
                    eb.setColor(new moderation.user.User(authorId).getColor(guild, author));
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
            }
        });
    }

    private List<String> checkForNewRole(int level, GuildMessageReceivedEvent event, String lang) {
        var guild = event.getGuild();
        var internalGuild = new moderation.guild.Guild(guild.getIdLong());
        var roleIds = internalGuild.getLevelRole(level, event.getGuild(), event.getAuthor());
        var roles = new ArrayList<String>();
        for (var roleId : roleIds)
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

    private void checkForAchievements(int level, GuildMessageReceivedEvent event) {
        var guild = event.getGuild();
        var author = event.getAuthor();
        var internalAuthor = new moderation.user.User(author.getIdLong());
        var message = event.getMessage();

        if (level >= 10) {
            if (!internalAuthor.hasAchievement("level10", guild, author) && !hasHigherLevelAchievement(internalAuthor, level, guild, author)) {
                internalAuthor.setAchievement("level10", 10, guild, author);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 20) {
            if (!internalAuthor.hasAchievement("level20", guild, author) && !hasHigherLevelAchievement(internalAuthor, level, guild, author)) {
                internalAuthor.setAchievement("level20", 20, guild, author);
                internalAuthor.unsetAchievement("level10", guild, author);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 30) {
            if (!internalAuthor.hasAchievement("level30", guild, author) && !hasHigherLevelAchievement(internalAuthor, level, guild, author)) {
                internalAuthor.setAchievement("level30", 30, guild, author);
                internalAuthor.unsetAchievement("level20", guild, author);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 40) {
            if (!internalAuthor.hasAchievement("level40", guild, author) && !hasHigherLevelAchievement(internalAuthor, level, guild, author)) {
                internalAuthor.setAchievement("level40", 40, guild, author);
                internalAuthor.unsetAchievement("level30", guild, author);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 50) {
            if (!internalAuthor.hasAchievement("level50", guild, author) && !hasHigherLevelAchievement(internalAuthor, level, guild, author)) {
                internalAuthor.setAchievement("level50", 50, guild, author);
                internalAuthor.unsetAchievement("level40", guild, author);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 60) {
            if (!internalAuthor.hasAchievement("level60", guild, author) && !hasHigherLevelAchievement(internalAuthor, level, guild, author)) {
                internalAuthor.setAchievement("level60", 60, guild, author);
                internalAuthor.unsetAchievement("level50", guild, author);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 69) {
            if (!internalAuthor.hasAchievement("level69", guild, author) && !hasHigherLevelAchievement(internalAuthor, level, guild, author)) {
                internalAuthor.setAchievement("level69", 69, guild, author);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 70) {
            if (!internalAuthor.hasAchievement("level70", guild, author) && !hasHigherLevelAchievement(internalAuthor, level, guild, author)) {
                internalAuthor.setAchievement("level70", 70, guild, author);
                internalAuthor.unsetAchievement("level60", guild, author);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 80) {
            if (!internalAuthor.hasAchievement("level80", guild, author) && !hasHigherLevelAchievement(internalAuthor, level, guild, author)) {
                internalAuthor.setAchievement("level80", 80, guild, author);
                internalAuthor.unsetAchievement("level70", guild, author);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 90) {
            if (!internalAuthor.hasAchievement("level90", guild, author) && !hasHigherLevelAchievement(internalAuthor, level, guild, author)) {
                internalAuthor.setAchievement("level90", 90, guild, author);
                internalAuthor.unsetAchievement("level80", guild, author);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 100) {
            if (!internalAuthor.hasAchievement("level100", guild, author) && !hasHigherLevelAchievement(internalAuthor, level, guild, author)) {
                internalAuthor.setAchievement("level100", 100, guild, author);
                internalAuthor.unsetAchievement("level90", guild, author);
                new MessageHandler().reactAchievement(message);
            }
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean hasHigherLevelAchievement(moderation.user.User internalUser, int level, Guild guild, User user) {
        var achievements = internalUser.getLevelAchievements(guild, user);
        String highestLevelAchievement = null;
        for (var achievement : achievements) {
            highestLevelAchievement = achievement;
            if (!highestLevelAchievement.equals("level69")) break;
        }

        if (highestLevelAchievement != null) {
            int highestLevel = Integer.parseInt(highestLevelAchievement.substring(5));
            return level > highestLevel;
        } else return false;
    }
}
