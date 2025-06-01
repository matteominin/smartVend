package com.smartvend.app;

import com.smartvend.app.db.DatabaseInitializer;
import java.util.Scanner;

import javax.swing.text.html.parser.Entity;
import jakarta.persistence.EntityManagerFactory;

import com.smartvend.app.controllers.WorkerController;
import com.smartvend.app.dao.impl.TaskDaoImpl;
import com.smartvend.app.dao.impl.UserDaoImpl;
import com.smartvend.app.dao.impl.WorkerDaoImpl;
import com.smartvend.app.model.user.*;
import com.smartvend.app.services.TaskService;
import com.smartvend.app.services.UserService;
import com.smartvend.app.services.WorkerService;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("SmartVend application started successfully!");
            // Initialize the database
            EntityManagerFactory emf = DatabaseInitializer.getEntityManagerFactory();

            // Initialize controllers
            UserService userService = new UserService(new UserDaoImpl());

            
            }

            scanner.close();
        }catch(

    Exception e)
    {
        System.err.println("Application failed to start: " + e.getMessage());
        e.printStackTrace();
    }finally
    {
        DatabaseInitializer.shutdown();
        System.out.println("Main application logic finished.");
    }
}}