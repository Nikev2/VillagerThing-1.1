package com.example.helloworldmod;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

public class FollowPlayerGoal extends Goal {
    private final MobEntity mob;
    private PlayerEntity targetPlayer;

    public FollowPlayerGoal(MobEntity mob, PlayerEntity targetPlayer) {
        this.mob = mob;
        this.targetPlayer = targetPlayer;
    }

    @Override
    public boolean canStart() {
        // You can add conditions here for when the mob should start following
        return true;
    }

    @Override
    public void start() {
        // Initial setup when the goal starts
    }

    @Override
    public void tick() {
        // Update the mob's path to follow the player each tick
        if (targetPlayer != null) {
            this.mob.getNavigation().startMovingTo(targetPlayer, 1.0);

        }
    }

    @Override
    public boolean shouldContinue() {
        // Conditions for the goal to continue running
        return targetPlayer != null && targetPlayer.isAlive();
    }
}
