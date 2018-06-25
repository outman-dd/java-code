package code.performance.cat;

import org.springframework.util.StopWatch;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.*;

/**
 * 〈CatSortCommand〉<p>
 *
 *
 * @author zixiao
 * @date 18/6/12
 */
public class CatSortCommand implements ICommand<CatSortResult>{

    /**
     * 线程池队列大小
     */
    private final int THREAD_POOL_SIZE =  Runtime.getRuntime().availableProcessors();

    /**
     * 队列大小
     *  等于 linux文件限制数 32000
     */
    private final int QUEUE_SIZE  = 32000;

    /**
     * 线程池
     */
    private ExecutorService executorService;

    /**
     * 异步执行服务
     */
    private CompletionService<Map<String, Integer>> completionService;

    /**
     * MD5 与 行字符串的映射关系
     */
    private final ConcurrentHashMap<String, String> md5LineMap = new ConcurrentHashMap<String, String>(1024);

    private final String fileDir;

    private final String fileRegex;

    private final String keyword;

    private final SortType sortType;

    public CatSortCommand(String filePath, String keyword, SortType sortType) {
        this.fileDir = filePath.substring(0, filePath.lastIndexOf("/"));
        this.fileRegex = filePath.substring(filePath.lastIndexOf("/")+1)
                .replaceAll("\\.", "\\\\.").replaceAll("\\*", ".*");
        this.keyword = keyword;
        this.sortType = sortType;
    }

    public void init(){
        executorService = new ThreadPoolExecutor(THREAD_POOL_SIZE,
                THREAD_POOL_SIZE,
                1000 * 60,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(QUEUE_SIZE));
        completionService = new ExecutorCompletionService(executorService);
    }

    @Override
    public Result<CatSortResult> execute() {
        try {
            //0、初始化
            init();
            StopWatch stopWatch = new StopWatch("CatSortCommand");

            //1、匹配获取文件列表
            stopWatch.start("匹配获取文件列表");
            List<File> files = listFiles(fileDir, fileRegex);
            stopWatch.stop();

            //2、多线程统计结果
            for(final File file : files){
                completionService.submit(new Callable<Map<String, Integer>>() {
                    @Override
                    public Map<String, Integer> call() throws Exception {
                        return grepAndCount(file, keyword);
                    }
                });
            }

            //3、获取结果，并合并
            Map<String, Integer> md5CountMap;

            stopWatch.start("多线程统计, 合并结果");
            md5CountMap = getAndMerge(files.size());
//            md5CountMap = countAndMergeForkJoin(files.size());
            stopWatch.stop();

            //4、排序
            stopWatch.start("排序");
            List<Map.Entry<String, Integer>> md5CountList = new ArrayList<Map.Entry<String, Integer>>(md5CountMap.size());
            for(Map.Entry entry : md5CountMap.entrySet()){
                md5CountList.add(entry);
            }
            sort(md5CountList, sortType);
            stopWatch.stop();

            //5、格式化并输出结果
            stopWatch.start("格式化");
            List<CatSortResult> resultList = convertAndFormat(md5CountList);
            stopWatch.stop();

            stopWatch.start("输出结果");
            for(CatSortResult catSortResult : resultList){
                System.out.println(catSortResult);
            }
            stopWatch.stop();
            System.out.println(stopWatch.prettyPrint());

            return Result.buildSuccess(resultList);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return Result.buildFail("-1", e.getMessage());
        } finally {
            destroy();
        }
    }

    /**
     * 获取结果，并合并
     * @param fileCount
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private Map<String, Integer> getAndMerge(int fileCount) throws InterruptedException, ExecutionException {
        Map<String, Integer> md5CountMap = new HashMap<String, Integer>(1024);
        for(int i=0; i < fileCount; i++){
            mergeMap(md5CountMap, completionService.take().get());
        }
        return md5CountMap;
    }

    /**
     * 获取结果，并合并
     * @param fileCount
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private Map<String, Integer> getAndMergeForkJoin(int fileCount) throws InterruptedException, ExecutionException {
        List<Map<String, Integer>> maps = new ArrayList<Map<String, Integer>>();
        for(int i=0; i < fileCount; i++){
            maps.add(completionService.take().get());
        }
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        MergeCountTask countTask = new MergeCountTask(maps);
        ForkJoinTask<Map<String, Integer>> forkJoinRet = forkJoinPool.submit(countTask);
        return forkJoinRet.get();
    }

    /**
     * 格式化并输出结果
     * @param resultList
     * @return
     */
    private List<CatSortResult> convertAndFormat(List<Map.Entry<String, Integer>> resultList) {
        List<CatSortResult> list = new ArrayList<CatSortResult>(resultList.size());
        for(Map.Entry<String, Integer> entry : resultList){
            String md5 = entry.getKey();
            list.add(new CatSortResult(md5LineMap.get(md5), entry.getValue()));
        }
        return list;
    }

