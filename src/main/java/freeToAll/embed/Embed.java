// Author: Tancred423 (https://github.com/Tancred423)
package freeToAll.embed;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.time.OffsetDateTime;
import java.util.List;

public class Embed {
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
    private MessageChannel messageChannel;

    void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }
    void setAuthorIconUrl(String authorIconUrl) {
        this.authorIconUrl = authorIconUrl;
    }
    void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
    void setDescription(String description) {
        this.description = description;
    }
    public void setFields(List<MessageEmbed.Field> fields) {
        this.fields = fields;
    }
    void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    void setFooterText(String footerText) {
        this.footerText = footerText;
    }
    void setFooterIconUrl(String footerIconUrl) {
        this.footerIconUrl = footerIconUrl;
    }
    void setMessageChannel(MessageChannel messageChannel) {
        this.messageChannel = messageChannel;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }
    void setTitleUrl(String titleUrl) {
        this.titleUrl = titleUrl;
    }

    String getAuthorName() {
        return authorName;
    }
    String getAuthorUrl() {
        return authorUrl;
    }
    String getAuthorIconUrl() {
        return authorIconUrl;
    }
    String getThumbnailUrl() {
        return thumbnailUrl;
    }
    String getDescription() {
        return description;
    }
    public List<MessageEmbed.Field> getFields() {
        return fields;
    }
    String getImageUrl() {
        return imageUrl;
    }
    String getFooterText() {
        return footerText;
    }
    String getFooterIconUrl() {
        return footerIconUrl;
    }
    MessageChannel getMessageChannel() {
        return messageChannel;
    }
    public String getTitle() {
        return title;
    }
    OffsetDateTime getTimestamp() {
        return timestamp;
    }
    String getTitleUrl() {
        return titleUrl;
    }
}
