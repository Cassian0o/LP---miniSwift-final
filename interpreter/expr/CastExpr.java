package interpreter.expr;

import java.util.Collection;
import java.util.Map;
import error.LanguageException;
import interpreter.type.primitive.*;
import interpreter.value.Value;

public class CastExpr extends Expr {
    public enum CastOp { ToBoolOp, ToIntOp, ToFloatOp, ToCharOp, ToStringOp }
    private final CastOp op;
    private final Expr expr;

    public CastExpr(int line, CastOp op, Expr expr) {
        super(line);
        this.op = op;
        this.expr = expr;
    }

    @Override
    public Value expr() {
        Value value = expr.expr();
        try {
            switch (op) {
                case ToStringOp:
                    return new Value(StringType.instance(), value.data.toString());
                case ToBoolOp:
                    return new Value(BoolType.instance(), toBool(value));
                case ToIntOp:
                    return new Value(IntType.instance(), toInt(value));
                case ToFloatOp:
                    float number = toFloat(value);
                    if (!Float.isFinite(number)) throw invalidOperation();
                    return new Value(FloatType.instance(), number);
                case ToCharOp:
                    if (value.type instanceof CharType) return value;
                    if (value.type instanceof StringType && ((String) value.data).length() == 1)
                        return new Value(CharType.instance(), ((String) value.data).charAt(0));
                    if (value.type instanceof IntType) {
                        int code = (Integer) value.data;
                        if (code >= Character.MIN_VALUE && code <= Character.MAX_VALUE)
                            return new Value(CharType.instance(), (char) code);
                    }
                    throw invalidOperation();
                default:
                    throw new IllegalStateException("Unknown cast: " + op);
            }
        } catch (NumberFormatException e) {
            throw invalidOperation();
        }
    }

    private boolean toBool(Value value) {
        switch (value.type.getCategory()) {
            case Bool: return (Boolean) value.data;
            case Int: return (Integer) value.data != 0;
            case Float: return (Float) value.data != 0.0f;
            case Char: return (Character) value.data != 0;
            case String:
                if (value.data.equals("true")) return true;
                if (value.data.equals("false")) return false;
                throw invalidOperation();
            case Array: return !((Collection<?>) value.data).isEmpty();
            case Dict: return !((Map<?, ?>) value.data).isEmpty();
            default: throw invalidOperation();
        }
    }

    private int toInt(Value value) {
        switch (value.type.getCategory()) {
            case Int: return (Integer) value.data;
            case Char: return (Character) value.data;
            case Bool: return (Boolean) value.data ? 1 : 0;
            case Float:
                float number = (Float) value.data;
                if (!Float.isFinite(number) || number < Integer.MIN_VALUE || (double) number > Integer.MAX_VALUE)
                    throw invalidOperation();
                return (int) number;
            case String: return Integer.parseInt((String) value.data);
            default: throw invalidOperation();
        }
    }

    private float toFloat(Value value) {
        switch (value.type.getCategory()) {
            case Float: return (Float) value.data;
            case Int: return ((Integer) value.data).floatValue();
            case Char: return (Character) value.data;
            case Bool: return (Boolean) value.data ? 1.0f : 0.0f;
            case String: return Float.parseFloat((String) value.data);
            default: throw invalidOperation();
        }
    }

    private LanguageException invalidOperation() {
        return LanguageException.instance(getLine(), LanguageException.Error.InvalidOperation);
    }
}
