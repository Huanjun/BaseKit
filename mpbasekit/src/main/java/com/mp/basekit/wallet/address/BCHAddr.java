package com.mp.basekit.wallet.address;

import com.mp.basekit.util.HexUtils;
import com.mp.basekit.wallet.address.cashaddr.CashAddress;

public class BCHAddr extends BaseAddress {

    public BCHAddr(String address) {
        super(address);
    }

    public BCHAddr(int scriptType, boolean isTest, byte[] hash160) {
        super(scriptType, isTest, hash160);
    }

    @Override
    public String encode(byte[] hash, boolean testNet, int type) {
        return CashAddress.encode(testNet ? CashAddress.PREFIX_TEST : CashAddress.PREFIX_MAIN,
                type == SCRIPT_HASH ? CashAddress.P2SH : CashAddress.P2PKH,
                hash);
    }

    @Override
    public BaseAddress decode(String address) throws Exception {
        CashAddress cash = CashAddress.decode(address);
        return new BaseAddress(cash.scriptType.equals(CashAddress.P2PKH) ? PUBKEY_HASH : SCRIPT_HASH,
                cash.prefix.equals(CashAddress.PREFIX_TEST), HexUtils.fromHex(cash.hash160));
    }

    /**
     * 获取传统格式地址
     *
     * @return
     */
    public String getOldFormat() {
        return new BTCAddr(scriptType, isTest, HexUtils.fromHex(hash160)).address;
    }

    public static boolean isValid(String address) {
        return CashAddress.isValid(address);
    }
}
