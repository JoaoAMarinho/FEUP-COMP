package pt.up.fe.comp.jasmin;

public class StackManager {
    private int stackCounter;
    private int stackLimit;

    public StackManager() {
        this.stackCounter = 0;
        this.stackLimit = 0;
    }

    public int getStackLimit() {
        return this.stackLimit;
    }

    public void decrementStackCounter() {
        this.stackCounter--;
    }

    public void decrementStackCounter(int value) {
        this.stackCounter -= value;
    }

    public void incrementStackCounter() {
        this.stackCounter++;
        if (this.stackCounter > this.stackLimit) {
            this.stackLimit = this.stackCounter;
        }
    }

}
