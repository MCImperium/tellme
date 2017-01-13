package fi.dy.masa.tellme.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import com.google.common.base.Optional;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import fi.dy.masa.tellme.TellMe;
import fi.dy.masa.tellme.command.ClientCommandTellme;
import fi.dy.masa.tellme.config.Configs;
import fi.dy.masa.tellme.datadump.BlockDump;
import fi.dy.masa.tellme.datadump.ItemDump;

public class ClientProxy extends CommonProxy
{
    @Override
    public void getCurrentBiomeInfoClientSide(EntityPlayer player, Biome bgb)
    {
        BlockPos pos = player.getPosition();
        String pre = TextFormatting.YELLOW.toString();
        String rst = TextFormatting.RESET.toString() + TextFormatting.WHITE.toString();

        // These are client-side only:
        player.sendMessage(new TextComponentString(String.format("Grass color: %s0x%08X (%d)%s",
                pre, bgb.getModdedBiomeGrassColor(bgb.getGrassColorAtPos(pos)), bgb.getModdedBiomeGrassColor(bgb.getGrassColorAtPos(pos)), rst)));
        player.sendMessage(new TextComponentString(String.format("Foliage color: %s0x%08X (%d)%s",
                pre, bgb.getModdedBiomeFoliageColor(bgb.getFoliageColorAtPos(pos)), bgb.getModdedBiomeFoliageColor(bgb.getFoliageColorAtPos(pos)), rst)));
    }

    @Override
    public void getDataForBlockSubtypes(Block block, ResourceLocation rl, BlockDump blockDump)
    {
        Item item = Item.getItemFromBlock(block);

        if (item != null)
        {
            List<ItemStack> stacks = new ArrayList<ItemStack>();
            CreativeTabs tab = block.getCreativeTabToDisplayOn();
            block.getSubBlocks(item, tab, stacks);
            boolean subtypes = stacks.size() > 1;

            for (ItemStack stack : stacks)
            {
                blockDump.addData(block, rl, true, subtypes, stack);
            }
        }
        else
        {
            blockDump.addData(block, rl, false, false, null);
        }
    }

    @Override
    public void getDataForItemSubtypes(Item item, ResourceLocation rl, ItemDump itemDump)
    {
        if (item.getHasSubtypes())
        {
            for (CreativeTabs tab : item.getCreativeTabs())
            {
                List<ItemStack> stacks = new ArrayList<ItemStack>();
                item.getSubItems(item, tab, stacks);

                for (ItemStack stack : stacks)
                {
                    // FIXME: Ignore identical duplicate entries from different tabs...
                    itemDump.addData(item, rl, true, stack);
                }
            }
        }
        else
        {
            itemDump.addData(item, rl, false, new ItemStack(item, 1, 0));
        }
    }

    @Override
    public void getExtendedBlockStateInfo(World world, IBlockState state, BlockPos pos, List<String> lines)
    {
        try
        {
            state = state.getBlock().getExtendedState(state, world, pos);
        }
        catch (Exception e)
        {
            TellMe.logger.error("getFullBlockInfo(): Exception while calling getExtendedState() on the block");
        }

        if (state instanceof IExtendedBlockState)
        {
            lines.add("IExtendedBlockState properties:");

            IExtendedBlockState extendedState = (IExtendedBlockState) state;
            UnmodifiableIterator<Entry<IUnlistedProperty<?>, Optional<?>>> iterExt = extendedState.getUnlistedProperties().entrySet().iterator();

            while (iterExt.hasNext() == true)
            {
                Entry<IUnlistedProperty<?>, Optional<?>> entry = iterExt.next();
                lines.add(entry.getKey().toString() + ": " + entry.getValue().toString());
            }
        }
    }

    @Override
    public void registerClientCommand()
    {
        TellMe.logger.info("Registering the client-side command");
        ClientCommandHandler.instance.registerCommand(new ClientCommandTellme());
    }

    @Override
    public void registerEventHandlers()
    {
        MinecraftForge.EVENT_BUS.register(new Configs());
    }
}
