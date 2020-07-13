package utilities;

import java.util.Random;

public class MathUtil {
    public static int randomBiased (int max, float bias) {
        var random = new Random();
        var v = Math.pow(random.nextDouble(), bias);
        return (int)(v * max);
    }
}
