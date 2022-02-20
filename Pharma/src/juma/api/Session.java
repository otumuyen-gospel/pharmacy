/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juma.api;

/**
 *
 * @author user1
 */
public class Session {
    static Object value;
    public static void set(Object value){
        Session.value = value;
    }
    public static Object get(){
        return value;
    }
    public static void destroy(){
        value = null;
    }
}
