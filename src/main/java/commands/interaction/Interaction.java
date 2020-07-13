// Author: Tancred423 (https://github.com/Tancred423)
package commands.interaction;

public class Interaction {
    private String name;
    private int shared;
    private int received;

    public Interaction(String name, int shared, int received) {
        this.name = name;
        this.shared = shared;
        this.received = received;
    }

    public void setName(String name) { this.name = name; }
    public void setShared(int shared) { this.shared = shared; }
    public void setReceived(int received) { this.received = received; }

    public String getName() { return name; }
    public int getShared() { return shared; }
    public int getReceived() { return received; }
}
