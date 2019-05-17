/*********** Da mettere in State ***********/
public int blackOnEscape() {
    if(kingCoord[0]==-1)
        getKingCoord();
    
    int pawns = 0;
    
    for (int[] pCoord : getPawnsCoord("B")) {
        
        for(int[] point : escapePoints) {
            if(point[0] == pCoord[0] && point[1] == pCoord[1])
                pawns++;
        }
            
    }
    
    return pawns;
        
}

//Queste dovrebbero sostituire la enemiesNearKing()

public int enemiesNearKingCardinal() {

    int enemies = 0;

    if(kingCoord[0] == -1) {
        kingCoord = getKingCoord();
    }
    
    //Pedina nera sopra il re
    if (kingCoord[0]-1 >= 0 && this.getPawn(kingCoord[0]-1, kingCoord[1]).equalsPawn(Pawn.BLACK.toString()))
        enemies++;
    
    //Pedina nera sotto il re
    if (kingCoord[0]+1 <= 8 && this.getPawn(kingCoord[0]+1, kingCoord[1]).equalsPawn(Pawn.BLACK.toString()))
        enemies++;
            
    //Pedina nera a sinistra del re
    if (kingCoord[1]-1 >= 0 && this.getPawn(kingCoord[0], kingCoord[1]-1).equalsPawn(Pawn.BLACK.toString()))
        enemies++;
    
    //Pedina nera a destra del re
    if (kingCoord[1]+1 <= 8 && this.getPawn(kingCoord[0], kingCoord[1]+1).equalsPawn(Pawn.BLACK.toString()))
        enemies++;
    
    return enemies;

}



public int enemiesNearKingDiagonal() {

    int enemies = 0;

    if(kingCoord[0] == -1) {
        kingCoord = getKingCoord();
    }
    
    //In alto a sinistra
    if (kingCoord[0]-1 >= 0 && kingCoord[1]-1 >= 0 && this.getPawn(kingCoord[0]-1, kingCoord[1]-1).equalsPawn(Pawn.BLACK.toString()))
        enemies++;
    
    //In alto a destra
    if (kingCoord[0]-1 >= 0 && kingCoord[1]+1 <= 8 && this.getPawn(kingCoord[0]-1, kingCoord[1]+1).equalsPawn(Pawn.BLACK.toString()))
        enemies++;
            
    //In basso a sinistra
    if (kingCoord[0]+1 <= 8 && kingCoord[1]-1 >= 0 && this.getPawn(kingCoord[0]+1, kingCoord[1]-1).equalsPawn(Pawn.BLACK.toString()))
        enemies++;
    
    //In basso a destra
    if (kingCoord[0]+1 <= 8 && kingCoord[1]+1 <= 8 &&this.getPawn(kingCoord[0]+1, kingCoord[1]+1).equalsPawn(Pawn.BLACK.toString()))
        enemies++;
    
    return enemies;

}








/*********** Da mettere in Evaluate ***********/
private static boolean blackOnEscape(State state, Action action) {
    int[] kingCoord = state.getKingCoord();
    List<int[]> escapePoints = state.getEscapePoints();
    int x = action.getRowTo();
    int y = action.getColumnTo();
    
    /*
     * I: (0,6), (0,7), (1,8), (2,8)
     * II: (6,8), (7,8), (8,6), (8,7)
     * III: (8,2), (8,1), (6,0), (7,0)
     * IV: (2,0), (1,0), (0,1), (0,2)
     */
    
    if (kingCoord[0] == 4 && kingCoord[1] == 4) {
        for(int[] point : escapePoints) {
            if(point[0] == x && point[1] == y)
                return true;
        }
        return false;
    }
    else if (kingCoord[0] <= 4) {
        
        if (kingCoord[1] >= 4) {
            /*controllo I quadrante*/
            if((x==0 && y==6) || (x==0 && y==7) || (x==1 && y==8) || (x==2 && y==8))
                return true;
            return false;
        }
        else {
            /*controllo IV quadrante*/
            if((x==2 && y==0) || (x==1 && y==0) || (x==0 && y==1) || (x==0 && y==2))
                return true;
            return false;
        }
        
    }
    else { //kingCoord[0] > 4
        
        if (kingCoord[1] >= 4) {
            /*controllo II quadrante*/
            if((x==6 && y==8) || (x==7 && y==8) || (x==8 && y==6) || (x==8 && y==7))
                return true;
            return false;
        }
        else {
            /*controllo III quadrante*/
            if((x==8 && y==2) || (x==8 && y==1) || (x==6 && y==0) || (x==7 && y==0))
                return true;
            return false;
        }
        
    }
        
}


private static boolean enemiesNearKingCardinal(State state, Action action) {

    int[] kingCoord = state.getKingCoord();
    
    //Pedina nera sopra il re
    if (kingCoord[0]-1 >= 0 && kingCoord[0]-1 == action.getRowTo() && kingCoord[1] == action.getColumnTo())
        return true;
    
    //Pedina nera sotto il re
    if (kingCoord[0]+1 <= 8 && kingCoord[0]+1 == action.getRowTo() && kingCoord[1] == action.getColumnTo())
        return true;
            
    //Pedina nera a sinistra del re
    if (kingCoord[1]-1 >= 0 && kingCoord[0] == action.getRowTo() && kingCoord[1]-1 == action.getColumnTo())
        return true;
    
    //Pedina nera a destra del re
    if (kingCoord[1]+1 <= 8 && kingCoord[0] == action.getRowTo() && kingCoord[1]+1 == action.getColumnTo())
        return true;
    
    return false;

}



private static boolean enemiesNearKingDiagonal(State state, Action action) {

    int[] kingCoord = state.getKingCoord();
    
    //In alto a sinistra
    if (kingCoord[0]-1 >= 0 && kingCoord[1]-1 >= 0 && kingCoord[0]-1 == action.getRowTo() && kingCoord[1]-1 == action.getColumnTo())
        return true;
    
    //In alto a destra
    if (kingCoord[0]-1 >= 0 && kingCoord[1]+1 <= 8 && kingCoord[0]-1 == action.getRowTo() && kingCoord[1]+1 == action.getColumnTo())
        return true;
            
    //In basso a sinistra
    if (kingCoord[0]+1 <= 8 && kingCoord[1]-1 >= 0 && kingCoord[0]+1 == action.getRowTo() && kingCoord[1]-1 == action.getColumnTo())
        return true;
    
    //In basso a destra
    if (kingCoord[0]+1 <= 8 && kingCoord[1]+1 <= 8 && kingCoord[0]+1 == action.getRowTo() && kingCoord[1]+1 == action.getColumnTo())
        return true;
    
    return false;

}






/**************** proposta di evaluate action ****************/
else { // caso black

			
    if(kingEatableWithAction(state, a))
        return 2000;
    
    if (isKingMarked(state, a))
        return 1000;

    int value = 0;
    
    if (checkCaptureBlackPawnLeft(state, a) || checkCaptureBlackPawnRight(state, a) || checkCaptureBlackPawnUp(state, a) || checkCaptureBlackPawnDown(state, a))
        value +=100;
        
    if (enemiesNearKingCardinal(state, a))
        value += 50;
    
    if (enemiesNearKingDiagonal(state, a))
        value += 30;
    
    if (blackOnEscape(state, a))
        value += 20;
    
    return value;
}






/**************** proposta di evaluate ****************/
