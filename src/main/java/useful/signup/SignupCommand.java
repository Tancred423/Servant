// Author: Tancred423 (https://github.com/Tancred423)
package useful.signup;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import owner.blacklist.Blacklist;
import servant.Servant;
import useful.InvalidTitleException;
import utilities.*;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.Timestamp;
import java.time.DateTimeException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;

public class SignupCommand extends Command {
    public SignupCommand() {
        this.name = "signup";
        this.aliases = new String[] { "event" };
        this.help = "Organise events.";
        this.category = new Category("Useful");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                if (!Toggle.isEnabled(event, name)) return;
                if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

                var lang = LanguageHandler.getLanguage(event);
                var p = GuildHandler.getPrefix(event);

                if (event.getArgs().isEmpty()) {
                    var description = LanguageHandler.get(lang, "signup_description");
                    var usage = String.format(LanguageHandler.get(lang, "signup_usage"), p, name, p, name, p, name, p, name, p, name);
                    var hint = LanguageHandler.get(lang, "signup_hint");
                    event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
                    return;
                }

                var contentSplit = event.getArgs().trim().replaceAll(" +", " ").replace("\\", "").split(" ");

                String title;
                try {
                    title = parseTitle(event, contentSplit);
                } catch (InvalidTitleException e) {
                    event.replyError(e.getMessage());
                    return;
                }

                int amount;
                try {
                    amount = parseAmount(event, contentSplit, title);
                } catch (InvalidAmountException e) {
                    event.replyError(e.getMessage());
                    return;
                }

                ZonedDateTime eventDate;
                try {
                    eventDate = parseDate(event, contentSplit, amount);
                } catch (InvalidDateException e) {
                    event.replyError(e.getMessage());
                    return;
                }

                var internalGuild = new Guild(event.getGuild().getIdLong());
                var offset = internalGuild.getOffset(event.getGuild(), event.getAuthor());
                var isCustomDate = false;
                if (eventDate != null) isCustomDate = true;
                else eventDate = ZonedDateTime.now(ZoneOffset.of(offset)).plusWeeks(4);
                title = title == null ? "" : title;

                var guild = event.getGuild();
                var author = event.getAuthor();
                var internalAuthor = new User(author.getIdLong());

                var eb = new EmbedBuilder();
                eb.setColor(internalAuthor.getColor(guild, author));
                eb.setTitle(title.isEmpty() ? LanguageHandler.get(lang, "signup_embedtitle_empty") :
                        String.format(LanguageHandler.get(lang, "signup_embedtitle_notempty"), title));
                eb.setDescription(String.format(LanguageHandler.get(lang, "signup_embeddescription"),
                        Emote.getEmoji("upvote"), amount,
                        (isCustomDate ? LanguageHandler.get(lang, "signup_embeddescription_custom") : ""),
                        author.getAsMention()));
                eb.setFooter(isCustomDate ? LanguageHandler.get(lang, "signup_event") : LanguageHandler.get(lang, "signup_timeout"),
                        Image.getImageUrl("clock", guild, author));
                eb.setTimestamp(eventDate.toInstant());

                var upvote = Emote.getEmoji("upvote");
                var downvote = Emote.getEmoji("end");
                if (upvote == null || downvote == null) return;

                var finalEventDate = eventDate;
                var finalIsCustomDate = isCustomDate;
                var finalTitle = title;
                event.getChannel().sendMessage(eb.build()).queue(sentMessage -> {
                    sentMessage.addReaction(upvote).queue();
                    sentMessage.addReaction(downvote).queue();
                    internalGuild.setSignup(sentMessage.getIdLong(), author.getIdLong(), amount, finalTitle, Timestamp.valueOf(finalEventDate.toLocalDateTime()), sentMessage.getChannel().getIdLong(), finalIsCustomDate, guild, author);
                });

                // Statistics.
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
                new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Servant.threadPool);
    }

    private String parseTitle(CommandEvent event, String[] contentSplit) throws InvalidTitleException {
        var lang = LanguageHandler.getLanguage(event);
        if (contentSplit[0].startsWith("\"")) {
            if (contentSplit[0].endsWith("\"") && !contentSplit[0].equals("\"")) {
                // One Word Title
                var title = contentSplit[0].replace("\"", "");
                if (title.length() > 256) throw new InvalidTitleException(LanguageHandler.get(lang, "signup_titlelength"));
                else return title;
            } else {
                // Multiple Word title
                var title = new StringBuilder();
                var counter = 0;
                for (var content : contentSplit) {
                    title.append(content).append(" ");
                    if (content.endsWith("\"") && counter != 0) return title.toString().replace("\"", "").trim();
                    counter++;
                }
            }
        } else return null;

        throw new InvalidTitleException(LanguageHandler.get(lang, "signup_invalidtitle"));
    }

    private int parseAmount(CommandEvent event, String[] contentSplit, String title) throws InvalidAmountException {
        var lang = LanguageHandler.getLanguage(event);
        if (title == null) {
            // No title
            int amount;
            try {
                amount = Integer.parseInt(contentSplit[0]);
            } catch (NumberFormatException e) {
                throw new InvalidAmountException(String.format(LanguageHandler.get(lang, "signup_invalidamountparse"), contentSplit[0]));
            }
            if (amount < 1 || amount > 100) throw new InvalidAmountException(LanguageHandler.get(lang, "signup_invalidamountrange"));
            else return amount;
        } else {
            // Has a title
            if (contentSplit.length > 1) {
                var counter = 0;
                for (int i = 0; i < contentSplit.length; i++) {
                    if (contentSplit[i].endsWith("\"") && !(contentSplit[i].equals("\"") && counter == 0)) {
                        try {
                            return Integer.parseInt(contentSplit[i + 1]);
                        } catch (IndexOutOfBoundsException e) {
                            throw new InvalidAmountException(LanguageHandler.get(lang, "signup_missingamount"));
                        }
                    }
                    counter++;
                }
            } else throw new InvalidAmountException(LanguageHandler.get(lang, "signup_missingamount"));
        }

        throw new InvalidAmountException(LanguageHandler.get(lang, "signup_invalidamount"));
    }

    private ZonedDateTime parseDate(CommandEvent event, String[] contentSplit, int amount) throws InvalidDateException {
        var lang = LanguageHandler.getLanguage(event);

        // Only amount was given
        if (contentSplit.length == 1) return null;

        var date = contentSplit[contentSplit.length - 2];
        var time = contentSplit[contentSplit.length - 1];

        // No date and time given
        if (time.matches("[0-9]+") && time.length() <= 3 && Integer.parseInt(contentSplit[contentSplit.length - 1]) == amount)
            return null;

        if (Parser.isValidDateTime(date + " " + time)) {
            var offset = new Guild(event.getGuild().getIdLong()).getOffset(event.getGuild(), event.getAuthor());
            var now = ZonedDateTime.now(ZoneOffset.of(offset));
            ZonedDateTime eventDate;
            try {
                eventDate = ZonedDateTime.parse(date + "T" + time + offset);
            } catch (DateTimeException e) {
                throw new InvalidDateException(LanguageHandler.get(lang, "signup_invaliddateday"));
            }
            var weekMillis = Constants.WEEK_MILLIS;
            var timeDif = Parser.getTimeDifferenceInMillis(now, eventDate.minusMinutes(30));
            if (timeDif <= 4 * weekMillis && timeDif > 0L) return eventDate;
            else throw new InvalidDateException(LanguageHandler.get(lang, "signup_invaliddatedistance"));
        } else throw new InvalidDateException(LanguageHandler.get(lang, "signup_invaliddate"));
    }
}
