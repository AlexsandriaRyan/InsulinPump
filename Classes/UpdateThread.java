package Classes;

public class UpdateThread implements Runnable {

    private final Pump pump;
    public UpdateThread(Pump pump) { this.pump = pump; }
    private static final int ONE_MINUTE = 60000;
    private static final int TEN_SECONDS = 10000;

    @Override
    public void run() {
        // while the pump permits updates, print time, active insulin amount, and repeat every 1 minute
        while (Pump.update) {
            System.out.println("\n\n------------------------------");
            System.out.println(pump.getTime());
            System.out.println("ACTIVE INSULIN:\t" + pump.getActiveInsulin());
            System.out.printf("RESERVOIR:\t\t%.3f\n", pump.getReservoir());
            System.out.println("OPTIONS:\t\t1.Bolus 2.Menu");
            pump.checkForMenu();

            try {
                Thread.sleep(TEN_SECONDS);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}