// Author: Tancred423 (https://github.com/Tancred423)
package fun;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import moderation.guild.Guild;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import owner.blacklist.Blacklist;
import servant.Log;
import servant.Servant;
import utilities.Constants;
import utilities.MessageHandler;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class LoveCommand extends Command {
    public LoveCommand() {
        this.name = "love";
        this.aliases = new String[]{"ship"};
        this.help = "I ship it!";
        this.category = new Command.Category("Fun");
        this.arguments = "@user1 @user2";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, name)) return;
            if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

            var message = event.getMessage();
            List<Member> mentioned = message.getMentionedMembers();
            var lang = LanguageHandler.getLanguage(event, name);
            var p = GuildHandler.getPrefix(event, name);

            if (mentioned.size() < 1) {
                try {
                    var description = LanguageHandler.get(lang, "love_description");
                    var usage = String.format(LanguageHandler.get(lang, "love_usage"), p, name, p, name);
                    var hint = LanguageHandler.get(lang, "love_hint");
                    event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                }
                return;
            }

            Member first;
            Member second;
            var isSelfLove = false;

            if (mentioned.size() > 1) {
                first = mentioned.get(0);
                second = mentioned.get(1);
            } else {
                var splitContentRaw = event.getArgs().split(" ");

                if (splitContentRaw.length > 1) {
                    if (splitContentRaw[0].trim().equals(splitContentRaw[1].trim())) {
                        first = mentioned.get(0);
                        second = mentioned.get(0);
                        isSelfLove = true;
                    } else {
                        first = event.getGuild().getMemberById(event.getAuthor().getIdLong());
                        second = mentioned.get(0);
                    }
                } else {
                    first = event.getGuild().getMemberById(event.getAuthor().getIdLong());
                    second = mentioned.get(0);
                }
            }

            var author = event.getAuthor();
            var internalAuthor = new User(author.getIdLong());

            var love = ThreadLocalRandom.current().nextInt(0, 101);
            if ((first.getUser().getId().equals(Servant.config.getBotOwnerId()) && second.getUser() == event.getJDA().getSelfUser()
                    || (first.getUser() == event.getJDA().getSelfUser() && second.getUser().getId().equals(Servant.config.getBotOwnerId()))))
                love = 100;
            var bar = getBar(love);
            var quote = getQuote(love, isSelfLove, lang);
            var shippingName = getShippingName(first, second);

            try {
                checkLoveAchievements(internalAuthor, message, love);

                new MessageHandler().sendEmbed(event.getChannel(),
                        internalAuthor.getColor(),
                        quote,
                        null,
                        event.getJDA().getSelfUser().getAvatarUrl(),
                        null,
                        "https://i.imgur.com/BaeIVWa.png", // :tancLove:
                        first.getAsMention() + "♥" + second.getAsMention() + "\n" +
                                "\n" +
                                bar,
                        null,
                        null,
                        shippingName,
                        "https://i.imgur.com/JAKcV8F.png"
                );
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            }

            // Statistics.
            try {
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
                if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
            }
        });
    }

    private String getBar(int love) {
        if        (love >= 100) {
            return "▮▮▮▮▮▮▮▮▮▮ " + love + "%";
        } else if (love >=  90) {
            return "▮▮▮▮▮▮▮▮▮▯ " + love + "%";
        } else if (love >=  80) {
            return "▮▮▮▮▮▮▮▮▯▯ " + love + "%";
        } else if (love >=  70) {
            return "▮▮▮▮▮▮▮▯▯▯ " + love + "%";
        } else if (love >=  60) {
            return "▮▮▮▮▮▮▯▯▯▯ " + love + "%";
        } else if (love >=  50) {
            return "▮▮▮▮▮▯▯▯▯▯ " + love + "%";
        } else if (love >=  40) {
            return "▮▮▮▮▯▯▯▯▯▯ " + love + "%";
        } else if (love >=  30) {
            return "▮▮▮▯▯▯▯▯▯▯ " + love + "%";
        } else if (love >=  20) {
            return "▮▮▯▯▯▯▯▯▯▯ " + love + "%";
        } else if (love >=  10) {
            return "▮▯▯▯▯▯▯▯▯▯ " + love + "%";
        } else if (love >=   0) {
            return "▯▯▯▯▯▯▯▯▯▯ " + love + "%";
        } else return "▯▯▯▯▯▯▯▯▯▯ error";
    }

    private String getQuote(int love, boolean isSelfLove, String lang) {
        if (isSelfLove) {
            if (love >= 100) return LanguageHandler.get(lang, "love_self_100");
            else if (love >= 90) return LanguageHandler.get(lang, "love_self_90");
            else if (love >= 80) return LanguageHandler.get(lang, "love_self_80");
            else if (love >= 70) return LanguageHandler.get(lang, "love_self_70");
            else if (love == 69) return LanguageHandler.get(lang, "love_self_69");
            else if (love >= 60) return LanguageHandler.get(lang, "love_self_60");
            else if (love >= 50) return LanguageHandler.get(lang, "love_self_50");
            else if (love == 42) return LanguageHandler.get(lang, "love_self_42");
            else if (love >= 40) return LanguageHandler.get(lang, "love_self_40");
            else if (love >= 30) return LanguageHandler.get(lang, "love_self_30");
            else if (love >= 20) return LanguageHandler.get(lang, "love_self_20");
            else if (love >= 10) return LanguageHandler.get(lang, "love_self_10");
            else if (love >= 0) return LanguageHandler.get(lang, "love_self_0");
            else return LanguageHandler.get(lang, "love_fallback");
        } else {
            if (love >= 100) return LanguageHandler.get(lang, "love_noself_100");
            else if (love >= 90) return LanguageHandler.get(lang, "love_noself_90");
            else if (love >= 80) return LanguageHandler.get(lang, "love_noself_80");
            else if (love >= 70) return LanguageHandler.get(lang, "love_noself_70");
            else if (love == 69) return LanguageHandler.get(lang, "love_noself_69");
            else if (love >= 60) return LanguageHandler.get(lang, "love_noself_60");
            else if (love >= 50) return LanguageHandler.get(lang, "love_noself_50");
            else if (love == 42) return LanguageHandler.get(lang, "love_noself_42");
            else if (love >= 40) return LanguageHandler.get(lang, "love_noself_40");
            else if (love >= 30) return LanguageHandler.get(lang, "love_noself_30");
            else if (love >= 20) return LanguageHandler.get(lang, "love_noself_20");
            else if (love >= 10) return LanguageHandler.get(lang, "love_noself_10");
            else if (love >= 0) return LanguageHandler.get(lang, "love_noself_0");
            else return LanguageHandler.get(lang, "love_fallback");
        }
    }

    private String getShippingName(Member first, Member second) {
        String firstName = first.getEffectiveName();
        String secondName = second.getEffectiveName();
        return firstName.substring(0, 1).toUpperCase() + firstName.substring(1, firstName.length() / 2).toLowerCase()
                + secondName.substring(secondName.length() / 2).toLowerCase();
    }

    private void checkLoveAchievements(User internalAuthor, Message message, int love) throws SQLException {
        if (love == 69) {
            if (!internalAuthor.hasAchievement("love69")) {
                internalAuthor.setAchievement("love69", 69);
                new MessageHandler().reactAchievement(message);
            }
        } else if (love == 42) {
            if (!internalAuthor.hasAchievement("love42")) {
                internalAuthor.setAchievement("love42", 42);
                new MessageHandler().reactAchievement(message);
            }
        }
    }
}
