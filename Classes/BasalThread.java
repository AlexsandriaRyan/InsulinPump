package Classes;

public class BasalThread implements Runnable {

    private final Pump pump;
    private static final int ONE_MINUTE = 60000;

    public BasalThread (Pump pump) {
        this.pump = pump;
    }

    @Override
    public void run() {
        // while the pump is active, run the basal() function
        // and repeat every 1 minute
        while (Pump.active && pump.getReservoir() > 0) {
            pump.basal();

            try {
                Thread.sleep(ONE_MINUTE);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
