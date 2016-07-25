SET wixPath=%1
IF "%wixPath%"=="" (SET wixPath=C:\progs\wix40)

%wixPath%\heat.exe dir jre7 -o target\jre-files.wxs -scom -sfrag -srd -sreg -gg -cg jre7 -dr JREDIRECTORY
%wixPath%\candle.exe target\jre-files.wxs -o target\jre-files.wixobj
%wixPath%\candle.exe wix-install-config-win32.wxs -o target\wix-install-config-win32.wixobj
%wixPath%\light.exe -b jre7 -o target\ClipboardImageToBase64.msi target\wix-install-config-win32.wixobj target\jre-files.wixobj
