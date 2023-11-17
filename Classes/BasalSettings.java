package Classes;

import java.text.SimpleDateFormat;
import java.util.*;

public class BasalSettings {
    ArrayList<ArrayList<Double>> basalPatterns = new ArrayList<>();
    int currentBasalPattern;
    //SimpleDateFormat sdf = new SimpleDateFormat("hh a");

    // ***** CONSTRUCTOR *********************************************
    protected BasalSettings() {
        setBasalPattern();
    }

    BasalSettings(HashMap<String, ArrayList<String>> configs) {
        for (HashMap.Entry<String, ArrayList<String>> entry : configs.entrySet()) {
            if (entry.getKey().startsWith("BASAL_PATTERN")) {
                setBasalPattern(entry.getKey(), entry.getValue());
            }
        }
    }

    // ***** GETTERS *********************************************
    double getBasalPattern(int hour) {
        return basalPatterns.get(currentBasalPattern).get(hour);
    }

    // ***** SETTERS *********************************************
    private void setBasalPattern() {
        // show available basal patterns (1, 2, 3... etc.)
        // view a basal pattern
        // delete a current basal pattern
        // new basal pattern
        // needs a way to change between patterns...?

        System.out.println("\n***NEW BASAL PATTERN***");
        System.out.println("Enter units of insulin per hour:");
        
        ArrayList<Double> temp = new ArrayList<>();

        Scanner scan = new Scanner(System.in);
        for (int i = 0; i < 24; i++) {
            System.out.printf("%02d:00: ", i);
            double basal = scan.nextDouble();
            temp.add(basal);
        }
        
        basalPatterns.add(temp);

        // write to configs
    }

    private void setBasalPattern(String pattern, ArrayList<String> configs) {
        // set basal patterns from configs
        ArrayList<Double> temp = new ArrayList<>();

        for(String str : configs) {
            double basal = Double.parseDouble(str);
            temp.add(basal);
        }

        basalPatterns.add(temp);

        // check if this pattern should be the current basal pattern
        if (pattern.endsWith("*")) {
            currentBasalPattern = basalPatterns.size()-1;
        }

        // write to configs
    }
}