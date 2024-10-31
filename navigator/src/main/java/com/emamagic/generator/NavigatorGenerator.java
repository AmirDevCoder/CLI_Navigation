//package com.emamagic.generator;
//
//import com.emamagic.Navigable;
//import com.emamagic.annotation.Page;
//import com.emamagic.annotation.Param;
//import com.squareup.javapoet.*;
//import javax.annotation.processing.Filer;
//import javax.lang.model.element.Modifier;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
//public class NavigatorGenerator {
//
//    public static void generateNavigator(Filer filer, Set<Class<? extends Navigable>> pageClasses) throws IOException {
//        // Define the pages map
//        FieldSpec pagesField = FieldSpec.builder(ParameterizedTypeName.get(Map.class, String.class, Navigable.class),
//                        "pages", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
//                .initializer("new $T<>()", HashMap.class)
//                .build();
//
//        // Define the static initializer for registering pages
//        CodeBlock.Builder staticInitializer = CodeBlock.builder();
//
//        // Create the Navigator class builder
//        TypeSpec.Builder navigatorBuilder = TypeSpec.classBuilder("Navigator")
//                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
//                .addField(pagesField);
//
//        for (Class<? extends Navigable> pageClass : pageClasses) {
//            String pageName = pageClass.getAnnotation(Page.class).name();
//            staticInitializer.addStatement("pages.put($S, new $T())", pageName, pageClass);
//
//            // Generate a specific navigation method for each page
//            MethodSpec.Builder specificNavMethod = MethodSpec.methodBuilder("navTo" + capitalize(pageName))
//                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//                    .returns(void.class);
//
//            // Add parameters based on @Param annotations
//            var fields = pageClass.getDeclaredFields();
//            for (var field : fields) {
//                if (field.isAnnotationPresent(Param.class)) {
//                    specificNavMethod.addParameter(field.getType(), field.getName());
//                    specificNavMethod.addStatement("Navigable page = pages.get($S)", pageName);
//                    specificNavMethod.addStatement("((AdminPage) page).$L = $L", field.getName(), field.getName());
//                }
//            }
//
//            // Add the method to the Navigator class
//            specificNavMethod.addStatement("Navigable page = pages.get($S)", pageName);
//            specificNavMethod.addStatement("page.display()");
//            navigatorBuilder.addMethod(specificNavMethod.build());
//        }
//
//        // Add the static initializer block
//        navigatorBuilder.addStaticBlock(staticInitializer.build());
//
//        // Build the Navigator class
//        TypeSpec navigatorClass = navigatorBuilder.build();
//
//        // Create and write the Java file
//        JavaFile javaFile = JavaFile.builder("navigation", navigatorClass)
//                .build();
//
//        javaFile.writeTo(filer);
//    }
//
//    private static String capitalize(String str) {
//        if (str == null || str.isEmpty()) {
//            return str;
//        }
//        return str.substring(0, 1).toUpperCase() + str.substring(1);
//    }
//}
