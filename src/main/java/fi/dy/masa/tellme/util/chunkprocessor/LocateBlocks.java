package fi.dy.masa.tellme.util.chunkprocessor;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import com.google.common.collect.Sets;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import fi.dy.masa.tellme.TellMe;
import fi.dy.masa.tellme.util.BlockInfo;
import fi.dy.masa.tellme.util.WorldUtils;
import fi.dy.masa.tellme.util.datadump.DataDump;

public class LocateBlocks extends LocateBase
{
    protected final Set<BlockState> filters;

    protected LocateBlocks(DataDump.Format format, List<String> filterStrings) throws CommandSyntaxException
    {
        super(format);

        this.filters = this.generateBlockStateFilters(filterStrings);
    }

    protected Set<BlockState> generateBlockStateFilters(List<String> filterStrings) throws CommandSyntaxException
    {
        Set<BlockState> filters = Sets.newIdentityHashSet();

        for (String str : filterStrings)
        {
            int index = str.indexOf('[');
            String name = index > 0 ? str.substring(0, index) : str;
            ResourceLocation key;

            try
            {
                key = new ResourceLocation(name);
            }
            catch (Exception e)
            {
                TellMe.logger.warn("Invalid block name '{}'", str);
                throw INVALID_NAME_EXCEPTION.create(str);
            }

            @SuppressWarnings("deprecation")
            Optional<Block> block = Registry.BLOCK.getOptional(key);

            if (block.isPresent())
            {
                // First get all valid states for this block
                Collection<BlockState> states = block.get().getStateDefinition().getPossibleStates();
                // Then get the list of properties and their values in the given name (if any)
                List<Pair<String, String>> props = BlockInfo.getProperties(str);

                // ... and then filter the list of all valid states by the provided properties and their values
                if (props.isEmpty() == false)
                {
                    for (Pair<String, String> pair : props)
                    {
                        states = BlockInfo.getFilteredStates(states, pair.getLeft(), pair.getRight());
                    }
                }

                filters.addAll(states);
            }
            else
            {
                TellMe.logger.warn("Invalid block name '{}'", str);
                throw INVALID_NAME_EXCEPTION.create(str);
            }
        }

        return filters;
    }

    @Override
    public void processChunks(Collection<LevelChunk> chunks, BlockPos posMin, BlockPos posMax)
    {
        final long timeBefore = System.nanoTime();
        Set<BlockState> filters = this.filters;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int count = 0;

        for (LevelChunk chunk : chunks)
        {
            if (this.data.size() > 100000)
            {
                TellMe.logger.warn("Over 100 000 blocks found already, aborting...");
                break;
            }

            ChunkPos chunkPos = chunk.getPos();
            final String dim = WorldUtils.getDimensionId(chunk.getLevel());
            final int xMin = Math.max(chunkPos.x << 4, posMin.getX());
            final int zMin = Math.max(chunkPos.z << 4, posMin.getZ());
            final int xMax = Math.min((chunkPos.x << 4) + 15, posMax.getX());
            final int zMax = Math.min((chunkPos.z << 4) + 15, posMax.getZ());
            final int yMin = Math.max(chunk.getMinBuildHeight(), posMin.getY());
            final int yMax = Math.min(chunk.getHighestSectionPosition() + 15, posMax.getY());

            for (int z = zMin; z <= zMax; ++z)
            {
                for (int x = xMin; x <= xMax; ++x)
                {
                    for (int y = yMin; y <= yMax; ++y)
                    {
                        pos.set(x, y, z);
                        BlockState state = chunk.getBlockState(pos);

                        if (filters.contains(state))
                        {
                            ResourceLocation name = ForgeRegistries.BLOCKS.getKey(state.getBlock());
                            this.data.add(LocationData.of(name.toString(), dim, new Vec3(x, y, z)));
                            count++;
                        }
                    }
                }
            }
        }

        final long timeAfter = System.nanoTime();
        TellMe.logger.info(String.format(Locale.US, "Located %d blocks in %d chunks in %.3f seconds.",
                                         count, chunks.size(), (timeAfter - timeBefore) / 1000000000D));
    }
}
