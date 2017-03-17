package com.pulin.spring;
        import org.springframework.beans.BeanUtils;
        import org.springframework.beans.factory.config.BeanExpressionContext;
        import org.springframework.beans.factory.config.BeanExpressionResolver;
        import org.springframework.beans.factory.config.ConfigurableBeanFactory;
        import org.springframework.core.GenericCollectionTypeResolver;
        import org.springframework.core.MethodParameter;
        import org.springframework.core.annotation.AnnotationUtils;
        import org.springframework.core.convert.ConversionService;
        import org.springframework.core.convert.TypeDescriptor;
        import org.springframework.util.Assert;
        import org.springframework.util.LinkedMultiValueMap;
        import org.springframework.util.MultiValueMap;
        import org.springframework.util.StringUtils;
        import org.springframework.validation.BindException;
        import org.springframework.validation.Errors;
        import org.springframework.validation.annotation.Validated;
        import org.springframework.web.bind.MissingServletRequestParameterException;
        import org.springframework.web.bind.WebDataBinder;
        import org.springframework.web.bind.annotation.RequestPart;
        import org.springframework.web.bind.annotation.ValueConstants;
        import org.springframework.web.bind.support.WebDataBinderFactory;
        import org.springframework.web.context.request.NativeWebRequest;
        import org.springframework.web.context.request.RequestScope;
        import org.springframework.web.method.annotation.ModelFactory;
        import org.springframework.web.method.support.HandlerMethodArgumentResolver;
        import org.springframework.web.method.support.ModelAndViewContainer;
        import org.springframework.web.multipart.MultipartException;
        import org.springframework.web.multipart.MultipartFile;
        import org.springframework.web.multipart.MultipartHttpServletRequest;
        import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;
        import org.springframework.web.util.UriComponentsBuilder;
        import org.springframework.web.util.WebUtils;

        import javax.servlet.ServletException;
        import javax.servlet.http.HttpServletRequest;
        import javax.servlet.http.Part;
        import java.io.BufferedReader;
        import java.lang.annotation.Annotation;
        import java.util.*;
        import java.util.concurrent.ConcurrentHashMap;

