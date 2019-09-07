package adsbrecorder.common.aop.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aspectj.lang.reflect.MethodSignature;

public interface AnnotationUtils {

    default <T extends Annotation> T filterAnnotationByType(Annotation[] annotations, Class<T> clazz) {
        for (Annotation annotation : annotations) {
            if (clazz.isAssignableFrom(annotation.getClass())) {
                @SuppressWarnings("unchecked")
                T result = (T) annotation;
                return result;
            }
        }
        return null;
    }

    default <T extends Annotation> T searchFirst(MethodSignature signature, Class<T> clazz) {
        return searchFirst(signature, clazz, null);
    }

    default <T extends Annotation> T searchFirst(MethodSignature signature, Class<T> clazz, int[] index) {
        Method method = signature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            T paramAnnotation = filterAnnotationByType(annotations, clazz);
            if (paramAnnotation != null) {
                if (index != null && index.length > 0) {
                    index[0] = i;
                }
                return paramAnnotation;
            }
        }
        return null;
    }
}
