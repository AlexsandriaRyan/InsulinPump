package Classes;

public class ActiveInsulinThread implements Runnable {

    private final Pump pump;
    private static final int ONE_HOUR = 3600000;
    private static final int UPDATE = ONE_HOUR / Pump.ACTIVE_INSULIN_PER_HOUR;

    public ActiveInsulinThread (Pump pump) {
        this.pump = pump;
    }

    @Override
    public void run() {
        // while the pump is turned on,
        // run the reduceActiveInsulin() function
        while (Pump.on) {
            pump.reduceActiveInsulin();

            try {
                Thread.sleep(UPDATE);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
