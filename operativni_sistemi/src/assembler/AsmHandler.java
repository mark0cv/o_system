package assembler;

import memory.FixedPartition;
import memory.Process;
import memory.Ram;

import java.util.ArrayList;
import java.util.List;

public class AsmHandler {

    // Metoda za čitanje i izvršavanje instrukcija iz RAM-a
    public void instructionReader(Process process) {
        List<String> asmFileLines = new ArrayList<>();
        Operations operations = new Operations(process);

        // Čitanje instrukcija iz RAM-a
        Ram ram = Ram.getInstance();
        for (FixedPartition partition : ram.getPartitions()) {
            if (partition.isOccupied() && partition.getProcessName().equalsIgnoreCase(process.getProcessName())) {
                asmFileLines.addAll(partition.getContent());
            }
        }

        // Izvršavanje instrukcija redom (bez pauziranja)
        for (String instruction : asmFileLines) {
            instructionRunner(instruction, process, operations);
        }
    }

    // Metoda koja izvršava jednu instrukciju
    private void instructionRunner(String instruction, Process process, Operations operations) {
        process.currentInstruction = instruction;
        String[] arr = instruction.split(" ");

        switch (arr[0]) {
            case "ADD":
                operations.add();
                break;

            case "SUB":
                operations.sub();
                break;

            case "MUL":
                operations.mul();
                break;

            case "DIV":
                operations.div();
                break;

            case "PUSH":
                operations.push(arr[1]);
                break;

            case "POP":
                String val = operations.pop();
                process.setResult(Integer.parseInt(val));
                break;

            case "INC":
                operations.inc();
                break;

            case "DEC":
                operations.dec();
                break;

            default:
                System.err.println("Nepoznata instrukcija: " + instruction);
                break;
        }
    }
}
