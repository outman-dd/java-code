package code.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Excel导出工具类
 *
 * @author zixiao
 */
public class ExcelExportUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelExportUtils.class);

    private static final int BATCH_SIZE = 1000;

    /**
     * 导出Excel文件的通用方法
     *
     * @param headArray head头的值数组
     * @param fileName 文件名
     * @param valueList 值列表
     * @param response
     * @param headRow 从第几行开始，只能>=1
     */
    public static void exportExcel(String[] headArray, String fileName, List<Object[]> valueList, HttpServletResponse response, int headRow) {
        OutputStream outputStream = null;
        try {
            SXSSFWorkbook wb = createExcel(headArray, valueList, headRow);

            response.setContentType("application/force-download;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Location", fileName);
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

            outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.close();
        } catch (Exception e) {
            LOGGER.error("Export excel error", e);
        } finally {
            if(outputStream != null){
                IOUtils.closeQuietly(outputStream);
            }
        }
    }

    /**
     * 创建Excel
     *
     * @param headArray
     * @param valueList 值列表
     * @param headRow   从第几行开始，只能>=1
     * @return
     */
    public static SXSSFWorkbook createExcel(String[] headArray, List<Object[]> valueList, int headRow) {
        if(null == headArray || headArray.length == 0){
            throw new IllegalArgumentException("参数错误: 表头列表不能为空");
        }
        if(valueList == null || valueList.size() == 0){
            throw new IllegalArgumentException("参数错误: 数据列表不能为空");
        }

        SXSSFWorkbook wb = new SXSSFWorkbook(getRowAccessWindowSize(valueList.size(), headRow));
        createSheet(wb, "sheet1", headArray, valueList, headRow);
        return wb;
    }

    /**
     * 创建sheet
     * @param wb
     * @param sheet1
     * @param headArray
     * @param valueList
     * @param headRow
     */
    private static void createSheet(SXSSFWorkbook wb, String sheet1, String[] headArray, List<Object[]> valueList, int headRow) {
        Sheet sheet = wb.createSheet(sheet1);
        XSSFRichTextString xssfValue;
        Row row = sheet.createRow(headRow - 1);
        // 打印表头
        for (int i = 0; i < headArray.length; i++) {
            Cell cell = row.createCell(i);
            xssfValue = new XSSFRichTextString(headArray[i]);
            cell.setCellValue(xssfValue);
        }

        Object value = null;
        // 打印具体值
        for (int i = 0; i < valueList.size(); i++) {
            row = sheet.createRow(i + headRow);
            for (int j = 0; j < valueList.get(i).length; j++) {
                Cell cell = row.createCell(j);
                value = valueList.get(i)[j];
                if(value != null){
                    setCellValue(cell, value);
                }
            }
        }
    }

    /**
     * 导出Excel文件的通用方法
     *
     * @param headArray head头的值数组
     * @param fileName 文件名
     * @param valueList 值列表
     * @param response
     * @param headRow 从第几行开始，只能>=1
     */
    public static void exportExcel(String[] headArray, String[] attrArray, String fileName, List<?> valueList, HttpServletResponse response, int headRow) {
        OutputStream outputStream = null;
        try {
            SXSSFWorkbook wb = createExcel(headArray, attrArray, valueList, headRow);

            response.setContentType("application/force-download;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Location", fileName);
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

            outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.close();
        } catch (Exception e) {
            LOGGER.error("Export excel error", e);
        } finally {
            if(outputStream != null){
                IOUtils.closeQuietly(outputStream);
            }
        }
    }

    /**
     * 创建Excel
     *
     * @param headArray 表头的值数组
     * @param attrArray 显示的属性数组
     * @param valueList 值列表
     * @param headRow 从第几行开始，只能>=1
     * @return
     * @throws IOException
     */
    public static SXSSFWorkbook createExcel(String[] headArray, String[] attrArray, List<?> valueList, int headRow) throws IOException {
        if(null == headArray || headArray.length == 0){
            throw new IllegalArgumentException("参数错误: 表头列表不能为空");
        }
        if(valueList == null || valueList.size() == 0){
            throw new IllegalArgumentException("参数错误: 数据列表不能为空");
        }
        SXSSFWorkbook wb = new SXSSFWorkbook(getRowAccessWindowSize(valueList.size(), headRow));
        try {
            createSheet(wb, "sheet1", headArray, attrArray, valueList, headRow);
        } catch (Exception e) {
            throw new IOException("创建工作表失败", e);
        }
        return wb;
    }

    /**
     * 创建sheet
     * @param workbook  工作表
     * @param sheetName sheet名称
     * @param headArray 表头的值数组
     * @param attrArray 显示的属性数组
     * @param valueList 值列表
     * @param headRow
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private static void createSheet(SXSSFWorkbook workbook, String sheetName, String[] headArray,
                                    String[] attrArray, List<?> valueList, int headRow) throws InvocationTargetException, IllegalAccessException {
        Sheet sheet1 = workbook.createSheet(sheetName);

        Row header = sheet1.createRow(headRow - 1);
        // 打印表头
        for (int i = 0; i < headArray.length; i++) {
            Cell headCell = header.createCell(i);
            headCell.setCellValue(new XSSFRichTextString(headArray[i]));
        }
        // 打印具体值
        for (int i = 0; i < valueList.size(); i++) {
            Row row = sheet1.createRow(i + headRow);
            // 获取列表里面的值，并进行遍历得到每个属性的值
            Object line = valueList.get(i);
            if(line instanceof Map){
                // map对象
                for (int j = 0; j < attrArray.length; j++) {
                    String attrName = attrArray[j];
                    Cell cell = row.createCell(j);
                    setCellValue(cell, (Map)line, attrName);
                }
            }else{
                // 获得对象所有属性
                Field[] fields = line.getClass().getDeclaredFields();
                Field[] fatherFields = getFatherField(line);
                Field[] allFields = ArrayUtils.addAll(fields, fatherFields);
                for (int j = 0; j < attrArray.length; j++) {
                    String attrName = attrArray[j];
                    Cell cell = row.createCell(j);
                    setCellValue(cell, line, attrName, allFields);
                }
            }
        }
    }

    private static Field[] getFatherField(Object obj){
        Class<?> fatherClazz = obj.getClass().getSuperclass();
        Field[] fatherFields = null;
        while (fatherClazz != Object.class){
            fatherFields = ArrayUtils.addAll(fatherFields, fatherClazz.getDeclaredFields());
            fatherClazz = fatherClazz.getSuperclass();
        }
        return fatherFields;
    }

    private static Method getMethod(Class c, String method){
        Method me = null;
        try{
            me  = c.getMethod(method, null);
        }catch(Exception e){
            //ignore
        }
        return me;
    }

    /**
     * 设置单元格值(Map)
     * @param cell
     * @param line
     * @param attrName
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private static void setCellValue(Cell cell, Map<String, Object> line, String attrName) throws IllegalAccessException, InvocationTargetException {
        Object value = line.get(attrName);
        if (null != value) {
            setCellValue(cell, value);
        }
    }

    /**
     * 设置单元格值(Object)
     * @param cell
     * @param line
     * @param attrName
     * @param allFields
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private static void setCellValue(Cell cell, Object line, String attrName, Field[] allFields)
            throws IllegalAccessException, InvocationTargetException {
        for (int k = 0; k < allFields.length; k++) {
            Field field = allFields[k];
            field.setAccessible(true);
            String attribute = field.getName();

            Method attrMethod = getMethod(line.getClass(), attrName);
            if (attribute.equals(attrName) || attrMethod != null) {
                Object value = null;
                if(attribute.equals(attrName)) {
                    value = field.get(line);
                } else if(attrMethod != null){
                    attrMethod.setAccessible(true);
                    value = attrMethod.invoke(line);
                }
                if (null != value) {
                    setCellValue(cell, value);
                }
            }
        }
    }


    private static int getRowAccessWindowSize(int valueCount, int headRow){
        return valueCount < BATCH_SIZE ? (valueCount+headRow) : BATCH_SIZE;
    }

    /**
     * 设置单元格的值
     * @param cell
     * @param value
     */
    private static void setCellValue(Cell cell, Object value){
        if (value instanceof String) {
            String string = (String) value;
            cell.setCellValue(new XSSFRichTextString(string));
        } else if (value instanceof Number) {
            Number number = (Number) value;
            cell.setCellValue(number.toString());
        } else if (value instanceof Date) {
            Date date = (Date) value;
            cell.setCellValue(DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss"));
        } else if(value instanceof Boolean){
            Boolean bValue = (Boolean) value;
            cell.setCellValue(bValue);
        } else {
            cell.setCellValue(new XSSFRichTextString(value.toString()));
        }
    }

}
