package com.rs.game.player.dialogues.npcs;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.item.Item;
import com.rs.game.player.dialogues.Dialogue;
import com.rs.utils.Utils;

public class Bob extends Dialogue {

	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, 9827,
				"Hello, " + player.getDisplayName() + ". I can repair all your broken barrows pieces.");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch (stage) {
		case -1:
			if (getRepairPrice() != 0) {
				sendOptionsDialogue("Do you want to have your items repaired for "
						+ Utils.getFormattedNumber(getRepairPrice(), ',') + " coins?", "Yes", "No");
				stage = 0;
			} else {
				sendNPCDialogue(npcId, 9827, "You don't have any items to repair.");
				stage = -2;
			}
		case 0:
			switch (option) {
			case OPTION_1:
				if (player.takeMoney(getRepairPrice())) {
					repairAll();
					sendNPCDialogue(npcId, 9827, "Thank you.");
					stage = -2;
				} else {
					sendNPCDialogue(npcId, 9827, "You need at least " + Utils.getFormattedNumber(getRepairPrice(), ',')
							+ " coins to repair this.");
					stage = -2;
				}
				break;
			}
			break;
		default:
			end();
			break;
		}
	}

	@Override
	public void finish() {

	}

	private Object[][] prices = new Object[][] { { " 100", 5000 }, { " 75", 25000 }, { " 50", 50000 }, { " 25", 75000 },
			{ " 0", 100000 } };

	private int getRepairPrice() {
		int price = 0;
		for (Item item : player.getInventory().getItems().toArray()) {
			if (item == null)
				continue;
			String name = item.getName();
			if (isRepairable(item)) {
				for (int i = 0; i < prices.length; i++) {
					if (name.toLowerCase().replace("'", "").contains(prices[i][0].toString())) {
						try {
							price += (int) prices[i][1];
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return price;
	}

	private int getRepairId(Item item) {
		// Ahrims
		if (item.getId() >= 4856 && item.getId() <= 4860)
			return 4708;// ahrims hood
		if (item.getId() == 4861)
			return 4709;// noted ahrims staff
		if (item.getId() >= 4862 && item.getId() <= 4866)
			return 4710;// ahrims staff
		if (item.getId() == 4867)
			return 4711;// noted ahrims staff
		if (item.getId() >= 4868 && item.getId() <= 4872)
			return 4712;// ahrims top
		if (item.getId() == 4873)
			return 4713;// noted ahrims top
		if (item.getId() >= 4874 && item.getId() <= 4878)
			return 4714;// ahrims skirt
		if (item.getId() == 4879)
			return 4715;// noted ahrim skirt
		// Dharoks
		if (item.getId() >= 4880 && item.getId() <= 4884)
			return 4716;// dharoks helm
		if (item.getId() == 4885)
			return 4717;// noted dharok helm
		if (item.getId() >= 4886 && item.getId() <= 4890)
			return 4718;// dharoks axe
		if (item.getId() == 4891)
			return 4719;// noted dharok axe
		if (item.getId() >= 4892 && item.getId() <= 4896)
			return 4720;// dharoks platebody
		if (item.getId() == 4897)
			return 4721;// noted dharok platebody
		if (item.getId() >= 4898 && item.getId() <= 4902)
			return 4722;// dharoks platelegs
		if (item.getId() == 4903)
			return 4723;// noted dharok platelegs
		// Guthans
		if (item.getId() >= 4904 && item.getId() <= 4908)
			return 4724;// guthans helm
		if (item.getId() == 4909)
			return 4725;// noted guthans helm
		if (item.getId() >= 4910 && item.getId() <= 4914)
			return 4726;// guthans warspear
		if (item.getId() == 4915)
			return 4727;// noted guthans warspear
		if (item.getId() >= 4916 && item.getId() <= 4920)
			return 4728;// guthans platebody
		if (item.getId() == 4921)
			return 4729;// noted guthans platebody
		if (item.getId() >= 4922 && item.getId() <= 4926)
			return 4730;// guthans chainskirt
		if (item.getId() == 4927)
			return 4731;// noted guthans chainskirt
		// Karils
		if (item.getId() >= 4928 && item.getId() <= 4932)
			return 4732;// karils coif
		if (item.getId() == 4933)
			return 4733;// noted karils coif
		if (item.getId() >= 4934 && item.getId() <= 4938)
			return 4734;// karils crossbow
		if (item.getId() == 4939)
			return 4735;// noted karils crossbow
		if (item.getId() >= 4940 && item.getId() <= 4944)
			return 4736;// karils leathertop
		if (item.getId() == 4945)
			return 4737;// noted karils leathertop
		if (item.getId() >= 4946 && item.getId() <= 4950)
			return 4738;// karils leatherskirt
		if (item.getId() == 4951)
			return 4739;// noted karils leatherskirt
		// Torags
		if (item.getId() >= 4952 && item.getId() <= 4956)
			return 4745;// torags helm
		if (item.getId() == 4957)
			return 4746;// noted torags helm
		if (item.getId() >= 4958 && item.getId() <= 4962)
			return 4747;// torags hammers
		if (item.getId() == 4963)
			return 4748;// noted torags hammers
		if (item.getId() >= 4964 && item.getId() <= 4968)
			return 4749;// torags platebody
		if (item.getId() == 4969)
			return 4750;// noted torags platebody
		if (item.getId() >= 4970 && item.getId() <= 4974)
			return 4751;// torags platelegs
		if (item.getId() == 4975)
			return 4752;// noted torags platelegs
		// Veracs
		if (item.getId() >= 4976 && item.getId() <= 4980)
			return 4753;// veracs helm
		if (item.getId() == 4981)
			return 4754;// noted veracs helm
		if (item.getId() >= 4982 && item.getId() <= 4986)
			return 4755;// veracs flail
		if (item.getId() == 4987)
			return 4756;// noted veracs flail
		if (item.getId() >= 4988 && item.getId() <= 4992)
			return 4757;// veracs brassard
		if (item.getId() == 4993)
			return 4758;// noted veracs brassard
		if (item.getId() >= 4994 && item.getId() <= 4998)
			return 4759;// veracs plateskirt
		if (item.getId() == 4999)
			return 4760;// noted veracs plateskirt
		return 0;
	}

	private void repairAll() {
		for (Item item : player.getInventory().getItems().toArray()) {
			if (item == null)
				continue;
			if (isRepairable(item)) {
				int newId = (ItemDefinitions.getItemDefinitions(getRepairId(item)).isNoted()
						? ItemDefinitions.getItemDefinitions(getRepairId(item)).getCertId() : getRepairId(item));
				item.setId(newId);
				player.getInventory().refresh();
			}
		}
	}

	private boolean isRepairable(Item item) {
		if (item.getDefinitions().isNoted())
			return false;
		try {
			String name = item.getName();
			for (int i = 0; i < prices.length; i++) {
				if (name.toLowerCase().replace("'", "").contains(prices[i][0].toString())) {
					return true;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
}