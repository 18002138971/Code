package com.miui.agingtesting.common;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

/**
 * Created by liubin on 17-6-1.
 */

public class StorageOperator {

    /**
     * 向手机存储空间中写文件
     * @param size 文件大小(KB)
     * @param path 手机写入路径
     */
    public void makeFile(int size, String path) {
        FileOutputStream outStream = null;

        try {
            File file = new File(path);
            if (!file.exists()) {
                outStream = new FileOutputStream(file);
                outStream.write(new byte[1024*size]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != outStream) {
                try {
                    outStream.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 复制整个文件夹内容
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public void copyFolder(String oldPath, String newPath, int delay) {
        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a=new File(oldPath);
            String[] file=a.list();
            File temp=null;
            for (int i = 0; i < file.length; i++) {
                if(oldPath.endsWith(File.separator)){
                    temp=new File(oldPath+file[i]);
                } else {
                    temp=new File(oldPath+File.separator+file[i]);
                }

                if(temp.isFile()){
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ( (len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                    if (delay > 0) {
                        Thread.sleep(delay);
                    }
                }
                if(temp.isDirectory()){//如果是子文件夹
                    copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i], delay);
                }
            }
        } catch (Exception e) {
            System.out.println("复制文件夹内容操作出错");
            e.printStackTrace();
        }
    }

    /**
     * 删除文件
     * @param name
     */
    public long removeFile(String name, int delay) {
        long length = 0;
        try {
            File file = new File(name);
            if (file.exists()) {
                length = file.length();
                file.delete();
            }
            if (delay > 0) {
                Thread.sleep(delay);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return length;
    }

    /**
     * 删除文件夹
     * @param path
     */
    public void removeDirectory(String path) {
        try {
            File file = new File(path);
            if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    removeDirectory(files[i].getPath().toString());
                }
                file.delete();
            } else if (file.isFile()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //内部存储剩余空间大小
    public long getDataFreeSize(){
        //取得SD卡文件路径
        File path = Environment.getDataDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSizeLong();
        //空闲的数据块的数量
        long freeBlocks = sf.getFreeBlocksLong();
        //返回SD卡空闲大小
        return (freeBlocks * blockSize)/1024 /1024; //单位MB
    }

    //内部存储总容量
    public long getDataAllSize(){
        //取得SD卡文件路径
        File path = Environment.getDataDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSizeLong();
        //获取所有数据块数
        long allBlocks = sf.getBlockCountLong();
        //返回SD卡大小
        //return allBlocks * blockSize; //单位Byte
        //return (allBlocks * blockSize)/1024; //单位KB
        return (allBlocks * blockSize)/1024/1024; //单位MB
    }

    /**
     * 保持剩余空间不小于比例percent
     * @param percent
     * @param path 若剩余空间不足,可删除此路径下文件
     */
    public void controlFreeSize(float percent, String path, boolean running) {
        if ((float)getDataFreeSize()/getDataAllSize() >= percent) {
            return;
        }
        File file = new File(path);
        if (file.isDirectory()) {
            File files[] = file.listFiles();

            for (int i = 0; i < files.length/2; i++) {
                if ((float)getDataFreeSize()/getDataAllSize() *100 < percent && running) {
                    int delPos = new Random().nextInt(files.length-1);
                    Log.i("SimulationUser", "controlFreeSize:remove:"+files[delPos].toString());
                    removeDirectory(files[delPos].toString());
                } else {
                    break;
                }
            }
        }
    }

    /**
     * 保持剩余空间(剩余一定大小空间)：绝对值
     * @param free :MB
     * @param path 若剩余空间不足,可删除此路径下文件
     */
    public void controlFreeSizeByAbsolute(float free, String path) {
        if (getDataFreeSize() >= free) {
            return;
        }
        File file = new File(path);
        if (file.isDirectory()) {
            File files[] = file.listFiles();

            for (int i = 0; i < files.length/2; i++) {
                if (getDataFreeSize() <= free) {
                    int delPos = new Random().nextInt(files.length-1);
                    Log.i("SimulationUser", "controlFreeSize:remove:"+files[delPos].toString());
                    removeDirectory(files[delPos].toString());
                } else {
                    break;
                }
            }
        }
    }

    // 将字符串写入到文本文件中

    /**
     * 写txt类型的文件
     * @param strContent
     * @param filePath
     * @param fileName
     * @param isCreate 是否新建
     */
    public void writeTxtFile(String strContent, String filePath, String fileName, boolean isCreate) {
        String strFilePath = filePath+fileName;
        if (isCreate) {
            if (new File(strFilePath).exists())
                removeFile(strFilePath, 0);
        }

        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        RandomAccessFile raf = null;
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        } finally {
            if (null != raf) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String readTxtFile(String fileName) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fileName);
            byte[] temp = new byte[1024];
            StringBuilder sb = new StringBuilder("");
            int len = 0;
            while ((len = fis.read(temp)) > 0) {
                sb.append(new String(temp, 0, len));
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fis) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    // 生成文件
    public File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e+"");
        }
    }
}
