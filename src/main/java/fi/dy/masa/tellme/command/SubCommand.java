package fi.dy.masa.tellme.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;

public abstract class SubCommand implements ISubCommand
{
    protected ArrayList<String> subSubCommands = new ArrayList<String>();

    public SubCommand()
    {
        this.subSubCommands.add("help");
    }

    @Override
    public List<String> getSubCommands()
    {
        return this.subSubCommands;
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args)
    {
        if (args.length == 2 || (args.length == 3 && args[1].equals("help")))
        {
            return CommandBase.getListOfStringsMatchingLastWord(args, this.getSubCommands());
        }

        return null;
    }

    @Override
    public String getSubCommandsHelpString()
    {
        StringBuilder str = new StringBuilder(I18n.translateToLocal("info.subcommands.available") + ": ");

        for (int i = 0; i < this.subSubCommands.size() - 1; ++i)
        {
            str.append(this.subSubCommands.get(i) + ", ");
        }
        if (this.subSubCommands.size() >= 1)
        {
            str.append(this.subSubCommands.get(this.subSubCommands.size() - 1));
            return str.toString();
        }

        return "";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        // "/tellme command"
        if (args.length == 1)
        {
            sender.addChatMessage(new TextComponentString(this.getSubCommandsHelpString()));
        }
        // "/tellme command [help|unknown]"
        else if (args.length == 2)
        {
            if (args[1].equals("help"))
            {
                sender.addChatMessage(new TextComponentString(this.getSubCommandsHelpString()));
            }
            else if (this.subSubCommands.contains(args[1]) == false)
            {
                throw new WrongUsageException(I18n.translateToLocal("info.command.unknown.subcommand") + " '" + args[1] + "'", new Object[0]);
            }
        }
        // "/tellme command help subsubcommand"
        else if (args.length == 3 && args[1].equals("help"))
        {
            if (args[2].equals("help"))
            {
                sender.addChatMessage(new TextComponentString(I18n.translateToLocal("info.subcommands.help")));
            }
            else if (this.subSubCommands.contains(args[2]) == true)
            {
                sender.addChatMessage(new TextComponentString(I18n.translateToLocal("info.subcommand." + args[0] + ".help." + args[2])));
            }
            else
            {
                throw new WrongUsageException(I18n.translateToLocal("info.subcommands.help.unknown") + " " + args[3], new Object[0]);
            }
        }
    }
}
