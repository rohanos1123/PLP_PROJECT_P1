package Generator;

public class MutableInteger {
    private int value = 0;

    public MutableInteger(int value) {
        this.value = value;
    }
    
    public int inc() {
        return value++;
    }

    public int preInc() {
        return ++value;
    }

    public int add(int value) {
        return this.value += value;
    }

    public int value() {
        return value;
    }
}
