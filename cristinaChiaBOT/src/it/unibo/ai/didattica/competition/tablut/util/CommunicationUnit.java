package it.unibo.ai.didattica.competition.tablut.util;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.concurrent.ConcurrentHashMap;

public class CommunicationUnit {
    private Action[] actions;
    private State state;
    private int maxDepth;
    private long timeMs;
    private long timeout;
    private String me;
    private ConcurrentHashMap statesMap;


    public CommunicationUnit(Action[] actions, State state, int maxDepth, long timeMs, long timeout, String me, ConcurrentHashMap statesMap) {
        this.actions = actions;
        this.state = state;
        this.maxDepth = maxDepth;
        this.timeMs = timeMs;
        this.timeout = timeout;
        this.me = me;
        this.statesMap = statesMap;
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
    public String getMe() {
        return me;
    }
    public ConcurrentHashMap getStatesMap() { return statesMap; }
}
