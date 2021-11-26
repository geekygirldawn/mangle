# Mangle Developers' Guide

## Sub Modules

Mangle is a spring-boot application with implementation of web services to invoke Fault injection on Supported Endpoints.&#x20;

The mangle Code is organised is as below sub modules using Maven build tool.&#x20;

```
├──  assets/files
├──  checkstyle
├──  docker
├──  docs
├──  formatter
├──  mangle-byteman-root
├──  mangle-default-plugin
├──  mangle-metric-reporter
├──  mangle-models
├──  mangle-services
├──  mangle-support
├──  mangle-task-framework
├──  mangle-test-plugin
├──  mangle-ui
├──  mangle-utils
├──  mangle-vcenter-adapter
│    .gitbook.yaml
│    .gitignore
│    CONTRIBUTING.md
│    LICENSE
│    NOTICE
│    pom.xml
```

### mangle-byteman-root:

Module for the Mangle java Agent, which is used to support application level fault injection against apps running on JVM.&#x20;

### mangle-models:

&#x20;Module for the complete data model of Mangle Web Application.&#x20;

### mangle–utils:&#x20;

Module for all the core logic shared across Mangle Application.&#x20;

### mangle-task-framework:&#x20;

Module for command execution orchestration in Mangle. Every fault execution or request processing in Mangle is handled as an asynchronous task. This module also contains the code responsible for managing scheduler functionality of Mangle.&#x20;

### mangle-test-plugin:&#x20;

Module for the test utilities required for testing the mangle–task-framework.&#x20;

### mangle-default-plugin:&#x20;

Module for all the out of the box faults supported by Mangle as a plugin. This is developed using the pf4j-spring framework. Mangle can support fault execution only if this module is available as plugin.&#x20;

### mangle-ui

Module for the presentation layer of mangle.&#x20;

### mangle –services

Module for core web services exposed to user, corresponding persistence layer and business logic.&#x20;

### mangle-vcenter-adapter:

Module for the spring-boot application that has to be deployed as a container or hosted as an application reachable by Mangle for executing faults against VMware vCenter Server.&#x20;

The order of execution of sub modules is:&#x20;

```
mangle-byteman-root
mangle-models
mangle-utils
mangle-task-framework
mangle-test-plugin
mangle-default-plugin
mangle-ui
mangle-services
mangle-vcenter-adapter
```

## Build Profiles

Different build profiles available in Mangle pom are:&#x20;

### default

This profile builds mangle with last known good configuration of mangle-byteman-agent and does not take latest changes to mangle-byteman-root module into consideration.&#x20;

To build:

```
mvn clean install
```

### build-all

This profile builds mangle with latest changes of mangle-byteman-agent and is recommended for use if the Mangle java agent jar has to be updated.

To build using this profile:

```
mvn clean install –P build-all
OR
mvn clean install --activate-profiles build-all
```

### byteman

This profile builds only the mangle-byteman-root module with the latest changes.&#x20;

To build using profile byteman

```
mvn clean install –P byteman
OR
mvn clean install --activate-profiles byteman
```

### vcenter-adapter

This profile builds only mangle-vcenter-adapter with the latest changes.

```
mvn clean install –P vcenter-adapter
OR
mvn clean install --activate-profiles vcenter-adapter
```

## Building the code

### Prerequisites

#### Java

