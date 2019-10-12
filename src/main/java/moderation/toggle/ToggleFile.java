// Author: Tancred423 (https://github.com/Tancred423)
package moderation.toggle;

import nu.studer.java.util.OrderedProperties;

import java.io.*;

public class ToggleFile {
    // Moderation
    private boolean autorole;
    private boolean birthday;
    private boolean bestofimage;
    private boolean bestofquote;
    private boolean clear;
    private boolean join;
    private boolean leave;
    private boolean levelrole;
    private boolean livestream;
    private boolean mediaonlychannel;
    private boolean reactionrole;
    private boolean role;
    private boolean server;
    private boolean setupwizard;
    private boolean user;
    private boolean voicelobby;

    // Information
    private boolean botinfo;
    private boolean guildinfo;

    // Useful
    private boolean alarm;
    private boolean giveaway;
    private boolean reminder;
    private boolean signup;
    private boolean timezone;

    /// Votes
    private boolean quickvote;
    private boolean vote;

    // Fun
    private boolean achievement;
    private boolean avatar;
    private boolean baguette;
    private boolean bird;
    private boolean cat;
    private boolean coinflip;
    private boolean createembed;
    private boolean dog;
    private boolean editembed;
    private boolean flip;
    private boolean level;
    private boolean love;
    private boolean profile;

    /// Interaction
    private boolean interaction;

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

        // Moderation
        toggle.setProperty("autorole", "on");
        toggle.setProperty("birthday", "on");
        toggle.setProperty("bestofimage", "on");
        toggle.setProperty("bestofquote", "on");
        toggle.setProperty("clear", "on");
        toggle.setProperty("guild", "on");
        toggle.setProperty("join", "on");
        toggle.setProperty("leave", "on");
        toggle.setProperty("levelrole", "on");
        toggle.setProperty("livestream", "on");
        toggle.setProperty("mediaonlychannel", "on");
        toggle.setProperty("reactionrole", "on");
        toggle.setProperty("role", "on");
        toggle.setProperty("setupwizard", "on");
        toggle.setProperty("user", "on");
        toggle.setProperty("voicelobby", "on");

        // Information
        toggle.setProperty("botinfo", "on");
        toggle.setProperty("guildinfo", "on");

        // Useful
        toggle.setProperty("alarm", "on");
        toggle.setProperty("giveaway", "on");
        toggle.setProperty("reminder", "on");
        toggle.setProperty("signup", "on");
        toggle.setProperty("timezone", "on");

        /// Votes
        toggle.setProperty("quickvote", "on");
        toggle.setProperty("vote", "on");

        // Fun
        toggle.setProperty("easteregg", "on");
        toggle.setProperty("avatar", "on");
        toggle.setProperty("baguette", "on");
        toggle.setProperty("bird", "on");
        toggle.setProperty("cat", "on");
        toggle.setProperty("coinflip", "on");
        toggle.setProperty("createembed", "on");
        toggle.setProperty("dog", "on");
        toggle.setProperty("editembed", "on");
        toggle.setProperty("flip", "on");
        toggle.setProperty("level", "off");
        toggle.setProperty("love", "on");
        toggle.setProperty("profile", "on");

        /// Interaction
        toggle.setProperty("interaction", "on");

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

        // Moderation
        this.autorole = toggle.getProperty("autorole").equals("on");
        this.birthday = toggle.getProperty("birthday").equals("on");
        this.bestofimage = toggle.getProperty("bestofimage").equals("on");
        this.bestofquote = toggle.getProperty("bestofquote").equals("on");
        this.clear = toggle.getProperty("clear").equals("on");
        this.server = toggle.getProperty("guild").equals("on");
        this.join = toggle.getProperty("join").equals("on");
        this.leave = toggle.getProperty("leave").equals("on");
        this.levelrole = toggle.getProperty("levelrole").equals("on");
        this.livestream = toggle.getProperty("livestream").equals("on");
        this.mediaonlychannel = toggle.getProperty("mediaonlychannel").equals("on");
        this.reactionrole = toggle.getProperty("reactionrole").equals("on");
        this.role = toggle.getProperty("role").equals("on");
        this.setupwizard = toggle.getProperty("setupwizard").equals("on");
        this.user = toggle.getProperty("user").equals("on");
        this.voicelobby = toggle.getProperty("voicelobby").equals("on");

        // Information
        this.botinfo = toggle.getProperty("botinfo").equals("on");
        this.guildinfo = toggle.getProperty("guildinfo").equals("on");

        // Useful
        this.alarm = toggle.getProperty("alarm").equals("on");
        this.giveaway = toggle.getProperty("giveaway").equals("on");
        this.reminder = toggle.getProperty("reminder").equals("on");
        this.signup = toggle.getProperty("signup").equals("on");
        this.timezone = toggle.getProperty("timezone").equals("on");

        /// Votes
        this.quickvote = toggle.getProperty("quickvote").equals("on");
        this.vote = toggle.getProperty("vote").equals("on");

        // Fun
        this.achievement = toggle.getProperty("easteregg").equals("on");
        this.avatar = toggle.getProperty("avatar").equals("on");
        this.baguette = toggle.getProperty("baguette").equals("on");
        this.bird = toggle.getProperty("bird").equals("on");
        this.cat = toggle.getProperty("cat").equals("on");
        this.coinflip = toggle.getProperty("coinflip").equals("on");
        this.createembed = toggle.getProperty("createembed").equals("on");
        this.dog = toggle.getProperty("dog").equals("on");
        this.editembed = toggle.getProperty("editembed").equals("on");
        this.flip = toggle.getProperty("flip").equals("on");
        this.level = toggle.getProperty("level").equals("on");
        this.love = toggle.getProperty("love").equals("on");
        this.profile = toggle.getProperty("profile").equals("on");

        /// Interaction
        this.interaction = toggle.getProperty("interaction").equals("on");
    }

    // Getter.
    public boolean get(String feature) {
        switch (feature.toLowerCase()) {
            // Moderation
            case "autorole": return autorole;
            case "birthday": return birthday;
            case "bestofimage": return bestofimage;
            case "bestofquote": return bestofquote;
            case "clear": return clear;
            case "guild": return server;
            case "join": return join;
            case "leave": return leave;
            case "levelrole": return levelrole;
            case "livestream": return livestream;
            case "mediaonlychannel": return mediaonlychannel;
            case "reactionrole": return reactionrole;
            case "role": return role;
            case "setupwizard": return setupwizard;
            case "user": return user;
            case "voicelobby": return voicelobby;

            // Information
            case "botinfo": return botinfo;
            case "guildinfo": return guildinfo;

            // Useful
            case "alarm": return alarm;
            case "giveaway": return giveaway;
            case "reminder": return reminder;
            case "signup": return signup;
            case "timezone": return timezone;

            /// Votes
            case "quickvote": return quickvote;
            case "vote": return vote;

            // Fun
            case "easteregg": return achievement;
            case "avatar": return avatar;
            case "baguette": return baguette;
            case "bird": return bird;
            case "cat": return cat;
            case "coinflip": return coinflip;
            case "createembed": return createembed;
            case "dog": return dog;
            case "editembed": return editembed;
            case "flip": return flip;
            case "level": return level;
            case "love": return love;
            case "profile": return profile;

            /// Interaction
            case "interaction": return interaction;

            default: return true;
        }
    }
}
