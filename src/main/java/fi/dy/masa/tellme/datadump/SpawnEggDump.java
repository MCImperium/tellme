package fi.dy.masa.tellme.datadump;

import java.lang.reflect.Field;
import java.util.List;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import fi.dy.masa.tellme.TellMe;
import fi.dy.masa.tellme.util.datadump.DataDump;
import net.minecraftforge.registries.ForgeRegistries;

public class SpawnEggDump
{
    public static List<String> getFormattedSpawnEggDump(DataDump.Format format)
    {
        DataDump spawnEggDump = new DataDump(4, format);

        Field fieldPrimaryColor = null;
        Field fieldSecondaryColor = null;

        try
        {
            fieldPrimaryColor = ObfuscationReflectionHelper.findField(SpawnEggItem.class, "f_151200_");
            fieldSecondaryColor = ObfuscationReflectionHelper.findField(SpawnEggItem.class, "f_151201_");
        }
        catch (Exception e)
        {
            TellMe.logger.warn("Exception while reflecting spawn egg color fields", e);
        }

        for (SpawnEggItem egg : SpawnEggItem.eggs())
        {
            try
            {
                String id = ForgeRegistries.ITEMS.getKey(egg).toString();

                int primaryColor = (int) fieldPrimaryColor.get(egg);
                int secondaryColor = (int) fieldSecondaryColor.get(egg);
                String colorPrimary = String.format("0x%08X (%10d)", primaryColor, primaryColor);
                String colorSecondary = String.format("0x%08X (%10d)", secondaryColor, secondaryColor);

                spawnEggDump.addData(id, ForgeRegistries.ENTITY_TYPES.getKey(egg.getType(null)).toString(), colorPrimary, colorSecondary);
            }
            catch (Exception e)
            {
                TellMe.logger.warn("Exception while dumping spawn eggs", e);
            }
        }

        spawnEggDump.addTitle("Registry Name", "Spawned ID", "Egg primary color", "Egg secondary color");

        return spawnEggDump.getLines();
    }
}
