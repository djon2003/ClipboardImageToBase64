# ClipboardImageToBase64

Java software that detect HTML copied to the clipboard and changes all local images to base 64 ones.

#### Installation

##### Java Project

- Pull the project
- Install WIX : http://wixtoolset.org/
- Install JRE7 : Installation file included in install-files directory
- Make a symbolic link from your JRE7 installation path to PROJECT_PATH/jre7

##### Binaries

- <a href="http://cints.net/freefiles/ClipboardImageToBase64.msi">Windows 32 bits installer</a>

#### Usage

##### Building

To build the project, run it using Maven with "clean package" goals

##### Running in Eclipse

Run the project directly from Eclipse by using the ClipboardImageToBase64.java file

##### Making installer

Run the file "wix-make-installer.bat" from PROJECT_PATH. If WIX was not installed in C:\progs\wix40, then you can specify the path as a parameter of the batch file.

##### Running binary

Install & execute shortcut created on desktop