package v3lop5.mojangapi.api;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.server.v1_7_R4.DataWatcher;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedEntitySpawn;
import net.minecraft.util.com.mojang.authlib.GameProfile;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NPC {

	private UUID uuid;
	private Location loc;
	private int entityID;
	private Random r;
	private List<Player> viewers;
	private String name;

	public NPC(Location loc, UUID uuid, String name, List<Player> viewers) {
		this.uuid = uuid;
		this.loc = loc;
		this.r = new Random();
		this.entityID = r.nextInt(98765678);
		this.viewers = viewers;
		this.name = name;
	}

	public void spawn() {
		try {
			PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();
			set(packet, "a", entityID);
			set(packet, "b", new GameProfile(uuid, name));
			set(packet, "c", (int) loc.getX() * 32);
			set(packet, "d", (int) loc.getY() * 32);
			set(packet, "e", (int) loc.getZ() * 32);
			set(packet, "f", (byte) (((int) loc.getYaw() * 256F )/ 360F));
			set(packet, "g", (byte) (((int) loc.getPitch() * 256F) / 360F));
			set(packet, "h", 0);
			DataWatcher dw = new DataWatcher(null);
			dw.a(6, 20F);
			dw.a(10, (byte) 127);
			set(packet, "i", dw);

			for (Player p : viewers) {
				if (p != null) {
					((CraftPlayer) p).getHandle().playerConnection
							.sendPacket(packet);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void remove() {
		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(
				entityID);

		for (Player p : viewers) {
			if (p != null) {
				((CraftPlayer) p).getHandle().playerConnection
						.sendPacket(packet);
			}
		}
	}

	public void addToTab() {

	}

	public void remFromTab() {

	}

	private void set(Object i, String n, Object v) throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		java.lang.reflect.Field field = i.getClass().getDeclaredField(n);
		field.setAccessible(true);
		field.set(i, v);
	}
}
