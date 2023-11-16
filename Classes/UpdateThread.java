package Classes;

public class UpdateThread implements Runnable {

    // is it more convenient to use a public static Pump in main, or pass it through as a parameter?
    private final Pump pump;
    public UpdateThread(Pump pump) {
        this.pump = pump;
    }

    @Override
    public void run() {
        // while the pump is active, print time, active insulin amount, and repeat every 1 minute
        while (pump.active) {
            System.out.println("------------------------------");
            System.out.println(pump.getTime());
            System.out.println("ACTIVE INSULIN:\t" + pump.getActiveInsulin());
            System.out.println("OPTIONS:\t\t1.Bolus 2.Menu");

            try {
                Thread.sleep(60000);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}