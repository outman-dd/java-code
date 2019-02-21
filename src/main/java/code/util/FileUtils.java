package code.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * 〈文件工具〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/2/21
 */
public class FileUtils {

    /**
     * 获取文件列表
     * @param path 路径
     * @return
     */
    public static List<File> listFiles(String path, final String regex){
        File directory = new File(path);
        if(!directory.isDirectory()){
            throw new IllegalArgumentException("'"+path+"'不是目录");
        }

        //文件名匹配
        File[] files = directory.listFiles(new FilenameFilter() {
            @Override
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

}
