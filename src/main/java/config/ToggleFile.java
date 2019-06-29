package config;

import nu.studer.java.util.OrderedProperties;

import java.io.*;

/*
 * You may ask how big my autism is.
 * My answer is yes.
 */
public class ToggleFile {
    private boolean autorole;
    private boolean clear;
    private boolean guild;
    private boolean join;
    private boolean mediaonlychannel;
    private boolean coinflip;
    private boolean level;
    private boolean avatar;
    private boolean user;
    private boolean interaction;
    private boolean baguette;

    // Constructor.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public ToggleFile() throws IOException {
        String currentDir   = System.getProperty("user.dir");
        String resourcesDir = currentDir   + "/resources" ;
        String toggleDir    = resourcesDir + "/toggle.ini";

        // Create ./resources if it does not exist already.
        File resources = new File(resourcesDir);
        if (!resources.exists()) resources.mkdir();

        // Create ./resources/config.ini if it does not exist already.
        File toggle = new File(toggleDir);
        if (!toggle.exists()) createDefault(resourcesDir);

        readToggle(toggleDir);
    }

    // Write default config file.
    private void createDefault(String resourcesDir) throws IOException {
        OrderedProperties config = new OrderedProperties();
        OutputStream os = new FileOutputStream(resourcesDir + "/toggle.ini");

        config.setProperty("autorole", "on");
        config.setProperty("clear", "on");
        config.setProperty("guild", "on");
        config.setProperty("join", "on");
        config.setProperty("mediaonlychannel", "on");
        config.setProperty("coinflip", "on");
        config.setProperty("level", "off");
        config.setProperty("avatar", "on");
        config.setProperty("user", "on");
        config.setProperty("interaction", "on");
        config.getProperty("baguette", "on");

        config.store(os,
                "Project: Servant\n" +
                "Author: Tancred#0001\n" +
                "GitHub: https://github.com/Tancred423/Servant");
        os.close();
    }

    // Read config file.
    private void readToggle(String configDir) throws IOException {
        OrderedProperties config = new OrderedProperties();
        InputStream is = new FileInputStream(configDir);
        config.load(is);

        this.autorole = config.getProperty("autorole").equals("on");
        this.clear = config.getProperty("clear").equals("on");
        this.guild = config.getProperty("guild").equals("on");
        this.join = config.getProperty("join").equals("on");
        this.mediaonlychannel = config.getProperty("mediaonlychannel").equals("on");
        this.coinflip = config.getProperty("coinflip").equals("on");
        this.level = config.getProperty("level").equals("on");
        this.avatar = config.getProperty("avatar").equals("on");
        this.user = config.getProperty("user").equals("on");
        this.interaction = config.getProperty("interaction").equals("on");
        this.baguette = config.getProperty("baguette").equals("on");
    }

    // Setter.
    public void setAutorole (String autorole) { this.autorole = autorole.equals("on"); }
    public void setClear (String clear) { this.clear  = clear.equals("on"); }
    public void setGuild (String guild) { this.guild = guild.equals("on"); }
    public void setJoin (String join) { this.join = join.equals("on"); }
    public void setMediaonlychannel (String mediaonlychannel   ) { this.mediaonlychannel = mediaonlychannel.equals("on"); }
    public void setCoinflip (String coinflip) { this.coinflip = coinflip.equals("on"); }
    public void setLevel (String level) { this.level = level.equals("on"); }
    public void setAvatar (String avatar) { this.avatar = avatar.equals("on"); }
    public void setUser (String user) { this.user = user.equals("on"); }
    public void setInteraction (String interaction) { this.interaction = interaction.equals("on"); }
    public void setBaguette (String baguette) { this.baguette = baguette.equals("on"); }

    // Getter.
    public boolean get(String feature) {
        switch (feature.toLowerCase()) {
            case "autorole":
                return autorole;
            case "clear":
                return clear;
            case "guild":
                return guild;
            case "join":
                return join;
            case "mediaonlychannel":
                return mediaonlychannel;
            case "coinflip":
                return coinflip;
            case "level":
                return level;
            case "avatar":
                return avatar;
            case "user":
                return user;
            case "interaction":
                return interaction;
            case "baguette":
                return baguette;
            default:
                return true;
        }
    }

    public boolean getAutorole() { return autorole; }
    public boolean getClear() { return clear; }
    public boolean getGuild() { return guild; }
    public boolean getJoin() { return join; }
    public boolean getMediaOnlyChannel() { return mediaonlychannel; }
    public boolean getCoinflip() { return coinflip; }
    public boolean getLevel() { return level; }
    public boolean getAvatar() { return avatar; }
    public boolean getUser() { return user; }
    public boolean getInteraction() { return interaction; }
    public boolean getBaguette() { return baguette; }
}
