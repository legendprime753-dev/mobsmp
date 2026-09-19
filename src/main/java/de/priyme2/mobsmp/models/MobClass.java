package de.priyme2.mobsmp.models;

import de.priyme2.mobsmp.utils.Msg;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public enum MobClass {
    CHICKEN, TURTLE, VILLAGER, ZOMBIE, CREEPER, SPIDER, WITCH, DRAGON, WARDEN, WITHER, ENDERMAN, SHULKER, PIGLIN, BLAZE;

    public Component getColoredName() {
        String mm = switch (this) {
            case CHICKEN -> "<gradient:#ffffff:#aaaaaa>CHICKEN</gradient>";
            case TURTLE -> "<gradient:#2ecc71:#27ae60>TURTLE</gradient>";
            case VILLAGER -> "<gradient:#f1c40f:#e67e22>VILLAGER</gradient>";
            case ZOMBIE -> "<gradient:#2ecc71:#16a085>ZOMBIE</gradient>";
            case CREEPER -> "<gradient:#2ecc71:#000000>CREEPER</gradient>";
            case SPIDER -> "<gradient:#c0392b:#8e44ad>SPIDER</gradient>";
            case WITCH -> "<gradient:#9b59b6:#34495e>WITCH</gradient>";
            case DRAGON -> "<gradient:#8e44ad:#2c3e50>DRAGON</gradient>";
            case WARDEN -> "<gradient:#1abc9c:#2980b9>WARDEN</gradient>";
            case WITHER -> "<gradient:#7f8c8d:#2c3e50>WITHER</gradient>";
            case ENDERMAN -> "<gradient:#9b59b6:#000000>ENDERMAN</gradient>";
            case SHULKER -> "<gradient:#e056fd:#be2edd>SHULKER</gradient>";
            case PIGLIN -> "<gradient:#f1c40f:#e74c3c>PIGLIN</gradient>";
            case BLAZE -> "<gradient:#ff7979:#eb4d4b>BLAZE</gradient>";
        };
        return net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(mm);
    }

    public java.util.List<Component> getLore() {
        java.util.List<Component> lore = new java.util.ArrayList<>();
        lore.add(Component.empty());
        switch (this) {
            case CHICKEN -> {
                lore.add(loreGray("ᴘᴀꜱꜱɪᴠ: 8 ʜᴇʀᴢᴇɴ, ʟᴀɴɢꜱᴀᴍᴇʀ ꜰᴀʟʟ"));
                lore.add(loreYellow("3 ᴋɪʟʟꜱ: ᴘʀᴏᴊᴇᴋᴛɪʟ-ᴇɪ (ʙʟɪɴᴅɴᴇꜱꜱ)"));
                lore.add(loreGold("5 ᴋɪʟʟꜱ: ʙᴇꜱᴄʜᴡᴏᴇʀᴛ ʜᴜᴇʜɴᴇʀ ᴍɪᴛ ʙᴀʙʏ-ᴢᴏᴍʙɪᴇꜱ"));
            }
            case ZOMBIE -> {
                lore.add(loreGray("ᴘᴀꜱꜱɪᴠ: ꜱᴛᴀᴇʀᴋᴇ 1 ʙᴇɪ ɴᴀᴄʜᴛ/ᴅᴜɴᴋᴇʟʜᴇɪᴛ"));
                lore.add(loreYellow("3 ᴋɪʟʟꜱ: ʙᴇꜱᴄʜᴡᴏᴇʀᴛ 3 ᴢᴏᴍʙɪᴇꜱ"));
                lore.add(loreGold("5 ᴋɪʟʟꜱ: ɴᴀᴇᴄʜꜱᴛᴇʀ ʜɪᴛ ɢɪʙᴛ ʜᴜɴɢᴇʀ & ᴡɪᴛʜᴇʀ"));
            }
            case CREEPER -> {
                lore.add(loreGray("ᴘᴀꜱꜱɪᴠ: ᴋᴇɪɴᴇ"));
                lore.add(loreYellow("3 ᴋɪʟʟꜱ: ꜱᴘᴀᴡɴᴛ ɢᴇᴢᴜᴇɴᴅᴇᴛᴇꜱ ᴛɴᴛ"));
                lore.add(loreGold("5 ᴋɪʟʟꜱ: ɴᴀᴇᴄʜꜱᴛᴇʀ ʜɪᴛ ʙᴇꜱᴄʜᴡᴏᴇʀᴛ ʙʟɪᴛᴢ"));
            }
            case SPIDER -> {
                lore.add(loreGray("ᴘᴀꜱꜱɪᴠ: ꜱᴘᴇᴇᴅ 2"));
                lore.add(loreYellow("3 ᴋɪʟʟꜱ: ᴡɪʀꜰᴛ ɢɪꜰᴛ-ᴛʀᴀɴᴋ"));
                lore.add(loreGold("5 ᴋɪʟʟꜱ: ꜱᴘɪɴɴᴇɴɴᴇᴛᴢ ᴀᴜꜰ ᴅᴇɴ ᴋᴏᴘꜰ"));
            }
            case ENDERMAN -> {
                lore.add(loreGray("ᴘᴀꜱꜱɪᴠ: 13 ʜᴇʀᴢᴇɴ, ꜱᴘᴇᴇᴅ 2, ᴡᴀꜱꜱᴇʀꜱᴄʜᴀᴅᴇɴ"));
                lore.add(loreYellow("3 ᴋɪʟʟꜱ: ᴇɴᴅᴇʀᴘᴇʀʟᴇ (ᴄᴅ ꜱɪɴᴋᴛ ᴍɪᴛ ᴋɪʟʟꜱ)"));
                lore.add(loreGold("5 ᴋɪʟʟꜱ: ᴛᴇʟᴇᴘᴏʀᴛ ʜɪɴᴛᴇʀ ɢᴇɢɴᴇʀ"));
            }
            case WARDEN -> {
                lore.add(loreGray("ᴘᴀꜱꜱɪᴠ: 13 ʜᴇʀᴢᴇɴ, ʀᴇꜱɪꜱᴛᴇɴᴢ 1"));
                lore.add(loreYellow("3 ᴋɪʟʟꜱ: ꜱᴏɴɪᴄ ʙᴏᴏᴍ (8 ᴅᴍɢ + ʀᴜᴇᴄᴋꜱᴛᴏꜱꜱ)"));
                lore.add(loreGold("5 ᴋɪʟʟꜱ: ɢᴇɢɴᴇʀɴ ɪᴍ ᴜᴍᴋʀᴇɪꜱ ᴅᴀʀᴋɴᴇꜱꜱ"));
            }
            case WITHER -> {
                lore.add(loreGray("ᴘᴀꜱꜱɪᴠ: ɪᴍᴍᴜɴ ɢᴇɢᴇɴ ᴡɪᴛʜᴇʀ"));
                lore.add(loreYellow("3 ᴋɪʟʟꜱ: ꜱᴄʜɪᴇꜱꜱᴛ 2 ᴡɪᴛʜᴇʀ-ꜱᴄʜᴀᴇᴅᴇʟ"));
                lore.add(loreGold("5 ᴋɪʟʟꜱ: ɢᴇɢɴᴇʀɴ ɪᴍ ᴜᴍᴋʀᴇɪꜱ ᴡɪᴛʜᴇʀ-ᴇꜰꜰᴇᴋᴛ"));
            }
            case PIGLIN -> {
                lore.add(loreGray("ᴘᴀꜱꜱɪᴠ: ꜱᴛᴀᴇʀᴋᴇ 1, ꜱᴘᴇᴇᴅ 2 ᴍɪᴛ ᴀxᴛ"));
                lore.add(loreYellow("3 ᴋɪʟʟꜱ: 15ꜱ ꜱᴛᴀᴇʀᴋᴇ 2"));
                lore.add(loreGold("5 ᴋɪʟʟꜱ: ʙᴇꜱᴄʜᴡᴏᴇʀᴛ 2 ᴘɪɢʟɪɴ ʙʀᴜᴛᴇꜱ (20ꜱ)"));
            }
            case BLAZE -> {
                lore.add(loreGray("ᴘᴀꜱꜱɪᴠ: ꜰᴇᴜᴇʀʀᴇꜱɪꜱᴛᴇɴᴢ, ᴡᴀꜱꜱᴇʀꜱᴄʜᴀᴅᴇɴ"));
                lore.add(loreYellow("3 ᴋɪʟʟꜱ: ꜱᴄʜɪᴇꜱꜱᴛ 3 ꜰᴇᴜᴇʀʙᴀᴇʟʟᴇ"));
                lore.add(loreGold("5 ᴋɪʟʟꜱ: ᴇɴᴛᴢᴜᴇɴᴅᴇᴛ ɢᴇɢɴᴇʀ ɪᴍ ᴜᴍᴋʀᴇɪꜱ"));
            }
            case VILLAGER -> {
                lore.add(loreGray("ᴘᴀꜱꜱɪᴠ: ʜᴇʟᴅ ᴅᴇꜱ ᴅᴏʀꜰᴇꜱ (ʜᴀɴᴅᴇʟꜱ-ʀᴀʙᴀᴛᴛᴇ)"));
                lore.add(loreYellow("3 ᴋɪʟʟꜱ: ᴠᴇʀᴢᴀᴜʙᴇʀᴛ 1 ɪᴛᴇᴍ ᴢᴜꜰᴀᴇʟʟɪɢ"));
                lore.add(loreGold("5 ᴋɪʟʟꜱ: ʙᴏᴅʏɢᴜᴀʀᴅ-ᴇɪꜱᴇɴɢᴏʟᴇᴍ (20ꜱ)"));
            }
            case TURTLE -> {
                lore.add(loreGray("ᴘᴀꜱꜱɪᴠ: ᴅᴇʟᴘʜɪɴ-ꜱᴄʜᴡɪᴍᴍᴇɴ, ꜱʟᴏᴡɴᴇꜱꜱ ᴀɴ ʟᴀɴᴅ"));
                lore.add(loreYellow("3 ᴋɪʟʟꜱ: 8ꜱ ʀᴇꜱɪꜱᴛᴇɴᴢ 3"));
                lore.add(loreGold("5 ᴋɪʟʟꜱ: ꜱᴄʜᴀᴅᴇɴ + ꜱʟᴏᴡɴᴇꜱꜱ ɪᴍ ᴜᴍᴋʀᴇɪꜱ"));
            }
            case SHULKER -> {
                lore.add(loreGray("ᴘᴀꜱꜱɪᴠ: ʀᴇꜱɪꜱᴛᴇɴᴢ 2, ꜱʟᴏᴡɴᴇꜱꜱ 2"));
                lore.add(loreYellow("3 ᴋɪʟʟꜱ: ᴢɪᴇʟᴠᴇʀꜰᴏʟɢᴇɴᴅᴇꜱ ꜱʜᴜʟᴋᴇʀ-ᴘʀᴏᴊᴇᴋᴛɪʟ"));
                lore.add(loreGold("5 ᴋɪʟʟꜱ: ɢᴇɢɴᴇʀɴ ɪᴍ ᴜᴍᴋʀᴇɪꜱ ʟᴇᴠɪᴛᴀᴛɪᴏɴ"));
            }
            case WITCH -> {
                lore.add(loreGray("ᴘᴀꜱꜱɪᴠ: ɪᴍᴍᴜɴ ɢᴇɢᴇɴ ɢɪꜰᴛ, ᴡɪᴛʜᴇʀ, ᴡᴇᴀᴋɴᴇꜱꜱ"));
                lore.add(loreYellow("3 ᴋɪʟʟꜱ: ᴢᴜꜰᴀᴇʟʟɪɢᴇʀ ᴅᴇʙᴜꜰꜰ-ᴛʀᴀɴᴋ"));
                lore.add(loreGold("5 ᴋɪʟʟꜱ: ʜᴇɪʟᴛ 4 ʜᴇʀᴢᴇɴ + ʀᴇɢᴇɴ"));
            }
            case DRAGON -> {
                lore.add(Component.text("ᴘᴀꜱꜱɪᴠ: 13 ʜᴇʀᴢᴇɴ (ɴᴜʀ ᴍɪᴛ ᴅʀᴀɢᴏɴ ᴇɢɢ)", TextColor.color(0xAA00AA)));
                lore.add(loreYellow("3 ᴋɪʟʟꜱ: ᴅʀᴀᴄʜᴇɴᴀᴛᴇᴍ-ᴡᴏʟᴋᴇ"));
                lore.add(loreGold("5 ᴋɪʟʟꜱ: ꜱᴄʜʟᴇᴜᴅᴇʀᴛ ɢᴇɢɴᴇʀ ɪɴ ᴅɪᴇ ʟᴜꜰᴛ"));
            }
        }
        return lore;
    }

    private static Component loreGray(String text) {
        return Component.text(text, TextColor.color(0xAAAAAA));
    }
    private static Component loreYellow(String text) {
        return Component.text(text, TextColor.color(0xFFFF55));
    }
    private static Component loreGold(String text) {
        return Component.text(text, TextColor.color(0xFFAA00));
    }
}
