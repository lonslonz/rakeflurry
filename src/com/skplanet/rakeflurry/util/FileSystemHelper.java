package com.skplanet.rakeflurry.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.cask.util.Pair;
import com.skplanet.cask.util.StringUtil;
import com.skplanet.rakeflurry.file.DirStatus;
 
public class FileSystemHelper {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";    
    
    public static InputStream openFile(URI uri, String file) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(uri, conf);
        
        String relative = getRelative(uri);
        
        Path filePath = new Path(relative + "/" + file);
        
        InputStream is = new BufferedInputStream(fs.open(filePath));
        
        return is;
    }
    
    public static OutputStream createFile(URI uri, String file) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(uri, conf);
        
        String relative = getRelative(uri);
        
        Path filePath = new Path(relative + "/" + file);
        
        OutputStream os = new BufferedOutputStream(fs.create(filePath, true));
        
        return os;
    }
    public static void closeStream(Closeable stream) {
        Logger logger = LoggerFactory.getLogger(FileSystemHelper.class);   
        if(stream != null) {
            try {
                stream.close();
            } catch(IOException e) {
                logger.error("error : stream close() failed.");
            }
        }
    }
    
    public static FileStatus[] listFiles(URI uri, PathFilter filter) throws IOException {
        return listFiles(uri, "", filter);
    }
    public static FileStatus[] listFiles(URI uri, String dir) throws IOException {

        return listFiles(uri,dir,null);
    }
    public static FileStatus[] listFiles(URI uri, String dir, PathFilter filter) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(uri, conf);
        
        String sourceRelative = FileSystemHelper.getRelative(uri);
        Path path = makePathFrom2Dir(sourceRelative,dir);
        
        FileStatus[] files;
        if(filter == null) {
            files = fs.listStatus(path);
        } else {
            files = fs.listStatus(path, filter);
        }
        return files;
    }
    public static boolean mkdir(URI destUri, String destDir) throws Exception {
        return mkdir(destUri, destDir, null);
    }
    public static boolean mkdir(URI destUri, String destDir, Configuration fsConf) throws Exception {
        Logger logger = LoggerFactory.getLogger(FileSystemHelper.class);    
        
        FileSystem fsDest;
        if(fsConf == null) {
            fsDest = FileSystem.get(destUri,new Configuration());
        } else {
            fsDest = FileSystem.get(fsConf);
        }
        
        String destRelative = getRelative(destUri);
        
        Path destPath = new Path(destRelative + "/" + destDir);
        boolean delete = true;
        if(!fsDest.exists(destPath)) {
            logger.info("dest file not exists. so make it : {}", destPath);
            delete = fsDest.mkdirs(destPath);
        }
        
        return delete;
    }
    
    
    public static boolean remove(URI destUri, String destFile, boolean recursive) throws Exception {
        Logger logger = LoggerFactory.getLogger(FileSystemHelper.class);    
        
        Configuration conf = new Configuration();
        FileSystem fsDest = FileSystem.get(destUri, conf);
        
        String destRelative = getRelative(destUri);
        
        Path destPath = new Path(destRelative + "/" + destFile);
        boolean delete = true;
        if(fsDest.exists(destPath)) {
            logger.info("dest file exists. so remove it : {}", destPath);
            delete = fsDest.delete(destPath, recursive);
        }
        return delete;
    }
    public static List<String> remove(URI destUri, String moreThan, String name, boolean remove) throws Exception {
        
        Logger logger = LoggerFactory.getLogger(FileSystemHelper.class);    
        
        Configuration conf = new Configuration();
        FileSystem fsDest = FileSystem.get(destUri, conf);
        
        Path realPath = new Path(getRelative(destUri));
        
        FileStatus[] files = fsDest.listStatus(realPath);
        FileStatus curr = null;
        List<String> removed = new ArrayList<String>();
        
        if(files == null) {
            return removed;
        }
        
        long now = System.currentTimeMillis();
        long days = 86400 * 1000;
        
        for(int i = 0; i < files.length; ++i) {
            curr = files[i];
//            logger.info("now : {}, curr.getModificationTime() : {}, moreThan : {}, now - curr.getModificationTime() : {}, moreThan * days:{}",
//                    new Object[]{now, curr.getModificationTime(), moreThan,  now - curr.getModificationTime(),
//                    Integer.parseInt(moreThan) * days});
            if((moreThan == null) || 
               (now - curr.getModificationTime() > (Long.parseLong(moreThan) * days))) {
                if(curr.getPath().getName().matches(name)) {
                    boolean delete = false;
                    SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String date = form.format(new Date(curr.getModificationTime()));
                    String msg;
                    removed.add(curr.getPath().toString());
                    if(remove) {
                        delete = fsDest.delete(curr.getPath(), true);
                        msg = "removed";
                    } else {
                        msg = "will be removed ";
                    }
                    logger.info("{} {}: mtime : {}, delete : {}, moreThan : {}", new Object[]{curr.getPath(), msg, date, delete, moreThan});
                }
            }
        }        
        return removed;
    }
    public static boolean copy(URI sourceUri, String sourceDir, URI destUri, String destDir) throws Exception {
        return copy(sourceUri, sourceDir, destUri, destDir, false, true);
    }
    public static boolean copy(URI sourceUri, String sourceDir, URI destUri, String destDir, Configuration fsConf) throws Exception {
        return copy(sourceUri, sourceDir, destUri, destDir, false, true, fsConf);
    }
    public static boolean copy(URI sourceUri, String sourceDir, URI destUri, String destDir, 
            boolean deleteSource, boolean overwrite) throws Exception {
        
        return copy(sourceUri, sourceDir, destUri, destDir, deleteSource, overwrite, null);
    }
    // when sourceDir is file, action is the same as normal file system.
    // when sourceDir is dir, action is in hadoop context.
    public static boolean copy(
            URI sourceUri, String sourceDir, URI destUri, String destDir, 
            boolean deleteSource, boolean overwrite, Configuration fsConf) throws Exception {
        
        FileSystem fsDest;
        Configuration conf;
        
        if(fsConf == null) {
            conf = new Configuration();
            fsDest = FileSystem.get(destUri, conf);
        } else {
            conf = fsConf;
            fsDest = FileSystem.get(fsConf);
        }
        FileSystem fsSource = FileSystem.get(sourceUri, conf);
        
        String sourceRelative = getRelative(sourceUri);
        String destRelative = getRelative(destUri);
        
        Path sourcePath = new Path(sourceRelative + "/" + sourceDir);
        Path destPath = new Path(destRelative + "/" + destDir);
        
        return FileUtil.copy(fsSource, sourcePath, fsDest, destPath, deleteSource, overwrite, conf);
    }
    
    @Deprecated
    public static long copyMerge(
            URI sourceUri, URI destUri, String destFile) throws Exception  {

        Configuration conf = new Configuration();
        FileSystem fsSource = FileSystem.get(sourceUri, conf);
        FileSystem fsDest = FileSystem.get(destUri, conf);
        

        String sourceRelative = getRelative(sourceUri);
        String destRelative = getRelative(destUri);
        
        Path destPath = new Path(destRelative + "/" + destFile);
        writeDiskStatus("source", fsSource, new Path(sourceRelative));
        FileUtil.copyMerge(fsSource, new Path(sourceRelative), fsDest, destPath, false, conf, "");
        writeDiskStatus("dest", fsDest, new Path(destRelative + "/" + destFile));
        
        FileStatus status = fsDest.getFileStatus(destPath);
        return status.getLen();
    }   
    @Deprecated
    public static void copy(
            URI sourceUri, URI destUri, String destDir) throws Exception  {

        Configuration conf = new Configuration();
        FileSystem fsSource = FileSystem.get(sourceUri, conf);
        FileSystem fsDest = FileSystem.get(destUri, conf);
        
        String sourceRelative = getRelative(sourceUri);
        String destRelative = getRelative(destUri);
        
        writeDiskStatus("source", fsSource, new Path(sourceRelative));
        FileUtil.copy(fsSource, new Path(sourceRelative), fsDest, new Path(destRelative + "/" + destDir), false, conf);
        writeDiskStatus("dest", fsDest, new Path(destRelative + "/" + destDir));
    }  
    
    // copy makes new dir with source dir name, move doesnt.
    // copy is in hadoop copy context.
    // move is in normal move context.
    public static boolean move(URI sourceUri, String sourceDir, URI destUri, String destDir) throws Exception {
        
        Configuration conf = new Configuration();
        FileSystem fsSource = FileSystem.get(sourceUri, conf);
        FileSystem fsDest = FileSystem.get(destUri, conf);
        
        String sourceRelative = getRelative(sourceUri);
        String destRelative = getRelative(destUri);
        
        Path sourcePath = new Path(sourceRelative + "/" + sourceDir);
        String destToDir = destRelative + "/" + destDir + "/";
        
        FileStatus[] status = fsSource.listStatus(sourcePath);
        
        boolean result = true;
        for(int i = 0; i < status.length; i++){    
            
            Path moveFromPath = status[i].getPath();
            String moveFromLastName = moveFromPath.getName();
            
            Path moveToPath = new Path(destToDir + moveFromLastName);
            
            if(fsDest.exists(moveToPath)) {
                fsDest.delete(moveToPath, true);
            }
            result = copy(sourceUri, sourceDir + "/" + moveFromLastName, 
                          destUri, destDir, true, true);
        }
        
        fsDest.close();
        return result;
    }
    public static boolean touchFile(URI destUri, String touchFile) throws Exception {
        Configuration conf = new Configuration();
        FileSystem fsDest = FileSystem.get(destUri, conf);
        
        String destRelative = getRelative(destUri);
        
        Path destPath = new Path(destRelative + "/" + touchFile);
        return fsDest.createNewFile(destPath);
    }
    public static void makeCompleteFile(URI destUri, String completeFile) throws Exception {
        
        Logger logger = LoggerFactory.getLogger(FileSystemHelper.class);    
        
        Configuration conf = new Configuration();
        FileSystem fsDest = FileSystem.get(destUri, conf);
        
        String destRelative = getRelative(destUri);
        
        Path destPath = new Path(destRelative + "/" + completeFile);
        
        logger.debug("make complete file : {}", destPath);
        FSDataOutputStream dos = fsDest.create(destPath, true);
        dos.writeBytes(StringUtil.date2Str(new Date()));
        dos.close();
    }
    
    public static String getRelative(URI uri) {
        //return uri.getPath().substring(1);
        return uri.getPath();
    }
    
    public static HashMap<String, Pair<FileStatus, FileStatus>> list2DirFilesDeeply(
            URI sourceUri, URI destUri, String destDir) throws Exception  {
        
        Configuration conf = new Configuration();
        FileSystem fsSource = FileSystem.get(sourceUri, conf);
        FileSystem fsDest = FileSystem.get(destUri, conf);

        String sourceRelative = getRelative(sourceUri);
        String destRelative = getRelative(destUri);
        
        HashMap<String, FileStatus> sourceStatusList = new HashMap<String, FileStatus>();
        listFilesDeeply(fsSource, sourceRelative, ".", sourceStatusList);
        
        HashMap<String, FileStatus> destStatusList = new HashMap<String, FileStatus>();
        listFilesDeeply(fsDest, destRelative + "/" + destDir, ".", destStatusList);
        
        HashMap<String, Pair<FileStatus, FileStatus>> merged = 
                merge2DirFiles(sourceStatusList, destStatusList);
        return merged;
    }
    
    // check 2 files list are same
    public DirStatus checkFilesListSame(Map<String, Pair<FileStatus, FileStatus> > fileStatusMap) throws Exception {
        Set<String> keySet = fileStatusMap.keySet();
        Iterator<String> it = keySet.iterator();
        
        DirStatus dirStatus = new DirStatus();
         
        while(it.hasNext()) {
            String key = it.next();
            Pair<FileStatus, FileStatus> currPair = fileStatusMap.get(key);
            
            writeFileStatus(currPair.first, currPair.second);
            checkFileStatusSameLow(currPair.first, currPair.second);
            
            dirStatus.incFileCount();
            dirStatus.addTotalSize(currPair.first.getLen());
        }
        return dirStatus;
    }
    
    private static HashMap<String, Pair<FileStatus, FileStatus>> merge2DirFiles(
            Map<String, FileStatus> firstMap,
            Map<String, FileStatus> secondMap) throws IOException {
        
        Set<String> keySet = firstMap.keySet();
        Iterator<String> it = keySet.iterator();
        HashMap<String, Pair<FileStatus, FileStatus>> mergedMap = new HashMap<String, Pair<FileStatus, FileStatus>>();
        
        while(it.hasNext()) {
            String key = it.next();
            
            Pair<FileStatus, FileStatus> currPair = 
                    new Pair<FileStatus, FileStatus>(firstMap.get(key), secondMap.get(key));
            mergedMap.put(key, currPair);
        }
        return mergedMap;   
    }
    private static void listFilesDeeply(FileSystem fs, String dir, String relativePath, Map<String, FileStatus> statusList) throws IOException {
        
        FileStatus[] files = fs.listStatus(new Path(dir));
        FileStatus curr = null;
        String dirRelativePath = null;
        
        for(int i = 0; i < files.length; ++i) {
            
            curr = files[i];
            if(curr.isDir()) {
                dirRelativePath = relativePath + "/" + curr.getPath().getName(); 
                listFilesDeeply(fs, dir + "/" + curr.getPath().getName(), dirRelativePath, statusList); 
            }
            else {
                String key = relativePath + "/" + curr.getPath().getName();
                statusList.put(key, curr);
            }
        }
    }
    
    
    
    public static void writeDiskStatus(String context, FileSystem fs, Path path) throws IOException {
        Logger logger = LoggerFactory.getLogger(FileSystemHelper.class);    
        
        ContentSummary sum = fs.getContentSummary(path);
        logger.info("{} disk status({}) : dir count : {}, file count : {}, quota : {}, space quota : {}, size : {}, space used : {} ", 
                new Object[] {context, path, sum.getDirectoryCount(), sum.getFileCount(), sum.getQuota(), sum.getSpaceQuota(), 
                sum.getLength(), sum.getSpaceConsumed()});
    }
    
    public static void writeFileStatus(FileStatus first, FileStatus second) {
        
        Logger logger = LoggerFactory.getLogger(FileSystemHelper.class);
        
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        String firstMTime = formatter.format(new Date(first.getModificationTime()));
        String secondMTime = formatter.format(new Date(second.getModificationTime()));
        
        logger.info("{} {} {} {} {} {} {} => {} {} {} {} {} {} {}", 
                new Object[]{ first.getPermission(), first.getReplication(), first.getOwner(), first.getGroup(), 
                              first.getLen(), firstMTime, first.getPath(),
                              second.getPermission(), second.getReplication(), second.getOwner(), second.getGroup(), 
                              second.getLen(), secondMTime, second.getPath()});
    }
    
    private static void checkFileStatusSameLow(FileStatus first, FileStatus second) throws Exception {
        if(!first.getPath().getName().equals(second.getPath().getName())) {
            throw new Exception("copy - file name invalid, source : " + first.getPath().getName() + 
                    ", dest : " + second.getPath().getName());
        }
        if(first.getLen() != second.getLen()) {
            throw new Exception("copy - file size invalid, source : " + first.getLen() + 
                                ", dest : " + second.getLen());
        }
    }
    // source dir => dest dir/source
    // source file => dest dir/file
    // source/* => dest dir/*
    // if overwrite is true, remove & copy
    // else failure,
    // append mode for hive table append
    public static void copyNormal(
            URI sourceUri, 
            String sourceDir, 
            URI destUri, 
            String destDir, 
            boolean overwrite, 
            boolean append) throws Exception {
        
        Path sourcePath = makeFullPath(sourceUri, sourceDir);
        
        if(!exists(destUri, destDir)) {
            throw new Exception("target path doesn't exist. : " + makeFullPath(destUri, destDir));
        }
        
        if(sourcePath.getName().equals("*")) {
            
            String removeStar = sourceUri.toString() + "/" + sourceDir;
            removeStar = trimLastChar(removeStar, '*');
            removeStar = trimLastChar(removeStar, '/');
            copyNormalAll(new URI(removeStar), "", destUri, destDir, overwrite, append);
        }
        else {
            copyNormalLow(sourceUri, sourceDir, destUri, destDir, overwrite, append);
        }
    }
  
    public static String trimLastChar(String str, char target) {
        
        int last = str.length() - 1;
        for(;last >= 0;) {
            if(str.charAt(last) == target) {
                --last;
            } else {
                break;
            }
        }
        return str.substring(0, last + 1);
        
    }
 // copy dir/* to dest/
    // always recursive
    public static void copyNormalAll(
            URI sourceUri, 
            String sourceDir, 
            URI destUri, 
            String destDir, 
            boolean overwrite, 
            boolean append) throws Exception {
            
        FileStatus[] files = listFiles(sourceUri, sourceDir);
        for(int i = 0; i < files.length; i++) {
            FileStatus curr = files[i];
            
            String sourceFile = sourceDir + "/" + curr.getPath().getName();
            copyNormalLow(sourceUri, sourceFile, destUri, destDir, overwrite, append);
        }
    }
 // if exsist & overwrite => remove & copy
    // else if append => make new file
    //      else error
    public static boolean copyMerge(
            URI sourceUri, String sourceDir, URI destUri, String destDir, boolean overwrite) throws Exception  {

        Configuration conf = new Configuration();
        FileSystem fsSource = FileSystem.get(sourceUri, conf);
        FileSystem fsDest = FileSystem.get(destUri, conf);
        

        String sourceRelative = FileSystemHelper.getRelative(sourceUri);
        String destRelative = FileSystemHelper.getRelative(destUri);
        
        Path sourcePath = makePathFrom2Dir(sourceRelative, sourceDir);
        Path destPath = makePathFrom2Dir(destRelative, destDir);
        
        if(overwrite && exists(destUri, destDir)) {
            remove(destUri, destDir, true);
        }
        
        return FileUtil.copyMerge(fsSource, sourcePath, fsDest, destPath, false, conf, "");
    }   
    private static void copyNormalLow(URI sourceUri, 
            String sourceDir, 
            URI destUri, 
            String destDir, 
            boolean overwrite, 
            boolean append) throws Exception {

        Path sourcePath = makeFullPath(sourceUri, sourceDir);
        String newCopyDest;

        newCopyDest = destDir + "/" + sourcePath.getName();
        
        if(exists(destUri, newCopyDest)) {
            if(overwrite) {
                FileSystemHelper.remove(destUri, newCopyDest, true);
                FileSystemHelper.copy(sourceUri, sourceDir, destUri, newCopyDest, false, true);
            } else {
                if(append && !isDir(sourceUri, sourceDir)) {
                    newCopyDest += "." + makeTempFileName();
                    FileSystemHelper.copy(sourceUri,  sourceDir, destUri, newCopyDest, false, true);
                } else {
                    throw new Exception("already exists : " + makeFullPath(destUri, newCopyDest));
                }
            }
        } else {
            FileSystemHelper.copy(sourceUri,  sourceDir,  destUri, newCopyDest, false, true);
            
        }
    }   
    private static String makeTempFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddHHmmssSSS");
        return sdf.format(new Date());
    }
    public static boolean exists(URI uri, String file) throws Exception {
        
        return exists(uri, file, null);
    }
    public static boolean exists(URI uri, String file, Configuration fsConf) throws Exception {
        
        FileSystem fsDest;
        if(fsConf == null) {
            fsDest = FileSystem.get(uri,new Configuration());
        } else {
            fsDest = FileSystem.get(fsConf);
        }
        
        String destRelative = FileSystemHelper.getRelative(uri);
        
        Path destPath = makePathFrom2Dir(destRelative,file);
        
        return fsDest.exists(destPath);
    }
    public static boolean isDir(URI uri, String dir) throws Exception {
        Configuration conf = new Configuration();

        FileSystem fsDest = FileSystem.get(uri, conf);
        
        String destRelative = FileSystemHelper.getRelative(uri);
        Path destPath = makePathFrom2Dir(destRelative,dir);
        
        FileStatus status = fsDest.getFileStatus(destPath);        
        return status.isDir();
    }
    public static Path makePathFrom2Dir(String dir, String dir2) {
        Path path;
        if(dir2.isEmpty()) {
            path = new Path(dir);
        } else {
            path = new Path(dir + "/" + dir2);
        }
        return path;
    }
    public static Path makeFullPath(URI uri, String subdir) {
        return makePathFrom2Dir(FileSystemHelper.getRelative(uri),subdir);
    }
    public static void chmod(URI uri, String file, short permission) throws Exception {
        
        chmod(uri, file, permission, null);
    }
    public static void chmod(URI uri, String file, short permission, Configuration fsConf) throws Exception {
        FileSystem fsDest;
        if(fsConf == null) {
            fsDest = FileSystem.get(uri,new Configuration());
        } else {
            fsDest = FileSystem.get(fsConf);
        }
        String destRelative = FileSystemHelper.getRelative(uri);
        Path destPath = makePathFrom2Dir(destRelative,file);
        
        FsPermission perm = FsPermission.createImmutable(permission);
        fsDest.setPermission(destPath, perm);
    }
    
}
