package com.mycompany.recommenderengine;

import javax.swing.*;    
import java.awt.event.*;    
import java.util.HashSet;
import java.util.Set;
public class GUI {    
JFrame f;    
GUI(){    
    Recommender ob = new Recommender();
    ob.generateNetwork();
    ob.getUsers();
    ob.getAllCompletePaths();
    
    Set<Integer> inputUsers = new HashSet<>(ob.source);
    String dropDownList[] = new String[inputUsers.size()];
    int i=0;
    for (int element:inputUsers)
        dropDownList[i++] = Integer.toString(element);
    
    f=new JFrame("ComboBox Example");   
    final JLabel label = new JLabel();          
    label.setHorizontalAlignment(JLabel.CENTER);  
    label.setSize(800,400);  
    JButton b=new JButton("Show");  
    b.setBounds(200,100,75,20);  
    //String languages[]={"C","C++","C#","Java","PHP"};        
    final JComboBox cb=new JComboBox(dropDownList);    
    cb.setBounds(50, 100,90,20);    
    f.add(cb); f.add(label); f.add(b);    
    f.setLayout(null);    
    f.setSize(350,350);    
    f.setVisible(true);       
    b.addActionListener(new ActionListener() {  
        public void actionPerformed(ActionEvent e) {   
            ob.getCompletePathSetForTargetUser(Integer.parseInt(""+cb.getItemAt(cb.getSelectedIndex())));
            ob.gettingSST(Integer.parseInt(""+cb.getItemAt(cb.getSelectedIndex())));
            ob.generateSRP(Integer.parseInt(""+cb.getItemAt(cb.getSelectedIndex())));
            String output = "The top 5 recommendations are : ";
//            Set<Integer> setOfValues = new HashSet<>();
//            setOfValues = ob.finalOutput.values();
            int counter = 0;
            for (int x:ob.finalOutput.values()) {
                counter += 1;
                if (counter == 6)
                    break;
                output += x + ",";
            }
            
 
label.setText(output.substring(0,output.length()-1));  
} 
});           
}    
public static void main(String[] args) {    
    new GUI();         
}    
}    