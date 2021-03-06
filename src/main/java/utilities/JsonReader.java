// Author: Tancred423 (https://github.com/Tancred423)
package utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class JsonReader {
    private static String readAll(Reader rd) throws IOException {
        var sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) sb.append((char) cp);
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException, FileNotFoundException {
        try (var is = new URL(url).openStream()) {
            var rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            var jsonText = readAll(rd);
            return new JSONObject(jsonText);
        }
    }
}
