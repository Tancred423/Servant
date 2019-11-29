// Author: Tancred423 (https://github.com/Tancred423)
package fun.random;

public class Image {
    private String title;
    private String link;
    private String directLink;

    public Image(String title, String link, String directLink) {
        this.title = title;
        this.link = link;
        this.directLink = directLink;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDirectLink() {
        return directLink;
    }
}
