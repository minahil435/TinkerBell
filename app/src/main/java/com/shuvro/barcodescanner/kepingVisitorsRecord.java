package com.shuvro.barcodescanner;

import android.net.Uri;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URI;

/**
 * Created by Admin on 1/18/2018.
 */

public class kepingVisitorsRecord
{

    String time;
    String visitor;
    String uri;
    String keyvisitor;


    public kepingVisitorsRecord(String name,String tim,String u)
    {
        visitor=name;
        time=tim;
        uri=u;
    }
    public kepingVisitorsRecord(String name,String tim,String u,String key)
    {
        visitor=name;
        time=tim;
        uri=u;
        keyvisitor=key;
    }
    public kepingVisitorsRecord()
    {

    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getVisitor()
    {
        return visitor;
    }

    public void setVisitor(String time)
    {
        this.visitor = time;
    }

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public String getKeyvisitor() {
        return keyvisitor;
    }

    public void setKeyvisitor(String keyvisitor) {
        this.keyvisitor = keyvisitor;
    }
}

