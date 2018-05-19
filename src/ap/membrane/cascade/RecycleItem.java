/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ap.membrane.cascade;

/**
 *
 * @author Afke
 */
class RecycleItem {
    private Flow mFlow;
    private String mStage;
    private String mSource;
    
   RecycleItem(Flow pFlow, String pStage, String pSource){
       mFlow = new Flow();
       mFlow.xMakeFlow(pFlow);
       mStage = pStage;
       mSource = pSource;
   } 
   
   Flow xFlow(){
       return mFlow;
   }
   
   String xSource(){
       return mSource;
   }
   
   boolean xIsItem(String pStage, String pSource){
       if (mStage.equals(pStage) && mSource.equals(pSource)){
           return true;
       } else {
           return false;
       }
   }
}
