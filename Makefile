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


first_obj := $(firstword $(objects))
rest_obj := $(wordlist 2,$(words $(objects)),$(objects))

$(first_obj): $(src) $(libs) $(BUILD_DIR)
	$(JAVAC) $(JAVAC_ARGS) -cp $(subst $(space),:,$(libs)):$(BUILD_DIR) -d $(BUILD_DIR) $(src)

$(rest_obj): $(first_obj)


first_test_obj := $(firstword $(test_objects))
rest_test_obj := $(wordlist 2,$(words $(test_objects)),$(test_objects))
$(first_test_obj): $(test_src) $(objects)
	$(JAVAC) $(JAVAC_ARGS) -cp $(subst $(space),:,$(libs)):$(BUILD_DIR) -d $(BUILD_DIR) $(test_src)

$(rest_test_obj): $(first_test_obj)
