package zChatLib;

public final class Morpheme {
    final String surface;
    public final String feature;
    public final int start;

    Morpheme(String var1, String var2, int var3) {
        this.surface = var1;
        this.feature = var2;
        this.start = var3;
    }
}
