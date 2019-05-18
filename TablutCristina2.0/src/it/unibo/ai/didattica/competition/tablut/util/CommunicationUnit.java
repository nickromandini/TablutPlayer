package it.unibo.ai.didattica.competition.tablut.util;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public class CommunicationUnit {
    private Action[] actions;
    private State state;
    private int maxDepth;
    private long timeMs;
    private long timeout;
    private int turn;
    private String me;
    public CommunicationUnit(Action[] actions, State state, int maxDepth, long timeMs, long timeout, int turn, String me) {
        this.actions = actions;
        this.state = state;
        this.maxDepth = maxDepth;
        this.timeMs = timeMs;
        this.timeout = timeout;
        this.turn = turn;
        this.me = me;
    }

    public Action[] getActions() {
        return actions;
    }
    public State getState() {
        return state;
    }
    public int getMaxDepth() {
        return maxDepth;
    }
    public long getTimeMs() {
        return timeMs;
    }
    public long getTimeout() {
        return timeout;
    }
    public int getTurn() {
        return turn;
    }
    public String getMe() {
        return me;
    }
}
