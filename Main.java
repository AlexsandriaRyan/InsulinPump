// Alexsandria Ryan
// INFT 3000 - Capstone
// NSCC Fall 2023

import Classes.BasalThread;
import Classes.Pump;
import Classes.UpdateThread;

import java.io.*;
import java.util.*;

// TO DO / CONSIDER / Notes
// write configs to file
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
}
