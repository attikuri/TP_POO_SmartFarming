import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

/**
 * Farm.java — ESI Smart Farming Central Management Shell
 * Redesigned with a modern, minimalist UI.
 */
public class Farm extends JFrame {

    // ─── Shared Domain State ────────────────────────────────────────────────
    private final List<GeographicalZone>             zones          = new ArrayList<>();
    private final AlertSystem                        alertSystem    = new AlertSystem();
    private final DefaultListModel<GeographicalZone> zoneListModel  = new DefaultListModel<>();
    private final DefaultListModel<Alert>            alertListModel = new DefaultListModel<>();

    private javax.swing.Timer simTimer;
    private boolean           simRunning = false;

    // ─── Live dashboard labels ───────────────────────────────────────────────
    private JLabel lblTotalZones, lblActiveZones, lblActiveAlerts, lblSensorsOnline;
    private JLabel lblTotalSub, lblActiveSub, lblAlertSub, lblSensorSub;
    private JLabel simStatusLabel;

    // ─── Alert badge on sidebar nav item ────────────────────────────────────
    private JLabel alertNavBadge;

    // ─── Currently selected nav item (for highlight state) ──────────────────
    private JPanel selectedNavItem = null;
    private final List<JPanel> navItems = new ArrayList<>();

    // ─── Color Palette ───────────────────────────────────────────────────────
    // Backgrounds
    private static final Color BG_PRIMARY   = new Color(0xFF, 0xFF, 0xFF);
    private static final Color BG_SECONDARY = new Color(0xF7, 0xF7, 0xF5);
    private static final Color BG_TERTIARY  = new Color(0xF0, 0xF0, 0xED);

    // Semantic backgrounds (light tints)
    private static final Color BG_SUCCESS   = new Color(0xEA, 0xF3, 0xDE);
    private static final Color BG_INFO      = new Color(0xE6, 0xF1, 0xFB);
    private static final Color BG_WARNING   = new Color(0xFA, 0xEE, 0xDA);
    private static final Color BG_DANGER    = new Color(0xFC, 0xEB, 0xEB);

    // Text colors
    private static final Color TEXT_PRIMARY   = new Color(0x1A, 0x1A, 0x18);
    private static final Color TEXT_SECONDARY = new Color(0x5F, 0x5E, 0x5A);
    private static final Color TEXT_TERTIARY  = new Color(0x88, 0x87, 0x80);

    // Semantic text
    private static final Color TEXT_SUCCESS = new Color(0x3B, 0x6D, 0x11);
    private static final Color TEXT_INFO    = new Color(0x18, 0x5F, 0xA5);
    private static final Color TEXT_WARNING = new Color(0x85, 0x4F, 0x0B);
    private static final Color TEXT_DANGER  = new Color(0xA3, 0x2D, 0x2D);

    // Border
    private static final Color BORDER = new Color(0x00, 0x00, 0x00, 38); // ~0.15α

    // ─── Layout ──────────────────────────────────────────────────────────────
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     mainArea   = new JPanel(cardLayout);

