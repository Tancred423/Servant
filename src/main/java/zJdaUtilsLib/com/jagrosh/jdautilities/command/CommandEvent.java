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

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.internal.utils.Checks;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.impl.CommandClientImpl;
import zJdaUtilsLib.com.jagrosh.jdautilities.commons.utils.SafeIdUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;

public class CommandEvent {
    public static int MAX_MESSAGES = 2;
    
    private final MessageReceivedEvent event;
    private String args;
    private final CommandClient client;

    public CommandEvent(MessageReceivedEvent event, String args, CommandClient client) {
        this.event = event;
        this.args = args == null ? "" : args;
        this.client = client;
    }

    public String getArgs()
    {
        return args;
    }
    void setArgs(String args)
    {
        this.args = args;
    }

    public MessageReceivedEvent getEvent()
    {
        return event;
    }

    public CommandClient getClient() {
        return client;
    }

    public void linkId(Message message) {
        Checks.check(message.getAuthor().equals(getSelfUser()), "Attempted to link a Message who's author was not the bot!");
        ((CommandClientImpl)client).linkIds(event.getMessageIdLong(), message);
    }

    public SelfUser getSelfUser() {
        return event.getJDA().getSelfUser();
    }

    public void reply(String message)
    {
        sendMessage(event.getChannel(), message);
    }
    public void reply(String message, Consumer<Message> success)
    {
    	sendMessage(event.getChannel(), message, success);
    }
    public void reply(String message, Consumer<Message> success, Consumer<Throwable> failure) {
        sendMessage(event.getChannel(), message, success, failure);
    }
    public void reply(MessageEmbed embed) {
        event.getChannel().sendMessage(embed).queue(m -> {
            if(event.isFromType(ChannelType.TEXT))
                linkId(m);
        });
    }
    public void reply(MessageEmbed embed, Consumer<Message> success) {
    	event.getChannel().sendMessage(embed).queue(m -> {
    	    if(event.isFromType(ChannelType.TEXT))
    	        linkId(m);
    	    success.accept(m);
        });
    }
    public void reply(MessageEmbed embed, Consumer<Message> success, Consumer<Throwable> failure) {
        event.getChannel().sendMessage(embed).queue(m -> {
            if(event.isFromType(ChannelType.TEXT))
                linkId(m);
            success.accept(m);
        }, failure);
    }
    public void reply(Message message) {
        event.getChannel().sendMessage(message).queue(m -> {
            if(event.isFromType(ChannelType.TEXT))
                linkId(m);
        });
    }
    public void reply(Message message, Consumer<Message> success) {
        event.getChannel().sendMessage(message).queue(m -> {
            if(event.isFromType(ChannelType.TEXT))
                linkId(m);
            success.accept(m);
        });
    }
    public void reply(Message message, Consumer<Message> success, Consumer<Throwable> failure) {
        event.getChannel().sendMessage(message).queue(m -> {
            if(event.isFromType(ChannelType.TEXT))
                linkId(m);
            success.accept(m);
        }, failure);
    }
    public void reply(File file, String filename)
    {
        event.getChannel().sendMessage("").addFile(file, filename).queue();
    }
    public void reply(String message, File file, String filename) {
        Message msg = message==null ? new MessageBuilder().build() : new MessageBuilder().append(splitMessage(message).get(0)).build();
        event.getChannel().sendMessage(msg).addFile(file, filename).queue();
    }

    public void replyFormatted(String format, Object... args) {
        sendMessage(event.getChannel(), String.format(format, args));
    }

    public void replyOrAlternate(MessageEmbed embed, String alternateMessage) {
        try {
            event.getChannel().sendMessage(embed).queue();
        } catch(PermissionException e) {
            reply(alternateMessage);
        }
    }

    public void replyOrAlternate(String message, File file, String filename, String alternateMessage) {
        Message msg = message==null ? new MessageBuilder().build() : new MessageBuilder().append(splitMessage(message).get(0)).build();
        try {
            event.getChannel().sendMessage(msg).addFile(file, filename).queue();
        } catch(Exception e) {
            reply(alternateMessage);
        }
    }


    public void replyInDm(String message) {
        if(event.isFromType(ChannelType.PRIVATE)) reply(message);
        else event.getAuthor().openPrivateChannel().queue(pc -> sendMessage(pc, message));
    }

