package it.unibo.ai.didattica.competition.tablut.client;

import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;


public class TablutCristina extends TablutClient {

    private int game;

    private long timeMs;
    private long timeoutValue = 55000;
    private int turn = 0;

    public TablutCristina(String player, String name, int gameChosen) throws UnknownHostException, IOException {
        super(player, name);
        game = gameChosen;
    }


    public TablutCristina(String player) throws UnknownHostException, IOException {
        this(player, "random", 4);
    }

    public TablutCristina(String player, String name) throws UnknownHostException, IOException {
        this(player, name, 4);
    }

    public TablutCristina(String player, int gameChosen) throws UnknownHostException, IOException {
        this(player, "random", gameChosen);
    }

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
        int gametype = 4;
        String role = "";
        String name = "CristinaChiaBOT";
        // TODO: change the behavior?
        if (args.length < 1) {
            System.out.println("You must specify which player you are (WHITE or BLACK)");
            System.exit(-1);
        } else {
            System.out.println(args[0]);
            role = (args[0]);
        }
        if (args.length == 2) {
            System.out.println(args[1]);
            gametype = Integer.parseInt(args[1]);
        }
        if (args.length == 3) {
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

        while (true) {
            try {
                this.read();
            } catch (ClassNotFoundException | IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                System.exit(1);
            }
            turn++;
            this.timeMs = System.currentTimeMillis();
            System.out.println("Current state:");
            state = this.getCurrentState();
            System.out.println(state.toString());

            if (this.getPlayer().equals(Turn.WHITE)) {
                // � il mio turno
                if (this.getCurrentState().getTurn().equals(Turn.WHITE)) {
                    //List<Action> actionList = state.getAllLegalMoves();
                    Action a = this.alphaBetaSearch(state);//getBestAction(actionList, state);
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
                    //List<Action> actionList = state.getAllLegalMoves();
                    Action a = this.alphaBetaSearch(state);//getBestAction(actionList, state);
                    System.out.println("Mossa scelta: " + a.toString());
                    try {
                        this.write(a);
                    } catch (ClassNotFoundException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                }

                else if (state.getTurn().equals(Turn.WHITE)) {
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


    public Action alphaBetaSearch(State state) {
        List<Action> actions = state.getAllLegalMoves();
        //System.out.println("Valore prima azione: " + actions.descendingMap().firstEntry().getKey());


        if(actions.get(0).getValue() >= 1000 && state.getTurn().equalsTurn(Turn.WHITE.toString()))
            return actions.get(0);
        else if(actions.get(0).getValue() >= 1000 && state.getTurn().equalsTurn(Turn.BLACK.toString()))
            return actions.get(0);



        int numThread = Runtime.getRuntime().availableProcessors();

        ForkJoinPool fjp = new ForkJoinPool(numThread);

        MinMaxTask mmt = new MinMaxTask(false, numThread, actions, state, timeMs, timeoutValue, turn, state.getTurn().toString());

        Result resMinMax = fjp.invoke(mmt);

        System.out.println("Valore minmax " + resMinMax.getValue());

        return resMinMax.getAction();
    }

    private final class Result {

        private int value;
        private Action action;

        public Result(int value, Action action) {
            this.value = value;
            this.action = action;
        }

        public int getValue() {
            return value;
        }

        public Action getAction() {
            return action;
        }

    }


    private final class MinMaxTask extends RecursiveTask<Result> {

        private boolean isChild;
        private List<Action> actions;
        private State state;
        private int maxDepth;
        private long timeMs;
        private long timeout;
        private AtomicInteger atomicInteger;
        private double numThread;
        private int turn;
        private boolean maxDepthAlreadyUpdated = false;
        private String me;


        public MinMaxTask(boolean isChild, List<Action> actions, State state, int maxDepth, long timeMs, long timeout, AtomicInteger atomicInteger, int turn, String me) {
            this.isChild = isChild;
            this.actions = actions;
            this.state = state;
            this.maxDepth = maxDepth;
            this.timeMs = timeMs;
            this.timeout = timeout;
            this.atomicInteger = atomicInteger;
            this.turn = turn;
            this.me = me;
        }

        public MinMaxTask(boolean isChild, double numThread, List<Action> actions, State state, long timeMs, long timeout, int turn, String me) {
            this.isChild = isChild;
            this.actions = actions;
            this.state = state;
            this.timeMs = timeMs;
            this.timeout = timeout;
            this.numThread = numThread;
            this.turn = turn;
            this.me = me;
        }


        @Override
        public Result compute() {





            if(this.isChild) {
                int v = -20000;
                Action result = null;
                int temp;
                //System.out.println(Thread.currentThread().getName() + " parto con azioni " + this.actions.size());
                for (Action action : this.actions) {

                    this.atomicInteger.incrementAndGet();

                    //valuto azione e metto dentro struttura dati
                    temp = maxValue(resultState(this.state, action), -20000, 20000, 0, this.maxDepth, this.atomicInteger);
                    if( temp > v ) {
                        v = temp;
                        result = action;
                    }
                    if (System.currentTimeMillis() - this.timeMs > this.timeout) {
                        //System.out.println(Thread.currentThread().getName() + " termino per timeout");
                        System.out.println(Thread.currentThread().getName() + " valore azione timeout " + v);
                        return new Result(v, result);
                    }
                }
                //System.out.println(Thread.currentThread().getName() + " termino");
                System.out.println(Thread.currentThread().getName() + " valore azione " + v);
                return new Result(v, result);

            } else {

                List<MinMaxTask> tasks = new ArrayList<>();

                Result res = null;

                AtomicInteger ai = new AtomicInteger(0);
                double percentage1 = (0.1 * 4) / (numThread);
                double percentage2 = (0.2 * 4) / (numThread);
                double percentage3 = (0.3 * 4) / (numThread);
                double percentage4 = (0.4 * 4) / (numThread);
                int size = actions.size();
                int limit = (int) Math.round(percentage1 * size);
                if(limit < 1 )
                    limit = 1;
                int maxDepth = 4;
                int from = 0;
                int to = limit;
                System.out.println("numero azioni " + actions.size());
                for (int i = 1; i <= numThread; i++) {
                    System.out.println("from " + from + " to " + to + " limit " + limit);
                    if (i == numThread)
                        tasks.add(new MinMaxTask(true, this.actions.subList(from, actions.size()), this.state, maxDepth, this.timeMs, this.timeout, ai, this.turn, this.me));
                    else {
                        tasks.add(new MinMaxTask(true, this.actions.subList(from, to), this.state, maxDepth, this.timeMs, this.timeout, ai, this.turn, this.me));
                        if (i == (numThread / 4)) {
                            from = to;
                            limit = (int) Math.round(percentage2 * size);
                            to = from + limit;
                            maxDepth = 4;
                        } else if (i == (numThread / 2)) {
                            from = to;
                            limit = (int) Math.round(percentage3 * size);
                            to = from + limit;
                            maxDepth = 4;
                        } else if (i == 3 * (numThread / 4)) {
                            from = to;
                            limit = (int) Math.round(percentage4 * size);
                            to = from + limit;
                            maxDepth = 3;
                        } else {
                            from = to;
                            to = to + limit;
                        }
                    }
                }

                for(MinMaxTask t : tasks) {
                    t.fork();
                }
                for (MinMaxTask t : tasks) {
                    try {
                        Result temp = t.get();
                        if (res == null)
                            res = temp;
                        else if (temp.getValue() > res.getValue()) {
                            res = temp;
                        }
                    } catch(Exception e) {
                        System.out.println("ERRORE " + e.getMessage() + "\n" + e.getCause());
                        e.printStackTrace();
                    }
                }

                System.out.println("Nodi esplorati: " + ai.get() + " in " + ( System.currentTimeMillis() - timeMs));
                return res;
            }


        }

        private int maxValue(State state, int alpha, int beta, int depth, int maxDepth, AtomicInteger atomicInteger) {
            if(state.isTerminalWhite())
                return 10000;
            else if(state.isTerminalBlack())
                return -10000;
            else if(!maxDepthAlreadyUpdated && depth == maxDepth && System.currentTimeMillis() - this.timeMs < 26000) {
                maxDepthAlreadyUpdated = true;
                maxDepth++;
            }
            else if(depth == maxDepth || System.currentTimeMillis() - this.timeMs > timeout)
                if(this.me.equals("W"))
                    return Evaluation.evaluate(state, this.turn);
                else if(this.me.equals("B"))
                    return -Evaluation.evaluate(state, this.turn);
                else
                    throw new InputMismatchException();


            atomicInteger.incrementAndGet();

            List<Action> actions = state.getAllLegalMoves();

            int v = -20000;
            for (Action action : actions) {
                v = Math.max(v, minValue(resultState(state, action), alpha, beta, depth + 1, maxDepth, atomicInteger));
                if (v >= beta)
                    return v;
                alpha = Math.max(alpha, v);
            }
            return v;
        }

        private int minValue(State state, int alpha, int beta, int depth, int maxDepth, AtomicInteger atomicInteger) {


            if(state.isTerminalWhite())
                return 10000;
            else if(state.isTerminalBlack())
                return -10000;
            else if(!maxDepthAlreadyUpdated && depth == maxDepth && System.currentTimeMillis() - this.timeMs < 26000 ) {
                maxDepthAlreadyUpdated = true;
                maxDepth++;
            }
            else if (depth == maxDepth || System.currentTimeMillis() - this.timeMs > timeout)
                if(this.me.equals("W"))
                    return Evaluation.evaluate(state, this.turn);
                else if(this.me.equals("B"))
                    return -Evaluation.evaluate(state, this.turn);
                else
                    throw new InputMismatchException();


            //return 1000, 0 o -1000 a seconda del caso

            atomicInteger.incrementAndGet();
            //else continua
            List<Action> actions = state.getAllLegalMoves();

            int v = 20000;
            for (Action action : actions) {
                v = Math.min(v, maxValue(resultState(state, action), alpha, beta, depth + 1, maxDepth, atomicInteger));
                if (v <= alpha)
                    return v;
                beta = Math.max(beta, v);
            }
            return v;
        }

        private State resultState(State state, Action action) {
            State returnState = state.clone();
            returnState.move(action);
            return returnState;
        }
    }


}
