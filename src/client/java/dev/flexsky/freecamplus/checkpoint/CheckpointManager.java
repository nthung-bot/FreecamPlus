package dev.flexsky.freecamplus.checkpoint;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CheckpointManager {
	public record Waypoint(String name, int rgb, Vec3 position, ResourceKey<Level> dimension) {
	}

	private final List<Waypoint> waypoints = new ArrayList<>();

	public void add(String name, int rgb, Vec3 position, ResourceKey<Level> dimension) {
		this.waypoints.add(new Waypoint(name, rgb & 0xFFFFFF, position, dimension));
	}

	public List<Waypoint> getWaypoints() {
		return Collections.unmodifiableList(this.waypoints);
	}

	public boolean isEmpty() {
		return this.waypoints.isEmpty();
	}

	public void clear() {
		this.waypoints.clear();
	}
}
