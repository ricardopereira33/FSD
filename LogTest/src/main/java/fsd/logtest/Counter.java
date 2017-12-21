/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fsd.logtest;

/**
 *
 * @author Ricardo
 */
class Counter {
    private int num;
    
    public Counter(int num){
        this.num = num;
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
}
