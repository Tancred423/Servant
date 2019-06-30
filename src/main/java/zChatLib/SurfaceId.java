package zChatLib;

final class SurfaceId {
    private static final int idOffset = Misc.readIntFromFile("category.bin");
    private static final byte[] nodes = Misc.readBytesFromFile("surface-id.bin.node", 1);
    private static final byte[] exts = Misc.readBytesFromFile("surface-id.bin.ext", 1);
    private static final byte[] char_to_chck = Misc.readBytesFromFile();

    static void eachCommonPrefix(String var0, int var1, WordDic.Callback var2) {
        long var3 = getNode(0);
        int var5 = idOffset;
        CodeStream var6 = new CodeStream(var0, var1);

        while(true) {
            if (isTerminal(var3)) {
                WordDic.eachViterbiNode(var2, var5++, var1, var6.position() - var1, false);
            }

            if (var6.isEos()) {
                return;
            }

            if (!checkEncodedChildren(var6, var3)) {
                return;
            }

            char var7 = read(var6);
            long var8 = getNode(base(var3) + var7);
            if (chck(var8) != var7) {
                return;
            }

            var3 = var8;
            var5 += siblingTotal(var8);
        }
    }

    private static char read(CodeStream var0) {
        return (char)(char_to_chck[var0.read()] & 255);
    }

    private static boolean checkEncodedChildren(CodeStream var0, long var1) {
        if (type(var1) == 0) return checkEC(var0, var1);
        return true;
    }

    private static boolean checkEC(CodeStream var0, long var1) {
        char var3 = (char)((int)(var1 >> 27 & 127L));
        return var3 == 0 || read(var0) == var3 && !var0.isEos();
    }

    private static char chck(long var0) {
        return (char)((int)(var0 >> 20 & 127L));
    }

    private static int base(long var0) {
        return (int)(var0 & 524287L);
    }

    private static boolean isTerminal(long var0) {
        return (var0 >> 19 & 1L) == 1L;
    }

    private static int type(long var0) {
        return (var0 >> 39 & 1L) == 1L ? 2 + (int)(var0 >> 38 & 1L) : 0;
    }

    private static int siblingTotal(long var0) {
        switch(type(var0)) {
            case 0:
                return (int)(var0 >> 34 & 31L);
            case 2:
                return (int)(var0 >> 27 & 2047L);
            default:
                int var2 = (int)(var0 >> 27 & 2047L);
                return (exts[var2 * 4] & 255) << 24 | (exts[var2 * 4 + 1] & 255) << 16 | (exts[var2 * 4 + 2] & 255) << 8 | (exts[var2 * 4 + 3] & 255);
        }
    }

    private static long getNode(int var0) {
        return (long)(nodes[var0 * 5] & 255) << 32 | (long)(nodes[var0 * 5 + 1] & 255) << 24 | (long)(nodes[var0 * 5 + 2] & 255) << 16 | (long)(nodes[var0 * 5 + 3] & 255) << 8 | (long)(nodes[var0 * 5 + 4] & 255);
    }
}
