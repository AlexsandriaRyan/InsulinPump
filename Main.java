// Alexsandria Ryan
// INFT 3000 - Capstone
// NSCC Fall 2023

import Classes.BasalThread;
import Classes.Pump;
import Classes.UpdateThread;

import java.io.*;
import java.util.*;

// TO DO / CONSIDER
// Ensure there is insulin in the reservoir before basal / bolus.
// If there is not enough insulin, alert user of outstanding insulin that was not delivered
// Put Menu printlns in a different function? Within the pump class or main class?
// write configs to file

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

        // should I be putting the threads in main? I'm assuming so?
        BasalThread basalThread = new BasalThread(pump);
        Thread thread1 = new Thread(basalThread);
        thread1.start();

        UpdateThread updateThread = new UpdateThread(pump);
        Thread thread2 = new Thread(updateThread);
        thread2.start();

        // check for input (bolus or menu)
        char input = '0';
        while (input == '0') {
            Scanner scan = new Scanner(System.in);
            input = scan.next().charAt(0);
            if (input == '1' || input == '2') {
                thread2.interrupt();

                if (input == '1') {
                    pump.bolus();

                } else if (input == '2') {
                    // stop the "update" thread to prevent it appearing over the user input
                    thread2.interrupt();

                    // TO DO HERE:
                    // Implement the menu system
                    // Implement a timeout feature (bookmarked on chrome)
                    mainMenu();

                    input = scan.next().charAt(0);
                    switch (input) {
                        case 1:
                            pump.setActive();
                        case 2:
                            pump.newReservoir();
                        case 3:
                            insulinMenu();
                        case 4:
                        case 'Q' | 'q':
                            System.out.println("Exiting Menu...");
                        default:
                            System.out.println("Invalid entry. Exiting Menu...");
                    }
                }
            }

            // once navigation is complete, restart the "update" thread
            thread2.start();
        }
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

    // put all UI in a diff class
    private static void mainMenu() {
        System.out.println("MAIN MENU:");
        System.out.println("1. Suspend Delivery");
        System.out.println("2. New Reservoir");
        System.out.println("3. Insulin Settings");
        System.out.println("4. Change Date / Time");
        System.out.println("Q to exit");
    }

    private static void insulinMenu() {
        System.out.println("INSULIN MENU:");
        System.out.println("1. Bolus Settings");
        System.out.println("2. Basal Settings");
        System.out.println("Q to exit");
    }

    private static void bolusMenu() {
        System.out.println("BOLUS MENU:");
        System.out.println("1. Carb Ratio");
        System.out.println("2. Insulin Sensitivity");
        System.out.println("3. Insulin Longevity");
        System.out.println("4. Target Glucose");
        System.out.println("Q to exit");
    }

    private static void basalMenu() {
        System.out.println("BASAL MENU:");
        // for each loop of basal patterns
        System.out.println("Q to exit");
    }
}
