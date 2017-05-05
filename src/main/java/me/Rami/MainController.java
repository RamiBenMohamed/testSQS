package me.Rami;

import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;

@RestController
public class MainController {
         private static final String message = "Hello";
	@Autowired
         MessageService ms;
        @RequestMapping("/MessageService")
        public MessageService  write(@RequestParam(value="name", defaultValue="World") String name) {
                ms.sendMessage(String.format(message,name));
                return ms;
                
        }
}


