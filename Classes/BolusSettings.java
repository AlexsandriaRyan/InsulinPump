package Classes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class BolusSettings {
    private final ArrayList<Double> carbRatio = new ArrayList<>();
    private final ArrayList<Double> insulinSensitivity = new ArrayList<>();
    private int insulinLongevity;
    private double[] targetGlucose;
    //SimpleDateFormat sdf = new SimpleDateFormat("hh a");

    // ***** CONSTRUCTOR *********************************************
    protected BolusSettings() {
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
    double getCarbRatio(int hour) {
        return carbRatio.get(hour-1);
    }

    double getInsulinSensitivity(int hour) {
        return insulinSensitivity.get(hour);
    }

    int getInsulinLongevity() {
        return insulinLongevity;
    }

    double[] getTargetGlucose() {
        return targetGlucose;
    }

    // ***** SETTERS ********************
    private void setCarbRatio() {
        System.out.println("\n***CARB RATIO***");
        System.out.println("Enter the grams of carbs per unit of insulin:");

        Scanner scan = new Scanner(System.in);
        for (int i = 0; i < 24; i++) {
            System.out.format("%02d:00: ", i);
            double temp = scan.nextDouble();
            carbRatio.add(temp);
        }

        // write to configs
    }

    private void setCarbRatio(ArrayList<String> configs) {
        // set up of carb ratio schedule from configs
        for(String str : configs) {
            double ratio = Double.parseDouble(str);
            carbRatio.add(ratio);
        }

        // write to configs
    }

    private void setInsulinSensitivity() {
        System.out.println("\n***INSULIN SENSITIVITY***");
        System.out.println("Enter the insulin sensitivity: ");

        Scanner scan = new Scanner(System.in);
        for (int i = 0; i < 24; i++) {
            System.out.format("%02d:00: ", i);
            double temp = scan.nextDouble();
            insulinSensitivity.add(temp);
        }

        // write to configs
    }

    private void setInsulinSensitivity(ArrayList<String> configs) {
        // set up insulin sensitivity schedule from configs
        for(String str : configs) {
            double ratio = Double.parseDouble(str);
            insulinSensitivity.add(ratio);
        }

        // write to configs
    }

    private void setInsulinLongevity() {
        System.out.println("\n***INSULIN LONGEVITY***");
        System.out.print("Enter the insulin longevity: ");

        Scanner scan = new Scanner(System.in);
        insulinLongevity = scan.nextInt();

        // write to configs
    }

    private void setInsulinLongevity(ArrayList<String> configs) {
        // set up insulin longevity from configs
        for(String str : configs) {
            insulinLongevity = Integer.parseInt(str);
        }

        // write to configs
    }

    private void setTargetGlucose() {
        System.out.println("\n***TARGET GLUCOSE***");
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

        // write to configs
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

        targetGlucose = new double[]{low, high};

        // write to configs
    }
}