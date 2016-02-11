JAVA := java
JAVAC := javac
JAVAC_ARGS := -Xlint:unchecked

PRGM := monitor-lib
src := $(wildcard *.java)
objects := $(patsubst %.java,%.class,$(src))
PKG := ca/dioo/java/MonitorLib

#libs = libs/java-getopt.jar

ROOT_DIR := $(dir $(lastword $(MAKEFILE_LIST)))

.PHONY: test
test: all
	$(JAVA) -cp $(subst " ",":",$(libs)):. Test $(ARGS)


.PHONY: all
all: $(objects) $(libs)


$(objects): %.class: %.java
	$(JAVAC) $(JAVAC_ARGS) -cp $(subst " ",":",$(libs)):.:$(ROOT_DIR)/$(PKG) -d $(ROOT_DIR) $<


.PHONY: run
run: all
	$(JAVA) -cp $(subst " ",":",$(libs)):. $(PRGM) $(ARGS)


.PHONY: libs
libs: $(libs)


ClientMessage.class: MonitorLib.class
ControlMessage.class : MonitorLib.class
ServerMessage.class : MonitorLib.class
XmlStringReader.class : ClientMessage.class ControlMessage.class ServerMessage.class
XmlStringWriter.class : ClientMessage.class ControlMessage.class ServerMessage.class
Test.class : XmlStringReader.class XmlStringWriter.class
