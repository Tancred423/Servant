// Author: Tancred423 (https://github.com/Tancred423)
package utilities;

import files.language.LanguageHandler;

public class Achievement {
    public static String getFancyName(String name, String lang) {
        switch (name.toLowerCase()) {
            case "excalibur": return LanguageHandler.get(lang, "achievement_excalibur");

            case "level10": return LanguageHandler.get(lang, "achievement_level10");
            case "level20": return LanguageHandler.get(lang, "achievement_level20");
            case "level30": return LanguageHandler.get(lang, "achievement_level30");
            case "level40": return LanguageHandler.get(lang, "achievement_level40");
            case "level50": return LanguageHandler.get(lang, "achievement_level50");
            case "level60": return LanguageHandler.get(lang, "achievement_level60");
            case "level70": return LanguageHandler.get(lang, "achievement_level70");
            case "level80": return LanguageHandler.get(lang, "achievement_level80");
            case "level90": return LanguageHandler.get(lang, "achievement_level90");
            case "level100": return LanguageHandler.get(lang, "achievement_level100");

            default: return name;
        }
    }
}
