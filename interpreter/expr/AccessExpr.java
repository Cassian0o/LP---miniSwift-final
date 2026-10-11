package interpreter.expr;

import java.util.List;
import java.util.Map;
import error.LanguageException;
import interpreter.type.Type;
import interpreter.type.composed.ArrayType;
import interpreter.type.composed.DictType;
import interpreter.type.primitive.CharType;
import interpreter.value.Value;

public class AccessExpr extends SetExpr {
    private final SetExpr base;
    private final Expr index;

    public AccessExpr(int line, SetExpr base, Expr index) {
        super(line);
        this.base = base;
        this.index = index;
    }

    @Override
    public Value expr() {
        Value container = base.expr();
        Value key = index.expr();
        switch (container.type.getCategory()) {
            case Array:
                List<Value> items = array(container);
                return items.get(position(key, items.size()));
            case String:
                String text = (String) container.data;
                return new Value(CharType.instance(), text.charAt(position(key, text.length())));
            case Dict:
                require(((DictType) container.type).getKeyType(), key);
                Value result = dictionary(container).get(key);
                if (result == null) throw invalidOperation();
                return result;
            default:
                throw invalidOperation();
        }
    }

    @Override
    public void setValue(Value value) {
        Value container = base.expr();
        Value key = index.expr();
        switch (container.type.getCategory()) {
            case Array:
                require(((ArrayType) container.type).getInnerType(), value);
                List<Value> items = array(container);
                items.set(position(key, items.size()), value);
                break;
            case String:
                require(CharType.instance(), value);
                String text = (String) container.data;
                int offset = position(key, text.length());
                base.setValue(new Value(container.type,
                    text.substring(0, offset) + value.data + text.substring(offset + 1)));
                break;
            case Dict:
                DictType type = (DictType) container.type;
                require(type.getKeyType(), key);
                require(type.getValueType(), value);
                dictionary(container).put(key, value);
                break;
            default:
                throw invalidOperation();
        }
    }

    private int position(Value key, int size) {
        if (key.type.getCategory() != Type.Category.Int)
            throw LanguageException.instance(getLine(), LanguageException.Error.InvalidType, key.type.toString());
        int position = (Integer) key.data;
        if (position < 0 || position >= size) throw invalidOperation();
        return position;
    }

    private void require(Type expected, Value value) {
        if (!expected.match(value.type))
            throw LanguageException.instance(getLine(), LanguageException.Error.InvalidType, value.type.toString());
    }

    private LanguageException invalidOperation() {
        return LanguageException.instance(getLine(), LanguageException.Error.InvalidOperation);
    }

    @SuppressWarnings("unchecked")
    private List<Value> array(Value value) { return (List<Value>) value.data; }

    @SuppressWarnings("unchecked")
    private Map<Value, Value> dictionary(Value value) { return (Map<Value, Value>) value.data; }
}
