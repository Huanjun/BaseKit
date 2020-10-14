package com.mp.basekit.wallet;

public class MetaMaskUtil {

    public static class MetaMask {
        public static final String CODE = "ethereum:";
        private String amount = "";
        private String token = "ETH";
        private String decimal;
        private String contractAddr;
        private String address = "";

        public MetaMask(String value) {
            try {
                int index = value.indexOf("?");
                int beginIndex = value.indexOf(":") + 1;
                if (index == -1) {
                    this.address = value.substring(beginIndex);
                } else {
                    this.address = value.substring(beginIndex, index);
                }
                String str = value.substring(index + 1);
                String[] params = str.split("&");
                for (String param : params) {
                    String[] temp = param.split("=");
                    if ("value".equals(temp[0])) {
                        if (temp.length > 1) {
                            this.amount = temp[1];
                        }
                    } else if ("token".equals(temp[0])) {
                        if (temp.length > 1) {
                            this.token = temp[1];
                        }
                    } else if ("contractAddress".equals(temp[0])) {
                        if (temp.length > 1) {
                            this.contractAddr = temp[1];
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        MetaMask(String address, String amount, String name) {
            this.address = address;
            this.amount = amount;
            this.token = name;
        }

        public String getAddress() {
            return this.address;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getContractAddr() {
            return this.contractAddr;
        }

        public String toString() {
            return CODE + address + "?value=" + amount + "&token=" + token + "&contractAddress=" + contractAddr;
        }
    }
}