    /**
     * 把每个文件的合并 行字符串md5 计数map合并到 全局结果集
     * @param md5CountMap
     * @param md5CountMapEachFile
     */
    private static Map<String, Integer> mergeMap(Map<String, Integer> md5CountMap, Map<String, Integer> md5CountMapEachFile){
        for(Map.Entry<String, Integer> md5CountEachFileEntry :  md5CountMapEachFile.entrySet()){
            String md5 = md5CountEachFileEntry.getKey();
            if(md5CountMap.containsKey(md5)){
                md5CountMap.put(md5, md5CountMap.get(md5).intValue() + md5CountEachFileEntry.getValue());
            }else{
                md5CountMap.put(md5, md5CountEachFileEntry.getValue());
            }
        }
        return md5CountMap;
    }

    /**
     * 快排
     * @param list
     * @param sortType
     */
    private void sort(List<Map.Entry<String, Integer>> list, final SortType sortType) {
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                if(SortType.DESC == sortType){
                    //从大到小
                    return (o2.getValue() - o1.getValue());
                } else {
                    //从小到大
                    return (o1.getValue() - o2.getValue());
                }
            }
        });
    }


    /**
     * 获取文件列表
     * @param path 路径
     * @return
     */
    private List<File> listFiles(String path, final String regex){
        File directory = new File(path);
        if(!directory.isDirectory()){
           throw new IllegalArgumentException("'"+path+"'不是目录");
        }

        //文件名匹配
        File[] files = directory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.matches(regex);
            }
        });

        //过滤文件夹
        List<File> fileList = new ArrayList<File>(files.length);
        for(File file : files){
            if(file.isFile()){
                fileList.add(file);
            }
        }
        return fileList;
    }

    /**
     * 查找一个文件中符合关键字的行并计数
     *
     * @param file
     * @param keyword
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    private Map<String,Integer> grepAndCount(File file, String keyword) throws IOException, NoSuchAlgorithmException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        String matchedLineMd5;
        Map<String,Integer> md5CountMap = new HashMap<String,Integer>();
        while( (line = br.readLine()) != null ){
            if(line.contains(keyword)){
                //行字符串转MD5
                matchedLineMd5 = MD5Utils.md5(line);
                if(md5CountMap.containsKey(matchedLineMd5)){
                    int num = md5CountMap.get(matchedLineMd5)+1;
                    md5CountMap.put(matchedLineMd5, num);
                }else{
                    md5LineMap.putIfAbsent(matchedLineMd5, line);
                    md5CountMap.put(matchedLineMd5, 1);
                }
            }
        }
        br.close();
        return md5CountMap;
    }

    public void destroy(){
        if(executorService != null){
            executorService.shutdown();
        }
        md5LineMap.clear();
    }

    /**
     * 合并结果
     */
    private static class MergeCountTask extends RecursiveTask<Map<String, Integer>> {

        private List<Map<String, Integer>> maps;

        public MergeCountTask(List<Map<String, Integer>> maps) {
            this.maps = maps;
        }

        @Override
        protected Map<String, Integer> compute() {
            int mapCount = maps.size();
            if (mapCount == 1){
                return maps.get(0);
            } else if (mapCount == 2){
                return mergeMap(maps.get(0), maps.get(1));
            } else {
                int middle = (mapCount) / 2;
                MergeCountTask task1 = new MergeCountTask(maps.subList(0, middle));
                MergeCountTask task2 = new MergeCountTask(maps.subList(middle, mapCount));
                // 开启线程
                invokeAll(task1, task2);
                Map<String, Integer> joinedMap1 = task1.join();
                Map<String, Integer> joinedMap2 = task2.join();

                return mergeMap(joinedMap1, joinedMap2);
            }
        }
    }

}