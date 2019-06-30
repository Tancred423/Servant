package zChatLib;

public final class WordDic {
    public static void search(String var0, int var1, WordDic.Callback var2) {
        SurfaceId.eachCommonPrefix(var0, var1, var2);
    }

    static void eachViterbiNode(WordDic.Callback var0, int var1, int var2, int var3, boolean var4) {
        for (MorphemeDic.Entry var6 : MorphemeDic.getMorphemes(var1))
            var0.call(new ViterbiNode(var2, (short) var3, var6.cost, var6.posId, var4));
    }

    public interface Callback {
        void call(ViterbiNode var1);
        boolean isEmpty();
    }
}
