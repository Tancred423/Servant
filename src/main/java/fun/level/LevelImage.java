// Author: Tancred423 (https://github.com/Tancred423)
package fun.level;

import files.language.LanguageHandler;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import patreon.PatreonHandler;
import utilities.Achievement;
import utilities.Parser;
import utilities.StringFormat;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;

public class LevelImage {
    private User user;
    private Guild guild;
    private BufferedImage image;

    LevelImage(User user, Guild guild, String lang) throws NoninvertibleTransformException {
        this.user = user;
        this.guild = guild;
        setProfilePicture(lang);
    }

    public User getUser() {
        return user;
    }

    public BufferedImage getImage() {
        return image;
    }


    private void setProfilePicture(String lang) throws NoninvertibleTransformException {
        var internalUser = new moderation.user.User(user.getIdLong());
        var internalGuild = new moderation.guild.Guild(guild.getIdLong());

        var lemon = new Font("Lemon/Milk", Font.PLAIN, 80);
        var lemonS = new Font("Lemon/Milk", Font.PLAIN, 60);
        var myriad = new Font("Myriad Pro", Font.BOLD, 60);

        var width = 2000;
        var height = 1500;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var g2d = image.createGraphics();

        // Background
        var bg = getBg(user);
        if (bg == null) {
            var border = 30;
            g2d.setColor(Color.decode("#202225"));
            g2d.fillRect(0, 0, width, height);
            g2d.setColor(Color.decode("#36393f"));
            g2d.fillRect(border, border, width - border * 2, height - border * 2);
        } else g2d.drawImage(bg, 0, 0, null);

        // Avatar
        var avaX = 75;
        var avaY = 75;
        var avaW = 300;
        var avaH = 300;
        var avaFW = 2.35;
        var avaFH = 2.35;
        var avatar = getAvatar();
        if (avatar == null) return;
        avatar = scale(avatar, Image.SCALE_DEFAULT, avaW, avaH, avaFW, avaFH);
        avatar = circle(avatar);
        g2d.drawImage(avatar, avaX, avaY, null);

        // Badge
        var badgeX = -30;
        var badgeY = -30;
        var badge = getBadge(user);
        if (badge != null) {
            g2d.drawImage(badge, badgeX, badgeY, null);
        }

        /* Name */
        var text = user.getName() + "#" + user.getDiscriminator();
        float outline = 10;
        var nameX = avaX * 2 + avaW;
        var nameY = avaY + 80;
        g2d.translate(nameX, nameY);
        g2d.setFont(lemon);
        g2d.setColor(Color.BLACK);
        var frc = g2d.getFontRenderContext();
        var layout = new TextLayout(text, lemon, frc);
        var shape = layout.getOutline(null);
        g2d.setStroke(new BasicStroke(outline));
        g2d.draw(shape);
        g2d.setColor(Color.WHITE);
        g2d.fill(shape);

        g2d.translate(nameX * -1, nameY * -1); // Reset

        /* Level */
        var levelX = avaX * 2 + avaW;
        var levelY = nameY + 20;
        var levelW = 1475;
        var levelH = 90;
        var border = 5;
        var textBorder = 10;

        // Gray Border
        g2d.setColor(Color.decode("#202225"));
        g2d.fillRect(levelX, levelY, levelW, levelH);

        // Grac Content
        g2d.setColor(Color.decode("#2f3136"));
        g2d.fillRect(levelX + border, levelY + border, levelW - border * 2, levelH - border * 2);

        // Level Bar
        var currentExp = internalUser.getExp(guild.getIdLong(), guild, user);
        var currentLevel = Parser.getLevelFromExp(currentExp);
        var neededExp = Parser.getLevelExp(currentLevel);
        var currentExpOnThisLevel = currentExp - Parser.getTotalLevelExp(currentLevel - 1);
        var percent = ((float) currentExpOnThisLevel) / neededExp;
        g2d.setColor(Color.decode(internalUser.getColorCode(guild, user)));
        g2d.fillRect(levelX + border, levelY + border, Math.round(percent * (levelW - border * 2)), levelH - border * 2);

        // Level Text
        text = LanguageHandler.get(lang, "level_level") + " " + currentLevel + " (" + currentExpOnThisLevel + "/" + neededExp + ")";
        var levelTextX = levelX + border + textBorder;
        var levelTextY = levelY + levelH * 0.75;
        g2d.translate(levelTextX, levelTextY);
        g2d.setColor(Color.BLACK);
        layout = new TextLayout(text, myriad, frc);
        shape = layout.getOutline(null);
        g2d.setStroke(new BasicStroke(outline));
        g2d.draw(shape);
        g2d.setColor(Color.WHITE);
        g2d.fill(shape);

        g2d.translate(levelTextX * -1, levelTextY * -1); // Reset

        // Guild Rank
        text = LanguageHandler.get(lang, "level_rank") + " #" + internalGuild.getUserRank(user.getIdLong(), guild, user);
        var rankX = levelX;
        var rankY = levelY + levelH + 70;
        g2d.translate(rankX, rankY);
        g2d.setColor(Color.BLACK);
        layout = new TextLayout(text, myriad, frc);
        shape = layout.getOutline(null);
        g2d.setStroke(new BasicStroke(outline));
        g2d.draw(shape);
        g2d.setColor(Color.WHITE);
        g2d.fill(shape);

        g2d.translate(rankX * -1, rankY * -1); // Reset

        // Guild Rank
        text = internalUser.getBio(guild, user);
        if (!text.isEmpty()) {
            var bioX = rankX;
            var bioY = rankY + 80;
            g2d.translate(bioX, bioY);
            g2d.setColor(Color.BLACK);
            layout = new TextLayout(text, myriad, frc);
            shape = layout.getOutline(null);
            g2d.setStroke(new BasicStroke(outline));
            g2d.draw(shape);
            g2d.setColor(Color.WHITE);
            g2d.fill(shape);

            g2d.translate(bioX * -1, bioY * -1); // Reset
        }

        /* Baguette */
        var baguetteX = avaX;
        var baguetteY = avaY * 2 + avaH;
        var baguetteImage = getBaguette(user);
        if (baguetteImage == null) return;
        g2d.drawImage(baguetteImage, baguetteX, baguetteY, null);

        // Baguette Text
        var baguette = internalUser.getBaguette(guild, user).entrySet().iterator().hasNext() ? internalUser.getBaguette(guild, user).entrySet().iterator().next() : null;
        text = baguette == null ? LanguageHandler.get(lang, "profile_nobaguette") :
                String.format(LanguageHandler.get(lang, "profile_baguette"), baguette.getKey(), baguette.getValue());

        var bagTextX = baguetteX + 125;
        var bagTextY = baguetteY + 75;
        var subOutline = 7;
        g2d.translate(bagTextX, bagTextY);
        g2d.setColor(Color.BLACK);
        layout = new TextLayout(text, myriad, frc);
        shape = layout.getOutline(null);
        g2d.setStroke(new BasicStroke(subOutline));
        g2d.draw(shape);
        g2d.setColor(Color.WHITE);
        g2d.fill(shape);

        g2d.translate(bagTextX * -1, bagTextY * -1); // Reset

        /* Most Used Commands */
        // MUC Title
        text = LanguageHandler.get(lang, "profile_mostused");
        var mucTitleX = avaX;
        var mucTitleY = baguetteY + 200;
        g2d.translate(mucTitleX, mucTitleY);
        g2d.setColor(Color.BLACK);
        layout = new TextLayout(text, lemonS, frc);
        shape = layout.getOutline(null);
        g2d.setStroke(new BasicStroke(subOutline));
        g2d.draw(shape);
        g2d.setColor(Color.WHITE);
        g2d.fill(shape);

        g2d.translate(mucTitleX * -1, mucTitleY * -1); // Reset

        // MUC Content
        var features = internalUser.getTop10MostUsedFeatures(guild, user, lang);
        var top10Features = new StringBuilder();

        var mucContentX = mucTitleX;
        var mucContentY = mucTitleY;
        var mucContentH = 70;
        g2d.translate(mucContentX, mucContentY);

        var counter = 0;
        if (features.isEmpty()) {
            top10Features.append(LanguageHandler.get(lang, "profile_nocommands"));
        } else for (var feature : features.entrySet()) {
            g2d.translate(0, mucContentH);

            text = feature.getKey() + ": " + feature.getValue();
            g2d.setColor(Color.BLACK);
            layout = new TextLayout(text, myriad, frc);
            shape = layout.getOutline(null);
            g2d.setStroke(new BasicStroke(subOutline));
            g2d.draw(shape);
            if (counter == 0) g2d.setColor(Color.decode(internalUser.getColorCode(guild, user)));
            else g2d.setColor(Color.WHITE);
            g2d.fill(shape);

            counter++;
        }

        g2d.translate(mucContentX * -1, (mucContentY * -1) + (counter * mucContentH * -1)); // Reset

        // Achievement Title
        text = LanguageHandler.get(lang, "profile_achievements");
        var achTitleX = width / 2 + avaX;
        var achTitleY = mucTitleY;
        g2d.translate(achTitleX, achTitleY);
        g2d.setColor(Color.BLACK);
        layout = new TextLayout(text, lemonS, frc);
        shape = layout.getOutline(null);
        g2d.setStroke(new BasicStroke(subOutline));
        g2d.draw(shape);
        g2d.setColor(Color.WHITE);
        g2d.fill(shape);

        g2d.translate(achTitleX * -1, achTitleY * -1); // Reset

        // Achievement Content
        var achievements = internalUser.getAchievements(guild, user, lang);
        var achievementsWithName = new LinkedHashMap<String, Integer>();
        for (var achievement : achievements.entrySet())
            achievementsWithName.put(Achievement.getFancyName(achievement.getKey(), lang), achievement.getValue());
        achievementsWithName = StringFormat.achievementSortByKey(achievementsWithName, lang, internalUser, guild, user);

        var achContentX = achTitleX;
        var achContentY = achTitleY;
        var achContentH = 70;
        g2d.translate(achContentX, achContentY);

        counter = 0;
        if (achievementsWithName.isEmpty()) {
            g2d.translate(0, achContentH);
            text = LanguageHandler.get(lang, "profile_noachievements");
            g2d.setColor(Color.BLACK);
            layout = new TextLayout(text, myriad, frc);
            shape = layout.getOutline(null);
            g2d.setStroke(new BasicStroke(subOutline));
            g2d.draw(shape);
            g2d.setColor(Color.WHITE);
            g2d.fill(shape);
        } else for (var achievement : achievementsWithName.entrySet()) {
            g2d.translate(0, achContentH);
            text = achievement.getKey() + ": " + achievement.getValue();
            g2d.setColor(Color.BLACK);
            layout = new TextLayout(text, myriad, frc);
            shape = layout.getOutline(null);
            g2d.setStroke(new BasicStroke(subOutline));
            g2d.draw(shape);
            if (counter == 0) g2d.setColor(Color.decode(internalUser.getColorCode(guild, user)));
            else g2d.setColor(Color.WHITE);
            g2d.fill(shape);

            counter++;
        }

        g2d.translate(achContentX * -1, (achContentY * -1) + (counter * achContentH * -1)); // Reset

        // Build
        g2d.dispose();
    }

