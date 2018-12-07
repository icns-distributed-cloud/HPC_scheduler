package com.icnslab;

import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;
import mousio.etcd4j.responses.EtcdKeysResponse;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alicek106 on 2017-08-03.
 */

public class EtcdConnector {
    private EtcdClient etcd;

    @SuppressWarnings("deprecation")
    public EtcdConnector() {
        etcd = new EtcdClient(URI.create("http://163.180.117.68:2379"), URI.create("http://163.180.117.68:2379"));
        System.out.println(etcd.getVersion());
    }

    public String getRegistryAddr(){return getValue("/nodes/registry");}

    public List<String> listNode(String nodePath){
        List<String> list = new ArrayList<String>();

        try{
            EtcdResponsePromise<EtcdKeysResponse> node = etcd.get(nodePath).send();
            List<EtcdKeysResponse.EtcdNode> etcdNodeList = node.get().node.getNodes();
            for(EtcdKeysResponse.EtcdNode etcdNode : etcdNodeList){
                list.add(etcdNode.key);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }

    public List<String> getWorkerAddr(){
        List<String> nodeList = listNode("/docker/nodes");
        List<String> newList = new ArrayList<String>();

        for(String str : nodeList){
            newList.add("http://" + str.split("/")[3].replace("10.", "163."));
        }

        return newList;
    }

    private String getValue(String keyPath) {
        try {
            EtcdResponsePromise<EtcdKeysResponse> node = etcd.get(keyPath).send();
            return node.get().node.getValue();
        }

        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void closeConnection() {
        try {
            etcd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    public static void main(String[] args) {
        EtcdConnector etcd = new EtcdConnector();
        System.out.println(etcd.getValue("/nodes/builder"));
        System.out.println(etcd.getWorkerAddr());
        etcd.closeConnection();
    }*/

}
