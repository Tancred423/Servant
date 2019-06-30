package zChatLib;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class NodemapperOperator {
    public static int size(Nodemapper node) {
        HashSet<String> set = new HashSet<>();
        if (node.shortCut) set.add("<THAT>");
        if (node.key != null) set.add(node.key);
        if (node.map != null) set.addAll(node.map.keySet());
        return set.size();
    }

    public static void put(Nodemapper node, String key, Nodemapper value) {
        if (node.map != null) node.map.put(key, value);
        else {
            node.key = key;
            node.value = value;
        }
    }

    public static Nodemapper get(Nodemapper node, String key) {
        if (node.map != null) return node.map.get(key);
        else return key.equals(node.key) ? node.value : null;
    }

    static boolean containsKey(Nodemapper node, String key) {
        if (node.map != null) return node.map.containsKey(key);
        else return key.equals(node.key);
    }

    static Set<String> keySet(Nodemapper node) {
        if (node.map != null) return node.map.keySet();
        else {
            Set<String> set = new HashSet<>();
            if (node.key != null) set.add(node.key);
            return set;
        }
    }

    static boolean isLeaf(Nodemapper node) {
        return node.category != null;
    }

    static void upgrade(Nodemapper node) {
        node.map = new HashMap<>();
        node.map.put(node.key, node.value);
        node.key = null;
        node.value = null;
    }
}
