package Generator;

import Util.TYPE;

public non-sealed class Immediate implements GeneratorResult {
    public TYPE type = TYPE.VOID;
    public int id = -1;
    public String identifier = "";

    public Immediate() { }

    public Immediate(TYPE type, int id) {
        this.type = type;
        this.id = id;
    }

    public Immediate(TYPE type, String identifier) {
        this.type = type;
        this.identifier = identifier;
    }
}
