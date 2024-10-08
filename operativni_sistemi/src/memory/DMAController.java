package memory;

//import system.ShellCommands;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class DMAController {

    public static void fromDiskToRam(Process process) {
        Ram ram = Ram.getInstance();
        ArrayList<FixedPartition> partitions = ram.getPartitions();
        ArrayList<String> instructions = process.getInstructions();
        int instructionIndex = 0;

        try {
            for (FixedPartition partition : partitions) {
                if (!partition.isOccupied()) {

                    if (instructions.size() > FixedPartition.SIZE) {
                        System.out.println("Proces " + process.getProcessName() + " ima previše instrukcija da bi stao u jednu particiju.");
                        return;
                    }

                    ArrayList<String> partitionData = new ArrayList<>();

                    while (instructionIndex < instructions.size() && partitionData.size() < FixedPartition.SIZE) {
                        partitionData.add(instructions.get(instructionIndex));
                        instructionIndex++;
                    }

                    partition.allocate(process.getProcessName(), partitionData);
                    System.out.println("Proces " + process.getProcessName() + " uspešno prebačen u particiju RAM-a.");
                    return;
                }
            }

            System.out.println("Nema slobodnih particija u RAM-u za proces " + process.getProcessName() + ".");

        } catch (Exception e) {
            System.err.println("Greška prilikom učitavanja procesa iz diska u RAM: " + e.getMessage());
        }
    }


    public static void fromRamToDisk(Process process) {
        Ram ram = Ram.getInstance();
        FreeSpacePointer freeSpacePointer = Disc.freeSpace;
        ArrayList<FixedPartition> partitions = ram.getPartitions();

        try {
            // Kreiramo fajl na disku da sačuvamo sadržaj procesa
            File newFile = new File("path/to/save/" + process.getSaveFileName() + ".txt");
            if (!newFile.exists()) {
                newFile.createNewFile();
                FileWriter writer = new FileWriter(newFile);

                // Prolazimo kroz RAM particije koje pripadaju procesu
                for (FixedPartition partition : partitions) {
                    if (partition.isOccupied() && partition.getProcessName().equals(process.getProcessName())) {
                        // Uzimamo slobodan blok sa diska
                        Block freeBlock = allocateFreeBlock();

                        if (freeBlock == null) {
                            System.out.println("Nema slobodnih blokova na disku za proces.");
                            writer.close();
                            return;
                        }

                        // Prebacujemo sadržaj particije u slobodan blok i upisujemo na disk
                        ArrayList<String> partitionData = partition.getContent();
                        for (String line : partitionData) {
                            writer.write(line + "\n");
                            freeBlock.getContent().add(line); // Dodajemo sadržaj u disk blok
                        }

                        partition.free(); // Oslobađamo particiju nakon prebacivanja
                    }
                }

                writer.close();

                // Simuliramo dodavanje fajla u memoriju diska
                FileInMemory fileInMemory = new FileInMemory(process.getSaveFileName(), (int) newFile.length(), null);
                Disc.listOfFiles.add(fileInMemory);
            }

        } catch (IOException e) {
            System.err.println("Greška prilikom prebacivanja procesa iz RAM-a na disk: " + e.getMessage());
        }
    }

    public static Block allocateFreeBlock() {
        if (Disc.freeSpace != null) {
            Block freeBlock = Disc.freeSpace.getBlock(); //uzima slobodan blok

            Disc.freeSpace = Disc.freeSpace.getNextBlock(); //pokazivac se prebacuje na sledeci slobodna

            if (Disc.freeSpace != null) {
                Disc.freeSpace.setPreviousBlock(null); // ako sledeci slobodan postoji(ako ima jos slobodnih blokova)
                                                       // njegov prethodni ide na null jer nije vise slobodan
            }
            Disc.occupiedSpace.add(freeBlock); //dodajemo blok u zauzet prostor(imace svoj indeks u listi)

            return freeBlock;
        } else {
            System.out.println("Nema slobodnih blokova na disku.");
            return null;
        }
    }


    public static void freeBlock(Block block) {
        Disc.occupiedSpace.remove(block); //izbacujemo ga iz zauzete liste

        // povratak u dvostruko povezanu listu slobodnih blokova
        FreeSpacePointer newFreeSpace = new FreeSpacePointer(block);
        newFreeSpace.setNextBlock(Disc.freeSpace); // dodajemo ga na pocetak liste
        if (Disc.freeSpace != null) {
            Disc.freeSpace.setPreviousBlock(newFreeSpace);
        }
        Disc.freeSpace = newFreeSpace;
    }


}
