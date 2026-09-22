package ing.whatyouth.noproxymod;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Instant;

import static ing.whatyouth.noproxymod.NoProxy.LOGGER;

public class Server {
    public static final Server EMPTY = new Server("", "", "", -1, false);

    public final String country;
    public final String ip;
    public final String location;
    public long ping;
    public boolean reachable;

    public Server(String country, String ip, String location, long ping, boolean reachable) {
        this.country = country;
        this.ip = ip;
        this.location = location;
        this.ping = ping;
        this.reachable = reachable;
    }

    public Server(String country, String ip, String location) {
        this(country, ip, location, -1, true);
        Thread.startVirtualThread(() -> {
            try {
                try (Socket socket = new Socket()) {
                    long start = Instant.now().toEpochMilli();
                    int timeout = 3000;
                    socket.setSoTimeout(timeout);
                    socket.connect(new InetSocketAddress(this.ip, 1080), timeout);
                    long ping = Instant.now().toEpochMilli() - start;
                    this.reachable = ping < timeout;
                    this.ping = ping;
                    MainScreen.update();
                    return;
                }
            } catch (Exception e) {
                LOGGER.error("Failed to ping NoProxy server", e);
            }
            this.reachable = false;
        });
    }

    public boolean empty() {
        return this.ip.isEmpty();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Server server && this.ip.equals(server.ip);
    }

    @Override
    public int hashCode() {
        return this.ip.hashCode();
    }
}
