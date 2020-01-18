// Author: Tancred423 (https://github.com/Tancred423)
package easteregg;

import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import owner.blacklist.Blacklist;
import servant.Servant;
import utilities.Constants;
import utilities.Emote;
import utilities.Image;
import utilities.MessageHandler;

import java.awt.*;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;

public class EasterEggsListener extends ListenerAdapter {
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getGuild().getIdLong() == 264445053596991498L) return; // Discord Bot List
        if (!Toggle.isEnabled(event, "easteregg")) return;
        CompletableFuture.runAsync(() -> {
            var guild = event.getGuild();
            var author = event.getAuthor();
            if (author.isBot()) return;
            var internalAuthor = new User(author.getIdLong());
            var message = event.getMessage();
            var contentRaw = message.getContentRaw().toLowerCase();
            Color color;
            try {
                color = internalAuthor.getColor(event.getGuild(), author);
            } catch (NullPointerException e) { // Only happens on boot up
                color = Color.decode(Servant.config.getDefaultColorCode());
            }

            switch (contentRaw) {
                case "its name is":
                case "its name's":
                    if (Blacklist.isBlacklisted(author, event.getGuild())) return;
                    System.out.println("[" + OffsetDateTime.now(ZoneId.of(Constants.LOG_OFFSET)).toString().replaceAll("T", " ").substring(0, 19) + "] " +
                            "Easteregg found: " + contentRaw + ". " +
                            "Guild: " + event.getGuild().getName() + " (" + event.getGuild().getIdLong() + "). " +
                            "User: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + " (" + event.getAuthor().getIdLong() + ").");
                    event.getChannel().sendMessage(new EmbedBuilder().setColor(color).setImage("https://i.imgur.com/ohcdKgU.gif").build()).queue(sentMessage -> {
                        if (!internalAuthor.hasAchievement("excalibur", guild, author)) {
                            internalAuthor.setAchievement("excalibur", 50, guild, author);
                            new MessageHandler().reactAchievement(message);
                        }
                    }, failure -> System.out.println("Easter egg message couldn't be sent: " + event.getAuthor().getName()
                            + "#" + event.getAuthor().getDiscriminator() + " (" + event.getAuthor().getIdLong() + ")" +
                            " Guild: " + event.getGuild().getName() + " (" + event.getGuild().getIdLong() + ")"));
                    break;

                case "i am the bone of my sword":
                case "i'm the bone of my sword":
                    if (Blacklist.isBlacklisted(author, event.getGuild())) return;
                    System.out.println("[" + OffsetDateTime.now(ZoneId.of(Constants.LOG_OFFSET)).toString().replaceAll("T", " ").substring(0, 19) + "] " +
                            "Easteregg found: " + contentRaw + ". " +
                            "Guild: " + event.getGuild().getName() + " (" + event.getGuild().getIdLong() + "). " +
                            "User: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + " (" + event.getAuthor().getIdLong() + ").");
                    event.getChannel().sendMessage(new EmbedBuilder().setColor(color).setImage("https://i.imgur.com/XZ7q5Xg.gif").build()).queue(sentMessage -> {
                        if (!internalAuthor.hasAchievement("unlimited_blade_works", guild, author)) {
                            internalAuthor.setAchievement("unlimited_blade_works", 50, guild, author);
                            new MessageHandler().reactAchievement(message);
                        }
                    }, failure -> System.out.println("Easter egg message couldn't be sent: " + event.getAuthor().getName()
                            + "#" + event.getAuthor().getDiscriminator() + " (" + event.getAuthor().getIdLong() + ")" +
                            " Guild: " + event.getGuild().getName() + " (" + event.getGuild().getIdLong() + ")"));
                    break;

                case "gae bolg":
                    if (Blacklist.isBlacklisted(author, event.getGuild())) return;
                    System.out.println("[" + OffsetDateTime.now(ZoneId.of(Constants.LOG_OFFSET)).toString().replaceAll("T", " ").substring(0, 19) + "] " +
                            "Easteregg found: " + contentRaw + ". " +
                            "Guild: " + event.getGuild().getName() + " (" + event.getGuild().getIdLong() + "). " +
                            "User: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + " (" + event.getAuthor().getIdLong() + ").");
                    event.getChannel().sendMessage(new EmbedBuilder().setColor(color).setImage("https://i.imgur.com/Mu1vw26.gif").build()).queue(sentMessage -> {
                        if (!internalAuthor.hasAchievement("gae_bolg", guild, author)) {
                            internalAuthor.setAchievement("gae_bolg", 50, guild, author);
                            new MessageHandler().reactAchievement(message);
                        }
                    }, failure -> System.out.println("Easter egg message couldn't be sent: " + event.getAuthor().getName()
                            + "#" + event.getAuthor().getDiscriminator() + " (" + event.getAuthor().getIdLong() + ")" +
                            " Guild: " + event.getGuild().getName() + " (" + event.getGuild().getIdLong() + ")"));
                    break;

                case "hey listen":
                    if (Blacklist.isBlacklisted(author, event.getGuild())) return;
                    System.out.println("[" + OffsetDateTime.now(ZoneId.of(Constants.LOG_OFFSET)).toString().replaceAll("T", " ").substring(0, 19) + "] " +
                            "Easteregg found: " + contentRaw + ". " +
                            "Guild: " + event.getGuild().getName() + " (" + event.getGuild().getIdLong() + "). " +
                            "User: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + " (" + event.getAuthor().getIdLong() + ").");
                    event.getChannel().sendMessage(new EmbedBuilder().setColor(color).setImage("https://i.imgur.com/w4S5NIt.gif").build()).queue(sentMessage -> {
                        if (!internalAuthor.hasAchievement("navi", guild, author)) {
                            internalAuthor.setAchievement("navi", 50, guild, author);
                            new MessageHandler().reactAchievement(message);
                        }
                    }, failure -> System.out.println("Easter egg message couldn't be sent: " + event.getAuthor().getName()
                            + "#" + event.getAuthor().getDiscriminator() + " (" + event.getAuthor().getIdLong() + ")" +
                            " Guild: " + event.getGuild().getName() + " (" + event.getGuild().getIdLong() + ")"));
                    break;

                case "deus vult":
                    if (Blacklist.isBlacklisted(author, event.getGuild())) return;
                    System.out.println("[" + OffsetDateTime.now(ZoneId.of(Constants.LOG_OFFSET)).toString().replaceAll("T", " ").substring(0, 19) + "] " +
                            "Easteregg found: " + contentRaw + ". " +
                            "Guild: " + event.getGuild().getName() + " (" + event.getGuild().getIdLong() + "). " +
                            "User: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + " (" + event.getAuthor().getIdLong() + ").");
                    event.getChannel().sendMessage(new EmbedBuilder().setColor(color).setImage("https://i.kym-cdn.com/photos/images/newsfeed/001/176/858/c69.gif").build()).queue(sentMessage -> {
                        if (!internalAuthor.hasAchievement("deusvult", guild, author)) {
                            internalAuthor.setAchievement("deusvult", 10, guild, author);
                            new MessageHandler().reactAchievement(message);
                        }
                    }, failure -> System.out.println("Easter egg message couldn't be sent: " + event.getAuthor().getName()
                            + "#" + event.getAuthor().getDiscriminator() + " (" + event.getAuthor().getIdLong() + ")" +
                            " Guild: " + event.getGuild().getName() + " (" + event.getGuild().getIdLong() + ")"));
                    break;

                case "<@!436916794796670977> fite me":
                case "<@!436916794796670977> fight me":
                case "<@436916794796670977> fite me":
                case "<@436916794796670977> fight me":
                case "<@!550309058251456512> fite me":
                case "<@!550309058251456512> fight me":
                case "<@550309058251456512> fite me":
                case "<@550309058251456512> fight me":
                    if (Blacklist.isBlacklisted(author, event.getGuild())) return;
                    System.out.println("[" + OffsetDateTime.now(ZoneId.of(Constants.LOG_OFFSET)).toString().replaceAll("T", " ").substring(0, 19) + "] " +
                            "Easteregg found: " + contentRaw + ". " +
                            "Guild: " + event.getGuild().getName() + " (" + event.getGuild().getIdLong() + "). " +
                            "User: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + " (" + event.getAuthor().getIdLong() + ").");
                    event.getChannel().sendMessage(new EmbedBuilder().setColor(color).setImage("https://i.imgur.com/wINPdOJ.gif").build()).queue(sentMessage -> {
                        if (!internalAuthor.hasAchievement("fiteme", guild, author)) {
                            internalAuthor.setAchievement("fiteme", 10, guild, author);
                            new MessageHandler().reactAchievement(message);
                        }
                    }, failure -> System.out.println("Easter egg message couldn't be sent: " + event.getAuthor().getName()
                            + "#" + event.getAuthor().getDiscriminator() + " (" + event.getAuthor().getIdLong() + ")" +
                            " Guild: " + event.getGuild().getName() + " (" + event.getGuild().getIdLong() + ")"));
                    break;

                case "happy christmas":
                case "happy xmas":
                case "merry christmas":
                case "merry xmas":
                    if (Blacklist.isBlacklisted(author, event.getGuild())) return;
                    System.out.println("[" + OffsetDateTime.now(ZoneId.of(Constants.LOG_OFFSET)).toString().replaceAll("T", " ").substring(0, 19) + "] " +
                            "Easteregg found: " + contentRaw + ". " +
                            "Guild: " + event.getGuild().getName() + " (" + event.getGuild().getIdLong() + "). " +
                            "User: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + " (" + event.getAuthor().getIdLong() + ").");
                    event.getChannel().sendMessage(Emote.getEmoteMention(event.getJDA(), "servant_padoru", guild, author)).queue(sentMessage -> {
                                if (!internalAuthor.hasAchievement("xmas", guild, author)) {
                                    internalAuthor.setAchievement("xmas", 10, guild, author);
                                    new MessageHandler().reactAchievement(message);
                                }
                            }, failure -> System.out.println("Easter egg message couldn't be sent: " + event.getAuthor().getName()
                            + "#" + event.getAuthor().getDiscriminator() + " (" + event.getAuthor().getIdLong() + ")" +
                            " Guild: " + event.getGuild().getName() + " (" + event.getGuild().getIdLong() + ")"));
                    break;

                case "padoru":
                    if (Blacklist.isBlacklisted(author, event.getGuild())) return;
                    System.out.println("[" + OffsetDateTime.now(ZoneId.of(Constants.LOG_OFFSET)).toString().replaceAll("T", " ").substring(0, 19) + "] " +
                            "Easteregg found: " + contentRaw + ". " +
                            "Guild: " + event.getGuild().getName() + " (" + event.getGuild().getIdLong() + "). " +
                            "User: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + " (" + event.getAuthor().getIdLong() + ").");
                    event.getChannel().sendMessage(new EmbedBuilder().setColor(internalAuthor.getColor(guild, author)).setImage(Image.getImageUrl("padoru", guild, author)).build()).queue();
                    break;
            }
        });
    }
}
