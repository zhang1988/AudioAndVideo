package com.example.commonlib.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;

import com.example.commonlib.safe.CloseUtil;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * @author zhangchao
 * @date 2018-03-17
 * @description 文件辅助类
 * 所有接口的传参约定： appath: 表示应用内部的目录结构（比如，有聊某群组的语音存储/data/group/{gid}/voice
 * filename: 表示文件名（包含扩展名）
 * 1、约定一级目录结构：SDCarDir/APP_DIR/ 2、所有接口都支持传入二级目录+文件名，path+file
 */
public class FileUtils {
    public static final File EXTERNAL_STORAGE_DIRECTORY = Environment.getExternalStorageDirectory();
    public static final int SD_MIN_AVAILABLE_SIZE = 2;
    /**
     * 错误号定义
     */
    public static final int ERR_FILE_OK = 0;
    // SD卡相关错误
    public static final int ERR_FILE_NO_SD = 1; // 无法找到存储卡
    public static final int ERR_FILE_SHARED_SD = 2; // 您的存储卡被USB占用，请更改数据线连接方式
    public static final int ERR_FILE_IO_SD = 3; // 存储卡读写失败

    // TODO: 2018/3/17
    //此处需要确认
    private static String APP_DIR = "AudioAndVideo";

    /**
     * 设置应用根目录
     */
    public static void setTmpDir(String dir) {
        APP_DIR = dir;
    }

    /**
     * 检查SD卡是否存在
     *
     * @return true：存在； false：不存在
     */
    public static boolean checkSD() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取文件目录路径
     *
     * @param filename 文件名
     * @return String
     * 文件的路径名
     */
    public static String getFileDireciory(String filename) {
        if (filename == null) {
            return null;
        }

        return EXTERNAL_STORAGE_DIRECTORY + "/" + APP_DIR + "/" + filename;
    }

