package Generator;

import Util.GenericType;
import Util.TYPE;

public non-sealed class Immediate implements GeneratorResult {
    public GenericType type = TYPE.VOID;
    public String id = "";

    public Immediate() { }

    public Immediate(GenericType type, String id) {
        this.type = type;
        this.id = id;
    }
}