    public void replyInDm(String message, Consumer<Message> success) {
        if(event.isFromType(ChannelType.PRIVATE)) reply(message, success);
        else event.getAuthor().openPrivateChannel().queue(pc -> sendMessage(pc, message, success));
    }

    public void replyInDm(String message, Consumer<Message> success, Consumer<Throwable> failure) {
        if(event.isFromType(ChannelType.PRIVATE)) reply(message, success, failure);
        else event.getAuthor().openPrivateChannel().queue(pc -> sendMessage(pc, message, success, failure), failure);
    }

    public void replyInDm(MessageEmbed embed) {
        if(event.isFromType(ChannelType.PRIVATE)) reply(embed);
        else event.getAuthor().openPrivateChannel().queue(pc -> pc.sendMessage(embed).queue());
    }

    public void replyInDm(MessageEmbed embed, Consumer<Message> success) {
        if(event.isFromType(ChannelType.PRIVATE)) getPrivateChannel().sendMessage(embed).queue(success);
        else event.getAuthor().openPrivateChannel().queue(pc -> pc.sendMessage(embed).queue(success));
    }

    public void replyInDm(MessageEmbed embed, Consumer<Message> success, Consumer<Throwable> failure) {
        if(event.isFromType(ChannelType.PRIVATE)) getPrivateChannel().sendMessage(embed).queue(success, failure);
        else event.getAuthor().openPrivateChannel().queue(pc -> pc.sendMessage(embed).queue(success, failure), failure);
    }

    public void replyInDm(Message message) {
        if(event.isFromType(ChannelType.PRIVATE)) reply(message);
        else event.getAuthor().openPrivateChannel().queue(pc -> pc.sendMessage(message).queue());
    }

    public void replyInDm(Message message, Consumer<Message> success) {
        if(event.isFromType(ChannelType.PRIVATE)) getPrivateChannel().sendMessage(message).queue(success);
        else event.getAuthor().openPrivateChannel().queue(pc -> pc.sendMessage(message).queue(success));
    }

    public void replyInDm(Message message, Consumer<Message> success, Consumer<Throwable> failure) {
        if(event.isFromType(ChannelType.PRIVATE)) getPrivateChannel().sendMessage(message).queue(success, failure);
        else event.getAuthor().openPrivateChannel().queue(pc -> pc.sendMessage(message).queue(success, failure), failure);
    }

    public void replyInDm(String message, File file, String filename) {
        if(event.isFromType(ChannelType.PRIVATE)) reply(message, file, filename);
        else {
            Message msg = message==null ? new MessageBuilder().build() : new MessageBuilder().append(splitMessage(message).get(0)).build();
            event.getAuthor().openPrivateChannel().queue(pc -> pc.sendMessage(msg).addFile(file, filename).queue());
        }
    }

    public void replySuccess(String message)
    {
        reply(client.getSuccess()+" "+message);
    }

    public void replySuccess(String message, Consumer<Message> queue)
    {
        reply(client.getSuccess()+" "+message, queue);
    }

    public void replyWarning(String message)
    {
        reply(client.getWarning()+" "+message);
    }

    public void replyWarning(String message, Consumer<Message> queue)
    {
        reply(client.getWarning()+" "+message, queue);
    }

    public void replyError(String message)
    {
        reply(client.getError()+" "+message);
    }

    public void replyError(String message, Consumer<Message> queue)
    {
        reply(client.getError()+" "+message, queue);
    }

    public void reactSuccess()
    {
        react(client.getSuccess());
    }

    public void reactWarning()
    {
        react(client.getWarning());
    }

    public void reactError()
    {
        react(client.getError());
    }

    public void async(Runnable runnable) {
        Checks.notNull(runnable, "Runnable");
        client.getScheduleExecutor().submit(runnable);
    }

    private void react(String reaction) {
        if(reaction == null || reaction.isEmpty()) return;
        try {
            var id = SafeIdUtil.safeConvert(reaction.replaceAll("<a?:.+:(\\d+)>", "$1"));
            var emote = event.getJDA().getEmoteById(id);
            if (emote == null) event.getMessage().addReaction(reaction).queue(s -> {}, f -> {});
            else event.getMessage().addReaction(emote).queue(s -> {}, f -> {});
        } catch(PermissionException ignored) {}
    }
    
