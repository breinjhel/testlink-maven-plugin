# Testlink Maven Plugin

A maven plugin that will scan all Junit XML reports and send to Testlink for reporting.

# Installation
Install the maven plugin in your local repo
```sh
$ mvn clean install
```
Import the plugin in your junit project's pom.xml
  ```xml
  <plugin>
    <groupId>com.breinjhel</groupId>
    <artifactId>testlink-maven-plugin</artifactId>
    <version>1.4-SNAPSHOT</version>
    <configuration>
        <url>{Testlink URL}</url>
        <devKey>{API Key}</devKey>
        <projectName>{TestProject}</projectName>
        <testPlan>{TestPlan}</testPlan>
        <platform>{Platform}</platform>
        <customField>TestMethodName</customField>
        <outputDir>target/failsafe-reports</outputDir>
        <filename>TEST-*.xml</filename>
        <build>Nightly Build</build>
    </configuration>
    <executions>
        <execution>
            <id>testlink-reports</id>
            <phase>post-integration-test</phase>
            <goals>
                <goal>report-result</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
If you are using FailSafe when running your integration test
```sh
$ mvn clean verify
```
After your test is done, the testlink-maven-plugin will automatically run and look for junit xml report under the folder you specify in outputDir config.

You can also run the plugin directly by this command:
```sh
$ mvn testlink:report-result
```

### Configuration
   - url - Your testlinka api url e.g. (http://mytestlink.com/lib/api/xmlrpc/v1/xmlrpc.php)
   - devKey - Testlink API dev key
   - projectName - Testlink Project Name
   - testPlan - Testlink Test Plan
   - platform - (Optional) Testlink platform
   - build - Testlink build name (if build doesnt exist in testlink the plugin automatically creates it)
   - customField - Testlink custom field used to connect your testcases and junit code
   - outputDir - junit reports directory (e.g. target/failsafe-reports since im using failsafe)
   - filename - filenames of the xml reports can be a wildcard (TEST-*.xml)


### Todos

 - Write Tests
 - Add Code Comments

License
----

MIT
