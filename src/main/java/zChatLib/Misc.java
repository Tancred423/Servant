package zChatLib;

import java.io.*;
import java.nio.charset.StandardCharsets;

public final class Misc {
    private static InputStream openDictionaryData(String var0) {
        return Misc.class.getResourceAsStream("/net/reduls/sanmoku/dicdata/" + var0);
    }

    static DataInputStream openDictionaryDataAsDIS(String var0) {
        return new DataInputStream(new BufferedInputStream(openDictionaryData(var0), 80960));
    }

    static BufferedReader openDictionaryDataAsBR() {
        return new BufferedReader(new InputStreamReader(openDictionaryData("pos.bin"), StandardCharsets.UTF_8), 80960);
    }

    public static String readLine(BufferedReader var0) {
        try {
            return var0.readLine();
        } catch (IOException var2) {
            throw new AssertionError(var2.getMessage());
        }
    }

    public static void close(Closeable var0) {
        try {
            var0.close();
        } catch (IOException var2) {
            throw new AssertionError(var2.getMessage());
        }
    }

    static int readInt(DataInput var0) {
        try {
            return var0.readInt();
        } catch (IOException var2) {
            throw new AssertionError(var2.getMessage());
        }
    }

    static byte readByte(DataInput var0) {
        try {
            return var0.readByte();
        } catch (IOException var2) {
            throw new AssertionError(var2.getMessage());
        }
    }

    static byte[] readBytesFromFile(String var0, int var1) {
        DataInputStream var2 = openDictionaryDataAsDIS(var0);
        int var3 = readInt(var2);
        byte[] var4 = new byte[var3 * var1];

        try {
            var2.readFully(var4, 0, var4.length);
            return var4;
        } catch (Exception var6) {
            throw new RuntimeException(var6);
        }
    }

    static byte[] readBytesFromFile() {
        DataInputStream var3 = openDictionaryDataAsDIS("surface-id.bin.char");
        byte[] var4 = new byte[256];

        try {
            var3.readFully(var4, 0, var4.length);
            return var4;
        } catch (Exception var6) {
            throw new RuntimeException(var6);
        }
    }

    static int readIntFromFile(String var0) {
        DataInputStream var1 = openDictionaryDataAsDIS(var0);
        int var2 = readInt(var1);
        close(var1);
        return var2;
    }
}
