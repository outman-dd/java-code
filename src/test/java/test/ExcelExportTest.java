package test;

import code.util.DateFormatUtils;
import code.util.ExcelExportUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.util.StopWatch;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 〈Excel导出测试〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/6/6
 */
public class ExcelExportTest {

    public static void main(String[] args) throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("数组方式导出");
        exportTest();
        stopWatch.stop();

        stopWatch.start("反射方式导出");
        exportTest2();
        stopWatch.stop();

        System.out.println(stopWatch.prettyPrint());

//        convert("abc");
//        convert(new Date());
//        convert(new BigDecimal("10.00"));
//        convert(100L);
//        convert(0.01);
//        convert(10);
//        convert(Short.valueOf("9"));
//        convert(true);
    }

    private static void exportTest() throws IOException {
        String[] headArray = {"id", "姓名", "生日", "是否成人", "身高"};
        int total = 10 * 10000;
        List<Object[]> valueList = new ArrayList<Object[]>(total);
        for(int i=0; i < total; i++){
            Object[] value = {i, "zixiao"+i, new Date(), new Random().nextBoolean(), "1.8"};
            valueList.add(value);
        }
        SXSSFWorkbook wb = ExcelExportUtils.createExcel(headArray, valueList, 1);
        wb.write(new FileOutputStream("/data/www/1.xlsx"));
    }

    private static void exportTest2() throws IOException {
        String[] headArray = {"id", "姓名", "生日", "是否成人", "身高"};
        String[] attrArray = {"id", "name", "birthday", "audit", "height"};
        int total = 10 * 10000;
        List<Student> valueList = new ArrayList<Student>(total);
        for(int i=0; i < total; i++){
            valueList.add(new Student(i, "zixiao"+i, new Date(), new Random().nextBoolean(), "1.8"));
        }
        SXSSFWorkbook wb = ExcelExportUtils.createExcel(headArray, attrArray, valueList, 1);
        wb.write(new FileOutputStream("/data/www/2.xlsx"));
    }

    private static void convert(Object value){
        if (value instanceof String) {
            String string = (String) value;
            System.out.println(string);
        } else if (value instanceof Number) {
            Number number = (Number) value;
            System.out.println(number.toString());
        } else if (value instanceof Date) {
            Date date = (Date) value;
            System.out.println(DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss"));
        } else if(value instanceof Boolean){
            Boolean bValue = (Boolean) value;
            System.out.println(bValue.toString());
        } else {
            value.toString();
        }
    }

    static class Student{
        private int id;

        private String name;

        private Date birthday;

        private boolean audit;

        private BigDecimal height;

        public Student(int id, String name, Date birthday, boolean audit, String height) {
            this.id = id;
            this.name = name;
            this.birthday = birthday;
            this.audit = audit;
            this.height = new BigDecimal(height);
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getBirthday() {
            return birthday;
        }

        public void setBirthday(Date birthday) {
            this.birthday = birthday;
        }

        public boolean isAudit() {
            return audit;
        }

        public void setAudit(boolean audit) {
            this.audit = audit;
        }

        public BigDecimal getHeight() {
            return height;
        }

        public void setHeight(BigDecimal height) {
            this.height = height;
        }
    }
}
