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
public class SilverLevelCustomer implements AccountLevel {

    @Override
    public double calculateFee(double purchaseAmount) {
        return purchaseAmount + 20.0;
    }
    
    @Override
    public String toString(){
        return "Silver";
    }
    
}
