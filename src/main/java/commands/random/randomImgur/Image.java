// Author: Tancred423 (https://github.com/Tancred423)
package commands.random.randomImgur;

public class Image {
    private final String title;
    private final String link;
    private final String directLink;

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
