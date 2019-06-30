package zChatLib;

import java.io.DataInputStream;

final class Matrix {
    private static final byte[] matrix;
    private static final int leftNum;
    private static final byte[] posid_map = Misc.readBytesFromFile("posid-map.bin", 2);
    private static final byte[] val = Misc.readBytesFromFile("matrix.map", 2);

    static short linkCost(short var0, short var1) {
        int var2 = posid(var0) * leftNum + posid(var1);
        long var3 = node(var2 / 4);
        int var5 = (int)(var3 >> var2 % 4 * 14) & 16383;
        return (short)(val[var5 * 2] << 8 | val[var5 * 2 + 1] & 255);
    }

    private static short posid(short var0) {
        return (short)(posid_map[var0 * 2] << 8 | posid_map[var0 * 2 + 1] & 255);
    }

    private static long node(int var0) {
        return (long)(matrix[var0 * 7] & 255) << 48 | (long)(matrix[var0 * 7 + 1] & 255) << 40 | (long)(matrix[var0 * 7 + 2] & 255) << 32 | (long)(matrix[var0 * 7 + 3] & 255) << 24 | (long)(matrix[var0 * 7 + 4] & 255) << 16 | (long)(matrix[var0 * 7 + 5] & 255) << 8 | (long)(matrix[var0 * 7 + 6] & 255);
    }

    static {
        DataInputStream var0 = Misc.openDictionaryDataAsDIS("matrix.bin");
        int var1 = Misc.readInt(var0);
        leftNum = Misc.readInt(var0);
        matrix = new byte[var1 * 7];

        try {
            var0.readFully(matrix, 0, matrix.length);
        } catch (Exception var3) {
            throw new RuntimeException(var3);
        }

        Misc.close(var0);
    }
}
