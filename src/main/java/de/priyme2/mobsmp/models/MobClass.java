package de.priyme2.mobsmp.models;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public enum MobClass {
    CHICKEN, TURTLE, VILLAGER, ZOMBIE, CREEPER, SPIDER, WITCH, DRAGON, WARDEN, WITHER, ENDERMAN, SHULKER, PIGLIN, BLAZE;

    public Component getColoredName() {
        return Component.text(name(), switch (this) {
            case CHICKEN -> NamedTextColor.WHITE;
            case TURTLE -> NamedTextColor.GREEN;
            case VILLAGER -> NamedTextColor.GOLD;
            case ZOMBIE -> NamedTextColor.DARK_GREEN;
            case CREEPER -> NamedTextColor.GREEN;
            case SPIDER -> NamedTextColor.DARK_RED;
            case WITCH -> NamedTextColor.LIGHT_PURPLE;
            case DRAGON -> NamedTextColor.DARK_PURPLE;
            case WARDEN -> NamedTextColor.AQUA;
            case WITHER -> NamedTextColor.GRAY;
            case ENDERMAN -> NamedTextColor.DARK_PURPLE;
            case SHULKER -> NamedTextColor.LIGHT_PURPLE;
            case PIGLIN -> NamedTextColor.GOLD;
            case BLAZE -> NamedTextColor.RED;
        });
    }
}
