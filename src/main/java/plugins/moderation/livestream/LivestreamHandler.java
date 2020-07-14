package plugins.moderation.livestream;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import servant.MyGuild;
import servant.MyUser;
import utilities.ImageUtil;

import java.awt.*;
import java.util.ArrayList;

public class LivestreamHandler {
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

    public static void sendNotification(User author, Activity newActivity, net.dv8tion.jda.api.entities.Guild guild, MyGuild myGuild, boolean isStreamerMode, String lang, MyUser myUser) {
        if (myGuild.getStreamTcId() != 0 && myUser.isStreamer(guild.getIdLong())) {
            var tc = guild.getTextChannelById(myGuild.getStreamTcId());
            if (tc != null) tc.sendMessage(getNotifyMessage(guild.getJDA(), author, newActivity, new MyUser(author), guild.getRoleById(myGuild.getStreamPingRoleId()), isStreamerMode, lang)).queue();
        }
    }

    public static Message getNotifyMessage(JDA jda, User author, Activity newActivity, MyUser myUser, Role pingRole, boolean isStreamerMode, String lang) {
        MessageBuilder mb = new MessageBuilder();
        EmbedBuilder eb = new EmbedBuilder();
        if (isStreamerMode) mb.setContent(pingRole != null ? pingRole.getAsMention() : "");
        eb.setColor(Color.decode(myUser.getColorCode()));
        eb.setAuthor(LanguageHandler.get(lang, "livestream_announcement_title"), newActivity.getUrl(), ImageUtil.getUrl(jda, "twitch_logo"));
        eb.setTitle(newActivity.getName());
        eb.setDescription(String.format(LanguageHandler.get(lang, "livestream_announcement"), author.getAsMention(), newActivity.getUrl()));
        eb.setThumbnail(author.getAvatarUrl());
        var rp = newActivity.asRichPresence();
        eb.setFooter(String.format(LanguageHandler.get(lang, "livestream_announcement_game"), rp == null ? "" :  rp.getDetails()), null);
        mb.setEmbed(eb.build());
        return mb.build();
    }
}
