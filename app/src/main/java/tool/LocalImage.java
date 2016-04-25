package tool;

/**
 * Created by zangliguang on 16/4/19.
 */
public class LocalImage {
    String sourceHashCode;
    String filePath;
    long fileSize;
    String uri;
    int avgPixel;

    public LocalImage(String sourceHashCode, String filePath, long fileSize,String uri) {
        this.sourceHashCode = sourceHashCode;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.uri=uri;
    }

    public LocalImage() {

    }

    @Override
    public String toString() {
        return "LocalImage{" +
                ", sourceHashCode='" + sourceHashCode + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                '}';
    }


    public String getSourceHashCode() {
        return sourceHashCode;
    }

    public void setSourceHashCode(String sourceHashCode) {
        this.sourceHashCode = sourceHashCode;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getAvgPixel() {
        return avgPixel;
    }

    public void setAvgPixel(int avgPixel) {
        this.avgPixel = avgPixel;
    }
}
