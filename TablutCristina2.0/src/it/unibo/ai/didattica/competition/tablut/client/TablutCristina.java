package it.unibo.ai.didattica.competition.tablut.client;

import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.util.CommunicationUnit;
import it.unibo.ai.didattica.competition.tablut.util.MinMaxThread;
import it.unibo.ai.didattica.competition.tablut.util.Result;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;


public class TablutCristina extends TablutClient {

    private int game;

    private long timeMs;
    private static long timeoutValue = 55000;
    private int turn = 1;
    private ConcurrentHashMap<String, Integer> statesMap = new ConcurrentHashMap<>();

    private BlockingQueue<CommunicationUnit> qCu = new ArrayBlockingQueue<>(4);
    private BlockingQueue<Result> qResult = new ArrayBlockingQueue<>(4);


    public TablutCristina(String player, String name, int gameChosen) throws IOException {
        super(player, name);
        game = gameChosen;
    }


    public TablutCristina(String player) throws IOException {
        this(player, "random", 4);
    }

    public TablutCristina(String player, String name) throws IOException {
        this(player, name, 4);
    }

    public TablutCristina(String player, int gameChosen) throws IOException {
        this(player, "random", gameChosen);
    }

    public static void main(String[] args) throws IOException {
        int gametype = 4;
        String role = "";
        String name = "CChiaBOT";
        // TODO: change the behavior?
        if (args.length < 1) {
            System.out.println("You must specify which player you are (WHITE or BLACK)");
            System.exit(-1);
        } else {
            System.out.println(args[0]);
            role = (args[0]);
        }
        if (args.length == 2) {
            timeoutValue = Integer.valueOf(args[2]);
        }
        if (args.length == 3) {
            System.out.println(args[1]);
            gametype = Integer.parseInt(args[1]);
        }
        if (args.length == 4) {
            name = args[2];
        }
        System.out.println("Selected client: " + args[0]);

        TablutCristina client = new TablutCristina(role, name, gametype);
        client.run();
    }

