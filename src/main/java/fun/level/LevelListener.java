// Author: Tancred423 (https://github.com/Tancred423)
package fun.level;

import files.language.LanguageHandler;
import moderation.toggle.Toggle;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import owner.blacklist.Blacklist;
import servant.Servant;
import utilities.Parser;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class LevelListener extends ListenerAdapter {
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.isFromGuild()) {
            if (event.getGuild().getIdLong() == 264445053596991498L) return; // Discord Bot List
            CompletableFuture.runAsync(() -> {
                var author = event.getAuthor();
                var guild = event.getGuild();

                if (author.isBot()) return;
                if (!Toggle.isEnabled(event, "level")) return;
                if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

                var lang = new moderation.guild.Guild(event.getGuild().getIdLong()).getLanguage(guild, author);

                var userCd = Level.guildCds.get(guild);

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
                Level.guildCds.put(guild, userCd);

                var authorId = author.getIdLong();
                var guildId = guild.getIdLong();

                var currentLevel = Level.getLevel(authorId, guildId, guild, author);
                var randomExp = ThreadLocalRandom.current().nextInt(15, 26); // Between 15 and 25 inclusively.
                new moderation.user.User(authorId).setExp(guildId, randomExp, guild, author);
                var updatedLevel = Level.getLevel(authorId, guildId, guild, author);

                if (updatedLevel > currentLevel) {
                    Level.checkForAchievements(updatedLevel, event);
                    var sb = new StringBuilder();
                    var roles = Level.checkForNewRole(updatedLevel, event, lang);
                    if (!roles.isEmpty()) for (var roleName : roles) sb.append(roleName).append("\n");
                    var selfMember = event.getGuild().getMemberById(event.getJDA().getSelfUser().getIdLong());

                    if (selfMember != null && selfMember.hasPermission(Permission.MESSAGE_EMBED_LINKS)) {
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
            }, Servant.threadPool);
        }
    }
}
