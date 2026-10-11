package interpreter.expr;

import interpreter.value.Value;
import error.LanguageException;
import interpreter.type.primitive.BoolType;

public class ConditionalExpr extends Expr {
    private Expr cond;
    private Expr trueExpr;
    private Expr falseExpr;

    public ConditionalExpr(int line, Expr cond, Expr trueExpr, Expr falseExpr) {
        super(line);
        this.cond = cond;
        this.trueExpr = trueExpr;
        this.falseExpr = falseExpr;
    }



    @Override
    public Value expr() {
        Value conditionValue = cond.expr();
        if (!BoolType.instance().match(conditionValue.type))
            throw LanguageException.instance(getLine(), LanguageException.Error.InvalidType, conditionValue.type.toString());

    if ((boolean) conditionValue.data) {
        return trueExpr.expr();
    } else {
        return falseExpr.expr();
    }
    }
    
}
