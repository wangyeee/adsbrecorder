package adsbrecorder.common.aop;

import static java.util.Objects.requireNonNull;

import java.math.BigInteger;
import java.util.Map;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

import adsbrecorder.aop.exception.ArgumentResolverException;
import adsbrecorder.aop.exception.EntityNotFoundException;
import adsbrecorder.common.aop.annotation.PathEntity;
import adsbrecorder.common.utils.AutoResolvableEntity;

public class PathEntityArgumentResolver implements HandlerMethodArgumentResolver {

    private Map<Class<?>, Function<? super Number, ? extends AutoResolvableEntity>> idEntityResolverMap;
    private Map<Class<?>, Function<String, ? extends Number>> idDataConversionMap;

    public PathEntityArgumentResolver(Map<Class<?>, Function<? super Number, ? extends AutoResolvableEntity>> idEntityResolverMap) {
        this.idEntityResolverMap = requireNonNull(idEntityResolverMap);
        this.idDataConversionMap = Map.of(
            Long.class, Long::valueOf,
            BigInteger.class, str -> new BigInteger(str)
        );
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        PathEntity pe = parameter.getParameterAnnotation(PathEntity.class);
        if (pe == null)
            return false;
        if (pe.name() == null || pe.name().length() == 0)
            return false;
        return this.idEntityResolverMap.containsKey(parameter.getParameterType())
            && this.idDataConversionMap.containsKey(pe.idDataType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        PathEntity pe = parameter.getParameterAnnotation(PathEntity.class);
        try {
            HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
            @SuppressWarnings("unchecked")
            Map<String, String> pathVariables = (Map<String, String>) httpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            if (pathVariables.containsKey(pe.name())) {
                String paramValue = pathVariables.get(pe.name());
                Function<? super Number, ? extends AutoResolvableEntity> func = this.idEntityResolverMap.get(parameter.getParameterType());
                Number id = this.idDataConversionMap.get(pe.idDataType()).apply(paramValue);
                AutoResolvableEntity entity = func.apply(id);
                if (entity == null || entity.isValidEntity() == false)
                    throw new EntityNotFoundException(String.format(
                            "Could not found object given %s = %s", pe.name(), pathVariables.get(pe.name())));
                return entity;
            }
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ArgumentResolverException(String.format("Failed to resolve parameter %s", pe.name()), e);
        }
        // bad request
        throw new ArgumentResolverException(String.format("Failed to resolve parameter %s", pe.name()));
    }
}
