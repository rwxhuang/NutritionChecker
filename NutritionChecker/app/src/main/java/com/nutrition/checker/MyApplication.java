package com.nutrition.checker;

import android.app.Application;
import android.graphics.Bitmap;

import java.util.HashMap;

public class MyApplication
{
    private static MyApplication mInstance= null;

    public HashMap<String, Bitmap> images = new HashMap<>();

    protected MyApplication(){}

    public static synchronized MyApplication getInstance()
    {
        if(null == mInstance){
            mInstance = new MyApplication();
        }
        return mInstance;
    }

    public HashMap<String, Bitmap> getImages()
    {
        return images;
    }
    public void addImage(String name,Bitmap image)
    {
        images.put(name, image);
    }

}
