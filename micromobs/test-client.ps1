param(
    [switch]$PrepOnly
)

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectRoot

Write-Host "[MicroMobs] Project:" $projectRoot

if (-not (Test-Path ".\gradle\wrapper\gradle-wrapper.jar")) {
    Write-Host "[MicroMobs] gradle-wrapper.jar is missing. Running setup.ps1..."
    & ".\setup.ps1"
}

Write-Host "[MicroMobs] Preparing Fabric client launch files and assets..."
cmd.exe /c "gradlew.bat configureClientLaunch downloadAssets ideaSyncTask"
if ($LASTEXITCODE -ne 0) {
    throw "Gradle prep failed with exit code $LASTEXITCODE"
}

if ($PrepOnly) {
    Write-Host "[MicroMobs] Prep complete. Launch later with: .\gradlew.bat runClient"
    exit 0
}

Write-Host "[MicroMobs] Launching development client..."
cmd.exe /c "gradlew.bat runClient"
if ($LASTEXITCODE -ne 0) {
    throw "runClient failed with exit code $LASTEXITCODE"
}
