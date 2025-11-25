package codes.tino.hashhive;

public class CountdownManager {
    private static CountdownManager instance;
    private int ticksRemaining = 0;
    private boolean isActive = false;

    // 10 minutes = 600 seconds = 12000 ticks (20 ticks per second)
    private static final int COUNTDOWN_DURATION = 12000;

    private CountdownManager() {}

    public static CountdownManager getInstance() {
        if (instance == null) {
            instance = new CountdownManager();
        }
        return instance;
    }

    /**
     * Starts a 10-minute countdown
     */
    public void startCountdown() {
        this.ticksRemaining = COUNTDOWN_DURATION;
        this.isActive = true;
        HashHiveLogger.LOGGER.info("Started 10-minute countdown");
    }

    /**
     * Updates the countdown, should be called every client tick
     */
    public void tick() {
        if (isActive && ticksRemaining > 0) {
            ticksRemaining--;

            if (ticksRemaining <= 0) {
                isActive = false;
            }
        }
    }

    /**
     * Returns whether the countdown is currently active
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Returns the remaining time as a formatted string (MM:SS)
     */
    public String getFormattedTime() {
        if (!isActive) {
            return "";
        }

        int totalSeconds = ticksRemaining / 20;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Stops the countdown
     */
    public void stop() {
        this.isActive = false;
        this.ticksRemaining = 0;
    }
}
