package interpreter.expr;

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

    protected FunctionExpr(int line, FunctionOp op, Expr expr, Expr arg) {
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
        //Tem que completar
        return null;
    }

    private Value countOp(Value value, Value arg2) {
        //Tem que completar
        return null;
    }
    
}
