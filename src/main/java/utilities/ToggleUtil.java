package utilities;

public class ToggleUtil {
    public static boolean commandIsEnabledByDefault(String command) {
        return true;
    }

    public static boolean featureIsEnabledByDefault(String feature) {
        return !feature.equalsIgnoreCase("cmddeletion");
    }

    public static boolean categoryIsEnabledByDefault(String category) {
        return true;
    }

    public static boolean pluginIsEnabledByDefault(String plugin) {
        return !plugin.equalsIgnoreCase("level");
    }
}
