// Author: Tancred423 (https://github.com/Tancred423)
package servant.supporter;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import servant.MyGuild;
import utilities.ImageUtil;

import java.time.LocalDateTime;

public class SupporterHandler {
    public static void sendSupporterAlert(GuildMemberRoleAddEvent event, String rank) {
        var eb = new EmbedBuilder();
        var supporter = event.getMember().getUser();
        var lang = new MyGuild(event.getGuild()).getLanguageCode();
        var jda = event.getJDA();

        eb.setColor(event.getRoles().get(0).getColor());
        eb.setAuthor(supporter.getName() + "#" + supporter.getDiscriminator(), null, supporter.getAvatarUrl());
        switch (rank) {
            case "donation":
                eb.setDescription(String.format(LanguageHandler.get(lang, "supporter_dono"), supporter.getAsMention()));
                eb.setThumbnail(ImageUtil.getUrl(jda, "paypal_logo"));
                break;
            case "patreon":
                eb.setDescription(String.format(LanguageHandler.get(lang, "supporter_patron"), supporter.getAsMention()));
                eb.setThumbnail(ImageUtil.getUrl(jda, "patreon_logo"));
                break;
            case "boost":
                eb.setDescription(String.format(LanguageHandler.get(lang, "supporter_boost"), supporter.getAsMention()));
                eb.setThumbnail(ImageUtil.getUrl(jda, "boost_logo"));
                break;
            default:
                return;
        }

        eb.setTimestamp(LocalDateTime.now());

        var guild = event.getJDA().getGuildById(436925371577925642L);
        if (guild != null) {
            var tc = guild.getTextChannelById(502477863757545472L);
            if (tc != null) tc.sendMessage(eb.build()).queue();
        }
    }
}
