package com.cdk.crm.util.mailUtility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public abstract class Template {
    protected String path;
    protected File mailMessage;


    public String readFile(){
        Scanner scan = null;
        try {
            scan = new Scanner(new FileReader(mailMessage));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String message = "";
        while(scan.hasNextLine()) {
            message += scan.nextLine()+"\n";
        }
        return message;
    }


}