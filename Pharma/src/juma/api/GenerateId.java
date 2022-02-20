/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juma.api;

import java.util.Random;

/**
 *
 * @author user1
 */
public class GenerateId {
    static String id="Rs-";
    public static void set(){
        String alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rand = new Random();
        for(int i = 0;i<10;i++){
           id+=alphanumeric.charAt(rand.nextInt(alphanumeric.length()));
        }
    }
    public static String get(){
        return id;
    }
     public static void reset(){
        id = "Rs-";
    }
}
