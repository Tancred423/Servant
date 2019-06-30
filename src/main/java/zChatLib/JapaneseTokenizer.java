package zChatLib;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class JapaneseTokenizer {
    private static final Pattern tagPattern = Pattern.compile("(<.*>.*</.*>)|(<.*/>)");

    private static String buildFragment(String fragment) {
        String result = "";

        Morpheme e;
        for(Iterator i$ = Tagger.parse(fragment).iterator(); i$.hasNext(); result = result + e.surface + " ") {
            e = (Morpheme)i$.next();
        }

        return result.trim();
    }

    static String morphSentence(String sentence) {
        if (!MagicBooleans.jp_morphological_analysis) {
            return sentence;
        } else {
            StringBuilder result = new StringBuilder();
            Matcher matcher = tagPattern.matcher(sentence);

            while(matcher.find()) {
                int i = matcher.start();
                int j = matcher.end();
                String prefix;
                if (i > 0) {
                    prefix = sentence.substring(0, i - 1);
                } else {
                    prefix = "";
                }

                String tag = sentence.substring(i, j);
                result.append(" ").append(buildFragment(prefix)).append(" ").append(tag);
                if (j < sentence.length()) {
                    sentence = sentence.substring(j);
                } else {
                    sentence = "";
                }
            }

            while(result.toString().contains("  ")) result = new StringBuilder(result.toString().replace("  ", " "));

            return result.toString().trim();
        }
    }
}
