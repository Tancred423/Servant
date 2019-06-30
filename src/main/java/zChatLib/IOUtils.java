package zChatLib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class IOUtils {
    static String readInputTextLine() {
        BufferedReader lineOfText = new BufferedReader(new InputStreamReader(System.in));
        String textLine = null;

        try {
            textLine = lineOfText.readLine();
        } catch (IOException var3) {
            var3.printStackTrace();
        }

        return textLine;
    }

    public static String system() {
        return "";
    }
}
