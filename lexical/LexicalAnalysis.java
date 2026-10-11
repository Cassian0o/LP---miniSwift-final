package lexical;

import java.io.InputStream;
import java.io.PushbackReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import error.InternalException;
import interpreter.type.primitive.CharType;
import interpreter.type.primitive.FloatType;
import interpreter.type.primitive.IntType;
import interpreter.type.primitive.StringType;
import interpreter.value.Value;

public class LexicalAnalysis implements AutoCloseable {

    private int line;
    private PushbackReader input;
    private static Map<String, Token.Type> keywords;

    static {
        keywords = new HashMap<String, Token.Type>();

        // SYMBOLS
        keywords.put(".", Token.Type.DOT);
        keywords.put(":", Token.Type.COLON);
        keywords.put(";", Token.Type.SEMICOLON);
        keywords.put(",", Token.Type.COMMA);
        keywords.put("(", Token.Type.OPEN_PAR);
        keywords.put(")", Token.Type.CLOSE_PAR);
        keywords.put("[", Token.Type.OPEN_BRA);
        keywords.put("]", Token.Type.CLOSE_BRA);
        keywords.put("{", Token.Type.OPEN_CUR);
        keywords.put("}", Token.Type.CLOSE_CUR);

        // OPERATORS
        keywords.put("=", Token.Type.ASSIGN);
        keywords.put("?", Token.Type.TERNARY);
        keywords.put("&&", Token.Type.AND);
        keywords.put("||", Token.Type.OR);
        keywords.put("<", Token.Type.LOWER_THAN);
        keywords.put(">", Token.Type.GREATER_THAN);
        keywords.put("<=", Token.Type.LOWER_EQUAL);
        keywords.put(">=", Token.Type.GREATER_EQUAL);
        keywords.put("==", Token.Type.EQUALS);
        keywords.put("!=", Token.Type.NOT_EQUALS);
        keywords.put("+", Token.Type.ADD);
        keywords.put("-", Token.Type.SUB);
        keywords.put("*", Token.Type.MUL);
        keywords.put("/", Token.Type.DIV);
        keywords.put("!", Token.Type.NOT);

        // KEYWORDS
        keywords.put("var", Token.Type.VAR);
        keywords.put("let", Token.Type.LET);
        keywords.put("print", Token.Type.PRINT);
        keywords.put("println", Token.Type.PRINTLN);
        keywords.put("dump", Token.Type.DUMP);
        keywords.put("if", Token.Type.IF);
        keywords.put("else", Token.Type.ELSE);
        keywords.put("while", Token.Type.WHILE);
        keywords.put("for", Token.Type.FOR);
        keywords.put("in", Token.Type.IN);
        keywords.put("Bool", Token.Type.BOOL);
        keywords.put("Int", Token.Type.INT);
        keywords.put("Float", Token.Type.FLOAT);
        keywords.put("Char", Token.Type.CHAR);
        keywords.put("String", Token.Type.STRING);
        keywords.put("Array", Token.Type.ARRAY);
        keywords.put("Dict", Token.Type.DICT);
        keywords.put("false", Token.Type.FALSE);
        keywords.put("true", Token.Type.TRUE);
        keywords.put("read", Token.Type.READ);
        keywords.put("random", Token.Type.RANDOM);
        keywords.put("toBool", Token.Type.TO_BOOL);
        keywords.put("toInt", Token.Type.TO_INT);
        keywords.put("toFloat", Token.Type.TO_FLOAT);
        keywords.put("toChar", Token.Type.TO_CHAR);
        keywords.put("toString", Token.Type.TO_STRING);
        keywords.put("count", Token.Type.COUNT);
        keywords.put("empty", Token.Type.EMPTY);
        keywords.put("keys", Token.Type.KEYS);
        keywords.put("values", Token.Type.VALUES);
        keywords.put("append", Token.Type.APPEND);
        keywords.put("contains", Token.Type.CONTAINS);
    }

    public LexicalAnalysis(InputStream is) {
        input = new PushbackReader(new InputStreamReader(is, StandardCharsets.UTF_8), 2);
        line = 1;
    }

    public void close() {
        try { input.close(); }
        catch (java.io.IOException e) { throw new InternalException("Unable to close file: " + e.getMessage()); }
    }

    public int getLine() { return line; }

