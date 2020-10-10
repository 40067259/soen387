package com.concordia.chatroom.controller;

import com.concordia.chatroom.entity.Message;
import com.concordia.chatroom.service.ChatManagerImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Controller
public class MessageController {
    @Autowired
    ChatManagerImp chatManager;

    @RequestMapping(value = "/getMessages")
    public String getMessages(@RequestParam(value = "from", required = false) String from,
                              @RequestParam(value = "to", required = false) String to,
                              @RequestParam(value = "format", required = false) String format,
                              Map<String, Object> map) {
        map.put("recording", new Message("User", "Hello World"));
        chatManager.listMessages(from, to);
        chatManager.postMessage("Fred", "I love coding");
        chatManager.postMessage("Taylor", "Welcome to NewYork");
        map.put("messages", chatManager.getMessagesStore());
        return "chatRoom";
    }

    @RequestMapping(value = "/message", method = RequestMethod.POST)
    public String chatRoom(@RequestParam(value = "username", required = false) String user,
                           @RequestParam(value = "message", required = true) String message,
                           Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) {

        /*if (user != "") {
            Cookie cookie = new Cookie("user", user);
            cookie.setMaxAge(60 * 60);
        }*/
        if (user == "") user = "anonymous";
        chatManager.postMessage(user, message);
        map.put("messages", chatManager.getMessagesStore());
        map.put("chatRoomName","Concordia");
        //add
        /*inputText += user + ": " + message + " " + "\n";
        request.setAttribute("inputText",inputText);*/
        return "chatRoom";
    }

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String index(){
        return "chatRoom";
    }


    @RequestMapping(value="/download", method=RequestMethod.GET)
    public String download(@RequestParam(value = "format", required = false) String format,
                           @RequestParam(value = "from", required = false) String from,
                           @RequestParam(value = "to", required = false) String to,
                           HttpServletResponse  response) throws Exception {
        List<Message> chatRecord;
        chatRecord = chatManager.listMessages(from, to);

        //download file
        if (format.equals("txt")){
            String filename = "Chat Record.txt";
            response.setContentType("text/plain");
            response.setHeader("Content-Disposition", "attachment;filename= " + filename);

            PrintWriter out = response.getWriter();
            Message currentMsg;

            for (int i =0; i< chatRecord.size(); i++){
                currentMsg = chatRecord.get(i);
                out.println(currentMsg.toString());
            }
            out.flush();
            out.close();
        }
        if (format.equals("xml")){
            String filename = "Chat Record.xml";
            response.setContentType("text/xml");
            response.setHeader("Content-Disposition", "attachment;filename= " + filename);

            PrintWriter out = response.getWriter();
            Message currentMsg;
            out.println("<Chat>");
            for (int i =0; i< chatRecord.size(); i++){
                currentMsg = chatRecord.get(i);
                out.println("<message>");
                out.println("\t<name>" + currentMsg.getUser() + "</name>");
                out.println("\t<date>" + currentMsg.getDate() + "</date>");
                out.println("\t<content>" + currentMsg.getContent() + "</content>");
                out.println("</message>\n");
            }
            out.println("</Chat>");
            out.flush();
            out.close();
        }

        return "chatRoom";
    }


    @RequestMapping(value = "/clear",method = RequestMethod.GET)
    public String clear(@RequestParam(value = "from", required = false) String from,
                        @RequestParam(value = "to", required = false) String to,
                        Map<String, Object> map, HttpServletRequest request, HttpServletResponse response){

        chatManager.clearChat(from,to);
        map.put("messages", chatManager.getMessagesStore());
        return "chatRoom";
    }

}
