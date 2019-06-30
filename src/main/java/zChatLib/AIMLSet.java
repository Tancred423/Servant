package zChatLib;

import java.io.*;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AIMLSet extends HashSet<String> {
    private String setName;
    int maxLength = 1;
    private String host;
    private String botid;
    private boolean isExternal = false;

    AIMLSet(String name) {
        this.setName = name.toLowerCase();
    }

    public boolean contains(String s) {
        if (this.isExternal && MagicBooleans.enable_external_sets) {
            String[] split = s.split(" ");
            if (split.length > this.maxLength) return false;
            else {
                String query = MagicStrings.set_member_string + this.setName.toUpperCase() + " " + s;
                String response = Sraix.sraix(null, query, "false", null, this.host, this.botid);
                return response.equals("true");
            }
        } else if (this.setName.equals(MagicStrings.natural_number_set_name)) {
            Pattern numberPattern = Pattern.compile("[0-9]+");
            Matcher numberMatcher = numberPattern.matcher(s);
            return numberMatcher.matches();
        } else return super.contains(s);
    }

    private void readAIMLSetFromInputStream(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String strLine;
        try {
            while((strLine = br.readLine()) != null && strLine.length() > 0) {
                String[] splitLine;
                if (strLine.startsWith("external")) {
                    splitLine = strLine.split(":");
                    if (splitLine.length >= 4) {
                        this.host = splitLine[1];
                        this.botid = splitLine[2];
                        this.maxLength = Integer.parseInt(splitLine[3]);
                        this.isExternal = true;
                    }
                } else {
                    strLine = strLine.toUpperCase().trim();
                    splitLine = strLine.split(" ");
                    int length = splitLine.length;
                    if (length > this.maxLength) this.maxLength = length;
                    this.add(strLine.trim());
                }
            }
        } catch (Exception var8) {
            var8.printStackTrace();
        }
    }

    void readAIMLSet() {
        try {
            File file = new File(MagicStrings.sets_path + "/" + this.setName + ".txt");
            if (file.exists()) {
                FileInputStream fstream = new FileInputStream(MagicStrings.sets_path + "/" + this.setName + ".txt");
                this.readAIMLSetFromInputStream(fstream);
                fstream.close();
            }
        } catch (Exception var4) {
            System.err.println("Error: " + var4.getMessage());
        }
    }
}
