// Alexsandria Ryan
// INFT 3000 - Capstone
// NSCC Fall 2023

import Classes.BasalThread;
import Classes.Pump;
import Classes.UpdateThread;

import java.io.*;
import java.util.*;

public class Main {

    // notes:
    // consider making a 'common' class with commonly used / static variables
    // variables could include THE hashmap (which would be updated upon setting bolus / basal settings), a variable for hours (24), etc

    // design the navigations. ie., menus, the options within those menus, etc.

    final static String configFilePath = "Configs/configs.txt";
    public static Pump pump;

    public static void main(String[] args) {
        // create hashmaps based on config file then create
        // pump object, either with configs or via setup process
        HashMap<String, ArrayList<String>> configs = init();

        if(!configs.isEmpty()) {
//            for (HashMap.Entry<String, ArrayList<String>> entry : configs.entrySet()) {
//                System.out.println(entry.getKey() + "\n" + entry.getValue() + "\n");
//            }

            pump = new Pump(configs);
            //pump.printConfigs();

        } else {
            pump = new Pump();
        }

        // should I be putting the threads in main? I'm assuming so?
        BasalThread basalThread = new BasalThread(pump);
        Thread thread1 = new Thread(basalThread);
        thread1.start();

        UpdateThread updateThread = new UpdateThread(pump);
        Thread thread2 = new Thread(updateThread);
        thread2.start();

         while (pump.active) {
            // check for input (bolus or menu)
            char input = '0';
            while (input == '0') {
                Scanner scan = new Scanner(System.in);
                input = scan.next().charAt(1);
                if (input == '1' || input == '2') {
                    // if any valid input, cancel the timer, and continue through the selected menu
                    //timer.cancel();
    
                    if (input == '1') {
                        pump.bolus();
    
                    } else if (input == '2') {
                        System.out.println("MAIN MENU:");
                        System.out.println("1. Suspend Delivery");
                        System.out.println("2. New Reservoir");
                        System.out.println("3. Insulin Settings");
                        System.out.println("4. Change Date / Time");
                        System.out.println("Q to exit");

                        // reset input
                        input = '0';

                        input = scan.next().charAt(1);
                        switch (input) {
                            case 1:
                                pump.setActive();
                            case 2:
                                pump.newReservoir();
                            case 3:
                                System.out.println("INSULIN SETTINGS MENU");
                            case 4:
                            case 'Q' | 'q':
                                System.out.println("Exiting Menu...");
                            default:
                                System.out.println("Invalid entry. Exiting Menu...");
                        }
                    }
                }
            }
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
}


// Initially had a timer task, opted for multi threading. unsure which is more efficient.
//class RunPump extends TimerTask {
//    @Override
//    public void run() {
//        Main.pump.basal();
//        System.out.println(Main.pump.getTime().toString());
//        System.out.println("ACTIVE INSULIN:\t" + Main.pump.getActiveInsulin());
//        System.out.println("OPTIONS:\t\t1.Bolus 2.Menu");
//    }
//}

//this part would go under while(pump.active) {
// create the task & schedule it
//RunPump runPump = new RunPump();
//Timer timer = new Timer();
//timer.schedule(runPump, 0, 60000);

