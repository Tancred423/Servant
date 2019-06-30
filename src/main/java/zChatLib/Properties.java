package zChatLib;

import java.io.*;
import java.util.HashMap;

public class Properties extends HashMap<String, String> {
    public String get(String key) {
        String result = super.get(key);
        return result == null ? MagicStrings.unknown_property_value : result;
    }

    private void getPropertiesFromInputStream(InputStream in) {
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

    void getProperties(String filename) {
        try {
            File file = new File(filename);
            if (file.exists()) {
                FileInputStream fstream = new FileInputStream(filename);
                this.getPropertiesFromInputStream(fstream);
                fstream.close();
            }
        } catch (Exception var4) {
            System.err.println("Error: " + var4.getMessage());
        }
    }
}
