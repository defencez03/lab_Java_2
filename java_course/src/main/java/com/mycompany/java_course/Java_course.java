/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.java_course;

import java.io.BufferedReader;
import java.io.Serializable;
import javax.swing.table.DefaultTableModel;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 *
 * @author Василий
 */
public class Java_course 
{
    public static void main(String[] args)
    {
        System.out.println("Hello World!");
    }
}

class Result implements Serializable
{
    private String s1;
    private String s2;
    private String s3;
    private String s4;
    
    Result (String n1, String n2, String n3, String n4)
    {
        s1 = n1;
        s2 = n2;
        s3 = n3;
        s4 = n4;
    }
    
    String getS1() { return s1; }
    String getS2() { return s2; }
    String getS3() { return s3; }
    String getS4() { return s4; }
}

class IntRes extends Thread
{
    private double step = 0;
    private double start = 0;
    private double finish = 0;
    private double res = 0;
    
    IntRes(double step, double start, double finish)
    {
        this.step = step;
        this.start = start;
        this.finish = finish;
    }
    
    public void run()
    {              
        try {      
            res = Integral.TaskForFindIntegral(start, finish, step);
        } catch (MyException ex) {
            Logger.getLogger(IntRes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public double getRes() { return res; }
}

class ServerProcess implements Runnable
{
    private static Socket clientDialog;  
    private static double start = 0;
    private static double finish = 0;
    private static double step = 0;
    private static double res = 0;
    static ExecutorService executeProcs = Executors.newFixedThreadPool(5);
    static ExecutorService executeClients = Executors.newFixedThreadPool(5);
    private ArrayList<Socket> arrSocket = new ArrayList<Socket>();
    
    public void setValues(double step, double start, double finish)
    {
        ServerProcess.step = step;
        ServerProcess.start = start;
        ServerProcess.finish = finish;
    }
    
    public double getRes() { return res; }
    
    @Override
    public void run()
    {
        // запускаем подключение сокета по известным координатам и нициализируем приём сообщений с консоли клиента      
        try (ServerSocket serverSocket = new ServerSocket(80);
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in)))
        {
            System.out.println("Server to start...");
            
            // Запускаем клиенты
            for (int i = 0; i < 5; i++)
            {
                executeClients.execute(new Client());
                arrSocket.add(serverSocket.accept());
                //executeProcs.execute(new MonoThreadClientHandler(arrSocket.getLast()));
            }

            // Проверяем живой ли канал и работаем если живой           
            while (!serverSocket.isClosed())
            {               
                int count = 0;
                
                // Ожидаем получения результатов от пользователся
                while (step == 0 && start == 0 && finish == 0)
                        Thread.sleep(1);
                
                res = 0;
                
                // Отправляем данные клиентам
                for (var item : arrSocket)
                {
                    DataOutputStream oos = new DataOutputStream(item.getOutputStream());

                    oos.writeUTF(Double.toString(step) + " " +
                                 Double.toString(start+(finish-start)*count*((double)1 / (double)5)) + " " +
                                 Double.toString(start+(finish-start)*(count+1)*((double)1 / (double)5)));
                    oos.flush();
                    //oos.close();
                    count++;
                }
                
                // Получаем результат от клиентов
                for (var item : arrSocket)
                {
                    DataInputStream ois = new DataInputStream(item.getInputStream());
                    
                    while (ois.available() == 0)
                        Thread.sleep(1);
                    
                    res += Double.parseDouble(ois.readUTF());  
                    //ois.close();
                }
                
                start = 0; step = 0; finish = 0;
            }
            
            executeProcs.shutdown();
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

class MonoThreadClientHandler implements Runnable 
{

    private static Socket clientDialog;
    private static double result = 0;
    private static double step = 0;
    private static double start = 0;
    private static double finish = 0;

    public MonoThreadClientHandler(Socket client)
    {
        MonoThreadClientHandler.clientDialog = client;
    }
    
    public void SetValues(double step, double start, double finish)
    {
        MonoThreadClientHandler.step = step;
        MonoThreadClientHandler.start = start;
        MonoThreadClientHandler.finish = finish;
    }
    
    public double getRes() { return result; }

    @Override
    public void run() {

        try (DataOutputStream out = new DataOutputStream(clientDialog.getOutputStream());
            DataInputStream in = new DataInputStream(clientDialog.getInputStream());)
        {
            // Инициируем каналы общения в сокете, для сервера

            // Начинаем диалог с подключенным клиентом
            if (!clientDialog.isClosed()) 
            {
                // Ждем получения табличных данных 
                while (step == 0 && start == 0 && finish == 0)
                    Thread.sleep(1);
                
                // Передаем данные клинету для вычисления
                out.writeUTF(Double.toString(step) + " " +
                             Double.toString(start) + " " +
                             Double.toString(finish));
                out.flush();
                
                step = 0; start = 0; finish = 0; 
                
                // Ждем результат вычислений 
                while (in.available() == 0)
                    Thread.sleep(1);
                
                // Считываем результат
                result = Double.parseDouble(in.readUTF());
            }

            System.out.println("ClientMini disconnected");
            
            // Закрываем сокет общения с клиентом в нити моносервера
            clientDialog.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
        } 
    }
    
}