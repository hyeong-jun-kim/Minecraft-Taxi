package taxi.main;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import taxi.command.CommandManager;
import taxi.data.DataManager;
import taxi.event.TaxiEventManager;


public class Main extends JavaPlugin{
	public static DataManager data;
	public static Main main;
	@Override
	public void onEnable() {
		data = new DataManager(this);
		main = this;
		getServer().getPluginManager().registerEvents(new TaxiEventManager(), this);
		getCommand("택시설정").setExecutor(new CommandManager());
		getCommand("택시호출").setExecutor(new CommandManager());
		getCommand("강제택시호출중단").setExecutor(new CommandManager());
		/*
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		manager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.POSITION) {
			@Override
			public void onPacketReceiving(PacketEvent e) {
				PacketContainer packet = e.getPacket();
				Player p = e.getPlayer();
				double x = packet.getDoubles().read(0);
				double y = packet.getDoubles().read(0);
				double z = packet.getDoubles().read(0);
				boolean isOnGround = packet.getBooleans().read(0);
				
				p.sendMessage("INBOUND PACKET: x: " + x + " y " + y + " z : " + z);
				p.sendMessage("ON GROUND? " + isOnGround);
			}
		});*/
	}
	@Override
	public void onDisable() {
		TaxiEventManager taxiEventManager = TaxiEventManager.taxiEventManager;
		ArrayList<UUID> uuids = taxiEventManager.all_armorStand_list;
		for (UUID uuid : uuids) {
			for (World w : Bukkit.getWorlds()) {
				for (Entity e : w.getEntities()) {
					if (e.getUniqueId().equals(uuid))
						e.remove();
				}
			}
		}
	}
}
