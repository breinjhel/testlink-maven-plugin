package com.breinjhel.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by breinjhel on 8/16/16.
 */
public class Error {

    String message;

    String systemOut;

    String systemErr;

    public String getMessage() {
        return message;
    }


    @XmlAttribute
    public void setMessage(String message) {
        this.message = message;
    }

    public String getSystemOut() {
        return systemOut;
    }


    @XmlElement(name = "system-out")
    public void setSystemOut(String systemOut) {
        this.systemOut = systemOut;
    }

    public String getSystemErr() {
        return systemErr;
    }


    @XmlElement(name = "system-err")
    public void setSystemErr(String systemErr) {
        this.systemErr = systemErr;
    }
}
