package com.seven.contract.manage.utils;

import com.alibaba.fastjson.JSON;
import com.seven.contract.manage.conf.BytomProperties;
import io.bytom.api.Compile;
import io.bytom.api.Transaction;
import io.bytom.exception.BytomException;
import io.bytom.http.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 2018/12/27.
 */
public class BytomUtil {

    private static Logger logger = LoggerFactory.getLogger(BytomUtil.class);

    /**
     * 创建客户端连接
     * @return
     * @throws BytomException
     */
    private static Client generateClient() throws BytomException {
        String coreURL = BytomProperties.defalutCoreUrl;
        String accessToken = BytomProperties.accessToken;
        logger.debug("coreURL = {}, accessToken = {}", coreURL, accessToken);
        return new Client(coreURL, accessToken);
    }

    /**
     * 合约编译
     * @param
     * @return
     */
    public static String compile(List<String> signatures) throws BytomException {
        // http发送的json参数数据的对象
        Compile.ContractJson data = new Compile.ContractJson();

        for (String signature : signatures) {
            Compile.Param p = new Compile.Param();
            p.setParam(signature);
            data.push_back(p);
        }
        // 生成合约，几个表示有多少个签名
        data.generateContract(signatures.size()-1);
        //发送数据
        Compile compile = new Compile();
        Client client = BytomUtil.generateClient();
        Compile resp = Compile.compile(client, data);

        logger.debug("resp = {}", JSON.toJSONString(resp));
        logger.debug("resp.program = {}", resp.program);

        return resp.program;
    }

    /**
     * 锁定电子合同币到合约，锁定交易
     * @return
     * @throws BytomException
     */
    public static String locked(String program) throws BytomException {
        Client client = BytomUtil.generateClient();
        // setAccountAlias设置账户的名字   setAssetAlias设置资产的名字   setAmount设置发送多少数量
        // 构造手续费 100000000指1个btm手续费
        Transaction.Action.SpendFromAccount spend = new Transaction.Action.SpendFromAccount().setAccountAlias(BytomProperties.electronicAccont).setAssetAlias("BTM").setAmount(100000000);
        // 构造电子合同币的输入 100000000指发送1个合同币
        Transaction.Action.SpendFromAccount intput = new Transaction.Action.SpendFromAccount().setAccountAlias(BytomProperties.electronicAccont).setAssetAlias(BytomProperties.electronicContract).setAmount(100000000);
        // 构造电子合同币的输出 setControlProgram指编译合约返回的program setAmount是接受多少个合同币，输入多少，接受多少
        Transaction.Action.ControlWithReceiver receiver = new Transaction.Action.ControlWithReceiver().setControlProgram(program).setAssetAlias(BytomProperties.electronicContract).setAmount(100000000);
        Transaction.Template tml = new Transaction.Builder().addAction(spend).addAction(intput).addAction(receiver).build(client);
        // 123456是账户的密码，签名交易
        Transaction.Template singer = new Transaction.SignerBuilder().sign(client, tml, BytomProperties.password);
        logger.info("rawTransaction:" + singer.rawTransaction);
        logger.info("singer:" + singer.toJson());
        // 提交交易
        Transaction.SubmitResponse txs = Transaction.submit(client, singer);
        // 记录tx_id,以及通过tx_id获取到交易，记录输出的control_pragram与编译合约返回的program相同的ID 。在后面解锁交易的时候需要用到这个ID
        logger.info("txs:" + txs.toJson());

        logger.info("after transaction.");

        return txs.tx_id;
    }

    /**
     * 解锁电子合同币到合约账户，解锁交易
     * @return
     * @throws BytomException
     */
    public static String unlock(List<String> publicKeys, String outputId) throws BytomException {
        Client client = BytomUtil.generateClient();

        // arguments是arguments的结构
        List<Transaction.Argument> arguments = new ArrayList<Transaction.Argument>();
        for (String publicKey : publicKeys) {
            //截取公私前64位
            String xpub = publicKey.substring(0, 64);

            // rawData1\rawData2是上面截图中的红色标注的raw_data数据，value是指xpub即公钥的前64位
            Transaction.Argument.RawData rawData = new Transaction.Argument.RawData(xpub);
            // argument1/argument2是红色标注的type、raw_data的部分
            Transaction.Argument argument = new Transaction.Argument(rawData, "data");

            arguments.add(argument);
        }

        // spendUnspent是actions中spend_account_unspent_output的数据块
        Transaction.Action.SpendAccountUnspentOutput spendUnspent = new Transaction.Action.SpendAccountUnspentOutput();
        // setOutputID设置的是锁定交易输出中的ID，输出的controlProgram等于编译合约返回的program
        // 锁定交易通过返回的tx_id获取，可以在前面发送交易后保存下来，这里需要的时候获取一下
        spendUnspent.setOutputID(outputId);
        spendUnspent.setArguments(arguments);
        //手续费，与锁定交易类似
        Transaction.Action.SpendFromAccount spend = new Transaction.Action.SpendFromAccount().setAccountAlias(BytomProperties.electronicAccont).setAssetAlias("BTM").setAmount(100000000);
        // 解锁合同币的接受者，setControlProgram设置接收地址对应的control_program
        Transaction.Action.ControlWithReceiver receiver = new Transaction.Action.ControlWithReceiver().setControlProgram(BytomProperties.controlProgram).setAssetAlias(BytomProperties.electronicContract).setAmount(100000000);
        // build交易
        Transaction.Template tml = new Transaction.Builder().addAction(spendUnspent).addAction(spend).addAction(receiver).build(client);
        //签名交易
        Transaction.Template singer = new Transaction.SignerBuilder().sign(client, tml, BytomProperties.password);
        logger.info("rawTransaction:" + singer.rawTransaction);
        logger.info("singer:" + singer.toJson());
        // 提交交易
        Transaction.SubmitResponse txs = Transaction.submit(client, singer);

        logger.info("txs:" + txs.toJson());

        logger.info("after transaction.");

        return txs.toJson();
    }

    /**
     * 根据tx_id获取交易，并获取输出中的control_program与编译合约返回program相同的ID
     * @return
     * @throws BytomException
     */
    public static String getOutputId(String txId, String program) throws BytomException {
        Client client = BytomUtil.generateClient();
//        String txID = "9ae8172a96387d9f92c69fb504858dbcbadde75dd250946faed476c016f63b4f";
//        String program = "2091c3ea050a9226ca95348ba2ed6f98ab6fd8ec1ce43a7b140476592d4406c4494078ef95aad8aaece42f625b0e566d0c1b763e0a73b7cb0dec2fe10fb5271cfada77cdc9a18a813aacf9bbb2abb1bdfc3372e4568fa19621c1d3fda367542bf303406152734afa31611d0c77714c93242134d2e50e14f8aa31d96722d8debc1a228b8962c27e6b896bdb6623809d72e829ee5ad79eeb718a46a43e27c86226f76d0f74095279557aac697c7bac00c0";

        logger.debug("txId = {}, program = {}", txId, program);
        Transaction.QueryBuilder queryTxById = new Transaction.QueryBuilder();
        queryTxById.setTxId(txId);

        logger.debug("queryTxById is start");
        Transaction tx = queryTxById.get(client);
        logger.debug("queryTxById is end");
        for(Transaction.Output output: tx.outputs){
            if(output.controlProgram.equals(program)) {
                logger.info("outputId = {}", output.id);
                return output.id;
            }

        }

        logger.debug("return outputId is null");

        return null;
    }

}
