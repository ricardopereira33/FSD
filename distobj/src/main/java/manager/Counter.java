/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manager;

/**
 *
 * @author Ricardo
 */
class Counter {
    private int num;
    private int index;
    
    public Counter(int num){
        this.num = num;
        this.index = 0;
    }
    
    public void incr(int sum){
        num+=sum;
    }
    
    public void setNum(int num){
        this.num = num;
    }
    
    public int getNum(){
        return num;
    }
    
    public void setIndex(int i){
        this.index = i;
    }
    
    public int getIndex(){
        return index;
    }
}
