package interpreter.expr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import error.LanguageException;
import interpreter.type.Type;
import interpreter.type.composed.ArrayType;
import interpreter.type.composed.DictType;
import interpreter.type.primitive.BoolType;
import interpreter.type.primitive.CharType;
import interpreter.type.primitive.IntType;
import interpreter.value.Value;

public class FunctionExpr extends Expr {
    public enum FunctionOp { Count, Empty, Keys, Values, Append, Contains }

    private final FunctionOp op;
    private final Expr expr;
    private final Expr arg;

    public FunctionExpr(int line, FunctionOp op, Expr expr, Expr arg) {
        super(line);
        this.op = op;
        this.expr = expr;
        this.arg = arg;
    }

    @Override
    public Value expr() {
        Value value = expr.expr();
        switch (op) {
            case Count:
                return new Value(IntType.instance(), size(value));
            case Empty:
                return new Value(BoolType.instance(), size(value) == 0);
            case Keys:
            case Values:
                if (!(value.type instanceof DictType)) throw invalidOperation();
                DictType type = (DictType) value.type;
                Map<Value, Value> map = dictionary(value);
                return new Value(ArrayType.instance(op == FunctionOp.Keys ? type.getKeyType() : type.getValueType()),
                    new ArrayList<Value>(op == FunctionOp.Keys ? map.keySet() : map.values()));
            case Append:
                if (!(value.type instanceof ArrayType)) throw invalidOperation();
                Value item = arg.expr();
                require(((ArrayType) value.type).getInnerType(), item);
                array(value).add(item);
                return value;
            case Contains:
                Value target = arg.expr();
                if (value.type instanceof ArrayType) {
                    require(((ArrayType) value.type).getInnerType(), target);
                    return new Value(BoolType.instance(), array(value).contains(target));
                }
                if (value.type.getCategory() == Type.Category.String) {
                    require(CharType.instance(), target);
                    return new Value(BoolType.instance(), ((String) value.data).indexOf((Character) target.data) >= 0);
                }
                throw invalidOperation();
            default:
                throw new IllegalStateException("Unknown function: " + op);
        }
    }

    private int size(Value value) {
        switch (value.type.getCategory()) {
            case String: return ((String) value.data).length();
            case Array: return array(value).size();
            case Dict: return dictionary(value).size();
            default: throw invalidOperation();
        }
    }

    private void require(Type type, Value value) {
        if (!type.match(value.type))
            throw LanguageException.instance(getLine(), LanguageException.Error.InvalidType, value.type.toString());
    }

    private LanguageException invalidOperation() {
        return LanguageException.instance(getLine(), LanguageException.Error.InvalidOperation);
    }

    @SuppressWarnings("unchecked")
    private List<Value> array(Value value) { return (List<Value>) value.data; }

    @SuppressWarnings("unchecked")
    private Map<Value, Value> dictionary(Value value) { return (Map<Value, Value>) value.data; }
}
