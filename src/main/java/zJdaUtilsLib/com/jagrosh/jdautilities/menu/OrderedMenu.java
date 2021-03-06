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
package zJdaUtilsLib.com.jagrosh.jdautilities.menu;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class OrderedMenu extends Menu {
    private final Color color;
    private final String text;
    private final String description;
    private final List<String> choices;
    private final BiConsumer<Message, Integer> action;
    private final Consumer<Message> cancel;
    private final boolean useLetters;
    private final boolean allowTypedInput;
    private final boolean useCancel;
    
    public final static String[] NUMBERS = new String[]{"1\u20E3","2\u20E3","3\u20E3",
        "4\u20E3","5\u20E3","6\u20E3","7\u20E3","8\u20E3","9\u20E3", "\uD83D\uDD1F"};

    public final static String[] LETTERS = new String[]{"\uD83C\uDDE6","\uD83C\uDDE7","\uD83C\uDDE8",
        "\uD83C\uDDE9","\uD83C\uDDEA","\uD83C\uDDEB","\uD83C\uDDEC","\uD83C\uDDED","\uD83C\uDDEE","\uD83C\uDDEF"};

    public final static String CANCEL = "\u274C";
    
    OrderedMenu(EventWaiter waiter, Set<User> users, Set<Role> roles, long timeout, TimeUnit unit,
                Color color, String text, String description, List<String> choices, BiConsumer<Message,Integer> action,
                Consumer<Message> cancel, boolean useLetters, boolean allowTypedInput, boolean useCancel) {
        super(waiter, users, roles, timeout, unit);
        this.color = color;
        this.text = text;
        this.description = description;
        this.choices = choices;
        this.action = action;
        this.cancel = cancel;
        this.useLetters = useLetters;
        this.allowTypedInput = allowTypedInput;
        this.useCancel = useCancel;
    }

    @Override
    public void display(MessageChannel channel) {
        // This check is basically for whether or not the menu can even display.
        // Is from text channel
        // Does not allow typed input
        // Does not have permission to add reactions
        if(channel.getType()==ChannelType.TEXT
                && !allowTypedInput
                && !PermissionUtil.checkPermission((TextChannel)channel,
                ((TextChannel)channel).getGuild().getSelfMember(), Permission.MESSAGE_ADD_REACTION))
            throw new PermissionException("Must be able to add reactions if not allowing typed input!");
        initialize(channel.sendMessage(getMessage()));
    }

    @Override
    public void display(Message message) {
        // This check is basically for whether or not the menu can even display.
        // Is from text channel
        // Does not allow typed input
        // Does not have permission to add reactions
        if(message.getChannelType() == ChannelType.TEXT
                && !allowTypedInput 
                && !PermissionUtil.checkPermission(message.getTextChannel(),
                message.getGuild().getSelfMember(), Permission.MESSAGE_ADD_REACTION))
            throw new PermissionException("Must be able to add reactions if not allowing typed input!");
        initialize(message.editMessage(getMessage()));
    }

    // Initializes the OrderedMenu using a Message RestAction
    // This is either through editing a previously existing Message
    // OR through sending a new one to a TextChannel.
    private void initialize(RestAction<Message> ra) {
        ra.queue(m -> {
            try {
                // From 0 until the number of choices.
                // The last run of this loop will be used to queue
                // the last reaction and possibly a cancel emoji
                // if useCancel was set true before this OrderedMenu
                // was built.
                for(int i = 0; i < choices.size(); i++)
                {
                    // If this is not the last run of this loop
                    if(i < choices.size()-1)
                        m.addReaction(getEmoji(i)).queue();
                    // If this is the last run of this loop
                    else 
                    {
                        RestAction<Void> re = m.addReaction(getEmoji(i));
                        // If we're using the cancel function we want
                        // to add a "step" so we queue the last emoji being
                        // added and then make the RestAction to start waiting
                        // on the cancel reaction being added.
                        if(useCancel)
                        {
                            re.queue(); // queue the last emoji
                            re = m.addReaction(CANCEL);
                        }
                        // queue the last emoji or the cancel button
                        re.queue(v -> {
                            // Depending on whether we are allowing text input,
                            // we call a different method.
                            if(allowTypedInput)
                                waitGeneric(m);
                            else
                                waitReactionOnly(m);
                        });
                    }
                }
            } catch(PermissionException ex) {
                // If there is a permission exception mid process, we'll still
                // attempt to make due with what we have.
                if(allowTypedInput)
                    waitGeneric(m);
                else
                    waitReactionOnly(m);
            }
        });
    }

    // Waits for either a button being pushed OR a typed input
    private void waitGeneric(Message m) {
        // Wait for a GenericMessageEvent
        waiter.waitForEvent(GenericMessageEvent.class, e -> {
            // If we're dealing with a message reaction being added we return whether it's valid
            if(e instanceof MessageReactionAddEvent) return isValidReaction(m, (MessageReactionAddEvent)e);
            // If we're dealing with a received message being added we return whether it's valid
            if(e instanceof MessageReceivedEvent) return isValidMessage(m, (MessageReceivedEvent)e);
            // Otherwise return false
            return false;
        }, e -> {
            m.delete().queue();
            // If it's a valid MessageReactionAddEvent
            if(e instanceof MessageReactionAddEvent) {
                MessageReactionAddEvent event = (MessageReactionAddEvent)e;
                // Process which reaction it is
                if(event.getReaction().getReactionEmote().getName().equals(CANCEL))
                    cancel.accept(m);
                else
                    // The int provided in the success consumer is not indexed from 0 to number of choices - 1,
                    // but from 1 to number of choices. So the first choice will correspond to 1, the second
                    // choice to 2, etc.
                    action.accept(m, getNumber(event.getReaction().getReactionEmote().getName()));
            }
            // If it's a valid MessageReceivedEvent
            else if (e instanceof MessageReceivedEvent) {
                MessageReceivedEvent event = (MessageReceivedEvent)e;
                // Get the number in the message and process
                int num = getMessageNumber(event.getMessage().getContentRaw());
                if(num<0 || num>choices.size()) cancel.accept(m);
                else action.accept(m, num);
            }
        }, timeout, unit, () -> cancel.accept(m));
    }

    // Waits only for reaction input
    private void waitReactionOnly(Message m) {
        // This one is only for reactions
        waiter.waitForEvent(MessageReactionAddEvent.class, e -> {
            return isValidReaction(m, e);
        }, e -> {
            m.delete().queue();
            if(e.getReaction().getReactionEmote().getName().equals(CANCEL))
                cancel.accept(m);
            else
                // The int provided in the success consumer is not indexed from 0 to number of choices - 1,
                // but from 1 to number of choices. So the first choice will correspond to 1, the second
                // choice to 2, etc.
                action.accept(m, getNumber(e.getReaction().getReactionEmote().getName()));
        }, timeout, unit, () -> cancel.accept(m));
    }

    // This is where the displayed message for the OrderedMenu is built.
    private Message getMessage() {
        MessageBuilder mbuilder = new MessageBuilder();
        if(text!=null) mbuilder.append(text);
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<choices.size(); i++) sb.append("\n").append(getEmoji(i)).append(" ").append(choices.get(i));
        mbuilder.setEmbed(new EmbedBuilder().setColor(color)
                .setDescription(description==null ? sb.toString() : description+sb.toString()).build());
        return mbuilder.build();
    }
    
    private boolean isValidReaction(Message m, MessageReactionAddEvent e) {
        // The message is not the same message as the menu
        if(!e.getMessageId().equals(m.getId())) return false;
        // The user is not valid
        if(!isValidUser(e.getUser(), e.getGuild())) return false;
        // The reaction is the cancel reaction
        if(e.getReaction().getReactionEmote().getName().equals(CANCEL)) return true;

        int num = getNumber(e.getReaction().getReactionEmote().getName());
        return !(num<0 || num>choices.size());
    }
    
    private boolean isValidMessage(Message m, MessageReceivedEvent e) {
        // If the channel is not the same channel
        if(!e.getChannel().equals(m.getChannel()))
            return false;
        // Otherwise if it's a valid user or not
        return isValidUser(e.getAuthor(), e.getGuild());
    }
    
    private String getEmoji(int number)
    {
        return useLetters ? LETTERS[number] : NUMBERS[number];
    }

    // Gets the number emoji by the name.
    // This is kinda the opposite of the getEmoji method
    // except it's implementation is to provide the number
    // to the selection consumer when a choice is made.
    private int getNumber(String emoji) {
        String[] array = useLetters ? LETTERS : NUMBERS;
        for(int i=0; i<array.length; i++) if(array[i].equals(emoji)) return i+1;
        return -1;
    }
    
    private int getMessageNumber(String message) {
        if(useLetters)
            // This doesn't look good, but bear with me for a second:
            // So the maximum number of letters you can have as reactions
            // is 10 (the maximum number of choices in general even).
            // If you look carefully, you'll see that a corresponds to the
            // index 1, b to the index 2, and so on.
            return message.length()==1 ? " abcdefghij".indexOf(message.toLowerCase()) : -1;
        else {
            // The same as above applies here, albeit in a different way.
            if(message.length()==1) return " 123456789".indexOf(message);
            return message.equals("10") ? 10 : -1;
        }
    }

    public static class Builder extends Menu.Builder<Builder, OrderedMenu> {
        private Color color;
        private String text;
        private String description;
        private final List<String> choices = new LinkedList<>();
        private BiConsumer<Message, Integer> selection;
        private Consumer<Message> cancel = (m) -> {};
        private boolean useLetters = false;
        private boolean allowTypedInput = true;
        private boolean addCancel = false;

        @Override
        public OrderedMenu build() {
            Checks.check(waiter != null, "Must set an EventWaiter");
            Checks.check(!choices.isEmpty(), "Must have at least one choice");
            Checks.check(choices.size() <= 10, "Must have no more than ten choices");
            Checks.check(selection != null, "Must provide an selection consumer");
            Checks.check(text != null || description != null, "Either text or description must be set");
            return new OrderedMenu(waiter,users,roles,timeout,unit,color,text,description,choices,
                selection,cancel,useLetters,allowTypedInput,addCancel);
        }

        public Builder setColor(Color color) {
            this.color = color;
            return this;
        }

        public Builder useLetters() {
            this.useLetters = true;
            return this;
        }

        public Builder useNumbers() {
            this.useLetters = false;
            return this;
        }

        public Builder allowTextInput(boolean allow) {
            this.allowTypedInput = allow;
            return this;
        }

        public Builder useCancelButton(boolean use) {
            this.addCancel = use;
            return this;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setSelection(BiConsumer<Message, Integer> selection) {
            this.selection = selection;
            return this;
        }

        public Builder setCancel(Consumer<Message> cancel) {
            this.cancel = cancel;
            return this;
        }

        public Builder addChoice(String choice) {
            Checks.check(choices.size() < 10, "Cannot set more than 10 choices");

            this.choices.add(choice);
            return this;
        }

        public Builder addChoices(String... choices) {
            for(String choice : choices) addChoice(choice);
            return this;
        }

        public Builder setChoices(String... choices) {
            clearChoices();
            return addChoices(choices);
        }

        public Builder clearChoices() {
            this.choices.clear();
            return this;
        }
    }
}
