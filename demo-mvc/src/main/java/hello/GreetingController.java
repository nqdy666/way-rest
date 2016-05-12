package hello;

/**
 * @author way
 * Created on 2016/5/12.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    private MessageSource messageSource;

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
//        return new Greeting(counter.incrementAndGet(),
//                String.format(template, name));
//        return new Greeting(counter.incrementAndGet(), messageSource.getMessage("hello", new Object[]{}, Locale.ENGLISH));
//        return new Greeting(counter.incrementAndGet(), messageSource.getMessage("hello", new Object[]{}, Locale.CHINA));
        return new Greeting(counter.incrementAndGet(), messageSource.getMessage("hello", new Object[]{}, LocaleContextHolder.getLocale()));

    }
}
