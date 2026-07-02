package dev.flexsky.freecamplus;

import dev.flexsky.freecamplus.config.FreecamConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class FreecamController {
	private static final double MIN_SPEED = 0.05;
	private static final double MAX_SPEED = 50.0;
	// blend factor per tick toward the target velocity; higher = snappier
	private static final double SMOOTH_BLEND = 0.35;

	private boolean active;
	private Vec3 position = Vec3.ZERO;
	private Vec3 prevPosition = Vec3.ZERO;
	private Vec3 velocity = Vec3.ZERO;
	private float yaw;
	private float pitch;
	private boolean wasNoGravity;

	public boolean isActive() {
		return this.active;
	}

	public boolean isFullbright() {
		return this.active && FreecamPlusClient.CONFIG.fullbright;
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
			this.prevPosition = this.position;
			this.velocity = Vec3.ZERO;
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
			// persist any speed changes made with the scroll wheel
			FreecamPlusClient.CONFIG.save();
		}
	}

	public void onMouseTurn(double dx, double dy) {
		if (!this.active) {
			return;
		}
		this.yaw = this.yaw + (float) (dx * 0.15);
		this.pitch = Mth.clamp(this.pitch + (float) (dy * 0.15), -90.0F, 90.0F);
	}

	public void onScroll(double deltaY) {
		if (!this.active) {
			return;
		}
		FreecamConfig config = FreecamPlusClient.CONFIG;
		config.speed = Mth.clamp(config.speed * Math.pow(1.15, deltaY), MIN_SPEED, MAX_SPEED);
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

		this.prevPosition = this.position;

		FreecamConfig config = FreecamPlusClient.CONFIG;
		Options options = client.options;

		float forward = 0.0F;
		float strafe = 0.0F;
		float vertical = 0.0F;
		if (client.screen == null) {
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
		}

		Vec3 wishVelocity = Vec3.ZERO;
		if (forward != 0.0F || strafe != 0.0F || vertical != 0.0F) {
			double yawRad = Math.toRadians(this.yaw);
			double pitchRad = Math.toRadians(this.pitch);
			double sinYaw = Math.sin(yawRad);
			double cosYaw = Math.cos(yawRad);
			double cosPitch = Math.cos(pitchRad);
			double sinPitch = Math.sin(pitchRad);

			double dx = (-sinYaw * cosPitch) * forward + cosYaw * strafe;
			double dy = (-sinPitch) * forward + vertical;
			double dz = (cosYaw * cosPitch) * forward + sinYaw * strafe;

			double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
			if (length > 1.0E-4) {
				double speed = 0.6 * config.speed * (options.keySprint.isDown() ? 3.0 : 1.0);
				wishVelocity = new Vec3(dx / length * speed, dy / length * speed, dz / length * speed);
			}
		}

		if (config.smoothMovement) {
			this.velocity = this.velocity.lerp(wishVelocity, SMOOTH_BLEND);
			if (this.velocity.lengthSqr() < 1.0E-6 && wishVelocity.lengthSqr() == 0.0) {
				this.velocity = Vec3.ZERO;
			}
		} else {
			this.velocity = wishVelocity;
		}

		this.position = this.position.add(this.velocity);
	}

	public Vec3 getRenderPosition(float partialTick) {
		return new Vec3(
				Mth.lerp(partialTick, this.prevPosition.x, this.position.x),
				Mth.lerp(partialTick, this.prevPosition.y, this.position.y),
				Mth.lerp(partialTick, this.prevPosition.z, this.position.z));
	}

	public float getYaw() {
		return this.yaw;
	}

	public float getPitch() {
		return this.pitch;
	}
}