    public Token nextToken() {
        int c;
        int start;
        for (;;) {
            c = getc();
            if (Character.isWhitespace(c)) continue;
            start = line;
            if (c != '/') break;
            int next = getc();
            if (next == '/') {
                while ((c = getc()) != -1 && c != '\n') { }
                continue;
            }
            if (next == '*') {
                int previous = 0;
                while ((c = getc()) != -1 && !(previous == '*' && c == '/')) previous = c;
                if (c == -1) return token("/*", Token.Type.UNEXPECTED_EOF, null, start);
                continue;
            }
            ungetc(next);
            return token("/", Token.Type.DIV, null, start);
        }

        if (c == -1) return token("", Token.Type.END_OF_FILE, null, start);
        StringBuilder text = new StringBuilder();
        text.append((char) c);
        if (c == '_' || Character.isLetter(c)) {
            while ((c = getc()) == '_' || Character.isLetterOrDigit(c)) text.append((char) c);
            ungetc(c);
            String name = text.toString();
            return token(name, keywords.getOrDefault(name, Token.Type.NAME), null, start);
        }

        if (Character.isDigit(c)) {
            while (Character.isDigit(c = getc())) text.append((char) c);
            boolean floating = false;
            if (c == '.') {
                int next = getc();
                if (Character.isDigit(next)) {
                    floating = true;
                    text.append('.').append((char) next);
                    while (Character.isDigit(c = getc())) text.append((char) c);
                } else {
                    ungetc(next);
                }
            }
            ungetc(c);
            String number = text.toString();
            try {
                if (floating) {
                    float value = Float.parseFloat(number);
                    if (!Float.isFinite(value)) throw new NumberFormatException();
                    return token(number, Token.Type.FLOAT_LITERAL, new Value(FloatType.instance(), value), start);
                }
                return token(number, Token.Type.INTEGER_LITERAL, new Value(IntType.instance(), Integer.parseInt(number)), start);
            } catch (NumberFormatException e) {
                return token(number, Token.Type.INVALID_TOKEN, null, start);
            }
        }

        if (c == '\'' || c == '"') return quoted(c, start);
        if (c == '=' || c == '!' || c == '<' || c == '>') {
            int next = getc();
            if (next == '=') text.append('=');
            else ungetc(next);
        } else if (c == '&' || c == '|') {
            int next = getc();
            if (next == c) text.append((char) next);
            else ungetc(next);
        }
        String symbol = text.toString();
        return token(symbol, keywords.getOrDefault(symbol, Token.Type.INVALID_TOKEN), null, start);
    }

    private Token quoted(int quote, int start) {
        StringBuilder text = new StringBuilder();
        int c;
        while ((c = getc()) != quote) {
            if (c == -1) return token(text.toString(), Token.Type.UNEXPECTED_EOF, null, start);
            if (c == '\\') {
                c = getc();
                switch (c) {
                    case -1: return token(text.toString(), Token.Type.UNEXPECTED_EOF, null, start);
                    case 'n': c = '\n'; break;
                    case 'r': c = '\r'; break;
                    case 't': c = '\t'; break;
                    case '0': c = '\0'; break;
                    case '\\': case '\'': case '"': break;
                    default: return token("\\" + (char) c, Token.Type.INVALID_TOKEN, null, start);
                }
            }
            text.append((char) c);
        }
        String literal = text.toString();
        if (quote == '"')
            return token(literal, Token.Type.STRING_LITERAL, new Value(StringType.instance(), literal), start);
        if (literal.length() == 1)
            return token(literal, Token.Type.CHAR_LITERAL, new Value(CharType.instance(), literal.charAt(0)), start);
        return token(literal, Token.Type.INVALID_TOKEN, null, start);
    }

    private Token token(String lexeme, Token.Type type, Value literal, int start) {
        Token token = new Token(lexeme, type, literal);
        token.line = start;
        return token;
    }

    private int getc() {
        try {
            int c = input.read();
            if (c == '\n') line++;
            return c;
        } catch (java.io.IOException e) {
            throw new InternalException("Unable to read file: " + e.getMessage());
        }
    }

    private void ungetc(int c) {
        if (c == -1) return;
        try {
            input.unread(c);
            if (c == '\n') line--;
        } catch (java.io.IOException e) {
            throw new InternalException("Unable to unread character: " + e.getMessage());
        }
    }
}
