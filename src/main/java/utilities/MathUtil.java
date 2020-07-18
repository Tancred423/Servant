package utilities;

import java.util.Random;

public class MathUtil {
    public static int randomBiased (int max, float bias) {
        max++;
        var random = new Random();
        var v = Math.pow(random.nextDouble(), bias);
        var randomNumber = (int)(v * max);
        return Math.min(randomNumber, max);
    }
}
