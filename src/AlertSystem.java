import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlertSystem {
    private List<Alert> activeAlerts;
    private List<Alert> alertHistory;
    private int alertCounter;

    public AlertSystem() {
        this.activeAlerts = new ArrayList<>();
        this.alertHistory = new ArrayList<>();
        this.alertCounter = 1;
    }

    public void triggerAlert(int sensorCode, double value, AlertLevel level) {

        String generatedId = String.format("ALT-%04d", alertCounter++);


        Alert newAlert = new Alert(generatedId, sensorCode, value, level);

        activeAlerts.add(newAlert);
        alertHistory.add(newAlert);
        System.out.println(" SYSTEM ALERT: " + generatedId + " triggered.");
    }

    public void acknowledgeAlert(String alertId) {
        for (int i = 0; i < activeAlerts.size(); i++) {
            if (activeAlerts.get(i).getId().equals(alertId)) { // Use .equals() for Strings!
                activeAlerts.get(i).acknowledge();
                activeAlerts.remove(i);
                System.out.println("Alert " + alertId + " acknowledged and removed from active panel.");
                return;
            }
        }
    }


    public List<Alert> getActiveAlerts() { return Collections.unmodifiableList(activeAlerts); }
    public Alert getLatestAlert() { return activeAlerts.get(activeAlerts.size() - 1); }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("=== PANNEAU DES ALERTES ACTIVES ===\n");

        if (activeAlerts.isEmpty()) {
            sb.append(" Tout est normal. Aucune alerte active.\n");
            return sb.toString();
        }


        for (Alert a : activeAlerts) {
            if (a.getLevel() == AlertLevel.CRITICAL) {
                sb.append(a.toString()).append("\n");
            }
        }

        for (Alert a : activeAlerts) {
            if (a.getLevel() == AlertLevel.WARNING) {
                sb.append(a.toString()).append("\n");
            }
        }

        return sb.toString();
    }
}