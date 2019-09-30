// Author: Tancred423 (https://github.com/Tancred423)
package utilities;

import files.language.LanguageHandler;
import servant.Servant;

public class Achievement {
    public static String getFancyName(String name, String lang) {
        switch (name.toLowerCase()) {
            case "excalibur": return LanguageHandler.get(lang, "achievement_excalibur");
            case "unlimited_blade_works": return LanguageHandler.get(lang, "unlimited_blade_works");
            case "gae_bolg": return LanguageHandler.get(lang, "gae_bolg");

            case "level10": return LanguageHandler.get(lang, "achievement_level10");
            case "level20": return LanguageHandler.get(lang, "achievement_level20");
            case "level30": return LanguageHandler.get(lang, "achievement_level30");
            case "level40": return LanguageHandler.get(lang, "achievement_level40");
            case "level50": return LanguageHandler.get(lang, "achievement_level50");
            case "level60": return LanguageHandler.get(lang, "achievement_level60");
            case "level69": return LanguageHandler.get(lang, "achievement_level69");
            case "level70": return LanguageHandler.get(lang, "achievement_level70");
            case "level80": return LanguageHandler.get(lang, "achievement_level80");
            case "level90": return LanguageHandler.get(lang, "achievement_level90");
            case "level100": return LanguageHandler.get(lang, "achievement_level100");

            case "love42": return LanguageHandler.get(lang, "achievement_love42");
            case "love69": return LanguageHandler.get(lang, "achievement_love69");

            case "kind": return String.format(LanguageHandler.get(lang, "achievement_kind"), Servant.jda.getSelfUser().getName());

            default: return name;
        }
    }
}
