// Author: Tancred423 (https://github.com/Tancred423)
package commands.fun.love;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import servant.MyGuild;
import servant.MyUser;
import utilities.Constants;
import utilities.ImageUtil;
import utilities.MathUtil;
import utilities.MessageUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.awt.*;

public class LoveCommand extends Command {
    public LoveCommand() {
        this.name = "love";
        this.aliases = new String[] { "ship" };
        this.help = "Ship two people, or just one with themselves";
        this.category = new Command.Category("Fun");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.modCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
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
        var p = new MyGuild(event.getGuild()).getPrefix();

        if (mentioned.size() < 1) {
            var description = LanguageHandler.get(lang, "love_description");
            var usage = String.format(LanguageHandler.get(lang, "love_usage"), p, name, p, name);
            event.reply(MessageUtil.createUsageEmbed(name, event.getAuthor(), lang, description, aliases, usage, null));
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
        var myUser = new MyUser(author);

        var love = MathUtil.randomBiased(100, 0.5f); // 1 is linear, the lower the rarer the lower numbers

        if (first == null) return;
        var bar = getBar(love);
        var quote = getQuote(love, isSelfLove, lang);
        var shippingName = getShippingName(first, second);

        checkLoveAchievements(myUser, message, love);

        var jda = event.getJDA();

        event.getChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.decode(myUser.getColorCode()))
                        .setAuthor(quote, null,  event.getJDA().getSelfUser().getAvatarUrl())
                        .setThumbnail(ImageUtil.getUrl(jda, "love"))
                        .setDescription(first.getAsMention() + "♥" + second.getAsMention() + "\n" +
                                "\n" +
                                bar)
                        .setFooter(shippingName, ImageUtil.getUrl(jda, "ship_rings"))
                        .build()
        ).queue();
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

    private void checkLoveAchievements(MyUser myUser, Message message, int love) {
        if (love == 69) {
            if (!myUser.hasAchievement("love69") && new MyGuild(message.getGuild()).featureIsEnabled("achievements")) {
                myUser.setAchievement("love69");
                new MessageUtil().reactAchievement(message);
            }
        } else if (love == 42) {
            if (!myUser.hasAchievement("love42") && new MyGuild(message.getGuild()).featureIsEnabled("achievements")) {
                myUser.setAchievement("love42");
                new MessageUtil().reactAchievement(message);
            }
        }
    }
}
