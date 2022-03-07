package dev.mediamod.mixin;

import dev.mediamod.MediaMod;
import gg.essential.universal.UMatrixStack;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameForge.class)
public class InGameHudMixin {
    @Inject(
        method = "renderGameOverlay",
        at = @At(value = "TAIL")
    )
    public void mediamod_onRenderTick(CallbackInfo ci) {
        MediaMod.INSTANCE.getRenderManager().onRenderTick(new UMatrixStack());
    }
}
