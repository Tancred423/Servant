package commands.fun.baguette;

public class Baguette {
    private final int size;
    private final int counter;

    public Baguette(int size, int counter) {
        this.size = size;
        this.counter = counter;
    }

    public int getSize() {
        return size;
    }

    public int getCounter() {
        return counter;
    }
}
