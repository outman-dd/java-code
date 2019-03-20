package code.designpattern.pipeline;

import code.spi.extension.SPI;

/**
 * 〈PipelineBuilder〉<p>
 * 〈SPI〉
 *
 * @author zixiao
 * @date 2019/3/20
 */
@SPI(value = "default")
public interface PipelineBuilder {

    Pipeline build();

}
