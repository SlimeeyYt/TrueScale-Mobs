# MicroMobs — first-time setup
# Run this script once to bootstrap the Gradle wrapper JAR.
# Usage:  .\setup.ps1

$ErrorActionPreference = "Stop"
$wrapperDir = "$PSScriptRoot\gradle\wrapper"
$wrapperJar = "$wrapperDir\gradle-wrapper.jar"

if (Test-Path $wrapperJar) {
    Write-Host "[MicroMobs] gradle-wrapper.jar already present. Nothing to do."
    exit 0
}

Write-Host "[MicroMobs] Downloading gradle-wrapper.jar ..."

$jarUrl = "https://raw.githubusercontent.com/gradle/gradle/v8.8.0/gradle/wrapper/gradle-wrapper.jar"

try {
    Invoke-WebRequest -Uri $jarUrl -OutFile $wrapperJar -UseBasicParsing
    Write-Host "[MicroMobs] Downloaded successfully."
} catch {
    Write-Host "[MicroMobs] Download failed. Trying alternate method via curl ..."
    curl.exe -L $jarUrl -o $wrapperJar
}

if (Test-Path $wrapperJar) {
    Write-Host ""
    Write-Host "[MicroMobs] Setup complete!  Now run:  .\gradlew.bat build"
} else {
    Write-Host "[MicroMobs] ERROR: Could not download gradle-wrapper.jar."
    Write-Host "  Please download it manually from:"
    Write-Host "  $jarUrl"
    Write-Host "  and place it at: $wrapperJar"
    exit 1
}

