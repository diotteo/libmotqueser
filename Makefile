JAVA ?= java
JAVA_ARGS ?=
JAVAC ?= javac

#1.7 changed InvocationTargetException to extend a new class: ReflectiveOperationException
# and older Android APIs don't like that
JAVAC_ARGS ?= -Xlint:unchecked -source 1.6 -bootclasspath ${HOME}/java/jdk1.6.0_45/jre/lib/rt.jar

PRGM := monitor-lib
PKG := ca/dioo/java/MonitorLib
ROOT_DIR := $(dir $(lastword $(MAKEFILE_LIST)))
BUILD_DIR := $(ROOT_DIR)/build
BPATH := $(BUILD_DIR)/$(PKG)
empty :=
space := $(empty) $(empty)

src := $(wildcard src/*.java)
objects := $(patsubst src/%.java,$(BPATH)/%.class,$(src))

test_src := $(wildcard tests/*.java)
test_objects := $(patsubst tests/%.java,$(BUILD_DIR)/%.class,$(test_src))

libs = libs/dioo-commons.jar

.PHONY: all
all: $(objects)


.PHONY: jar
jar: del_test monitor-lib.jar

.PHONY: del_test
del_test:
	@for i in $(test_objects); do [ ! -e "$$i" ] || rm "$$i"; done

monitor-lib.jar: $(objects)
	jar -cf $@ -C $(BUILD_DIR) .


.PHONY: test
test: $(test_objects)
	$(JAVA) -ea $(JAVA_ARGS) -cp $(subst $(space),:,$(libs)):$(BUILD_DIR) $(patsubst /,.,Test) $(ARGS)


.PHONY: clean
clean:
	@[ ! -e $(BUILD_DIR) ] || rm -rv $(BUILD_DIR)


$(BUILD_DIR):
	@[ -d $(BUILD_DIR) ] || mkdir -p $(BUILD_DIR)


$(objects): $(BPATH)/%.class: src/%.java $(libs) $(BUILD_DIR)
	$(JAVAC) $(JAVAC_ARGS) -cp $(subst $(space),:,$(libs)):$(BUILD_DIR) -d $(BUILD_DIR) $<

$(patsubst %,$(BPATH)/%.class,XmlFactory XmlParser XmlSerializer): $(patsubst %,src/%.java,XmlFactory XmlParser XmlSerializer)
	$(JAVAC) $(JAVAC_ARGS) -cp $(subst $(space),:,$(libs)):$(BUILD_DIR) -d $(BUILD_DIR) $(patsubst %,src/%.java,XmlFactory XmlParser XmlSerializer)

$(test_objects): $(BUILD_DIR)/%.class: tests/%.java $(objects)
	$(JAVAC) $(JAVAC_ARGS) -cp $(subst $(space),:,$(libs)):$(BUILD_DIR) -d $(BUILD_DIR) $<


.PHONY: run
run: $(objects)
	$(JAVA) $(JAVA_ARGS) -cp $(subst $(space),:,$(libs)):. $(PRGM) $(ARGS)


.PHONY: libs
libs: $(libs)


libs/dioo-commons.jar:
	$(MAKE) -C ../java-commons jar


$(patsubst %,$(BPATH)/%.class,XmlFactory XmlParser XmlSerializer) : $(patsubst %,$(BPATH)/%.class,ProgrammerBrainNotFoundError XmlParserException)
$(BPATH)/XmlStringWriter.class : $(patsubst %,$(BPATH)/%.class,XmlSerializer)
$(BPATH)/XmlStringReader.class : $(patsubst %,$(BPATH)/%.class,XmlParser)
$(BPATH)/BadActionTypeException.class : $(patsubst %,$(BPATH)/%.class,MalformedMessageException)
$(BPATH)/Message.class : $(patsubst %,$(BPATH)/%.class,XmlParser XmlSerializer)
$(BPATH)/ServerMessage.class : $(patsubst %,$(BPATH)/%.class,Message XmlStringReader XmlStringWriter ClientMessage BadActionTypeException)
$(BPATH)/ClientMessage.class : $(patsubst %,$(BPATH)/%.class,Message XmlStringReader XmlStringWriter)
$(BPATH)/ControlMessage.class : $(patsubst %,$(BPATH)/%.class,Message XmlStringReader XmlStringWriter)
$(BPATH)/MessageFactory.class : $(patsubst %,$(BPATH)/%.class,ClientMessage ControlMessage ServerMessage)
$(BUILD_DIR)/Test.class : $(patsubst %,$(BPATH)/%.class,XmlStringReader XmlStringWriter)
