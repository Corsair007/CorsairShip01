package com.swagger.doc;

import java.time.LocalDate;
import java.util.List;

import org.springframework.context.annotation.Bean;

/**
 * Created by haoyifen on 16-9-28 2016 下午5:43
 */

import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Lists;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class MySwaggerConfig {

     @Bean
     public Docket restApi() {
         return new Docket(DocumentationType.SWAGGER_2)
        		 .select()
                 .apis(RequestHandlerSelectors.any())
                 .paths(PathSelectors.any())
                 .build()
                 .apiInfo(apiInfo())
               .pathMapping("/")
               .directModelSubstitute(LocalDate.class,
                   String.class)
               .genericModelSubstitutes(ResponseEntity.class)
//               .alternateTypeRules(
//                   newRule(typeResolver.resolve(DeferredResult.class,
//                           typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
//                       typeResolver.resolve(WildcardType.class)))
               .useDefaultResponseMessages(false)
               .globalResponseMessage(RequestMethod.GET,
            		   Lists.newArrayList(new ResponseMessageBuilder()
                       .code(500)
                       .message("500 message")
                       .responseModel(new ModelRef("Error"))
                       .build()))
               .securitySchemes(Lists.newArrayList(apiKey()))
               .securityContexts(Lists.newArrayList(securityContext()))
               .enableUrlTemplating(true)
//               .globalOperationParameters(
//                   Lists.newArrayList(new ParameterBuilder()
//                       .name("someGlobalParameter")
//                       .description("Description of someGlobalParameter")
//                       .modelRef(new ModelRef("string"))
//                       .parameterType("query")
//                       .required(true)
//                       .build()))
              // .tags(new Tag("Pet Service", "All apis relating to pets")) 
               //.additionalModels(typeResolver.resolve(AdditionalModel.class)) 
               ;
     }
     
//     @Autowired
//     private TypeResolver typeResolver;

     private ApiKey apiKey() {
       return new ApiKey("HTTP-ACCESS-TOKEN", "api_key", "header");
     }

     private SecurityContext securityContext() {
       return SecurityContext.builder()
           .securityReferences(defaultAuth())
           .forPaths(PathSelectors.regex("/*"))
           .build();
     }

     List<SecurityReference> defaultAuth() {
       AuthorizationScope authorizationScope
           = new AuthorizationScope("global", "accessEverything");
       AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
       authorizationScopes[0] = authorizationScope;
       return Lists.newArrayList(
           new SecurityReference("HTTP-ACCESS-TOKEN", authorizationScopes));
     }

     @Bean
     SecurityConfiguration security() {
       return new SecurityConfiguration(
           "test-app-client-id",
           "test-app-client-secret",
           "test-app-realm",
           "test-app",
           "apiKey",
           ApiKeyVehicle.HEADER, 
				"api_key", 
           "," /*scope separator*/);
     }
    
    
     private ApiInfo apiInfo() {
         return new ApiInfoBuilder()
                 .title("API Test  ")
                 .description("MyAPITest Description")
                 .contact(new Contact("haoyifen", "http://blog.csdn.net/haoyifen", "haoyifen@yy.com"))
                 .license("Apache 2.0")
                 .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                 .version("1.0.0")
                 .build();
     }
}
