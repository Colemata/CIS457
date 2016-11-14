package gvsu457;

import java.util.Date;

/**
 * Created by Administrator on 11/13/2016.
 */
public class QueuedUser {

    private String name;
    private String ip;
    private int port;
    private Date queueTime;

    QueuedUser(String name, String ip, int port, Date queueTime){
        this.setName(name);
        this.setIp(ip);
        this.setPort(port);
        this.setQueueTime(queueTime);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Date getQueueTime() {
        return queueTime;
    }

    public void setQueueTime(Date queueTime) {
        this.queueTime = queueTime;
    }
}
