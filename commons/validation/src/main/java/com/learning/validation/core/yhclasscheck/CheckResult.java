package com.learning.validation.core.yhclasscheck;

/**
 * @author WangWei
 * @version v 1.0
 * @description
 * @date 2024-06-21
 **/
public class CheckResult {
    private boolean valited;
    private String msg;
    private String failPty;

    public CheckResult(boolean valited) {
        this.valited = valited;
    }

    public CheckResult(boolean valited, String msg) {
        this.valited = valited;
        this.msg = msg;
    }

    public CheckResult(boolean valited, String failPty, String msg) {
        this.valited = valited;
        this.failPty = failPty;
        this.msg = msg;
    }

    public static CheckResult success() {
        return new CheckResult(true);
    }

    public static CheckResult fail(String msg) {
        return new CheckResult(false, msg);
    }

    public static CheckResult fail(String failPty, String msg) {
        return new CheckResult(false, failPty, msg);
    }

    public boolean isValited() {
        return this.valited;
    }

    public String getMsg() {
        return this.msg;
    }

    public String getFailPty() {
        return this.failPty;
    }

    public void setValited(final boolean valited) {
        this.valited = valited;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public void setFailPty(final String failPty) {
        this.failPty = failPty;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof CheckResult)) {
            return false;
        } else {
            CheckResult other = (CheckResult)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.isValited() != other.isValited()) {
                return false;
            } else {
                Object this$msg = this.getMsg();
                Object other$msg = other.getMsg();
                if (this$msg == null) {
                    if (other$msg != null) {
                        return false;
                    }
                } else if (!this$msg.equals(other$msg)) {
                    return false;
                }

                Object this$failPty = this.getFailPty();
                Object other$failPty = other.getFailPty();
                if (this$failPty == null) {
                    if (other$failPty != null) {
                        return false;
                    }
                } else if (!this$failPty.equals(other$failPty)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof CheckResult;
    }

    public int hashCode() {
        int PRIME = true;
        int result = 1;
        int result = result * 59 + (this.isValited() ? 79 : 97);
        Object $msg = this.getMsg();
        result = result * 59 + ($msg == null ? 43 : $msg.hashCode());
        Object $failPty = this.getFailPty();
        result = result * 59 + ($failPty == null ? 43 : $failPty.hashCode());
        return result;
    }

    public String toString() {
        return "CheckResult(valited=" + this.isValited() + ", msg=" + this.getMsg() + ", failPty=" + this.getFailPty() + ")";
    }
}