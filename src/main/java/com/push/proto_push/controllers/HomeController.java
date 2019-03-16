package com.push.proto_push.controllers;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.push.proto_push.helpers.AndroidPushNotificationsService;
import com.push.proto_push.helpers.FirebaseResponse;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    AndroidPushNotificationsService androidPushNotificationsService;

    @RequestMapping(value = "/send", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> send() {


        JSONObject body = new JSONObject();
        // JsonArray registration_ids = new JsonArray();
        // body.put("registration_ids", registration_ids);
        // Italo Berg
        body.put("to", "dQ1wo4ZHyjY:APA91bHXtjCkV8NQCbJ_kxYogesogqGhM5eCvlwuTfESSElGkeyyStnnSaJj-Dfa6MuGDwlndk3N9Dvoakn_RpZ6kHDgc6ALXzfRAmAbhpLPEVbyf3eNKqzIFQ8YrMOxRkp5Q8C4lCc8");
        // Mateus Oliveira
        // body.put("to", "d0tFrPLRfAI:APA91bEXnCGBexpDnUMxfWuUXXbvYj5b7IpwFB5wuExQkzcAJeVkRamUkgxorcGhgHHNzcY24xI8KmPv8-8Jcc2BU_ZQgtlUxugg687-ra6hyq83ZCwiIDrXYYHHDQKJ1dWsqID5uX6W");
        body.put("priority", "high");
        // body.put("dry_run", true);

        JSONObject notification = new JSONObject();
        notification.put("body", "Eu vim do spring boot xD");
        notification.put("title", "Sci Push - Italo");
        // notification.put("icon", "myicon");

        JSONObject data = new JSONObject();
        data.put("key1", "value1");
        data.put("key2", "value2");

        body.put("notification", notification);
        body.put("data", data);

        HttpEntity<String> request = new HttpEntity<>(body.toString());

        CompletableFuture<FirebaseResponse> pushNotification = androidPushNotificationsService.send(request);
        CompletableFuture.allOf(pushNotification).join();

        try {
            FirebaseResponse firebaseResponse = pushNotification.get();
            if (firebaseResponse.getSuccess() == 1) {
                log.info("push notification sent ok!");
            } else {
                log.error("error sending push notifications: " + firebaseResponse.toString());
            }
            return new ResponseEntity<>(firebaseResponse.toString(), HttpStatus.OK);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("the push notification cannot be send.", HttpStatus.BAD_REQUEST);
    }
}
