package com.concordia.message_board.controller;

import com.concordia.message_board.entities.Post;
import com.concordia.message_board.mapper.MessageMapper;
import com.concordia.message_board.service.PostManager;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
public class BoardController {
    @Autowired
    private PostManager postManager;
    private MessageMapper messageMapper;

    @Value("${display.number}")
    private String number;

    @GetMapping("/")
    public String logIn(){
        return "login";
    }

    @GetMapping("/ok")
    public String ok(){
        return "Ok";
    }


    @PostMapping("/authentication")
    public String authentication(@RequestParam("userId") String userId,
                                 @RequestParam("password") String password,
                                 Map<String,Object> map, HttpSession session){
        System.out.println("Number---->"+number);
        if(postManager.authentication(userId, password)){
            session.setAttribute("userId",userId);
            return "redirect:/message";
        }

        return "error";
    }

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public String post(@RequestParam("title") String title,
                       @RequestParam("content") String content,
                       @RequestParam("file") MultipartFile file,
                       Model model,HttpSession session) throws SQLException, IOException {

        String date = messageMapper.getPostTime();
        InputStream in = null;
        if (file.isEmpty()){
            model.addAttribute("uploadMessage", "The file is empty!");
            return "postMessage";
        }
        else
            in = file.getInputStream();

        Post post = new Post("1",title,content,date, (Blob) in);

        messageMapper.insertIntoDB(post);
        //get All post from DB
        List<Post> posts = messageMapper.getAllPost();
        model.addAttribute("posts", posts);

        return "viewMessage";
    }

    @GetMapping("/allPosts")
    public String allPosts(Model model) throws SQLException {

        List<Post> posts = messageMapper.getAllPost();

        model.addAttribute("posts", posts);

        return "viewMessage";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(@RequestParam("file") MultipartFile file, Model model){
        if (file.isEmpty()){
            model.addAttribute("uploadMessage", "The file is empty!");
            return "postMessage";
        }
        try{
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
