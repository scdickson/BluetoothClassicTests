JFLAGS = -classpath .:bluecove-gpl-2.1.0.jar:bluecove-2.1.0.jar
JC = javac
JVM = java
MAIN = BT
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java
CLASSES = \
	  BT.java
default: classes
classes: $(CLASSES:.java=.class)
clean:
	$(RM) *.class
run: $(MAIN).class
	$(JVM) $(JFLAGS) $(MAIN)
