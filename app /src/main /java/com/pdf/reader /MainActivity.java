import java.util.*;
import java.util.stream.Collectors;

public class MedicalBattleGame {
    
    // ==================== 枚举定义 ====================
    enum PlayerRole { DOCTOR, DISEASE }
    enum School { CLASSICAL_FORMULA, WARM_DISEASE, GOLDEN_NEEDLE, EARTH_TONIFYING }
    enum HerbProperty { COLD, COOL, NEUTRAL, WARM, HOT }
    enum DiseaseType { ILLNESS, FATIGUE, INJURY }
    enum GameResult { WIN, LOSE, DRAW, PENDING }
    enum Rarity { COMMON, RARE, EPIC, LEGENDARY }
    
    // ==================== 核心类定义 ====================
    
    // 基础卡牌类
    static abstract class Card {
        String id;
        String name;
        String description;
        int level;
        Rarity rarity;
        
        public Card(String name, int level, Rarity rarity) {
            this.id = UUID.randomUUID().toString();
            this.name = name;
            this.level = level;
            this.rarity = rarity;
        }
        
        abstract String getType();
    }
    
    // 药材卡
    static class HerbCard extends Card {
        HerbProperty property;
        String channel; // 归经
        int basePower;
        
        public HerbCard(String name, HerbProperty property, String channel, int basePower) {
            super(name, 1, Rarity.COMMON);
            this.property = property;
            this.channel = channel;
            this.basePower = basePower;
            this.description = name + " (" + property + ")，归" + channel + "经";
        }
        
        @Override
        String getType() { return "HERB"; }
    }
    
    // 穴位卡
    static class AcupointCard extends Card {
        String meridian; // 经络
        int basePower;
        
        public AcupointCard(String name, String meridian, int basePower) {
            super(name, 1, Rarity.COMMON);
            this.meridian = meridian;
            this.basePower = basePower;
            this.description = name + " (" + meridian + "经)";
        }
        
        @Override
        String getType() { return "ACUPOINT"; }
    }
    
    // 症状卡
    static class SymptomCard extends Card {
        DiseaseType type;
        int baseDamage;
        
        public SymptomCard(String name, DiseaseType type, int baseDamage) {
            super(name, 1, Rarity.COMMON);
            this.type = type;
            this.baseDamage = baseDamage;
            this.description = name + " (" + type + ")";
        }
        
        @Override
        String getType() { return "SYMPTOM"; }
    }
    
    // 方剂卡
    static class FormulaCard extends Card {
        List<HerbCard> herbs;
        int effect;
        
        public FormulaCard(String name, List<HerbCard> herbs) {
            super(name, herbs.size(), Rarity.RARE);
            this.herbs = herbs;
            this.effect = calculateEffect(herbs);
            this.description = name + "：由" + herbs.stream()
                .map(h -> h.name)
                .collect(Collectors.joining("、")) + "组成";
        }
        
        private int calculateEffect(List<HerbCard> herbs) {
            return herbs.stream().mapToInt(h -> h.basePower).sum() * 2;
        }
        
        @Override
        String getType() { return "FORMULA"; }
    }
    
    // 针法卡
    static class NeedleMethodCard extends Card {
        List<AcupointCard> acupoints;
        int effect;
        
        public NeedleMethodCard(String name, List<AcupointCard> acupoints) {
            super(name, acupoints.size(), Rarity.RARE);
            this.acupoints = acupoints;
            this.effect = calculateEffect(acupoints);
            this.description = name + "针法：" + acupoints.stream()
                .map(a -> a.name)
                .collect(Collectors.joining("、"));
        }
        
        private int calculateEffect(List<AcupointCard> acupoints) {
            return acupoints.stream().mapToInt(a -> a.basePower).sum() * 3;
        }
        
        @Override
        String getType() { return "NEEDLE_METHOD"; }
    }
    
    // 综合征卡
    static class SyndromeCard extends Card {
        List<SymptomCard> symptoms;
        int damage;
        
