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
public class ParserException extends Exception{
    String errMesssage;
    public ParserException(String errMessage){
        this.errMesssage = errMessage;
    }
    @Override
    public String toString(){
        return this.errMesssage;
    }
}
