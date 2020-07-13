package commands.utility.polls.quickpoll;

import commands.utility.polls.Poll;

public class QuickpollEndTask implements Runnable {
    private final Poll quickpoll;

    public QuickpollEndTask(Poll quickpoll) {
        this.quickpoll = quickpoll;
    }

    @Override
    public void run() {
        quickpoll.endQuickPoll();
    }
}
