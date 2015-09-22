# Introduction #

HOWTO Build struts-annotations source with mvn (maven)


# Details #

You must edit your pom.xml file and add where is located tools.jar file ( for example : C:\Program Files\Java\jdk1.6.0\_03\lib\tools.jar )
example :
```
 <dependency>
      <groupId>com.sun</groupId>
      <artifactId>tools</artifactId>
      <version>1.5.0</version>
      <scope>system</scope>
      <systemPath>C:/tools.jar</systemPath>
    </dependency>
```