package code.designpattern.pipeline;

/**
 * 〈前置Stage〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/20
 */
public abstract class PreStage<T> extends Stage<T> {

    public PreStage(){
        this.type = StageType.PRE;
    }
}
