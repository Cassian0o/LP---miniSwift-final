package interpreter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import error.InternalException;
import interpreter.command.Command;
import interpreter.expr.Expr;

public class Interpreter {
    private static final BufferedReader input =
        new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

    private Interpreter() { }

    public static String readLine() {
        try { return input.readLine(); }
        catch (IOException e) { throw new InternalException("Unable to read input: " + e.getMessage()); }
    }

    public static void interpret(Command cmd) { cmd.execute(); }

    public static void interpret(Expr expr) { System.out.println(expr.expr()); }
}
