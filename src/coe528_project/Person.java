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
public class Person {
    private String username;
    private String password;
    private String role;
    
    public Person(String username, String password, String role){
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    public String getUsername(){
        return this.username;
    }
    
    public String getPassword(){
        return this.password;
    }
    
    public String getRole(){
        return this.role;
    }
    
}
