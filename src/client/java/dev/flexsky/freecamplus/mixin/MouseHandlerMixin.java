package dev.flexsky.freecamplus.mixin;

import dev.flexsky.freecamplus.FreecamPlusClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Redirect(
			method = "turnPlayer",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"))
	private void freecamplus$redirectTurn(LocalPlayer player, double dx, double dy) {
		if (FreecamPlusClient.CONTROLLER.isActive()) {
			FreecamPlusClient.CONTROLLER.onMouseTurn(dx, dy);
		} else {
			player.turn(dx, dy);
		}
	}

	@Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
	private void freecamplus$scrollAdjustsSpeed(long windowHandle, double deltaX, double deltaY, CallbackInfo ci) {
		if (FreecamPlusClient.CONTROLLER.isActive()
				&& windowHandle == this.minecraft.getWindow().handle()
				&& this.minecraft.getOverlay() == null
				&& this.minecraft.screen == null
				&& this.minecraft.player != null) {
			FreecamPlusClient.CONTROLLER.onScroll(Math.signum(deltaY));
			ci.cancel();
		}
	}
}
