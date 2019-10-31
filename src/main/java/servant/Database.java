// Author: Tancred423 (https://github.com/Tancred423)
package servant;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import files.HikariFile;

import java.io.IOException;

public class Database {
    private HikariDataSource hikari;

    boolean connectToDatabase() throws IOException {
        new HikariFile();
        var resourcesDir = System.getProperty("user.dir") + "/resources";
        var hikariConfig = new HikariConfig(resourcesDir + "/hikari.properties");
        try {
            hikari = new HikariDataSource(hikariConfig);
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("hikari.properties was generated. Please fill in the data.");
            return false;
        }
    }

    public HikariDataSource getHikari() {
        return hikari;
    }
}
