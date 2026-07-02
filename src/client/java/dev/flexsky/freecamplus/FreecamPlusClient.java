package dev.flexsky.freecamplus;

import com.mojang.blaze3d.platform.InputConstants;
import dev.flexsky.freecamplus.config.FreecamConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import org.lwjgl.glfw.GLFW;

public class FreecamPlusClient implements ClientModInitializer {
	public static final String MOD_ID = "freecamplus";
	public static final KeyMapping.Category CATEGORY =
			KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"));

	public static final FreecamController CONTROLLER = new FreecamController();
	public static FreecamConfig CONFIG;
	public static KeyMapping TOGGLE_KEY;

	private boolean wasKeyDown = false;

	@Override
	public void onInitializeClient() {
		CONFIG = FreecamConfig.load();

		TOGGLE_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.freecamplus.toggle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY));

		ClientTickEvents.END_CLIENT_TICK.register(this::onEndTick);

		AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) ->
				CONTROLLER.isActive() ? InteractionResult.FAIL : InteractionResult.PASS);
		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) ->
				CONTROLLER.isActive() ? InteractionResult.FAIL : InteractionResult.PASS);
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) ->
				CONTROLLER.isActive() ? InteractionResult.FAIL : InteractionResult.PASS);
		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) ->
				CONTROLLER.isActive() ? InteractionResult.FAIL : InteractionResult.PASS);
		UseItemCallback.EVENT.register((player, world, hand) ->
				CONTROLLER.isActive() ? InteractionResult.FAIL : InteractionResult.PASS);
	}

	private void onEndTick(Minecraft client) {
		if (client.player == null || client.level == null) {
			if (CONTROLLER.isActive()) {
				CONTROLLER.setActive(client, false);
			}
			return;
		}

		if (client.screen == null) {
			if (CONFIG.activationMode == FreecamConfig.ActivationMode.TOGGLE) {
				while (TOGGLE_KEY.consumeClick()) {
					CONTROLLER.toggle(client);
				}
			} else {
				boolean down = TOGGLE_KEY.isDown();
				if (down != this.wasKeyDown) {
					CONTROLLER.setActive(client, down);
					this.wasKeyDown = down;
				}
			}
		}

		CONTROLLER.tick(client);
	}
}
