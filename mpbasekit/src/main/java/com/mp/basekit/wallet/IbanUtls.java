package com.mp.basekit.wallet;

import com.mp.basekit.wallet.address.ETHAddr;

import java.math.BigInteger;

/**
 * Created by Administrator on 2018/5/16.
 */
public class IbanUtls {

    public static class Iban {
        public static final String IBAN_CODE = "iban:";
        private String amount;
        private String token;
        String iban;

        public Iban(String value) {
            try {
                int index = value.indexOf("?");
                int beginIndex = value.indexOf(":") + 1;
                this.iban = value.substring(beginIndex, index);
                String str = value.substring(index + 1);
                String[] params = str.split("&");
                for (String param : params) {
                    String[] temp = param.split("=");
                    if ("amount".equals(temp[0])) {
                        if (temp.length > 1) {
                            this.amount = temp[1];
                        }
                    } else if ("token".equals(temp[0])) {
                        if (temp.length > 1) {
                            this.token = temp[1];
                        }
                    }
                }
            } catch (Exception e) {
                throw new Error("Not support data!");
            }
        }

        Iban(String iban, String amount, String name) {
            this.iban = iban;
            this.amount = amount;
            this.token = name;
        }

        public boolean isDirect() {
            return this.iban.length() == 34 || this.iban.length() == 35;
        }

        public String getAddress() {
            if (this.isDirect()) {
                String base36 = this.iban.substring(4);
                BigInteger asBn = new BigInteger(base36, 36);
                return ETHAddr.toChecksumAddress(Numeric.toHexStringWithPrefixZeroPadded(asBn, 40));
            }
            return "";
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

        public boolean isIndirect() {
            return iban.length() == 20;
        }

        public String client() {
            return isIndirect() ? iban.substring(11) : "";
        }

        public String toString() {
            return IBAN_CODE + iban + "?amount=" + amount + "&token=" + token;
        }
    }

    private static String leftPad(String string, int bytes) {
        String result = string;
        while (result.length() < bytes * 2) {
            result = "0" + result;
        }
        return result;
    }

    /**
     * Prepare an IBAN for mod 97 computation by moving the first 4 chars to the end and transforming the letters to
     * numbers (A = 10, B = 11, ..., Z = 35), as specified in ISO13616.
     *
     * @param {String} iban the IBAN
     * @method iso13616Prepare
     * @returns {String} the prepared IBAN
     */
    private static String iso13616Prepare(String iban) {
        char A = "A".charAt(0);
        char Z = "Z".charAt(0);

        iban = iban.toUpperCase();
        iban = iban.substring(4) + iban.substring(0, 4);
        String[] a = iban.split("");
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i < a.length; i++) {
            char code = a[i].charAt(0);
            if (code >= A && code <= Z) {
                sb.append(code - A + 10);
            } else {
                sb.append(a[i]);
            }
        }
        return sb.toString();
    }

    /**
     * Calculates the MOD 97 10 of the passed IBAN as specified in ISO7064.
     *
     * @param iban
     * @returns {Number}
     */
    private static int mod9710(String iban) {
        String remainder = iban;
        String block;

        while (remainder.length() >= 9) {
            if (remainder.length() < 9) {
                throw new Error("Not support data!");
            }
            block = remainder.substring(0, 9);
            remainder = Integer.valueOf(block, 10) % 97 + remainder.substring(block.length());
        }

        return Integer.valueOf(remainder, 10) % 97;
    }

    public static Iban fromAddress(String address, String amount, String token) {
        if (!ETHAddr.isValid(address)) {
            throw new Error("Provided address is not a valid address: " + address);
        }
        address = address.replace("0x", "").replace("0X", "");
        BigInteger asBn = new BigInteger(address, 16);
        String base36 = asBn.toString(36);
        String padded = leftPad(base36, 15);
        return fromBban(padded.toUpperCase(), amount, token);
    }

    /**
     * Convert the passed BBAN to an IBAN for this country specification.
     * Please note that <i>"generation of the IBAN shall be the exclusive responsibility of the bank/branch servicing the account"</i>.
     * This method implements the preferred algorithm described in http://en.wikipedia.org/wiki/International_Bank_Account_Number#Generating_IBAN_check_digits
     *
     * @param amount
     * @param bban   the BBAN to convert to IBAN  @method fromBban
     * @returns {Iban} the IBAN object
     */
    private static Iban fromBban(String bban, String amount, String token) {
        String countryCode = "XE";

        int remainder = mod9710(iso13616Prepare(countryCode + "00" + bban));
        String ban = "0" + (98 - remainder);
        String checkDigit = ban.substring(ban.length() - 2);
        return new Iban(countryCode + checkDigit + bban, amount, token);
    }
}
