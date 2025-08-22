/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.threads;

/**
 *
 * @author hcadavid
 */
public class CountThreadsMain {
    
    public static void main(String a[]){


        CountThread thread1 = new CountThread(0,99);
        thread1.run();
        System.out.print(thread1+ " ");

        CountThread thread2 = new CountThread(99,199);
        thread2.run();
        System.out.print(thread2 + " ");
        CountThread thread3 = new CountThread(200,299);
        thread3.run();
        System.out.print(thread3 + " ");        
    }
    
}


