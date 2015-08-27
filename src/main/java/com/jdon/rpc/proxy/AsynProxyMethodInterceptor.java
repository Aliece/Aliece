package com.jdon.rpc.proxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by zhangsaizhong on 15/7/29.
 */
public class AsynProxyMethodInterceptor implements MethodInterceptor {

    private ExecutorService executor;

    public AsynProxyMethodInterceptor(ExecutorService executor) {
        super();
        this.executor = executor;
    }

    public Object intercept(final Object object, final Method invokedmethod, final Object[] args, final MethodProxy methodProxy) throws Throwable {
        if (invokedmethod.getName().equals("finalize"))
            return null;
        Future<Object> future = null;
        Object result = null;
        try {
            future = executor.submit(new Callable<Object>() {
                public Object call() {
                    try {
                        Object result = methodProxy.invokeSuper(object, args);
                        return result;
                    } catch (Throwable e) {
                        return null;
                    }
                }
            });

            Class clazz = invokedmethod.getReturnType();
            if (Object.class.isAssignableFrom(clazz) && !String.class.isAssignableFrom(clazz)) {
                if (clazz.isInterface()) {
                    result = new AsynProxy<>(resolveInterface(clazz).newInstance()).getProxy(future);
                } else {
                    result = new AsynProxy<>(instantiate(resolveInterface(clazz))).getProxy(future);
                }
            } else {
                result = future.get();
            }

        } catch (Exception ex) {
            System.out.println(ex);
        } catch (Throwable ex) {
            System.out.println(ex);
            throw new Throwable(ex);
        }
        return result;
    }

    protected Class<?> resolveInterface(Class<?> type) {
        Class<?> classToCreate;
        if (type == List.class || type == Collection.class) {
            classToCreate = ArrayList.class;
        } else if (type == Map.class) {
            classToCreate = HashMap.class;
        } else if (type == SortedSet.class) { // issue #510 Collections Support
            classToCreate = TreeSet.class;
        } else if (type == Set.class) {
            classToCreate = HashSet.class;
        } else {
            classToCreate = type;
        }
        return classToCreate;
    }

    private static Object instantiate(Class<?> cl) throws Exception {
        Constructor<?>[] constructors = cl.getDeclaredConstructors();
        Constructor<?> constructor = null;
        int argc = Integer.MAX_VALUE;
        for (Constructor<?> c : constructors) {
            if (c.getParameterTypes().length < argc) {
                argc = c.getParameterTypes().length;
                constructor = c;
            }
        }

        if (constructor != null) {
            Class<?>[] paramTypes = constructor.getParameterTypes();
            Object[] constructorArgs = new Object[paramTypes.length];
            for (int i = 0; i < constructorArgs.length; i++) {
                constructorArgs[i] = getConstructorArg(paramTypes[i]);
            }
            try {
                constructor.setAccessible(true);
                return constructor.newInstance(constructorArgs);
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
        }

        return cl.newInstance();
    }

    private static Object getConstructorArg(Class<?> cl) {
        if (boolean.class.equals(cl) || Boolean.class.equals(cl)) {
            return Boolean.FALSE;
        } else if (byte.class.equals(cl) || Byte.class.equals(cl)) {
            return Byte.valueOf((byte) 0);
        } else if (short.class.equals(cl) || Short.class.equals(cl)) {
            return Short.valueOf((short) 0);
        } else if (int.class.equals(cl) || Integer.class.equals(cl)) {
            return Integer.valueOf(0);
        } else if (long.class.equals(cl) || Long.class.equals(cl)) {
            return Long.valueOf(0L);
        } else if (float.class.equals(cl) || Float.class.equals(cl)) {
            return Float.valueOf((float) 0);
        } else if (double.class.equals(cl) || Double.class.equals(cl)) {
            return Double.valueOf((double) 0);
        } else if (char.class.equals(cl) || Character.class.equals(cl)) {
            return new Character((char) 0);
        } else {
            return null;
        }
    }

}