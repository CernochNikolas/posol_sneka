package cz.PosolSneka.core;

/**
 * Uchovává průběžný stav hry (progres příběhu, stav šneka, tahy hráče)
 * a poskytuje jednoduchý cyklus pohybu hlavního šneka.
 *
 * Třída je navržena jednoduše tak, aby byla snadno testovatelná
 * v rámci školního projektu.
 *
 * @author Niki Ci
 */
public class GameState {

    /**
     * Výčet oblastí, ve kterých se může nacházet hlavní šnek.
     */
    public enum SnailZone {
        CORRIDOR, NEST, KITCHEN
    }

    private int turn = 0;

    private boolean knowsTunnelHint;
    private boolean boneUsedOnDog;
    private boolean notebookRead;
    private boolean prisonerFreed;
    private boolean mainSnailAlive = true;
    private boolean corridorTrapPlaced;
    private boolean gameWon;
    private int babySnailsRemaining = 3;

    /**
     * Vrátí aktuální číslo tahu.
     *
     * @return aktuální tah (0, 1, 2, ...)
     */
    public int getTurn() {
        return turn;
    }

    /**
     * Posune hru o jeden tah dopředu.
     */
    public void advanceTurn() {
        turn++;
    }

    /**
     * Vrátí aktuální lokaci hlavního šneka podle čísla tahu.
     * Cyklus je:
     * 0 = chodba, 1 = hnízdo, 2 = kuchyň, 3 = chodba, ...
     *
     * Pokud je hlavní šnek mrtvý, vrací konzistentně hodnotu NEST.
     *
     * @return aktuální zóna hlavního šneka
     */
    public SnailZone getSnailZone() {
        if (!mainSnailAlive) return SnailZone.NEST; // už nehlídá, ale necháme konzistentní hodnotu
        int phase = turn % 3;
        if (phase == 0) return SnailZone.CORRIDOR;
        if (phase == 1) return SnailZone.NEST;
        return SnailZone.KITCHEN;
    }

    /**
     * Zjistí, zda je chodba v aktuálním tahu nebezpečná
     * (tj. hlavní šnek je naživu a právě hlídkuje na chodbě).
     *
     * @return {@code true}, pokud je chodba nebezpečná, jinak {@code false}
     */
    public boolean isSnailDangerOnCorridor() {
        return mainSnailAlive && getSnailZone() == SnailZone.CORRIDOR;
    }

    /**
     * Zjistí, zda hráč zná nápovědu o tajném tunelu.
     *
     * @return {@code true}, pokud byla získána nápověda od vězně
     */
    public boolean knowsTunnelHint() {
        return knowsTunnelHint;
    }

    /**
     * Nastaví, zda hráč zná nápovědu o tajném tunelu.
     *
     * @param knowsTunnelHint hodnota příznaku znalosti nápovědy
     */
    public void setKnowsTunnelHint(boolean knowsTunnelHint) {
        this.knowsTunnelHint = knowsTunnelHint;
    }

    /**
     * Zjistí, zda byla použita kost na psa.
     *
     * @return {@code true}, pokud je pes zabaven kostí
     */
    public boolean isBoneUsedOnDog() {
        return boneUsedOnDog;
    }

    /**
     * Nastaví, zda byla použita kost na psa.
     *
     * @param boneUsedOnDog hodnota příznaku použití kosti
     */
    public void setBoneUsedOnDog(boolean boneUsedOnDog) {
        this.boneUsedOnDog = boneUsedOnDog;
    }

    /**
     * Zjistí, zda hráč přečetl polovinu sešitu.
     *
     * @return {@code true}, pokud byl sešit přečten
     */
    public boolean isNotebookRead() {
        return notebookRead;
    }

    /**
     * Nastaví, zda hráč přečetl polovinu sešitu.
     *
     * @param notebookRead hodnota příznaku přečtení sešitu
     */
    public void setNotebookRead(boolean notebookRead) {
        this.notebookRead = notebookRead;
    }

    /**
     * Zjistí, zda byl vězeň z cely 1 osvobozen.
     *
     * @return {@code true}, pokud je vězeň osvobozen
     */
    public boolean isPrisonerFreed() {
        return prisonerFreed;
    }

    /**
     * Nastaví stav osvobození vězně.
     *
     * @param prisonerFreed hodnota příznaku osvobození vězně
     */
    public void setPrisonerFreed(boolean prisonerFreed) {
        this.prisonerFreed = prisonerFreed;
    }

    /**
     * Zjistí, zda hlavní šnek žije.
     *
     * @return {@code true}, pokud je hlavní šnek naživu
     */
    public boolean isMainSnailAlive() {
        return mainSnailAlive;
    }

    /**
     * Označí hlavního šneka jako mrtvého.
     */
    public void killMainSnail() {
        this.mainSnailAlive = false;
    }

    /**
     * Zjistí, zda je na chodbě připravená past ze soli.
     *
     * @return {@code true}, pokud je past připravena
     */
    public boolean isCorridorTrapPlaced() {
        return corridorTrapPlaced;
    }

    /**
     * Nastaví stav pasti ze soli na chodbě.
     *
     * @param corridorTrapPlaced hodnota příznaku pasti
     */
    public void setCorridorTrapPlaced(boolean corridorTrapPlaced) {
        this.corridorTrapPlaced = corridorTrapPlaced;
    }

    /**
     * Vrátí počet zbývajících malých šneků ve finále.
     *
     * @return počet malých šneků
     */
    public int getBabySnailsRemaining() {
        return babySnailsRemaining;
    }

    /**
     * Sníží počet malých šneků o 1, pokud je počet větší než 0.
     */
    public void killOneBabySnail() {
        if (babySnailsRemaining > 0) babySnailsRemaining--;
    }

    /**
     * Zjistí, zda hráč vyhrál hru.
     *
     * @return {@code true}, pokud je hra vyhraná
     */
    public boolean isGameWon() {
        return gameWon;
    }

    /**
     * Nastaví stav vítězství ve hře.
     *
     * @param gameWon hodnota příznaku výhry
     */
    public void setGameWon(boolean gameWon) {
        this.gameWon = gameWon;
    }
}