package interpreter.expr;

import java.util.List;

import error.LanguageException;
import interpreter.type.Type;
import interpreter.type.Type.Category;
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

            List<DictItem> listDictItems = (List<DictItem>) value.data;
            for(DictItem item : listDictItems){
                if(item.getKey().expr().data == index.expr().data){
                    Type typeValue = item.getValue().expr().type;
                    return new Value(typeValue, item.getValue().expr());
                }
            }
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation,index.expr().data.toString() + " not found in Dict.");
           
        } else {
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                    value.type.toString() + "Access Error1");
        }
        
    }

    @Override
    public void setValue(Value value) {
        Value value1 = base.expr();
        if (Category.Array == value1.type.getCategory() || Category.String == value1.type.getCategory()) {

            List<Value> elements = (List<Value>) value1.data;
            int position = (int) index.expr().data;
            elements.set(position, value);

        } else if (Category.Dict == value1.type.getCategory()) {
            List<DictItem> listDictItems = (List<DictItem>) value1.data;
            for(DictItem item : listDictItems){
                if(item.getKey() == index.expr().data){
                    DictItem dictItemOld = (DictItem) item.getKey().expr().data;
                    listDictItems.remove(dictItemOld);
                    Expr keyExpr = item.getKey();
                    DictItem dictItem = new DictItem(keyExpr,null);
                    Expr exprVal = new ConstExpr(0,value);
                    dictItem.setValue(exprVal);
                    listDictItems.add(dictItem);

                }
            }
        } else {
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                    value.type.toString() + "AccessError3");
        }

    }
    
}