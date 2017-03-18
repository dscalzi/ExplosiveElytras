/*
 * ExplosiveElytras
 * Copyright (C) 2017 Daniel D. Scalzi
 * See License.txt for license information.
 */
package com.dscalzi.explosiveelytras.managers;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dscalzi.explosiveelytras.ExplosiveElytras;

public class MessageManager {

	private static boolean initialized;
	private static MessageManager instance;
	private static final char b = (char)8226;
	
	private ExplosiveElytras plugin;
	private final String prefix;
	private final ChatColor cPrimary;
	private final ChatColor cTrim;
	private final ChatColor cMessage;
	private final ChatColor cSuccess;
	private final ChatColor cError;
	
	private MessageManager(ExplosiveElytras plugin){
		this.plugin = plugin;
		this.cPrimary = ChatColor.GRAY;
		this.cTrim = ChatColor.DARK_RED;
		this.cMessage = ChatColor.RED;
		this.cSuccess = ChatColor.GREEN;
		this.cError = ChatColor.RED;
		this.prefix = cPrimary + "| " + cTrim + ChatColor.BOLD + "E" + cTrim + "xplosive" + ChatColor.BOLD + "E" + cTrim + "lytras" + cPrimary + " |" + ChatColor.RESET;
		
		this.plugin.getLogger().info(plugin.getDescription().getName() + " is loading.");
	}
	
	public static void initialize(ExplosiveElytras plugin){
		if(!initialized){
			instance = new MessageManager(plugin);
			initialized = true;
		}
	}
	
	public static MessageManager getInstance(){
		return MessageManager.instance;
	}
	
	/* Message Distribution */
	
	public void sendMessage(CommandSender sender, String message){
		sender.sendMessage(prefix + cMessage + " " + message);
	}
	
	public void sendSuccess(CommandSender sender, String message){
		sender.sendMessage(prefix + cSuccess + " " + message);
	}
	
	public void sendError(CommandSender sender, String message){
		sender.sendMessage(prefix + cError + " " + message);
	}
	
	public void sendGlobal(String message, String permission){
		for(Player p : plugin.getServer().getOnlinePlayers()){
			if(p.hasPermission(permission)){
				sendMessage(p, message);
			}
		}
	}
	
	/* Messages */
	
	public void helpMessage(CommandSender sender){
		final String listPrefix = cMessage + " "+b+" ";
		
		String header = prefix + cMessage + " Command List";
		List<String> cmds = new ArrayList<String>();
		
		cmds.add(listPrefix + "/ExplosiveElytras help " + ChatColor.RESET + "- View command list.");
		if(sender.hasPermission("explosiveelytras.reload"))
			cmds.add(listPrefix + "/ExplosiveElytras reload " + ChatColor.RESET + "- Reload the configuration.");
		cmds.add(listPrefix + "/ExplosiveElytras version " + ChatColor.RESET + "- View version information.");
		
		sender.sendMessage(header);
		for(String s : cmds) sender.sendMessage(s);
	}
	
	public void noPermission(CommandSender sender){
		sendError(sender, "You do not have permission to do this.");
	}
	
	public void reloadSuccessful(CommandSender sender){
		sendSuccess(sender, "Plugin successfully reloaded.");
	}
	
	public void reloadFailed(CommandSender sender){
		sendError(sender, "Plugin failed to reload, see console for details..");
	}
	
	public void versionMessage(CommandSender sender){
		sendMessage(sender, "ExplosiveElytras Version " + plugin.getDescription().getVersion() + 
				"\n" + cPrimary + "| " + cMessage + "Metrics" + cPrimary + " | " + cMessage + "TBD" + 
				"\n" + cPrimary + "| " + cMessage + "Source" + cPrimary + " | " + cMessage + "TBD");
	}
	
}
