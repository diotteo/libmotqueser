GIT_TAG :=$(shell git describe --tags 2>/dev/null)
ifeq ($(GIT_TAG),)
	VERSION := $(shell git log --pretty=format:%h -n 1)
else
	VERSION := $(GIT_TAG)
endif

JAVA ?= java
JAVA_ARGS ?=
JAVAC ?= javac

#1.7 changed InvocationTargetException to extend a new class: ReflectiveOperationException
# and older Android APIs don't like that
JAVAC_ARGS ?= -Xlint:unchecked -source 1.6 -bootclasspath ${HOME}/java/jdk1.6.0_45/jre/lib/rt.jar

PRGM := libmotqueser
PKG := ca/dioo/java/libmotqueser
ROOT_DIR := $(dir $(lastword $(MAKEFILE_LIST)))
SRC_DIR := $(ROOT_DIR)/src
LIB_DIR := $(ROOT_DIR)/libs
TEST_DIR := $(ROOT_DIR)/tests
BUILD_DIR := $(ROOT_DIR)/build
BPATH := $(BUILD_DIR)/$(PKG)
empty :=
space := $(empty) $(empty)

src := $(wildcard $(SRC_DIR)/*.java)
objects := $(patsubst $(SRC_DIR)/%.java,$(BPATH)/%.class,$(src))
libs := $(LIB_DIR)/dioo-commons.jar
res := $(BPATH)/version.properties

test_src := $(wildcard $(TEST_DIR)/*.java)
test_objects := $(patsubst $(TEST_DIR)/%.java,$(BUILD_DIR)/%.class,$(test_src))


.PHONY: all
all: $(objects)


.PHONY: jar
jar: del_test $(ROOT_DIR)/$(PRGM)-$(VERSION).jar

.PHONY: del_test
del_test:
	@for i in $(test_objects); do [ ! -e "$$i" ] || rm "$$i"; done

$(ROOT_DIR)/$(PRGM)-$(VERSION).jar: $(objects)
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


$(LIB_DIR)/dioo-commons.jar:
	$(MAKE) -C $(ROOT_DIR)/../java-commons jar


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
