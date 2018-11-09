package com.university.unicornslayer.lab2;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

public class Note implements Serializable
{
    public Integer id;
    public String content;
    public Date date;
    public boolean notifyMe;
}