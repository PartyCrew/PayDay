package me.Pvpeon.Payday;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin{
	
	FileConfiguration config = this.getConfig();
	private static Economy econ = null;
	public static String PREFIX = "&7[&a&lPayDay&7]&r ";
	
	private double Price;
	
	ConsoleCommandSender CCS = Bukkit.getServer().getConsoleSender();
	
	@Override
	public void onEnable() {
		CCS.sendMessage(colorize(PREFIX + "has been enabled"));
		loadConfig();
		if (!setupEconomy()) {
	    	CCS.sendMessage(String.format("[%s] - Disabled due to no Vault dependency found!", new Object[] { getDescription().getName() }));
	    	getServer().getPluginManager().disablePlugin(this);
	    	return;
	    }
	}
	
	@Override
	public void onDisable() {
		CCS.sendMessage(colorize(PREFIX + "has been disabled"));
		saveDefaultConfig();
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
	
	private void loadConfig() {
	    FileConfiguration cfg = getConfig();
	    cfg.options().copyDefaults(true);
	    saveDefaultConfig();
	    reloadConfig();
	  }
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if((sender instanceof Player)) {
			Player p = (Player)sender;
			
			if(cmd.getName().equalsIgnoreCase("pdreload")) {
				if(!p.hasPermission("payday.reload")) {
					String message = this.getConfig().getString("No Permission");
		        	message = message.replace("{player}", p.getName());
		        	
		        	p.sendMessage(colorize(PREFIX + message));
					return true;
				}
				loadConfig();
				p.sendMessage(colorize(PREFIX + "Successfully reloaded config.yml"));
			}
			
			if(cmd.getName().equalsIgnoreCase("payday")) {
				if(!p.hasPermission("payday.allow")) {
					String message = this.getConfig().getString("No Permission");
		        	message = message.replace("{player}", p.getName());
		        	
		        	p.sendMessage(colorize(PREFIX + message));
					return true;
				}
				if(args.length == 0) {
					Server server = getServer();
					
				    long time = server.getWorld(p.getWorld().getName()).getTime();

				    if(time > 0 && time < 12300) {
				        p.sendMessage(colorize(PREFIX + "&cYou can't buy day if it's already day!"));
				    } else {
				    	Double balance = Double.valueOf(econ.getBalance(p.getName()));
			        	Price = getConfig().getDouble("Price");
			        	
			        	if (balance.doubleValue() >= Price) {
			        		String message = this.getConfig().getString("Bought Day");
			            	message = message.replace("{player}", p.getName());
			            	
			            	getServer().broadcastMessage(colorize(PREFIX + message));
			            	
			            	econ.withdrawPlayer(p.getName(), Price);
			              
			            	for(World world : Bukkit.getServer().getWorlds()){
			            		world.setTime(1000);
			            	}
			        	}else {
			        		String message = this.getConfig().getString("Not Enough Money");
			            	message = message.replace("{player}", p.getName()).replace("{cost}", commas(Price));
			            	
			            	p.sendMessage(colorize(PREFIX + message));
			        	}
				    }
				}else {
					p.sendMessage(colorize(PREFIX + "&cThis command doesn't take any arguments!"));
				}
			}
		}else {
			if (cmd.getName().equalsIgnoreCase("pdreload")) {
				loadConfig();
				CCS.sendMessage(colorize(PREFIX + "Successfully reloaded config.yml"));
	        }
	    	
	    	if (cmd.getName().equalsIgnoreCase("payday")) {
	    		CCS.sendMessage(colorize(PREFIX + "This command is only supported for players. Please refer to using the command &aday&r in console or if you are trying to reload the config use &apdreload&r"));
	    	}
		}
		
		return true;
	}
	
	public String commas(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        String number = formatter.format(amount);
        return number;
    }
	
	private static String colorize(String string) {
	    return ChatColor.translateAlternateColorCodes('&', string);
	}
	
	public static Economy getEcononomy() {
		return econ;
	}
}
