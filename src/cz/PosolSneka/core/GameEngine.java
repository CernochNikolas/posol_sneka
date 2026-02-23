package cz.PosolSneka.core;

import cz.PosolSneka.actors.Player;
import cz.PosolSneka.world.Direction;
import cz.PosolSneka.world.Room;
import cz.PosolSneka.world.World;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Hlavní herní engine textové hry Posol Šneka.
 *
 * Zpracovává herní logiku, pohyb hráče, práci s inventářem,
 * interakce s předměty a vyhodnocení důležitých událostí.
 *
 * @author Niki Ci
 */
public class GameEngine {

    private final World world;
    private final Player player;
    private final GameState state;

    /**
     * Vytvoří herní engine s výchozím herním stavem.
     *
     * @param world herní svět
     * @param player hráč
     */
    public GameEngine(World world, Player player) {
        this(world, player, new GameState());
    }

    /**
     * Vytvoří herní engine s předaným herním stavem.
     *
     * @param world herní svět
     * @param player hráč
     * @param state aktuální stav hry
     */
    public GameEngine(World world, Player player, GameState state) {
        this.world = Objects.requireNonNull(world);
        this.player = Objects.requireNonNull(player);
        this.state = Objects.requireNonNull(state);
        seedImportantItems();
    }

    /**
     * Vrátí herní svět.
     *
     * @return instance světa
     */
    public World getWorld() { return world; }

    /**
     * Vrátí hráče.
     *
     * @return instance hráče
     */
    public Player getPlayer() { return player; }

    /**
     * Vrátí aktuální stav hry.
     *
     * @return instance herního stavu
     */
    public GameState getState() { return state; }

    /**
     * Zjistí, zda je hra vyhraná.
     *
     * @return {@code true}, pokud je hra vyhraná
     */
    public boolean isGameWon() {
        return state.isGameWon();
    }

    /**
     * Vypíše úvodní text hry.
     */
    public void printWelcome() {
        System.out.println("=== Posol sneka ===");
        System.out.println("Textova verze (stealth-puzzle) - skolske demo");
        System.out.println("Napis 'napoveda' pro seznam prikazu.\n");
    }

    /**
     * Vykreslí aktuální stav lokace (název, východy, předměty a nápovědy).
     */
    public void render() {
        Room r = world.getCurrentRoom();
        System.out.println("\n---");
        System.out.println("Jsi v lokaci: " + r.getName() + " [" + r.getId() + "]");

        List<String> exits = r.getExits().keySet().stream()
                .map(Enum::name)
                .sorted()
                .toList();
        System.out.println("Vychody: " + (exits.isEmpty() ? "(zadne)" : exits));

        if (r.getItems().isEmpty()) {
            System.out.println("Predmety: (zadne)");
        } else {
            System.out.println("Predmety: " + r.getItems());
        }

        System.out.println(shortRoomHint(r));
        System.out.println(snailStatusHint());
    }

    /**
     * Vypíše seznam dostupných příkazů.
     */
    public void printHelp() {
        System.out.println("""
                Prikazy:
                  jdi <smer>     (nebo jen: sever/jih/vychod/zapad/nahoru/dolu)
                  cekej
                  prohledej
                  seber <predmet>
                  pouzij <predmet> [na cil]
                  cti [predmet]
                  mluv
                  inventar
                  nasyp sul
                  kric
                  napoveda
                  konec
                """);
    }

    /**
     * Vypíše obsah inventáře hráče.
     */
    public void showInventory() {
        var inv = player.getInventory();
        if (inv.isEmpty()) {
            System.out.println("Inventar je prazdny.");
        } else {
            System.out.println("Inventar: " + inv.list());
        }
    }

