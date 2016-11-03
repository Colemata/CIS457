package package1;

/**
 * Created by Administrator on 11/3/2016.
 */
public class FileData {

    FileData(String speed, String hostname, String filename){
        this.speed = speed;
        this.filename = filename;
        this.hostname = hostname;
    }

    private String speed;
    private String hostname;
    private String filename;

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
