package in.shopy.Utils;

import android.widget.TextView;

/**
 * Created by am361000 on 28/01/18.
 */

public class BillDetails {
    public static String comment = "";
    public static String date;
    public static String deliveryaddress;
    public static String from;
    public static String orderid; //
    public static String paymentmode;
    public static String status;

    public static String total;
    public static String deliverycharge;
    public static String discount;
    public static String grandtotal;
    public static String walletamt;
    public static String payable_amount;

    public static String appliedPromo;

    public BillDetails() {

    }

    public static String getAppliedPromo() {
        return appliedPromo;
    }

    public static void setAppliedPromo(String appliedPromo) {
        BillDetails.appliedPromo = appliedPromo;
    }

    public static String getTotal() {
        return total;
    }

    public static void setTotal(String total) {
        BillDetails.total = total;
    }

    public static String getDeliverycharge() {
        return deliverycharge;
    }

    public static void setDeliverycharge(String deliverycharge) {
        BillDetails.deliverycharge = deliverycharge;
    }

    public static String getDiscount() {
        return discount;
    }

    public static void setDiscount(String discount) {
        BillDetails.discount = discount;
    }

    public static String getGrandtotal() {
        return grandtotal;
    }

    public static void setGrandtotal(String grandtotal) {
        BillDetails.grandtotal = grandtotal;
    }

    public static String getWalletamt() {
        return walletamt;
    }

    public static void setWalletamt(String walletamt) {
        BillDetails.walletamt = walletamt;
    }

    public static String getPayable_amount() {
        return payable_amount;
    }

    public static void setPayable_amount(String payable_amount) {
        BillDetails.payable_amount = payable_amount;
    }

    public static String getComment() {
        return comment;
    }

    public static void setComment(String comment) {
        BillDetails.comment = comment;
    }

    public static String getDate() {
        return date;
    }

    public static void setDate(String date) {
        BillDetails.date = date;
    }

    public static String getDeliveryaddress() {
        return deliveryaddress;
    }

    public static void setDeliveryaddress(String deliveryaddress) {
        BillDetails.deliveryaddress = deliveryaddress;
    }

    public static String getFrom() {
        return from;
    }

    public static void setFrom(String from) {
        BillDetails.from = from;
    }

    public static String getOrderid() {
        return orderid;
    }

    public static void setOrderid(String orderid) {
        BillDetails.orderid = orderid;
    }

    public static String getPaymentmode() {
        return paymentmode;
    }

    public static void setPaymentmode(String paymentmode) {
        BillDetails.paymentmode = paymentmode;
    }

    public static String getStatus() {
        return status;
    }

    public static void setStatus(String status) {
        BillDetails.status = status;
    }
}
