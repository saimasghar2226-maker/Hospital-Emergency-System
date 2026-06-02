import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

class EmergencyRoom {

    private final PriorityQueue<Patient> waitingQueue;
    private final ArrayList<Patient> treatedPatients;

    public EmergencyRoom() {
        waitingQueue = new PriorityQueue<>((p1, p2) -> {
            if (p1.getSeverity() != p2.getSeverity()) {
                return Integer.compare(p1.getSeverity(), p2.getSeverity());
            }
            return Long.compare(p1.getArrivalOrder(), p2.getArrivalOrder());
        });

        treatedPatients = new ArrayList<>();
    }

    public void addPatient(Patient patient) {
        waitingQueue.add(patient);
    }

    public void addLoadedWaitingPatient(Patient patient) {
        waitingQueue.add(patient);
    }

    public void addLoadedTreatedPatient(Patient patient) {
        treatedPatients.add(patient);
    }

    public Patient treatNextPatient() {
        Patient patient = waitingQueue.poll();

        if (patient != null) {
            patient.markTreated();
            treatedPatients.add(patient);
        }

        return patient;
    }

    public Patient peekNextPatient() {
        return waitingQueue.peek();
    }

    public boolean removeWaitingPatientById(int id) {
        return waitingQueue.removeIf(patient -> patient.getId() == id);
    }

    public List<Patient> getWaitingPatientsSorted() {
        ArrayList<Patient> patients = new ArrayList<>(waitingQueue);
        patients.sort((p1, p2) -> {
            if (p1.getSeverity() != p2.getSeverity()) {
                return Integer.compare(p1.getSeverity(), p2.getSeverity());
            }
            return Long.compare(p1.getArrivalOrder(), p2.getArrivalOrder());
        });
        return patients;
    }

    public List<Patient> getTreatedPatients() {
        return new ArrayList<>(treatedPatients);
    }

    public List<Patient> searchWaitingPatients(String keyword) {
        ArrayList<Patient> result = new ArrayList<>();

        for (Patient patient : getWaitingPatientsSorted()) {
            if (matches(patient, keyword)) {
                result.add(patient);
            }
        }

        return result;
    }

    public List<Patient> searchTreatedPatients(String keyword) {
        ArrayList<Patient> result = new ArrayList<>();

        for (Patient patient : treatedPatients) {
            if (matches(patient, keyword)) {
                result.add(patient);
            }
        }

        return result;
    }

    private boolean matches(Patient patient, String keyword) {
        return String.valueOf(patient.getId()).contains(keyword) ||
                patient.getName().toLowerCase().contains(keyword) ||
                patient.getCondition().toLowerCase().contains(keyword) ||
                patient.getSeverityText().toLowerCase().contains(keyword);
    }

    public int getWaitingCount() {
        return waitingQueue.size();
    }

    public int getTreatedCount() {
        return treatedPatients.size();
    }

    public void clearAll() {
        waitingQueue.clear();
        treatedPatients.clear();
    }
}
