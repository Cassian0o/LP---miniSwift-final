package interpreter.expr;

import interpreter.value.Value;

public class CastExpr extends Expr {

    public static enum CastOp{
        toBoolOp,
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'expr'");
    }
    
}
