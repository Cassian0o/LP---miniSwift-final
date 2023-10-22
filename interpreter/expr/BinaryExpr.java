package interpreter.expr;

import error.LanguageException;
import interpreter.type.primitive.BoolType;
import interpreter.type.primitive.FloatType;
import interpreter.type.primitive.IntType;
import interpreter.value.Value;

public class BinaryExpr extends Expr {

    public static enum Op {
        And,
        Or,
        Equal,
        NotEqual,
        LowerThan,
        LowerEqual,
        GreaterThan,
        GreaterEqual,
        Add,
        Sub,
        Mul,
        Div
    }

    private Expr left;
    private Op op;
    private Expr right;
    
    public BinaryExpr(int line, Expr left, Op op, Expr right) {
        super(line);
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public Value expr() {
        Value lvalue = left.expr();
        Value rvalue = right.expr();

        Value ret = null;
        switch (op) {
            case And:
                ret = andOp(lvalue, rvalue);
                break;
            case Or:
                ret = orOp(lvalue, rvalue);
                break;
            case Equal:
                ret = equalOp(lvalue, rvalue);
                break;
            case NotEqual:
                ret = notEqualOp(lvalue, rvalue);
                break;
            case LowerThan:
                ret = lowerThanOp(lvalue, rvalue);
                break;
            case LowerEqual:
                ret = lowerEqualOp(lvalue, rvalue);
                break;
            case GreaterThan:
                ret = greaterThanOp(lvalue, rvalue);
                break;
            case GreaterEqual:
                ret = greaterEqualOp(lvalue, rvalue);
                break;
            case Add:
                ret = addOp(lvalue, rvalue);
                break;
            case Sub:
                ret = subOp(lvalue, rvalue);
                break;
            case Mul:
                ret = mulOp(lvalue, rvalue);
                break;
            case Div:
                ret = divOp(lvalue, rvalue);
                break;
            default:
                throw new InternalError("unreachable");
        }

        return ret;
    }

    private Value andOp(Value lvalue, Value rvalue) {
        BoolType boolType = BoolType.instance();
            if (boolType.match(lvalue.type)) {
                if (boolType.match(rvalue.type)) {
                    boolean m = (Boolean) lvalue.data;
                    boolean n = (Boolean) rvalue.data;
                    Value v = new Value(BoolType.instance(), (m && n));
                    return v;
                } else {
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, rvalue.type.toString());
                }
            } else {
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, lvalue.type.toString());
            }
    }

    private Value orOp(Value lvalue, Value rvalue) {
        BoolType boolType = BoolType.instance();
            if (boolType.match(lvalue.type)) {
                if (boolType.match(rvalue.type)) {
                    boolean m = (Boolean) lvalue.data;
                    boolean n = (Boolean) rvalue.data;
                    Value v = new Value(BoolType.instance(), (m || n));
                    return v;
                } else {
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, rvalue.type.toString());
                }
            } else {
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, lvalue.type.toString());
            }
    }

    private Value equalOp(Value lvalue, Value rvalue) {
        IntType intType = IntType.instance();
        if (intType.match(lvalue.type)) {
            if (intType.match(rvalue.type)) {
                int m = (Integer) lvalue.data;
                int n = (Integer) rvalue.data;
                Value v = new Value(BoolType.instance(), (m == n));
                return v;
            } else {
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, rvalue.type.toString());
            }
        } else {
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, lvalue.type.toString());
        }
    }

    private Value notEqualOp(Value lvalue, Value rvalue) {
        IntType intType = IntType.instance();
        if (intType.match(lvalue.type)) {
            if (intType.match(rvalue.type)) {
                int m = (Integer) lvalue.data;
                int n = (Integer) rvalue.data;
                Value v = new Value(BoolType.instance(), (m != n));
                return v;
            } else {
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, rvalue.type.toString());
            }
        } else {
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, lvalue.type.toString());
        }
    }

    private Value lowerThanOp(Value lvalue, Value rvalue) {
        IntType intType = IntType.instance();
        if (intType.match(lvalue.type)) {
            if (intType.match(rvalue.type)) {
                int m = (Integer) lvalue.data;
                int n = (Integer) rvalue.data;
                Value v = new Value(BoolType.instance(), (m < n));
                return v;
            } else {
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, rvalue.type.toString());
            }
        } else {
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, lvalue.type.toString());
        }
    }

    private Value lowerEqualOp(Value lvalue, Value rvalue) {
        IntType intType = IntType.instance();
        if (intType.match(lvalue.type)) {
            if (intType.match(rvalue.type)) {
                int m = (Integer) lvalue.data;
                int n = (Integer) rvalue.data;
                Value v = new Value(BoolType.instance(), (m <= n));
                return v;
            } else {
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, rvalue.type.toString());
            }
        } else {
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, lvalue.type.toString());
        }
    }

    private Value greaterThanOp(Value lvalue, Value rvalue) {
        IntType intType = IntType.instance();
        if (intType.match(lvalue.type)) {
            if (intType.match(rvalue.type)) {
                int m = (Integer) lvalue.data;
                int n = (Integer) rvalue.data;
                Value v = new Value(BoolType.instance(), (m > n));
                return v;
            } else {
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, rvalue.type.toString());
            }
        } else {
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, lvalue.type.toString());
        }
    }

    private Value greaterEqualOp(Value lvalue, Value rvalue) {
        IntType intType = IntType.instance();
        if (intType.match(lvalue.type)) {
            if (intType.match(rvalue.type)) {
                int m = (Integer) lvalue.data;
                int n = (Integer) rvalue.data;
                Value v = new Value(BoolType.instance(), (m >= n));
                return v;
            } else {
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, rvalue.type.toString());
            }
        } else {
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, lvalue.type.toString());
        }
    }

    private Value addOp(Value lvalue, Value rvalue) {
        IntType intType = IntType.instance();
        FloatType floatType = FloatType.instance();
        if (intType.match(lvalue.type)) {
            if (intType.match(rvalue.type)) {
                int m = (Integer) lvalue.data;
                int n = (Integer) rvalue.data;
                Value v = new Value(intType, (m + n));
                return v;
            } else {
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, rvalue.type.toString());
            }
        } else if (floatType.match(lvalue.type)) {
            if (floatType.match(rvalue.type)) {
                float m = (Float) lvalue.data;
                float n = (Float) rvalue.data;

                Value v = new Value(floatType, (m + n));
                return v;
            } else {
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, rvalue.type.toString());
            }
        } else {
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, lvalue.type.toString());
        }
    }

    private Value subOp(Value lvalue, Value rvalue) {
        IntType intType = IntType.instance();
        FloatType floatType = FloatType.instance();
        if (intType.match(lvalue.type)) {
            if (intType.match(rvalue.type)) {
                int m = (Integer) lvalue.data;
                int n = (Integer) rvalue.data;
                Value v = new Value(intType, (m - n));
                return v;
            } else {
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, rvalue.type.toString());
            }
        } else if (floatType.match(lvalue.type)) {
            if (floatType.match(rvalue.type)) {
                float m = (Float) lvalue.data;
                float n = (Float) rvalue.data;

                Value v = new Value(floatType, (m - n));
                return v;
            } else {
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, rvalue.type.toString());
            }
        } else {
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, lvalue.type.toString());
        }
    }

    private Value mulOp(Value lvalue, Value rvalue) {
        IntType intType = IntType.instance();
        FloatType floatType = FloatType.instance();
        if (intType.match(lvalue.type)) {
            if (intType.match(rvalue.type)) {
                int m = (Integer) lvalue.data;
                int n = (Integer) rvalue.data;
                Value v = new Value(intType, (m * n));
                return v;
            } else {
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, rvalue.type.toString());
            }
        } else if (floatType.match(lvalue.type)) {
            if (floatType.match(rvalue.type)) {
                float m = (Float) lvalue.data;
                float n = (Float) rvalue.data;

                Value v = new Value(floatType, (m * n));
                return v;
            } else {
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, rvalue.type.toString());
            }
        } else {
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, lvalue.type.toString());
        }
    }

    private Value divOp(Value lvalue, Value rvalue) {
        IntType intType = IntType.instance();
        FloatType floatType = FloatType.instance();
        if (intType.match(lvalue.type)) {
            if (intType.match(rvalue.type)) {
                int m = (Integer) lvalue.data;
                int n = (Integer) rvalue.data;
                Value v = new Value(intType, (m / n));
                return v;
            } else {
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, rvalue.type.toString());
            }
        } else if (floatType.match(lvalue.type)) {
            if (floatType.match(rvalue.type)) {
                float m = (Float) lvalue.data;
                float n = (Float) rvalue.data;

                Value v = new Value(floatType, (m / n));
                return v;
            } else {
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, rvalue.type.toString());
            }
        } else {
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, lvalue.type.toString());
        }
    }

}
