package dev.flexsky.freecamplus.gui;

import dev.flexsky.freecamplus.FreecamPlusClient;
import dev.flexsky.freecamplus.config.FreecamConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Locale;

public class CreateWaypointScreen extends Screen {
	private final Vec3 position;
	private final ResourceKey<Level> dimension;
	private EditBox nameField;
	private EditBox customColorField;
	private CycleButton<FreecamConfig.CheckpointColor> colorButton;
	private Button clearAllButton;

	public CreateWaypointScreen(Vec3 position, ResourceKey<Level> dimension) {
		super(Component.translatable("freecamplus.waypoint.create.title"));
		this.position = position;
		this.dimension = dimension;
	}

	@Override
	protected void init() {
		FreecamConfig config = FreecamPlusClient.CONFIG;
		int centerX = this.width / 2;
		int y = this.height / 2 - 72;

		this.nameField = this.addRenderableWidget(new EditBox(
				this.font, centerX - 100, y, 200, 20,
				Component.translatable("freecamplus.waypoint.name")));
		this.nameField.setMaxLength(48);
		this.nameField.setValue(config.checkpointText);
		this.nameField.setHint(Component.translatable("freecamplus.waypoint.name"));
		this.setInitialFocus(this.nameField);

		y += 24;

		this.colorButton = this.addRenderableWidget(CycleButton
				.<FreecamConfig.CheckpointColor>builder(CreateWaypointScreen::colorLabel, config.checkpointColor)
				.withValues(List.of(FreecamConfig.CheckpointColor.values()))
				.create(
						centerX - 100,
						y,
						200,
						20,
						Component.translatable("freecamplus.waypoint.color"),
						(button, value) -> this.customColorField.visible = value == FreecamConfig.CheckpointColor.CUSTOM));

		y += 24;

		this.customColorField = this.addRenderableWidget(new EditBox(
				this.font, centerX - 100, y, 200, 20,
				Component.translatable("freecamplus.waypoint.customColor")));
		this.customColorField.setMaxLength(7);
		this.customColorField.setValue(config.customColorHex);
		this.customColorField.setHint(Component.literal("RRGGBB"));
		this.customColorField.visible = config.checkpointColor == FreecamConfig.CheckpointColor.CUSTOM;

		y += 32;

		this.addRenderableWidget(Button.builder(Component.translatable("freecamplus.waypoint.create"), button -> this.create())
				.bounds(centerX - 100, y, 98, 20)
				.build());
		this.addRenderableWidget(Button.builder(Component.translatable("freecamplus.waypoint.cancel"), button -> this.onClose())
				.bounds(centerX + 2, y, 98, 20)
				.build());

		y += 24;

		this.clearAllButton = this.addRenderableWidget(Button.builder(
						Component.translatable("freecamplus.waypoint.clearAll", FreecamPlusClient.CHECKPOINTS.getWaypoints().size()),
						button -> {
							FreecamPlusClient.CHECKPOINTS.clear();
							this.onClose();
						})
				.bounds(centerX - 100, y, 200, 20)
				.build());
		this.clearAllButton.visible = !FreecamPlusClient.CHECKPOINTS.isEmpty();
	}

	private void create() {
		FreecamConfig config = FreecamPlusClient.CONFIG;
		String name = this.nameField.getValue().isBlank() ? "Waypoint" : this.nameField.getValue().trim();
		FreecamConfig.CheckpointColor selected = this.colorButton.getValue();

		int rgb;
		if (selected == FreecamConfig.CheckpointColor.CUSTOM) {
			rgb = parseHex(this.customColorField.getValue());
			config.customColorHex = this.customColorField.getValue().replace("#", "").toUpperCase(Locale.ROOT);
		} else {
			rgb = selected.rgb;
		}

		FreecamPlusClient.CHECKPOINTS.add(name, rgb, this.position, this.dimension);

		// remember the choices as defaults for the next waypoint
		config.checkpointText = name;
		config.checkpointColor = selected;
		config.save();

		this.onClose();
	}

	private static int parseHex(String value) {
		String hex = value.replace("#", "").trim();
		try {
			return (int) Long.parseLong(hex, 16) & 0xFFFFFF;
		} catch (NumberFormatException e) {
			return 0xFFFFFF;
		}
	}

	private static Component colorLabel(FreecamConfig.CheckpointColor color) {
		return Component.literal(color.label)
				.withStyle(style -> style.withColor(TextColor.fromRgb(color.rgb)));
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 96, 0xFFFFFFFF);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void onClose() {
		if (this.minecraft != null) {
			this.minecraft.setScreen(null);
		}
	}
}
