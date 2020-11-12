// Author: Tancred423 (https://github.com/Tancred423)
package utilities;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import servant.LoggingTask;
import servant.Servant;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static servant.Database.closeQuietly;

public class ImageUtil {
    public static String getUrl(JDA jda, String imageName) {
        Connection connection = null;
        String imageUrl = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT image_url FROM const_images WHERE image_name=?");
            select.setString(1, imageName);
            var resultSet = select.executeQuery();
            var imageUrls = new ArrayList<String>();
            while (resultSet.next()) imageUrls.add(resultSet.getString("image_url"));

            if (imageUrls.size() == 0) System.out.println("Image not found for: " + imageName);
            else {
                var randomIndex = ThreadLocalRandom.current().nextInt(Math.max(imageUrls.size() - 1, 1));
                imageUrl = imageUrls.get(randomIndex);
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "ImageUtil#getImageUrl"));
        } finally {
            closeQuietly(connection);
        }

        return imageUrl;
    }

    public static Map<String, Emote> getFeatureEmotes(JDA jda) { // TODO: fuck a lot of completes
        return new HashMap<>() {{
            var sm = jda.getShardManager();
            if (sm != null) {
                var servantEmotes1 = sm.getGuildById(599222484134264852L);
                var servantEmotes2 = sm.getGuildById(673449424755752991L);
                if (servantEmotes1 != null && servantEmotes2 != null) {
                    // Servant Emotes #1
                    put("achievements", servantEmotes1.retrieveEmoteById(673449238457221150L).complete());
                    put("alarm", servantEmotes1.retrieveEmoteById(673449238193111051L).complete());
                    put("autorole", servantEmotes1.retrieveEmoteById(673449239099211796L).complete());
                    put("avatar", servantEmotes1.retrieveEmoteById(673449239149281290L).complete());
                    put("baguette", servantEmotes1.retrieveEmoteById(673449238545301504L).complete());
                    put("beg", servantEmotes1.retrieveEmoteById(673449239006937088L).complete());
                    put("bestofimage", servantEmotes1.retrieveEmoteById(673449238457221140L).complete());
                    put("bestofquote", servantEmotes1.retrieveEmoteById(673449239157669888L).complete());
                    put("bio", servantEmotes1.retrieveEmoteById(673449238985834532L).complete());
                    put("bird", servantEmotes1.retrieveEmoteById(673449238692364301L).complete());
                    put("birthday", servantEmotes1.retrieveEmoteById(673449239480893460L).complete());
                    put("botinfo", servantEmotes1.retrieveEmoteById(673449238977576991L).complete());
                    put("cat", servantEmotes1.retrieveEmoteById(673449238683844627L).complete());
                    put("clear", servantEmotes1.retrieveEmoteById(673449238570467351L).complete());
                    put("coinflip", servantEmotes1.retrieveEmoteById(673449239006806053L).complete());
                    put("cookie", servantEmotes1.retrieveEmoteById(673449238969057290L).complete());
                    put("cop", servantEmotes1.retrieveEmoteById(673449239292149780L).complete());
                    put("createembed", servantEmotes1.retrieveEmoteById(673449238642032656L).complete());
                    put("dab", servantEmotes1.retrieveEmoteById(673449238645964800L).complete());
                    put("dice", servantEmotes1.retrieveEmoteById(673449239098949643L).complete());
                    put("dog", servantEmotes1.retrieveEmoteById(673449239069589524L).complete());
                    put("editembed", servantEmotes1.retrieveEmoteById(673449239300407316L).complete());
                    put("flex", servantEmotes1.retrieveEmoteById(673449239300538398L).complete());
                    put("flip", servantEmotes1.retrieveEmoteById(673449238994223107L).complete());
                    put("fox", servantEmotes1.retrieveEmoteById(673449242043482113L).complete());
                    put("giveaway", servantEmotes1.retrieveEmoteById(673449239187161099L).complete());
                    put("highfive", servantEmotes1.retrieveEmoteById(673449239010869279L).complete());
                    put("hug", servantEmotes1.retrieveEmoteById(673449239090561045L).complete());
                    put("join", servantEmotes1.retrieveEmoteById(673449238755278889L).complete());
                    put("joinmessage", servantEmotes1.retrieveEmoteById(673449239321378816L).complete());
                    put("kiss", servantEmotes1.retrieveEmoteById(673449239077978152L).complete());
                    put("koala", servantEmotes1.retrieveEmoteById(673449239032102923L).complete());
                    put("leave", servantEmotes1.retrieveEmoteById(673449238704947230L).complete());
                    put("leavemessage", servantEmotes1.retrieveEmoteById(673449238939566120L).complete());
                    put("levelrole", servantEmotes1.retrieveEmoteById(673449239241555998L).complete());
                    put("lick", servantEmotes1.retrieveEmoteById(673449239044685825L).complete());
                    put("livestream", servantEmotes1.retrieveEmoteById(673449239388487690L).complete());
                    put("love", servantEmotes1.retrieveEmoteById(673449239145218067L).complete());
                    put("mediaonlychannel", servantEmotes1.retrieveEmoteById(673449239078109194L).complete());
                    put("panda", servantEmotes1.retrieveEmoteById(673449238952411138L).complete());
                    put("pat", servantEmotes1.retrieveEmoteById(673449239262789633L).complete());
                    put("supporter", servantEmotes1.retrieveEmoteById(673449239216390166L).complete());
                    put("pikachu", servantEmotes1.retrieveEmoteById(673449239199875084L).complete());
                    put("ping", servantEmotes1.retrieveEmoteById(673449238717399058L).complete());
                    put("poke", servantEmotes1.retrieveEmoteById(673449242240483338L).complete());
                    put("customcommands", servantEmotes1.retrieveEmoteById((748097130459168869L)).complete());

                    // Servant Emotes #2
                    put("poll", servantEmotes2.retrieveEmoteById(673449568825901076L).complete());
                    put("profile", servantEmotes2.retrieveEmoteById(673449568884621313L).complete());
                    put("quickpoll", servantEmotes2.retrieveEmoteById(673449569077428225L).complete());
                    put("reactionrole", servantEmotes2.retrieveEmoteById(673449568930627584L).complete());
                    put("redpanda", servantEmotes2.retrieveEmoteById(673449568964313088L).complete());
                    put("remindme", servantEmotes2.retrieveEmoteById(767720936618197042L).complete());
                    put("role", servantEmotes2.retrieveEmoteById(673449569023164437L).complete());
                    put("serverinfo", servantEmotes2.retrieveEmoteById(673449568678969345L).complete());
                    put("serversetup", servantEmotes2.retrieveEmoteById(673449568796540963L).complete());
                    put("signup", servantEmotes2.retrieveEmoteById(673449569081884672L).complete());
                    put("slap", servantEmotes2.retrieveEmoteById(673449569253851146L).complete());
                    put("timezone", servantEmotes2.retrieveEmoteById(673449569157382144L).complete());
                    put("toggle", servantEmotes2.retrieveEmoteById(673449568977027088L).complete());
                    put("unflip", servantEmotes2.retrieveEmoteById(673449569203519518L).complete());
                    put("user", servantEmotes2.retrieveEmoteById(673449569199194141L).complete());
                    put("voicelobby", servantEmotes2.retrieveEmoteById(673449568888946689L).complete());
                    put("wave", servantEmotes2.retrieveEmoteById(673449569253720074L).complete());
                    put("wink", servantEmotes2.retrieveEmoteById(673449569324892169L).complete());
                    put("mostusedcommands", servantEmotes2.retrieveEmoteById(673461657246367744L).complete());
                    put("sloth", servantEmotes2.retrieveEmoteById(673472485093801994L).complete());
                    put("log", servantEmotes2.retrieveEmoteById(687334387095830563L).complete());
                    put("shame", servantEmotes2.retrieveEmoteById(732582351045001276L).complete());
                    put("rate", servantEmotes2.retrieveEmoteById(732590489722683412L).complete());
                    put("help", servantEmotes2.retrieveEmoteById(732591206143623179L).complete());
                    put("cheers", servantEmotes2.retrieveEmoteById(737297654341697576L).complete());
                    put("frog", servantEmotes2.retrieveEmoteById(737297654345760769L).complete());
                    put("wolf", servantEmotes2.retrieveEmoteById(737297654412869753L).complete());
                    put("f", servantEmotes2.retrieveEmoteById(747417182014537798L).complete());
                    put("fennec", servantEmotes2.retrieveEmoteById(756512706214756353L).complete());
                    put("tictactoe", servantEmotes2.retrieveEmoteById(756525713129209877L).complete());
                    put("setbirthday", servantEmotes2.retrieveEmoteById(774651584344752159L).complete());
                }
            }
        }};
    }

    public static Map<String, Emote> getAchievementsEmotes(JDA jda) { // TODO: fuck a lot of completes
        return new HashMap<>() {{
            var sm = jda.getShardManager();
            if (sm != null) {
                var servantEmotes2 = sm.getGuildById(673449424755752991L);
                if (servantEmotes2 != null) {
                    // Servant Emotes #2
                    put("deusvult", servantEmotes2.retrieveEmoteById(673456738921480192L).complete());
                    put("excalibur", servantEmotes2.retrieveEmoteById(673456739060023306L).complete());
                    put("fiteme", servantEmotes2.retrieveEmoteById(673456738778873893L).complete());
                    put("gae_bolg", servantEmotes2.retrieveEmoteById(673456739072344064L).complete());
                    put("kind", servantEmotes2.retrieveEmoteById(673456738850045964L).complete());
                    put("level10", servantEmotes2.retrieveEmoteById(673456739101704202L).complete());
                    put("level20", servantEmotes2.retrieveEmoteById(673456739072475176L).complete());
                    put("level30", servantEmotes2.retrieveEmoteById(673456739147972639L).complete());
                    put("level40", servantEmotes2.retrieveEmoteById(673456739248766986L).complete());
                    put("level50", servantEmotes2.retrieveEmoteById(673456739324264451L).complete());
                    put("level60", servantEmotes2.retrieveEmoteById(673456739328196608L).complete());
                    put("level70", servantEmotes2.retrieveEmoteById(673456739332653066L).complete());
                    put("level80", servantEmotes2.retrieveEmoteById(673456739357818921L).complete());
                    put("level90", servantEmotes2.retrieveEmoteById(673456739433316361L).complete());
                    put("level100", servantEmotes2.retrieveEmoteById(673456743279493150L).complete());
                    put("love42", servantEmotes2.retrieveEmoteById(673456739307487238L).complete());
                    put("love69", servantEmotes2.retrieveEmoteById(673456739470802945L).complete());
                    put("navi", servantEmotes2.retrieveEmoteById(673456739341041665L).complete());
                    put("nicelevel", servantEmotes2.retrieveEmoteById(673456739290710029L).complete());
                    put("padoru", servantEmotes2.retrieveEmoteById(673456739827449866L).complete());
                    put("unlimited_blade_works", servantEmotes2.retrieveEmoteById(673456739760472089L).complete());
                    put("xmas", servantEmotes2.retrieveEmoteById(673456739919593472L).complete());
                    put("console", servantEmotes2.retrieveEmoteById(726354898605703229L).complete());
                    put("arenanet", servantEmotes2.retrieveEmoteById(737304992373014560L).complete());
                }
            }
        }};
    }
}
