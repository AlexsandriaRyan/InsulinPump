package Classes;

import java.util.*;
import java.text.SimpleDateFormat;

public class Pump {
    private static BolusSettings bolusSettings;
    private static BasalSettings basalSettings;
    private Date time;
    public boolean active;
    private boolean warning10;
    private boolean warning20;
    private double reservoir;
    private ArrayList<Double> activeInsulin;
    // make diff variables
    static final int BASAL_PER_HOUR = 60;
    static final int UPDATE_PER_HOUR = 60;
    static final int ACTIVE_INSULIN_PER_HOUR = 12;
    private SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMMM, yyyy hh:mm a");

    // ***** CONSTRUCTOR *********************************************
    public Pump() {
        System.out.println("***NEW INSULIN PUMP***");
        setTime();
        newReservoir();
        System.out.println(sdf.format(time));
        this.active = true;
        this.warning10 = false;
        this.warning20 = false;
        this.activeInsulin = new ArrayList<>();
        bolusSettings = new BolusSettings();
        basalSettings = new BasalSettings();
    }

    public Pump(HashMap<String, ArrayList<String>> configs) {
        System.out.println("***WELCOME BACK***");
        setTime();
        newReservoir();
        bolusSettings = new BolusSettings(configs);
        basalSettings = new BasalSettings(configs);
        this.active = true;
        this.warning10 = false;
        this.warning20 = false;
        this.activeInsulin = new ArrayList<>();
    }

    // ***** GETTERS *********************************************
    public String getTime() {
        // look at using "clock" instead of date or time?
        setTime();
        return sdf.format(time);
    }

    public int getHour() {
        // gets & returns a 24h format of the current time's hour
        String hourStr = new SimpleDateFormat("kk").format(time);
        return Integer.parseInt(hourStr);
    }

    public double getReservoir() { return reservoir; }

    public double getActiveInsulin() {
        double sum = 0;

        if (!activeInsulin.isEmpty()) {
            for (double i : activeInsulin) {
                sum += i;
            }
        }

        return sum;
    }

    // ***** SETTERS *********************************************
    private void setTime() {
        time = new Date();
    }

    public void setActive() { active = active ? false : true; }

    public void setReservoir(double insulinUsed) {
        reservoir = reservoir - insulinUsed;

        if (reservoir <= 10 && !warning10) {
            System.out.println("WARNING: Reservoir has 10 units of insulin remaining. Please change reservoir soon.");
            warning10 = true;

        } else if (reservoir <= 20 && !warning20) {
            System.out.println("WARNING: Reservoir has 20 units of insulin remaining. Please change reservoir soon.");
            warning20 = true;
        }
    }

    private void setActiveInsulin(double bolus) {
        // divide the bolus amount by the amount of times that the pump will update the active insulin amount, then divide this amount by the insulin longevity
        // Example:
        //      Bolus of 10 units
        //      10 units / 12 checks per hour = 0.83
        //      0.83 / 4 hours = 0.21
        //      The active insulin will reduce by 0.21 every 5 minutes for 4 hours
        double bolusDivided = (bolus / ACTIVE_INSULIN_PER_HOUR) / bolusSettings.getInsulinLongevity();

        // add the divided bolus amount to the activeInsulin ArrayList
        for(int i = 0; i < ACTIVE_INSULIN_PER_HOUR * bolusSettings.getInsulinLongevity(); i++) {
            activeInsulin.add(bolusDivided);
        }
    }

    // ***** FUNCTIONS *********************************************
    private void saveConfigs() {
        // create a function that will save configs based on the pump's
        // bolus / basal settings. This should be run every time that
        // the settings are updated.
    }

    public void printConfigs() {
        System.out.println("PUMP CONFIGS: ");
        System.out.println("CURRENT TIME: " + time);
        System.out.println("CURRENT CARB RATIO: " + bolusSettings.getCarbRatio(getHour()));
        System.out.println("CURRENT INSULIN SENSITIVITY: " + bolusSettings.getInsulinSensitivity(getHour()));
        System.out.println("INSULIN LONGEVITY: " + bolusSettings.getInsulinSensitivity(getHour()));
        System.out.println("TARGET GLUCOSE: " + Arrays.toString(bolusSettings.getTargetGlucose()));
        System.out.println("CURRENT BASAL PATTERN: " + basalSettings.getBasalPattern(getHour()));
    }

    public void bolus() {
        System.out.println("***BOLUS***");

        Scanner scan = new Scanner(System.in);

        System.out.print("BG: ");
        double bg = scan.nextDouble();

        System.out.print("Carbs: ");
        double carbs = scan.nextDouble();

        System.out.print("Manual Bolus: ");
        double manual = scan.nextDouble();

        double bolus = bg + carbs + manual;

        System.out.printf("Total: %.2f", bolus);
        System.out.println("OK?");
        System.out.println("1. Yes");
        System.out.println("2. No");

        int input = scan.nextInt();

        if (input == 1) {
            setReservoir(bolus);
            setActiveInsulin(bolus);
            System.out.printf("%.2f delivered.", bolus);
        }
    }

    public void basal() {
        // reduce reservoir based on amount specified by the current basal settings
        setReservoir(basalSettings.getBasalPattern(getHour()) / BASAL_PER_HOUR);
    }

    private void reduceActiveInsulin() {
        if (!activeInsulin.isEmpty()) {
            activeInsulin.remove(0);
        }
    }

    public void newReservoir() {
        System.out.println("NEW RESERVOIR FUNCTION");

        // hardcoded to be a full reservoir (3mL / 300 units)
        reservoir = 20;

        // make a function that retracts the piston and allows a new vial to be inserted
        // when a new vial is put in, push the piston up to meet where the new vial is filled to
        // measure the reservoir amount
        // prime the tubing
        // prime the cannula
        // restart the timer
    }
}
