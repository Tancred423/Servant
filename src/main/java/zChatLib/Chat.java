package zChatLib;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class Chat {
    public Bot bot;
    String customerId;
    History<History<String>> thatHistory;
    History<String> requestHistory;
    History<String> responseHistory;
    static String matchTrace = "";
    History<String> inputHistory;
    static String longitude;
    static String latitude;
    static boolean locationKnown = false;
    Predicates predicates;

    public Chat(Bot bot) {
        this(bot, "0");
    }

    private Chat(Bot bot, String customerId) {
        this.customerId = MagicStrings.unknown_customer_id;
        this.thatHistory = new History<>();
        this.requestHistory = new History<>();
        this.responseHistory = new History<>();
        this.inputHistory = new History<>();
        this.predicates = new Predicates();
        this.customerId = customerId;
        this.bot = bot;
        History<String> contextThatHistory = new History<>();
        contextThatHistory.add(MagicStrings.default_that);
        this.thatHistory.add(contextThatHistory);
        this.addPredicates();
        this.predicates.put("topic", MagicStrings.default_topic);
    }

    private void addPredicates() {
        try {
            this.predicates.getPredicateDefaults(MagicStrings.config_path + "/predicates.txt");
        } catch (Exception var2) {
            var2.printStackTrace();
        }
    }

    public void chat() {
        BufferedWriter bw;
        String logFile = MagicStrings.log_path + "/log_" + this.customerId + ".txt";

        try {
            bw = new BufferedWriter(new FileWriter(logFile, true));
            String request = "SET PREDICATES";
            this.multisentenceRespond(request);

            while(!request.equals("quit")) {
                request = IOUtils.readInputTextLine();
                String response = this.multisentenceRespond(request);
                bw.write("Human: " + request);
                bw.newLine();
                bw.write("Robot: " + response);
                bw.newLine();
                bw.flush();
            }

            bw.close();
        } catch (Exception var5) {
            var5.printStackTrace();
        }
    }

    private String respond(String input, String that, String topic, History<String> contextThatHistory) {
        this.inputHistory.add(input);
        String response = AIMLProcessor.respond(input, that, topic, this);
        String normResponse = this.bot.preProcessor.normalize(response);
        normResponse = JapaneseTokenizer.morphSentence(normResponse);
        String[] sentences = this.bot.preProcessor.sentenceSplit(normResponse);

        for (String sentence : sentences) {
            that = sentence;
            if (that.trim().equals("")) that = MagicStrings.default_that;
            contextThatHistory.add(that);
        }

        return response.trim() + "  ";
    }

    private String respond(String input, History<String> contextThatHistory) {
        History<String> hist = this.thatHistory.get(0);
        String that;
        if (hist == null) that = MagicStrings.default_that;
        else that = hist.getString(0);

        return this.respond(input, that, this.predicates.get("topic"), contextThatHistory);
    }

    public String multisentenceRespond(String request) {
        StringBuilder response = new StringBuilder();

        try {
            String norm = this.bot.preProcessor.normalize(request);
            norm = JapaneseTokenizer.morphSentence(norm);

            String[] sentences = this.bot.preProcessor.sentenceSplit(norm);
            History<String> contextThatHistory = new History<>();
            int i = 0;

            while(true) {
                if (i >= sentences.length) {
                    this.requestHistory.add(request);
                    this.responseHistory.add(response.toString());
                    this.thatHistory.add(contextThatHistory);
                    break;
                }

                AIMLProcessor.trace_count = 0;
                String reply = this.respond(sentences[i], contextThatHistory);
                response.append("  ").append(reply);
                ++i;
            }
        } catch (Exception var8) {
            var8.printStackTrace();
            return MagicStrings.error_bot_response;
        }

        this.bot.writeLearnfIFCategories();
        return response.toString().trim();
    }

    static void setMatchTrace(String newMatchTrace) {
        matchTrace = newMatchTrace;
    }
}
