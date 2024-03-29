package com.slackow.endfight.util;

import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.List;

@SuppressWarnings("ALL")
public class FakeArrow extends Entity {
    private final float upward;
    public int shake;
    public int pickup;
    public Entity owner;
    private int blockX = -1;
    private int blockY = -1;
    private int blockZ = -1;
    private int block = 0;
    private int blockData = 0;
    private boolean inGround = false;
    private int life;
    private int field_4022;
    private double damage = 2.0D;
    private int punch;

    private boolean hitCrystal = false;

    public FakeArrow(World world, PlayerEntity livingEntity, float f, float upward) {
        super(world);
        this.upward = upward;
        this.renderDistanceMultiplier = 10.0D;
        this.owner = livingEntity;
        if (livingEntity instanceof PlayerEntity) {
            this.pickup = 1;
        }

        this.setBounds(0.5F, 0.5F);
        this.refreshPositionAndAngles(livingEntity.x, livingEntity.y + (double) livingEntity.getEyeHeight(), livingEntity.z, livingEntity.yaw, livingEntity.pitch);
        this.x -= (double) (MathHelper.cos(this.yaw / 180.0F * 3.1415927F) * 0.16F);
        this.y -= 0.10000000149011612D;
        this.z -= (double) (MathHelper.sin(this.yaw / 180.0F * 3.1415927F) * 0.16F);
        this.updatePosition(this.x, this.y, this.z);
        this.heightOffset = 0.0F;
        this.velocityX = (double) (-MathHelper.sin(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F));
        this.velocityZ = (double) (MathHelper.cos(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F));
        this.velocityY = (double) (-MathHelper.sin(this.pitch / 180.0F * 3.1415927F));
        this.setVelocity(this.velocityX, this.velocityY, this.velocityZ, f * 1.5F, 0.0F);
    }

    protected void initDataTracker() {
        this.dataTracker.track(16, (byte) 0);
    }

    public void setVelocity(double x, double y, double z, float speed, float divergence) {
        float var9 = MathHelper.sqrt(x * x + y * y + z * z);
        x /= (double) var9;
        y /= (double) var9;
        z /= (double) var9;
        x += this.random.nextGaussian() * 0.0075F * (double) divergence;
        y += this.random.nextGaussian() * 0.0075F * (double) divergence;
        z += this.random.nextGaussian() * 0.0075F * (double) divergence;
        x *= (double) speed;
        y *= (double) speed;
        z *= (double) speed;
        this.velocityX = x;
        this.velocityY = y;
        this.velocityZ = z;
        float var10 = MathHelper.sqrt(x * x + z * z);
        this.prevYaw = this.yaw = (float) (Math.atan2(x, z) * 180.0D / (float) Math.PI);
        this.prevPitch = this.pitch = (float) (Math.atan2(y, (double) var10) * 180.0D / (float) Math.PI);
        this.life = 0;
    }

    public void method_2488(double d, double e, double f, float g, float h, int i) {
        this.updatePosition(d, e, f);
        this.setRotation(g, h);
    }

