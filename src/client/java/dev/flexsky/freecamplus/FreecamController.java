package dev.flexsky.freecamplus;

import dev.flexsky.freecamplus.config.FreecamConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class FreecamController {
	private boolean active;
	private Vec3 position = Vec3.ZERO;
	private float yaw;
	private float pitch;
	private boolean wasNoGravity;

	public boolean isActive() {
		return this.active;
	}

	public void toggle(Minecraft client) {
		setActive(client, !this.active);
	}

	public void setActive(Minecraft client, boolean value) {
		if (value == this.active) {
			return;
		}
		LocalPlayer player = client.player;
		if (value) {
			if (player == null) {
				return;
			}
			this.position = client.gameRenderer.getMainCamera().position();
			this.yaw = player.getYRot();
			this.pitch = player.getXRot();
			this.wasNoGravity = player.isNoGravity();
			player.setNoGravity(true);
			player.setDeltaMovement(Vec3.ZERO);
			this.active = true;
		} else {
			this.active = false;
			if (player != null) {
				player.setNoGravity(this.wasNoGravity);
			}
		}
	}

	public void onMouseTurn(double dx, double dy) {
		if (!this.active) {
			return;
		}
		this.yaw = this.yaw + (float) (dx * 0.15);
		this.pitch = Mth.clamp(this.pitch + (float) (dy * 0.15), -90.0F, 90.0F);
	}

	public void tick(Minecraft client) {
		if (!this.active) {
			return;
		}
		LocalPlayer player = client.player;
		if (player == null) {
			setActive(client, false);
			return;
		}
		player.setDeltaMovement(Vec3.ZERO);

		FreecamConfig config = FreecamPlusClient.CONFIG;
		Options options = client.options;

		float forward = 0.0F;
		float strafe = 0.0F;
		float vertical = 0.0F;
		if (options.keyUp.isDown()) {
			forward += 1.0F;
		}
		if (options.keyDown.isDown()) {
			forward -= 1.0F;
		}
		if (options.keyRight.isDown()) {
			strafe += 1.0F;
		}
		if (options.keyLeft.isDown()) {
			strafe -= 1.0F;
		}
		if (options.keyJump.isDown()) {
			vertical += 1.0F;
		}
		if (options.keyShift.isDown()) {
			vertical -= 1.0F;
		}

		if (forward == 0.0F && strafe == 0.0F && vertical == 0.0F) {
			return;
		}

		double yawRad = Math.toRadians(this.yaw);
		double pitchRad = Math.toRadians(this.pitch);
		double sinYaw = Math.sin(yawRad);
		double cosYaw = Math.cos(yawRad);
		double cosPitch = Math.cos(pitchRad);
		double sinPitch = Math.sin(pitchRad);

		double forwardX = -sinYaw * cosPitch;
		double forwardY = -sinPitch;
		double forwardZ = cosYaw * cosPitch;
		double rightX = cosYaw;
		double rightZ = sinYaw;

		double dx = forwardX * forward + rightX * strafe;
		double dy = forwardY * forward + vertical;
		double dz = forwardZ * forward + rightZ * strafe;

		double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
		if (length < 1.0E-4) {
			return;
		}

		double speed = 0.6 * config.speed * (options.keySprint.isDown() ? 3.0 : 1.0);
		dx = dx / length * speed;
		dy = dy / length * speed;
		dz = dz / length * speed;

		this.position = this.position.add(dx, dy, dz);
	}

	public Vec3 getPosition() {
		return this.position;
	}

	public float getYaw() {
		return this.yaw;
	}

	public float getPitch() {
		return this.pitch;
	}
}
