package de.priyme2.mobsmp.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

/**
 * Utility class to convert text to small caps and provide consistent plugin messaging.
 */
public class Msg {
    
    private static final String SMALL_CAPS = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀꜱᴛᴜᴠᴡxʏᴢ";

    // Plugin prefix: [MobSMP]
    private static final Component PREFIX = Component.text("")
            .append(Component.text("「", TextColor.color(0x555555)))
            .append(Component.text("ᴍᴏʙꜱᴍᴘ", TextColor.color(0xFFAA00)))
            .append(Component.text("」 ", TextColor.color(0x555555)));

    /**
     * Convert a string to small caps unicode.
     */
    public static String small(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                sb.append(SMALL_CAPS.charAt(c - 'a'));
            } else if (c >= 'A' && c <= 'Z') {
                sb.append(SMALL_CAPS.charAt(c - 'A'));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Create a prefixed message with small caps text.
     */
    public static Component info(String text) {
        return PREFIX.append(Component.text(small(text), TextColor.color(0xAAAAAA)));
    }

    /**
     * Create a prefixed success message.
     */
    public static Component success(String text) {
        return PREFIX.append(Component.text(small(text), TextColor.color(0x55FF55)));
    }

    /**
     * Create a prefixed error message.
     */
    public static Component error(String text) {
        return PREFIX.append(Component.text(small(text), TextColor.color(0xFF5555)));
    }

    /**
     * Create a prefixed warning/highlight message.
     */
    public static Component warn(String text) {
        return PREFIX.append(Component.text(small(text), TextColor.color(0xFFFF55)));
    }

    /**
     * Create a prefixed special/rare message (gold gradient feel).
     */
    public static Component special(String text) {
        return PREFIX.append(Component.text(small(text), TextColor.color(0xFFAA00)));
    }

    /**
     * Create a prefixed message with custom color.
     */
    public static Component colored(String text, TextColor color) {
        return PREFIX.append(Component.text(small(text), color));
    }

    /**
     * Create a prefixed message with raw Component appended (e.g., for gradient names).
     */
    public static Component withComponent(String text, TextColor color, Component extra) {
        return PREFIX.append(Component.text(small(text), color)).append(extra);
    }

    /**
     * Get just the prefix.
     */
    public static Component prefix() {
        return PREFIX;
    }
}
