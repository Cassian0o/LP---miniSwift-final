import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import error.LanguageException;
import interpreter.Environment;
import interpreter.Interpreter;
import interpreter.command.Command;
import lexical.LexicalAnalysis;
import syntatic.SyntaticAnalysis;

public class msi {
    public static void main(String[] args) {
        int status = 0;
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
            System.setErr(new PrintStream(System.err, true, "UTF-8"));
            if (args.length == 1 && (args[0].equals("--help") || args[0].equals("-h"))) {
                usage();
            } else if (args.length == 0) {
                runPrompt();
            } else if (args.length == 1) {
                runFile(args[0]);
            } else {
                usage();
                status = 2;
            }
        } catch (LanguageException e) {
            System.err.println(e.getMessage());
            status = 1;
        } catch (IOException e) {
            System.err.println("Erro de arquivo: " + e.getMessage());
            status = 1;
        } catch (RuntimeException e) {
            System.err.println("Erro interno: " + e.getMessage());
            e.printStackTrace(System.err);
            status = 2;
        }
        if (status != 0) System.exit(status);
    }

    private static void usage() {
        System.out.println("Uso: java -cp build/classes msi [arquivo.mswift]");
        System.out.println("Sem arquivo: prompt interativo; --help: exibe esta ajuda.");
    }

    private static void runPrompt() {
        Environment environment = new Environment();
        for (;;) {
            System.out.print("> ");
            System.out.flush();
            String line = Interpreter.readLine();
            if (line == null) {
                System.out.println();
                return;
            }
            Environment candidate = environment.copy();
            try (LexicalAnalysis lex = new LexicalAnalysis(
                    new ByteArrayInputStream(line.getBytes(StandardCharsets.UTF_8)))) {
                Command cmd = new SyntaticAnalysis(lex, candidate).process();
                environment = candidate;
                Interpreter.interpret(cmd);
            } catch (LanguageException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private static void runFile(String filename) throws IOException {
        try (InputStream input = new FileInputStream(filename);
             LexicalAnalysis lex = new LexicalAnalysis(input)) {
            Command cmd = new SyntaticAnalysis(lex).process();
            Interpreter.interpret(cmd);
        }
    }
}
