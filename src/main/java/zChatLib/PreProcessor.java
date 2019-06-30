package zChatLib;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreProcessor {
    private int normalCount;
    private int denormalCount;
    private int personCount;
    private int person2Count;
    private int genderCount;
    private String[] normalSubs;
    private Pattern[] normalPatterns;
    private String[] denormalSubs;
    private Pattern[] denormalPatterns;
    private String[] personSubs;
    private Pattern[] personPatterns;
    private String[] person2Subs;
    private Pattern[] person2Patterns;
    private String[] genderSubs;
    private Pattern[] genderPatterns;

    PreProcessor() {
        this.normalSubs = new String[MagicNumbers.max_substitutions];
        this.normalPatterns = new Pattern[MagicNumbers.max_substitutions];
        this.denormalSubs = new String[MagicNumbers.max_substitutions];
        this.denormalPatterns = new Pattern[MagicNumbers.max_substitutions];
        this.personSubs = new String[MagicNumbers.max_substitutions];
        this.personPatterns = new Pattern[MagicNumbers.max_substitutions];
        this.person2Subs = new String[MagicNumbers.max_substitutions];
        this.person2Patterns = new Pattern[MagicNumbers.max_substitutions];
        this.genderSubs = new String[MagicNumbers.max_substitutions];
        this.genderPatterns = new Pattern[MagicNumbers.max_substitutions];
        this.normalCount = this.readSubstitutions(MagicStrings.config_path + "/normal.txt", this.normalPatterns, this.normalSubs);
        this.denormalCount = this.readSubstitutions(MagicStrings.config_path + "/denormal.txt", this.denormalPatterns, this.denormalSubs);
        this.personCount = this.readSubstitutions(MagicStrings.config_path + "/person.txt", this.personPatterns, this.personSubs);
        this.person2Count = this.readSubstitutions(MagicStrings.config_path + "/person2.txt", this.person2Patterns, this.person2Subs);
        this.genderCount = this.readSubstitutions(MagicStrings.config_path + "/gender.txt", this.genderPatterns, this.genderSubs);
    }

    String normalize(String request) {
        return this.substitute(request, this.normalPatterns, this.normalSubs, this.normalCount);
    }

    String denormalize(String request) {
        return this.substitute(request, this.denormalPatterns, this.denormalSubs, this.denormalCount);
    }

    public String person(String input) {
        return this.substitute(input, this.personPatterns, this.personSubs, this.personCount);
    }

    String person2(String input) {
        return this.substitute(input, this.person2Patterns, this.person2Subs, this.person2Count);
    }

    public String gender(String input) {
        return this.substitute(input, this.genderPatterns, this.genderSubs, this.genderCount);
    }

    private String substitute(String request, Pattern[] patterns, String[] subs, int count) {
        String result = " " + request + " ";

        try {
            for(int i = 0; i < count; ++i) {
                String replacement = subs[i];
                Pattern p = patterns[i];
                Matcher m = p.matcher(result);
                if (m.find()) {
                    result = m.replaceAll(replacement);
                }
            }

            while(result.contains("  ")) {
                result = result.replace("  ", " ");
            }

            result = result.trim();
        } catch (Exception var10) {
            var10.printStackTrace();
        }

        return result.trim();
    }

    private int readSubstitutionsFromInputStream(InputStream in, Pattern[] patterns, String[] subs) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        int subCount = 0;

        String strLine;
        try {
            while((strLine = br.readLine()) != null) {
                strLine = strLine.trim();
                Pattern pattern = Pattern.compile("\"(.*?)\",\"(.*?)\"", Pattern.DOTALL);
                Matcher matcher = pattern.matcher(strLine);
                if (matcher.find() && subCount < MagicNumbers.max_substitutions) {
                    subs[subCount] = matcher.group(2);
                    String quotedPattern = Pattern.quote(matcher.group(1));
                    patterns[subCount] = Pattern.compile(quotedPattern, Pattern.CASE_INSENSITIVE);
                    ++subCount;
                }
            }
        } catch (Exception var10) {
            var10.printStackTrace();
        }

        return subCount;
    }

    private int readSubstitutions(String filename, Pattern[] patterns, String[] subs) {
        int subCount = 0;

        try {
            File file = new File(filename);
            if (file.exists()) {
                FileInputStream fstream = new FileInputStream(filename);
                subCount = this.readSubstitutionsFromInputStream(fstream, patterns, subs);
                fstream.close();
            }
        } catch (Exception var7) {
            System.err.println("Error: " + var7.getMessage());
        }

        return subCount;
    }

    String[] sentenceSplit(String line) {
        line = line.replace("。", ".");
        line = line.replace("？", "?");
        line = line.replace("！", "!");
        String[] result = line.split("[.!?]");
        for(int i = 0; i < result.length; ++i) result[i] = result[i].trim();
        return result;
    }
}
