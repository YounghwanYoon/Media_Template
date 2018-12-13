package com.example.android.media_template;

import java.lang.reflect.InvocationTargetException;

public class MyExceptions extends InvocationTargetException {

    public MyExceptions(){ super(); }
    public MyExceptions (String message) { super(message); }
    public MyExceptions (String message, Throwable cause) { super(message, cause); }
    public MyExceptions(Throwable cause) { super(cause); }

}