    private BufferedImage getAvatar() {
        try {
            var url = new URL(user.getEffectiveAvatarUrl());
            return ImageIO.read(url);
        } catch (IOException e) {
            return null;
        }
    }

    private BufferedImage getBadge(User user) {
        try {
            if (PatreonHandler.isVIP(user))
                return ImageIO.read(new URL(utilities.Image.getImageUrl("profile_badge_vip", guild, user)));
            else if (PatreonHandler.is$10Patron(user))
                return ImageIO.read(new URL(utilities.Image.getImageUrl("profile_badge_$10", guild, user)));
            else if (PatreonHandler.is$5Patron(user))
                return ImageIO.read(new URL(utilities.Image.getImageUrl("profile_badge_$5", guild, user)));
            else if (PatreonHandler.is$3Patron(user))
                return ImageIO.read(new URL(utilities.Image.getImageUrl("profile_badge_$3", guild, user)));
            else if (PatreonHandler.is$1Patron(user))
                return ImageIO.read(new URL(utilities.Image.getImageUrl("profile_badge_$1", guild, user)));
            else if (PatreonHandler.isDonator(user))
                return ImageIO.read(new URL(utilities.Image.getImageUrl("profile_badge_donator", guild, user)));
            else if (PatreonHandler.isServerBooster(user))
                return ImageIO.read(new URL(utilities.Image.getImageUrl("profile_badge_booster", guild, user)));
            else return null;
        } catch (IOException e) {
            return null;
        }
    }

    private Image getBg(User user) {
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

    private BufferedImage scale(BufferedImage sbi, int imageType, int dWidth, int dHeight, double fWidth, double fHeight) {
        BufferedImage dbi = null;
        if(sbi != null) {
            dbi = new BufferedImage(dWidth, dHeight, imageType);
            Graphics2D g = dbi.createGraphics();
            AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
            g.drawRenderedImage(sbi, at);
        }
        return dbi;
    }

    private BufferedImage circle(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        var circleBuffer = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = circleBuffer.createGraphics();
        g2.setClip(new Ellipse2D.Float(0, 0, width, width));
        g2.drawImage(bufferedImage, 0, 0, width, width, null);
        return circleBuffer;
    }

    private BufferedImage getBaguette(User user) {
        try {
            return ImageIO.read(new URL(utilities.Image.getImageUrl("profile_baguette", guild, user)));
        } catch (IOException e) {
            return null;
        }
    }
}
