package interpreter.expr;

import java.util.List;

import interpreter.type.composed.ArrayType;
import interpreter.type.composed.DictType;
import interpreter.value.Value;

public class DictExpr extends Expr{
    private DictType type;
    private List<DicItem> items;

    protected DictExpr(int line, DictType type, List<DicItem> items) {
        super(line);
        this.type = type;
        this.items = items;
    }

    @Override
    public Value expr() {
        //Tem que completar
        throw new UnsupportedOperationException("Unimplemented method 'expr'");
    }
    
}
