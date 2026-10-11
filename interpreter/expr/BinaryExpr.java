package interpreter.expr;

import error.LanguageException;
import interpreter.type.Type.Category;
import interpreter.type.primitive.BoolType;
import interpreter.value.Value;

public class BinaryExpr extends Expr {
    public enum Op {
        And, Or, Equal, NotEqual, LowerThan, LowerEqual, GreaterThan, GreaterEqual, Add, Sub, Mul, Div
    }

    private final Expr left;
    private final Op op;
    private final Expr right;

    public BinaryExpr(int line, Expr left, Op op, Expr right) {
        super(line);
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public Value expr() {
        Value a = left.expr();
        if (op == Op.And || op == Op.Or) {
            requireBool(a);
            boolean condition = (Boolean) a.data;
            if ((op == Op.And && !condition) || (op == Op.Or && condition))
                return bool(condition);
            Value b = right.expr();
            requireBool(b);
            return b;
        }

        Value b = right.expr();
        if (!a.type.match(b.type))
            throw LanguageException.instance(getLine(), LanguageException.Error.InvalidType, b.type.toString());
        Category category = a.type.getCategory();
        if (op == Op.Equal || op == Op.NotEqual) {
            boolean equal = category == Category.Float ? (float) a.data == (float) b.data : a.equals(b);
            return bool(op == Op.Equal ? equal : !equal);
        }

        if (op == Op.LowerThan || op == Op.LowerEqual || op == Op.GreaterThan || op == Op.GreaterEqual) {
            if (category != Category.Int && category != Category.Float && category != Category.Char && category != Category.String)
                throw invalidOperation();
            int comparison = compare(a, b);
            switch (op) {
                case LowerThan: return bool(comparison < 0);
                case LowerEqual: return bool(comparison <= 0);
                case GreaterThan: return bool(comparison > 0);
                case GreaterEqual: return bool(comparison >= 0);
                default: throw new IllegalStateException();
            }
        }

        if (category == Category.String && op == Op.Add)
            return new Value(a.type, (String) a.data + (String) b.data);
        if (category == Category.Int) {
            int x = (Integer) a.data, y = (Integer) b.data;
            switch (op) {
                case Add: return new Value(a.type, x + y);
                case Sub: return new Value(a.type, x - y);
                case Mul: return new Value(a.type, x * y);
                case Div:
                    if (y == 0) throw invalidOperation();
                    return new Value(a.type, x / y);
                default: throw invalidOperation();
            }
        }
        if (category == Category.Float) {
            float x = (Float) a.data, y = (Float) b.data;
            switch (op) {
                case Add: return new Value(a.type, x + y);
                case Sub: return new Value(a.type, x - y);
                case Mul: return new Value(a.type, x * y);
                case Div:
                    if (y == 0.0f) throw invalidOperation();
                    return new Value(a.type, x / y);
                default: throw invalidOperation();
            }
        }
        throw invalidOperation();
    }

    @SuppressWarnings("unchecked")
    private int compare(Value a, Value b) {
        return ((Comparable<Object>) a.data).compareTo(b.data);
    }

    private void requireBool(Value value) {
        if (!BoolType.instance().match(value.type))
            throw LanguageException.instance(getLine(), LanguageException.Error.InvalidType, value.type.toString());
    }

    private Value bool(boolean value) { return new Value(BoolType.instance(), value); }

    private LanguageException invalidOperation() {
        return LanguageException.instance(getLine(), LanguageException.Error.InvalidOperation);
    }
}
