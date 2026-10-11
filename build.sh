#!/usr/bin/env sh
set -eu
cd "$(CDPATH='' cd -- "$(dirname -- "$0")" && pwd)"
if [ -n "${JAVA_HOME:-}" ]; then
    JAVAC="$JAVA_HOME/bin/javac"
    JAVA="$JAVA_HOME/bin/java"
    JAR="$JAVA_HOME/bin/jar"
else
    JAVAC=javac
    JAVA=java
    JAR=jar
fi
mkdir -p build/classes
find error lexical syntatic interpreter -name '*.java' -print > build/sources.txt
printf '%s\n' msi.java >> build/sources.txt
"$JAVAC" -encoding UTF-8 -source 8 -target 8 -Xlint:-options -d build/classes @build/sources.txt
"$JAR" cfe build/miniSwift.jar msi -C build/classes .
printf '%s\n' 'Compilação concluída: build/miniSwift.jar'
case "${1:-}" in
    --test|--examples)
        mkdir -p build/test-classes
        "$JAVAC" -encoding UTF-8 -source 8 -target 8 -Xlint:-options -cp build/classes -d build/test-classes tests/TestSuite.java
        if [ "$1" = --examples ]; then
            "$JAVA" -ea -Dfile.encoding=UTF-8 -cp build/classes:build/test-classes TestSuite --examples-only
        else
            "$JAVA" -ea -Dfile.encoding=UTF-8 -cp build/classes:build/test-classes TestSuite
        fi
        ;;
    '') ;;
    *) printf '%s\n' 'Uso: sh build.sh [--test|--examples]' >&2; exit 2 ;;
esac
