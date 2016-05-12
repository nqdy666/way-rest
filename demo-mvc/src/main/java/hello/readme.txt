1.
        <parent>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-parent</artifactId>
            <version>1.3.5.RELEASE</version>
        </parent>

        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
            </dependency>
        </dependencies>
2.i18n
        配置messageSource
        messageSource.getMessage("hello", new Object[]{}, LocaleContextHolder.getLocale())
        默认locale的解析器是header中的Accept-Language，不填则根据操作系统的语言环境。