package interpreter.expr;

public class DictItem {

    public Expr key;
    public Expr value;
    
    public DictItem(Expr key, Expr value) {
        this.key = key;
        this.value = value;
    }
    public void setKey(Expr key) {
        this.key = key;
    }
    public Expr getKey() {
        return key;
    }
    public void setValue(Expr value) {
        this.value = value;
    }
    public Expr getValue() {
        return value;
    }
}
