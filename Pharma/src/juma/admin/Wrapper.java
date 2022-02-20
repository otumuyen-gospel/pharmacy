
package juma.admin;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
public class Wrapper extends ScrollPane{
    public void setWrapper(Node content){
        this.getChildren().clear();
        this.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.setStyle("-fx-background-color:transparent;");
        this.getChildren().clear();
        this.setContent(content);
    }
    
}
