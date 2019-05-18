package it.unibo.ai.didattica.competition.tablut.util;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class MinMaxThread extends Thread {

    private BlockingQueue<CommunicationUnit> queueCu;
    private BlockingQueue<Result> queueResult;

    private Action[] actions;
    private it.unibo.ai.didattica.competition.tablut.domain.State state;
    private int maxDepth;
    private long timeMs;
    private long timeout;
    private double numThread;
    private int turn;
    private boolean maxDepthAlreadyUpdated = false;
    private String me;
    private HashMap<String, Integer> stateMap;
    private int nodes;
    private int tagli;

    public MinMaxThread(BlockingQueue<CommunicationUnit> queueCu, BlockingQueue<Result> queueResult) {
        this.queueCu = queueCu;
        this.queueResult = queueResult;
        this.stateMap = new HashMap<>();

    }

    @Override
    public void run() {
        CommunicationUnit cu;
        boolean timeout;
        Thread.currentThread().setPriority(MAX_PRIORITY);
        try {
            while (true) {
                this.nodes = 0;
                this.tagli = 0;
                stateMap.clear();
                cu = queueCu.take();
                timeout = false;

                this.actions = cu.getActions();
                this.state = cu.getState();
                this.maxDepth = cu.getMaxDepth();
                this.timeMs = cu.getTimeMs();
                this.timeout = cu.getTimeout();
                this.turn = cu.getTurn();
                this.me = cu.getMe();


                System.out.println(Thread.currentThread().getName() + " inizio con " + actions.length + "azioni");
                String st;
                int v = -20000;
                Action result = null;
                int temp;
                it.unibo.ai.didattica.competition.tablut.domain.State stateTemp;
                //System.out.println(Thread.currentThread().getName() + " parto con azioni " + this.actions.size());
                for (Action action : this.actions) {

                    nodes++;

                    stateTemp = resultState(this.state, action);
                    st = stateTemp.toLinearString();
                    if(!stateMap.containsKey(st)) {
                        stateMap.put(st, 0);
                        //valuto azione e metto dentro struttura dati

                        temp = maxValue(stateTemp, -20000, 20000, 0, this.maxDepth);
                        if (temp > v && temp != 20000) {
                            v = temp;
                            result = action;
                        }
                        if (System.currentTimeMillis() - this.timeMs > this.timeout) {
                            //System.out.println(Thread.currentThread().getName() + " termino per timeout");
                            System.out.println(Thread.currentThread().getName() + " valore azione timeout " + v);
                            timeout = true;
                            queueResult.put(new Result(v, result, nodes));
                            break;
                        }
                    } else
                        tagli++;
                }
                //System.out.println(Thread.currentThread().getName() + " termino");
                if(!timeout) {
                    System.out.println(Thread.currentThread().getName() + " valore azione " + v);
                    queueResult.put(new Result(v, result, nodes));
                }

                //System.err.println("Tagli: " + tagli);
                /*for(String s : stateMap.keySet())
                    System.err.println(Thread.currentThread().getName() + "     " + s + "        " + stateMap.get(s));*/
            }
        } catch (InterruptedException e) {
            System.err.println("ERRORE");
        }

        System.out.println("Finisco");

    }

    private int maxValue(it.unibo.ai.didattica.competition.tablut.domain.State state, int alpha, int beta, int depth, int maxDepth) {
        //nodes++;
        if(state.isTerminalWhite()) {
            System.err.println("Trovato terminal white");
            System.err.println(state.toString());
            return 10000 * (this.me.equals("W") ? 1 : -1);
        }
        else if(state.isTerminalBlack()) {
            System.err.println("Trovato terminal black");
            System.err.println(state.toString());
            return -10000 * (this.me.equals("W") ? 1 : -1);
        }
        /*else if(!maxDepthAlreadyUpdated && depth == maxDepth && System.currentTimeMillis() - this.timeMs < 26000) {
            maxDepthAlreadyUpdated = true;
            maxDepth++;
        }*/
        else if(depth == maxDepth || System.currentTimeMillis() - this.timeMs > timeout)
            if(this.me.equals("W"))
                return Evaluation.evaluate(state, this.turn);
            else if(this.me.equals("B"))
                return -Evaluation.evaluate(state, this.turn);
            else
                throw new InputMismatchException();


        //nodes.incrementAndGet();

        List<Action> actions = state.getAllLegalMoves();
        String st;
        int v = -20000;
        it.unibo.ai.didattica.competition.tablut.domain.State stateTemp;
        for (Action action : actions) {
            stateTemp = resultState(state, action);
            st = stateTemp.toLinearString();
            if(!stateMap.containsKey(st) || stateMap.get(st) > depth) {
                stateMap.put(st, depth);
                v = Math.max(v, minValue(stateTemp, alpha, beta, depth + 1, maxDepth));
                if (v >= beta)
                    return v;
                alpha = Math.max(alpha, v);
            } else {
                tagli++;
            }
        }
        return v;
    }

    private int minValue(it.unibo.ai.didattica.competition.tablut.domain.State state, int alpha, int beta, int depth, int maxDepth) {

        //nodes++;


        if(state.isTerminalWhite()) {
            System.err.println("Trovato terminal white");
            System.err.println(state.toString());
            return 10000 * (this.me.equals("W") ? 1 : -1);
        }
        else if(state.isTerminalBlack()) {
            System.err.println("Trovato terminal black");
            System.err.println(state.toString());
            return -10000 * (this.me.equals("W") ? 1 : -1);
        }
        /*else if(!maxDepthAlreadyUpdated && depth == maxDepth && System.currentTimeMillis() - this.timeMs < 26000 ) {
            maxDepthAlreadyUpdated = true;
            maxDepth++;
        }*/
        else if (depth == maxDepth || System.currentTimeMillis() - this.timeMs > timeout)
            if(this.me.equals("W"))
                return Evaluation.evaluate(state, this.turn);
            else if(this.me.equals("B"))
                return -Evaluation.evaluate(state, this.turn);
            else
                throw new InputMismatchException();


        //return 1000, 0 o -1000 a seconda del caso

        //nodes.incrementAndGet();
        //else continua
        List<Action> actions = state.getAllLegalMoves();
        String st;
        it.unibo.ai.didattica.competition.tablut.domain.State stateTemp;
        int v = 20000;
        for (Action action : actions) {
            stateTemp = resultState(state, action);
            st = stateTemp.toLinearString();
            if(!stateMap.containsKey(st) || stateMap.get(st) > depth) {
                stateMap.put(st, depth);
                v = Math.min(v, maxValue(stateTemp, alpha, beta, depth + 1, maxDepth));
                if (v <= alpha)
                    return v;
                beta = Math.max(beta, v);
            } else
                tagli++;
        }
        return v;
    }

    private it.unibo.ai.didattica.competition.tablut.domain.State resultState(it.unibo.ai.didattica.competition.tablut.domain.State state, Action action) {
        it.unibo.ai.didattica.competition.tablut.domain.State returnState = state.clone();
        returnState.move(action);
        return returnState;
    }
}
