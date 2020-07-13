// Author: Tancred423 (https://github.com/Tancred423)
package servant;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.JDA;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class LoggingTask implements Runnable {
    private final Exception e;
    private final JDA jda;
    private final String function;
    private final CommandEvent commandEvent;

    public LoggingTask(Exception e, JDA jda, String function) {
        this.e = e;
        this.jda = jda;
        this.function = function;
        this.commandEvent = null;
    }

    public LoggingTask(Exception e, JDA jda, String function, CommandEvent commandEvent) {
        this.e = e;
        this.jda = jda;
        this.function = function;
        this.commandEvent = commandEvent;
    }

    @Override
    public void run() {
        if (commandEvent != null) {
            // Notify User
            var lang = LanguageHandler.getLanguage(commandEvent);
            commandEvent.replyError(LanguageHandler.get(lang, "general_error"));
        }

        // Error Log
        if (e != null) e.printStackTrace();

        // Error DM
        if (jda != null) {
            var shardManager = jda.getShardManager();
            if (shardManager != null) {
                var botOwner = shardManager.getUserById(Servant.config.getBotOwnerId());
                if (botOwner != null) {
                    botOwner.openPrivateChannel().queue(privateChannel ->
                            privateChannel.sendMessage("```c\n" +
                                    "Error\n" +
                                    "-----\n" +
                                    (function == null ? "" : "Function: " + function + "\n") +
                                    (e == null ? "" : "Error: " + e.getMessage() + "\n") +
                                    "```").queue());
                }
            }
        }
    }
}
