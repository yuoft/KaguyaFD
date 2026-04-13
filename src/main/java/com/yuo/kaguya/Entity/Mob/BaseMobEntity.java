package com.yuo.kaguya.Entity.Mob;

import com.github.tartaricacid.touhoulittlemaid.api.entity.IMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.monster.EntityFairy;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.yuo.kaguya.Entity.DanmakuColor;
import com.yuo.kaguya.Entity.DanmakuShootHelper;
import com.yuo.kaguya.Entity.DanmakuType;
import com.yuo.kaguya.Entity.ModEntityTypes;
import com.yuo.kaguya.Item.ModItems;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class BaseMobEntity extends Monster implements IMaid, RangedAttackMob {
    protected String modelId;

    public BaseMobEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
        this.setModelId(getRegId(type.getDescriptionId()));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount % 20 == 0) {
            this.heal(1);
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RangeAttackGoal(this, 6, 1.0d)); // 远程攻击
        this.goalSelector.addGoal(2, new ModAttackGoal(this, 1.2D, false, 6)); // 近战攻击
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D)); // 随机移动
        this.goalSelector.addGoal(4, new MoveTowardsTargetGoal(this, 1.0D, 32.0F)); // 如果看到玩家，向玩家移动
        // 看向实体
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, EntityMaid.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        // 攻击目标选择
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Animal.class, false));
    }

    //属性
    @NotNull
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.1D)
                .add(Attributes.MAX_HEALTH, 64.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ARMOR, 10.0d);
    }

    @Override
    public void setPersistenceRequired() {
        super.setPersistenceRequired();
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return InitSounds.FAIRY_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return InitSounds.MAID_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return InitSounds.MAID_DEATH.get();
    }

    @Override
    public String getModelId() {
        return getMaidModelId(this.modelId);
    }

    @Override
    public Mob asEntity() {
        return this;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        float damageBase = 1.0F;
        Difficulty difficulty = target.level().getDifficulty();
        switch (difficulty) {
            case NORMAL -> damageBase = 1.5F;
            case HARD -> damageBase = 2.0F;
        }
        DanmakuColor color = DanmakuColor.GRAY;
        if (this.getType() == ModEntityTypes.HAKUREI_REIMU.get()){
            color = DanmakuColor.RED;
        } else if (this.getType() == ModEntityTypes.KIRISAME_MARISA.get()){
            color = DanmakuColor.BLACK;
        } else if (this.getType() == ModEntityTypes.RUMIA.get()){
            color = DanmakuColor.BLUE;
        } else if (this.getType() == ModEntityTypes.CIRNO.get()){
            color = DanmakuColor.LIGHT_BLUE;
        } else if (this.getType() == ModEntityTypes.KOCHIYA_SANAE.get()){
            color = DanmakuColor.GREEN;
        }
        DanmakuShootHelper.shootDanmakuMob(this.level(), this, target, damageBase, color, DanmakuType.random(this.level().random));
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean b) {
        super.dropCustomDeathLoot(source, looting, b);
        Level level = this.level();
        RandomSource random = level.random;
        if (this.getType() == ModEntityTypes.CIRNO.get()){
            if (random.nextDouble() < 0.2 + looting * 0.05)
                this.spawnAtLocation(ModItems.icicleSword.get(), 1);
        }
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    private String getRegId(String s){
        String[] split = s.split("\\.");
        return split[split.length - 1];
    }

    protected String getMaidModelId(String id){
        if (id != null && !id.isEmpty()){
            for (String s : MODEL_IDS) {
                if (s.equals(id)) {
                    return "touhou_little_maid:" + s;
                }
            }

        }
        return "touhou_little_maid:hakurei_reimu";
    }

    private static final List<String> MODEL_IDS = List.of(
            "hakurei_reimu", // 博丽灵梦
            "hakurei_reimu_2", // 博丽灵梦（2）
            "hakurei_reimu_type_b", // 博丽灵梦（B型）
            "kirisame_marisa", // 雾雨魔理沙
            "kirisame_marisa_2", // 雾雨魔理沙（2）
            "rumia", // 露米娅
            "daiyousei", // 大妖精
            "cirno", // 琪露诺
            "cirno_maid", // 琪露诺（女仆）
            "cirno_tan", // 琪露诺（Tan）
            "cirno_slim", // 琪露诺（Slim）
            "hong_meiling", // 红美铃
            "koakuma", // 小恶魔
            "patchouli_knowledge", // 帕秋莉·诺蕾姬
            "patchouli_knowledge_2", // 帕秋莉·诺蕾姬（2）
            "izayoi_sakuya", // 十六夜咲夜
            "remilia_scarlet", // 蕾米莉亚·斯卡蕾特
            "remilia_scarlet_2", // 蕾米莉亚·斯卡蕾特（2）
            "flandre_scarlet", // 芙兰朵露·斯卡蕾特
            "letty_whiterock", // 蕾蒂·霍瓦特洛克
            "chen", // 橙
            "alice_margatroid", // 爱丽丝·玛格特罗伊德
            "shanghai_doll", // 上海人偶
            "hourai_doll", // 蓬莱人偶
            "goliath_doll", // 歌利亚人偶
            "lily_white", // 莉莉白
            "lily_black", // 莉莉黑
            "lunasa_prismriver", // 露娜萨·普莉兹姆利巴
            "merlin_prismriver", // 梅露兰·普莉兹姆利巴
            "lyrica_prismriver", // 莉莉卡·普莉兹姆利巴
            "konpaku_youmu", // 魂魄妖梦
            "saigyouji_yuyuko", // 西行寺幽幽子
            "yakumo_ran", // 八云蓝
            "yukari_yakumo", // 八云紫
            "ibuki_suika", // 伊吹萃香
            "wriggle_nightbug", // 莉格露·奈特巴格
            "mystia_lorelei", // 米斯蒂娅·萝蕾拉
            "keine_kamishirasawa", // 上白泽慧音
            "keine_kamishirasawa_2", // 上白泽慧音（2）
            "tewi_inaba", // 因幡帝
            "reisen_udongein_inaba", // 铃仙·优昙华院·因幡
            "yagokoro_eirin", // 八意永琳
            "houraisan_kaguya", // 蓬莱山辉夜
            "fujiwara_no_mokou", // 藤原妹红
            "syameimaru_aya", // 射命丸文
            "medicine_melancholy", // 梅蒂欣·梅兰可莉
            "kazami_yuka", // 风见幽香
            "onozuka_komachi", // 小野塚小町
            "shikieiki_yamaxanadu", // 四季映姬·亚玛萨那度
            "aki_sizuha", // 秋静叶
            "minoriko_aki", // 秋穰子
            "kagiyama_hina", // 键山雏
            "kawasiro_nitori", // 河城荷取
            "inubashiri_momizi", // 犬走椛
            "kochiya_sanae", // 东风谷早苗
            "yasaka_kanako", // 八坂神奈子
            "moriya_suwako", // 洩矢诹访子
            "nagae_iku", // 永江衣玖
            "hinanawi_tenshi", // 比那名居天子
            "kisume", // 琪斯美
            "kurodani_yamame", // 黑谷山女
            "mizuhashi_parsee", // 水桥帕露西
            "hoshiguma_yugi", // 星熊勇仪
            "komeiji_satori", // 古明地觉
            "kaenbyou_rin", // 火焰猫燐
            "reiuji_utsuho", // 灵乌路空
            "komeiji_koishi", // 古明地恋
            "komeiji_koishi_2", // 古明地恋（2）
            "nazrin", // 娜兹玲
            "tatara_kogasa", // 多多良小伞
            "kumoi_ichirin", // 云居一轮
            "murasa_minamitsu", // 村纱水蜜
            "toramaru_shou", // 寅丸星
            "hijiri_byakuren", // 圣白莲
            "houjuu_nue", // 封兽鵺
            "himekaidou_hatate", // 姬海棠果
            "kasodani_kyouko", // 幽谷响子
            "miyako_yoshika", // 宫古芳香
            "kaku_seiga", // 霍青娥
            "mononobe_no_futo", // 物部布都
            "toyosatomimi_no_miko", // 丰聪耳神子
            "soga_no_toziko", // 苏我屠自古
            "hata_no_kokoro", // 秦心
            "hutatsuiwa_mamizou", // 二岩猯藏
            "wakasagihime", // 若鹭姬
            "sekibanki", // 赤蛮奇
            "imaizumi_kagerou", // 今泉影狼
            "tsukumo_yatsuhashi", // 九十九八桥
            "tsukumo_benben", // 九十九弁弁
            "kijin_seija", // 鬼人正邪
            "sukuna_shinmyoumaru", // 少名针妙丸
            "horikawa_raiko", // 堀川雷鼓
            "usami_sumireko", // 宇佐见堇子
            "seiran", // 清兰
            "ringo", // 铃瑚
            "doremy_sweet", // 哆来咪·苏伊特
            "kisin_sagume", // 稀神探女
            "clownpiece", // 克劳恩皮丝
            "junko", // 纯狐
            "hecatia_lapislazuli", // 赫卡提娅·拉碧斯拉祖利
            "yorigami_jyoon", // 依神紫苑
            "yorigami_shion", // 依神女苑
            "eternity_larva", // 爱塔妮缇·拉尔瓦
            "sakata_nemuno", // 坂田合欢
            "komano_aunn", // 高丽野阿吽
            "yatadera_narumi", // 矢田寺成美
            "nishida_satono", // 尔子田里乃
            "teireid_mai", // 丁礼田舞
            "matara_okina", // 摩多罗隐岐奈
            "ebisu_eika", // 戎璎花
            "ushizaki_urumi", // 牛崎润美
            "niwatari_kutaka", // 庭渡久侘歌
            "kitcho_yachie", // 吉吊八千慧
            "joutougu_mayumi", // 杖刀偶磨弓
            "haniyasushin_keiki", // 埴安神袿姬
            "kurokoma_saki", // 骊驹早鬼
            "toutetsu_yuma", // 饕餮尤魔
            "goutokuzi_mike", // 豪德寺三花
            "yamashiro_takane", // 山城高岭
            "komakusa_sannyo", // 驹草山如
            "tamatsukuri_misumaru", // 玉造魅须丸
            "kudamaki_tsukasa", // 管狐
            "iizunamaru_megumu", // 饭纲丸龙
            "tenkyu_chimata", // 天弓千亦
            "himemushi_momoyo", // 姬虫百百世
            "son_biten", // 孙美天
            "mitsugashira_enoko", // 三头慧之子
            "tenkajin_chiyari", // 天火人血枪
            "yomotsu_hisami", // 黄泉醜女
            "nippaku_zanmu", // 日白残无
            "ubame_chirizuka", // 豫母都日狭美
            "chimi_houjuu", // 珍美
            "nareko_michigami", // 七圣
            "ariya_iwanaga", // 岩永
            "morichika_rinnosuke", // 森近霖之助
            "tokiko", // 朱鹭子
            "sunny_milk", // 桑尼·米尔克
            "luna_child", // 露娜·切云德
            "star_sapphire", // 斯塔·萨菲雅
            "watatsuki_no_toyohime", // 绵月丰姬
            "watatsuki_no_yorihime", // 绵月依姬
            "reisen", // 铃仙（月兔）
            "hieda_no_akyuu", // 稗田阿求
            "ibaraki_kasen", // 茨木华扇
            "ibaraki_kasen_2", // 茨木华扇（2）
            "motoori_kosuzu", // 本居小铃
            "miyadeguchi_mizuchi", // 宫出口瑞灵
            "miyoi_okunoda", // 奥野田美宵
            "usami_renko", // 宇佐见莲子
            "maribel_hearn", // 玛艾露贝莉·赫恩
            "satsuki_rin", // 五月雨
            "moesumika" // 萌澄果
    );

}
