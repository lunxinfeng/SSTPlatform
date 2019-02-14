package com.fintech.sst.other.netty;

public class ResponsePay {
    private String authToken;
    private int code;
    private String collectionAccount;
    private String collectionName;
    private String collectionUserId;
    private String define;
    private String money;
    private String orderNo;
    private String payId;
    private String payUrl;
    private String platformOrder;
    private String platformOrderStatus;
    private String remark;
    private String sessionKey;
    private long time;
    private String type;

    public ResponsePay() {
    }

    public boolean equals(Object var1) {
        if (this != var1) {
            if (var1 == null) {
                return false;
            }

            if (this.getClass() != var1.getClass()) {
                return false;
            }

            ResponsePay var2 = (ResponsePay) var1;
            if (this.authToken == null) {
                if (var2.authToken != null) {
                    return false;
                }
            } else if (!this.authToken.equals(var2.authToken)) {
                return false;
            }

            if (this.code != var2.code) {
                return false;
            }

            if (this.collectionAccount == null) {
                if (var2.collectionAccount != null) {
                    return false;
                }
            } else if (!this.collectionAccount.equals(var2.collectionAccount)) {
                return false;
            }

            if (this.collectionName == null) {
                if (var2.collectionName != null) {
                    return false;
                }
            } else if (!this.collectionName.equals(var2.collectionName)) {
                return false;
            }

            if (this.collectionUserId == null) {
                if (var2.collectionUserId != null) {
                    return false;
                }
            } else if (!this.collectionUserId.equals(var2.collectionUserId)) {
                return false;
            }

            if (this.define == null) {
                if (var2.define != null) {
                    return false;
                }
            } else if (!this.define.equals(var2.define)) {
                return false;
            }

            if (this.money == null) {
                if (var2.money != null) {
                    return false;
                }
            } else if (!this.money.equals(var2.money)) {
                return false;
            }

            if (this.orderNo == null) {
                if (var2.orderNo != null) {
                    return false;
                }
            } else if (!this.orderNo.equals(var2.orderNo)) {
                return false;
            }

            if (this.payId == null) {
                if (var2.payId != null) {
                    return false;
                }
            } else if (!this.payId.equals(var2.payId)) {
                return false;
            }

            if (this.payUrl == null) {
                if (var2.payUrl != null) {
                    return false;
                }
            } else if (!this.payUrl.equals(var2.payUrl)) {
                return false;
            }

            if (this.platformOrder == null) {
                if (var2.platformOrder != null) {
                    return false;
                }
            } else if (!this.platformOrder.equals(var2.platformOrder)) {
                return false;
            }

            if (this.platformOrderStatus == null) {
                if (var2.platformOrderStatus != null) {
                    return false;
                }
            } else if (!this.platformOrderStatus.equals(var2.platformOrderStatus)) {
                return false;
            }

            if (this.remark == null) {
                if (var2.remark != null) {
                    return false;
                }
            } else if (!this.remark.equals(var2.remark)) {
                return false;
            }

            if (this.sessionKey == null) {
                if (var2.sessionKey != null) {
                    return false;
                }
            } else if (!this.sessionKey.equals(var2.sessionKey)) {
                return false;
            }

            if (this.time != var2.time) {
                return false;
            }

            if (this.type == null) {
                return var2.type == null;
            } else return this.type.equals(var2.type);
        }

        return true;
    }

    public String getAuthToken() {
        return this.authToken;
    }

    public int getCode() {
        return this.code;
    }

    public String getCollectionAccount() {
        return this.collectionAccount;
    }

    public String getCollectionName() {
        return this.collectionName;
    }

    public String getCollectionUserId() {
        return this.collectionUserId;
    }

    public String getDefine() {
        return this.define;
    }

    public String getMoney() {
        return this.money;
    }

    public String getOrderNo() {
        return this.orderNo;
    }

    public String getPayId() {
        return this.payId;
    }

    public String getPayUrl() {
        return this.payUrl;
    }

    public String getPlatformOrder() {
        return this.platformOrder;
    }

    public String getPlatformOrderStatus() {
        return this.platformOrderStatus;
    }

    public String getRemark() {
        return this.remark;
    }

    public String getSessionKey() {
        return this.sessionKey;
    }

    public long getTime() {
        return this.time;
    }