* Java 8 JDK installed on your OS of choice (Mac OSX, Linux variants, Windows are all supported hosts)
* [Eclipse Luna](http://eclipse.org) or a modern IDE of your choice. Make sure to apply the same formatting profile for code.
* Git for source code management.

#### Maven

Maven is used to build and test the project. Install maven 3.5.X to your local system.

* _(Optional)_ Install Maven with your system's package manager (e.g. _apt_ on Ubuntu/Debian, _homebrew_ on OSX, ...).
* Set your `JAVA_HOME` environment variable to be the home of the Java 8 JDK. On OSX, this lands in `/Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/`.
* Run `mvn clean install` to compile the code, run checkstyle and run unit tests.

#### &#xD;Packaging a fat jar

Resulting JAR goes to `mangle-services/target/mangle-services.jar`.

`./mvnw clean package -DskipTests` (packages without running tests)

Please refer to [Mangle Administrator Guide](mangle-administration/supported-deployment-models/#deploying-the-mangle-virtual-appliance) for starting the jar by providing the Supported DB\_OPTIONS and supported CLUSTER\_OPTIONS as inputs to jar execution command.

```
java –jar mangle-services-.x.x.x-jar –D...... (Db parameters are mandatory)
```

Code Style
----------

### License

Each source file has to specify the license at the very top of the file:

```
/*
 * Copyright (c) 2016-2019 VMware, Inc. All Rights Reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 *
 * This product may include a number of subcomponents with separate copyright notices
 * and license terms. Your use of these subcomponents is subject to the terms and
 * conditions of the subcomponent's license, as noted in the LICENSE file.
 */
```

### Line length

Set to `100`

### Import statements

The order of import statements is:

* import static `java.*`
* import static `javax.*`
* blank line
* import static all other imports
* blank line
* import static `com.vmware.*`
* blank line
* import `java.*`
* import `javax.*`
* blank line
* import all other imports
* blank line
* import `com.vmware.*`

### Comments

Comments are always placed at independent line.\
Do not append the comment at the end of the line.

**Wrong**

```
  host.setLoggingLevel(Level.OFF);  // setting log level OFF
```

**Correct**

```
  // setting log level OFF
  host.setLoggingLevel(Level.OFF);
```

### Commit message

Follow the widely used format:

* [Commit Guidelines section of Pro Git](https://git-scm.com/book/en/v2/Distributed-Git-Contributing-to-a-Project#Commit-Guidelines)
* ["Shiny new commit styles" from Github blog](https://github.com/blog/926-shiny-new-commit-styles)

**Sample:**

```
Short (50 chars or less) summary of changes

More detailed explanatory text, if necessary.  Wrap it to
about 72 characters or so.  In some contexts, the first
line is treated as the subject of an email and the rest of
the text as the body.  The blank line separating the
summary from the body is critical (unless you omit the body
entirely); tools like rebase can get confused if you run
the two together.

Further paragraphs come after blank lines.

  - Bullet points are okay, too

  - Typically a hyphen or asterisk is used for the bullet,
    preceded by a single space, with blank lines in
    between, but conventions vary here
```

* 50 char in title
* Wrap the body at 72 char or less

### Checkstyle

Checkstyle runs as part of maven `validate` lifecycle.

You can call it manually like `./mvnw validate` or `./mvnw checkstyle:checkstyle`.

checkstyle file: [checkstyle.xml](../checkstyle/checkstyle.xml)

## IDE Settings

### Formatter

For both Eclipse and IntellJ, import contrib/eclipse-java-style.xml

#### IntelliJ

IntelliJ can import eclipse formatter file.

`Preference` - `Editor` - `Code Style` - `Manage` - `Import`

Import `contrib/eclipse-java-style.xml`.

### IntelliJ Specific

#### Setting java package import order

1. Update `.idea/codeStyleSettings.xml` with contrib/idea-java-style.xml
2. Restart IntelliJ

## Building Mangle Docker Images

### **Prerequisites**

Install docker on the developer machine following [docker installation manual](https://docs.docker.com/v17.12/manuals/). Change the working directory to the root of mangle code repository.

### **To build a docker image for Mangle**

```
#!/bin/bash
#stop the existing container if it is already running
docker rm -f $(docker ps -a | grep mangle-app | awk '{print$1}')
IMAGE=`docker images | grep "^mangle-app"| awk '{print $1}'`
$CONTAINER_NAME= mangle-app
if [ $IMAGE = $CONTAINER_NAME ]
then
               echo 'removing '$CONTAINER_NAME' image ...'
               docker rmi -f $CONTAINER_NAME
fi
 
#Build Image
docker build –f docker/Dockerfile -t $CONTAINER_NAME .
IP=`ifconfig eth0 | grep "inet addr" | cut -d ':' -f 2 | cut -d ' ' -f 1`
```

### **To build a docker image for Mangle vCenter Adapter**

```
#!/bin/bash
#stop the existing container if it is already running
docker rm -f $(docker ps -a | grep mangle-app | awk '{print$1}')
IMAGE=`docker images | grep "^mangle-app"| awk '{print $1}'`
$CONTAINER_NAME= mangle-app
if [ $IMAGE = $CONTAINER_NAME ]
then
               echo 'removing '$CONTAINER_NAME' image ...'
               docker rmi -f $CONTAINER_NAME
fi
 
#Build Image
docker build –f docker/Dockerfile -t $CONTAINER_NAME .
IP=`ifconfig eth0 | grep "inet addr" | cut -d ':' -f 2 | cut -d ' ' -f 1`
```
