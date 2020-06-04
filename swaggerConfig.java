@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * Swagger2 自由扫描配置符
     */
    public static final String SPLITOR = ";";

    @Bean
    public Docket createRestApi() {

        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        tokenPar.name("Authorization")
                .description("Token")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .defaultValue("bearer")
                .required(false)
                .build();
        pars.add(tokenPar.build());

        return new Docket(DocumentationType.SWAGGER_2)
//                .directModelSubstitute(LocalDateTime.class, Date.class)
//                .directModelSubstitute(LocalDateTime.class, String.class)
//                .directModelSubstitute(LocalDateTime.class, String.class)
//                .directModelSubstitute(ZonedDateTime.class, String.class)
                .apiInfo(apiInfo())
                .select()
                .apis(basePackage("com.xxx.controller"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(pars);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("xxxx API")
                .description("xxx")
                .termsOfServiceUrl("xxxx")
                .contact(new Contact("xxxx", "xx", "xxx"))
                .version("1.0.0")
                .build();
    }


    public static Predicate<RequestHandler> basePackage(final String basePackage) {
        return input -> declaringClass(input).transform(handlerPackage(basePackage)).or(true);
    }

    private static Function<Class<?>, Boolean> handlerPackage(final String basePackage) {
        return input -> {
            for (String strPackage : basePackage.split(SPLITOR)) {
                boolean isMatch = input.getPackage().getName().startsWith(strPackage);
                if (isMatch) {
                    return true;
                }
            }
            return false;
        };
    }

    private static Optional<? extends Class<?>> declaringClass(RequestHandler input) {
        return Optional.fromNullable(input.declaringClass());
    }
}
