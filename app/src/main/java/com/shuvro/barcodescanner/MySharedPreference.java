package com.shuvro.barcodescanner;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Admin on 2/2/2018.
 */

public class MySharedPreference
{
    private static Context mctx;
    private static MySharedPreference msharepreference;

    private static final String prefName="shareprefDemo";
    private static final String prefKey="Token";

    private MySharedPreference(Context context)
    {
        mctx=context;
    }

    public static synchronized MySharedPreference getInstance(Context context)
    {

        if (msharepreference==null)

            msharepreference=new MySharedPreference(context);

        return msharepreference;
    }
    public Boolean StoreToken(String token )

    {
        SharedPreferences instanceSharePreference =mctx.getSharedPreferences(prefName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=instanceSharePreference.edit();
        editor.putString(prefKey,token);
        editor.apply();
        return true;
    }
    public void Storedyanmiclink(String key)
    {

        SharedPreferences instanceSharePreference =mctx.getSharedPreferences(prefName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=instanceSharePreference.edit();
        editor.putString("link",key);
        editor.apply();

    }

    public String GetToken()
    {
        SharedPreferences instanceSharePreference =mctx.getSharedPreferences(prefName,Context.MODE_PRIVATE);
       return instanceSharePreference.getString(prefKey,null);
    }
    public String Getdynamiclink()
    {
        SharedPreferences instanceSharePreference =mctx.getSharedPreferences(prefName,Context.MODE_PRIVATE);
        return instanceSharePreference.getString("link",null);
    }
}
