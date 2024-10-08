package memory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import system.ShellCommands;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SCANDiskControl {

    private static List<DiskRequest> requestQueue = new LinkedList<>();

    public static void loadProcessIntoRam(Process process) {
        try {
            process.setInstructions((ArrayList<String>) Files.readAllLines(Paths.get(process.getFilePath())));
        } catch (IOException e) {
            System.err.println("Greska u ucitavanju ASM fajla: " + e.getMessage());
        }

        Ram ram = Ram.getInstance();
        boolean allocated = false;

        for (FixedPartition partition : ram.getPartitions()) {
            if (!partition.isOccupied()) {

                partition.allocate(process.getProcessName(), process.getInstructions());
                System.out.println("Alociran proces " + process.getProcessName() + " pripada particiji " + partition.getPartitionId());
                process.setPartitionId(partition.getPartitionId());
                allocated = true;
                break;
            }
        }

        if (!allocated) {
            System.err.println("Ne postoji particija za proces: " + process.getProcessName());
        }
    }

   public static void saveProcessToDisk(Process process) {
        try {
            File newFile = new File(ShellCommands.getCurrentDir() + "\\" + process.getSaveFileName() + ".txt");
            if (!newFile.exists()) {

                for (FileInMemory f : Disc.listOfFiles) {
                    if (f.getName().equals(ShellCommands.getCurrentDir())) {
                        FileInMemory fim = new FileInMemory(process.getSaveFileName(), 1, f);
                        Disc.listOfFiles.add(fim);
                        f.getChildrenFiles().add(fim);
                        newFile.createNewFile();

                        try (FileWriter fw = new FileWriter(newFile)) {
                            String message = "Rezultat izvrsavanja: " + process.getResultString();
                            fw.write(message);
                        }

                        ArrayList<String> contentList = new ArrayList<>();
                        contentList.add("Rezultat izvrsavanja: " + process.getResultString());
                        fim.setContent(contentList);

                        requestQueue.add(new DiskRequest(fim, newFile));

                        executeScan();

                        break;
                    } else {
                        System.out.println("Vec postoji directory ili direktorijum.");
                        return;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static void executeScan() {
        int currentPosition = 0;
        boolean movingRight = true;

        while (!requestQueue.isEmpty()) {
            if (movingRight) {

                for (int i = 0; i < requestQueue.size(); i++) {
                    DiskRequest request = requestQueue.get(i);
                    if (request.getFileName().compareTo(String.valueOf(currentPosition)) >= 0) {
                        System.out.println("Obrada zahtjeva za fajl: " + request.getFile().getName());
                        requestQueue.remove(i);
                        i--;
                        currentPosition++;
                    }
                }

                if (currentPosition >= Ram.NUM_OF_PARTITIONS) {
                    movingRight = false;
                    currentPosition = Ram.NUM_OF_PARTITIONS - 1;
                }
            } else {

                for (int i = requestQueue.size() - 1; i >= 0; i--) {
                    DiskRequest request = requestQueue.get(i);
                    if (request.getFileName().compareTo(String.valueOf(currentPosition)) <= 0) {
                        System.out.println("Obrada zahtjeva za fajl: " + request.getFile().getName());
                        requestQueue.remove(i);
                        currentPosition--;
                    }
                }
                if (currentPosition <= 0) {
                    movingRight = true;
                    currentPosition = 0;
                }
            }
        }
    }
}