package net.iamaprogrammer.mixin;

import net.iamaprogrammer.ExtendedPickingClient;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow @Nullable public ClientPlayerEntity player;

    @Shadow @Nullable public ClientWorld world;

    @Shadow protected abstract void addBlockEntityNbt(ItemStack stack, BlockEntity blockEntity);

    @Shadow @Nullable public ClientPlayerInteractionManager interactionManager;

    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "doItemPick", at = @At("HEAD"), cancellable = true)
    private void infPick(CallbackInfo ci) {
        if (!ExtendedPickingClient.CONFIG.shouldUseVanillaReach()) {
            ci.cancel();
            if (this.player != null) {
                boolean bl = this.player.getAbilities().creativeMode;

                BlockEntity blockEntity = null;

                HitResult target = getHitResult(
                        ExtendedPickingClient.CONFIG.getPickBlockRange(),
                        1.0f
                );
                HitResult.Type type = target.getType();
                ItemStack itemStack;
                if (type != HitResult.Type.MISS) {
                    if (type == net.minecraft.util.hit.HitResult.Type.BLOCK) {
                        BlockPos blockPos = ((BlockHitResult) target).getBlockPos();
                        BlockState blockState = this.world.getBlockState(blockPos);
                        if (blockState.isAir()) {
                            return;
                        }

                        Block block = blockState.getBlock();
                        itemStack = block.getPickStack(this.world, blockPos, blockState);
                        if (itemStack.isEmpty()) {
                            return;
                        }

                        if (bl && Screen.hasControlDown() && blockState.hasBlockEntity()) {
                            blockEntity = this.world.getBlockEntity(blockPos);
                        }
                    } else {
                        if (type != net.minecraft.util.hit.HitResult.Type.ENTITY || !bl) {
                            return;
                        }

                        Entity entity = ((EntityHitResult) target).getEntity();
                        itemStack = entity.getPickBlockStack();
                        if (itemStack == null) {
                            return;
                        }
                    }

                    if (itemStack.isEmpty()) {
                        String string = "";
                        if (type == net.minecraft.util.hit.HitResult.Type.BLOCK) {
                            string = Registries.BLOCK.getId(this.world.getBlockState(((BlockHitResult) target).getBlockPos()).getBlock()).toString();
                        } else {
                            string = Registries.ENTITY_TYPE.getId(((EntityHitResult) target).getEntity().getType()).toString();
                        }

                        LOGGER.warn("Picking on: [{}] {} gave null item", type, string);
                    } else {
                        PlayerInventory playerInventory = this.player.getInventory();
                        if (blockEntity != null) {
                            this.addBlockEntityNbt(itemStack, blockEntity);
                        }

                        int i = playerInventory.getSlotWithStack(itemStack);
                        if (bl) {
                            playerInventory.addPickBlock(itemStack);
                            this.interactionManager.clickCreativeStack(this.player.getStackInHand(Hand.MAIN_HAND), 36 + playerInventory.selectedSlot);
                        } else if (i != -1) {
                            if (PlayerInventory.isValidHotbarIndex(i)) {
                                playerInventory.selectedSlot = i;
                            } else {
                                this.interactionManager.pickFromInventory(i);
                            }
                        }
                    }
                }
            }
        }
    }

    @Unique
    private HitResult getHitResult(double maxDistance, float tickDelta) {
        BlockHitResult blockHitResult = (BlockHitResult) this.player.raycast(maxDistance, tickDelta, false);

        Vec3d point1 = this.player.getCameraPosVec(tickDelta);
        Vec3d rotation = this.player.getRotationVec(1.0F);
        Vec3d point2 = point1.add(rotation.x * maxDistance, rotation.y * maxDistance, rotation.z * maxDistance);

        Box box = this.player.getBoundingBox().stretch(rotation.multiply(maxDistance)).expand(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(this.player, point1, point2, box, (entityx) -> {
            return !entityx.isSpectator() && entityx.canHit();
        }, maxDistance*maxDistance);

        return entityHitResult != null && entityHitResult.squaredDistanceTo(this.player) < blockHitResult.squaredDistanceTo(this.player) ? entityHitResult : blockHitResult;
    }
}
