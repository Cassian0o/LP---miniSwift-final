package interpreter.expr;

import java.util.Random;
import error.LanguageException;
import interpreter.Interpreter;
import interpreter.type.primitive.FloatType;
import interpreter.type.primitive.StringType;
import interpreter.value.Value;

public class ActionExpr extends Expr {
    public enum Op { Read, Random }
    private static final Random random = new Random();
    private final Op op;

    public ActionExpr(int line, Op op) {
        super(line);
        this.op = op;
    }

    @Override
    public Value expr() {
        switch (op) {
            case Read:
                String line = Interpreter.readLine();
                if (line == null)
                    throw LanguageException.instance(getLine(), LanguageException.Error.UnexpectedEOF);
                return new Value(StringType.instance(), line);
            case Random:
                return new Value(FloatType.instance(), random.nextFloat());
            default:
                throw new IllegalStateException("Unknown action: " + op);
        }
    }
}
