package Types;

public class Terminal {

    private String ip;
    private String name;
    private String typ;
    private String lokalita;
    private String oblast;

    public Terminal(String ip, String name, String typ, String lokalita, String oblast) {
        this.ip = ip;
        this.name = name;
        this.typ = typ;
        this.lokalita = lokalita;
        this.oblast = oblast;
    }

    public String getIp() {
        return ip;
    }

    public String getName() {
        return name;
    }

    public String getTyp() {
        return typ;
    }

    public String getLokalita() {
        return lokalita;
    }

    public String getOblast() {
        return oblast;
    }

    @Override
    public String toString() {
        return "NetworkDevice{" +
                "ip='" + ip + '\'' +
                ", name='" + name + '\'' +
                ", typ='" + typ + '\'' +
                ", lokalita='" + lokalita + '\'' +
                ", oblast='" + oblast + '\'' +
                '}';
    }
}

