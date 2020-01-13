package com.dsl.myapplication.javapoet;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Modifier;

public class Example {
    public static void main(String[] args) throws IOException {

        TypeName wildcard = WildcardTypeName.subtypeOf(Object.class);

        TypeName classOfAny = ParameterizedTypeName.get(
                ClassName.get(Class.class), wildcard);

        TypeName string = ClassName.get(String.class);

        TypeName mapOfStringAndClassOfAny = ParameterizedTypeName.get(ClassName.get(Map.class), string, classOfAny);

        TypeName hashMapOfStringAndClassOfAny = ParameterizedTypeName.get(ClassName.get(HashMap.class), string, classOfAny);

        FieldSpec fieldSpec = FieldSpec.builder(mapOfStringAndClassOfAny, "ID_MAP")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .initializer("new $T()", hashMapOfStringAndClassOfAny)
                .build();

        TypeSpec fieldImpl = TypeSpec.classBuilder("NewFile")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(fieldSpec)
                .build();

        JavaFile javaFile = JavaFile.builder("com.dsl.myapplication", fieldImpl)
                .build();

        javaFile.writeTo(System.out);


        PrintStream stream = new PrintStream("./app/src/main/java/com/dsl/myapplication/" + "NewFile.java", "UTF-8");
        javaFile.writeTo(stream);
    }
}
