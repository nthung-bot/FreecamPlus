package dev.flexsky.freecamplus.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FreecamConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("freecamplus.json");

	public ActivationMode activationMode = ActivationMode.TOGGLE;
	public double speed = 1.0D;
	public boolean smoothMovement = true;
	public boolean fullbright = true;
	public String checkpointText = "Waypoint";
	public CheckpointColor checkpointColor = CheckpointColor.YELLOW;
	public String customColorHex = "FF8800";

	public enum ActivationMode {
		TOGGLE,
		HOLD
	}

	public enum CheckpointColor {
		WHITE("White", 0xFFFFFF),
		RED("Red", 0xFF5555),
		ORANGE("Orange", 0xFFAA00),
		YELLOW("Yellow", 0xFFFF55),
		GREEN("Green", 0x55FF55),
		AQUA("Aqua", 0x55FFFF),
		BLUE("Blue", 0x5599FF),
		PURPLE("Purple", 0xC060FF),
		PINK("Pink", 0xFF7EB6),
		CUSTOM("Custom", 0xFFFFFF);

		public final String label;
		public final int rgb;

		CheckpointColor(String label, int rgb) {
			this.label = label;
			this.rgb = rgb;
		}
	}

	public static FreecamConfig load() {
		if (Files.exists(PATH)) {
			try (Reader reader = Files.newBufferedReader(PATH, StandardCharsets.UTF_8)) {
				FreecamConfig loaded = GSON.fromJson(reader, FreecamConfig.class);
				if (loaded != null) {
					if (loaded.activationMode == null) {
						loaded.activationMode = ActivationMode.TOGGLE;
					}
					if (loaded.checkpointText == null) {
						loaded.checkpointText = "Waypoint";
					}
					if (loaded.checkpointColor == null) {
						loaded.checkpointColor = CheckpointColor.YELLOW;
					}
					if (loaded.customColorHex == null) {
						loaded.customColorHex = "FF8800";
					}
					return loaded;
				}
			} catch (IOException | JsonSyntaxException ignored) {
				// fall back to defaults below
			}
		}
		FreecamConfig config = new FreecamConfig();
		config.save();
		return config;
	}

	public void save() {
		try {
			Files.createDirectories(PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(PATH, StandardCharsets.UTF_8)) {
				GSON.toJson(this, writer);
			}
		} catch (IOException e) {
			throw new RuntimeException("FreecamPlus: failed to save config", e);
		}
	}
}
