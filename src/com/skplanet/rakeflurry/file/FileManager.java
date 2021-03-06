package com.skplanet.rakeflurry.file;

import java.io.File;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.cask.container.config.ConfigReader;
import com.skplanet.cask.util.StringUtil;
import com.skplanet.rakeflurry.dashboard.AccessCodeSummary;
import com.skplanet.rakeflurry.dashboard.ApiKeySummary;
import com.skplanet.rakeflurry.dashboard.ApiSummary;
import com.skplanet.rakeflurry.dashboard.DashBoard;
import com.skplanet.rakeflurry.dashboard.RunningStatus;
import com.skplanet.rakeflurry.util.FileSystemHelper;
import com.skplanet.rakeflurry.util.HiberUtil;

public class FileManager {
    private String localDataDir = null;
    private String hdfsDestDir = null;
    private static DateFormat dirDateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private static FileManager instance = new FileManager();
    private static String fileFormat = "%s_%s_%s.log";
    private static String HADOOP_CORE_CONF_FILE = "core-site.xml";
    private static String HADOOP_HDFS_CONF_FILE = "hdfs-site.xml";
    private URI destUri;
    private String strFullDestUri;
    private Configuration hadoopConf;
    private Boolean useHadoop = true;
    
    private Logger logger = LoggerFactory.getLogger(FileManager.class);
    
    public static FileManager getInstance() {
        return instance;
    }
    
    public synchronized void init() throws Exception {
        useHadoop = Boolean.parseBoolean(ConfigReader.getInstance().getServerConfig().getPropValue("useHadoop"));
    	
        readHadoopConf();
        
        String dateDir = dirDateFormat.format(new Date());
        localDataDir = 
                ConfigReader.getInstance().getServerConfig().getPropValue("localDataDir") + "/" + 
                        dateDir;
        
        hdfsDestDir = dateDir;
        
        File dir = new File(localDataDir);
        if(dir.exists()) {
            logger.info("dir already exists: {}", localDataDir);
        } else {
            if(dir.mkdirs()) {
                logger.info("dir created : {}", localDataDir); 
            } else {
                throw new Exception("mkdirs failed. " + localDataDir);
            }
        }
        mkdirHdfs();
        logger.info("FileManager initialized.");
    }
    private Boolean useHadoop() {
    	return useHadoop;
    }
    private void mkdirHdfs() throws Exception {
    	if(!useHadoop()) {
    		return;
    	}
    	
        // acs hdfs file + aks file name 
        String strUri = ConfigReader.getInstance().getServerConfig().getPropValue("hdfsDestUri");
        destUri = new URI(strUri);
        
        if(!FileSystemHelper.exists(destUri, "", hadoopConf)) {
            throw new Exception("destination dir not exists. " + destUri);
        } 
        
        strFullDestUri = strUri + "/" + hdfsDestDir;
        
        if(FileSystemHelper.exists(destUri, hdfsDestDir, hadoopConf)) {
            logger.info("dir already exists : {}, {}", destUri.getPath(), hdfsDestDir);
        } else {
            if(FileSystemHelper.mkdir(destUri, hdfsDestDir, hadoopConf)) {
                logger.info("hdfs dir created : {}, {}", destUri.getPath(), hdfsDestDir);
                chmodAllDir(destUri, hdfsDestDir);
            } else {
                throw new Exception("mkdirs failed. " + destUri.toString() + "/" + hdfsDestDir);
            }
        }
    }
    private void chmodAllDir(URI destUri, String destDir) throws Exception {
    	if(!useHadoop()) {
    		return;
    	}
    	
        String[] destDirArray = destDir.split("/");
        
        String curr;
        StringBuilder sb = new StringBuilder();
        String permission = ConfigReader.getInstance().getServerConfig().getPropValue("hdfsChmod");
        short realPerm = Short.parseShort(permission, 8);
        
        for(int i = 0; i < destDirArray.length; i++) {
            curr = destDirArray[i];
            if(curr.equals("")) {
                continue;
            }
            sb.append(curr);
            sb.append("/");
            logger.info("will chmod : destUri : {}, dir : {}, perm : {}", 
                    new Object[]{destUri, sb, realPerm});
            FileSystemHelper.chmod(destUri, sb.toString(), realPerm, hadoopConf);
            logger.info("chmod : destUri : {}, dir : {}, perm : {}", 
                        new Object[]{destUri, sb, realPerm});
        }
        
    }
   
