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

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.utils.Checks;
import zJdaUtilsLib.com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class SelectionDialog extends Menu {
    private final List<String> choices;
    private final String leftEnd, rightEnd;
    private final String defaultLeft, defaultRight;
    private final Function<Integer,Color> color;
    private final boolean loop;
    private final Function<Integer,String> text;
    private final BiConsumer<Message, Integer> success;
    private final Consumer<Message> cancel;
    
    public static final String UP = "\uD83D\uDD3C";
    public static final String DOWN = "\uD83D\uDD3D";
    public static final String SELECT = "\u2705";
    public static final String CANCEL = "\u274E";
    
    SelectionDialog(EventWaiter waiter, Set<User> users, Set<Role> roles, long timeout, TimeUnit unit,
                    List<String> choices, String leftEnd, String rightEnd, String defaultLeft, String defaultRight,
                    Function<Integer,Color> color, boolean loop, BiConsumer<Message, Integer> success,
                    Consumer<Message> cancel, Function<Integer,String> text) {
        super(waiter, users, roles, timeout, unit);
        this.choices = choices;
        this.leftEnd = leftEnd;
        this.rightEnd = rightEnd;
        this.defaultLeft = defaultLeft;
        this.defaultRight = defaultRight;
        this.color = color;
        this.loop = loop;
        this.success = success;
        this.cancel = cancel;
        this.text = text;
    }

    @Override
    public void display(MessageChannel channel)
    {
        showDialog(channel, 1);
    }

    @Override
    public void display(Message message)
    {
        showDialog(message, 1);
    }

    public void showDialog(MessageChannel channel, int selection) {
        if(selection<1)
            selection = 1;
        else if(selection>choices.size())
            selection = choices.size();
        Message msg = render(selection);
        initialize(channel.sendMessage(msg), selection);
    }

    public void showDialog(Message message, int selection) {
        if(selection<1)
            selection = 1;
        else if(selection>choices.size())
            selection = choices.size();
        Message msg = render(selection);
        initialize(message.editMessage(msg), selection);
    }
    
    private void initialize(RestAction<Message> action, int selection) {
        action.queue(m -> {
            if(choices.size()>1) {
                m.addReaction(UP).queue();
                m.addReaction(SELECT).queue();
                m.addReaction(CANCEL).queue();
                m.addReaction(DOWN).queue(v -> selectionDialog(m, selection), v -> selectionDialog(m, selection));
            } else {
                m.addReaction(SELECT).queue();
                m.addReaction(CANCEL).queue(v -> selectionDialog(m, selection), v -> selectionDialog(m, selection));
            }
        });
    }
    
    private void selectionDialog(Message message, int selection) {
        waiter.waitForEvent(MessageReactionAddEvent.class, event -> {
            if(!event.getMessageId().equals(message.getId()))
                return false;
            if(!(UP.equals(event.getReaction().getReactionEmote().getName())
                    || DOWN.equals(event.getReaction().getReactionEmote().getName())
                    || CANCEL.equals(event.getReaction().getReactionEmote().getName())
                    || SELECT.equals(event.getReaction().getReactionEmote().getName())))
                return false;
            return isValidUser(event.getUser(), event.getGuild());
        }, event -> {
            int newSelection = selection;
            switch(event.getReaction().getReactionEmote().getName()) {
                case UP:
                    if(newSelection>1)
                        newSelection--;
                    else if(loop)
                        newSelection = choices.size();
                    break;
                case DOWN:
                    if(newSelection<choices.size())
                        newSelection++;
                    else if(loop)
                        newSelection = 1;
                    break;
                case SELECT:
                    success.accept(message, selection);
                    break;
                case CANCEL:
                    cancel.accept(message);
                    return;

            }
            try {
                event.getReaction().removeReaction(event.getUser()).queue();
            } catch (PermissionException ignored) {}
            int n = newSelection;
            message.editMessage(render(n)).queue(m -> selectionDialog(m, n));
        }, timeout, unit, () -> cancel.accept(message));
    }
    
    private Message render(int selection) {
        StringBuilder sbuilder = new StringBuilder();
        for(int i=0; i<choices.size(); i++)
            if(i+1==selection)
                sbuilder.append("\n").append(leftEnd).append(choices.get(i)).append(rightEnd);
            else
                sbuilder.append("\n").append(defaultLeft).append(choices.get(i)).append(defaultRight);
        MessageBuilder mbuilder = new MessageBuilder();
        String content = text.apply(selection);
        if(content!=null)
            mbuilder.append(content);
        return mbuilder.setEmbed(new EmbedBuilder()
                .setColor(color.apply(selection))
                .setDescription(sbuilder.toString())
                .build()).build();
    }

    public static class Builder extends Menu.Builder<Builder, SelectionDialog> {
        private final List<String> choices = new LinkedList<>();
        private String leftEnd = "";
        private String rightEnd  = "";
        private String defaultLeft = "";
        private String defaultRight = "";
        private Function<Integer,Color> color = i -> null;
        private boolean loop = true;
        private Function<Integer,String> text = i -> null;
        private BiConsumer<Message, Integer> selection;
        private Consumer<Message> cancel = (m) -> {};

        @Override
        public SelectionDialog build() {
            Checks.check(waiter != null, "Must set an EventWaiter");
            Checks.check(!choices.isEmpty(), "Must have at least one choice");
            Checks.check(selection != null, "Must provide a selection consumer");

            return new SelectionDialog(waiter,users,roles,timeout,unit,choices,leftEnd,rightEnd,
                    defaultLeft,defaultRight,color,loop, selection, cancel,text);
        }

        public Builder setColor(Color color) {
            this.color = i -> color;
            return this;
        }

        public Builder setColor(Function<Integer,Color> color) {
            this.color = color;
            return this;
        }

        public Builder setText(String text) {
            this.text = i -> text;
            return this;
        }

        public Builder setText(Function<Integer,String> text) {
            this.text = text;
            return this;
        }

        public Builder setSelectedEnds(String left, String right) {
            this.leftEnd = left;
            this.rightEnd = right;
            return this;
        }

        public Builder setDefaultEnds(String left, String right) {
            this.defaultLeft = left;
            this.defaultRight = right;
            return this;
        }

        public Builder useLooping(boolean loop) {
            this.loop = loop;
            return this;
        }

        public Builder setSelectionConsumer(BiConsumer<Message, Integer> selection) {
            this.selection = selection;
            return this;
        }

        public Builder setCanceled(Consumer<Message> cancel) {
            this.cancel = cancel;
            return this;
        }

        public Builder clearChoices() {
            this.choices.clear();
            return this;
        }

        public Builder setChoices(String... choices) {
            this.choices.clear();
            this.choices.addAll(Arrays.asList(choices));
            return this;
        }

        public Builder addChoices(String... choices) {
            this.choices.addAll(Arrays.asList(choices));
            return this;
        }
    }
}
