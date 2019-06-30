package zChatLib;

import java.util.ArrayList;
import java.util.List;

final class Tagger {
    private static final ArrayList<ViterbiNode> BOS_NODES = new ArrayList<>(1);

    static List<Morpheme> parse(String var0) {
        return parse(var0, new ArrayList<>(var0.length() / 2));
    }

    private static List<Morpheme> parse(String var0, List<Morpheme> var1) {
        for(ViterbiNode var2 = parseImpl(var0); var2 != null; var2 = var2.prev) {
            String var3 = var0.substring(var2.start, var2.start + var2.length());
            String var4 = PartsOfSpeech.get(var2.posId());
            var1.add(new Morpheme(var3, var4, var2.start));
        }

        return var1;
    }

    private static ViterbiNode parseImpl(String var0) {
        int var1 = var0.length();
        ArrayList<ArrayList<ViterbiNode>> var2 = new ArrayList<>(var1 + 1);
        var2.add(BOS_NODES);

        for(int var3 = 1; var3 <= var1; ++var3) {
            var2.add(new ArrayList<>());
        }

        Tagger.MakeLattice var7 = new Tagger.MakeLattice(var2);

        for(int var4 = 0; var4 < var1; ++var4) {
            if (!(var2.get(var4)).isEmpty()) {
                var7.set(var4);
                WordDic.search(var0, var4, var7);
                Unknown.search(var0, var4, var7);
                if (var4 > 0) {
                    (var2.get(var4)).clear();
                }
            }
        }

        ViterbiNode var8 = setMincostNode(ViterbiNode.makeBOSEOS(), var2.get(var1)).prev;

        ViterbiNode var5;
        ViterbiNode var6;
        for(var5 = null; var8.prev != null; var8 = var6) {
            var6 = var8.prev;
            var8.prev = var5;
            var5 = var8;
        }

        return var5;
    }

    private static ViterbiNode setMincostNode(ViterbiNode var0, ArrayList<ViterbiNode> var1) {
        ViterbiNode var2 = var0.prev = var1.get(0);
        int var3 = var2.cost + Matrix.linkCost(var2.posId(), var0.posId());

        for(int var4 = 1; var4 < var1.size(); ++var4) {
            ViterbiNode var5 = var1.get(var4);
            int var6 = var5.cost + Matrix.linkCost(var5.posId(), var0.posId());
            if (var6 < var3) {
                var3 = var6;
                var0.prev = var5;
            }
        }

        var0.cost += var3;
        return var0;
    }

    static {
        BOS_NODES.add(ViterbiNode.makeBOSEOS());
    }

    private static final class MakeLattice implements WordDic.Callback {
        private final ArrayList<ArrayList<ViterbiNode>> nodesAry;
        private int i;
        private ArrayList<ViterbiNode> prevs;
        private boolean empty = true;

        MakeLattice(ArrayList<ArrayList<ViterbiNode>> var1) {
            this.nodesAry = var1;
        }

        public void set(int var1) {
            this.i = var1;
            this.prevs = this.nodesAry.get(var1);
            this.empty = true;
        }

        public void call(ViterbiNode var1) {
            this.empty = false;
            if (var1.isSpace()) this.nodesAry.get(this.i + var1.length()).addAll(this.prevs);
            else this.nodesAry.get(this.i + var1.length()).add(Tagger.setMincostNode(var1, this.prevs));
        }

        public boolean isEmpty() {
            return this.empty;
        }
    }
}
