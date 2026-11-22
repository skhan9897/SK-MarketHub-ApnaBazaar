@echo off
set "SK_MARKETHUB_JWT_SECRET=wtHAWZrvXwCXMgJUmkFA8Qm44sNocBO/RmZ+pMWxWx8="
"C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot\bin\java.exe" -jar "C:\Users\Sajid Khan\SK-MarketHub\backend\target\backend-0.0.1-SNAPSHOT.jar"
echo EXIT_CODE=%errorlevel% > backend-exit-code.txt
