package com.ymjrhk.rbac.config;

import com.ymjrhk.rbac.interceptor.AuditFailInterceptor;
import com.ymjrhk.rbac.interceptor.AuthInterceptor;
import com.ymjrhk.rbac.interceptor.PermissionInterceptor;
import com.ymjrhk.rbac.json.JacksonObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
// 将 extends WebMvcConfigurationSupport 改为 implements WebMvcConfigurer，
// 并将重写的方法属性由 protected 改为 public，
// 修改后可不重写静态资源映射的 addResourceHandlers 方法，
// 因为 implements 了 WebMvcConfigurer 接口之后可以自动映射静态资源
public class WebConfigurer implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    private final PermissionInterceptor permissionInterceptor;

    private final AuditFailInterceptor auditFailInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 1. 认证拦截器（先执行）
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login") // "/api/auth/logout" 要拦
                .order(1);

        // 2. 鉴权拦截器（后执行）
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/**",
                        "/api/me/**"   // auth-only 只需认证不需鉴权
                )
                .order(2);

        // 3. 参数类型失败审计日志记录拦截器（只有 afterCompetition）
        registry.addInterceptor(auditFailInterceptor)
                .addPathPatterns("/api/**")
                .order(3);
    }

    @Bean
    public OpenAPI publicApi(Environment environment) {
        return new OpenAPI()
//                .servers(serverList())
                .info(new Info()
                        .title("RBAC项目")
                        //.extensions(Map.of("x-audience", "external-partner", "x-application-id", "APP-12345"))
                        .description("RBAC项目接口文档")
                        .version("1.0")
                );
//        .addSecurityItem(new SecurityRequirement().addList("bearer-jwt", Arrays.asList("read", "write"))).security(securityList());
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");
        //创造一个消息转换器对象
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        //需要为消息转换器设置一个对象转换器，对象转换器可以将Java对象序列化为json数据
        converter.setObjectMapper(new JacksonObjectMapper());
        //将自己的消息转换器加入容器中
        converters.addFirst(converter);

        // 需要追加byte，否则springdoc-openapi接口会响应Base64编码内容，导致接口文档显示失败
        // 由于覆盖了 Spring 默认注册的 HttpMessageConverter ，因此也应该注册 ByteArrayHttpMessageConverter
        // 顺序必须在自定义的“前面”
        converters.addFirst(new ByteArrayHttpMessageConverter());
    }
}
