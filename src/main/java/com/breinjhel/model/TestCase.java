package com.breinjhel.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by breinjhel on 8/16/16.
 */
@XmlRootElement(name = "testcase")
public class TestCase {

    String name;
    String classname;
    String time;


    Error error;
    Failure failure;

    public Failure getFailure() {
        return failure;
    }

    @XmlElement
    public void setFailure(Failure failure) {
        this.failure = failure;
    }

    public Error getError() {
        return error;
    }


    @XmlElement
    public void setError(Error error) {
        this.error = error;
    }

    public String getName() {
        return name;
    }

    @XmlAttribute
    public void setName(String name) {
        this.name = name;
    }

    public String getClassname() {
        return classname;
    }

    @XmlAttribute
    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getTime() {
        return time;
    }

    @XmlAttribute
    public void setTime(String time) {
        this.time = time;
    }
}
