package Classes;

public class BasalThread implements Runnable {

    // is it more convenient to use a public static Pump in main, or pass it through as a parameter?
    private final Pump pump;
    public BasalThread (Pump pump) {
        this.pump = pump;
    }

    @Override
    public void run() {
        // while the pump is active, run the basal() function and repeat every 1 minute
        while (pump.active) {
            pump.basal();

            try {
                Thread.sleep(Pump.BASAL_PER_HOUR * 1000);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
