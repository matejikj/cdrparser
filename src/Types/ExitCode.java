package Types;

public class ExitCode {

    // Private fields for the properties
    private String exitCode;
    private String zprava;
    private String uspech;

    public ExitCode(String exitCode, String zprava, String uspech) {
        this.exitCode = exitCode;
        this.zprava = zprava;
        this.uspech = uspech;
    }

    public String getExitCode() {
        return exitCode;
    }

    public String getZprava() {
        return zprava;
    }

    public String getUspech() {
        return uspech;
    }

    @Override
    public String toString() {
        return "Exit Code: " + exitCode + ", Zpráva: " + zprava + ", Úspěch: " + uspech;
    }
}

