import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorCardGUI extends JFrame {

    // ==================== COLORS ====================
    private static final Color BLUE_DARK    = new Color(0x18, 0x5F, 0xA5);
    private static final Color BLUE_LIGHT   = new Color(0xE6, 0xF1, 0xFB);
    private static final Color BLUE_TEXT    = new Color(0x0C, 0x44, 0x7C);
    private static final Color GREEN_BG     = new Color(0xE1, 0xF5, 0xEE);
    private static final Color GREEN_TEXT   = new Color(0x08, 0x50, 0x41);
    private static final Color RED_BG       = new Color(0xFC, 0xEB, 0xEB);
    private static final Color RED_TEXT     = new Color(0x79, 0x1F, 0x1F);
    private static final Color AMBER_BG     = new Color(0xFA, 0xEE, 0xDA);
    private static final Color AMBER_TEXT   = new Color(0x63, 0x38, 0x06);
    private static final Color SURFACE      = new Color(0xF5, 0xF5, 0xF5);
    private static final Color BORDER_COLOR = new Color(0xDD, 0xDD, 0xDD);
    private static final Color TEXT_PRIMARY = new Color(0x1A, 0x1A, 0x1A);
    private static final Color TEXT_SEC     = new Color(0x6B, 0x6B, 0x6B);
    private static final Color TEXT_HINT    = new Color(0x9E, 0x9E, 0x9E);

    // ==================== SENSOR DATA ====================
    private final NumericalSensor sensor;
    private final String sensorCode;
    private final String zoneCode;
    private final String sensorType;
    private final String sensorClass;
    private final String unit;
    private final String status;
    private final double minThreshold;
    private final double maxThreshold;
    private final Reading[] todayRecord;
    private final HashMap<Date, Reading[]> history;

    // ==================== CONSTRUCTOR ====================
    public SensorCardGUI(NumericalSensor sensor) {
        this.sensor       = sensor;
        this.sensorCode   = String.valueOf(sensor.getCode());
        this.zoneCode     = String.valueOf(sensor.getZoneCode());
        this.sensorType   = sensor.getTypeAsString();
        this.sensorClass  = sensor.getClass().getSimpleName()
                .replace("Sensor", " sensor");
        this.unit         = sensor.getUnit().toString();
        this.status       = sensor.getStatus().toString();
        this.minThreshold = sensor.getMinThreshold();
        this.maxThreshold = sensor.getMaxThreshold();
        this.todayRecord  = sensor.getRecord();
        this.history      = sensor.getHistory();

        setTitle("Sensor card — " + sensorCode);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Color.WHITE);
        main.setBorder(new LineBorder(BORDER_COLOR, 1, true));

        main.add(buildHeader(), BorderLayout.NORTH);

        // REPLACE WITH this:
        JPanel body = buildBody();
        JScrollPane bodyScroll = new JScrollPane(body);
        bodyScroll.setBorder(BorderFactory.createEmptyBorder());
        bodyScroll.getVerticalScrollBar().setUnitIncrement(16);
        bodyScroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        SwingUtilities.invokeLater(() -> {
            bodyScroll.getViewport().setViewPosition(new Point(0, 0));
            bodyScroll.revalidate();
        });
        main.add(bodyScroll, BorderLayout.CENTER);

        setContentPane(main);
        setSize(700, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ==================== HEADER ====================
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BLUE_DARK);
        header.setBorder(new EmptyBorder(14, 18, 14, 18));

        // left: icon + titles
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("\uD83D\uDCE1");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        icon.setForeground(Color.WHITE);
        icon.setPreferredSize(new Dimension(36, 36));
        icon.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setOpaque(false);

        JLabel titleLbl = new JLabel(sensorClass);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLbl.setForeground(Color.WHITE);

        JLabel subLbl = new JLabel("Numerical sensor — " + sensorCode);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLbl.setForeground(new Color(181, 212, 244));

        titles.add(titleLbl);
        titles.add(subLbl);
        left.add(icon);
        left.add(titles);

        // right: status badge
        Color[] bc = badgeColors(status);
        JPanel badgeWrap = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bc[0]);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        badgeWrap.setOpaque(false);
        JLabel badge = new JLabel(status);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(bc[1]);
        badge.setOpaque(false);
        badge.setBorder(new EmptyBorder(4, 10, 4, 10));
        badgeWrap.add(badge);

        header.add(left,      BorderLayout.WEST);
        header.add(badgeWrap, BorderLayout.EAST);
        return header;
    }

    private Color[] badgeColors(String s) {
        switch (s.toUpperCase()) {
            case "ACTIVE":    return new Color[]{GREEN_BG,  GREEN_TEXT};
            case "FAULTY":    return new Color[]{RED_BG,    RED_TEXT};
            case "SUSPENDED": return new Color[]{AMBER_BG,  AMBER_TEXT};
            default:          return new Color[]{SURFACE,   TEXT_SEC};
        }
    }

    // ==================== BODY ====================
    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(18, 18, 18, 18));

        // ⬇ THIS is the key fix — tells scroll pane the real size
        body.setPreferredSize(null); // reset first

        body.add(buildInfoGrid());
        body.add(Box.createVerticalStrut(24));
        body.add(separator());
        body.add(Box.createVerticalStrut(14));
        body.add(sectionLabel("Today's readings — hour ly (" + unit + ")"));
        body.add(Box.createVerticalStrut(8));

        // chart with FIXED explicit size
        JPanel chart = buildChart();
        chart.setPreferredSize(new Dimension(600, 180));
        chart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        chart.setMinimumSize(new Dimension(300, 180)); // ← add this
        body.add(chart);

        body.add(Box.createVerticalStrut(16));
        body.add(separator());
        body.add(Box.createVerticalStrut(14));
        body.add(buildBrowseButton());
        body.add(Box.createVerticalStrut(20));

        return body;
    }

    // ==================== INFO GRID ====================
    private JPanel buildInfoGrid() {
        JPanel full = new JPanel();
        full.setLayout(new BoxLayout(full, BoxLayout.Y_AXIS));
        full.setOpaque(false);

        JPanel grid = new JPanel(new GridLayout(2, 2, 10, 10));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);

        grid.add(infoCard("Code",      sensorCode));
        grid.add(infoCard("Zone code", zoneCode));
        grid.add(infoCard("Type",      sensorType));
        grid.add(infoCard("Unit",      unit));

        full.add(grid);
        full.add(Box.createVerticalStrut(10));

        // threshold full-width card
        JPanel thresh = new JPanel(new BorderLayout(0, 4));
        thresh.setBackground(SURFACE);
        thresh.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
        thresh.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        thresh.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel("THRESHOLD");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lbl.setForeground(TEXT_HINT);

        JPanel vals = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        vals.setOpaque(false);

        JLabel minL = new JLabel("\u25bc Min: " + minThreshold + " " + unit);
        minL.setFont(new Font("Segoe UI", Font.BOLD, 13));
        minL.setForeground(BLUE_TEXT);

        JLabel pipe = new JLabel("|");
        pipe.setForeground(BORDER_COLOR);

        JLabel maxL = new JLabel("\u25b2 Max: " + maxThreshold + " " + unit);
        maxL.setFont(new Font("Segoe UI", Font.BOLD, 13));
        maxL.setForeground(RED_TEXT);

        vals.add(minL);
        vals.add(pipe);
        vals.add(maxL);

        thresh.add(lbl,  BorderLayout.NORTH);
        thresh.add(vals, BorderLayout.CENTER);

        full.add(thresh);
        return full;
    }

    private JPanel infoCard(String label, String value) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(SURFACE);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lbl.setForeground(TEXT_HINT);
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 14));
        val.setForeground(TEXT_PRIMARY);
        card.add(lbl, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }

    // ==================== BAR CHART ====================
    private JPanel buildChart() {
        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                int padL = 42, padR = 10, padT = 10, padB = 28;
                int chartW = w - padL - padR;
                int chartH = h - padT - padB;
                double dataMin = 0, dataMax = maxThreshold * 1.2;
                int n = todayRecord.length;
                double barW = (double) chartW / n;

                // grid lines + y labels
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                int steps = 5;
                for (int i = 0; i <= steps; i++) {
                    double v = dataMax * i / steps;
                    int y = padT + chartH - (int)(v / dataMax * chartH);
                    g2.setColor(new Color(220, 220, 220));
                    g2.setStroke(new BasicStroke(0.5f));
                    g2.drawLine(padL, y, padL + chartW, y);
                    g2.setColor(TEXT_HINT);
                    String label = String.format("%.0f", v);
                    g2.drawString(label, 2, y + 4);
                }

                // threshold dashed lines
                g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 10, new float[]{6, 4}, 0));
                int yMin = padT + chartH - (int)(minThreshold / dataMax * chartH);
                int yMax2 = padT + chartH - (int)(maxThreshold / dataMax * chartH);
                g2.setColor(new Color(55, 138, 221, 140));
                g2.drawLine(padL, yMin, padL + chartW, yMin);
                g2.setColor(new Color(226, 75, 74, 140));
                g2.drawLine(padL, yMax2, padL + chartW, yMax2);
                g2.setStroke(new BasicStroke(1f));

                // bars
                for (int i = 0; i < n; i++) {
                    if (todayRecord[i] == null) continue;
                    double val = todayRecord[i].getValue();
                    boolean out = todayRecord[i].isOutOfRange();
                    int barH = (int)(val / dataMax * chartH);
                    int x = padL + (int)(i * barW) + 2;
                    int y = padT + chartH - barH;
                    int bw = Math.max(1, (int) barW - 3);
                    g2.setColor(out ? new Color(226, 75, 74)
                            : new Color(55, 138, 221));
                    g2.fillRoundRect(x, y, bw, barH, 3, 3);

                    // hour label every 4h
                    if (i % 4 == 0) {
                        g2.setColor(TEXT_HINT);
                        g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                        g2.drawString(i + "h", x, h - 8);
                    }
                }

                // chart border
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(0.5f));
                g2.drawRect(padL, padT, chartW, chartH);
            }
        };
        chart.setPreferredSize(new Dimension(500, 180));
        chart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        chart.setMinimumSize(new Dimension(300, 180)); // add this line
        chart.setBackground(Color.WHITE);
        chart.setAlignmentX(Component.LEFT_ALIGNMENT);
        return chart;
    }

    // ==================== BROWSE BUTTON ====================
    private JButton buildBrowseButton() {
        JButton btn = new JButton("Browse history") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed()  ? BLUE_TEXT  :
                        getModel().isRollover() ? new Color(0x0F, 0x50, 0x8A)
                                : BLUE_DARK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addActionListener(e -> openBrowseDialog());
        return btn;
    }

    // ==================== BROWSE DIALOG ====================
    private void openBrowseDialog() {
        JDialog dialog = new JDialog(this, "Browse sensor history", true);
        dialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // title
        JLabel title = new JLabel("Browse sensor history — " + sensorCode);
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(BLUE_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(16));

        // date fields
        JPanel dateRow = new JPanel(new GridLayout(1, 2, 12, 0));
        dateRow.setOpaque(false);
        dateRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        dateRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField startField = labeledField("Start date (yyyy-mm-dd)");
        JTextField endField   = labeledField("End date (yyyy-mm-dd)");

// add labels above fields manually
        JPanel startWrap = new JPanel();
        startWrap.setLayout(new BoxLayout(startWrap, BoxLayout.Y_AXIS));
        startWrap.setOpaque(false);
        JLabel startLbl = new JLabel("Start date (yyyy-mm-dd)");
        startLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        startLbl.setForeground(TEXT_SEC);
        startWrap.add(startLbl);
        startWrap.add(Box.createVerticalStrut(4));
        startWrap.add(startField);

        JPanel endWrap = new JPanel();
        endWrap.setLayout(new BoxLayout(endWrap, BoxLayout.Y_AXIS));
        endWrap.setOpaque(false);
        JLabel endLbl = new JLabel("End date (yyyy-mm-dd)");
        endLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        endLbl.setForeground(TEXT_SEC);
        endWrap.add(endLbl);
        endWrap.add(Box.createVerticalStrut(4));
        endWrap.add(endField);

        dateRow.add(startWrap);
        dateRow.add(endWrap);
        panel.add(dateRow);
        panel.add(Box.createVerticalStrut(10));

        // error label
        JLabel errLabel = new JLabel(" ");
        errLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errLabel.setForeground(RED_TEXT);
        errLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(errLabel);
        panel.add(Box.createVerticalStrut(8));

        // search button
        JButton searchBtn = styledButton("Search", BLUE_DARK, BLUE_TEXT);
        searchBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        panel.add(searchBtn);
        panel.add(Box.createVerticalStrut(14));

        // results area
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(resultsPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setPreferredSize(new Dimension(500, 320));
        scroll.setVisible(false);
        panel.add(scroll);

        // done button
        JButton doneBtn = styledButton("Done — browse again",
                new Color(0x1D, 0x9E, 0x75),
                new Color(0x08, 0x50, 0x41));
        doneBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        doneBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        doneBtn.setVisible(false);
        panel.add(Box.createVerticalStrut(10));
        panel.add(doneBtn);

        // ---- search action ----
        searchBtn.addActionListener(e -> {
            errLabel.setText(" ");
            resultsPanel.removeAll();
            scroll.setVisible(false);
            doneBtn.setVisible(false);

            String s  = startField.getText().trim();
            String en = endField.getText().trim();

            try {
                // exceptions
                if (s.isEmpty() || en.isEmpty())
                    throw new IllegalArgumentException(
                            "Dates cannot be empty.");

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                Date sd = sdf.parse(s);
                Date ed = sdf.parse(en);

                if (sd.after(ed))
                    throw new IllegalArgumentException(
                            "Start date cannot be after end date.");

                // filter history
                List<Date> found = new ArrayList<>();
                for (Date key : history.keySet()) {
                    if (!key.before(sd) && !key.after(ed))
                        found.add(key);
                }
                Collections.sort(found);

                if (found.isEmpty())
                    throw new IllegalArgumentException(
                            "No records found in the selected date range.");

                // build day blocks
                for (Date day : found) {
                    Reading[] readings = history.get(day);
                    resultsPanel.add(buildDayBlock(day, readings));
                    resultsPanel.add(Box.createVerticalStrut(12));
                }

                scroll.setVisible(true);
                doneBtn.setVisible(true);

            } catch (ParseException ex) {
                errLabel.setText(
                        "Invalid date format. Please use yyyy-mm-dd.");
            } catch (IllegalArgumentException ex) {
                errLabel.setText(ex.getMessage());
            }

            dialog.revalidate();
            dialog.repaint();
            dialog.pack();
        });

        // ---- done action: reset without closing ----
        doneBtn.addActionListener(e -> {
            resultsPanel.removeAll();
            scroll.setVisible(false);
            doneBtn.setVisible(false);
            startField.setText("");
            endField.setText("");
            errLabel.setText(" ");
            dialog.revalidate();
            dialog.repaint();
            dialog.pack();
        });

        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(540, 300));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ==================== DAY BLOCK ====================
    private JPanel buildDayBlock(Date date, Reading[] readings) {
        JPanel block = new JPanel();
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setOpaque(false);
        block.setAlignmentX(Component.LEFT_ALIGNMENT);

        // day title bar
        SimpleDateFormat fmt = new SimpleDateFormat(
                "EEEE, dd MMMM yyyy", Locale.ENGLISH);
        JLabel dayLbl = new JLabel("  " + fmt.format(date));
        dayLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        dayLbl.setForeground(BLUE_TEXT);
        dayLbl.setBackground(BLUE_LIGHT);
        dayLbl.setOpaque(true);
        dayLbl.setBorder(new EmptyBorder(6, 8, 6, 8));
        dayLbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        dayLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        block.add(dayLbl);
        block.add(Box.createVerticalStrut(4));

        // table
        String[] cols = {"Hour", "Value", "Status"};
        Object[][] rows = new Object[24][3];
        for (int h = 0; h < 24; h++) {
            rows[h][0] = String.format("%02d:00", h);
            if (readings[h] == null) {
                rows[h][1] = "—";
                rows[h][2] = "No data";
            } else {
                rows[h][1] = String.format("%.1f %s",
                        readings[h].getValue(), unit);
                rows[h][2] = readings[h].isOutOfRange()
                        ? "Out of range" : "Normal";
            }
        }

        JTable table = new JTable(rows, cols) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }

            @Override
            public Component prepareRenderer(
                    javax.swing.table.TableCellRenderer renderer,
                    int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (readings[row] == null) {
                    c.setForeground(TEXT_HINT);
                    c.setBackground(SURFACE);
                } else if (col == 2) {
                    boolean out = readings[row].isOutOfRange();
                    c.setForeground(out ? RED_TEXT   : GREEN_TEXT);
                    c.setBackground(out ? RED_BG     : GREEN_BG);
                } else {
                    c.setForeground(TEXT_PRIMARY);
                    c.setBackground(row % 2 == 0 ? Color.WHITE : SURFACE);
                }
                return c;
            }
        };

        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(24);
        table.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 11));
        table.getTableHeader().setBackground(SURFACE);
        table.getTableHeader().setForeground(TEXT_SEC);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(110);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        tableScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        tableScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        block.add(tableScroll);

        return block;
    }

    // ==================== HELPERS ====================
    private JSeparator separator() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(BORDER_COLOR);
        return sep;
    }

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXT_SEC);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField labeledField(String labelText) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        field.setToolTipText(labelText);
        return field;
    }

    private JButton styledButton(String text, Color bg, Color hover) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed()  ? hover  :
                        getModel().isRollover() ?
                                bg.darker()         : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}

