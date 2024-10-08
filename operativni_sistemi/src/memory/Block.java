package memory;

import java.util.ArrayList;

public class Block {
    public static int size=4;
    private ArrayList<String>content=new ArrayList<>();
    private int address;
    private boolean ocuppied;
    private String fileName;
    public Block(int address){
        this.address=address;
        this.ocuppied=false;
    }

    public static int getSize() {
        return size;
    }

    public ArrayList<String> getContent() {
        return content;
    }

    public int getAddress() {
        return address;
    }

    public boolean isOcuppied() {
        return ocuppied;
    }

    public String getFileName() {
        return fileName;
    }

    public static void setSize(int size) {
        Block.size = size;
    }

    public void setContent(ArrayList<String> content) {
        this.content = content;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public void setOcuppied(boolean ocuppied) {
        this.ocuppied = ocuppied;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
