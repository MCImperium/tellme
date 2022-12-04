package fi.dy.masa.tellme.datadump;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraftforge.registries.ForgeRegistries;
import fi.dy.masa.tellme.util.datadump.DataDump;

public class BlockStatesDump
{
    public static List<String> getFormattedBlockStatesDumpByBlock()
    {
        List<String> outputLines = new ArrayList<>();

        for (Map.Entry<ResourceKey<Block>, Block> entry : ForgeRegistries.BLOCKS.getEntries())
        {
            Block block = entry.getValue();
            List<String> lines = new ArrayList<>();

            for (Entry<Property<?>, Comparable<?>> propertyComparableEntry : block.defaultBlockState().getValues().entrySet())
            {
                lines.add(propertyComparableEntry.getKey().toString());
            }

            outputLines.add(ForgeRegistries.BLOCKS.getKey(block).toString() + ": " + String.join(", ", lines));
        }

        Collections.sort(outputLines);

        outputLines.add(0, "Block registry name | BlockState properties");
        outputLines.add(1, "-------------------------------------------------------------------------------------");

        return outputLines;
    }

    public static List<String> getFormattedBlockStatesDumpByState(DataDump.Format format)
    {
        DataDump blockStatesDump = new DataDump(3, format);

        for (Map.Entry<ResourceKey<Block>, Block> entry : ForgeRegistries.BLOCKS.getEntries())
        {
            Block block = entry.getValue();
            String regName = ForgeRegistries.BLOCKS.getKey(block).toString();

            ImmutableList<BlockState> validStates = block.getStateDefinition().getPossibleStates();

            for (BlockState state : validStates)
            {
                List<String> lines = new ArrayList<>();

                for (Entry<Property<?>, Comparable<?>> propEntry : state.getValues().entrySet())
                {
                    lines.add(propEntry.getKey().getName() + "=" + propEntry.getValue().toString());
                }
                ModelManager mm = Minecraft.getInstance().getModelManager();
                ModelResourceLocation loc = BlockModelShaper.stateToModelLocation(state);
                BakedModel model = mm.getModel(loc);

                blockStatesDump.addData(regName, String.join(",", lines), String.valueOf(model instanceof MultiPartBakedModel));
            }
        }

        blockStatesDump.addTitle("Block registry name", "BlockState properties", "Multipart");

        return blockStatesDump.getLines();
    }
}
