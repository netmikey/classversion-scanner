classversion-scanner
====================

Tool that scans compiled Java .class files and reports the java runtime version they're targeted at. Supports 
filtering for min and max java runtime version and traverses directories and archives recursively.

This might be useful if you're running on an older Java runtime and are getting the well-known error message:

    java.lang.UnsupportedClassVersionError: Bad version number in .class file

This error might come without any hint at the class, the archive it was loaded from, or even a stack trace.
If you face this issue, you can use this tool and point it at the root of your application (you can even
point it at the root of your JEE application server or servlet container to scan the whole thing) and
tell it to look for class files thare are newer than your target java runtime.


Building
--------

You'll need any Java6+ JDK and gradle to build. If you have both installed, just run the following in the root
directory of your checkout:

    classversion-scanner$ gradle install

There will be a ready-to-use installation in `build/install/classversion-scanner/` you can launch the tool by
executing `build/install/classversion-scanner/bin/classversion-scanner`.


Usage
-----

    classversion-scanner$ build/install/classversion-scanner/bin/classversion-scanner --help
    
    usage: com.netmikey.cvscanner.ClassVersionScanner
     -d,--dir <arg>     the root directory from which to search for class
                        files and java archives (if not set, the current
                        working directory will be used).
     -h,--help          display this help info
     -n,--newer <arg>   only look for class files compiled for the specified
                        JRE and newer
     -o,--older <arg>   only look for class files compiled for the specified
                        JRE and older
     -v,--verbose       display more processing info


Example
-------

Find all class files in the tool's own installation directory compiled for Java6 or newer: 

    build/install/classversion-scanner$ bin/classversion-scanner --newer 6
    
    30.08.2012 13:57:47 com.netmikey.cvscanner.Scanner processClassFile
    INFO: Found matching class file: classversion-scanner/build/install/classversion-scanner/lib/classversion-scanner.jar/com/netmikey/cvscanner/ClassVersionScanner.class compiled for Java version: 6
    30.08.2012 13:57:47 com.netmikey.cvscanner.Scanner processClassFile
    INFO: Found matching class file: classversion-scanner/build/install/classversion-scanner/lib/classversion-scanner.jar/com/netmikey/cvscanner/JavaVersion.class compiled for Java version: 6
    30.08.2012 13:57:47 com.netmikey.cvscanner.Scanner processClassFile
    INFO: Found matching class file: classversion-scanner/build/install/classversion-scanner/lib/classversion-scanner.jar/com/netmikey/cvscanner/Scanner$1.class compiled for Java version: 6
    30.08.2012 13:57:47 com.netmikey.cvscanner.Scanner processClassFile
    INFO: Found matching class file: classversion-scanner/build/install/classversion-scanner/lib/classversion-scanner.jar/com/netmikey/cvscanner/Scanner$2.class compiled for Java version: 6
    30.08.2012 13:57:47 com.netmikey.cvscanner.Scanner processClassFile
    INFO: Found matching class file: classversion-scanner/build/install/classversion-scanner/lib/classversion-scanner.jar/com/netmikey/cvscanner/Scanner$3.class compiled for Java version: 6
    30.08.2012 13:57:47 com.netmikey.cvscanner.Scanner processClassFile
    INFO: Found matching class file: classversion-scanner/build/install/classversion-scanner/lib/classversion-scanner.jar/com/netmikey/cvscanner/Scanner$4.class compiled for Java version: 6
    30.08.2012 13:57:47 com.netmikey.cvscanner.Scanner processClassFile
    INFO: Found matching class file: classversion-scanner/build/install/classversion-scanner/lib/classversion-scanner.jar/com/netmikey/cvscanner/Scanner.class compiled for Java version: 6