public class CustomMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private static String requestBodyCache = "";
    private static final TypeDescriptor STRING_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(String.class);
    private final boolean annotationNotRequired;
    private final ConfigurableBeanFactory configurableBeanFactory;

    private final BeanExpressionContext expressionContext;
    private final boolean useDefaultResolution;

    private Map<MethodParameter, NamedValueInfo> namedValueInfoCache = new ConcurrentHashMap<MethodParameter, NamedValueInfo>(256);


    public CustomMethodArgumentResolver(ConfigurableBeanFactory beanFactory) {
        this.configurableBeanFactory = beanFactory;
        this.expressionContext = (beanFactory != null ? new BeanExpressionContext(beanFactory, new RequestScope()) : null);
        this.annotationNotRequired=true;
        this.useDefaultResolution = false;
    }

    public CustomMethodArgumentResolver() {
        this.annotationNotRequired=true;
        this.useDefaultResolution = false;
        this.configurableBeanFactory = null;
        this.expressionContext = null;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType();
        if (parameter.hasParameterAnnotation(RequestCustomParam.class)) {
            return true;
        }  else {
            if (parameter.hasParameterAnnotation(RequestPart.class)) {
                return false;
            }
            else if (MultipartFile.class.equals(paramType) || "javax.servlet.http.Part".equals(paramType.getName())) {
                return true;
            } else
                return this.useDefaultResolution && BeanUtils.isSimpleProperty(paramType);
        }

    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);

        String content=servletRequest.getContentType();
        //如果请求类型是application/json ，则调用fastjson 去解析
        if (servletRequest.getContentType().contains("application/json")) {
            return bindRequestParameters( parameter, servletRequest);
        } else {
            Class<?> parameterType = parameter.getParameterType();
            if (BeanUtils.isSimpleProperty(parameterType)) {
                //如果是Map对象，调用resolveMapArgument方法
                if (Map.class.isAssignableFrom(parameter.getParameterType())) {
                    return resolveMapArgument(parameter,mavContainer,webRequest,binderFactory);
                }
                Class<?> paramType = parameter.getParameterType();
                NamedValueInfo namedValueInfo = getNamedValueInfo(parameter);

                Object arg = resolveName(namedValueInfo.name, parameter, webRequest);
                if (arg == null) {
                    if (namedValueInfo.defaultValue != null) {
                        arg = resolveDefaultValue(namedValueInfo.defaultValue);
                    }
                    else if (namedValueInfo.required && !parameter.getParameterType().getName().equals("java.util.Optional")) {
                        handleMissingValue(namedValueInfo.name, parameter);
                    }
                    arg = handleNullValue(namedValueInfo.name, arg, paramType);
                }
                else if ("".equals(arg) && namedValueInfo.defaultValue != null) {
                    arg = resolveDefaultValue(namedValueInfo.defaultValue);
                }

                if (binderFactory != null) {
                    WebDataBinder binder = binderFactory.createBinder(webRequest, null, namedValueInfo.name);
                    arg = binder.convertIfNecessary(arg, paramType, parameter);
                }

                handleResolvedValue(arg, namedValueInfo.name, parameter, mavContainer, webRequest);

                return arg;
            } else {
                String name = ModelFactory.getNameForParameter(parameter);
                Object attribute = (mavContainer.containsAttribute(name) ?
                        mavContainer.getModel().get(name) : createAttribute(name, parameter, binderFactory, webRequest));

                WebDataBinder binder = binderFactory.createBinder(webRequest, attribute, name);
                if (binder.getTarget() != null) {
                    bindRequestParameters(binder, webRequest);
                    validateIfApplicable(binder, parameter);
                    if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) {
                        throw new BindException(binder.getBindingResult());
                    }
                }

                // Add resolved attribute and BindingResult at the end of the model
                Map<String, Object> bindingResultModel = binder.getBindingResult().getModel();
                mavContainer.removeAttributes(bindingResultModel);
                mavContainer.addAllAttributes(bindingResultModel);

                return binder.convertIfNecessary(binder.getTarget(), parameter.getParameterType(), parameter);
            }
        }
    }
    public Object resolveMapArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                     NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        Class<?> paramType = parameter.getParameterType();

        Map<String, String[]> parameterMap = webRequest.getParameterMap();
        if (MultiValueMap.class.isAssignableFrom(paramType)) {
            MultiValueMap<String, String> result = new LinkedMultiValueMap<String, String>(parameterMap.size());
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                for (String value : entry.getValue()) {
                    result.add(entry.getKey(), value);
                }
            }
            return result;
        }
        else {
            Map<String, String> result = new LinkedHashMap<String, String>(parameterMap.size());
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                if (entry.getValue().length > 0) {
                    result.put(entry.getKey(), entry.getValue()[0]);
                }
            }
            return result;
        }
    }
    /**
     * Obtain the named value for the given method parameter.
     */
    private NamedValueInfo getNamedValueInfo(MethodParameter parameter) {
        NamedValueInfo namedValueInfo = this.namedValueInfoCache.get(parameter);
        if (namedValueInfo == null) {
            namedValueInfo = createNamedValueInfo(parameter);
            namedValueInfo = updateNamedValueInfo(parameter, namedValueInfo);
            this.namedValueInfoCache.put(parameter, namedValueInfo);
        }
        return namedValueInfo;
    }
    protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        RequestCustomParam ann = parameter.getParameterAnnotation(RequestCustomParam.class);
        return (ann != null ? new RequestCustomParamNamedValueInfo(ann) : new RequestCustomParamNamedValueInfo());
    }

    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest webRequest) throws Exception {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        MultipartHttpServletRequest multipartRequest =
                WebUtils.getNativeRequest(servletRequest, MultipartHttpServletRequest.class);
        Object arg;

        if (MultipartFile.class.equals(parameter.getParameterType())) {
            assertIsMultipartRequest(servletRequest);
            Assert.notNull(multipartRequest, "Expected MultipartHttpServletRequest: is a MultipartResolver configured?");
            arg = multipartRequest.getFile(name);
        }
        else if (isMultipartFileCollection(parameter)) {
            assertIsMultipartRequest(servletRequest);
            Assert.notNull(multipartRequest, "Expected MultipartHttpServletRequest: is a MultipartResolver configured?");
            arg = multipartRequest.getFiles(name);
        }
        else if (isMultipartFileArray(parameter)) {
            assertIsMultipartRequest(servletRequest);
            Assert.notNull(multipartRequest, "Expected MultipartHttpServletRequest: is a MultipartResolver configured?");
            List<MultipartFile> multipartFiles = multipartRequest.getFiles(name);
            arg = multipartFiles.toArray(new MultipartFile[multipartFiles.size()]);
        }
        else if ("javax.servlet.http.Part".equals(parameter.getParameterType().getName())) {
            assertIsMultipartRequest(servletRequest);
            arg = servletRequest.getPart(name);
        }
        else if (isPartCollection(parameter)) {
            assertIsMultipartRequest(servletRequest);
            arg = new ArrayList<Object>(servletRequest.getParts());
        }
        else if (isPartArray(parameter)) {
            assertIsMultipartRequest(servletRequest);
            arg = RequestPartResolver.resolvePart(servletRequest);
        }
        else {
            arg = null;
            if (multipartRequest != null) {
                List<MultipartFile> files = multipartRequest.getFiles(name);
                if (!files.isEmpty()) {
                    arg = (files.size() == 1 ? files.get(0) : files);
                }
            }
            if (arg == null) {
                String[] paramValues = webRequest.getParameterValues(name);
                if (paramValues != null) {
                    arg = paramValues.length == 1 ? paramValues[0] : paramValues;
                }
            }
        }
        return arg;
    }

    private void assertIsMultipartRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("multipart/")) {
            throw new MultipartException("The current request is not a multipart request");
        }
    }

    private boolean isMultipartFileCollection(MethodParameter parameter) {
        Class<?> collectionType = getCollectionParameterType(parameter);
        return ((collectionType != null) && collectionType.equals(MultipartFile.class));
    }

    private boolean isPartCollection(MethodParameter parameter) {
        Class<?> collectionType = getCollectionParameterType(parameter);
        return ((collectionType != null) && "javax.servlet.http.Part".equals(collectionType.getName()));
    }

    private boolean isPartArray(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType().getComponentType();
        return ((paramType != null) && "javax.servlet.http.Part".equals(paramType.getName()));
    }

    private boolean isMultipartFileArray(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType().getComponentType();
        return ((paramType != null) && MultipartFile.class.equals(paramType));
    }

    private Class<?> getCollectionParameterType(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType();
        if (Collection.class.equals(paramType) || List.class.isAssignableFrom(paramType)){
            Class<?> valueType = GenericCollectionTypeResolver.getCollectionParameterType(parameter);
            if (valueType != null) {
                return valueType;
            }
        }
        return null;
    }

    protected void handleMissingValue(String name, MethodParameter parameter) throws ServletException {
        throw new MissingServletRequestParameterException(name, parameter.getParameterType().getSimpleName());
    }


    public void contributeMethodArgument(MethodParameter parameter, Object value,
                                         UriComponentsBuilder builder, Map<String, Object> uriVariables, ConversionService conversionService) {


        Class<?> paramType = parameter.getParameterType();
        if (Map.class.isAssignableFrom(paramType) || MultipartFile.class.equals(paramType) ||
                "javax.servlet.http.Part".equals(paramType.getName())) {
            return;
        }

        RequestCustomParam ann = parameter.getParameterAnnotation(RequestCustomParam.class);
        String name = (ann == null || !StringUtils.hasText(ann.value()) ? parameter.getParameterName() : ann.value());

        if (value == null) {
            builder.queryParam(name);
        }
        else if (value instanceof Collection) {
            for (Object element : (Collection<?>) value) {
                element = formatUriValue(conversionService, TypeDescriptor.nested(parameter, 1), element);
                builder.queryParam(name, element);
            }
        }
        else {
            builder.queryParam(name, formatUriValue(conversionService, new TypeDescriptor(parameter), value));
        }
    }

    protected String formatUriValue(ConversionService cs, TypeDescriptor sourceType, Object value) {
        if (value == null) {
            return null;
        }
        else if (value instanceof String) {
            return (String) value;
        }
        else if (cs != null) {
            return (String) cs.convert(value, sourceType, STRING_TYPE_DESCRIPTOR);
        }
        else {
            return value.toString();
        }
    }

    /**
     * Create a new NamedValueInfo based on the given NamedValueInfo with sanitized values.
     */
    private NamedValueInfo updateNamedValueInfo(MethodParameter parameter, NamedValueInfo info) {
        String name = info.name;
        if (info.name.length() == 0) {
            name = parameter.getParameterName();
            if (name == null) {
                throw new IllegalArgumentException("Name for argument type [" + parameter.getParameterType().getName() +
                        "] not available, and parameter name information not found in class file either.");
            }
        }
        String defaultValue = (ValueConstants.DEFAULT_NONE.equals(info.defaultValue) ? null : info.defaultValue);
        return new NamedValueInfo(name, info.required, defaultValue);
    }



    /**
     * Resolves the given default value into an argument value.
     */
    private Object resolveDefaultValue(String defaultValue) {
        if (this.configurableBeanFactory == null) {
            return defaultValue;
        }
        String placeholdersResolved = this.configurableBeanFactory.resolveEmbeddedValue(defaultValue);
        BeanExpressionResolver exprResolver = this.configurableBeanFactory.getBeanExpressionResolver();
        if (exprResolver == null) {
            return defaultValue;
        }
        return exprResolver.evaluate(placeholdersResolved, this.expressionContext);
    }



    /**
     * A {@code null} results in a {@code false} value for {@code boolean}s or an exception for other primitives.
     */
    private Object handleNullValue(String name, Object value, Class<?> paramType) {
        if (value == null) {
            if (Boolean.TYPE.equals(paramType)) {
                return Boolean.FALSE;
            }
            else if (paramType.isPrimitive()) {
                throw new IllegalStateException("Optional " + paramType + " parameter '" + name +
                        "' is present but cannot be translated into a null value due to being declared as a " +
                        "primitive type. Consider declaring it as object wrapper for the corresponding primitive type.");
            }
        }
        return value;
    }

    /**
     * Invoked after a value is resolved.
     * @param arg the resolved argument value
     * @param name the argument name
     * @param parameter the argument parameter type
     * @param mavContainer the {@link ModelAndViewContainer}, which may be {@code null}
     * @param webRequest the current request
     */
    protected void handleResolvedValue(Object arg, String name, MethodParameter parameter,
                                       ModelAndViewContainer mavContainer, NativeWebRequest webRequest) {
    }

    /**
     * Extension point to create the model attribute if not found in the model.
     * The default implementation uses the default constructor.
     *
     * @param attributeName the name of the attribute (never {@code null})
     * @param methodParam   the method parameter
     * @param binderFactory for creating WebDataBinder instance
     * @param request       the current request
     * @return the created model attribute (never {@code null})
     */
    protected Object createAttribute(String attributeName, MethodParameter methodParam,
                                     WebDataBinderFactory binderFactory, NativeWebRequest request) throws Exception {

        return BeanUtils.instantiateClass(methodParam.getParameterType());
    }

    /**
     * Extension point to bind the request to the target object.
     *
     * @param binder  the data binder instance to use for the binding
     * @param request the current request
     */
    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
        HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
        ((ExtendedServletRequestDataBinder) binder).bind(servletRequest);
    }

    /**
     * Validate the model attribute if applicable.
     * <p>The default implementation checks for {@code @javax.validation.Valid},
     * Spring's {@link org.springframework.validation.annotation.Validated},
     * and custom annotations whose name starts with "Valid".
     *
     * @param binder      the DataBinder to be used
     * @param methodParam the method parameter
     */
    protected void validateIfApplicable(WebDataBinder binder, MethodParameter methodParam) {
        Annotation[] annotations = methodParam.getParameterAnnotations();
        for (Annotation ann : annotations) {
            Validated validatedAnn = AnnotationUtils.getAnnotation(ann, Validated.class);
            if (validatedAnn != null || ann.annotationType().getSimpleName().startsWith("Valid")) {
                Object hints = (validatedAnn != null ? validatedAnn.value() : AnnotationUtils.getValue(ann));
                Object[] validationHints = (hints instanceof Object[] ? (Object[]) hints : new Object[]{hints});
                binder.validate(validationHints);
                break;
            }
        }
    }

    /**
     * Whether to raise a fatal bind exception on validation errors.
     *
     * @param binder      the data binder used to perform data binding
     * @param methodParam the method argument
     * @return {@code true} if the next method argument is not of type {@link Errors}
     */
    protected boolean isBindExceptionRequired(WebDataBinder binder, MethodParameter methodParam) {
        int i = methodParam.getParameterIndex();
        Class<?>[] paramTypes = methodParam.getMethod().getParameterTypes();
        boolean hasBindingResult = (paramTypes.length > (i + 1) && Errors.class.isAssignableFrom(paramTypes[i + 1]));
        return !hasBindingResult;
    }

    public boolean supportsReturnType(MethodParameter returnType) {
        if (returnType.getMethodAnnotation(RequestCustomParam.class) != null) {
            return true;
        } else
            return this.annotationNotRequired && !BeanUtils.isSimpleProperty(returnType.getParameterType());
    }

    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

        if (returnValue != null) {
            String name = ModelFactory.getNameForReturnValue(returnValue, returnType);
            mavContainer.addAttribute(name, returnValue);
        }
    }

    private Object bindRequestParameters( MethodParameter parameter, HttpServletRequest servletRequest) throws Exception {

        RequestCustomParam ann = parameter.getParameterAnnotation(RequestCustomParam.class);
        String name = (ann == null || !StringUtils.hasText(ann.value()) ? parameter.getParameterName() : ann.value());
        Object resultObj = null;
        // 把reqeust的body读取到StringBuilder
        BufferedReader reader = servletRequest.getReader();
        StringBuilder sb = new StringBuilder();
        char[] buf = new char[1024];
        int rd;
        while ((rd = reader.read(buf)) != -1) {
            sb.append(buf, 0, rd);
        }
        String requestBody = sb.toString();
        requestBody = requestBody.replaceAll("\r|\n|\r\n", "");

        if (StringUtils.hasText(requestBody)) {
            requestBodyCache = requestBody;
        } else {
            requestBody = requestBodyCache;
        }

        Class<?> parameterType = parameter.getParameterType();

        String parameterTypeName = parameterType.getName();

/*        if (BeanUtils.isSimpleProperty(parameterType)) {
            try {
                JSONObject json = JSONObject.parseObject(requestBody);
                resultObj = json.get(name);
                if (resultObj == null) {
                    Annotation annotaion = parameter.getParameterAnnotations()[0];
                    RequestCustomParam param = (RequestCustomParam) annotaion;
                    String defaultvalue = param.defaultValue();
                    defaultvalue = defaultvalue.replaceAll("\r|\n|\\s*", "");
                    resultObj=  ConvertUtils.convert(defaultvalue, parameterType);
                }
            } catch (Exception e) {
                //throw new UnauthenticatedException(ErrorCode.INVALID_FIELD_ERROR, SpringUtils.getMessage(servletRequest, SpringUtils.INVALID_FIELD_ERROR_CODE));
            }
        } else if (BatchDto.class.isAssignableFrom(parameterType)) {
            try {
                resultObj = JSONObject.parseObject(requestBody, parameterType);
            } catch (Exception e) {
                throw new UnauthenticatedException(ErrorCode.INVALID_FIELD_ERROR, SpringUtils.getMessage(servletRequest, SpringUtils.INVALID_FIELD_ERROR_CODE));
            }
        } else {
            try {
                resultObj = JSONObject.parseObject(requestBody, parameterType);
            } catch (Exception e) {
                throw new UnauthenticatedException(ErrorCode.INVALID_FIELD_ERROR, SpringUtils.getMessage(servletRequest, SpringUtils.INVALID_FIELD_ERROR_CODE));
            }
        }*/



        return resultObj;
    }

    private static class RequestCustomParamNamedValueInfo extends NamedValueInfo {

        public RequestCustomParamNamedValueInfo() {
            super("", false, ValueConstants.DEFAULT_NONE);
        }

        public RequestCustomParamNamedValueInfo(RequestCustomParam annotation) {
            super(annotation.value(), annotation.required(), annotation.defaultValue());
        }
    }

    private static class RequestPartResolver {

        public static Object resolvePart(HttpServletRequest servletRequest) throws Exception {
            return servletRequest.getParts().toArray(new Part[servletRequest.getParts().size()]);
        }
    }
    /**
     * Represents the information about a named value, including name, whether it's required and a default value.
     */
    protected static class NamedValueInfo {

        private final String name;

        private final boolean required;

        private final String defaultValue;

        public NamedValueInfo(String name, boolean required, String defaultValue) {
            this.name = name;
            this.required = required;
            this.defaultValue = defaultValue;
        }
    }
}
