package com.rs.game.player.content.quest.impl.cooksassistant.dialogues;


import com.rs.game.item.Item;
import com.rs.game.player.content.quest.QuestList.Quests;
import com.rs.game.player.content.quest.State.QuestState;
import com.rs.game.player.content.quest.impl.cooksassistant.CooksAssistant;
import com.rs.game.player.dialogues.Dialogue;
import com.rs.game.player.dialogues.Mood;

/**
 * The Lumbridge Cook.
 * 
 * @author Vip3r
 * Version 1.0 - 16/03/2015; 03:57
 */
public class LumbridgeCook extends Dialogue {

	/*
	 * (non-Javadoc)
	 * @see org.fb.game.content.dialogue.Dialogue#getNPCID()
	 */
	@Override
	public int getNPCID() {
		return 278;
	}

	/*
	 * (non-Javadoc)
	 * @see org.fb.game.content.dialogue.Dialogue#start()
	 */
	@Override
	public void start() {
		stage = (byte) parameters[0];
		int progress = player.getQuestManager().get(Quests.COOKS_ASSISTANT).getStage();
		System.out.println(progress);
		if (player.getQuestManager().get(Quests.COOKS_ASSISTANT).getState() == QuestState.COMPLETED) {
			sendNPCChat(Mood.HAPPY,  "Hi there, "
					+ (player.getAppearence().isMale() ? "sir"
							: "ma'am") + "!",
					"What can I help you with today?");
			stage = -1;
			return;
		}
		if (progress > 0) {
			if (progress == 1) {
				sendPlayerChat(Mood.HAPPY, "I'm always happy to help a cook in distress.");
				stage = 6;
			} else if (progress == 2) {
				sendNPCChat(Mood.ASKING, "How are you getting on with finding the ingredients?");
				stage = 11;
			} else if (progress == 3) {
				sendNPCChat(Mood.HAPPY, "You've brought me everything I need! I am saved! Thank you!");
				stage = 14;
			}
		}
		switch(stage) {
		/**
		 * Not Started
		 */
		case 0:
			sendNPCChat(Mood.SAD, "What am I to do?");
			stage = 1;
			break;
			/*
			 * Accepted.
			 */
		case -1:
			sendPlayerChat(Mood.HAPPY, "I'm always happy to help a cook in distress.");
			stage = 6;
			break;
			
			/**
			 * Declined
			 */
		case -2:
			sendPlayerChat(Mood.NORMAL, "I can't right now. Maybe later.");
			stage = 10;
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.fb.game.content.dialogue.Dialogue#run(int, int)
	 */
	@Override
	public void run(int interfaceId, int componentId) {
		switch(stage) {
		case -1:
			sendOptionsDialogue("Select an Option",
					"Do you have any other quests for me?",
					"I am getting strong and mighty.",
					"I keep on dying.",
					"Can I use your range?");
			stage = 25;
			break;
		case 1:
			sendOptionsDialogue("Select an Option", "What's wrong?", "Can you make me a cake?", "You don't look very happy.", "Nice hat!");
			stage = 2;
			break;
		case 2:
			switch(componentId) {
			case OPTION_1:
				sendPlayerChat(Mood.ASKING, "What's wrong?");
				stage = 3; 
				break;
			case OPTION_2:
				sendPlayerChat(Mood.ASKING, "Can you make me a cake?");
				stage = 19;
				break;
			case OPTION_3:
				sendPlayerChat(Mood.NORMAL, "You don't look very happy.");
				stage = 22;
				break;
			case OPTION_4:
				sendPlayerChat(Mood.HAPPY, "Nice hat!");
				stage = 24; 
				break;
			}
			break;
		case 3:
			sendNPCChat(Mood.SAD, "Oh dear, oh dear, oh dear, I'm in a terrible, terrible mess!",
					"It's the Duke's birthday today, and I should be making him", 
					"a lovely, big birthday cake using special ingredients...");
			stage = 4;
			break;
		case 4:
			sendNPCChat(Mood.SAD, "...but I've forgotten to get the ingredients. I'll never get",
					"them in time now. He'll sack me! Whatever will I do? I have", "four children anda goat to look after. Would you help me?", "Please?");
			stage = 5;
			break;
		case 5:
			end();
			player.getQuestManager().get(Quests.COOKS_ASSISTANT).sendStartOption();
			break;
		case 6:
			sendNPCChat(Mood.HAPPY, "Oh, thank you. I must tell you that this is no ",
					"ordinary cake, though - only the best ingredients will do! I", "need a super large egg, top-quality milk and some extra", "fine flour.");
			stage = 7;
			break;
		case 7:
			sendPlayerChat(Mood.ASKING, "Where can I find those, then?");
			stage = 8;
			break;
		case 8:
			sendNPCChat(Mood.NORMAL,  "That's the problem: I don't exactly know. I usually send my assistant",
					"to get them for me but he quit. I've marked some places on your world",
					"map in red. You might want to consider investigating them.");
			stage = 9;
			break;
		case 9:
			sendPlayerChat(Mood.ASKING, "That's all the information I need. Thanks!");
			player.getQuestManager().get(Quests.COOKS_ASSISTANT).setStage(2);
			stage = 10;
			break;
		case 10:
			end();
			break;
		case 11:
			if (player.getInventory().containsItem(CooksAssistant.BUCKET_OF_MILK) ||
					player.getInventory().containsItem(CooksAssistant.EGG) ||
					player.getInventory().containsItem(CooksAssistant.POT_OF_FLOUR)) {
				sendPlayerChat(Mood.HAPPY, "I think have what you asked for.");
				stage = 12;
			} else {
				sendPlayerChat(Mood.NORMAL, "I'm still working on it.");
				stage = 10;
			}
			break;
		case 12:
			if (player.getInventory().containsItem(CooksAssistant.BUCKET_OF_MILK)) {
				player.getInventory().deleteItem(CooksAssistant.BUCKET_OF_MILK);
				player.getQuestManager().get(Quests.COOKS_ASSISTANT).getData().put("bucket_of_milk", "true");
				sendPlayerChat(Mood.HAPPY, "Here's some Top-quality milk.");
				stage = 13;
			} else if (player.getInventory().containsItem(CooksAssistant.EGG)) {
				player.getInventory().deleteItem(CooksAssistant.EGG);
				player.getQuestManager().get(Quests.COOKS_ASSISTANT).getData().put("egg", "true");
				sendPlayerChat(Mood.HAPPY, "Here's a Super large egg.");
				stage = 13;
			} else if (player.getInventory().containsItem(CooksAssistant.POT_OF_FLOUR)) {
				player.getInventory().deleteItem(CooksAssistant.POT_OF_FLOUR);
				player.getQuestManager().get(Quests.COOKS_ASSISTANT).getData().put("flour", "true");
				sendPlayerChat(Mood.HAPPY, "Here's a pot of extra fine flour.");
				stage = 13;
			} else if (player.getQuestManager().get(Quests.COOKS_ASSISTANT).getData().get("egg") == null
					|| player.getQuestManager().get(Quests.COOKS_ASSISTANT).getData().get("flour") == null
					|| player.getQuestManager().get(Quests.COOKS_ASSISTANT).getData().get("bucket_of_milk") == null) {
				sendNPCChat(Mood.NORMAL, "You're still missing some ingredients.");
				stage = 10;
			} else if (player.getQuestManager().get(Quests.COOKS_ASSISTANT).getData().get("egg") != null
					&& player.getQuestManager().get(Quests.COOKS_ASSISTANT).getData().get("flour") != null
					&& player.getQuestManager().get(Quests.COOKS_ASSISTANT).getData().get("bucket_of_milk") != null) {
				sendNPCChat(Mood.HAPPY, "You've brought me everything I need! I am saved! Thank you!");
				player.getQuestManager().get(Quests.COOKS_ASSISTANT).setStage(3);
				stage = 14;
			}
			break;
		case 13:
			sendNPCChat(Mood.HAPPY, "Ta' very much!");
			stage = 12;
			break;
		case 14:
			sendPlayerChat(Mood.HAPPY, "So, do I get to go to the Duke's party?");
			stage = 15;
			break;
		case 15:
			sendNPCChat(Mood.NORMAL, "I'm afraid not. Only the big cheeses get to dine with the Duke.");
			stage = 16;
			break;
		case 16:
			sendPlayerChat(Mood.NORMAL, "Well, maybe one day, I'll be important enough to sit at the Duke's table.");
			stage = 17;
			break;
		case 17:
			sendNPCChat(Mood.NORMAL, "Maybe, but I won't be holding my breath.");
			stage = 18;
			break;
		case 18:
			end();
			player.getQuestManager().get(Quests.COOKS_ASSISTANT).setStage(4);
			player.getQuestManager().get(Quests.COOKS_ASSISTANT).finish();
			break;
		case 19:
			sendNPCChat(Mood.SLIGHTLY_ANGRY,
					"Oh I don't have time to make you a cake, can't you see I'm a bit busy?");
			stage = 20;
			break;
		case 20:
			sendOptionsDialogue("Select an Option", "What's wrong?",
					"Nevermind");
			stage = 21;
			break;
		case 21:
			if (componentId == OPTION_1) {
				sendPlayerChat(Mood.ASKING, "What's wrong?");
				stage = 3;
			} else if (componentId == OPTION_2) {
				end();
			}
			break;
		case 22:
			sendNPCChat(Mood.SAD,
					"I know, I've been busy trying to figure out what I should do.");
			stage = 20;
			break;
		case 24:
			sendNPCChat(Mood.SLIGHTLY_ANGRY,
					"I don't have time for your pesky compliments!");
			stage = 21;
			break;
		case 25:
			switch(componentId) {
			case OPTION_1:
				sendPlayerChat(Mood.ASKING, "Do you have any other quests for me?");
				stage = 26; 
				break;
			case OPTION_2:
				sendPlayerChat(Mood.ASKING, "I am getting strong and mighty.");
				stage = 31;
				break;
			case OPTION_3:
				sendPlayerChat(Mood.NORMAL, "I keep on dying.");
				stage = 32;
				break;
			case OPTION_4:
				sendPlayerChat(Mood.HAPPY, "Can I use your range?");
				stage = 33; 
				break;
			case OPTION_5:
				sendPlayerChat(Mood.HAPPY, "Can you tell me anything about that chest in the basement?");
				stage = 23; 
				break;
			}
			break;
		case 26:
			sendPlayerChat(Mood.HAPPY, "That last one was fun!");
			stage = 27;
			break;
		case 27:
			sendNPCChat(Mood.SAD,
					"Ooh dear, yes I do.");
			stage = 28;
			break;
		case 28:
			sendNPCChat(Mood.SAD,
					"It's the Duke of Lumbridge's birthday today, and",
					"I need to bake him a cake!");
			stage = 29;
			break;
		case 29:
			sendNPCChat(Mood.NORMAL,
					"I need you to bring me some eggs, some flour, ",
					"some milk and a chocolate bar...");
			stage = 30;
			break;
		case 30:
			sendNPCChat(Mood.LAUGHING,
					"Nah, not really, I'm just messing with you! ",
					"Thanks for all your help, I know I can count on you",
					"again in the future!");
			stage = -1;
			break;
		case 31:
			sendNPCChat(Mood.HAPPY, "Glad to hear it.");
			stage = 10;
			break;
		case 32:
			sendNPCChat(Mood.HAPPY,
					"Ah, well atleast you keep coming back to life too!");
			stage = 10;
			break;
		case 33:
			sendNPCChat(Mood.HAPPY,
					"Go ahead! It's a very good range; it's better than ",
					"most other ranges.");
			stage = 34;
			break;
		case 34:
			sendNPCChat(Mood.HAPPY,
					"It's called the Cook-o-Matic 25 and it uses a ",
					"combination of state-of-the-art temperature ",
					"regulation and magic.");
			stage = 35;
			break;
		case 35:
			sendPlayerChat(Mood.HAPPY, "Will it mean my food will burn less often?");
			stage = 36;
			break;
		case 36:
			sendNPCChat(Mood.HAPPY,
					"As long as the food is fairly easy to cook in ",
					"the first place!");
			stage = 37;
			break;
		case 37:
			sendNPCChat(Mood.NORMAL,
					"Here, take this manual. It should tell you ",
					"everything you need to know about this range.");
			stage = 38;
			break;
		case 38:
			sendHandedItem(15411, "The cook hands you a manuel.");
			player.getInventory().addItem(new Item(15411, 1));
			stage = 10;
			break;
		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.fb.game.content.quest.dialogue.QuestDialogue#finish()
	 */
	@Override
	public void finish() {

	}

}
