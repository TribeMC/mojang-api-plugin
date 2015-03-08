package v3lop5.mojangapi.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import v3lop5.mojangapi.api.MojangAPI;

public class CMD_MojangAPI implements CommandExecutor {
	MojangAPI api;

	public CMD_MojangAPI(MojangAPI api) {
		this.api = api;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command arg1, String arg2,
			String[] args) {

		if (args.length == 0) {
			if (!api.hasPerm(cs, "mojangapi.help"))
				return true;
			api.sendHelp(cs);
		} else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("help")) {
				if (!api.hasPerm(cs, "mojangapi.help"))
					return true;
				api.sendHelp(cs);
			} else if (args[0].equalsIgnoreCase("info")) {
				if (!api.hasPerm(cs, "mojangapi.info.plugin"))
					return true;

				api.sendInfo(cs);
			} else {
				if (!api.hasPerm(cs, "mojangapi.info.name"))
					return true;

				api.getProfileInformationforName(cs, args[0]);
			}
		} else if (args.length >= 2) {
			if (args[0].equalsIgnoreCase("names")) {
				if (!api.hasPerm(cs, "mojangapi.info.names"))
					return true;

				int i = 2;
				String temp = args[1];
				while (args.length > i) {
					temp += " " + args[i];
					i++;
				}
				if (api.isUUID(temp)) {
					api.getNamesForUUID(cs, temp);
				} else {
					api.getNamesForName(cs, temp);
				}
			} else if (args[0].equalsIgnoreCase("converttime")
					|| args[0].equalsIgnoreCase("time")) {
				if (!api.hasPerm(cs, "mojangapi.info.time"))
					return true;

				api.convertTime(cs, args[1]);
			} else if (args[0].equalsIgnoreCase("info")) {
				if (!api.hasPerm(cs, "mojangapi.info.uuid"))
					return true;
				int i = 2;
				String temp = args[1];
				while (args.length > i) {
					temp += " " + args[i];
					i++;
				}
				if (api.isUUID(temp)) {
					api.getProfileInformationforUUID(cs, temp);
				} else {
					api.getProfileInformationforName(cs, temp);
				}
			} else if (args[0].equalsIgnoreCase("name")) {
				if (!api.hasPerm(cs, "mojangapi.info.name"))
					return true;

				api.getProfileInformationforName(cs, args[1]);
			} else if (args[0].equalsIgnoreCase("spawn")) {
				if (!api.hasPerm(cs, "mojangapi.spawn"))
					return true;

				int i = 2;
				String temp = args[1];
				while (args.length > i) {
					temp += " " + args[i];
					i++;
				}
				if (api.isUUID(temp)) {
					api.spawnPlayerWithUUID(cs, temp, "SkinView");
				} else {
					api.spawnPlayerWithUUID(cs, api.getUUID(temp), temp);
				}
			} else {
				if (!api.hasPerm(cs, "mojangapi.help"))
					return true;
				api.sendHelp(cs);
			}
		}

		return true;
	}
}
