package neo.tao.mvc.servlet;

import neo.tao.mvc.annotation.Autowired;
import neo.tao.mvc.annotation.Controller;
import neo.tao.mvc.annotation.RequestMapping;
import neo.tao.mvc.annotation.Service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author neotao
 * @Date 2018/8/3
 * @Version V0.0.1
 * @Desc
 */
public class DispatcherServlet extends HttpServlet {

    private Properties contextConfig = new Properties();
    private List<String> classNames = new ArrayList<>();
    private Map<String, Object> ioc = new HashMap<>();

    private Map<String, Method> handlerMapping = new HashMap<>();
    private List<Handler> handlerMappingList = new ArrayList<Handler>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 1、加载配置
        doLoadConfigurations(config.getInitParameter("contextConfigLocation"));
        //2、扫描类
        doScanner(contextConfig.getProperty("componentScan.basePackage"));
        //3、初始化类
        doInstance();
        //4、依赖注入
        doAutowired();
        //5、初始化请求路径
        initHandlerMapping();

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
//        super.doPost(req, resp);
        try {
            doDispatcher(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500" + e.getStackTrace());
        }
    }

    /**
     * 改造前
     * @param req
     * @param resp
     * @throws IOException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
//    private void doDispatcher(HttpServletRequest req, HttpServletResponse resp) throws IOException, InvocationTargetException, IllegalAccessException {
//        String uri = req.getRequestURI();
//        String contextPath = req.getContextPath();
//        uri = uri.replace(contextPath, "").replaceAll("/+", "/");
//        if (!handlerMapping.containsKey(uri)) {
//            resp.getWriter().write(" 404 not found ");
//        }
//        Method method = this.handlerMapping.get(uri);
//        Map<String, String[]> params = req.getParameterMap();
//        method.invoke(ioc.get("1"));
//        System.out.println(method);
//    }

    /**
     * 改造后
     *
     * @param
     */
    private void doDispatcher(HttpServletRequest req, HttpServletResponse resp) throws IOException, InvocationTargetException, IllegalAccessException {
        try {
            Handler handler = getHandler(req);
            if (handler == null) {
                resp.getWriter().write("404 not found ! ");
                return;
            }

            Class<?>[] paramTypes = handler.method.getParameterTypes();
            Object[] paramValues = new Object[paramTypes.length];

            Map<String, String[]> params = req.getParameterMap();

            for (Map.Entry<String, String[]> param : params.entrySet()) {
                String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "");

                if (!handler.paramIndexMapping.containsKey(param.getKey())) {
                    continue;
                }
                int index = handler.paramIndexMapping.get(param.getKey());
                paramValues[index] = convert(paramTypes[index], value);
            }


            //设置方法中request  response对象
            int reqIndex = handler.paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;

            int respIndex = handler.paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;


            handler.method.invoke(handler.controller, paramValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object convert(Class<?> paramType, String value) {
        return null;
    }

    private Handler getHandler(HttpServletRequest req) {
        if (handlerMapping.isEmpty()) {
            return null;
        }
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");
        for (Handler handler : handlerMappingList) {
            try {
                Matcher matcher = handler.pattern.matcher(url);
                //如果没有匹配上则继续下一个
                if (!matcher.matches()) {
                    continue;
                }
                return handler;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void doLoadConfigurations(String contextConfigLocation) {
        InputStream configStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            contextConfig.load(configStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != configStream) {
                try {
                    configStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classDir = new File(url.getFile());
        for (File file : classDir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                String className = (scanPackage + "." + file.getName().replace(".class", ""));
                classNames.add(className);
            }
        }
    }

    private void doInstance() {
        if (classNames.isEmpty()) {
            return;
        }
        try {
            for (String className : classNames) {
                Class<?> clazz = Class.forName(className);
                //初始化
                //只初始化注解类
                if (clazz.isAnnotationPresent(Controller.class)) {
                    Object instance = clazz.newInstance();
                    String beanName = firstToLowerCase(clazz.getSimpleName());
                    ioc.put(beanName, instance);

                } else if (clazz.isAnnotationPresent(Service.class)) {
                    //1、默认首字母小写
                    //2、优先使用自定义beanId
                    Service service = clazz.getAnnotation(Service.class);
                    String beanName = service.value();
                    if ("".equals(beanName.trim())) {
                        beanName = firstToLowerCase(clazz.getSimpleName());
                    }
                    Object instance = clazz.newInstance();
                    ioc.put(beanName, instance);

                    //3、key接口的type
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> i : interfaces) {
                        ioc.put(i.getName(), instance);
                    }

                } else {
                    continue;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private void doAutowired() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                //只有注解才赋值
                if (!field.isAnnotationPresent(Autowired.class)) {
                    continue;
                }
                Autowired autowired = field.getAnnotation(Autowired.class);
                String beanName = autowired.value().trim();
                if ("".equals(beanName)) {
                    beanName = field.getType().getName();
                }
                field.setAccessible(true);//反射开启private访问权限
                try {
                    field.set(entry.getValue(), ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void initHandlerMapping() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(Controller.class)) {
                continue;
            }

            String baseUrl = "";
            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                baseUrl = requestMapping.value();
            }
            for (Method method : clazz.getMethods()) {
                if (!method.isAnnotationPresent(RequestMapping.class)) {
                    continue;
                }

                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                /**
                 * requestMapping Map写法
                 */
                String url = (baseUrl + "/" + requestMapping.value()).replaceAll("/+", "/");
                handlerMapping.put(url, method);
                System.out.println("[Map] handlerMapping Mapped " + url + "," + method);

                /**
                 * requestMapping List写法
                 */
                String regex = ("/" + baseUrl + requestMapping.value()).replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);
                handlerMappingList.add(new Handler(pattern, entry.getValue(), method));
                System.out.println("[List] handlerMapping Mapped " + regex + "," + method);

            }
        }
    }

    private String firstToLowerCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private class Handler {
        protected Object controller;
        protected Method method;
        protected Pattern pattern;
        protected Map<String, Integer> paramIndexMapping;//参数序列

        protected Handler(Pattern pattern, Object controller, Method method) {
            this.controller = controller;
            this.method = method;
            this.pattern = pattern;

            paramIndexMapping = new HashMap<>();
            putParamIndexMapping(method);
        }

        private void putParamIndexMapping(Method method) {
            Parameter[] params = method.getParameters();
            for (int index = 0; index < params.length; index++) {
                paramIndexMapping.put(params[index].getName(), index);
            }
        }
    }
}
