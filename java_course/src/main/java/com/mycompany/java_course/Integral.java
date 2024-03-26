/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.java_course;

/**
 *
 * @author Василий
 */
public class Integral
{

    // Поиск определенного интеграла с f(x) = tg(x) по формуле Трапеции
    public static double TaskForFindIntegral(double startPoint, double finishPoint, double step) throws MyException
    {
        double result = 0;
        double sum = 0, numForLoop = 0;
        
        // Определение начала интегрирования
        if (startPoint < finishPoint)
        {
            sum = startPoint;
            numForLoop = finishPoint;
        }
        else
        {
            sum = finishPoint;
            numForLoop = startPoint;
        }
        
        if (numForLoop - sum < step)
            throw new MyException("Step over the range");
        
        // Расчет площадей трапейций, входящих в промежуток интегрирования
        while (sum < numForLoop)
        {
            result += (Math.tan(sum) + Math.tan(sum + step)) * (step / (double)2);
            sum += step;
        }
        
        // Изменение шага, если шаг вышел за промежуток интегрирования
        if (sum != numForLoop)
        {
            sum -= step;
            step = numForLoop - sum;
            result += (Math.tan(sum) + Math.tan(sum + step)) * (step / (double)2);
        }
        
        return result;
    }
}
