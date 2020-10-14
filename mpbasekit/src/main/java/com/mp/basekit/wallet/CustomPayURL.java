package com.mp.basekit.wallet;

public class CustomPayURL {

    private String address;
    private String token;
    private String amount;

    public CustomPayURL(String addr, String amount, String tokenName) {
        setAddress(addr);
        setAmount(amount);
        setToken(tokenName);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getURL(){
        return String.format("%s?amount=%s&tokenName=%s", address, amount, token);
    }

    public CustomPayURL(String value) {
        try {
            int index = value.indexOf("?");
            if (index == -1){
                this.address = value;
            } else {
                int beginIndex = 0;
                this.address = value.substring(beginIndex, index);
                String str = value.substring(index + 1);
                String[] params = str.split("&");
                for (String param : params) {
                    String[] temp = param.split("=");
                    if ("amount".equals(temp[0])) {
                        if (temp.length > 1) {
                            this.amount = temp[1];
                        }
                    } else if ("token".equals(temp[0]) || "tokenName".equals(temp[0])) {
                        if (temp.length > 1) {
                            this.token = temp[1];
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
