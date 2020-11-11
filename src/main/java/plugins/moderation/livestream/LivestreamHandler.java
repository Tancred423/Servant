package plugins.moderation.livestream;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import servant.MyGuild;
import servant.MyUser;
import utilities.ImageUtil;

import java.util.ArrayList;

public class LivestreamHandler {
    public static ArrayList<Livestreamer> activeStreamers;

    public static void addRole(net.dv8tion.jda.api.entities.Guild guild, Member member, Role role) {
        var selfMember = guild.getMemberById(guild.getJDA().getSelfUser().getIdLong());
        if (selfMember == null) return;

        if (role != null && selfMember.canInteract(member)) {
            var rolesToAdd = new ArrayList<Role>();
            rolesToAdd.add(role);
            guild.modifyMemberRoles(member, rolesToAdd, null).queue();
        }
    }

    public static void removeRole(net.dv8tion.jda.api.entities.Guild guild, Member member, Role role) {
        var selfMember = guild.getMemberById(guild.getJDA().getSelfUser().getIdLong());
        if (selfMember == null) return;

        if (role != null && selfMember.canInteract(member) && member.getRoles().contains(role))
            guild.removeRoleFromMember(member, role).queue();
    }

    public static void sendNotification(User user, Member member, Activity newActivity, net.dv8tion.jda.api.entities.Guild guild, MyGuild myGuild, String lang) {
        var tc = guild.getTextChannelById(myGuild.getStreamTcId());
        if (tc != null) {
            var streamerRoleIdsUser = new ArrayList<Long>();
            var streamerRoleIds = myGuild.getStreamerRoleIds();
            var memberRoles = member.getRoles();

            for (var streamerRoleId : streamerRoleIds) {
                for (var memberRole : memberRoles) {
                    var memberRoleId = memberRole.getIdLong();

                    if (memberRoleId == streamerRoleId)
                        streamerRoleIdsUser.add(streamerRoleId);
                }
            }

            var pingRoles = myGuild.getStreamPingRoles(streamerRoleIdsUser);

            var sb = new StringBuilder();
            for (var pingRole : pingRoles) sb.append(pingRole.getAsMention()).append(" ");

            var mb = new MessageBuilder();
            mb.setContent(sb.toString());

            var eb = new EmbedBuilder();
            eb.setColor(new MyUser(user).getColor());
            eb.setAuthor(LanguageHandler.get(lang, "livestream_announcement_title"), newActivity.getUrl(), ImageUtil.getUrl(guild.getJDA(), "twitch_logo"));
            eb.setTitle(newActivity.getName());
            eb.setDescription(String.format(LanguageHandler.get(lang, "livestream_announcement"), user.getAsMention(), newActivity.getUrl()));
            eb.setThumbnail(user.getAvatarUrl());
            var rp = newActivity.asRichPresence();
            eb.setFooter(String.format(LanguageHandler.get(lang, "livestream_announcement_game"), rp == null ? "" : rp.getDetails()), null);

            mb.setEmbed(eb.build());
            tc.sendMessage(mb.build()).queue();
        }
    }
}
