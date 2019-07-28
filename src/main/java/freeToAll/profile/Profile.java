package freeToAll.profile;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import utilities.Parser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Map;

public class Profile {
    private User user;
    private Guild guild;
    private BufferedImage image;

    Profile(User user, Guild guild) throws SQLException, NoninvertibleTransformException {
        this.user = user;
        this.guild = guild;
        setProfilePicture();
    }

    public User getUser() {
        return user;
    }

    BufferedImage getImage() {
        return image;
    }

    private void setProfilePicture() throws SQLException, NoninvertibleTransformException {
        var internalUser = new servant.User(user.getIdLong());
        var internalGuild = new moderation.guild.Guild(guild.getIdLong());

        var lemon = new Font("Lemon/Milk", Font.PLAIN, 100);
        var myriad = new Font("Myriad Pro", Font.BOLD, 80);

        var width = 2400;
        var height = 1800;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var g2d = image.createGraphics();

        // Background Border
        var border = 30;
        g2d.setColor(Color.decode("#202225"));
        g2d.fillRect(0, 0, width, height);

        // Background
        g2d.setColor(Color.decode("#36393f"));
        g2d.fillRect(border, border, width - border * 2, height - border * 2);

        // Avatar
        var image = getAvatar();
        if (image == null) return;
        g2d.drawImage(image.getScaledInstance(400, 400, 0), 100, 100, null);

        // Name
        var text = user.getName() + "#" + user.getDiscriminator();
        var outline = 10;
        var x = Math.round(549 + outline / 2);
        var y = Math.round(179 + outline / 2);
        var transform = g2d.getTransform();
        transform.translate(x, y);
        g2d.transform(transform);
        g2d.setFont(lemon);
        g2d.setColor(Color.BLACK);
        var frc = g2d.getFontRenderContext();
        var layout = new TextLayout(text, lemon, frc);
        var shape = layout.getOutline(null);
        g2d.setStroke(new BasicStroke(outline));
        g2d.draw(shape);
        g2d.setColor(Color.WHITE);
        g2d.fill(shape);

        // Level Background Border
        transform.invert();
        g2d.transform(transform);
        g2d.setColor(Color.decode("#202225"));
        g2d.fillRect(550, 220, 1750, 120);

        // Level Background
        border = 10;
        g2d.setColor(Color.decode("#2f3136"));
        g2d.fillRect(550 + border, 220 + border, 1750 - border * 2, 120 - border * 2);

        // Level Percentage Bar
        var currentExp = internalUser.getExp(guild.getIdLong());
        var currentLevel = Parser.getLevelFromExp(currentExp);
        var neededExp = Parser.getLevelExp(currentLevel);
        var currentExpOnThisLevel = currentExp - Parser.getTotalLevelExp(currentLevel - 1);

        var percent = ((float) currentExpOnThisLevel) / neededExp;

        g2d.setColor(Color.decode("#36b3ff"));
        g2d.fillRect(550 + border, 220 + border, Math.round(percent * (1750 - border * 2)), 120 - border * 2);

        // Level Text
        text = "Level " + currentLevel + " (" + currentExpOnThisLevel + "/" + neededExp + ")";
        x = 20;
        y = 125;
        transform.invert();
        transform.translate(x, y);
        g2d.transform(transform);
        g2d.setColor(Color.BLACK);
        layout = new TextLayout(text, myriad, frc);
        shape = layout.getOutline(null);
        g2d.setStroke(new BasicStroke(outline));
        g2d.draw(shape);
        g2d.setColor(Color.WHITE);
        g2d.fill(shape);


        // Guild Rank
        text = "Rank #" + internalGuild.getUserRank(user.getIdLong());
        x = 555;
        y = 430;
        transform.invert();
        transform.translate(x, y);
        g2d.transform(transform);
        g2d.setColor(Color.BLACK);
        layout = new TextLayout(text, myriad, frc);
        shape = layout.getOutline(null);
        g2d.setStroke(new BasicStroke(outline));
        g2d.draw(shape);
        g2d.setColor(Color.WHITE);
        g2d.fill(shape);

        // Most Used Command
        Map<String, Integer> mostUsedFeature = internalUser.getMostUsedFeature();
        Map.Entry<String, Integer> entry = mostUsedFeature.entrySet().iterator().next();
        text = "Most used command: " + (entry.getKey().equals("Not found.") ? "" : internalGuild.getPrefix()) + entry.getKey() + " (" + entry.getValue() + " times)";
        x = -437;
        y = 80;
        transform.translate(x, y);
        g2d.transform(transform);
        g2d.setColor(Color.BLACK);
        layout = new TextLayout(text, myriad, frc);
        shape = layout.getOutline(null);
        g2d.setStroke(new BasicStroke(outline));
        g2d.draw(shape);
        g2d.setColor(Color.WHITE);
        g2d.fill(shape);

        // Build
        g2d.dispose();
    }

    private Image getAvatar() {
        try {
            var url = new URL(user.getAvatarUrl());
            return ImageIO.read(url);
        } catch (IOException e) {
            return null;
        }
    }
}
