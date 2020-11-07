package com.concordia.message_board.controller;

import com.concordia.message_board.service.PostManager;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Map;

@Controller
public class BoardController {
    @Autowired
    private PostManager postManager;

    @Value("${display.number}")
    private String number;

    @GetMapping("/")
    public String logIn(){
        return "login";
    }

    @PostMapping("/authentication")
    public String authentication(@RequestParam("username") String username,
                                 @RequestParam("password") String password,
                                 Map<String,Object> map, HttpSession session){

        System.out.println("Number---->"+number);
        if(postManager.authentication(username, password)) return "Ok";

        return "error";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(@RequestParam("file") MultipartFile file, Model model){
        if (file.isEmpty()){
            model.addAttribute("uploadMessage", "The file is empty!");
            return "postMessage";
        }
        try{
            //InputStream in = file.getInputStream();

            byte[] bytes = file.getBytes();
            //Path path = Paths.get("E:\\fileUpload/" + file.getOriginalFilename());
            Path path = Paths.get("C:\\Users\\Administrator\\Desktop\\SOEN387\\A2\\fileUpload/" + file.getOriginalFilename());
            Files.write(path, bytes);
            model.addAttribute("uploadMessage", "success");

        }catch (Exception e){
            e.printStackTrace();
        }
        return "postMessage";
    }
}