        public SyndromeCard(String name, List<SymptomCard> symptoms) {
            super(name, symptoms.size(), Rarity.RARE);
            this.symptoms = symptoms;
            this.damage = calculateDamage(symptoms);
            this.description = name + "综合征：" + symptoms.stream()
                .map(s -> s.name)
                .collect(Collectors.joining("、"));
        }
        
        private int calculateDamage(List<SymptomCard> symptoms) {
            return symptoms.stream().mapToInt(s -> s.baseDamage).sum() * 2;
        }
        
        @Override
        String getType() { return "SYNDROME"; }
    }
    
    // 玩家类
    static class Player {
        String name;
        PlayerRole role;
        School school;
        int level;
        String title;
        int health; // 正气值/邪气值
        int maxHealth;
        List<Card> handCards;
        List<Card> deck;
        
        public Player(String name, PlayerRole role, School school) {
            this.name = name;
            this.role = role;
            this.school = school;
            this.level = 1;
            this.title = role == PlayerRole.DOCTOR ? "药徒" : "初阶病邪";
            this.maxHealth = 100;
            this.health = maxHealth;
            this.handCards = new ArrayList<>();
            this.deck = new ArrayList<>();
        }
        
        void drawCards(int count) {
            for (int i = 0; i < count && !deck.isEmpty(); i++) {
                handCards.add(deck.remove(0));
            }
        }
        
        boolean isAlive() {
            return health > 0;
        }
        
        void takeDamage(int damage) {
            health -= damage;
            if (health < 0) health = 0;
        }
        
        void heal(int amount) {
            health += amount;
            if (health > maxHealth) health = maxHealth;
        }
        
