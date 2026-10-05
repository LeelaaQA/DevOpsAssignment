# Waits until the Selenium Grid reports "ready" and all browser nodes are registered.
# Used by the Jenkinsfile; you can also run it by hand:
#   powershell -NoProfile -ExecutionPolicy Bypass -File docker\wait-for-grid.ps1
param(
    [string]$GridUrl = "http://localhost:4444",
    [int]$ExpectedNodes = 3,
    [int]$TimeoutSeconds = 180
)

$deadline = (Get-Date).AddSeconds($TimeoutSeconds)
Write-Host "Waiting for Selenium Grid at $GridUrl (expecting $ExpectedNodes nodes)..."

while ((Get-Date) -lt $deadline) {
    try {
        $status = Invoke-RestMethod -Uri "$GridUrl/status" -TimeoutSec 5
        $nodeCount = ($status.value.nodes | Measure-Object).Count
        if ($status.value.ready -and $nodeCount -ge $ExpectedNodes) {
            Write-Host "Grid is READY with $nodeCount node(s)."
            exit 0
        }
        Write-Host "Grid reachable: ready=$($status.value.ready), nodes=$nodeCount. Waiting..."
    }
    catch {
        Write-Host "Grid not reachable yet. Waiting..."
    }
    Start-Sleep -Seconds 3
}

Write-Host "ERROR: Grid was not ready within $TimeoutSeconds seconds."
exit 1
