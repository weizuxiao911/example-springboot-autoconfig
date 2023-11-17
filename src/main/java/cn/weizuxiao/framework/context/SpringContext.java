package cn.weizuxiao.framework.context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * spring上下文
 */
public class SpringContext implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContext.context = applicationContext;
    }

    /**
     * 获取spring bean
     * @param <T>
     * @param className
     * @return
     */
    public static <T> T getBean(Class<T> className) {
        return SpringContext.context.getBean(className);
    }

    /**
     * 获取spring bean
     * @param <T>
     * @param className
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String className) {
        return (T) SpringContext.context.getBean(className);
    }
    
}
