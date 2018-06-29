package me.pvpeon.payday;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Main extends org.bukkit.plugin.java.JavaPlugin implements org.bukkit.event.Listener
{
  public static ChatColor BOLD = ChatColor.BOLD;
  public static String PREFIX = ChatColor.translateAlternateColorCodes('&', "&7[&a&lPayDay&7]&r ");
  private static Economy econ = null;
  
  public int Price;
  
  ConsoleCommandSender CCS = Bukkit.getServer().getConsoleSender();
  
  public Main() {}
  
  public void onEnable() { getServer().getPluginManager().registerEvents(this, this);
  	CCS.sendMessage(colorize(PREFIX + "has been Enabled!\033[0m"));
  	this.Price = getConfig().getInt("Price", 100);
    getConfig().options().copyDefaults(true);
    reloadConfig();
    saveConfig();
    if (!setupEconomy()) {
    	CCS.sendMessage(String.format("[%s] - Disabled due to no Vault dependency found!", new Object[] { getDescription().getName() }));
    	getServer().getPluginManager().disablePlugin(this);
    	return;
    }
  }
  
  public void onDisable()
  {
	  CCS.sendMessage(colorize(PREFIX + " has been Disabled!\033[0m"));
  }
  
  private boolean setupEconomy() {
    if (getServer().getPluginManager().getPlugin("Vault") == null) {
      return false;
    }
    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp == null) {
      return false;
    }
    econ = (Economy)rsp.getProvider();
    return econ != null;
  }
  
  
  @SuppressWarnings("deprecation")
public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
  {
    if ((sender instanceof Player)) {
      Player player = (Player)sender;
      
      if (cmd.getName().equalsIgnoreCase("pdreload")) {
          if (!player.hasPermission("payday.reload")) {
        	  String message = this.getConfig().getString("No Permission");
          	  message = message.replace("{player}", player.getName());
          	
          	  player.sendMessage(colorize(PREFIX + message));
          	  return true;
          }
          getConfig().getDefaults();
          getConfig().options().copyDefaults(true);
          reloadConfig();
          saveConfig();
          player.sendMessage(colorize(PREFIX + "Config reloaded!"));
          CCS.sendMessage(colorize(PREFIX + "Config reloaded!"));
      }
      
      if (cmd.getName().equalsIgnoreCase("payday")) {
        if (!player.hasPermission("payday.allow")) {
        	String message = this.getConfig().getString("No Permission");
        	message = message.replace("{player}", player.getName());
        	
        	player.sendMessage(colorize(PREFIX + message));
        	return true;
        }

        if(args.length == 0) {
        	Double balance = Double.valueOf(econ.getBalance(player.getName()));
        	this.Price = getConfig().getInt("Price", 100);
        	
            if (balance.doubleValue() >= Price) {
            	
            	String message = this.getConfig().getString("Bought Day");
            	message = message.replace("{player}", player.getName());
            	
            	getServer().broadcastMessage(colorize(PREFIX + message));
            	
            	econ.withdrawPlayer(player.getName(), Price);
              
            	for(World world : Bukkit.getServer().getWorlds()){
            		world.setTime(1000);
            	}
            } else {
            	
            	String message = this.getConfig().getString("Not Enough Money");
            	message = message.replace("{player}", player.getName());
            	
            	player.sendMessage(colorize(PREFIX + message));
            }
        }else {
        	player.sendMessage(colorize(PREFIX + "&cThis command doesn't take any arguments!"));
        }
        
        
      }
    }else {
    	if (cmd.getName().equalsIgnoreCase("pdreload")) {
    		getConfig().getDefaults();
    		getConfig().options().copyDefaults(true);
            reloadConfig();
            saveConfig();
            System.out.println(PREFIX + "Config reloaded!");
        }
    	
    	if (cmd.getName().equalsIgnoreCase("payday")) {
    		CCS.sendMessage(colorize(PREFIX + "This command is only supported for players. Please refer to using the command &aday&r in console or if you are trying to reload the config use &apdreload&r"));
    	}
    }
    return true;
  }
  
  private static String colorize(String string) {
	    return ChatColor.translateAlternateColorCodes('&', string);
	}
  
  public static Economy getEcononomy() {
    return econ;
  }
}
