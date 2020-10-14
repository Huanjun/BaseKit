package com.mp.basekit.wallet.address;

import com.mp.basekit.util.HexUtils;

public class BaseAddress {
    //0-pkh,1-sh,2-pk,3-pke,4-pks
    public static final int PUBKEY_HASH = 0;
    public static final int SCRIPT_HASH = 1;
    public static final int PUBKEY = 2;
    public static final int PKH_EDWARDS = 3;
    public static final int PKH_SCHNORR = 4;


    public boolean isTest;
    public int scriptType;
    public String hash160;
    public String address;

    public BaseAddress(String address) {
        this.address = address;
        try {
            BaseAddress addr = decode(address);
            isTest = addr.isTest;
            scriptType = addr.scriptType;
            hash160 = addr.hash160;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BaseAddress(int scriptType, boolean isTest, byte[] hash160) {
        this.scriptType = scriptType;
        this.hash160 = HexUtils.toHex(hash160);
        this.isTest = isTest;
        this.address = encode(hash160, isTest, scriptType);
    }

    public String encode(byte[] hash, boolean testNet, int type) {
        return address;
    }

    public BaseAddress decode(String address) throws Exception {
        return this;
    }

}
