package Classes;
public class UpdateThread implements Runnable {

    private final Pump pump;
    private static final int ONE_MINUTE = 60000;
    private static final int TEN_SECONDS = 10000;

    public UpdateThread(Pump pump) { this.pump = pump; }

    @Override
    public void run() {
        // while the pump permits updates, print time, active insulin amount,
        // reservoir amount, and prompt for menu options.
        // Repeat as per variable selected above.
        while (Pump.update) {
            System.out.println("\n\n------------------------------");
            System.out.println(pump.getTime());
            System.out.printf("ACTIVE INSULIN:\t%.2f\n", pump.getActiveInsulin());
            System.out.printf("RESERVOIR:\t\t%.2f\n", pump.getReservoir());
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