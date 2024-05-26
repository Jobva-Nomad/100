package com.example;

import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите выражение: ");
        String expression = scanner.nextLine();

        String classContent = String.format("""
            package org.example;
            public class DynamicEval {
                public Object getResult() {
                    return %s;
                }
            }
        """, expression);

        String filePath = "./target/classes/org/example/DynamicEval.java";
        File sourceFile = new File(filePath);
        sourceFile.getParentFile().mkdirs();

        try (FileOutputStream fos = new FileOutputStream(sourceFile)) {
            fos.write(classContent.getBytes());
        } catch (IOException e) {
            System.out.println("Ошибка при записи исходного кода: " + e.getMessage());
            return;
        }

        int compilationResult = com.sun.tools.javac.Main.compile(new String[]{filePath});
        if (compilationResult != 0) {
            System.out.println("Ошибка при компиляции кода.");
            return;
        }

        try {
            Class<?> dynamicClass = Class.forName("org.example.DynamicEval");
            Object instance = dynamicClass.getDeclaredConstructor().newInstance();
            Method getResultMethod = dynamicClass.getMethod("getResult");
            Object result = getResultMethod.invoke(instance);
            System.out.println("Результат выражения: " + result);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException | InvocationTargetException e) {
            System.out.println("Ошибка при загрузке или выполнении класса: " + e.getMessage());
        }
    }
}
