package commands.utility.giveaway;

public class GiveawayEndTask implements Runnable {
    private final Giveaway giveaway;

    public GiveawayEndTask(Giveaway giveaway) {
        this.giveaway = giveaway;
    }

    @Override
    public void run() {
        giveaway.end();
    }
}
