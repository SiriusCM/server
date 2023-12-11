package org.sirius.server.conf;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gaoliandi
 * @date 2023/11/6
 */
public abstract class ConfBase {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected static Map<String, Integer> getIndexMap(Sheet sheet, int nameIndex) {
        Map<String, Integer> indexMap = new HashMap<>();
        Row name = sheet.getRow(nameIndex);
        for (int i = 0; i < name.getPhysicalNumberOfCells(); i++) {
            Cell indexCell = name.getCell(i);
            if (indexCell == null) {
                continue;
            }
            indexMap.put(indexCell.getStringCellValue().trim(), i);
        }
        return indexMap;
    }

    protected static void setFieldValue(Object object, Field field, Cell cell) throws IllegalAccessException, ParseException {
        String value = String.valueOf(getCellValue(cell));
        field.set(object, toValue(field.getType(), value));
    }

    private static Object getCellValue(Cell cell) {
        return switch (cell.getCellType()) {
            case BOOLEAN -> cell.getBooleanCellValue();
            case NUMERIC -> cell.getNumericCellValue();
            case STRING -> cell.getStringCellValue();
            default -> null;
        };
    }

    private static Object toValue(Class<?> clazz, String value) throws ParseException {
        if (clazz.isArray()) {
            String[] values = value.split(",");
            if (clazz.equals(boolean[].class)) {
                boolean[] array = new boolean[values.length];
                for (int i = 0; i < values.length; i++) {
                    array[i] = (boolean) toValue(clazz.getComponentType(), values[i]);
                }
                return array;
            } else if (clazz.equals(int[].class)) {
                int[] array = new int[values.length];
                for (int i = 0; i < values.length; i++) {
                    array[i] = (int) toValue(clazz.getComponentType(), values[i]);
                }
                return array;
            } else if (clazz.equals(long[].class)) {
                long[] array = new long[values.length];
                for (int i = 0; i < values.length; i++) {
                    array[i] = (long) toValue(clazz.getComponentType(), values[i]);
                }
                return array;
            } else if (clazz.equals(float[].class)) {
                float[] array = new float[values.length];
                for (int i = 0; i < values.length; i++) {
                    array[i] = (float) toValue(clazz.getComponentType(), values[i]);
                }
                return array;
            } else if (clazz.equals(double[].class)) {
                double[] array = new double[values.length];
                for (int i = 0; i < values.length; i++) {
                    array[i] = (double) toValue(clazz.getComponentType(), values[i]);
                }
                return array;
            } else if (clazz.equals(String[].class)) {
                String[] array = new String[values.length];
                for (int i = 0; i < values.length; i++) {
                    array[i] = (String) toValue(clazz.getComponentType(), values[i]);
                }
                return array;
            } else if (clazz.equals(Date[].class)) {
                Date[] array = new Date[values.length];
                for (int i = 0; i < values.length; i++) {
                    array[i] = (Date) toValue(clazz.getComponentType(), values[i]);
                }
                return array;
            }
        } else {
            if (clazz.equals(boolean.class)) {
                if (isNumber(value)) {
                    return false;
                } else {
                    return Boolean.valueOf(value);
                }
            } else if (clazz.equals(int.class)) {
                if (isNumber(value)) {
                    return (int) Double.parseDouble(value);
                } else {
                    return 0;
                }
            } else if (clazz.equals(long.class)) {
                if (isNumber(value)) {
                    return (long) Double.parseDouble(value);
                } else {
                    return 0L;
                }
            } else if (clazz.equals(float.class)) {
                if (isNumber(value)) {
                    return (float) Double.parseDouble(value);
                } else {
                    return 0F;
                }
            } else if (clazz.equals(double.class)) {
                if (isNumber(value)) {
                    return Double.parseDouble(value);
                } else {
                    return 0D;
                }
            } else if (clazz.equals(String.class)) {
                return value;
            } else if (clazz.equals(Date.class)) {
                if (value.equals("null") || value.equals("-1")) {
                    return null;
                } else {
                    return simpleDateFormat.parse(value);
                }
            }
        }
        return null;
    }

    private static boolean isNumber(String value) {
        return value.matches("-?\\d+(\\.\\d+)?");
    }
}
