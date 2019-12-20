package code.algorithm.divideconquer;

import code.collection.MinHeap;
import code.collection.MinArray;
import code.io.file.StringFileReader;
import code.util.FileUtils;
import org.springframework.util.StopWatch;

import java.io.*;
import java.util.*;

/**
 * 〈WorkCount〉<p>
 * MapReduce，分治算法
 *
 * @author zixiao
 * @date 2019/2/21
 */
public class WordCountMain {

    private static int ONE_YI = 100000000;

    private static String lineSeparator = java.security.AccessController.doPrivileged(
            new sun.security.action.GetPropertyAction("line.separator"));

    private static String fileRoot = "/Users/zixiao/logs/";

    private static String fileName = "words.txt";

    private static String filePath = fileRoot + fileName;

    private static String subFilePrefix = "words_";

    private static String subFileRegex = "words_[0-9]{4}";

    private static String countSuffix = ".count";

    private static String subFileCountRegex = subFileRegex + "\\" + countSuffix;

    public static void main(String[] args) throws IOException, InterruptedException {
        int subFileSize = 100;
        int k = 100;
        //1 生成文件
        if(!new File(filePath).exists()){
            genRandomWordFile(ONE_YI/10);
        }

        StopWatch stopWatch = new StopWatch("Word Count");
        //2 文件按word hash切分
        stopWatch.start("splitWordsFile");
        splitWordsFile(subFileSize);
        stopWatch.stop();

        //3 统计并写入子文件中
        stopWatch.start("countSubFile");
        List<File> subFiles = FileUtils.listFiles(fileRoot, subFileRegex);
        subFiles.parallelStream().forEach(subFile -> {
            try {
                countSubFile(subFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        stopWatch.stop();

        //4 所有统计结果写入topK 最小堆数组
        List<File> subCountFiles = FileUtils.listFiles(fileRoot, subFileCountRegex);
        stopWatch.start("array topK");
        MinArray<Pair> minArray = new MinArray<Pair>(k, new PairCompatator());
        subCountFiles.forEach(subCountFile -> {
            try {
                doTopK(subCountFile, minArray);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        stopWatch.stop();

        stopWatch.start("minHeap topK");
        MinHeap<Pair> minHeap = new MinHeap<>(k, new PairCompatator());
        subCountFiles.forEach(subCountFile -> {
            try {
                doTopK(subCountFile, minHeap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        stopWatch.stop();

        System.out.println(stopWatch.prettyPrint());

        System.out.println("+++++++++++++ Top K +++++++++++++");
        minArray.sortedList().forEach(System.out::println);

        System.out.println("+++++++++++++ Top K +++++++++++++");
        minHeap.sortedList().forEach(System.out::println);

        //5 清理
        subFiles.forEach(java.io.File::delete);
        subCountFiles.forEach(java.io.File::delete);
    }

    private static void splitWordsFile(int fileNum) throws IOException {
        List<BufferedWriter> writers = new ArrayList<>(fileNum);
        for (int i = 0; i < fileNum; i++) {
            String fileName = fileRoot + subFilePrefix + String.format("%04d", i);
            writers.add(new BufferedWriter(new FileWriter(fileName)));
        }
        int total = 0;
        StringFileReader fileReader = new StringFileReader(filePath);
        while (true){
            List<String> strings = fileReader.readData(1000);
            total += strings.size();
            for (String string : strings) {
                BufferedWriter writer = writers.get(hashIndex(string, fileNum));
                writer.write(string);
                writer.newLine();
            }
            if(strings.size() < 1000){
                break;
            }
        }
        writers.forEach(writer -> {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println(">>>Total:"+total);
    }


    private static int hashIndex(String string, int fileNum){
        return Math.abs(string.hashCode())%fileNum;
    }

    private static void doTopK(File subCountFile, MinArray<Pair> minArray) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(subCountFile));
        while (true){
            String string = reader.readLine();
            if(string == null){
                break;
            }
            minArray.add(stringToPair(string));
        }
        reader.close();
    }

    private static void doTopK(File subCountFile, PriorityQueue<Pair> minHeap) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(subCountFile));
        while (true){
            String string = reader.readLine();
            if(string == null){
                break;
            }
            minHeap.add(stringToPair(string));
        }
        reader.close();
    }

//    /**
//     * split -l 1000000 -a 4 words.txt words_
//     * @param subFilePrefix
//     * @param batchSize
//     * @return
//     */
//    private static void split(String subFilePrefix, int batchSize) throws IOException, InterruptedException {
//        String command = String.format("split -l %s -a 4 %s %s", batchSize, fileName, subFilePrefix);
//        System.out.println(command);
//    }

    private static String countSubFile(File subFile) throws IOException {
        Map<String, Integer> countMap = new HashMap<String, Integer>();
        StringFileReader fileReader = new StringFileReader(subFile);
        while (true){
            List<String> words = fileReader.readData(1000);
            words.forEach(word -> {
                Integer count = countMap.get(word);
                if(count == null){
                    countMap.put(word, 1);
                }else{
                    countMap.put(word, ++count);
                }
            });
            if(words.size() < 1000){
                break;
            }
        }
        fileReader.close();

        String subFileName = fileRoot + subFile.getName() + countSuffix;
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(subFileName));
        Iterator<Map.Entry<String, Integer>> iterator = countMap.entrySet().iterator();
        boolean first = true;
        while (iterator.hasNext()){
            if(first){
                first = false;
            }else{
                bufferedWriter.newLine();
            }
            bufferedWriter.write(entryToString(iterator.next()));
        }
        bufferedWriter.flush();
        bufferedWriter.close();
        return subFileName;
    }

    private static String entryToString(Map.Entry<String, Integer> entry){
        return entry.getKey() + "," + entry.getValue();
    }

    private static Pair stringToPair(String string){
        String[] strArray = string.split(",");
        return new Pair(strArray[0], Integer.parseInt(strArray[1]));
    }

    private static void genRandomWordFile(int size) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath));
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                bufferedWriter.newLine();
            }
            bufferedWriter.write(randomWord());
        }
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    private static char[] randomWord(){
        int charSize = (int)(2 + Math.random()*3);
        char[] chars = new char[charSize];
        for (int i = 0; i < charSize; i++) {
            chars[i] = randomChar();
        }
        return chars;
    }

    private static char randomChar(){
        return (char)(int)(Math.random()*26+97);
    }

    private static class Pair{

        private String word;

        private Integer count;

        public Pair(String word, Integer count) {
            this.word = word;
            this.count = count;
        }

        public String getWord() {
            return word;
        }

        public Integer getCount() {
            return count;
        }

        @Override
        public String toString() {
            return "word='" + word + ", count=" + count;
        }
    }

    private static class PairCompatator implements Comparator<Pair> {
        @Override
        public int compare(Pair o1, Pair o2) {
            return o1.count.compareTo(o2.count);
        }
    }

}
