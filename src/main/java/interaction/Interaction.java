// Author: Tancred423 (https://github.com/Tancred423)
package interaction;

public class Interaction {
    private String name;
    private int shared;
    private int received;

    public Interaction(String name, int shared, int received) {
        this.name = name;
        this.shared = shared;
        this.received = received;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getShared() {
        return shared;
    }

    public void setShared(int shared) {
        this.shared = shared;
    }

    public int getReceived() {
        return received;
    }

    public void setReceived(int received) {
        this.received = received;
    }
}
