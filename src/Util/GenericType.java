package Util;

public sealed interface GenericType permits TYPE, CLASS {
    public static GenericType getPtrType(GenericType t) {
        return switch (t) {
            case TYPE tp -> switch (tp) {
                case INT -> TYPE.INTPTR;
                case BOOL -> TYPE.BOOLPTR;
                default -> t;
            };
            case CLASS cp -> new CLASS(cp.name() + "*");
        };
    }

    public static GenericType getValueType(GenericType t) {
        return switch (t) {
            case TYPE tp -> switch (tp) {
                case INTPTR -> TYPE.INT;
                case BOOLPTR -> TYPE.BOOL;
                default -> t;
            };
            case CLASS cp -> new CLASS(cp.name().substring(0, cp.name().length() - 1));
        };
    }

    public static boolean isPtr(GenericType t) {
        return switch (t) {
            case TYPE tp -> switch (tp) {
                case INTPTR -> true;
                case BOOLPTR -> true;
                case STRING -> true;
                default -> false;
            };
            case CLASS cp -> cp.name().endsWith("*");
        };
    }
}