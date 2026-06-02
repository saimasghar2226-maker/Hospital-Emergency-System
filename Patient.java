import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Patient {
    private static int nextId = 1001;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final int id;
    private final String name;
    private final int age;
    private final String gender;
    private final String condition;
    private final int severity;
    private final long arrivalOrder;
    private final String arrivalTime;
    private String treatedTime;

    public Patient(String name, int age, String gender, String condition, int severity) {
        this.id = nextId++;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.condition = condition;
        this.severity = severity;
        this.arrivalOrder = System.nanoTime();
        this.arrivalTime = LocalDateTime.now().format(FORMATTER);
        this.treatedTime = "Not Treated";
    }

    private Patient(int id, String name, int age, String gender, String condition, int severity,
                    long arrivalOrder, String arrivalTime, String treatedTime) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.condition = condition;
        this.severity = severity;
        this.arrivalOrder = arrivalOrder;
        this.arrivalTime = arrivalTime;
        this.treatedTime = treatedTime;
    }

    public void markTreated() {
        this.treatedTime = LocalDateTime.now().format(FORMATTER);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getCondition() {
        return condition;
    }

    public int getSeverity() {
        return severity;
    }

    public long getArrivalOrder() {
        return arrivalOrder;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getTreatedTime() {
        return treatedTime;
    }

    public String getSeverityText() {
        return switch (severity) {
            case 1 -> "Critical";
            case 2 -> "Serious";
            case 3 -> "Moderate";
            case 4 -> "Minor";
            default -> "Unknown";
        };
    }

    public String toFileString() {
        return id + "|" + escape(name) + "|" + age + "|" + escape(gender) + "|" +
                escape(condition) + "|" + severity + "|" + arrivalOrder + "|" +
                escape(arrivalTime) + "|" + escape(treatedTime);
    }

    public static Patient fromFileString(String data) {
        String[] parts = data.split("\\|", -1);

        int id = Integer.parseInt(parts[0]);
        String name = unescape(parts[1]);
        int age = Integer.parseInt(parts[2]);
        String gender = unescape(parts[3]);
        String condition = unescape(parts[4]);
        int severity = Integer.parseInt(parts[5]);
        long arrivalOrder = Long.parseLong(parts[6]);
        String arrivalTime = unescape(parts[7]);
        String treatedTime = unescape(parts[8]);

        return new Patient(id, name, age, gender, condition, severity, arrivalOrder, arrivalTime, treatedTime);
    }

    private static String escape(String value) {
        return value.replace("|", ",");
    }

    private static String unescape(String value) {
        return value;
    }

    public static int getNextId() {
        return nextId;
    }

    public static void setNextId(int nextId) {
        Patient.nextId = nextId;
    }
}
