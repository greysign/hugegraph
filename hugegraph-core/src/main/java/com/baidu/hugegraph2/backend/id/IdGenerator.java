package com.baidu.hugegraph2.backend.id;

import java.io.UnsupportedEncodingException;

import com.baidu.hugegraph2.type.schema.SchemaType;

public class IdGenerator {

    public static Id generate(SchemaType entry) {
        String id = String.format("%02X:%s", entry.type().code(), entry.name());
        return generate(id);
    }

    public static Id generate(String id) {
        return new StringId(id);
    }

    public static Id generate(long id) {
        return new LongId(id);
    }


    /****************************** id defines ******************************/

    static class StringId implements Id {

        private String id;

        public StringId(String id) {
            this.id = id;
        }

        @Override
        public String asString() {
            return this.id;
        }

        @Override
        public long asLong() {
            return Long.parseLong(this.id);
        }

        @Override
        public byte[] asBytes() {
            try {
                return this.id.getBytes("utf-8");
            } catch (UnsupportedEncodingException e) {
                // TODO: serialize string
                return null;
            }
        }

        @Override
        public int compareTo(Id other) {
            return this.id.compareTo(((StringId) other).id);
        }

        @Override
        public String toString() {
            return this.asString();
        }
    }

    static class LongId implements Id {

        private long id;

        public LongId(long id) {
            this.id = id;
        }

        @Override
        public String asString() {
            return String.valueOf(this.id);
        }

        @Override
        public long asLong() {
            return this.id;
        }

        @Override
        public byte[] asBytes() {
            byte[] bytes = new byte[Long.BYTES];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) (this.id >> i);
            }
            return bytes;
        }

        @Override
        public int compareTo(Id other) {
            long otherId = ((LongId) other).id;
            return Long.compare(this.id, otherId);
        }

        @Override
        public String toString() {
            return this.asString();
        }
    }
}
