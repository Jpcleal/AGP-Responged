package agp.andwhat5.commands.gyms;

import java.util.List;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;

import agp.andwhat5.AGP;
import agp.andwhat5.Utils;
import agp.andwhat5.commands.Command;
import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

/**
 * Created by nickd on 4/30/2017.
 */
public class EditGym extends Command
{
	public EditGym()
	{
		super("editgym", "/editgym <gym> <name:(name) | badge:(badge) | require:(gym) | rules:(rules) | level:(level) | money:(money) | items:(item1,item2)>");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(args.length >= 2){
			GymStruc gym = Utils.getGym(args[0]);
			if(gym == null)
			{
				sender.sendMessage(Utils.toText("&7That gym does not exist!", true));
				return;
			}
			// Parse options
			for(int i = 1; i < args.length; i++)
			{
				if(args[i].toLowerCase().startsWith("rules:")&&!args[i].toLowerCase().equalsIgnoreCase("rules:"))
				{
					gym.Rules = "";
					int index = 0;
					for(String s : args)
					{
						if(index != 0)
						{
							if(s.startsWith("rules:"))
							{
								gym.Rules += s.split("rules:")[1] + " ";
							}
							else
							{
								gym.Rules += s + " ";
							}
						}
						index++;
					}
					if(AGPConfig.Storage.storageType.equalsIgnoreCase("flatfile"))
					{
						Utils.editGym(gym);
						AGP.getInstance().getStorage().saveData(DataStruc.gcon);
					}
					else
						Utils.addGym(gym);
					sender.sendMessage(Utils.toText("&7Successfully updated the gyms rules!", true));
					return;
				}
				else
				if (args[i].toLowerCase().startsWith("require:")&&!args[i].toLowerCase().equalsIgnoreCase("require:"))
				{
					if(Utils.gymExists(args[i].substring(8)))
					{
						GymStruc g = Utils.getGym(args[i].substring(8));
						if(args[0].equalsIgnoreCase(g.Name))
						{
							sender.sendMessage(Utils.toText("&7You can not set this gyms requirement to itself!", true));
							return;
						}
						gym.Requirement = g.Name;
						sender.sendMessage(Utils.toText("&7Successfully changed the requirement of the &b"+gym.Name+"&7 gym to the &b"+g.Name+"&7 gym!", true));
					}
					else
					{
						sender.sendMessage(Utils.toText("&7The &b"+args[i].substring(8)+" &7gym does not exist!", true));
					}
					
				}
				else if (args[i].toLowerCase().startsWith("level:")&&!args[i].toLowerCase().equalsIgnoreCase("level:"))
				{
					int levelcap = Integer.valueOf(args[i].substring(6));
					if(levelcap >= 0 && levelcap <= PixelmonConfig.maxLevel)
					{
						gym.LevelCap = Integer.valueOf(args[i].substring(6));
						sender.sendMessage(Utils.toText("&7Successfully changed the level cap of the &b"+gym.Name+"&7 gym to &b"+gym.LevelCap+"&7!", true));
					}
					else
					{
						sender.sendMessage(Utils.toText("&7The level you provided is out of the level cap range, &b0-"+PixelmonConfig.maxLevel+"&7!", true));
					}
				}
				else if(args[i].toLowerCase().startsWith("money:")&&!args[i].toLowerCase().equalsIgnoreCase("money:")){
					gym.Money = Integer.parseInt(args[i].substring(6));
					sender.sendMessage(Utils.toText("&7Successfully changed the reward money of the &b"+gym.Name+"&7 gym to &b"+gym.Money+"&7!", true));
				}
				else if(args[i].toLowerCase().startsWith("name:")&&!args[i].toLowerCase().equalsIgnoreCase("name:"))
				{
					if(!Utils.gymExists(args[i].substring(5)))
					{
						gym.Name = args[i].substring(5);
						sender.sendMessage(Utils.toText("&7Successfully changed the name of the &b"+args[0]+"&7 gym to &b"+gym.Name+"&7!", true));
					}
					else
					{
						sender.sendMessage(Utils.toText("&7That gym already exists!", true));
					}
				}
				else if(args[i].toLowerCase().startsWith("badge:")&&!args[i].toLowerCase().equalsIgnoreCase("badge:"))
				{
					gym.Badge = args[i].substring(6);
					sender.sendMessage(Utils.toText("&7Successfully changed the badge of the &b"+gym.Name+"&7 gym to &b"+gym.Badge+"&7!", true));

				}
				else if(args[i].toLowerCase().startsWith("items:")&&!args[i].toLowerCase().equalsIgnoreCase("items:"))
				{
					if(args[i].substring(6).split(",").length == 2)
					{
						gym.Items.clear();
						gym.Items.add(args[i].substring(5).split(",")[0]);
						gym.Items.add(args[i].substring(5).split(",")[1]);
						sender.sendMessage(Utils.toText("&7You have successfully changed the two items of the &b"+gym.Name+" &7Gym to &b"
								+args[i].substring(5).split(",")[0].replace("null", "nothing")+" &7and &b"+args[i].substring(5).split(",")[1].replace("null", "nothing")
								+"&7!", true));
					}
					else
					{
						sender.sendMessage(Utils.toText("&7Incorrect formatting of item values. Example: &b/EditGym Water items:pixelmon:rare_candy,null", true));
					}
				}
				else
				{
					super.sendUsage(sender);
				}
			}
			if(AGPConfig.Storage.storageType.equalsIgnoreCase("flatfile"))
			{
				Utils.editGym(gym);
				AGP.getInstance().getStorage().saveData(DataStruc.gcon);
			}
			else
				Utils.addGym(gym);
		}
		else
			super.sendUsage(sender);
	}
	
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, Utils.getGymNames(true));
		}
		else
		if(args.length == 2)
		{
			return getListOfStringsMatchingLastWord(args, Lists.newArrayList("money:", "require:", "rules:", "badge:", "name:", "items:", "level:", "rules:"));
		}
		return null;
	}
}