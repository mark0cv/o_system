package memory;

public class FreeSpacePointer {
    private Block block;
    private FreeSpacePointer previousBlock;
    private FreeSpacePointer nextBlock;

    public FreeSpacePointer(Block block){
        this.block=block;
    }

    public Block getBlock() {
        return block;
    }

    public FreeSpacePointer getPreviousBlock() {
        return previousBlock;
    }

    public FreeSpacePointer getNextBlock() {
        return nextBlock;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public void setPreviousBlock(FreeSpacePointer previousBlock) {
        this.previousBlock = previousBlock;
    }

    public void setNextBlock(FreeSpacePointer nextBlock) {
        this.nextBlock = nextBlock;
    }
}
