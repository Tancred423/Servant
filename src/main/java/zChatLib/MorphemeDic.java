package zChatLib;

import java.util.Iterator;

final class MorphemeDic {
    private static final byte[] morps = Misc.readBytesFromFile("morp.info.bin", 2);
    private static final byte[] morpMap = Misc.readBytesFromFile("morp.info.map", 4);
    private static final byte[] leafs = Misc.readBytesFromFile("morp.leaf.bin", 8);
    private static final byte[] leafAccCounts = Misc.readBytesFromFile("morp.leaf.cnt.bin", 2);
    private static final int nextBase = Misc.readIntFromFile("morp.base.bin");

    static Iterable<MorphemeDic.Entry> getMorphemes(final int var0) {
        return () -> new MorphemeIterator(var0);
    }

    private static int nextNode(int var0) {
        long var1 = getLeaf(var0);
        return !hasNext(var1, var0) ? -1 : nextNode(var1, var0);
    }

    private static boolean hasNext(long var0, int var2) {
        long var3 = 1L << var2 % 64;
        return (var0 & var3) != 0L;
    }

    private static int nextNode(long var0, int var2) {
        int var3 = var2 / 64;
        int var4 = (leafAccCounts[var3 * 2] & 255) << 8 | leafAccCounts[var3 * 2 + 1] & 255;
        long var5 = (1L << var2 % 64) - 1L;
        return nextBase + var4 + Long.bitCount(var0 & var5);
    }

    private static long getLeaf(int var0) {
        int var1 = var0 / 64;
        return (long)leafs[var1 * 8] << 56 | (long)(leafs[var1 * 8 + 1] & 255) << 48 | (long)(leafs[var1 * 8 + 2] & 255) << 40 | (long)(leafs[var1 * 8 + 3] & 255) << 32 | (long)(leafs[var1 * 8 + 4] & 255) << 24 | (long)(leafs[var1 * 8 + 5] & 255) << 16 | (long)(leafs[var1 * 8 + 6] & 255) << 8 | (long)(leafs[var1 * 8 + 7] & 255);
    }

    static class Entry {
        final short posId;
        final short cost;
        final int morphemeId;

        private Entry(int var1) {
            int var2 = (MorphemeDic.morps[var1 * 2] & 255) << 8 | MorphemeDic.morps[var1 * 2 + 1] & 255;
            this.posId = (short)((short)(MorphemeDic.morpMap[var2 * 4] << 8) | (short)(MorphemeDic.morpMap[var2 * 4 + 1] & 255));
            this.cost = (short)((short)(MorphemeDic.morpMap[var2 * 4 + 2] << 8) | (short)(MorphemeDic.morpMap[var2 * 4 + 3] & 255));
            this.morphemeId = var1;
        }
    }

    static class MorphemeIterator implements Iterator<MorphemeDic.Entry> {
        private int node;

        MorphemeIterator(int var1) {
            this.node = var1;
        }

        public boolean hasNext() {
            return this.node != -1;
        }

        public MorphemeDic.Entry next() {
            MorphemeDic.Entry var1 = new MorphemeDic.Entry(this.node);
            this.node = MorphemeDic.nextNode(this.node);
            return var1;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
