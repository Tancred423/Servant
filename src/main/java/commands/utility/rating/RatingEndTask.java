package commands.utility.rating;

public class RatingEndTask implements Runnable {
    private final Rating rating;

    public RatingEndTask(Rating rating) {
        this.rating = rating;
    }

    @Override
    public void run() {
        rating.end();
    }
}
