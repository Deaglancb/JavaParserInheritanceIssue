import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;
import com.github.javaparser.resolution.declarations.ResolvedClassDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.SourceRoot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Main {


    public static void main(String[] args) throws IOException {

        String path = "/home/deaglan/Workspace/ExtendsPotentialBug/src/main/resources/sample_src/";

        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        combinedTypeSolver.add(new JavaParserTypeSolver(new File(path)));

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
        StaticJavaParser.getConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_11);

        StaticJavaParser.getConfiguration().setLexicalPreservationEnabled(true);



        SourceRoot sourceRoot = new SourceRoot(Path.of(path));


        sourceRoot.setParserConfiguration(StaticJavaParser.getConfiguration());

        sourceRoot.tryToParse();

        for(CompilationUnit cu : sourceRoot.getCompilationUnits()) {
            LexicalPreservingPrinter.setup(cu);
        }

        ResolvedClassDeclaration classExample = sourceRoot.getCompilationUnits().get(0).getType(0).resolve().asClass();
        ResolvedClassDeclaration otherClass = sourceRoot.getCompilationUnits().get(1).getType(0).resolve().asClass();


        System.out.println(classExample);
        ((ClassOrInterfaceDeclaration)classExample.toAst().get()).addExtendedType(otherClass.getName());
        System.out.println(classExample);



    }
}
