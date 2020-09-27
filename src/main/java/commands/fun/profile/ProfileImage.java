// Author: Tancred423 (https://github.com/Tancred423)
package commands.fun.profile;

import commands.interaction.Interaction;
import files.language.LanguageHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import servant.MyGuild;
import servant.MyUser;
import utilities.ImageUtil;
import utilities.Parser;
import utilities.Resizer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileImage {
    private final JDA jda;
    private final User user;
    private final Guild guild;
    private final String lang;

    ProfileImage(JDA jda, User user, Guild guild, String lang) {
        this.jda = jda;
        this.user = user;
        this.guild = guild;
        this.lang = lang;
    }

    public BufferedImage generateImage() {
        // Internal User & Guild
        var myUser = new MyUser(user);
        var myGuild = new MyGuild(guild);

        // Fonts
        var myriadPlain100pt = new Font("Myriad Pro", Font.PLAIN, 100); // Name, Bio
        var myriadPlain75pt = new Font("Myriad Pro", Font.PLAIN, 75); // Patreon,
        var myriadPlain60pt = new Font("Myriad Pro", Font.PLAIN, 60); // General Info
        var myriadPlain50pt = new Font("Myriad Pro", Font.PLAIN, 50); // MOC
        var myriadPlain40pt = new Font("Myriad Pro", Font.PLAIN, 40); // Command Info, Coloured Titles, Counter

        // Colors
        var myUserColor = Color.decode(myUser.getColorCode());

        // Canvas
        var width = 2000;
        var height = 1500;
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g2d.drawImage(getBg(myUser), 0, 0, null);
        g2d.drawImage(getOverlay(jda), 0, 0, null);

        // Avatar
        var avaX = 750; // X and Y is not centered
        var avaY = 500;
        var avaWH = 500;
        var avatar = getAvatar();
        if (avatar != null) {
            avatar = Resizer.PROGRESSIVE_BILINEAR.resize(avatar, avaWH, avaWH);
            avatar = circle(avatar);
            g2d.drawImage(avatar, avaX, avaY, null);
        }

        // EXP
        var currentExp = myUser.getExp(guild.getIdLong());
        var currentLevel = Parser.getLevelFromExp(currentExp);
        var neededExp = Parser.getLevelExp(currentLevel);
        var currentExpOnThisLevel = currentExp - Parser.getTotalLevelExp(currentLevel - 1);
        var percent = ((float) currentExpOnThisLevel) / neededExp;

        // EXP Ring
        var expRing = createRingShape(percent); // X and Y is centered
        g2d.setColor(myUserColor);
        g2d.fill(expRing);
        g2d.setColor(Color.BLACK);
        g2d.draw(expRing);

        // User Name
        var name = user.getName();
        var rect = new Rectangle(0, 500, 800, 450);
        drawCenteredString(g2d, name, rect, myriadPlain100pt, Color.WHITE);

        // Patreon Title
        var title = getTitle(myUser);
        rect = new Rectangle(0, 750, 800, 100);
        drawCenteredString(g2d, title, rect, myriadPlain75pt, myUserColor);

        // Bio
        var bio = myUser.getBio();
        if (bio != null && !bio.isEmpty()) {
            var bioWithLineBreaks = getLineBreaks(bio);
            if (bioWithLineBreaks.size() == 1) {
                // One Line
                rect = new Rectangle(1300, 500, 630, 500);
                drawCenteredString(g2d, bioWithLineBreaks.get(0).trim(), rect, myriadPlain100pt, Color.WHITE);
            } else if (bioWithLineBreaks.size() == 2) {
                // Two Lines
                var line = bioWithLineBreaks.get(0).trim();
                rect = new Rectangle(1300, 500, 630, 400);
                drawCenteredString(g2d, line, rect, myriadPlain100pt, Color.WHITE);

                line = bioWithLineBreaks.get(1).trim();
                rect = new Rectangle(1300, 550, 630, 500);
                drawCenteredString(g2d, line, rect, myriadPlain100pt, Color.WHITE);
            } else if (bioWithLineBreaks.size() >= 3) {
                // Three Lines
                var line = bioWithLineBreaks.get(0).trim();
                rect = new Rectangle(1300, 500, 630, 300);
                drawCenteredString(g2d, line, rect, myriadPlain100pt, Color.WHITE);

                line = bioWithLineBreaks.get(1).trim();
                rect = new Rectangle(1300, 550, 630, 400);
                drawCenteredString(g2d, line, rect, myriadPlain100pt, Color.WHITE);

                var thirdLine = new StringBuilder();
                for (int i = 2; i < bioWithLineBreaks.size(); i++)
                    thirdLine.append(bioWithLineBreaks.get(i)).append(" ");
                line = thirdLine.toString().trim();
                rect = new Rectangle(1300, 600, 630, 500);
                drawCenteredString(g2d, line, rect, myriadPlain100pt, Color.WHITE);
            }
        }

        // Titles: General Info, Command Stats, Most Used Commands, Achievements
        var generalInfo = LanguageHandler.get(lang, "profile_generalinfo").toUpperCase();
        var commandStats = LanguageHandler.get(lang, "profile_commandstats").toUpperCase();
        var mostUsedCommands = LanguageHandler.get(lang, "profile_mostused").toUpperCase();
        var achievements = LanguageHandler.get(lang, "profile_achievements").toUpperCase();

        rect = new Rectangle(0, 0, 1000, 100);
        drawCenteredString(g2d, generalInfo, rect, myriadPlain40pt, myUserColor);

        rect = new Rectangle(1000, 0, 1000, 100);
        drawCenteredString(g2d, commandStats, rect, myriadPlain40pt, myUserColor);

        rect = new Rectangle(0, 1000, 1000, 100);
        drawCenteredString(g2d, mostUsedCommands, rect, myriadPlain40pt, myUserColor);

        rect = new Rectangle(1000, 1000, 1000, 100);
        drawCenteredString(g2d, achievements, rect, myriadPlain40pt, myUserColor);

        // General Info - Level
        var levelText = LanguageHandler.get(lang, "profile_level");
        var levelX = 100;
        var levelY = 150;
        float outline = 10;
        var frc = g2d.getFontRenderContext();
        var layout = new TextLayout(levelText, myriadPlain60pt, frc);
        var shape = layout.getOutline(null);

        g2d.setColor(Color.BLACK);
        g2d.translate(levelX, levelY);
        g2d.setStroke(new BasicStroke(outline));
        g2d.draw(shape);
        g2d.setColor(Color.WHITE);
        g2d.fill(shape);
        g2d.translate(levelX * -1, levelY * -1); // Reset

        var levelValue = String.valueOf(currentLevel);
        var generalInfoWidth = 900;
        var levelActualWidth = g2d.getFontMetrics().stringWidth(levelValue);
        levelX = generalInfoWidth - levelActualWidth;

        layout = new TextLayout(levelValue, myriadPlain60pt, frc);
        shape = layout.getOutline(null);

        g2d.setColor(Color.BLACK);
        g2d.translate(levelX, levelY);
        g2d.setStroke(new BasicStroke(outline));
        g2d.draw(shape);
        g2d.setColor(Color.WHITE);
        g2d.fill(shape);
        g2d.translate(levelX * -1, levelY * -1); // Reset

        // General Info - Rank
        var rankText = LanguageHandler.get(lang, "profile_rank");
        var rankX = 100;
        var rankY = 250;
        layout = new TextLayout(rankText, myriadPlain60pt, frc);
        shape = layout.getOutline(null);

        g2d.setColor(Color.BLACK);
        g2d.translate(rankX, rankY);
        g2d.setStroke(new BasicStroke(outline));
        g2d.draw(shape);
        g2d.setColor(Color.WHITE);
        g2d.fill(shape);
        g2d.translate(rankX * -1, rankY * -1); // Reset

        var rankValue = String.valueOf(myGuild.getUserRank(user.getIdLong()));
        var rankActualWidth = g2d.getFontMetrics().stringWidth(rankValue);
        rankX = generalInfoWidth - rankActualWidth;

        layout = new TextLayout(rankValue, myriadPlain60pt, frc);
        shape = layout.getOutline(null);

        g2d.setColor(Color.BLACK);
        g2d.translate(rankX, rankY);
        g2d.setStroke(new BasicStroke(outline));
        g2d.draw(shape);
        g2d.setColor(Color.WHITE);
        g2d.fill(shape);
        g2d.translate(rankX * -1, rankY * -1); // Reset

        // General Info - Commands Used
        var commandsUsedText = LanguageHandler.get(lang, "commands_used");
        var commandsUsedX = 100;
        var commandsUsedY = 350;
        layout = new TextLayout(commandsUsedText, myriadPlain60pt, frc);
        shape = layout.getOutline(null);

        g2d.setColor(Color.BLACK);
        g2d.translate(commandsUsedX, commandsUsedY);
        g2d.setStroke(new BasicStroke(outline));
        g2d.draw(shape);
        g2d.setColor(Color.WHITE);
        g2d.fill(shape);
        g2d.translate(commandsUsedX * -1, commandsUsedY * -1); // Reset

        var commandsUsedValue = String.valueOf(myUser.getCommandsTotalCount());
        var commandsUsedActualWidth = g2d.getFontMetrics().stringWidth(commandsUsedValue);
        commandsUsedX = generalInfoWidth - commandsUsedActualWidth;

        layout = new TextLayout(commandsUsedValue, myriadPlain60pt, frc);
        shape = layout.getOutline(null);

        g2d.setColor(Color.BLACK);
        g2d.translate(commandsUsedX, commandsUsedY);
        g2d.setStroke(new BasicStroke(outline));
        g2d.draw(shape);
        g2d.setColor(Color.WHITE);
        g2d.fill(shape);
        g2d.translate(commandsUsedX * -1, commandsUsedY * -1); // Reset

        // General Info - Achivement Points
        var apText = LanguageHandler.get(lang, "ap");
        var apX = 100;
        var apY = 450;
        layout = new TextLayout(apText, myriadPlain60pt, frc);
        shape = layout.getOutline(null);

        g2d.setColor(Color.BLACK);
        g2d.translate(apX, apY);
        g2d.setStroke(new BasicStroke(outline));
        g2d.draw(shape);
        g2d.setColor(Color.WHITE);
        g2d.fill(shape);
        g2d.translate(apX * -1, apY * -1); // Reset

        var apValue = String.valueOf(myUser.getTotalAP());
        var apActualWidth = g2d.getFontMetrics().stringWidth(apValue);
        apX = generalInfoWidth - apActualWidth;

        layout = new TextLayout(apValue, myriadPlain60pt, frc);
        shape = layout.getOutline(null);

        g2d.setColor(Color.BLACK);
        g2d.translate(apX, apY);
        g2d.setStroke(new BasicStroke(outline));
        g2d.draw(shape);
        g2d.setColor(Color.WHITE);
        g2d.fill(shape);
        g2d.translate(apX * -1, apY * -1); // Reset

        // Commands Stats - Baguette
        var baguetteText = LanguageHandler.get(lang, "profile_baguette");
        var baguetteX = 1100;
        var baguetteY = 140;
        layout = new TextLayout(baguetteText, myriadPlain40pt, frc);
        shape = layout.getOutline(null);

        g2d.setColor(Color.BLACK);
        g2d.translate(baguetteX, baguetteY);
        g2d.setStroke(new BasicStroke(outline));
        g2d.draw(shape);
        g2d.setColor(Color.WHITE);
        g2d.fill(shape);
        g2d.translate(baguetteX * -1, baguetteY * -1); // Reset

        var baguette = myUser.getBaguette();
        var baguetteValue = baguette == null ? LanguageHandler.get(lang, "profile_nobaguette") :
                String.format(LanguageHandler.get(lang, "profile_baguette_value"), baguette.getSize(), baguette.getCounter());

        var commandsStatsWidth = 1900;
        var baguetteActualWidth = g2d.getFontMetrics().stringWidth(baguetteValue);
        baguetteX = commandsStatsWidth - baguetteActualWidth;

        layout = new TextLayout(baguetteValue, myriadPlain40pt, frc);
        shape = layout.getOutline(null);

        g2d.setColor(Color.BLACK);
        g2d.translate(baguetteX, baguetteY);
        g2d.setStroke(new BasicStroke(outline));
        g2d.draw(shape);
        g2d.setColor(Color.WHITE);
        g2d.fill(shape);
        g2d.translate(baguetteX * -1, baguetteY * -1); // Reset

        // Commands Stats - Favourite Animal
        var animalText = LanguageHandler.get(lang, "profile_animal");
        var animalX = 1100;
        var animalY = 220;
        layout = new TextLayout(animalText, myriadPlain40pt, frc);
        shape = layout.getOutline(null);

        g2d.setColor(Color.BLACK);
        g2d.translate(animalX, animalY);
        g2d.setStroke(new BasicStroke(outline));
        g2d.draw(shape);
        g2d.setColor(Color.WHITE);
        g2d.fill(shape);
        g2d.translate(animalX * -1, animalY * -1); // Reset

        var animalValue = myUser.getFavoriteAnimal(lang);
        if (animalValue != null) {
            var animalActualWidth = g2d.getFontMetrics().stringWidth(animalValue);
            animalX = commandsStatsWidth - animalActualWidth;

            layout = new TextLayout(animalValue, myriadPlain40pt, frc);
            shape = layout.getOutline(null);

            g2d.setColor(Color.BLACK);
            g2d.translate(animalX, animalY);
            g2d.setStroke(new BasicStroke(outline));
            g2d.draw(shape);
            g2d.setColor(Color.WHITE);
            g2d.fill(shape);
            g2d.translate(animalX * -1, animalY * -1); // Reset
        }

        // Commands Stats - Most Shared Interaction
        var msiText = LanguageHandler.get(lang, "profile_msi");
        var msiX = 1100;
        var msiY = 300;
        layout = new TextLayout(msiText, myriadPlain40pt, frc);
        shape = layout.getOutline(null);

        g2d.setColor(Color.BLACK);
        g2d.translate(msiX, msiY);
        g2d.setStroke(new BasicStroke(outline));
        g2d.draw(shape);
        g2d.setColor(Color.WHITE);
        g2d.fill(shape);
        g2d.translate(msiX * -1, msiY * -1); // Reset

        var interactions = myUser.getInteractionCounts();
        var msi = getMsi(interactions);
        if (msi != null) {
            var msiValue = msi.getName();
            var msiActualWidth = g2d.getFontMetrics().stringWidth(msiValue);
            msiX = commandsStatsWidth - msiActualWidth;

            layout = new TextLayout(msiValue, myriadPlain40pt, frc);
            shape = layout.getOutline(null);

            g2d.setColor(Color.BLACK);
            g2d.translate(msiX, msiY);
            g2d.setStroke(new BasicStroke(outline));
            g2d.draw(shape);
            g2d.setColor(Color.WHITE);
            g2d.fill(shape);
            g2d.translate(msiX * -1, msiY * -1); // Reset
        }

        // Commands Stats - Most Received Interaction
        var mriText = LanguageHandler.get(lang, "profile_mri");
        var mriX = 1100;
        var mriY = 380;
        layout = new TextLayout(mriText, myriadPlain40pt, frc);
        shape = layout.getOutline(null);

        g2d.setColor(Color.BLACK);
        g2d.translate(mriX, mriY);
        g2d.setStroke(new BasicStroke(outline));
        g2d.draw(shape);
        g2d.setColor(Color.WHITE);
        g2d.fill(shape);
        g2d.translate(mriX * -1, mriY * -1); // Reset

        var mri = getMri(interactions);
        if (mri != null) {
            var mriValue = mri.getName();
            var mriActualWidth = g2d.getFontMetrics().stringWidth(mriValue);
            mriX = commandsStatsWidth - mriActualWidth;

            layout = new TextLayout(mriValue, myriadPlain40pt, frc);
            shape = layout.getOutline(null);

            g2d.setColor(Color.BLACK);
            g2d.translate(mriX, mriY);
            g2d.setStroke(new BasicStroke(outline));
            g2d.draw(shape);
            g2d.setColor(Color.WHITE);
            g2d.fill(shape);
            g2d.translate(mriX * -1, mriY * -1); // Reset
        }

        // Most Used Commands
        var featureIcons = getCommandIcons(jda);
        var featureCounts = myUser.getCommandCounts();

        var i = 0;
        for (var entry : featureCounts.entrySet()) {
            if (i == 24) break;

            var coords = new ProfileCoordinates(i);
            var x = coords.getX();
            var y = coords.getY();

            // Set Icon
            var icon = getIcon(featureIcons.get(entry.getKey()));
            if (icon != null) {
                g2d.drawImage(icon, x, y, null);

                // Set Text
                rect = new Rectangle(x, y + 73, 64, 45);
                drawCenteredString(g2d, String.valueOf(entry.getValue()), rect, myriadPlain50pt, Color.WHITE);

                i++;
            }
        }

        // Achievements
        var achievementIcons = getAchievementIcons(jda);
        var achievementsMap = myUser.getAchievements();

        i = 0;
        for (var entry : achievementsMap.entrySet()) {
            if (i == 24) break;

            var coords = new ProfileCoordinates(i);
            var x = coords.getX();
            var y = coords.getY();

            x += 975;

            // Set Icon
            var icon = getIcon(achievementIcons.get(entry.getKey()));
            if (icon != null) {
                g2d.drawImage(icon, x, y, null);

                // Set Text
                rect = new Rectangle(x, y + 73, 64, 45);
                drawCenteredString(g2d, String.valueOf(entry.getValue()), rect, myriadPlain50pt, Color.WHITE);
            }

            i++;
        }

        g2d.dispose();
        return image;
    }

    private List<String> getLineBreaks(String bio) {
        var lineBreaks = new ArrayList<String>();
        var bioSplit = bio.split(" ");
        var sb = new StringBuilder();
        for (var bioPart : bioSplit) {
            if (sb.toString().isEmpty()) {
                if (bioPart.length() > 10) {
                    lineBreaks.add(bioPart.substring(0, 11));
                    bioPart = bioPart.substring(11);
                }
                if (bioPart.length() > 10) {
                    lineBreaks.add(bioPart.substring(0, 11));
                    bioPart = bioPart.substring(11);
                }
                if (bioPart.length() > 10) {
                    lineBreaks.add(bioPart.substring(0, 11));
                    bioPart = bioPart.substring(11);
                }
                sb.append(bioPart);
            } else if ((sb.toString() + bioPart).length() < 12)
                sb.append(" ").append(bioPart);
            else {
                lineBreaks.add(sb.toString());
                sb = new StringBuilder();
                sb.append(bioPart);
            }
        }
        lineBreaks.add(sb.toString());

        var lineBreaksResult = new ArrayList<String>();

        for (var lineBreak : lineBreaks) if (!lineBreak.isEmpty()) lineBreaksResult.add(lineBreak);

        return lineBreaksResult;
    }

    private Image getBg(MyUser myUser) {
        try {
            return ImageIO.read(new URL(myUser.getProfileBgImageUrl()));
        } catch (IOException e) {
            return null;
        }
    }

    private Image getOverlay(JDA jda) {
        try {
            return ImageIO.read(new URL(ImageUtil.getUrl(jda, "profile_overlay")));
        } catch (IOException e) {
            return null;
        }
    }

    private BufferedImage getAvatar() {
        try {
            return ImageIO.read(new URL(user.getEffectiveAvatarUrl() + "?size=2048"));
        } catch (IOException e) {
            return null;
        }
    }

    private BufferedImage circle(BufferedImage bufferedImage) {
        var widthHeight = bufferedImage.getWidth();
        var circleBuffer = new BufferedImage(widthHeight, widthHeight, BufferedImage.TYPE_INT_ARGB);
        var g2d = circleBuffer.createGraphics();
        g2d.setClip(new Ellipse2D.Float(0, 0, widthHeight, widthHeight));
        g2d.drawImage(bufferedImage, 0, 0, widthHeight, widthHeight, null);
        return circleBuffer;
    }

    private static Shape createRingShape(double percentage) {
        var centerX = 1000;
        var centerY = 750;
        var outerRadius = 250;
        var thickness = 15;

        // Outer Circle
        var outer = new Ellipse2D.Double(
                centerX - outerRadius,
                centerY - outerRadius,
                outerRadius + outerRadius,
                outerRadius + outerRadius);

        // Inner Circle
        var inner = new Ellipse2D.Double(
                centerX - outerRadius + thickness,
                centerY - outerRadius + thickness,
                outerRadius + outerRadius - thickness - thickness,
                outerRadius + outerRadius - thickness - thickness);

        // Percent based Pie (Inversed)
        var portion = new Arc2D.Double(750, 500, 500, 500, 90, 360 * (1 - percentage), Arc2D.PIE); // X and Y is not centered


        // Subtract areas from outer circle to produce a percent based ring
        var area = new Area(outer);
        area.subtract(new Area(inner));
        area.subtract(new Area(portion));

        return area;
    }

    public void drawCenteredString(Graphics2D g2d, String text, Rectangle rect, Font font, Color fontColor) {
        var metrics = g2d.getFontMetrics(font);
        var x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        var y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        float outline = 10;
        g2d.setFont(font);
        var frc = g2d.getFontRenderContext();
        var layout = new TextLayout(text, font, frc);
        var shape = layout.getOutline(null);

        g2d.setColor(Color.BLACK);
        g2d.translate(x, y);
        g2d.setStroke(new BasicStroke(outline));
        g2d.draw(shape);
        g2d.setColor(fontColor);
        g2d.fill(shape);
        g2d.translate(x * -1, y * -1); // Reset
    }

    private String getTitle(MyUser myUser) {
        if (myUser.isCreator())
            return LanguageHandler.get(lang, "profile_title_creator");
        else if (myUser.isSupporter())
            return LanguageHandler.get(lang, "profile_title_supporter");
        else
            return LanguageHandler.get(lang, "profile_title_normal");
    }

    private Map<String, String> getCommandIcons(JDA jda) {
        var featureIcons = new HashMap<String, String>();
        var featureNames = getValidCommands();
        for (var featureName : featureNames)
            featureIcons.put(featureName, ImageUtil.getUrl(jda, "f_" + featureName));
        return featureIcons;
    }

    public static List<String> getValidCommands() {
        return new ArrayList<>() {{
            add("botinfo");
            add("help");
            add("supporter");
            add("ping");
            add("clear");
            add("editembed");
            add("customcommands");
            add("giveaway");
            add("poll");
            add("quickpoll");
            add("rate");
            add("remindme");
            add("signup");
            add("timezone");
            add("achievements");
            add("avatar");
            add("baguette");
            add("bubblewrap");
            add("coinflip");
            add("commands");
            add("flip");
            add("love");
            add("profile");
            add("tictactoe");
            add("random");
            add("bird");
            add("cat");
            add("dog");
            add("fennec");
            add("fox");
            add("frog");
            add("koala");
            add("panda");
            add("pikachu");
            add("redpanda");
            add("sloth");
            add("wolf");
            add("beg");
            add("birthday");
            add("bite");
            add("bully");
            add("cookie");
            add("cop");
            add("cheers");
            add("dab");
            add("f");
            add("flex");
            add("highfive");
            add("hug");
            add("kiss");
            add("lick");
            add("pat");
            add("poke");
            add("shame");
            add("slap");
            add("wave");
            add("wink");
        }};
    }

    private Map<String, String> getAchievementIcons(JDA jda) {
        var achievementIcons = new HashMap<String, String>();
        var achievementNames = getValidAchievements();
        for (var achievementName : achievementNames)
            achievementIcons.put(achievementName, ImageUtil.getUrl(jda, "a_" + achievementName));
        return achievementIcons;
    }

    public static List<String> getValidAchievements() {
        return new ArrayList<>() {{
            add("console");

            add("excalibur");
            add("unlimited_blade_works");
            add("gae_bolg");

            add("navi");
            add("deusvult");
            add("fiteme");
            add("xmas");
            add("padoru");

            add("level10");
            add("level20");
            add("level30");
            add("level40");
            add("level50");
            add("level60");
            add("nicelevel");
            add("level70");
            add("level80");
            add("level90");
            add("level100");

            add("love42");
            add("love69");

            add("kind");

            add("arenanet");
        }};
    }

    private BufferedImage getIcon(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e) {
            return null;
        }
    }

    private Interaction getMsi(List<Interaction> interactions) {
        Interaction msi = null;

        for (var interaction : interactions) {
            if (msi == null || interaction.getShared() > msi.getShared())
                msi = interaction;
        }

        return msi;
    }

    private Interaction getMri(List<Interaction> interactions) {
        Interaction mri = null;

        for (var interaction : interactions) {
            if (mri == null || interaction.getReceived() > mri.getReceived())
                mri = interaction;
        }

        return mri;
    }
}
