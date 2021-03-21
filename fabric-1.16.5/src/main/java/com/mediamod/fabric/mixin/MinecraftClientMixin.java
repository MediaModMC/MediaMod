package com.mediamod.fabric.mixin;

import com.mediamod.fabric.event.ClientTickEvent;
import com.mediamod.fabric.event.RenderTickEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow
    @Final
    private RenderTickCounter renderTickCounter;

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE_STRING",
            target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
            args = "ldc=blit"
        )
    )
    private void onRender(boolean tick, CallbackInfo ci) {
        RenderTickEvent.Companion.getEvent().invoker().onTick(renderTickCounter.tickDelta);
    }

    @Inject(
        method = "tick",
        at = @At(
            value = "INVOKE_STRING",
            target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
            args = "ldc=gameRenderer",
            shift = At.Shift.AFTER
        )
    )
    private void onClientTick(CallbackInfo ci) {
        ClientTickEvent.Companion.getEvent().invoker().onTick();
    }
}
