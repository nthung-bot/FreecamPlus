package dev.flexsky.freecamplus;

import com.mojang.blaze3d.platform.InputConstants;
import dev.flexsky.freecamplus.checkpoint.CheckpointManager;
import dev.flexsky.freecamplus.checkpoint.CheckpointRenderer;
import dev.flexsky.freecamplus.config.FreecamConfig;
import dev.flexsky.freecamplus.gui.CreateWaypointScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
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
	public static final CheckpointManager CHECKPOINTS = new CheckpointManager();
	public static FreecamConfig CONFIG;
	public static KeyMapping TOGGLE_KEY;
	public static KeyMapping CHECKPOINT_KEY;

	private boolean wasKeyDown = false;

	@Override
	public void onInitializeClient() {
		CONFIG = FreecamConfig.load();

		TOGGLE_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.freecamplus.toggle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY));
		CHECKPOINT_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.freecamplus.checkpoint", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, CATEGORY));

		ClientTickEvents.END_CLIENT_TICK.register(this::onEndTick);
		WorldRenderEvents.AFTER_ENTITIES.register(new CheckpointRenderer());

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
			CHECKPOINTS.clear();
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

			if (CHECKPOINT_KEY.consumeClick()) {
				// while freecam is active the main camera is the freecam, so the
				// waypoint is placed exactly where the user is looking from
				client.setScreen(new CreateWaypointScreen(
						client.gameRenderer.getMainCamera().position(), client.level.dimension()));
				// drain any extra queued presses so only one dialog opens
				while (CHECKPOINT_KEY.consumeClick()) {
					// no-op
				}
			}
		}

		CONTROLLER.tick(client);
	}
}