    /**
     * 检测客户端临时文件夹是否存在
     *
     * @param path 文件路径
     * @return true：存在； false：不存在
     */
    public static boolean checkTempDir(String path) {
        if (checkSD() == false) {
            return false;
        }
        File tf = new File(path);
        if (!tf.exists()) {
            try {
                return tf.mkdirs();
            } catch (Exception ex) {
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * 返回错误号
     *
     * @return int
     * 错误类型
     */
    public static int getSdError() {
        String status = Environment.getExternalStorageState();
        int error = ERR_FILE_OK;

        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return error;
        }

        if (status.equals(Environment.MEDIA_UNMOUNTED) || status.equals(Environment.MEDIA_UNMOUNTABLE)
                || status.equals(Environment.MEDIA_REMOVED)) {
            error = ERR_FILE_NO_SD;
        } else if (status.equals(Environment.MEDIA_SHARED)) {
            error = ERR_FILE_SHARED_SD;
        } else {
            error = ERR_FILE_IO_SD;
        }
        return error;
    }

    /**
     * 获取全路径
     *
     * @param appath 应用内路径，为null表示使用应用根目录
     * @return String
     * 全路径
     */
    public static String getPath(String appath) {
        String path = null;
        if (appath != null) {
            path = EXTERNAL_STORAGE_DIRECTORY + "/" + APP_DIR + "/" + appath + "/";
        } else {
            path = EXTERNAL_STORAGE_DIRECTORY + "/" + APP_DIR + "/";
        }
        return path;
    }

    /**
     * 检测SD卡是否有空间
     *
     * @return 有 true;没有 false
     */
    public static boolean checkSDHasSpace() {
        try {
            StatFs statfs = new StatFs(EXTERNAL_STORAGE_DIRECTORY.getPath());
            long availaBlock = statfs.getAvailableBlocks();
            long blocSize = statfs.getBlockSize();
            long sdFreeSize = availaBlock * blocSize / 1024 / 1024;

            return sdFreeSize > SD_MIN_AVAILABLE_SIZE;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 获取文件全路径
     *
     * @param appath   应用内路径，为null表示使用应用根目录
     * @param filename 文件名
     * @return String
     * 文件全路径
     */
    public static String getFilePath(String appath, String filename) {
        String file = null;
        if (appath != null) {
            file = EXTERNAL_STORAGE_DIRECTORY + "/" + APP_DIR + "/" + appath + "/" + filename;
        } else {
            file = EXTERNAL_STORAGE_DIRECTORY + "/" + APP_DIR + "/" + filename;
        }
        return file;
    }

    /**
     * 获取文件全路径(到应用根目录)
     *
     * @param filename 文件名
     * @return Sting
     * 文件全路径(到应用根目录)
     */
    public static String getFilePath(String filename) {
        return getFilePath(null, filename);
    }

    /**
     * 获得文件所在目录
     *
     * @param fullfile 全路径
     * @return String
     * 文件所在目录
     */
    private static String getDir(String fullfile) {
        int index = fullfile.lastIndexOf("/");
        if (index > 0 && index < fullfile.length()) {
            return fullfile.substring(0, index);
        }
        return null;
    }

    /**
     * 检查并创建文件目录
     *
     * @param appath   文件所在路径
     * @param filename 文件名
     * @return boolean
     * ，目录存在或者创建成功，返回true；否则，返回false
     */
    public static boolean checkAndMkdirs(String appath, String filename) {
        String fullfile = getFilePath(appath, filename);
        String fulldir = getDir(fullfile);
        File fulldirObj = new File(fulldir);
        boolean ret = false;
        if (!fulldirObj.exists()) {
            try {
                ret = fulldirObj.mkdirs();
                if (ret == false) {
                    // BdLog.e("error fulldirObj.mkdirs:" + fulldir);
                    return false;
                }
            } catch (Exception ex) {
                LogUtils.print("FileUtils", ex.getMessage());
                return false;
            }
        }
        return true;
    }


    /**
     * 检查文件夹是否存在
     *
     * @param appath 文件夹路径
     * @return true：存在； false：不存在
     */
    public static boolean checkDir(String appath) {
        String dir = getPath(appath);

        if (checkSD() == false) {
            return false;
        }
        File tf = new File(dir);

        if (tf.exists()) {
            return true;
        }

        boolean ret = tf.mkdirs();
        return ret;
    }

    /**
     * 检查文件夹中的文件是否存在
     *
     * @param appath   文件路径
     * @param filename 文件名
     * @return true：存在； false：不存在
     */
    public static boolean checkFile(String appath, String filename) {
        if (!checkSD()) {
            return false;
        }
        try {
            File tf = getFile(appath, filename);

            if (tf.exists()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            LogUtils.print("FileUtils", ex.getMessage());
            return false;
        }
    }

    public static boolean checkFile(String file) {
        return checkFile(null, file);
    }

    public static File getFile(String file) {
        return getFile(null, file);
    }

    /**
     * 在文件夹中创建新文件，如果存在先删除
     *
     * @param filename 新文件名字
     * @return 成功：File； 失败：空
     */
    public static File createFile(String appath, String filename) {
        if (!checkDir(appath)) {
            return null;
        }
        try {
            // 先解决目录问题
            boolean ret = checkAndMkdirs(appath, filename);
            if (!ret) {
                return null;
            }

            // 再创建文件
            File file = getFile(appath, filename);
            if (file.exists()) {
                if (file.delete() == false) {
                    // BdLog.e("error file.delete");
                    return null;
                }
            }

            if (file.createNewFile() == true) {
                return file;
            } else {
                // BdLog.e("error createNewFile" + appath + filename);
                return null;
            }
        } catch (Exception ex) {
            LogUtils.print("FileUtils", ex.getMessage());
            return null;
        }
    }

    /**
     * 获得文件夹中的文件对象（不区分文件是否存在）
     *
     * @param filename 文件名
     * @return 成功：File； 失败：空
     */
    public static File getFile(String appath, String filename) {
        if (checkDir(appath) == false) {
            return null;
        }

        try {
            String file = getFilePath(appath, filename);
            File fileObj = new File(file);
            return fileObj;
        } catch (SecurityException ex) {
            LogUtils.print("FileUtils", ex.getMessage());
            return null;
        }
    }

    public static File createFile(String file) {
        return createFile(null, file);
    }

    /**
     * 在文件夹中创建新文件，如果文件存在直接返回
     *
     * @param filename 新文件名字
     * @return 成功：File； 失败：空
     */
    public static File createFileIfNotFound(String appath, String filename) {
        if (checkDir(appath) == false) {
            return null;
        }
        try {
            File file = getFile(appath, filename);
            if (file.exists()) {
                return file;
            } else {
                if (file.createNewFile() == true) {
                    return file;
                } else {
                    return null;
                }
            }
        } catch (Exception ex) {
            LogUtils.print("FileUtils", ex.getMessage());
            return null;
        }
    }

    public static File createFileIfNotFound(String file) {
        return createFileIfNotFound(null, file);
    }

    /**
     * 在文件夹中保存图片文件
     *
     * @param appath    路径
     * @param filename  临时文件名
     * @param imageData 图片数据
     * @return 成功：保存文件的全路径 ； 失败：空
     */
    public static boolean saveGifFile(String appath, String filename, byte[] imageData) {
        if (checkDir(appath) == false) {
            return false;
        }
        if (imageData == null) {
            return false;
        }

        FileOutputStream fOut = null;
        try {

            File file = createFile(appath, filename);
            if (file != null) {
                fOut = new FileOutputStream(file, true);
            } else {
                return false;
            }

            fOut.write(imageData);
            fOut.flush();
            fOut.close();
            fOut = null;
            return true;
        } catch (Exception ex) {
            LogUtils.print("FileUtils", ex.getMessage());
            return false;
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (Exception ex) {
                LogUtils.print("FileUtils", ex.getMessage());
            }
        }
    }

    public static boolean saveGifFile(String file, byte[] imageData) {
        return saveGifFile(null, file, imageData);
    }

    /**
     * 在临时文件夹中保存图片文件
     *
     * @param path     路径
     * @param filename 临时文件名
     * @param bm       图片
     * @param quality  图片质量
     * @return 成功：保存文件的全路径 ； 失败：空
     */
    public static String saveFile(String path, String filename, Bitmap bm, int quality) {
        if (bm == null) {
            return null;
        }

        if (checkDir(path) == false) {
            return null;
        }

        // 先处理创建目录
        if (FileUtils.checkAndMkdirs(path, filename) != true) {
            return null;
        }

        File file = getFile(path, filename);

        try {
            if (file.exists()) {
                if (file.delete() == false) {
                    return null;
                }
            }
            if (file.createNewFile() == false) {
                return null;
            }
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, quality, fOut);
            fOut.flush();
            fOut.close();
            return file.getPath();
        } catch (Exception ex) {
            LogUtils.print("FileUtils", ex.getMessage());
            return null;
        }
    }

    /**
     * @param fileName
     * @param content
     * @return
     */
    public static boolean saveFile(final String fileName, final String content) {
        if (StringUtils.isEmpty(fileName) || content == null) {
            return false;
        }
        return saveFile(fileName, content.getBytes());
    }

    public static boolean saveFile(String appath, String filename, byte[] data) {
        return saveFile(appath, filename, data, false);
    }

    /**
     * 保存文件
     *
     * @param appath   路径
     * @param filename 文件名
     * @param data     要保存的数据
     * @param append   是否追加
     * @return boolean
     * true：保存成功； false：保存失败
     */
    public static boolean saveFile(String appath, String filename, byte[] data, boolean append) {
        if (checkDir(appath) == false) {
            return false;
        }

        // 先处理创建目录
        if (FileUtils.checkAndMkdirs(appath, filename) != true) {
            // BdLog.e("checkAndMkdirs fail:" + appath + filename);
            return false;
        }

        File file = getFile(appath, filename);
        FileOutputStream fOut = null;
        try {
            if (append) {
                if (file.exists() == false) {
                    if (file.createNewFile() == false) {
                        return false;
                    }
                }
                fOut = new FileOutputStream(file, true);
            } else {
                if (file.exists()) {
                    if (file.delete() == false) {
                        return false;
                    }
                }

                if (file.createNewFile() == false) {
                    return false;
                }
                fOut = new FileOutputStream(file);
            }

            fOut.write(data, 0, data.length);
            fOut.flush();
            fOut.close();
            fOut = null;
            return true;
        } catch (IOException ex) {
            LogUtils.print("FileUtils", ex.getMessage());
            return false;
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (Exception ex) {
                LogUtils.print("FileUtils", ex.getMessage());
            }
        }
    }

    /**
     * 在当前路径下保存文件
     *
     * @param filename 文件名
     * @param data     要保存的数据
     * @return boolean
     * true：保存成功； false：保存失败
     */
    public static boolean saveFile(String filename, byte[] data) {
        return saveFile(null, filename, data);
    }

    /**
     * 复制文件
     *
     * @param srcAppath   源文件路径
     * @param srcFilename 源文件名
     * @param dstAppath   新文件路径
     * @param dstFilename 新文件名
     * @return boolean
     * true：复制成功； false：复制失败
     */
    public static boolean copyFile(String srcAppath, String srcFilename, String dstAppath, String dstFilename) {
        boolean result = false;
        InputStream fosfrom = null;
        OutputStream fosto = null;
        try {
            File srcFile = getFile(srcAppath, srcFilename);
            File dstFile = getFile(dstAppath, dstFilename);
            if (!srcFile.exists()) {
                return result;
            }
            fosfrom = new FileInputStream(srcFile);
            fosto = new FileOutputStream(dstFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosfrom = null;
            fosto.close();
            fosto = null;
        } catch (Exception e) {
            LogUtils.print("FileUtils", e.getMessage());
        } finally {
            try {
                if (fosfrom != null) {
                    fosfrom.close();
                }
            } catch (Exception e) {
                LogUtils.print("FileUtils", e.getMessage());
            }
            try {
                if (fosto != null) {
                    fosto.close();
                }
            } catch (Exception e) {
                LogUtils.print("FileUtils", e.getMessage());
            }
        }
        return result;
    }

    /**
     * 在当前路径下复制文件
     *
     * @param srcFile 源文件名
     * @param dstFile 新的文件名
     * @return boolean
     * true：复制成功； false：复制失败
     */
    public static boolean copyFile(String srcFile, String dstFile) {
        return copyFile(null, srcFile, null, dstFile);
    }

    /**
     * 获得文件夹中文件的输入流
     *
     * @param filename 文件名
     * @return 成功：输入流； 失败：空
     */
    public static InputStream getInStreamFromFile(String appath, String filename) {
        File f = getFile(appath, filename);
        return getInStreamFromFile(f);
    }

    /**
     * 修改文件名
     *
     * @param srcAppath   源文件路径
     * @param srcFilename 源文件名
     * @param dstAppath   新文件路径
     * @param dstFilename 新文件名
     * @return boolean
     * true：修改成功； false：修改失败
     */
    public static boolean renameFile(String srcAppath, String srcFilename, String dstAppath, String dstFilename) {
        boolean result = false;
        try {
            // 确保目录存在，否则逐级创建
            boolean ret = checkAndMkdirs(dstAppath, dstFilename);
            if (ret == false) {
                // BdLog.e("error checkAndMkdirs");
                return result;
            }

            File srcFile = getFile(srcAppath, srcFilename);
            File dstFile = getFile(dstAppath, dstFilename);
            if (!srcFile.exists()) {
                // BdLog.e("src File not exist:" + srcAppath + srcFilename + " "
                // + dstAppath
                // + dstFilename);
                return result;
            }
            if (dstFile.exists()) {
                // BdLog.e("dst File exist:" + srcAppath + srcFilename + " " +
                // dstAppath
                // + dstFilename);
                return result;
            }
            result = srcFile.renameTo(dstFile);
            return result;
        } catch (Exception e) {
            LogUtils.print("FileUtils", e.getMessage());
        }
        return result;
    }

    /**
     * 在当前路径下修改文件
     *
     * @param srcFile 源文件名
     * @param dstFile 新的文件名
     * @return boolean
     * true：修改成功； false：修改失败
     */
    public static boolean renameFile(String srcFile, String dstFile) {
        return renameFile(null, srcFile, null, dstFile);
    }

    /**
     * 获得文件夹中文件的输入流
     *
     * @param file 文件
     * @return 成功：输入流； 失败：空
     */
    public static InputStream getInStreamFromFile(File file) {
        if (file != null) {
            try {
                return new FileInputStream(file);
            } catch (Exception ex) {
                LogUtils.print("FileUtils", ex.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 获得文件夹中文件的输出流
     *
     * @param filename 文件名
     * @return 成功：输入流； 失败：空
     */
    public static OutputStream getOutStreamFromFile(String appath, String filename) {
        File f = getFile(appath, filename);
        return getOutStreamFromFile(f);
    }

    /**
     * 获得文件夹中文件的输出流
     *
     * @param file 文件
     * @return 成功：输入流； 失败：空
     */
    public static OutputStream getOutStreamFromFile(File file) {
        if (file != null) {
            try {
                return new FileOutputStream(file);
            } catch (Exception ex) {
                LogUtils.print("FileUtils", ex.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 删除文件夹中的文件
     *
     * @param filename 文件名
     * @return 成功：true； 失败：false
     */
    public static boolean delFile(String appath, String filename) {
        if (checkDir(appath) == false) {
            return false;
        }
        File file = getFile(appath, filename);
        try {
            if (file.exists()) {
                return file.delete();
            } else {
                return false;
            }
        } catch (Exception ex) {
            LogUtils.print("FileUtils", ex.getMessage());
            return false;
        }
    }

    /**
     * 在当前文件夹下删除文件
     *
     * @param file 文件
     * @return boolean
     * 成功：true； 失败：false
     */
    public static boolean delFile(String file) {
        return delFile(null, file);
    }

    /**
     * 按照指定路径删除文件
     *
     * @param appath   文件路径
     * @param filename 文件名
     * @return boolean
     * 成功：true； 失败：false
     */
    public static boolean deleteDir(String appath, String filename) {
        File file = getFile(appath, filename);
        return deleteDir(file);
    }


    /**
     * 删除文件夹
     *
     * @param dir 要删除的文件夹
     * @return boolean
     * 删除成功：true；失败：false
     */
    private static boolean deleteDir(File dir) {
        if (null == dir)
            return false;
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (null != children) {
                // 递归删除目录中的子目录下
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 写入Wave文件头
     *
     * @param out
     * @param totalAudioLen
     * @param totalDataLen
     * @param longSampleRate
     * @param channels
     * @param byteRate
     * @throws IOException
     */
    public static void writeWaveFileHeader(DataOutputStream out, long totalAudioLen, long totalDataLen,
                                           long longSampleRate, int channels, long byteRate) throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }

    /**
     * 获取文件夹大小
     *
     * @param path              文件夹路径
     * @param isIgnoreDirectory 是否忽略目录
     * @return long
     * 文件夹大小
     */
    public static long getDirectorySize(String path, boolean isIgnoreDirectory) {
        return getDirectorySize(new File(path), isIgnoreDirectory);
    }

    /**
     * 得到文件夹
     *
     * @param context
     * @param dirName
     * @return
     */
    public static String getDataDir(Context context, String dirName) {
        String dataDir = "";
        if (context != null && dirName != null && !"".equals(dirName)) {
            File filesDir = context.getFilesDir();
            if (filesDir != null) {
                String filesDirPath = context.getFilesDir().getPath();
                dataDir = filesDirPath.replaceAll("files$", "") + dirName + "/";
            }
        }
        return dataDir;
    }

    /**
     * 获得目录
     *
     * @param upperDir
     * @param dir
     * @return
     */
    public static String getDir(String upperDir, String dir) {
        if (upperDir == null || upperDir.length() == 0) {
            if (dir != null) {
                return dir;
            } else {
                return "";
            }
        }
        if (dir == null || dir.length() == 0) {
            if (upperDir != null) {
                return upperDir;
            }
        }
        if (upperDir.startsWith("H_") && (!dir.startsWith("H_"))) {
            return upperDir;
        }
        return dir;
    }

    /**
     * 确保目录存在
     *
     * @param filePath 文件路径
     */
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception ex) {
            LogUtils.print("FileUtils", ex.getMessage());
        }
    }


    /**
     * 获取文件夹的大小，包含子文件夹也可以
     *
     * @param f File 实例
     * @return long
     * 文件夹大小，单位：字节
     * @throws Exception
     */
    public static long getDirSize(File f) throws Exception {
        if (null == f) {
            return 0;
        }
        long size = 0;
        File fileList[] = f.listFiles();
        if (null == fileList) {
            return size;
        }
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                size = size + getDirSize(fileList[i]);
            } else {
                size = size + fileList[i].length();
            }
        }
        return size;
    }

    /**
     * 获得文件大小
     *
     * @param f 文件
     * @return long
     * 文件的大小
     */
    public static long getFileSize(File f) {
        long s = 0;
        FileInputStream fis = null;
        try {
            if (f.exists()) {
                fis = new FileInputStream(f);
                s = fis.available();
            }
        } catch (Exception ex) {
            s = 0;
        } finally {
            CloseUtil.close(fis);
        }
        return s;
    }

    /**
     * 获取文件夹大小
     *
     * @param f                 文件夹
     * @param isIgnoreDirectory 是否忽略目录
     * @return long
     * 文件夹的大小
     */
    public static long getDirectorySize(File f, boolean isIgnoreDirectory) {
        long size = 0;
        File fileArr[] = f.listFiles();
        if (null == fileArr) {
            return size;
        }
        for (int i = 0; i < fileArr.length; i++) {
            if (fileArr[i].isDirectory() && !isIgnoreDirectory) {
                size = size + getDirectorySize(fileArr[i], false);
            } else {
                size = size + fileArr[i].length();
            }
        }
        return size;
    }

    /**
     * 从Assets资源文件读取
     *
     * @param context  上下文实例
     * @param fileName assets中资源文件名称
     * @return 文件内容
     */
    public static String readFileFromAssets(Context context, String fileName) {
        StringBuilder sb = new StringBuilder();
        String line = null;

        if (null == context || StringUtils.isEmpty(fileName)) {
            return null;
        }

        InputStreamReader inputReader = null;

        try {
            inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            while ((line = bufReader.readLine()) != null) {
                sb.append(line);
            }

            return sb.toString();
        } catch (IOException e) {
            LogUtils.print("FileUtils", e.getMessage());
        } finally {
            CloseUtil.close(inputReader);
        }

        return null;
    }

    /**
     * 追加文件：使用FileWriter
     *
     * @param fileName
     * @param content
     * @param isAdd    是否以追加的方式写入文件
     */
    public static void writeContentToFile(String fileName, String content, boolean isAdd) {
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, isAdd);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            LogUtils.print("FileUtils", e.getMessage());
        }
    }
}
