package taxi.handler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import taxi.data.DataManager;
import taxi.main.Main;

public class Taxi {
	DataManager data = Main.data;
	FileConfiguration file = data.getFile();

	public void makeTaxiTicket(Player p) { // 택시 티켓 호출 메서드
		ItemStack item = p.getInventory().getItemInMainHand();
		if (item == null || item.getType() == Material.AIR) {
			p.sendMessage(ChatColor.RED + "맨손으로는 이 커맨드를 사용할 수 없습니다.");
			return;
		}
		file.set("ticket.item", item);
		data.saveConfig();
		p.sendMessage(ChatColor.GREEN + "성공적으로 티켓 아이템이 등록되었습니다.");
	}

	public void setTaxiLocation(Player p, int num) { // 택시 호출 및 위치 저장 메서드
		if (num == 1) {
			// 메인택시 위치 data.yml에 저장
			Location loc = p.getLocation();
			int cnt = 1;
			if (file.contains("main_taxiLocation")) {
				Set<String> keys = file.getConfigurationSection("main_taxiLocation").getKeys(false);
				ArrayList<String> keys_array = new ArrayList<String>();
				for(String key : keys) {
					if(file.getString("main_taxiLocation."+key+".loc") == null)
						continue;
					keys_array.add(key);
				}
				Iterator<String> iter = keys_array.iterator();
				while (iter.hasNext()) {
					String key = iter.next();
					cnt++;
				}
			} else {
				cnt = 1;
			}
			file.set("main_taxiLocation." + cnt + ".loc", loc);
			data.saveConfig();
			p.sendMessage(ChatColor.GREEN + "현재위치가 (메인)택시호출 장소로 지정되었습니다.");
		} else if (num == 2) {
			Location loc = p.getLocation();
			int cnt = 1;
			if (file.contains("deco_taxiLocation")) {
				Set<String> keys = file.getConfigurationSection("deco_taxiLocation").getKeys(false);
				ArrayList<String> keys_array = new ArrayList<String>();
				for(String key : keys) {
					if(file.getString("deco_taxiLocation."+key+".loc") == null)
						continue;
					keys_array.add(key);
				}
				Iterator<String> iter = keys_array.iterator();
				while (iter.hasNext()) {
					String key = iter.next();
					cnt++;
				}
			} else {
				cnt = 1;
			}
			file.set("deco_taxiLocation." + cnt + ".loc", loc);
			data.saveConfig();
			p.sendMessage(ChatColor.GREEN + "현재위치가 (데코)택시호출 장소로 지정되었습니다.");
		}
	}

