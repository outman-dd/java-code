package code.kv;

import code.util.DateFormatUtils;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/5/20
 */
public class KvTest {

    private static Map<String, KvModel> kvModelMap = new HashMap<>();

    private static Map<String, KvField> kvFieldMap = new HashMap<>();

    private static Map<String, KvTemplate> kvTemplateMap = new HashMap<>();

    private static Set<String> booleanSet = new HashSet<>();

    static {
        kvModelMap.put("loan", new KvModel(1, "loan", "进件"));

        KvField kvField = new KvField(1, "age", "年龄", DataType.INTEGER, "loan");
        kvFieldMap.put(kvField.getCode(), kvField);
        kvField = new KvField(2, "name", "姓名", DataType.STRING, "loan");
        kvFieldMap.put(kvField.getCode(), kvField);
        kvField = new KvField(3, "birthday", "出生年月日", DataType.DATE, "loan");
        kvFieldMap.put(kvField.getCode(), kvField);
        kvField = new KvField(4, "amount", "金额", DataType.DECIMAL, "loan");
        kvFieldMap.put(kvField.getCode(), kvField);

        KvTemplate kvTemplate = new KvTemplate("tpl1", "age", DataType.INTEGER, true);
        kvTemplateMap.put(kvTemplate.getBizType() + kvTemplate.getFieldCode(), kvTemplate);

        kvTemplate = new KvTemplate("tpl1", "name", DataType.STRING, true);
        kvTemplate.setNotBlank(true);
        kvTemplate.setMaxLength(20);
        kvTemplateMap.put(kvTemplate.getBizType() + kvTemplate.getFieldCode(), kvTemplate);

        kvTemplate = new KvTemplate("tpl1", "birthday", DataType.DATE, true);
        kvTemplateMap.put(kvTemplate.getBizType() + kvTemplate.getFieldCode(), kvTemplate);

        kvTemplate = new KvTemplate("tpl1", "amount", DataType.DECIMAL, true);
        kvTemplateMap.put(kvTemplate.getBizType() + kvTemplate.getFieldCode(), kvTemplate);

        kvTemplate = new KvTemplate("tpl1", "ok", DataType.BOOLEAN, true);
        kvTemplateMap.put(kvTemplate.getBizType() + kvTemplate.getFieldCode(), kvTemplate);

        booleanSet.add("0");
        booleanSet.add("1");
        booleanSet.add("false");
        booleanSet.add("true");
        booleanSet.add("yes");
        booleanSet.add("no");
        booleanSet.add("否");
        booleanSet.add("是");
    }

    public static void main(String[] args) {
        List<KvData> list = new ArrayList<>();
        list.add(new KvData("123", "tpl1", "loan", "age", "30", 0));
        list.add(new KvData("123", "tpl1", "loan", "name", "zixiao", 1));
        list.add(new KvData("123", "tpl1", "loan", "birthday","2019-01-02", 2));
        list.add(new KvData("123", "tpl1", "loan", "amount", "100.00", 3));
        list.add(new KvData("123", "tpl1", "loan", "ok", "是", 4));

        list.forEach(kvData -> {
            checkField(kvData);
        });
    }

    private static void checkField(KvData kvData){
        KvTemplate kvTemplate = kvTemplateMap.get(kvData.getBizType() + kvData.getFieldCode());
        String value = kvData.getValue();
        DataType dataType = kvTemplate.getDataType();
        if(kvTemplate.isRequired()){
            Assert.notNull(value, "字段不能为null");
        }
        if(kvTemplate.getMaxLength() > 0){
            Assert.isTrue(value.length() <= kvTemplate.getMaxLength(), "字段长度超过限制");
        }
        if(dataType.equals(DataType.STRING)){
            if(kvTemplate.isNotBlank()){
                Assert.hasText(value, "字段不能为空");
            }
        }else if(dataType.equals(DataType.DATE) || dataType.equals(DataType.DATETIME)){
            try {
                DateFormatUtils.tryParse(value);
            } catch (ParseException e) {
                throw new IllegalArgumentException("字段格式错误，"+dataType.name());
            }
        }else if(dataType.equals(DataType.INTEGER)){
            try {
                Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("字段格式错误，"+dataType.name());
            }
        }else if(dataType.equals(DataType.DECIMAL)){
            try {
                new BigDecimal(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("字段格式错误，"+dataType.name());
            }
        }else if(dataType.equals(DataType.BOOLEAN)){
            if(!booleanSet.contains(value.toLowerCase())){
                throw new IllegalArgumentException("字段格式错误，"+dataType.name());
            }
        }
    }
}
