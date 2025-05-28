package net.hinson820.arcanearbor.core.init;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.menu.ArcaneEnchantmentMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MenuTypesInit {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, ArcaneArbor.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<ArcaneEnchantmentMenu>> ARCANE_ENCHANTMENT_MENU =
            MENUS.register("arcane_enchantment_menu",
                    () -> IMenuTypeExtension.create(ArcaneEnchantmentMenu::new));

}
