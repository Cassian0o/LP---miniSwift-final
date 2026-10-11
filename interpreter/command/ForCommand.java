package interpreter.command;

import java.util.ArrayList;
import java.util.List;
import error.LanguageException;
import interpreter.expr.Expr;
import interpreter.expr.Variable;
import interpreter.type.Type.Category;
import interpreter.type.composed.ArrayType;
import interpreter.type.primitive.CharType;
import interpreter.value.Value;

public class ForCommand extends Command {
    private final Variable var;
    private final Expr expr;
    private final Command cmds;
    private final boolean initialize;

    public ForCommand(int line, Variable var, Expr expr, Command cmds, boolean initialize) {
        super(line);
        this.var = var;
        this.expr = expr;
        this.cmds = cmds;
        this.initialize = initialize;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute() {
        Value iterable = expr.expr();
        List<Value> items = new ArrayList<>();
        if (iterable.type.getCategory() == Category.Array) {
            if (!var.getType().match(((ArrayType) iterable.type).getInnerType()))
                throw invalidType(iterable);
            items.addAll((List<Value>) iterable.data);
        } else if (iterable.type.getCategory() == Category.String && var.getType().getCategory() == Category.Char) {
            for (char ch : ((String) iterable.data).toCharArray())
                items.add(new Value(CharType.instance(), ch));
        } else {
            throw invalidType(iterable);
        }
        for (Value item : items) {
            if (initialize) var.initialize(item);
            else var.setValue(item);
            cmds.execute();
        }
    }

    private LanguageException invalidType(Value value) {
        return LanguageException.instance(getLine(), LanguageException.Error.InvalidType, value.type.toString());
    }
}
