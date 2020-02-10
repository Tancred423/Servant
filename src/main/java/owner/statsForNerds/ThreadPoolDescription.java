// Author: Tancred423 (https://github.com/Tancred423)
package owner.statsForNerds;

public class ThreadPoolDescription {
    private boolean isRunning;
    private int poolSize;
    private int activeThreads;
    private int queuedTasks;
    private int completedTasks;

    public ThreadPoolDescription(String threadPoolString) {
        setAttributes(threadPoolString);
    }

    public boolean isRunning() { return isRunning; }
    public int getPoolSize() { return poolSize; }
    public int getActiveThreads() { return activeThreads; }
    public int getQueuedTasks() { return queuedTasks; }
    public int getCompletedTasks() { return completedTasks; }

    private void setAttributes(String threadPoolString) {
        // java.util.concurrent.ThreadPoolExecutor@7a76c491[Running, pool size = 2, active threads = 2, queued tasks = 0, completed tasks = 0]

        var split = threadPoolString.split("\\[");
        if (split.length > 1) {
            var data = split[1]; // Running, pool size = 2, active threads = 2, queued tasks = 0, completed tasks = 0]
            data = data.substring(0, data.length() - 1); // Running, pool size = 2, active threads = 2, queued tasks = 0, completed tasks = 0
            var dataSplit = data.split(", "); // [Running,pool size = 2,active threads = 2,queued tasks = 0,completed tasks = 0]

            this.isRunning = dataSplit[0].equalsIgnoreCase("running");
            this.poolSize = Integer.parseInt(dataSplit[1].split("=")[1].trim());
            this.activeThreads = Integer.parseInt(dataSplit[2].split("=")[1].trim());
            this.queuedTasks = Integer.parseInt(dataSplit[3].split("=")[1].trim());
            this.completedTasks = Integer.parseInt(dataSplit[4].split("=")[1].trim());
        } else {
            this.isRunning = false;
            this.poolSize = 0;
            this.activeThreads = 0;
            this.queuedTasks = 0;
            this.completedTasks = 0;
        }
    }
}
