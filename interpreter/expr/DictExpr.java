package interpreter.expr;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import interpreter.type.composed.DictType;
import interpreter.value.Value;
import error.LanguageException;

public class DictExpr extends Expr{
    private DictType type;
    private List<DictItem> items;

    public DictExpr(int line, DictType type, List<DictItem> items) {
        super(line);
        this.type = type;
        this.items = items;
    }

    @Override
    public Value expr() {
        Map<Value, Value> dictMap = new LinkedHashMap<>();
        for (DictItem item : items) {
            Value key = item.key.expr();     
            Value value = item.value.expr(); 
            if (!type.getKeyType().match(key.type))
                throw LanguageException.instance(getLine(), LanguageException.Error.InvalidType, key.type.toString());
            if (!type.getValueType().match(value.type))
                throw LanguageException.instance(getLine(), LanguageException.Error.InvalidType, value.type.toString());
            dictMap.put(key, value);
        }
        return new Value(type, dictMap);
    }
}
