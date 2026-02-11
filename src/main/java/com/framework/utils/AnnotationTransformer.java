package com.framework.utils;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * AnnotationTransformer: Dynamically sets the RetryAnalyzer for all tests.
 * This eliminates the need to manually add the retry parameter to every @Test annotation.
 */
public class AnnotationTransformer implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass,
                          Constructor testConstructor, Method testMethod) {

        // Globally attach the RetryAnalyzer to every test case
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
}
