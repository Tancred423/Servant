package zChatLib;

import java.util.ArrayList;

public class Stars extends ArrayList<String> {
    public String star(int i) {
        return i < this.size() ? this.get(i) : null;
    }
}
