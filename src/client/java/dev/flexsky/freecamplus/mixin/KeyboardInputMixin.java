package dev.flexsky.freecamplus.mixin;

import dev.flexsky.freecamplus.FreecamPlusClient;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin {
	@Shadow
	public Input keyPresses;

	@Shadow
	protected Vec2 moveVector;

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void freecamplus$freezeWhileActive(CallbackInfo ci) {
		if (FreecamPlusClient.CONTROLLER.isActive()) {
			this.keyPresses = Input.EMPTY;
			this.moveVector = Vec2.ZERO;
			ci.cancel();
		}
	}
}
