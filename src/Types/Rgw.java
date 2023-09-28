package Types;

public class Rgw {

    private String ip;
    private String rgw;

    public Rgw(String ip, String rgw) {
        this.ip = ip;
        this.rgw = rgw;
    }

    public String getIp() {
        return ip;
    }

    public String getRgw() {
        return rgw;
    }

    @Override
    public String toString() {
        return "IP: " + ip + ", RGW: " + rgw;
    }
}

