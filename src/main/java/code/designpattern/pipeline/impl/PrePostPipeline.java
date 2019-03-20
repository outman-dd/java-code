package code.designpattern.pipeline.impl;

import code.designpattern.pipeline.Pipeline;
import code.designpattern.pipeline.Stage;
import code.designpattern.pipeline.StageType;

/**
 * 〈前置&后置 Pipeline〉<p>
 * first -> PreStage1 -> PreStage2 ->...-> Stage1 ->...-> PostStage1 -> PostStage2 ->...
 *
 * @author zixiao
 * @date 2019/3/20
 */
public class PrePostPipeline extends Pipeline {

    @Override
    public void addFirst(Stage stage) {
        // 插入到first后
        if (stage.getType() == StageType.PRE) {
            append(first, stage);
        // 插入到最后一个非post节点
        } else if (stage.getType() == StageType.POST) {
            append(findLastNotPost(), stage);
        // 插入到最后一个pre节点
        } else {
            append(findLastPre(), stage);
        }
        if(stage.getNext() == null){
            end = stage;
        }
    }

    @Override
    public void addLast(Stage stage) {
        // 插入到最后一个pre节点
        if(stage.getType() == StageType.PRE){
            append(findLastPre(), stage);
        }else if(stage.getType() == StageType.POST){
            end.setNext(stage);
        // 插入到最后一个非post节点
        }else{
            append(findLastNotPost(), stage);
        }
        if(stage.getNext() == null){
            end = stage;
        }
    }

    /**
     * append after source
     *
     * @param source
     * @param append
     */
    private void append(Stage source, Stage append) {
        append.setNext(source.getNext());
        source.setNext(append);
    }

    /**
     * find the last not post（pre or default）stage
     *
     * @return
     */
    private Stage findLastNotPost() {
        Stage stage = first;
        while (stage.getNext() != null && stage.getNext().getType() != StageType.POST){
            stage = stage.getNext();
        }
        return stage;
    }

    /**
     * find the last pre stage
     *
     * @return
     */
    private Stage findLastPre() {
        Stage stage = first;
        while (stage.getNext() != null && stage.getNext().getType() == StageType.PRE){
            stage = stage.getNext();
        }
        return stage;
    }

}