    /**
     * Pokusí se přesunout hráče zadaným směrem.
     * Pokud směr neexistuje nebo je cesta uzamčená, hráč zůstává na místě.
     *
     * @param dir směr pohybu
     */
    public void movePlayer(Direction dir) {
        if (dir == null) {
            System.out.println("Neznamy smer.");
            return;
        }

        Room before = world.getCurrentRoom();
        Room target = before.getExit(dir);

        // dle zadani: pokud exit neexistuje, zustavas na miste
        if (target == null) {
            System.out.println("Tim smerem to nejde. Zustavas v " + before.getName() + ".");
            advanceTurn();
            return;
        }

        // zamek / podminka na exitu
        String lockTag = before.getExitLock(dir);
        if (lockTag != null && !canPassLock(lockTag)) {
            printLockMessage(lockTag);
            advanceTurn();
            return;
        }

        world.move(dir);
        Room after = world.getCurrentRoom();
        System.out.println("Presel jsi do: " + after.getName());

        boolean turnAlreadyConsumed = onEnterRoom(after);
        if (!turnAlreadyConsumed && !state.isGameWon()) {
            advanceTurn();

            // po posunu tahu se snek muze presunout k hraci
            if (!state.isGameWon() && isMainSnailInRoom(world.getCurrentRoom())) {
                catchPlayer("Po tvem pohybu se do mistnosti presunul hlavni snek.", false);
                return;
            }
        }
    }

    /**
     * Provede čekání jednoho tahu a posune herní čas.
     */
    public void waitTurn() {
        System.out.println("Chvili cekas a poslouchas zvuky v komplexu...");
        advanceTurn();
        if (isMainSnailInRoom(world.getCurrentRoom())) {
            catchPlayer("Pri cekani do mistnosti prilezl hlavni snek.", false);
            return;
        }
        System.out.println(snailStatusHint());
    }

    /**
     * Prohledá aktuální místnost a vypíše kontextovou nápovědu.
     */
    public void searchCurrentRoom() {
        Room r = world.getCurrentRoom();
        switch (r.getId()) {
            case "cell3" -> {
                System.out.println("Prohledavas celu 3. V rozich jsou skrabance a podezrely otvor.");
                if (!state.knowsTunnelHint()) {
                    System.out.println("Bez rady od vezne te tunel nenapadne. Zkus 'mluv'.");
                } else {
                    System.out.println("Diky rade od vezne uz vis, ze vychodni stena skryva tunel do ucebny.");
                }
            }
            case "cell2" -> System.out.println("V cele 2 lezi kostlivec. Vidis u nej kost.");
            case "classroom" -> System.out.println("Byvala ucebna. Prach, stare lavice a utrzeny kus sesitu.");
            case "kitchen" -> System.out.println("Kuchyn vypada zlovestne. Jsou tu dvere do skladu soli a vstup do ventilace.");
            case "keyroom" -> {
                if (!state.isBoneUsedOnDog()) {
                    System.out.println("Pes vrci a hlida klice. Bez kosti to bude pruser.");
                } else {
                    System.out.println("Pes je zabaveny kosti. Mas kratke okno na sebrani klicu.");
                }
            }
            case "storage" -> System.out.println("Sklad soli: velka kupka na past + male davky pro finale.");
            case "nest" -> {
                if (state.isMainSnailAlive()) {
                    System.out.println("Hnizdo hlavniho sneka. Jsi tu v nebezpeci.");
                } else {
                    System.out.println("Hnizdo je po smrti sneka tissi. U vychodu se ale krouti mali sneci.");
                }
            }
            default -> System.out.println("Prohledal jsi mistnost. Nic zvlastniho navic jsi nenasel.");
        }
    }

    /**
     * Provede dialog/interakci v aktuální místnosti.
     */
    public void talk() {
        Room r = world.getCurrentRoom();

        if ("cell1".equals(r.getId())) {
            if (state.isPrisonerFreed()) {
                if (state.isNotebookRead()) {
                    System.out.println("Vezneny survivor: 'Dobre. Uz vis rytmus. Jdeme do hnizda a ven.'");
                } else {
                    System.out.println("Vezneny survivor: 'Diky! Vezmi sesit z ucebny, bude se hodit.'");
                }
                return;
            }

            state.setKnowsTunnelHint(true);
            System.out.println("Veznen z cely 1 septa: 'V cele 3 je tunel do byvale ucebny. Hledej vychodni stenu.'");
            if (state.isNotebookRead()) {
                System.out.println("Doda: 'Druhou pulku sesitu znam z pameti. Snek casto strida chodbu, hnizdo a kuchyn.'");
            } else {
                System.out.println("Doda: 'A pozor na kuchyn a psa u klicu.'");
            }
            return;
        }

        if ("prison".equals(r.getId())) {
            System.out.println("Slysiš hlas z cely 1. Zkus vejit do 'cell1' a pouzit 'mluv'.");
            return;
        }

        System.out.println("Nikdo tady nema naladu si povidat.");
    }

