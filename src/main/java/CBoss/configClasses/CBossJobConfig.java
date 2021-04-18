package CBoss.configClasses;

public class CBossJobConfig {

    String source_path;
    String destination_path;

    public CBossJobConfig(String source_path, String destination_path) {
        this.source_path = source_path;
        this.destination_path = destination_path;
    }


    public String getSource_path() {
        return this.source_path;
    }

    public String getDestination_path() {
        return this.destination_path;
    }
}
