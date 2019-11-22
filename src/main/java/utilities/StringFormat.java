// Author: Tancred423 (https://github.com/Tancred423)
package utilities;

import files.language.LanguageHandler;
import fun.level.LevelImage;
import moderation.user.User;
import net.dv8tion.jda.api.entities.Guild;

import java.util.*;

public class StringFormat {
    public static String fillWithWhitespace(String text, int desiredStringLength) {
        if (desiredStringLength <= text.length()) return text;
        else return text + " ".repeat(desiredStringLength - text.length());
    }

    public static String pushWithWhitespace(String text, int desiredStringLength) {
        if (desiredStringLength <= text.length()) return text;
        else return " ".repeat(desiredStringLength - text.length()) + text;
    }

    public static String flipString(String text) {
        var normal = "abcdefghijklmnopqrstuvwxyz_,;.?!/\\'ɐqɔpǝɟbɥıظʞןɯuodbɹsʇnʌʍxʎz‾'؛˙¿¡/\\,";
        var flip  = "ɐqɔpǝɟbɥıظʞןɯuodbɹsʇnʌʍxʎz‾'؛˙¿¡/\\,abcdefghijklmnopqrstuvwxyz_,;.?!/\\'";
        //maj
        normal += "ABCDEFGHIJKLMNOPQRSTUVWXYZ∀ꓭϽᗡƎℲƃHIſʞ˥WNOԀὉᴚS⊥∩ΛMXʎZ";
        flip  += "∀ꓭϽᗡƎℲƃHIſʞ˥WNOԀὉᴚS⊥∩ΛMXʎZABCDEFGHIJKLMNOPQRSTUVWXYZ";
        //number
        normal += "01234567890ƖᄅƐㄣϛ9ㄥ86";
        flip  += "0ƖᄅƐㄣϛ9ㄥ860123456789";

        var flipped = new StringBuilder();
        char letter;
        for (int i=0; i< text.length(); i++) {
            letter = text.charAt(i);
            var a = normal.indexOf(letter);
            flipped.append(a != -1 ? flip.charAt(a) : letter);
        }

        return flipped.reverse().toString();
    }

    public static LinkedHashMap<String, String> achievementSortByKey(LinkedHashMap<String, String> map, String lang, User internalUser, Guild guild, net.dv8tion.jda.api.entities.User user, Locale locale) {
        SortedSet<String> keys = new TreeSet<>(map.keySet());
        var newMap = new LinkedHashMap<String, String>();
        newMap.put(LanguageHandler.get(lang, "profile_total_ap"), LevelImage.formatDecimal(locale, internalUser.getTotelAP(guild, user)));
        for (var key : keys) newMap.put(key, map.get(key));
        return newMap;
    }
}
