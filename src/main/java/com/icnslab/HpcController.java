package com.icnslab;

import com.icnslab.assist.JSONAssistant;
import com.icnslab.assist.TimeGetter;
import com.icnslab.database.PlatformDao;
import com.icnslab.message.AckResponse;
import com.icnslab.message.JobMessage;
import com.icnslab.message.JobMessageBuilder;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by alicek106 on 2017-08-04.
 */
@RestController
public class HpcController {

    @Autowired
    HpcService hpcService;

    @Autowired
    HpcScheduler hpcScheduler;

    @Autowired
    PlatformDao platformDao;

    @RequestMapping(value = "/api/test", method = RequestMethod.GET)
    @ResponseBody
    public AckResponse getTest(){
        return new AckResponse("test");
    }

    @RequestMapping(value = "/api/initialize", method = RequestMethod.POST)
    @ResponseBody
    public AckResponse initialize(){
        String responseMsg = (hpcService.discoverWorker() && hpcScheduler.initialize())?"success":"false";
        return new AckResponse(responseMsg);
    }

    @RequestMapping(value = "/api/job/list", method = RequestMethod.GET)
    @ResponseBody
    public List<JobMessage> getJob(@RequestParam(value = "user", required = true) String uid){
        return platformDao.selectJob(uid);
    }


    @RequestMapping(value = "/api/job/check", method = RequestMethod.POST)
    @ResponseBody
    public AckResponse checkCompletedJob(@RequestParam(value = "user", required = true) String uid){
        JSONObject res = hpcService.checkJob(uid);
        return new AckResponse(res.toJSONString());
    }

    @RequestMapping(value = "/api/job/submit", method = RequestMethod.POST)
    @ResponseBody
    public AckResponse submitJob(@RequestParam(value = "data", required = true) String data){
        JSONObject job = JSONAssistant.parseJSON(data);
        System.out.println(job);
        JobMessage jobMessage = new JobMessageBuilder().
                //setExepath(JSONAssistant.getString(job, "exepath")).
                setCpu(JSONAssistant.getInt(job, "cpu")).
                setMem(JSONAssistant.getInt(job, "mem")).
                setBlki(JSONAssistant.getInt(job, "blki")).
                setBlko(JSONAssistant.getInt(job, "blko")).
                setNeti(JSONAssistant.getInt(job, "neti")).
                setNeto(JSONAssistant.getInt(job, "neto")).
                setCount(JSONAssistant.getInt(job, "count")).
                setMpicmd(JSONAssistant.getString(job, "mpicmd")).
                setImage(JSONAssistant.getString(job, "image")).
                setName(JSONAssistant.getString(job, "name")).
                setUser(JSONAssistant.getString(job, "user")).
                setMetadata(JSONAssistant.getString(job,"metadata")).
                setCreated(TimeGetter.getCurrentDate()).
                createJobMessage();
        hpcService.submitJob(jobMessage);
        return new AckResponse("test");
    }
}
