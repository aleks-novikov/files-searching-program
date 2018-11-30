package filesSearching;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class FilesSearching {
    public static String[] filesSearching(File folder, String text, String extension) {
        File[] relevantFiles = folder.listFiles((dir, name) -> name.endsWith(extension));
        if (relevantFiles != null) {
            return lookingForTheText(relevantFiles, text);


        }
        return null;
    }

    public static String[] lookingForTheText(File[] files, String text) {
        String line;
        List<String> finalFilesList = new LinkedList<>();
        Map<String, List> filesMap = new HashMap<>();

        for (File file : files) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                while ((line = br.readLine()) != null) {
                    if (line.contains(text)) {
                        getFolder(file, filesMap);

                        finalFilesList.add(file.toString());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (finalFilesList.size() != 0) {
            String[] str = new String[finalFilesList.size()];
            return finalFilesList.toArray(str);
        }
        return null;
    }


    private static void getFolder(File file, Map map) {
        int folderPos = file.getParent().lastIndexOf("\\") + 1;
        String folderName = file.getParent().substring(folderPos);

// если ключа нет:
// 1.заносим имя папки в map
// 2.заполняем лист
// 3.добавляем заполненнный лист в map
//        если ключ есть, заполняем list дальше

        if (!map.containsKey(folderName)) {
            List<String> files = new ArrayList<>();

            map.put(folderName, files);

        }
    }

}