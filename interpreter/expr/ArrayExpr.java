package interpreter.expr;

import java.util.ArrayList;
import java.util.List;

import interpreter.type.composed.ArrayType;
import interpreter.value.Value;

public class ArrayExpr extends Expr {
    private ArrayType type;
    private List<Expr> items;

    public ArrayExpr(int line, ArrayType type, List<Expr> items) {
        super(line);
        this.type = type;
        this.items = items;
    }

    @Override
    public Value expr() {
        List<Value> elementValues = new ArrayList<>();

    for (Expr item : items) {
        Value itemValue = item.expr();
        elementValues.add(itemValue);
    }

    Value arrayValue = new Value(type, elementValues);

    return arrayValue;
    }
    
}
