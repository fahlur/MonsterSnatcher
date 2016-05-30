package me.Fahlur.MonsterSnatcher;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.material.SpawnEgg;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


public class main extends JavaPlugin implements Listener {

	main plugin;
	Permission permission;
	PluginDescriptionFile pdfFile = this.getDescription();
	final FileConfiguration config = this.getConfig();

	
	@Override
	public void onEnable(){
		plugin = this;
		setupPermissions();
		loadConfiguration();
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
	}
	
	public void onDisable(){
	}
	
	
public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		

		if (cmd.getName().equalsIgnoreCase("monstersnatcher")){
			if (args.length == 1 && args[0].equalsIgnoreCase("reload")){
				if ((sender instanceof Player)){
					if (!permission.playerHas((Player) sender, "ms.reload")) {
					      sender.sendMessage(ChatColor.RED + "Sorry, you don't have permission to use this command.");
					      return true;
					}
				}
				reloadConfig();
				saveConfig();
				sender.sendMessage(ChatColor.RED + "[MonsterSnatcher]" + ChatColor.GRAY + ": " + ChatColor.GRAY + "Reloaded Configuration File!");
				Bukkit.getLogger().info("[MonsterSnatcher]: " + sender.getName() + " performed a configuration reload");
				return true;
			}
			sender.sendMessage(ChatColor.GREEN + "MonsterSnatcher");
			sender.sendMessage(ChatColor.AQUA + "Version: " + ChatColor.GOLD + pdfFile.getVersion());
			sender.sendMessage(ChatColor.AQUA + "Author(s): " + ChatColor.GOLD + pdfFile.getAuthors());
		}
		
		
		
		
		return true;
}
	
	
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void eggThrowEvent(EntityDamageEvent event){
		
		EntityDamageByEntityEvent damageEvent = null;
		
		Egg egg = null;
		
	    Entity entity = event.getEntity();
	    
	    Player player = null;
	    
		if (!(event instanceof EntityDamageByEntityEvent)) {
		    return;
		}
		
		damageEvent = (EntityDamageByEntityEvent)event;
		if (!(damageEvent.getDamager() instanceof Egg)) {
			return;
		}
		
		egg = (Egg)damageEvent.getDamager();
		player = (Player) egg.getShooter();
		
		if (entity instanceof Tameable && (((Tameable)entity).isTamed())){
			player.sendMessage(ChatColor.RED + "[MonsterSnatcher] " + ChatColor.GRAY + "You are unable to capture tamed mobs!");
			return;
		}
		
		if (!(egg.getShooter() instanceof Player)){
			return;
		}
		Location loc = entity.getLocation();
		
		
		if (player.hasPermission("ms.catchable")){
		
			if (!(config.contains("mobs."+entity.getType()))){
				player.sendMessage(ChatColor.RED + "[MonsterSnatcher] " + ChatColor.GRAY + "The mob you are trying to catch is uncatchable!");
				return;
			}
			double livingEntity = config.getDouble("mobs."+entity.getType());
			boolean rand = randomChance(livingEntity, player, entity);
		
			if (livingEntity == 0 && !isOp(player)){
				player.sendMessage(ChatColor.RED + "[MonsterSnatcher] " + ChatColor.GRAY + "The mob you are trying to catch is uncatchable!");
				return;
			}
			
			if (rand && !isOp(player)){
				entity.remove();
				SpawnEgg spawnEgg = new SpawnEgg();
				spawnEgg.setSpawnedType(entity.getType());
				loc.getWorld().dropItemNaturally(loc, spawnEgg.toItemStack(1));
			}
			
			if (isOp(player)){
				entity.remove();
				SpawnEgg spawnEgg = new SpawnEgg();
				spawnEgg.setSpawnedType(entity.getType());
				loc.getWorld().dropItemNaturally(loc, spawnEgg.toItemStack(1));
			}
			
		}
		
		
		
	}
	
	public boolean isOp(Player player){
		if (!player.isOp()){
			return false;
		}
		if (!config.getBoolean("settings.opOverride")){
			return false;	
		}	
		return true;
	}
	
	public void loadConfiguration(){
		config.options().copyDefaults(true);
		this.saveConfig();
	}
	
	public boolean randomChance(double catchChance, Player player, Entity entity){
		double d = Math.random();
		if (d <= catchChance)
        {
			player.getLocation().getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1.0F);	
			ParticleEffect.VILLAGER_HAPPY.display(0.4F, 0.4F, 0.4F, 1.0F, 25, entity.getLocation().add(0.0D, 1.0D, 0.0D), 100.0D);
			return true;
        }
		return false;
	}
	
	
	 private Boolean setupPermissions()
	  {
	    RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
	    if (permissionProvider != null) {
	      permission = (Permission)permissionProvider.getProvider();
	    }
	    if (permission != null) {
	      return Boolean.valueOf(true);
	    }
	    return Boolean.valueOf(false);
	  }
	
	
}
