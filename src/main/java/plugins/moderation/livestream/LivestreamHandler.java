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

    public static void sendNotification(User user, Activity newActivity, net.dv8tion.jda.api.entities.Guild guild, MyGuild myGuild, String lang) {
        var tc = guild.getTextChannelById(myGuild.getStreamTcId());
        if (tc != null) {
            var pingRole = guild.getRoleById(myGuild.getStreamPingRoleId());

            var mb = new MessageBuilder();
            mb.setContent(pingRole != null ? pingRole.getAsMention() : "");

            var eb = new EmbedBuilder();
            eb.setColor(new MyUser(user).getColor());
            eb.setAuthor(LanguageHandler.get(lang, "livestream_announcement_title"), newActivity.getUrl(), ImageUtil.getUrl(guild.getJDA(), "twitch_logo"));
            eb.setTitle(newActivity.getName());
            eb.setDescription(String.format(LanguageHandler.get(lang, "livestream_announcement"), user.getAsMention(), newActivity.getUrl()));
            eb.setThumbnail(user.getAvatarUrl());
            var rp = newActivity.asRichPresence();
            eb.setFooter(String.format(LanguageHandler.get(lang, "livestream_announcement_game"), rp == null ? "" :  rp.getDetails()), null);

            mb.setEmbed(eb.build());
            tc.sendMessage(mb.build()).queue();
        }
    }
}
