package com.mp.basekit.wallet.address;

import com.mp.basekit.util.HexUtils;
import com.mp.basekit.wallet.Hash;
import com.mp.basekit.wallet.Numeric;

public class ETHAddr extends BaseAddress {

    private static final int ADDRESS_LENGTH_IN_HEX = 40;

    public ETHAddr(String address) {
        super(address);
    }

    public ETHAddr(int scriptType, boolean isTest, byte[] hash160) {
        super(scriptType, isTest, hash160);
    }

    @Override
    public String encode(byte[] hash, boolean testNet, int type) {
        return toChecksumAddress(HexUtils.toHex(hash));
    }

    @Override
    public BaseAddress decode(String address) throws Exception {
        return new BaseAddress(PUBKEY_HASH, true, HexUtils.fromHex(Numeric.cleanHexPrefix(address).toLowerCase()));
    }

    /**
     * Checksum address encoding as per
     * <a href="https://github.com/ethereum/EIPs/blob/master/EIPS/eip-55.md">EIP-55</a>.
     *
     * @param address a valid hex encoded address
     * @return hex encoded checksum address
     */
    public static String toChecksumAddress(String address) {
        String lowercaseAddress = Numeric.cleanHexPrefix(address).toLowerCase();
        String addressHash = Numeric.cleanHexPrefix(Hash.sha3String(lowercaseAddress));

        StringBuilder result = new StringBuilder(lowercaseAddress.length() + 2);

        result.append("0x");

        for (int i = 0; i < lowercaseAddress.length(); i++) {
            if (Integer.parseInt(String.valueOf(addressHash.charAt(i)), 16) >= 8) {
                result.append(String.valueOf(lowercaseAddress.charAt(i)).toUpperCase());
            } else {
                result.append(lowercaseAddress.charAt(i));
            }
        }

        return result.toString();
    }

    public static boolean isValid(String address) {
        try {
            String addressNoPrefix = Numeric.cleanHexPrefix(address);
            return addressNoPrefix.length() == ADDRESS_LENGTH_IN_HEX;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
