package code.designpattern.pipeline.impl;

import code.designpattern.pipeline.Pipeline;
import code.designpattern.pipeline.Stage;

/**
 * 〈链式 Pipeline〉<p>
 * stage1 -> stage2 ...
 *
 * @author zixiao
 * @date 2019/3/20
 */
public class ChainPipeline extends Pipeline {

    @Override
    public void addFirst(Stage stage) {
        stage.setNext(first.getNext());
        first.setNext(stage);
        if (end == first) {
            end = stage;
        }
    }

    @Override
    public void addLast(Stage stage) {
        end.setNext(stage);
        end = stage;
    }

}
