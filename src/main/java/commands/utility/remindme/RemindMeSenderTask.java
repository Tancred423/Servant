package commands.utility.remindme;

public class RemindMeSenderTask implements Runnable {
    private final RemindMe remindMe;

    public RemindMeSenderTask(RemindMe remindMe) {
        this.remindMe = remindMe;
    }

    @Override
    public void run() {
        remindMe.end();
    }
}
