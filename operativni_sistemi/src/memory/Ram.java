package memory;

import java.util.ArrayList;

public class Ram {
    public static final int NUM_OF_PARTITIONS = 64;
    private ArrayList<FixedPartition> partitions;

    private static Ram instance;

    private Ram() {
        this.partitions = new ArrayList<>(NUM_OF_PARTITIONS);
        for (int i = 0; i < NUM_OF_PARTITIONS; i++) {
            partitions.add(new FixedPartition(i));
        }
    }

    public static Ram getInstance() {
        if (instance == null) {
            instance = new Ram(); // inicijalizuje samo prvi put
        }
        return instance;
    }

    public ArrayList<FixedPartition> getPartitions() {
        return partitions;
    }
}
