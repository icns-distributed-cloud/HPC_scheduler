package com.icnslab.assist;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.messages.ExecCreation;

/**
 * Created by alicek106 on 2017-08-06.
 */
public class ContainerCmdExecuter {
    static public void executeCmd_nonSync(String cmd, String id, DockerClient dc) {
        try {
            System.out.println("non-sync : " + cmd);
            String[] arr = cmd.split(" ");
            ExecCreation execCreation = dc.execCreate(id, arr, DockerClient.ExecCreateParam.attachStdout(),
                    DockerClient.ExecCreateParam.attachStderr());
            String execId = execCreation.id();
            try (LogStream stream = dc.execStart(execId)) {
                stream.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    static public String executeCmd(String cmd, String id, DockerClient dc) {
        try {
            String[] arr = cmd.split(" ");

            ExecCreation execCreation = dc.execCreate(id, arr, DockerClient.ExecCreateParam.attachStdout(),
                    DockerClient.ExecCreateParam.attachStderr());

            String execId = execCreation.id();

            try (LogStream stream = dc.execStart(execId)) {
                final String output = stream.readFully();
                stream.close();
                return output;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    static public String executeCmd(String[] cmd, String id, DockerClient dc) {
        try {
            ExecCreation execCreation = dc.execCreate(id, cmd, DockerClient.ExecCreateParam.attachStdout(),
                    DockerClient.ExecCreateParam.attachStderr());

            String execId = execCreation.id();

            try (LogStream stream = dc.execStart(execId)) {
                final String output = stream.readFully();
                stream.close();
                return output;
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

}
