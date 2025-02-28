# Google Maps Commute Comparison PowerShell Script
# Usage: .\GoogleMapsCommuteComparison.ps1 Address "Your address" -JsonFilePath "path\to\locations.json"
# Written by Claude (and Skyler)

param(
    [Parameter(Mandatory=$true)]
    [string]$Address,
    
    [Parameter(Mandatory=$false)]
    [string]$JsonFilePath = "locations.json"
)

# Function to URL encode a string
function UrlEncode($string) {
    [System.Web.HttpUtility]::UrlEncode($string)
}

# Check if the JSON file exists
if (-not (Test-Path $JsonFilePath)) {
    Write-Error "JSON file not found at path: $JsonFilePath"
    exit 1
}

# Try to read and parse the JSON file
try {
    $locationsData = Get-Content -Path $JsonFilePath -Raw | ConvertFrom-Json
}
catch {
    Write-Error "Failed to parse JSON file. Please ensure it's valid JSON."
    Write-Error $_.Exception.Message
    exit 1
}

# URL encode the fixed address
$encodedFixedAddress = UrlEncode $Address
Write-Host "**********************************************"
Write-Host "Comparing Driving times to $Address"
Write-Host "**********************************************\n"

# Process each location in the JSON file
$count = 0
foreach ($location in $locationsData) {
    # Validate location data
    if (-not $location.address -or -not $location.type) {
        Write-Warning "Skipping location with missing address or type"
        continue
    }

    # Check if type is valid
    if ($location.type -ne "source" -and $location.type -ne "destination") {
        Write-Warning "Skipping location with invalid type: $($location.type). Must be 'source' or 'destination'."
        continue
    }

    # URL encode the location address
    $encodedLocationAddress = UrlEncode $location.address

    # Build the URL based on the type
    $url = "https://www.google.com/maps/dir/?api=1&travelmode=driving&maptype=map"
    
    if ($location.type -eq "source") {
        # The JSON location is the source, fixed address is the destination
        $url += "&origin=$encodedLocationAddress&destination=$encodedFixedAddress"
        $routeDesc = "From $($location.address) to $Address"
    }
    else {
        # The fixed address is the source, JSON location is the destination
        $url += "&origin=$encodedFixedAddress&destination=$encodedLocationAddress"
        $routeDesc = "From $Address to $($location.address)"
    }

    # Open the URL in the default browser
    Start-Process $url
    $count++

    # Display information about the route
    Write-Host "Opening route #$count in Google Maps:"
    Write-Host $routeDesc
    Write-Host "URL: $url"
    Write-Host ""

    # Add a small delay to avoid overwhelming the browser
    Start-Sleep -Milliseconds 500
}

Write-Host "Opened $count routes from the JSON file."