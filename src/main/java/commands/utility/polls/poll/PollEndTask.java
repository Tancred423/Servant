package commands.utility.polls.poll;

import commands.utility.polls.Poll;

public class PollEndTask implements Runnable {
    private final Poll poll;

    public PollEndTask(Poll poll) {
        this.poll = poll;
    }

    @Override
    public void run() {
        poll.endPoll();
    }
}
