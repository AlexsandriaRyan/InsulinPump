// Alexsandria Ryan
// INFT 3000 - Capstone
// NSCC Fall 2023

import Classes.BasalThread;
import Classes.Pump;
import Classes.UpdateThread;

import java.io.*;
import java.util.*;

// TO DO / CONSIDER / Notes
// fix write configs: insulin sensitivity seems to be the same as carb ratio...?
// implement timeout feature for capturing chars
// work on correct() function
// work on reduceActiveInsulin() function
// work on basalMenu() function
// work on using a foreach loop over basal patterns

public class Main {
    final static String configFilePath = "Configs/configs.txt";
    public static Pump pump;

    public static void main(String[] args) {
        // create hashmaps based on config file
        HashMap<String, ArrayList<String>> configs = init();

        if(!configs.isEmpty()) {
            // create pump with configs
            pump = new Pump(configs);
            //pump.printConfigs();

        } else {
            // create pump with new configs
            pump = new Pump();
        }

        // begin basal tasks
        BasalThread basalThread = new BasalThread(pump);
        Thread thread1 = new Thread(basalThread);
        thread1.start();

        // begin update tasks
        UpdateThread updateThread = new UpdateThread(pump);
        Thread thread2 = new Thread(updateThread);
        thread2.start();

        writeConfigs();
     }


    // Followed this guide on reading files & storing to Hash Maps:
    // https://www.geeksforgeeks.org/reading-text-file-into-java-hashmap/
    // The function will read the configs.txt file, and store the
    // "Title" (String) and the subsequent values (ArrayList)
    private static HashMap<String, ArrayList<String>> init() {
        HashMap<String, ArrayList<String>> tempMap = new HashMap<>();
        BufferedReader br = null;

        try {
            // create file object & Buffered Reader & temp String storage
            File file = new File(configFilePath);
            br = new BufferedReader(new FileReader(file));
            String line;

            // read configs line by line, searching for specific titles
            while ((line = br.readLine()) != null) {
                if (line.equals("CARB_RATIO") ||
                    line.equals("INSULIN_SENSITIVITY") ||
                    line.equals("INSULIN_LONGEVITY") ||
                    line.equals("TARGET_GLUCOSE") ||
                    line.startsWith("BASAL_PATTERN")) {

                    String title = line;
                    ArrayList<String> schedule = new ArrayList<>();

                    while((line = br.readLine()) != null) {
                        if (!line.isBlank()) {
                            schedule.add(line);
                        } else {
                            break;
                        }
                    }

                    tempMap.put(title, schedule);
                }
            }

        } catch (Exception e) {
            // replace with better exception?
            e.printStackTrace();

        } finally {
            // close Buffered Reader
            if (br != null) {

                try {
                    br.close();

                } catch (Exception e) {
                    // replace with better exception?
                    e.printStackTrace();
                }
            }
        }

        return tempMap;
    }

    //https://stackoverflow.com/questions/26785315/how-to-overwrite-an-existing-txt-file
    public static void writeConfigs() {
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
            ArrayList<Double> writeCarbs = pump.getBolusSettings().getCarbRatio();
            for (int i = 0; i < writeCarbs.size(); i++) {
                bw.write(String.valueOf(writeCarbs.get(i)) + "\n");
            }
            bw.write("\n");

            // write insulin sensitivities to file
            bw.write("INSULIN_SENSITIVITY\n");
            ArrayList<Double> writeSensitivity = pump.getBolusSettings().getInsulinSensitivity();
            for (int i = 0; i < writeSensitivity.size(); i++) {
                bw.write(writeSensitivity.get(i) + "\n");
            }
            bw.write("\n");

            // write insulin longevity to file
            bw.write("INSULIN_LONGEVITY\n");
            int writeLongevity = pump.getBolusSettings().getInsulinLongevity();
            bw.write(String.valueOf(writeLongevity) + "\n\n");

            // write targets to file
            bw.write("TARGET_GLUCOSE\n");
            double[] writeTarget = pump.getBolusSettings().getTargetGlucose();
            for (double target : writeTarget) {
                bw.write(target + "\n");
            }
            bw.write("\n");

            // write basal patterns to file
            // get current basal pattern #
            int current = pump.getBasalSettings().getCurrentBasalPattern();
            for (int i = 0; i < pump.getBasalSettings().getBasalPatternsSize(); i++) {
                bw.write("BASAL_PATTERN " + (i+1));

                // if this pattern is the current pattern, add an '*'
                if (i+1 == current) {
                    bw.write(" *\n");

                } else {
                    bw.write("\n");
                }

                // begin writing the pattern data
                ArrayList<Double> writeBasal = pump.getBasalSettings().getBasalPatternIndex(i);
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
