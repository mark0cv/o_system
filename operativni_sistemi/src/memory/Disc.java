package memory;

import java.util.ArrayList;

public class Disc {

    public static ArrayList<FileInMemory>listOfFiles=new ArrayList<>();
    private static int NumOfBlocks=1024/Block.getSize();
    public static int SIZE=1024;
    public static ArrayList<Block>occupiedSpace=new ArrayList<>(NumOfBlocks);

    public static FreeSpacePointer freeSpace;

}
