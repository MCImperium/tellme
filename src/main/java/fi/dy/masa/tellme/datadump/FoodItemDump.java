package fi.dy.masa.tellme.datadump;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import fi.dy.masa.tellme.util.datadump.DataDump;
import fi.dy.masa.tellme.util.datadump.DataDump.Alignment;
import fi.dy.masa.tellme.util.datadump.DataDump.Format;

public class FoodItemDump
{
    private static void addData(DataDump dump, Item item, ResourceLocation rl)
    {
        String registryName = rl.toString();
        ItemStack stack = new ItemStack(item);
        String displayName = stack.isEmpty() == false ? stack.getHoverName().getString() : DataDump.EMPTY_STRING;
        displayName = ChatFormatting.stripFormatting(displayName);

        FoodProperties food = item.getFoodProperties();
        String hunger = String.valueOf(food.getNutrition());
        String saturation = String.valueOf(food.getSaturationModifier());
        String isMeat = String.valueOf(food.isMeat());
        String isFastEat = String.valueOf(food.isFastFood());
        List<Pair<MobEffectInstance, Float>> effects = food.getEffects();
        String effectsStr = effects.stream()
                .map((pair) -> "{[" + pair.getFirst().toString() + "], Probability: " + pair.getSecond() + "}")
                .collect(Collectors.joining(", "));

        dump.addData(registryName, displayName, hunger, saturation, isMeat, isFastEat, ItemDump.getTagNamesJoined(item), effectsStr);
    }

    public static List<String> getFormattedFoodItemDump(Format format)
    {
        DataDump itemDump = new DataDump(8, format);

        for (Map.Entry<ResourceKey<Item>, Item> entry : ForgeRegistries.ITEMS.getEntries())
        {
            Item item = entry.getValue();

            if (item.isEdible())
            {
                addData(itemDump, item, ForgeRegistries.ITEMS.getKey(item));
            }
        }

        itemDump.addTitle("Registry name", "Display name", "Hunger", "Saturation", "Is meat", "Fast to eat", "Tags", "Effects");

        itemDump.setColumnProperties(2, Alignment.RIGHT, true); // hunger
        itemDump.setColumnProperties(3, Alignment.RIGHT, true); // saturation

        return itemDump.getLines();
    }
}
