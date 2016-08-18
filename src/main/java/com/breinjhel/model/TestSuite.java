package com.breinjhel.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;

/**
 * Created by breinjhel on 8/11/16.
 */
@XmlRootElement(name = "testsuite")
public class TestSuite {

    String name;
    String time;
    int tests;
    int errors;
    int skipped;
    int failures;

    public String getName() {
        return name;
    }

    @XmlAttribute
    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    @XmlAttribute
    public void setTime(String time) {
        this.time = time;
    }

    public int getTests() {
        return tests;
    }

    @XmlAttribute
    public void setTests(int tests) {
        this.tests = tests;
    }

    public int getErrors() {
        return errors;
    }

    @XmlAttribute
    public void setErrors(int errors) {
        this.errors = errors;
    }

    public int getSkipped() {
        return skipped;
    }

    @XmlAttribute
    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }


    public int getFailures() {
        return failures;
    }
    @XmlAttribute
    public void setFailures(int failures) {
        this.failures = failures;
    }


    @XmlElement(name = "testcase")
    private TestCase[] testcase;


    public TestCase[] getTestCases() {
        return testcase;
    }

    public void setTestCases(TestCase[] testCases) {
        this.testcase = testcase;
    }

}
