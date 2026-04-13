package Generator;

import Util.TYPE;

public non-sealed class Immediate implements GeneratorResult {
    public TYPE type = TYPE.VOID;
    public String id = "";

    public Immediate() { }

    public Immediate(TYPE type, String id) {
        this.type = type;
        this.id = id;
    }
}
