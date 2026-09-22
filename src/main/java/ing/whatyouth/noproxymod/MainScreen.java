package ing.whatyouth.noproxymod;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import nofrills.hud.clickgui.Settings;
import nofrills.hud.clickgui.components.PlainLabel;
import nofrills.misc.RenderColor;
import nofrills.misc.Rendering;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainScreen extends Settings {
    public static final ButtonComponent.Renderer buttonRendererGreen = (context, button, delta) -> {
        context.fill(button.getX(), button.getY(), button.getX() + button.getWidth(), button.getY() + button.getHeight(), 0xff101010);
        Rendering.drawBorder(context, button.getX(), button.getY(), button.getWidth(), button.getHeight(), RenderColor.GREEN.getArgb());
    };
    public static final MutableComponent connectText = Component.literal("Connect").withColor(RenderColor.WHITE.getHex());
    public static final MutableComponent connectedText = Component.literal("Connected").withColor(RenderColor.GREEN.getHex());
    public final Screen previous;

    public MainScreen(List<FlowLayout> list, Screen previous) {
        super(list);
        this.setTitle(Component.literal("NoProxy"));
        this.previous = previous;
    }

    private static Component formatPing(Server server) {
        if (server.reachable) {
            if (server.ping == -1) {
                return Component.literal("Pinging...").withStyle(ChatFormatting.GRAY);
            }
            MutableComponent text = Component.literal(server.ping + "ms");
            if (server.ping <= 80) {
                return text.withStyle(ChatFormatting.GREEN);
            }
            if (server.ping <= 160) {
                return text.withStyle(ChatFormatting.YELLOW);
            } else {
                return text.withStyle(ChatFormatting.RED);
            }
        } else {
            return Component.literal("Error").withStyle(ChatFormatting.RED);
        }
    }

    private static List<FlowLayout> buildServers() {
        List<FlowLayout> list = new ArrayList<>();
        List<ButtonComponent> buttons = new ArrayList<>();
        list.add(new Settings.Separator("Servers"));
        for (Server server : Servers.get()) {
            FlowLayout layout = UIContainers.horizontalFlow(Sizing.content(), Sizing.content());
            layout.padding(Insets.of(5)).horizontalAlignment(HorizontalAlignment.LEFT);
            layout.child(new PlainLabel(Component.literal(server.location + ", " + server.country.toUpperCase(Locale.ROOT)))
                    .verticalTextAlignment(VerticalAlignment.CENTER)
                    .margins(Insets.of(0, 0, 0, 5))
                    .sizing(Sizing.content(), Sizing.fixed(20))
            );
            layout.child(new PlainLabel(Component.literal(server.ip).withStyle(ChatFormatting.GRAY))
                    .verticalTextAlignment(VerticalAlignment.CENTER)
                    .margins(Insets.of(0, 0, 0, 5))
                    .sizing(Sizing.content(), Sizing.fixed(20))
            );
            layout.child(new PlainLabel(formatPing(server))
                    .verticalTextAlignment(VerticalAlignment.CENTER)
                    .margins(Insets.of(0, 0, 0, 5))
                    .sizing(Sizing.content(), Sizing.fixed(20))
            );
            ButtonComponent button = UIComponents.button(Servers.connected(server) ? connectedText : connectText, btn -> {
                if (Servers.connected(server)) {
                    Servers.disconnect();
                    NoProxy.lastIP.set("");
                    btn.renderer(buttonRendererWhite).setMessage(connectText);
                } else {
                    Servers.connect(server);
                    NoProxy.lastIP.set(server.ip);
                    btn.renderer(buttonRendererGreen).setMessage(connectedText);
                }
                buttons.stream()
                        .filter(b -> b != btn)
                        .forEach(b -> b.renderer(buttonRendererWhite).setMessage(connectText));
            });
            button.renderer(Servers.connected(server) ? buttonRendererGreen : buttonRendererWhite)
                    .sizing(Sizing.fixed(60), Sizing.fixed(20))
                    .positioning(Positioning.relative(100, 0));
            buttons.add(button);
            layout.child(button);
            list.add(layout);
        }
        return list;
    }

    public static MainScreen build(Screen previous) {
        List<FlowLayout> list = buildServers();
        list.add(new Settings.BigButton("Refresh", _ -> Servers.refresh()));
        list.add(new Settings.Separator("Options"));
        list.add(new Settings.Toggle("Auto Connect", NoProxy.autoConnect, "Automatically connects to the last selected server."));
        list.add(new Settings.EnumToggle<>("Button Positioning", NoProxy.positioning, "The positioning of the button in the multiplayer screen."));
        list.add(new Settings.TextInput("Username", NoProxy.username, "Your NoProxy account username. Used for authentication."));
        list.add(new Settings.TextInput("Password", "", "", "Your NoProxy account password. Used for authentication.\nThis input field will appear as empty to not reveal your password.", NoProxy.password::set));
        return new MainScreen(list, previous);
    }

    public static void update() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen instanceof MainScreen mainScreen) {
            minecraft.execute(() -> minecraft.setScreen(MainScreen.build(mainScreen.previous)));
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.previous);
    }
}
