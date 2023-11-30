package Classes;

import java.util.Scanner;

public class Menu {

    private final Pump pump;
    public Menu(Pump pump) { this.pump = pump; }
    protected void mainMenu() {
        System.out.println("MAIN MENU:");
        System.out.println("1. Suspend/Resume Delivery");
        System.out.println("2. New Reservoir");
        System.out.println("3. Insulin Settings");
        System.out.println("Q to exit");

        Scanner scan = new Scanner(System.in);
        char input = scan.next().charAt(0);

        switch (input) {
            case '1':
                pump.setActive();
                break;

            case '2':
                pump.newReservoir();
                break;

            case '3':
                insulinMenu();
                break;

            case 'Q' | 'q':
                System.out.println("Exiting Menu...");
                break;

            default:
                System.out.println("Invalid entry. Exiting Menu...");
                break;
        }
    }

    protected void insulinMenu() {
        System.out.println("INSULIN MENU:");
        System.out.println("1. Bolus Settings");
        System.out.println("2. Basal Settings");
        System.out.println("Q to exit");

        Scanner scan = new Scanner(System.in);
        char input = scan.next().charAt(0);

        switch (input) {
            case '1':
                bolusMenu();
                break;

            case '2':
                basalMenu();
                break;

            case 'Q' | 'q':
                System.out.println("Exiting Menu...");
                break;

            default:
                System.out.println("Invalid entry. Exiting Menu...");
                break;
        }
    }

    protected void bolusMenu() {
        System.out.println("BOLUS MENU:");
        System.out.println("1. Carb Ratio");
        System.out.println("2. Insulin Sensitivity");
        System.out.println("3. Insulin Longevity");
        System.out.println("4. Target Glucose");
        System.out.println("Q to exit");

        Scanner scan = new Scanner(System.in);
        char input = scan.next().charAt(0);

        switch (input) {
            case '1':
                Pump.bolusSettings.setCarbRatio();
                break;

            case '2':
                Pump.bolusSettings.setInsulinSensitivity();
                break;

            case '3':
                Pump.bolusSettings.setInsulinLongevity();
                break;

            case '4':
                Pump.bolusSettings.setTargetGlucose();
                break;

            case 'Q' | 'q':
                System.out.println("Exiting Menu...");
                break;

            default:
                System.out.println("Invalid entry. Exiting Menu...");
                break;
        }
    }

    protected void basalMenu() {
        System.out.println("BASAL MENU:");
        System.out.println("1. Add New Basal Pattern");
        System.out.println("2. Delete Basal Pattern");
        System.out.println("3. Select Current Basal Pattern");
        System.out.println("Q to exit");

        Scanner scan = new Scanner(System.in);
        char input = scan.next().charAt(0);

        switch (input) {
            case '1':
                Pump.basalSettings.setBasalPattern();
                break;

            case '2':
                Pump.basalSettings.deleteBasalPattern();
                break;

            case '3':
                Pump.bolusSettings.setInsulinLongevity();
                break;

            case 'Q' | 'q':
                System.out.println("Exiting Menu...");
                break;

            default:
                System.out.println("Invalid entry. Exiting Menu...");
                break;
        }
        // for each loop of basal patterns
        System.out.println("Q to exit");
    }
}
