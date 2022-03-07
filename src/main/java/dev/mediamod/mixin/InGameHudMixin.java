package dev.mediamod.mixin;

import dev.mediamod.MediaMod;
import gg.essential.universal.UMatrixStack;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(
        method = "render",
        at = @At("TAIL")
    )
    public void mediamod_onRenderTick(MatrixStack matrixStack, float tickDelta, CallbackInfo ci) {
        MediaMod.INSTANCE.getRenderManager().onRenderTick(new UMatrixStack(matrixStack));
    }
}
