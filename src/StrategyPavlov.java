/**
 * Class containing the tit-for-tat strategy.
 * @author	081028AW
 */
public class StrategyPavlov extends Strategy
   {
  /**
   * Encoding for tit-for-tat strategy.
   */

  // 0 = defect, 1 = cooperate

   public StrategyPavlov()
      {
      name = "Pavlov";
      opponentLastMove = 1;
      myLastMove = 1;
      }

   public int nextMove()
      {
	   if (opponentLastMove == 1)
		   //this means opponent cooperated. 
		   //regardless of whether you cooperated or not, you benefited
		   //therefore, keep your old decision
			   return myLastMove;
	   else
		   //opponent defected. whether you cooperated, or defected, this harms you.
		   //therefore, do the opposite of your last decision
		   if (myLastMove == 0)
			   return 1;
		   else 
			   return 0;
      }  /* nextMove */

   }  /* class StrategyPavlov */

