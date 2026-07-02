package dev.flexsky.freecamplus.mixin;

import dev.flexsky.freecamplus.FreecamPlusClient;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
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
}
