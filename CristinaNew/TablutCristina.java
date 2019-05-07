package it.unibo.ai.didattica.competition.tablut.client;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;


public class TablutCristina extends TablutClient {

    private int game;

    private long timeMs;
    private long timeoutValue = 30000;

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
        String name = "cristina_chiaBOT";
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

        List<int[]> pawns = new ArrayList<int[]>();
        System.out.println("You are player " + this.getPlayer().toString() + "!");

        while (true) {
            try {
                this.read();
            } catch (ClassNotFoundException | IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                System.exit(1);
            }
            this.timeMs = System.currentTimeMillis();
            System.out.println("Current state:");
            state = this.getCurrentState();
            System.out.println(state.toString());

            if (this.getPlayer().equals(Turn.WHITE)) {
                // � il mio turno
                if (this.getCurrentState().getTurn().equals(Turn.WHITE)) {
                	System.out.println("E' il mio turno");
                    //List<Action> actionList = state.getAllLegalMoves();
                    Action a = this.alphaBetaSearch(state);//getBestAction(actionList, state);
                    System.out.println("Mossa scelta: " + a.toString());
                    try {
                        this.write(a);
                    } catch (ClassNotFoundException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    pawns.clear();

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

    /*private Action getBestAction(List<Action> actions, State state) {
        TreeMap<Integer, Action> actionMap = new TreeMap<Integer, Action>();
        int i = 0;
        for(Action a : actions) {
            int value = Evaluation.evaluateAction(a, state );
            actionMap.put(value + i, a);
            i++;
            if (value == 1000) {
                break;
            }
        }
        if(actionMap.descendingMap().firstEntry().getKey() < 500) {
            System.out.println("Mossa random");
            return actionMap.get(new Random().nextInt(actionMap.size()));
        }
        return actionMap.descendingMap().firstEntry().getValue();
    }*/

    public Action alphaBetaSearch(State state) {
        TreeMap<Integer,Action> actions = state.getAllLegalMoves();
        System.out.println("Valore prima azione: " + actions.descendingMap().firstEntry().getKey());

        /*
        if(actions.descendingMap().firstEntry().getKey() == 10000 && state.getTurn().equalsTurn(Turn.WHITE.toString())) {
            return actions.descendingMap().firstEntry().getValue();
        }
        */

        int depth = 0;

        ForkJoinPool fjp = new ForkJoinPool(4);


        List<Action> actionList = new ArrayList<>(actions.descendingMap().values());//actions.descendingMap().values().stream().collect(Collectors.toList());

        MinMaxTask mmt = new MinMaxTask(false, actionList, state,depth,new Evaluation(), timeMs, timeoutValue);

        Result res = fjp.invoke(mmt);
        
        System.out.println("Azione minmax valore " + res.value);

        /*
        int v;
        for (Action action : actions.descendingMap().values()) {
            //valuto azione e metto dentro struttura dati
            v = maxValue(resultState(state, action), -20000, 20000, depth);
            evaluatedActions.put(v, action);
            if (System.currentTimeMillis() - this.timeMs > 10000) {
                return evaluatedActions.descendingMap().firstEntry().getValue();
            }
        }
        */

        return res.getAction();
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
        private int depth;
        private Evaluation evaluator;
        private long timeMs;
        private long timeout;

        public MinMaxTask(boolean isChild, List<Action> actions, State state, int depth, Evaluation evaluator, long timeMs, long timeout) {
            this.isChild = isChild;
            this.actions = actions;
            this.state = state;
            this.depth = depth;
            this.evaluator = evaluator;
            this.timeMs = timeMs;
            this.timeout = timeout;
        }

        @Override
        public Result compute() {

            List<MinMaxTask> tasks = new ArrayList<>();
            int limit = (int)Math.round(actions.size() / 4.0) ;
            Result res = null;

            if(isChild) {
                int v = -20000;
                Action result = null;
                int temp;
                System.out.println("Azione valore prima " + actions.get(0));
                for (Action action : actions) {
                    //valuto azione e metto dentro struttura dati
                    temp = maxValue(resultState(state, action), -20000, 20000, depth, evaluator);
                    if( temp > v ) {
                        v = temp;
                        result = action;
                    }
                    if (System.currentTimeMillis() - this.timeMs > timeout) {
                        return new Result(v, result);
                    }
                }
                return new Result(v, result);

            } else {
                tasks.add(new MinMaxTask(true, actions.subList(0, limit), state, depth, evaluator, timeMs, timeout));
                tasks.add(new MinMaxTask(true, actions.subList(limit, 2 * limit), state, depth, evaluator, timeMs, timeout));
                tasks.add(new MinMaxTask(true, actions.subList(2 * limit, 3 * limit), state, depth, evaluator, timeMs, timeout));
                tasks.add(new MinMaxTask(true, actions.subList(3 * limit, actions.size()), state, depth, evaluator, timeMs, timeout));

                tasks.forEach( task -> task.fork());

                for(MinMaxTask t : tasks) {
                    Result temp = t.join();
                    if(res == null)
                        res = temp;
                    else if(temp.getValue() > res.getValue()) {
                        res = temp;
                    }
                }
            }

            return res;
        }

        private int maxValue(State state, int alpha, int beta, int depth, Evaluation evaluator) {
            if(state.isTerminalWhite()) {
                return 10000;
            } else if(state.isTerminalBlack()){
                return -10000;
            } else if(depth > 3 || System.currentTimeMillis() - this.timeMs > timeout) {
                return evaluator.evaluate(state);
            }

            TreeMap<Integer, Action> actions = state.getAllLegalMoves();

            int v = -20000;
            for (Action action : actions.descendingMap().values()) {
                v = Math.max(v, minValue(resultState(state, action), alpha, beta, depth + 1, evaluator));
                if (v >= beta)
                    return v;
                alpha = Math.max(alpha, v);
            }
            return v;
        }

        private int minValue(State state, int alpha, int beta, int depth, Evaluation evaluator) {


            if(state.isTerminalWhite()) {
                System.out.println("Trovato white terminale");
                return 10000;
            } else if(state.isTerminalBlack()){
                return -10000;
            } else if (depth > 3 || System.currentTimeMillis() - this.timeMs > timeout) {
                return evaluator.evaluate(state);
            }
            //return 1000, 0 o -1000 a seconda del caso


            //else continua
            TreeMap<Integer, Action> actions = state.getAllLegalMoves();

            int v = 20000;
            for (Action action : actions.descendingMap().values()) {
                v = Math.min(v, maxValue(resultState(state, action), alpha, beta, depth + 1, evaluator));
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
