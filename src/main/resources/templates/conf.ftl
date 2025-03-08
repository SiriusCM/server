package ${packageName};

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.jow.littlebug.game.conf.ConfBase;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class ${className} extends ConfBase {
    <#list fieldList as field>
    public ${field.type} ${field.name};

    </#list>
    private static List<${className}> dataList = new ArrayList<>();
    <#if !primary.nullAble>

    private static Map<Object, ${className}> primaryMap = new HashMap<>();
    </#if>

    <#list keyList as key>
    private static Map<Object, List<${className}>> ${key[0]}Map = new HashMap<>();

    </#list>
    <#if !primary.nullAble>

    public static ${className} get(Object sn) {
        return primaryMap.get(sn);
    }
    </#if>

    <#list keyList as key>
    public static List<${className}> findAllBy${key[1]}(Object sn) {
        return ${key[0]}Map.get(sn);
    }

    </#list>
    public static List<${className}> findAll() {
        return dataList;
    }

    public static void reload(Sheet sheet, int nameIndex, int offset) throws NoSuchFieldException, IllegalAccessException, ParseException {
        List<${className}> list = new ArrayList<>();
        <#if !primary.nullAble>
        Map<Object, ${className}> map = new HashMap<>();
        </#if>
        Map<String, Integer> indexMap = getIndexMap(sheet, nameIndex);
        for (int i = offset; i < sheet.getPhysicalNumberOfRows(); i++) {
            ${className} conf = new ${className}();
            Row row = sheet.getRow(i);
            for (Field field : ${className}.class.getFields()) {
                int index = indexMap.get(field.getName());
                Cell cell = row.getCell(index);
                if (cell == null) {
                    continue;
                }
                setFieldValue(conf, field, row.getCell(index));
            }
            list.add(conf);
            <#if !primary.nullAble>
            map.put(conf.sn, conf);
            </#if>
        }
        dataList = list;
        <#if !primary.nullAble>
        primaryMap = map;
        </#if>
        <#list keyList as key>
        ${key[0]}Map = list.stream().collect(Collectors.groupingBy(conf -> conf.${key[0]}));
        </#list>
    }
}