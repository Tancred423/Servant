/*
 * Copyright 2016-2018 John Grosh (jagrosh) & Kaidan Gustave (TheMonitorLizard)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Modified by Tancred (https://github.com/Tancred423)
// Modification: AboutCommand() -> More detailed constructor.
package zJdaUtilsLib.com.jagrosh.jdautilities.examples.command;

import java.time.temporal.ChronoUnit;

import net.dv8tion.jda.core.Permission;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.examples.doc.Author;

@Author("John Grosh (jagrosh)")
public class PingCommand extends Command {
    public PingCommand() {
        this.name = "ping";
        this.aliases = new String[]{"pong", "latency"};
        this.help = "Bot's latency.";
        this.category = null;
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.cooldown = 0;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[0];
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply("Ping: ...", m -> {
            long ping = event.getMessage().getCreationTime().until(m.getCreationTime(), ChronoUnit.MILLIS);
            m.editMessage("Ping: " + ping  + "ms | Websocket: " + event.getJDA().getPing() + "ms").queue();
        });
    }
}
