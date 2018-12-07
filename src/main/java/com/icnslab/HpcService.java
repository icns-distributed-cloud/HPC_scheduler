package com.icnslab;

import com.icnslab.assist.ContainerCmdExecuter;
import com.icnslab.assist.TimeGetter;
import com.icnslab.database.PlatformDao;
import com.icnslab.message.JobContainer;
import com.icnslab.message.JobMessage;
import com.icnslab.message.Server;
import com.icnslab.mpiSetter.MpiSetter;
import com.icnslab.mpiSetter.MpichSetter;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificates;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerInfo;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by alicek106 on 2017-08-04.
 */

@Repository
public class HpcService {

    @Autowired
    PlatformDao platformDao;
    EtcdConnector etcdConnector = new EtcdConnector();

    @Autowired
    HpcScheduler hpcScheduler;

    // Intializer
    public boolean discoverWorker(){
        List<String> list = etcdConnector.getWorkerAddr();
        for (String str : list) {
            str = str.replace("http", "https");
            try {
                DockerClient worker = DefaultDockerClient.builder()
                        .uri(str)
                        .dockerCertificates(new DockerCertificates(Paths.get("keys")))
                        .build();
                if(!platformDao.insertServer(str, worker.info().cpus(), (int) (worker.info().memTotal() / 1024 / 1024))){
                    System.out.println("Already inserted server, " + str);
                }else{
                    System.out.println("Successfully inserted server, " + str);
                }
            } catch(DockerCertificateException | InterruptedException | DockerException e){
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public void submitJob(JobMessage jobMessage){
        List<DockerClient> nodeList = hpcScheduler.getAllocServer(jobMessage);
        String status = (nodeList == null)?"wait":"running";
        platformDao.insertJob(jobMessage, status);
        String origin_image = jobMessage.getImage();
        jobMessage.setImage(etcdConnector.getRegistryAddr() + jobMessage.getImage());
        // # step 0. if resource is available to allocate job,
        if(nodeList!=null){

            // # step 1. create containers
            List<ContainerInfo> infoList = new ArrayList<ContainerInfo>();
            for(DockerClient dc : nodeList){
                infoList.add(ContainerCreator.createContainer(dc, jobMessage));
            }
            DockerClient dc = nodeList.get(nodeList.size()-1);

            // # step 2. set mpi settings. temp : mpich.
            MpiSetter mpiSetter;
            //if(metadata.mpiType == "mpich"){
            mpiSetter = new MpichSetter();
            //}

            mpiSetter.createHostfile(dc, infoList);
            mpiSetter.startMpi(dc, infoList, jobMessage);

            // # step 3. update servers' available resources.
            hpcScheduler.updateServer();

            // # step 4. insert job-container information
            platformDao.insertJob_container(infoList, nodeList, jobMessage.getUser(), jobMessage.getCreated(), jobMessage.getName(), origin_image);
        }
    }

    public JSONObject checkJob(String uid){
        JSONObject result = new JSONObject();

        List<JobMessage> list = platformDao.selectRunningJob(uid);
        int count = 0;
        for(JobMessage jobMessage : list){
            try {
                if (jobMessage.getStatus().equals("running")) {
                    List<JobContainer> conList = platformDao.selectJobContainer(uid, jobMessage.getCreated());
                    String url = String.format("https://%s:2375", conList.get(0).getServer());

                    try {
                        DockerClient dc = hpcScheduler.createDockerClient(url);
                        String res = ContainerCmdExecuter.executeCmd(String.format("ls /root/output/%s", jobMessage.getCreated()),
                                conList.get(0).getName(), dc);

                        if (res.length() < 50) {
                            count++;
                            // 여기로로
                            String resultStr = ContainerCmdExecuter.executeCmd(String.format("cat /root/output/%s", jobMessage.getCreated()), conList.get(0).getName(), dc);
                            result.put("output", resultStr);
                            result.put("created", jobMessage.getCreated());

                            // reclaim container
                            // # 1. delete containers
                            for (int i = 0; i < conList.size(); i++) {
                                url = String.format("https://%s:2375", conList.get(i).getServer());
                                dc = hpcScheduler.createDockerClient(url);
                                dc.removeContainer(conList.get(i).getName(), DockerClient.RemoveContainerParam.forceKill());
                                dc.close();

                                // # 2. update container status alive
                                platformDao.updateJobContainerStatus(conList.get(i).getName(), uid);
                            }

                            // # 3. update job status
                            platformDao.updateJobStatus(uid, jobMessage.getCreated(), "finished");

                            // # 4. update server available resource
                            // # 4.1 aggregate reclaimed resource
                            HashMap<String, Integer> reclaimedResourceMem = new HashMap<String, Integer>();
                            HashMap<String, Integer> reclaimedResourceCpu = new HashMap<String, Integer>();
                            int cpu = jobMessage.getCpu();
                            int mem = jobMessage.getMem();
                            for (JobContainer jc : conList) {
                                url = String.format("https://%s:2375", jc.getServer());
                                if (reclaimedResourceMem.get(url) == null) {
                                    reclaimedResourceMem.put(url, mem);
                                    reclaimedResourceCpu.put(url, cpu);
                                } else {
                                    reclaimedResourceMem.put(url,
                                            reclaimedResourceMem.get(url) + mem);
                                    reclaimedResourceCpu.put(url,
                                            reclaimedResourceCpu.get(url) + cpu);
                                }
                            }

                            // # 4.2 update Server Resource
                            for (String key : reclaimedResourceMem.keySet()) {
                                Server server = platformDao.selectServer(key);
                                platformDao.updateServerResource(key, server.getAvcpu() + reclaimedResourceCpu.get(key),
                                        server.getAvmem() + reclaimedResourceMem.get(key));
                            }

                            // # 5. reload server resource
                            hpcScheduler.reloadServer();
                        }

                        dc.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }catch(Exception e){
                System.out.println("index out");
            }
        }

        result.put("count", count);
        return result;
    }
}
