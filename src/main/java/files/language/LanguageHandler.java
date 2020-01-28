// Author: Tancred423 (https://github.com/Tancred423)
package files.language;

import moderation.guild.Server;
import moderation.user.Master;
import servant.Servant;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.io.File;
import java.io.IOException;

public class LanguageHandler {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void initialize() {
        String currentDir = System.getProperty("user.dir");
        String resourcesLangDir = currentDir + "/resources/lang";

        File lang = new File(resourcesLangDir);
        if (!lang.exists()) lang.mkdir();

        File de_de = new File(resourcesLangDir + "/de_de.ini");
        if (!de_de.exists()) createDefaultDE_DE();

        File en_gb = new File(resourcesLangDir + "/en_gb.ini");
        if (!en_gb.exists()) createDefaultEN_GB();
    }

    private static void createDefaultDE_DE() {
        try {
            LanguageFile.createDefaultDE_DE();
        } catch (IOException e) {
            System.out.println("Error @ Creating de.ini: " + e.getMessage());
        }
    }

    private static void createDefaultEN_GB() {
        try {
            LanguageFile.createDefaultEN_GB();
        } catch (IOException e) {
            System.out.println("Error @ Creating en.ini: " + e.getMessage());
        }
    }

    public static String get(String lang, String key) {
        try {
            String text = LanguageFile.get(lang, key);
            if (text == null) text = "Text not found.";
            return text;
        } catch (IOException e) {
            return "Text not found.";
        }
    }

    public static String getLanguage(CommandEvent event) {
        if (event == null) return Servant.config.getDefaultLanguage();
        else return event.getGuild() == null ?
                new Master(event.getAuthor()).getLanguage() :
                new Server(event.getGuild()).getLanguage();
    }
}
