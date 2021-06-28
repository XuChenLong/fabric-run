package com.fabric.fabricrun.controller;

import com.fabric.fabricrun.Untility.FabricClient;
import com.fabric.fabricrun.entity.Adminmessage;
import com.fabric.fabricrun.entity.Record;
import com.fabric.fabricrun.repository.AdminRepository;
import com.fabric.fabricrun.repository.RecordPepository;
import org.hyperledger.fabric.sdk.TransactionRequest;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserProperty {
    @Value("${userMsp}")
    private String userMsp;
    @Autowired
    RecordPepository propertyPepository;

    @Autowired
    private RecordPepository recordPepository;

    @Autowired
    AdminRepository adminRepository;

    @ResponseBody
    @PostMapping(value = "/property/lend")
    public Map lendproperty(
            @RequestParam("aname") String aname,
            @RequestParam("id") String id,
            @RequestParam("num") int num,
            HttpServletRequest request) {
        HashMap map = new HashMap();
        HttpSession session = request.getSession();
        FabricClient fabricClient = (FabricClient) session.getAttribute("fabricClient");
        if (!fabricClient.hfClient.getUserContext().getMspId().equals(userMsp)) {
            map.put("code",403);
            map.put("msg","No access");
            return map;
        }
        Record record = propertyPepository.findRecordByAdminAndUserAndProperty(aname,fabricClient.hfClient.getUserContext().getName(),id);
        if (record == null) {
            String[] args = {aname, id, String.valueOf(num)};
            try {
                map = (HashMap) fabricClient.invoke_channel(TransactionRequest.Type.GO_LANG,"userinfo","lend",args);
            }catch (Exception e) {
                map.put("code",500);
                map.put("msg","申请失败，检查内容正确性");
                return map;
            }
            if (500 == (int) map.get("code")){
                map.put("code",500);
                map.put("msg","申请失败，检查内容正确性");
                return map;
            }
            record = new Record();
            record.setNum(num);
            record.setAdmin(aname);
            record.setUser(fabricClient.hfClient.getUserContext().getName());
            record.setProperty(id);
            propertyPepository.save(record);
            return map;
        }else {
            map.put("code",500);
            map.put("msg","申请已经提交");
            return map;
        }
    }

    @ResponseBody
    @PostMapping(value = "/property/return")
    public Map returnproperty(
            @RequestParam("property") String property,
            HttpServletRequest request
    ) throws TransactionException, ProposalException, InvalidArgumentException {
        HashMap map = new HashMap();
        HttpSession session = request.getSession();
        FabricClient fabricClient = (FabricClient) session.getAttribute("fabricClient");
        String[] args = {property};
        map = (HashMap) fabricClient.invoke_channel(TransactionRequest.Type.GO_LANG,"userinfo","return",args);
        return map;
    }

    @ResponseBody
    @PostMapping(value = "/property/lenddelete")
    public Map lenddelete(
            @RequestParam("owner") String owner,
            @RequestParam("property") String property,
            HttpServletRequest request
    ) throws TransactionException, ProposalException, InvalidArgumentException {
        HashMap map = new HashMap();
        HttpSession session = request.getSession();
        FabricClient fabricClient = (FabricClient) session.getAttribute("fabricClient");
        String[] args = {property};
        map = (HashMap) fabricClient.invoke_channel(TransactionRequest.Type.GO_LANG,"userinfo","lenddelete",args);
        if (map.get("code").equals(200)) {
            recordPepository.deleteRecordByAdminAndUserAndProperty(owner,fabricClient.hfClient.getUserContext().getName(),property);
        }
        return map;
    }

    @ResponseBody
    @PostMapping(value = "/property/look")
    public String myproperty(HttpServletRequest request) throws TransactionException, ProposalException, InvalidArgumentException {
        HashMap map = new HashMap();
        HttpSession session = request.getSession();
        FabricClient fabricClient = (FabricClient) session.getAttribute("fabricClient");
        String[] args = {fabricClient.hfClient.getUserContext().getName()};
        map = (HashMap) fabricClient.query_channel(TransactionRequest.Type.GO_LANG,"userinfo","querygoods",args);
        return (String) map.get("data");
    }

    @ResponseBody
    @PostMapping(value = "/apply/delete")
    public Map applydelete(@RequestParam("property") String property,HttpServletRequest request) throws TransactionException, ProposalException, InvalidArgumentException {
        HashMap map = new HashMap();
        HttpSession session = request.getSession();
        FabricClient fabricClient = (FabricClient) session.getAttribute("fabricClient");
        String[] args = {property};
        map = (HashMap) fabricClient.invoke_channel(TransactionRequest.Type.GO_LANG,"userinfo","lenddelete",args);
        return map;
    }

    @ResponseBody
    @GetMapping(value = "property/search")
    public String propertysearch(
            @RequestParam("admin") String admin,
            @RequestParam("property") String property,
            @RequestParam("state") String state,
            HttpServletRequest request
    ) throws TransactionException, ProposalException, InvalidArgumentException, UnsupportedEncodingException {
        HashMap map = new HashMap();
        HashMap map1 = new HashMap();
        HttpSession session = request.getSession();
        FabricClient fabricClient = (FabricClient) session.getAttribute("fabricClient");
        String[] args = {URLDecoder.decode(admin,"utf-8")};
        map = (HashMap) fabricClient.query_channel(TransactionRequest.Type.GO_LANG,"userinfo","querygoods",args);
        return (String) map.get("data");
    }

    @ResponseBody
    @PostMapping(value = "/findAdmin")
    public List<Adminmessage> Admins() {
        List<Adminmessage> admins;
        admins = adminRepository.findAllName();
        return admins;
    }
}
