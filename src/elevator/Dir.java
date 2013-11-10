package elevator;

/**
 * 
 * @author Wendy
 * Used by elevator and riders 
 * If rider, 
 * 	DOWN = rider wants to enter elevator going down
 * 	UP = rider wants to enter elevator going up
 * 	X = rider wants to exit elevator (direction doesn't matter)
 * 
 * If elevator,
 * 	DOWN = elevator currently going down
 * 	UP = elevator currently going up
 * 	X = elevator has no preference (idle)
 */
public enum Dir {
	DOWN, X, UP
}
