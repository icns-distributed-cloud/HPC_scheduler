package com.icnslab.mpiSetter;
import com.icnslab.assist.ContainerCmdExecuter;
import com.icnslab.message.JobMessage;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.ContainerInfo;
import java.util.ArrayList;
import java.util.List;

import static com.icnslab.ContainerCreator.NETWORK;

/**
 * Created by alicek106 on 2017-08-06.
 */
public class MpichSetter implements MpiSetter {
    public static final String MPI_LIB_BIND_PATH = "/mnt/lustre/app/mpich/%s/lib/:/home/app/mpich/mpich-libs";
    public static final String MPI_EXAMPLE_BIND_PATH = "/mnt/lustre/app/mpich/examples/:/home/app/mpich/example";

    public List<String> setMpiBind(JobMessage jobMessage){
        // MPI Library Specific ... Settings
        List<String> binds = new ArrayList<String>();
        binds.add(String.format(MPI_LIB_BIND_PATH, "3.2v"));
        binds.add(MPI_EXAMPLE_BIND_PATH);

        // default
        binds.add(MPI_HOSTFILE_EXAMPLE_PATH);
        binds.add(String.format(MPI_EXE_BIND_PATH, jobMessage.getUser()));
        binds.add(String.format(MPI_OUTPUT_PATH, jobMessage.getUser()));
        return binds;
    }

    public void startMpi(DockerClient dc, List<ContainerInfo> infoList, JobMessage jobMessage){
        String cmd = String.format("./home/app/mpich/mpich-libs/bin/mpirun -outfile-pattern /root/output/%s " +
                        "--hostfile /hostfile %s",
                jobMessage.getCreated(), jobMessage.getMpicmd());
        System.out.println(cmd);

        String res = ContainerCmdExecuter.executeCmd(cmd, infoList.get(infoList.size() - 1).id(), dc);
        System.out.println(res);
    }

    public void createHostfile(DockerClient dc, List<ContainerInfo> infoList) {
        ContainerInfo startNode = infoList.get(infoList.size() - 1);
        ContainerCmdExecuter.executeCmd_nonSync("cp /hostfile_example /hostfile", startNode.id(), dc);

        for(ContainerInfo info : infoList){
            String[] arr = {"sed","-i","$ a\\" + info.config().hostname() + "." + NETWORK,"/hostfile"};
            ContainerCmdExecuter.executeCmd(arr, startNode.id(), dc);
        }
    }
}
