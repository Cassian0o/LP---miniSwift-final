package interpreter.expr;

import interpreter.value.Value;

public class CastExpr extends Expr {

    public static enum CastOp{
        ToBoolOp,
        ToIntOp,
        ToFloatOp,
        ToCharOp,
        ToStringOp
    }

    private CastOp op;
    private Expr expr;

    protected CastExpr(int line, CastOp op, Expr expr) {
        super(line);
        this.op = op;
        this.expr = expr;
        
    }

    @Override
    public Value expr() {
        Value value = expr.expr();
        Value ret = null;

        switch(op){
            case ToBoolOp:
                ret = toBoolOp(value);
                break;
            case ToIntOp:
                ret = toIntOp(value);
                break;
            case ToFloatOp:
                ret = toFloatOp(value);
                break;
            case ToCharOp:
                ret = toCharOp(value);
                break;
            case ToStringOp:
                ret = toStringOP(value);
                break;
            default:
                throw new InternalError("unreachable");
        }
        
        return ret;
    }

    private Value toStringOP(Value value) {
        //Tem que completar
        return null;
    }

    private Value toCharOp(Value value) {
        //Tem que completar
        return null;
    }

    private Value toFloatOp(Value value) {
        //Tem que completar
        return null;
    }

    private Value toIntOp(Value value) {
        //Tem que completar
        return null;
    }

    private Value toBoolOp(Value value) {
        //Tem que completar
        return null;
    }
    
}