    public String getType() {
        return this.type;
    }

    public int hashCode() {
        int var14 = 0;
        int var1;
        if (this.authToken == null) {
            var1 = 0;
        } else {
            var1 = this.authToken.hashCode();
        }

        int var15 = this.code;
        int var2;
        if (this.collectionAccount == null) {
            var2 = 0;
        } else {
            var2 = this.collectionAccount.hashCode();
        }

        int var3;
        if (this.collectionName == null) {
            var3 = 0;
        } else {
            var3 = this.collectionName.hashCode();
        }

        int var4;
        if (this.collectionUserId == null) {
            var4 = 0;
        } else {
            var4 = this.collectionUserId.hashCode();
        }

        int var5;
        if (this.define == null) {
            var5 = 0;
        } else {
            var5 = this.define.hashCode();
        }

        int var6;
        if (this.money == null) {
            var6 = 0;
        } else {
            var6 = this.money.hashCode();
        }

        int var7;
        if (this.orderNo == null) {
            var7 = 0;
        } else {
            var7 = this.orderNo.hashCode();
        }

        int var8;
        if (this.payId == null) {
            var8 = 0;
        } else {
            var8 = this.payId.hashCode();
        }

        int var9;
        if (this.payUrl == null) {
            var9 = 0;
        } else {
            var9 = this.payUrl.hashCode();
        }

        int var10;
        if (this.platformOrder == null) {
            var10 = 0;
        } else {
            var10 = this.platformOrder.hashCode();
        }

        int var11;
        if (this.platformOrderStatus == null) {
            var11 = 0;
        } else {
            var11 = this.platformOrderStatus.hashCode();
        }

        int var12;
        if (this.remark == null) {
            var12 = 0;
        } else {
            var12 = this.remark.hashCode();
        }

        int var13;
        if (this.sessionKey == null) {
            var13 = 0;
        } else {
            var13 = this.sessionKey.hashCode();
        }

        int var16 = (int) (this.time ^ this.time >>> 32);
        if (this.type != null) {
            var14 = this.type.hashCode();
        }

        return (((((((((((((((var1 + 31) * 31 + var15) * 31 + var2) * 31 + var3) * 31 + var4) * 31 + var5) * 31 + var6) * 31 + var7) * 31 + var8) * 31 + var9) * 31 + var10) * 31 + var11) * 31 + var12) * 31 + var13) * 31 + var16) * 31 + var14;
    }

    public void setAuthToken(String var1) {
        this.authToken = var1;
    }

    public void setCode(int var1) {
        this.code = var1;
    }

    public void setCollectionAccount(String var1) {
        this.collectionAccount = var1;
    }

    public void setCollectionName(String var1) {
        this.collectionName = var1;
    }

    public void setCollectionUserId(String var1) {
        this.collectionUserId = var1;
    }

    public void setDefine(String var1) {
        this.define = var1;
    }

    public void setMoney(String var1) {
        this.money = var1;
    }

    public void setOrderNo(String var1) {
        this.orderNo = var1;
    }

    public void setPayId(String var1) {
        this.payId = var1;
    }

    public void setPayUrl(String var1) {
        this.payUrl = var1;
    }

    public void setPlatformOrder(String var1) {
        this.platformOrder = var1;
    }

    public void setPlatformOrderStatus(String var1) {
        this.platformOrderStatus = var1;
    }

    public void setRemark(String var1) {
        this.remark = var1;
    }

    public void setSessionKey(String var1) {
        this.sessionKey = var1;
    }

    public void setTime(long var1) {
        this.time = var1;
    }

    public void setType(String var1) {
        this.type = var1;
    }

    public String toString() {
        return "ResponsePay [code=" + this.code + ", define=" + this.define + ", time=" + this.time + ", payId=" + this.payId + ", money=" + this.money + ", remark=" + this.remark + ", type=" + this.type + ", payUrl=" + this.payUrl + ", authToken=" + this.authToken + ", sessionKey=" + this.sessionKey + ", platformOrder=" + this.platformOrder + ", platformOrderStatus=" + this.platformOrderStatus + ", collectionAccount=" + this.collectionAccount + ", collectionName=" + this.collectionName + ", collectionUserId=" + this.collectionUserId + ", orderNo=" + this.orderNo + "]";
    }
}