    // ══════════════════════════════════════════════════════════════════════════
    //  CONSTRUCTOR
    // ══════════════════════════════════════════════════════════════════════════
    public Farm() {
        super("ESI Smart Farming — Central Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 740);
        setMinimumSize(new Dimension(960, 620));
        setLocationRelativeTo(null);

        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        // Make sure all panels use our BG
        UIManager.put("Panel.background", BG_PRIMARY);

        setBackground(BG_PRIMARY);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);
        buildPanels();
        add(mainArea, BorderLayout.CENTER);

        loadDemoData();
        refreshDashboard();
        cardLayout.show(mainArea, "DASHBOARD");
        // Highlight Dashboard nav item (first in list)
        if (!navItems.isEmpty()) {
            JPanel first = navItems.get(0);
            Component pill = first.getComponent(0);
            if (pill instanceof JPanel && ((JPanel) pill).getComponentCount() > 0) {
                Component inner = ((JPanel) pill).getComponent(0);
                if (inner instanceof JPanel) {
                    JPanel pillInner = (JPanel) inner;
                    for (Component c : pillInner.getComponents()) {
                        if (c instanceof JLabel) {
                            setNavSelected(first, (JLabel) c, pillInner);
                            break;
                        }
                    }
                }
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SIDEBAR
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BG_PRIMARY);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setMinimumSize(new Dimension(220, 0));
        sidebar.setMaximumSize(new Dimension(220, Integer.MAX_VALUE));
        sidebar.setBorder(new MatteBorder(0, 0, 0, 1, BORDER));

        // ── Logo ─────────────────────────────────────────────────────────────
        JPanel logoPanel = new JPanel(new BorderLayout(10, 0));
        logoPanel.setOpaque(false);
        logoPanel.setBorder(new EmptyBorder(16, 16, 14, 16));
        logoPanel.setMaximumSize(new Dimension(220, 68));
        logoPanel.setAlignmentX(LEFT_ALIGNMENT);

        // Green icon box
        JPanel logoIcon = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_SUCCESS);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
            }
        };
        logoIcon.setOpaque(false);
        logoIcon.setPreferredSize(new Dimension(36, 36));
        logoIcon.setMinimumSize(new Dimension(36, 36));
        logoIcon.setMaximumSize(new Dimension(36, 36));
        JLabel leafLbl = new JLabel("🌿", SwingConstants.CENTER);
        leafLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        logoIcon.add(leafLbl, BorderLayout.CENTER);

        JPanel logoText = new JPanel();
        logoText.setLayout(new BoxLayout(logoText, BoxLayout.Y_AXIS));
        logoText.setOpaque(false);
        JLabel logoName = new JLabel("ESI Farm");
        logoName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoName.setForeground(TEXT_PRIMARY);
        JLabel logoSub = new JLabel("Smart Management");
        logoSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        logoSub.setForeground(TEXT_TERTIARY);
        logoText.add(logoName);
        logoText.add(Box.createRigidArea(new Dimension(0, 2)));
        logoText.add(logoSub);

        logoPanel.add(logoIcon, BorderLayout.WEST);
        logoPanel.add(logoText, BorderLayout.CENTER);

        sidebar.add(logoPanel);
        sidebar.add(sideSep());

        // ── Navigation ───────────────────────────────────────────────────────
        sidebar.add(sideSection("Overview"));
        sidebar.add(navItem("Dashboard",        "DASHBOARD"));
        sidebar.add(sideSection("Manage"));
        sidebar.add(navItem("Zones",            "ZONES"));
        sidebar.add(navItem("Sensors",          "SENSORS"));
        sidebar.add(navItem("Feeding Programs", "FEEDING"));
        sidebar.add(navItem("Production",       "PRODUCTION"));
        sidebar.add(sideSection("System"));
        sidebar.add(navItemWithBadge("Alerts Console", "ALERTS"));
        sidebar.add(navItem("Simulation",       "SIMULATION"));

        sidebar.add(Box.createVerticalGlue());

        // ── Footer ───────────────────────────────────────────────────────────
        sidebar.add(sideSep());
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 9));
        footer.setOpaque(false);
        footer.setMaximumSize(new Dimension(220, 38));
        footer.setAlignmentX(LEFT_ALIGNMENT);

        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(alertListModel.isEmpty() ? TEXT_SUCCESS : TEXT_WARNING);
                g2.fillOval(0, 3, 8, 8);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(8, 14); }
        };
        dot.setOpaque(false);
        JLabel statusLbl = new JLabel("All systems normal");
        statusLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLbl.setForeground(TEXT_TERTIARY);
        footer.add(dot);
        footer.add(statusLbl);
        sidebar.add(footer);

        return sidebar;
    }

    /** Creates a standard nav item as a clickable JPanel with left-aligned label. */
    private JPanel navItem(String text, String card) {
        return buildNavItem(text, card, false);
    }

    /** Nav item that also stores a reference for the alert count badge. */
    private JPanel navItemWithBadge(String text, String card) {
        return buildNavItem(text, card, true);
    }

    private JPanel buildNavItem(String text, String card, boolean withBadge) {
        JPanel item = new JPanel(new BorderLayout(0, 0));
        item.setOpaque(false);
        item.setBackground(BG_PRIMARY);
        item.setMaximumSize(new Dimension(220, 34));
        item.setPreferredSize(new Dimension(220, 34));
        item.setMinimumSize(new Dimension(220, 34));
        item.setAlignmentX(LEFT_ALIGNMENT);
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Inner pill panel with left padding — this is what gets the background tint
        JPanel pill = new JPanel(new BorderLayout(0, 0));
        pill.setOpaque(false);
        pill.setBorder(new EmptyBorder(0, 8, 0, 8));

        JPanel pillInner = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                if (isOpaque()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                } else {
                    super.paintComponent(g);
                }
            }
        };
        pillInner.setOpaque(false);
        pillInner.setBorder(new EmptyBorder(0, 8, 0, 8));

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(TEXT_SECONDARY);
        pillInner.add(lbl, BorderLayout.CENTER);

        if (withBadge) {
            alertNavBadge = new JLabel("1");
            alertNavBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
            alertNavBadge.setForeground(TEXT_WARNING);
            alertNavBadge.setBackground(BG_WARNING);
            alertNavBadge.setOpaque(true);
            alertNavBadge.setBorder(new EmptyBorder(1, 6, 1, 6));
            alertNavBadge.setVisible(false);
            pillInner.add(alertNavBadge, BorderLayout.EAST);
        }

        pill.add(pillInner, BorderLayout.CENTER);
        item.add(pill, BorderLayout.CENTER);

        navItems.add(item);

        // Selection logic
        MouseAdapter ma = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                setNavSelected(item, lbl, pillInner);
                cardLayout.show(mainArea, card);
            }
            @Override public void mouseEntered(MouseEvent e) {
                if (selectedNavItem != item) {
                    pillInner.setOpaque(true);
                    pillInner.setBackground(BG_SECONDARY);
                    pillInner.repaint();
                }
            }
            @Override public void mouseExited(MouseEvent e) {
                if (selectedNavItem != item) {
                    pillInner.setOpaque(false);
                    pillInner.repaint();
                }
            }
        };
        item.addMouseListener(ma);
        lbl.addMouseListener(ma);
        pillInner.addMouseListener(ma);
        if (withBadge && alertNavBadge != null) alertNavBadge.addMouseListener(ma);

        return item;
    }

    private void setNavSelected(JPanel item, JLabel lbl, JPanel pillInner) {
        // Deselect all
        for (JPanel navItem : navItems) {
            navItem.setBackground(BG_PRIMARY);
            // find pill inner and reset
            if (navItem.getComponentCount() > 0) {
                Component pill = navItem.getComponent(0);
                if (pill instanceof JPanel && ((JPanel) pill).getComponentCount() > 0) {
                    Component inner = ((JPanel) pill).getComponent(0);
                    if (inner instanceof JPanel) {
                        inner.setBackground(BG_PRIMARY);
                        ((JPanel) inner).setOpaque(false);
                        // reset label color
                        for (Component c : ((JPanel) inner).getComponents()) {
                            if (c instanceof JLabel && !(c == alertNavBadge)) {
                                c.setForeground(TEXT_SECONDARY);
                                ((JLabel) c).setFont(new Font("Segoe UI", Font.PLAIN, 13));
                            }
                        }
                        inner.repaint();
                    }
                }
            }
        }
        // Select this item
        selectedNavItem = item;
        pillInner.setOpaque(true);
        pillInner.setBackground(BG_SUCCESS);
        lbl.setForeground(TEXT_SUCCESS);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pillInner.repaint();
    }

    private JSeparator sideSep() {
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(BORDER);
        sep.setBackground(BG_PRIMARY);
        sep.setMaximumSize(new Dimension(220, 1));
        sep.setAlignmentX(LEFT_ALIGNMENT);
        return sep;
    }

    private JLabel sideSection(String text) {
        JLabel lbl = new JLabel(text.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lbl.setForeground(TEXT_TERTIARY);
        lbl.setBorder(new EmptyBorder(10, 16, 4, 16));
        lbl.setMaximumSize(new Dimension(220, 28));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  BUILD ALL PANELS
    // ══════════════════════════════════════════════════════════════════════════
    private void buildPanels() {
        mainArea.add(buildDashboardPanel(),  "DASHBOARD");
        mainArea.add(buildZonePanel(),       "ZONES");
        mainArea.add(buildSensorPanel(),     "SENSORS");
        mainArea.add(buildFeedingPanel(),    "FEEDING");
        mainArea.add(buildProductionPanel(), "PRODUCTION");
        mainArea.add(buildAlertPanel(),      "ALERTS");
        mainArea.add(buildSimulationPanel(), "SIMULATION");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  1. DASHBOARD PANEL
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildDashboardPanel() {
        JPanel panel = bgPanel(new BorderLayout());

        panel.add(buildTopBar("Farm dashboard", "Overview of all zones and activity",
                "Refresh", "Add zone", null), BorderLayout.NORTH);

        JPanel body = bgPanel(new BorderLayout(16, 16));
        body.setBorder(new EmptyBorder(20, 24, 20, 24));

        // ── Stat cards ───────────────────────────────────────────────────────
        JPanel statsRow = bgPanel(new GridLayout(1, 4, 12, 0));
        statsRow.setPreferredSize(new Dimension(0, 100));

        lblTotalZones     = metricLabel("0");
        lblActiveZones    = metricLabel("0");
        lblActiveAlerts   = metricLabel("0");
        lblSensorsOnline  = metricLabel("0");
        lblTotalSub       = metricSub("—");
        lblActiveSub      = metricSub("—");
        lblAlertSub       = metricSub("—");
        lblSensorSub      = metricSub("—");

        lblTotalZones.setForeground(TEXT_INFO);
        lblActiveZones.setForeground(TEXT_SUCCESS);
        lblActiveAlerts.setForeground(TEXT_WARNING);
        lblSensorsOnline.setForeground(TEXT_SUCCESS);

        statsRow.add(statCard("Total zones",     lblTotalZones,  lblTotalSub));
        statsRow.add(statCard("Active zones",    lblActiveZones, lblActiveSub));
        statsRow.add(statCard("Active alerts",   lblActiveAlerts, lblAlertSub));
        statsRow.add(statCard("Sensors online",  lblSensorsOnline, lblSensorSub));

        // ── Bottom row: table + alerts ───────────────────────────────────────
        JPanel bottomRow = bgPanel(new BorderLayout(16, 0));

        // Zone table
        String[] cols = {"Code", "Name", "Type", "Status",
                "Entities", "Sensors", "Records"};
        DefaultTableModel tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable zoneTable = buildStyledTable(tableModel);
        zoneTable.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0
                    && zoneTable.isShowing()) {
                refreshZoneTable(tableModel);
            }
        });

        JPanel tableCard = tableCard("Zone overview", styledScroll(zoneTable));

        // Alerts side panel
        JList<Alert> alertList = new JList<>(alertListModel);
        alertList.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        alertList.setFixedCellHeight(56);
        alertList.setCellRenderer(new AlertCellRenderer());
        alertList.setBackground(BG_PRIMARY);

        JPanel alertCard = tableCard("Recent alerts", styledScroll(alertList));
        alertCard.setPreferredSize(new Dimension(268, 0));

        bottomRow.add(tableCard,  BorderLayout.CENTER);
        bottomRow.add(alertCard,  BorderLayout.EAST);

        body.add(statsRow,   BorderLayout.NORTH);
        body.add(bottomRow,  BorderLayout.CENTER);

        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private void refreshZoneTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (GeographicalZone z : zones) {
            model.addRow(new Object[]{
                    z.getCode(),
                    z.getName(),
                    zoneTypeName(z),
                    z.getStatus(),
                    z.getEntityCount(),
                    z.sensors.size(),
                    z.getProductionHistory().size()
            });
        }
    }

    private void refreshDashboard() {
        int total  = zones.size();
        int active = (int) zones.stream()
                .filter(z -> z.getStatus() == ZoneStatus.ACTIVE).count();
        List<Alert> alerts = alertSystem.getActiveAlerts();
        int critical = (int) alerts.stream()
                .filter(a -> a.getLevel() == AlertLevel.CRITICAL).count();
        int sensors = zones.stream().mapToInt(z -> z.sensors.size()).sum();

        if (lblTotalZones != null) {
            lblTotalZones.setText(String.valueOf(total));
            lblActiveZones.setText(String.valueOf(active));
            lblActiveAlerts.setText(String.valueOf(alerts.size()));
            lblSensorsOnline.setText(String.valueOf(sensors));

            // subtitles
            long crop = zones.stream().filter(z -> z instanceof CropZone).count();
            long live = zones.stream().filter(z -> z instanceof LivestockZone).count();
            long aqua = zones.stream().filter(z -> z instanceof AquacultureZone).count();
            lblTotalSub.setText(crop + " crop · " + live + " livestock");
            lblActiveSub.setText((total - active) + " suspended");
            lblAlertSub.setText(critical + " critical");
            lblSensorSub.setText("Across " + zones.stream()
                    .filter(z -> !z.sensors.isEmpty()).count() + " zones");

            // alert value color
            lblActiveAlerts.setForeground(
                    critical > 0 ? TEXT_DANGER
                            : alerts.size() > 0 ? TEXT_WARNING
                            : TEXT_SUCCESS);
        }

        // update sidebar badge
        if (alertNavBadge != null) {
            int count = alertListModel.getSize();
            alertNavBadge.setText(String.valueOf(count));
            alertNavBadge.setVisible(count > 0);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  2. ZONE MANAGEMENT PANEL
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildZonePanel() {
        JPanel panel = bgPanel(new BorderLayout());
        panel.add(buildTopBar("Zone management",
                "Create, inspect and manage farm zones",
                null, null, null), BorderLayout.NORTH);

        JPanel body = bgPanel(new BorderLayout(16, 0));
        body.setBorder(new EmptyBorder(20, 24, 20, 24));

        // ── Left: list + add form ─────────────────────────────────────────
        JList<GeographicalZone> zoneList = new JList<>(zoneListModel);
        zoneList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        zoneList.setFixedCellHeight(40);
        zoneList.setCellRenderer(new ZoneCellRenderer());
        zoneList.setBackground(BG_PRIMARY);

        // Add form
        JPanel formCard = whiteCard();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel formTitle = new JLabel("Add new zone");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formTitle.setForeground(TEXT_PRIMARY);
        formTitle.setAlignmentX(LEFT_ALIGNMENT);

        JTextField fCode    = plainField(8);
        JTextField fName    = plainField(14);
        JTextField fSurface = plainField(8);
        JComboBox<String> fType = styledCombo(
                new String[]{"Crop", "Livestock", "Aquaculture"});

        JButton btnAdd = primaryButton("Add zone");
        btnAdd.setAlignmentX(LEFT_ALIGNMENT);
        btnAdd.addActionListener(e -> {
            String code = fCode.getText().trim();
            String name = fName.getText().trim();
            String surf = fSurface.getText().trim();
            if (code.isEmpty() || name.isEmpty() || surf.isEmpty()) {
                showError("Please fill all fields."); return;
            }
            try {
                double surface = Double.parseDouble(surf);
                GeographicalZone nz = createZone(
                        (String) fType.getSelectedItem(), code, name, surface);
                zones.add(nz);
                zoneListModel.addElement(nz);
                fCode.setText(""); fName.setText(""); fSurface.setText("");
                refreshDashboard();
            } catch (NumberFormatException ex) {
                showError("Surface must be a number.");
            }
        });

        formCard.add(formTitle);
        formCard.add(spacer(10));
        formCard.add(fieldGroup("Code",         fCode));
        formCard.add(spacer(8));
        formCard.add(fieldGroup("Name",         fName));
        formCard.add(spacer(8));
        formCard.add(fieldGroup("Surface (ha)", fSurface));
        formCard.add(spacer(8));
        formCard.add(fieldGroup("Type",         fType));
        formCard.add(spacer(12));
        formCard.add(btnAdd);

        JPanel leftPanel = bgPanel(new BorderLayout(0, 12));
        leftPanel.setPreferredSize(new Dimension(300, 0));
        leftPanel.add(tableCard("Zones", styledScroll(zoneList)), BorderLayout.CENTER);
        leftPanel.add(formCard, BorderLayout.SOUTH);

        // ── Right: details + actions ──────────────────────────────────────
        JTextArea detailArea = new JTextArea("Select a zone to view details.");
        detailArea.setEditable(false);
        detailArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        detailArea.setBackground(BG_PRIMARY);
        detailArea.setForeground(TEXT_PRIMARY);
        detailArea.setBorder(new EmptyBorder(14, 16, 14, 16));

        zoneList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                GeographicalZone z = zoneList.getSelectedValue();
                if (z != null) detailArea.setText(buildZoneDetail(z));
            }
        });

        // Action buttons
        JButton btnToggle = secondaryButton("Suspend / Activate");
        JButton btnSensor = secondaryButton("View sensors");
        JButton btnProd   = secondaryButton("Add production record");
        JButton btnDelete = dangerButton("Delete zone");

        btnToggle.addActionListener(e -> {
            GeographicalZone z = zoneList.getSelectedValue();
            if (z != null) {
                if (z.getStatus() == ZoneStatus.ACTIVE) z.suspend();
                else z.reactivate();
                zoneList.repaint();
                detailArea.setText(buildZoneDetail(z));
                refreshDashboard();
            }
        });

        btnDelete.addActionListener(e -> {
            GeographicalZone z = zoneList.getSelectedValue();
            if (z != null && JOptionPane.showConfirmDialog(this,
                    "Delete zone" + z.getName() + "?", "Confirm deletion",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                zones.remove(z);
                zoneListModel.removeElement(z);
                detailArea.setText("Select a zone to view details.");
                refreshDashboard();
            }
        });

        btnSensor.addActionListener(e -> {
            GeographicalZone z = zoneList.getSelectedValue();
            if (z == null) return;
            if (z.sensors.isEmpty()) {
                showInfo("No sensors assigned to this zone yet."); return;
            }
            for (Sensor s : z.sensors) {
                if (s instanceof NumericalSensor)
                    new SensorCardGUI((NumericalSensor) s).setVisible(true);
            }
        });

        btnProd.addActionListener(e -> {
            GeographicalZone z = zoneList.getSelectedValue();
            if (z == null) return;
            showAddProductionDialog(z);
            detailArea.setText(buildZoneDetail(z));
        });

        JPanel actionRow = bgPanel(new GridLayout(2, 2, 8, 8));
        actionRow.add(btnToggle);
        actionRow.add(btnSensor);
        actionRow.add(btnProd);
        actionRow.add(btnDelete);

        JPanel rightCard = whiteCard();
        rightCard.setLayout(new BorderLayout(0, 12));
        rightCard.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel detailHeader = new JPanel(new BorderLayout());
        detailHeader.setBackground(BG_SECONDARY);
        detailHeader.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(10, 16, 10, 16)));
        JLabel detailTitle = new JLabel("Zone details");
        detailTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        detailTitle.setForeground(TEXT_PRIMARY);
        detailHeader.add(detailTitle, BorderLayout.WEST);

        JPanel actionPad = bgPanel(new BorderLayout());
        actionPad.setBorder(new EmptyBorder(0, 16, 14, 16));
        actionPad.add(actionRow, BorderLayout.CENTER);

        rightCard.add(detailHeader,         BorderLayout.NORTH);
        rightCard.add(styledScroll(detailArea), BorderLayout.CENTER);
        rightCard.add(actionPad,            BorderLayout.SOUTH);

        body.add(leftPanel, BorderLayout.WEST);
        body.add(rightCard, BorderLayout.CENTER);
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private String buildZoneDetail(GeographicalZone z) {
        StringBuilder sb = new StringBuilder();
        sb.append("Code      : ").append(z.getCode()).append("\n");
        sb.append("Name      : ").append(z.getName()).append("\n");
        sb.append("Type      : ").append(zoneTypeName(z)).append("\n");
        sb.append("Status    : ").append(z.getStatus()).append("\n");
        sb.append("Entities  : ").append(z.getEntityCount()).append("\n");
        sb.append("Sensors   : ").append(z.sensors.size()).append("\n");
        if (!z.sensors.isEmpty()) {
            sb.append("\nSensors:\n");
            for (Sensor s : z.sensors)
                sb.append("  • ").append(s.getClass().getSimpleName())
                        .append(" [#").append(s.getCode()).append("] — ")
                        .append(s.getStatus()).append("\n");
        }
        if (z instanceof LivestockZone) {
            FeedingPgm fp = ((LivestockZone<?>) z).getFeedingProgram();
            sb.append("\nFeeding Program:\n")
                    .append(fp != null ? "  " + fp : "  None assigned").append("\n");
        }
        if (z instanceof AquacultureZone) {
            FeedingPgm fp = ((AquacultureZone) z).getFeedingProgram();
            sb.append("\nFeeding Program:\n")
                    .append(fp != null ? "  " + fp : "  None assigned").append("\n");
        }
        List<ProductionRecord> history = z.getProductionHistory();
        sb.append("\nProduction records: ").append(history.size()).append("\n");
        int show = Math.min(history.size(), 3);
        for (int i = history.size() - show; i < history.size(); i++) {
            ProductionRecord pr = history.get(i);
            sb.append("  • ").append(pr.getRecordDate())
                    .append(" — ").append(pr.getProductionValue())
                    .append(" ").append(pr.getUnit()).append("\n");
        }
        return sb.toString();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  3. SENSOR MONITOR PANEL
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildSensorPanel() {
        JPanel panel = bgPanel(new BorderLayout());
        panel.add(buildTopBar("Sensor monitor",
                "View and control all sensors across all zones",
                "Refresh", null, null), BorderLayout.NORTH);

        JPanel body = bgPanel(new BorderLayout(0, 0));
        body.setBorder(new EmptyBorder(20, 24, 20, 24));

        String[] cols = {"Sensor #", "Zone", "Class", "Type",
                "Status", "Min", "Max", "Actions"};
        DefaultTableModel tm = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 7; }
        };
        JTable tbl = buildStyledTable(tm);
        tbl.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        tbl.getColumn("Actions").setCellEditor(
                new ButtonEditor(new JCheckBox(), tm, tbl, this));
        tbl.getColumn("Actions").setPreferredWidth(110);

        // Refresh trigger
        JButton refreshBtn = tbl.getParent() == null ? secondaryButton("Refresh") : secondaryButton("Refresh");
        refreshBtn.addActionListener(e -> populateSensorTable(tm));

        JPanel top = bgPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        top.setBorder(new EmptyBorder(0, 0, 12, 0));
        JButton rb = secondaryButton("Refresh");
        rb.addActionListener(e -> populateSensorTable(tm));
        top.add(rb);

        populateSensorTable(tm);
        body.add(top,              BorderLayout.NORTH);
        body.add(styledScroll(tbl), BorderLayout.CENTER);
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private void populateSensorTable(DefaultTableModel tm) {
        tm.setRowCount(0);
        for (GeographicalZone z : zones) {
            for (Sensor s : z.sensors) {
                String type = "—", min = "—", max = "—";
                if (s instanceof NumericalSensor) {
                    NumericalSensor ns = (NumericalSensor) s;
                    type = ns.getTypeAsString();
                    min  = String.format("%.1f", ns.getMinThreshold());
                    max  = String.format("%.1f", ns.getMaxThreshold());
                } else if (s instanceof GPSCollar) {
                    type = "GPS";
                }
                tm.addRow(new Object[]{
                        "#" + s.getCode(),
                        z.getCode() + " – " + z.getName(),
                        s.getClass().getSimpleName(),
                        type, s.getStatus(), min, max, "View card"
                });
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  4. FEEDING PROGRAM PANEL
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildFeedingPanel() {
        JPanel panel = bgPanel(new BorderLayout());
        panel.add(buildTopBar("Feeding programs",
                "Manage feeding schedules for livestock and aquaculture zones",
                null, null, null), BorderLayout.NORTH);

        JPanel body = bgPanel(new BorderLayout(16, 0));
        body.setBorder(new EmptyBorder(20, 24, 20, 24));

        String[] cols = {"Zone", "Zone type", "Food type",
                "Qty/meal (kg)", "Meals/day", "Daily total (kg)"};
        DefaultTableModel tm = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = buildStyledTable(tm);

        Runnable refresh = () -> {
            tm.setRowCount(0);
            for (GeographicalZone z : zones) {
                FeedingPgm fp = null;
                if (z instanceof LivestockZone)   fp = ((LivestockZone<?>) z).getFeedingProgram();
                if (z instanceof AquacultureZone) fp = ((AquacultureZone) z).getFeedingProgram();
                if (fp != null) {
                    tm.addRow(new Object[]{
                            z.getName(), zoneTypeName(z),
                            fp.getFoodType(),
                            String.format("%.2f", fp.getQttPerMeal()),
                            "3",
                            String.format("%.2f", fp.calculateDailyRequirement())
                    });
                }
            }
        };
        refresh.run();

        // ── Assign form ────────────────────────────────────────────────────
        JPanel formCard = whiteCard();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(new EmptyBorder(16, 16, 16, 16));
        formCard.setPreferredSize(new Dimension(270, 0));

        JLabel ft = new JLabel("Assign / update program");
        ft.setFont(new Font("Segoe UI", Font.BOLD, 13));
        ft.setForeground(TEXT_PRIMARY);
        ft.setAlignmentX(LEFT_ALIGNMENT);

        JComboBox<GeographicalZone> zoneCombo = new JComboBox<>();
        for (GeographicalZone z : zones)
            if (z instanceof LivestockZone || z instanceof AquacultureZone)
                zoneCombo.addItem(z);
        zoneCombo.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> l, Object v, int i, boolean sel, boolean foc) {
                super.getListCellRendererComponent(l, v, i, sel, foc);
                if (v instanceof GeographicalZone)
                    setText(((GeographicalZone) v).getName());
                return this;
            }
        });

        JTextField fId    = plainField(14);
        fId.setText("FP-" + (int)(Math.random() * 900 + 100));
        JTextField fFood  = plainField(14);
        fFood.setText("Hay & Grain");
        JTextField fQty   = plainField(14);
        fQty.setText("5.0");
        JTextField fMeals = plainField(14);
        fMeals.setText("3");

        JButton btnAssign = primaryButton("Assign program");
        btnAssign.setAlignmentX(LEFT_ALIGNMENT);
        btnAssign.addActionListener(e -> {
            GeographicalZone z = (GeographicalZone) zoneCombo.getSelectedItem();
            if (z == null) return;
            try {
                FeedingPgm fp = new FeedingPgm(
                        fId.getText().trim(), fFood.getText().trim(),
                        Double.parseDouble(fQty.getText().trim()),
                        Integer.parseInt(fMeals.getText().trim()));
                if (z instanceof LivestockZone)
                    ((LivestockZone<?>) z).setFeedingProgram(fp);
                else if (z instanceof AquacultureZone)
                    ((AquacultureZone) z).setFeedingProgram(fp);
                refresh.run();
                showInfo("Program assigned to " + z.getName() + ".");
            } catch (Exception ex) {
                showError("Error: " + ex.getMessage());
            }
        });

        formCard.add(ft);
        formCard.add(spacer(12));
        formCard.add(fieldGroup("Zone",        zoneCombo));
        formCard.add(spacer(8));
        formCard.add(fieldGroup("Program ID",  fId));
        formCard.add(spacer(8));
        formCard.add(fieldGroup("Food type",   fFood));
        formCard.add(spacer(8));
        formCard.add(fieldGroup("Qty / meal",  fQty));
        formCard.add(spacer(8));
        formCard.add(fieldGroup("Meals / day", fMeals));
        formCard.add(spacer(14));
        formCard.add(btnAssign);

        body.add(styledScroll(tbl), BorderLayout.CENTER);
        body.add(formCard,          BorderLayout.EAST);
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  5. PRODUCTION PANEL
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildProductionPanel() {
        JPanel panel = bgPanel(new BorderLayout());
        panel.add(buildTopBar("Production records",
                "Track harvest, milk, egg and aquaculture yields by zone",
                null, "Add record", null), BorderLayout.NORTH);

        JPanel body = bgPanel(new BorderLayout(0, 0));
        body.setBorder(new EmptyBorder(20, 24, 20, 24));

        String[] cols = {"Zone", "Type", "Date", "Value", "Unit", "Status"};
        DefaultTableModel tm = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = buildStyledTable(tm);
        tbl.setDefaultRenderer(Object.class, new ProductionTableRenderer());

        tbl.addHierarchyListener(ev -> {
            if ((ev.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0
                    && tbl.isShowing())
                refreshProductionTable(tm);
        });

        JButton btnAdd     = primaryButton("Add record");
        JButton btnRefresh = secondaryButton("Refresh");
        btnAdd.addActionListener(e -> {
            GeographicalZone[] arr = zones.toArray(new GeographicalZone[0]);
            if (arr.length == 0) { showInfo("No zones available."); return; }
            GeographicalZone chosen = (GeographicalZone)
                    JOptionPane.showInputDialog(this, "Select zone:",
                            "Add production", JOptionPane.PLAIN_MESSAGE,
                            null, arr, arr[0]);
            if (chosen != null) {
                showAddProductionDialog(chosen);
                refreshProductionTable(tm);
            }
        });
        btnRefresh.addActionListener(e -> refreshProductionTable(tm));

        JPanel top = bgPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        top.setBorder(new EmptyBorder(0, 0, 12, 0));
        top.add(btnRefresh);
        top.add(btnAdd);

        refreshProductionTable(tm);
        body.add(top,              BorderLayout.NORTH);
        body.add(styledScroll(tbl), BorderLayout.CENTER);
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private void refreshProductionTable(DefaultTableModel tm) {
        tm.setRowCount(0);
        for (GeographicalZone z : zones) {
            for (ProductionRecord pr : z.getProductionHistory()) {
                String type = pr.getClass().getSimpleName()
                        .replace("ProductionRecord", "");
                boolean low = pr.getProductionValue() < pr.getExpectedMinimumThreshold();
                tm.addRow(new Object[]{
                        z.getName(), type, pr.getRecordDate(),
                        String.format("%.2f", pr.getProductionValue()),
                        pr.getUnit(),
                        low ? "Below threshold" : "Normal"
                });
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  6. ALERTS CONSOLE
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildAlertPanel() {
        JPanel panel = bgPanel(new BorderLayout());
        panel.add(buildTopBar("Alerts console",
                "Monitor, acknowledge and review all system alerts",
                null, null, null), BorderLayout.NORTH);

        JPanel body = bgPanel(new BorderLayout(16, 0));
        body.setBorder(new EmptyBorder(20, 24, 20, 24));

        JList<Alert> alertList = new JList<>(alertListModel);
        alertList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        alertList.setFixedCellHeight(56);
        alertList.setCellRenderer(new AlertCellRenderer());
        alertList.setBackground(BG_PRIMARY);

        JPanel listCard = tableCard("Active alerts", styledScroll(alertList));
        listCard.setPreferredSize(new Dimension(480, 0));

        // Detail
        JTextArea detail = new JTextArea("Select an alert to view details.");
        detail.setEditable(false);
        detail.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        detail.setBackground(BG_SECONDARY);
        detail.setForeground(TEXT_PRIMARY);
        detail.setBorder(new EmptyBorder(14, 16, 14, 16));

        alertList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Alert a = alertList.getSelectedValue();
                detail.setText(a != null ? a.toString() : "Select an alert.");
            }
        });

        JButton btnAck = primaryButton("Acknowledge");
        JButton btnDel = dangerButton("Remove");

        btnAck.addActionListener(e -> {
            Alert a = alertList.getSelectedValue();
            if (a != null) {
                alertSystem.acknowledgeAlert(a.getId());
                alertListModel.removeElement(a);
                detail.setText("Alert acknowledged.");
                refreshDashboard();
            }
        });
        btnDel.addActionListener(e -> {
            Alert a = alertList.getSelectedValue();
            if (a != null) {
                alertListModel.removeElement(a);
                detail.setText("Alert removed.");
                refreshDashboard();
            }
        });

        JPanel btnRow = bgPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBorder(new EmptyBorder(12, 0, 0, 0));
        btnRow.add(btnAck);
        btnRow.add(btnDel);

        JPanel right = bgPanel(new BorderLayout(0, 0));
        right.add(styledScroll(detail), BorderLayout.CENTER);
        right.add(btnRow,               BorderLayout.SOUTH);

        body.add(listCard, BorderLayout.WEST);
        body.add(right,    BorderLayout.CENTER);
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  7. SIMULATION PANEL
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildSimulationPanel() {
        JPanel panel = bgPanel(new BorderLayout());
        panel.add(buildTopBar("Simulation mode",
                "Generate test data and simulate farm activity",
                null, null, null), BorderLayout.NORTH);

        JPanel body = bgPanel(new BorderLayout(0, 16));
        body.setBorder(new EmptyBorder(20, 24, 24, 24));

        // Log
        JTextArea log = new JTextArea();
        log.setEditable(false);
        log.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        log.setBackground(new Color(0x18, 0x18, 0x16));
        log.setForeground(new Color(0x4A, 0xDE, 0x80));
        log.setBorder(new EmptyBorder(14, 16, 14, 16));
        JScrollPane logScroll = new JScrollPane(log);
        logScroll.setBorder(new LineBorder(new Color(0x2A, 0x2A, 0x28), 1, true));

        // Status
        simStatusLabel = new JLabel("Simulation stopped");
        simStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        simStatusLabel.setForeground(TEXT_TERTIARY);

        // Buttons
        JButton btnGenZones  = secondaryButton("Generate demo zones");
        JButton btnSimAlerts = secondaryButton("Simulate alerts");
        JButton btnSimProd   = secondaryButton("Simulate production");
        JButton btnSimFeed   = secondaryButton("Simulate feeding");
        JButton btnLiveStart = primaryButton("Start live simulation");
        JButton btnLiveStop  = dangerButton("Stop live simulation");

        btnGenZones.addActionListener(e -> {
            loadDemoData();
            log.append("[GEN] Demo zones generated. Total: " + zones.size() + "\n");
            refreshDashboard();
        });

        btnSimAlerts.addActionListener(e -> {
            if (zones.isEmpty()) { log.append("[WARN] No zones. Generate data first.\n"); return; }
            Random rng = new Random();
            int count = rng.nextInt(3) + 1;
            for (int i = 0; i < count; i++) {
                AlertLevel level = rng.nextBoolean() ? AlertLevel.CRITICAL : AlertLevel.WARNING;
                int sCode = rng.nextInt(200) + 1;
                double val = 30 + rng.nextDouble() * 20;
                alertSystem.triggerAlert(sCode, val, level);
                Alert a = alertSystem.getLatestAlert();
                alertListModel.addElement(a);
                log.append("[ALERT] " + a.getId() + " | " + level
                        + " | Sensor #" + sCode
                        + " | Value: " + String.format("%.1f", val) + "\n");
            }
            refreshDashboard();
        });

        btnSimProd.addActionListener(e -> {
            if (zones.isEmpty()) { log.append("[WARN] No zones. Generate data first.\n"); return; }
            Random rng = new Random();
            for (GeographicalZone z : zones) {
                ProductionRecord pr = null;
                double val = 50 + rng.nextDouble() * 100;
                if (z instanceof LivestockZone)
                    pr = new MilkProductionRecord(z.getCode(), val, 40.0);
                else if (z instanceof CropZone)
                    pr = new HarvestProductionRecord(z.getCode(), val, 30.0);
                else if (z instanceof AquacultureZone)
                    pr = new HarvestProductionRecord(z.getCode(), val, 20.0);
                if (pr != null) {
                    z.addProductionRecord(pr);
                    log.append("[PROD] Zone " + z.getCode() + " → "
                            + String.format("%.1f", val) + " " + pr.getUnit() + "\n");
                }
            }
        });

        btnSimFeed.addActionListener(e -> {
            String[] foods = {"Hay", "Grain Mix", "Fish Pellets", "Vegetable Waste"};
            Random rng = new Random();
            for (GeographicalZone z : zones) {
                FeedingPgm fp = new FeedingPgm("FP-SIM",
                        foods[rng.nextInt(foods.length)],
                        2.5 + rng.nextDouble() * 7.5,
                        rng.nextInt(3) + 2);
                if (z instanceof LivestockZone)
                    ((LivestockZone<?>) z).setFeedingProgram(fp);
                else if (z instanceof AquacultureZone)
                    ((AquacultureZone) z).setFeedingProgram(fp);
                log.append("[FEED] Zone " + z.getCode() + " → " + fp.getFoodType()
                        + " | Daily: " + String.format("%.1f",
                        fp.calculateDailyRequirement()) + " kg\n");
            }
        });

        btnLiveStart.addActionListener(e -> {
            if (simRunning) return;
            simRunning = true;
            simStatusLabel.setForeground(TEXT_SUCCESS);
            simStatusLabel.setText("Simulation running");
            simTimer = new javax.swing.Timer(2500, ev -> {
                if (!simRunning) return;
                Random rng = new Random();
                if (!zones.isEmpty() && rng.nextDouble() < 0.4) {
                    AlertLevel level = rng.nextDouble() < 0.3
                            ? AlertLevel.CRITICAL : AlertLevel.WARNING;
                    int sCode = rng.nextInt(100) + 1;
                    double val = 30 + rng.nextDouble() * 30;
                    alertSystem.triggerAlert(sCode, val, level);
                    Alert a = alertSystem.getLatestAlert();
                    alertListModel.addElement(a);
                    log.append("[LIVE] Alert " + a.getId() + " → " + level + "\n");
                    refreshDashboard();
                }
                log.setCaretPosition(log.getDocument().getLength());
            });
            simTimer.start();
            log.append("[SIM] Live simulation started.\n");
        });

        btnLiveStop.addActionListener(e -> {
            if (!simRunning) return;
            simRunning = false;
            if (simTimer != null) simTimer.stop();
            simStatusLabel.setForeground(TEXT_TERTIARY);
            simStatusLabel.setText("Simulation stopped");
            log.append("[SIM] Live simulation stopped.\n");
        });

        JPanel grid = bgPanel(new GridLayout(2, 3, 12, 12));
        grid.setPreferredSize(new Dimension(0, 100));
        grid.add(btnGenZones);
        grid.add(btnSimAlerts);
        grid.add(btnSimProd);
        grid.add(btnSimFeed);
        grid.add(btnLiveStart);
        grid.add(btnLiveStop);

        JPanel top = bgPanel(new BorderLayout(0, 8));
        top.add(grid,          BorderLayout.CENTER);
        top.add(simStatusLabel, BorderLayout.SOUTH);

        body.add(top,        BorderLayout.NORTH);
        body.add(logScroll,  BorderLayout.CENTER);
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  DIALOGS
    // ══════════════════════════════════════════════════════════════════════════
    private void showAddProductionDialog(GeographicalZone z) {
        String[] types;
        if (z instanceof CropZone)
            types = new String[]{"Harvest (kg)"};
        else if (z instanceof LivestockZone)
            types = new String[]{"Milk (L)", "Eggs"};
        else
            types = new String[]{"Aquaculture Harvest (kg)"};

        JComboBox<String> typeCombo   = new JComboBox<>(types);
        JTextField valField           = new JTextField("100.0");
        JTextField threshField        = new JTextField("50.0");

        Object[] msg = {
                "Production type:", typeCombo,
                "Value:",           valField,
                "Min threshold:",   threshField
        };
        int result = JOptionPane.showConfirmDialog(this, msg,
                "Add production record — " + z.getName(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                double val    = Double.parseDouble(valField.getText().trim());
                double thresh = Double.parseDouble(threshField.getText().trim());
                String sel    = (String) typeCombo.getSelectedItem();

                ProductionRecord pr;
                if (sel != null && sel.startsWith("Milk"))
                    pr = new MilkProductionRecord(z.getCode(), val, thresh);
                else if (sel != null && sel.startsWith("Eggs"))
                    pr = new EggProductionRecord(z.getCode(), (int) val, thresh);
                else
                    pr = new HarvestProductionRecord(z.getCode(), val, thresh);

                z.addProductionRecord(pr);
                if (val < thresh) {
                    alertSystem.triggerAlert(-1, val, AlertLevel.WARNING);
                    alertListModel.addElement(alertSystem.getLatestAlert());
                    refreshDashboard();
                }
                showInfo("Record added successfully.");
            } catch (NumberFormatException ex) {
                showError("Invalid number input.");
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  DEMO DATA LOADER
    // ══════════════════════════════════════════════════════════════════════════
    private void loadDemoData() {
        if (!zones.isEmpty()) return;
        try {
            // Zone 1: Crop
            CropZone crop1 = new CropZone("Z01", "North Wheat Field",
                    ZoneStatus.ACTIVE, 150.0);
            EnvironmentalSensor envTemp = new EnvironmentalSensor(
                    54, 10, EnvironmentalType.TEMPERATURE, 15.0, 35.0);
            SoilSensor soilPH = new SoilSensor(24, 10, SoilType.PH, 5.5, 7.5);
            for (int h = 0; h < 24; h++) {
                envTemp.addReading(h, 18 + h * 0.7);
                soilPH.addReading(h, 6.2 + h * 0.03);
            }
            crop1.addEnvironmentalSensor(envTemp);
            crop1.addSoilSensor(soilPH);
            crop1.addCrop(new Crop("WHEAT-01", 7.5, 5.5, 50.0, 80.0,
                    LocalDate.of(2026, 3, 1), LocalDate.of(2026, 8, 15),
                    GrowthStatus.CROISSANCE, CropFamily.CEREAL));
            crop1.addProductionRecord(
                    new HarvestProductionRecord("Z01", 980.0, 800.0));
            addZone(crop1);

            // Zone 2: Livestock (cattle)
            FeedingPgm cowFeed = new FeedingPgm("FP-01", "Hay & Silage", 8.0, 3);
            LivestockZone<Ruminant> livestock1 = new LivestockZone<>(
                    "Z02", "South Cattle Paddock", ZoneStatus.ACTIVE, 80.0, cowFeed);
            BiometricSensor bioTemp = new BiometricSensor(
                    50, 11, BiometricType.TEMPERATURE, 37.5, 39.5);
            for (int h = 0; h < 24; h++)
                bioTemp.addReading(h, 38.2 + (h % 5) * 0.1);
            GPSCollar gps = new GPSCollar(51, 11, 36.5, 37.0, 2.8, 3.5);
            livestock1.addBiometricSensor(bioTemp);
            livestock1.addGPSSensor(gps);
            livestock1.addAnimal(new Ruminant(
                    "R001", "Cow", 4, 650.0, HealthStatus.HEALTHY));
            livestock1.addAnimal(new Ruminant(
                    "R002", "Cow", 3, 580.0, HealthStatus.HEALTHY));
            livestock1.addProductionRecord(
                    new MilkProductionRecord("Z02", 120.0, 100.0));
            addZone(livestock1);

            // Zone 3: Aquaculture
            FeedingPgm fishFeed = new FeedingPgm("FP-02", "Fish Pellets", 2.0, 4);
            AquacultureZone aqua1 = new AquacultureZone(
                    "Z03", "East Tilapia Basin", ZoneStatus.ACTIVE, 40.0,
                    new Aquaculture("AQ-01", "Tilapia", 800, 26.0, 8.5, 7.2),
                    fishFeed);
            WaterSensor waterO2 = new WaterSensor(
                    114, 12, WaterType.DISSOLVED_O2, 6.0, 12.0);
            for (int h = 0; h < 24; h++)
                waterO2.addReading(h, 8.0 + (h % 6) * 0.3);
            aqua1.addWaterSensor(waterO2);
            aqua1.addProductionRecord(
                    new HarvestProductionRecord("Z03", 340.0, 300.0));
            addZone(aqua1);

            // Zone 4: Livestock (poultry)
            FeedingPgm henFeed = new FeedingPgm("FP-03", "Layer Mash", 0.12, 2);
            LivestockZone<Poultry> poultry1 = new LivestockZone<>(
                    "Z04", "West Hen House", ZoneStatus.ACTIVE, 12.0, henFeed);
            poultry1.addAnimal(new Poultry("P001", "Hen", 1, 2.0, HealthStatus.HEALTHY));
            poultry1.addAnimal(new Poultry("P002", "Hen", 1, 1.9, HealthStatus.HEALTHY));
            poultry1.addAnimal(new Poultry("P003", "Hen", 2, 2.1, HealthStatus.SICK));
            poultry1.addProductionRecord(new EggProductionRecord("Z04", 240, 200));
            addZone(poultry1);

            // Zone 5: Suspended crop
            addZone(new CropZone("Z05", "Fallow Tomato Plot",
                    ZoneStatus.SUSPENDED, 30.0));

            // Seed one alert
            alertSystem.triggerAlert(54, 38.5, AlertLevel.WARNING);
            alertListModel.addElement(alertSystem.getLatestAlert());

        } catch (ThresholdException e) {
            System.err.println("Demo data sensor config error: " + e.getMessage());
        }
        refreshDashboard();
    }

    private void addZone(GeographicalZone z) {
        zones.add(z);
        zoneListModel.addElement(z);
    }

    private GeographicalZone createZone(String type, String code,
                                        String name, double surface) {
        switch (type) {
            case "Livestock":
                return new LivestockZone<>(code, name, ZoneStatus.ACTIVE, surface, null);
            case "Aquaculture":
                return new AquacultureZone(code, name, ZoneStatus.ACTIVE, surface, null, null);
            default:
                return new CropZone(code, name, ZoneStatus.ACTIVE, surface);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CELL RENDERERS
    // ══════════════════════════════════════════════════════════════════════════
    private class ZoneCellRenderer extends DefaultListCellRenderer {
        @Override public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean sel, boolean foc) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(
                    list, value, index, sel, foc);
            if (value instanceof GeographicalZone) {
                GeographicalZone z = (GeographicalZone) value;
                String icon = z instanceof CropZone ? "🌾"
                        : z instanceof LivestockZone ? "🐄"
                        : z instanceof AquacultureZone ? "🐟" : "📍";
                lbl.setText("  " + icon + "  " + z.getName()
                        + "  [" + z.getCode() + "]");
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                if (!sel) {
                    boolean active = z.getStatus() == ZoneStatus.ACTIVE;
                    lbl.setForeground(active ? TEXT_PRIMARY : TEXT_TERTIARY);
                    lbl.setBackground(index % 2 == 0 ? BG_PRIMARY : BG_SECONDARY);
                }
            }
            return lbl;
        }
    }

    private class AlertCellRenderer extends DefaultListCellRenderer {
        @Override public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean sel, boolean foc) {
            if (!(value instanceof Alert)) return super.getListCellRendererComponent(
                    list, value, index, sel, foc);

            Alert a = (Alert) value;
            boolean crit = a.getLevel() == AlertLevel.CRITICAL;

            JPanel item = new JPanel(new BorderLayout(10, 0));
            item.setBorder(new CompoundBorder(
                    new MatteBorder(0, 0, 1, 0, BORDER),
                    new EmptyBorder(10, 14, 10, 14)));
            item.setBackground(sel ? BG_SECONDARY
                    : (crit ? new Color(0xFC, 0xEB, 0xEB) : BG_PRIMARY));

            // Colored dot
            JPanel dot = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(crit ? TEXT_DANGER : TEXT_WARNING);
                    g2.fillOval(0, 5, 8, 8);
                }
            };
            dot.setOpaque(false);
            dot.setPreferredSize(new Dimension(8, 18));

            JPanel textCol = new JPanel();
            textCol.setOpaque(false);
            textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));

            JLabel id = new JLabel(a.getId());
            id.setFont(new Font("Segoe UI", Font.BOLD, 12));
            id.setForeground(TEXT_PRIMARY);

            JLabel meta = new JLabel("Sensor #" + a.getSensorCode()
                    + "  ·  value " + String.format("%.1f", a.getReadingValue()));
            meta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            meta.setForeground(TEXT_TERTIARY);

            textCol.add(id);
            textCol.add(meta);

            // Badge
            JLabel badge = new JLabel(crit ? "Critical" : "Warning");
            badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
            badge.setForeground(crit ? TEXT_DANGER : TEXT_WARNING);
            badge.setBackground(crit ? BG_DANGER : BG_WARNING);
            badge.setOpaque(true);
            badge.setBorder(new EmptyBorder(2, 7, 2, 7));

            item.add(dot,    BorderLayout.WEST);
            item.add(textCol, BorderLayout.CENTER);
            item.add(badge,  BorderLayout.EAST);
            return item;
        }
    }

    private class ProductionTableRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(
                JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            Component comp = super.getTableCellRendererComponent(
                    t, v, sel, foc, r, c);
            if (!sel && c == 5) {
                String val = v != null ? v.toString() : "";
                boolean low = val.startsWith("Below");
                comp.setForeground(low ? TEXT_DANGER : TEXT_SUCCESS);
                if (comp instanceof JLabel)
                    ((JLabel) comp).setFont(new Font("Segoe UI", Font.BOLD, 12));
            } else if (!sel) {
                comp.setForeground(TEXT_PRIMARY);
            }
            return comp;
        }
    }

    private static class ButtonRenderer extends JButton
            implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.PLAIN, 11));
            setBackground(new Color(0xF0, 0xF0, 0xED));
            setForeground(new Color(0x1A, 0x1A, 0x18));
            setBorderPainted(false);
            setBorder(new EmptyBorder(4, 10, 4, 10));
        }
        @Override public Component getTableCellRendererComponent(
                JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            setText(v != null ? v.toString() : "View");
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private final DefaultTableModel tm;
        private final JTable            table;
        private final Farm              farm;

        public ButtonEditor(JCheckBox cb, DefaultTableModel tm,
                            JTable table, Farm farm) {
            super(cb);
            this.tm    = tm;
            this.table = table;
            this.farm  = farm;
        }

        @Override public Component getTableCellEditorComponent(
                JTable t, Object v, boolean sel, int row, int col) {
            JButton btn = new JButton("View card");
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btn.setBackground(BG_SECONDARY);
            btn.setForeground(TEXT_PRIMARY);
            btn.setBorderPainted(false);
            btn.setBorder(new EmptyBorder(4, 10, 4, 10));
            btn.addActionListener(e -> {
                String codeStr = tm.getValueAt(row, 0).toString().replace("#", "");
                int code = Integer.parseInt(codeStr);
                for (GeographicalZone z : zones) {
                    for (Sensor s : z.sensors) {
                        if (s.getCode() == code && s instanceof NumericalSensor) {
                            new SensorCardGUI((NumericalSensor) s).setVisible(true);
                            stopCellEditing();
                            return;
                        }
                    }
                }
                showInfo("Sensor card only available for numerical sensors.");
                stopCellEditing();
            });
            return btn;
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  UI UTILITY METHODS
    // ══════════════════════════════════════════════════════════════════════════

    /** Top bar with title, subtitle and optional action buttons. */
    private JPanel buildTopBar(String title, String subtitle,
                               String secondaryLabel, String primaryLabel,
                               ActionListener primaryAction) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_PRIMARY);
        bar.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(14, 24, 14, 24)));

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 15));
        t.setForeground(TEXT_PRIMARY);
        JLabel s = new JLabel(subtitle);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        s.setForeground(TEXT_TERTIARY);
        titles.add(t);
        titles.add(s);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        if (secondaryLabel != null) {
            actions.add(secondaryButton(secondaryLabel));
        }
        if (primaryLabel != null) {
            JButton pb = primaryButton(primaryLabel);
            if (primaryAction != null) pb.addActionListener(primaryAction);
            actions.add(pb);
        }

        bar.add(titles,  BorderLayout.WEST);
        bar.add(actions, BorderLayout.EAST);
        return bar;
    }

    /** White card with header + scrollable body. */
    private JPanel tableCard(String headerTitle, JScrollPane content) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_PRIMARY);
        card.setBorder(new LineBorder(BORDER, 1, true));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_SECONDARY);
        header.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(9, 14, 9, 14)));
        JLabel ht = new JLabel(headerTitle);
        ht.setFont(new Font("Segoe UI", Font.BOLD, 12));
        ht.setForeground(TEXT_PRIMARY);
        header.add(ht, BorderLayout.WEST);

        card.add(header,  BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel statCard(String label, JLabel valueLabel, JLabel subLabel) {
        JPanel card = new JPanel();
        card.setBackground(BG_SECONDARY);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT_TERTIARY);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        valueLabel.setAlignmentX(LEFT_ALIGNMENT);
        subLabel.setAlignmentX(LEFT_ALIGNMENT);

        card.add(lbl);
        card.add(spacer(6));
        card.add(valueLabel);
        card.add(spacer(4));
        card.add(subLabel);
        return card;
    }

    private JLabel metricLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 26));
        l.setForeground(TEXT_PRIMARY);
        return l;
    }

    private JLabel metricSub(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(TEXT_TERTIARY);
        return l;
    }

    private JPanel bgPanel(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(BG_TERTIARY);
        return p;
    }

    private JPanel whiteCard() {
        JPanel p = new JPanel();
        p.setBackground(BG_PRIMARY);
        p.setBorder(new LineBorder(BORDER, 1, true));
        return p;
    }

    private JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(TEXT_SUCCESS);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 16, 7, 16));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(0x27, 0x50, 0x0A));
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(TEXT_SUCCESS);
            }
        });
        return btn;
    }

    private JButton secondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(BG_PRIMARY);
        btn.setForeground(TEXT_SECONDARY);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(6, 14, 6, 14)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(BG_SECONDARY);
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(BG_PRIMARY);
            }
        });
        return btn;
    }

    private JButton dangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(BG_PRIMARY);
        btn.setForeground(TEXT_DANGER);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
                new LineBorder(new Color(0xF0, 0x95, 0x95), 1, true),
                new EmptyBorder(6, 14, 6, 14)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(BG_DANGER);
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(BG_PRIMARY);
            }
        });
        return btn;
    }

    private JTable buildStyledTable(DefaultTableModel tm) {
        JTable t = new JTable(tm);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setRowHeight(34);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        t.setGridColor(new Color(0x00, 0x00, 0x00, 20));
        t.setSelectionBackground(BG_INFO);
        t.setSelectionForeground(TEXT_PRIMARY);
        t.setBackground(BG_PRIMARY);
        t.setForeground(TEXT_PRIMARY);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 11));
        t.getTableHeader().setBackground(BG_SECONDARY);
        t.getTableHeader().setForeground(TEXT_TERTIARY);
        t.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        t.getTableHeader().setReorderingAllowed(false);
        return t;
    }

    private JScrollPane styledScroll(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.getViewport().setBackground(BG_PRIMARY);
        return sp;
    }

    /** Label-above field group for forms. */
    private JPanel fieldGroup(String label, Component field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT_TERTIARY);
        lbl.setAlignmentX(LEFT_ALIGNMENT);

        ((JComponent) field).setAlignmentX(LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        p.add(lbl);
        p.add(Box.createRigidArea(new Dimension(0, 3)));
        p.add(field);
        return p;
    }

    private JTextField plainField(int cols) {
        JTextField f = new JTextField(cols);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setBackground(BG_SECONDARY);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(TEXT_PRIMARY);
        f.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(5, 8, 5, 8)));
        return f;
    }

    private JComboBox<String> styledCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setBackground(BG_SECONDARY);
        cb.setForeground(TEXT_PRIMARY);
        return cb;
    }

    private Component spacer(int height) {
        return Box.createRigidArea(new Dimension(0, height));
    }

    private String zoneTypeName(GeographicalZone z) {
        if (z instanceof CropZone)        return "Crop";
        if (z instanceof LivestockZone)   return "Livestock";
        if (z instanceof AquacultureZone) return "Aquaculture";
        return "Unknown";
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MAIN ENTRY POINT
    // ══════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new Farm().setVisible(true);
        });
    }
}