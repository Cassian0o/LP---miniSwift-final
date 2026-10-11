# miniSwift

Interpretador educacional de uma linguagem inspirada em Swift, implementado em Java. O projeto faz análise léxica, análise sintática por descida recursiva e execução de uma árvore de comandos e expressões.

**Aluno/autor:** Marcelo Soares Cassiano.

O miniSwift possui sintaxe própria e um subconjunto de recursos de Swift. Arquivos de entrada usam a extensão `.mswift`. Não é necessário instalar Swift, Maven, Gradle ou bibliotecas externas.

## Sumário

- [Requisitos](#requisitos)
- [Instalação e compilação](#instalação-e-compilação)
- [Execução](#execução)
- [Exemplos do repositório](#exemplos-do-repositório)
- [Referência da linguagem](#referência-da-linguagem)
- [Testes e integração contínua](#testes-e-integração-contínua)
- [Organização e funcionamento](#organização-e-funcionamento)
- [Correções realizadas](#correções-realizadas)
- [Limitações](#limitações)
- [Solução de problemas](#solução-de-problemas)

## Requisitos

- **JDK 8 ou superior**, com os comandos `java`, `javac` e `jar` disponíveis no `PATH`, ou com `JAVA_HOME` apontando para o JDK.
- PowerShell no Windows; shell POSIX no Linux/macOS.
- Git para clonar o repositório, ou um ZIP do código-fonte.

Um JRE permite executar o JAR pronto, mas não compilar o código. Confira sua instalação:

```console
java -version
javac -version
```

O bytecode produzido pelos scripts tem como alvo Java 8. A suíte foi validada localmente no Windows com JDK 21 e Java 8.

## Instalação e compilação

```console
git clone https://github.com/Cassian0o/LP---miniSwift-final.git
cd LP---miniSwift-final
```

### Windows — PowerShell

```powershell
.\build.ps1
```

Para compilar e testar tudo de uma vez:

```powershell
.\build.ps1 -Test
```

Se a política de execução impedir a abertura do script, execute-o em um processo separado:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\build.ps1 -Test
```

### Linux/macOS

```sh
sh build.sh
sh build.sh --test
```

Ambos os scripts geram `build/classes/` e **`build/miniSwift.jar`**. O JAR contém o interpretador; as classes de teste ficam separadas em `build/test-classes/`.

Também é possível compilar diretamente, a partir da raiz:

```console
javac -encoding UTF-8 -d build/classes msi.java
```

Nesse comando manual, o compilador encontra os demais arquivos Java pelos pacotes importados e usa o alvo padrão do JDK instalado.

## Execução

### Executar um arquivo

```console
java -jar build/miniSwift.jar examples/hello.mswift
```

Alternativa usando as classes compiladas:

```console
java -cp build/classes msi examples/test.mswift
```

Os caminhos de arquivos são relativos ao diretório atual. Coloque caminhos com espaços entre aspas.

### Prompt interativo

```console
java -jar build/miniSwift.jar
```

Exemplo de sessão:

```text
> var n: Int = 2
> println(n + 3)
5
> n = 10
> println(n)
10
```

As variáveis persistem durante a sessão. Cada entrada deve conter comandos completos em **uma linha**; blocos podem ser escritos na mesma linha. Expressões avulsas são avaliadas, mas use `println(...)` ou `dump(...)` para exibir seu resultado.

`read()` consome a próxima linha da mesma entrada padrão. Para sair, envie EOF: `Ctrl+D` em terminais Unix ou `Ctrl+Z` seguido de Enter no Windows. Erros da linguagem são exibidos, e o prompt continua aceitando comandos. Uma linha com erro sintático não registra suas novas declarações.

### Ajuda e códigos de saída

```console
java -jar build/miniSwift.jar --help
```

| Código | Significado |
| --- | --- |
| `0` | Execução concluída, ajuda exibida ou encerramento normal do prompt. |
| `1` | Erro da linguagem, arquivo ausente ou erro de leitura do arquivo. |
| `2` | Argumentos inválidos ou falha interna inesperada. |

Erros são escritos em `stderr`, com a linha quando se trata de um erro da linguagem. No prompt, erros recuperáveis não encerram a sessão. Alterações já executadas antes de um erro em tempo de execução não são desfeitas.

## Exemplos do repositório

Os três exemplos originais são preservados e executados pela suíte com comparação da saída esperada, `stderr` vazio e código de saída `0`.

| Arquivo | O que demonstra | Resultado |
| --- | --- | --- |
| `examples/hello.mswift` | `print` de uma string. | `hello world!`, sem quebra de linha. |
| `examples/test.mswift` | `Char`, constante, comparação e `if`. | `certissimo`, seguido de quebra de linha. |
| `examples/anagrama.mswift` | Entrada, strings, laços, dicionário e ternário. | Reconhece anagramas e relata diferenças. |

### Anagramas

```console
java -jar build/miniSwift.jar examples/anagrama.mswift
```

Digite, nesta ordem, `amor`, `roma` e `mar`, uma entrada por linha:

```text
Entre com uma palavra: amor
Entre com um anagrama de 'amor': roma
  Okay
Entre com um anagrama de 'amor': mar
  Falha: o(1)
```

`Falha: o(1)` é o resultado normal da comparação: falta uma letra `o`. O programa termina ao receber um candidato que não é anagrama. Ele considera apenas `A-Z` e `a-z`, distingue maiúsculas de minúsculas e ignora os demais caracteres na contagem. Um EOF antes dessa condição é um erro de entrada.

As entradas automatizadas ficam em `tests/fixtures/anagrama.in`; a saída correspondente fica em `tests/fixtures/anagrama.out`. A saída capturada não contém o eco das palavras digitadas pelo terminal.

Para validar somente os exemplos:

```powershell
.\build.ps1 -Examples
```

```sh
sh build.sh --examples
```

## Referência da linguagem

### Tipos, declarações e escopo

| Tipo | Representação |
| --- | --- |
| `Bool` | `true` ou `false`. |
| `Int` | Inteiro Java de 32 bits. |
| `Float` | Ponto flutuante Java de 32 bits; literais como `1.5`. |
| `Char` | Um caractere UTF-16 entre aspas simples, como `'a'` ou `'ç'`. |
| `String` | Texto entre aspas duplas, como `"Olá"`. |
| `Array<T>` | Lista de valores do tipo `T`. |
| `Dict<K,V>` | Mapeamento de chaves `K` para valores `V`. |

Os tipos são explícitos. `var` permite reatribuição e pode começar sem valor; ler uma variável ainda não inicializada gera erro. `let` exige inicializador e impede reatribuição. Declarações podem ser separadas por vírgula. O ponto e vírgula ao final dos comandos simples é opcional.

```miniswift
var idade: Int = 20, pontos: Int
pontos = 10
let nome: String = "Ana", ativo: Bool = true
println(nome + ": " + toString(idade + pontos))
```

Blocos `{ ... }` criam escopos. É possível usar o mesmo nome em um bloco interno; nomes declarados nele não ficam disponíveis fora. Declarações executadas novamente em laços reinicializam suas variáveis.

`let` protege a referência da variável. Arrays e dicionários são coleções mutáveis: seus elementos podem ser alterados mesmo quando a variável usa `let`, como ocorre no exemplo de anagramas. Atribuir uma coleção a outra variável compartilha essa coleção. Strings são imutáveis internamente; alterar um índice de uma string exige uma variável `var`.

### Operadores

Da maior para a menor precedência:

| Operadores | Significado |
| --- | --- |
| `[...]`, `.método(...)`, `(expressão)` | Acesso, métodos e agrupamento. |
| `!`, `-` unário | Negação lógica e numérica. |
| `*`, `/` | Multiplicação e divisão. |
| `+`, `-` | Soma, concatenação de strings e subtração. |
| `<`, `>`, `<=`, `>=`, `==`, `!=` | Comparações. |
| `&&` | E lógico. |
| `||` | Ou lógico. |
| `condição ? verdadeiro : falso` | Escolha condicional. |

Operações binárias exigem tipos compatíveis; não há promoção automática entre `Int` e `Float`. Converta explicitamente quando necessário. A divisão de inteiros descarta a parte fracionária. Divisão por zero gera erro. `&&`, `||` e o ternário avaliam somente os operandos/ramos necessários. Condições exigem `Bool`.

`==` e `!=` comparam valores, incluindo strings e coleções. Comparações de ordem aceitam números, caracteres e strings. Comparações consecutivas como `a < b < c` não são aceitas; use `a < b && b < c`.

```miniswift
println(2 + 3 * 4)
println(toFloat(2) + 0.5)
println(false && 1 / 0 == 0)
println(true ? "sim" : "não")
```

### Controle de fluxo

`if`, `else` e `while` recebem uma condição e um comando ou bloco. Parênteses em torno da condição são opcionais.

```miniswift
var n: Int = 0
while n < 3 {
    if n == 1
        println("um")
    else
        println(n)
    n = n + 1
}
```

`for` percorre strings como `Char` ou arrays como seu tipo de elemento. A variável pode ser declarada com `var`/`let` no cabeçalho ou ser uma variável `var` já existente. Variáveis declaradas no cabeçalho ficam restritas ao laço; uma variável `let` não pode ser reatribuída no corpo. A iteração usa uma cópia da sequência de elementos, permitindo alterar a coleção durante o laço sem alterar a sequência percorrida.

```miniswift
for let c: Char in "abc"
    print(toString(c))
println("")

var soma: Int = 0
for var n: Int in Array<Int>(1, 2, 3) {
    soma = soma + n
}
println(soma)
```

### Arrays, dicionários e strings

Construtores de arrays recebem elementos separados por vírgula. Dicionários recebem pares `chave: valor`. Ambos aceitam construtores vazios e tipos aninhados. Chaves iguais substituem o valor anterior; a ordem de inserção do dicionário é preservada.

```miniswift
let numeros: Array<Int> = Array<Int>(10, 20)
numeros.append(30)
numeros[0] = 99
println(numeros[0])
println(numeros.contains(30))

let mapa: Dict<Char,Int> = Dict<Char,Int>('a': 1, 'b': 2)
mapa['a'] = mapa['a'] + 1
mapa['c'] = 3
println(mapa.keys().contains('c'))
println(mapa.values().count())
```

Índices de strings e arrays começam em `0` e precisam ser `Int`. Acessos fora dos limites e leituras de chaves ausentes geram erro. Atribuir a uma chave ausente insere um novo par. Elementos, chaves e valores precisam corresponder aos tipos declarados.

| Método | Receptores | Retorno/efeito |
| --- | --- | --- |
| `.count()` | `String`, `Array`, `Dict` | Quantidade de caracteres, elementos ou pares (`Int`). |
| `.empty()` | `String`, `Array`, `Dict` | Se a quantidade é zero (`Bool`). |
| `.keys()` | `Dict<K,V>` | Novo `Array<K>` com as chaves. |
| `.values()` | `Dict<K,V>` | Novo `Array<V>` com os valores. |
| `.append(valor)` | `Array<T>` | Insere ao final e retorna o próprio array. |
| `.contains(valor)` | `Array<T>`, `String` | Verifica a presença (`Bool`); strings recebem `Char`. |

```miniswift
let matriz: Array<Array<Int>> = Array<Array<Int>>(Array<Int>(1, 2))
matriz[0][1] = 7
println(matriz[0][1])

var texto: String = "abc"
texto[1] = 'x'
println(texto)
println(texto.contains('x'))
```

### Conversões

| Função | Entradas aceitas e comportamento |
| --- | --- |
| `toBool(x)` | `Bool`; números/caracteres diferentes de zero; strings `"true"`/`"false"`; coleções não vazias. |
| `toInt(x)` | `Int`, `Char`, `Bool` (`1`/`0`), `Float` finito dentro da faixa (trunca) e string inteira válida. |
| `toFloat(x)` | `Float`, `Int`, `Char`, `Bool` (`1.0`/`0.0`) e string numérica válida; resultado deve ser finito. |
| `toChar(x)` | `Char`, `Int` entre `0` e `65535`, ou string com uma unidade UTF-16. |
| `toString(x)` | Representação textual do valor. Coleções usam a representação de seus elementos tipados. |

Conversões de `Char` para números usam o código da unidade UTF-16. Conversões inválidas geram erro da linguagem, sem substituir silenciosamente o resultado por zero.

```miniswift
println(toInt("42") + 1)
println(toFloat('A'))
println(toChar(65))
println(toBool(Array<Int>()))
```

### Entrada, saída e aleatoriedade

| Recurso | Comportamento |
| --- | --- |
| `read()` | Lê uma linha e retorna `String`, preservando espaços e retirando apenas o terminador de linha. EOF gera erro. |
| `random()` | Retorna `Float` no intervalo `0.0 <= x < 1.0`. |
| `print(expressão)` | Exibe o conteúdo sem quebra de linha. |
| `println(expressão)` | Exibe o conteúdo com quebra de linha. |
| `dump(expressão)` | Exibe tipo e conteúdo, por exemplo `Int(42)`. |

Arquivos, entrada e saída usam UTF-8. Comentários de uma linha usam `//`; comentários de bloco usam `/* ... */`. Strings e caracteres aceitam `\n`, `\r`, `\t`, `\0`, `\\`, `\'` e `\"`.

## Testes e integração contínua

```powershell
.\build.ps1 -Test
```

```sh
sh build.sh --test
```

A suíte não depende de JUnit. São **72 verificações**, cobrindo os três exemplos e regressões de análise, execução, entrada, saída, erros e infraestrutura de tipos. Cada programa é executado em uma JVM isolada, com assertions habilitadas e timeout de 10 segundos.

Os testes positivos conferem a saída completa, `stderr` vazio e código `0`. Os testes negativos fornecem programas inválidos de propósito e exigem código `1` e o diagnóstico correspondente. Esses erros esperados contam como testes aprovados.

Todo arquivo `examples/*.mswift` é descoberto automaticamente e precisa ter uma saída esperada em `tests/fixtures/<nome>.out`; programas interativos também podem ter `<nome>.in`. As comparações normalizam apenas as quebras de linha Windows/Unix, preservando espaços e a presença ou ausência de uma quebra final.

O workflow `.github/workflows/test.yml` executa os mesmos testes no Windows e no Ubuntu, com Java 8 e 21, em pushes e pull requests. Consulte a aba Actions para o resultado de cada execução.

## Organização e funcionamento

```text
.
├── msi.java                    # CLI, arquivos e prompt
├── lexical/                    # Leitura UTF-8, tokens e palavras reservadas
├── syntatic/                   # Parser e construção da árvore
├── interpreter/
│   ├── command/                # Declarações, atribuições, saída e controle
│   ├── expr/                   # Operadores, acessos, métodos e conversões
│   ├── type/                   # Tipos primitivos e compostos
│   ├── value/                  # Valores tipados e igualdade
│   ├── Environment.java        # Declarações e escopos
│   └── Interpreter.java        # Execução e entrada compartilhada
├── error/                      # Diagnósticos da linguagem e falhas internas
├── examples/                   # Três programas originais
├── tests/TestSuite.java        # Testes de ponta a ponta e tipos
├── tests/fixtures/             # Entradas e saídas esperadas
├── build.ps1 / build.sh        # Compilação, JAR e testes
└── .github/workflows/test.yml  # Validação automatizada
```

O lexer entrega tokens ao parser. O parser resolve nomes no ambiente léxico e cria comandos/expressões. A execução percorre essa árvore e valida os tipos dos valores usados. Arrays armazenam listas de `Value`; dicionários armazenam mapas de `Value` para `Value`. Igualdade e hash por conteúdo permitem recuperar uma chave criada por outra expressão com o mesmo valor.

## Correções realizadas

- Preservação de expressões sem métodos, construção do ternário e encadeamento correto de chamadas.
- Construção independente dos pares de dicionário e composição de acessos aninhados.
- Representação consistente de arrays e dicionários em construtores, acessos, métodos e laços.
- Leitura, inserção e atualização de dicionários com igualdade de chaves por valor.
- Concatenação de strings, comparações, precedência lógica e curto-circuito.
- Escopo e inicialização de variáveis de `for`, múltiplas declarações e reinicialização em blocos repetidos.
- Conversões com tipo/representação corretos e validação de entradas inválidas.
- Lexer UTF-8, escapes, contagem de linhas e término de comentários no EOF.
- Entrada compartilhada entre prompt e `read()`, preservação de espaços e diagnósticos com códigos de saída.
- Scripts de compilação, JAR executável, testes automatizados e documentação.

## Limitações

- Não há funções definidas pelo usuário, classes, módulos, opcionais, inferência de tipos, `break`, `continue` ou `return`.
- Não há sintaxe de literais `[1, 2]`, intervalos, `%`, `+=` ou interpolação de strings; use os construtores e operadores documentados.
- Literais numéricos são decimais; não há notação científica. Um literal `Float` exige dígitos após o ponto, como `1.0`.
- `Int` segue a aritmética Java de 32 bits; operações que excedem sua faixa podem dar a volta. Literais positivos fora da faixa são rejeitados.
- Caracteres e índices de strings usam unidades UTF-16; alguns símbolos, como emojis, ocupam duas unidades.
- Coleções compartilham referências e não implementam cópia por valor de Swift. Use preferencialmente chaves primitivas em dicionários; coleções mutáveis usadas como chave não devem ser alteradas após a inserção.
- O prompt recebe uma linha de código por vez. Use arquivos para programas com múltiplas linhas.
- O timeout pertence aos testes; o interpretador permite laços sem limite durante uso normal.

## Solução de problemas

| Sintoma | Como resolver |
| --- | --- |
| `javac` não encontrado | Instale um JDK e configure `PATH` ou `JAVA_HOME` para a pasta do JDK. |
| `Could not find or load main class msi` | Compile e execute com `-cp build/classes`, a partir da raiz, ou use o JAR. |
| `Unable to access jarfile` | Execute o script de compilação e confira o caminho `build/miniSwift.jar`. |
| `UnsupportedClassVersionError` | Recompile com o script, que usa alvo Java 8, e execute com Java 8 ou superior. |
| PowerShell bloqueia o script | Use o comando com `-ExecutionPolicy Bypass` indicado na instalação. |
| Acentos incorretos | Salve o fonte em UTF-8 e configure o terminal para UTF-8. No PowerShell, para pipelines, use `$OutputEncoding = [System.Text.UTF8Encoding]::new()`. |
| `Fim de arquivo inesperado` | Confira aspas, comentários, delimitadores e a quantidade de linhas fornecidas a `read()`. |
| O anagrama continua perguntando | Digite um candidato que não seja anagrama; candidatos válidos mantêm o laço. |
| Um teste de novo exemplo falha por falta de fixture | Adicione a saída esperada `.out` e, se necessário, a entrada `.in`. |
