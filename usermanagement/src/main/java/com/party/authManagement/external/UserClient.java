package com.party.authManagement.external;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "usermanagement")
public interface UserClient {
    
    @PostMapping("/user/checkPassword/{username}")
    Map<String, String> checkPassword(@PathVariable(name = "username") String username, @RequestBody String password); 
}
