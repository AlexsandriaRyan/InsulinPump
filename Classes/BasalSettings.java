package Classes;

import java.text.SimpleDateFormat;
import java.util.*;

public class BasalSettings {
    ArrayList<ArrayList<Double>> basalPatterns = new ArrayList<>();
    Integer currentBasalPattern;
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

    public int getBasalPatternsSize() {
        return basalPatterns.size();
    }

    public ArrayList<Double> getBasalPatternIndex(int index) {
        return basalPatterns.get(index);
    }

    public int getCurrentBasalPattern() {
        return currentBasalPattern;
    }

    // ***** SETTERS *********************************************
    protected void setBasalPattern() {
        System.out.println("\n\n***NEW BASAL PATTERN***");
        System.out.println("Enter units of insulin per hour:");
        
        ArrayList<Double> temp = new ArrayList<>();

        Scanner scan = new Scanner(System.in);
        for (int i = 0; i < 24; i++) {
            System.out.printf("%02d:00: ", i);
            double basal = scan.nextDouble();
            temp.add(basal);
        }
        
        basalPatterns.add(temp);

        System.out.println("Basal Pattern saved!");

        // if the current basal pattern is not set,
        // ensure that it is set to this new pattern
        // otherwise, ask the user if they would like to update
        // their current basal pattern to this new pattern
        if (currentBasalPattern == null) {
            currentBasalPattern = basalPatterns.size()-1;

        } else {
            System.out.println("Save this pattern as the current basal pattern?");
            System.out.println("1. Yes");
            System.out.println("2. No");

            char input = scan.next().charAt(0);

            if (input == '1') {
                currentBasalPattern = basalPatterns.size()-1;
                System.out.print("Current Basal Pattern updated!");
            }
        }

        // update config file
        Pump.writeConfigs();
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

        // update config file
        Pump.writeConfigs();
    }

    // ***** FUNCTIONS *********************************************
    protected void deleteBasalPattern() {
        System.out.println("Available Basal Patterns to delete:");

        for(int i = 0; i < basalPatterns.size(); i++) {
            double dailyInsulin = 0;

            for (double index : basalPatterns.get(i)) {
                dailyInsulin += index;
            }

            System.out.println("Basal Pattern #" + (i+1) + " - Daily Insulin of " + dailyInsulin);
        }

        Scanner scan = new Scanner(System.in);
        int input = scan.nextInt();

        if (basalPatterns.get(input-1) != null) {
            System.out.println("Delete Basal Pattern #" + input + "?");
            System.out.println("1. Yes");
            System.out.println("2. No");

            input = scan.nextInt();

            if (input == 1) {
                basalPatterns.remove(input-1);
                System.out.println("Basal Pattern #" + input + " has been deleted.");

            } else {
                System.out.println("Exiting Menu...");
            }

        } else {
            System.out.println("Basal Pattern #" + input + " does not exist.");
            System.out.println("Exiting Menu...");
        }
    }
}