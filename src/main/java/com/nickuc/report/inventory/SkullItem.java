package com.nickuc.report.inventory;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class SkullItem {

    private static final ItemStack SKULL_ITEM;
    private static final Field PROFILE_FIELD;

    static {
        Material skullMaterial;
        try {
            skullMaterial = Material.valueOf("LEGACY_SKULL_ITEM");
        } catch (IllegalArgumentException exception) {
            skullMaterial = Material.valueOf("SKULL_ITEM");
        }

        try {
            String obcPrefix = Bukkit.getServer().getClass().getPackage().getName();
            Class<?> skullMetaClass = Class.forName(obcPrefix + "inventory.CraftMetaSkull");
            PROFILE_FIELD = skullMetaClass.getDeclaredField("profile");
            PROFILE_FIELD.setAccessible(true);
        } catch (ClassNotFoundException | NoClassDefFoundError | NoSuchFieldException exception) {
            throw new RuntimeException("Could not load SkullItem class!", exception);
        }

        SKULL_ITEM = new ItemStack(skullMaterial);
        SKULL_ITEM.setDurability((short) 3);
    }

    public static ItemStack createSkullItem(String url) {
        try {
            ItemStack skullItem = SKULL_ITEM.clone();
            SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
            if (skullMeta == null) {
                throw new IllegalArgumentException("SkullMeta cannot be null!");
            }

            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            String encodedUrl = Base64.getEncoder().encodeToString(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes(StandardCharsets.UTF_8));
            profile.getProperties().put("textures", new Property("textures", encodedUrl));
            PROFILE_FIELD.set(skullMeta, profile);

            skullItem.setItemMeta(skullMeta);
            return skullItem;
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException("Could not get head from " + url + "!", exception);
        }
    }

}
