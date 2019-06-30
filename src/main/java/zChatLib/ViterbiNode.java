package zChatLib;

public final class ViterbiNode {
    int cost;
    ViterbiNode prev = null;
    final int start;
     private final int length_posId_isSpace;

    ViterbiNode(int var1, short var2, short var3, short var4, boolean var5) {
        this.cost = var3;
        this.start = var1;
        this.length_posId_isSpace = (var2 << 17) + (var4 << 1) + (var5 ? 1 : 0);
    }

    public short length() {
        return (short)(this.length_posId_isSpace >> 17);
    }
    short posId() {
        return (short)(this.length_posId_isSpace >> 1 & '\uffff');
    }
    boolean isSpace() {
        return (this.length_posId_isSpace & 1) == 1;
    }
    static ViterbiNode makeBOSEOS() {
        return new ViterbiNode(0, (short)0, (short)0, (short)0, false);
    }
}
