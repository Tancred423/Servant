// Author: Tancred423 (https://github.com/Tancred423)
package fun.randomAnimal;

import moderation.guild.Guild;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import servant.Log;
import utilities.Constants;
import utilities.Parser;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class CatCommand extends Command {
    public CatCommand() {
        this.name = "cat";
        this.aliases = new String[]{"catto"};
        this.help = "Random cat picture.";
        this.category = new Category("Fun");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, name)) return;
            event.getChannel().sendTyping().queue();

            var guild = event.getGuild();
            var author = event.getAuthor();

            try {
                var eb = new EmbedBuilder();
                eb.setColor(new User(event.getAuthor().getIdLong()).getColor(guild, author));
                eb.setImage(getImageUrl());
                event.reply(eb.build());
            } catch (IOException | IllegalArgumentException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            }

            // Statistics.
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
        });
    }

    private String getImageUrl() throws IOException {
        var url = new URL("http://random.cat");
        var urlConnection = url.openConnection();
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

        var sourceCode = toString(urlConnection.getInputStream());
        var split = sourceCode.split("<img src=\"");
        sourceCode = split[1];
        split = sourceCode.split("\" alt=");

        if (Parser.isValidDirectUrl(split[0])) return split[0];
        else return getImageUrl();
    }

    private static String toString(InputStream inputStream) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = bufferedReader.readLine()) != null) stringBuilder.append(inputLine);
            return stringBuilder.toString();
        }
    }
}
