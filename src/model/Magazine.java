package model;

import java.io.Serializable;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Joseph
 */

public class Magazine implements Serializable{
    private String magName;
    private float price;
    
    public Magazine(String magName, float price){
        this.magName = magName;
        this.price = price;
    }
    
    public String getName(){
        return magName;
    }  
    
    public float getPrice(){
       return price; 
    }
    
    public float setName(String magName){
        return 0;
    }
    
    public float setPrice(float price){
        if(price < 0){
               System.out.println("invalid flaot was inputted");
           }
        return 0;
        }
    }
//class Singleton {
//    private static Singleton singleInstance = null;
//
//    public String name;
//    public int price;
//    
//    private Singleton(){
//        this.name = name;
//        this.price = price; 
//    }
//
//    public static synchronized getInstance(){
//        if(singleInstance == null)
//            singleInstance = new Singleton();
//        return singleInstance;
//    }
//}
