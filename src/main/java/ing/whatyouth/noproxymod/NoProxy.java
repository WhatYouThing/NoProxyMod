package ing.whatyouth.noproxymod;

import net.fabricmc.api.ModInitializer;
import nofrills.config.Feature;
import nofrills.config.SettingBool;
import nofrills.config.SettingEnum;
import nofrills.config.SettingString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoProxy implements ModInitializer {
    public static final String MOD_ID = "noproxymod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Feature instance = new Feature("noProxyMod");

    public static final SettingBool autoConnect = new SettingBool(false, "autoConnect", instance);
    public static final SettingEnum<Positioning> positioning = new SettingEnum<>(Positioning.BottomRight, Positioning.class, "positioning", instance);
    public static final SettingString lastIP = new SettingString("", "lastIP", instance);
    public static final SettingString username = new SettingString("", "username", instance);
    public static final SettingString password = new SettingString("", "password", instance);

    public static int[] getButtonPos(int width, int height) {
        return switch (positioning.value()) {
            case TopLeft -> new int[]{4, 4};
            case TopRight -> new int[]{width - 84, 4};
            case BottomLeft -> new int[]{4, height - 24};
            case BottomRight -> new int[]{width - 84, height - 24};
        };
    }

    @Override
    public void onInitialize() {
        Servers.refresh();
    }

    public enum Positioning {
        TopLeft("Top Left"),
        TopRight("Top Right"),
        BottomLeft("Bottom Left"),
        BottomRight("Bottom Right");

        private final String label;

        Positioning(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return this.label;
        }
    }
}
