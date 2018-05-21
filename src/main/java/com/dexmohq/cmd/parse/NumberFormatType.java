package com.dexmohq.cmd.parse;

public enum NumberFormatType implements AbstractNumberFormatType {
    DECIMAL() {
        @Override
        public Integer parseInternal(String in) {
            return Integer.parseInt(in);
        }
    },

    HEX() {
        @Override
        public Integer parseInternal(String in) {
            return Integer.decode(in);
        }
    }

}
