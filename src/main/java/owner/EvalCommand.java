package owner;

import groovy.lang.GroovyShell;
import net.dv8tion.jda.core.Permission;
import utilities.Constants;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class EvalCommand extends Command {
    private final GroovyShell engine;
    private final String imports;

    public EvalCommand() {
        this.name = "eval";
        this.aliases = new String[0];
        this.help = "Evaluates groovy code";
        this.category = new Category("Owner");
        this.arguments = "[code]";
        this.hidden = true;
        this.guildOnly = false;
        this.ownerCommand = true;
        this.cooldown = Constants.OWNER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[0];

        this.engine = new GroovyShell();
        this.imports = "import java.io.*\n" +
                "import java.lang.*\n" +
                "import java.util.*\n" +
                "import java.util.concurrent.*\n" +
                "import net.dv8tion.jda.core.*\n" +
                "import net.dv8tion.jda.core.entities.*\n" +
                "import net.dv8tion.jda.core.entities.impl.*\n" +
                "import net.dv8tion.jda.core.managers.*\n" +
                "import net.dv8tion.jda.core.managers.impl.*\n" +
                "import net.dv8tion.jda.core.utils.*\n";
    }

    @Override
    protected void execute(CommandEvent event) {
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

            event.getChannel().sendMessage(out == null ? "Executed without error" : out.toString()).queue();
        } catch (Exception e) {
            event.reply(e.getMessage());
        }
    }
}
