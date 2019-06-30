package zChatLib;

import java.io.DataInputStream;

public final class Char {
    private static final Char.Category[] charCategorys;
    private static final byte[] charInfos;

    public static Char.Category category(char var0) {
        return charCategorys[findNode(var0) >> 16];
    }

    static boolean isCompatible(char var0, char var1) {
        return (compatibleMask(var0) & compatibleMask(var1)) != 0;
    }

    private static int compatibleMask(char var0) {
        return findNode(var0) & '\uffff';
    }

    private static int findNode(char var0) {
        int var1 = 0;
        int var2 = charInfos.length / 6;

        while(true) {
            int var3 = var1 + (var2 - var1) / 2;
            if (var2 - var1 == 1) {
                return nodeValue(var1);
            }

            if (var0 < nodeCode(var3)) {
                var2 = var3;
            } else if (var0 >= nodeCode(var3)) {
                var1 = var3;
            }
        }
    }

    private static int nodeCode(int var0) {
        return (charInfos[var0 * 6] & 255) << 16 | (charInfos[var0 * 6 + 1] & 255) << 8 | (charInfos[var0 * 6 + 2] & 255);
    }

    private static int nodeValue(int var0) {
        return (charInfos[var0 * 6 + 3] & 255) << 16 | (charInfos[var0 * 6 + 4] & 255) << 8 | (charInfos[var0 * 6 + 5] & 255);
    }

    static {
        DataInputStream var0 = Misc.openDictionaryDataAsDIS("category.bin");
        int var1 = Misc.readInt(var0);
        charCategorys = new Char.Category[var1];

        for(int var2 = 0; var2 < var1; ++var2) {
            charCategorys[var2] = new Char.Category(var2, Misc.readByte(var0) == 1, Misc.readByte(var0) == 1, Misc.readByte(var0));
        }

        Misc.close(var0);
        charInfos = Misc.readBytesFromFile("code.bin", 6);
    }

    public static final class Category {
        public final int id;
        final boolean invoke;
        final boolean group;
        public final byte length;

        public Category(int var1, boolean var2, boolean var3, byte var4) {
            this.id = var1;
            this.invoke = var2;
            this.group = var3;
            this.length = var4;
        }
    }
}
