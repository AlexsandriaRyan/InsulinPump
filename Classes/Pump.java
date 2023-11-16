package Classes;

import java.util.*;
import java.text.SimpleDateFormat;

public class Pump {
    private BolusSettings bolusSettings;
    private BasalSettings basalSettings;
    private Date time;
    public boolean active;
    private boolean warning10;
    private boolean warning20;
    private double reservoir;
    private ArrayList<Double> activeInsulin;
    static final int BASAL_PER_HOUR = 60;
    static final int ACTIVE_INSULIN_PER_HOUR = 12;
    private SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMMM, yyyy hh:mm a");

    // ***** CONSTRUCTOR ********************
    public Pump() {
        System.out.println("***NEW INSULIN PUMP***");
        setTime();
        newReservoir();
        System.out.println(sdf.format(time));
        this.active = true;
        this.warning10 = false;
        this.warning20 = false;
        this.activeInsulin = new ArrayList<>();
        this.bolusSettings = new BolusSettings();
        this.basalSettings = new BasalSettings();
    }

    public Pump(HashMap<String, ArrayList<String>> configs) {
        System.out.println("***WELCOME BACK***");
        setTime();
        newReservoir();
        System.out.println(time);
        this.bolusSettings = new BolusSettings(configs);
        this.basalSettings = new BasalSettings(configs);
        this.active = true;
        this.warning10 = false;
        this.warning20 = false;
        this.activeInsulin = new ArrayList<>();
    }

    // ***** GETTERS ********************
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

    // ***** SETTERS ********************
    private void setTime() {
        time = new Date();
    }

    public void setActive() { active = active ? false : true; }

    public void setReservoir(double insulinUsed) {
        reservoir = reservoir - insulinUsed;

        if (reservoir <= 10 && !warning10) {
            System.out.println("WARNING: Reservoir has 10 units of remaining. Please change reservoir soon.");
            warning10 = true;

        } else if (reservoir <= 20 && !warning20) {
            System.out.println("WARNING: Reservoir has 20 units of remaining. Please change reservoir soon.");
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

    private void reduceActiveInsulin() {
        if (!activeInsulin.isEmpty()) {
            activeInsulin.remove(0);
        }
    }

    // ***** FUNCTIONS ********************
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

    private void timer() {
        // make a function that runs every x minutes.
        // this should...
        // * administer basal insulin
        // * reduce active insulin
    }

    public void bolus() {
        System.out.println("BOLUS FUNCTION");
        double bolus = 10;

        // make a function that administers a bolus
        // bolusing will push the piston up, and remeasure the reservoir amount
        // update active insulin
        setReservoir(bolus);
        setActiveInsulin(bolus);
    }

    public void basal() {
        // reduce reservoir based on amount specified by the current basal settings
        setReservoir(basalSettings.getBasalPattern(getHour()) / BASAL_PER_HOUR);
    }

    public void newReservoir() {
        System.out.println("NEW RESERVOIR FUNCTION");

        // hardcoded to be a full reservoir (3mL / 300 units)
        reservoir = 300;

        // make a function that retracts the piston and allows a new vial to be inserted
        // when a new vial is put in, push the piston up to meet where the new vial is filled to
        // measure the reservoir amount
        // prime the tubing
        // prime the cannula
        // restart the timer
    }
}
