/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juma.api;

import java.util.ArrayList;

/**
 *
 * @author user1
 */
public class Cart extends ArrayList<Query> {
    public void addItem(Query q){
        this.add(q);
    }
    public void removeItem(int index){
        this.remove(index);
    }
    public void removeItem(Query item){
        this.remove(item);
    }
    public Query getItem(int i){
        return (Query)this.get(i);
    }
}
