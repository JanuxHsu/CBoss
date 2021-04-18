package CBoss.configClasses;


import org.springframework.beans.factory.annotation.Value;

public class NetappConfig {

    private final String host;
    private final int port;
    private final String user;
    private final String password;

    public NetappConfig(String host, int port, String user, String env_password) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = System.getenv(env_password);
    }


    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
