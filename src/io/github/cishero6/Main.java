package io.github.cishero6;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin {
	static Location center;
	static boolean beforeGameCountdown;
	static boolean[] bedExists = new boolean[8]; 
	static Player[][] Teams = new Player[8][4];
	static Player[] players = new Player[32];
	static int curPlayers;
	static int maxPlayers = 2;
	static int maxTeams = 8;
	static boolean gameCreated;
	static boolean gameStarted;	
	static Inventory chooseTeamInventory;
	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(new BedHuntEvents(this),this);
		this.getCommand("game").setExecutor(new Game(this));
		this.getCommand("set").setExecutor(new Game(this));
		for(int i=0;i<8;i++) {
			for(int j=0;j<4;j++) {
				Teams[i][j] = null;
			}
		}
	}
	@Override
	public void onDisable() {
		
	}
	public static int getTeamID(Player player) {
		for(int i = 0 ; i < maxTeams;i++) {
			for (int j = 0 ; j < maxPlayers;j++) {
				if (player == Teams[i][j]) return i;
			}
		}
		return -1;
	}
	public static boolean isPlaying(Player player) {
		for(int i = 0;i< curPlayers;i++) {
			if(player == players[i]) return true;
		}
		return false;
	}
	public static boolean playerJoinTeam(Player player, int teamID) {
			for (int i = 0; i < maxPlayers; i++) {
				if (Teams[teamID][i] == player) {
					player.sendMessage(ChatColor.RED + "You are already in this team!");
					player.closeInventory();
					return false;
				}
				if (Teams[teamID][i] == null) {
					Teams[teamID][i] = player;
					ItemStack armor = new ItemStack(Material.LEATHER_HELMET);
					LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
					switch (teamID) {
					case 0:
						player.sendMessage(ChatColor.GREEN + "You joined Red Team!" );
						armor = new ItemStack(Material.LEATHER_HELMET);
						meta = (LeatherArmorMeta) armor.getItemMeta();
						meta.setColor(Color.RED);
						meta.setUnbreakable(true);
						armor.setItemMeta(meta);
						player.getInventory().setHelmet(armor);
						armor = new ItemStack(Material.LEATHER_CHESTPLATE);
						meta = (LeatherArmorMeta) armor.getItemMeta();
						meta.setColor(Color.RED);
						meta.setUnbreakable(true);
						armor.setItemMeta(meta);
						player.getInventory().setChestplate(armor);
						player.closeInventory();
						return true;
					case 1:
						player.sendMessage(ChatColor.GREEN + "You joined Blue Team!" );
						armor = new ItemStack(Material.LEATHER_HELMET);
						meta = (LeatherArmorMeta) armor.getItemMeta();
						meta.setColor(Color.BLUE);
						meta.setUnbreakable(true);
						armor.setItemMeta(meta);
						player.getInventory().setHelmet(armor);
						armor = new ItemStack(Material.LEATHER_CHESTPLATE);
						meta = (LeatherArmorMeta) armor.getItemMeta();
						meta.setColor(Color.BLUE);
						meta.setUnbreakable(true);
						armor.setItemMeta(meta);
						player.getInventory().setChestplate(armor);
						player.closeInventory();
						return true;
					case 2:
						player.sendMessage(ChatColor.GREEN + "You joined Green Team!" );
						armor = new ItemStack(Material.LEATHER_HELMET);
						meta = (LeatherArmorMeta) armor.getItemMeta();
						meta.setColor(Color.GREEN);
						meta.setUnbreakable(true);
						armor.setItemMeta(meta);
						player.getInventory().setHelmet(armor);
						armor = new ItemStack(Material.LEATHER_CHESTPLATE);
						meta = (LeatherArmorMeta) armor.getItemMeta();
						meta.setColor(Color.GREEN);
						meta.setUnbreakable(true);
						armor.setItemMeta(meta);
						player.getInventory().setChestplate(armor);
						player.closeInventory();
						return true;
					case 3:
						player.sendMessage(ChatColor.GREEN + "You joined Yellow Team!" );
						armor = new ItemStack(Material.LEATHER_HELMET);
						meta = (LeatherArmorMeta) armor.getItemMeta();
						meta.setColor(Color.YELLOW);
						meta.setUnbreakable(true);
						armor.setItemMeta(meta);
						player.getInventory().setHelmet(armor);
						armor = new ItemStack(Material.LEATHER_CHESTPLATE);
						meta = (LeatherArmorMeta) armor.getItemMeta();
						meta.setColor(Color.YELLOW);
						meta.setUnbreakable(true);
						armor.setItemMeta(meta);
						player.getInventory().setChestplate(armor);
						player.closeInventory();
						return true;
					case 4:
						player.sendMessage(ChatColor.GREEN + "You joined Magenta Team!" );
						armor = new ItemStack(Material.LEATHER_HELMET);
						meta = (LeatherArmorMeta) armor.getItemMeta();
						meta.setColor(Color.PURPLE);
						meta.setUnbreakable(true);
						armor.setItemMeta(meta);
						player.getInventory().setHelmet(armor);
						armor = new ItemStack(Material.LEATHER_CHESTPLATE);
						meta = (LeatherArmorMeta) armor.getItemMeta();
						meta.setColor(Color.PURPLE);
						meta.setUnbreakable(true);
						armor.setItemMeta(meta);
						player.getInventory().setChestplate(armor);
						player.closeInventory();
						return true;
					case 5:
						player.sendMessage(ChatColor.GREEN + "You joined Black Team!" );
						armor = new ItemStack(Material.LEATHER_HELMET);
						meta = (LeatherArmorMeta) armor.getItemMeta();
						meta.setColor(Color.BLACK);
						meta.setUnbreakable(true);
						armor.setItemMeta(meta);
						player.getInventory().setHelmet(armor);
						armor = new ItemStack(Material.LEATHER_CHESTPLATE);
						meta = (LeatherArmorMeta) armor.getItemMeta();
						meta.setColor(Color.BLACK);
						meta.setUnbreakable(true);
						armor.setItemMeta(meta);
						player.getInventory().setChestplate(armor);
						player.closeInventory();
						return true;
					case 6:
						player.sendMessage(ChatColor.GREEN + "You joined White Team!" );
						armor = new ItemStack(Material.LEATHER_HELMET);
						meta = (LeatherArmorMeta) armor.getItemMeta();
						meta.setColor(Color.WHITE);
						meta.setUnbreakable(true);
						armor.setItemMeta(meta);
						player.getInventory().setHelmet(armor);
						armor = new ItemStack(Material.LEATHER_CHESTPLATE);
						meta = (LeatherArmorMeta) armor.getItemMeta();
						meta.setColor(Color.WHITE);
						meta.setUnbreakable(true);
						armor.setItemMeta(meta);
						player.getInventory().setChestplate(armor);
						player.closeInventory();
						return true;
					case 7:
						player.sendMessage(ChatColor.GREEN + "You joined Cyan Team!" );
						armor = new ItemStack(Material.LEATHER_HELMET);
						meta = (LeatherArmorMeta) armor.getItemMeta();
						meta.setColor(Color.AQUA);
						meta.setUnbreakable(true);
						armor.setItemMeta(meta);
						player.getInventory().setHelmet(armor);
						armor = new ItemStack(Material.LEATHER_CHESTPLATE);
						meta = (LeatherArmorMeta) armor.getItemMeta();
						meta.setColor(Color.AQUA);
						meta.setUnbreakable(true);
						armor.setItemMeta(meta);
						player.getInventory().setChestplate(armor);
						player.closeInventory();
						return true;
					}
				}
			}
		player.closeInventory();
		player.sendMessage(ChatColor.RED + "This team is full!");
		return false;
	}
	public static int playerJoinRandomTeam(Player player) {
		Random rand = new Random();
		int team;
		do {
		team = rand.nextInt(maxTeams);
		} while(!playerJoinTeam(player,team));
		return team;
	}
	public static void createTeamSelectorInv() {
		chooseTeamInventory = Bukkit.createInventory(null, 27, ChatColor.GOLD + "" + ChatColor.BOLD + "Select Team!");
		ItemStack item = new ItemStack(Material.RED_WOOL);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		
		
		//RED TEAM
		item.setType(Material.RED_WOOL);
		meta.setDisplayName(ChatColor.RED + "Red Team"); //0
		lore.add(ChatColor.ITALIC + "" + ChatColor.GRAY + getPlayersInTeam(0)+ "/" + maxPlayers);
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Click to join!");
		meta.setLore(lore);
		item.setItemMeta(meta);
		chooseTeamInventory.setItem(0, item);
		lore.clear();

		//BLUE TEAM
		item.setType(Material.BLUE_WOOL);
		meta.setDisplayName(ChatColor.BLUE + "Blue Team"); //1
		lore.add(ChatColor.ITALIC + "" + ChatColor.GRAY + getPlayersInTeam(1)+ "/" + maxPlayers);
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Click to join!");
		meta.setLore(lore);
		item.setItemMeta(meta);
		chooseTeamInventory.setItem(1, item);
		lore.clear();
		
		//RANDOM TEAM
		item.setType(Material.REDSTONE_BLOCK);
		meta.setDisplayName(ChatColor.RED + "Random Team"); 
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Click to join!");
		meta.setLore(lore);
		item.setItemMeta(meta);
		chooseTeamInventory.setItem(20, item);
		lore.clear();
		
		if (io.github.cishero6.Main.maxTeams >2) {
		//GREEN TEAM
		item.setType(Material.GREEN_WOOL);
		meta.setDisplayName(ChatColor.GREEN + "Green Team");//2
		lore.add(ChatColor.ITALIC + "" + ChatColor.GRAY + getPlayersInTeam(2)+ "/" + maxPlayers);
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Click to join!");
		meta.setLore(lore);
		item.setItemMeta(meta);
		chooseTeamInventory.setItem(2, item);
		lore.clear();
		}
		if (io.github.cishero6.Main.maxTeams >3) {
		//YELLOW TEAM
		item.setType(Material.YELLOW_WOOL);
		meta.setDisplayName(ChatColor.YELLOW + "Yellow Team");//3
		lore.add(ChatColor.ITALIC + "" + ChatColor.GRAY + getPlayersInTeam(3)+ "/" + maxPlayers);
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Click to join!");
		meta.setLore(lore);
		item.setItemMeta(meta);
		chooseTeamInventory.setItem(9, item);
		lore.clear();
		}
		if (io.github.cishero6.Main.maxTeams >4) {
		//MAGENTA TEAM
		item.setType(Material.MAGENTA_WOOL);
		meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Magenta Team");//4
		lore.add(ChatColor.ITALIC + "" + ChatColor.GRAY + getPlayersInTeam(4)+ "/" + maxPlayers);
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Click to join!");
		meta.setLore(lore);
		item.setItemMeta(meta);
		chooseTeamInventory.setItem(10, item);
		lore.clear();
		}
		if (io.github.cishero6.Main.maxTeams >5) {
		//BLACK TEAM
		item.setType(Material.BLACK_WOOL);
		meta.setDisplayName(ChatColor.DARK_GRAY	 + "Black Team");//5
		lore.add(ChatColor.ITALIC + "" + ChatColor.GRAY + getPlayersInTeam(5)+ "/" + maxPlayers);
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Click to join!");
		meta.setLore(lore);
		item.setItemMeta(meta);
		chooseTeamInventory.setItem(11, item);
		lore.clear();
		}
		if (io.github.cishero6.Main.maxTeams >6) {
		//WHITE TEAM
		item.setType(Material.WHITE_WOOL);
		meta.setDisplayName(ChatColor.WHITE + "White Team");//6
		lore.add(ChatColor.ITALIC + "" + ChatColor.GRAY + getPlayersInTeam(6)+ "/" + maxPlayers);
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Click to join!");
		meta.setLore(lore);
		item.setItemMeta(meta);
		chooseTeamInventory.setItem(18, item);
		lore.clear();
		}
		if (io.github.cishero6.Main.maxTeams ==8) {
		//CYAN TEAM
		item.setType(Material.CYAN_WOOL);
		meta.setDisplayName(ChatColor.AQUA + "Cyan Team");//7
		lore.add(ChatColor.ITALIC + "" + ChatColor.GRAY + getPlayersInTeam(7)+ "/" + maxPlayers);
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Click to join!");
		meta.setLore(lore);
		item.setItemMeta(meta);
		chooseTeamInventory.setItem(19, item);
		lore.clear();
		}
		item.setType(Material.BARRIER);
		lore.clear();
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Click to cancel!");
		meta.setDisplayName(ChatColor.RED + "Cancel choice");
		meta.setLore(lore);
		item.setItemMeta(meta);
		chooseTeamInventory.setItem(6, item);
		chooseTeamInventory.setItem(7, item);
		chooseTeamInventory.setItem(8, item);
		chooseTeamInventory.setItem(15, item);
		chooseTeamInventory.setItem(16, item);
		chooseTeamInventory.setItem(17, item);
		chooseTeamInventory.setItem(24, item);
		chooseTeamInventory.setItem(25, item);
		chooseTeamInventory.setItem(26, item);
	}
	
	public static int getPlayersInTeam(int teamID) {
		for(int i =0;i<maxPlayers;i++) {
			if(Teams[teamID][i] ==null) return i;
		}
		return maxPlayers;
	}
	public static boolean playerRemoveTeam(Player player) {
		for(int i =0;i< io.github.cishero6.Main.maxTeams;i++) {
			for(int j =0;j< io.github.cishero6.Main.maxPlayers;j++) {
				if (io.github.cishero6.Main.Teams[i][j] == player) {
					for(int p =j;p< io.github.cishero6.Main.maxPlayers-1;p++) {
						io.github.cishero6.Main.Teams[i][p] = io.github.cishero6.Main.Teams[i][p+1];
					}
					io.github.cishero6.Main.Teams[i][io.github.cishero6.Main.maxPlayers-1] = null;
					return true;
				}
			}
		}
		return false;
	}
}
	
