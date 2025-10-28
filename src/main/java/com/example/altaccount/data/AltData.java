package com.example.altaccount.data;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AltData implements ConfigurationSerializable {
    
    private ItemStack[] inventory;
    private ItemStack[] armor;
    private ItemStack offHand;
    private Location location;
    private int experience;
    private int level;
    private float exhaustion;
    private int foodLevel;
    private float saturation;
    private double health;
    private int fireTicks;
    private int airTicks;
    
    public AltData() {
        // Default constructor for deserialization
    }
    
    public AltData(ItemStack[] inventory, ItemStack[] armor, ItemStack offHand, 
                   Location location, int experience, int level, float exhaustion,
                   int foodLevel, float saturation, double health, int fireTicks, int airTicks) {
        this.inventory = inventory;
        this.armor = armor;
        this.offHand = offHand;
        this.location = location;
        this.experience = experience;
        this.level = level;
        this.exhaustion = exhaustion;
        this.foodLevel = foodLevel;
        this.saturation = saturation;
        this.health = health;
        this.fireTicks = fireTicks;
        this.airTicks = airTicks;
    }
    
    // Getters
    public ItemStack[] getInventory() { return inventory; }
    public ItemStack[] getArmor() { return armor; }
    public ItemStack getOffHand() { return offHand; }
    public Location getLocation() { return location; }
    public int getExperience() { return experience; }
    public int getLevel() { return level; }
    public float getExhaustion() { return exhaustion; }
    public int getFoodLevel() { return foodLevel; }
    public float getSaturation() { return saturation; }
    public double getHealth() { return health; }
    public int getFireTicks() { return fireTicks; }
    public int getAirTicks() { return airTicks; }
    
    // Setters
    public void setInventory(ItemStack[] inventory) { this.inventory = inventory; }
    public void setArmor(ItemStack[] armor) { this.armor = armor; }
    public void setOffHand(ItemStack offHand) { this.offHand = offHand; }
    public void setLocation(Location location) { this.location = location; }
    public void setExperience(int experience) { this.experience = experience; }
    public void setLevel(int level) { this.level = level; }
    public void setExhaustion(float exhaustion) { this.exhaustion = exhaustion; }
    public void setFoodLevel(int foodLevel) { this.foodLevel = foodLevel; }
    public void setSaturation(float saturation) { this.saturation = saturation; }
    public void setHealth(double health) { this.health = health; }
    public void setFireTicks(int fireTicks) { this.fireTicks = fireTicks; }
    public void setAirTicks(int airTicks) { this.airTicks = airTicks; }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        result.put("inventory", inventory);
        result.put("armor", armor);
        result.put("offHand", offHand);
        result.put("location", location);
        result.put("experience", experience);
        result.put("level", level);
        result.put("exhaustion", exhaustion);
        result.put("foodLevel", foodLevel);
        result.put("saturation", saturation);
        result.put("health", health);
        result.put("fireTicks", fireTicks);
        result.put("airTicks", airTicks);
        return result;
    }
    
    public static AltData deserialize(Map<String, Object> args) {
        AltData data = new AltData();
        
        // Handle inventory array conversion
        Object inventoryObj = args.get("inventory");
        if (inventoryObj instanceof List) {
            List<?> inventoryList = (List<?>) inventoryObj;
            data.inventory = inventoryList.toArray(new ItemStack[0]);
        } else if (inventoryObj instanceof ItemStack[]) {
            data.inventory = (ItemStack[]) inventoryObj;
        }
        
        // Handle armor array conversion
        Object armorObj = args.get("armor");
        if (armorObj instanceof List) {
            List<?> armorList = (List<?>) armorObj;
            data.armor = armorList.toArray(new ItemStack[0]);
        } else if (armorObj instanceof ItemStack[]) {
            data.armor = (ItemStack[]) armorObj;
        }
        
        data.offHand = (ItemStack) args.get("offHand");
        data.location = (Location) args.get("location");
        
        // Handle numeric values with null checks
        Object exp = args.get("experience");
        data.experience = exp != null ? (Integer) exp : 0;
        
        Object lvl = args.get("level");
        data.level = lvl != null ? (Integer) lvl : 0;
        
        Object exh = args.get("exhaustion");
        data.exhaustion = exh != null ? ((Number) exh).floatValue() : 0.0f;
        
        Object food = args.get("foodLevel");
        data.foodLevel = food != null ? (Integer) food : 20;
        
        Object sat = args.get("saturation");
        data.saturation = sat != null ? ((Number) sat).floatValue() : 5.0f;
        
        Object hp = args.get("health");
        data.health = hp != null ? ((Number) hp).doubleValue() : 20.0;
        
        Object fire = args.get("fireTicks");
        data.fireTicks = fire != null ? (Integer) fire : 0;
        
        Object air = args.get("airTicks");
        data.airTicks = air != null ? (Integer) air : 300;
        
        return data;
    }
}