package dev.flexsky.freecamplus.gui;

import com.mojang.blaze3d.platform.InputConstants;
import dev.flexsky.freecamplus.FreecamPlusClient;
import dev.flexsky.freecamplus.config.FreecamConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class FreecamConfigScreen extends Screen {
	private final Screen parent;
	private @Nullable KeyMapping selectedKey;
	private Button keyBindButton;

	public FreecamConfigScreen(Screen parent) {
		super(Component.translatable("freecamplus.config.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		FreecamConfig config = FreecamPlusClient.CONFIG;
		int centerX = this.width / 2;
		int y = this.height / 2 - 96;

		this.addRenderableWidget(CycleButton
				.<FreecamConfig.ActivationMode>builder(
						mode -> Component.translatable(
								mode == FreecamConfig.ActivationMode.TOGGLE
										? "freecamplus.config.activationMode.toggle"
										: "freecamplus.config.activationMode.hold"),
						config.activationMode)
				.withValues(List.of(FreecamConfig.ActivationMode.values()))
				.create(
						centerX - 100,
						y,
						200,
						20,
						Component.translatable("freecamplus.config.activationMode"),
						(button, value) -> {
							config.activationMode = value;
							config.save();
						}));

		y += 24;

		this.addRenderableWidget(CycleButton
				.<Double>builder(speed -> Component.literal(String.format("%.2fx", speed)), config.speed)
				.withValues(List.of(0.25, 0.5, 1.0, 1.5, 2.0, 3.0, 5.0, 8.0))
				.create(
						centerX - 100,
						y,
						200,
						20,
						Component.translatable("freecamplus.config.speed"),
						(button, value) -> {
							config.speed = value;
							config.save();
						}));

		y += 24;

		this.addRenderableWidget(CycleButton
				.onOffBuilder(config.smoothMovement)
				.create(
						centerX - 100,
						y,
						200,
						20,
						Component.translatable("freecamplus.config.smooth"),
						(button, value) -> {
							config.smoothMovement = value;
							config.save();
						}));

		y += 24;

		this.addRenderableWidget(CycleButton
				.onOffBuilder(config.fullbright)
				.create(
						centerX - 100,
						y,
						200,
						20,
						Component.translatable("freecamplus.config.fullbright"),
						(button, value) -> {
							config.fullbright = value;
							config.save();
						}));

		y += 24;

		this.addRenderableWidget(Button.builder(Component.translatable("freecamplus.config.checkpoint"),
						button -> this.minecraft.setScreen(new FreecamCheckpointScreen(this)))
				.bounds(centerX - 100, y, 200, 20)
				.build());

		y += 24;

		this.keyBindButton = this.addRenderableWidget(Button.builder(this.getKeyLabel(), button -> {
					this.selectedKey = FreecamPlusClient.TOGGLE_KEY;
					button.setMessage(Component.translatable("freecamplus.config.keybind.prompt"));
				})
				.bounds(centerX - 100, y, 200, 20)
				.build());

		y += 32;

		this.addRenderableWidget(Button.builder(Component.translatable("freecamplus.config.done"), button -> this.onClose())
				.bounds(centerX - 100, y, 200, 20)
				.build());
	}

	private Component getKeyLabel() {
		return Component.translatable("freecamplus.config.keybind")
				.append(": ")
				.append(FreecamPlusClient.TOGGLE_KEY.getTranslatedKeyMessage());
	}

	@Override
	public boolean keyPressed(KeyEvent keyEvent) {
		if (this.selectedKey != null) {
			if (!keyEvent.isEscape()) {
				this.selectedKey.setKey(InputConstants.getKey(keyEvent));
			}
			this.selectedKey = null;
			this.keyBindButton.setMessage(this.getKeyLabel());
			if (this.minecraft != null) {
				this.minecraft.options.save();
			}
			return true;
		}
		return super.keyPressed(keyEvent);
	}

	@Override
	public void onClose() {
		FreecamPlusClient.CONFIG.save();
		if (this.minecraft != null) {
			this.minecraft.setScreen(this.parent);
		}
	}
}
