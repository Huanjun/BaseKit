package com.mp.basekit.wallet.address.cashaddr;


@SuppressWarnings("serial")
public class AddressFormatException extends IllegalArgumentException {
    public AddressFormatException() {
        super();
    }

    public AddressFormatException(String message) {
        super(message);
    }
}