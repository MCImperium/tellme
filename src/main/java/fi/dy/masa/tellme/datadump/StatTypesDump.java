package fi.dy.masa.tellme.datadump;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraftforge.registries.ForgeRegistries;
import fi.dy.masa.tellme.util.datadump.DataDump;
import fi.dy.masa.tellme.util.datadump.DataDump.Format;

public class StatTypesDump
{
    public static List<String> getFormattedDump(Format format)
    {
        DataDump dump = new DataDump(2, format);

        for (Map.Entry<ResourceKey<StatType<?>>, StatType<?>> entry : ForgeRegistries.STAT_TYPES.getEntries())
        {
            StatType<?> type = entry.getValue();
            String typeName = ForgeRegistries.STAT_TYPES.getKey(type).toString();

            for (Stat<?> stat : type)
            {
                dump.addData(typeName, stat.getName());
            }
        }

        dump.addTitle("Type registry name", "Stat name");

        return dump.getLines();
    }

    public static List<String> getFormattedDumpCustomStats(Format format)
    {
        DataDump dump = new DataDump(2, format);

        for (ResourceLocation key : Registry.CUSTOM_STAT.keySet())
        {
            String typeName = key.toString();
            Optional<ResourceLocation> stat = Registry.CUSTOM_STAT.getOptional(key);

            if (stat.isPresent())
            {
                dump.addData(typeName, stat.get().toString());
            }
        }

        dump.addTitle("Registry name", "Stat name");

        return dump.getLines();
    }
}
