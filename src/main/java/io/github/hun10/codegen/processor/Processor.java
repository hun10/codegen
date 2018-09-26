package io.github.hun10.codegen.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("io.github.hun10.codegen.annotations.Processor")
public class Processor extends AbstractProcessor {
    private final Set<Name> processors = new HashSet<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            try (PrintWriter pw = new PrintWriter(spiFile().openWriter())) {
                processors.forEach(pw::println);
            } catch (IOException e) {
                processingEnv.getMessager()
                        .printMessage(Diagnostic.Kind.ERROR, e.getLocalizedMessage());
            }
        } else {
            annotations.stream()
                    .map(roundEnv::getElementsAnnotatedWith)
                    .flatMap(Collection::stream)
                    .map(TypeElement.class::cast)
                    .map(TypeElement::getQualifiedName)
                    .forEach(processors::add);
        }

        return true;
    }

    private FileObject spiFile() throws IOException {
        return processingEnv.getFiler().createResource(
                StandardLocation.CLASS_OUTPUT,
                "",
                "META-INF/services/javax.annotation.processing.Processor"
        );
    }
}
