package com.fabric.fabricrun.Untility;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class FabricClient {

    private static final Logger log = LoggerFactory.getLogger(com.fabric.fabricrun.Untility.FabricClient.class);

    public HFClient hfClient;
    public Channel channel;

    public FabricClient(UserContext userContext) throws IllegalAccessException, InvocationTargetException, InvalidArgumentException, InstantiationException, NoSuchMethodException, CryptoException, ClassNotFoundException {
        hfClient = HFClient.createNewInstance();
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        hfClient.setCryptoSuite(cryptoSuite);
        hfClient.setUserContext(userContext);

    }
    public Channel createChannel(String channelName, Orderer order, String Pathtx) throws InvalidArgumentException, TransactionException, IOException {
        ChannelConfiguration channelConfiguration = new ChannelConfiguration(new File(Pathtx));
        return hfClient.newChannel(channelName,order,channelConfiguration,hfClient.getChannelConfigurationSignature(channelConfiguration,hfClient.getUserContext()));
    }

    public Orderer getOrder(String name, String grpcURL) throws InvalidArgumentException {
        Properties properties = new Properties();
        properties.setProperty("pemFile","G:\\fabric-sdk\\src\\main\\resources\\crypto-config\\ordererOrganizations\\example.com\\tlsca\\tlsca.example.com-cert.pem");
        Orderer order = hfClient.newOrderer(name,grpcURL,properties);
        return order;
    }

    public Peer getPeer(String name, String grpcURL,String tlsPath) throws InvalidArgumentException {
        Properties properties = new Properties();
        properties.setProperty("pemFile",tlsPath);
        Peer peer = hfClient.newPeer(name,grpcURL,properties);
        return peer;
    }

    public Channel getChannel(String name) throws InvalidArgumentException, TransactionException {
        try {
            channel = hfClient.newChannel(name);
        }catch (InvalidArgumentException e) {
            return channel;
        }
        return channel;
    }

    public void addchannel(List<Peer> peers,Orderer orderer,String channelName) throws InvalidArgumentException, TransactionException {
        channel = getChannel(channelName);
        channel.addOrderer(orderer);
        for(Peer p : peers) {
            channel.addPeer(p);
        }
        channel.initialize();
    }

    public void installChaincode(TransactionRequest.Type lang, String chaincodeName, String chaincodeVersion ,String chaincodeLocation,String chaincodePath, List<Peer> peers) throws InvalidArgumentException, ProposalException, ProposalException {
        InstallProposalRequest installProposalRequest = hfClient.newInstallProposalRequest();
        ChaincodeID.Builder builder = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion);
        installProposalRequest.setChaincodeLanguage(lang);
        installProposalRequest.setChaincodeID(builder.build());
        installProposalRequest.setChaincodeSourceLocation(new File(chaincodeLocation));
        installProposalRequest.setChaincodePath(chaincodePath);
        Collection<ProposalResponse> responses =  hfClient.sendInstallProposal(installProposalRequest,peers);
        for(ProposalResponse response:responses){
            if(response.getStatus().getStatus()==200){
                log.info("{} installed sucess",response.getPeer().getName());
            }else{
                log.error("{} installed fail",response.getMessage());
            }
        }
    }

    public void initChaincode(String channelName, TransactionRequest.Type lang, String chaincodeName, String chaincodeVersion, Orderer order, Peer peer, String funcName, String args[]) throws TransactionException, ProposalException, InvalidArgumentException {
        channel = getChannel(channelName);
        channel.addPeer(peer);
        channel.addOrderer(order);
        channel.initialize();
        InstantiateProposalRequest instantiateProposalRequest = hfClient.newInstantiationProposalRequest();
        instantiateProposalRequest.setArgs(args);
        instantiateProposalRequest.setFcn(funcName);
        instantiateProposalRequest.setChaincodeLanguage(lang);
        ChaincodeID.Builder builder = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion);
        instantiateProposalRequest.setChaincodeID(builder.build());
        Collection<ProposalResponse> responses =  channel.sendInstantiationProposal(instantiateProposalRequest);
        for(ProposalResponse response:responses){
            if(response.getStatus().getStatus()==200){
                log.info("{} init sucess",response.getPeer().getName());
            }else{
                log.error("{} init fail",response.getMessage());
            }
        }
        channel.sendTransaction(responses);
    }

    public void upgradeChaincode(String channelName, TransactionRequest.Type lang, String chaincodeName, String chaincodeVersion, Orderer order, Peer peer, String funcName, String args[]) throws TransactionException, ProposalException, InvalidArgumentException, IOException, ChaincodeEndorsementPolicyParseException, ChaincodeEndorsementPolicyParseException {
        channel = getChannel(channelName);
        channel.addPeer(peer);
        channel.addOrderer(order);
        channel.initialize();
        UpgradeProposalRequest upgradeProposalRequest = hfClient.newUpgradeProposalRequest();
        upgradeProposalRequest.setArgs(args);
        upgradeProposalRequest.setFcn(funcName);
        upgradeProposalRequest.setChaincodeLanguage(lang);
        ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
        chaincodeEndorsementPolicy.fromYamlFile(new File("E:\\chaincode\\src\\basicInfo\\chaincodeendorsementpolicy.yaml"));
        upgradeProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
        ChaincodeID.Builder builder = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion);
        upgradeProposalRequest.setChaincodeID(builder.build());
        Collection<ProposalResponse> responses =  channel.sendUpgradeProposal(upgradeProposalRequest);
        for(ProposalResponse response:responses){
            if(response.getStatus().getStatus()==200){
                log.info("{} upgrade sucess",response.getPeer().getName());
            }else{
                log.error("{} upgrade fail",response.getMessage());
            }
        }
        channel.sendTransaction(responses);
    }

    public Map invoke(String channelName, TransactionRequest.Type lang, String chaincodeName, Orderer order, List<Peer> peers, String funcName, String args[]) throws TransactionException, ProposalException, InvalidArgumentException {
        channel = getChannel(channelName);
        channel.addOrderer(order);
        for(Peer p : peers) {
            try {
                channel.addPeer(p);
            }catch (InvalidArgumentException e) {}
        }if (!channel.isInitialized()) {
            channel.initialize();
        }
        HashMap map = new HashMap();
        String s;
        TransactionProposalRequest transactionProposalRequest = hfClient.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeLanguage(lang);
        transactionProposalRequest.setArgs(args);
        transactionProposalRequest.setFcn(funcName);
        ChaincodeID.Builder builder = ChaincodeID.newBuilder().setName(chaincodeName);
        transactionProposalRequest.setChaincodeID(builder.build());
        Collection<ProposalResponse> responses = channel.sendTransactionProposal(transactionProposalRequest,peers);
        for(ProposalResponse response:responses){
            if(response.getStatus().getStatus()==200){
                log.info("{} invoke proposal {} sucess",response.getPeer().getName(),funcName);
                s=new String(response.getProposalResponse().getResponse().getPayload().toByteArray());
                map.put("code",response.getStatus().getStatus());
            }else{
                String logArgs[] = {response.getMessage(),funcName,response.getPeer().getName()};
                log.error("{} invoke proposal {} fail on {}",logArgs);
                s = response.getMessage();
                map.put("code",response.getStatus().getStatus());
                map.put("msg",s);
            }
        }
        channel.sendTransaction(responses);
        return map;
    }

    public Map invoke_channel(TransactionRequest.Type lang, String chaincodeName, String funcName, String args[]) throws TransactionException, ProposalException, InvalidArgumentException {
        HashMap map = new HashMap();
        String s;
        TransactionProposalRequest transactionProposalRequest = hfClient.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeLanguage(lang);
        transactionProposalRequest.setArgs(args);
        transactionProposalRequest.setFcn(funcName);
        ChaincodeID.Builder builder = ChaincodeID.newBuilder().setName(chaincodeName);
        transactionProposalRequest.setChaincodeID(builder.build());
        Collection<ProposalResponse> responses = channel.sendTransactionProposal(transactionProposalRequest,channel.getPeers());
        for(ProposalResponse response:responses){
            if(response.getStatus().getStatus()==200){
                log.info("{} invoke proposal {} sucess",response.getPeer().getName(),funcName);
                s=new String(response.getProposalResponse().getResponse().getPayload().toByteArray());
                map.put("code",response.getStatus().getStatus());
            }else{
                String logArgs[] = {response.getMessage(),funcName,response.getPeer().getName()};
                log.error("{} invoke proposal {} fail on {}",logArgs);
                s = response.getMessage();
                map.put("code",response.getStatus().getStatus());
                map.put("msg",s);
            }
        }
        channel.sendTransaction(responses);
        return map;
    }

    public Map query_channel(TransactionRequest.Type lang, String chaincodeName, String funcName, String args[]) throws TransactionException, InvalidArgumentException, ProposalException {
        HashMap map = new HashMap();
        QueryByChaincodeRequest queryByChaincodeRequest = hfClient.newQueryProposalRequest();
        ChaincodeID.Builder builder = ChaincodeID.newBuilder().setName(chaincodeName);
        queryByChaincodeRequest.setChaincodeID(builder.build());
        queryByChaincodeRequest.setArgs(args);
        queryByChaincodeRequest.setFcn(funcName);
        queryByChaincodeRequest.setChaincodeLanguage(lang);
        Collection<ProposalResponse> responses = channel.queryByChaincode(queryByChaincodeRequest);
        for (ProposalResponse response : responses) {
            if (response.getStatus().getStatus() == 200) {
                log.info("data is {}", response.getProposalResponse().getResponse().getPayload());
                map.put("code",response.getStatus().getStatus());
                map.put("data",new String(response.getProposalResponse().getResponse().getPayload().toByteArray()));
                return map;
            } else {
                log.error("data get error {}", response.getMessage());
                map.put(response.getStatus().getStatus(),response.getMessage());
                return map;
            }
        }
        return null;
    }

    public Map queryChaincode(List<Peer> peers, String channelName, TransactionRequest.Type lang, String chaincodeName, String funcName, String args[]) throws TransactionException, InvalidArgumentException, ProposalException {
        channel = getChannel(channelName);
        for(Peer p : peers) {
            channel.addPeer(p);
        }
        channel.initialize();
        HashMap map = new HashMap();
        QueryByChaincodeRequest queryByChaincodeRequest = hfClient.newQueryProposalRequest();
        ChaincodeID.Builder builder = ChaincodeID.newBuilder().setName(chaincodeName);
        queryByChaincodeRequest.setChaincodeID(builder.build());
        queryByChaincodeRequest.setArgs(args);
        queryByChaincodeRequest.setFcn(funcName);
        queryByChaincodeRequest.setChaincodeLanguage(lang);
        Collection<ProposalResponse> responses = channel.queryByChaincode(queryByChaincodeRequest);
        for (ProposalResponse response : responses) {
            if (response.getStatus().getStatus() == 200) {
                log.info("data is {}", response.getProposalResponse().getResponse().getPayload());
                map.put(response.getStatus().getStatus(),new String(response.getProposalResponse().getResponse().getPayload().toByteArray()));
                return map;
            } else {
                log.error("data get error {}", response.getMessage());
                map.put(response.getStatus().getStatus(),response.getMessage());
                return map;
            }
        }
        return null;
    }

    public String query(List<Peer> peers, String channelName, TransactionRequest.Type lang, String chaincodeName, String funcName, String args[]) throws TransactionException, InvalidArgumentException, ProposalException {
        channel = getChannel(channelName);
        for(Peer p : peers) {
            channel.addPeer(p);
        }
        channel.initialize();
        String s = new String();
        QueryByChaincodeRequest queryByChaincodeRequest = hfClient.newQueryProposalRequest();
        ChaincodeID.Builder builder = ChaincodeID.newBuilder().setName(chaincodeName);
        queryByChaincodeRequest.setChaincodeID(builder.build());
        queryByChaincodeRequest.setArgs(args);
        queryByChaincodeRequest.setFcn(funcName);
        queryByChaincodeRequest.setChaincodeLanguage(lang);
        Collection<ProposalResponse> responses = channel.queryByChaincode(queryByChaincodeRequest);
        for (ProposalResponse response : responses) {
            if (response.getStatus().getStatus() == 200) {
                log.info("data is {}", response.getProposalResponse().getResponse().getPayload());
                s=new String(response.getProposalResponse().getResponse().getPayload().toByteArray());
                if(s.equals("{\"identity\":\"unkown\"}"))
                    return null;
                return s;
            } else {
                log.error("data get error {}", response.getMessage());
                return null;
            }
        }
        return null;
    }

    public String channel_query(Channel channel1, TransactionRequest.Type lang, String chaincodeName, String funcName, String args[]) throws TransactionException, InvalidArgumentException, ProposalException, UnsupportedEncodingException {
        channel = channel1;
        String s = new String();
        QueryByChaincodeRequest queryByChaincodeRequest = hfClient.newQueryProposalRequest();
        ChaincodeID.Builder builder = ChaincodeID.newBuilder().setName(chaincodeName);
        queryByChaincodeRequest.setChaincodeID(builder.build());
        queryByChaincodeRequest.setArgs(args);
        queryByChaincodeRequest.setFcn(funcName);
        queryByChaincodeRequest.setChaincodeLanguage(lang);
        Collection<ProposalResponse> responses = channel.queryByChaincode(queryByChaincodeRequest);
        for (ProposalResponse response : responses) {
            if (response.getStatus().getStatus() == 200) {
                log.info("data is {}", response.getProposalResponse().getResponse().getPayload());
                s=new String(response.getProposalResponse().getResponse().getPayload().toByteArray(),"utf-8");
                if(s.equals("{\"identity\":\"unkown\"}"))
                    return null;
                return s;
            } else {
                log.error("data get error {}", response.getMessage());
                return null;
            }
        }
        return null;
    }

    public String channel_invoke(Channel channel1, TransactionRequest.Type lang, String chaincodeName, String funcName, String args[]) throws ProposalException, InvalidArgumentException {
        channel = channel1;
        String s = new String();
        TransactionProposalRequest transactionProposalRequest = hfClient.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeLanguage(lang);
        transactionProposalRequest.setArgs(args);
        transactionProposalRequest.setFcn(funcName);
        ChaincodeID.Builder builder = ChaincodeID.newBuilder().setName(chaincodeName);
        transactionProposalRequest.setChaincodeID(builder.build());
        Collection<ProposalResponse> responses = channel.sendTransactionProposal(transactionProposalRequest,channel1.getPeers());
        for(ProposalResponse response:responses){
            if(response.getStatus().getStatus()==200){
                log.info("{} invoke proposal {} sucess",response.getPeer().getName(),funcName);
                s=new String(response.getProposalResponse().getResponse().getPayload().toByteArray());
            }else{
                String logArgs[] = {response.getMessage(),funcName,response.getPeer().getName()};
                log.error("{} invoke proposal {} fail on {}",logArgs);
                s = null;
            }
        }
        channel.sendTransaction(responses);
        return s;
    }
}