    private void chmodFile(URI destUri, String destFile) throws Exception {
    	if(!useHadoop()) {
    		return;
    	}
    	
        String permission = ConfigReader.getInstance().getServerConfig().getPropValue("hdfsChmod");
        short realPerm = Short.parseShort(permission, 8);
        FileSystemHelper.chmod(destUri, destFile, realPerm, hadoopConf);
    }
    public boolean copyToHdfs(AccessCodeSummary acs, ApiKeySummary aks, DashBoard dashboard) throws Exception {
        
    	if(!useHadoop()) {
    		return false;
    	}
    	
        URI sourceUri = null;
        String sourceFile = null;
        boolean error = false;
        String destFile = null;
        try {
            String strLocalUri = "file://" + localDataDir;
            sourceUri = new URI(strLocalUri);
            
            sourceFile = getDataFileName(acs, aks, dashboard);
            destFile = hdfsDestDir + "/" + sourceFile; 
            boolean result = FileSystemHelper.copy(sourceUri, sourceFile, destUri, destFile, hadoopConf);
            chmodFile(destUri,  destFile);
            
            if(!result) {
                throw new Exception("copy to hdfs failed.");
            }
            
            logger.info("copy to hdfs complete.{}, {} => {}, {}",
                    new Object[]{sourceUri, sourceFile, destUri, destFile});
        } catch(Exception e) {
            logger.error("copy to hdfs failed. {}, {} => {}, {}, {}",
                    new Object[]{sourceUri, sourceFile, destUri, destFile, StringUtil.exception2Str(e)});
            error = true;
            aks.setRunningStatus(RunningStatus.ERROR);
            acs.setRunningStatus(RunningStatus.ERROR);
            aks.setErrorMsgLimit(StringUtil.exception2Str(e));
            HiberUtil.update(acs, "update error api key.");
        } 
        
        return error;
    }
    
    public void readHadoopConf() {
    	if(!useHadoop()) {
    		return;
    	}
    	
        hadoopConf = new Configuration();
        
        Path confPath = new Path(ConfigReader.getInstance().getServerConfig().getPropValue("hadoopConfDir"),
                                 HADOOP_CORE_CONF_FILE);
        Path confPath2 = new Path(ConfigReader.getInstance().getServerConfig().getPropValue("hadoopConfDir"),
                                HADOOP_HDFS_CONF_FILE);
        hadoopConf.addResource(confPath);
        hadoopConf.addResource(confPath2);
        
    }
    
    //AccessCodeSummary acs, ApiKeySummary aks
    public static String getDataFileName(AccessCodeSummary acs, ApiKeySummary aks, DashBoard dashboard) {
        return String.format(fileFormat, 
                             acs.getMbrNo(), 
                             aks.getApiKey(), 
                             dashboard.getDashboardId());
    }
    public String getLocalDataDir() {
        return localDataDir;
    }

    public void setLocalDataDir(String localDataDir) {
        this.localDataDir = localDataDir;
    }

    public String getHdfsDestDir() {
        return hdfsDestDir;
    }

    public void setHdfsDestDir(String hdfsDestDir) {
        this.hdfsDestDir = hdfsDestDir;
    }

    public URI getDestUri() {
        return destUri;
    }

    public void setDestUri(URI destUri) {
        this.destUri = destUri;
    }

    public String getStrFullDestUri() {
        return strFullDestUri;
    }

    public void setStrFullDestUri(String strFullDestUri) {
        this.strFullDestUri = strFullDestUri;
    }

}
