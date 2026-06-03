import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class HospitalEmergencySystem extends JFrame {

    private final EmergencyRoom emergencyRoom = new EmergencyRoom();

    private JTextField nameField;
    private JTextField ageField;
    private JTextField conditionField;
    private JComboBox<String> genderBox;
    private JComboBox<String> severityBox;
    private JTextField searchField;

    private JTable waitingTable;
    private JTable treatedTable;
    private DefaultTableModel waitingModel;
    private DefaultTableModel treatedModel;

    private JLabel totalWaitingLabel;
    private JLabel totalTreatedLabel;
    private JLabel nextPatientLabel;

    private static final String FILE_NAME = "hospital_emergency_data.txt";

    public HospitalEmergencySystem() {
        setTitle("Hospital Emergency System - Swing GUI");
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        refreshTables();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(33, 37, 41));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Hospital Emergency Management System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 26));

        JLabel subtitle = new JLabel("Priority-based patient triage using Java Swing and PriorityQueue");
        subtitle.setForeground(new Color(220, 220, 220));
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(subtitle);

        panel.add(textPanel, BorderLayout.WEST);
        return panel;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        mainPanel.add(createInputPanel(), BorderLayout.WEST);
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        mainPanel.add(createStatsPanel(), BorderLayout.EAST);

        return mainPanel;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(280, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Patient Registration"));

        nameField = new JTextField();
        ageField = new JTextField();
        conditionField = new JTextField();

        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        severityBox = new JComboBox<>(new String[]{
                "1 - Critical",
                "2 - Serious",
                "3 - Moderate",
                "4 - Minor"
        });

        panel.add(label("Patient Name:"));
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(10));

        panel.add(label("Age:"));
        panel.add(ageField);
        panel.add(Box.createVerticalStrut(10));

        panel.add(label("Gender:"));
        panel.add(genderBox);
        panel.add(Box.createVerticalStrut(10));

        panel.add(label("Emergency Condition:"));
        panel.add(conditionField);
        panel.add(Box.createVerticalStrut(10));

        panel.add(label("Severity Level:"));
        panel.add(severityBox);
        panel.add(Box.createVerticalStrut(15));

        JButton addButton = new JButton("Add Patient");
        JButton clearButton = new JButton("Clear Fields");
        JButton treatButton = new JButton("Treat Next Patient");
        JButton removeButton = new JButton("Remove Selected Waiting Patient");

        addButton.addActionListener(this::addPatient);
        clearButton.addActionListener(e -> clearInputFields());
        treatButton.addActionListener(e -> treatNextPatient());
        removeButton.addActionListener(e -> removeSelectedPatient());

        panel.add(addButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(clearButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(treatButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(removeButton);
        panel.add(Box.createVerticalStrut(20));

        panel.add(label("Search by ID or Name:"));
        searchField = new JTextField();
        JButton searchButton = new JButton("Search Patient");
        JButton resetButton = new JButton("Reset Search");

        searchButton.addActionListener(e -> searchPatient());
        resetButton.addActionListener(e -> {
            searchField.setText("");
            refreshTables();
        });

        panel.add(searchField);
        panel.add(Box.createVerticalStrut(8));
        panel.add(searchButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(resetButton);

        return panel;
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));

        waitingModel = new DefaultTableModel(new String[]{
                "ID", "Name", "Age", "Gender", "Condition", "Severity", "Arrival Time"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        treatedModel = new DefaultTableModel(new String[]{
                "ID", "Name", "Age", "Gender", "Condition", "Severity", "Arrival Time", "Treated Time"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        waitingTable = new JTable(waitingModel);
        treatedTable = new JTable(treatedModel);

        waitingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        treatedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane waitingScroll = new JScrollPane(waitingTable);
        JScrollPane treatedScroll = new JScrollPane(treatedTable);

        JPanel waitingPanel = new JPanel(new BorderLayout());
        waitingPanel.setBorder(BorderFactory.createTitledBorder("Waiting Emergency Queue"));
        waitingPanel.add(waitingScroll, BorderLayout.CENTER);

        JPanel treatedPanel = new JPanel(new BorderLayout());
        treatedPanel.setBorder(BorderFactory.createTitledBorder("Treated Patient History"));
        treatedPanel.add(treatedScroll, BorderLayout.CENTER);

        panel.add(waitingPanel);
        panel.add(treatedPanel);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(250, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("System Dashboard"));

        totalWaitingLabel = dashboardLabel("Waiting Patients: 0");
        totalTreatedLabel = dashboardLabel("Treated Patients: 0");
        nextPatientLabel = dashboardLabel("Next Patient: None");

        JButton saveButton = new JButton("Save Data to File");
        JButton loadButton = new JButton("Load Data from File");
        JButton clearAllButton = new JButton("Clear All Data");
        JButton aboutButton = new JButton("Project Info");

        saveButton.addActionListener(e -> saveDataToFile());
        loadButton.addActionListener(e -> loadDataFromFile());
        clearAllButton.addActionListener(e -> clearAllData());
        aboutButton.addActionListener(e -> showProjectInfo());

        panel.add(Box.createVerticalStrut(20));
        panel.add(totalWaitingLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(totalTreatedLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(nextPatientLabel);
        panel.add(Box.createVerticalStrut(30));
        panel.add(saveButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(loadButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(clearAllButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(aboutButton);

        return panel;
    }

    private JLabel dashboardLabel(String text) {
        JLabel label = new JLabel("<html>" + text + "</html>");
        label.setFont(new Font("Arial", Font.BOLD, 15));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(248, 249, 250));
        JLabel footer = new JLabel("DSA Concepts Used: PriorityQueue, ArrayList, Searching, Sorting, File Handling");
        footer.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(footer);
        return panel;
    }

    private void addPatient(ActionEvent event) {
        String name = nameField.getText().trim();
        String ageText = ageField.getText().trim();
        String gender = genderBox.getSelectedItem().toString();
        String condition = conditionField.getText().trim();
        int severity = severityBox.getSelectedIndex() + 1;

        if (name.isEmpty() || ageText.isEmpty() || condition.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageText);
            if (age <= 0 || age > 120) {
                JOptionPane.showMessageDialog(this, "Please enter a valid age between 1 and 120.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Patient patient = new Patient(name, age, gender, condition, severity);
        emergencyRoom.addPatient(patient);
        clearInputFields();
        refreshTables();

        JOptionPane.showMessageDialog(this, "Patient added successfully. Patient ID: " + patient.getId());
    }

    private void clearInputFields() {
        nameField.setText("");
        ageField.setText("");
        conditionField.setText("");
        genderBox.setSelectedIndex(0);
        severityBox.setSelectedIndex(0);
    }

    private void treatNextPatient() {
        Patient patient = emergencyRoom.treatNextPatient();

        if (patient == null) {
            JOptionPane.showMessageDialog(this, "No patient is waiting.");
            return;
        }

        refreshTables();
        JOptionPane.showMessageDialog(this,
                "Now treating patient:\n" +
                        "ID: " + patient.getId() + "\n" +
                        "Name: " + patient.getName() + "\n" +
                        "Severity: " + patient.getSeverityText());
    }

    private void removeSelectedPatient() {
        int selectedRow = waitingTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a waiting patient first.");
            return;
        }

        int id = Integer.parseInt(waitingModel.getValueAt(selectedRow, 0).toString());
        boolean removed = emergencyRoom.removeWaitingPatientById(id);

        if (removed) {
            refreshTables();
            JOptionPane.showMessageDialog(this, "Patient removed from waiting queue.");
        } else {
            JOptionPane.showMessageDialog(this, "Patient not found.");
        }
    }

    private void searchPatient() {
        String keyword = searchField.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter patient ID or name to search.");
            return;
        }

        List<Patient> waitingResults = emergencyRoom.searchWaitingPatients(keyword);
        List<Patient> treatedResults = emergencyRoom.searchTreatedPatients(keyword);

        loadWaitingTable(waitingResults);
        loadTreatedTable(treatedResults);

        if (waitingResults.isEmpty() && treatedResults.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No patient found for: " + keyword);
        }
    }

    private void refreshTables() {
        loadWaitingTable(emergencyRoom.getWaitingPatientsSorted());
        loadTreatedTable(emergencyRoom.getTreatedPatients());
        updateDashboard();
    }

    private void loadWaitingTable(List<Patient> patients) {
        waitingModel.setRowCount(0);
        for (Patient patient : patients) {
            waitingModel.addRow(new Object[]{
                    patient.getId(),
                    patient.getName(),
                    patient.getAge(),
                    patient.getGender(),
                    patient.getCondition(),
                    patient.getSeverityText(),
                    patient.getArrivalTime()
            });
        }
    }

    private void loadTreatedTable(List<Patient> patients) {
        treatedModel.setRowCount(0);
        for (Patient patient : patients) {
            treatedModel.addRow(new Object[]{
                    patient.getId(),
                    patient.getName(),
                    patient.getAge(),
                    patient.getGender(),
                    patient.getCondition(),
                    patient.getSeverityText(),
                    patient.getArrivalTime(),
                    patient.getTreatedTime()
            });
        }
    }

    private void updateDashboard() {
        totalWaitingLabel.setText("Waiting Patients: " + emergencyRoom.getWaitingCount());
        totalTreatedLabel.setText("Treated Patients: " + emergencyRoom.getTreatedCount());

        Patient next = emergencyRoom.peekNextPatient();
        if (next == null) {
            nextPatientLabel.setText("<html>Next Patient:<br>None</html>");
        } else {
            nextPatientLabel.setText("<html>Next Patient:<br>" + next.getName() + "<br>" + next.getSeverityText() + "</html>");
        }
    }

    private void saveDataToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            writer.println("NEXT_ID|" + Patient.getNextId());

            for (Patient patient : emergencyRoom.getWaitingPatientsSorted()) {
                writer.println("WAITING|" + patient.toFileString());
            }

            for (Patient patient : emergencyRoom.getTreatedPatients()) {
                writer.println("TREATED|" + patient.toFileString());
            }

            JOptionPane.showMessageDialog(this, "Data saved successfully to " + FILE_NAME);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDataFromFile() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "No saved file found.");
            return;
        }

        emergencyRoom.clearAll();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("NEXT_ID|")) {
                    int nextId = Integer.parseInt(line.split("\\|", 2)[1]);
                    Patient.setNextId(nextId);
                    continue;
                }

                String[] parts = line.split("\\|", 2);
                if (parts.length < 2) {
                    continue;
                }

                String status = parts[0];
                Patient patient = Patient.fromFileString(parts[1]);

                if (status.equals("WAITING")) {
                    emergencyRoom.addLoadedWaitingPatient(patient);
                } else if (status.equals("TREATED")) {
                    emergencyRoom.addLoadedTreatedPatient(patient);
                }
            }

            refreshTables();
            JOptionPane.showMessageDialog(this, "Data loaded successfully.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearAllData() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear all data?",
                "Confirm Clear",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            emergencyRoom.clearAll();
            refreshTables();
            JOptionPane.showMessageDialog(this, "All data cleared.");
        }
    }

    private void showProjectInfo() {
        JOptionPane.showMessageDialog(this,
                "Hospital Emergency System\n\n" +
                        "Main Purpose:\n" +
                        "This system manages emergency patients based on severity level.\n\n" +
                        "Data Structures Used:\n" +
                        "1. PriorityQueue - treats critical patients first.\n" +
                        "2. ArrayList - stores treated patient history.\n\n" +
                        "Algorithms Used:\n" +
                        "1. Priority-based sorting using Comparator.\n" +
                        "2. Linear search by ID or name.\n" +
                        "3. File handling for saving/loading records.",
                "Project Information",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new HospitalEmergencySystem().setVisible(true);
        });
    }
}


