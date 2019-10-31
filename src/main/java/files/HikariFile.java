// Author: Tancred423 (https://github.com/Tancred423)
package files;

import nu.studer.java.util.OrderedProperties;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class HikariFile {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public HikariFile() throws IOException {
        var currentDir = System.getProperty("user.dir");
        var resourcesDir = currentDir + "/resources";
        var configDir = resourcesDir + "/hikari.properties";

        var resources = new File(resourcesDir);
        if (!resources.exists()) resources.mkdir();

        var config = new File(configDir);
        if (!config.exists()) createDefault(resourcesDir);
    }

    private void createDefault(String resourcesDir) throws IOException {
        var config = new OrderedProperties();
        var os = new FileOutputStream(resourcesDir + "/hikari.properties");

        config.setProperty("dataSourceClassName", "");
        config.setProperty("dataSource.user", "");
        config.setProperty("dataSource.password", "");
        config.setProperty("dataSource.databaseName", "");
        config.setProperty("dataSource.portNumber", "");
        config.setProperty("dataSource.serverName", "");

        config.store(os,
                "Project: Servant\n" +
                "Author: Tancred#0001\n" +
                "GitHub: https://github.com/Tancred423/Servant");
        os.close();
    }
}
