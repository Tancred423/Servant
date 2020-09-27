package commands.fun.tictactoe;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import servant.MyUser;
import utilities.EmoteUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class TicTacToeCommand extends Command {
    private final EventWaiter waiter;
    private final String ACCEPT = "✅";
    private final String DECLINE = "❌";
    private final String X = "\uD83C\uDDFD";
    private final String O = "\uD83C\uDD7E️";
    private final String BLANK = "⬛";
    private final String[] arrows = { "↖️", "⬆️", "↗️", "⬅️", "⏹️", "➡️", "↙️", "⬇️", "↘️" };
    private final String[] arrowsCorner = { "↖️", "↗️", "↙️", "↘️" };
    private final HashMap<Long, Integer> rounds = new HashMap<>();
    static HashMap<Long, String> moveNames = new HashMap<>();

    public TicTacToeCommand(EventWaiter waiter) {
        this.name = "tictactoe";
        this.aliases = new String[]{"ttt"};
        this.help = "Play TicTacToe with someone";
        this.category = new Category("Fun");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.modCommand = false;
        this.cooldown = 0;
        this.cooldownScope = CooldownScope.USER;
        this.botPermissions = new Permission[]{
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };

        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);

        var jda = event.getJDA();
        var channel = event.getChannel();
        var msg = event.getMessage();
        var author = event.getAuthor();

        var firstArg = event.getArgs().split(" ")[0];
        if (!firstArg.isEmpty() && (firstArg.equals("statistics") || firstArg.equals("stats"))) {
            User mentioned;
            if (msg.getMentionedUsers().size() > 0) mentioned = msg.getMentionedUsers().get(0);
            else mentioned = author;

            var myMentioned = new MyUser(mentioned);
            var tttStats = myMentioned.getTttStatistic();
            var messageEmbed = new EmbedBuilder()
                    .setColor(myMentioned.getColor())
                    .setAuthor(mentioned.getName(), null, mentioned.getEffectiveAvatarUrl())
                    .addField("Wins", String.valueOf(tttStats.getWins()), true)
                    .addField("Draws", String.valueOf(tttStats.getDraws()), true)
                    .addField("Loses", String.valueOf(tttStats.getLoses()), true)
                    .build();
            event.reply(messageEmbed);
            return;
        }

        if (msg.getMentionedMembers().size() == 0) {
            channel.sendMessage(LanguageHandler.get(lang, "ttt_ping")).queue(
                    botMessage -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                            e -> e.getAuthor().equals(author)
                                    && e.getMessage().getMentionedMembers().size() > 0,
                            e -> challengedUser(jda, event, channel, msg, botMessage, e.getMessage(), author, lang),
                            15, TimeUnit.MINUTES, () -> timeoutRequest(botMessage, event, lang))
            );
        } else challengedUser(jda, event, channel, msg, null, null, author, lang);
    }

    // Timeout request
    private void timeoutRequest(Message botMessage, CommandEvent event, String lang) {
        event.reactWarning();
        botMessage.clearReactions().queue();
        botMessage.editMessage(LanguageHandler.get(lang, "ttt_timeout_request")).queue();
    }

    // Timeout match
    private void timeoutMatch(JDA jda, Message botMessage, User author, User enemy, User userOnTurn, LinkedHashMap<String, String> entries, CommandEvent event, String lang) {
        var tttField = getTttField(jda, author, enemy, userOnTurn, entries, true, null, true, lang, false);
        event.reactWarning();
        botMessage.clearReactions().queue();
        botMessage.editMessage(tttField).queue();
    }

    // TTT Field
    private String getTttField(JDA jda, User author, User enemy, User userOnTurn, LinkedHashMap<String, String> entries, boolean gameIsOver, User winner, boolean timedOut, String lang, boolean error) {
        var tttField = new StringBuilder();
        tttField.append(EmoteUtil.getEmote(jda, "tictactoe").getAsMention())
                .append(" **TicTacToe:** ")
                .append(author.getAsMention())
                .append(" ").append(LanguageHandler.get(lang, "ttt_vs")).append(" ")
                .append(enemy.getAsMention())
                .append("\n\n");

        var counter = 1;
        for (var entry : entries.entrySet()) {
            tttField.append(entry.getValue());
            if (counter != 3 && counter != 6 && counter != 9) tttField.append(" ");
            else tttField.append("\n");
            counter++;
        }

        if (error) tttField.append("\n").append(LanguageHandler.get(lang, "ttt_error"));
        else if (gameIsOver && timedOut) tttField.append("\n").append(LanguageHandler.get(lang, "ttt_timeout_match"));
        else if (gameIsOver) tttField.append("\n").append(winner == null ? LanguageHandler.get(lang, "ttt_draw") : String.format(LanguageHandler.get(lang, "ttt_won"), winner.getAsMention()));
        else {
            if (enemy.equals(jda.getSelfUser())) {
                // AI
                if (userOnTurn.equals(author)) tttField.append("\n").append(String.format(LanguageHandler.get(lang, "ttt_turn_ai"), enemy.getAsMention()));
                else tttField.append("\n").append(String.format(LanguageHandler.get(lang, "ttt_turn"), author.getAsMention()));
            } else tttField.append("\n").append(String.format(LanguageHandler.get(lang, "ttt_turn"), userOnTurn.equals(author) ? enemy.getAsMention() : author.getAsMention()));
        }
        return tttField.toString();
    }

    // Check for game over
    private boolean gameIsOver(LinkedHashMap<String, String> entries) {
        var upLeft = entries.get(arrows[0]);
        var up = entries.get(arrows[1]);
        var upRight = entries.get(arrows[2]);

        var left = entries.get(arrows[3]);
        var middle = entries.get(arrows[4]);
        var right = entries.get(arrows[5]);

        var downLeft = entries.get(arrows[6]);
        var down = entries.get(arrows[7]);
        var downRight = entries.get(arrows[8]);

        return !upLeft.equals(BLANK) && !up.equals(BLANK) && !upRight.equals(BLANK)
                && !left.equals(BLANK) && !middle.equals(BLANK) && !right.equals(BLANK)
                && !downLeft.equals(BLANK) && !down.equals(BLANK) && !downRight.equals(BLANK);
    }

    // Check for win
    private User getWinner(LinkedHashMap<String, String> entries, User author, User enemy) {
        var upLeft = entries.get(arrows[0]);
        var up = entries.get(arrows[1]);
        var upRight = entries.get(arrows[2]);

        var left = entries.get(arrows[3]);
        var middle = entries.get(arrows[4]);
        var right = entries.get(arrows[5]);

        var downLeft = entries.get(arrows[6]);
        var down = entries.get(arrows[7]);
        var downRight = entries.get(arrows[8]);

        if (upLeft.equals(up) && up.equals(upRight) && !upLeft.equals(BLANK)) {
            // Upper horizontal win
            return up.equals(X) ? author : enemy;
        } else if (left.equals(middle) && middle.equals(right) && !left.equals(BLANK)) {
            // Middle horizontal win
            return middle.equals(X) ? author : enemy;
        } else if (downLeft.equals(down) && down.equals(downRight) && !downLeft.equals(BLANK)) {
            // Lower horizontal win
            return down.equals(X) ? author : enemy;
        } else if (upLeft.equals(left) && left.equals(downLeft) && !upLeft.equals(BLANK)) {
            // Left vertical win
            return left.equals(X) ? author : enemy;
        } else if (up.equals(middle) && middle.equals(down) && !up.equals(BLANK)) {
            // Middle vertical win
            return middle.equals(X) ? author : enemy;
        } else if (upRight.equals(right) && right.equals(downRight) && !upRight.equals(BLANK)) {
            // Right vertical win
            return right.equals(X) ? author : enemy;
        } else if (upLeft.equals(middle) && middle.equals(downRight) && !upLeft.equals(BLANK)) {
            // UpLeft to downRight diagonal win
            return upLeft.equals(X) ? author : enemy;
        } else if (upRight.equals(middle) && middle.equals(downLeft) && !upRight.equals(BLANK)) {
            // UpRight ro downLeft diagonal win
            return upRight.equals(X) ? author : enemy;
        } else {
            // No winner
            return null;
        }
    }

    // User
    private void challengedUser(JDA jda, CommandEvent event, MessageChannel channel, Message originalMessage, Message botMessage, Message answerMessage, User author, String lang) {
        var enemy = botMessage != null ? answerMessage.getMentionedUsers().get(0) : originalMessage.getMentionedUsers().get(0);

        if (author.equals(enemy)) {
            event.replyWarning(LanguageHandler.get(lang, "ttt_challenge_self"));
            return;
        }

        if (enemy.isBot() && enemy.getIdLong() != jda.getSelfUser().getIdLong()) {
            event.reply(LanguageHandler.get(lang, "ttt_challenge_bot"));
            return;
        }

        if (answerMessage != null) answerMessage.delete().queue();
        if (botMessage != null) botMessage.delete().queue();

        var challengedServant = enemy.getIdLong() == jda.getSelfUser().getIdLong();
        if (challengedServant) startTttAi(jda, event, author, jda.getSelfUser(), lang);
        else answerChallenge(jda, event, channel, author, enemy, lang);
    }

    private void answerChallenge(JDA jda, CommandEvent event, MessageChannel channel, User author, User enemy, String lang) {
        channel.sendMessage(String.format(LanguageHandler.get(lang, "ttt_challenge_user"), enemy.getAsMention(), author.getAsMention())).queue(
                sentMessage -> sentMessage.addReaction(ACCEPT).queue(
                        s1 -> sentMessage.addReaction(DECLINE).queue(
                                s2 -> waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                                        e -> e.getUser().equals(enemy)
                                                && (e.getReactionEmote().getName().equals(ACCEPT)
                                                || e.getReactionEmote().getName().equals(DECLINE)),
                                        e -> {
                                            if (e.getReactionEmote().getName().equals(ACCEPT)) {
                                                sentMessage.clearReactions().queue();
                                                startTtt(jda, event, sentMessage, author, enemy, lang);
                                            } else {
                                                sentMessage.clearReactions().queue();
                                                sentMessage.editMessage(LanguageHandler.get(lang, "ttt_challenge_denied")).queue();
                                            }
                                        }, 15, TimeUnit.MINUTES, () -> timeoutRequest(sentMessage, event, lang))
                        )
                )
        );
    }

    private void startTtt(JDA jda, CommandEvent event, Message msg, User author, User enemy, String lang) {
        var entries = new LinkedHashMap<String, String>();
        for (var arrow : arrows) entries.put(arrow, BLANK);

        var tttField = getTttField(jda, author, enemy, author, entries, false, null, false, lang, false);

        msg.editMessage(tttField).queue(tttMessage -> {
            for (var arrow : arrows) tttMessage.addReaction(arrow).queue();
            waitForField(jda, event, msg, author, enemy, lang, entries, enemy);
        });
    }

    private void waitForField(JDA jda, CommandEvent event, Message msg, User author, User enemy, String lang, LinkedHashMap<String, String> entries, User userOnTurn) {
        waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                e -> e.getUser().equals(userOnTurn)
                        && e.getMessageIdLong() == msg.getIdLong()
                        && Arrays.asList(arrows).contains(e.getReactionEmote().getName())
                        && entries.get(e.getReactionEmote().getName()).equals(BLANK), // check for arrow already used
                e -> {
                    var emoji = e.getReactionEmote().getName();
                    entries.put(emoji, (userOnTurn.equals(author) ? X : O));
                    updateTtt(jda, event, msg, author, enemy, lang, entries, userOnTurn);
                }, 15, TimeUnit.MINUTES, () -> timeoutMatch(jda, msg, author, enemy, userOnTurn, entries, event, lang));
    }

    private void updateTtt(JDA jda, CommandEvent event, Message msg, User author, User enemy, String lang, LinkedHashMap<String, String> entries, User userOnTurn) {
        var tttField = getTttField(jda, author, enemy, userOnTurn, entries, false, null, false, lang, false);
        msg.editMessage(tttField).queue(newMsg -> {
            var gameIsOver = gameIsOver(entries);
            var winner = getWinner(entries, author, enemy);

            if (winner != null || gameIsOver) {
                // Game over. Someone won.
                endTtt(jda, author, enemy, userOnTurn, entries, msg, winner, lang, false);
            } else {
                // The show must go on
                waitForField(jda, event, newMsg, author, enemy, lang, entries, (userOnTurn.equals(author) ? enemy : author));
            }
        });
    }

    // AI
    private void startTttAi(JDA jda, CommandEvent event, User author, User ai, String lang) {
        var entries = new LinkedHashMap<String, String>();
        for (var arrow : arrows) entries.put(arrow, BLANK);

        var tttField = getTttField(jda, author, ai, author, entries, false, null, false, lang, false);

        var userMoves = new LinkedHashMap<Integer, String>(); // <Round, Emoji>

        event.getTextChannel().sendMessage(tttField).queue(tttMessage -> {
            rounds.put(tttMessage.getIdLong(), 1);
            for (var arrow : arrows) tttMessage.addReaction(arrow).queue();
            waitForFieldAi(jda, event, tttMessage, author, ai, lang, entries, ai, userMoves);
        });
    }

    private void waitForFieldAi(JDA jda, CommandEvent event, Message msg, User author, User ai, String lang, LinkedHashMap<String, String> entries, User userOnTurn, LinkedHashMap<Integer, String> userMoves) {
        var msgId = msg.getIdLong();
        var error = false;
        if (userOnTurn.equals(ai)) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            switch (rounds.get(msgId)) {
                case 1:
                    // Set in a random corner
                    var cornerEmoji = arrowsCorner[ThreadLocalRandom.current().nextInt(4)];
                    entries.put(cornerEmoji, O);
                    break;

                case 2:
                    var round2Emoji = TicTacToeMoves.round2(msgId, entries);
                    if (!round2Emoji.isEmpty()) entries.put(round2Emoji, O);
                    else {
                        System.out.println("ERROR! round2Emoji is empty");
                        error = true;
                    }
                    break;

                case 3:
                    var round3Emoji = TicTacToeMoves.round3(msgId, entries);
                    if (!round3Emoji.isEmpty()) entries.put(round3Emoji, O);
                    else {
                        System.out.println("ERROR! round3Emoji is empty");
                        error = true;
                    }
                    break;

                case 4:
                    var round4Emoji = TicTacToeMoves.round4(entries);
                    if (!round4Emoji.isEmpty()) entries.put(round4Emoji, O);
                    else {
                        System.out.println("ERROR! round4Emoji is empty");
                        error = true;
                    }
                    break;

                case 5:
                    // Only happens on draw, so we gonna fill the last remaining field
                    var round5Emoji = "";
                    for (var entry : entries.entrySet()) {
                        if (entry.getValue().equals(BLANK))
                            round5Emoji = entry.getKey();
                    }
                    if (!round5Emoji.isEmpty()) entries.put(round5Emoji, O);
                    else {
                        System.out.println("ERROR! round5Emoji is empty");
                        error = true;
                    }
                    break;
            }

            updateTttAi(jda, event, msg, author, ai, lang, entries, userOnTurn, userMoves, error);
        } else {
            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(userOnTurn)
                            && e.getMessageIdLong() == msg.getIdLong()
                            && Arrays.asList(arrows).contains(e.getReactionEmote().getName())
                            && entries.get(e.getReactionEmote().getName()).equals(BLANK),
                    e -> {
                        var emoji = e.getReactionEmote().getName();
                        entries.put(emoji, (userOnTurn.equals(author) ? X : O));
                        userMoves.put(rounds.get(msgId), emoji);
                        rounds.put(msgId, rounds.get(msgId) + 1);
                        updateTttAi(jda, event, msg, author, ai, lang, entries, userOnTurn, userMoves, false);
                    }, 15, TimeUnit.MINUTES, () -> timeoutMatch(jda, msg, author, ai, userOnTurn, entries, event, lang));
        }
    }

    private void updateTttAi(JDA jda, CommandEvent event, Message msg, User author, User enemy, String lang, LinkedHashMap<String, String> entries, User userOnTurn, LinkedHashMap<Integer, String> userMoves, boolean error) {
        var tttField = getTttField(jda, author, enemy, userOnTurn, entries, false, null, false, lang, false);
        msg.editMessage(tttField).queue(newMsg -> {
            var gameIsOver = gameIsOver(entries);
            var winner = getWinner(entries, author, enemy);

            if ((winner != null || gameIsOver) || error) {
                // Game over. Someone won.
                endTtt(jda, author, enemy, userOnTurn, entries, msg, winner, lang, error);
            } else {
                // The show must go on
                waitForFieldAi(jda, event, newMsg, author, enemy, lang, entries, (userOnTurn.equals(author) ? enemy : author), userMoves);
            }
        });
    }

    // End is used for both User and AI
    private void endTtt(JDA jda, User author, User enemy, User userOnTurn, LinkedHashMap<String, String> entries, Message msg, User winner, String lang, boolean error) {
        msg.clearReactions().queue();
        var tttField = getTttField(jda, author, enemy, userOnTurn, entries, true, winner, false, lang, error);
        msg.editMessage(tttField).queue();
        rounds.remove(msg.getIdLong());
        moveNames.remove(msg.getIdLong());

        var myAuthor = new MyUser(author);
        var myEnemy = new MyUser(enemy);

        if (winner == null) {
            // Draw
            myAuthor.incrementTttDraw();
            myEnemy.incrementTttDraw();
        } else if (author.equals(winner)) {
            // Author wins
            myAuthor.incrementTttWin();
            myEnemy.incrementTttLose();
        } else if (enemy.equals((winner))) {
            // Enemy wins
            myAuthor.incrementTttLose();
            myEnemy.incrementTttWin();
        }
    }
}