	public void listTaxiLocation(Player p) { // 택시 리스트 출력 메서드
		Set<String> keys = file.getConfigurationSection("main_taxiLocation").getKeys(false);
		ArrayList<String> keys_array = new ArrayList<String>();
		for(String key : keys) {
			if(file.getString("main_taxiLocation."+key+".loc") == null)
				continue;
			keys_array.add(key);
		}
		Iterator<String> iter = keys_array.iterator();
		p.sendMessage(ChatColor.YELLOW + "-----------------------------------------------------------------");
		p.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "                   <(메인)택시위치 목록>                   ");
		p.sendMessage(ChatColor.YELLOW + "-----------------------------------------------------------------");
		while (iter.hasNext()) {
			String key = iter.next();
			Location loc = (Location) file.get("main_taxiLocation." + key + ".loc");
			p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + key + ChatColor.GREEN + " | x : "
					+ loc.getX() + " y : " + loc.getY() + " z : " + loc.getZ());
		}
		p.sendMessage(ChatColor.YELLOW + "-----------------------------------------------------------------");
		keys = file.getConfigurationSection("deco_taxiLocation").getKeys(false);
		keys_array.clear();
		for(String key : keys) {
			if(file.getString("deco_taxiLocation."+key+".loc") == null)
				continue;
			keys_array.add(key);
		}
		iter = keys_array.iterator();
		p.sendMessage(ChatColor.YELLOW + "-----------------------------------------------------------------");
		p.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "                   <(데코)택시위치 목록>                   ");
		p.sendMessage(ChatColor.YELLOW + "-----------------------------------------------------------------");
		while (iter.hasNext()) {
			String key = iter.next();
			Location loc = (Location) file.get("deco_taxiLocation." + key + ".loc");
			p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + key + ChatColor.GREEN + " | x : "
					+ loc.getX() + " y : " + loc.getY() + " z : " + loc.getZ());
		}
		p.sendMessage(ChatColor.YELLOW + "-----------------------------------------------------------------");
	}

	public void deleteTaxiLocation(Player p, int num) { // 택시 위치 삭제 메서드
		Set<String> main_keys = file.getConfigurationSection("main_taxiLocation").getKeys(false);
		ArrayList<String> main_keys_array = new ArrayList<String>();
		for(String key : main_keys) {
			if(file.getString("main_taxiLocation."+key+".loc") == null)
				continue;
			main_keys_array.add(key);
		}
		Iterator<String> main_iter = main_keys_array.iterator();
		Set<String> deco_keys = file.getConfigurationSection("deco_taxiLocation").getKeys(false);
		ArrayList<String> deco_keys_array = new ArrayList<String>();
		for(String key : deco_keys) {
			if(file.getString("deco_taxiLocation."+key+".loc") == null)
				continue;
			deco_keys_array.add(key);
		}
		Iterator<String> deco_iter = deco_keys_array.iterator();
		if (main_keys.size() != deco_keys.size() || (main_keys == null || deco_keys == null)) { // 메인 택시 수랑 데코 택시의 수가
																								// 동일하지 않을 때
			p.sendMessage(ChatColor.RED + "메인 택시의 숫자랑 데코 택시의 숫자가 동일하지 않아 삭제가 불가능합니다.");
			return;
		}
		if (main_keys.size() < num || deco_keys.size() < num) { // 번호를 큰 경우를 입력할 때
			p.sendMessage(ChatColor.RED + "번호를 확인하시고 다시 입력해주세요.");
			return;
		}else if(main_keys.size() == num  && deco_keys.size() == num) {
			file.set("main_taxiLocation."+num, null);
			file.set("deco_taxiLocation."+num, null);
			p.sendMessage(ChatColor.GREEN + "성공적으로 위치 좌표를 제거하였습니다.");
			data.saveConfig();
			return;
		}
		file.set("main_taxiLocation."+num, null);
		file.set("deco_taxiLocation."+num, null);
		data.saveConfig();

		ArrayList<Location> locArr = new ArrayList<Location>();
		while (main_iter.hasNext()) { // main 택시 처리
			String iter = main_iter.next();
			int key = Integer.parseInt(iter);
			if (key > num) { // 삭제 후 다시 정렬
				locArr.add((Location) file.get("main_taxiLocation." + key + ".loc"));
				file.set("main_taxiLocation." + key + ".loc", null);
				data.saveConfig();
			}
		}
		int cnt = num;
		for (int i = 0; i < locArr.size(); i++) {
			file.set("main_taxiLocation." + cnt + ".loc", locArr.get(i));
			data.saveConfig();
			cnt++;
		}

		locArr.clear();
		while (deco_iter.hasNext()) { // deco 택시 처리
			int key = Integer.parseInt(deco_iter.next());
			if (key > num) { // 삭제 후 다시 정렬
				locArr.add((Location) file.get("deco_taxiLocation." + key + ".loc"));
				file.set("deco_taxiLocation." + key + ".loc", null);
				data.saveConfig();
			}
		}
		cnt = num;
		for (int i = 0; i < locArr.size(); i++) {
			file.set("deco_taxiLocation." + cnt + ".loc", locArr.get(i));
			data.saveConfig();
			cnt++;
		}
		p.sendMessage(ChatColor.GREEN + "성공적으로 위치 좌표를 제거하였습니다.");
	}

	public void setModelTaxi(Player p, int num) { // 택시 모델 아이템 지정 메서드
		ItemStack item = p.getInventory().getItemInMainHand();
		if (item == null || item.getType() == Material.AIR) {
			p.sendMessage(ChatColor.RED + "맨손으로는 이 커맨드를 사용할 수 없습니다.");
			return;
		}
		if (num == 1) {
			file.set("main_taxi.item", item);
			data.saveConfig();
			p.sendMessage(ChatColor.GREEN + "성공적으로 메인택시 모델의 아이템이 등록되었습니다.");
		} else if (num == 2) {
			file.set("deco_taxi.item", item);
			data.saveConfig();
			p.sendMessage(ChatColor.GREEN + "성공적으로 데코택시 모델의 아이템이 등록되었습니다.");
		}
	}
	public void printTaxi(Player p) {
		p.sendMessage(ChatColor.YELLOW + "--------------------------------------------");
		p.sendMessage(ChatColor.AQUA + "         <택시호출 명령어>         ");
		p.sendMessage(ChatColor.YELLOW + "--------------------------------------------");
		p.sendMessage(ChatColor.GOLD + "/택시설정 호출권" + ChatColor.WHITE + "- 손에 들고 있는 아이템을 1회용 택시 호출권으로 지정함.");
		p.sendMessage(ChatColor.GOLD + "/택시설정 위치 [1~2]" + ChatColor.WHITE + "- 지정된 모델의 서있는 위치를 택시호출 장소로 지정함.");
		p.sendMessage(ChatColor.GOLD + "/택시설정 위치목록" + ChatColor.WHITE + "- 택시가 생성된 위치 좌표랑 번호 표시함.");
		p.sendMessage(ChatColor.GOLD + "/택시설정 위치삭제 [int]" + ChatColor.WHITE + "- 해당 번호의 위치를 제거함. (메인, 데코 모델 둘다 / 메인 데코 번호가 똑같을 때만 실행 됨).");
		p.sendMessage(ChatColor.GOLD + "/택시설정 모델지정 [1~2]" + ChatColor.WHITE + "- 손에 들고 있는 아이템을 <int>번 아머스탠드 머리위에 쓰일 모델로 지정함.");
		p.sendMessage(ChatColor.GOLD + "/택시호출" + ChatColor.WHITE + "- 소모권이 없더라도 해당 위치에서 택시를 부름.");
		p.sendMessage(ChatColor.GOLD + "/강제택시호출중단 [name]" + ChatColor.WHITE + "- 강제적으로 호출한 택시, 혹은 이미 택시를 호출했을 경우 취소시킴. (이 경우, 호출권을 소모하지 않음).");
		p.sendMessage(ChatColor.YELLOW + "--------------------------------------------");
	}
}
