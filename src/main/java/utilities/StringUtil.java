// Author: Tancred423 (https://github.com/Tancred423)
package utilities;

public class StringUtil {
    public static String fillWithWhitespace(String text, int desiredStringLength) {
        if (desiredStringLength <= text.length()) return text;
        else return text + " ".repeat(desiredStringLength - text.length());
    }

    public static String pushWithWhitespace(String text, int desiredStringLength) {
        if (desiredStringLength <= text.length()) return text;
        else return " ".repeat(desiredStringLength - text.length()) + text;
    }

    public static String flip(String text) {
        var flipped = new StringBuilder();
        for (var i = 0; i < text.length(); i++) {
            var character = text.charAt(i);
            var flippedCharacter = getFlippedChar(character);
            flipped.append(flippedCharacter);
        }
        return flipped.reverse().toString();
    }
    public static String mirror(String text) {
        var mirrored = new StringBuilder();
        for (var i = 0; i < text.length(); i++) {
            var character = text.charAt(i);
            var mirroredCharacter = getMirroredChar(character);
            mirrored.append(mirroredCharacter);
        }
        return mirrored.reverse().toString();
    }

    private static Character getFlippedChar(char character) {
        switch (character) {
            // Punctation
            case '!':
                return '¡';
            case '¡':
                return '!';

            case '?':
                return '¿';
            case '¿':
                return '?';
            case '⸮':
                return 'ƾ';
            case 'ƾ':
                return '⸮';

            case '.':
                return '˙';
            case '˙':
                return '.';

            case '\'':
            case '`':
            case '´':
                return ',';
            case ',':
                return '\'';

            case '"':
                return '„';
            case '„':
                return '"';

            case '&':
                return '⅋';
            case '⅋':
                return '&';

            case ':':
                return ':';

            case ';':
                return '؛';
            case '؛':
                return ';';

            case '<':
                return '>';
            case '>':
                return '<';

            case '(':
                return ')';
            case ')':
                return '(';

            case '{':
                return '}';
            case '}':
                return '{';

            case '[':
                return ']';
            case ']':
                return '[';

            case '⁅':
                return '⁆';
            case '⁆':
                return '⁅';

            case '_':
                return '‾';
            case '‾':
                return '_';

            case '‿':
                return '⁀';
            case '⁀':
                return '‿';

            case '∴':
                return '∵';
            case '∵':
                return '∴';


            // Numbers
            case '0':
                return '0';

            case '1':
                return '⇂';
            case '⇂':
                return '1';
            case 'Ɩ':
                return 'Ɩ';

            case '2':
                return 'ᘔ';
            case 'ᘔ':
                return '2';
            case 'ς':
                return 'ς';

            case '3':
                return 'Ɛ';
            case 'Ɛ':
                return '3';

            case '4':
                return 'ᔭ';
            case 'ᔭ':
                return '4';
            case 'μ':
                return 'ત';
            case 'ત':
                return 'μ';

            case '5':
                return 'ϛ';
            case 'ϛ':
                return '5';
            case 'ट':
                return '૨';
            case '૨':
                return 'ट';

            case '6':
                return '9';
            case 'მ':
                return 'ϱ';
            case 'ϱ':
                return 'მ';

            case '7':
                return 'Ɫ';
            case 'Ɫ':
                return '7';
            case '٢':
                return '˩';
            case '˩':
                return '٢';

            case '8':
                return '8';

            case '9':
                return '6';
            case '୧':
                return '୧';

            // Letters
            case 'A':
                return '∀';
            case '∀':
                return 'A';

            case 'B':
                return 'ᗺ';
            case 'ᗺ':
                return 'B';

            case 'C':
                return 'Ɔ';
            case 'Ɔ':
                return 'C';

            case 'D':
                return 'ᗡ';
            case 'ᗡ':
                return 'D';

            case 'E':
                return 'Ǝ';
            case 'Ǝ':
                return 'E';

            case 'F':
                return 'Ⅎ';
            case 'Ⅎ':
                return 'F';
            case 'ꟻ':
                return 'Է';
            case 'Է':
                return 'ꟻ';

            case 'G':
                return '⅁';
            case '⅁':
                return 'G';

            case 'H':
                return 'H';

            case 'I':
                return 'I';

            case 'J':
                return 'ᒋ';
            case 'ᒋ':
                return 'J';
            case 'Ⴑ':
                return 'Ղ';
            case 'Ղ':
                return 'Ⴑ';

            case 'K':
                return 'ꓘ';
            case 'ꓘ':
                return 'K';

            case 'L':
                return '⅂';
            case '⅂':
                return 'L';
            case '⅃':
                return 'Γ';
            case 'Γ':
                return '⅃';

            case 'M':
                return 'W';

            case 'N':
                return 'N';
            case 'И':
                return 'И';

            case 'O':
                return 'O';

            case 'P':
                return 'Ԁ';
            case 'Ԁ':
                return 'P';
            case 'ꟼ':
                return 'ḇ';
            case 'ḇ':
                return 'ꟼ';

            case 'Q':
                return 'ტ';
            case 'ტ':
                return 'Q';
            case 'Ϙ':
                return '⥀';
            case '⥀':
                return 'Ϙ';

            case 'R':
                return 'ᴚ';
            case 'ᴚ':
                return 'R';
            case 'Я':
                return 'ʁ';
            case 'ʁ':
                return 'Я';

            case 'S':
                return 'S';
            case 'Ƨ':
                return 'Ƨ';

            case 'T':
                return 'Ʇ';
            case 'Ʇ':
                return 'T';

            case 'U':
                return 'Ո';
            case 'Ո':
                return 'U';

            case 'V':
                return 'Λ';
            case 'Λ':
                return 'V';

            case 'W':
                return 'M';

            case 'X':
                return 'X';

            case 'Y':
                return '⅄';
            case '⅄':
                return 'Y';

            case 'Z':
                return 'Z';

            case 'a':
                return 'ɐ';
            case 'ɐ':
                return 'a';
            case 'ɒ':
                return 'ɑ';
            case 'ɑ':
                return 'ɒ';

            case 'b':
                return 'q';

            case 'c':
                return 'ɔ';
            case 'ɔ':
                return 'c';

            case 'd':
                return 'p';

            case 'e':
                return 'ǝ';
            case 'ǝ':
                return 'e';

            case 'f':
                return 'ɟ';
            case 'ɟ':
                return 'f';
            case 'ʇ':
                return 'ɻ';
            case 'ɻ':
                return 'ʇ';

            case 'g':
                return 'ɓ';
            case 'ɓ':
                return 'g';

            case 'h':
                return 'ɥ';
            case 'ɥ':
                return 'h';
            case 'ʜ':
                return 'ʜ';

            case 'i':
                return 'ᴉ';
            case 'ᴉ':
                return 'i';

            case 'j':
                return 'ɾ';
            case 'ɾ':
                return 'j';
            case 'į':
                return 'ๅ';
            case 'ๅ':
                return 'į';

            case 'k':
                return 'ʞ';
            case 'ʞ':
                return 'k';

            case 'l':
                return 'l';

            case 'm':
                return 'ɯ';
            case 'ɯ':
                return 'm';

            case 'n':
                return 'u';

            case 'o':
                return 'o';

            case 'p':
                return 'd';

            case 'q':
                return 'b';

            case 'r':
                return 'ɹ';
            case 'ɹ':
                return 'r';
            case 'ɿ':
                return 'ɿ';

            case 's':
                return 's';
            case 'ƨ':
                return 'ƨ';

            case 't':
                return 't';
            case 'Ɉ':
                return 'ϝ';
            case 'ϝ':
                return 'Ɉ';

            case 'u':
                return 'n';

            case 'v':
                return 'ʌ';
            case 'ʌ':
                return 'v';

            case 'w':
                return 'ʍ';
            case 'ʍ':
                return 'w';

            case 'x':
                return 'x';

            case 'y':
                return 'ʎ';
            case 'ʎ':
                return 'y';
            case 'γ':
                return 'λ';
            case 'λ':
                return 'γ';

            case 'z':
                return 'z';

            // Default
            default:
                return character;
        }
    }

