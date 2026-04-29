package Util;

public sealed interface GenericType permits TYPE, CLASS {
    public static GenericType getPtrType(GenericType t) {
        return switch (t) {
            case TYPE tp -> switch (tp) {
                case INT -> TYPE.INTPTR;
                case BOOL -> TYPE.BOOLPTR;
                case REAL -> TYPE.REALPTR;
                case CHAR-> TYPE.CHARPTR;
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
                case REALPTR -> TYPE.REAL;
                case CHARPTR -> TYPE.CHAR;
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
                case REALPTR -> true;
                case CHARPTR -> true;
                default -> false;
            };
            case CLASS cp -> cp.name().endsWith("*");
        };
    }

    public static int getSize(GenericType t) {
        return switch (t) {
            case TYPE tp -> switch (tp) {
                case INT -> 4;
                case BOOL -> 1;
                case REAL -> 8;
                case CHAR-> 1;
                default -> 4;
            };
            case @SuppressWarnings("unused") CLASS cp -> 0;
        };
    }
}