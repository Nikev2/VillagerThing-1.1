import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.village.VillageGossipType;
import net.minecraft.village.VillagerGossips;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import java.util.List;
import com.mojang.brigadier.CommandDispatcher;
public class NoNegativeGossips {

    public static void register(){

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
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
        });

    }
}
