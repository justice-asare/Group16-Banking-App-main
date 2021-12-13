package com.trade.bankapp.exceptions;

public class InvalidOrder extends RuntimeException{
    public InvalidOrder(String message){ super(message); }
}
