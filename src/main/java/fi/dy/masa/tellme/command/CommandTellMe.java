package fi.dy.masa.tellme.command;

import java.util.Collections;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import fi.dy.masa.tellme.command.argument.BiomeArgument;
import fi.dy.masa.tellme.command.argument.BlockStateCountGroupingArgument;
import fi.dy.masa.tellme.command.argument.FileArgument;
import fi.dy.masa.tellme.command.argument.GroupingArgument;
import fi.dy.masa.tellme.command.argument.OutputFormatArgument;
import fi.dy.masa.tellme.command.argument.OutputTypeArgument;
import fi.dy.masa.tellme.command.argument.StringCollectionArgument;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.registries.DeferredRegister;

public class CommandTellMe
{
    public static void registerServerCommand(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        register(dispatcher, "tellme", 4);
    }

    public static void registerClientCommand(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        register(dispatcher, "ctellme", 0);
    }

    public static void registerArgumentTypes(DeferredRegister<ArgumentTypeInfo<?, ?>> argTypeRegistry)
    {
        argTypeRegistry.register("biome", () -> ArgumentTypeInfos.registerByClass(BiomeArgument.class, SingletonArgumentInfo.contextFree(BiomeArgument::create)));
        argTypeRegistry.register("block_grouping", () -> ArgumentTypeInfos.registerByClass(BlockStateCountGroupingArgument.class, SingletonArgumentInfo.contextFree(BlockStateCountGroupingArgument::create)));
        argTypeRegistry.register("file", () -> ArgumentTypeInfos.registerByClass(FileArgument.class, SingletonArgumentInfo.contextFree(FileArgument::createEmpty)));
        argTypeRegistry.register("grouping", () -> ArgumentTypeInfos.registerByClass(GroupingArgument.class, SingletonArgumentInfo.contextFree(GroupingArgument::create)));
        argTypeRegistry.register("output_format", () -> ArgumentTypeInfos.registerByClass(OutputFormatArgument.class, SingletonArgumentInfo.contextFree(OutputFormatArgument::create)));
        argTypeRegistry.register("output_type", () -> ArgumentTypeInfos.registerByClass(OutputTypeArgument.class, SingletonArgumentInfo.contextFree(OutputTypeArgument::create)));
        argTypeRegistry.register("string_collection", () -> ArgumentTypeInfos.registerByClass(StringCollectionArgument.class, SingletonArgumentInfo.contextFree(() -> StringCollectionArgument.create(Collections::emptyList, ""))));
    }

    protected static void register(CommandDispatcher<CommandSourceStack> dispatcher, String baseCommandName, final int permissionLevel)
    {
        dispatcher.register(
                Commands.literal(baseCommandName)
                    .requires((src) -> src.hasPermission(permissionLevel))
                    .then(SubCommandBatchRun.registerSubCommand(dispatcher))
                    .then(SubCommandBiome.registerSubCommand(dispatcher))
                    .then(SubCommandBiomeLocate.registerSubCommand(dispatcher))
                    .then(SubCommandBiomeStats.registerSubCommand(dispatcher))
                    .then(SubCommandBlockStats.registerSubCommand(dispatcher))
                    .then(SubCommandCopyToClipboard.registerSubCommand(dispatcher))
                    .then(SubCommandDump.registerSubCommand(dispatcher))
                    .then(SubCommandDumpJson.registerSubCommand(dispatcher))
                    .then(SubCommandDumpPackdevUtilsSnippet.registerSubCommand(dispatcher))
                    .then(SubCommandEntityData.registerSubCommand(dispatcher))
                    .then(SubCommandHolding.registerSubCommand(dispatcher))
                    .then(SubCommandLoaded.registerSubCommand(dispatcher))
                    .then(SubCommandLocate.registerSubCommand(dispatcher))
                    .then(SubCommandLookingAt.registerSubCommand(dispatcher))
        );
    }
}
