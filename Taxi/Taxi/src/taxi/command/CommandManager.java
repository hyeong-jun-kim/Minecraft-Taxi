package taxi.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import taxi.event.TaxiEventManager;
import taxi.handler.Taxi;

public class CommandManager implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("cmd에서는 이 작업을 수행하지 못합니다.");
			return true;
		} else {
			Player p = (Player) sender;
			if (p.isOp()) {
				if (label.equalsIgnoreCase("택시설정")) {
					int len = args.length;
					Taxi taxi = new Taxi();
					switch (len) {
					case 0:
						taxi.printTaxi(p);
						break;
					case 1:
						if (args[0].equals("호출권")) {
							taxi.makeTaxiTicket(p);
						} else if (args[0].equals("위치목록")) {
							taxi.listTaxiLocation(p);
						}
						break;
					case 2:
						int num = Integer.parseInt(args[1]);
						if (args[0].equals("위치삭제")) {
							taxi.deleteTaxiLocation(p, num);
						} else if (args[0].equals("모델지정")) {
							taxi.setModelTaxi(p, num);
						} else if (args[0].equals("위치")) {
							taxi.setTaxiLocation(p, num);
						}
						break;
					}
				} else if (label.equalsIgnoreCase("택시호출")) {
					if (args.length == 0) {
						TaxiEventManager taxieventManager = TaxiEventManager.taxiEventManager;
						taxieventManager.forcecallTaxi(p);
					}
				} else if (label.equalsIgnoreCase("강제택시호출중단")) {
					if (args.length == 1) {
						String name = args[0];
						TaxiEventManager taxieventManager = TaxiEventManager.taxiEventManager;
						taxieventManager.forceCancelTaxi(p, name);
					}

				}
			}
		}
		return true;
	}
}
