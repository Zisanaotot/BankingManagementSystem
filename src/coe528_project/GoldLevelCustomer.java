/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coe528_project;

/**
 *
 * @author alifa
 */
public class GoldLevelCustomer implements AccountLevel {
    
    @Override
    public double calculateFee(double purchaseAmount) {
        return purchaseAmount + 10.0; // Gold level incurs a fee of $10 for online purchases
    }
    
    @Override
    public String toString(){
        return "Gold";
    }
    
}
