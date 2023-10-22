package interpreter.expr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import error.LanguageException;
import interpreter.type.composed.ArrayType;
import interpreter.type.composed.DictType;
import interpreter.type.primitive.BoolType;
import interpreter.type.primitive.CharType;
import interpreter.type.primitive.FloatType;
import interpreter.type.primitive.IntType;
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

    protected CastExpr(int line, CastOp op, Expr expr) {
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
        //Tem que completar
        return null;
    }

    private Value toCharOp(Value value) {
        //Tem que completar
        return null;
    }

    private Value toFloatOp(Value value) {
        //Tem que completar
        return null;
    }

    private Value toIntOp(Value value) {
        //Tem que completar
        return null;
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
            // List<Value> arrayValue = (List<Value>) value.data;
            // boolean boolValue = !arrayValue.isEmpty();
            // return new Value(BoolType.instance(), boolValue);
            return null;
        } else if (value.type instanceof DictType) {
            // Map<Value, Value> dictValue = (Map<Value, Value>) value.data;
            // boolean boolValue = !dictValue.isEmpty();
            // return new Value(BoolType.instance(), boolValue);
            return null;
        } else {
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, value.type.toString());
        }
    }
}
