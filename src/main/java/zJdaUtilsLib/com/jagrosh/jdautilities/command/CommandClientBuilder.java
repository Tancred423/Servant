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
package zJdaUtilsLib.com.jagrosh.jdautilities.command;

import java.util.ArrayList;
import java.util.LinkedList;

import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.impl.AnnotatedModuleCompilerImpl;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.impl.CommandClientImpl;

public class CommandClientBuilder {
    private Game game = Game.playing("default");
    private OnlineStatus status = OnlineStatus.ONLINE;
    private String ownerId;
    private String[] coOwnerIds;
    private String prefix;
    private String altprefix;
    private String serverInvite;
    private String success;
    private String warning;
    private String error;
    private String carbonKey;
    private String botsKey;
    private String botsOrgKey;
    private final LinkedList<Command> commands = new LinkedList<>();
    private CommandListener listener;
    private boolean useHelp = true;
    private Consumer<CommandEvent> helpConsumer;
    private String helpWord;
    private ScheduledExecutorService executor;
    private int linkedCacheSize = 0;
    private AnnotatedModuleCompiler compiler = new AnnotatedModuleCompilerImpl();
    private GuildSettingsManager manager = null;

    public CommandClient build() {
        CommandClient client = new CommandClientImpl(ownerId, coOwnerIds, prefix, altprefix, game, status, serverInvite,
                                                     success, warning, error, carbonKey, botsKey, botsOrgKey, new ArrayList<>(commands), useHelp,
                                                     helpConsumer, helpWord, executor, linkedCacheSize, compiler, manager);
        if(listener!=null) client.setListener(listener);
        return client;
    }

    public CommandClientBuilder setOwnerId(String ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public CommandClientBuilder setCoOwnerIds(String... coOwnerIds) {
    	this.coOwnerIds = coOwnerIds;
    	return this;
    }

    public CommandClientBuilder setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public CommandClientBuilder setAlternativePrefix(String prefix) {
        this.altprefix = prefix;
        return this;
    }

    public CommandClientBuilder useHelpBuilder(boolean useHelp) {
    	this.useHelp = useHelp;
        return this;
    }

    public CommandClientBuilder setHelpConsumer(Consumer<CommandEvent> helpConsumer) {
        this.helpConsumer = helpConsumer;
        return this;
    }

    public CommandClientBuilder setHelpWord(String helpWord) {
        this.helpWord = helpWord;
        return this;
    }

    public CommandClientBuilder setServerInvite(String serverInvite) {
        this.serverInvite = serverInvite;
        return this;
    }

    public CommandClientBuilder setEmojis(String success, String warning, String error) {
        this.success = success;
        this.warning = warning;
        this.error = error;
        return this;
    }

    public CommandClientBuilder setGame(Game game) {
        this.game = game;
        return this;
    }

    public CommandClientBuilder useDefaultGame() {
        this.game = Game.playing("default");
        return this;
    }

    public CommandClientBuilder setStatus(OnlineStatus status) {
        this.status = status;
        return this;
    }

    public CommandClientBuilder addCommand(Command command) {
        commands.add(command);
        return this;
    }

    public CommandClientBuilder addCommands(Command... commands) {
        for(Command command: commands)
            this.addCommand(command);
        return this;
    }

    public CommandClientBuilder addAnnotatedModule(Object module) {
        this.commands.addAll(compiler.compile(module));
        return this;
    }

    public CommandClientBuilder addAnnotatedModules(Object... modules) {
        for(Object command : modules)
            addAnnotatedModule(command);
        return this;
    }

    public CommandClientBuilder setAnnotatedCompiler(AnnotatedModuleCompiler compiler) {
        this.compiler = compiler;
        return this;
    }

    public CommandClientBuilder setCarbonitexKey(String key) {
        this.carbonKey = key;
        return this;
    }

    public CommandClientBuilder setDiscordBotsKey(String key) {
        this.botsKey = key;
        return this;
    }

    public CommandClientBuilder setDiscordBotListKey(String key) {
        this.botsOrgKey = key;
        return this;
    }

    public CommandClientBuilder setListener(CommandListener listener) {
        this.listener = listener;
        return this;
    }

    public CommandClientBuilder setScheduleExecutor(ScheduledExecutorService executor) {
        this.executor = executor;
        return this;
    }

    public CommandClientBuilder setLinkedCacheSize(int linkedCacheSize) {
        this.linkedCacheSize = linkedCacheSize;
        return this;
    }

    public CommandClientBuilder setGuildSettingsManager(GuildSettingsManager manager) {
        this.manager = manager;
        return this;
    }
}