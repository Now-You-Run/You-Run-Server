package com.running.you_run.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/friend")
public class FriendController {
    @GetMapping("")
    public void addFriend(@RequestParam Long user1Id, @RequestParam Long user2Id){

    }
}
