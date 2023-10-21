package interpreter.expr;

import java.util.List;

import interpreter.type.composed.ArrayType;
import interpreter.value.Value;

public class ArrayExpr extends Expr {
    private ArrayType type;
    private List<Expr> items;

    protected ArrayExpr(int line, ArrayType type, List<Expr> items) {
        super(line);
        this.type = type;
        this.items = items;
    }

    @Override
    public Value expr() {
        // Tem que completar
        throw new UnsupportedOperationException("Unimplemented method 'expr'");
    }
    
}
