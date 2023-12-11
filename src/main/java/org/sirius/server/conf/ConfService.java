package org.sirius.server.conf;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author gaoliandi
 * @date 2023/11/4
 */
@Service
public class ConfService {
    @Autowired
    private Configuration configuration;

    public void processJava(String excelPath, String outputPath, String packageName) throws Exception {
        File outputFile = new File(outputPath);
        if (!outputFile.exists()) {
            outputFile.mkdir();
        }
        Template template = configuration.getTemplate("conf.ftl");
        File[] files = Objects.requireNonNull(new File(excelPath).listFiles());
        Arrays.stream(files).parallel().forEach(file -> {
            if (file.isDirectory()) {
                return;
            }
            if (!file.getName().contains("xlsx")) {
                return;
            }
            try (Workbook workbook = WorkbookFactory.create(new FileInputStream(file))) {
                var iterator = workbook.sheetIterator();
                while (iterator.hasNext()) {
                    Sheet sheet = iterator.next();
                    String sheetName = sheet.getSheetName();
                    if (!sheetName.contains("|")) {
                        continue;
                    }
                    sheetName = "Conf" + sheetName.split("\\|")[1];
                    Row server = sheet.getRow(0);
                    Row type = sheet.getRow(1);
                    Row name = sheet.getRow(2);
                    Map<String, Object> dataMap = new HashMap<>();
                    List<FieldTempate> fieldList = new ArrayList<>();
                    List<String[]> keyList = new ArrayList<>();
                    for (int i = 0; i < server.getPhysicalNumberOfCells(); i++) {
                        Cell serverCell = server.getCell(i);
                        if (serverCell == null) {
                            continue;
                        }
                        String serverStr = serverCell.getStringCellValue();
                        if (!serverStr.contains("s")) {
                            continue;
                        }
                        String typeStr = type.getCell(i).getStringCellValue();
                        String nameStr = name.getCell(i).getStringCellValue();
                        if (serverStr.contains("i")) {
                            keyList.add(new String[]{nameStr, nameStr.substring(0, 1).toUpperCase() + nameStr.substring(1)});
                        }
                        if (Objects.equals(typeStr, "sn")) {
                            dataMap.put("primary", typeStr);
                        }
                        if (typeStr.contains("string")) {
                            typeStr = typeStr.replaceAll("string", "String");
                        }
                        if (typeStr.contains("timestamp")) {
                            typeStr = typeStr.replaceAll("timestamp", "Date");
                        }
                        FieldTempate fieldTempate = new FieldTempate();
                        fieldTempate.setType(typeStr);
                        fieldTempate.setName(nameStr);
                        fieldList.add(fieldTempate);
                    }
                    if (fieldList.isEmpty()) {
                        continue;
                    }
                    dataMap.put("packageName", packageName);
                    dataMap.put("className", sheetName);
                    dataMap.put("fieldList", fieldList);
                    dataMap.put("keyList", keyList);
                    template.process(dataMap, new OutputStreamWriter(new FileOutputStream(outputPath + sheetName + ".java")));
                }
            } catch (TemplateException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void reloadConfData(String filePath, String packageName, int nameIndex, int offset) {
        File[] files = Objects.requireNonNull(new File(filePath).listFiles());
        Arrays.stream(files).parallel().forEach(file -> {
            if (file.isDirectory()) {
                return;
            }
            if (!file.getName().contains("xlsx")) {
                return;
            }
            try (Workbook workbook = WorkbookFactory.create(new FileInputStream(file))) {
                var iterator = workbook.sheetIterator();
                while (iterator.hasNext()) {
                    Sheet sheet = iterator.next();
                    String sheetName = sheet.getSheetName();
                    if (!sheetName.contains("|")) {
                        continue;
                    }
                    sheetName = "Conf" + sheetName.split("\\|")[1];
                    try {
                        Class<?> clazz = Class.forName(packageName + "." + sheetName);
                        Method method = clazz.getMethod("reload", Sheet.class, int.class, int.class);
                        method.invoke(null, sheet, nameIndex, offset);
                    } catch (ClassNotFoundException ignored) {
                    } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
