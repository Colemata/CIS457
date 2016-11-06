package package1;

/**
 * Pojo to contain file data for the search results.
 */
public class FileData {

    FileData(String speed, String hostname, String filename, int port) {
        this.speed = speed;
        this.filename = filename;
        this.hostname = hostname;
        this.port = port;
    }

    private String speed;
    private String hostname;
    private String filename;
    private int port;

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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
