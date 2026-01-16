package com.agbunag.controller;
import org.springframework.web.bind.annotation.*;
import com.agbunag.model.AppDetail;

import java.util.List;
@RestController
public class AppDetailsController {
    public  List<AppDetail> getMainMenu(){
        List<AppDetail> appDetails = null;
        return appDetails;
    }
}