    private static Character getMirroredChar(char character) {
        switch (character) {
            // Punctation
            case '!':
                return '!';
            case '¡':
                return '¡';

            case '?':
                return '⸮';
            case '⸮':
                return '?';
            case '¿':
                return 'ƾ';

            case '.':
                return '.';
            case '˙':
                return '˙';

            case '\'':
                return '\'';
            case '`':
                return '´';
            case '´':
                return '`';
            case ',':
                return ',';

            case '"':
                return '"';
            case '„':
                return '„';

            case '&':
                return '&';
            case '⅋':
                return '⅋';

            case ':':
                return ':';

            case ';':
                return ';';
            case '؛':
                return '؛';

            case '<':
                return '>';
            case '>':
                return '<';

            case '(':
                return ')';
            case ')':
                return '(';

            case '{':
                return '}';
            case '}':
                return '{';

            case '[':
                return ']';
            case ']':
                return '[';

            case '⁅':
                return '⁆';
            case '⁆':
                return '⁅';

            case '_':
                return '_';
            case '‾':
                return '‾';

            case '‿':
                return '‿';
            case '⁀':
                return '⁀';

            case '∴':
                return '∴';
            case '∵':
                return '∵';

            // Numbers
            case '0':
                return '0';

            case '1':
                return 'Ɩ';
            case 'Ɩ':
                return '1';
            case '⇂':
                return '⇂';

            case '2':
                return 'ς';
            case 'ς':
                return '2';
            case 'ᘔ':
                return 'ᘔ';

            case '3':
                return 'Ɛ';
            case 'Ɛ':
                return '3';

            case '4':
                return 'μ';
            case 'μ':
                return '4';
            case 'ᔭ':
                return 'ત';
            case 'ત':
                return 'ᔭ';

            case '5':
                return 'ट';
            case 'ट':
                return '5';
            case 'ϛ':
                return '૨';
            case '૨':
                return 'ϛ';

            case '6':
                return 'მ';
            case 'მ':
                return '6';

            case '7':
                return '٢';
            case '٢':
                return '7';
            case 'Ɫ':
                return '˩';
            case '˩':
                return 'Ɫ';

            case '8':
                return '8';

            case '9':
                return '୧';
            case '୧':
                return '9';

            // Letters
            case 'A':
                return 'A';
            case '∀':
                return '∀';

            case 'B':
                return 'ᗺ';
            case 'ᗺ':
                return 'B';

            case 'C':
                return 'Ɔ';
            case 'Ɔ':
                return 'C';

            case 'D':
                return 'ᗡ';
            case 'ᗡ':
                return 'D';

            case 'E':
                return 'Ǝ';
            case 'Ǝ':
                return 'E';

            case 'F':
                return 'ꟻ';
            case 'ꟻ':
                return 'F';
            case 'Ⅎ':
                return 'Է';
            case 'Է':
                return 'Ⅎ';

            case 'G':
                return 'Ә';
            case 'Ә':
                return 'G';
            case '⅁':
                return 'Ҽ';
            case 'Ҽ':
                return '⅁';

            case 'H':
                return 'H';

            case 'I':
                return 'I';

            case 'J':
                return 'Ⴑ';
            case 'Ⴑ':
                return 'J';
            case 'ᒋ':
                return 'Ղ';
            case 'Ղ':
                return 'ᒋ';

            case 'K':
                return 'ꓘ';
            case 'ꓘ':
                return 'K';

            case 'L':
                return '⅃';
            case '⅃':
                return 'L';
            case '⅂':
                return 'Γ';
            case 'Γ':
                return '⅂';

            case 'M':
                return 'M';

            case 'N':
                return 'И';
            case 'И':
                return 'N';

            case 'O':
                return 'O';

            case 'P':
                return 'ꟼ';
            case 'ꟼ':
                return 'P';
            case 'Ԁ':
                return 'ḇ';
            case 'ḇ':
                return 'Ԁ';

            case 'Q':
                return 'Ϙ';
            case 'Ϙ':
                return 'Q';
            case 'ტ':
                return '⥀';
            case '⥀':
                return 'ტ';

            case 'R':
                return 'Я';
            case 'Я':
                return 'R';
            case 'ᴚ':
                return 'ʁ';
            case 'ʁ':
                return 'ᴚ';

            case 'S':
                return 'Ƨ';
            case 'Ƨ':
                return 'S';

            case 'T':
                return 'T';
            case 'Ʇ':
                return 'Ʇ';

            case 'U':
                return 'U';
            case 'Ո':
                return 'Ո';

            case 'V':
                return 'V';
            case 'Λ':
                return 'Λ';

            case 'W':
                return 'W';

            case 'X':
                return 'X';

            case 'Y':
                return 'Y';
            case '⅄':
                return '⅄';

            case 'Z':
                return 'Z';

            case 'a':
                return 'ɒ';
            case 'ɒ':
                return 'a';
            case 'ɐ':
                return 'ɑ';
            case 'ɑ':
                return 'ɐ';

            case 'b':
                return 'd';

            case 'c':
                return 'ɔ';
            case 'ɔ':
                return 'c';

            case 'd':
                return 'b';

            case 'e':
                return 'e';
            case 'ǝ':
                return 'ǝ';

            case 'f':
                return 'ʇ';
            case 'ʇ':
                return 'f';
            case 'ɟ':
                return 'ɻ';
            case 'ɻ':
                return 'ɟ';

            case 'g':
                return 'ϱ';
            case 'ϱ':
                return 'g';
            case 'ɓ':
                return 'ɓ';

            case 'h':
                return 'ʜ';
            case 'ʜ':
                return 'h';
            case 'ɥ':
                return 'ɥ';

            case 'i':
                return 'i';
            case 'ᴉ':
                return 'ᴉ';

            case 'j':
                return 'į';
            case 'į':
                return 'j';
            case 'ɾ':
                return 'ๅ';
            case 'ๅ':
                return 'ɾ';

            case 'k':
                return 'ʞ';
            case 'ʞ':
                return 'k';

            case 'l':
                return 'l';

            case 'm':
                return 'm';
            case 'ɯ':
                return 'ɯ';

            case 'n':
                return 'n';

            case 'o':
                return 'o';

            case 'p':
                return 'q';

            case 'q':
                return 'p';

            case 'r':
                return 'ɿ';
            case 'ɿ':
                return 'r';
            case 'ɹ':
                return 'ɹ';

            case 's':
                return 'ƨ';
            case 'ƨ':
                return 's';

            case 't':
                return 'Ɉ';
            case 'Ɉ':
                return 't';

            case 'u':
                return 'u';

            case 'v':
                return 'v';
            case 'ʌ':
                return 'ʌ';

            case 'w':
                return 'w';
            case 'ʍ':
                return 'ʍ';

            case 'x':
                return 'x';

            case 'y':
                return 'γ';
            case 'γ':
                return 'y';
            case 'ʎ':
                return 'λ';
            case 'λ':
                return 'ʎ';

            case 'z':
                return 'z';

            // Default
            default:
                return character;
        }
    }
}
