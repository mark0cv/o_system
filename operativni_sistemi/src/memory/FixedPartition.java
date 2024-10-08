package memory;

import java.util.ArrayList;

public class FixedPartition {

    public static final int SIZE = 20; // Veličina particije
    private ArrayList<String> content; // Sadržaj particije
    private String processName; // Ime procesa koji koristi particiju
    private int partitionId; // ID particije
    private boolean occupied; // Status zauzetosti

    public FixedPartition(int partitionId) {
        this.partitionId = partitionId;
        this.content = new ArrayList<>(SIZE);
        this.occupied = false;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void allocate(String processName, ArrayList<String> data) {
        if (!occupied) {
            this.processName = processName;
            this.content.clear(); // Očisti prethodni sadržaj
            this.content.addAll(data); // Dodaj nove podatke
            occupied = true; // Postavi status na zauzet
        }
    }

    public void free() {
        occupied = false; // Postavi status na slobodan
        this.processName = null; // Očisti ime procesa
        content.clear(); // Očisti sadržaj
    }

    public ArrayList<String> getContent() {
        return content;
    }

    public String getProcessName() {
        return processName;
    }

    public int getPartitionId() {
        return partitionId;
    }

    @Override
    public String toString() {
        return "Partition ID: " + partitionId + " | Process: " + processName + " | Content: " + content;
    }
}
