package code.concurrency.example.cat;

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
public class CatSortCommand2 implements ICommand<CatSortResult>{

    /**
     * MD5 与 行字符串的映射关系
     */
    private final ConcurrentHashMap<String, String> md5LineMap = new ConcurrentHashMap<String, String>(1024);

    private final String fileDir;

    private final String fileRegex;

    private final String keyword;

    private final SortType sortType;

    public CatSortCommand2(String filePath, String keyword, SortType sortType) {
        this.fileDir = filePath.substring(0, filePath.lastIndexOf("/"));
        this.fileRegex = filePath.substring(filePath.lastIndexOf("/")+1)
                .replaceAll("\\.", "\\\\.").replaceAll("\\*", ".*");
        this.keyword = keyword;
        this.sortType = sortType;
    }

    public void init(){

    }

    @Override
    public Result<CatSortResult> execute() {
        try {
            //0、初始化
            init();
            StopWatch stopWatch = new StopWatch("CatSortCommand2");

            //1、匹配获取文件列表
            stopWatch.start("匹配获取文件列表");
            final List<File> files = listFiles(fileDir, fileRegex);
            stopWatch.stop();

            //2、多线程统计结果
            stopWatch.start("多线程统计");
            final List<Map<String, Integer>> filesMap = new ArrayList<Map<String, Integer>>(files.size());
            ForkJoinPool forkJoinPool = new ForkJoinPool();
            ForkJoinTask<Void> forkJoinRet = forkJoinPool.submit(new CatCountTask(files, filesMap));
            forkJoinRet.get();
            stopWatch.stop();

            //3、合并结果
            stopWatch.start("合并结果");
            Map<String, Integer> md5CountMap = getAndMerge(filesMap);
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
     * @param mapList
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private Map<String, Integer> getAndMerge(List<Map<String, Integer>> mapList) throws InterruptedException, ExecutionException {
        Map<String, Integer> md5CountMap = new HashMap<String, Integer>(1024);
        for(Map<String, Integer> map : mapList){
            mergeMap(md5CountMap, map);
        }
        return md5CountMap;
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
            @Override
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
        md5LineMap.clear();
    }

    /**
     * 合并结果
     */
    private  class CatCountTask extends RecursiveAction {

        private List<File> files;

        private List<Map<String, Integer>> allMap;

        public CatCountTask(List<File> files, List<Map<String, Integer>> allMap) {
            this.files = files;
            this.allMap = allMap;
        }

        @Override
        protected void compute() {
            int fileCount = files.size();
            if (fileCount == 1){
                try {
                    allMap.add(grepAndCount(files.get(0), keyword));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            } else {
                int middle = (fileCount) / 2;
                CatCountTask task1 = new CatCountTask(files.subList(0, middle), allMap);
                CatCountTask task2 = new CatCountTask(files.subList(middle, fileCount), allMap);
                // 开启线程
                invokeAll(task1, task2);
                task1.join();
                task2.join();
            }
        }
    }

}