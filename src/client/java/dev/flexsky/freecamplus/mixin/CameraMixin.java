package dev.flexsky.freecamplus.mixin;

import dev.flexsky.freecamplus.FreecamController;
import dev.flexsky.freecamplus.FreecamPlusClient;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
	@Shadow
	protected abstract void setRotation(float yaw, float pitch);

	@Shadow
	protected abstract void setPosition(Vec3 pos);

	@Inject(method = "setup", at = @At("TAIL"))
	private void freecamplus$overrideCamera(
			Level level, Entity entity, boolean detached, boolean thirdPersonInverted, float tickDelta, CallbackInfo ci) {
		FreecamController controller = FreecamPlusClient.CONTROLLER;
		if (controller.isActive()) {
			this.setRotation(controller.getYaw(), controller.getPitch());
			this.setPosition(controller.getPosition());
		}
	}
}
