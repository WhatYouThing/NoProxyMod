package ing.whatyouth.noproxymod.mixin;

import ing.whatyouth.noproxymod.NoProxy;
import ing.whatyouth.noproxymod.Servers;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.proxy.Socks5ProxyHandler;
import net.minecraft.network.BandwidthDebugMonitor;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;

@Mixin(Connection.class)
public class ConnectionMixin {

    @Inject(method = "configureSerialization", at = @At("TAIL"))
    private static void onAddHandlers(ChannelPipeline pipeline, PacketFlow inboundDirection, boolean local, BandwidthDebugMonitor monitor, CallbackInfo ci) {
        if (inboundDirection.equals(PacketFlow.CLIENTBOUND) && !local && Servers.connected()) {
            InetSocketAddress address = new InetSocketAddress(Servers.current().ip, 1080);
            pipeline.addFirst(new Socks5ProxyHandler(address, NoProxy.username.value(), NoProxy.password.value()));
        }
    }
}
