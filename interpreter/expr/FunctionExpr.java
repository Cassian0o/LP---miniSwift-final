package interpreter.expr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import interpreter.type.Type;
import interpreter.type.composed.ArrayType;
import interpreter.type.composed.DictType;
import interpreter.type.primitive.BoolType;
import interpreter.type.primitive.IntType;
import interpreter.type.primitive.StringType;
import interpreter.value.Value;

public class FunctionExpr extends Expr {
    public static enum FunctionOp {
        Count,
        Empty,
        Keys,
        Values,
        Append,
        Contains
    }

    private FunctionOp op;
    private Expr expr;
    private Expr arg;

    public FunctionExpr(int line, FunctionOp op, Expr expr, Expr arg) {
        super(line);
        this.op = op;
        this.expr = expr;
        this.arg = arg;
    }

    @Override
    public Value expr() {
        Value value = expr.expr();
        Value varg = arg.expr();
        Value ret = null;
        switch (op) {
            case Count:
                ret = countOp(value, varg);
                break;
            case Empty:
                ret = emptyOp(value, varg);
                break;
            case Keys:
                ret = keysOp(value, varg);
                break;
            case Values:
                ret = valuesOp(value, varg);
                break;
            case Append:
                ret = appendOp(value, varg);
                break;
            case Contains:
                ret = containsOp(value, varg);
                break;
            default:
                throw new InternalError("unreachable");
        }

        return ret;
    }

    private Value containsOp(Value value, Value varg) {
        if (value != null && varg != null && value.type instanceof ArrayType) {
            ArrayType arrayType = (ArrayType) value.type;
            Type innerType = arrayType.getInnerType();

            if (innerType.match(varg.type)) {
                Object[] array = (Object[]) value.data;
                Object target = varg.data;

                for (Object element : array) {
                    if (element.equals(target)) {
                        return new Value(BoolType.instance(), true);
                    }
                }
            }
        }
        return new Value(BoolType.instance(), false);
    }

    private Value appendOp(Value value, Value varg) {
        if (value != null && value.type instanceof ArrayType) {
            ArrayType arrayType = (ArrayType) value.type;
            Type innerType = arrayType.getInnerType();

            if (innerType.match(varg.type)) {
                Object[] originalArray = (Object[]) value.data;
                Object[] newArray = new Object[originalArray.length + 1];
                System.arraycopy(originalArray, 0, newArray, 0, originalArray.length);
                newArray[originalArray.length] = varg.data;

                return new Value(ArrayType.instance(innerType), newArray);
            }
        }
        return null;
    }

    private Value valuesOp(Value value, Value varg) {
        if (value instanceof Map) {
            Map<?, ?> dictionary = (Map<?, ?>) value;

            Type valueType = null;

            for (Object val : dictionary.values()) {
                if (val instanceof Value) {
                    valueType = ((Value) val).type;
                    break;
                }
            }

            List<Object> valuesList = new ArrayList<>();

            for (Object val : dictionary.values()) {
                valuesList.add(val);
            }

            Object[] valuesArray = valuesList.toArray();

            return new Value(ArrayType.instance(valueType), valuesArray);
        } else {
            return null;
        }
    }

    private Value keysOp(Value value, Value varg) {
        if (value instanceof Map) {
            Map<?, ?> dictionary = (Map<?, ?>) value;

            List<Object> keysList = new ArrayList<>();

            for (Object key : dictionary.keySet()) {
                keysList.add(key);
            }

            Object[] keysArray = keysList.toArray();

            Type keyType = null;
            for (Object key : dictionary.keySet()) {
                if (key instanceof Value) {
                    keyType = ((Value) key).type;
                    break;
                }
            }

            Value keysValue = new Value(ArrayType.instance(keyType), keysArray);

            return keysValue;
        } else {
            return null;
        }
    }

    private Value emptyOp(Value value, Value varg) {
        if (arg != null) {
            throw new UnsupportedOperationException("Operação 'empty' não suporta argumentos");
        }
        if (value.type instanceof StringType) {
            String str = (String) value.data;
            return new Value(BoolType.instance(), str == null);
        } else if (value.type instanceof ArrayType) {
            ArrayExpr arrayValue = (ArrayExpr) value.data;
            return new Value(BoolType.instance(), arrayValue == null);
        } else if (value.type instanceof DictType) {
            DictExpr dict = (DictExpr) value.data;
            return new Value(BoolType.instance(), dict == null);
        } else {
            throw new UnsupportedOperationException("Operação 'empty' não suportada para o tipo de valor fornecido");
        }
    }

    private Value countOp(Value value, Value varg) {
        int count = 0;
        if (varg != null) {
            throw new IllegalArgumentException("A função 'count' não aceita argumentos.");
        }
        if (value.type instanceof StringType) {
            String stringValue = (String) value.data;
            count = stringValue.length();
            return new Value(IntType.instance(), count);
        } else if (value.type instanceof ArrayType) {
            ArrayExpr arrayValue = (ArrayExpr) value.data;
            String count2 = arrayValue.toString();
            count = count2.length();
            return new Value(IntType.instance(), count);
        } else {
            throw new IllegalArgumentException("A função 'count' só pode ser aplicada a strings ou arrays.");
        }
    }

}
