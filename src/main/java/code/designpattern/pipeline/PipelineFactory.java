package code.designpattern.pipeline;

import code.spi.extension.ExtensionLoader;

/**
 * 〈pipeline工厂〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/20
 */
public class PipelineFactory {

    private static volatile PipelineBuilder builder = null;

    private static final ExtensionLoader<PipelineBuilder> LOADER = ExtensionLoader.load(PipelineBuilder.class);

    private PipelineFactory() {}

    public static Pipeline build(String name) {
        builder  = LOADER.find(name);
        if(builder == null){
            throw new IllegalArgumentException();
        }
        return builder.build();
    }

}
