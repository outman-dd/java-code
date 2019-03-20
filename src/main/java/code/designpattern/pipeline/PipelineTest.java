package code.designpattern.pipeline;

import code.designpattern.pipeline.impl.PrePostPipeline;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 〈测试〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/20
 */
public class PipelineTest {

    @Test
    public void test() throws Throwable {
        Pipeline pipeline = new PrePostPipeline();
        pipeline.addFirst(new PrePrintStage(1));
        pipeline.addLast(new PrePrintStage(2));

        pipeline.addFirst(new PrintStage(1));
        pipeline.addLast(new PrintStage(2));

        pipeline.addFirst(new PostPrintStage(1));
        pipeline.addLast(new PostPrintStage(2));

        pipeline.entry(null, "");
    }

    @Test
    public void batchTest() throws Throwable {
        List<Command> commands = new ArrayList<>();
        commands.add(pipeline -> pipeline.addFirst(new PrePrintStage(1)));
        commands.add(pipeline -> pipeline.addLast(new PrePrintStage(2)));
        commands.add(pipeline -> pipeline.addFirst(new PrintStage(1)));
        commands.add(pipeline -> pipeline.addLast(new PrintStage(2)));
        commands.add(pipeline -> pipeline.addFirst(new PostPrintStage(1)));
        commands.add(pipeline -> pipeline.addLast(new PostPrintStage(2)));

        for (int i = 0; i < 1000; i++) {
            final PrePostPipeline pipeline = new PrePostPipeline();
            Collections.shuffle(commands);
            commands.forEach(command -> command.execute(pipeline));
            validate(pipeline);
            pipeline.entry(null, "");
        }

    }

    private interface Command{
        void execute(PrePostPipeline pipeline);
    }

    private void validate(PrePostPipeline pipeline){
        Stage stage = pipeline.first;
        Stage stage1 = stage.getNext();
        Stage stage2 = stage1.getNext();
        Assert.isTrue(stage1 instanceof PrePrintStage, "stage1");
        Assert.isTrue(stage2 instanceof PrePrintStage, "stage2");
        Assert.isTrue(isSmall(stage1, stage2), "stage1 < stage2");

        Stage stage3 = stage2.getNext();
        Stage stage4 = stage3.getNext();
        Assert.isTrue(stage3 instanceof PrintStage, "stage3");
        Assert.isTrue(stage4 instanceof PrintStage, "stage4");
        Assert.isTrue(isSmall(stage3, stage4), "stage3 < stage4");

        Stage stage5 = stage4.getNext();
        Stage stage6 = stage5.getNext();
        Assert.isTrue(stage5 instanceof PostPrintStage, "stage5");
        Assert.isTrue(stage6 instanceof PostPrintStage, "stage6");
        Assert.isTrue(isSmall(stage5, stage6), "stage5 < stage6");
    }

    private boolean isSmall(Stage a, Stage b){
        return Integer.valueOf(a.toString()).compareTo(Integer.valueOf(b.toString())) == -1;
    }


    private class PrintStage extends Stage<String> {

        private int num = 20000;

        public PrintStage(int add){
            super();
            this.num += add;
        }

        @Override
        public void entry(PipelineContext context, String param, Object... args) throws Throwable {
            System.out.println("===DEF "+num);
            super.fireEntry(context, param, args);
        }

        @Override
        public void exit(PipelineContext context, Object... args) {
            super.fireExit(context, args);
        }


        @Override
        public String toString() {
            return String.valueOf(num);
        }
    }

    private class PrePrintStage extends PreStage<String> {

        private int num = 10000;

        public PrePrintStage(int add){
            super();
            this.num += add;
        }

        @Override
        public void entry(PipelineContext context, String param, Object... args) throws Throwable {
            System.out.println(">>>PRE "+num);
            super.fireEntry(context, param, args);
        }

        @Override
        public void exit(PipelineContext context, Object... args) {
            super.fireExit(context, args);
        }

        @Override
        public String toString() {
            return String.valueOf(num);
        }
    }

    private class PostPrintStage extends PostStage<String> {

        private int num = 30000;

        public PostPrintStage(int add){
            super();
            this.num += add;
        }

        @Override
        public void entry(PipelineContext context, String param, Object... args) throws Throwable {
            System.out.println("<<<POST "+num);
            super.fireEntry(context, param, args);
        }

        @Override
        public void exit(PipelineContext context, Object... args) {
            super.fireExit(context, args);
        }

        @Override
        public String toString() {
            return String.valueOf(num);
        }
    }
}
