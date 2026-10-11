param([switch]$Test, [switch]$Examples)
$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    $javac = 'javac'
    $java = 'java'
    $jar = 'jar'
    if ($env:JAVA_HOME) {
        $javac = Join-Path $env:JAVA_HOME 'bin/javac.exe'
        $java = Join-Path $env:JAVA_HOME 'bin/java.exe'
        $jar = Join-Path $env:JAVA_HOME 'bin/jar.exe'
    }
    New-Item -ItemType Directory -Force build/classes | Out-Null
    $sources = @('msi.java') + @(Get-ChildItem error,lexical,syntatic,interpreter -Recurse -Filter '*.java' | ForEach-Object { $_.FullName })
    & $javac -encoding UTF-8 -source 8 -target 8 '-Xlint:-options' -d build/classes $sources
    if ($LASTEXITCODE -ne 0) { throw 'Falha na compilação.' }
    & $jar cfe build/miniSwift.jar msi -C build/classes .
    if ($LASTEXITCODE -ne 0) { throw 'Falha ao criar o JAR.' }
    Write-Host 'Compilação concluída: build/miniSwift.jar'
    if ($Test -or $Examples) {
        New-Item -ItemType Directory -Force build/test-classes | Out-Null
        & $javac -encoding UTF-8 -source 8 -target 8 '-Xlint:-options' -cp build/classes -d build/test-classes tests/TestSuite.java
        if ($LASTEXITCODE -ne 0) { throw 'Falha na compilação dos testes.' }
        $testArgs = @('-ea', '-Dfile.encoding=UTF-8', '-cp', 'build/classes;build/test-classes', 'TestSuite')
        if ($Examples -and -not $Test) { $testArgs += '--examples-only' }
        & $java $testArgs
        if ($LASTEXITCODE -ne 0) { throw 'Falha na validação.' }
    }
} finally {
    Pop-Location
}
