/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.java_course;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Василий
 */
public class Client extends Socket implements Runnable
{  
    private static double start;
    private static double finish;
    private static double step;
    private static double res = 0;
    private static String[] results;
    private static String result;
    
    public void setValues(double step, double start, double finish)
    {
        Client.step = step;
        Client.start = start;
        Client.finish = finish;
    }
    
    // Функция интегрирование
//    static double dataCalculation() throws InterruptedException
//    {
//        double res = 0;
//        int NumStep = 5;
//
//        // Интегрирование в отдельных потоках
//        ArrayList<IntRes> resInt = new ArrayList<IntRes>(); 
//
//        for (int j = 0; j < NumStep; j++)
//            resInt.add(new IntRes(step, 
//                    start+(finish-start)*j*((double)1 / (double)NumStep),
//                    start+(finish-start)*(j+1)*((double)1 / (double)NumStep)));
//
//        for (int j = 0; j < NumStep; j++)
//            resInt.get(j).start();
//
//        for (int j = 0; j < NumStep; j++)
//            resInt.get(j).join();
//
//        for (int j = 0; j < NumStep; j++)
//            res += resInt.get(j).getRes();
//
//        return res;
//    }
    
    @Override
    public void run()
    {
        // Запускаем подключение сокета по известным координатам и нициализируем приём сообщений с консоли клиента      
        try (Socket socket = new Socket("localhost", 80);
                DataOutputStream oos = new DataOutputStream(socket.getOutputStream());
                DataInputStream ois = new DataInputStream(socket.getInputStream()); )
        {

            // Проверяем живой ли канал и работаем если живой           
            while (!socket.isOutputShutdown())
            {               
                while (ois.available() == 0)
                    Thread.sleep(10);
                   
                result = ois.readUTF();
                results = result.split(" ");
                
                step = Double.parseDouble(results[0]);
                start = Double.parseDouble(results[1]);
                finish = Double.parseDouble(results[2]);
                
                try {      
                    res = Integral.TaskForFindIntegral(start, finish, step);
                } catch (MyException ex) {
                    Logger.getLogger(IntRes.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                oos.writeUTF(Double.toString(res));
                oos.flush();
                
                step = 0; start = 0; finish = 0;      
            }
        } 
        catch (UnknownHostException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException ex) 
        {
            ex.printStackTrace();
        } 
        catch (InterruptedException ex) 
        {
            ex.printStackTrace();
        }

    }
}

//class IntRes extends Thread
//{
//    private double step = 0;
//    private double start = 0;
//    private double finish = 0;
//    private double res = 0;
//    
//    IntRes(double step, double start, double finish)
//    {
//        this.step = step;
//        this.start = start;
//        this.finish = finish;
//    }
//    
//    public void run()
//    {              
//        try {      
//            res = Integral.TaskForFindIntegral(start, finish, step);
//        } catch (MyException ex) {
//            Logger.getLogger(IntRes.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//    
//    public double getRes() { return res; }
//}
