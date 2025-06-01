package net.hinson820.arcanearbor.common.event.enchantment;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantmentManager;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantmentRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@EventBusSubscriber(modid = ArcaneArbor.MODID)
public class ArcaneEnchantmentTooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        Optional<Map.Entry<String, Integer>> activeEnchantment = ArcaneEnchantmentManager.getActiveArcaneEnchantment(stack);

        activeEnchantment.ifPresent(entry -> {
            event.getToolTip().add(Component.literal("")); // Add a blank line for spacing
            event.getToolTip().add(Component.translatable("tooltip." + ArcaneArbor.MODID + ".arcane_enchantment_singular")
                    .withStyle(ChatFormatting.GOLD));

            String id = entry.getKey();
            int level = entry.getValue();

            ArcaneEnchantmentRegistry.get(id).ifPresent(arcaneEnchant -> {
                event.getToolTip().add(arcaneEnchant.getDisplayName(level).copy().withStyle(ChatFormatting.AQUA));
                List<Component> specificTooltipLines = arcaneEnchant.getTooltip(stack, level);
                specificTooltipLines.forEach(line -> event.getToolTip().add(line.copy().withStyle(ChatFormatting.GRAY)));

                // It's good practice to check if event.getEntity() is null, though for tooltips it's usually the player.
                LivingEntity tooltipUser = event.getEntity();
                int activationCost = arcaneEnchant.getActivationManaCost(level, stack, tooltipUser); // tooltipUser can be null
                if (activationCost > 0) {
                    event.getToolTip().add(Component.translatable("tooltip." + ArcaneArbor.MODID + ".activation_cost", activationCost)
                            .withStyle(ChatFormatting.DARK_PURPLE));
                }
            });
        });
    }
}
