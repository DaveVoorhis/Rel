@del parser\*.java
@del parser\*.class
@del parser\*.jj
@call c:\temp\javacc-4.0\bin\jjtree TutorialD.jjt
move TutorialD.jj parser
cd parser
@call c:\temp\javacc-4.0\bin\javacc TutorialD.jj
