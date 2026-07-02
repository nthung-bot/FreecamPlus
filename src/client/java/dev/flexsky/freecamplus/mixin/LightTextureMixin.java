package dev.flexsky.freecamplus.mixin;

import dev.flexsky.freecamplus.FreecamPlusClient;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightTexture.class)
public abstract class LightTextureMixin {
	@Redirect(
			method = "updateLightTexture",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/player/LocalPlayer;hasEffect(Lnet/minecraft/core/Holder;)Z",
					ordinal = 0))
	private boolean freecamplus$forceNightVision(LocalPlayer player, Holder<MobEffect> effect) {
		return FreecamPlusClient.CONTROLLER.isFullbright() || player.hasEffect(effect);
	}

	@Redirect(
			method = "updateLightTexture",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/GameRenderer;getNightVisionScale(Lnet/minecraft/world/entity/LivingEntity;F)F"))
	private float freecamplus$fullNightVisionScale(LivingEntity entity, float partialTick) {
		return FreecamPlusClient.CONTROLLER.isFullbright() ? 1.0F : GameRenderer.getNightVisionScale(entity, partialTick);
	}
}
