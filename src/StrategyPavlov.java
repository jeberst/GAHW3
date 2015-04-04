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
	   if (opponentLastMove == myLastMove)
		   return 1;
	   else
		   return 0;
      }  /* nextMove */

   }  /* class StrategyTitForTat */

