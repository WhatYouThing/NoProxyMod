package ing.whatyouth.noproxymod.mixin;

import ing.whatyouth.noproxymod.MainScreen;
import ing.whatyouth.noproxymod.NoProxy;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JoinMultiplayerScreen.class)
public class JoinMultiplayerScreenMixin extends Screen {

    @Unique
    @Final
    Button noProxyButton = Button.builder(
            Component.literal("NoProxy"),
            _ -> this.minecraft.execute(() -> this.minecraft.gui.setScreen(MainScreen.build(this.minecraft.gui.screen())))
    ).width(80).build();

    protected JoinMultiplayerScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/HeaderAndFooterLayout;visitWidgets(Ljava/util/function/Consumer;)V"))
    private void beforeVisitWidgets(CallbackInfo ci) {
        this.addRenderableWidget(this.noProxyButton);
    }

    @Inject(method = "repositionElements", at = @At("TAIL"))
    private void afterRepositionElements(CallbackInfo ci) {
        int[] pos = NoProxy.getButtonPos(this.width, this.height);
        this.noProxyButton.setX(pos[0]);
        this.noProxyButton.setY(pos[1]);
    }
}
