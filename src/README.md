# Posol Šneka

Textová hra vytvořená jako školní projekt v Javě.  
Hráč se pohybuje mezi místnostmi, sbírá předměty, používá je ve správných situacích a snaží se uniknout z komplexu se šneky.

## Autor
Niki Ci

## O projektu
Hra je navržena jako textová adventura.  
Hlavní důraz byl kladen na:
- načítání světa z JSON souboru
- pohyb mezi místnostmi
- práci s příkazy
- základní herní logiku
- testování důležitých částí programu

## Struktura programu

### `Room`
Reprezentuje jednu místnost ve hře.  
Obsahuje:
- identifikátor místnosti
- název místnosti
- propojení do dalších místností
- předměty v místnosti
- případné podmínky pro průchod

### `World`
Uchovává celý herní svět a aktuální místnost hráče.  
Po načtení JSON souboru obsahuje všechny místnosti a jejich propojení.

### `GameState`
Drží průběžný stav hry, například:
- počet tahů
- aktuální cyklus pohybu hlavního šneka
- jestli hráč zná nápovědu o tunelu
- jestli použil kost na psa
- jestli přečetl sešit
- jestli osvobodil vězně
- jestli je hlavní šnek naživu
- počet malých šneků ve finále
- stav vítězství

### `GameEngine`
Obsahuje hlavní herní logiku.  
Řeší:
- pohyb hráče
- kontrolu zamčených cest
- prohledávání místností
- sbírání předmětů
- použití předmětů
- dialog s vězněm
- chování hlavního šneka
- chycení hráče
- podmínky vítězství

### Commandy
Příkazy od hráče jsou zpracovány pomocí command systému.  
Například:
- `jdi`
- `cekej`
- `seber`
- `pouzij`
- `cti`
- `mluv`
- `inventar`
- `nasyp sul`
- `kric`

## Načítání světa
Svět je definován v JSON souboru.  
Při spuštění programu se:
1. načtou data z JSONu
2. vytvoří se objekty místností
3. nastaví se propojení mezi místnostmi
4. uloží se předměty do jednotlivých místností

Výhodou tohoto řešení je, že lze měnit mapu hry bez úpravy Java kódu.

## Herní logika
Ve hře hráč:
- začíná v cele 3
- získává nápovědu od vězně
- hledá kost pro psa
- získává klíč od skladu
- bere sůl pro boj se šneky
- nastraží past na hlavního šneka
- získá klíč od cely 1
- osvobodí vězně
- porazí malé šneky u východu
- unikne ven

## Testování
Byly vytvořeny unit testy pomocí JUnitu.  
Testovány byly hlavně:
- průběh tahů v `GameState`
- cyklus pohybu šneka
- práce s místnostmi v `Room`
- propojení místností
- ukládání locků na východech

## Co je ve výsledné verzi uděláno jinak
Během vývoje byl projekt zjednodušen tak, aby byl funkční, přehledný a odpovídal rozsahu školního zadání.

### Postavy
Ve hře se vyskytují postavy jako:
- vězeň
- pes
- hlavní šnek

Nakonec nejsou všechny řešeny jako plně samostatné objekty s vlastní složitou logikou.  
Jejich chování je z velké části řízeno přímo v `GameEngine` podle aktuální místnosti a stavu hry.

Například:
- vězeň reaguje přes metodu `talk()`
- pes ovlivňuje možnost vzít klíč
- hlavní šnek je řízen pomocí cyklu v `GameState`

### Předměty
Předměty nejsou ve finální verzi řešeny jako složitá hierarchie objektů.  
Ve většině případů jsou reprezentovány textovými identifikátory, například:
- `bone`
- `storage_key`
- `big_salt`
- `small_salt_1`

To zjednodušilo implementaci a umožnilo rychleji dokončit funkční verzi hry.

### Připravené třídy
Některé třídy byly v projektu připraveny jako základ návrhu, ale jejich logika nebyla ve finální verzi plně rozvedena.  
Hlavní důraz byl nakonec kladen na funkčnost hry, načítání světa, příkazy a splnění zadání.

## Shrnutí
Projekt splňuje hlavní požadavky zadání:
- svět je načítán z JSON souboru
- místnosti jsou propojené
- hráč se může pohybovat pouze po platných cestách
- hra obsahuje příkazy a herní logiku
- byly vytvořeny unit testy
- kód je doplněn komentáři JavaDoc

Hlavním cílem bylo vytvořit funkční textovou hru s přehlednou strukturou a možností dalšího rozšiřování.