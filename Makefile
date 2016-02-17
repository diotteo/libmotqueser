JAVA := java
JAVA_ARGS :=
JAVAC := javac
JAVAC_ARGS := -Xlint:unchecked

PRGM := monitor-lib
PKG := ca/dioo/java/MonitorLib
ROOT_DIR := $(dir $(lastword $(MAKEFILE_LIST)))
BUILD_DIR := $(ROOT_DIR)/build
BPATH := $(BUILD_DIR)/$(PKG)

src := $(wildcard src/*.java)
objects := $(patsubst src/%.java,$(BPATH)/%.class,$(src))

test_src := $(wildcard tests/*.java)
test_objects := $(patsubst tests/%.java,$(BUILD_DIR)/%.class,$(test_src))

#libs = libs/java-getopt.jar

.PHONY: all
all: $(objects) $(libs)


.PHONY: jar
jar: del_test monitor-lib.jar

.PHONY: del_test
del_test:
	@rm $(test_objects) || true

monitor-lib.jar: all
	jar -cf $@ -C $(BUILD_DIR) .


.PHONY: test
test: $(test_objects)
	$(JAVA) -ea $(JAVA_ARGS) -cp $(subst " ",":",$(libs)):$(BUILD_DIR) $(patsubst /,.,Test) $(ARGS)


.PHONY: clean
clean:
	@rm -rv $(BUILD_DIR) || true


$(BUILD_DIR):
	@[ -d $(BUILD_DIR) ] || mkdir -p $(BUILD_DIR)


$(objects): $(BPATH)/%.class: src/%.java $(BUILD_DIR)
	$(JAVAC) $(JAVAC_ARGS) -cp $(subst " ",":",$(libs)):$(BUILD_DIR) -d $(BUILD_DIR) $<

$(patsubst %,$(BPATH)/%.class,XmlFactory XmlParser XmlSerializer): $(patsubst %,src/%.java,XmlFactory XmlParser XmlSerializer)
	$(JAVAC) $(JAVAC_ARGS) -cp $(subst " ",":",$(libs)):$(BUILD_DIR) -d $(BUILD_DIR) $(patsubst %,src/%.java,XmlFactory XmlParser XmlSerializer)

$(test_objects): $(BUILD_DIR)/%.class: tests/%.java all
	$(JAVAC) $(JAVAC_ARGS) -cp $(subst " ",":",$(libs)):$(BUILD_DIR) -d $(BUILD_DIR) $<


.PHONY: run
run: all
	$(JAVA) $(JAVA_ARGS) -cp $(subst " ",":",$(libs)):. $(PRGM) $(ARGS)


.PHONY: libs
libs: $(libs)

$(patsubst %,$(BPATH)/%.class,XmlFactory XmlParser XmlSerializer) : $(patsubst %,$(BPATH)/%,Utils.class ProgrammerBrainNotFoundError.class XmlParserException.class)
$(BPATH)/XmlStringWriter.class : $(patsubst %,$(BPATH)/%,XmlSerializer.class)
$(BPATH)/XmlStringReader.class : $(patsubst %,$(BPATH)/%,XmlParser.class)
$(BPATH)/Message.class : $(patsubst %,$(BPATH)/%,XmlParser.class XmlSerializer.class)
$(BPATH)/ClientMessage.class : $(patsubst %,$(BPATH)/%,Message.class XmlStringReader.class XmlStringWriter.class)
$(BPATH)/ServerMessage.class : $(patsubst %,$(BPATH)/%,Message.class XmlStringReader.class XmlStringWriter.class)
$(BPATH)/ControlMessage.class : $(patsubst %,$(BPATH)/%,Message.class XmlStringReader.class XmlStringWriter.class)
$(BPATH)/MessageFactory.class : $(patsubst %,$(BPATH)/%,ClientMessage.class ControlMessage.class ServerMessage.class)
$(BUILD_DIR)/Test.class : $(patsubst %,$(BPATH)/%,XmlStringReader.class XmlStringWriter.class)
