package com.emamagic.processor;

import com.emamagic.annotation.Page;
import com.emamagic.annotation.Param;
import com.emamagic.util.PageData;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

@SupportedAnnotationTypes("com.emamagic.annotation.Page")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class NavigationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<String, PageData> pageParamMap = new HashMap<>();

        for (Element element : roundEnv.getElementsAnnotatedWith(Page.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                String pageName = typeElement.getSimpleName().toString();
                String qualifiedName = typeElement.getQualifiedName().toString();

                List<VariableElement> params = new ArrayList<>();
                for (Element enclosedElement : typeElement.getEnclosedElements()) {
                    if (enclosedElement.getKind() == ElementKind.FIELD && enclosedElement.getAnnotation(Param.class) != null) {
                        params.add((VariableElement) enclosedElement);
                    }
                }

                pageParamMap.put(pageName, new PageData(qualifiedName, params));
            }
        }

        generateNavigatorClass(pageParamMap);
        return true;
    }


    private void generateNavigatorClass(Map<String, PageData> pageParamMap) {
        TypeSpec.Builder navigatorClass = TypeSpec.classBuilder("Navigator")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .addStatement("throw new $T($S)", RuntimeException.class, "You cannot create an instance of Navigator")
                        .build());

        for (Map.Entry<String, PageData> entry : pageParamMap.entrySet()) {
            String pageName = entry.getKey();
            PageData pageData = entry.getValue();

            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("navTo" + capitalize(pageName))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

            methodBuilder.addStatement("$T page = new $T()",
                    ClassName.bestGuess(pageData.qualifiedName()),
                    ClassName.bestGuess(pageData.qualifiedName()));

            for (VariableElement param : pageData.params()) {
                methodBuilder.addParameter(TypeName.get(param.asType()), param.getSimpleName().toString());

                methodBuilder.addStatement("try { $T field = page.getClass().getDeclaredField($S); field.setAccessible(true); field.set(page, $L); } catch (Exception e) { throw new RuntimeException(e); }",
                        Field.class, param.getSimpleName().toString(), param.getSimpleName().toString());
            }

            methodBuilder.addStatement("page.display()");
            navigatorClass.addMethod(methodBuilder.build());
        }

        JavaFile javaFile = JavaFile.builder("com.emamagic.navigator", navigatorClass.build())
                .build();

        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException ignored) {
            // Handle exception if necessary
        }
    }



    private String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}