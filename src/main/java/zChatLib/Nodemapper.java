package zChatLib;

import java.util.ArrayList;
import java.util.HashMap;

public class Nodemapper {
    public Category category = null;
    int height;
    StarBindings starBindings;
    public HashMap<String, Nodemapper> map;
    public String key;
    public Nodemapper value;
    boolean shortCut;
    ArrayList<String> sets;

    Nodemapper() {
        this.height = MagicNumbers.max_graph_height;
        this.starBindings = null;
        this.map = null;
        this.key = null;
        this.value = null;
        this.shortCut = false;
    }
}
