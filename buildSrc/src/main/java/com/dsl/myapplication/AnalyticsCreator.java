package com.dsl.myapplication;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import javax.lang.model.element.Modifier;


class AnalyticsCreator {

    private static final String ANALYTICS_JSON = "/Users/anand/Documents/android/MyApplication5/buildSrc/src/main/resources/latest/report_1.0.json";
    private final String CLASS_FILE_NAME = "AnalyticsInstrumentation";
    private final String INTERFACE_FILE_NAME = "AnalyticsApi";
    private final String PATH = "./app/src/main/java/com/dsl/myapplication/";

    public static void main(String[] args) throws Exception {
        getJson(ANALYTICS_JSON);
    }

    void createAnalyticsClass() throws Exception {

        FieldSpec paramA = FieldSpec
                .builder(AnalyticsClient.class, "analyticsClient", Modifier.PRIVATE)
                .build();

        FieldSpec paramB = FieldSpec
                .builder(String.class, "language", Modifier.PRIVATE)
                .build();

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(AnalyticsClient.class, "analyticsClient")
                .addParameter(String.class, "language")
                .addStatement("this.analyticsClient = analyticsClient")
                .addStatement("this.language = language")
                .build();

        //Create Class
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(CLASS_FILE_NAME);
        classBuilder.addSuperinterface(AnalyticsApi.class);
        classBuilder.addModifiers(Modifier.PUBLIC);
        classBuilder.addField(paramA);
        classBuilder.addField(paramB);
        classBuilder.addMethod(constructor);





        JSONObject analyticsJsonObj = (JSONObject) readJson(ANALYTICS_JSON);
        System.out.println(analyticsJsonObj);
        analyticsJsonObj.keySet().forEach(methodName ->
        {
            Object keyvalue = analyticsJsonObj.get(methodName);
            System.out.println("key: " + methodName); //createAnalyticsClass method
            JSONObject hashMapValues = (JSONObject) keyvalue;

            //Create Method
            MethodSpec methodSpec = createMethod(""+methodName, hashMapValues);
            classBuilder.addMethod(methodSpec);

        });


        TypeSpec classImpl = classBuilder.build();

        JavaFile javaFile = JavaFile.builder("com.dsl.myapplication", classImpl)
                .build();


        javaFile.writeTo(System.out);
        PrintStream stream = new PrintStream(PATH + CLASS_FILE_NAME + ".java", "UTF-8");
        javaFile.writeTo(stream);
    }

    @NotNull
    private MethodSpec createMethod(String methodName, JSONObject hashMapValues) {
        TypeName hashMap = ClassName.get(HashMap.class);
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(getProperMethodName(methodName));
        methodBuilder.addModifiers(Modifier.PUBLIC);
        methodBuilder.returns(void.class);
        methodBuilder.addStatement("$T<String , String> hashMap = new HashMap<>()",hashMap);

        hashMapValues.keySet().forEach(valueStr ->
        {
            String key = String.valueOf(valueStr).toLowerCase();
            String value = String.valueOf(hashMapValues.get(valueStr));
            System.out.println(valueStr + " : " + value); // createAnalyticsClass hashmap
            if (value.contains("<") && value.contains(">")) {
                methodBuilder.addParameter(String.class, key); //dynamic
                methodBuilder.addStatement("hashMap.put(\"" + valueStr + "\","+ key+")"); //dynamic

            } else {
                methodBuilder.addStatement("hashMap.put(\"" + valueStr + "\", \"" + value + "\")"); //dynamic
            }
        });
        methodBuilder.addStatement("analyticsClient.trackPage(\"details\", hashMap)");
        return methodBuilder.build();
    }

    private MethodSpec createMethodForInterface(String methodName, JSONObject hashMapValues) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(getProperMethodName(methodName));
        methodBuilder.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        methodBuilder.returns(void.class);
        hashMapValues.keySet().forEach(valueStr ->
        {
            String key = String.valueOf(valueStr).toLowerCase();
            String value = String.valueOf(hashMapValues.get(valueStr));
            System.out.println(valueStr + " : " + value); // createAnalyticsClass hashmap
            if (value.contains("<") && value.contains(">")) {
                methodBuilder.addParameter(String.class, key); //dynamic
            }
        });
        return methodBuilder.build();
    }

    private String getProperMethodName(String methodName) {
        //String properMethodName = methodName.replaceAll(" ","_").replaceAll(":", "_");
        StringBuilder properMethodName = new StringBuilder();
        String[] splitedBySpace = methodName.split("\\s+");
        for (String str: splitedBySpace) {
            properMethodName.append(str.substring(0, 1).toUpperCase()).append(str.substring(1));
        }

        String[] splitedByUnderscore = properMethodName.toString().split("_");
        properMethodName = new StringBuilder();
        for (String str: splitedByUnderscore) {
            properMethodName.append(str.substring(0, 1).toUpperCase()).append(str.substring(1));
        }

        String removedUnderscore = properMethodName.toString().replaceAll(":", "_");
        System.out.println("removedUnderscore"+removedUnderscore);

        String finalName = capitalize(removedUnderscore);
        System.out.println("removedUnderscore"+finalName);

        return finalName;
    }


    private String capitalize(String str) {
        if(str== null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    private static Object readJson(String filename) throws Exception {
        FileReader reader = new FileReader(filename);
        JSONParser jsonParser = new JSONParser();
        return jsonParser.parse(reader);
    }

    void createAnalyticsInterface() throws Exception {

        TypeSpec.Builder interfaceBuilder = TypeSpec.interfaceBuilder(INTERFACE_FILE_NAME);
        interfaceBuilder.addModifiers(Modifier.PUBLIC);


        JSONObject analyticsJsonObj = (JSONObject) readJson(ANALYTICS_JSON);
        analyticsJsonObj.keySet().forEach(methodName ->
        {
            Object keyvalue = analyticsJsonObj.get(methodName);
            System.out.println("key: " + methodName); //createAnalyticsClass method
            JSONObject hashMapValues = (JSONObject) keyvalue;

            //Create Method
            MethodSpec methodSpec = createMethodForInterface("" + methodName, hashMapValues);
            interfaceBuilder.addMethod(methodSpec);

        });

        TypeSpec interfaceImpl = interfaceBuilder.build();

        JavaFile javaFile = JavaFile.builder("com.dsl.myapplication", interfaceImpl)
                .build();


        try {
            javaFile.writeTo(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintStream stream = new PrintStream(PATH + INTERFACE_FILE_NAME + ".java", "UTF-8");
        javaFile.writeTo(stream);
    }

    static void getJson(String filename) throws Exception {
        JSONObject jsonObject = (JSONObject) readJson(filename);
        System.out.println(jsonObject);
        jsonObject.keySet().forEach(keyStr ->
        {
            Object keyvalue = jsonObject.get(keyStr);
            System.out.println("key: " + keyStr); //createAnalyticsClass method
            JSONObject valueObj = (JSONObject) keyvalue;

            valueObj.keySet().forEach(valueStr ->
            {
                Object value = valueObj.get(valueStr);
                System.out.println(valueStr + " : " + value); // createAnalyticsClass hashmap

            });
        });
    }
}
