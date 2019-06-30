package zChatLib;

public class ParseState {
    Nodemapper leaf;
    public String input;
    public String that;
    public String topic;
    Chat chatSession;
    int depth;
    Predicates vars;

    ParseState(int depth, zChatLib.Chat chatSession, String input, String that, String topic, Nodemapper leaf) {
        this.chatSession = chatSession;
        this.input = input;
        this.that = that;
        this.topic = topic;
        this.leaf = leaf;
        this.depth = depth;
        this.vars = new Predicates();
    }
}
