package net.gumcode.matratrader.utilities;

import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.gumcode.matratrader.models.Account;
import net.gumcode.matratrader.models.Contract;
import net.gumcode.matratrader.models.Report;
import net.gumcode.matratrader.models.Stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by A. Fauzi Harismawan on 3/30/2016.
 */
public class JSONParser {

    public static Account parseAccountInfo(String content) {
        Account account = new Account();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(content);

            if (rootNode.get("authorize") != null) {
                account.binaryToken = rootNode.get("echo_req").get("authorize").asText();
                account.account = rootNode.get("authorize").get("loginid").asText();
                account.email = rootNode.get("authorize").get("email").asText();
                account.balance = rootNode.get("authorize").get("balance").asDouble();

                return account;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String parseProposal(String content) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(content);

            if (rootNode.get("proposal") != null) {
                return rootNode.get("proposal").get("id").asText();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Contract parseContract(String content) {
        Contract contract = new Contract();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(content);

            if (rootNode.get("buy") != null) {
                contract.id = rootNode.get("buy").get("contract_id").asText();
                contract.longcode = rootNode.get("buy").get("longcode").asText();
                return contract;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Stream parseStream(String content) {
        Stream stream = new Stream();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(content);

            if (rootNode.get("proposal_open_contract") != null) {
                stream.isValidSale = rootNode.get("proposal_open_contract").get("is_valid_to_sell").asInt();
                stream.bidPrice = rootNode.get("proposal_open_contract").get("bid_price").asDouble();
                stream.buyPrice = rootNode.get("proposal_open_contract").get("buy_price").asDouble();
                stream.entrySpot = rootNode.get("proposal_open_contract").get("entry_spot").asDouble();
                stream.currentSpot = rootNode.get("proposal_open_contract").get("current_spot").asDouble();
                stream.entrySpotTime = rootNode.get("proposal_open_contract").get("purchase_time").asLong();
                stream.currentSpotTime = rootNode.get("proposal_open_contract").get("current_spot_time").asLong();
                stream.expireTime = rootNode.get("proposal_open_contract").get("date_expiry").asLong();
                stream.isExpired = rootNode.get("proposal_open_contract").get("is_expired").asInt();

                return stream;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Report> parseReportList(String content) {
        ArrayList<Report> list = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(content);

            Log.d("CONTENT", rootNode.get("profit_table").get("count").asText());
            if (rootNode.get("profit_table") != null) {
                JsonNode array = rootNode.get("profit_table").get("transactions");
                Log.d("CONTENT", rootNode.get("profit_table").asText());
                Log.d("CONTENT", rootNode.get("profit_table").get("transactions").asText());
                for (int i = 0; i < 10; i++) {
                    JsonNode object = array.get(i);
                    Report report = new Report();
                    report.market = object.get("sell_time").asLong();
                    report.buy = object.get("buy_price").asDouble();
                    report.sell = object.get("sell_price").asDouble();
                    Log.d("CONTENT", object.get("contract_id").asText() + " - " + object.get("buy_price").asDouble() + " - " + object.get("sell_price").asDouble());
                    list.add(report);
                }

                return list;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean parseSellResponse(String content) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(content);

            if (rootNode.get("sell") != null) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

//    public static double parsePingResponse(String content) {
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            JsonNode rootNode = mapper.readTree(content);
//
//            if (rootNode.get("ping") != null) {
//                Log.d("PING", rootNode.get("ping").asText());
//                return rootNode.get("debug").get(0).asDouble();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }


    public static Account parseSignInResponse(InputStream inputStream) {
        Account account = new Account();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(inputStream);

            if (rootNode.get("status").asBoolean()) {
                account.id = rootNode.get("message").get(0).get("id").asInt();
                account.email = rootNode.get("message").get(0).get("email").asText();
                account.serverToken = rootNode.get("message").get(0).get("server_token").asText();
                account.validity = rootNode.get("message").get(0).get("validity").asText();
                return account;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
