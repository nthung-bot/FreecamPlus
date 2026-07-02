package dev.flexsky.freecamplus.checkpoint;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.flexsky.freecamplus.FreecamPlusClient;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.Locale;

public class CheckpointRenderer implements WorldRenderEvents.AfterEntities {
	// labels further than this are pulled toward the camera and scaled up,
	// keeping a constant on-screen size like minimap waypoints
	private static final double CLAMP_DISTANCE = 48.0;

	@Override
	public void afterEntities(WorldRenderContext context) {
		Minecraft client = Minecraft.getInstance();
		CheckpointManager checkpoints = FreecamPlusClient.CHECKPOINTS;
		if (checkpoints.isEmpty() || client.level == null) {
			return;
		}

		Camera camera = client.gameRenderer.getMainCamera();
		Vec3 cameraPos = camera.position();
		Font font = client.font;
		int background = (int) (client.options.getBackgroundOpacity(0.25F) * 255.0F) << 24;
		PoseStack poseStack = context.matrices();

		for (CheckpointManager.Waypoint waypoint : checkpoints.getWaypoints()) {
			if (!client.level.dimension().equals(waypoint.dimension())) {
				continue;
			}

			Vec3 toTarget = waypoint.position().subtract(cameraPos);
			double distance = toTarget.length();
			if (distance < 0.5) {
				continue;
			}

			double renderDistance = Math.min(distance, CLAMP_DISTANCE);
			Vec3 renderPos = cameraPos.add(toTarget.scale(renderDistance / distance));
			float scale = 0.025F * (float) Math.max(1.0, renderDistance / 6.0);

			Component name = Component.literal(waypoint.name());
			Component distanceLabel = Component.literal(formatDistance(distance));
			int color = 0xFF000000 | waypoint.rgb();

			poseStack.pushPose();
			poseStack.translate(renderPos.x - cameraPos.x, renderPos.y - cameraPos.y, renderPos.z - cameraPos.z);
			poseStack.mulPose(camera.rotation());
			poseStack.scale(scale, -scale, scale);
			Matrix4f pose = new Matrix4f(poseStack.last().pose());

			font.drawInBatch(name, -font.width(name) / 2.0F, -10.0F, color, false,
					pose, context.consumers(), Font.DisplayMode.SEE_THROUGH, background, LightTexture.FULL_BRIGHT);
			font.drawInBatch(distanceLabel, -font.width(distanceLabel) / 2.0F, 1.0F, 0xFFFFFFFF, false,
					pose, context.consumers(), Font.DisplayMode.SEE_THROUGH, background, LightTexture.FULL_BRIGHT);

			poseStack.popPose();
		}
	}

	private static String formatDistance(double distance) {
		if (distance >= 1000.0) {
			return String.format(Locale.ROOT, "%.1fkm", distance / 1000.0);
		}
		return String.format(Locale.ROOT, "%dm", (int) distance);
	}
}
