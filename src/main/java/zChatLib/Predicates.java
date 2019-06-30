package zChatLib;

import java.io.*;
import java.util.HashMap;

public class Predicates extends HashMap<String, String> {
    public String put(String key, String value) {
        return super.put(key, value);
    }

    public String get(String key) {
        String result = super.get(key);
        return result == null ? MagicStrings.unknown_predicate_value : result;
    }

    private void getPredicateDefaultsFromInputStream(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String strLine;
        try {
            while((strLine = br.readLine()) != null) {
                if (strLine.contains(":")) {
                    String property = strLine.substring(0, strLine.indexOf(":"));
                    String value = strLine.substring(strLine.indexOf(":") + 1);
                    this.put(property, value);
                }
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }
    }

    void getPredicateDefaults(String filename) {
        try {
            File file = new File(filename);
            if (file.exists()) {
                FileInputStream fstream = new FileInputStream(filename);
                this.getPredicateDefaultsFromInputStream(fstream);
                fstream.close();
            }
        } catch (Exception var4) {
            System.err.println("Error: " + var4.getMessage());
        }
    }
}
