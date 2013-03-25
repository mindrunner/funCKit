/*
 * funCKit - functional Circuit Kit
 * Copyright (C) 2013  Lukas Elsner <open@mindrunner.de>
 * Copyright (C) 2013  Peter Dahlberg <catdog2@tuxzone.org>
 * Copyright (C) 2013  Julian Stier <mail@julian-stier.de>
 * Copyright (C) 2013  Sebastian Vetter <mail@b4sti.eu>
 * Copyright (C) 2013  Thomas Poxrucker <poxrucker_t@web.de>
 * Copyright (C) 2013  Alexander Treml <alex.treml@directbox.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.sep2011.funckit.util;

import java.applet.Applet;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Wrapper for netscape.javascript.JSObject which uses Reflection.
 */
public class ReflectiveJSObject {
    
    /**
     * The Instance of JSObject this Class uses
     */
    private Object jsobjectInstance;

    private Class<?> jsobject;

    protected ReflectiveJSObject(Applet applet) throws ClassNotFoundException {

        try {
            jsobject = Class.forName("netscape.javascript.JSObject");
            Method getWindow = jsobject.getMethod("getWindow", Applet.class);
            jsobjectInstance = invokeMethod(getWindow, null, applet);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }
    
    public static ReflectiveJSObject getWindow(Applet applet) throws ClassNotFoundException {
        return new ReflectiveJSObject(applet);
    }
    
    public void call(String methodName, Object... args) {
        try {
            Method eval = jsobject.getMethod("call", String.class, Object[].class);
            invokeMethod(eval, jsobjectInstance, methodName, args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    
    public Object eval(String s) {
        try {
            Method eval = jsobject.getMethod("eval", String.class);
            return invokeMethod(eval, jsobjectInstance, s);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public Object getMember(String name) {
        try {
            Method eval = jsobject.getMethod("getMember", String.class);
            return invokeMethod(eval, jsobjectInstance, name);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public void setMember(String name, Object value) {
        try {
            Method eval = jsobject.getMethod("setMember", String.class, Object.class);
            invokeMethod(eval, jsobjectInstance, name, value);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    
    public void removeMember(String name) {
        try {
            Method eval = jsobject.getMethod("removeMember", String.class);
            invokeMethod(eval, jsobjectInstance, name);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    
    public Object getSlot(int index) {
        try {
            Method eval = jsobject.getMethod("getSlot", int.class);
            return invokeMethod(eval, jsobjectInstance, index);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public void setSlot(int index, Object value) {
        try {
            Method eval = jsobject.getMethod("setSlot", int.class, Object.class);
            invokeMethod(eval, jsobjectInstance, index, value);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    
    
    private Object invokeMethod(Method method, Object obj, Object... args) {
        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

}
