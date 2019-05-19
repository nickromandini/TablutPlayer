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
    private long timeoutValue;
    private int turn = 1;
    private ConcurrentHashMap<String, Integer> statesMap = new ConcurrentHashMap<>();

    private BlockingQueue<CommunicationUnit> qCu = new ArrayBlockingQueue<>(4);
    private BlockingQueue<Result> qResult = new ArrayBlockingQueue<>(4);


    public TablutCristina(String player, String name, int gameChosen, long timeoutValue) throws IOException {
        super(player, name);
        game = gameChosen;
        this.timeoutValue = timeoutValue;
    }


    public static void main(String[] args) throws IOException {
        int gametype = 4;
        String role = "";
        String name = "CChiaBOT";
        long timeout = 52000;
        if (args.length < 1) {
            System.out.println("You must specify which player you are (WHITE or BLACK)");
            System.exit(-1);
        } else {
            role = (args[0]);
            if(!args[1].equals("${timeout}"))
                timeout = Long.valueOf(args[1]) - 5000;
        }
        TablutCristina client = new TablutCristina(role, name, gametype, timeout);
        client.run();
    }

    @Override
    public void run() {

        try {
            this.declareName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        State state = new StateTablut();
        state.setTurn(Turn.WHITE);

        System.out.println("You are player " + this.getPlayer().toString() + "!");

        MinMaxThread[] tasks = new MinMaxThread[4];

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

            state = this.getCurrentState();

            if (this.getPlayer().equals(Turn.WHITE)) {
                if (state.getTurn().equals(Turn.WHITE)) {
                    System.out.println("I'm thinking... ");
                    this.timeMs = System.currentTimeMillis();
                    state = this.getCurrentState();
                    Action a = null;
                    if(this.turn == 1) {
                        try {
                            a = new Action("d5", "d4", Turn.WHITE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (this.turn == 2) {
                        if(state.getPawn(5,3).equalsPawn("O"))
                            try {
                                a = new Action("e6", "d6", Turn.WHITE);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        else
                            a = this.alphaBetaSearch(state);
                    } else {
                        a = this.alphaBetaSearch(state);
                    }
                    try {
                        this.write(a);
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                    statesMap.clear();
                }
                else if (state.getTurn().equals(Turn.BLACK)) {
                    this.turn++;
                    System.out.println("Waiting for your opponent move... ");
                }
                else if (state.getTurn().equals(Turn.WHITEWIN)) {
                    System.out.println("YOU WIN!");
                    System.exit(0);
                }
                else if (state.getTurn().equals(Turn.BLACKWIN)) {
                    System.out.println("YOU LOSE!");
                    System.exit(0);
                }
                else if (state.getTurn().equals(Turn.DRAW)) {
                    System.out.println("DRAW!");
                    System.exit(0);
                }

            } else {
                if (state.getTurn().equals(Turn.BLACK)) {
                    System.out.println("I'm thinking... ");
                    this.timeMs = System.currentTimeMillis();
                    Action a = this.alphaBetaSearch(state);
                    try {
                        this.write(a);
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                    this.turn++;
                    statesMap.clear();
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
        boolean recalculatedActions = false;
        List<Action> actions = state.getAllLegalMoves(true);
        int size = actions.size();

        if(size == 0) {
            actions = state.getAllLegalMoves(false);
            size = actions.size();
            recalculatedActions = true;
        } else if(size == 1)
            return actions.get(0);

        if(actions.get(0).getValue() >= 500 && state.getTurn().equalsTurn(Turn.WHITE.toString()))
            return actions.get(0);
        else if(actions.get(0).getValue() >= 500 && state.getTurn().equalsTurn(Turn.BLACK.toString()))
            return actions.get(0);

        int numThread = 4;
        int limit = size / numThread;
        Action[][] array = new Action[numThread - 1][limit];
        Action[] arrayLast = new Action[limit + size % numThread];

        int k = 0;
        int j = 0;
        int maxDepth;
        if(!recalculatedActions || timeoutValue >= 30000) {
            if (size >= 40)
                maxDepth = 3;
            else
                maxDepth = 4;
        } else
            maxDepth = 2;

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
                    qCu.put(new CommunicationUnit(arrayLast, state, maxDepth, this.timeMs, timeoutValue, state.getTurn().toString(), statesMap));
                else
                    qCu.put(new CommunicationUnit(array[i], state, maxDepth, this.timeMs, timeoutValue, state.getTurn().toString(), statesMap));
        } catch (InterruptedException e) {
            System.out.println("ERROR");
        }

        Result temp;
        Result res = null;
        try {
            for (int i = 0; i < numThread; i++) {
                temp = qResult.take();
                if(res == null)
                    res = temp;
                else if( res.getValue() < temp.getValue())
                    res = temp;
            }
        } catch(InterruptedException e) {
            System.err.println("ERROR");
        }

        return res.getAction();
    }

}
