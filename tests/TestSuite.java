import java.io.File;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import interpreter.type.Type;
import interpreter.type.composed.*;
import interpreter.type.primitive.*;
import interpreter.value.Value;

/** End-to-end regressions: each program runs in an isolated JVM, with a timeout. */
public class TestSuite {
    private static int passed;
    private static final String JAVA = Paths.get(System.getProperty("java.home"), "bin", "java").toString();
    private static final String CLASSPATH = Paths.get("build/classes").toAbsolutePath().toString();

    public static void main(String[] args) throws Exception {
        System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        System.setErr(new java.io.PrintStream(System.err, true, "UTF-8"));
        examples();
        if (args.length == 0) regressions();
        System.out.println("OK: " + passed + " verificações passaram.");
    }

    private static void examples() throws Exception {
        List<Path> examples = new ArrayList<>();
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(Paths.get("examples"), "*.mswift")) {
            for (Path path : paths) examples.add(path);
        }
        Collections.sort(examples);
        if (examples.isEmpty()) throw new AssertionError("Nenhum exemplo encontrado");
        for (Path path : examples) {
            String name = path.getFileName().toString().replace(".mswift", "");
            Path input = Paths.get("tests/fixtures", name + ".in");
            String stdin = Files.exists(input) ? read(input) : "";
            String expected = read(Paths.get("tests/fixtures", name + ".out"));
            success("example " + name, run(stdin, path.toString()), expected);
        }
    }

    private static void regressions() throws Exception {
        program("precedência e unários", "println(2 + 3 * 4) println((2 + 3) * 4) println(--2) println(!!true) println(7 / 2)", "14\n20\n2\ntrue\n3\n");
        program("comparações", "println('a' < 'b') println(2 >= 2) println(2.5 > 1.5) println(\"a\" == \"a\") println(true != false) println('z' == 'z')", "true\ntrue\ntrue\ntrue\ntrue\ntrue\n");
        program("strings e UTF-8", "let ação: String = \"Olá, ação!\" println(ação + \" ✓\") println('ç')", "Olá, ação! ✓\nç\n");
        program("escapes", "println(\"a\\nb\\t\\\"\\\\\") println('\\n' == toChar(10))", "a\nb\t\"\\\ntrue\n");
        program("comentários e divisão", "/* duas\nlinhas **/ // comentário\nprintln(8/2) // fim", "4\n");
        program("múltiplas declarações", "var a: Int = 1, b: Int = 2, c: Int c = 3 let d: Int = 4, e: Int = 5 println(a+b+c+d+e)", "15\n");
        program("escopo", "let x: Int = 1 { let x: Int = 2 println(x) } println(x)", "2\n1\n");
        program("if e while", "var n: Int = 0 while n < 3 { n = n + 1 } if n == 3 println(n) else println(0)", "3\n");
        program("lógica e curto-circuito", "println(true || false && false) println(false && 1 / 0 == 0) println(true || 1 / 0 == 0)", "true\nfalse\ntrue\n");
        program("ternário preguiçoso", "println(true ? 7 : 1/0) println(false ? 1/0 : true ? 8 : 9)", "7\n8\n");
        program("arrays", "let a: Array<Int> = Array<Int>(1,2) a.append(3) a[0] = 9 println(a.count()) println(a[0]) println(a.contains(3)) println(a.empty()) println(Array<Int>().empty())", "3\n9\ntrue\nfalse\ntrue\n");
        program("índices aninhados", "let a: Array<Array<Int>> = Array<Array<Int>>(Array<Int>(1,2)) a[0][1] = 7 println(a[0][1])", "7\n");
        program("dicionários e chaves por valor", "let d: Dict<String,Int> = Dict<String,Int>(\"a\":1,\"b\":2) d[\"a\"] = 3 d[\"c\"] = 4 println(d.count()) println(d[\"a\"]) println(d[\"b\"]) println(d.keys().contains(\"c\")) println(d.values().contains(4))", "3\n3\n2\ntrue\ntrue\n");
        program("chaves repetidas", "let d: Dict<Char,Int> = Dict<Char,Int>('a':1,'a':2) println(d.count()) println(d['a'])", "1\n2\n");
        program("coleções vazias encadeadas", "println(Dict<Char,Int>().keys().empty()) println(Dict<Char,Int>().values().count()) println(Dict<Char,Int>().empty())", "true\n0\ntrue\n");
        program("strings indexadas", "var s: String = \"abc\" s[1] = 'x' println(s) println(s[0]) println(s.count()) println(s.contains('x')) println(\"\".empty())", "axc\na\n3\ntrue\ntrue\n");
        program("for let e escopo", "for let c: Char in \"ab\" print(toString(c)) for let c: Char in \"cd\" print(toString(c)) println(\"\")", "abcd\n");
        program("for array e variável existente", "var total: Int = 0 for var n: Int in Array<Int>(1,2,3) { n = n + 1 total = total + n } var n: Int for n in Array<Int>(4,5) total = total + n println(total) println(n)", "18\n5\n");
        program("for por snapshot", "let a: Array<Int> = Array<Int>(1,2) for let n: Int in a a.append(n) println(a.count())", "4\n");
        program("conversões numéricas", "println(toFloat(2)+0.5) println(toFloat('A')) println(toInt(2.9)) println(toInt('A')) println(toInt(true)) println(toFloat(\"1.25\")) println(toInt(\"42\"))", "2.5\n65.0\n2\n65\n1\n1.25\n42\n");
        program("conversões booleanas e char", "println(toBool(0)) println(toBool(1.0)) println(toBool(Array<Int>())) println(toBool(Dict<Char,Int>('a':1))) println(toBool(\"false\")) println(toChar(65)) println(toChar(\"ç\")) println(toString(false))", "false\ntrue\nfalse\ntrue\nfalse\nA\nç\nfalse\n");
        program("igualdade de coleções", "println(Array<Int>(1,2) == Array<Int>(1,2)) println(Dict<Char,Int>('a':1) == Dict<Char,Int>('a':1))", "true\ntrue\n");
        program("random", "let r: Float = random() println(r >= 0.0 && r < 1.0)", "true\n");
        program("dump", "dump(42)", "Int(42)\n");
        success("read preserva espaços", source("println(read()) println(read())", "  Olá  \nsegunda\n"), "  Olá  \nsegunda\n");
        success("prompt e read compartilham entrada", run("let s: String = read()\nOlá\nprintln(s)\n", new String[0]), "> > Olá\n> \n");
        Result recovery = run("var broken: Int =\nvar broken: Int = 7\nprintln(broken)\n", new String[0]);
        check("prompt recupera erro de sintaxe", recovery.status == 0 && recovery.out.equals("> > > 7\n> \n") && recovery.err.contains("Fim de arquivo inesperado"));

        failure("constante", "let a: Int = 1 a = 2", "Atribuição em variável constante");
        failure("constante no for", "for let c: Char in \"a\" c = 'b'", "Atribuição em variável constante");
        failure("variável inexistente", "println(x)", "Variável não declarada");
        failure("variável não inicializada", "var x: Int println(x)", "Variável não inicializada");
        failure("inicialização reinicia bloco", "var i: Int = 0 while i < 2 { var x: Int if i == 0 x = 1 println(x) i = i + 1 }", "Variável não inicializada");
        failure("redeclarada", "var x: Int var x: Int", "Variável já declarada");
        failure("tipo atribuído", "var x: Int = \"a\"", "Tipo inválido");
        failure("tipo de array", "Array<Int>(\"a\")", "Tipo inválido");
        failure("tipo de chave", "Dict<Char,Int>(1:2)", "Tipo inválido");
        failure("tipo de valor", "Dict<Char,Int>('a':true)", "Tipo inválido");
        failure("tipo append", "Array<Int>().append(true)", "Tipo inválido");
        failure("tipo contains", "Array<Int>().contains(true)", "Tipo inválido");
        failure("tipo índice", "let a: Array<Int> = Array<Int>(1) println(a[true])", "Tipo inválido");
        failure("índice negativo", "let a: Array<Int> = Array<Int>(1) println(a[-1])", "Operação inválida");
        failure("índice fora", "let a: Array<Int> = Array<Int>(1) println(a[1])", "Operação inválida");
        failure("índice string fora", "let s: String = \"a\" println(s[1])", "Operação inválida");
        failure("chave ausente", "let d: Dict<Char,Int> = Dict<Char,Int>() println(d['x'])", "Operação inválida");
        failure("tipo ao inserir", "let d: Dict<Char,Int> = Dict<Char,Int>() d['x'] = true", "Tipo inválido");
        failure("string constante", "let s: String = \"a\" s[0] = 'b'", "Atribuição em variável constante");
        failure("divisão inteira por zero", "println(1/0)", "Operação inválida");
        failure("divisão float por zero", "println(1.0/0.0)", "Operação inválida");
        failure("ternário não booleano", "println(1 ? 2 : 3)", "Tipo inválido");
        failure("if não booleano", "if 1 println(2)", "Tipo inválido");
        failure("while não booleano", "while 1 println(2)", "Tipo inválido");
        failure("for com tipo incompatível", "for let n: Int in Array<Char>() println(n)", "Tipo inválido");
        failure("variável for fora do escopo", "for let c: Char in \"a\" print(c) println(c)", "Variável não declarada");
        failure("comentário não terminado", "/* sem fim", "Fim de arquivo inesperado");
        failure("string não terminada", "println(\"sem fim)", "Fim de arquivo inesperado");
        failure("char inválido", "println('ab')", "Lexema inválido");
        failure("escape inválido", "println(\"\\q\")", "Lexema inválido");
        failure("overflow literal", "println(99999999999999999999)", "Lexema inválido");
        failure("método inválido", "println(\"a\".bogus())", "Lexema não esperado");
        failure("receptor inválido", "println(1.count())", "Operação inválida");
        failure("cast inválido", "println(toInt(\"abc\"))", "Operação inválida");
        failure("char fora da faixa", "println(toChar(65536))", "Operação inválida");
        failure("EOF em read", "println(read())", "Fim de arquivo inesperado");
        Result line = source("/* linha 1\nlinha 2 */\nprintln(@)", "");
        check("linha após comentário", line.status == 1 && line.err.startsWith("03: Lexema inválido"));
        Result missing = run("", "tests/arquivo-inexistente.mswift");
        check("arquivo ausente", missing.status == 1 && missing.err.contains("Erro de arquivo"));
        Result usage = run("", "one", "two");
        check("argumentos inválidos", usage.status == 2 && usage.out.contains("Uso:"));
        Result help = run("", "--help");
        check("ajuda", help.status == 0 && help.out.contains("Uso:") && help.err.isEmpty());
        Type nested = ComposedType.instance(Type.Category.Array, ArrayType.instance(IntType.instance()));
        Value key1 = new Value(nested, Arrays.asList(new Value(ArrayType.instance(IntType.instance()), Arrays.asList(new Value(IntType.instance(), 1)))));
        Value key2 = new Value(nested, Arrays.asList(new Value(ArrayType.instance(IntType.instance()), Arrays.asList(new Value(IntType.instance(), 1)))));
        check("igualdade e hash de tipos compostos", key1.equals(key2) && key1.hashCode() == key2.hashCode());
        check("fábrica de dicionário", ComposedType.instance(Type.Category.Dict, CharType.instance(), IntType.instance()).match(DictType.instance(CharType.instance(), IntType.instance())));
    }

    private static void program(String name, String code, String expected) throws Exception {
        success(name, source(code, ""), expected);
    }

    private static void failure(String name, String code, String message) throws Exception {
        Result result = source(code, "");
        check(name, result.status == 1 && result.err.contains(message) && !result.err.contains("Exception") && !result.err.contains("\tat "));
    }

    private static void success(String name, Result result, String expected) {
        if (result.status != 0 || !result.err.isEmpty() || !result.out.equals(expected))
            throw new AssertionError(name + "\nexit=" + result.status + "\nexpected=" + expected + "\nstdout=" + result.out + "\nstderr=" + result.err);
        check(name, true);
    }

    private static void check(String name, boolean condition) {
        if (!condition) throw new AssertionError(name);
        passed++;
        System.out.println("PASS " + name);
    }

    private static Result source(String code, String input) throws Exception {
        Path path = Files.createTempFile("miniswift-", ".mswift");
        try {
            Files.write(path, code.getBytes(StandardCharsets.UTF_8));
            return run(input, path.toString());
        } finally { Files.deleteIfExists(path); }
    }

    private static Result run(String input, String... args) throws Exception {
        List<String> command = new ArrayList<>(Arrays.asList(JAVA, "-ea", "-Dfile.encoding=UTF-8", "-cp", CLASSPATH, "msi"));
        command.addAll(Arrays.asList(args));
        Path stdout = Files.createTempFile("miniswift-out-", ".txt");
        Path stderr = Files.createTempFile("miniswift-err-", ".txt");
        Process process = null;
        try {
            process = new ProcessBuilder(command).redirectOutput(stdout.toFile()).redirectError(stderr.toFile()).start();
            try (OutputStream stdin = process.getOutputStream()) { stdin.write(input.getBytes(StandardCharsets.UTF_8)); }
            if (!process.waitFor(10, TimeUnit.SECONDS)) throw new AssertionError("Timeout: " + command);
            return new Result(process.exitValue(), read(stdout), read(stderr));
        } finally {
            if (process != null && process.isAlive()) { process.destroyForcibly(); process.waitFor(); }
            Files.deleteIfExists(stdout);
            Files.deleteIfExists(stderr);
        }
    }

    private static String read(Path path) throws Exception {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8).replace("\r\n", "\n");
    }

    private static class Result {
        final int status;
        final String out, err;
        Result(int status, String out, String err) { this.status = status; this.out = out; this.err = err; }
    }
}