        String getHealthBar() {
            int bars = health * 20 / maxHealth;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < 20; i++) {
                sb.append(i < bars ? "█" : "░");
            }
            sb.append("] ").append(health).append("/").append(maxHealth);
            return sb.toString();
        }
    }
    
    // 医案类
    static class MedicalCase {
        String description;
        List<SymptomCard> symptoms;
        String tongue; // 舌象
        String pulse;  // 脉象
        
        public MedicalCase(String description, List<SymptomCard> symptoms, String tongue, String pulse) {
            this.description = description;
            this.symptoms = symptoms;
            this.tongue = tongue;
            this.pulse = pulse;
        }
        
        void display() {
            System.out.println("\n========== 医案 ==========");
            System.out.println("症状：" + description);
            System.out.println("舌象：" + tongue);
            System.out.println("脉象：" + pulse);
            System.out.print("主要表现：");
            for (SymptomCard s : symptoms) {
                System.out.print(s.name + "(" + s.type + ") ");
            }
            System.out.println("\n==========================");
        }
    }
    
    // 游戏引擎
    static class GameEngine {
        Player doctor;
        Player disease;
        MedicalCase currentCase;
        int turn;
        boolean gameOver;
        GameResult result;
        
        public GameEngine(Player doctor, Player disease) {
            this.doctor = doctor;
            this.disease = disease;
            this.turn = 1;
            this.gameOver = false;
            this.result = GameResult.PENDING;
        }
        
        void startGame() {
            System.out.println("======= 医战开始！=======");
            System.out.println("医生：" + doctor.name + " (" + doctor.school + ")");
            System.out.println("疾病：" + disease.name + " (" + disease.school + ")");
            
            // 初始化卡组
            initializeDecks();
            
            // 抽起始手牌
            doctor.drawCards(5);
            disease.drawCards(5);
            
            // 生成医案
            generateMedicalCase();
        }
        
        void initializeDecks() {
            // 医生卡组
            doctor.deck.add(new HerbCard("麻黄", HerbProperty.WARM, "肺膀胱", 15));
            doctor.deck.add(new HerbCard("桂枝", HerbProperty.WARM, "心肺膀胱", 12));
            doctor.deck.add(new HerbCard("石膏", HerbProperty.COLD, "肺胃", 20));
            doctor.deck.add(new HerbCard("甘草", HerbProperty.NEUTRAL, "心肺脾胃", 10));
            doctor.deck.add(new AcupointCard("合谷", "手阳明大肠经", 15));
            doctor.deck.add(new AcupointCard("足三里", "足阳明胃经", 18));
            doctor.deck.add(new AcupointCard("大椎", "督脉", 20));
            
            // 疾病卡组
            disease.deck.add(new SymptomCard("发热", DiseaseType.ILLNESS, 15));
            disease.deck.add(new SymptomCard("咳嗽", DiseaseType.ILLNESS, 12));
            disease.deck.add(new SymptomCard("头痛", DiseaseType.ILLNESS, 10));
            disease.deck.add(new SymptomCard("乏力", DiseaseType.FATIGUE, 8));
            disease.deck.add(new SymptomCard("外伤", DiseaseType.INJURY, 20));
            
            Collections.shuffle(doctor.deck);
            Collections.shuffle(disease.deck);
        }
        
        void generateMedicalCase() {
            List<SymptomCard> symptoms = Arrays.asList(
                new SymptomCard("发热", DiseaseType.ILLNESS, 15),
                new SymptomCard("咳嗽", DiseaseType.ILLNESS, 12),
                new SymptomCard("恶寒", DiseaseType.ILLNESS, 10)
            );
            currentCase = new MedicalCase(
                "患者发热3日，体温39℃，咳嗽痰黄，口渴欲饮",
                symptoms,
                "舌红苔黄",
                "脉浮数"
            );
        }
        
        void playTurn() {
            if (gameOver) return;
            
            System.out.println("\n======= 第 " + turn + " 回合 =======");
            System.out.println("医生" + doctor.getHealthBar() + " vs 疾病" + disease.getHealthBar());
            
            // 显示医案
            currentCase.display();
            
            // 显示手牌
            displayHandCards();
            
            // 医生行动
            doctorAction();
            if (checkGameOver()) return;
            
            // 疾病行动
            diseaseAction();
            checkGameOver();
            
            // 抽牌
            doctor.drawCards(1);
            disease.drawCards(1);
            
            turn++;
        }
        
        void displayHandCards() {
            System.out.println("\n【医生手牌】");
            for (int i = 0; i < doctor.handCards.size(); i++) {
                Card card = doctor.handCards.get(i);
                System.out.println(i + ". " + card.name + " - " + card.description);
            }
            
            System.out.println("\n【疾病手牌】");
            for (int i = 0; i < disease.handCards.size(); i++) {
                Card card = disease.handCards.get(i);
                System.out.println(i + ". " + card.name + " - " + card.description);
            }
        }
        
        void doctorAction() {
            System.out.println("\n>>> 医生行动：选择治疗方案");
            Scanner scanner = new Scanner(System.in);
            
            // 简单AI：随机选择或合成
            Random rand = new Random();
            int choice = rand.nextInt(3);
            
            switch (choice) {
                case 0:
                    // 使用单味药
                    if (!doctor.handCards.isEmpty()) {
                        Card card = doctor.handCards.get(rand.nextInt(doctor.handCards.size()));
                        if (card instanceof HerbCard) {
                            HerbCard herb = (HerbCard) card;
                            int healAmount = herb.basePower * getSchoolMultiplier(doctor.school);
                            doctor.heal(healAmount);
                            disease.takeDamage(healAmount / 2);
                            System.out.println("医生使用【" + herb.name + "】治疗，回复" + healAmount + "点正气，对疾病造成" + (healAmount/2) + "点伤害");
                            doctor.handCards.remove(card);
                        }
                    }
                    break;
                    
                case 1:
                    // 尝试合成方剂
                    List<HerbCard> herbs = doctor.handCards.stream()
                        .filter(c -> c instanceof HerbCard)
                        .map(c -> (HerbCard) c)
                        .collect(Collectors.toList());
                    
                    if (herbs.size() >= 2) {
                        List<HerbCard> selected = herbs.subList(0, Math.min(2, herbs.size()));
                        FormulaCard formula = new FormulaCard("临时方剂", selected);
                        
                        int effect = formula.effect * getSchoolMultiplier(doctor.school);
                        doctor.heal(effect);
                        disease.takeDamage(effect);
                        
                        System.out.println("医生合成方剂【" + formula.name + "】，治疗效果" + effect + "点");
                        
                        // 移除使用的卡牌
                        doctor.handCards.removeAll(selected);
                    }
                    break;
                    
                case 2:
                    // 使用穴位
                    List<AcupointCard> points = doctor.handCards.stream()
                        .filter(c -> c instanceof AcupointCard)
                        .map(c -> (AcupointCard) c)
                        .collect(Collectors.toList());
                    
                    if (!points.isEmpty()) {
                        AcupointCard point = points.get(rand.nextInt(points.size()));
                        int effect = point.basePower * 2;
                        disease.takeDamage(effect);
                        System.out.println("医生针刺【" + point.name + "】，对疾病造成" + effect + "点伤害");
                        doctor.handCards.remove(point);
                    }
                    break;
            }
        }
        
        void diseaseAction() {
            System.out.println("\n>>> 疾病行动：发动攻击");
            Random rand = new Random();
            
            if (!disease.handCards.isEmpty()) {
                Card card = disease.handCards.get(rand.nextInt(disease.handCards.size()));
                
                if (card instanceof SymptomCard) {
                    SymptomCard symptom = (SymptomCard) card;
                    int damage = symptom.baseDamage;
                    
                    // 疾病流派加成
                    if (disease.school == School.WARM_DISEASE && symptom.type == DiseaseType.ILLNESS) {
                        damage = (int)(damage * 1.5);
                    }
                    
                    doctor.takeDamage(damage);
                    System.out.println("疾病发动【" + symptom.name + "】，对医生造成" + damage + "点伤害");
                    disease.handCards.remove(card);
                }
            }
        }
        
        int getSchoolMultiplier(School school) {
            switch (school) {
                case CLASSICAL_FORMULA: return 2; // 经方派方剂加成
                case GOLDEN_NEEDLE: return 3;     // 金针派针灸加成
                case EARTH_TONIFYING: return 2;   // 补土派治疗加成
                default: return 1;
            }
        }
        
        boolean checkGameOver() {
            if (!doctor.isAlive()) {
                gameOver = true;
                result = GameResult.LOSE;
                System.out.println("\n❌ 治疗失败！疾病获胜！");
                System.out.println(disease.name + "晋升为更强大的病邪！");
                return true;
            }
            
            if (!disease.isAlive()) {
                gameOver = true;
                result = GameResult.WIN;
                System.out.println("\n✅ 治疗成功！医生获胜！");
                System.out.println(doctor.name + "医术精进，获得称号【悬壶济世】");
                return true;
            }
            
            if (turn > 10) {
                gameOver = true;
                result = GameResult.DRAW;
                System.out.println("\n⚖️  战斗超时，平局！");
                return true;
            }
            
            return false;
        }
    }
    
    // ==================== 主程序 ====================
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("========== 医战游戏 ==========");
        System.out.println("1. 开始新游戏");
        System.out.println("2. 退出");
        System.out.print("请选择: ");
        
        int choice = scanner.nextInt();
        
        if (choice == 1) {
            // 创建玩家
            Player doctor = new Player("张仲景", PlayerRole.DOCTOR, School.CLASSICAL_FORMULA);
            Player disease = new Player("温邪", PlayerRole.DISEASE, School.WARM_DISEASE);
            
            // 创建游戏引擎
            GameEngine game = new GameEngine(doctor, disease);
            
            // 开始游戏
            game.startGame();
            
            // 游戏主循环
            while (!game.gameOver) {
                game.playTurn();
                
                // 暂停一下，方便观察
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            // 显示最终结果
            System.out.println("\n======= 游戏结束 =======");
            System.out.println("总回合数: " + game.turn);
            System.out.println("最终结果: " + game.result);
            System.out.println("医生剩余血量: " + doctor.health);
            System.out.println("疾病剩余血量: " + disease.health);
            
            // 显示卡牌统计
            System.out.println("\n【本局卡牌使用统计】");
            System.out.println("医生手牌剩余: " + doctor.handCards.size());
            System.out.println("疾病手牌剩余: " + disease.handCards.size());
        }
        
        scanner.close();
        System.out.println("\n感谢游玩医战游戏！");
    }
}
