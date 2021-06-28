package com.fabric.fabricrun.controller;

import com.fabric.fabricrun.Untility.FabricClient;
import com.fabric.fabricrun.entity.Property;
import com.fabric.fabricrun.entity.Record;
import com.fabric.fabricrun.repository.RecordPepository;
import org.hyperledger.fabric.sdk.TransactionRequest;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminProperty {
    @Value("${adminMsp}")
    private String adminMsp;

    @Autowired
    private RecordPepository recordPepository;

    @ResponseBody
    @PostMapping(value = "/property/add")
    public Map addproperty(Property property, HttpServletRequest request) throws InvalidArgumentException, ProposalException, TransactionException {
        HashMap map = new HashMap();
        HttpSession session = request.getSession();
        FabricClient fabricClient = (FabricClient) session.getAttribute("fabricClient");
        if (!fabricClient.hfClient.getUserContext().getMspId().equals(adminMsp)) {
            map.put("code",403);
            map.put("msg","No access");
            return map;
        }
        String[] args = {property.getId(),property.getName(), String.valueOf(property.getNum())};
        map = (HashMap) fabricClient.invoke_channel(TransactionRequest.Type.GO_LANG,"userinfo","addgoods",args);
        return map;
    }

    @ResponseBody
    @PostMapping(value = "/property/delete")
    public Map deleteproperty(@RequestParam("property") String property, HttpServletRequest request) throws InvalidArgumentException, ProposalException, TransactionException {
        HashMap map = new HashMap();
        HttpSession session = request.getSession();
        FabricClient fabricClient = (FabricClient) session.getAttribute("fabricClient");
        if (!fabricClient.hfClient.getUserContext().getMspId().equals(adminMsp)) {
            map.put("code",403);
            map.put("msg","No access");
            return map;
        }
        String[] args = {property};
        map = (HashMap) fabricClient.invoke_channel(TransactionRequest.Type.GO_LANG,"userinfo","deletegoods",args);
        return map;
    }

    @ResponseBody
    @PostMapping(value = "/apply/look")
    public Map lookapply(HttpServletRequest request) {
        HashMap map = new HashMap();
        HttpSession session = request.getSession();
        FabricClient fabricClient = (FabricClient) session.getAttribute("fabricClient");
        if (!fabricClient.hfClient.getUserContext().getMspId().equals(adminMsp)) {
            map.put("code",403);
            map.put("msg","No access");
            return map;
        }
        List<Record> records = recordPepository.findRecordsByAdmin(fabricClient.hfClient.getUserContext().getName());
        map.put("code",200);
        map.put("applys",records);
        return map;
    }

    @ResponseBody
    @PostMapping(value = "/apply/check")
    public Map checkapply(@RequestParam("username") String username,
                          @RequestParam("property") String property,
                          @RequestParam("opinion")  String oponion,
                          HttpServletRequest request
    ) throws TransactionException, ProposalException, InvalidArgumentException {
        HashMap map = new HashMap();
        HttpSession session = request.getSession();
        FabricClient fabricClient = (FabricClient) session.getAttribute("fabricClient");
        if (!fabricClient.hfClient.getUserContext().getMspId().equals(adminMsp)) {
            map.put("code",403);
            map.put("msg","No access");
            return map;
        }
        String[] args = {username,property,oponion};
        map = (HashMap) fabricClient.invoke_channel(TransactionRequest.Type.GO_LANG,"userinfo","checklend",args);
        if (map.get("code").equals(200)) {
            recordPepository.deleteRecordByAdminAndUserAndProperty(fabricClient.hfClient.getUserContext().getName(),username,property);
            map.put("msg","审核成功");
        }
        return map;
    }

}
