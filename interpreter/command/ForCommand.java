package interpreter.command;

import error.LanguageException;
import interpreter.expr.Expr;
import interpreter.expr.Variable;
import interpreter.type.primitive.BoolType;
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
        Value initialValue = expr.expr(); 
        BoolType boolType = BoolType.instance();

        if (!boolType.match(initialValue.type)) {
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, initialValue.type.toString());
        }

        while (true) {
            Value conditionValue = expr.expr(); 
            if (!boolType.match(conditionValue.type)) {
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, conditionValue.type.toString());
            }

            boolean b = (Boolean) conditionValue.data;
            if (!b) {
                break; 
            }

            cmds.execute(); 

            

            conditionValue = expr.expr();
            if (!boolType.match(conditionValue.type)) {
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, conditionValue.type.toString());
            }

            b = (Boolean) conditionValue.data;
            if (!b) {
                break; 
            }
        }
    }
}