    /**
     * Pokusí se přečíst předmět (typicky polovinu sešitu) z inventáře.
     *
     * @param itemId identifikátor předmětu (může být i alias)
     */
    public void read(String itemId) {
        String normalized = normalizeItemName(itemId);
        if (normalized == null) normalized = "notebook_half";

        if (!player.getInventory().has(normalized)) {
            System.out.println("Nemáš co cist (chybi '" + normalized + "').");
            return;
        }

        if (!"notebook_half".equals(normalized)) {
            System.out.println("Tohle se cist moc neda.");
            return;
        }

        state.setNotebookRead(true);
        System.out.println("Ctes polovinu sesitu: zapis o rytmu sneka a bezpecnych oknech. Chybi druha cast.");
        System.out.println("Tip: zajdi za veznem z cely 1 ('mluv').");
    }

    /**
     * Pokusí se sebrat předmět v aktuální místnosti.
     * Obsahuje i speciální logiku pro psa a klíče.
     *
     * @param rawItemId název předmětu zadaný hráčem
     */
    public void pickup(String rawItemId) {
        String itemId = normalizeItemName(rawItemId);
        if (itemId == null) {
            System.out.println("Pouziti: seber <predmet>");
            return;
        }

        Room room = world.getCurrentRoom();

        // speciální logika psa u klíčů
        if ("keyroom".equals(room.getId()) && "storage_key".equals(itemId) && !state.isBoneUsedOnDog()) {
            System.out.println("Pes po tobe vyjede a zacne stekat! Bez kosti klice nesebereš.");
            if (state.isMainSnailAlive()) {
                catchPlayer("Stěkot prilakal sneka. Byl jsi chycen.");
            }
            return;
        }

        if ("nest".equals(room.getId()) && "cell1_key".equals(itemId) && state.isMainSnailAlive()) {
            System.out.println("Klic od cely 1 jeste nema z ceho vypadnout. Hlavni snek zije.");
            return;
        }

        if (room.removeItem(itemId)) {
            player.getInventory().add(itemId);
            System.out.println("Sebral jsi: " + itemId);
        } else {
            System.out.println("Tady to neni: " + itemId);
        }
    }

