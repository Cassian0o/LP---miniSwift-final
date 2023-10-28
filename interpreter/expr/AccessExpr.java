package interpreter.expr;

import java.util.List;
import java.util.Map;

import error.LanguageException;
import interpreter.type.Type.Category;
import interpreter.type.composed.DictType;
import interpreter.value.Value;

public class AccessExpr extends SetExpr {
    private SetExpr base;
    private Expr index;

    public AccessExpr(int line, SetExpr base, Expr index) {
        super(line);
        this.base = base;
        this.index = index;
    }

    @Override
    public Value expr() {
        Value value = base.expr();

        if (Category.Array == value.type.getCategory() || Category.String == value.type.getCategory()) {

            List<Value> elements = (List<Value>) value.data;

            int position = (int) index.expr().data;
            Value element = elements.get(position);
            return new Value(element.type, element);
        } else if (Category.Dict == value.type.getCategory()) {

            Map<Expr, Expr> map = (Map<Expr, Expr>) value.data;
            DictType type = (DictType) value.type;
            Expr element = map.get(index);
            return new Value(type.getValueType(), element);
        } else {
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                    value.type.toString());
        }

    }

    public void setValue(Value value) {
        Value value1 = base.expr();

        if (Category.Array == value1.type.getCategory() || Category.String == value1.type.getCategory()) {

            List<Value> elements = (List<Value>) value1.data;
            int position = (int) index.expr().data;
            elements.set(position, value);

        } else if (Category.Dict == value.type.getCategory()) {

            Map<Expr, Expr> map = (Map<Expr, Expr>) value1.data;
            DictType type = (DictType) value1.type;
            DictItem item = (DictItem) value.data;

            if (type.getCategory() == value.type.getCategory()) {
                map.replace(item.key, item.value);
                base = (SetExpr) map;
            } else {
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                        value.type.toString());
            }
        } else {
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                    value.type.toString());
        }

    }

}
