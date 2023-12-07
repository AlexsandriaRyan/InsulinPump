package Classes;
import java.text.SimpleDateFormat;
import java.util.*;

public class BolusSettings {
    private final ArrayList<Double> carbRatio = new ArrayList<>();
    private final ArrayList<Double> insulinSensitivity = new ArrayList<>();
    private int insulinLongevity;
    private double[] targetGlucose;

    // ***** CONSTRUCTOR *********************************************
    BolusSettings() {
        setCarbRatio();
        setInsulinSensitivity();
        setInsulinLongevity();
        setTargetGlucose();
    }

    BolusSettings(HashMap<String, ArrayList<String>> configs) {
        setCarbRatio(configs.get("CARB_RATIO"));
        setInsulinSensitivity(configs.get("INSULIN_SENSITIVITY"));
        setInsulinLongevity(configs.get("INSULIN_LONGEVITY"));
        setTargetGlucose(configs.get("TARGET_GLUCOSE"));
    }

    // ***** GETTERS *********************************************
    public double getCarbRatio(int hour) {
        return carbRatio.get(hour);
    }

    public ArrayList<Double> getCarbRatio() {
        return carbRatio;
    }

    public double getInsulinSensitivity(int hour) {
        return insulinSensitivity.get(hour);
    }

    public ArrayList<Double> getInsulinSensitivity() {
        return insulinSensitivity;
    }

    public int getInsulinLongevity() {
        return insulinLongevity;
    }

    public double[] getTargetGlucose() {
        return targetGlucose;
    }

    public double getLowTarget() { return targetGlucose[0]; }

    public double getHighTarget() { return targetGlucose[1]; }

    // ***** SETTERS *********************************************
    protected void setCarbRatio() {
        // reset the carb ratio arraylist
        carbRatio.clear();

        System.out.println("\n\n***CARB RATIO***");
        System.out.println("Enter the grams of carbs per unit of insulin:");

        Scanner scan = new Scanner(System.in);
        for (int i = 0; i < 24; i++) {
            System.out.printf("%02d:00: ", i);
            double temp = scan.nextDouble();
            carbRatio.add(temp);
        }

        System.out.println("Carb Ratio saved!");

        // update config file
        Pump.writeConfigs();
    }

    private void setCarbRatio(ArrayList<String> configs) {
        // set up of carb ratio schedule from configs
        for(String str : configs) {
            double ratio = Double.parseDouble(str);
            carbRatio.add(ratio);
        }

        // update config file
        Pump.writeConfigs();
    }

    protected void setInsulinSensitivity() {
        // reset the insulin sensitivity arraylist
        insulinSensitivity.clear();

        System.out.println("\n\n***INSULIN SENSITIVITY***");
        System.out.println("Enter the insulin sensitivity: ");

        Scanner scan = new Scanner(System.in);
        for (int i = 0; i < 24; i++) {
            System.out.printf("%02d:00: ", i);
            double temp = scan.nextDouble();
            insulinSensitivity.add(temp);
        }

        System.out.println("Insulin Sensitivity saved!");

        // update config file
        Pump.writeConfigs();
    }

    private void setInsulinSensitivity(ArrayList<String> configs) {
        // set up insulin sensitivity schedule from configs
        for(String str : configs) {
            double ratio = Double.parseDouble(str);
            insulinSensitivity.add(ratio);
        }

        // update config file
        Pump.writeConfigs();
    }

    protected void setInsulinLongevity() {
        System.out.println("\n\n***INSULIN LONGEVITY***");
        System.out.print("Enter the insulin longevity: ");

        Scanner scan = new Scanner(System.in);
        insulinLongevity = scan.nextInt();

        System.out.println("Insulin Longevity saved!");

        // update config file
        Pump.writeConfigs();
    }

    private void setInsulinLongevity(ArrayList<String> configs) {
        // set up insulin longevity from configs
        for(String str : configs) {
            insulinLongevity = Integer.parseInt(str);
        }

        // update config file
        Pump.writeConfigs();
    }

    protected void setTargetGlucose() {
        System.out.println("\n\n***TARGET GLUCOSE***");
        Scanner scan = new Scanner(System.in);

        System.out.print("Enter the lowest target: ");
        double low = scan.nextDouble();

        System.out.print("Enter the highest target: ");
        double high = scan.nextDouble();

        // if the "low" variable is actually larger than the "high" variable,
        // then swap the variables' values
        if (low > high) {
            double temp = high;
            high = low;
            low = temp;
        }

        targetGlucose = new double[]{low, high};

        System.out.println("Target Glucose saved!\n");

        // update config file
        Pump.writeConfigs();
    }

    private void setTargetGlucose(ArrayList<String> configs) {
        // set up target glucose range from configs
        double low = 0, high = 0;

        for(String str : configs) {
            if (low == 0) {
                low = Double.parseDouble(str);
            } else {
                high = Double.parseDouble(str);
            }
        }

        // if the "low" variable is actually larger than the "high" variable,
        // then swap the variables' values
        if (low > high) {
            double temp = high;
            high = low;
            low = temp;
        }

        targetGlucose = new double[]{ low, high };

        // update config file
        Pump.writeConfigs();
    }
}