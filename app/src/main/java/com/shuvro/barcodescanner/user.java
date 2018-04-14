package com.shuvro.barcodescanner;

/**
 * Created by Admin on 10/30/2017.
 */

public class user {

    private String name;
    private String address;
    private String Latitude;
    private String Longitude;
    private String profilepicture;



    public user()
    {

    }


    public user(String nam, String a, String b, String i,String pic)
    {
        name=nam;
        address=a;
        Latitude=b;
        Longitude=i;
        profilepicture=pic;


    }


    public String getname()

    {
        return name;
    }
    public String getLatitude()
    {
        return Latitude;
    }
    public String getLongitude()
    {
        return Longitude;
    }
    String getprofilepicture()
    {
        return profilepicture;
    }
    public String getaddress()

    {
        return address;
    }

    public void setLatitude(String b)
    {
        Latitude=b;
    }
    public void setLongitude(String i)

    {
        Longitude=i;
    }
    public void setprofilepicture(String picture)
    {
        profilepicture=picture;
    }

}


