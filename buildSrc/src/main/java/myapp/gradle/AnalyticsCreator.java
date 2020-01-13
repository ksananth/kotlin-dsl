package myapp.gradle;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Modifier;


class AnalyticsCreator {

    public static void main(String[] args) throws Exception {
        getJson("/Users/anand/Documents/android/MyApplication5/buildSrc/src/main/resources/latest/report_1.0.json");
    }

    void createFile() throws IOException {
        TypeName wildcard = WildcardTypeName.subtypeOf(Object.class);

        TypeName classOfAny = ParameterizedTypeName.get(ClassName.get(Class.class), wildcard);

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

    void create() throws IOException {

        MethodSpec main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();


        TypeSpec fieldImpl = TypeSpec.classBuilder("NewFile")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(main)
                .build();

        JavaFile javaFile = JavaFile.builder("com.dsl.myapplication", fieldImpl)
                .build();


        javaFile.writeTo(System.out);
        PrintStream stream = new PrintStream("./app/src/main/java/com/dsl/myapplication/" + "NewFile.java", "UTF-8");
        javaFile.writeTo(stream);
    }


    static void getJson(String filename) throws Exception {
        JSONObject jsonObject = (JSONObject) readJson(filename);
        System.out.println(jsonObject);
        jsonObject.keySet().forEach(keyStr ->
        {
            Object keyvalue = jsonObject.get(keyStr);
            System.out.println("key: " + keyStr);
            JSONObject valueObj = (JSONObject) keyvalue;

            valueObj.keySet().forEach(valueStr ->
            {
                Object value = valueObj.get(valueStr);
                System.out.println(valueStr + " : " + value);

            });
        });
    }

    private static Object readJson(String filename) throws Exception {
        FileReader reader = new FileReader(filename);
        JSONParser jsonParser = new JSONParser();
        return jsonParser.parse(reader);
    }
}
