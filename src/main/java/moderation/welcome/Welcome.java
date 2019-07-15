package moderation.welcome;

import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.Role;

import java.time.OffsetDateTime;
import java.util.List;

public class Welcome {
    private String authorName;
    private String authorUrl;
    private String authorIconUrl;
    private String thumbnailUrl;
    private String title;
    private String titleUrl;
    private String description;
    private List<MessageEmbed.Field> fields;
    private String imageUrl;
    private String footerText;
    private String footerIconUrl;
    private OffsetDateTime timestamp;

    private String reactEmoji;
    private Emote reactEmote;

    private Role role;
    private MessageChannel messageChannel;
    private boolean emoteOrEmojiWasSet;
    private int amountFields;

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }
    public void setAuthorIconUrl(String authorIconUrl) {
        this.authorIconUrl = authorIconUrl;
    }
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setFields(List<MessageEmbed.Field> fields) {
        this.fields = fields;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setFooterText(String footerText) {
        this.footerText = footerText;
    }
    public void setFooterIconUrl(String footerIconUrl) {
        this.footerIconUrl = footerIconUrl;
    }
    public void setReactEmoji(String reactEmoji) {
        this.reactEmoji = reactEmoji;
    }
    public void setReactEmote(Emote reactEmote) {
        this.reactEmote = reactEmote;
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public void setMessageChannel(MessageChannel messageChannel) {
        this.messageChannel = messageChannel;
    }
    public void setEmoteOrEmojiWasSet(boolean emoteOrEmojiWasSet) {
        this.emoteOrEmojiWasSet = emoteOrEmojiWasSet;
    }
    public void setAmountFields(int amountFields) {
        this.amountFields = amountFields;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public void setTitleUrl(String titleUrl) {
        this.titleUrl = titleUrl;
    }

    public String getAuthorName() {
        return authorName;
    }
    public String getAuthorUrl() {
        return authorUrl;
    }
    public String getAuthorIconUrl() {
        return authorIconUrl;
    }
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
    public String getDescription() {
        return description;
    }
    public List<MessageEmbed.Field> getFields() {
        return fields;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getFooterText() {
        return footerText;
    }
    public String getFooterIconUrl() {
        return footerIconUrl;
    }
    public String getReactEmoji() {
        return reactEmoji;
    }
    public Emote getReactEmote() {
        return reactEmote;
    }
    public Role getRole() {
        return role;
    }
    public MessageChannel getMessageChannel() {
        return messageChannel;
    }
    public int getAmountFields() {
        return amountFields;
    }
    public boolean emoteOrEmojiWasSet() {
        return emoteOrEmojiWasSet;
    }
    public String getTitle() {
        return title;
    }
    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
    public String getTitleUrl() {
        return titleUrl;
    }
}
