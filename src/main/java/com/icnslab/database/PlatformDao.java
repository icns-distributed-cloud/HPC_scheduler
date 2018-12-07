package com.icnslab.database;

import com.icnslab.message.JobContainer;
import com.icnslab.message.JobMessage;
import com.icnslab.message.Server;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.ContainerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * Created by alicek106 on 2017-08-03.
 */
@Repository
public class PlatformDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // only used when initialize scheduler
    public boolean insertServer(String url, int cpu, int mem){
        try {
            String query = String.format("insert into server values('%s','%d','%d','%s','%s');",
                    url, cpu, mem, cpu, mem);
            jdbcTemplate.update(query);
        }catch(Exception e){
            return false;
        }
        return true;
    }

    public List<Server> selectServer(){
        String query = String.format("SELECT * FROM server");
        List<Server> servers = jdbcTemplate.query(query, new BeanPropertyRowMapper(Server.class));
        return servers;
    }

    public Server selectServer(String url){
        String query = String.format("SELECT * FROM server where url = '%s'", url);
        List<Server> servers = jdbcTemplate.query(query, new BeanPropertyRowMapper(Server.class));
        return servers.get(0);
    }

    public void updateServerResource(HashMap<Server, DockerClient> servers){
        for( Server key : servers.keySet() ){
            String query = String.format("update server set avcpu='%s' where url = '%s'", key.getAvcpu(), key.getUrl());
            jdbcTemplate.update(query);

            query = String.format("update server set avmem='%s' where url = '%s'", key.getAvmem(), key.getUrl());
            jdbcTemplate.update(query);
        }
    }

    public void updateServerResource(String url, int cpu, int mem){
        String query = String.format("update server set avcpu='%s' where url = '%s'", cpu, url);
        jdbcTemplate.update(query);

        query = String.format("update server set avmem='%s' where url = '%s'", mem, url);
        jdbcTemplate.update(query);
    }

    public boolean insertJob(JobMessage jobMessage, String status){
        try {
            String query = String.format("insert into job values('%s','%s','%s','%d','%d'," +
                            " '%d','%d','%d' ,'%d' ,'%s', '%s', '%s', '%s', '%d');",
                    jobMessage.getName(), jobMessage.getUser(), jobMessage.getImage(),
                    jobMessage.getCpu(), jobMessage.getMem(), jobMessage.getBlki(),
                    jobMessage.getBlko(), jobMessage.getNeti(), jobMessage.getNeto(),
                    //jobMessage.getExepath(),
                    jobMessage.getMpicmd(), status,
                    jobMessage.getCreated(), jobMessage.getMetadata(), jobMessage.getCount());
            jdbcTemplate.update(query);
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void insertJob_container(List<ContainerInfo> list, List<DockerClient> dc, String uid, String created, String jobName, String imageName){
        try{
            for(int i = 0; i < list.size(); i++){
                String query = String.format("insert into job_container values('%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                        list.get(i).id(), uid, dc.get(i).getHost(), "true", created, jobName, imageName);
                jdbcTemplate.update(query);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public List<JobContainer> selectJobContainer(String uid, String created){
        String query = String.format("SELECT * FROM job_container where user = '%s' and created = '%s'", uid, created);
        List<JobContainer> jobs = jdbcTemplate.query(query, new BeanPropertyRowMapper(JobContainer.class));
        return jobs;
    }

    public void updateJobContainerStatus(String id, String uid){
        String query = String.format("update job_container set alive='false'" +
                " where name = '%s' and user = '%s'", id, uid);
        jdbcTemplate.update(query);
    }

    public void updateJobStatus(String uid, String created, String status){
        String query = String.format("update job set status='%s' where user = '%s' and created = '%s'", status, uid, created);
        jdbcTemplate.update(query);
    }

    public List<JobMessage> selectJob(String uid){
        String query = String.format("SELECT * FROM job where user = '%s'", uid);
        List<JobMessage> jobs = jdbcTemplate.query(query, new BeanPropertyRowMapper(JobMessage.class));
        return jobs;
    }

    public List<JobMessage> selectRunningJob(String uid){
        String query = String.format("SELECT * FROM job where user = '%s' and status = 'running'", uid);
        List<JobMessage> jobs = jdbcTemplate.query(query, new BeanPropertyRowMapper(JobMessage.class));
        return jobs;
    }
}
