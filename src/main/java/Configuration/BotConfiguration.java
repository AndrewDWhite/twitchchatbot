package Configuration;

import org.pircbotx.Configuration;

import java.util.ArrayList;
import java.util.List;

public class BotConfiguration {

    public Configuration.Builder getBotConfigurationBuilder(){
        Configuration.ServerEntry server = new Configuration.ServerEntry(twitchIRCServer,twitchIRCServerPort);
        List<Configuration.ServerEntry> servers = new ArrayList<>();
        servers.add(server);

        return new Configuration.Builder()
                .setName(botName)
                .setServers(servers)
                .setServerPassword(OAuthPassword)
                //.addListener(new MainBot(this))
                .addAutoJoinChannel("#"+channelName);
                //.buildConfiguration();
    }

    private String botName;
    private String twitchIRCServer;
    private int twitchIRCServerPort;
    private String OAuthPassword;
    private String channelName;

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public String getTwitchIRCServer() {
        return twitchIRCServer;
    }

    public void setTwitchIRCServer(String twitchIRCServer) {
        this.twitchIRCServer = twitchIRCServer;
    }

    public int getTwitchIRCServerPort() {
        return twitchIRCServerPort;
    }

    public void setTwitchIRCServerPort(int twitchIRCServerPort) {
        this.twitchIRCServerPort = twitchIRCServerPort;
    }

    public String getOAuthPassword() {
        return OAuthPassword;
    }

    public void setOAuthPassword(String OAuthPassword) {
        this.OAuthPassword = OAuthPassword;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
