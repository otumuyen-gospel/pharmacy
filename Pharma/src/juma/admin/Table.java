package juma.admin;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author user1
 */
public class Table extends GridPane{
   String[]columns;
   int col = 0;
   int row = 0;
   int tableColumnLength;
   int rowSize;
   int arr_row = 0;
   Node[][]rows;
   int anticlockwise;
    public Table(){
        this.setId("table");
    }
    public void setRowSize(int rowSize){
        this.rowSize = rowSize;
    }
    public void setColumns(String [] columns){
        this.columns = columns;
        tableColumnLength = columns.length;
        anticlockwise = columns.length;
        rows = new Node[rowSize][tableColumnLength];
       for (String column : columns) {
           Label columnLabel = new Label(column);
           columnLabel.setWrapText(true);
           this.add(columnLabel, col, row);
           columnLabel.setId("columnHeader");
           col++;
       }
       //prepare for next Row
       this.nextRow();
    }
    private void nextRow(){
        row++;col= 0; arr_row = row -1;
    }
    public String[] getColumns(){
        return this.columns;
    }
    public void AddRow(Node nodes[]){
        for(Node node:nodes){
            this.add(node, col, row);
            rows[arr_row][col] = node;
            if(col >= tableColumnLength-1){
                this.nextRow();
            }else{
                col++;
            }
        }
    }
    public void AddFooterRows(Node nodes[]){
        int step = 2;
        for(Node node:nodes){
            this.add(node, (anticlockwise-step), row);
            if(step <= 1){
                this.nextRow();
                step = 2;
            }else{
                step--;
            }
        }
    }
    public Node[] getRow(int index){
        Node[] rowset = new Node[tableColumnLength];
       System.arraycopy(rows[index], 0, rowset, 0, rowset.length);
        return rowset;
    }
    
}
