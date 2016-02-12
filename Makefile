JAVA := java
JAVAC := javac
JAVAC_ARGS := -Xlint:unchecked

PRGM := monitor-lib
PKG := ca/dioo/java/MonitorLib
src := $(wildcard src/*.java)
objects := $(patsubst src/%.java,$(PKG)/%.class,$(src))

tests_src := $(wildcard tests/*.java)
tests_objects := $(patsubst tests/%.java,$(PKG)/%.class,$(tests_src))

#libs = libs/java-getopt.jar

ROOT_DIR := $(dir $(lastword $(MAKEFILE_LIST)))

.PHONY: test
test: all $(PKG)/Test.class
	$(JAVA) -cp $(subst " ",":",$(libs)):. $(patsubst /,.,$(PKG)/Test) $(ARGS)


.PHONY: all
all: $(objects) $(libs)


$(objects): $(PKG)/%.class: src/%.java
	$(JAVAC) $(JAVAC_ARGS) -cp $(subst " ",":",$(libs)):$(ROOT_DIR) -d $(ROOT_DIR) $< && touch $@

$(tests_objects): $(PKG)/%.class: tests/%.java
	$(JAVAC) $(JAVAC_ARGS) -cp $(subst " ",":",$(libs)):$(ROOT_DIR) -d $(ROOT_DIR) $< && touch $@


.PHONY: run
run: all
	$(JAVA) -cp $(subst " ",":",$(libs)):. $(PRGM) $(ARGS)


.PHONY: libs
libs: $(libs)


$(PKG)/MonitorLib.class : $(patsubst %,$(PKG)/%,Utils.class)
$(PKG)/ClientMessage.class : $(patsubst %,$(PKG)/%,MonitorLib.class XmlStringReader.class XmlStringWriter.class)
$(PKG)/ServerMessage.class : $(patsubst %,$(PKG)/%,MonitorLib.class XmlStringReader.class XmlStringWriter.class)
$(PKG)/ControlMessage.class : $(patsubst %,$(PKG)/%,MonitorLib.class XmlStringReader.class XmlStringWriter.class)
$(PKG)/Test.class : $(patsubst %,$(PKG)/%,XmlStringReader.class XmlStringWriter.class)
