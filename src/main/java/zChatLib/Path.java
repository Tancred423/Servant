package zChatLib;

import java.util.ArrayList;

public class Path extends ArrayList<String> {
    public String word = null;
    public Path next = null;
    public int length = 0;

    static Path sentenceToPath(String sentence) {
        sentence = sentence.trim();
        return arrayToPath(sentence.split(" "));
    }

    static String pathToSentence(Path path) {
        String result = "";

        for(Path p = path; p != null; p = p.next) result = result + " " + path.word;

        return result.trim();
    }

    private static Path arrayToPath(String[] array) {
        Path tail = null;
        Path head = null;

        for(int i = array.length - 1; i >= 0; --i) {
            head = new Path();
            head.word = array[i];
            head.next = tail;
            if (tail == null) head.length = 1;
            else head.length = tail.length + 1;

            tail = head;
        }

        return head;
    }
}
