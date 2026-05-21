import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class Farm extends JFrame {

    // Core Data Lists
    private List<GeographicalZone> zones = new ArrayList<>();
    private List<Alert> activeAlerts = new ArrayList<>();

    // UI Models for the Lists
    private DefaultListModel<GeographicalZone> zoneListModel = new DefaultListModel<>();
    private DefaultListModel<Alert> alertListModel = new DefaultListModel<>();

    // Modern Color Palette
    private static final Color SIDEBAR_BG = new Color(0x0F, 0x17, 0x2A); // Slate 900
    private static final Color MAIN_BG = new Color(0xF8, 0xFA, 0xFC);    // Slate 50
    private static final Color ACCENT_BLUE = new Color(0x18, 0x5F, 0xA5); // Matches SensorCardGUI
    private static final Color ACCENT_GREEN = new Color(0x10, 0xB9, 0x81);
    private static final Color DANGER_RED = new Color(0xEF, 0x44, 0x44);

    // Layout Manager for "Tabs"
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainContentPanel = new JPanel(cardLayout);

    public Farm() {
        super("ESI Smart Farming - Central Management");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(MAIN_BG);

        // Build the modern UI
        add(createSidebar(), BorderLayout.WEST);

        mainContentPanel.add(createZonePanel(), "ZONES");
        mainContentPanel.add(createAlertPanel(), "ALERTS");
        add(mainContentPanel, BorderLayout.CENTER);

        // Load initial test data
        loadMockData();
    }

    // ==========================================
    // MODERN SIDEBAR NAVIGATION
    // ==========================================
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(new EmptyBorder(30, 15, 20, 15));

        JLabel logo = new JLabel("ESI FARMING");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnZones = createSidebarButton("🌍 Zones & Sensors");
        JButton btnAlerts = createSidebarButton("⚠️ Alerts Console");

        btnZones.addActionListener(e -> cardLayout.show(mainContentPanel, "ZONES"));
        btnAlerts.addActionListener(e -> cardLayout.show(mainContentPanel, "ALERTS"));

        sidebar.add(logo);
        sidebar.add(Box.createRigidArea(new Dimension(0, 50)));
        sidebar.add(btnZones);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(btnAlerts);

        return sidebar;
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(new Color(200, 200, 200));
        btn.setBackground(SIDEBAR_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover Effect
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(Color.WHITE); }
            public void mouseExited(MouseEvent e) { btn.setForeground(new Color(200, 200, 200)); }
        });
        return btn;
    }

    // ==========================================
    // MODERN ZONE MANAGEMENT PANEL
    // ==========================================
    private JPanel createZonePanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(MAIN_BG);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Title
        JLabel title = new JLabel("Zone Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(SIDEBAR_BG);

        // Modern List
        JList<GeographicalZone> zoneList = new JList<>(zoneListModel);
        zoneList.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        zoneList.setFixedCellHeight(35);
        zoneList.setSelectionBackground(new Color(0xE2, 0xE8, 0xF0));
        zoneList.setSelectionForeground(SIDEBAR_BG);

        JScrollPane scrollPane = new JScrollPane(zoneList);
        scrollPane.setBorder(new LineBorder(new Color(0xCB, 0xD5, 0xE1), 1, true));

        // Right side controls
        JPanel actionPanel = new JPanel(new GridLayout(4, 1, 0, 15));
        actionPanel.setBackground(MAIN_BG);
        actionPanel.setPreferredSize(new Dimension(250, 0));

        JButton btnSuspend = styleButton("Suspend / Reactivate", SIDEBAR_BG, Color.WHITE);
        JButton btnDelete = styleButton("Delete Selected Zone", DANGER_RED, Color.WHITE);
        JButton btnOpenSensor = styleButton("Open Sensor Dashboard", ACCENT_BLUE, Color.WHITE);

        btnSuspend.addActionListener(e -> {
            GeographicalZone selected = zoneList.getSelectedValue();
            if (selected != null) {
                if (selected.getStatus() == ZoneStatus.ACTIVE) selected.suspend();
                else selected.reactivate();
                zoneList.repaint();
            }
        });

        btnDelete.addActionListener(e -> {
            GeographicalZone selected = zoneList.getSelectedValue();
            if (selected != null) {
                zones.remove(selected);
                zoneListModel.removeElement(selected);
            }
        });

        btnOpenSensor.addActionListener(e -> {
            GeographicalZone selectedZone = zoneList.getSelectedValue();
            if (selectedZone != null) {
                if (selectedZone.sensors != null && !selectedZone.sensors.isEmpty()) {
                    Sensor firstSensor = selectedZone.sensors.get(0);
                    if (firstSensor instanceof NumericalSensor) {
                        new SensorCardGUI((NumericalSensor) firstSensor).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "This sensor is not a NumericalSensor.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No sensors are assigned to this zone yet.", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        actionPanel.add(btnOpenSensor);
        actionPanel.add(btnSuspend);
        actionPanel.add(btnDelete);

        // Bottom Form (Includes Surface parameter)
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new LineBorder(new Color(0xCB, 0xD5, 0xE1), 1, true));

        JTextField codeInput = styleTextField(8);
        JTextField nameInput = styleTextField(12);
        JTextField surfaceInput = styleTextField(8);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Crop", "Livestock", "Aquaculture"});
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnAddZone = styleButton("Add Zone", ACCENT_GREEN, Color.WHITE);

        formPanel.add(new JLabel("Code:")); formPanel.add(codeInput);
        formPanel.add(new JLabel("Name:")); formPanel.add(nameInput);
        formPanel.add(new JLabel("Surface:")); formPanel.add(surfaceInput);
        formPanel.add(new JLabel("Type:")); formPanel.add(typeCombo);
        formPanel.add(btnAddZone);

        btnAddZone.addActionListener(e -> {
            String code = codeInput.getText().trim();
            String name = nameInput.getText().trim();
            String surfaceTxt = surfaceInput.getText().trim();

            if (!code.isEmpty() && !name.isEmpty() && !surfaceTxt.isEmpty()) {
                try {
                    double surface = Double.parseDouble(surfaceTxt);
                    String type = (String) typeCombo.getSelectedItem();
                    GeographicalZone newZone = null;

                    if ("Crop".equals(type)) {
                        newZone = new CropZone(code, name, ZoneStatus.ACTIVE, surface);
                    } else if ("Livestock".equals(type)) {
                        newZone = new LivestockZone<>(code, name, ZoneStatus.ACTIVE, surface, null);
                    } else if ("Aquaculture".equals(type)) {
                        newZone = new AquacultureZone(code, name, ZoneStatus.ACTIVE, surface, null, null);
                    }

                    if (newZone != null) {
                        zones.add(newZone);
                        zoneListModel.addElement(newZone);
                        codeInput.setText(""); nameInput.setText(""); surfaceInput.setText("");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Surface must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.EAST);
        panel.add(formPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ==========================================
    // MODERN ALERTS CONSOLE
    // ==========================================
    private JPanel createAlertPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(MAIN_BG);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("System Alerts");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(SIDEBAR_BG);

        JList<Alert> alertList = new JList<>(alertListModel);
        alertList.setFont(new Font("Segoe UI", Font.BOLD, 15));
        alertList.setForeground(DANGER_RED);
        alertList.setFixedCellHeight(40);

        JScrollPane scrollPane = new JScrollPane(alertList);
        scrollPane.setBorder(new LineBorder(new Color(0xCB, 0xD5, 0xE1), 1, true));

        JButton btnAck = styleButton("Acknowledge Selected Alert", SIDEBAR_BG, Color.WHITE);
        btnAck.addActionListener(e -> {
            Alert selected = alertList.getSelectedValue();
            if (selected != null) {
                selected.acknowledge();
                activeAlerts.remove(selected);
                alertListModel.removeElement(selected);
            }
        });

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnAck, BorderLayout.SOUTH);

        return panel;
    }

    // ==========================================
    // UI UTILITIES & MOCK DATA
    // ==========================================
    private JButton styleButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 40));
        return btn;
    }

    private JTextField styleTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0xCB, 0xD5, 0xE1)),
                new EmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private void loadMockData() {
        CropZone zone1 = new CropZone("Z01", "North Wheat Field", ZoneStatus.ACTIVE, 150.5);

        try {
            EnvironmentalSensor envSensor = new EnvironmentalSensor(54, 10, EnvironmentalType.TEMPERATURE, 15.0, 35.0);
            zone1.sensors.add(envSensor);
        } catch (Exception e) {
            System.out.println("Could not create mock sensor.");
        }

        zones.add(zone1);
        zoneListModel.addElement(zone1);

        Alert testAlert = new Alert("ALT-001", 54, 42.0, AlertLevel.CRITICAL);
        activeAlerts.add(testAlert);
        alertListModel.addElement(testAlert);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            Farm dashboard = new Farm();
            dashboard.setVisible(true);
        });
    }
}