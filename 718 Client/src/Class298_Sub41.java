/* Class298_Sub41 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */

public class Class298_Sub41 extends Class298 {
    Class298_Sub23 aClass298_Sub23_7420;
    static Class453 aClass453_7421 = new Class453();
    int anInt7422;
    int anInt7423;
    int anInt7424;
    int anInt7425;
    Class298_Sub19_Sub2 aClass298_Sub19_Sub2_7426;
    int anInt7427;
    int anInt7428;
    int anInt7429;
    int anInt7430;
    static int anInt7431 = 512;
    int[] anIntArray7432;
    NPC aClass365_Sub1_Sub1_Sub2_Sub1_7433;
    Player aClass365_Sub1_Sub1_Sub2_Sub2_7434;
    ObjectDefinitions aClass432_7435;
    static int anInt7436 = 1;
    boolean aBoolean7437;
    static int anInt7438 = 3;
    int anInt7439 = 0;
    static HashTable aClass437_7440;
    int anInt7441;
    static int anInt7442 = 0;
    int anInt7443;
    boolean aBoolean7444;
    Class298_Sub23 aClass298_Sub23_7445;
    Class298_Sub26_Sub1 aClass298_Sub26_Sub1_7446;
    Class298_Sub19_Sub2 aClass298_Sub19_Sub2_7447;
    static int anInt7448 = 2;
    int anInt7449;
    int anInt7450;
    boolean aBoolean7451;
    int anInt7452;
    Class298_Sub26_Sub1 aClass298_Sub26_Sub1_7453;
    int anInt7454;
    static Class453 aClass453_7455 = new Class453();
    public static int anInt7456;

    void method3519(int i) {
	try {
	    int i_0_ = 391847895 * this.anInt7443;
	    boolean bool = this.aBoolean7444;
	    if (null != this.aClass432_7435) {
		ObjectDefinitions class432 = (this.aClass432_7435.method5777((client.anInt8724 * 1596783995 == 0 ? (Interface23) Class87.anInterface23_796 : Class128.aClass148_6331), 2115683030));
		if (null != class432) {
		    this.anInt7443 = class432.anInt5422 * 128561991;
		    this.aBoolean7444 = class432.aBoolean5426;
		    this.anInt7430 = 1644605369 * (-161350689 * class432.anInt5408 << 9);
		    this.anInt7449 = -1807698503 * class432.anInt5425;
		    this.anInt7450 = 763489431 * class432.anInt5427;
		    this.anInt7422 = class432.anInt5428 * 1972784599;
		    this.anIntArray7432 = class432.anIntArray5429;
		    this.aBoolean7451 = class432.aBoolean5395;
		    this.anInt7452 = class432.anInt5439 * -1937096719;
		    this.anInt7441 = class432.anInt5438 * 1100795;
		} else {
		    this.anInt7443 = -502744039;
		    this.aBoolean7444 = false;
		    this.anInt7430 = 0;
		    this.anInt7449 = 0;
		    this.anInt7450 = 0;
		    this.anInt7422 = 0;
		    this.anIntArray7432 = null;
		    this.aBoolean7451 = false;
		    this.anInt7452 = -1197363456;
		    this.anInt7441 = -900102912;
		    this.anInt7429 = 0;
		}
	    } else if ((this.aClass365_Sub1_Sub1_Sub2_Sub1_7433) != null) {
		int i_1_ = (Class125.method1398((this.aClass365_Sub1_Sub1_Sub2_Sub1_7433), 2054416095));
		if (i_1_ != i_0_) {
		    this.anInt7443 = i_1_ * 502744039;
		    NPCDefinitions class503 = (this.aClass365_Sub1_Sub1_Sub2_Sub1_7433.aClass503_10190);
		    if (class503.anIntArray6188 != null)
			class503 = class503.method6240(Class128.aClass148_6331, 1733144869);
		    if (class503 != null) {
			this.anInt7430 = ((1525111487 * class503.anInt6140 << 9) * 1644605369);
			this.anInt7429 = 406226903 * (-1422618341 * class503.anInt6175 << 9);
			this.anInt7449 = -1283486135 * class503.anInt6179;
			this.aBoolean7444 = class503.aBoolean6180;
			this.anInt7452 = class503.anInt6161 * 1142643823;
			this.anInt7441 = class503.anInt6190 * 1682293055;
		    } else {
			this.anInt7429 = 0;
			this.anInt7430 = 0;
			this.anInt7449 = 0;
			this.aBoolean7444 = (this.aClass365_Sub1_Sub1_Sub2_Sub1_7433.aClass503_10190.aBoolean6180);
			this.anInt7452 = -1197363456;
			this.anInt7441 = -900102912;
		    }
		}
	    } else if ((this.aClass365_Sub1_Sub1_Sub2_Sub2_7434) != null) {
		this.anInt7443 = (Class375.method4652((this.aClass365_Sub1_Sub1_Sub2_Sub2_7434), 906629819)) * 502744039;
		this.aBoolean7444 = (this.aClass365_Sub1_Sub1_Sub2_Sub2_7434.aBoolean10196);
		this.anInt7430 = (((this.aClass365_Sub1_Sub1_Sub2_Sub2_7434.anInt10214) * 780357347) << 9) * 1644605369;
		this.anInt7429 = 0;
		this.anInt7449 = -1138033583 * (this.aClass365_Sub1_Sub1_Sub2_Sub2_7434.anInt10215);
		this.anInt7452 = -1197363456;
		this.anInt7441 = -900102912;
	    }
	    if ((this.anInt7443 * 391847895 != i_0_ || bool != this.aBoolean7444) && this.aClass298_Sub19_Sub2_7447 != null) {
		Class285.aClass298_Sub19_Sub4_3083.method3048(this.aClass298_Sub19_Sub2_7447);
		this.aClass298_Sub19_Sub2_7447 = null;
		this.aClass298_Sub26_Sub1_7446 = null;
		this.aClass298_Sub23_7445 = null;
	    }
	}
	catch (RuntimeException runtimeexception) {
	    throw Class346.method4175(runtimeexception, new StringBuilder().append("abz.b(").append(')').toString());
	}
    }

    static {
	aClass437_7440 = new HashTable(16);
    }

    Class298_Sub41() {
	/* empty */
    }
}
