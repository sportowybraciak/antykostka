package pl.antykostka;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class Antykostka implements ClientModInitializer {

    private static KeyBinding saveKey;
    private static KeyBinding restoreKey;

    // Zapisany stan hotbara (9 slotów)
    private static ItemStack[] savedHotbar = null;

    @Override
    public void onInitializeClient() {
        // Klawisz do ZAPISANIA hotbara (domyślnie: H)
        saveKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.antykostka.save",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "category.antykostka"
        ));

        // Klawisz do PRZYWRÓCENIA hotbara (domyślnie: J)
        restoreKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.antykostka.restore",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_J,
            "category.antykostka"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Obsługa zapisu
            while (saveKey.wasPressed()) {
                handleSave(client);
            }
            // Obsługa przywrócenia
            while (restoreKey.wasPressed()) {
                handleRestore(client);
            }
        });
    }

    private void handleSave(MinecraftClient client) {
        if (client.player == null) return;

        savedHotbar = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            savedHotbar[i] = client.player.getInventory().getStack(i).copy();
        }

        client.player.sendMessage(
            Text.literal("§a[Antykostka] §fHotbar zapisany!"),
            true // true = actionbar (nad hotbarem), false = chat
        );
    }

    private void handleRestore(MinecraftClient client) {
        if (client.player == null) return;

        if (savedHotbar == null) {
            client.player.sendMessage(
                Text.literal("§c[Antykostka] §fNie zapisano żadnego hotbara!"),
                true
            );
            return;
        }

        for (int i = 0; i < 9; i++) {
            client.player.getInventory().setStack(i, savedHotbar[i].copy());
        }

        client.player.sendMessage(
            Text.literal("§b[Antykostka] §fHotbar przywrócony!"),
            true
        );
    }
}
