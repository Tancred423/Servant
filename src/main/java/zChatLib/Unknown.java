package zChatLib;

public final class Unknown {
    private static final Char.Category space = Char.category(' ');

    public static void search(String var0, int var1, WordDic.Callback var2) {
        char var3 = var0.charAt(var1);
        Char.Category var4 = Char.category(var3);
        if (var2.isEmpty() || var4.invoke) {
            boolean var5 = var4 == space;
            int var6 = Math.min(var0.length(), var4.length + var1);

            int var7;
            for(var7 = var1; var7 < var6; ++var7) {
                WordDic.eachViterbiNode(var2, var4.id, var1, var7 - var1 + 1, var5);
                if (var7 + 1 != var6 && !Char.isCompatible(var3, var0.charAt(var7 + 1))) {
                    return;
                }
            }

            if (var4.group && var7 < var0.length()) {
                while(var7 < var0.length()) {
                    if (!Char.isCompatible(var3, var0.charAt(var7))) {
                        WordDic.eachViterbiNode(var2, var4.id, var1, var7 - var1, var5);
                        return;
                    }
                    ++var7;
                }
                WordDic.eachViterbiNode(var2, var4.id, var1, var0.length() - var1, var5);
            }
        }
    }
}