    /**
     * Použije předmět z inventáře v aktuální místnosti.
     *
     * @param rawItemId název předmětu zadaný hráčem
     * @param rawTarget volitelný cíl použití
     */
    public void use(String rawItemId, String rawTarget) {
        String itemId = normalizeItemName(rawItemId);
        String target = normalizeTarget(rawTarget);

        if (itemId == null) {
            System.out.println("Pouziti: pouzij <predmet> [na cil]");
            return;
        }

        if (!player.getInventory().has(itemId)) {
            System.out.println("V inventari nemas: " + itemId);
            return;
        }

        Room r = world.getCurrentRoom();

        if ("bone".equals(itemId)) {
            if (!"keyroom".equals(r.getId())) {
                System.out.println("Kost dává smysl hlavne u psa v mistnosti Klice.");
                return;
            }
            state.setBoneUsedOnDog(true);
            player.getInventory().remove("bone");
            System.out.println("Hodil jsi kost psovi. Pes se zabavil a prestal hlidat klice.");
            return;
        }

        if ("storage_key".equals(itemId)) {
            if (!"kitchen".equals(r.getId())) {
                System.out.println("Tento klic pouzij v kuchyni na dvere do skladu soli.");
                return;
            }
            System.out.println("Mas klic od skladu soli. Ted muzes jit z kuchyne na JIH do skladu.");
            return;
        }

        if ("cell1_key".equals(itemId)) {
            if (!"cell1".equals(r.getId()) && !"prison".equals(r.getId())) {
                System.out.println("Tento klic je od cely 1. Pouzij ho u cely 1.");
                return;
            }
            if (state.isPrisonerFreed()) {
                System.out.println("Cela 1 uz je odemcena.");
                return;
            }
            state.setPrisonerFreed(true);
            System.out.println("Odemkl jsi celu 1. Veznen je volny a pujde s tebou do hnizda.");
            return;
        }

        if (itemId.startsWith("small_salt")) {
            if (!"nest".equals(r.getId())) {
                System.out.println("Malou davku soli vyuzijes az ve finale v hnizde.");
                return;
            }
            if (state.isMainSnailAlive()) {
                System.out.println("Nejdriv musis porazit hlavniho sneka.");
                return;
            }
            if (!state.isPrisonerFreed()) {
                System.out.println("Nejdřív osvoboď vězně z cely 1, pak běžte do finále spolu.");
                return;
            }
            player.getInventory().remove(itemId);
            state.killOneBabySnail();
            System.out.println("Zasypal jsi maleho sneka soli. Zbyva: " + state.getBabySnailsRemaining());
            if (state.getBabySnailsRemaining() == 0) {
                System.out.println("Vchod je volny! Muzes jit na ZAPAD ven.");
            }
            return;
        }

        System.out.println("Tohle pouzit ted nedava smysl.");
    }

    /**
     * Nasype velkou sůl na chodbu jako past na hlavního šneka.
     */
    public void pourSalt() {
        Room r = world.getCurrentRoom();
        if (!"corridor".equals(r.getId())) {
            System.out.println("Past se soli dává smysl hlavně na chodbě.");
            return;
        }

        if (!player.getInventory().has("big_salt")) {
            System.out.println("Nemáš velkou kupku soli ('big_salt').");
            return;
        }

        if (state.isCorridorTrapPlaced()) {
            System.out.println("Past uz je na chodbě pripravená.");
            return;
        }

        player.getInventory().remove("big_salt");
        state.setCorridorTrapPlaced(true);
        System.out.println("Nasypal jsi velkou kupku soli do trasy sneka. Ted ho musis prilakat ('kric').");
    }

    /**
     * Vyvolá hlasitý křik, který může přilákat hlavního šneka.
     * Pokud je připravená past na chodbě, může dojít k jeho poražení.
     */
    public void scream() {
        Room r = world.getCurrentRoom();
        System.out.println("Zarves do tmy komplexu...");

        if (!state.isMainSnailAlive()) {
            System.out.println("Ozvena. Hlavni snek uz je mrtvy.");
            advanceTurn();
            return;
        }

        if ("corridor".equals(r.getId()) && state.isCorridorTrapPlaced()) {
            state.killMainSnail();
            state.setCorridorTrapPlaced(false);
            Room nest = world.getRoom("nest");
            if (nest != null && !nest.hasItem("cell1_key")) {
                nest.addItem("cell1_key");
            }
            System.out.println("Snek vyrazil na chodbu, vjel do soli a zkolaboval! Z hlavniho sneka vypadl klic od cely 1 (v hnizde).");
            advanceTurn();
            return;
        }

        System.out.println("Krik prilakal sneka, ale past neni pripravena...");
        if (state.isMainSnailAlive()) {
            catchPlayer("Snek te po kriku rychle vystopoval.");
            return;
        }
        advanceTurn();
    }

    /**
     * Zkontroluje podmínky vítězství při vstupu do lokace outside.
     */
    public void tryFinishByLeavingOutside() {
        Room r = world.getCurrentRoom();
        if (!"outside".equals(r.getId())) return;

        if (state.getBabySnailsRemaining() > 0) {
            System.out.println("Mali sneci stale blokuji utek. Nestacilo jen dobehnout ke dverim.");
            return;
        }
        if (!state.isPrisonerFreed()) {
            System.out.println("Muzes utéct, ale cil hry je osvobodit vezne a uniknout spolu.");
            return;
        }

        state.setGameWon(true);
        System.out.println("\n*** VITEZSTVI ***");
        System.out.println("Porazil jsi sneka, osvobodil vezne a unikli jste ven.");
    }

