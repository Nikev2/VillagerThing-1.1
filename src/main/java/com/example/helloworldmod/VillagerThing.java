package com.example.helloworldmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.brain.task.VillagerBreedTask;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.entity.ai.brain.ScheduleBuilder;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillageGossipType;
import com.example.helloworldmod.CreeperSpawnHandler;
import net.minecraft.village.VillagerGossips;
import net.minecraft.world.gen.feature.VillagePlacedFeatures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.entity.EntityType;
import java.util.Comparator;
import java.util.EventListener;
import java.util.List;
import net.minecraft.client.render.entity.EntityRendererFactory;
import com.example.helloworldmod.mixin.VillagerClothingFeatureRendererMixin;
import com.example.helloworldmod.mixin.VillagerClothingFeatureRendererAccessor;
import com.example.helloworldmod.mixin.VillagerEntityMixin;
import com.example.helloworldmod.mixin.FeatureRendererAccessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.ai.pathing.MobNavigation;
import com.example.helloworldmod.FollowPlayerGoal;
public class VillagerThing implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("villagerthing");

    @Override
    public void onInitialize() {
        System.out.print("Hello Fabric world!");
        CreeperSpawnHandler.register();

        // Register your commands here
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("ModifyVillagerSchedule")
                    .executes(context -> {
                        ServerPlayerEntity player = context.getSource().getPlayer();
                        if (player == null) {
                            context.getSource().sendError(Text.of("Player is not available."));
                            return 0;
                        }

                        VillagerEntity nearestVillager = findNearestVillager(player);
                        if (nearestVillager != null) {
                            var brain = nearestVillager.getBrain();
                            brain.setSchedule(
                                    new ScheduleBuilder(Schedule.EMPTY)
                                            .withActivity(22000, Activity.IDLE)
                                            .withActivity(23000, Activity.WORK)
                                            .withActivity(6000, Activity.MEET)
                                            .withActivity(7000, Activity.WORK)
                                            .withActivity(10000, Activity.MEET)
                                            .withActivity(11000, Activity.IDLE)
                                            .withActivity(13000, Activity.REST)
                                            .build()
                            );
                            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal("Modified schedule of " + nearestVillager.getName()));
                        } else {
                            context.getSource().sendError(Text.of("No nearby villagers found."));
                        }

                        return 1;
                    }));

            dispatcher.register(CommandManager.literal("RemoveNegativeGossip")
                    .executes(context -> {
                        ServerPlayerEntity player = context.getSource().getPlayer();
                        if (player == null) {
                            context.getSource().sendError(Text.of("Player is not available."));
                            return 0;
                        }

                        ServerWorld world = player.getServerWorld();
                        List<VillagerEntity> villagers = world.getEntitiesByClass(VillagerEntity.class, new Box(player.getBlockPos()).expand(50), villager -> true); // Change 50 to your desired radius

                        int affectedVillagers = 0;
                        for (VillagerEntity villager : villagers) {
                            VillagerGossips gossip = villager.getGossip();
                            gossip.remove(VillageGossipType.MAJOR_NEGATIVE);
                            gossip.remove(VillageGossipType.MINOR_NEGATIVE);
                            affectedVillagers++;
                        }

                        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal(affectedVillagers + " villagers' negative gossips removed."));
                        return 1;
                    }));
            dispatcher.register(CommandManager.literal("BabyVillagerSchedule")
                    .executes(context -> {
                        ServerPlayerEntity player = context.getSource().getPlayer();
                        if (player == null) {
                            context.getSource().sendError(Text.of("Player is not available."));
                            return 0;
                        }

                        VillagerEntity nearestVillager = findNearestVillager(player);
                        if (nearestVillager != null) {
                            var brain = nearestVillager.getBrain();
                            brain.setSchedule(Schedule.VILLAGER_BABY);

                            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal("Modified schedule of " + nearestVillager.getName()));
                        } else {
                            context.getSource().sendError(Text.of("No nearby villagers found."));
                        }
                        return 1;
                    }));

            dispatcher.register(CommandManager.literal("changevillager")
                    .executes(context -> {
                        ServerPlayerEntity player = context.getSource().getPlayer();
                        if (player == null) {
                            context.getSource().sendError(Text.of("Player is not available."));
                            return 0;
                        }

                        VillagerEntity nearestVillager = findNearestVillager(player);
                        if (nearestVillager != null) {



                        } else {
                            context.getSource().sendError(Text.of("No nearby villagers found."));
                        }
                        return 1;
                    }));


        });
    }

    private void moveVillagerTo(VillagerEntity villager, double x, double y, double z) {

        BlockPos destination = new BlockPos((int)x,(int)y,(int)z);

        // Get the villager's navigation instance
        MobNavigation navigation = (MobNavigation) villager.getNavigation();

        // Command the villager to move to the destination
        navigation.startMovingTo(destination.getX(), destination.getY(), destination.getZ(), 1.0);
    }

    // Utility method to find the nearest villager to the player
    private VillagerEntity findNearestVillager(ServerPlayerEntity player) {
        Box searchBox = new Box(player.getBlockPos()).expand(10); // Search in a 10-block radius
        return player.getWorld().getEntitiesByClass(VillagerEntity.class, searchBox, e -> true)
                .stream()
                .min(Comparator.comparingDouble(v -> v.squaredDistanceTo(player)))
                .orElse(null);

    }
}
