package com.icnslab;

import com.icnslab.message.JobMessage;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.*;

import java.util.ArrayList;
import java.util.List;
import com.icnslab.mpiSetter.MpichSetter;

/**
 * Created by alicek106 on 2017-08-04.
 */
public class ContainerCreator {

    final public static String NETWORK = "hpc-network";
    final private static String CALICO_NETWORK_INTERFACE = "cali0";
    final private static String CAP_ADD_NETWORK = "NET_ADMIN";
    /*
    * Create Container
    * */
    public static ContainerInfo createContainer(DockerClient dc, JobMessage jobMessage){
        // temp : MPICH

        //if(metadata.mpiType == "mpich"){
        MpichSetter mpichSetter = new MpichSetter();
        List<String> binds = mpichSetter.setMpiBind(jobMessage);

        //}

        final HostConfig hostConfig = HostConfig.builder()
                .networkMode(NETWORK)
                .memory((long)(jobMessage.getMem() * 1024 * 1024))
                .cpuShares((long)jobMessage.getCpu() * 1024)
                .binds(binds)
                .capAdd(CAP_ADD_NETWORK)
                .networkMode("hpc-network")
                .build();

        List<String> cmd = new ArrayList<>();
        cmd.add(CALICO_NETWORK_INTERFACE);
        cmd.add(String.format("%dm", jobMessage.getNeto()));
        cmd.add(String.format("%dm", jobMessage.getNeti()));

        // Create container with exposed ports
        final ContainerConfig containerConfig = ContainerConfig.builder()
                .hostConfig(hostConfig)
                .image(jobMessage.getImage())
                .cmd(cmd)
                .tty(true)
                .attachStderr(true)
                .attachStdin(true)
                .attachStdout(true)
                .stdinOnce(true)
                .openStdin(true)
                .build();
        try {
            final ContainerCreation creation = dc.createContainer(containerConfig);
            final String id = creation.id();
            dc.startContainer(id);
            return dc.inspectContainer(id);
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
