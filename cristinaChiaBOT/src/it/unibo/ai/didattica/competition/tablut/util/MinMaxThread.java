package it.unibo.ai.didattica.competition.tablut.util;

import it.unibo.ai.didattica.competition.tablut.domain.Action;

import java.util.InputMismatchException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class MinMaxThread extends Thread {

    private BlockingQueue<CommunicationUnit> queueCu;
    private BlockingQueue<Result> queueResult;

    private Action[] actions;
    private it.unibo.ai.didattica.competition.tablut.domain.State state;
    private int maxDepth;
    private long timeMs;
    private long timeout;
    private String me;
    private ConcurrentHashMap<String, Integer> statesMap;

    public MinMaxThread(BlockingQueue<CommunicationUnit> queueCu, BlockingQueue<Result> queueResult) {
        this.queueCu = queueCu;
        this.queueResult = queueResult;
    }

    @Override
    public void run() {
        CommunicationUnit cu;
        boolean timeout;
        Thread.currentThread().setPriority(MAX_PRIORITY);
        try {
            while (true) {
                cu = queueCu.take();
                timeout = false;

                this.actions = cu.getActions();
                this.state = cu.getState();
                this.maxDepth = cu.getMaxDepth();
                this.timeMs = cu.getTimeMs();
                this.timeout = cu.getTimeout();
                this.me = cu.getMe();
                this.statesMap = cu.getStatesMap();

                String st;
                int v = -20000;
                Action result = null;
                int temp;
                it.unibo.ai.didattica.competition.tablut.domain.State stateTemp;
                for (Action action : this.actions) {
                    stateTemp = resultState(this.state, action);
                    st = stateTemp.toLinearString();
                    if(!statesMap.containsKey(st)) {
                        statesMap.put(st, 0);
                        temp = maxValue(stateTemp, -20000, 20000, 0, this.maxDepth);
                        if (temp > v && temp != 20000) {
                            v = temp;
                            result = action;
                        }
                        if (System.currentTimeMillis() - this.timeMs > this.timeout) {
                            timeout = true;
                            queueResult.put(new Result(v, result));
                            break;
                        }
                    }
                }
                if(!timeout) {
                    queueResult.put(new Result(v, result));
                }
            }
        } catch (InterruptedException e) {
            System.err.println("ERRORE");
        }

        System.out.println("Finisco");

    }

    private int maxValue(it.unibo.ai.didattica.competition.tablut.domain.State state, int alpha, int beta, int depth, int maxDepth) {
        if(state.isTerminalWhite()) {
            return 10000 * (this.me.equals("W") ? 1 : -1);
        }
        else if(state.isTerminalBlack()) {
            return -10000 * (this.me.equals("W") ? 1 : -1);
        }
        else if(depth == maxDepth || System.currentTimeMillis() - this.timeMs > timeout)
            if(this.me.equals("W"))
                return Evaluation.evaluate(state);
            else if(this.me.equals("B"))
                return -Evaluation.evaluate(state);
            else
                throw new InputMismatchException();

        List<Action> actions = state.getAllLegalMoves(false);
        String st;
        int v = -20000;
        it.unibo.ai.didattica.competition.tablut.domain.State stateTemp;
        for (Action action : actions) {
            stateTemp = resultState(state, action);
            st = stateTemp.toLinearString();
            if(!statesMap.containsKey(st) || statesMap.get(st) > depth) {
                statesMap.put(st, depth);
                v = Math.max(v, minValue(stateTemp, alpha, beta, depth + 1, maxDepth));
                if (v >= beta)
                    return v;
                alpha = Math.max(alpha, v);
            }
        }
        return v;
    }

    private int minValue(it.unibo.ai.didattica.competition.tablut.domain.State state, int alpha, int beta, int depth, int maxDepth) {

        if(state.isTerminalWhite()) {
            return 10000 * (this.me.equals("W") ? 1 : -1);
        }
        else if(state.isTerminalBlack()) {
            return -10000 * (this.me.equals("W") ? 1 : -1);
        }

        else if (depth == maxDepth || System.currentTimeMillis() - this.timeMs > timeout)
            if(this.me.equals("W"))
                return Evaluation.evaluate(state);
            else if(this.me.equals("B"))
                return -Evaluation.evaluate(state);
            else
                throw new InputMismatchException();

        List<Action> actions = state.getAllLegalMoves(false);
        String st;
        it.unibo.ai.didattica.competition.tablut.domain.State stateTemp;
        int v = 20000;
        for (Action action : actions) {
            stateTemp = resultState(state, action);
            st = stateTemp.toLinearString();
            if(!statesMap.containsKey(st) || statesMap.get(st) > depth) {
                statesMap.put(st, depth);
                v = Math.min(v, maxValue(stateTemp, alpha, beta, depth + 1, maxDepth));
                if (v <= alpha)
                    return v;
                beta = Math.max(beta, v);
            }
        }
        return v;
    }

    private it.unibo.ai.didattica.competition.tablut.domain.State resultState(it.unibo.ai.didattica.competition.tablut.domain.State state, Action action) {
        it.unibo.ai.didattica.competition.tablut.domain.State returnState = state.clone();
        returnState.move(action);
        return returnState;
    }
}
