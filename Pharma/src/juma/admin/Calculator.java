package juma.admin;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import juma.api.Parser;
import juma.api.ParserException;

public class Calculator extends Stage {
    BorderPane root = new BorderPane();
    public GridPane pane = new GridPane();
    TextField screen = new TextField();
    Button plus = new Button("+");
    Button minus = new Button("-");
    Button times = new Button("*");
    Button divide = new Button("/");
    Button zero = new Button("0");
    Button zero_zero = new Button("00");
    Button one = new Button("1");
    Button two = new Button("2");
    Button three = new Button("3");
    Button four = new Button("4");
    Button five = new Button("5");
    Button six = new Button("6");
     Button seven = new Button("7");
    Button eight = new Button("8");
    Button nine = new Button("9");
    Button point = new Button(".");
    Button c = new Button("C");
    Button clear = new Button("Clear");
    Button equal = new Button("=");
    StringBuffer text = new StringBuffer();
    boolean put = true;
    public Calculator(){
        root.setId("root");
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/juma/styles/calculator.css");
        this.setScene(scene);
        this.initStyle(StageStyle.UNDECORATED);
        this.setResizable(false);
        this.setTitle("Simple Calculator");
        this.initModality(Modality.WINDOW_MODAL);
        this.initOwner(App.stage);
        Rectangle2D rect = Screen.getPrimary().getBounds();
        this.setX(rect.getMaxX()/1.50);
        this.setY(rect.getMinY());
        this.setWidth(rect.getWidth()/3);
    }
    public void run() {
        BorderPane top = new BorderPane();
        top.setId("top");
        Label close = new Label("x");Label title= new Label("Simple Calculator");
        close.setId("close");title.setId("title");
        close.setOnMouseClicked(e->{this.close();});
        top.setLeft(close);top.setRight(title);
        root.setTop(top);
        
        screen.setId("screen");
        screen.setText("0");
        screen.setDisable(true);
        pane.add(screen,0,0,4,1);
       
        plus.setId("blue"); minus.setId("blue"); times.setId("blue"); divide.setId("blue");
        pane.add(plus, 0, 1); pane.add(minus, 0, 2); pane.add(times, 0, 3); pane.add(divide, 0, 4);
        zero.setId("green"); zero_zero.setId("green"); one.setId("green"); two.setId("green");
        three.setId("green");four.setId("green");five.setId("green");six.setId("green");seven.setId("green");
        eight.setId("green");nine.setId("green");point.setId("green");
        pane.add(three, 1, 1); pane.add(two, 1, 2); pane.add(one, 1, 3); pane.add(zero, 1, 4);
        pane.add(six, 2, 1); pane.add(five, 2, 2); pane.add(four, 2, 3); pane.add(zero_zero, 2, 4);
        pane.add(nine, 3, 1); pane.add(eight, 3, 2); pane.add(seven, 3, 3); pane.add(point, 3, 4);
        c.setId("dark");clear.setId("clear");equal.setId("dark");
        pane.add(c, 0, 5); pane.add(clear, 1, 5,2,1); pane.add(equal, 3, 5);
        
        plus.setOnAction(e->{
            if(!text.toString().isEmpty()){
                switch (text.charAt(text.length()-1)) {
                    case '.':
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                    case ' ':
                        text.append("");
                        break;
                    default:
                        String space = " "+plus.getText()+" ";
                        text.append(space);
                        put = true;
                        break;
                }
                screen.setText(text.toString());
                screen.selectEnd();
            }
            
        });
        
        minus.setOnAction(e->{
            if(!text.toString().isEmpty()){
                switch (text.charAt(text.length()-1)) {
                    case '.':
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                    case ' ':
                        text.append("");
                        break;
                    default:
                        String space = " "+minus.getText()+" ";
                        text.append(space);
                        put = true;
                        break;
                }
            }else{
                 text.append("-");
            }
            screen.setText(text.toString());
            screen.selectEnd();
        });
        times.setOnAction(e->{
            if(!text.toString().isEmpty()){
                switch (text.charAt(text.length()-1)) {
                    case '.':
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                    case ' ':
                        text.append("");
                        break;
                    default:
                        String space = " "+times.getText()+" ";
                        text.append(space);
                        put = true;
                        break;
                }
                screen.setText(text.toString());
                screen.selectEnd();
            }
            
        });
        divide.setOnAction(e->{
           if(!text.toString().isEmpty()){
                switch (text.charAt(text.length()-1)) {
                    case '.':
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                    case ' ':
                        text.append("");
                        break;
                    default:
                        String space = " "+divide.getText()+" ";
                        text.append(space);
                        put = true;
                        break;
                }
                screen.setText(text.toString());
                screen.selectEnd();
            }
            
        });
      
        point.setOnAction(e->{
            if(!text.toString().isEmpty()){
                switch (text.charAt(text.length()-1)) {
                    case '.':
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                    case ' ':
                        text.append("");
                        break;
                    default:
                        if(put){
                             text.append(".");
                             put = false;
                        }
                       
                        break;
                }
            }else{
                 text.append("0.");
                  put = false;
            }
            screen.setText(text.toString());
            screen.selectEnd();
        });

        zero_zero.setOnAction(e->{
             if(!text.toString().isEmpty()){
                switch (text.charAt(text.length()-1)) {
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                    case ' ':
                        text.append("");
                        break;
                    default:
                        if(text.toString().endsWith("+ 0") || text.toString().endsWith("- 0") || text.toString().endsWith("/ 0") || 
                                text.toString().endsWith("* 0")){
                            text.append("");
                        }else{
                            text.append(zero_zero.getText());
                        }
                        
                        break;
                }
               screen.setText(text.toString());
               screen.selectEnd();
            }
            
        });
        zero.setOnAction(e->{
            if(!text.toString().isEmpty()){
                    if(text.toString().endsWith("+ 0") || text.toString().endsWith("- 0") || text.toString().endsWith("/ 0") || 
                            text.toString().endsWith("* 0")){
                        text.append("");
                    }else{
                        text.append(zero.getText());
                    }
                    
                    screen.setText(text.toString());
                    screen.selectEnd();
                        
                }
               
        });
        one.setOnAction(e->{
            if(!text.toString().isEmpty()){
                    if(text.toString().endsWith("+ 0") || text.toString().endsWith("- 0") || text.toString().endsWith("/ 0") || 
                            text.toString().endsWith("* 0")){
                        text.append("");
                    }else{
                        text.append(one.getText());
                    }
                    
                        
            }else{
                 text.append(one.getText());
                
            }
            
          screen.setText(text.toString());
          screen.selectEnd();
        });
         two.setOnAction(e->{
             if(!text.toString().isEmpty()){
                    if(text.toString().endsWith("+ 0") || text.toString().endsWith("- 0") || text.toString().endsWith("/ 0") || 
                            text.toString().endsWith("* 0")){
                        text.append("");
                    }else{
                        text.append(two.getText());
                    }
                    
                        
            }else{
                 text.append(two.getText());
                
            }
            
          screen.setText(text.toString());
          screen.selectEnd();
        });
         three.setOnAction(e->{
             if(!text.toString().isEmpty()){
                    if(text.toString().endsWith("+ 0") || text.toString().endsWith("- 0") || text.toString().endsWith("/ 0") || 
                            text.toString().endsWith("* 0")){
                        text.append("");
                    }else{
                        text.append(three.getText());
                    }
                    
                        
            }else{
                 text.append(three.getText());
                
            }
            
          screen.setText(text.toString());
          screen.selectEnd();
        });
         four.setOnAction(e->{
             if(!text.toString().isEmpty()){
                    if(text.toString().endsWith("+ 0") || text.toString().endsWith("- 0") || text.toString().endsWith("/ 0") || 
                            text.toString().endsWith("* 0")){
                        text.append("");
                    }else{
                        text.append(four.getText());
                    }
                    
                        
            }else{
                 text.append(four.getText());
                
            }
            
          screen.setText(text.toString());
          screen.selectEnd();
        });
         five.setOnAction(e->{
             if(!text.toString().isEmpty()){
                    if(text.toString().endsWith("+ 0") || text.toString().endsWith("- 0") || text.toString().endsWith("/ 0") || 
                            text.toString().endsWith("* 0")){
                        text.append("");
                    }else{
                        text.append(five.getText());
                    }
                    
                        
            }else{
                 text.append(five.getText());
                
            }
            
          screen.setText(text.toString());
          screen.selectEnd();
        });
        six.setOnAction(e->{
             if(!text.toString().isEmpty()){
                    if(text.toString().endsWith("+ 0") || text.toString().endsWith("- 0") || text.toString().endsWith("/ 0") || 
                            text.toString().endsWith("* 0")){
                        text.append("");
                    }else{
                        text.append(six.getText());
                    }
                    
                        
            }else{
                 text.append(six.getText());
                
            }
            
          screen.setText(text.toString());
          screen.selectEnd();
        });
        seven.setOnAction(e->{
             if(!text.toString().isEmpty()){
                    if(text.toString().endsWith("+ 0") || text.toString().endsWith("- 0") || text.toString().endsWith("/ 0") || 
                            text.toString().endsWith("* 0")){
                        text.append("");
                    }else{
                        text.append(seven.getText());
                    }
                    
                        
            }else{
                 text.append(seven.getText());
                
            }
            
          screen.setText(text.toString());
          screen.selectEnd();
        });
        eight.setOnAction(e->{
             if(!text.toString().isEmpty()){
                    if(text.toString().endsWith("+ 0") || text.toString().endsWith("- 0") || text.toString().endsWith("/ 0") || 
                            text.toString().endsWith("* 0")){
                        text.append("");
                    }else{
                        text.append(eight.getText());
                    }
                    
                        
            }else{
                 text.append(eight.getText());
                
            }
            
          screen.setText(text.toString());
          screen.selectEnd();
        });
         nine.setOnAction(e->{
             if(!text.toString().isEmpty()){
                    if(text.toString().endsWith("+ 0") || text.toString().endsWith("- 0") || text.toString().endsWith("/ 0") || 
                            text.toString().endsWith("* 0")){
                        text.append("");
                    }else{
                        text.append(nine.getText());
                    }
                    
                        
            }else{
                 text.append(nine.getText());
                
            }
            
          screen.setText(text.toString());
          screen.selectEnd();
        });
        c.setOnAction(e->{
            if(text.length() != 0){
                char ch = text.charAt(text.length()-1);
                if(ch == '.'){
                    put = true;
                }
                if(ch == ' '){
                    text.delete(text.length()-3, text.length()-1);
                }
                text.deleteCharAt(text.length()-1);
                screen.setText(text.toString());
                if(text.toString().isEmpty()){
                    screen.setText("0");
                }
                if(text.length()==1 && text.charAt(text.length()-1) == '0'){
                    text.replace(0, 1, "");
                }
              
                screen.selectEnd();
               
            }else{
                screen.setText("0");
            }
           
        });
        clear.setOnAction(e->{
            text.delete(0, text.length());
            screen.setText("0");
            screen.selectEnd();
             put = true;
        });
        equal.setOnAction(e->{
            try {
                put = true;
                double result = new Parser().evaluate(text.toString());
                screen.setText(String.valueOf(result));
                screen.selectEnd();
                text.delete(0, text.length());
                text.append(result);
            } catch (ParserException ex) {
                
            }
        });
        pane.setVgap(1);
        pane.setHgap(1);
        pane.setId("grid");
        pane.setAlignment(Pos.CENTER);
        root.setCenter(pane);
        this.showAndWait();
    }

}
