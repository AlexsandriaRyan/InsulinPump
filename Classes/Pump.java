package Classes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.text.SimpleDateFormat;

public class Pump {
    protected static BolusSettings bolusSettings;
    protected static BasalSettings basalSettings;
    private static Menu menus = null;
    private Date time;
    public static boolean active = true;
    public static boolean update = true;
    private boolean warning10 = false;
    private boolean warning20 = false;
    private boolean warning0 = false;
    private double reservoir;
    private ArrayList<Double> activeInsulin = new ArrayList<>();
    private static final int BASAL_PER_HOUR = 60;
    private static final int ACTIVE_INSULIN_PER_HOUR = 60;
    private final SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMMM, yyyy hh:mm a");
    private final String warning0String = "WARNING: Reservoir has 0 units of insulin remaining. Please change the reservoir immediately.";
    public final static String configFilePath = "Configs/configs.txt";

    // ***** CONSTRUCTOR *********************************************
    public Pump() {
        System.out.println("***NEW INSULIN PUMP***");
        setTime();
        System.out.println(sdf.format(time));
        bolusSettings = new BolusSettings();
        basalSettings = new BasalSettings();
        writeConfigs();
        newReservoir();
        menus = new Menu(this);
    }

    public Pump(HashMap<String, ArrayList<String>> configs) {
        System.out.println("***WELCOME BACK***");
        setTime();
        bolusSettings = new BolusSettings(configs);
        basalSettings = new BasalSettings(configs);
        newReservoir();
        menus = new Menu(this);
    }

    // ***** GETTERS *********************************************
    public String getTime() {
        setTime();
        return sdf.format(time);
    }

