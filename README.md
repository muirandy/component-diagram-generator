# What?
Build a component(*) diagram from your OpenTracing (Jaeger) traces.

* - No strictly a UML Component diagram. 

## Setup
Install graphviz on your system - eg for Mac:
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

