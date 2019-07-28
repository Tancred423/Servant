package config;

import nu.studer.java.util.OrderedProperties;

import java.io.*;

/*
 * You may ask how big my autism is.
 * My answer is yes.
 */
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
    private boolean reactionrole;
    private boolean user;

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
        OrderedProperties toggle = new OrderedProperties();
        OutputStream os = new FileOutputStream(resourcesDir + "/toggle.ini");

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
        toggle.setProperty("reactionrole", "on");
        toggle.setProperty("user", "on");

        toggle.store(os,
                "Project: Servant\n" +
                "Author: Tancred#0001\n" +
                "GitHub: https://github.com/Tancred423/Servant");
        os.close();
    }

    // Read config file.
    private void readToggle(String configDir) throws IOException, NullPointerException {
        OrderedProperties toggle = new OrderedProperties();
        InputStream is = new FileInputStream(configDir);
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
        this.reactionrole = toggle.getProperty("reactionrole").equals("on");
        this.user = toggle.getProperty("user").equals("on");
    }

    // Setter.
    public void setAutorole (String autorole) { this.autorole = autorole.equals("on"); }
    public void setAvatar (String avatar) { this.avatar = avatar.equals("on"); }
    public void setBaguette (String baguette) { this.baguette = baguette.equals("on"); }
    public void setChatbot (String chatbot) { this.chatbot = chatbot.equals("on"); }
    public void setClear (String clear) { this.clear  = clear.equals("on"); }
    public void setCoinflip (String coinflip) { this.coinflip = coinflip.equals("on"); }
    public void setEmbed (String embed) { this.embed = embed.equals("on"); }
    public void setGuild (String guild) { this.guild = guild.equals("on"); }
    public void setInteraction (String interaction) { this.interaction = interaction.equals("on"); }
    public void setJoin (String join) { this.join = join.equals("on"); }
    public void setLevel (String level) { this.level = level.equals("on"); }
    public void setLobby (String lobby) { this.lobby = lobby.equals("on"); }
    public void setLove (String love) { this.love = love.equals("on"); }
    public void setMediaonlychannel (String mediaonlychannel) { this.mediaonlychannel = mediaonlychannel.equals("on"); }
    public void setProfile (String profile) { this.profile = profile.equals("on"); }
    public void setReactionrole (String reactionrole) { this.reactionrole = reactionrole.equals("on"); }
    public void setUser (String user) { this.user = user.equals("on"); }

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
            case "reactionrole": return reactionrole;
            case "user": return user;
            default: return true;
        }
    }

    public boolean getAutorole() { return autorole; }
    public boolean getAvatar() { return avatar; }
    public boolean getBaguette() { return baguette; }
    public boolean getChatbot() { return chatbot; }
    public boolean getClear() { return clear; }
    public boolean getCoinflip() { return coinflip; }
    public boolean getEmbed() { return embed; }
    public boolean getGuild() { return guild; }
    public boolean getInteraction() { return interaction; }
    public boolean getJoin() { return join; }
    public boolean getLevel() { return level; }
    public boolean getLobby() { return lobby; }
    public boolean getLove() { return love; }
    public boolean getMediaOnlyChannel() { return mediaonlychannel; }
    public boolean getProfile() { return profile; }
    public boolean getReactionRole() { return reactionrole; }
    public boolean getUser() { return user; }
}
