// Author: Tancred423 (https://github.com/Tancred423)
package fun;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.user.Master;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import utilities.Constants;
import utilities.MessageUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.concurrent.ThreadLocalRandom;

public class LoveCommand extends Command {
    public LoveCommand() {
        this.name = "love";
        this.aliases = new String[] { "ship" };
        this.help = "I ship it!";
        this.category = new Command.Category("Fun");
        this.arguments = "@user1 @user2";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var message = event.getMessage();
        var mentioned = message.getMentionedMembers();
        var lang = LanguageHandler.getLanguage(event);
        var p = GuildHandler.getPrefix(event);

        if (mentioned.size() < 1) {
            var description = LanguageHandler.get(lang, "love_description");
            var usage = String.format(LanguageHandler.get(lang, "love_usage"), p, name, p, name);
            var hint = LanguageHandler.get(lang, "love_hint");
            event.reply(MessageUtil.createUsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint));
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
                if (splitContentRaw[0].trim().equals(splitContentRaw[1].trim())) first = mentioned.get(0);
                else first = event.getGuild().getMemberById(event.getAuthor().getIdLong());
            } else first = event.getGuild().getMemberById(event.getAuthor().getIdLong());
            second = mentioned.get(0);
        }

        if (first == second) isSelfLove = true;

        var author = event.getAuthor();
        var master = new Master(author);

        var love = ThreadLocalRandom.current().nextInt(0, 101);
        if (first == null) return;
        var bar = getBar(love);
        var quote = getQuote(love, isSelfLove, lang);
        var shippingName = getShippingName(first, second);

        checkLoveAchievements(master, message, love);

        new MessageUtil().sendEmbed(event.getChannel(),
                master.getColor(),
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

    private void checkLoveAchievements(Master master, Message message, int love) {
        if (love == 69) {
            if (!master.hasAchievement("love69")) {
                master.setAchievement("love69", 69);
                new MessageUtil().reactAchievement(message);
            }
        } else if (love == 42) {
            if (!master.hasAchievement("love42")) {
                master.setAchievement("love42", 42);
                new MessageUtil().reactAchievement(message);
            }
        }
    }
}
