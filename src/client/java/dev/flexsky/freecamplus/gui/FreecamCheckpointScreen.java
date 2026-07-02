package dev.flexsky.freecamplus.gui;

import com.mojang.blaze3d.platform.InputConstants;
import dev.flexsky.freecamplus.FreecamPlusClient;
import dev.flexsky.freecamplus.config.FreecamConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class FreecamCheckpointScreen extends Screen {
	private final Screen parent;
	private @Nullable KeyMapping selectedKey;
	private Button keyBindButton;
	private EditBox textField;

	public FreecamCheckpointScreen(Screen parent) {
		super(Component.translatable("freecamplus.checkpoint.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		FreecamConfig config = FreecamPlusClient.CONFIG;
		int centerX = this.width / 2;
		int y = this.height / 2 - 60;

		this.textField = this.addRenderableWidget(new EditBox(
				this.font, centerX - 100, y, 200, 20,
				Component.translatable("freecamplus.checkpoint.text")));
		this.textField.setMaxLength(48);
		this.textField.setValue(config.checkpointText);
		this.textField.setResponder(value -> config.checkpointText = value);

		y += 24;

		this.addRenderableWidget(CycleButton
				.<FreecamConfig.CheckpointColor>builder(FreecamCheckpointScreen::colorLabel, config.checkpointColor)
				.withValues(List.of(FreecamConfig.CheckpointColor.values()))
				.create(
						centerX - 100,
						y,
						200,
						20,
						Component.translatable("freecamplus.checkpoint.color"),
						(button, value) -> {
							config.checkpointColor = value;
							config.save();
						}));

		y += 24;

		this.keyBindButton = this.addRenderableWidget(Button.builder(this.getKeyLabel(), button -> {
					this.selectedKey = FreecamPlusClient.CHECKPOINT_KEY;
					button.setMessage(Component.translatable("freecamplus.config.keybind.prompt"));
				})
				.bounds(centerX - 100, y, 200, 20)
				.build());

		y += 32;

		this.addRenderableWidget(Button.builder(Component.translatable("freecamplus.config.done"), button -> this.onClose())
				.bounds(centerX - 100, y, 200, 20)
				.build());
	}

	private static Component colorLabel(FreecamConfig.CheckpointColor color) {
		return Component.literal(color.label)
				.withStyle(style -> style.withColor(TextColor.fromRgb(color.rgb)));
	}

	private Component getKeyLabel() {
		return Component.translatable("freecamplus.checkpoint.key")
				.append(": ")
				.append(FreecamPlusClient.CHECKPOINT_KEY.getTranslatedKeyMessage());
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
