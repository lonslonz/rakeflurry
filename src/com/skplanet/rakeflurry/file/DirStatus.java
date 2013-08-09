package com.skplanet.rakeflurry.file;

public class DirStatus {
    public long totalSize = 0;
    public int fileCount = 0;
    
    public long getTotalSize() {
        return totalSize;
    }
    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }
    public void addTotalSize(long size) {
        this.totalSize += size;
    }
    
    public int getFileCount() {
        return fileCount;
    }
    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }
    public void incFileCount() {
        ++this.fileCount;
    }
}