    public void setVelocityClient(double x, double y, double z) {
        this.velocityX = x;
        this.velocityY = y;
        this.velocityZ = z;
        if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
            float var7 = MathHelper.sqrt(x * x + z * z);
            this.prevYaw = this.yaw = (float) (Math.atan2(x, z) * 180.0D / 3.1415927410125732D);
            this.prevPitch = this.pitch = (float) (Math.atan2(y, (double) var7) * 180.0D / 3.1415927410125732D);
            this.prevPitch = this.pitch;
            this.prevYaw = this.yaw;
            this.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
            this.life = 0;
        }
    }

    public void tick() {
        super.tick();
        if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
            float var1 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
            this.prevYaw = this.yaw = (float) (Math.atan2(this.velocityX, this.velocityZ) * 180.0D / 3.1415927410125732D);
            this.prevPitch = this.pitch = (float) (Math.atan2(this.velocityY, (double) var1) * 180.0D / 3.1415927410125732D);
        }

        int var16 = this.world.getBlock(this.blockX, this.blockY, this.blockZ);
        if (var16 > 0) {
            Block.BLOCKS[var16].onRender(this.world, this.blockX, this.blockY, this.blockZ);
            Box var2 = Block.BLOCKS[var16].getBoundingBox(this.world, this.blockX, this.blockY, this.blockZ);
            if (var2 != null && var2.contains(Vec3d.method_603().getOrCreate(this.x, this.y, this.z))) {
                this.inGround = true;
            }
        }

        if (this.shake > 0) {
            --this.shake;
        }

        if (this.inGround) {
            int var19 = this.world.getBlock(this.blockX, this.blockY, this.blockZ);
            int var21 = this.world.getBlockData(this.blockX, this.blockY, this.blockZ);
            if (var19 == this.block && var21 == this.blockData) {
                ++this.life;
                if (this.life == 1200) {
                    this.remove();
                }
            } else {
                this.inGround = false;
                this.velocityX *= (double) (this.random.nextFloat() * 0.2F);
                this.velocityY *= (double) (this.random.nextFloat() * 0.2F);
                this.velocityZ *= (double) (this.random.nextFloat() * 0.2F);
                this.life = 0;
                this.field_4022 = 0;
            }
        } else {
            ++this.field_4022;
            Vec3d var17 = Vec3d.method_603().getOrCreate(this.x, this.y, this.z);
            Vec3d var3 = Vec3d.method_603().getOrCreate(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
            BlockHitResult var4 = this.world.rayTrace(var17, var3, false, true);
            var17 = Vec3d.method_603().getOrCreate(this.x, this.y, this.z);
            var3 = Vec3d.method_603().getOrCreate(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
            if (var4 != null) {
                var3 = Vec3d.method_603().getOrCreate(var4.pos.x, var4.pos.y, var4.pos.z);
            }

            Entity var5 = null;
            List<Entity> var6 = this.world.getEntitiesIn(this, this.boundingBox.stretch(this.velocityX, this.velocityY, this.velocityZ).expand(1.0, 1.0, 1.0));
            double var7 = 0.0D;

            for (Entity var10 : var6) {
                if (var10.collides() && (var10 != this.owner || this.field_4022 >= 5)) {
                    float var11 = 0.3F;
                    Box var12 = var10.boundingBox.expand((double) var11, (double) var11, (double) var11);
                    BlockHitResult var13 = var12.method_585(var17, var3);
                    if (var13 != null) {
                        double var14 = var17.distanceTo(var13.pos);
                        if (var14 < var7 || var7 == 0.0) {
                            var5 = var10;
                            var7 = var14;
                        }
                    }
                }
            }

            if (var5 != null) {
                var4 = new BlockHitResult(var5);
            }

            if (var4 != null) {
                if (var4.entity != null) {
                    float var22 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
                    int var26 = MathHelper.ceil((double) var22 * this.damage);
                    if (this.isCritical()) {
                        var26 += this.random.nextInt(var26 / 2 + 2);
                    }

                    DamageSource var29 = null;

                    if (this.isOnFire()) {
                        var4.entity.setOnFireFor(5);
                    }

                    if (var4.entity instanceof EndCrystalEntity) {
                        this.hitCrystal = true;
                    }

                    this.velocityX *= -0.1F;
                    this.velocityY *= -0.1F;
                    this.velocityZ *= -0.1F;
                    this.yaw += 180.0F;
                    this.prevYaw += 180.0F;
                    this.field_4022 = 0;
                } else {
                    this.blockX = var4.x;
                    this.blockY = var4.y;
                    this.blockZ = var4.z;
                    this.block = this.world.getBlock(this.blockX, this.blockY, this.blockZ);
                    this.blockData = this.world.getBlockData(this.blockX, this.blockY, this.blockZ);
                    this.velocityX = (double) ((float) (var4.pos.x - this.x));
                    this.velocityY = (double) ((float) (var4.pos.y - this.y));
                    this.velocityZ = (double) ((float) (var4.pos.z - this.z));
                    float var23 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
                    this.x -= this.velocityX / (double) var23 * 0.05F;
                    this.y -= this.velocityY / (double) var23 * 0.05F;
                    this.z -= this.velocityZ / (double) var23 * 0.05F;
                    this.world.playSound(this, "random.bowhit", 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
                    this.inGround = true;
                    this.shake = 7;
                    this.setCritical(false);
                }
            }

            if (this.isCritical()) {
                for (int var24 = 0; var24 < 4; ++var24) {
                    this.world
                            .spawnParticle(
                                    "crit",
                                    this.x + this.velocityX * (double) var24 / 4.0,
                                    this.y + this.velocityY * (double) var24 / 4.0,
                                    this.z + this.velocityZ * (double) var24 / 4.0,
                                    -this.velocityX,
                                    -this.velocityY + 0.2,
                                    -this.velocityZ
                            );
                }
            }

            this.x += this.velocityX;
            this.y += this.velocityY;
            this.z += this.velocityZ;
            float var25 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
            this.yaw = (float) (Math.atan2(this.velocityX, this.velocityZ) * 180.0 / (float) Math.PI);
            this.pitch = (float) (Math.atan2(this.velocityY, (double) var25) * 180.0 / (float) Math.PI);

            while (this.pitch - this.prevPitch < -180.0F) {
                this.prevPitch -= 360.0F;
            }

            while (this.pitch - this.prevPitch >= 180.0F) {
                this.prevPitch += 360.0F;
            }

            while (this.yaw - this.prevYaw < -180.0F) {
                this.prevYaw -= 360.0F;
            }

            while (this.yaw - this.prevYaw >= 180.0F) {
                this.prevYaw += 360.0F;
            }

            this.pitch = this.prevPitch + (this.pitch - this.prevPitch) * 0.2F;
            this.yaw = this.prevYaw + (this.yaw - this.prevYaw) * 0.2F;
            float var27 = 0.99F;
            float var30 = 0.05F;
            if (this.isTouchingWater()) {
                for (int var32 = 0; var32 < 4; ++var32) {
                    float var33 = 0.25F;
                    this.world
                            .spawnParticle(
                                    "bubble",
                                    this.x - this.velocityX * (double) var33,
                                    this.y - this.velocityY * (double) var33,
                                    this.z - this.velocityZ * (double) var33,
                                    this.velocityX,
                                    this.velocityY,
                                    this.velocityZ
                            );
                }

                var27 = 0.8F;
            }

            this.velocityX *= (double) var27;
            this.velocityY *= (double) var27;
            this.velocityZ *= (double) var27;
            this.velocityY -= (double) var30;
            this.updatePosition(this.x, this.y, this.z);
            this.checkBlockCollision();
        }
    }

    public boolean hasHitCrystal() {
        return this.hitCrystal;
    }

    public void writeCustomDataToNbt(NbtCompound tag) {
        tag.putShort("xTile", (short) this.blockX);
        tag.putShort("yTile", (short) this.blockY);
        tag.putShort("zTile", (short) this.blockZ);
        tag.putShort("life", (short) this.life);
        tag.putByte("inTile", (byte) this.block);
        tag.putByte("inData", (byte) this.blockData);
        tag.putByte("shake", (byte) this.shake);
        tag.putByte("inGround", (byte) (this.inGround ? 1 : 0));
        tag.putByte("pickup", (byte) this.pickup);
        tag.putDouble("damage", this.damage);
    }

    public void readCustomDataFromNbt(NbtCompound tag) {
        this.blockX = tag.getShort("xTile");
        this.blockY = tag.getShort("yTile");
        this.blockZ = tag.getShort("zTile");
        this.life = tag.getShort("life");
        this.block = tag.getByte("inTile") & 255;
        this.blockData = tag.getByte("inData") & 255;
        this.shake = tag.getByte("shake") & 255;
        this.inGround = tag.getByte("inGround") == 1;
        if (tag.contains("damage")) {
            this.damage = tag.getDouble("damage");
        }

        if (tag.contains("pickup")) {
            this.pickup = tag.getByte("pickup");
        } else if (tag.contains("player")) {
            this.pickup = tag.getBoolean("player") ? 1 : 0;
        }
    }

    public void onPlayerCollision(PlayerEntity player) {
        if (!this.world.isClient && this.inGround && this.shake <= 0) {
            boolean var2 = this.pickup == 1 || this.pickup == 2 && player.abilities.creativeMode;
            if (this.pickup == 1 && !player.inventory.insertStack(new ItemStack(Item.ARROW, 1))) {
                var2 = false;
            }

            if (var2) {
                this.world.playSound(this, "random.pop", 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                player.method_3162(this, 1);
                this.remove();
            }
        }
    }

    protected boolean canClimb() {
        return false;
    }

    public float method_2475() {
        return 0.0F;
    }

    public double getDamage() {
        return this.damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void setPunch(int punch) {
        this.punch = punch;
    }

    public boolean isAttackable() {
        return false;
    }

    public boolean isCritical() {
        byte var1 = this.dataTracker.getByte(16);
        return (var1 & 1) != 0;
    }

    public void setCritical(boolean critical) {
        byte var2 = this.dataTracker.getByte(16);
        if (critical) {
            this.dataTracker.setProperty(16, (byte) (var2 | 1));
        } else {
            this.dataTracker.setProperty(16, (byte) (var2 & -2));
        }
    }
}
