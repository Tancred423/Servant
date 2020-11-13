package commands.utility.rating;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.*;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.awt.*;
import java.text.ParseException;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class RatingCommand extends Command {
    public RatingCommand() {
        this.name = "rating";
        this.aliases = new String[]{"rate"};
        this.help = "User rating out of 5";
        this.category = new Category("Utility");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.modCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.botPermissions = new Permission[]{
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var author = event.getAuthor();
        var myAuthor = new MyUser(author);
        var guild = event.getGuild();
        var myGuild = new MyGuild(guild);
        var lang = LanguageHandler.getLanguage(event);
        var p = myGuild.getPrefix();

        if (event.getArgs().isEmpty()) {
            var description = LanguageHandler.get(lang, "rate_description");
            var usage = String.format(LanguageHandler.get(lang, "rate_usage"), p, name, p, name, p, name);
            var hint = LanguageHandler.get(lang, "rate_hint");
            event.reply(MessageUtil.createUsageEmbed(name, author, lang, description, aliases, usage, hint));
            return;
        }

        String topic;
        HashMap<Character, Integer> timeArgs;
        try {
            topic = Parser.parseText(lang, event.getArgs(), 100);
            timeArgs = Parser.parseArguments(lang, event.getArgs());
        } catch (ParseException e) {
            event.replyWarning(e.getMessage());
            return;
        }

        var days = timeArgs.get('d');
        if (days == null) days = 0;
        var hours = timeArgs.get('h');
        if (hours == null) hours = 0;
        var minutes = timeArgs.get('m');
        if (minutes == null) minutes = 0;

        if (days == 0 && hours == 0 && minutes == 0) {
            event.replyWarning(LanguageHandler.get(lang, "missing_time_args"));
            return;
        }

        var now = Instant.now();
        var eventTime = now.plusMillis(TimeUtil.daysToMillis(days)).plusMillis(TimeUtil.hoursToMillis(hours)).plusMillis(TimeUtil.minutesToMillis(minutes));
        var nowIn30Days = now.plusMillis(TimeUtil.daysToMillis(30));

        if (eventTime.toEpochMilli() > nowIn30Days.toEpochMilli()) {
            event.replyWarning(LanguageHandler.get(lang, "rate_30_days"));
            return;
        }

        // Build message
        var jda = event.getJDA();
        var eb = new EmbedBuilder()
                .setColor(Color.decode(myAuthor.getColorCode()))
                .setAuthor(topic == null ? "Rating!" : "Rate: " + topic, null, author.getEffectiveAvatarUrl())
                .setDescription(String.format(LanguageHandler.get(lang, "rate_end_manually"), event.getAuthor().getAsMention()))
                .setFooter(LanguageHandler.get(lang, "rate_ends_at"), ImageUtil.getUrl(jda, "clock"))
                .setTimestamp(eventTime);

        event.getTextChannel().sendMessage(eb.build()).queue(msg -> {
            msg.addReaction(EmoteUtil.getEmoji(jda, "one")).queue();
            msg.addReaction(EmoteUtil.getEmoji(jda, "two")).queue();
            msg.addReaction(EmoteUtil.getEmoji(jda, "three")).queue();
            msg.addReaction(EmoteUtil.getEmoji(jda, "four")).queue();
            msg.addReaction(EmoteUtil.getEmoji(jda, "five")).queue();
            msg.addReaction(EmoteUtil.getEmoji(jda, "end")).queue();

            var rating = new Rating(event.getJDA(), lang, msg.getGuild().getIdLong(), msg.getTextChannel().getIdLong(), msg.getIdLong());
            rating.set(author.getIdLong(), eventTime, topic);

            /* End rating to given time.
             * In case the bot restarts, this service will break.
             * Therefore we will run any current giveaway in the database
             * from listeners.ReadyListener#startExecutorNew.
             */
            var delayInMillis = eventTime.toEpochMilli() - Instant.now().toEpochMilli();
            Servant.scheduledService.schedule(
                    new RatingEndTask(rating),
                    delayInMillis,
                    TimeUnit.MILLISECONDS
            );
        });
    }
}
