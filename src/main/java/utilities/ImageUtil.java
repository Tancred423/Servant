// Author: Tancred423 (https://github.com/Tancred423)
package utilities;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import servant.LoggingTask;
import servant.Servant;

import java.sql.Connection;
import java.sql.ResultSet;
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
            var select = connection.prepareStatement("SELECT image_url FROM const_images WHERE image_name=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setString(1, imageName);
            var resultSet = select.executeQuery();
            var imageUrls = new ArrayList<String>();
            if (resultSet.first()) {
                do {
                    imageUrls.add(resultSet.getString("image_url"));
                } while (resultSet.next());

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

    public static Map<String, Emote> getFeatureEmotes(JDA jda) {
        return new HashMap<>() {{
            var sm = jda.getShardManager();
            if (sm != null) {
                var servantEmotes1 = sm.getGuildById(599222484134264852L);
                var servantEmotes2 = sm.getGuildById(673449424755752991L);
                if (servantEmotes1 != null && servantEmotes2 != null) {
                    // Servant Emotes #1
                    put("achievements", servantEmotes1.getEmoteById(673449238457221150L));
                    put("alarm", servantEmotes1.getEmoteById(673449238193111051L));
                    put("autorole", servantEmotes1.getEmoteById(673449239099211796L));
                    put("avatar", servantEmotes1.getEmoteById(673449239149281290L));
                    put("baguette", servantEmotes1.getEmoteById(673449238545301504L));
                    put("beg", servantEmotes1.getEmoteById(673449239006937088L));
                    put("bestofimage", servantEmotes1.getEmoteById(673449238457221140L));
                    put("bestofquote", servantEmotes1.getEmoteById(673449239157669888L));
                    put("bio", servantEmotes1.getEmoteById(673449238985834532L));
                    put("bird", servantEmotes1.getEmoteById(673449238692364301L));
                    put("birthday", servantEmotes1.getEmoteById(673449239480893460L));
                    put("botinfo", servantEmotes1.getEmoteById(673449238977576991L));
                    put("cat", servantEmotes1.getEmoteById(673449238683844627L));
                    put("clear", servantEmotes1.getEmoteById(673449238570467351L));
                    put("coinflip", servantEmotes1.getEmoteById(673449239006806053L));
                    put("cookie", servantEmotes1.getEmoteById(673449238969057290L));
                    put("cop", servantEmotes1.getEmoteById(673449239292149780L));
                    put("createembed", servantEmotes1.getEmoteById(673449238642032656L));
                    put("dab", servantEmotes1.getEmoteById(673449238645964800L));
                    put("dice", servantEmotes1.getEmoteById(673449239098949643L));
                    put("dog", servantEmotes1.getEmoteById(673449239069589524L));
                    put("editembed", servantEmotes1.getEmoteById(673449239300407316L));
                    put("flex", servantEmotes1.getEmoteById(673449239300538398L));
                    put("flip", servantEmotes1.getEmoteById(673449238994223107L));
                    put("fox", servantEmotes1.getEmoteById(673449242043482113L));
                    put("giveaway", servantEmotes1.getEmoteById(673449239187161099L));
                    put("highfive", servantEmotes1.getEmoteById(673449239010869279L));
                    put("hug", servantEmotes1.getEmoteById(673449239090561045L));
                    put("join", servantEmotes1.getEmoteById(673449238755278889L));
                    put("joinmessage", servantEmotes1.getEmoteById(673449239321378816L));
                    put("kiss", servantEmotes1.getEmoteById(673449239077978152L));
                    put("koala", servantEmotes1.getEmoteById(673449239032102923L));
                    put("leave", servantEmotes1.getEmoteById(673449238704947230L));
                    put("leavemessage", servantEmotes1.getEmoteById(673449238939566120L));
                    put("levelrole", servantEmotes1.getEmoteById(673449239241555998L));
                    put("lick", servantEmotes1.getEmoteById(673449239044685825L));
                    put("livestream", servantEmotes1.getEmoteById(673449239388487690L));
                    put("love", servantEmotes1.getEmoteById(673449239145218067L));
                    put("mediaonlychannel", servantEmotes1.getEmoteById(673449239078109194L));
                    put("meme", servantEmotes1.getEmoteById(673449239547740160L));
                    put("panda", servantEmotes1.getEmoteById(673449238952411138L));
                    put("pat", servantEmotes1.getEmoteById(673449239262789633L));
                    put("supporter", servantEmotes1.getEmoteById(673449239216390166L));
                    put("pikachu", servantEmotes1.getEmoteById(673449239199875084L));
                    put("ping", servantEmotes1.getEmoteById(673449238717399058L));
                    put("poke", servantEmotes1.getEmoteById(673449242240483338L));

                    // Servant Emotes #2
                    put("poll", servantEmotes2.getEmoteById(673449568825901076L));
                    put("profile", servantEmotes2.getEmoteById(673449568884621313L));
                    put("quickpoll", servantEmotes2.getEmoteById(673449569077428225L));
                    put("reactionrole", servantEmotes2.getEmoteById(673449568930627584L));
                    put("redpanda", servantEmotes2.getEmoteById(673449568964313088L));
                    put("reminder", servantEmotes2.getEmoteById(673449568993804350L));
                    put("role", servantEmotes2.getEmoteById(673449569023164437L));
                    put("server", servantEmotes2.getEmoteById(673449568972570630L));
                    put("serverinfo", servantEmotes2.getEmoteById(673449568678969345L));
                    put("serversetup", servantEmotes2.getEmoteById(673449568796540963L));
                    put("signup", servantEmotes2.getEmoteById(673449569081884672L));
                    put("slap", servantEmotes2.getEmoteById(673449569253851146L));
                    put("timezone", servantEmotes2.getEmoteById(673449569157382144L));
                    put("toggle", servantEmotes2.getEmoteById(673449568977027088L));
                    put("unflip", servantEmotes2.getEmoteById(673449569203519518L));
                    put("user", servantEmotes2.getEmoteById(673449569199194141L));
                    put("voicelobby", servantEmotes2.getEmoteById(673449568888946689L));
                    put("wave", servantEmotes2.getEmoteById(673449569253720074L));
                    put("wink", servantEmotes2.getEmoteById(673449569324892169L));
                    put("mostusedcommands", servantEmotes2.getEmoteById(673461657246367744L));
                    put("sloth", servantEmotes2.getEmoteById(673472485093801994L));
                    put("log", servantEmotes2.getEmoteById(687334387095830563L));
                    put("shame", servantEmotes2.getEmoteById(732582351045001276L));
                    put("rate", servantEmotes2.getEmoteById(732590489722683412L));
                    put("help", servantEmotes2.getEmoteById(732591206143623179L));
                    put("cheers", servantEmotes2.getEmoteById(737297654341697576L));
                    put("frog", servantEmotes2.getEmoteById(737297654345760769L));
                    put("wolf", servantEmotes2.getEmoteById(737297654412869753L));
                }
            }
        }};
    }

    public static Map<String, Emote> getAchievementsEmotes(JDA jda) {
        return new HashMap<>() {{
            var sm = jda.getShardManager();
            if (sm != null) {
                var servantEmotes2 = sm.getGuildById(673449424755752991L);
                if (servantEmotes2 != null) {
                    // Servant Emotes #2
                    put("deusvult", servantEmotes2.getEmoteById(673456738921480192L));
                    put("excalibur", servantEmotes2.getEmoteById(673456739060023306L));
                    put("fiteme", servantEmotes2.getEmoteById(673456738778873893L));
                    put("gae_bolg", servantEmotes2.getEmoteById(673456739072344064L));
                    put("kind", servantEmotes2.getEmoteById(673456738850045964L));
                    put("level10", servantEmotes2.getEmoteById(673456739101704202L));
                    put("level20", servantEmotes2.getEmoteById(673456739072475176L));
                    put("level30", servantEmotes2.getEmoteById(673456739147972639L));
                    put("level40", servantEmotes2.getEmoteById(673456739248766986L));
                    put("level50", servantEmotes2.getEmoteById(673456739324264451L));
                    put("level60", servantEmotes2.getEmoteById(673456739328196608L));
                    put("level70", servantEmotes2.getEmoteById(673456739332653066L));
                    put("level80", servantEmotes2.getEmoteById(673456739357818921L));
                    put("level90", servantEmotes2.getEmoteById(673456739433316361L));
                    put("level100", servantEmotes2.getEmoteById(673456743279493150L));
                    put("love42", servantEmotes2.getEmoteById(673456739307487238L));
                    put("love69", servantEmotes2.getEmoteById(673456739470802945L));
                    put("navi", servantEmotes2.getEmoteById(673456739341041665L));
                    put("nicelevel", servantEmotes2.getEmoteById(673456739290710029L));
                    put("padoru", servantEmotes2.getEmoteById(673456739827449866L));
                    put("unlimited_blade_works", servantEmotes2.getEmoteById(673456739760472089L));
                    put("xmas", servantEmotes2.getEmoteById(673456739919593472L));
                    put("console", servantEmotes2.getEmoteById(726354898605703229L));
                    put("arenanet", servantEmotes2.getEmoteById(737304992373014560L));
                }
            }
        }};
    }
}
