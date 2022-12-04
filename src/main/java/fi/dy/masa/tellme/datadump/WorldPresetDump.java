package fi.dy.masa.tellme.datadump;

import java.util.List;
import java.util.Map;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import fi.dy.masa.tellme.util.datadump.DataDump;
import net.minecraftforge.server.ServerLifecycleHooks;

public class WorldPresetDump
{
    public static List<String> getFormattedDump(DataDump.Format format)
    {
        DataDump potionDump = new DataDump(1, format);
        RegistryAccess access = ServerLifecycleHooks.getCurrentServer().registryAccess();
        for (Map.Entry<ResourceKey<WorldPreset>, WorldPreset> entry : access.registry(Registry.WORLD_PRESET_REGISTRY).get().entrySet())
        {
            WorldPreset type = entry.getValue();
            ResourceLocation id = access.registry(Registry.WORLD_PRESET_REGISTRY).get().getKey(type);

            potionDump.addData(id.toString());
        }

        potionDump.addTitle("Registry name");

        return potionDump.getLines();
    }
}