    @Override
    public void run() {

        try {
            this.declareName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        State state;

        Game rules = null;
        switch (this.game) {
            case 1:
                state = new StateTablut();
                rules = new GameTablut();
                break;
            case 2:
                state = new StateTablut();
                rules = new GameModernTablut();
                break;
            case 3:
                state = new StateBrandub();
                rules = new GameTablut();
                break;
            case 4:
                state = new StateTablut();
                state.setTurn(Turn.WHITE);
                rules = new GameAshtonTablut(99, 0, "garbage", "fake", "fake");
                System.out.println("Ashton Tablut game");
                break;
            default:
                System.out.println("Error in game selection");
                System.exit(4);
        }

        System.out.println("You are player " + this.getPlayer().toString() + "!");


        MinMaxThread[] tasks = new MinMaxThread[Runtime.getRuntime().availableProcessors()];

        for(int i = 0; i< tasks.length; i++) {
            tasks[i] = new MinMaxThread(qCu, qResult);
            tasks[i].start();
        }


        while (true) {
            try {
                this.read();
            } catch (ClassNotFoundException | IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                System.exit(1);
            }
            System.out.println(turn);
            this.timeMs = System.currentTimeMillis();
            System.out.println("Current state:");
            state = this.getCurrentState();
            System.out.println(state.toString());
            Action a = null;

            statesMap.clear();

            if (this.getPlayer().equals(Turn.WHITE)) {
                // � il mio turno
                if (this.getCurrentState().getTurn().equals(Turn.WHITE)) {
                    if(this.turn == 1) {
                        try {
                            a = new Action("d5", "d4", Turn.WHITE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (this.turn == 2) {
                        System.out.println("turno due " + state.getPawn(5,3).equalsPawn("O"));
                        if(state.getPawn(5,3).equalsPawn("O"))
                            try {
                                a = new Action("e6", "d6", Turn.WHITE);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        else
                            a = this.alphaBetaSearch(state);
                    } else {
                        //List<Action> actionList = state.getAllLegalMoves();
                        a = this.alphaBetaSearch(state);//getBestAction(actionList, state);
                    }
                    System.out.println("Mossa scelta: " + a.toString());
                    try {
                        this.write(a);
                    } catch (ClassNotFoundException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                // � il turno dell'avversario
                else if (state.getTurn().equals(Turn.BLACK)) {
                    this.turn++;
                    System.out.println("Waiting for your opponent move... ");
                }
                // ho vinto
                else if (state.getTurn().equals(Turn.WHITEWIN)) {
                    System.out.println("YOU WIN!");
                    System.exit(0);
                }
                // ho perso
                else if (state.getTurn().equals(Turn.BLACKWIN)) {
                    System.out.println("YOU LOSE!");
                    System.exit(0);
                }
                // pareggio
                else if (state.getTurn().equals(Turn.DRAW)) {
                    System.out.println("DRAW!");
                    System.exit(0);
                }

            } else {

                // � il mio turno
                if (this.getCurrentState().getTurn().equals(Turn.BLACK)) {
                    if(this.turn == 1) {
                        try {
                            a = new Action("f1", "f5", Turn.BLACK);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //List<Action> actionList = state.getAllLegalMoves();
                        a = this.alphaBetaSearch(state);//getBestAction(actionList, state);
                    }
                    System.out.println("Mossa scelta: " + a.toString());
                    try {
                        this.write(a);
                    } catch (ClassNotFoundException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    this.turn++;

                } else if (state.getTurn().equals(Turn.WHITE)) {
                    System.out.println("Waiting for your opponent move... ");
                } else if (state.getTurn().equals(Turn.WHITEWIN)) {
                    System.out.println("YOU LOSE!");
                    System.exit(0);
                } else if (state.getTurn().equals(Turn.BLACKWIN)) {
                    System.out.println("YOU WIN!");
                    System.exit(0);
                } else if (state.getTurn().equals(Turn.DRAW)) {
                    System.out.println("DRAW!");
                    System.exit(0);
                }

            }
        }

    }


    private Action alphaBetaSearch(State state) {
        List<Action> actions = state.getAllLegalMoves();
        int size = actions.size();
        System.out.println(size + " azioni possibili");

        if(size == 0) {
            System.out.println("Premuto pulsante autodistruzione... ");
        } else if(size == 1)
            return actions.get(0);

        System.out.println("Valore prima azione: " + actions.get(0).getValue());


        if(actions.get(0).getValue() >= 500 && state.getTurn().equalsTurn(Turn.WHITE.toString()))
            return actions.get(0);
        else if(actions.get(0).getValue() >= 500 && state.getTurn().equalsTurn(Turn.BLACK.toString()))
            return actions.get(0);

        int numThread = 4;
        int limit = size / numThread; //(int) Math.round(percentage1 * size);
        Action[][] array = new Action[numThread - 1][limit];
        Action[] arrayLast = new Action[limit + size % numThread];

        int k = 0;
        int j = 0;
        int maxDepth;
        if (size >= 45)
            maxDepth = 3;
        else if (size > 20)
            maxDepth = 4;
        else
            maxDepth = 4;
        for (int i = 0; i < size; i++) {
            if (i >= numThread * limit) {
                arrayLast[k + limit] = actions.get(i);
                k++;
            } else {
                if ((i + 1) % numThread == 0) {
                    arrayLast[j] = actions.get(i);
                    j++;
                    k = 0;
                } else {
                    array[k][j] = actions.get(i);
                    k++;
                }
            }
        }
        try {
            for (int i = 0; i < numThread; i++)
                if (i == numThread - 1)
                    qCu.put(new CommunicationUnit(arrayLast, state, maxDepth, this.timeMs, timeoutValue, this.turn, state.getTurn().toString(), statesMap));
                else
                    qCu.put(new CommunicationUnit(array[i], state, maxDepth, this.timeMs, timeoutValue, this.turn, state.getTurn().toString(), statesMap));
        } catch (InterruptedException e) {
            System.out.println("ERRORE");
        }

        Result temp;
        Result res = null;
        int nodes = 0;
        try {
            for (int i = 0; i < numThread; i++) {
                temp = qResult.take();
                nodes += temp.getAi();
                if(res == null)
                    res = temp;
                else if( res.getValue() < temp.getValue())
                    res = temp;
            }
        } catch(InterruptedException e) {
            System.err.println("ERRORE");
        }
        System.out.println("Nodi esplorati " + nodes + " in " + (System.currentTimeMillis() - timeMs));
        System.out.println("Scelta azione con valore " + res.getValue());


        return res.getAction();
    }

}
