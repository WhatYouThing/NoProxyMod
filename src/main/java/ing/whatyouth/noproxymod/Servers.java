package ing.whatyouth.noproxymod;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.mojang.text2speech.Narrator.LOGGER;

public class Servers {
    public static List<Server> LIST = new ArrayList<>();
    public static Server CURRENT = Server.EMPTY;

    public static List<Server> get() {
        return LIST;
    }

    public static Server current() {
        return CURRENT;
    }

    public static boolean connected() {
        return !CURRENT.empty() && CURRENT.reachable;
    }

    public static boolean connected(Server server) {
        return CURRENT.equals(server) && connected();
    }

    public static void connect(Server server) {
        CURRENT = server;
    }

    public static void disconnect() {
        CURRENT = Server.EMPTY;
    }

    public static void refresh() {
        Thread.startVirtualThread(() -> {
            List<Server> list = new ArrayList<>();
            try {
                InputStream connection = URI.create("https://whatyouth.ing/api/noproxy/servers").toURL().openStream();
                JsonArray json = JsonParser.parseReader(new InputStreamReader(connection)).getAsJsonArray();
                for (JsonElement element : json) {
                    JsonObject server = element.getAsJsonObject();
                    list.add(new Server(
                            server.get("country").getAsString(),
                            server.get("ip").getAsString(),
                            server.get("location").getAsString())
                    );
                }
            } catch (Exception e) {
                LOGGER.error("Failed to fetch NoProxy server list.", e);
            }
            LIST = list;
            if (NoProxy.autoConnect.value()) {
                for (Server server : list) {
                    if (server.ip.equals(NoProxy.lastIP.value())) {
                        Servers.connect(server);
                        break;
                    }
                }
            }
            MainScreen.update();
        });
    }
}
