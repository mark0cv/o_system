package memory;

import assembler.AsmHandler;
//import system.ShellCommands;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Process extends Thread {

    public Stack<String> stack = new Stack<>();
    private String processName;
    private String processNameWithID;
    private boolean save = false;
    private int result;
    private String saveFileName;
    private ArrayList<String> instructions = new ArrayList<>();
    private int idProces;
    public String currentInstruction;
    private int partitionId;
    private String resultString;


    private long arrivalTime;
    private long serviceTime;
    private long waitingTime;
    private long startTime;

    public Process(String filePath, int id) {
        this.processName = filePath;
        this.idProces = id;
        this.processNameWithID = extractProcessNameWithID(filePath, id);
    }

    private String extractProcessNameWithID(String filePath, int id) {
        int x = -1;
        if (filePath.contains("/")) {
            x = filePath.lastIndexOf("/"); //za UNIX sisteme
        } else if (filePath.contains("\\")) {
            x = filePath.lastIndexOf("\\"); //Windows sistemi
        }

        try {
            return filePath.substring(x + 1) + "(" + id + ")";
        } catch (StringIndexOutOfBoundsException e) {
            return filePath + "(" + id + ")";
        }
    }


    public int getIdProces() {
        return idProces;
    }

    public String getSaveFileName() {
        return saveFileName;
    }

    public void setSaveFileName(String saveFileName) {
        this.saveFileName = saveFileName;
    }


    public boolean isSave() {
        return save;
    }

    public void setSave(boolean save) {
        this.save = save;
    }

    public ArrayList<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(ArrayList<String> instructions) {
        this.instructions = instructions;
    }

    public String getProcessName() {
        return processNameWithID;
    }

    public String getFilePath() {
        return this.processName;
    }

    public void setResultString(String resultString) {
        this.resultString = resultString;
    }
    public void setPartitionId(int partitionId) {
        this.partitionId = partitionId;
    }
    public String getResultString() {
        return resultString;
    }
    public int getPartitionId(){
        return partitionId;
    }


    @Override
    public void run() {
        try {
            AsmHandler asmHandler = new AsmHandler();

            DMAController.fromDiskToRam(this);
            SCANDiskControl.loadProcessIntoRam(this);

            if (!hasEnoughRamSpace()) {
                System.out.println("Nema dovoljno slobodnih particija u RAM-u za proces " + this.getProcessName());
                return;
            }

            asmHandler.instructionReader(this);

            freeRamResources();

            if (this.save) {
                DMAController.fromRamToDisk(this);
                SCANDiskControl.saveProcessToDisk(this);
            }

        } catch (Exception e) {
            System.err.println("Greška u izvršavanju procesa: " + e.getMessage());
        }
    }

    private boolean hasEnoughRamSpace(){

        List<FixedPartition>partitions=Ram.getInstance().getPartitions();

        for(FixedPartition partition:partitions){
            if(!partition.isOccupied())
                return true; //postoji bar jedn particija koja je slobodna
        }
        return false;
    }
    private void freeRamResources() {
        List<FixedPartition> partitions = Ram.getInstance().getPartitions();
        for (FixedPartition partition : partitions) {
            if (partition.isOccupied() && partition.getProcessName().equals(this.getProcessName())) {
                partition.free();
            }
        }
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public long getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(long serviceTime) {
        this.serviceTime = serviceTime;
    }

    public long getWaitingTime() {
        return waitingTime;
    }

    public void updateWaitingTime(long currentTime) {
        this.waitingTime = currentTime - this.arrivalTime;
    }

    public long getResponseRatio(long currentTime) {
        updateWaitingTime(currentTime);
        return (waitingTime + serviceTime) / serviceTime;
    }

    @Override
    public String toString() {
        return processNameWithID;
    }
}
