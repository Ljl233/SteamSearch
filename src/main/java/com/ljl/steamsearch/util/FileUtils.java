package com.ljl.steamsearch.util;

import java.io.*;
import java.util.HashSet;

public class FileUtils {
    public static String readFile(String path) {
        StringBuffer sb = new StringBuffer();
        try {
            FileReader fr = new FileReader(path);
            char[] buf = new char[100];
            while (fr.read(buf) != -1) {
                sb.append(buf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static HashSet<String> readFileByLines(String fileName) {
        HashSet<String> set = new HashSet<>();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            //一次读一行，读入null时文件结束
            while ((tempString = reader.readLine()) != null) {
                set.add(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return set;
    }

    public static void writeFile(String content, String path, Boolean append) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(path, append);
            fw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void writeFile(HashSet<String> set, String path, Boolean append) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(path, append);
            for (String s : set) {
                fw.write(s + '\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
