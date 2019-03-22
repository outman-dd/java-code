package code.designpattern.pipeline;

/**
 * 〈阶段〉<p>
 * 一个Pipeline可以划分为若干个Stage，每个Stage代表一组操作
 *
 * @author zixiao
 * @date 2019/3/20
 */
public abstract class Stage<T> {

    private Stage<T> next = null;

    protected StageType type = StageType.DEFAULT;

    /**
     * Entrance of this stage.
     *
     * @param context
     * @param param
     * @param args
     * @throws Throwable
     */
    public abstract void entry(PipelineContext context, T param, Object... args) throws Throwable;

    /**
     * Means finish of entry
     *
     * @param context
     * @param obj
     * @param args
     * @throws Throwable
     */
    protected void fireEntry(PipelineContext context, Object obj, Object... args)
            throws Throwable {
        if (next != null) {
            next.transformEntry(context, obj, args);
        }
    }

    void transformEntry(PipelineContext context, Object obj, Object... args)
            throws Throwable {
        T t = (T)obj;
        entry(context, t, args);
    }

    /**
     * Exit of this stage.
     *
     * @param context
     * @param args
     */
    public abstract void exit(PipelineContext context, Object... args);

    /**
     * Means finish of exit.
     *
     * @param context
     * @param args
     */
    protected void fireExit(PipelineContext context, Object... args) {
        if (next != null) {
            next.exit(context, args);
        }
    }

    public boolean hasNext(){
        return next != null;
    }

    public Stage<T> getNext() {
        return next;
    }

    public void setNext(Stage<T> next) {
        this.next = next;
    }

    public StageType getType(){
        return type;
    }

}