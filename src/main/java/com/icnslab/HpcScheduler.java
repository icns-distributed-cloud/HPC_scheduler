package com.icnslab;

import com.icnslab.database.PlatformDao;
import com.icnslab.message.JobMessage;
import com.icnslab.message.Server;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificates;
import com.spotify.docker.client.DockerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by alicek106 on 2017-08-04.
 */
@Repository
public class HpcScheduler {
    @Autowired
    PlatformDao platformDao;
    HashMap<Server, DockerClient> servers = new HashMap<Server, DockerClient>();

    public DockerClient createDockerClient(String url) throws Exception{
        DockerClient dc = DefaultDockerClient.builder()
                .uri(url)
                .dockerCertificates(new DockerCertificates(Paths.get("keys")))
                .build();
        return dc;
    }

    public boolean initialize(){
        List<Server> servers = platformDao.selectServer();
        for(Server server : servers){
            try {
                DockerClient dc = createDockerClient(server.getUrl());
                this.servers.put(server, dc);
                System.out.println("Successfully registered, " + server.getUrl());
            } catch(Exception e){
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public void reloadServer(){
//        System.out.println(servers);
        System.out.println("Reloading server...");
        servers.clear();
        List<Server> servers = platformDao.selectServer();
        for(Server server : servers){
            try {
                DockerClient dc = DefaultDockerClient.builder()
                        .uri(server.getUrl())
                        .dockerCertificates(new DockerCertificates(Paths.get("keys")))
                        .build();
                this.servers.put(server, dc);
                System.out.println("Successfully registered, " + server.getUrl());
            } catch(Exception e){
                e.printStackTrace();
            }
        }
//        System.out.println(servers);
    }

    public void updateServer(){
        platformDao.updateServerResource(servers);
    }

    public List<DockerClient> getAllocServer(JobMessage jobMessage){
        List<DockerClient> list = new ArrayList<>();

        for(int i = 0; i < jobMessage.getCount(); i++){
            for( Server key : servers.keySet() ){
                // map.get(key))
                if(!(key.getAvcpu() - jobMessage.getCpu() <= 0 || key.getAvmem() - jobMessage.getMem() <= 0)){
                    list.add(servers.get(key));
                    key.setAvcpu(key.getAvcpu() - jobMessage.getCpu());
                    key.setAvmem(key.getAvmem() - jobMessage.getMem());
                    break;
                }
            }
        }

        if(list.size() != jobMessage.getCount()){
            reloadServer();
            return null;
        }
        return list;
    }
}