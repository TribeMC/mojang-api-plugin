package v3lop5.mojangapi.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Time;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import v3lop5.mojangapi.Main;
import v3lop5.mojangapi.MessageListener;
import v3lop5.mojangapi.api.JsonBuilder.ClickAction;
import v3lop5.mojangapi.api.JsonBuilder.HoverAction;

public class MojangAPI {

	private MessageListener ml;
	private Plugin pl;

	private String nameinformation;
	private String uuidinformation;
	private String uuidnames;
	private int despawndelay;

	public MojangAPI(Main main) {
		this.ml = main.getMessageListener();
		this.pl = main;
		this.nameinformation = main.getConfig()
				.getString("URL.nameinformation");
		this.uuidinformation = main.getConfig()
				.getString("URL.uuidinformation");
		this.uuidnames = main.getConfig().getString("URL.namesfromuuid");
		this.despawndelay = main.getConfig().getInt("NPC.despawndelay");
	}

	public String getJSON(String URLString) throws IOException {

		java.net.URL url = new URL(URLString);
		URLConnection urlc = url.openConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				urlc.getInputStream()));

		StringBuilder sb = new StringBuilder();
		int byteRead;
		while ((byteRead = br.read()) != -1)
			sb.append((char) byteRead);

		br.close();
		return sb.toString();

	}

	public void getProfileInformationforName(CommandSender cs, String name) {
		try {
			String json = getJSON(nameinformation.replace("%NAME", name));
			if (json == null) {
				cs.sendMessage(ml.getMessage("api.error").replace("%FOR", name));
				return;
			}
			if (json.contains("IllegalArgumentException")) {
				cs.sendMessage(ml.getMessage("api.noMatching").replace("%WHAT",
						name));
				return;
			}
			String id = null;
			String uname = name;
			boolean legacy = false;
			boolean demo = false;
			json = json.replace("{", "").replace("}", "").replace("[", "")
					.replace("]", "");
			String[] jsonsplit = json.split(",");
			try {
				for (String s : jsonsplit) {
					if (s.contains("id")) {
						id = s.split(":")[1].substring(1,
								s.split(":")[1].length() - 1);
					}
					if (s.contains("name")) {
						uname = s.split(":")[1].substring(1,
								s.split(":")[1].length() - 1);
					}
					if (s.contains("legacy")) {
						legacy = Boolean.valueOf(s.split(":")[1]);
					}
					if (s.contains("demo")) {
						demo = Boolean.valueOf(s.split(":")[1]);
					}
				}
				if (id == null) {
					cs.sendMessage(ml.getMessage("api.cracked").replace(
							"%NAME", name));
					return;
				}
				if (cs instanceof CraftPlayer) {
					cs.sendMessage(ml.getMessage("api.info.forname").replace(
							"%NAME", uname));
					new JsonBuilder()
							.parse(ml.getMessage("api.uuid.text").replace(
									"%UUID", id))
							.withClickEvent(ClickAction.RUN_COMMAND,
									"/mojangapi info " + id)
							.withHoverEvent(HoverAction.SHOW_TEXT,
									ml.getMSG("api.uuid.hover"))
							.sendJson(((CraftPlayer) cs));
					if (legacy) {
						cs.sendMessage(ml
								.getMessage("api.legacy.text")
								.replace(
										"%STATUS",
										(legacy) ? ml.getMSG("api.legacy.true")
												: ml.getMSG("api.legacy.false")));
					}
					if (demo) {
						cs.sendMessage(ml.getMessage("api.demo.text").replace(
								"%STATUS",
								(legacy) ? ml.getMSG("api.demo.true") : ml
										.getMSG("api.demo.false")));
					}
				} else {
					cs.sendMessage(ml.getMessage("api.info.forname").replace(
							"%NAME", uname));
					cs.sendMessage(ml.getMessage("api.uuid.text").replace(
							"%UUID", id));
					if (legacy) {
						cs.sendMessage(ml
								.getMessage("api.legacy.text")
								.replace(
										"%STATUS",
										(legacy) ? ml.getMSG("api.legacy.true")
												: ml.getMSG("api.legacy.false")));
					}
					if (demo) {
						cs.sendMessage(ml.getMessage("api.demo.text").replace(
								"%STATUS",
								(legacy) ? ml.getMSG("api.demo.true") : ml
										.getMSG("api.demo.false")));
					}
				}
			} catch (Exception e) {
				cs.sendMessage(ml.getMessage("api.error").replace("%FOR",
						e.getMessage()));
			}

		} catch (IOException e) {
			cs.sendMessage(ml.getMessage("api.error").replace("%FOR",
					e.getMessage()));

		}
	}

	public void getProfileInformationforUUID(CommandSender cs, String uuid) {
		try {
			String json = getJSON(uuidinformation.replace("%UUID", uuid));
			if (json == null) {
				cs.sendMessage(ml.getMessage("api.error").replace("%FOR", uuid));
				return;
			}
			if (json.contains("IllegalArgumentException")) {
				cs.sendMessage(ml.getMessage("api.noMatching").replace("%WHAT",
						uuid));
				return;
			}
			json = json.replace("[{", "").replace("}]}", "");
			String prop = null;
			for (String s : json.split(",")) {
				if (s.contains("value")) {
					prop = s.split(":")[1];

				}
			}
			if (prop == null) {
				cs.sendMessage(ml.getMessage("api.noMatching").replace("%WHAT",
						uuid));
				return;
			}
			prop = prop.substring(1, prop.length() - 1);
			String id = null;
			String name = null;
			String time = null;
			String isPublic = null;
			String skin = null;
			String cape = null;

			String res = Base64Coder.decodeString(prop);

			res = res.replace("{", "").replace("}", "");
			for (String s : res.split(",")) {
				if (s.contains("profileId")) {
					id = s.split(":")[1].substring(1,
							s.split(":")[1].length() - 1);
				} else if (s.contains("profileName")) {
					name = s.split(":")[1].substring(1,
							s.split(":")[1].length() - 1);
				} else if (s.contains("timestamp")) {
					time = s.split(":")[1];
				} else if (s.contains("isPublic")) {
					isPublic = s;
				} else if (s.contains("SKIN")) {
					skin = s.split(":")[s.split(":").length - 1].substring(1,
							s.split(":")[s.split(":").length - 1].length() - 1);
				} else if (s.contains("CAPE")) {
					cape = s.split(":")[s.split(":").length - 1].substring(1,
							s.split(":")[s.split(":").length - 1].length() - 1);
				}
			}

			if (id == null) {
				cs.sendMessage(ml.getMessage("api.noMatching").replace("%WHAT",
						uuid));
				return;
			}
			if (cs instanceof CraftPlayer) {
				cs.sendMessage(ml.getMessage("api.info.foruuid").replace(
						"%UUID", id));
				new JsonBuilder()
						.parse(ml.getMessage("api.name.text").replace("%NAME",
								name))
						.withClickEvent(ClickAction.RUN_COMMAND,
								"/mojangapi names " + id)
						.withHoverEvent(HoverAction.SHOW_TEXT,
								ml.getMSG("api.name.hover"))
						.sendJson(((CraftPlayer) cs));
				if (time != null) {
					new JsonBuilder()
							.parse(ml.getMessage("api.time.text")

							.replace("%TIME", time))
							.withClickEvent(ClickAction.RUN_COMMAND,
									"/mojangapi converttime " + time)
							.withHoverEvent(HoverAction.SHOW_TEXT,
									ml.getMSG("api.time.hover"))
							.sendJson(((CraftPlayer) cs));
				}
				if (isPublic != null) {
					cs.sendMessage(ml.getMessage("api.ispublic.text").replace(
							"%STATUS", isPublic));
				}
				if (skin != null) {
					new JsonBuilder()
							.parse(ml.getMessage("api.skin.text").replace(
									"%URL", skin))
							.withClickEvent(ClickAction.RUN_COMMAND,
									"/mojangapi spawn " + name)
							.withHoverEvent(HoverAction.SHOW_TEXT,
									ml.getMSG("api.skin.hover"))
							.sendJson(((CraftPlayer) cs));
				}
				if (cape != null) {
					new JsonBuilder()
							.parse(ml.getMessage("api.cape.text").replace(
									"%URL", cape))
							.withClickEvent(ClickAction.RUN_COMMAND,
									"/mojangapi spawn " + name)
							.withHoverEvent(HoverAction.SHOW_TEXT,
									ml.getMSG("api.cape.hover"))
							.sendJson(((CraftPlayer) cs));
				}
			} else {

			}

		} catch (IOException e) {
			cs.sendMessage(ml.getMessage("api.error").replace("%FOR",
					e.getMessage()));

		}
	}

	public void getNamesForName(CommandSender cs, String tname) {
		String uuid = null;

		try {
			String json = getJSON("https://api.mojang.com/users/profiles/minecraft/"
					+ tname);
			if (json == null) {
				cs.sendMessage(ml.getMessage("api.error")
						.replace("%FOR", tname));
				return;
			}
			if (json.contains("IllegalArgumentException")) {
				cs.sendMessage(ml.getMessage("api.noMatching").replace("%WHAT",
						tname));
				return;
			}
			json = json.replace("{", "").replace("}", "").replace("[", "")
					.replace("]", "");
			String[] jsonsplit = json.split(",");
			try {
				for (String s : jsonsplit) {
					if (s.contains("id")) {
						uuid = s.split(":")[1].substring(1,
								s.split(":")[1].length() - 1);
					}
				}
			} catch (Exception e) {
				cs.sendMessage(ml.getMessage("api.error").replace("%FOR",
						e.getMessage()));
			}

		} catch (IOException e) {
			cs.sendMessage(ml.getMessage("api.error").replace("%FOR",
					e.getMessage()));

		}

		try {
			String json = getJSON(uuidnames.replace("%UUID",
					transformUUID(uuid)));
			if (json.equals("null")) {
				cs.sendMessage(ml.getMessage("api.noMatching").replace("%WHAT",
						uuid));
				return;
			}
			json = json.replace("[{", "").replace("}]", "").replace("},", ";");

			String[] jsonsplit = json.split(";");
			cs.sendMessage(ml.getMessage("api.info.namesforUUid").replace(
					"%UUID", uuid));
			int i = 0;
			for (String s : jsonsplit) {
				i++;
				String[] temp = s.split(",");
				String name = null;
				String changedAt = null;
				for (String t : temp) {
					if (t.contains("name")) {
						name = t.split(":")[1].substring(1,
								t.split(":")[1].length() - 1);
					}
					if (t.contains("changedToAt")) {
						changedAt = t.split(":")[1];
					}
				}

				if (name == null) {
					cs.sendMessage(ml.getMessage("api.error").replace("%FOR",
							uuid));
					return;
				}
				if (cs instanceof CraftPlayer) {
					if (changedAt == null) {
						cs.sendMessage(ml.getMessage("api.names.withOutTime")
								.replace("%NAME", name)
								.replace("%COUNT", i + ""));
					} else {
						new JsonBuilder()
								.parse(ml.getMessage("api.names.withTime")
										.replace("%NAME", name)
										.replace("%COUNT", i + "")
										.replace("%TIME", changedAt))
								.withClickEvent(ClickAction.RUN_COMMAND,
										"/mojangapi converttime " + changedAt)
								.withHoverEvent(HoverAction.SHOW_TEXT,
										ml.getMSG("api.names.hover"))
								.sendJson(((CraftPlayer) cs));
					}
				} else {
					if (changedAt == null) {
						cs.sendMessage(ml.getMessage("api.names.withOutTime")
								.replace("%NAME", name)
								.replace("%COUNT", i + ""));
					} else {
						cs.sendMessage(ml.getMessage("api.names.withTime")
								.replace("%NAME", name)
								.replace("%COUNT", i + "")
								.replace("%TIME", changedAt));
					}
				}
			}
		} catch (IOException e) {
			cs.sendMessage(ml.getMessage("api.error").replace("%FOR",
					e.getMessage()));
		}
	}

	public void getNamesForUUID(CommandSender cs, String uuid) {
		try {
			String json = getJSON(uuidnames.replace("%UUID",
					transformUUID(uuid)));
			if (json.equals("null")) {
				cs.sendMessage(ml.getMessage("api.noMatching").replace("%WHAT",
						uuid));
				return;
			}
			json = json.replace("[{", "").replace("}]", "").replace("},", ";");

			String[] jsonsplit = json.split(";");
			cs.sendMessage(ml.getMessage("api.info.namesforUUid").replace(
					"%UUID", uuid));
			int i = 0;
			for (String s : jsonsplit) {
				i++;
				String[] temp = s.split(",");
				String name = null;
				String changedAt = null;
				for (String t : temp) {
					if (t.contains("name")) {
						name = t.split(":")[1].substring(1,
								t.split(":")[1].length() - 1);
					}
					if (t.contains("changedToAt")) {
						changedAt = t.split(":")[1];
					}
				}

				if (name == null) {
					cs.sendMessage(ml.getMessage("api.error").replace("%FOR",
							uuid));
					return;
				}
				if (cs instanceof CraftPlayer) {
					if (changedAt == null) {
						cs.sendMessage(ml.getMessage("api.names.withOutTime")
								.replace("%NAME", name)
								.replace("%COUNT", i + ""));
					} else {
						new JsonBuilder()
								.parse(ml.getMessage("api.names.withTime")
										.replace("%NAME", name)
										.replace("%COUNT", i + "")
										.replace("%TIME", changedAt))
								.withClickEvent(ClickAction.RUN_COMMAND,
										"/mojangapi converttime " + changedAt)
								.withHoverEvent(HoverAction.SHOW_TEXT,
										ml.getMSG("api.names.hover"))
								.sendJson(((CraftPlayer) cs));
					}
				} else {
					if (changedAt == null) {
						cs.sendMessage(ml.getMessage("api.names.withOutTime")
								.replace("%NAME", name)
								.replace("%COUNT", i + ""));
					} else {
						cs.sendMessage(ml.getMessage("api.names.withTime")
								.replace("%NAME", name)
								.replace("%COUNT", i + "")
								.replace("%TIME", changedAt));
					}
				}
			}
		} catch (IOException e) {
			cs.sendMessage(ml.getMessage("api.error").replace("%FOR",
					e.getMessage()));
		}
	}

	public void spawnPlayerWithUUID(final CommandSender cs, String uuid,
			final String name) {
		if (!(cs instanceof Player)) {
			cs.sendMessage(ml.getMessage("command.onlyPlayer"));
			return;
		}
		if (uuid == null) {
			cs.sendMessage(ml.getMessage("npc.noUUID"));
			return;
		}
		List<Player> view = new LinkedList<>();
		view.add(((Player) cs));
		String temp = transformUUID(uuid);
		String uuidrdy = temp.substring(0, 7) + "-" + temp.substring(8, 11)
				+ "-" + temp.substring(12, 15) + "-" + temp.substring(16, 19)
				+ "-" + temp.substring(20, 31);
		@SuppressWarnings("deprecation")
		Location loc = ((Player) cs).getTargetBlock(null, 0).getLocation()
				.clone().add(0, 1, 0);
		if (loc == null) {
			loc = ((Player) cs).getLocation().clone();
		}

		final NPC npc = new NPC(loc, UUID.fromString(uuidrdy), name, view);

		npc.spawn();
		cs.sendMessage(ml.getMessage("npc.spawned").replace("%NAME", name));
		Bukkit.getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {

			@Override
			public void run() {
				npc.remove();
				cs.sendMessage(ml.getMessage("npc.despawned").replace("%NAME",
						name));
			}
		}, despawndelay);
	}

	public boolean isUUID(String uuid) {

		return transformUUID(uuid).length() == 32;
	}

	private String transformUUID(String uuid) {

		String temp = uuid.toLowerCase();
		while (temp.contains("-")) {
			temp = temp.replace("-", "");
		}
		while (temp.contains(" ")) {
			temp = temp.replace(" ", "");
		}

		return temp;
	}

	@SuppressWarnings("deprecation")
	public void convertTime(CommandSender cs, String s) {
		long l;
		try {
			l = Long.valueOf(s);
		} catch (Exception e) {
			cs.sendMessage(ml.getMessage("time.noLong").replace("%CAUSE", s));
			return;
		}

		Time t = new Time(l);
		cs.sendMessage(ml.getMessage("time.time").replace("%TIME",
				t.toGMTString()));
	}

	public void sendHelp(CommandSender cs) {
		cs.sendMessage(ml.getMessage("Help.Header"));
		cs.sendMessage("");
		cs.sendMessage(ml.getMSG("Help.Command")
				.replace("%CMD", "/mojangapi [UserName]")
				.replace("%DES", "Zeigt UUiD vom UserName"));
		cs.sendMessage(ml.getMSG("Help.Command")
				.replace("%CMD", "/mojangapi name [UserName]")
				.replace("%DES", "Zeigt UUiD vom UserName"));

		cs.sendMessage(ml.getMSG("Help.Command")
				.replace("%CMD", "/mojangapi info [UUiD]")
				.replace("%DES", "Zeigt Informationen zur UUiD"));

		cs.sendMessage(ml.getMSG("Help.Command")
				.replace("%CMD", "/mojangapi names [UserName]")
				.replace("%DES", "Zeigt Namen von dem Namen"));
		cs.sendMessage(ml.getMSG("Help.Command")
				.replace("%CMD", "/mojangapi names [UUiD]")
				.replace("%DES", "Zeigt Namen von der UUiD"));

		cs.sendMessage(ml.getMSG("Help.Command")
				.replace("%CMD", "/mojangapi time [long]")
				.replace("%DES", "Konvertiert einen long zu einem Datum"));

		cs.sendMessage(ml.getMSG("Help.Command")
				.replace("%CMD", "/mojangapi spawn [UserName]")
				.replace("%DES", "Spawnt einen NPC vom User"));
		cs.sendMessage(ml.getMSG("Help.Command")
				.replace("%CMD", "/mojangapi spawn [UUiD]")
				.replace("%DES", "Spawnt einen NPC von der UUiD"));

		cs.sendMessage(ml.getMSG("Help.Command")
				.replace("%CMD", "/mojangapi help")
				.replace("%DES", "Zeigt alle möglichen Commands"));
		cs.sendMessage(ml.getMSG("Help.Command")
				.replace("%CMD", "/mojangapi info")
				.replace("%DES", "Zeigt Informationen über das Plugin"));

	}

	public void sendInfo(CommandSender cs) {
		cs.sendMessage(ml.getPrefix() + "§8##############################");
		cs.sendMessage(ml.getPrefix() + "§8#  §aPlugin made by §lV3lop5§a!");
		cs.sendMessage(ml.getPrefix() + "§8#  §eyoutube.com/V3lop5");
		cs.sendMessage(ml.getPrefix() + "§8##############################");
	}

	public boolean hasPerm(CommandSender cs, String perm) {
		if (cs.hasPermission(perm)) {
			return true;
		}

		cs.sendMessage(ml.getMessage("command.noPermission").replace("%PERM",
				perm));
		return false;
	}

	public String getUUID(String name) {

		try {
			String json = getJSON(nameinformation.replace("%NAME", name));
			if (json == null) {

				return null;
			}
			if (json.contains("IllegalArgumentException")) {
				return null;
			}
			json = json.replace("{", "").replace("}", "").replace("[", "")
					.replace("]", "");
			String[] jsonsplit = json.split(",");
			try {
				for (String s : jsonsplit) {
					if (s.contains("id")) {
						return s.split(":")[1].substring(1,
								s.split(":")[1].length() - 1);
					}
				}
			} catch (Exception e) {

			}

		} catch (IOException e) {

		}
		return null;
	}

}
