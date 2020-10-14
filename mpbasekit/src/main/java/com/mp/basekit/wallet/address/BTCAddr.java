package com.mp.basekit.wallet.address;

import com.mp.basekit.util.HexUtils;
import com.mp.basekit.wallet.SHA256;
import com.mp.basekit.wallet.encrypt.Base58;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class BTCAddr extends BaseAddress {

    //BTC 地址前缀
    private static final int TEST_NET_ADDRESS_SUFFIX = 0x6f;
    private static final int MAIN_NET_ADDRESS_SUFFIX = 0x00;

    private static final int TEST_NET_ADDRESS_P2SH = 0xc4;
    private static final int MAIN_NET_ADDRESS_P2SH = 0x05;


    public BTCAddr(String address) {
        super(address);
    }

    public BTCAddr(int scriptType, boolean isTest, byte[] hash160) {
        super(scriptType, isTest, hash160);
    }

    @Override
    public String encode(byte[] hash, boolean testNet, int type) {
        try {
            byte[] addressBytes = new byte[1 + hash.length + 4];
            //拼接测试网络或正式网络前缀
            addressBytes[0] = (byte) (testNet ? type == PUBKEY_HASH ? TEST_NET_ADDRESS_SUFFIX : TEST_NET_ADDRESS_P2SH :
                    type == PUBKEY_HASH ? MAIN_NET_ADDRESS_SUFFIX : MAIN_NET_ADDRESS_P2SH);

            System.arraycopy(hash, 0, addressBytes, 1, hash.length);
            //进行双 Sha256 运算
            byte[] check = SHA256.doubleSha256(addressBytes, 0, addressBytes.length - 4);

            //将双 Sha256 运算的结果前 4位 拼接到尾部
            System.arraycopy(check, 0, addressBytes, hash.length + 1, 4);

            Arrays.fill(hash, (byte) 0);
            Arrays.fill(check, (byte) 0);
            return Base58.encode(addressBytes);
        } catch (Exception e) {
            return HexUtils.toHex(hash);
        }
    }

    /**
     * 解码DCR地址
     *
     * @param address
     * @return
     */
    @Override
    public BaseAddress decode(String address) throws Exception {
        byte[] addressWithCheckSumAndNetworkCode = Base58.decode(address);
        if (addressWithCheckSumAndNetworkCode[0] != MAIN_NET_ADDRESS_SUFFIX &&
                addressWithCheckSumAndNetworkCode[0] != TEST_NET_ADDRESS_SUFFIX &&
                addressWithCheckSumAndNetworkCode[0] != MAIN_NET_ADDRESS_P2SH &&
                addressWithCheckSumAndNetworkCode[0] != (byte) TEST_NET_ADDRESS_P2SH) {
            throw new RuntimeException("Unknown address type");
        }
        byte[] bareAddress = new byte[20];
        System.arraycopy(addressWithCheckSumAndNetworkCode, 1, bareAddress, 0,
                bareAddress.length);
        MessageDigest digestSha = null;
        try {
            digestSha = MessageDigest.getInstance("SHA-256");
            digestSha.update(addressWithCheckSumAndNetworkCode, 0,
                    addressWithCheckSumAndNetworkCode.length - 4);
            byte[] calculatedDigest = digestSha.digest(digestSha.digest());
            for (int i = 0; i < 4; i++) {
                if (calculatedDigest[i] !=
                        addressWithCheckSumAndNetworkCode[addressWithCheckSumAndNetworkCode
                                .length - 4 + i]) {
                    throw new RuntimeException("Unknown address type");
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Unknown address type");
        }
        int type;
        if (addressWithCheckSumAndNetworkCode[0] == MAIN_NET_ADDRESS_SUFFIX
                || addressWithCheckSumAndNetworkCode[0] == TEST_NET_ADDRESS_SUFFIX) {
            type = PUBKEY_HASH;
        } else {
            type = SCRIPT_HASH;
        }

        return new BaseAddress(type, isTest, bareAddress);
    }

    public static boolean isValid(String address) {
        try {
            byte[] addressWithCheckSumAndNetworkCode = Base58.decode(address);
            if (addressWithCheckSumAndNetworkCode[0] != MAIN_NET_ADDRESS_SUFFIX &&
                    addressWithCheckSumAndNetworkCode[0] != TEST_NET_ADDRESS_SUFFIX &&
                    addressWithCheckSumAndNetworkCode[0] != MAIN_NET_ADDRESS_P2SH &&
                    addressWithCheckSumAndNetworkCode[0] != (byte) TEST_NET_ADDRESS_P2SH) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