    public int getHour() {
        // gets & returns a 24h format of the current time's hour
        String hourStr = new SimpleDateFormat("HH").format(time);
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

    public BasalSettings getBasalSettings() {
        return basalSettings;
    }

    public BolusSettings getBolusSettings() { return bolusSettings; }

    // ***** SETTERS *********************************************
    private void setTime() {
        time = new Date();
    }

    public void setActive() {
        Scanner scan = new Scanner(System.in);

        if (active) {
            System.out.println("Suspend pump activity?");
            System.out.println("1. Yes");
            System.out.println("2. No");

            char input = scan.next().charAt(0);
            if (input == '1') {
                active = false;
                System.out.println("Pump activity has been suspended.");
            }

        } else {
            System.out.println("Resume pump activity?");
            System.out.println("1. Yes");
            System.out.println("2. No");

            char input = scan.next().charAt(0);
            if (input == '1') {
                active = true;
                System.out.println("Pump activity has resumed.");
            }
        }
    }

    public void setReservoir(double insulinUsed, String command) {
        // set warning strings to avoid repetition
        String warning10String = "WARNING: Reservoir has < 10 units of insulin remaining. Please change reservoir soon.";
        String warning20String = "WARNING: Reservoir has < 20 units of insulin remaining. Please change reservoir soon.";

        // if reservoir has reached 0, simply return
        if (reservoir <= 0 && !warning0) {
            System.out.println(warning0String);
            warning0 = true;
            reservoir = 0; // ensure reservoir is simply '0'

            return;

        // if the reservoir is 10 units or under, display a warning.
        } else if (reservoir <= 10 && !warning10) {
            System.out.println(warning10String);
            warning10 = true;

        // if the reservoir is 20 units or under, display a warning.
        } else if (reservoir <= 20 && !warning20) {
            System.out.println(warning20String);
            warning20 = true;
        }

        // test to see if there is enough insulin in reservoir to complete action.
        double tempReservoir = reservoir - insulinUsed;

        // if tempReservoir reaches 0 units, display warning
        if (tempReservoir <= 0) {
            System.out.printf("WARNING: Could not complete requested bolus amount. Bolus delivered: %.2f of %.2f units\n", reservoir, insulinUsed);
            System.out.println(warning0String);
            warning0 = true;
            reservoir = 0; // ensure reservoir is simply '0'

        // if tempReservoir still has units remaining, update the reservoir amount
        } else {
            reservoir = tempReservoir;

            // print out confirmation of delivery if the setReservoir() request came from a bolus
            if (command.equals("bolus")) {
                System.out.printf("%.2f delivered.\n", insulinUsed);
            }
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
        // if the reservoir has no insulin left, do not bolus
        if (reservoir <= 0) {
            System.out.println(warning0String);

            return;
        }

        System.out.println("***BOLUS***");
        Scanner scan = new Scanner(System.in);

        // capture blood glucose & calculate corrective insulin
        System.out.print("BG: ");
        double bg = scan.nextDouble();
        double correction = correct(bg);

        // capture carbohydrates
        System.out.print("Carbs: ");
        double carbs = scan.nextDouble();

        // capture manual bolus
        System.out.print("Manual Bolus: ");
        double manual = scan.nextDouble();

        // add correction, carbs, and manual bolus to calculate total bolus amount
        double bolus = correction + carbs + manual;

        System.out.printf("Total: %.2f units\n", bolus);
        System.out.println("OK?");
        System.out.println("1. Yes");
        System.out.println("2. No");

        // if user accepts bolus, set the reservoir to the new amount, calculate active insulin, and print confirmation of bolus
        char input = scan.next().charAt(0);
        if (input == '1') {
            setReservoir(bolus, "bolus");
            setActiveInsulin(bolus);
        }
    }

    public void basal() {
        // reduce reservoir based on amount specified by the current basal settings
        setReservoir(basalSettings.getBasalPattern(getHour()) / BASAL_PER_HOUR, "basal");
    }

    private void reduceActiveInsulin() {
        if (!activeInsulin.isEmpty()) {
            activeInsulin.remove(0);
        }
    }

    public void newReservoir() {
        System.out.println("How much insulin is in the new reservoir?");

        Scanner scan = new Scanner(System.in);
        reservoir = scan.nextInt();

        // since there are no mechanics available to measure properly,
        // force the insulin amount to be between 20-300 units
        if (reservoir > 300) {
            reservoir = 300;

        } else if (reservoir < 20) {
            reservoir = 20;
        }

        System.out.println("Beep, boop, pretend I am retracting my piston...");
        System.out.println("Beep, boop, pretend I am measuring the new reservoir...");
        System.out.println("Beep, boop, new reservoir amount is " + reservoir + "...");
        System.out.println("Beep, boop, pretend I am priming the tubing, which uses 10 units...");

        // calculates insulin needed to prime tubing; this differs by product,
        // but is typically 10 units for me, personally
        reservoir -= 10;

        System.out.println("Beep, boop, pretend I am priming a 6mm cannula...");

        // calculates insulin needed for a 6mm cannula; 0.5 units per mm
        double cannula = 6 * 0.5;
        reservoir -= cannula;

        System.out.println("Beep, boop, new reservoir locked & loaded!");
    }

    private double correct(double bg) {
        // calculate insulin needed to meet glucose targets
        if (bg < bolusSettings.getLowTarget()) {
            // calculate less insulin
            return -1; // TEMP

        } else if (bg > bolusSettings.getHighTarget()) {
            // calculate more insulin
            return 1; // TEMP

        } else {
            // no adjustment needed
            return 0;
        }
    }

    public void checkForMenu() {
        // check for input (bolus or menu)
        char input = '0';
        while (input == '0') {
            Scanner scan = new Scanner(System.in);
            input = scan.next().charAt(0);

            if (input == '1' || input == '2') {
                // stop the "update" thread to prevent it appearing over the user input
                Pump.update = false;

                if (input == '1') {
                    // if the pump is not suspended, allow bolus.
                    if(active) {
                        bolus();

                    } else {
                        System.out.println("Pump is suspended. Please resume pump activity to enable bolus functions.");
                    }

                } else {
                    // TO DO HERE:
                    // Implement a timeout feature (bookmarked on chrome)
                    menus.mainMenu();
                }
            }
            // once navigation is complete, resume the updates
            update = true;
        }
    }

    //https://stackoverflow.com/questions/26785315/how-to-overwrite-an-existing-txt-file
    public static void writeConfigs() {
        if (bolusSettings != null && basalSettings != null) {
            try {
                File file = new File(configFilePath);
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);

                // config.txt's order is:
                // CARB_RATIO
                // INSULIN_SENSITIVITY
                // INSULIN_LONGEVITY
                // TARGET_GLUCOSE
                // BASAL_PATTERN (1, 2, 3...)

                // write carb ratios to file
                bw.write("CARB_RATIO\n");
                ArrayList<Double> writeCarbs = bolusSettings.getCarbRatio();
                for (int i = 0; i < writeCarbs.size(); i++) {
                    bw.write(String.valueOf(writeCarbs.get(i)) + "\n");
                }
                bw.write("\n");

                // write insulin sensitivities to file
                bw.write("INSULIN_SENSITIVITY\n");
                ArrayList<Double> writeSensitivity = bolusSettings.getInsulinSensitivity();
                for (int i = 0; i < writeSensitivity.size(); i++) {
                    bw.write(writeSensitivity.get(i) + "\n");
                }
                bw.write("\n");

                // write insulin longevity to file
                bw.write("INSULIN_LONGEVITY\n");
                int writeLongevity = bolusSettings.getInsulinLongevity();
                bw.write(String.valueOf(writeLongevity) + "\n\n");

                // write targets to file
                bw.write("TARGET_GLUCOSE\n");
                double[] writeTarget = bolusSettings.getTargetGlucose();
                for (double target : writeTarget) {
                    bw.write(target + "\n");
                }
                bw.write("\n");

                // write basal patterns to file
                // get current basal pattern #
                int current = basalSettings.getCurrentBasalPattern();
                for (int i = 0; i < basalSettings.getBasalPatternsSize(); i++) {
                    bw.write("BASAL_PATTERN " + (i+1));

                    // if this pattern is the current pattern, add an '*'
                    if (i == current) {
                        bw.write(" *\n");

                    } else {
                        bw.write("\n");
                    }

                    // begin writing the pattern data
                    ArrayList<Double> writeBasal = basalSettings.getBasalPatternIndex(i);
                    for(double d : writeBasal) {
                        bw.write(d + "\n");
                    }

                    bw.write("\n");
                }

                bw.close();

            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