    /**
     * Vyhodnotí události po vstupu do místnosti (šnek, pes, finále).
     *
     * @param room místnost, do které hráč vstoupil
     * @return {@code true}, pokud metoda už spotřebovala tah
     */
    private boolean onEnterRoom(Room room) {
        if (isMainSnailInRoom(room)) {
            catchPlayer("Vesel jsi do mistnosti, kde zrovna byl hlavni snek.");
            return true;
        }

        if ("keyroom".equals(room.getId()) && !state.isBoneUsedOnDog()) {
            System.out.println("Pes te zaregistroval a zaciná vrčet. Bez kosti bud opatrny.");
        }

        if ("nest".equals(room.getId()) && !state.isMainSnailAlive()) {
            if (state.getBabySnailsRemaining() > 0) {
                System.out.println("U vychodu vidiš " + state.getBabySnailsRemaining() + " male sneky. Potrebujes male davky soli.");
            }
        }

        tryFinishByLeavingOutside();
        return false;
    }

    /**
     * Zjistí, zda hráč splňuje podmínku průchodu uzamčeným východem.
     *
     * @param lockTag identifikátor zámku/podmínky
     * @return {@code true}, pokud lze projít
     */
    private boolean canPassLock(String lockTag) {
        String lock = lockTag.toLowerCase(Locale.ROOT);
        return switch (lock) {
            case "helper", "tunnel_hint" -> state.knowsTunnelHint();
            case "key", "storage_key" -> player.getInventory().has("storage_key");
            default -> true; // neznámé tagy necháme projít, aby hra nespadla
        };
    }

    /**
     * Vypíše zprávu vysvětlující, proč nelze projít zamčenou cestou.
     *
     * @param lockTag identifikátor zámku/podmínky
     */
    private void printLockMessage(String lockTag) {
        String lock = lockTag.toLowerCase(Locale.ROOT);
        switch (lock) {
            case "helper", "tunnel_hint" -> System.out.println("Nevis, kde presne hledat tajny tunel. Promluv si s veznem z cely 1.");
            case "key", "storage_key" -> System.out.println("Je zamceno. Potrebujes klic od skladu soli.");
            default -> System.out.println("Cesta je zatim blokovana (" + lockTag + ").");
        }
    }

    /**
     * Provede chycení hráče šnekem a reset hráče zpět do cely 3.
     *
     * @param reason důvod chycení
     */
    private void catchPlayer(String reason) {
        catchPlayer(reason, true);
    }

    /**
     * Provede chycení hráče šnekem a volitelně spotřebuje tah.
     *
     * @param reason důvod chycení
     * @param consumeTurn zda má být po chycení spotřebován tah
     */
    private void catchPlayer(String reason, boolean consumeTurn) {
        System.out.println(reason);
        System.out.println("Snek te chytil, odtahl zpatky do cely 3 a vyhazel ti inventar.");
        player.clearInventory();
        world.setCurrentRoom("cell3");

        // hra je bez game over – reset a jedeme dal
        if (consumeTurn) {
            advanceTurn();
        }
    }

    /**
     * Posune hru o jeden tah.
     */
    private void advanceTurn() {
        state.advanceTurn();
    }

    /**
     * Vrátí krátkou kontextovou nápovědu pro aktuální místnost.
     *
     * @param r aktuální místnost
     * @return text nápovědy
     */
    private String shortRoomHint(Room r) {
        return switch (r.getId()) {
            case "cell1" -> state.isPrisonerFreed()
                    ? "Osvobozeny veznen ceka, az vyrazite do hnizda."
                    : "Za mrizemi je vezneny survivor. Zkus 'mluv'.";
            case "cell2" -> "Kostlivec = potencialni kost pro psa (seber bone).";
            case "cell3" -> "Startovni cela. Tajny tunel vede do ucebny (po napovede).";
            case "classroom" -> "Tady by mel byt kus sesitu (notebook_half).";
            case "keyroom" -> "Mistnost s klici a psem. Kost pomuze.";
            case "storage" -> "Sklad soli: 1x big_salt + 3x small_salt_X.";
            case "corridor" -> "Chodba je nebezpecna podle rytmu sneka.";
            case "nest" -> state.isMainSnailAlive() ? "Hnizdo sneka." : "Finale: mali sneci u vychodu.";
            case "outside" -> "Vychod z komplexu.";
            default -> "";
        };
    }

