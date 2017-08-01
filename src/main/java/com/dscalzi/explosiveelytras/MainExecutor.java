/*
 * ExplosiveElytras
 * Copyright (C) 2017 Daniel D. Scalzi
 * See License.txt for license information.
 */
package com.dscalzi.explosiveelytras;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.dscalzi.explosiveelytras.managers.ConfigManager;
import com.dscalzi.explosiveelytras.managers.MessageManager;

public class MainExecutor implements CommandExecutor, TabCompleter{

	private final MessageManager mm;
	
	public MainExecutor(){
		mm = MessageManager.getInstance();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(args.length > 0){
			
			if(args[0].equalsIgnoreCase("help")){
				mm.helpMessage(sender);
				return true;
			}
			
			if(args[0].equalsIgnoreCase("version")){
				this.cmdVersion(sender);
				return true;
			}
			
			if(args[0].equalsIgnoreCase("reload")){
				this.cmdReload(sender);
				return true;
			}
		}
		
		mm.helpMessage(sender);
		return false;
	}
	
	private void cmdVersion(CommandSender sender){
		mm.versionMessage(sender);
	}
	
	private void cmdReload(CommandSender sender){
		if(ConfigManager.reload()) {
			mm.reloadSuccessful(sender);
		}
		else mm.reloadFailed(sender);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> ret = new ArrayList<String>();
		
		if(args.length == 1){
			if("help".startsWith(args[0].toLowerCase()))
				ret.add("help");
			if("version".startsWith(args[0].toLowerCase()))
				ret.add("version");
			if(sender.hasPermission("explosiveelytras.reload") && "reload".startsWith(args[0].toLowerCase()))
				ret.add("reload");
		}
		
		return ret.size() > 0 ? ret : null;
	}

}
