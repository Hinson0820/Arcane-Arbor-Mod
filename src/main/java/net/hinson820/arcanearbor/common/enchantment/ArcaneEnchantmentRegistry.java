package net.hinson820.arcanearbor.common.enchantment;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.enchantment.types.IgnorePainEnchantment;
import net.hinson820.arcanearbor.common.enchantment.types.LifeStealEnchantment;

import java.util.*;
import java.util.function.Supplier;

public class ArcaneEnchantmentRegistry {
    private static final Map<String, Supplier<ArcaneEnchantment>> REGISTERED_SUPPLIERS = new HashMap<>();
    private static final Map<String, ArcaneEnchantment> INSTANCES = new HashMap<>();

    public static final Supplier<ArcaneEnchantment> IGNORE_PAIN = register(IgnorePainEnchantment.ID, IgnorePainEnchantment::new);
    public static final Supplier<ArcaneEnchantment> LIFE_STEAL = register(LifeStealEnchantment.ID, LifeStealEnchantment::new);

    private static Supplier<ArcaneEnchantment> register(String id, Supplier<ArcaneEnchantment> enchantmentSupplier) {
        if (REGISTERED_SUPPLIERS.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate Arcane Enchantment ID: " + id);
        }
        ArcaneArbor.LOGGER.debug("ArcaneEnchantmentRegistry.register: Storing supplier for ID '{}'", id);
        Supplier<ArcaneEnchantment> cachingSupplier = () -> {
            ArcaneArbor.LOGGER.debug("ArcaneEnchantmentRegistry - CachingSupplier for '{}': Accessing INSTANCES.computeIfAbsent...", id);
            return INSTANCES.computeIfAbsent(id, key -> {
                ArcaneArbor.LOGGER.debug("ArcaneEnchantmentRegistry - CachingSupplier for '{}': INSTANCES miss, creating new instance via enchantmentSupplier.get()", id);
                return enchantmentSupplier.get();
            });
        };
        REGISTERED_SUPPLIERS.put(id, cachingSupplier);
        return cachingSupplier;
    }

    public static Optional<ArcaneEnchantment> get(String id) {
        ArcaneArbor.LOGGER.debug("ArcaneEnchantmentRegistry.get: Requesting ID '{}'", id);
        Supplier<ArcaneEnchantment> supplier = REGISTERED_SUPPLIERS.get(id);
        if (supplier != null) {
            ArcaneArbor.LOGGER.debug("ArcaneEnchantmentRegistry.get: Found supplier for ID '{}', calling supplier.get()", id);
            return Optional.of(supplier.get());
        }
        ArcaneArbor.LOGGER.debug("ArcaneEnchantmentRegistry.get: No supplier found for ID '{}'", id);
        return Optional.empty();
    }

    public static Collection<ArcaneEnchantment> getAllEnchantments() {
        ArcaneArbor.LOGGER.debug("ArcaneEnchantmentRegistry.getAllEnchantments: Ensuring all instances are populated into INSTANCES map.");
        for (String id : REGISTERED_SUPPLIERS.keySet()) {
            get(id);
        }
        ArcaneArbor.LOGGER.debug("ArcaneEnchantmentRegistry.getAllEnchantments: Returning {} values from INSTANCES map.", INSTANCES.values().size());
        return Collections.unmodifiableCollection(INSTANCES.values());
    }

    public static void initialize() {
        ArcaneArbor.LOGGER.info("ArcaneEnchantmentRegistry: Initializing... Static fields should have populated REGISTERED_SUPPLIERS.");
        Supplier<ArcaneEnchantment> ipRef = IGNORE_PAIN;
        Supplier<ArcaneEnchantment> lsRef = LIFE_STEAL;

        ArcaneArbor.LOGGER.info("ArcaneEnchantmentRegistry: REGISTERED_SUPPLIERS count after static field access: {}", REGISTERED_SUPPLIERS.size());
        REGISTERED_SUPPLIERS.forEach((id, supplier) -> ArcaneArbor.LOGGER.info(" -> Registered Supplier in REGISTERED_SUPPLIERS: {}", id));

        ArcaneArbor.LOGGER.info("ArcaneEnchantmentRegistry: Forcing instantiation of all enchantments into INSTANCES map via initialize()...");
        for (String id : REGISTERED_SUPPLIERS.keySet()) {
            get(id);
        }

        ArcaneArbor.LOGGER.info("Arcane Enchantment Registry Initialized. INSTANCES map contains {} enchantments.", INSTANCES.size());
        INSTANCES.forEach((id, inst) -> ArcaneArbor.LOGGER.info(" -> Instantiated in INSTANCES: {} -> {}", id, inst.getClass().getSimpleName()));
    }
}