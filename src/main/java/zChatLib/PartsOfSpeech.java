package zChatLib;

import java.io.BufferedReader;
import java.util.ArrayList;

public final class PartsOfSpeech {
    private static final String[] posArray;
    public static String get(int var0) {
        return posArray[var0];
    }

    static {
        BufferedReader var0 = Misc.openDictionaryDataAsBR();
        ArrayList<String> var1 = new ArrayList<>();

        for(String var2 = Misc.readLine(var0); var2 != null; var2 = Misc.readLine(var0)) var1.add(var2);

        Misc.close(var0);
        posArray = new String[var1.size()];

        for(int var3 = 0; var3 < posArray.length; ++var3) posArray[var3] = var1.get(var3);
    }
}
