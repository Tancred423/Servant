package fun.level;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import patreon.PatreonHandler;
import utilities.Parser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

public class ProfileImage {
    private User user;
    private Guild guild;
    private String lang;

    ProfileImage(User user, Guild guild, String lang) {
        this.user = user;
        this.guild = guild;
        this.lang = lang;
    }

    public BufferedImage generateImage() {
        // Locale for decimal formatting
        var locale = Locale.UK;
        switch (lang) {
            case "de_de":
                locale = Locale.GERMANY;
                break;
            case "en_us":
                locale = Locale.US;
                break;
        }

        // Internal User & Guild
        var internalUser = new moderation.user.User(user.getIdLong());
        var internalGuild = new moderation.guild.Guild(guild.getIdLong());

        // Fonts
        var myriadPlain100pt = new Font("Myriad Pro", Font.PLAIN, 100); // Name, Bio
        var myriadPlain75pt = new Font("Myriad Pro", Font.PLAIN, 75); // Patreon,
        var myriadPlain60pt = new Font("Myriad Pro", Font.PLAIN, 60); // General Info
        var myriadPlain40pt = new Font("Myriad Pro", Font.PLAIN, 40); // Command Info, Coloured Titles, Counter

        // Canvas
        var width = 2000;
        var height = 1500;
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g2d.drawImage(getBg(), 0, 0, null);
        g2d.drawImage(getOverlay(), 0, 0, null);

        // Avatar
        var avaX = 750; // X and Y is not centered
        var avaY = 500;
        var avaWH = 500;
        var avaFWH = 4;
        var avatar = getAvatar();
        if (avatar != null) {
            avatar = scale(avatar, avaWH, avaWH, avaFWH, avaFWH);
            avatar = circle(avatar);
            g2d.drawImage(avatar, avaX, avaY, null);
        }

        // EXP
        var currentExp = internalUser.getExp(guild.getIdLong(), guild, user);
        var currentLevel = Parser.getLevelFromExp(currentExp);
        var neededExp = Parser.getLevelExp(currentLevel);
        var currentExpOnThisLevel = currentExp - Parser.getTotalLevelExp(currentLevel - 1);
        var percent = ((float) currentExpOnThisLevel) / neededExp;

        // EXP Ring
        var expRing = createRingShape(percent); // X and Y is centered
        g2d.setColor(internalUser.getColor(guild, user));
        g2d.fill(expRing);
        g2d.setColor(Color.BLACK);
        g2d.draw(expRing);

        // User Name
        var name = user.getName() + "#" + user.getDiscriminator();
        var rect = new Rectangle(0, 500, 800, 450);
        g2d.setColor(Color.WHITE);
        drawCenteredString(g2d, name, rect, myriadPlain100pt);

        // Patreon Title
        var title = getTitle();
        rect = new Rectangle(0, 750, 800, 100);
        g2d.setColor(internalUser.getColor(guild, user));
        drawCenteredString(g2d, title, rect, myriadPlain75pt);

        // Bio
        var bio = internalUser.getBio(guild, user);
        g2d.setColor(Color.WHITE);
        if (bio.length() > 20) {
            // Three Lines
            var line = bio.substring(0, 10);
            rect = new Rectangle(1300, 500, 630, 300);
            drawCenteredString(g2d, line, rect, myriadPlain100pt);

            line = bio.substring(10, 20);
            rect = new Rectangle(1300, 550, 630, 400);
            drawCenteredString(g2d, line, rect, myriadPlain100pt);

            line = bio.substring(20);
            rect = new Rectangle(1300, 600, 630, 500);
            drawCenteredString(g2d, line, rect, myriadPlain100pt);
        } else if (bio.length() > 10) {
            // Two Lines
            var line = bio.substring(0, 10);
            rect = new Rectangle(1300, 500, 630, 400);
            drawCenteredString(g2d, line, rect, myriadPlain100pt);

            line = bio.substring(10);
            rect = new Rectangle(1300, 550, 630, 500);
            drawCenteredString(g2d, line, rect, myriadPlain100pt);
        } else {
            // One Line
            rect = new Rectangle(1300, 500, 630, 500);
            drawCenteredString(g2d, bio, rect, myriadPlain100pt);
        }

        // Titles: General Info, Command Stats, Most Used Commands, Achievements
        var generalInfo = LanguageHandler.get(lang, "profile_generalinfo").toUpperCase();
        var commandStats = LanguageHandler.get(lang, "profile_commandstats").toUpperCase();
        var mostUsedCommands = LanguageHandler.get(lang, "profile_mostused").toUpperCase();
        var achievements = LanguageHandler.get(lang, "profile_achievements").toUpperCase();
        g2d.setColor(internalUser.getColor(guild, user));

        rect = new Rectangle(0, 0, 1000, 100);
        drawCenteredString(g2d, generalInfo, rect, myriadPlain40pt);

        rect = new Rectangle(1000, 0, 1000, 100);
        drawCenteredString(g2d, commandStats, rect, myriadPlain40pt);

        rect = new Rectangle(0, 1000, 1000, 100);
        drawCenteredString(g2d, mostUsedCommands, rect, myriadPlain40pt);

        rect = new Rectangle(1000, 1000, 1000, 100);
        drawCenteredString(g2d, achievements, rect, myriadPlain40pt);

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

        var rankValue = String.valueOf(internalGuild.getUserRank(user.getIdLong(), guild, user));
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
        var commandsUsedText = LanguageHandler.get(lang, "profile_total_muc");
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

        var commandsUsedValue = String.valueOf(internalUser.getTotalFeatureCount(guild, user));
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
        var apText = LanguageHandler.get(lang, "profile_total_ap");
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

        var apValue = String.valueOf(internalUser.getTotalAP(guild, user));
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

        var baguette = internalUser.getBaguette(guild, user).entrySet().iterator().hasNext() ? internalUser.getBaguette(guild, user).entrySet().iterator().next() : null;
        var baguetteValue = baguette == null ? LanguageHandler.get(lang, "profile_nobaguette") :
                String.format(LanguageHandler.get(lang, "profile_baguette_value"), baguette.getKey(), baguette.getValue());

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

        var animalValue = internalUser.getFavouriteAnimal(guild, user, lang);
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

        // TBC
        var tbcText = "Coming soon™";
        var tbcX = 300;
        var tbcY = 1250;
        layout = new TextLayout(tbcText, myriadPlain60pt, frc);
        shape = layout.getOutline(null);

        g2d.setColor(Color.BLACK);
        g2d.translate(tbcX, tbcY);
        g2d.setStroke(new BasicStroke(outline));
        g2d.draw(shape);
        g2d.setColor(Color.WHITE);
        g2d.fill(shape);
        g2d.translate(tbcX * -1, tbcY * -1); // Reset

        tbcX = 1325;
        layout = new TextLayout(tbcText, myriadPlain60pt, frc);
        shape = layout.getOutline(null);

        g2d.setColor(Color.BLACK);
        g2d.translate(tbcX, tbcY);
        g2d.setStroke(new BasicStroke(outline));
        g2d.draw(shape);
        g2d.setColor(Color.WHITE);
        g2d.fill(shape);
        g2d.translate(tbcX * -1, tbcY * -1); // Reset

        g2d.dispose();
        return image;
    }

