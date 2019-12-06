# What?
Build a component(*) diagram from your OpenTracing (Jaeger) traces.

* - No strictly a UML Component diagram. 

## Setup
Check your version of graphviz/dot:
```
dot -V
```
If its not there, install it.
To Install graphviz on your system - eg for Mac:
```
brew install graphviz
```



If you see the following error message (perhaps when running AcceptanceTest), then see the above remedy!
```
java.io.IOException: Cannot run program "/opt/local/bin/dot": error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:1048)
	at java.lang.Runtime.exec(Runtime.java:620)
	at net.sourceforge.plantuml.cucadiagram.dot.ProcessRunner$MainThread.startThreads(ProcessRunner.java:163)
	at net.sourceforge.plantuml.cucadiagram.dot.ProcessRunner$MainThread.runJob(ProcessRunner.java:123)
	at net.sourceforge.plantuml.api.TimeoutExecutor$MyThread.run(TimeoutExecutor.java:79)
```

If it is there but less than version 2.43.0, the tests may fail due to differences in drawing the diagrams.
Install the new version then make it the default:
```
brew install graphviz
brew link --overwrite graphviz
```
