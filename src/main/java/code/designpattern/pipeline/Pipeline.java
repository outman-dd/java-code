package code.designpattern.pipeline;

/**
 * 〈管道〉<p>
 **
 * @author zixiao
 * @date 2019/3/20
 */
public abstract class Pipeline{

    protected Stage first = new PreStage() {

        @Override
        public void entry(PipelineContext context, Object t, Object... args)
                throws Throwable {
            super.fireEntry(context, t, args);
        }

        @Override
        public void exit(PipelineContext context, Object... args) {
            super.fireExit(context, args);
        }

    };

    protected Stage end = first;

    /**
     * Add a stage to the head of this slot chain.
     *
     * @param stage stage to be added.
     */
    public abstract void addFirst(Stage stage);

    /**
     * Add a stage to the tail of this slot chain.
     *
     * @param stage stage to be added.
     */
    public abstract void addLast(Stage stage);


    public void entry(PipelineContext context, Object t, Object... args)
            throws Throwable {
        first.transformEntry(context, t, args);
    }

    public void exit(PipelineContext context, Object... args) {
        first.exit(context, args);
    }

}