    /**
     * Vrátí stručný textový stav polohy hlavního šneka.
     *
     * @return textový status šneka
     */
    private String snailStatusHint() {
        if (!state.isMainSnailAlive()) return "Status: hlavni snek je mrtvy.";
        return "Status: snek je ted v lokaci -> " + state.getSnailZone();
    }

    /**
     * Doplní důležité předměty do mapy světa a sjednotí názvy itemů
     * kvůli kompatibilitě se starší verzí JSON mapy.
     */
    private void seedImportantItems() {
        ensureRoomItem("classroom", "notebook_half");
        ensureRoomItem("cell2", "bone");

        // Pokud starý JSON obsahuje jen 'key' / 'salt', premapujeme na konzistentní itemy.
        Room keyroom = world.getRoom("keyroom");
        if (keyroom != null) {
            if (keyroom.hasItem("key")) {
                keyroom.removeItem("key");
                keyroom.addItem("storage_key");
            }
            ensureRoomItem("keyroom", "storage_key");
        }

        Room storage = world.getRoom("storage");
        if (storage != null) {
            if (storage.hasItem("salt")) storage.removeItem("salt");
            ensureRoomItem("storage", "big_salt");
            ensureRoomItem("storage", "small_salt_1");
            ensureRoomItem("storage", "small_salt_2");
            ensureRoomItem("storage", "small_salt_3");
        }
    }

    /**
     * Zajistí, že daná místnost obsahuje zadaný předmět.
     *
     * @param roomId identifikátor místnosti
     * @param itemId identifikátor předmětu
     */
    private void ensureRoomItem(String roomId, String itemId) {
        Room room = world.getRoom(roomId);
        if (room != null && !room.hasItem(itemId)) {
            room.addItem(itemId);
        }
    }

    /**
     * Normalizuje název předmětu zadaný hráčem na interní identifikátor.
     *
     * @param raw původní text od hráče
     * @return normalizovaný identifikátor předmětu nebo {@code null}
     */
    private String normalizeItemName(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String s = raw.trim().toLowerCase(Locale.ROOT);

        return switch (s) {
            case "kost" -> "bone";
            case "klic", "klice", "key" -> "storage_key";
            case "klic_sklad", "storage_key" -> "storage_key";
            case "sesit", "notebook", "notebook_half", "sesit_polovina" -> "notebook_half";
            case "velka_sul", "big_salt", "sul_big" -> "big_salt";
            case "cell1_key", "klic_cela1", "klic1" -> "cell1_key";
            case "small_salt_1", "small_salt_2", "small_salt_3" -> s;
            default -> s.replace(' ', '_');
        };
    }

    /**
     * Normalizuje text cíle použití předmětu.
     *
     * @param raw původní text cíle
     * @return normalizovaný text cíle nebo {@code null}
     */
    private String normalizeTarget(String raw) {
        if (raw == null) return null;
        return raw.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * Zjistí, zda se hlavní šnek nachází ve stejné místnosti jako hráč.
     *
     * @param room aktuální místnost hráče
     * @return {@code true}, pokud je hlavní šnek v této místnosti
     */
    private boolean isMainSnailInRoom(Room room) {
        if (room == null || !state.isMainSnailAlive()) return false;

        return switch (state.getSnailZone()) {
            case CORRIDOR -> "corridor".equals(room.getId());
            case KITCHEN  -> "kitchen".equals(room.getId());
            case NEST     -> "nest".equals(room.getId());
        };
    }
}