    private void sendMessage(MessageChannel chan, String message) {
        var messages = splitMessage(message);
        for(int i=0; i<MAX_MESSAGES && i<messages.size(); i++) {
            chan.sendMessage(messages.get(i)).queue(m -> {
                if(event.isFromType(ChannelType.TEXT)) linkId(m);
            });
        }
    }
    
    private void sendMessage(MessageChannel chan, String message, Consumer<Message> success) {
        ArrayList<String> messages = splitMessage(message);
        for(int i=0; i<MAX_MESSAGES && i<messages.size(); i++) {
            if(i+1==MAX_MESSAGES || i+1==messages.size()) {
                chan.sendMessage(messages.get(i)).queue(m -> {
                    if(event.isFromType(ChannelType.TEXT)) linkId(m);
                    success.accept(m);
                });
            } else {
                chan.sendMessage(messages.get(i)).queue(m -> {
                    if(event.isFromType(ChannelType.TEXT)) linkId(m);
                });
            }
        }
    }

    private void sendMessage(MessageChannel chan, String message, Consumer<Message> success, Consumer<Throwable> failure) {
        ArrayList<String> messages = splitMessage(message);
        for(int i = 0; i < MAX_MESSAGES && i < messages.size(); i++) {
            if(i + 1 == MAX_MESSAGES || i + 1 == messages.size()) {
                chan.sendMessage(messages.get(i)).queue(m -> {
                    if(event.isFromType(ChannelType.TEXT)) linkId(m);
                    success.accept(m);
                }, failure);
            } else {
                chan.sendMessage(messages.get(i)).queue(m -> {
                    if(event.isFromType(ChannelType.TEXT)) linkId(m);
                });
            }
        }
    }

    public static ArrayList<String> splitMessage(String stringtoSend) {
        ArrayList<String> msgs =  new ArrayList<>();
        if(stringtoSend!=null) {
            stringtoSend = stringtoSend.replace("@everyone", "@\u0435veryone").replace("@here", "@h\u0435re").trim();
            while(stringtoSend.length()>2000) {
                int leeway = 2000 - (stringtoSend.length()%2000);
                int index = stringtoSend.lastIndexOf("\n", 2000);
                if(index<leeway)
                    index = stringtoSend.lastIndexOf(" ", 2000);
                if(index<leeway)
                    index=2000;
                String temp = stringtoSend.substring(0,index).trim();
                if(!temp.equals(""))
                    msgs.add(temp);
                stringtoSend = stringtoSend.substring(index).trim();
            }
            if(!stringtoSend.equals("")) msgs.add(stringtoSend);
        }
        return msgs;
    }

    public Member getSelfMember()
    {
        return event.getGuild() == null ? null : event.getGuild().getSelfMember();
    }

    public boolean isOwner() {
    	if(event.getAuthor().getId().equals(this.getClient().getOwnerId())) return true;
        if(this.getClient().getCoOwnerIds()==null) return false;
        for(String id : this.getClient().getCoOwnerIds()) if(id.equals(event.getAuthor().getId())) return true;
        return false;
    }

    public User getAuthor()
    {
        return event.getAuthor();
    }
    public MessageChannel getChannel()
    {
        return event.getChannel();
    }
    public ChannelType getChannelType()
    {
        return event.getChannelType();
    }
//    public Invite.Group getGroup()
//    {
//        return event.getGroup();
//    }
    public Guild getGuild() {
        return event.isFromGuild() ? event.getGuild() : null;
    }
    public JDA getJDA()
    {
        return event.getJDA();
    }
    public Member getMember()
    {
        return event.getMember();
    }
    public Message getMessage()
    {
        return event.getMessage();
    }
    public PrivateChannel getPrivateChannel()
    {
        return event.getPrivateChannel();
    }
    public long getResponseNumber()
    {
        return event.getResponseNumber();
    }
    public TextChannel getTextChannel()
    {
        return event.getTextChannel();
    }
    public boolean isFromType(ChannelType channelType)
    {
        return event.isFromType(channelType);
    }
}
