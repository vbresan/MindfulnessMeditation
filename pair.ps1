param (
    [Parameter(Mandatory=$true,Position=0)]
    [int]$LastIPOctet,
    [Parameter(Mandatory=$true,Position=1)]
    [int]$PortNumber
)

$IPAddress = "192.168.2.$LastIPOctet"
& $Env:LOCALAPPDATA\Android\sdk\platform-tools\adb.exe connect $IPAddress`:$PortNumber