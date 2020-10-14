package com.mp.basekit.wallet.address;

import android.text.TextUtils;

import com.mp.basekit.wallet.CustomPayURL;
import com.mp.basekit.wallet.IbanUtls;
import com.mp.basekit.wallet.MToken;
import com.mp.basekit.wallet.MetaMaskUtil;
import com.mp.basekit.wallet.address.cashaddr.CashAddress;
import com.mp.basekit.wallet.model.BitcoinPaymentURI;

/**
 * Created by Administrator on 2018/5/17.
 */

public class AddressUtils {

    public static class QRType {
        static final int QR_ADDRESS = 0;
        static final int QR_BITPAY = 1;
        static final int QR_MATEMASK = 2;
        static final int QR_CUSTOM = 3;
        static final int QR_IBANK = 4;
    }

    public static boolean isValidAddr(String address, String tokenName) {
        switch (tokenName) {
            case MToken.BTC:
                return BTCAddr.isValid(address);
            case MToken.BCH:
                return BCHAddr.isValid(address) || BTCAddr.isValid(address);
            case MToken.ETH:
                return ETHAddr.isValid(address);
            default:
                return !TextUtils.isEmpty(address);
        }
    }

    public static String getAddrToken(String address) {
        if (BTCAddr.isValid(address)) {
            return MToken.BTC;
        }
        if (BCHAddr.isValid(address)) {
            return MToken.BCH;
        }
        if (ETHAddr.isValid(address)) {
            return MToken.ETH;
        }
        return "";
    }

    private static int getQRType(String qrData) {
        if (qrData.startsWith(MetaMaskUtil.MetaMask.CODE)) {
            return QRType.QR_MATEMASK;
        }
        if (qrData.startsWith(BitcoinPaymentURI.SCHEME)) {
            return QRType.QR_BITPAY;
        }
        if (qrData.startsWith(IbanUtls.Iban.IBAN_CODE)) {
            return QRType.QR_IBANK;
        }
        if (qrData.startsWith(CashAddress.PREFIX_MAIN)
                || qrData.startsWith(CashAddress.PREFIX_TEST)) {
            return QRType.QR_CUSTOM;
        }
        if (qrData.contains("?")) {
            return QRType.QR_CUSTOM;
        }

        return QRType.QR_ADDRESS;
    }

    public static AddressDto parseAddress(String qrData) {
        AddressDto address = new AddressDto();
        int type = getQRType(qrData);
        switch (type) {
            case QRType.QR_ADDRESS:
                address.address = qrData;
                address.token = getAddrToken(qrData);
                break;
            case QRType.QR_MATEMASK:
                MetaMaskUtil.MetaMask mask = new MetaMaskUtil.MetaMask(qrData);
                address.address = mask.getAddress();
                address.token = mask.getToken();
                address.amount = mask.getAmount();
                break;
            case QRType.QR_BITPAY:
                BitcoinPaymentURI payment = BitcoinPaymentURI.parse(qrData);
                assert payment != null;
                address.address = payment.getAddress();
                address.token = payment.getToken();
                address.amount = payment.getAmount();
                break;
            case QRType.QR_CUSTOM:
                CustomPayURL customPayURL = new CustomPayURL(qrData);
                address.address = customPayURL.getAddress();
                address.token = customPayURL.getToken();
                address.amount = customPayURL.getAmount();
                break;
            case QRType.QR_IBANK:
                IbanUtls.Iban iban = new IbanUtls.Iban(qrData);
                address.address = iban.getAddress();
                address.token = iban.getToken();
                address.amount = iban.getAmount();
                break;
        }
        return address;
    }

}
