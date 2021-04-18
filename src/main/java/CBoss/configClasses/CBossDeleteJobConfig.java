package CBoss.configClasses;

public class CBossDeleteJobConfig extends CBossJobConfig {

    private int max_delete_level = 0;
    private final String start_path;

    public CBossDeleteJobConfig(String start_path, int max_delete_level) {
        super(null, start_path);
        this.max_delete_level = max_delete_level;
        this.start_path = start_path;
    }

    public String getStartPath (){
        return this.start_path;
    }

    public int getMax_delete_level() {
        return this.max_delete_level;
    }
}

