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
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    AndroidPushNotificationsService androidPushNotificationsService;

    @RequestMapping(value = "/send/{nome}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> send(@PathVariable("nome") String nome) {
        JSONObject body = new JSONObject();
        // JsonArray registration_ids = new JsonArray();
        // body.put("registration_ids", registration_ids);
     
        body.put("to", "eMSz72U5slE:APA91bGu3lx5rB_CV43goCSfIk6p0OEgTDHKO8ibcC-s8AyYE0iIY2OMrQgi4bO0lG79CFrEeUEtB3JV9f3N4dTs1Yg1G-4YHOeuc2Ut2KrpAxQDaox6WK1BxBEx4opXpTutEqj3vKrY");
        body.put("priority", "high");
 
        // body.put("dry_run", true);

        JSONObject notification = new JSONObject();
        notification.put("body", "Apenas um teste");
        notification.put("title", "Titulo de teste");
        // notification.put("icon", "myicon");

//        JSONObject data = new JSONObject();
        
        body.put("notification", notification);
//        body.put("data", data);

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
