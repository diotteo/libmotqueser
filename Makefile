JAVA ?= java
JAVA_ARGS ?=
JAVAC ?= javac

#1.7 changed InvocationTargetException to extend a new class: ReflectiveOperationException
# and older Android APIs don't like that
JAVAC_ARGS ?= -Xlint:unchecked -source 1.6 -bootclasspath ${HOME}/java/jdk1.6.0_45/jre/lib/rt.jar

PRGM := libmotqueser
PKG := ca/dioo/java/libmotqueser
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
jar: del_test $(PRGM).jar

.PHONY: del_test
del_test:
	@for i in $(test_objects); do [ ! -e "$$i" ] || rm "$$i"; done

$(PRGM).jar: $(objects)
	jar -cf $@ -C $(BUILD_DIR) .


.PHONY: test
test: $(test_objects)
	$(JAVA) -ea $(JAVA_ARGS) -cp $(subst $(space),:,$(libs)):$(BUILD_DIR) $(patsubst /,.,Test) $(ARGS)


.PHONY: clean
clean:
	@[ ! -e $(BUILD_DIR) ] || rm -rv $(BUILD_DIR)


$(BUILD_DIR):
	@[ -d $(BUILD_DIR) ] || mkdir -p $(BUILD_DIR)

.PHONY: run
run: $(objects)
	$(JAVA) $(JAVA_ARGS) -cp $(subst $(space),:,$(libs)):. $(PRGM) $(ARGS)


.PHONY: libs
libs: $(libs)


libs/dioo-commons.jar:
	$(MAKE) -C ../java-commons jar


xml_base := XmlFactory XmlParser XmlSerializer
xml_objects := $(patsubst %,$(BPATH)/%.class,$(xml_base))
objects := $(filter-out $(xml_objects), $(objects))
#Circular dependencies
$(xml_objects): $(patsubst %,src/%.java,$(xml_base))
	$(JAVAC) $(JAVAC_ARGS) -cp $(subst $(space),:,$(libs)):$(BUILD_DIR) -d $(BUILD_DIR) $(patsubst %,src/%.java,XmlFactory XmlParser XmlSerializer)

$(objects): $(BPATH)/%.class: src/%.java $(libs) $(BUILD_DIR)
	$(JAVAC) $(JAVAC_ARGS) -cp $(subst $(space),:,$(libs)):$(BUILD_DIR) -d $(BUILD_DIR) $<

$(test_objects): $(BUILD_DIR)/%.class: tests/%.java $(objects)
	$(JAVAC) $(JAVAC_ARGS) -cp $(subst $(space),:,$(libs)):$(BUILD_DIR) -d $(BUILD_DIR) $<


$(xml_objects) : $(patsubst %,$(BPATH)/%.class,ProgrammerBrainNotFoundError XmlParserException)

$(BPATH)/XmlStringWriter.class : $(patsubst %,$(BPATH)/%.class,XmlSerializer)
$(BPATH)/XmlStringReader.class : $(patsubst %,$(BPATH)/%.class,XmlParser)
$(BPATH)/Message.class : $(patsubst %,$(BPATH)/%.class,XmlParser XmlSerializer)
$(BPATH)/BaseServerMessage.class : $(patsubst %,$(BPATH)/%.class,Message XmlStringWriter)
$(BPATH)/ServerMessage.class : $(patsubst %,$(BPATH)/%.class,BaseServerMessage Message XmlStringWriter ClientMessage MediaType)
$(BPATH)/NotificationMessage.class : $(patsubst %,$(BPATH)/%.class,BaseServerMessage Message XmlStringWriter)
$(BPATH)/ErrorMessage.class : $(patsubst %,$(BPATH)/%.class,Message XmlStringReader XmlStringWriter)
$(BPATH)/ClientMessage.class : $(patsubst %,$(BPATH)/%.class,Message XmlStringReader XmlStringWriter MediaType)
$(BPATH)/ControlMessage.class : $(patsubst %,$(BPATH)/%.class,Message XmlStringReader XmlStringWriter)
$(BPATH)/MessageFactory.class : $(patsubst %,$(BPATH)/%.class,ClientMessage ControlMessage ErrorMessage ServerMessage)
$(BUILD_DIR)/Test.class : $(patsubst %,$(BPATH)/%.class,XmlStringReader XmlStringWriter)
