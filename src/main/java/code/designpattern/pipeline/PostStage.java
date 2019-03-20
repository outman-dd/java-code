package code.designpattern.pipeline;

/**
 * 〈后置Stage〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/20
 */
public abstract class PostStage<T> extends Stage<T> {

    public PostStage(){
        this.type = StageType.POST;
    }

}