    private Image getBg() {
        try {
            if (PatreonHandler.isVIP(user))
                return ImageIO.read(new URL(utilities.Image.getImageUrl("profile_bg_vip", guild, user)));
            else if (PatreonHandler.is$10Patron(user))
                return ImageIO.read(new URL(utilities.Image.getImageUrl("profile_bg_$10", guild, user)));
            else if (PatreonHandler.is$5Patron(user))
                return ImageIO.read(new URL(utilities.Image.getImageUrl("profile_bg_$5", guild, user)));
            else if (PatreonHandler.is$3Patron(user))
                return ImageIO.read(new URL(utilities.Image.getImageUrl("profile_bg_$3", guild, user)));
            else if (PatreonHandler.is$1Patron(user))
                return ImageIO.read(new URL(utilities.Image.getImageUrl("profile_bg_$1", guild, user)));
            else if (PatreonHandler.isDonator(user))
                return ImageIO.read(new URL(utilities.Image.getImageUrl("profile_bg_donator", guild, user)));
            else if (PatreonHandler.isServerBooster(user))
                return ImageIO.read(new URL(utilities.Image.getImageUrl("profile_bg_booster", guild, user)));
            else
                return ImageIO.read(new URL(utilities.Image.getImageUrl("profile_bg_normal", guild, user)));
        } catch (IOException e) {
            return null;
        }
    }

    private Image getOverlay() {
        try {
            return ImageIO.read(new URL(utilities.Image.getImageUrl("profile_overlay", guild, user)));
        } catch (IOException e) {
            return null;
        }
    }

    private BufferedImage getAvatar() {
        try {
            return ImageIO.read(new URL(user.getEffectiveAvatarUrl()));
        } catch (IOException e) {
            return null;
        }
    }

    private BufferedImage scale(BufferedImage image, int dWidth, int dHeight, double fWidth, double fHeight) {
        if(image != null) {
            var scaledImage = new BufferedImage(dWidth, dHeight, Image.SCALE_DEFAULT);
            var g2d = scaledImage.createGraphics();
            var affineTransform = AffineTransform.getScaleInstance(fWidth, fHeight);
            g2d.drawRenderedImage(image, affineTransform);
            return scaledImage;
        } else return null;
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

    public void drawCenteredString(Graphics g2d, String text, Rectangle rect, Font font) {
        var metrics = g2d.getFontMetrics(font);
        var x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        var y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.setFont(font);
        g2d.drawString(text, x, y);
    }

    private String getTitle() {
        if (PatreonHandler.isVIP(user))
            return LanguageHandler.get(lang, "profile_title_vip");
        else if (PatreonHandler.is$10Patron(user))
            return LanguageHandler.get(lang, "profile_title_$10");
        else if (PatreonHandler.is$5Patron(user))
            return LanguageHandler.get(lang, "profile_title_$5");
        else if (PatreonHandler.is$3Patron(user))
            return LanguageHandler.get(lang, "profile_title_$3");
        else if (PatreonHandler.is$1Patron(user))
            return LanguageHandler.get(lang, "profile_title_$1");
        else if (PatreonHandler.isDonator(user))
            return LanguageHandler.get(lang, "profile_title_donator");
        else if (PatreonHandler.isServerBooster(user))
            return LanguageHandler.get(lang, "profile_title_booster");
        else
            return LanguageHandler.get(lang, "profile_title_normal");
    }
}
