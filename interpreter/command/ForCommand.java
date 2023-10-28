package interpreter.command;

import java.util.List;

import error.LanguageException;
import interpreter.expr.Expr;
import interpreter.expr.Variable;
import interpreter.type.Type.Category;
import interpreter.value.Value;

public class ForCommand extends Command {

    private Variable var;
    private Expr expr;
    private Command cmds;

    public ForCommand(int line, Variable var, Expr expr, Command cmds) {
        super(line);
        this.var = var;
        this.expr = expr;
        this.cmds = cmds;
    }

    @Override
    public void execute() {
        Value array = expr.expr();
        List<?> list;
        char[] string;
        if (array.type.getCategory() == Category.Array) {
            list = (List<?>) array.data;
            for (Object iterable : list) {
                var.setValue(new Value(var.getType(), iterable));
                cmds.execute();
            }
        } else if (array.type.getCategory() == Category.String && (var.getType().getCategory() == Category.Char)) {
            string = ((String) array.data).toCharArray();
            for (char ch : string) {
                var.setValue(new Value(var.getType(), ch));
                cmds.execute();
            }
        } else {
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                    array.type.toString());
        }

    }
}
