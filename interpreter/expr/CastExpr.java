package interpreter.expr;


import error.LanguageException;
import interpreter.type.composed.ArrayType;
import interpreter.type.composed.DictType;
import interpreter.type.primitive.BoolType;
import interpreter.type.primitive.CharType;
import interpreter.type.primitive.FloatType;
import interpreter.type.primitive.IntType;
import interpreter.type.primitive.StringType;
import interpreter.value.Value;

public class CastExpr extends Expr {

    public static enum CastOp{
        ToBoolOp,
        ToIntOp,
        ToFloatOp,
        ToCharOp,
        ToStringOp
    }

    private CastOp op;
    private Expr expr;

    public CastExpr(int line, CastOp op, Expr expr) {
        super(line);
        this.op = op;
        this.expr = expr;
        
    }

    @Override
    public Value expr() {
        Value value = expr.expr();
        Value ret = null;

        switch(op){
            case ToBoolOp:
                ret = toBoolOp(value);
                break;
            case ToIntOp:
                ret = toIntOp(value);
                break;
            case ToFloatOp:
                ret = toFloatOp(value);
                break;
            case ToCharOp:
                ret = toCharOp(value);
                break;
            case ToStringOp:
                ret = toStringOP(value);
                break;
            default:
                throw new InternalError("unreachable");
        }
        
        return ret;
    }

    private Value toStringOP(Value value) {
        return new Value(StringType.instance(), value.data.toString());
    }

    private Value toCharOp(Value value) {
        CharType charType = CharType.instance();
        if (charType.match(value.type)) {
            return value;
        } else if(value.type instanceof IntType){
            int intValue = (int) value.data;
            char charValue = (char) intValue;
            return new Value(CharType.instance(),charValue);
        } else{
            return new Value(charType.instance(), '\0');
        }
    }

    private Value toFloatOp(Value value) {
        FloatType floatType = FloatType.instance();
        if (floatType.match(value.type)) {
            return value;
        } else if (value.type instanceof CharType) {
            char charValue = (char) value.data;
            float floatValue = (float) charValue;
            return new Value(IntType.instance(), floatValue);
        } else if (value.type instanceof IntType) {
            int intValue = (int) value.data;
            float floatValue = (float) intValue;
            return new Value(IntType.instance(), floatValue);
        } else {
            return new Value(floatType.instance(), 0.0);
        }
    }

    private Value toIntOp(Value value) {
        IntType intType = IntType.instance();
        if (intType.match(value.type)) {
            return value;
        } else if (value.type instanceof CharType) {
            char charValue = (char) value.data;
            int intValue = (int) charValue;
            return new Value(IntType.instance(), intValue);
        } else if (value.type instanceof FloatType) {
            float floatValue = (float) value.data;
            Integer intValue = (int) floatValue;
            return new Value(IntType.instance(), intValue.intValue());
        } else {
            return new Value(intType.instance(), 0);
        }
    }

    private Value toBoolOp(Value value) {
        //Tem que completar
        BoolType boolType = BoolType.instance();
        if (boolType.match(value.type)) {
            return value;
        } else if (value.type instanceof IntType) {
            int intValue = (int) value.data;
            boolean boolValue = intValue != 0;
            return new Value(BoolType.instance(), boolValue);
        } else if(value.type instanceof CharType){
            char charValue = (char) value.data;
            boolean boolValue = charValue != 0; 
            return new Value(BoolType.instance(), boolValue);
        } else if (value.type instanceof FloatType) {
            float floatValue = (float) value.data;
            boolean boolValue = floatValue != 0.0; 
            return new Value(BoolType.instance(), boolValue);
        } else if (value.type instanceof ArrayType) {
            ArrayExpr arrayValue = (ArrayExpr) value.data;
            boolean boolValue = arrayValue != null;
             return new Value(BoolType.instance(), boolValue);
        } else if (value.type instanceof DictType) {
            DictExpr dictValue = (DictExpr) value.data;
            boolean boolValue = dictValue != null;
            return new Value(BoolType.instance(), boolValue);
        } else {
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, value.type.toString());
        }
    }
}
