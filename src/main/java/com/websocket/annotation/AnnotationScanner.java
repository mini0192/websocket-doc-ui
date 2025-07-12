package com.websocket.annotation;

import com.websocket.exception.AnnotationScanException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

public class AnnotationScanner {
    private final String packageName;

    public AnnotationScanner(String packageName) {
        if (!StringUtils.hasText(packageName)) {
            throw new AnnotationScanException("Scan package name must be set.");
        }
        this.packageName = packageName;
    }

    public Set<Class<?>> findAnnotatedClasses(Class<? extends Annotation> annotation) {

        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(annotation));

        Set<Class<?>> classes = new HashSet<>();
        for (BeanDefinition bd : scanner.findCandidateComponents(this.packageName)) {
            try {
                classes.add(Class.forName(bd.getBeanClassName()));
            } catch (ClassNotFoundException e) {
                throw new AnnotationScanException("Failed to load class: " + bd.getBeanClassName(), e);
            }
        }
        return classes;
    }
}
