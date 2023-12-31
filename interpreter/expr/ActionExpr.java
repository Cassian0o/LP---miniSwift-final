package interpreter.expr;

import java.util.Scanner;
import java.util.Random;

import error.InternalException;
import interpreter.type.primitive.FloatType;
import interpreter.type.primitive.StringType;
import interpreter.value.Value;

public class ActionExpr extends Expr {
    
    public static enum Op {
        Read,
        Random
    }

    private static Scanner in = new Scanner(System.in);

    private Op op;

    public ActionExpr(int line, Op op) {
        super(line);
        this.op = op;
    }

    @Override
    public Value expr() {
        switch (op) {
            case Read:
                return new Value(StringType.instance(), in.nextLine().trim());
            case Random:
                Random random = new Random();
                float randomFloat = random.nextFloat();
                return new Value(FloatType.instance(), randomFloat);
                //throw new InternalException("Implement me!");
            default:
                throw new InternalException("Unreachable");
        }
    }

}
