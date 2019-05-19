# TablutCompetition: cristina_chiaBOT
Software per la [Tablut Students Competition](https://github.com/AGalassi/TablutCompetition).

Il progetto e' stato realizzato da Nicolo' Romandini e Ugo Leone Cavalcanti.

## Installazione su Ubuntu/Debian 

Queste sono le istruzioni per installare le librerie necessarie su ambiente
ubuntu/debian:

Da terminale, eseguire i seguenti comandi per installare JDK 8 e ANT.

```bash
sudo apt update
sudo apt install openjdk-8-jdk -y
sudo apt install ant -y
```

A questo punto, bisogna clonare la repository del progetto.

```bash
git clone https://github.com/nickromandini/TablutPlayer.git
```

## Eseguire il giocatore

Per eseguire il giocatore si utilizzi lo script di configurazione ANT da terminale. 

In particolare:

```bash
# Entrare nella cartella del progetto
cd TablutPlayer/cristinaChiaBOT

# Compilare il progetto:
ant clean
ant compile
```

A questo punto il progetto e' stato compilato nella cartella `build`. Per lanciare il server si puo' utilizzare il comando:

```bash
ant cristina_chiaBOT -Darg0 <White | Black> [-Darg1 <maxDurataTurno>]
```

Specificando `-Darg0 White` o `-Darg0 Black` si lancia il client, rispettivamente, per giocare con i bianchi o con i neri.

Mentre e' possibile specificare (in **millisecondi**) la durata massima richiesta per un turno con `-Darg1 <maxDurataTurno>` . Se non si specifica niente, la durata di default sara' di 60000 millisecondi.
