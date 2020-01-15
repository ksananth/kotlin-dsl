package com.dsl.myapplication;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import javax.lang.model.element.Modifier;


class AnalyticsCreator {

    private final String CLASS_FILE_NAME = "AnalyticsInstrumentation";
    private final String INTERFACE_FILE_NAME = "AnalyticsApi";
    private final String PATH = "./app/src/main/java/com/dsl/myapplication/";

    public static void main(String[] args) throws Exception {
        getJson("/Users/anand/Documents/android/MyApplication5/buildSrc/src/main/resources/latest/report_1.0.json");
    }

    void createAnalyticsClass() throws IOException {
        TypeName hashMap = ClassName.get(HashMap.class);

        FieldSpec paramA = FieldSpec
                .builder(AnalyticsClient.class, "analyticsClient", Modifier.PRIVATE)
                .build();

        FieldSpec paramB = FieldSpec
                .builder(String.class, "language", Modifier.PRIVATE)
                .build();


        MethodSpec main = MethodSpec.methodBuilder("category")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(String.class, "args")
                .addStatement("$T<String , String> hashMap = new HashMap<>()",hashMap)
                .addStatement("hashMap.put(\"page.category\", \"fb\")")
                .addStatement("analyticsClient.trackPage(\"details\", hashMap)")
                .build();

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(AnalyticsClient.class, "analyticsClient")
                .addParameter(String.class, "language")
                .addStatement("this.analyticsClient = analyticsClient")
                .addStatement("this.language = language")
                .build();


        TypeSpec fieldImpl = TypeSpec.classBuilder(CLASS_FILE_NAME)
                .addSuperinterface(AnalyticsApi.class)
                .addModifiers(Modifier.PUBLIC)
                .addField(paramA)
                .addField(paramB)
                .addMethod(constructor)
                .addMethod(main)
                .build();

        JavaFile javaFile = JavaFile.builder("com.dsl.myapplication", fieldImpl)
                .build();


        javaFile.writeTo(System.out);
        PrintStream stream = new PrintStream(PATH + CLASS_FILE_NAME + ".java", "UTF-8");
        javaFile.writeTo(stream);
    }

    private static Object readJson(String filename) throws Exception {
        FileReader reader = new FileReader(filename);
        JSONParser jsonParser = new JSONParser();
        return jsonParser.parse(reader);
    }

    void createAnalyticsInterface() throws IOException {

        TypeSpec fieldImpl = TypeSpec.interfaceBuilder(INTERFACE_FILE_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(MethodSpec.methodBuilder("category")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .addParameter(String.class, "args")
                        .build())
                .build();

        JavaFile javaFile = JavaFile.builder("com.dsl.myapplication", fieldImpl)
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
