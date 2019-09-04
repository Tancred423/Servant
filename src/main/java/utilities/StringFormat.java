// Author: Tancred423 (https://github.com/Tancred423)
package utilities;

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
        normal += "ABCDEFGHIJKLMNOPQRSTUVWXYZ∀qϽᗡƎℲƃHIſʞ˥WNOԀὉᴚS⊥∩ΛMXʎZ";
        split  += "∀qϽᗡƎℲƃHIſʞ˥WNOԀὉᴚS⊥∩ΛMXʎZABCDEFGHIJKLMNOPQRSTUVWXYZ";
        //number
        normal += "01234567890ƖᄅƐㄣϛ9ㄥ86";
        split  += "0ƖᄅƐㄣϛ9ㄥ860123456789";

        var flipped = new StringBuilder();
        char letter;
        for (int i=0; i< text.length(); i++) {
            letter = text.charAt(i);
            var a = normal.indexOf(letter);
            flipped.append((a != -1) ? split.charAt(a) : letter);
        }

        return flipped.reverse().toString();
    }
}
