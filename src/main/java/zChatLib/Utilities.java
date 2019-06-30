package zChatLib;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;

class Utilities {
    static String fixCSV(String line) {
        while(line.endsWith(";")) line = line.substring(0, line.length() - 1);
        if (line.startsWith("\"")) line = line.substring(1);
        if (line.endsWith("\"")) line = line.substring(0, line.length() - 1);
        line = line.replaceAll("\"\"", "\"");
        return line;
    }

    static String tagTrim(String xmlExpression) {
        String stag = "<" + "SET" + ">";
        String etag = "</" + "SET" + ">";
        if (xmlExpression.length() >= (stag + etag).length()) {
            xmlExpression = xmlExpression.substring(stag.length());
            xmlExpression = xmlExpression.substring(0, xmlExpression.length() - etag.length());
        }

        return xmlExpression;
    }

    static HashSet<String> stringSet(String... strings) {
        int len$ = strings.length;
        return new HashSet<>(Arrays.asList(strings).subList(0, len$));
    }

    private static String getFileFromInputStream(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder contents = new StringBuilder();

        String strLine;
        try {
            while((strLine = br.readLine()) != null) {
                if (strLine.length() == 0) contents.append("\n");
                else contents.append(strLine).append("\n");
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return contents.toString().trim();
    }

    private static String getFile(String filename) {
        String contents = "";

        try {
            File file = new File(filename);
            if (file.exists()) {
                FileInputStream fstream = new FileInputStream(filename);
                contents = getFileFromInputStream(fstream);
                fstream.close();
            }
        } catch (Exception var4) {
            System.err.println("Error: " + var4.getMessage());
        }

        return contents;
    }

    static String getPannousAPIKey() {
        String apiKey = getFile(MagicStrings.config_path + "/pannous-apikey.txt");
        if (apiKey.equals("")) {
            apiKey = MagicStrings.pannous_api_key;
        }

        return apiKey;
    }

    static String getPannousLogin() {
        String login = getFile(MagicStrings.config_path + "/pannous-login.txt");
        if (login.equals("")) login = MagicStrings.pannous_login;

        return login;
    }
}
