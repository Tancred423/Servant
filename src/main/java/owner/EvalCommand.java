// Author: Tancred423 (https://github.com/Tancred423)
package owner;

import groovy.lang.GroovyShell;
import moderation.guild.Guild;
import moderation.user.User;
import net.dv8tion.jda.api.Permission;
import servant.Servant;
import utilities.Constants;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.concurrent.CompletableFuture;

public class EvalCommand extends Command {
    private final GroovyShell engine;
    private final String imports;

    public EvalCommand() {
        this.name = "eval";
        this.aliases = new String[] { "e" };
        this.help = "Evaluates groovy code";
        this.category = new Category("Owner");
        this.arguments = "[code]";
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = true;
        this.cooldown = Constants.OWNER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY
        };

        this.engine = new GroovyShell();
        this.imports = "import java.io.*\n" +
                "import java.lang.*\n" +
                "import java.util.*\n" +
                "import java.util.concurrent.*\n" +
                "import net.dv8tion.jda.api.*\n" +
                "import net.dv8tion.jda.api.entities.*\n" +
                "import net.dv8tion.jda.api.entities.impl.*\n" +
                "import net.dv8tion.jda.api.managers.*\n" +
                "import net.dv8tion.jda.api.managers.impl.*\n" +
                "import net.dv8tion.jda.api.utils.*\n";
    }

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                if (event.getArgs().isEmpty()) {
                    event.reply("Missing arguments");
                    return;
                }

                try {
                    engine.setProperty("args", event.getArgs().split(" "));
                    engine.setProperty("event", event);
                    engine.setProperty("message", event.getMessage());
                    engine.setProperty("channel", event.getChannel());
                    engine.setProperty("jda", event.getJDA());
                    engine.setProperty("guild", event.getGuild());
                    engine.setProperty("member", event.getMember());

                    var script = imports + event.getMessage().getContentRaw().split("\\s+", 2)[1];
                    var out = engine.evaluate(script);

                    if (out == null) event.reactSuccess();
                    else event.getChannel().sendMessage(out.toString()).queue();
                } catch (Exception e) {
                    event.reply(e.getMessage());
                }

                var guild = event.getGuild();
                var author = event.getAuthor();

                // Statistics.
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
                if (event.getGuild() != null)
                    new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Servant.threadPool);
    }
}
