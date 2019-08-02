// Author: Tancred423 (https://github.com/Tancred423)
package config;

import nu.studer.java.util.OrderedProperties;

import java.io.*;

public class ConfigFile {
    private String botToken;
    private String defaultLanguage;
    private String defaultOffset;
    private String defaultColorCode;
    private String defaultPrefix;
    private String expCdMillis;
    private String botOwnerId;
    private String databaseUrl;
    private String databaseUsername;
    private String databasePassword;
    private String supportGuildInv;
    private String botVersion;

    // Constructor.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public ConfigFile() throws IOException {
        var currentDir = System.getProperty("user.dir");
        var resourcesDir = currentDir + "/resources";
        var configDir = resourcesDir + "/config.ini";

        // Create ./resources if it does not exist already.
        var resources = new File(resourcesDir);
        if (!resources.exists()) resources.mkdir();

        // Create ./resources/config.ini if it does not exist already.
        var config = new File(configDir);
        if (!config.exists()) createDefault(resourcesDir);

        readConfig(configDir);
    }

    // Write default config file.
    private void createDefault(String resourcesDir) throws IOException {
        var config = new OrderedProperties();
        var os = new FileOutputStream(resourcesDir + "/config.ini");

        config.setProperty("botToken", "");
        config.setProperty("defaultLanguage", "en"); // English.
        config.setProperty("defaultOffset", "00:00"); // UTC.
        config.setProperty("defaultColorCode", "0x6c86d5"); // Discord-themed blue.
        config.setProperty("defaultPrefix", "!");
        config.setProperty("expCdMillis", "60000"); // 1 minute.
        config.setProperty("botOwnerId", "");
        config.setProperty("databaseUrl", "");
        config.setProperty("databaseUsername", "root");
        config.setProperty("databasePassword", "");
        config.setProperty("supportGuildInv", "");
        config.setProperty("botVersion", "4.15.4");

        config.store(os,
                "Project: Servant\n" +
                "Author: Tancred#0001\n" +
                "GitHub: https://github.com/Tancred423/Servant");
        os.close();
    }

    // Read config file.
    private void readConfig(String configDir) throws IOException {
        var config = new OrderedProperties();
        var is = new FileInputStream(configDir);
        config.load(is);

        this.botToken = config.getProperty("botToken");
        this.defaultLanguage = config.getProperty("defaultLanguage");
        this.defaultOffset = config.getProperty("defaultOffset");
        this.defaultColorCode = config.getProperty("defaultColorCode");
        this.defaultPrefix = config.getProperty("defaultPrefix");
        this.expCdMillis = config.getProperty("expCdMillis");
        this.botOwnerId = config.getProperty("botOwnerId");
        this.databaseUrl = config.getProperty("databaseUrl");
        this.databaseUsername = config.getProperty("databaseUsername");
        this.databasePassword = config.getProperty("databasePassword");
        this.supportGuildInv = config.getProperty("supportGuildInv");
        this.botVersion = config.getProperty("botVersion");
    }

    // Getter.
    public String getBotToken() { return botToken; }
    public String getDefaultLanguage() { return defaultLanguage; }
    public String getDefaultOffset() { return defaultOffset; }
    public String getDefaultColorCode() { return defaultColorCode; }
    public String getDefaultPrefix() { return defaultPrefix; }
    public String getExpCdMillis() { return expCdMillis; }
    public String getBotOwnerId() { return botOwnerId; }
    public String getDatabaseUrl() { return databaseUrl; }
    public String getDatabaseUsername() { return databaseUsername; }
    public String getDatabasePassword() { return databasePassword; }
    public String getSupportGuildInv() { return supportGuildInv; }
    public String getBotVersion() { return botVersion; }

    // Checks.
    public boolean isMissing() {
        var corrupted = false;
        var missing = false;
        var shutdown = false;

        var errorMessage = "Please add a %s to the ./resources/config.ini file.";
        var corruptedMessage = "The config file is corrupted.\n" +
                "1. Backup your values.\n" +
                "2. Delete the ./resources/config.ini file.\n" +
                "3. Start the bot to generate a new config.ini file.\n" +
                "Remember not to delete entries from the config.ini file.";

        // No config parameter is allowed to be null or empty.
        if (getBotToken() == null) corrupted = true;
        if (getDefaultLanguage() == null) corrupted = true;
        if (getDefaultOffset() == null) corrupted = true;
        if (getDefaultColorCode() == null) corrupted = true;
        if (getDefaultPrefix() == null) corrupted = true;
        if (getExpCdMillis() == null) corrupted = true;
        if (getBotOwnerId() == null) corrupted = true;
        if (getDatabaseUrl() == null) corrupted = true;
        if (getDatabaseUsername() == null) corrupted = true;
        if (getDatabasePassword() == null) corrupted = true;
        if (getSupportGuildInv() == null) corrupted = true;
        if (getBotVersion() == null) corrupted = true;

        if (corrupted) System.out.println(corruptedMessage);
        else {
            if (getBotToken().isEmpty()) {
                System.out.println(String.format(errorMessage, "bot token"));
                missing = true;
            }

            if (getDefaultLanguage().isEmpty()) {
                System.out.println(String.format(errorMessage, "default language"));
                missing = true;
            }

            if (getDefaultOffset().isEmpty()) {
                System.out.println(String.format(errorMessage, "default offset"));
                missing = true;
            }

            if (getDefaultColorCode().isEmpty()) {
                System.out.println(String.format(errorMessage, "default color code"));
                missing = true;
            }

            if (getDefaultPrefix().isEmpty()) {
                System.out.println(String.format(errorMessage, "default prefix"));
                missing = true;
            }

            if (getExpCdMillis().isEmpty()) {
                System.out.println(String.format(errorMessage, "cooldown for experience gaining in milliseconds"));
                missing = true;
            }

            if (getBotOwnerId().isEmpty()) {
                System.out.println(String.format(errorMessage, "bot owner (user) ID"));
                missing = true;
            }

            if (getDatabaseUrl().isEmpty()) {
                System.out.println(String.format(errorMessage, "database URL"));
                missing = true;
            }

            if (getDatabaseUsername().isEmpty()) {
                System.out.println(String.format(errorMessage, "database username"));
                missing = true;
            }

            if (getDatabasePassword().isEmpty()) {
                System.out.println(String.format(errorMessage, "database password"));
                missing = true;
            }

            if (getSupportGuildInv().isEmpty()) {
                System.out.println(String.format(errorMessage, "support guild invite link"));
                missing = true;
            }

            if (getBotVersion().isEmpty()) {
                System.out.println(String.format(errorMessage, "bot version"));
                missing = true;
            }
        }

        if (corrupted || missing) shutdown = true;
        return shutdown;
    }
}
