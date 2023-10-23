package interpreter.expr;


import interpreter.type.composed.ArrayType;
import interpreter.type.composed.DictType;
import interpreter.type.primitive.BoolType;
import interpreter.type.primitive.IntType;
import interpreter.type.primitive.StringType;
import interpreter.value.Value;

public class FunctionExpr extends Expr {
    public static enum FunctionOp{
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
        switch(op){
            case Count:
                ret = countOp(value,varg);
                break;
            case Empty:
                ret = emptyOp(value,varg);
                break;
            case Keys:
                ret = keysOp(value,varg);
                break;
            case Values:
                ret = valuesOp(value,varg);
                break;
            case Append:
                ret = appendOp(value,varg);
                break;
            case Contains:
                ret = containsOp(value,varg);
                break;
            default:
                throw new InternalError("unreachable");
        }

        return ret;
    }

    private Value containsOp(Value value, Value varg) {
        //Tem que completar
        return null;
    }

    private Value appendOp(Value value, Value varg) {
        //Tem que completar
        return null;
    }

    private Value valuesOp(Value value, Value varg) {
        //Tem que completar
        return null;
    }

    private Value keysOp(Value value, Value varg) {
        //Tem que completar
        return null;
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
        return new Value(BoolType.instance(),arrayValue == null);
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
