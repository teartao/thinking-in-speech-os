package neo.tao.mvc.annotation;

import java.lang.annotation.*;

/**
 * @Author neotao
 * @Date 2018/8/3
 * @Version V0.0.1
 * @Desc
 */
@Documented
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String value() default "";
}
