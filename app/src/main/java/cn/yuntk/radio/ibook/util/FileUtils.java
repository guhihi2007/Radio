package cn.yuntk.radio.ibook.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.yuntk.radio.R;
import cn.yuntk.radio.XApplication;
import cn.yuntk.radio.ibook.service.AppCache;

/**
 * @author Stride
 * @version V1.0
 * @Title TAFileInfoUtil
 * @Package com.ta.util.filecache
 * @Description TAFileInfoUtil是一个字符串的操作类
 * @date 关于文件操作类20170811
 */
public class FileUtils {

    static final String TGA = FileUtils.class.getSimpleName();

    /**
     * 检查是否存在SDCard
     *
     * @return
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    // 递归
    public static long getFileSize1(File f) throws Exception// 取得文件夹大小
    {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSize(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }


    public static long getlist(File f) {// 递归求取目录文件个数
        long size = 0;
        File flist[] = f.listFiles();
        size = flist.length;
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getlist(flist[i]);
                size--;
            }
        }
        return size;

    }
//	android 获取文件夹、文件的大小 以B、KB、MB、GB 为单位

    public static final int SIZETYPE_B = 1;//获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;//获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;//获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;//获取文件大小单位为GB的double值

    /**
     * 获取文件指定文件的指定单位的大小（所有文件夹内文件大小）
     *
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    public static double getFileOrFilesSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("获取文件大小", "获取失败!");
        }
        return FormetFileSize(blockSize, sizeType);
    }

    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     *
     * @param filePath 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    public static String getAutoFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("获取文件大小", "获取失败!");
        }
        return FormetFileSize(blockSize);
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    public static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    public static double FormetFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    /**
     * 判断SDCard是否正常挂载
     *
     * @return
     */
    public static boolean isSDCardEnable() {
        // android.os.Environment
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡路径（File对象）
     *
     * @return
     */
    public static File getSDCartFile() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * 获取SD卡路径（无添加  -> / -> File.separator）
     *
     * @return
     */
    public static String getSDCartPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /*
    * 在公共目录里创建一个文件夹
     @param pubFolder(系统的公共文件夹名字) :
      Environment.DIRECTORY_DCIM="DCIM"，
      DIRECTORY_DOWNLOADS = "Download"，
      DIRECTORY_PICTURES = "Pictures"，
      DIRECTORY_MUSIC = "Music"
      等等
     @param folderName(自己想要创建的文件夹名字):
     "ImageFolder"，"MyFolder"
     @return 返回为空则表示创建失败
     */
    public static File creatFolder(String pubFolder, String folderName) {
        // 判断sd卡是否存在
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        System.out.println("sdCard存在: " + sdCardExist);
        File imageFolder = null;
        if (sdCardExist) {
            // 设定存放截图的文件夹的路径
            String path = Environment
                    .getExternalStoragePublicDirectory(pubFolder).getAbsolutePath()
                    + File.separator
                    + folderName
                    + File.separator;

//          声明
            imageFolder = new File(path);

            // 如果不存在此文件夹,则创建
            if (!imageFolder.exists()) {
                imageFolder.mkdirs();
            }
        } else {
            Log.i(TGA, "未检测到Sdcard！");
        }
        return imageFolder;
    }

    /*
    * 创建一个文件
    * fileName 文件名字，如果为空就创建一个一时间为文件名字的.png文件
    *  @return 返回为空则表示创建失败
    * */
    public static File creatFile(String pubFolder, String folderName, String fileName) {
        File filePath = creatFolder(pubFolder, folderName);//文件存放路径
        File picFile = null;//文件
        if (StringUtils.isEmpty(fileName)) {
            // 获取当前时间,作为截图文件的文件名
            String nowTime = java.text.MessageFormat.format(
                    "{0,date,yyyyMMdd_HHmmss}",
                    new Object[]{new java.sql.Date(System.currentTimeMillis())});
            // 声明文件对象
            picFile = new File(filePath.getAbsoluteFile()
                    + File.separator + "PIC" + nowTime + ".png");
            // 创建此文件
            try {
                picFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // 声明文件对象
            picFile = new File(filePath.getAbsoluteFile()
                    + File.separator + fileName);
            // 创建此文件
            try {
                picFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return picFile;
    }
    /*
    * 在根目录创建一个文件
    * fileName 文件名字，如果为空就创建一个PIC+时间为文件名字的.png文件
    *  @return 返回为空则表示创建失败
    * */

    public static File creatFileInRoot(String fileName) {
        // 判断sd卡是否存在
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        System.out.println("sdCard存在: " + sdCardExist);
        File picFile = null;//文件
        if (sdCardExist) {
            if (StringUtils.isEmpty(fileName)) {
                // 获取当前时间,作为截图文件的文件名
                String nowTime = java.text.MessageFormat.format(
                        "{0,date,yyyyMMdd_HHmmss}",
                        new Object[]{new java.sql.Date(System.currentTimeMillis())});
                // 声明文件对象
                picFile = new File(Environment.getExternalStorageDirectory()
                        + File.separator + "PIC" + nowTime + ".png");
                // 创建此文件
                try {
                    picFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // 声明文件对象
                picFile = new File(Environment.getExternalStorageDirectory()
                        + File.separator + fileName);
                // 创建此文件
                try {
                    picFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.i(TGA, "未检测到SdCard！");
        }

        return picFile;
    }

    //    截取当前屏幕
//    private File screen2png(View view) {
//        // 判断sd卡是否存在
//        boolean sdCardExist = Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED);
//        System.out.println("sdCard存在: " + sdCardExist);
//
//        if (sdCardExist) {
//            // 设定存放截图的文件夹的路径
//            String path = Environment
//                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
//                    + File.separator
//                    + "ImageFolder"
//                    + File.separator;
//
//            // 声明存放截图的文件夹的File对象
//            File imageFolder = new File(path);
//
//            // 如果不存在此文件夹,则创建
//            if (!imageFolder.exists()) {
//                imageFolder.mkdirs();
//            }
//
//            // 获取当前时间,作为截图文件的文件名
//            String nowTime = java.text.MessageFormat.format(
//                    "{0,date,yyyyMMdd_HHmmss}",
//                    new Object[]{new java.sql.Date(System.currentTimeMillis())});
//            // 声明截图文件对象
//            File picFile = new File(imageFolder.getAbsoluteFile()
//                    + File.separator + "PIC" + nowTime + ".png");
//            // 创建此截图文件
//            try {
//                picFile.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            System.out.println("截图文件: " + picFile.getAbsolutePath());
//            ScreenShot.shootView(picFile, view);
//            return picFile;
//        } else {
//            Log.i(TGA, "未检测到Sdcard！");
//            return null;
//        }
//    }

    public static File getSaveFile(Context context) {
        File file = new File(context.getFilesDir(), "pic.jpg");
        return file;
    }

    /**
     *
     * read file
     *
     * @param file
     * @param charsetName The name of a supported {@link java.nio.charset.Charset
     *                    </code>charset<code>}
     * @return if file not exist, return null, else return content of file
     * @throws RuntimeException if an error occurs while operator BufferedReader
     */
    public static String readFile(File file, String charsetName) {
        StringBuilder fileContent = new StringBuilder("");
        if (file == null || !file.isFile()) {
            return fileContent.toString();
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
        return fileContent.toString();
    }

    public static String readFile(String filePath, String charsetName) {
        return readFile(new File(filePath), charsetName);
    }

    public static String readFile(File file) {
        return readFile(file, "utf-8");
    }

    /**
     * 复制单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }

    /**
     * 计算文件的md5值
     */
    public static String calculateMD5(File updateFile) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e("FileUtils", "Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException e) {
            Log.e("FileUtils", "Exception while getting FileInputStream", e);
            return null;
        }

        //DigestInputStream

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e("FileUtils", "Exception on closing MD5 input stream", e);
            }
        }
    }

    /**
     * 计算文件的md5值
     */
    public static String calculateMD5(File updateFile, int offset, int partSize) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e("FileUtils", "Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException e) {
            Log.e("FileUtils", "Exception while getting FileInputStream", e);
            return null;
        }

        //DigestInputStream
        final int buffSize = 8192;//单块大小
        byte[] buffer = new byte[buffSize];
        int read;
        try {
            if (offset > 0) {
                is.skip(offset);
            }
            int byteCount = Math.min(buffSize, partSize), byteLen = 0;
            while ((read = is.read(buffer, 0, byteCount)) > 0 && byteLen < partSize) {
                digest.update(buffer, 0, read);
                byteLen += read;
                //检测最后一块，避免多读数据
                if (byteLen + buffSize > partSize) {
                    byteCount = partSize - byteLen;
                }
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e("FileUtils", "Exception on closing MD5 input stream", e);
            }
        }
    }

    /**
     * @param context
     * @return
     * @throws Exception
     *             获取当前缓存
     */
    public static String getTotalCacheSize(Context context) throws Exception {
        long cacheSize = getFolderSize(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            cacheSize += getFolderSize(context.getExternalCacheDir());
        }
        return getFormatSize(cacheSize);
    }

    /**
     * @param context
     *            删除缓存
     */
    public static void clearAllCache(Context context) {
        deleteDir(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteDir(context.getExternalCacheDir());
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            int size = 0;
            if (children != null) {
                size = children.length;
                for (int i = 0; i < size; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        if (dir == null) {
            return true;
        } else {
            return dir.delete();
        }
    }

    // 获取文件
    // Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/
    // 目录，一般放一些长时间保存的数据
    // Context.getExternalCacheDir() -->
    // SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            int size2 = 0;
            if (fileList != null) {
                size2 = fileList.length;
                for (int i = 0; i < size2; i++) {
                    // 如果下面还有文件
                    if (fileList[i].isDirectory()) {
                        size = size + getFolderSize(fileList[i]);
                    } else {
                        size = size + fileList[i].length();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 格式化单位
     * 计算缓存的大小
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            // return size + "Byte";
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

    public static String getArtistAndAlbum(String artist, String album) {
        if (TextUtils.isEmpty(artist) && TextUtils.isEmpty(album)) {
            return "";
        } else if (!TextUtils.isEmpty(artist) && TextUtils.isEmpty(album)) {
            return artist;
        } else if (TextUtils.isEmpty(artist) && !TextUtils.isEmpty(album)) {
            return album;
        } else {
            return artist + " - " + album;
        }
    }

    private static final String MP3 = ".mp3";
    private static final String LRC = ".lrc";
    /*音频下载 使用的方法*/
    public static String getFileName(String artist, String title) {
        artist = stringFilter(artist);
        title = stringFilter(title);
        if (TextUtils.isEmpty(artist)) {
            artist = AppCache.get().getContext().getString(R.string.unknown);
        }
        if (TextUtils.isEmpty(title)) {
            title = AppCache.get().getContext().getString(R.string.unknown);
        }
        return artist+" - "+title;
    }

    /**
     * 过滤特殊字符(\/:*?"<>|)
     */
    private static String stringFilter(String str) {
        if (str == null) {
            return null;
        }
        String regEx = "[\\/:*?\"<>|]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public static String getMp3FileName(String artist, String title) {
        return getFileName(artist, title) + MP3;
    }

    private static String mkdirs(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return dir;
    }

    public static String getRelativeMusicDir() {
        String dir ="ytk_wholesale/_mp3/";
        return mkdirs(dir);
    }

    public static String getMusicDir() {
        String dir = getAppDir() +"/_mp3/";
        return mkdirs(dir);
    }

    private static String getAppDir() {
        return XApplication.cachePath;
    }

    public static String getLrcFileName(String artist, String title) {
        return getFileName(artist, title) + LRC;
    }

}