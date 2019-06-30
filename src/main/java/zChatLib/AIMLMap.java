package zChatLib;

import java.io.*;
import java.util.HashMap;

public class AIMLMap extends HashMap<String, String> {
    private String mapName;
    private String host;
    private String botid;
    private boolean isExternal = false;

    AIMLMap(String name) {
        this.mapName = name;
    }

    public String get(String key) {
        int number;
        if (this.mapName.equals(MagicStrings.map_successor)) {
            try {
                number = Integer.parseInt(key);
                return String.valueOf(number + 1);
            } catch (Exception var5) {
                return MagicStrings.unknown_map_value;
            }
        } else if (this.mapName.equals(MagicStrings.map_predecessor)) {
            try {
                number = Integer.parseInt(key);
                return String.valueOf(number - 1);
            } catch (Exception var6) {
                return MagicStrings.unknown_map_value;
            }
        } else {
            String value;
            if (this.isExternal && MagicBooleans.enable_external_sets) {
                String query = this.mapName.toUpperCase() + " " + key;
                value = Sraix.sraix(null, query, MagicStrings.unknown_map_value, null, this.host, this.botid);
            } else value = super.get(key);

            if (value == null) value = MagicStrings.unknown_map_value;
            return value;
        }
    }

    public String put(String key, String value) {
        return super.put(key, value);
    }

    private void readAIMLMapFromInputStream(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String strLine;
        try {
            while((strLine = br.readLine()) != null && strLine.length() > 0) {
                String[] splitLine = strLine.split(":");
                if (splitLine.length >= 2) {
                    if (strLine.startsWith(MagicStrings.remote_map_key)) {
                        if (splitLine.length >= 3) {
                            this.host = splitLine[1];
                            this.botid = splitLine[2];
                            this.isExternal = true;
                        }
                    } else {
                        String key = splitLine[0].toUpperCase();
                        String value = splitLine[1];
                        this.put(key, value);
                    }
                }
            }
        } catch (Exception var9) {
            var9.printStackTrace();
        }
    }

    void readAIMLMap() {
        try {
            File file = new File(MagicStrings.maps_path + "/" + this.mapName + ".txt");
            if (file.exists()) {
                FileInputStream fstream = new FileInputStream(MagicStrings.maps_path + "/" + this.mapName + ".txt");
                this.readAIMLMapFromInputStream(fstream);
                fstream.close();
            }
        } catch (Exception var4) {
            System.err.println("Error: " + var4.getMessage());
        }
    }
}
