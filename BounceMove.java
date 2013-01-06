package team007.movement;

import team007.Action;
import team007.RobotPlayer;
import battlecode.common.*;

public class BounceMove implements Action { // wandering+ v1

    RobotController rc;
    boolean turning; // false if left, true if right
    Direction R1, R2, R3, O, L3, L2, L1;
    boolean isDiag = true;

    public BounceMove(RobotController rc) {
            this.rc = rc;
    }

    @Override
    public void execute() {
            if(!canExecute()) return;
            
            Direction oldDirection = rc.getDirection();
            try{

                    if(Clock.getRoundNum()%339 >= 334){
                            turning = !turning;
                    }
                    
                    if(rc.canMove(oldDirection)){
                            rc.moveForward();
                    }
                    else{
                            R1 = oldDirection.rotateRight();
                            R2 = oldDirection.rotateRight().rotateRight();
                            R3 = oldDirection.opposite().rotateLeft();
                            L3 = oldDirection.opposite().rotateRight();
                            L2 = oldDirection.rotateLeft().rotateLeft();
                            L1 = oldDirection.rotateLeft();
                            O = oldDirection.opposite();
                            isDiag = oldDirection.isDiagonal();
                            
                            
                            if(turning && isDiag){ // if wanting to go right
                                    //first try to turn 90, then 45, then 135. Then other way. If all fails, turn around.
                                    if( rc.canMove( R2 ) ){
                                            rc.setDirection( R2 );
                                    } else if( rc.canMove( R1 ) ){
                                            rc.setDirection( R1 );
                                    } else if( rc.canMove( L2 ) ){
                                            rc.setDirection( L2 );
                                            turning = !turning;
                                    } else if( rc.canMove( L1 )){
                                            rc.setDirection( L1 );
                                            turning = !turning;
                                    } else if( rc.canMove( R3 ) ){
                                            rc.setDirection( R3 );
                                    } else if( rc.canMove( L3 ) ){
                                            rc.setDirection( L3 );
                                    } else{
                                            rc.setDirection( O );
                                    }
                                    turning = !turning;
                                    rc.yield();
                            }
                            else if(turning && !isDiag){ // if wanting to go right
                                    if( rc.canMove( R1 ) ){
                                            rc.setDirection( R1 );
                                    } else if( rc.canMove( R3 ) ){
                                            rc.setDirection( R3 );
                                    } else if( rc.canMove( R2 ) ){
                                                    rc.setDirection( R2 );
                                    } else if( rc.canMove( L1 )){
                                            rc.setDirection( L1 );
                                            turning = !turning;
                                    } else if( rc.canMove( L3 ) ){
                                            rc.setDirection( L3 );
                                    } else if( rc.canMove( L2 ) ){
                                            rc.setDirection( L2 );
                                            turning = !turning;
                                    } else{
                                            rc.setDirection( O );
                                    }
                                    turning = !turning;
                                    rc.yield();
                            }
                            else if(!turning && isDiag){ // if wanting to go left
                                    if( rc.canMove( L2 ) ){
                                            rc.setDirection( L2 );
                                    } else if( rc.canMove( L1 ) ){
                                            rc.setDirection( L1 );
                                    } else if( rc.canMove( R2 ) ){
                                            rc.setDirection( R2 );
                                            turning = !turning;
                                    } else if( rc.canMove( R1 )){
                                            rc.setDirection( R1 );
                                            turning = !turning;
                                    } else if( rc.canMove( L3 ) ){
                                            rc.setDirection( L3 );
                                    } else if( rc.canMove( R3 ) ){
                                            rc.setDirection( R3 );
                                    } else{
                                            rc.setDirection( O );
                                    }
                                    turning = !turning;
                                    rc.yield();
                            }
                            else if(!turning && !isDiag){ // if wanting to go left
                                    if( rc.canMove( L1 ) ){
                                            rc.setDirection( L1 );
                                    } else if( rc.canMove( L3 ) ){
                                            rc.setDirection( L3 );
                                    } else if( rc.canMove( L2 ) ){
                                                    rc.setDirection( L2 );
                                    } else if( rc.canMove( R1 )){
                                            rc.setDirection( R1 );
                                            turning = !turning;
                                    } else if( rc.canMove( R3 ) ){
                                            rc.setDirection( R3 );
                                    } else if( rc.canMove( R2 ) ){
                                            rc.setDirection( R2 );
                                            turning = !turning;
                                    } else{
                                            rc.setDirection( O );
                                    }
                                    turning = !turning;
                                    rc.yield();
                            }
                    }
            } catch(Exception e){
                    //System.out.println("Error in BounceAction.execute()");
                    e.printStackTrace();
            }
    }

    public boolean canExecute(){
            return !(rc.isMovementActive());
    }
    
    public String toString(){
            return "BounceAction";
    }
}
