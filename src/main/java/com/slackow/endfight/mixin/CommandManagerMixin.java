package com.slackow.endfight.mixin;

import com.slackow.endfight.*;
import com.slackow.endfight.commands.ResetCommand;
import com.slackow.endfight.config.BigConfig;
import com.slackow.endfight.util.Medium;
import net.minecraft.command.*;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.stream.Collectors;


@Mixin(CommandManager.class)
public abstract class CommandManagerMixin extends CommandRegistry {
    @Unique
    private static void dragonHealth(CommandSource source, String[] args) {
        Optional<EnderDragonEntity> dragon = getDragon(((PlayerEntity) source).world);
        if (dragon.isPresent() && args.length > 0) {
            try {
                dragon.get().method_2668((int) MathHelper.clamp(Float.parseFloat(args[0]), 0, 200));
            } catch (NumberFormatException e) {
                source.method_3331("§c" + "Not a valid health");
            }
        }
        source.method_3331(dragon.map(entityDragon -> "Dragon Health is: " + entityDragon.method_2600()).orElse("§c" + "No Dragon Found"));
    }

    @Unique
    private static Optional<EnderDragonEntity> getDragon(World world) {
        for (Object loadedEntity : world.getLoadedEntities()) {
            if (loadedEntity instanceof EnderDragonEntity) {
                return Optional.of((EnderDragonEntity) loadedEntity);
            }
        }
        return Optional.empty();
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/command/AbstractCommand;setCommandProvider(Lnet/minecraft/command/CommandProvider;)V"))
    public void epicCommands(CallbackInfo ci) {
        // god
        registerCommand(new EndFightCommand() {
            @Override
            public String getCommandName() {
                return "god";
            }

            @Override
            public String getUsageTranslationKey(CommandSource source) {
                return "/god [crystal|dragon]";
            }

            @Override
            public void execute(CommandSource source, String[] args) {
                if (source instanceof PlayerEntity) {
                    if (args.length == 0) {
                        boolean god = BigConfig.getSelectedConfig().dGodPlayer ^= true;
                        source.method_3331((god) ? "God Mode Enabled" :
                                "God Mode Disabled");
                        PlayerEntity player = (PlayerEntity) source;
                        player.method_2668(20);
                        player.extinguish();
                        if (god) {
                            player.method_2654(new StatusEffectInstance(11, 100000, 255));
                        } else {
                            player.method_2643();
                        }
                        player.getHungerManager().add(20, 1);
                        player.getHungerManager().add(1, -8); // -16
                    } else {
                        switch (args[0]) {
                            case "crystal": {
                                boolean god = BigConfig.getSelectedConfig().dGodCrystals ^= true;
                                source.method_3331((god) ? "Crystal God Mode Enabled" :
                                        "Crystal God Mode Disabled");
                                break;
                            }
                            case "dragon": {
                                boolean god = BigConfig.getSelectedConfig().dGodDragon ^= true;
                                if (god) {
                                    getDragon(((PlayerEntity) source).world).ifPresent(dragon ->
                                            dragon.method_2668(dragon.method_2599()));
                                }
                                source.method_3331((god) ? "Dragon God Mode Enabled" :
                                        "Dragon God Mode Disabled");
                                break;
                            }
                            default: {
                                source.method_3331("Unrecognized Subcommand");
                                return;
                            }
                        }
                    }
                    BigConfig.save();
                }
            }

            @Override
            public List<String> method_3276(CommandSource source, String[] args) {
                if (args.length == 1) {
                    if (args[0].isEmpty()) {
                        return Arrays.asList("crystal", "dragon");
                    }
                    if ("crystal".startsWith(args[0])) {
                        return Collections.singletonList("crystal");
                    } else if ("dragon".startsWith(args[0])) {
                        return Collections.singletonList("dragon");
                    }
                }
                return Collections.emptyList();
            }
        });
        // heal
        registerCommand(new EndFightCommand() {
            @Override
            public String getCommandName() {
                return "heal";
            }

            @Override
            public String getUsageTranslationKey(CommandSource source) {
                return "/heal";
            }

            @Override
            public void execute(CommandSource source, String[] args) {
                if (source instanceof PlayerEntity) {
                    PlayerEntity player = (PlayerEntity) source;
                    heal(player);
                    player.method_3331("Healed");
                }
            }
        });
        // killcrystals
        registerCommand(new EndFightCommand() {
            @Override
            public String getCommandName() {
                return "killcrystals";
            }

            @Override
            public String getUsageTranslationKey(CommandSource source) {
                return "/killcrystals";
            }

            @Override
            public void execute(CommandSource source, String[] args) {
                long count = 0;
                for (Object loadedEntity : ((PlayerEntity) source).world.getLoadedEntities()) {
                    if (loadedEntity instanceof EndCrystalEntity) {
                        if (((EndCrystalEntity) loadedEntity).isAlive()) {
                            ((EndCrystalEntity) loadedEntity).remove();
                            count++;
                        }
                    }
                }
                if (count <= 0) {
                    source.method_3331("§c" + "No End Crystals Found");
                } else {
                    source.method_3331("Killed " + count + " End Crystals");
                }
            }
        });
        // killdragon
        registerCommand(new EndFightCommand() {
            @Override
            public String getCommandName() {
                return "killdragon";
            }

            @Override
            public String getUsageTranslationKey(CommandSource source) {
                return "/killdragon";
            }

            @Override
            public void execute(CommandSource source, String[] args) {
                Optional<EnderDragonEntity> dragon = getDragon(((PlayerEntity) source).world);
                if (dragon.isPresent()) {
                    dragon.get().remove();
                    source.method_3331("Killed dragon");
                } else {
                    source.method_3331("§c" + "No dragon found");
                }
            }
        });
        // setinv
        registerCommand(new EndFightCommand() {
            @Override
            public String getCommandName() {
                return "setinv";
            }

            @Override
            public String getUsageTranslationKey(CommandSource source) {
                return "/setinv";
            }

            @Override
            public void execute(CommandSource source, String[] args) throws CommandException {
                if (source instanceof PlayerEntity) {
                    EndFightMod.setInventory((PlayerEntity) source, BigConfig.getSelectedConfig().inventory);
                    source.method_3331("Saved Inventory");
                }
            }
        });
        // getinv
        registerCommand(new EndFightCommand() {
            @Override
            public String getCommandName() {
                return "getinv";
            }

            @Override
            public String getUsageTranslationKey(CommandSource source) {
                return "/getinv";
            }

            @Override
            public void execute(CommandSource source, String[] args) throws CommandException {
                if (source instanceof PlayerEntity) {
                    EndFightMod.giveInventory((PlayerEntity) source, BigConfig.getSelectedConfig().getInv());
                }
            }
        });
        // reset (THE BIG ONE)
        registerCommand(new ResetCommand());
        // dragon health
        registerCommand(new EndFightCommand() {
            @Override
            public String getCommandName() {
                return "dragonhealth";
            }

            @Override
            public String getUsageTranslationKey(CommandSource source) {
                return "/dragonhealth [health]";
            }

            @Override
            public void execute(CommandSource source, String[] args) {
                dragonHealth(source, args);
            }
        });
        // charge
        registerCommand(new EndFightCommand() {
            @Override
            public String getCommandName() {
                return "charge";
            }

            @Override
            public String getUsageTranslationKey(CommandSource source) {
                return "/charge";
            }

            @Override
            public void execute(CommandSource source, String[] args) {
                chargeCommand(source);
            }
        });
        // roll
        registerCommand(new EndFightCommand() {
            @Override
            public String getCommandName() {
                return "roll";
            }

            @Override
            public String getUsageTranslationKey(CommandSource source) {
                return "/roll";
            }

            @Override
            public void execute(CommandSource source, String[] args) {
                getDragon(((PlayerEntity) source).world).ifPresent(dragon -> {
                    dragon.field_3742 = dragon.x;
                    dragon.field_3751 = dragon.y + 1;
                    dragon.field_3752 = dragon.z;
                });
                source.method_3331("Rolled");
            }
        });
        // goodcharge
        registerCommand(new EndFightCommand() {
            @Override
            public String getCommandName() {
                return "goodcharge";
            }

            @Override
            public String getUsageTranslationKey(CommandSource source) {
                return "/goodcharge [distance, height]";
            }

            @Override
            public void execute(CommandSource source, String[] args) throws CommandException {
                if (source instanceof PlayerEntity) {
                    Optional<EnderDragonEntity> dragon = getDragon(((PlayerEntity) source).world);
                    if (dragon.isPresent()) {
                        int dist = 75;
                        int height = 24;
                        if (args.length >= 2) {
                            try {
                                dist = Integer.parseUnsignedInt(args[0]);
                                height = Integer.parseUnsignedInt(args[1]);
                            } catch (NumberFormatException e) {
                                throw new CommandException("invalid.input");
                            }
                        }

                        float yaw = ((PlayerEntity) source).getHeadRotation();
                        double sin = Math.sin((yaw + 90) * Math.PI / 180f),
                                cos = Math.cos((yaw + 90) * Math.PI / 180f);
                        EnderDragonEntity entityDragon = dragon.get();
                        entityDragon.updatePositionAndAngles(((PlayerEntity) source).x + cos * dist,
                                ((PlayerEntity) source).y + height,
                                ((PlayerEntity) source).z + sin * dist,
                                0,
                                (yaw + 360) % 360 - 180);
                        entityDragon.velocityX = 0;
                        entityDragon.velocityY = 0;
                        entityDragon.velocityZ = 0;
                        entityDragon.setForwardSpeed(0);
                        chargeCommand(source);
                    } else {
                        source.method_3331("§c" + "No Dragon Found");
                    }
                }
            }
        });
        // noinspection unchecked
        Medium.commandMap = (List<EndFightCommand>) getCommandMap().values().stream()
                .filter(cmd -> cmd instanceof EndFightCommand)
                .sorted()
                .collect(Collectors.toList());
    }

    @Unique
    private void chargeCommand(CommandSource source) {
        if (source instanceof PlayerEntity) {
            Optional<EnderDragonEntity> dragon = getDragon(((PlayerEntity) source).world);
            if (dragon.isPresent()) {
                EnderDragonEntity entityDragon = dragon.get();
                ((EnderDragonAccessor) entityDragon).setTarget((Entity) source);

                source.method_3331("Forced Dragon Charge");
            } else {
                source.method_3331("§c" + "No Dragon Found");
            }
        }
    }
}
