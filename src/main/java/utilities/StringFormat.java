// Author: Tancred423 (https://github.com/Tancred423)
package utilities;

import files.language.LanguageHandler;
import moderation.user.User;
import net.dv8tion.jda.core.entities.Guild;

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
        var split  = "ɐqɔpǝɟbɥıظʞןɯuodbɹsʇnʌʍxʎz‾'؛˙¿¡/\\,abcdefghijklmnopqrstuvwxyz_,;.?!/\\'";
        //maj
        normal += "ABCDEFGHIJKLMNOPQRSTUVWXYZ∀ꓭϽᗡƎℲƃHIſʞ˥WNOԀὉᴚS⊥∩ΛMXʎZ";
        split  += "∀ꓭϽᗡƎℲƃHIſʞ˥WNOԀὉᴚS⊥∩ΛMXʎZABCDEFGHIJKLMNOPQRSTUVWXYZ";
        //number
        normal += "01234567890ƖᄅƐㄣϛ9ㄥ86";
        split  += "0ƖᄅƐㄣϛ9ㄥ860123456789";

        var flipped = new StringBuilder();
        char letter;
        for (int i=0; i< text.length(); i++) {
            letter = text.charAt(i);
            var a = normal.indexOf(letter);
            flipped.append(a != -1 ? split.charAt(a) : letter);
        }

        return flipped.reverse().toString();
    }

    public static LinkedHashMap<String, Integer> achievementSortByKey(LinkedHashMap<String, Integer> map, String lang, User internalUser, Guild guild, net.dv8tion.jda.core.entities.User user) {
        SortedSet<String> keys = new TreeSet<>(map.keySet());
        var newMap = new LinkedHashMap<String, Integer>();
        newMap.put(LanguageHandler.get(lang, "profile_total_ap"), internalUser.getTotelAP(guild, user));
        for (var key : keys) newMap.put(key, map.get(key));
        return newMap;
    }
}
