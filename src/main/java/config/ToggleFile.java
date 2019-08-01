// Author: Tancred423 (https://github.com/Tancred423)
package config;

import nu.studer.java.util.OrderedProperties;

import java.io.*;

public class ToggleFile {
    private boolean autorole;
    private boolean avatar;
    private boolean baguette;
    private boolean chatbot;
    private boolean clear;
    private boolean coinflip;
    private boolean embed;
    private boolean guild;
    private boolean interaction;
    private boolean join;
    private boolean level;
    private boolean lobby;
    private boolean love;
    private boolean mediaonlychannel;
    private boolean profile;
    private boolean quickvote;
    private boolean reactionrole;
    private boolean stream;
    private boolean user;

    // Constructor.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public ToggleFile() throws IOException {
        var currentDir   = System.getProperty("user.dir");
        var resourcesDir = currentDir   + "/resources" ;
        var toggleDir    = resourcesDir + "/toggle.ini";

        // Create ./resources if it does not exist already.
        var resources = new File(resourcesDir);
        if (!resources.exists()) resources.mkdir();

        // Create ./resources/config.ini if it does not exist already.
        var toggle = new File(toggleDir);
        if (!toggle.exists()) createDefault(resourcesDir);

        try {
            readToggle(toggleDir);
        } catch (NullPointerException e) {
            toggle.delete();
            createDefault(resourcesDir);
            readToggle(toggleDir);
        }
    }

    // Write default config file.
    private void createDefault(String resourcesDir) throws IOException {
        var toggle = new OrderedProperties();
        var os = new FileOutputStream(resourcesDir + "/toggle.ini");

        toggle.setProperty("autorole", "on");
        toggle.setProperty("avatar", "on");
        toggle.setProperty("baguette", "on");
        toggle.setProperty("chatbot", "on");
        toggle.setProperty("clear", "on");
        toggle.setProperty("coinflip", "on");
        toggle.setProperty("embed", "on");
        toggle.setProperty("guild", "on");
        toggle.setProperty("interaction", "on");
        toggle.setProperty("join", "on");
        toggle.setProperty("level", "off");
        toggle.setProperty("lobby", "on");
        toggle.setProperty("love", "on");
        toggle.setProperty("mediaonlychannel", "on");
        toggle.setProperty("profile", "on");
        toggle.setProperty("quickvote", "on");
        toggle.setProperty("reactionrole", "on");
        toggle.setProperty("stream", "on");
        toggle.setProperty("user", "on");

        toggle.store(os,
                "Project: Servant\n" +
                "Author: Tancred#0001\n" +
                "GitHub: https://github.com/Tancred423/Servant");
        os.close();
    }

    // Read config file.
    private void readToggle(String configDir) throws IOException, NullPointerException {
        var toggle = new OrderedProperties();
        var is = new FileInputStream(configDir);
        toggle.load(is);

        this.autorole = toggle.getProperty("autorole").equals("on");
        this.avatar = toggle.getProperty("avatar").equals("on");
        this.baguette = toggle.getProperty("baguette").equals("on");
        this.chatbot = toggle.getProperty("chatbot").equals("on");
        this.clear = toggle.getProperty("clear").equals("on");
        this.coinflip = toggle.getProperty("coinflip").equals("on");
        this.embed = toggle.getProperty("embed").equals("on");
        this.guild = toggle.getProperty("guild").equals("on");
        this.interaction = toggle.getProperty("interaction").equals("on");
        this.join = toggle.getProperty("join").equals("on");
        this.level = toggle.getProperty("level").equals("on");
        this.lobby = toggle.getProperty("lobby").equals("on");
        this.love = toggle.getProperty("love").equals("on");
        this.mediaonlychannel = toggle.getProperty("mediaonlychannel").equals("on");
        this.profile = toggle.getProperty("profile").equals("on");
        this.quickvote = toggle.getProperty("quickvote").equals("on");
        this.reactionrole = toggle.getProperty("reactionrole").equals("on");
        this.stream = toggle.getProperty("stream").equals("on");
        this.user = toggle.getProperty("user").equals("on");
    }

    // Getter.
    public boolean get(String feature) {
        switch (feature.toLowerCase()) {
            case "autorole": return autorole;
            case "avatar": return avatar;
            case "baguette": return baguette;
            case "chatbot": return chatbot;
            case "clear": return clear;
            case "coinflip": return coinflip;
            case "embed": return embed;
            case "guild": return guild;
            case "interaction": return interaction;
            case "join": return join;
            case "level": return level;
            case "lobby": return lobby;
            case "love": return love;
            case "mediaonlychannel": return mediaonlychannel;
            case "profile": return profile;
            case "quickvote": return quickvote;
            case "reactionrole": return reactionrole;
            case "stream": return stream;
            case "user": return user;
            default: return true;
        }
    }
}
