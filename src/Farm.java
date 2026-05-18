import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Farm {
    private String name;
    private List<GeographicalZone> zones;
    private AlertSystem alertSystem;

    public Farm(String name) {
        this.name = name;
        this.zones = new ArrayList<>();
        this.alertSystem = new AlertSystem();
    }

    public void addZone(GeographicalZone zone) {
        zones.add(zone);
    }

    public static void main(String[] args) {
         Farm myFarm = new Farm("ESI Smart Farm");
         Scanner scanner = new Scanner(System.in);
        boolean running = true;

         while (running) {
             System.out.println("\n--- " + myFarm.name + " Management System ---");
            System.out.println("1. Afficher les zones (View Zones)");
            System.out.println("2. Simuler un relevé de capteur (Simulate Sensor Reading)");
            System.out.println("3. Quitter (Exit)");
            System.out.print("Choix: ");

             int choice = scanner.nextInt();

             switch (choice) {
                case 1:
                     for (GeographicalZone z : myFarm.zones) {
                        System.out.println("Zone: " + z.getName() + " | Status: " + z.getStatus() + " | Entities: " + z.getEntityCount());
                    }
                    if (myFarm.zones.isEmpty()) {
                        System.out.println("No zones registered yet.");
                    }
                    break;
                case 2:
                    System.out.println("Simulation de relevé déclenchée...");

                    break;
                case 3:
                     running = false;
                    System.out.println("Fermeture du système...");
                    break;
                default:
                    System.out.println("Choix invalide.");
            }
        }
         scanner.close();
    }
}