package com.yanlong.im.utils.socket;
public final class MsgBean {
    private MsgBean() {}
    public static void registerAllExtensions(
            com.google.protobuf.ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(
            com.google.protobuf.ExtensionRegistry registry) {
        registerAllExtensions(
                (com.google.protobuf.ExtensionRegistryLite) registry);
    }
    /**
     * <pre>
     * 消息类型
     * </pre>
     *
     * Protobuf enum {@code MessageType}
     */
    public enum MessageType
            implements com.google.protobuf.ProtocolMessageEnum {
        /**
         * <pre>
         * 普通聊天消息
         * </pre>
         *
         * <code>CHAT = 0;</code>
         */
        CHAT(0),
        /**
         * <pre>
         * 图片消息
         * </pre>
         *
         * <code>IMAGE = 1;</code>
         */
        IMAGE(1),
        /**
         * <pre>
         * 红包消息
         * </pre>
         *
         * <code>RED_ENVELOPER = 2;</code>
         */
        RED_ENVELOPER(2),
        /**
         * <pre>
         * 领取红包消息
         * </pre>
         *
         * <code>RECEIVE_RED_ENVELOPER = 3;</code>
         */
        RECEIVE_RED_ENVELOPER(3),
        /**
         * <pre>
         * 转账消息
         * </pre>
         *
         * <code>TRANSFER = 4;</code>
         */
        TRANSFER(4),
        /**
         * <pre>
         * 戳一下消息
         * </pre>
         *
         * <code>STAMP = 5;</code>
         */
        STAMP(5),
        /**
         * <pre>
         * 名片消息
         * </pre>
         *
         * <code>BUSINESS_CARD = 6;</code>
         */
        BUSINESS_CARD(6),
        /**
         * <pre>
         * 请求加好友消息
         * </pre>
         *
         * <code>REQUEST_FRIEND = 100;</code>
         */
        REQUEST_FRIEND(100),
        /**
         * <pre>
         * 接收好友请求
         * </pre>
         *
         * <code>ACCEPT_BE_FRIENDS = 101;</code>
         */
        ACCEPT_BE_FRIENDS(101),
        /**
         * <pre>
         *删除好友消息
         * </pre>
         *
         * <code>REMOVE_FRIEND = 102;</code>
         */
        REMOVE_FRIEND(102),
        /**
         * <pre>
         * 请求入群
         * </pre>
         *
         * <code>REQUEST_GROUP = 103;</code>
         */
        REQUEST_GROUP(103),
        /**
         * <pre>
         * 接受入群请求
         * </pre>
         *
         * <code>ACCEPT_BE_GROUP = 104;</code>
         */
        ACCEPT_BE_GROUP(104),
        /**
         * <pre>
         * 删除群成员
         * </pre>
         *
         * <code>REMOVE_GROUP_MEMBER = 105;</code>
         */
        REMOVE_GROUP_MEMBER(105),
        /**
         * <pre>
         * 转让群
         * </pre>
         *
         * <code>CHANGE_GROUP_MASTER = 106;</code>
         */
        CHANGE_GROUP_MASTER(106),
        /**
         * <pre>
         * 修改群名信息
         * </pre>
         *
         * <code>CHANGE_GROUP_NAME = 107;</code>
         */
        CHANGE_GROUP_NAME(107),
        /**
         * <pre>
         * 修改群公告
         * </pre>
         *
         * <code>CHANGE_GROUP_ANNOUNCEMENT = 108;</code>
         */
        CHANGE_GROUP_ANNOUNCEMENT(108),
        /**
         * <pre>
         * 销毁群
         * </pre>
         *
         * <code>DESTROY_GROUP = 109;</code>
         */
        DESTROY_GROUP(109),
        /**
         * <pre>
         * 退群
         * </pre>
         *
         * <code>OUT_GROUP = 110;</code>
         */
        OUT_GROUP(110),
        /**
         * <pre>
         * 登录冲突消息
         * </pre>
         *
         * <code>CONFLICT = 200;</code>
         */
        CONFLICT(200),
        UNRECOGNIZED(-1),
        ;

        /**
         * <pre>
         * 普通聊天消息
         * </pre>
         *
         * <code>CHAT = 0;</code>
         */
        public static final int CHAT_VALUE = 0;
        /**
         * <pre>
         * 图片消息
         * </pre>
         *
         * <code>IMAGE = 1;</code>
         */
        public static final int IMAGE_VALUE = 1;
        /**
         * <pre>
         * 红包消息
         * </pre>
         *
         * <code>RED_ENVELOPER = 2;</code>
         */
        public static final int RED_ENVELOPER_VALUE = 2;
        /**
         * <pre>
         * 领取红包消息
         * </pre>
         *
         * <code>RECEIVE_RED_ENVELOPER = 3;</code>
         */
        public static final int RECEIVE_RED_ENVELOPER_VALUE = 3;
        /**
         * <pre>
         * 转账消息
         * </pre>
         *
         * <code>TRANSFER = 4;</code>
         */
        public static final int TRANSFER_VALUE = 4;
        /**
         * <pre>
         * 戳一下消息
         * </pre>
         *
         * <code>STAMP = 5;</code>
         */
        public static final int STAMP_VALUE = 5;
        /**
         * <pre>
         * 名片消息
         * </pre>
         *
         * <code>BUSINESS_CARD = 6;</code>
         */
        public static final int BUSINESS_CARD_VALUE = 6;
        /**
         * <pre>
         * 请求加好友消息
         * </pre>
         *
         * <code>REQUEST_FRIEND = 100;</code>
         */
        public static final int REQUEST_FRIEND_VALUE = 100;
        /**
         * <pre>
         * 接收好友请求
         * </pre>
         *
         * <code>ACCEPT_BE_FRIENDS = 101;</code>
         */
        public static final int ACCEPT_BE_FRIENDS_VALUE = 101;
        /**
         * <pre>
         *删除好友消息
         * </pre>
         *
         * <code>REMOVE_FRIEND = 102;</code>
         */
        public static final int REMOVE_FRIEND_VALUE = 102;
        /**
         * <pre>
         * 请求入群
         * </pre>
         *
         * <code>REQUEST_GROUP = 103;</code>
         */
        public static final int REQUEST_GROUP_VALUE = 103;
        /**
         * <pre>
         * 接受入群请求
         * </pre>
         *
         * <code>ACCEPT_BE_GROUP = 104;</code>
         */
        public static final int ACCEPT_BE_GROUP_VALUE = 104;
        /**
         * <pre>
         * 删除群成员
         * </pre>
         *
         * <code>REMOVE_GROUP_MEMBER = 105;</code>
         */
        public static final int REMOVE_GROUP_MEMBER_VALUE = 105;
        /**
         * <pre>
         * 转让群
         * </pre>
         *
         * <code>CHANGE_GROUP_MASTER = 106;</code>
         */
        public static final int CHANGE_GROUP_MASTER_VALUE = 106;
        /**
         * <pre>
         * 修改群名信息
         * </pre>
         *
         * <code>CHANGE_GROUP_NAME = 107;</code>
         */
        public static final int CHANGE_GROUP_NAME_VALUE = 107;
        /**
         * <pre>
         * 修改群公告
         * </pre>
         *
         * <code>CHANGE_GROUP_ANNOUNCEMENT = 108;</code>
         */
        public static final int CHANGE_GROUP_ANNOUNCEMENT_VALUE = 108;
        /**
         * <pre>
         * 销毁群
         * </pre>
         *
         * <code>DESTROY_GROUP = 109;</code>
         */
        public static final int DESTROY_GROUP_VALUE = 109;
        /**
         * <pre>
         * 退群
         * </pre>
         *
         * <code>OUT_GROUP = 110;</code>
         */
        public static final int OUT_GROUP_VALUE = 110;
        /**
         * <pre>
         * 登录冲突消息
         * </pre>
         *
         * <code>CONFLICT = 200;</code>
         */
        public static final int CONFLICT_VALUE = 200;


        public final int getNumber() {
            if (this == UNRECOGNIZED) {
                throw new java.lang.IllegalArgumentException(
                        "Can't get the number of an unknown enum value.");
            }
            return value;
        }

        /**
         * @deprecated Use {@link #forNumber(int)} instead.
         */
        @java.lang.Deprecated
        public static MessageType valueOf(int value) {
            return forNumber(value);
        }

        public static MessageType forNumber(int value) {
            switch (value) {
                case 0: return CHAT;
                case 1: return IMAGE;
                case 2: return RED_ENVELOPER;
                case 3: return RECEIVE_RED_ENVELOPER;
                case 4: return TRANSFER;
                case 5: return STAMP;
                case 6: return BUSINESS_CARD;
                case 100: return REQUEST_FRIEND;
                case 101: return ACCEPT_BE_FRIENDS;
                case 102: return REMOVE_FRIEND;
                case 103: return REQUEST_GROUP;
                case 104: return ACCEPT_BE_GROUP;
                case 105: return REMOVE_GROUP_MEMBER;
                case 106: return CHANGE_GROUP_MASTER;
                case 107: return CHANGE_GROUP_NAME;
                case 108: return CHANGE_GROUP_ANNOUNCEMENT;
                case 109: return DESTROY_GROUP;
                case 110: return OUT_GROUP;
                case 200: return CONFLICT;
                default: return null;
            }
        }

        public static com.google.protobuf.Internal.EnumLiteMap<MessageType>
        internalGetValueMap() {
            return internalValueMap;
        }
        private static final com.google.protobuf.Internal.EnumLiteMap<
                MessageType> internalValueMap =
                new com.google.protobuf.Internal.EnumLiteMap<MessageType>() {
                    public MessageType findValueByNumber(int number) {
                        return MessageType.forNumber(number);
                    }
                };

        public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
            return getDescriptor().getValues().get(ordinal());
        }
        public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
            return getDescriptor();
        }
        public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
            return MsgBean.getDescriptor().getEnumTypes().get(0);
        }

        private static final MessageType[] VALUES = values();

        public static MessageType valueOf(
                com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
                throw new java.lang.IllegalArgumentException(
                        "EnumValueDescriptor is not for this type.");
            }
            if (desc.getIndex() == -1) {
                return UNRECOGNIZED;
            }
            return VALUES[desc.getIndex()];
        }

        private final int value;

        private MessageType(int value) {
            this.value = value;
        }

        // @@protoc_insertion_point(enum_scope:MessageType)
    }

    /**
     * <pre>
     * 消息拒发原因
     * </pre>
     *
     * Protobuf enum {@code RejectType}
     */
    public enum RejectType
            implements com.google.protobuf.ProtocolMessageEnum {
        /**
         * <pre>
         * 接受
         * </pre>
         *
         * <code>ACCEPTED = 0;</code>
         */
        ACCEPTED(0),
        /**
         * <pre>
         * 陌生人、黑名单、非群成员
         * </pre>
         *
         * <code>NOT_FRIENDS_OR_GROUP_MEMBER = 1;</code>
         */
        NOT_FRIENDS_OR_GROUP_MEMBER(1),
        /**
         * <pre>
         * 接收方存储空间不足
         * </pre>
         *
         * <code>NO_SPACE = 8;</code>
         */
        NO_SPACE(8),
        /**
         * <pre>
         * 服务器内部错误
         * </pre>
         *
         * <code>SERVICE_UNAVAILABLE = 100;</code>
         */
        SERVICE_UNAVAILABLE(100),
        UNRECOGNIZED(-1),
        ;

        /**
         * <pre>
         * 接受
         * </pre>
         *
         * <code>ACCEPTED = 0;</code>
         */
        public static final int ACCEPTED_VALUE = 0;
        /**
         * <pre>
         * 陌生人、黑名单、非群成员
         * </pre>
         *
         * <code>NOT_FRIENDS_OR_GROUP_MEMBER = 1;</code>
         */
        public static final int NOT_FRIENDS_OR_GROUP_MEMBER_VALUE = 1;
        /**
         * <pre>
         * 接收方存储空间不足
         * </pre>
         *
         * <code>NO_SPACE = 8;</code>
         */
        public static final int NO_SPACE_VALUE = 8;
        /**
         * <pre>
         * 服务器内部错误
         * </pre>
         *
         * <code>SERVICE_UNAVAILABLE = 100;</code>
         */
        public static final int SERVICE_UNAVAILABLE_VALUE = 100;


        public final int getNumber() {
            if (this == UNRECOGNIZED) {
                throw new java.lang.IllegalArgumentException(
                        "Can't get the number of an unknown enum value.");
            }
            return value;
        }

        /**
         * @deprecated Use {@link #forNumber(int)} instead.
         */
        @java.lang.Deprecated
        public static RejectType valueOf(int value) {
            return forNumber(value);
        }

        public static RejectType forNumber(int value) {
            switch (value) {
                case 0: return ACCEPTED;
                case 1: return NOT_FRIENDS_OR_GROUP_MEMBER;
                case 8: return NO_SPACE;
                case 100: return SERVICE_UNAVAILABLE;
                default: return null;
            }
        }

        public static com.google.protobuf.Internal.EnumLiteMap<RejectType>
        internalGetValueMap() {
            return internalValueMap;
        }
        private static final com.google.protobuf.Internal.EnumLiteMap<
                RejectType> internalValueMap =
                new com.google.protobuf.Internal.EnumLiteMap<RejectType>() {
                    public RejectType findValueByNumber(int number) {
                        return RejectType.forNumber(number);
                    }
                };

        public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
            return getDescriptor().getValues().get(ordinal());
        }
        public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
            return getDescriptor();
        }
        public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
            return MsgBean.getDescriptor().getEnumTypes().get(1);
        }

        private static final RejectType[] VALUES = values();

        public static RejectType valueOf(
                com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
                throw new java.lang.IllegalArgumentException(
                        "EnumValueDescriptor is not for this type.");
            }
            if (desc.getIndex() == -1) {
                return UNRECOGNIZED;
            }
            return VALUES[desc.getIndex()];
        }

        private final int value;

        private RejectType(int value) {
            this.value = value;
        }

        // @@protoc_insertion_point(enum_scope:RejectType)
    }

    public interface ChatMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:ChatMessage)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <pre>
         * 消息内容
         * </pre>
         *
         * <code>string msg = 1;</code>
         */
        java.lang.String getMsg();
        /**
         * <pre>
         * 消息内容
         * </pre>
         *
         * <code>string msg = 1;</code>
         */
        com.google.protobuf.ByteString
        getMsgBytes();
    }
    /**
     * <pre>
     * 普通消息
     * </pre>
     *
     * Protobuf type {@code ChatMessage}
     */
    public  static final class ChatMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:ChatMessage)
            ChatMessageOrBuilder {
        // Use ChatMessage.newBuilder() to construct.
        private ChatMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }
        private ChatMessage() {
            msg_ = "";
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }
        private ChatMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            int mutable_bitField0_ = 0;
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 10: {
                            java.lang.String s = input.readStringRequireUtf8();

                            msg_ = s;
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                makeExtensionsImmutable();
            }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return MsgBean.internal_static_ChatMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return MsgBean.internal_static_ChatMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            MsgBean.ChatMessage.class, MsgBean.ChatMessage.Builder.class);
        }

        public static final int MSG_FIELD_NUMBER = 1;
        private volatile java.lang.Object msg_;
        /**
         * <pre>
         * 消息内容
         * </pre>
         *
         * <code>string msg = 1;</code>
         */
        public java.lang.String getMsg() {
            java.lang.Object ref = msg_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                msg_ = s;
                return s;
            }
        }
        /**
         * <pre>
         * 消息内容
         * </pre>
         *
         * <code>string msg = 1;</code>
         */
        public com.google.protobuf.ByteString
        getMsgBytes() {
            java.lang.Object ref = msg_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                msg_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            if (!getMsgBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 1, msg_);
            }
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (!getMsgBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, msg_);
            }
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MsgBean.ChatMessage)) {
                return super.equals(obj);
            }
            MsgBean.ChatMessage other = (MsgBean.ChatMessage) obj;

            boolean result = true;
            result = result && getMsg()
                    .equals(other.getMsg());
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (37 * hash) + MSG_FIELD_NUMBER;
            hash = (53 * hash) + getMsg().hashCode();
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static MsgBean.ChatMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.ChatMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.ChatMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.ChatMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.ChatMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.ChatMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.ChatMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.ChatMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.ChatMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }
        public static MsgBean.ChatMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.ChatMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.ChatMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MsgBean.ChatMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        /**
         * <pre>
         * 普通消息
         * </pre>
         *
         * Protobuf type {@code ChatMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:ChatMessage)
                MsgBean.ChatMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_ChatMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_ChatMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.ChatMessage.class, MsgBean.ChatMessage.Builder.class);
            }

            // Construct using MsgBean.ChatMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }
            public Builder clear() {
                super.clear();
                msg_ = "";

                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return MsgBean.internal_static_ChatMessage_descriptor;
            }

            public MsgBean.ChatMessage getDefaultInstanceForType() {
                return MsgBean.ChatMessage.getDefaultInstance();
            }

            public MsgBean.ChatMessage build() {
                MsgBean.ChatMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public MsgBean.ChatMessage buildPartial() {
                MsgBean.ChatMessage result = new MsgBean.ChatMessage(this);
                result.msg_ = msg_;
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof MsgBean.ChatMessage) {
                    return mergeFrom((MsgBean.ChatMessage)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MsgBean.ChatMessage other) {
                if (other == MsgBean.ChatMessage.getDefaultInstance()) return this;
                if (!other.getMsg().isEmpty()) {
                    msg_ = other.msg_;
                    onChanged();
                }
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                MsgBean.ChatMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (MsgBean.ChatMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private java.lang.Object msg_ = "";
            /**
             * <pre>
             * 消息内容
             * </pre>
             *
             * <code>string msg = 1;</code>
             */
            public java.lang.String getMsg() {
                java.lang.Object ref = msg_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    msg_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }
            /**
             * <pre>
             * 消息内容
             * </pre>
             *
             * <code>string msg = 1;</code>
             */
            public com.google.protobuf.ByteString
            getMsgBytes() {
                java.lang.Object ref = msg_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    msg_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }
            /**
             * <pre>
             * 消息内容
             * </pre>
             *
             * <code>string msg = 1;</code>
             */
            public Builder setMsg(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                msg_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 消息内容
             * </pre>
             *
             * <code>string msg = 1;</code>
             */
            public Builder clearMsg() {

                msg_ = getDefaultInstance().getMsg();
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 消息内容
             * </pre>
             *
             * <code>string msg = 1;</code>
             */
            public Builder setMsgBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                msg_ = value;
                onChanged();
                return this;
            }
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:ChatMessage)
        }

        // @@protoc_insertion_point(class_scope:ChatMessage)
        private static final MsgBean.ChatMessage DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new MsgBean.ChatMessage();
        }

        public static MsgBean.ChatMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<ChatMessage>
                PARSER = new com.google.protobuf.AbstractParser<ChatMessage>() {
            public ChatMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new ChatMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<ChatMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<ChatMessage> getParserForType() {
            return PARSER;
        }

        public MsgBean.ChatMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface ImageMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:ImageMessage)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <code>string url = 1;</code>
         */
        java.lang.String getUrl();
        /**
         * <code>string url = 1;</code>
         */
        com.google.protobuf.ByteString
        getUrlBytes();
    }
    /**
     * <pre>
     * 图片消息
     * </pre>
     *
     * Protobuf type {@code ImageMessage}
     */
    public  static final class ImageMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:ImageMessage)
            ImageMessageOrBuilder {
        // Use ImageMessage.newBuilder() to construct.
        private ImageMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }
        private ImageMessage() {
            url_ = "";
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }
        private ImageMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            int mutable_bitField0_ = 0;
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 10: {
                            java.lang.String s = input.readStringRequireUtf8();

                            url_ = s;
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                makeExtensionsImmutable();
            }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return MsgBean.internal_static_ImageMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return MsgBean.internal_static_ImageMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            MsgBean.ImageMessage.class, MsgBean.ImageMessage.Builder.class);
        }

        public static final int URL_FIELD_NUMBER = 1;
        private volatile java.lang.Object url_;
        /**
         * <code>string url = 1;</code>
         */
        public java.lang.String getUrl() {
            java.lang.Object ref = url_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                url_ = s;
                return s;
            }
        }
        /**
         * <code>string url = 1;</code>
         */
        public com.google.protobuf.ByteString
        getUrlBytes() {
            java.lang.Object ref = url_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                url_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            if (!getUrlBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 1, url_);
            }
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (!getUrlBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, url_);
            }
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MsgBean.ImageMessage)) {
                return super.equals(obj);
            }
            MsgBean.ImageMessage other = (MsgBean.ImageMessage) obj;

            boolean result = true;
            result = result && getUrl()
                    .equals(other.getUrl());
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (37 * hash) + URL_FIELD_NUMBER;
            hash = (53 * hash) + getUrl().hashCode();
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static MsgBean.ImageMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.ImageMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.ImageMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.ImageMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.ImageMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.ImageMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.ImageMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.ImageMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.ImageMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }
        public static MsgBean.ImageMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.ImageMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.ImageMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MsgBean.ImageMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        /**
         * <pre>
         * 图片消息
         * </pre>
         *
         * Protobuf type {@code ImageMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:ImageMessage)
                MsgBean.ImageMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_ImageMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_ImageMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.ImageMessage.class, MsgBean.ImageMessage.Builder.class);
            }

            // Construct using MsgBean.ImageMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }
            public Builder clear() {
                super.clear();
                url_ = "";

                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return MsgBean.internal_static_ImageMessage_descriptor;
            }

            public MsgBean.ImageMessage getDefaultInstanceForType() {
                return MsgBean.ImageMessage.getDefaultInstance();
            }

            public MsgBean.ImageMessage build() {
                MsgBean.ImageMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public MsgBean.ImageMessage buildPartial() {
                MsgBean.ImageMessage result = new MsgBean.ImageMessage(this);
                result.url_ = url_;
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof MsgBean.ImageMessage) {
                    return mergeFrom((MsgBean.ImageMessage)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MsgBean.ImageMessage other) {
                if (other == MsgBean.ImageMessage.getDefaultInstance()) return this;
                if (!other.getUrl().isEmpty()) {
                    url_ = other.url_;
                    onChanged();
                }
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                MsgBean.ImageMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (MsgBean.ImageMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private java.lang.Object url_ = "";
            /**
             * <code>string url = 1;</code>
             */
            public java.lang.String getUrl() {
                java.lang.Object ref = url_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    url_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }
            /**
             * <code>string url = 1;</code>
             */
            public com.google.protobuf.ByteString
            getUrlBytes() {
                java.lang.Object ref = url_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    url_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }
            /**
             * <code>string url = 1;</code>
             */
            public Builder setUrl(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                url_ = value;
                onChanged();
                return this;
            }
            /**
             * <code>string url = 1;</code>
             */
            public Builder clearUrl() {

                url_ = getDefaultInstance().getUrl();
                onChanged();
                return this;
            }
            /**
             * <code>string url = 1;</code>
             */
            public Builder setUrlBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                url_ = value;
                onChanged();
                return this;
            }
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:ImageMessage)
        }

        // @@protoc_insertion_point(class_scope:ImageMessage)
        private static final MsgBean.ImageMessage DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new MsgBean.ImageMessage();
        }

        public static MsgBean.ImageMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<ImageMessage>
                PARSER = new com.google.protobuf.AbstractParser<ImageMessage>() {
            public ImageMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new ImageMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<ImageMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<ImageMessage> getParserForType() {
            return PARSER;
        }

        public MsgBean.ImageMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface RedEnvelopeMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:RedEnvelopeMessage)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <pre>
         * 红包id
         * </pre>
         *
         * <code>string id = 1;</code>
         */
        java.lang.String getId();
        /**
         * <pre>
         * 红包id
         * </pre>
         *
         * <code>string id = 1;</code>
         */
        com.google.protobuf.ByteString
        getIdBytes();

        /**
         * <pre>
         * 红包类型
         * </pre>
         *
         * <code>.RedEnvelopeMessage.RedEnvelopeType re_type = 2;</code>
         */
        int getReTypeValue();
        /**
         * <pre>
         * 红包类型
         * </pre>
         *
         * <code>.RedEnvelopeMessage.RedEnvelopeType re_type = 2;</code>
         */
        MsgBean.RedEnvelopeMessage.RedEnvelopeType getReType();

        /**
         * <pre>
         * 备注信息
         * </pre>
         *
         * <code>string comment = 3;</code>
         */
        java.lang.String getComment();
        /**
         * <pre>
         * 备注信息
         * </pre>
         *
         * <code>string comment = 3;</code>
         */
        com.google.protobuf.ByteString
        getCommentBytes();

        /**
         * <pre>
         *红包种类
         * </pre>
         *
         * <code>.RedEnvelopeMessage.RedEnvelopeStyle style = 4;</code>
         */
        int getStyleValue();
        /**
         * <pre>
         *红包种类
         * </pre>
         *
         * <code>.RedEnvelopeMessage.RedEnvelopeStyle style = 4;</code>
         */
        MsgBean.RedEnvelopeMessage.RedEnvelopeStyle getStyle();
    }
    /**
     * <pre>
     * 红包消息
     * </pre>
     *
     * Protobuf type {@code RedEnvelopeMessage}
     */
    public  static final class RedEnvelopeMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:RedEnvelopeMessage)
            RedEnvelopeMessageOrBuilder {
        // Use RedEnvelopeMessage.newBuilder() to construct.
        private RedEnvelopeMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }
        private RedEnvelopeMessage() {
            id_ = "";
            reType_ = 0;
            comment_ = "";
            style_ = 0;
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }
        private RedEnvelopeMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            int mutable_bitField0_ = 0;
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 10: {
                            java.lang.String s = input.readStringRequireUtf8();

                            id_ = s;
                            break;
                        }
                        case 16: {
                            int rawValue = input.readEnum();

                            reType_ = rawValue;
                            break;
                        }
                        case 26: {
                            java.lang.String s = input.readStringRequireUtf8();

                            comment_ = s;
                            break;
                        }
                        case 32: {
                            int rawValue = input.readEnum();

                            style_ = rawValue;
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                makeExtensionsImmutable();
            }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return MsgBean.internal_static_RedEnvelopeMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return MsgBean.internal_static_RedEnvelopeMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            MsgBean.RedEnvelopeMessage.class, MsgBean.RedEnvelopeMessage.Builder.class);
        }

        /**
         * Protobuf enum {@code RedEnvelopeMessage.RedEnvelopeType}
         */
        public enum RedEnvelopeType
                implements com.google.protobuf.ProtocolMessageEnum {
            /**
             * <pre>
             * 支付宝红包
             * </pre>
             *
             * <code>ALIPAY = 0;</code>
             */
            ALIPAY(0),
            /**
             * <pre>
             * 魔方红包
             * </pre>
             *
             * <code>MFPAY = 1;</code>
             */
            MFPAY(1),
            UNRECOGNIZED(-1),
            ;

            /**
             * <pre>
             * 支付宝红包
             * </pre>
             *
             * <code>ALIPAY = 0;</code>
             */
            public static final int ALIPAY_VALUE = 0;
            /**
             * <pre>
             * 魔方红包
             * </pre>
             *
             * <code>MFPAY = 1;</code>
             */
            public static final int MFPAY_VALUE = 1;


            public final int getNumber() {
                if (this == UNRECOGNIZED) {
                    throw new java.lang.IllegalArgumentException(
                            "Can't get the number of an unknown enum value.");
                }
                return value;
            }

            /**
             * @deprecated Use {@link #forNumber(int)} instead.
             */
            @java.lang.Deprecated
            public static RedEnvelopeType valueOf(int value) {
                return forNumber(value);
            }

            public static RedEnvelopeType forNumber(int value) {
                switch (value) {
                    case 0: return ALIPAY;
                    case 1: return MFPAY;
                    default: return null;
                }
            }

            public static com.google.protobuf.Internal.EnumLiteMap<RedEnvelopeType>
            internalGetValueMap() {
                return internalValueMap;
            }
            private static final com.google.protobuf.Internal.EnumLiteMap<
                    RedEnvelopeType> internalValueMap =
                    new com.google.protobuf.Internal.EnumLiteMap<RedEnvelopeType>() {
                        public RedEnvelopeType findValueByNumber(int number) {
                            return RedEnvelopeType.forNumber(number);
                        }
                    };

            public final com.google.protobuf.Descriptors.EnumValueDescriptor
            getValueDescriptor() {
                return getDescriptor().getValues().get(ordinal());
            }
            public final com.google.protobuf.Descriptors.EnumDescriptor
            getDescriptorForType() {
                return getDescriptor();
            }
            public static final com.google.protobuf.Descriptors.EnumDescriptor
            getDescriptor() {
                return MsgBean.RedEnvelopeMessage.getDescriptor().getEnumTypes().get(0);
            }

            private static final RedEnvelopeType[] VALUES = values();

            public static RedEnvelopeType valueOf(
                    com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
                if (desc.getType() != getDescriptor()) {
                    throw new java.lang.IllegalArgumentException(
                            "EnumValueDescriptor is not for this type.");
                }
                if (desc.getIndex() == -1) {
                    return UNRECOGNIZED;
                }
                return VALUES[desc.getIndex()];
            }

            private final int value;

            private RedEnvelopeType(int value) {
                this.value = value;
            }

            // @@protoc_insertion_point(enum_scope:RedEnvelopeMessage.RedEnvelopeType)
        }

        /**
         * Protobuf enum {@code RedEnvelopeMessage.RedEnvelopeStyle}
         */
        public enum RedEnvelopeStyle
                implements com.google.protobuf.ProtocolMessageEnum {
            /**
             * <pre>
             *普通
             * </pre>
             *
             * <code>NORMAL = 0;</code>
             */
            NORMAL(0),
            /**
             * <pre>
             *拼手气
             * </pre>
             *
             * <code>LUCK = 1;</code>
             */
            LUCK(1),
            UNRECOGNIZED(-1),
            ;

            /**
             * <pre>
             *普通
             * </pre>
             *
             * <code>NORMAL = 0;</code>
             */
            public static final int NORMAL_VALUE = 0;
            /**
             * <pre>
             *拼手气
             * </pre>
             *
             * <code>LUCK = 1;</code>
             */
            public static final int LUCK_VALUE = 1;


            public final int getNumber() {
                if (this == UNRECOGNIZED) {
                    throw new java.lang.IllegalArgumentException(
                            "Can't get the number of an unknown enum value.");
                }
                return value;
            }

            /**
             * @deprecated Use {@link #forNumber(int)} instead.
             */
            @java.lang.Deprecated
            public static RedEnvelopeStyle valueOf(int value) {
                return forNumber(value);
            }

            public static RedEnvelopeStyle forNumber(int value) {
                switch (value) {
                    case 0: return NORMAL;
                    case 1: return LUCK;
                    default: return null;
                }
            }

            public static com.google.protobuf.Internal.EnumLiteMap<RedEnvelopeStyle>
            internalGetValueMap() {
                return internalValueMap;
            }
            private static final com.google.protobuf.Internal.EnumLiteMap<
                    RedEnvelopeStyle> internalValueMap =
                    new com.google.protobuf.Internal.EnumLiteMap<RedEnvelopeStyle>() {
                        public RedEnvelopeStyle findValueByNumber(int number) {
                            return RedEnvelopeStyle.forNumber(number);
                        }
                    };

            public final com.google.protobuf.Descriptors.EnumValueDescriptor
            getValueDescriptor() {
                return getDescriptor().getValues().get(ordinal());
            }
            public final com.google.protobuf.Descriptors.EnumDescriptor
            getDescriptorForType() {
                return getDescriptor();
            }
            public static final com.google.protobuf.Descriptors.EnumDescriptor
            getDescriptor() {
                return MsgBean.RedEnvelopeMessage.getDescriptor().getEnumTypes().get(1);
            }

            private static final RedEnvelopeStyle[] VALUES = values();

            public static RedEnvelopeStyle valueOf(
                    com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
                if (desc.getType() != getDescriptor()) {
                    throw new java.lang.IllegalArgumentException(
                            "EnumValueDescriptor is not for this type.");
                }
                if (desc.getIndex() == -1) {
                    return UNRECOGNIZED;
                }
                return VALUES[desc.getIndex()];
            }

            private final int value;

            private RedEnvelopeStyle(int value) {
                this.value = value;
            }

            // @@protoc_insertion_point(enum_scope:RedEnvelopeMessage.RedEnvelopeStyle)
        }

        public static final int ID_FIELD_NUMBER = 1;
        private volatile java.lang.Object id_;
        /**
         * <pre>
         * 红包id
         * </pre>
         *
         * <code>string id = 1;</code>
         */
        public java.lang.String getId() {
            java.lang.Object ref = id_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                id_ = s;
                return s;
            }
        }
        /**
         * <pre>
         * 红包id
         * </pre>
         *
         * <code>string id = 1;</code>
         */
        public com.google.protobuf.ByteString
        getIdBytes() {
            java.lang.Object ref = id_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                id_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        public static final int RE_TYPE_FIELD_NUMBER = 2;
        private int reType_;
        /**
         * <pre>
         * 红包类型
         * </pre>
         *
         * <code>.RedEnvelopeMessage.RedEnvelopeType re_type = 2;</code>
         */
        public int getReTypeValue() {
            return reType_;
        }
        /**
         * <pre>
         * 红包类型
         * </pre>
         *
         * <code>.RedEnvelopeMessage.RedEnvelopeType re_type = 2;</code>
         */
        public MsgBean.RedEnvelopeMessage.RedEnvelopeType getReType() {
            MsgBean.RedEnvelopeMessage.RedEnvelopeType result = MsgBean.RedEnvelopeMessage.RedEnvelopeType.valueOf(reType_);
            return result == null ? MsgBean.RedEnvelopeMessage.RedEnvelopeType.UNRECOGNIZED : result;
        }

        public static final int COMMENT_FIELD_NUMBER = 3;
        private volatile java.lang.Object comment_;
        /**
         * <pre>
         * 备注信息
         * </pre>
         *
         * <code>string comment = 3;</code>
         */
        public java.lang.String getComment() {
            java.lang.Object ref = comment_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                comment_ = s;
                return s;
            }
        }
        /**
         * <pre>
         * 备注信息
         * </pre>
         *
         * <code>string comment = 3;</code>
         */
        public com.google.protobuf.ByteString
        getCommentBytes() {
            java.lang.Object ref = comment_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                comment_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        public static final int STYLE_FIELD_NUMBER = 4;
        private int style_;
        /**
         * <pre>
         *红包种类
         * </pre>
         *
         * <code>.RedEnvelopeMessage.RedEnvelopeStyle style = 4;</code>
         */
        public int getStyleValue() {
            return style_;
        }
        /**
         * <pre>
         *红包种类
         * </pre>
         *
         * <code>.RedEnvelopeMessage.RedEnvelopeStyle style = 4;</code>
         */
        public MsgBean.RedEnvelopeMessage.RedEnvelopeStyle getStyle() {
            MsgBean.RedEnvelopeMessage.RedEnvelopeStyle result = MsgBean.RedEnvelopeMessage.RedEnvelopeStyle.valueOf(style_);
            return result == null ? MsgBean.RedEnvelopeMessage.RedEnvelopeStyle.UNRECOGNIZED : result;
        }

        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            if (!getIdBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 1, id_);
            }
            if (reType_ != MsgBean.RedEnvelopeMessage.RedEnvelopeType.ALIPAY.getNumber()) {
                output.writeEnum(2, reType_);
            }
            if (!getCommentBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 3, comment_);
            }
            if (style_ != MsgBean.RedEnvelopeMessage.RedEnvelopeStyle.NORMAL.getNumber()) {
                output.writeEnum(4, style_);
            }
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (!getIdBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, id_);
            }
            if (reType_ != MsgBean.RedEnvelopeMessage.RedEnvelopeType.ALIPAY.getNumber()) {
                size += com.google.protobuf.CodedOutputStream
                        .computeEnumSize(2, reType_);
            }
            if (!getCommentBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, comment_);
            }
            if (style_ != MsgBean.RedEnvelopeMessage.RedEnvelopeStyle.NORMAL.getNumber()) {
                size += com.google.protobuf.CodedOutputStream
                        .computeEnumSize(4, style_);
            }
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MsgBean.RedEnvelopeMessage)) {
                return super.equals(obj);
            }
            MsgBean.RedEnvelopeMessage other = (MsgBean.RedEnvelopeMessage) obj;

            boolean result = true;
            result = result && getId()
                    .equals(other.getId());
            result = result && reType_ == other.reType_;
            result = result && getComment()
                    .equals(other.getComment());
            result = result && style_ == other.style_;
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (37 * hash) + ID_FIELD_NUMBER;
            hash = (53 * hash) + getId().hashCode();
            hash = (37 * hash) + RE_TYPE_FIELD_NUMBER;
            hash = (53 * hash) + reType_;
            hash = (37 * hash) + COMMENT_FIELD_NUMBER;
            hash = (53 * hash) + getComment().hashCode();
            hash = (37 * hash) + STYLE_FIELD_NUMBER;
            hash = (53 * hash) + style_;
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static MsgBean.RedEnvelopeMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.RedEnvelopeMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.RedEnvelopeMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.RedEnvelopeMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.RedEnvelopeMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.RedEnvelopeMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.RedEnvelopeMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.RedEnvelopeMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.RedEnvelopeMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }
        public static MsgBean.RedEnvelopeMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.RedEnvelopeMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.RedEnvelopeMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MsgBean.RedEnvelopeMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        /**
         * <pre>
         * 红包消息
         * </pre>
         *
         * Protobuf type {@code RedEnvelopeMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:RedEnvelopeMessage)
                MsgBean.RedEnvelopeMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_RedEnvelopeMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_RedEnvelopeMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.RedEnvelopeMessage.class, MsgBean.RedEnvelopeMessage.Builder.class);
            }

            // Construct using MsgBean.RedEnvelopeMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }
            public Builder clear() {
                super.clear();
                id_ = "";

                reType_ = 0;

                comment_ = "";

                style_ = 0;

                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return MsgBean.internal_static_RedEnvelopeMessage_descriptor;
            }

            public MsgBean.RedEnvelopeMessage getDefaultInstanceForType() {
                return MsgBean.RedEnvelopeMessage.getDefaultInstance();
            }

            public MsgBean.RedEnvelopeMessage build() {
                MsgBean.RedEnvelopeMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public MsgBean.RedEnvelopeMessage buildPartial() {
                MsgBean.RedEnvelopeMessage result = new MsgBean.RedEnvelopeMessage(this);
                result.id_ = id_;
                result.reType_ = reType_;
                result.comment_ = comment_;
                result.style_ = style_;
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof MsgBean.RedEnvelopeMessage) {
                    return mergeFrom((MsgBean.RedEnvelopeMessage)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MsgBean.RedEnvelopeMessage other) {
                if (other == MsgBean.RedEnvelopeMessage.getDefaultInstance()) return this;
                if (!other.getId().isEmpty()) {
                    id_ = other.id_;
                    onChanged();
                }
                if (other.reType_ != 0) {
                    setReTypeValue(other.getReTypeValue());
                }
                if (!other.getComment().isEmpty()) {
                    comment_ = other.comment_;
                    onChanged();
                }
                if (other.style_ != 0) {
                    setStyleValue(other.getStyleValue());
                }
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                MsgBean.RedEnvelopeMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (MsgBean.RedEnvelopeMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private java.lang.Object id_ = "";
            /**
             * <pre>
             * 红包id
             * </pre>
             *
             * <code>string id = 1;</code>
             */
            public java.lang.String getId() {
                java.lang.Object ref = id_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    id_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }
            /**
             * <pre>
             * 红包id
             * </pre>
             *
             * <code>string id = 1;</code>
             */
            public com.google.protobuf.ByteString
            getIdBytes() {
                java.lang.Object ref = id_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    id_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }
            /**
             * <pre>
             * 红包id
             * </pre>
             *
             * <code>string id = 1;</code>
             */
            public Builder setId(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                id_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 红包id
             * </pre>
             *
             * <code>string id = 1;</code>
             */
            public Builder clearId() {

                id_ = getDefaultInstance().getId();
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 红包id
             * </pre>
             *
             * <code>string id = 1;</code>
             */
            public Builder setIdBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                id_ = value;
                onChanged();
                return this;
            }

            private int reType_ = 0;
            /**
             * <pre>
             * 红包类型
             * </pre>
             *
             * <code>.RedEnvelopeMessage.RedEnvelopeType re_type = 2;</code>
             */
            public int getReTypeValue() {
                return reType_;
            }
            /**
             * <pre>
             * 红包类型
             * </pre>
             *
             * <code>.RedEnvelopeMessage.RedEnvelopeType re_type = 2;</code>
             */
            public Builder setReTypeValue(int value) {
                reType_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 红包类型
             * </pre>
             *
             * <code>.RedEnvelopeMessage.RedEnvelopeType re_type = 2;</code>
             */
            public MsgBean.RedEnvelopeMessage.RedEnvelopeType getReType() {
                MsgBean.RedEnvelopeMessage.RedEnvelopeType result = MsgBean.RedEnvelopeMessage.RedEnvelopeType.valueOf(reType_);
                return result == null ? MsgBean.RedEnvelopeMessage.RedEnvelopeType.UNRECOGNIZED : result;
            }
            /**
             * <pre>
             * 红包类型
             * </pre>
             *
             * <code>.RedEnvelopeMessage.RedEnvelopeType re_type = 2;</code>
             */
            public Builder setReType(MsgBean.RedEnvelopeMessage.RedEnvelopeType value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                reType_ = value.getNumber();
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 红包类型
             * </pre>
             *
             * <code>.RedEnvelopeMessage.RedEnvelopeType re_type = 2;</code>
             */
            public Builder clearReType() {

                reType_ = 0;
                onChanged();
                return this;
            }

            private java.lang.Object comment_ = "";
            /**
             * <pre>
             * 备注信息
             * </pre>
             *
             * <code>string comment = 3;</code>
             */
            public java.lang.String getComment() {
                java.lang.Object ref = comment_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    comment_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }
            /**
             * <pre>
             * 备注信息
             * </pre>
             *
             * <code>string comment = 3;</code>
             */
            public com.google.protobuf.ByteString
            getCommentBytes() {
                java.lang.Object ref = comment_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    comment_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }
            /**
             * <pre>
             * 备注信息
             * </pre>
             *
             * <code>string comment = 3;</code>
             */
            public Builder setComment(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                comment_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 备注信息
             * </pre>
             *
             * <code>string comment = 3;</code>
             */
            public Builder clearComment() {

                comment_ = getDefaultInstance().getComment();
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 备注信息
             * </pre>
             *
             * <code>string comment = 3;</code>
             */
            public Builder setCommentBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                comment_ = value;
                onChanged();
                return this;
            }

            private int style_ = 0;
            /**
             * <pre>
             *红包种类
             * </pre>
             *
             * <code>.RedEnvelopeMessage.RedEnvelopeStyle style = 4;</code>
             */
            public int getStyleValue() {
                return style_;
            }
            /**
             * <pre>
             *红包种类
             * </pre>
             *
             * <code>.RedEnvelopeMessage.RedEnvelopeStyle style = 4;</code>
             */
            public Builder setStyleValue(int value) {
                style_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             *红包种类
             * </pre>
             *
             * <code>.RedEnvelopeMessage.RedEnvelopeStyle style = 4;</code>
             */
            public MsgBean.RedEnvelopeMessage.RedEnvelopeStyle getStyle() {
                MsgBean.RedEnvelopeMessage.RedEnvelopeStyle result = MsgBean.RedEnvelopeMessage.RedEnvelopeStyle.valueOf(style_);
                return result == null ? MsgBean.RedEnvelopeMessage.RedEnvelopeStyle.UNRECOGNIZED : result;
            }
            /**
             * <pre>
             *红包种类
             * </pre>
             *
             * <code>.RedEnvelopeMessage.RedEnvelopeStyle style = 4;</code>
             */
            public Builder setStyle(MsgBean.RedEnvelopeMessage.RedEnvelopeStyle value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                style_ = value.getNumber();
                onChanged();
                return this;
            }
            /**
             * <pre>
             *红包种类
             * </pre>
             *
             * <code>.RedEnvelopeMessage.RedEnvelopeStyle style = 4;</code>
             */
            public Builder clearStyle() {

                style_ = 0;
                onChanged();
                return this;
            }
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:RedEnvelopeMessage)
        }

        // @@protoc_insertion_point(class_scope:RedEnvelopeMessage)
        private static final MsgBean.RedEnvelopeMessage DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new MsgBean.RedEnvelopeMessage();
        }

        public static MsgBean.RedEnvelopeMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<RedEnvelopeMessage>
                PARSER = new com.google.protobuf.AbstractParser<RedEnvelopeMessage>() {
            public RedEnvelopeMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new RedEnvelopeMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<RedEnvelopeMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<RedEnvelopeMessage> getParserForType() {
            return PARSER;
        }

        public MsgBean.RedEnvelopeMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface ReceiveRedEnvelopeMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:ReceiveRedEnvelopeMessage)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <pre>
         * 红包id
         * </pre>
         *
         * <code>string id = 1;</code>
         */
        java.lang.String getId();
        /**
         * <pre>
         * 红包id
         * </pre>
         *
         * <code>string id = 1;</code>
         */
        com.google.protobuf.ByteString
        getIdBytes();
    }
    /**
     * <pre>
     * 领取红包消息
     * </pre>
     *
     * Protobuf type {@code ReceiveRedEnvelopeMessage}
     */
    public  static final class ReceiveRedEnvelopeMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:ReceiveRedEnvelopeMessage)
            ReceiveRedEnvelopeMessageOrBuilder {
        // Use ReceiveRedEnvelopeMessage.newBuilder() to construct.
        private ReceiveRedEnvelopeMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }
        private ReceiveRedEnvelopeMessage() {
            id_ = "";
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }
        private ReceiveRedEnvelopeMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            int mutable_bitField0_ = 0;
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 10: {
                            java.lang.String s = input.readStringRequireUtf8();

                            id_ = s;
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                makeExtensionsImmutable();
            }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return MsgBean.internal_static_ReceiveRedEnvelopeMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return MsgBean.internal_static_ReceiveRedEnvelopeMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            MsgBean.ReceiveRedEnvelopeMessage.class, MsgBean.ReceiveRedEnvelopeMessage.Builder.class);
        }

        public static final int ID_FIELD_NUMBER = 1;
        private volatile java.lang.Object id_;
        /**
         * <pre>
         * 红包id
         * </pre>
         *
         * <code>string id = 1;</code>
         */
        public java.lang.String getId() {
            java.lang.Object ref = id_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                id_ = s;
                return s;
            }
        }
        /**
         * <pre>
         * 红包id
         * </pre>
         *
         * <code>string id = 1;</code>
         */
        public com.google.protobuf.ByteString
        getIdBytes() {
            java.lang.Object ref = id_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                id_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            if (!getIdBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 1, id_);
            }
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (!getIdBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, id_);
            }
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MsgBean.ReceiveRedEnvelopeMessage)) {
                return super.equals(obj);
            }
            MsgBean.ReceiveRedEnvelopeMessage other = (MsgBean.ReceiveRedEnvelopeMessage) obj;

            boolean result = true;
            result = result && getId()
                    .equals(other.getId());
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (37 * hash) + ID_FIELD_NUMBER;
            hash = (53 * hash) + getId().hashCode();
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static MsgBean.ReceiveRedEnvelopeMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.ReceiveRedEnvelopeMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.ReceiveRedEnvelopeMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.ReceiveRedEnvelopeMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.ReceiveRedEnvelopeMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.ReceiveRedEnvelopeMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.ReceiveRedEnvelopeMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.ReceiveRedEnvelopeMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.ReceiveRedEnvelopeMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }
        public static MsgBean.ReceiveRedEnvelopeMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.ReceiveRedEnvelopeMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.ReceiveRedEnvelopeMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MsgBean.ReceiveRedEnvelopeMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        /**
         * <pre>
         * 领取红包消息
         * </pre>
         *
         * Protobuf type {@code ReceiveRedEnvelopeMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:ReceiveRedEnvelopeMessage)
                MsgBean.ReceiveRedEnvelopeMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_ReceiveRedEnvelopeMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_ReceiveRedEnvelopeMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.ReceiveRedEnvelopeMessage.class, MsgBean.ReceiveRedEnvelopeMessage.Builder.class);
            }

            // Construct using MsgBean.ReceiveRedEnvelopeMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }
            public Builder clear() {
                super.clear();
                id_ = "";

                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return MsgBean.internal_static_ReceiveRedEnvelopeMessage_descriptor;
            }

            public MsgBean.ReceiveRedEnvelopeMessage getDefaultInstanceForType() {
                return MsgBean.ReceiveRedEnvelopeMessage.getDefaultInstance();
            }

            public MsgBean.ReceiveRedEnvelopeMessage build() {
                MsgBean.ReceiveRedEnvelopeMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public MsgBean.ReceiveRedEnvelopeMessage buildPartial() {
                MsgBean.ReceiveRedEnvelopeMessage result = new MsgBean.ReceiveRedEnvelopeMessage(this);
                result.id_ = id_;
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof MsgBean.ReceiveRedEnvelopeMessage) {
                    return mergeFrom((MsgBean.ReceiveRedEnvelopeMessage)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MsgBean.ReceiveRedEnvelopeMessage other) {
                if (other == MsgBean.ReceiveRedEnvelopeMessage.getDefaultInstance()) return this;
                if (!other.getId().isEmpty()) {
                    id_ = other.id_;
                    onChanged();
                }
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                MsgBean.ReceiveRedEnvelopeMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (MsgBean.ReceiveRedEnvelopeMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private java.lang.Object id_ = "";
            /**
             * <pre>
             * 红包id
             * </pre>
             *
             * <code>string id = 1;</code>
             */
            public java.lang.String getId() {
                java.lang.Object ref = id_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    id_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }
            /**
             * <pre>
             * 红包id
             * </pre>
             *
             * <code>string id = 1;</code>
             */
            public com.google.protobuf.ByteString
            getIdBytes() {
                java.lang.Object ref = id_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    id_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }
            /**
             * <pre>
             * 红包id
             * </pre>
             *
             * <code>string id = 1;</code>
             */
            public Builder setId(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                id_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 红包id
             * </pre>
             *
             * <code>string id = 1;</code>
             */
            public Builder clearId() {

                id_ = getDefaultInstance().getId();
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 红包id
             * </pre>
             *
             * <code>string id = 1;</code>
             */
            public Builder setIdBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                id_ = value;
                onChanged();
                return this;
            }
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:ReceiveRedEnvelopeMessage)
        }

        // @@protoc_insertion_point(class_scope:ReceiveRedEnvelopeMessage)
        private static final MsgBean.ReceiveRedEnvelopeMessage DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new MsgBean.ReceiveRedEnvelopeMessage();
        }

        public static MsgBean.ReceiveRedEnvelopeMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<ReceiveRedEnvelopeMessage>
                PARSER = new com.google.protobuf.AbstractParser<ReceiveRedEnvelopeMessage>() {
            public ReceiveRedEnvelopeMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new ReceiveRedEnvelopeMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<ReceiveRedEnvelopeMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<ReceiveRedEnvelopeMessage> getParserForType() {
            return PARSER;
        }

        public MsgBean.ReceiveRedEnvelopeMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface TransferMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:TransferMessage)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <pre>
         * 转账流水号
         * </pre>
         *
         * <code>string id = 1;</code>
         */
        java.lang.String getId();
        /**
         * <pre>
         * 转账流水号
         * </pre>
         *
         * <code>string id = 1;</code>
         */
        com.google.protobuf.ByteString
        getIdBytes();

        /**
         * <pre>
         * 转账金额
         * </pre>
         *
         * <code>int32 transaction_amount = 2;</code>
         */
        int getTransactionAmount();

        /**
         * <pre>
         * 备注信息
         * </pre>
         *
         * <code>string comment = 3;</code>
         */
        java.lang.String getComment();
        /**
         * <pre>
         * 备注信息
         * </pre>
         *
         * <code>string comment = 3;</code>
         */
        com.google.protobuf.ByteString
        getCommentBytes();
    }
    /**
     * <pre>
     * 转账消息
     * </pre>
     *
     * Protobuf type {@code TransferMessage}
     */
    public  static final class TransferMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:TransferMessage)
            TransferMessageOrBuilder {
        // Use TransferMessage.newBuilder() to construct.
        private TransferMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }
        private TransferMessage() {
            id_ = "";
            transactionAmount_ = 0;
            comment_ = "";
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }
        private TransferMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            int mutable_bitField0_ = 0;
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 10: {
                            java.lang.String s = input.readStringRequireUtf8();

                            id_ = s;
                            break;
                        }
                        case 16: {

                            transactionAmount_ = input.readInt32();
                            break;
                        }
                        case 26: {
                            java.lang.String s = input.readStringRequireUtf8();

                            comment_ = s;
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                makeExtensionsImmutable();
            }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return MsgBean.internal_static_TransferMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return MsgBean.internal_static_TransferMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            MsgBean.TransferMessage.class, MsgBean.TransferMessage.Builder.class);
        }

        public static final int ID_FIELD_NUMBER = 1;
        private volatile java.lang.Object id_;
        /**
         * <pre>
         * 转账流水号
         * </pre>
         *
         * <code>string id = 1;</code>
         */
        public java.lang.String getId() {
            java.lang.Object ref = id_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                id_ = s;
                return s;
            }
        }
        /**
         * <pre>
         * 转账流水号
         * </pre>
         *
         * <code>string id = 1;</code>
         */
        public com.google.protobuf.ByteString
        getIdBytes() {
            java.lang.Object ref = id_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                id_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        public static final int TRANSACTION_AMOUNT_FIELD_NUMBER = 2;
        private int transactionAmount_;
        /**
         * <pre>
         * 转账金额
         * </pre>
         *
         * <code>int32 transaction_amount = 2;</code>
         */
        public int getTransactionAmount() {
            return transactionAmount_;
        }

        public static final int COMMENT_FIELD_NUMBER = 3;
        private volatile java.lang.Object comment_;
        /**
         * <pre>
         * 备注信息
         * </pre>
         *
         * <code>string comment = 3;</code>
         */
        public java.lang.String getComment() {
            java.lang.Object ref = comment_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                comment_ = s;
                return s;
            }
        }
        /**
         * <pre>
         * 备注信息
         * </pre>
         *
         * <code>string comment = 3;</code>
         */
        public com.google.protobuf.ByteString
        getCommentBytes() {
            java.lang.Object ref = comment_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                comment_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            if (!getIdBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 1, id_);
            }
            if (transactionAmount_ != 0) {
                output.writeInt32(2, transactionAmount_);
            }
            if (!getCommentBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 3, comment_);
            }
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (!getIdBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, id_);
            }
            if (transactionAmount_ != 0) {
                size += com.google.protobuf.CodedOutputStream
                        .computeInt32Size(2, transactionAmount_);
            }
            if (!getCommentBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, comment_);
            }
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MsgBean.TransferMessage)) {
                return super.equals(obj);
            }
            MsgBean.TransferMessage other = (MsgBean.TransferMessage) obj;

            boolean result = true;
            result = result && getId()
                    .equals(other.getId());
            result = result && (getTransactionAmount()
                    == other.getTransactionAmount());
            result = result && getComment()
                    .equals(other.getComment());
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (37 * hash) + ID_FIELD_NUMBER;
            hash = (53 * hash) + getId().hashCode();
            hash = (37 * hash) + TRANSACTION_AMOUNT_FIELD_NUMBER;
            hash = (53 * hash) + getTransactionAmount();
            hash = (37 * hash) + COMMENT_FIELD_NUMBER;
            hash = (53 * hash) + getComment().hashCode();
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static MsgBean.TransferMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.TransferMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.TransferMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.TransferMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.TransferMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.TransferMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.TransferMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.TransferMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.TransferMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }
        public static MsgBean.TransferMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.TransferMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.TransferMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MsgBean.TransferMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        /**
         * <pre>
         * 转账消息
         * </pre>
         *
         * Protobuf type {@code TransferMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:TransferMessage)
                MsgBean.TransferMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_TransferMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_TransferMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.TransferMessage.class, MsgBean.TransferMessage.Builder.class);
            }

            // Construct using MsgBean.TransferMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }
            public Builder clear() {
                super.clear();
                id_ = "";

                transactionAmount_ = 0;

                comment_ = "";

                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return MsgBean.internal_static_TransferMessage_descriptor;
            }

            public MsgBean.TransferMessage getDefaultInstanceForType() {
                return MsgBean.TransferMessage.getDefaultInstance();
            }

            public MsgBean.TransferMessage build() {
                MsgBean.TransferMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public MsgBean.TransferMessage buildPartial() {
                MsgBean.TransferMessage result = new MsgBean.TransferMessage(this);
                result.id_ = id_;
                result.transactionAmount_ = transactionAmount_;
                result.comment_ = comment_;
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof MsgBean.TransferMessage) {
                    return mergeFrom((MsgBean.TransferMessage)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MsgBean.TransferMessage other) {
                if (other == MsgBean.TransferMessage.getDefaultInstance()) return this;
                if (!other.getId().isEmpty()) {
                    id_ = other.id_;
                    onChanged();
                }
                if (other.getTransactionAmount() != 0) {
                    setTransactionAmount(other.getTransactionAmount());
                }
                if (!other.getComment().isEmpty()) {
                    comment_ = other.comment_;
                    onChanged();
                }
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                MsgBean.TransferMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (MsgBean.TransferMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private java.lang.Object id_ = "";
            /**
             * <pre>
             * 转账流水号
             * </pre>
             *
             * <code>string id = 1;</code>
             */
            public java.lang.String getId() {
                java.lang.Object ref = id_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    id_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }
            /**
             * <pre>
             * 转账流水号
             * </pre>
             *
             * <code>string id = 1;</code>
             */
            public com.google.protobuf.ByteString
            getIdBytes() {
                java.lang.Object ref = id_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    id_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }
            /**
             * <pre>
             * 转账流水号
             * </pre>
             *
             * <code>string id = 1;</code>
             */
            public Builder setId(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                id_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 转账流水号
             * </pre>
             *
             * <code>string id = 1;</code>
             */
            public Builder clearId() {

                id_ = getDefaultInstance().getId();
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 转账流水号
             * </pre>
             *
             * <code>string id = 1;</code>
             */
            public Builder setIdBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                id_ = value;
                onChanged();
                return this;
            }

            private int transactionAmount_ ;
            /**
             * <pre>
             * 转账金额
             * </pre>
             *
             * <code>int32 transaction_amount = 2;</code>
             */
            public int getTransactionAmount() {
                return transactionAmount_;
            }
            /**
             * <pre>
             * 转账金额
             * </pre>
             *
             * <code>int32 transaction_amount = 2;</code>
             */
            public Builder setTransactionAmount(int value) {

                transactionAmount_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 转账金额
             * </pre>
             *
             * <code>int32 transaction_amount = 2;</code>
             */
            public Builder clearTransactionAmount() {

                transactionAmount_ = 0;
                onChanged();
                return this;
            }

            private java.lang.Object comment_ = "";
            /**
             * <pre>
             * 备注信息
             * </pre>
             *
             * <code>string comment = 3;</code>
             */
            public java.lang.String getComment() {
                java.lang.Object ref = comment_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    comment_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }
            /**
             * <pre>
             * 备注信息
             * </pre>
             *
             * <code>string comment = 3;</code>
             */
            public com.google.protobuf.ByteString
            getCommentBytes() {
                java.lang.Object ref = comment_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    comment_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }
            /**
             * <pre>
             * 备注信息
             * </pre>
             *
             * <code>string comment = 3;</code>
             */
            public Builder setComment(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                comment_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 备注信息
             * </pre>
             *
             * <code>string comment = 3;</code>
             */
            public Builder clearComment() {

                comment_ = getDefaultInstance().getComment();
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 备注信息
             * </pre>
             *
             * <code>string comment = 3;</code>
             */
            public Builder setCommentBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                comment_ = value;
                onChanged();
                return this;
            }
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:TransferMessage)
        }

        // @@protoc_insertion_point(class_scope:TransferMessage)
        private static final MsgBean.TransferMessage DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new MsgBean.TransferMessage();
        }

        public static MsgBean.TransferMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<TransferMessage>
                PARSER = new com.google.protobuf.AbstractParser<TransferMessage>() {
            public TransferMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new TransferMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<TransferMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<TransferMessage> getParserForType() {
            return PARSER;
        }

        public MsgBean.TransferMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface StampMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:StampMessage)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <pre>
         * 备注
         * </pre>
         *
         * <code>string comment = 1;</code>
         */
        java.lang.String getComment();
        /**
         * <pre>
         * 备注
         * </pre>
         *
         * <code>string comment = 1;</code>
         */
        com.google.protobuf.ByteString
        getCommentBytes();
    }
    /**
     * <pre>
     * 戳一下消息
     * </pre>
     *
     * Protobuf type {@code StampMessage}
     */
    public  static final class StampMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:StampMessage)
            StampMessageOrBuilder {
        // Use StampMessage.newBuilder() to construct.
        private StampMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }
        private StampMessage() {
            comment_ = "";
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }
        private StampMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            int mutable_bitField0_ = 0;
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 10: {
                            java.lang.String s = input.readStringRequireUtf8();

                            comment_ = s;
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                makeExtensionsImmutable();
            }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return MsgBean.internal_static_StampMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return MsgBean.internal_static_StampMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            MsgBean.StampMessage.class, MsgBean.StampMessage.Builder.class);
        }

        public static final int COMMENT_FIELD_NUMBER = 1;
        private volatile java.lang.Object comment_;
        /**
         * <pre>
         * 备注
         * </pre>
         *
         * <code>string comment = 1;</code>
         */
        public java.lang.String getComment() {
            java.lang.Object ref = comment_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                comment_ = s;
                return s;
            }
        }
        /**
         * <pre>
         * 备注
         * </pre>
         *
         * <code>string comment = 1;</code>
         */
        public com.google.protobuf.ByteString
        getCommentBytes() {
            java.lang.Object ref = comment_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                comment_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            if (!getCommentBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 1, comment_);
            }
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (!getCommentBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, comment_);
            }
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MsgBean.StampMessage)) {
                return super.equals(obj);
            }
            MsgBean.StampMessage other = (MsgBean.StampMessage) obj;

            boolean result = true;
            result = result && getComment()
                    .equals(other.getComment());
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (37 * hash) + COMMENT_FIELD_NUMBER;
            hash = (53 * hash) + getComment().hashCode();
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static MsgBean.StampMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.StampMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.StampMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.StampMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.StampMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.StampMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.StampMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.StampMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.StampMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }
        public static MsgBean.StampMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.StampMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.StampMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MsgBean.StampMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        /**
         * <pre>
         * 戳一下消息
         * </pre>
         *
         * Protobuf type {@code StampMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:StampMessage)
                MsgBean.StampMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_StampMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_StampMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.StampMessage.class, MsgBean.StampMessage.Builder.class);
            }

            // Construct using MsgBean.StampMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }
            public Builder clear() {
                super.clear();
                comment_ = "";

                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return MsgBean.internal_static_StampMessage_descriptor;
            }

            public MsgBean.StampMessage getDefaultInstanceForType() {
                return MsgBean.StampMessage.getDefaultInstance();
            }

            public MsgBean.StampMessage build() {
                MsgBean.StampMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public MsgBean.StampMessage buildPartial() {
                MsgBean.StampMessage result = new MsgBean.StampMessage(this);
                result.comment_ = comment_;
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof MsgBean.StampMessage) {
                    return mergeFrom((MsgBean.StampMessage)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MsgBean.StampMessage other) {
                if (other == MsgBean.StampMessage.getDefaultInstance()) return this;
                if (!other.getComment().isEmpty()) {
                    comment_ = other.comment_;
                    onChanged();
                }
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                MsgBean.StampMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (MsgBean.StampMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private java.lang.Object comment_ = "";
            /**
             * <pre>
             * 备注
             * </pre>
             *
             * <code>string comment = 1;</code>
             */
            public java.lang.String getComment() {
                java.lang.Object ref = comment_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    comment_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }
            /**
             * <pre>
             * 备注
             * </pre>
             *
             * <code>string comment = 1;</code>
             */
            public com.google.protobuf.ByteString
            getCommentBytes() {
                java.lang.Object ref = comment_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    comment_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }
            /**
             * <pre>
             * 备注
             * </pre>
             *
             * <code>string comment = 1;</code>
             */
            public Builder setComment(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                comment_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 备注
             * </pre>
             *
             * <code>string comment = 1;</code>
             */
            public Builder clearComment() {

                comment_ = getDefaultInstance().getComment();
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 备注
             * </pre>
             *
             * <code>string comment = 1;</code>
             */
            public Builder setCommentBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                comment_ = value;
                onChanged();
                return this;
            }
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:StampMessage)
        }

        // @@protoc_insertion_point(class_scope:StampMessage)
        private static final MsgBean.StampMessage DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new MsgBean.StampMessage();
        }

        public static MsgBean.StampMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<StampMessage>
                PARSER = new com.google.protobuf.AbstractParser<StampMessage>() {
            public StampMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new StampMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<StampMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<StampMessage> getParserForType() {
            return PARSER;
        }

        public MsgBean.StampMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface BusinessCardMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:BusinessCardMessage)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <pre>
         * 被推荐人uid
         * </pre>
         *
         * <code>uint64 uid = 1;</code>
         */
        long getUid();

        /**
         * <pre>
         * 头像地址
         * </pre>
         *
         * <code>string avatar = 2;</code>
         */
        java.lang.String getAvatar();
        /**
         * <pre>
         * 头像地址
         * </pre>
         *
         * <code>string avatar = 2;</code>
         */
        com.google.protobuf.ByteString
        getAvatarBytes();

        /**
         * <pre>
         * 昵称
         * </pre>
         *
         * <code>string nickname = 3;</code>
         */
        java.lang.String getNickname();
        /**
         * <pre>
         * 昵称
         * </pre>
         *
         * <code>string nickname = 3;</code>
         */
        com.google.protobuf.ByteString
        getNicknameBytes();

        /**
         * <pre>
         * 备注
         * </pre>
         *
         * <code>string comment = 4;</code>
         */
        java.lang.String getComment();
        /**
         * <pre>
         * 备注
         * </pre>
         *
         * <code>string comment = 4;</code>
         */
        com.google.protobuf.ByteString
        getCommentBytes();
    }
    /**
     * <pre>
     * 名片消息
     * </pre>
     *
     * Protobuf type {@code BusinessCardMessage}
     */
    public  static final class BusinessCardMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:BusinessCardMessage)
            BusinessCardMessageOrBuilder {
        // Use BusinessCardMessage.newBuilder() to construct.
        private BusinessCardMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }
        private BusinessCardMessage() {
            uid_ = 0L;
            avatar_ = "";
            nickname_ = "";
            comment_ = "";
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }
        private BusinessCardMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            int mutable_bitField0_ = 0;
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 8: {

                            uid_ = input.readUInt64();
                            break;
                        }
                        case 18: {
                            java.lang.String s = input.readStringRequireUtf8();

                            avatar_ = s;
                            break;
                        }
                        case 26: {
                            java.lang.String s = input.readStringRequireUtf8();

                            nickname_ = s;
                            break;
                        }
                        case 34: {
                            java.lang.String s = input.readStringRequireUtf8();

                            comment_ = s;
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                makeExtensionsImmutable();
            }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return MsgBean.internal_static_BusinessCardMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return MsgBean.internal_static_BusinessCardMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            MsgBean.BusinessCardMessage.class, MsgBean.BusinessCardMessage.Builder.class);
        }

        public static final int UID_FIELD_NUMBER = 1;
        private long uid_;
        /**
         * <pre>
         * 被推荐人uid
         * </pre>
         *
         * <code>uint64 uid = 1;</code>
         */
        public long getUid() {
            return uid_;
        }

        public static final int AVATAR_FIELD_NUMBER = 2;
        private volatile java.lang.Object avatar_;
        /**
         * <pre>
         * 头像地址
         * </pre>
         *
         * <code>string avatar = 2;</code>
         */
        public java.lang.String getAvatar() {
            java.lang.Object ref = avatar_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                avatar_ = s;
                return s;
            }
        }
        /**
         * <pre>
         * 头像地址
         * </pre>
         *
         * <code>string avatar = 2;</code>
         */
        public com.google.protobuf.ByteString
        getAvatarBytes() {
            java.lang.Object ref = avatar_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                avatar_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        public static final int NICKNAME_FIELD_NUMBER = 3;
        private volatile java.lang.Object nickname_;
        /**
         * <pre>
         * 昵称
         * </pre>
         *
         * <code>string nickname = 3;</code>
         */
        public java.lang.String getNickname() {
            java.lang.Object ref = nickname_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                nickname_ = s;
                return s;
            }
        }
        /**
         * <pre>
         * 昵称
         * </pre>
         *
         * <code>string nickname = 3;</code>
         */
        public com.google.protobuf.ByteString
        getNicknameBytes() {
            java.lang.Object ref = nickname_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                nickname_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        public static final int COMMENT_FIELD_NUMBER = 4;
        private volatile java.lang.Object comment_;
        /**
         * <pre>
         * 备注
         * </pre>
         *
         * <code>string comment = 4;</code>
         */
        public java.lang.String getComment() {
            java.lang.Object ref = comment_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                comment_ = s;
                return s;
            }
        }
        /**
         * <pre>
         * 备注
         * </pre>
         *
         * <code>string comment = 4;</code>
         */
        public com.google.protobuf.ByteString
        getCommentBytes() {
            java.lang.Object ref = comment_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                comment_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            if (uid_ != 0L) {
                output.writeUInt64(1, uid_);
            }
            if (!getAvatarBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 2, avatar_);
            }
            if (!getNicknameBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 3, nickname_);
            }
            if (!getCommentBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 4, comment_);
            }
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (uid_ != 0L) {
                size += com.google.protobuf.CodedOutputStream
                        .computeUInt64Size(1, uid_);
            }
            if (!getAvatarBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, avatar_);
            }
            if (!getNicknameBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, nickname_);
            }
            if (!getCommentBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(4, comment_);
            }
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MsgBean.BusinessCardMessage)) {
                return super.equals(obj);
            }
            MsgBean.BusinessCardMessage other = (MsgBean.BusinessCardMessage) obj;

            boolean result = true;
            result = result && (getUid()
                    == other.getUid());
            result = result && getAvatar()
                    .equals(other.getAvatar());
            result = result && getNickname()
                    .equals(other.getNickname());
            result = result && getComment()
                    .equals(other.getComment());
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (37 * hash) + UID_FIELD_NUMBER;
            hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
                    getUid());
            hash = (37 * hash) + AVATAR_FIELD_NUMBER;
            hash = (53 * hash) + getAvatar().hashCode();
            hash = (37 * hash) + NICKNAME_FIELD_NUMBER;
            hash = (53 * hash) + getNickname().hashCode();
            hash = (37 * hash) + COMMENT_FIELD_NUMBER;
            hash = (53 * hash) + getComment().hashCode();
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static MsgBean.BusinessCardMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.BusinessCardMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.BusinessCardMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.BusinessCardMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.BusinessCardMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.BusinessCardMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.BusinessCardMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.BusinessCardMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.BusinessCardMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }
        public static MsgBean.BusinessCardMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.BusinessCardMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.BusinessCardMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MsgBean.BusinessCardMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        /**
         * <pre>
         * 名片消息
         * </pre>
         *
         * Protobuf type {@code BusinessCardMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:BusinessCardMessage)
                MsgBean.BusinessCardMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_BusinessCardMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_BusinessCardMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.BusinessCardMessage.class, MsgBean.BusinessCardMessage.Builder.class);
            }

            // Construct using MsgBean.BusinessCardMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }
            public Builder clear() {
                super.clear();
                uid_ = 0L;

                avatar_ = "";

                nickname_ = "";

                comment_ = "";

                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return MsgBean.internal_static_BusinessCardMessage_descriptor;
            }

            public MsgBean.BusinessCardMessage getDefaultInstanceForType() {
                return MsgBean.BusinessCardMessage.getDefaultInstance();
            }

            public MsgBean.BusinessCardMessage build() {
                MsgBean.BusinessCardMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public MsgBean.BusinessCardMessage buildPartial() {
                MsgBean.BusinessCardMessage result = new MsgBean.BusinessCardMessage(this);
                result.uid_ = uid_;
                result.avatar_ = avatar_;
                result.nickname_ = nickname_;
                result.comment_ = comment_;
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof MsgBean.BusinessCardMessage) {
                    return mergeFrom((MsgBean.BusinessCardMessage)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MsgBean.BusinessCardMessage other) {
                if (other == MsgBean.BusinessCardMessage.getDefaultInstance()) return this;
                if (other.getUid() != 0L) {
                    setUid(other.getUid());
                }
                if (!other.getAvatar().isEmpty()) {
                    avatar_ = other.avatar_;
                    onChanged();
                }
                if (!other.getNickname().isEmpty()) {
                    nickname_ = other.nickname_;
                    onChanged();
                }
                if (!other.getComment().isEmpty()) {
                    comment_ = other.comment_;
                    onChanged();
                }
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                MsgBean.BusinessCardMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (MsgBean.BusinessCardMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private long uid_ ;
            /**
             * <pre>
             * 被推荐人uid
             * </pre>
             *
             * <code>uint64 uid = 1;</code>
             */
            public long getUid() {
                return uid_;
            }
            /**
             * <pre>
             * 被推荐人uid
             * </pre>
             *
             * <code>uint64 uid = 1;</code>
             */
            public Builder setUid(long value) {

                uid_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 被推荐人uid
             * </pre>
             *
             * <code>uint64 uid = 1;</code>
             */
            public Builder clearUid() {

                uid_ = 0L;
                onChanged();
                return this;
            }

            private java.lang.Object avatar_ = "";
            /**
             * <pre>
             * 头像地址
             * </pre>
             *
             * <code>string avatar = 2;</code>
             */
            public java.lang.String getAvatar() {
                java.lang.Object ref = avatar_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    avatar_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }
            /**
             * <pre>
             * 头像地址
             * </pre>
             *
             * <code>string avatar = 2;</code>
             */
            public com.google.protobuf.ByteString
            getAvatarBytes() {
                java.lang.Object ref = avatar_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    avatar_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }
            /**
             * <pre>
             * 头像地址
             * </pre>
             *
             * <code>string avatar = 2;</code>
             */
            public Builder setAvatar(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                avatar_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 头像地址
             * </pre>
             *
             * <code>string avatar = 2;</code>
             */
            public Builder clearAvatar() {

                avatar_ = getDefaultInstance().getAvatar();
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 头像地址
             * </pre>
             *
             * <code>string avatar = 2;</code>
             */
            public Builder setAvatarBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                avatar_ = value;
                onChanged();
                return this;
            }

            private java.lang.Object nickname_ = "";
            /**
             * <pre>
             * 昵称
             * </pre>
             *
             * <code>string nickname = 3;</code>
             */
            public java.lang.String getNickname() {
                java.lang.Object ref = nickname_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    nickname_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }
            /**
             * <pre>
             * 昵称
             * </pre>
             *
             * <code>string nickname = 3;</code>
             */
            public com.google.protobuf.ByteString
            getNicknameBytes() {
                java.lang.Object ref = nickname_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    nickname_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }
            /**
             * <pre>
             * 昵称
             * </pre>
             *
             * <code>string nickname = 3;</code>
             */
            public Builder setNickname(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                nickname_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 昵称
             * </pre>
             *
             * <code>string nickname = 3;</code>
             */
            public Builder clearNickname() {

                nickname_ = getDefaultInstance().getNickname();
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 昵称
             * </pre>
             *
             * <code>string nickname = 3;</code>
             */
            public Builder setNicknameBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                nickname_ = value;
                onChanged();
                return this;
            }

            private java.lang.Object comment_ = "";
            /**
             * <pre>
             * 备注
             * </pre>
             *
             * <code>string comment = 4;</code>
             */
            public java.lang.String getComment() {
                java.lang.Object ref = comment_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    comment_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }
            /**
             * <pre>
             * 备注
             * </pre>
             *
             * <code>string comment = 4;</code>
             */
            public com.google.protobuf.ByteString
            getCommentBytes() {
                java.lang.Object ref = comment_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    comment_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }
            /**
             * <pre>
             * 备注
             * </pre>
             *
             * <code>string comment = 4;</code>
             */
            public Builder setComment(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                comment_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 备注
             * </pre>
             *
             * <code>string comment = 4;</code>
             */
            public Builder clearComment() {

                comment_ = getDefaultInstance().getComment();
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 备注
             * </pre>
             *
             * <code>string comment = 4;</code>
             */
            public Builder setCommentBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                comment_ = value;
                onChanged();
                return this;
            }
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:BusinessCardMessage)
        }

        // @@protoc_insertion_point(class_scope:BusinessCardMessage)
        private static final MsgBean.BusinessCardMessage DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new MsgBean.BusinessCardMessage();
        }

        public static MsgBean.BusinessCardMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<BusinessCardMessage>
                PARSER = new com.google.protobuf.AbstractParser<BusinessCardMessage>() {
            public BusinessCardMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new BusinessCardMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<BusinessCardMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<BusinessCardMessage> getParserForType() {
            return PARSER;
        }

        public MsgBean.BusinessCardMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface RequestFriendMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:RequestFriendMessage)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <pre>
         * 招呼语
         * </pre>
         *
         * <code>string say_hi = 1;</code>
         */
        java.lang.String getSayHi();
        /**
         * <pre>
         * 招呼语
         * </pre>
         *
         * <code>string say_hi = 1;</code>
         */
        com.google.protobuf.ByteString
        getSayHiBytes();
    }
    /**
     * <pre>
     * 请求加好友消息
     * </pre>
     *
     * Protobuf type {@code RequestFriendMessage}
     */
    public  static final class RequestFriendMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:RequestFriendMessage)
            RequestFriendMessageOrBuilder {
        // Use RequestFriendMessage.newBuilder() to construct.
        private RequestFriendMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }
        private RequestFriendMessage() {
            sayHi_ = "";
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }
        private RequestFriendMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            int mutable_bitField0_ = 0;
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 10: {
                            java.lang.String s = input.readStringRequireUtf8();

                            sayHi_ = s;
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                makeExtensionsImmutable();
            }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return MsgBean.internal_static_RequestFriendMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return MsgBean.internal_static_RequestFriendMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            MsgBean.RequestFriendMessage.class, MsgBean.RequestFriendMessage.Builder.class);
        }

        public static final int SAY_HI_FIELD_NUMBER = 1;
        private volatile java.lang.Object sayHi_;
        /**
         * <pre>
         * 招呼语
         * </pre>
         *
         * <code>string say_hi = 1;</code>
         */
        public java.lang.String getSayHi() {
            java.lang.Object ref = sayHi_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                sayHi_ = s;
                return s;
            }
        }
        /**
         * <pre>
         * 招呼语
         * </pre>
         *
         * <code>string say_hi = 1;</code>
         */
        public com.google.protobuf.ByteString
        getSayHiBytes() {
            java.lang.Object ref = sayHi_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                sayHi_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            if (!getSayHiBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 1, sayHi_);
            }
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (!getSayHiBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, sayHi_);
            }
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MsgBean.RequestFriendMessage)) {
                return super.equals(obj);
            }
            MsgBean.RequestFriendMessage other = (MsgBean.RequestFriendMessage) obj;

            boolean result = true;
            result = result && getSayHi()
                    .equals(other.getSayHi());
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (37 * hash) + SAY_HI_FIELD_NUMBER;
            hash = (53 * hash) + getSayHi().hashCode();
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static MsgBean.RequestFriendMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.RequestFriendMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.RequestFriendMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.RequestFriendMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.RequestFriendMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.RequestFriendMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.RequestFriendMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.RequestFriendMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.RequestFriendMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }
        public static MsgBean.RequestFriendMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.RequestFriendMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.RequestFriendMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MsgBean.RequestFriendMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        /**
         * <pre>
         * 请求加好友消息
         * </pre>
         *
         * Protobuf type {@code RequestFriendMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:RequestFriendMessage)
                MsgBean.RequestFriendMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_RequestFriendMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_RequestFriendMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.RequestFriendMessage.class, MsgBean.RequestFriendMessage.Builder.class);
            }

            // Construct using MsgBean.RequestFriendMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }
            public Builder clear() {
                super.clear();
                sayHi_ = "";

                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return MsgBean.internal_static_RequestFriendMessage_descriptor;
            }

            public MsgBean.RequestFriendMessage getDefaultInstanceForType() {
                return MsgBean.RequestFriendMessage.getDefaultInstance();
            }

            public MsgBean.RequestFriendMessage build() {
                MsgBean.RequestFriendMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public MsgBean.RequestFriendMessage buildPartial() {
                MsgBean.RequestFriendMessage result = new MsgBean.RequestFriendMessage(this);
                result.sayHi_ = sayHi_;
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof MsgBean.RequestFriendMessage) {
                    return mergeFrom((MsgBean.RequestFriendMessage)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MsgBean.RequestFriendMessage other) {
                if (other == MsgBean.RequestFriendMessage.getDefaultInstance()) return this;
                if (!other.getSayHi().isEmpty()) {
                    sayHi_ = other.sayHi_;
                    onChanged();
                }
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                MsgBean.RequestFriendMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (MsgBean.RequestFriendMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private java.lang.Object sayHi_ = "";
            /**
             * <pre>
             * 招呼语
             * </pre>
             *
             * <code>string say_hi = 1;</code>
             */
            public java.lang.String getSayHi() {
                java.lang.Object ref = sayHi_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    sayHi_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }
            /**
             * <pre>
             * 招呼语
             * </pre>
             *
             * <code>string say_hi = 1;</code>
             */
            public com.google.protobuf.ByteString
            getSayHiBytes() {
                java.lang.Object ref = sayHi_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    sayHi_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }
            /**
             * <pre>
             * 招呼语
             * </pre>
             *
             * <code>string say_hi = 1;</code>
             */
            public Builder setSayHi(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                sayHi_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 招呼语
             * </pre>
             *
             * <code>string say_hi = 1;</code>
             */
            public Builder clearSayHi() {

                sayHi_ = getDefaultInstance().getSayHi();
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 招呼语
             * </pre>
             *
             * <code>string say_hi = 1;</code>
             */
            public Builder setSayHiBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                sayHi_ = value;
                onChanged();
                return this;
            }
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:RequestFriendMessage)
        }

        // @@protoc_insertion_point(class_scope:RequestFriendMessage)
        private static final MsgBean.RequestFriendMessage DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new MsgBean.RequestFriendMessage();
        }

        public static MsgBean.RequestFriendMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<RequestFriendMessage>
                PARSER = new com.google.protobuf.AbstractParser<RequestFriendMessage>() {
            public RequestFriendMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new RequestFriendMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<RequestFriendMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<RequestFriendMessage> getParserForType() {
            return PARSER;
        }

        public MsgBean.RequestFriendMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface AcceptBeFriendsMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:AcceptBeFriendsMessage)
            com.google.protobuf.MessageOrBuilder {
    }
    /**
     * <pre>
     * 接受好友消息
     * </pre>
     *
     * Protobuf type {@code AcceptBeFriendsMessage}
     */
    public  static final class AcceptBeFriendsMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:AcceptBeFriendsMessage)
            AcceptBeFriendsMessageOrBuilder {
        // Use AcceptBeFriendsMessage.newBuilder() to construct.
        private AcceptBeFriendsMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }
        private AcceptBeFriendsMessage() {
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }
        private AcceptBeFriendsMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                makeExtensionsImmutable();
            }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return MsgBean.internal_static_AcceptBeFriendsMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return MsgBean.internal_static_AcceptBeFriendsMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            MsgBean.AcceptBeFriendsMessage.class, MsgBean.AcceptBeFriendsMessage.Builder.class);
        }

        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MsgBean.AcceptBeFriendsMessage)) {
                return super.equals(obj);
            }
            MsgBean.AcceptBeFriendsMessage other = (MsgBean.AcceptBeFriendsMessage) obj;

            boolean result = true;
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static MsgBean.AcceptBeFriendsMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.AcceptBeFriendsMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.AcceptBeFriendsMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.AcceptBeFriendsMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.AcceptBeFriendsMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.AcceptBeFriendsMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.AcceptBeFriendsMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.AcceptBeFriendsMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.AcceptBeFriendsMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }
        public static MsgBean.AcceptBeFriendsMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.AcceptBeFriendsMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.AcceptBeFriendsMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MsgBean.AcceptBeFriendsMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        /**
         * <pre>
         * 接受好友消息
         * </pre>
         *
         * Protobuf type {@code AcceptBeFriendsMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:AcceptBeFriendsMessage)
                MsgBean.AcceptBeFriendsMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_AcceptBeFriendsMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_AcceptBeFriendsMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.AcceptBeFriendsMessage.class, MsgBean.AcceptBeFriendsMessage.Builder.class);
            }

            // Construct using MsgBean.AcceptBeFriendsMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }
            public Builder clear() {
                super.clear();
                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return MsgBean.internal_static_AcceptBeFriendsMessage_descriptor;
            }

            public MsgBean.AcceptBeFriendsMessage getDefaultInstanceForType() {
                return MsgBean.AcceptBeFriendsMessage.getDefaultInstance();
            }

            public MsgBean.AcceptBeFriendsMessage build() {
                MsgBean.AcceptBeFriendsMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public MsgBean.AcceptBeFriendsMessage buildPartial() {
                MsgBean.AcceptBeFriendsMessage result = new MsgBean.AcceptBeFriendsMessage(this);
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof MsgBean.AcceptBeFriendsMessage) {
                    return mergeFrom((MsgBean.AcceptBeFriendsMessage)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MsgBean.AcceptBeFriendsMessage other) {
                if (other == MsgBean.AcceptBeFriendsMessage.getDefaultInstance()) return this;
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                MsgBean.AcceptBeFriendsMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (MsgBean.AcceptBeFriendsMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:AcceptBeFriendsMessage)
        }

        // @@protoc_insertion_point(class_scope:AcceptBeFriendsMessage)
        private static final MsgBean.AcceptBeFriendsMessage DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new MsgBean.AcceptBeFriendsMessage();
        }

        public static MsgBean.AcceptBeFriendsMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<AcceptBeFriendsMessage>
                PARSER = new com.google.protobuf.AbstractParser<AcceptBeFriendsMessage>() {
            public AcceptBeFriendsMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new AcceptBeFriendsMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<AcceptBeFriendsMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<AcceptBeFriendsMessage> getParserForType() {
            return PARSER;
        }

        public MsgBean.AcceptBeFriendsMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface RemoveFriendMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:RemoveFriendMessage)
            com.google.protobuf.MessageOrBuilder {
    }
    /**
     * <pre>
     * 删除好友消息
     * </pre>
     *
     * Protobuf type {@code RemoveFriendMessage}
     */
    public  static final class RemoveFriendMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:RemoveFriendMessage)
            RemoveFriendMessageOrBuilder {
        // Use RemoveFriendMessage.newBuilder() to construct.
        private RemoveFriendMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }
        private RemoveFriendMessage() {
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }
        private RemoveFriendMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                makeExtensionsImmutable();
            }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return MsgBean.internal_static_RemoveFriendMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return MsgBean.internal_static_RemoveFriendMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            MsgBean.RemoveFriendMessage.class, MsgBean.RemoveFriendMessage.Builder.class);
        }

        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MsgBean.RemoveFriendMessage)) {
                return super.equals(obj);
            }
            MsgBean.RemoveFriendMessage other = (MsgBean.RemoveFriendMessage) obj;

            boolean result = true;
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static MsgBean.RemoveFriendMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.RemoveFriendMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.RemoveFriendMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.RemoveFriendMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.RemoveFriendMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.RemoveFriendMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.RemoveFriendMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.RemoveFriendMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.RemoveFriendMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }
        public static MsgBean.RemoveFriendMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.RemoveFriendMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.RemoveFriendMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MsgBean.RemoveFriendMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        /**
         * <pre>
         * 删除好友消息
         * </pre>
         *
         * Protobuf type {@code RemoveFriendMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:RemoveFriendMessage)
                MsgBean.RemoveFriendMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_RemoveFriendMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_RemoveFriendMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.RemoveFriendMessage.class, MsgBean.RemoveFriendMessage.Builder.class);
            }

            // Construct using MsgBean.RemoveFriendMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }
            public Builder clear() {
                super.clear();
                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return MsgBean.internal_static_RemoveFriendMessage_descriptor;
            }

            public MsgBean.RemoveFriendMessage getDefaultInstanceForType() {
                return MsgBean.RemoveFriendMessage.getDefaultInstance();
            }

            public MsgBean.RemoveFriendMessage build() {
                MsgBean.RemoveFriendMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public MsgBean.RemoveFriendMessage buildPartial() {
                MsgBean.RemoveFriendMessage result = new MsgBean.RemoveFriendMessage(this);
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof MsgBean.RemoveFriendMessage) {
                    return mergeFrom((MsgBean.RemoveFriendMessage)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MsgBean.RemoveFriendMessage other) {
                if (other == MsgBean.RemoveFriendMessage.getDefaultInstance()) return this;
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                MsgBean.RemoveFriendMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (MsgBean.RemoveFriendMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:RemoveFriendMessage)
        }

        // @@protoc_insertion_point(class_scope:RemoveFriendMessage)
        private static final MsgBean.RemoveFriendMessage DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new MsgBean.RemoveFriendMessage();
        }

        public static MsgBean.RemoveFriendMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<RemoveFriendMessage>
                PARSER = new com.google.protobuf.AbstractParser<RemoveFriendMessage>() {
            public RemoveFriendMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new RemoveFriendMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<RemoveFriendMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<RemoveFriendMessage> getParserForType() {
            return PARSER;
        }

        public MsgBean.RemoveFriendMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface GroupNoticeMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:GroupNoticeMessage)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <pre>
         * 请求人uid
         * </pre>
         *
         * <code>uint64 uid = 1;</code>
         */
        long getUid();

        /**
         * <pre>
         * 头像地址
         * </pre>
         *
         * <code>string avatar = 2;</code>
         */
        java.lang.String getAvatar();
        /**
         * <pre>
         * 头像地址
         * </pre>
         *
         * <code>string avatar = 2;</code>
         */
        com.google.protobuf.ByteString
        getAvatarBytes();

        /**
         * <pre>
         * 昵称
         * </pre>
         *
         * <code>string nickname = 3;</code>
         */
        java.lang.String getNickname();
        /**
         * <pre>
         * 昵称
         * </pre>
         *
         * <code>string nickname = 3;</code>
         */
        com.google.protobuf.ByteString
        getNicknameBytes();
    }
    /**
     * <pre>
     * 群通知消息
     * </pre>
     *
     * Protobuf type {@code GroupNoticeMessage}
     */
    public  static final class GroupNoticeMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:GroupNoticeMessage)
            GroupNoticeMessageOrBuilder {
        // Use GroupNoticeMessage.newBuilder() to construct.
        private GroupNoticeMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }
        private GroupNoticeMessage() {
            uid_ = 0L;
            avatar_ = "";
            nickname_ = "";
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }
        private GroupNoticeMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            int mutable_bitField0_ = 0;
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 8: {

                            uid_ = input.readUInt64();
                            break;
                        }
                        case 18: {
                            java.lang.String s = input.readStringRequireUtf8();

                            avatar_ = s;
                            break;
                        }
                        case 26: {
                            java.lang.String s = input.readStringRequireUtf8();

                            nickname_ = s;
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                makeExtensionsImmutable();
            }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return MsgBean.internal_static_GroupNoticeMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return MsgBean.internal_static_GroupNoticeMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            MsgBean.GroupNoticeMessage.class, MsgBean.GroupNoticeMessage.Builder.class);
        }

        public static final int UID_FIELD_NUMBER = 1;
        private long uid_;
        /**
         * <pre>
         * 请求人uid
         * </pre>
         *
         * <code>uint64 uid = 1;</code>
         */
        public long getUid() {
            return uid_;
        }

        public static final int AVATAR_FIELD_NUMBER = 2;
        private volatile java.lang.Object avatar_;
        /**
         * <pre>
         * 头像地址
         * </pre>
         *
         * <code>string avatar = 2;</code>
         */
        public java.lang.String getAvatar() {
            java.lang.Object ref = avatar_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                avatar_ = s;
                return s;
            }
        }
        /**
         * <pre>
         * 头像地址
         * </pre>
         *
         * <code>string avatar = 2;</code>
         */
        public com.google.protobuf.ByteString
        getAvatarBytes() {
            java.lang.Object ref = avatar_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                avatar_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        public static final int NICKNAME_FIELD_NUMBER = 3;
        private volatile java.lang.Object nickname_;
        /**
         * <pre>
         * 昵称
         * </pre>
         *
         * <code>string nickname = 3;</code>
         */
        public java.lang.String getNickname() {
            java.lang.Object ref = nickname_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                nickname_ = s;
                return s;
            }
        }
        /**
         * <pre>
         * 昵称
         * </pre>
         *
         * <code>string nickname = 3;</code>
         */
        public com.google.protobuf.ByteString
        getNicknameBytes() {
            java.lang.Object ref = nickname_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                nickname_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            if (uid_ != 0L) {
                output.writeUInt64(1, uid_);
            }
            if (!getAvatarBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 2, avatar_);
            }
            if (!getNicknameBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 3, nickname_);
            }
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (uid_ != 0L) {
                size += com.google.protobuf.CodedOutputStream
                        .computeUInt64Size(1, uid_);
            }
            if (!getAvatarBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, avatar_);
            }
            if (!getNicknameBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, nickname_);
            }
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MsgBean.GroupNoticeMessage)) {
                return super.equals(obj);
            }
            MsgBean.GroupNoticeMessage other = (MsgBean.GroupNoticeMessage) obj;

            boolean result = true;
            result = result && (getUid()
                    == other.getUid());
            result = result && getAvatar()
                    .equals(other.getAvatar());
            result = result && getNickname()
                    .equals(other.getNickname());
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (37 * hash) + UID_FIELD_NUMBER;
            hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
                    getUid());
            hash = (37 * hash) + AVATAR_FIELD_NUMBER;
            hash = (53 * hash) + getAvatar().hashCode();
            hash = (37 * hash) + NICKNAME_FIELD_NUMBER;
            hash = (53 * hash) + getNickname().hashCode();
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static MsgBean.GroupNoticeMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.GroupNoticeMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.GroupNoticeMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.GroupNoticeMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.GroupNoticeMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.GroupNoticeMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.GroupNoticeMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.GroupNoticeMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.GroupNoticeMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }
        public static MsgBean.GroupNoticeMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.GroupNoticeMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.GroupNoticeMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MsgBean.GroupNoticeMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        /**
         * <pre>
         * 群通知消息
         * </pre>
         *
         * Protobuf type {@code GroupNoticeMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:GroupNoticeMessage)
                MsgBean.GroupNoticeMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_GroupNoticeMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_GroupNoticeMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.GroupNoticeMessage.class, MsgBean.GroupNoticeMessage.Builder.class);
            }

            // Construct using MsgBean.GroupNoticeMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }
            public Builder clear() {
                super.clear();
                uid_ = 0L;

                avatar_ = "";

                nickname_ = "";

                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return MsgBean.internal_static_GroupNoticeMessage_descriptor;
            }

            public MsgBean.GroupNoticeMessage getDefaultInstanceForType() {
                return MsgBean.GroupNoticeMessage.getDefaultInstance();
            }

            public MsgBean.GroupNoticeMessage build() {
                MsgBean.GroupNoticeMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public MsgBean.GroupNoticeMessage buildPartial() {
                MsgBean.GroupNoticeMessage result = new MsgBean.GroupNoticeMessage(this);
                result.uid_ = uid_;
                result.avatar_ = avatar_;
                result.nickname_ = nickname_;
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof MsgBean.GroupNoticeMessage) {
                    return mergeFrom((MsgBean.GroupNoticeMessage)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MsgBean.GroupNoticeMessage other) {
                if (other == MsgBean.GroupNoticeMessage.getDefaultInstance()) return this;
                if (other.getUid() != 0L) {
                    setUid(other.getUid());
                }
                if (!other.getAvatar().isEmpty()) {
                    avatar_ = other.avatar_;
                    onChanged();
                }
                if (!other.getNickname().isEmpty()) {
                    nickname_ = other.nickname_;
                    onChanged();
                }
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                MsgBean.GroupNoticeMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (MsgBean.GroupNoticeMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private long uid_ ;
            /**
             * <pre>
             * 请求人uid
             * </pre>
             *
             * <code>uint64 uid = 1;</code>
             */
            public long getUid() {
                return uid_;
            }
            /**
             * <pre>
             * 请求人uid
             * </pre>
             *
             * <code>uint64 uid = 1;</code>
             */
            public Builder setUid(long value) {

                uid_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 请求人uid
             * </pre>
             *
             * <code>uint64 uid = 1;</code>
             */
            public Builder clearUid() {

                uid_ = 0L;
                onChanged();
                return this;
            }

            private java.lang.Object avatar_ = "";
            /**
             * <pre>
             * 头像地址
             * </pre>
             *
             * <code>string avatar = 2;</code>
             */
            public java.lang.String getAvatar() {
                java.lang.Object ref = avatar_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    avatar_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }
            /**
             * <pre>
             * 头像地址
             * </pre>
             *
             * <code>string avatar = 2;</code>
             */
            public com.google.protobuf.ByteString
            getAvatarBytes() {
                java.lang.Object ref = avatar_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    avatar_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }
            /**
             * <pre>
             * 头像地址
             * </pre>
             *
             * <code>string avatar = 2;</code>
             */
            public Builder setAvatar(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                avatar_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 头像地址
             * </pre>
             *
             * <code>string avatar = 2;</code>
             */
            public Builder clearAvatar() {

                avatar_ = getDefaultInstance().getAvatar();
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 头像地址
             * </pre>
             *
             * <code>string avatar = 2;</code>
             */
            public Builder setAvatarBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                avatar_ = value;
                onChanged();
                return this;
            }

            private java.lang.Object nickname_ = "";
            /**
             * <pre>
             * 昵称
             * </pre>
             *
             * <code>string nickname = 3;</code>
             */
            public java.lang.String getNickname() {
                java.lang.Object ref = nickname_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    nickname_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }
            /**
             * <pre>
             * 昵称
             * </pre>
             *
             * <code>string nickname = 3;</code>
             */
            public com.google.protobuf.ByteString
            getNicknameBytes() {
                java.lang.Object ref = nickname_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    nickname_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }
            /**
             * <pre>
             * 昵称
             * </pre>
             *
             * <code>string nickname = 3;</code>
             */
            public Builder setNickname(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                nickname_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 昵称
             * </pre>
             *
             * <code>string nickname = 3;</code>
             */
            public Builder clearNickname() {

                nickname_ = getDefaultInstance().getNickname();
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 昵称
             * </pre>
             *
             * <code>string nickname = 3;</code>
             */
            public Builder setNicknameBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                nickname_ = value;
                onChanged();
                return this;
            }
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:GroupNoticeMessage)
        }

        // @@protoc_insertion_point(class_scope:GroupNoticeMessage)
        private static final MsgBean.GroupNoticeMessage DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new MsgBean.GroupNoticeMessage();
        }

        public static MsgBean.GroupNoticeMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<GroupNoticeMessage>
                PARSER = new com.google.protobuf.AbstractParser<GroupNoticeMessage>() {
            public GroupNoticeMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new GroupNoticeMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<GroupNoticeMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<GroupNoticeMessage> getParserForType() {
            return PARSER;
        }

        public MsgBean.GroupNoticeMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface InviteJoinGroupMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:InviteJoinGroupMessage)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <code>.GroupNoticeMessage notice_message = 1;</code>
         */
        boolean hasNoticeMessage();
        /**
         * <code>.GroupNoticeMessage notice_message = 1;</code>
         */
        MsgBean.GroupNoticeMessage getNoticeMessage();
        /**
         * <code>.GroupNoticeMessage notice_message = 1;</code>
         */
        MsgBean.GroupNoticeMessageOrBuilder getNoticeMessageOrBuilder();
    }
    /**
     * <pre>
     * 被邀请进群消息
     * </pre>
     *
     * Protobuf type {@code InviteJoinGroupMessage}
     */
    public  static final class InviteJoinGroupMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:InviteJoinGroupMessage)
            InviteJoinGroupMessageOrBuilder {
        // Use InviteJoinGroupMessage.newBuilder() to construct.
        private InviteJoinGroupMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }
        private InviteJoinGroupMessage() {
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }
        private InviteJoinGroupMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            int mutable_bitField0_ = 0;
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 10: {
                            MsgBean.GroupNoticeMessage.Builder subBuilder = null;
                            if (noticeMessage_ != null) {
                                subBuilder = noticeMessage_.toBuilder();
                            }
                            noticeMessage_ = input.readMessage(MsgBean.GroupNoticeMessage.parser(), extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(noticeMessage_);
                                noticeMessage_ = subBuilder.buildPartial();
                            }

                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                makeExtensionsImmutable();
            }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return MsgBean.internal_static_InviteJoinGroupMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return MsgBean.internal_static_InviteJoinGroupMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            MsgBean.InviteJoinGroupMessage.class, MsgBean.InviteJoinGroupMessage.Builder.class);
        }

        public static final int NOTICE_MESSAGE_FIELD_NUMBER = 1;
        private MsgBean.GroupNoticeMessage noticeMessage_;
        /**
         * <code>.GroupNoticeMessage notice_message = 1;</code>
         */
        public boolean hasNoticeMessage() {
            return noticeMessage_ != null;
        }
        /**
         * <code>.GroupNoticeMessage notice_message = 1;</code>
         */
        public MsgBean.GroupNoticeMessage getNoticeMessage() {
            return noticeMessage_ == null ? MsgBean.GroupNoticeMessage.getDefaultInstance() : noticeMessage_;
        }
        /**
         * <code>.GroupNoticeMessage notice_message = 1;</code>
         */
        public MsgBean.GroupNoticeMessageOrBuilder getNoticeMessageOrBuilder() {
            return getNoticeMessage();
        }

        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            if (noticeMessage_ != null) {
                output.writeMessage(1, getNoticeMessage());
            }
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (noticeMessage_ != null) {
                size += com.google.protobuf.CodedOutputStream
                        .computeMessageSize(1, getNoticeMessage());
            }
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MsgBean.InviteJoinGroupMessage)) {
                return super.equals(obj);
            }
            MsgBean.InviteJoinGroupMessage other = (MsgBean.InviteJoinGroupMessage) obj;

            boolean result = true;
            result = result && (hasNoticeMessage() == other.hasNoticeMessage());
            if (hasNoticeMessage()) {
                result = result && getNoticeMessage()
                        .equals(other.getNoticeMessage());
            }
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            if (hasNoticeMessage()) {
                hash = (37 * hash) + NOTICE_MESSAGE_FIELD_NUMBER;
                hash = (53 * hash) + getNoticeMessage().hashCode();
            }
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static MsgBean.InviteJoinGroupMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.InviteJoinGroupMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.InviteJoinGroupMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.InviteJoinGroupMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.InviteJoinGroupMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.InviteJoinGroupMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.InviteJoinGroupMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.InviteJoinGroupMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.InviteJoinGroupMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }
        public static MsgBean.InviteJoinGroupMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.InviteJoinGroupMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.InviteJoinGroupMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MsgBean.InviteJoinGroupMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        /**
         * <pre>
         * 被邀请进群消息
         * </pre>
         *
         * Protobuf type {@code InviteJoinGroupMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:InviteJoinGroupMessage)
                MsgBean.InviteJoinGroupMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_InviteJoinGroupMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_InviteJoinGroupMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.InviteJoinGroupMessage.class, MsgBean.InviteJoinGroupMessage.Builder.class);
            }

            // Construct using MsgBean.InviteJoinGroupMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }
            public Builder clear() {
                super.clear();
                if (noticeMessageBuilder_ == null) {
                    noticeMessage_ = null;
                } else {
                    noticeMessage_ = null;
                    noticeMessageBuilder_ = null;
                }
                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return MsgBean.internal_static_InviteJoinGroupMessage_descriptor;
            }

            public MsgBean.InviteJoinGroupMessage getDefaultInstanceForType() {
                return MsgBean.InviteJoinGroupMessage.getDefaultInstance();
            }

            public MsgBean.InviteJoinGroupMessage build() {
                MsgBean.InviteJoinGroupMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public MsgBean.InviteJoinGroupMessage buildPartial() {
                MsgBean.InviteJoinGroupMessage result = new MsgBean.InviteJoinGroupMessage(this);
                if (noticeMessageBuilder_ == null) {
                    result.noticeMessage_ = noticeMessage_;
                } else {
                    result.noticeMessage_ = noticeMessageBuilder_.build();
                }
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof MsgBean.InviteJoinGroupMessage) {
                    return mergeFrom((MsgBean.InviteJoinGroupMessage)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MsgBean.InviteJoinGroupMessage other) {
                if (other == MsgBean.InviteJoinGroupMessage.getDefaultInstance()) return this;
                if (other.hasNoticeMessage()) {
                    mergeNoticeMessage(other.getNoticeMessage());
                }
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                MsgBean.InviteJoinGroupMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (MsgBean.InviteJoinGroupMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private MsgBean.GroupNoticeMessage noticeMessage_ = null;
            private com.google.protobuf.SingleFieldBuilderV3<
                    MsgBean.GroupNoticeMessage, MsgBean.GroupNoticeMessage.Builder, MsgBean.GroupNoticeMessageOrBuilder> noticeMessageBuilder_;
            /**
             * <code>.GroupNoticeMessage notice_message = 1;</code>
             */
            public boolean hasNoticeMessage() {
                return noticeMessageBuilder_ != null || noticeMessage_ != null;
            }
            /**
             * <code>.GroupNoticeMessage notice_message = 1;</code>
             */
            public MsgBean.GroupNoticeMessage getNoticeMessage() {
                if (noticeMessageBuilder_ == null) {
                    return noticeMessage_ == null ? MsgBean.GroupNoticeMessage.getDefaultInstance() : noticeMessage_;
                } else {
                    return noticeMessageBuilder_.getMessage();
                }
            }
            /**
             * <code>.GroupNoticeMessage notice_message = 1;</code>
             */
            public Builder setNoticeMessage(MsgBean.GroupNoticeMessage value) {
                if (noticeMessageBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    noticeMessage_ = value;
                    onChanged();
                } else {
                    noticeMessageBuilder_.setMessage(value);
                }

                return this;
            }
            /**
             * <code>.GroupNoticeMessage notice_message = 1;</code>
             */
            public Builder setNoticeMessage(
                    MsgBean.GroupNoticeMessage.Builder builderForValue) {
                if (noticeMessageBuilder_ == null) {
                    noticeMessage_ = builderForValue.build();
                    onChanged();
                } else {
                    noticeMessageBuilder_.setMessage(builderForValue.build());
                }

                return this;
            }
            /**
             * <code>.GroupNoticeMessage notice_message = 1;</code>
             */
            public Builder mergeNoticeMessage(MsgBean.GroupNoticeMessage value) {
                if (noticeMessageBuilder_ == null) {
                    if (noticeMessage_ != null) {
                        noticeMessage_ =
                                MsgBean.GroupNoticeMessage.newBuilder(noticeMessage_).mergeFrom(value).buildPartial();
                    } else {
                        noticeMessage_ = value;
                    }
                    onChanged();
                } else {
                    noticeMessageBuilder_.mergeFrom(value);
                }

                return this;
            }
            /**
             * <code>.GroupNoticeMessage notice_message = 1;</code>
             */
            public Builder clearNoticeMessage() {
                if (noticeMessageBuilder_ == null) {
                    noticeMessage_ = null;
                    onChanged();
                } else {
                    noticeMessage_ = null;
                    noticeMessageBuilder_ = null;
                }

                return this;
            }
            /**
             * <code>.GroupNoticeMessage notice_message = 1;</code>
             */
            public MsgBean.GroupNoticeMessage.Builder getNoticeMessageBuilder() {

                onChanged();
                return getNoticeMessageFieldBuilder().getBuilder();
            }
            /**
             * <code>.GroupNoticeMessage notice_message = 1;</code>
             */
            public MsgBean.GroupNoticeMessageOrBuilder getNoticeMessageOrBuilder() {
                if (noticeMessageBuilder_ != null) {
                    return noticeMessageBuilder_.getMessageOrBuilder();
                } else {
                    return noticeMessage_ == null ?
                            MsgBean.GroupNoticeMessage.getDefaultInstance() : noticeMessage_;
                }
            }
            /**
             * <code>.GroupNoticeMessage notice_message = 1;</code>
             */
            private com.google.protobuf.SingleFieldBuilderV3<
                    MsgBean.GroupNoticeMessage, MsgBean.GroupNoticeMessage.Builder, MsgBean.GroupNoticeMessageOrBuilder>
            getNoticeMessageFieldBuilder() {
                if (noticeMessageBuilder_ == null) {
                    noticeMessageBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
                            MsgBean.GroupNoticeMessage, MsgBean.GroupNoticeMessage.Builder, MsgBean.GroupNoticeMessageOrBuilder>(
                            getNoticeMessage(),
                            getParentForChildren(),
                            isClean());
                    noticeMessage_ = null;
                }
                return noticeMessageBuilder_;
            }
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:InviteJoinGroupMessage)
        }

        // @@protoc_insertion_point(class_scope:InviteJoinGroupMessage)
        private static final MsgBean.InviteJoinGroupMessage DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new MsgBean.InviteJoinGroupMessage();
        }

        public static MsgBean.InviteJoinGroupMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<InviteJoinGroupMessage>
                PARSER = new com.google.protobuf.AbstractParser<InviteJoinGroupMessage>() {
            public InviteJoinGroupMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new InviteJoinGroupMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<InviteJoinGroupMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<InviteJoinGroupMessage> getParserForType() {
            return PARSER;
        }

        public MsgBean.InviteJoinGroupMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface RequestGroupMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:RequestGroupMessage)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
         */
        java.util.List<MsgBean.GroupNoticeMessage>
        getNoticeMessageList();
        /**
         * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
         */
        MsgBean.GroupNoticeMessage getNoticeMessage(int index);
        /**
         * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
         */
        int getNoticeMessageCount();
        /**
         * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
         */
        java.util.List<? extends MsgBean.GroupNoticeMessageOrBuilder>
        getNoticeMessageOrBuilderList();
        /**
         * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
         */
        MsgBean.GroupNoticeMessageOrBuilder getNoticeMessageOrBuilder(
                int index);
    }
    /**
     * <pre>
     * 请求入群消息
     * </pre>
     *
     * Protobuf type {@code RequestGroupMessage}
     */
    public  static final class RequestGroupMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:RequestGroupMessage)
            RequestGroupMessageOrBuilder {
        // Use RequestGroupMessage.newBuilder() to construct.
        private RequestGroupMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }
        private RequestGroupMessage() {
            noticeMessage_ = java.util.Collections.emptyList();
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }
        private RequestGroupMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            int mutable_bitField0_ = 0;
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 10: {
                            if (!((mutable_bitField0_ & 0x00000001) == 0x00000001)) {
                                noticeMessage_ = new java.util.ArrayList<MsgBean.GroupNoticeMessage>();
                                mutable_bitField0_ |= 0x00000001;
                            }
                            noticeMessage_.add(
                                    input.readMessage(MsgBean.GroupNoticeMessage.parser(), extensionRegistry));
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                if (((mutable_bitField0_ & 0x00000001) == 0x00000001)) {
                    noticeMessage_ = java.util.Collections.unmodifiableList(noticeMessage_);
                }
                makeExtensionsImmutable();
            }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return MsgBean.internal_static_RequestGroupMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return MsgBean.internal_static_RequestGroupMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            MsgBean.RequestGroupMessage.class, MsgBean.RequestGroupMessage.Builder.class);
        }

        public static final int NOTICE_MESSAGE_FIELD_NUMBER = 1;
        private java.util.List<MsgBean.GroupNoticeMessage> noticeMessage_;
        /**
         * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
         */
        public java.util.List<MsgBean.GroupNoticeMessage> getNoticeMessageList() {
            return noticeMessage_;
        }
        /**
         * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
         */
        public java.util.List<? extends MsgBean.GroupNoticeMessageOrBuilder>
        getNoticeMessageOrBuilderList() {
            return noticeMessage_;
        }
        /**
         * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
         */
        public int getNoticeMessageCount() {
            return noticeMessage_.size();
        }
        /**
         * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
         */
        public MsgBean.GroupNoticeMessage getNoticeMessage(int index) {
            return noticeMessage_.get(index);
        }
        /**
         * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
         */
        public MsgBean.GroupNoticeMessageOrBuilder getNoticeMessageOrBuilder(
                int index) {
            return noticeMessage_.get(index);
        }

        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            for (int i = 0; i < noticeMessage_.size(); i++) {
                output.writeMessage(1, noticeMessage_.get(i));
            }
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            for (int i = 0; i < noticeMessage_.size(); i++) {
                size += com.google.protobuf.CodedOutputStream
                        .computeMessageSize(1, noticeMessage_.get(i));
            }
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MsgBean.RequestGroupMessage)) {
                return super.equals(obj);
            }
            MsgBean.RequestGroupMessage other = (MsgBean.RequestGroupMessage) obj;

            boolean result = true;
            result = result && getNoticeMessageList()
                    .equals(other.getNoticeMessageList());
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            if (getNoticeMessageCount() > 0) {
                hash = (37 * hash) + NOTICE_MESSAGE_FIELD_NUMBER;
                hash = (53 * hash) + getNoticeMessageList().hashCode();
            }
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static MsgBean.RequestGroupMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.RequestGroupMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.RequestGroupMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.RequestGroupMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.RequestGroupMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.RequestGroupMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.RequestGroupMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.RequestGroupMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.RequestGroupMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }
        public static MsgBean.RequestGroupMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.RequestGroupMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.RequestGroupMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MsgBean.RequestGroupMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        /**
         * <pre>
         * 请求入群消息
         * </pre>
         *
         * Protobuf type {@code RequestGroupMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:RequestGroupMessage)
                MsgBean.RequestGroupMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_RequestGroupMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_RequestGroupMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.RequestGroupMessage.class, MsgBean.RequestGroupMessage.Builder.class);
            }

            // Construct using MsgBean.RequestGroupMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                    getNoticeMessageFieldBuilder();
                }
            }
            public Builder clear() {
                super.clear();
                if (noticeMessageBuilder_ == null) {
                    noticeMessage_ = java.util.Collections.emptyList();
                    bitField0_ = (bitField0_ & ~0x00000001);
                } else {
                    noticeMessageBuilder_.clear();
                }
                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return MsgBean.internal_static_RequestGroupMessage_descriptor;
            }

            public MsgBean.RequestGroupMessage getDefaultInstanceForType() {
                return MsgBean.RequestGroupMessage.getDefaultInstance();
            }

            public MsgBean.RequestGroupMessage build() {
                MsgBean.RequestGroupMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public MsgBean.RequestGroupMessage buildPartial() {
                MsgBean.RequestGroupMessage result = new MsgBean.RequestGroupMessage(this);
                int from_bitField0_ = bitField0_;
                if (noticeMessageBuilder_ == null) {
                    if (((bitField0_ & 0x00000001) == 0x00000001)) {
                        noticeMessage_ = java.util.Collections.unmodifiableList(noticeMessage_);
                        bitField0_ = (bitField0_ & ~0x00000001);
                    }
                    result.noticeMessage_ = noticeMessage_;
                } else {
                    result.noticeMessage_ = noticeMessageBuilder_.build();
                }
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof MsgBean.RequestGroupMessage) {
                    return mergeFrom((MsgBean.RequestGroupMessage)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MsgBean.RequestGroupMessage other) {
                if (other == MsgBean.RequestGroupMessage.getDefaultInstance()) return this;
                if (noticeMessageBuilder_ == null) {
                    if (!other.noticeMessage_.isEmpty()) {
                        if (noticeMessage_.isEmpty()) {
                            noticeMessage_ = other.noticeMessage_;
                            bitField0_ = (bitField0_ & ~0x00000001);
                        } else {
                            ensureNoticeMessageIsMutable();
                            noticeMessage_.addAll(other.noticeMessage_);
                        }
                        onChanged();
                    }
                } else {
                    if (!other.noticeMessage_.isEmpty()) {
                        if (noticeMessageBuilder_.isEmpty()) {
                            noticeMessageBuilder_.dispose();
                            noticeMessageBuilder_ = null;
                            noticeMessage_ = other.noticeMessage_;
                            bitField0_ = (bitField0_ & ~0x00000001);
                            noticeMessageBuilder_ =
                                    com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                                            getNoticeMessageFieldBuilder() : null;
                        } else {
                            noticeMessageBuilder_.addAllMessages(other.noticeMessage_);
                        }
                    }
                }
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                MsgBean.RequestGroupMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (MsgBean.RequestGroupMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            private int bitField0_;

            private java.util.List<MsgBean.GroupNoticeMessage> noticeMessage_ =
                    java.util.Collections.emptyList();
            private void ensureNoticeMessageIsMutable() {
                if (!((bitField0_ & 0x00000001) == 0x00000001)) {
                    noticeMessage_ = new java.util.ArrayList<MsgBean.GroupNoticeMessage>(noticeMessage_);
                    bitField0_ |= 0x00000001;
                }
            }

            private com.google.protobuf.RepeatedFieldBuilderV3<
                    MsgBean.GroupNoticeMessage, MsgBean.GroupNoticeMessage.Builder, MsgBean.GroupNoticeMessageOrBuilder> noticeMessageBuilder_;

            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public java.util.List<MsgBean.GroupNoticeMessage> getNoticeMessageList() {
                if (noticeMessageBuilder_ == null) {
                    return java.util.Collections.unmodifiableList(noticeMessage_);
                } else {
                    return noticeMessageBuilder_.getMessageList();
                }
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public int getNoticeMessageCount() {
                if (noticeMessageBuilder_ == null) {
                    return noticeMessage_.size();
                } else {
                    return noticeMessageBuilder_.getCount();
                }
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public MsgBean.GroupNoticeMessage getNoticeMessage(int index) {
                if (noticeMessageBuilder_ == null) {
                    return noticeMessage_.get(index);
                } else {
                    return noticeMessageBuilder_.getMessage(index);
                }
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public Builder setNoticeMessage(
                    int index, MsgBean.GroupNoticeMessage value) {
                if (noticeMessageBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureNoticeMessageIsMutable();
                    noticeMessage_.set(index, value);
                    onChanged();
                } else {
                    noticeMessageBuilder_.setMessage(index, value);
                }
                return this;
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public Builder setNoticeMessage(
                    int index, MsgBean.GroupNoticeMessage.Builder builderForValue) {
                if (noticeMessageBuilder_ == null) {
                    ensureNoticeMessageIsMutable();
                    noticeMessage_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    noticeMessageBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public Builder addNoticeMessage(MsgBean.GroupNoticeMessage value) {
                if (noticeMessageBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureNoticeMessageIsMutable();
                    noticeMessage_.add(value);
                    onChanged();
                } else {
                    noticeMessageBuilder_.addMessage(value);
                }
                return this;
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public Builder addNoticeMessage(
                    int index, MsgBean.GroupNoticeMessage value) {
                if (noticeMessageBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureNoticeMessageIsMutable();
                    noticeMessage_.add(index, value);
                    onChanged();
                } else {
                    noticeMessageBuilder_.addMessage(index, value);
                }
                return this;
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public Builder addNoticeMessage(
                    MsgBean.GroupNoticeMessage.Builder builderForValue) {
                if (noticeMessageBuilder_ == null) {
                    ensureNoticeMessageIsMutable();
                    noticeMessage_.add(builderForValue.build());
                    onChanged();
                } else {
                    noticeMessageBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public Builder addNoticeMessage(
                    int index, MsgBean.GroupNoticeMessage.Builder builderForValue) {
                if (noticeMessageBuilder_ == null) {
                    ensureNoticeMessageIsMutable();
                    noticeMessage_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    noticeMessageBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public Builder addAllNoticeMessage(
                    java.lang.Iterable<? extends MsgBean.GroupNoticeMessage> values) {
                if (noticeMessageBuilder_ == null) {
                    ensureNoticeMessageIsMutable();
                    com.google.protobuf.AbstractMessageLite.Builder.addAll(
                            values, noticeMessage_);
                    onChanged();
                } else {
                    noticeMessageBuilder_.addAllMessages(values);
                }
                return this;
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public Builder clearNoticeMessage() {
                if (noticeMessageBuilder_ == null) {
                    noticeMessage_ = java.util.Collections.emptyList();
                    bitField0_ = (bitField0_ & ~0x00000001);
                    onChanged();
                } else {
                    noticeMessageBuilder_.clear();
                }
                return this;
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public Builder removeNoticeMessage(int index) {
                if (noticeMessageBuilder_ == null) {
                    ensureNoticeMessageIsMutable();
                    noticeMessage_.remove(index);
                    onChanged();
                } else {
                    noticeMessageBuilder_.remove(index);
                }
                return this;
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public MsgBean.GroupNoticeMessage.Builder getNoticeMessageBuilder(
                    int index) {
                return getNoticeMessageFieldBuilder().getBuilder(index);
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public MsgBean.GroupNoticeMessageOrBuilder getNoticeMessageOrBuilder(
                    int index) {
                if (noticeMessageBuilder_ == null) {
                    return noticeMessage_.get(index);  } else {
                    return noticeMessageBuilder_.getMessageOrBuilder(index);
                }
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public java.util.List<? extends MsgBean.GroupNoticeMessageOrBuilder>
            getNoticeMessageOrBuilderList() {
                if (noticeMessageBuilder_ != null) {
                    return noticeMessageBuilder_.getMessageOrBuilderList();
                } else {
                    return java.util.Collections.unmodifiableList(noticeMessage_);
                }
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public MsgBean.GroupNoticeMessage.Builder addNoticeMessageBuilder() {
                return getNoticeMessageFieldBuilder().addBuilder(
                        MsgBean.GroupNoticeMessage.getDefaultInstance());
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public MsgBean.GroupNoticeMessage.Builder addNoticeMessageBuilder(
                    int index) {
                return getNoticeMessageFieldBuilder().addBuilder(
                        index, MsgBean.GroupNoticeMessage.getDefaultInstance());
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public java.util.List<MsgBean.GroupNoticeMessage.Builder>
            getNoticeMessageBuilderList() {
                return getNoticeMessageFieldBuilder().getBuilderList();
            }
            private com.google.protobuf.RepeatedFieldBuilderV3<
                    MsgBean.GroupNoticeMessage, MsgBean.GroupNoticeMessage.Builder, MsgBean.GroupNoticeMessageOrBuilder>
            getNoticeMessageFieldBuilder() {
                if (noticeMessageBuilder_ == null) {
                    noticeMessageBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
                            MsgBean.GroupNoticeMessage, MsgBean.GroupNoticeMessage.Builder, MsgBean.GroupNoticeMessageOrBuilder>(
                            noticeMessage_,
                            ((bitField0_ & 0x00000001) == 0x00000001),
                            getParentForChildren(),
                            isClean());
                    noticeMessage_ = null;
                }
                return noticeMessageBuilder_;
            }
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:RequestGroupMessage)
        }

        // @@protoc_insertion_point(class_scope:RequestGroupMessage)
        private static final MsgBean.RequestGroupMessage DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new MsgBean.RequestGroupMessage();
        }

        public static MsgBean.RequestGroupMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<RequestGroupMessage>
                PARSER = new com.google.protobuf.AbstractParser<RequestGroupMessage>() {
            public RequestGroupMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new RequestGroupMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<RequestGroupMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<RequestGroupMessage> getParserForType() {
            return PARSER;
        }

        public MsgBean.RequestGroupMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface AcceptBeGroupMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:AcceptBeGroupMessage)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
         */
        java.util.List<MsgBean.GroupNoticeMessage>
        getNoticeMessageList();
        /**
         * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
         */
        MsgBean.GroupNoticeMessage getNoticeMessage(int index);
        /**
         * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
         */
        int getNoticeMessageCount();
        /**
         * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
         */
        java.util.List<? extends MsgBean.GroupNoticeMessageOrBuilder>
        getNoticeMessageOrBuilderList();
        /**
         * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
         */
        MsgBean.GroupNoticeMessageOrBuilder getNoticeMessageOrBuilder(
                int index);
    }
    /**
     * <pre>
     * 接受入群请求
     * </pre>
     *
     * Protobuf type {@code AcceptBeGroupMessage}
     */
    public  static final class AcceptBeGroupMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:AcceptBeGroupMessage)
            AcceptBeGroupMessageOrBuilder {
        // Use AcceptBeGroupMessage.newBuilder() to construct.
        private AcceptBeGroupMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }
        private AcceptBeGroupMessage() {
            noticeMessage_ = java.util.Collections.emptyList();
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }
        private AcceptBeGroupMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            int mutable_bitField0_ = 0;
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 10: {
                            if (!((mutable_bitField0_ & 0x00000001) == 0x00000001)) {
                                noticeMessage_ = new java.util.ArrayList<MsgBean.GroupNoticeMessage>();
                                mutable_bitField0_ |= 0x00000001;
                            }
                            noticeMessage_.add(
                                    input.readMessage(MsgBean.GroupNoticeMessage.parser(), extensionRegistry));
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                if (((mutable_bitField0_ & 0x00000001) == 0x00000001)) {
                    noticeMessage_ = java.util.Collections.unmodifiableList(noticeMessage_);
                }
                makeExtensionsImmutable();
            }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return MsgBean.internal_static_AcceptBeGroupMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return MsgBean.internal_static_AcceptBeGroupMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            MsgBean.AcceptBeGroupMessage.class, MsgBean.AcceptBeGroupMessage.Builder.class);
        }

        public static final int NOTICE_MESSAGE_FIELD_NUMBER = 1;
        private java.util.List<MsgBean.GroupNoticeMessage> noticeMessage_;
        /**
         * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
         */
        public java.util.List<MsgBean.GroupNoticeMessage> getNoticeMessageList() {
            return noticeMessage_;
        }
        /**
         * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
         */
        public java.util.List<? extends MsgBean.GroupNoticeMessageOrBuilder>
        getNoticeMessageOrBuilderList() {
            return noticeMessage_;
        }
        /**
         * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
         */
        public int getNoticeMessageCount() {
            return noticeMessage_.size();
        }
        /**
         * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
         */
        public MsgBean.GroupNoticeMessage getNoticeMessage(int index) {
            return noticeMessage_.get(index);
        }
        /**
         * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
         */
        public MsgBean.GroupNoticeMessageOrBuilder getNoticeMessageOrBuilder(
                int index) {
            return noticeMessage_.get(index);
        }

        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            for (int i = 0; i < noticeMessage_.size(); i++) {
                output.writeMessage(1, noticeMessage_.get(i));
            }
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            for (int i = 0; i < noticeMessage_.size(); i++) {
                size += com.google.protobuf.CodedOutputStream
                        .computeMessageSize(1, noticeMessage_.get(i));
            }
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MsgBean.AcceptBeGroupMessage)) {
                return super.equals(obj);
            }
            MsgBean.AcceptBeGroupMessage other = (MsgBean.AcceptBeGroupMessage) obj;

            boolean result = true;
            result = result && getNoticeMessageList()
                    .equals(other.getNoticeMessageList());
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            if (getNoticeMessageCount() > 0) {
                hash = (37 * hash) + NOTICE_MESSAGE_FIELD_NUMBER;
                hash = (53 * hash) + getNoticeMessageList().hashCode();
            }
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static MsgBean.AcceptBeGroupMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.AcceptBeGroupMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.AcceptBeGroupMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.AcceptBeGroupMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.AcceptBeGroupMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.AcceptBeGroupMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.AcceptBeGroupMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.AcceptBeGroupMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.AcceptBeGroupMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }
        public static MsgBean.AcceptBeGroupMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.AcceptBeGroupMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.AcceptBeGroupMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MsgBean.AcceptBeGroupMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        /**
         * <pre>
         * 接受入群请求
         * </pre>
         *
         * Protobuf type {@code AcceptBeGroupMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:AcceptBeGroupMessage)
                MsgBean.AcceptBeGroupMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_AcceptBeGroupMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_AcceptBeGroupMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.AcceptBeGroupMessage.class, MsgBean.AcceptBeGroupMessage.Builder.class);
            }

            // Construct using MsgBean.AcceptBeGroupMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                    getNoticeMessageFieldBuilder();
                }
            }
            public Builder clear() {
                super.clear();
                if (noticeMessageBuilder_ == null) {
                    noticeMessage_ = java.util.Collections.emptyList();
                    bitField0_ = (bitField0_ & ~0x00000001);
                } else {
                    noticeMessageBuilder_.clear();
                }
                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return MsgBean.internal_static_AcceptBeGroupMessage_descriptor;
            }

            public MsgBean.AcceptBeGroupMessage getDefaultInstanceForType() {
                return MsgBean.AcceptBeGroupMessage.getDefaultInstance();
            }

            public MsgBean.AcceptBeGroupMessage build() {
                MsgBean.AcceptBeGroupMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public MsgBean.AcceptBeGroupMessage buildPartial() {
                MsgBean.AcceptBeGroupMessage result = new MsgBean.AcceptBeGroupMessage(this);
                int from_bitField0_ = bitField0_;
                if (noticeMessageBuilder_ == null) {
                    if (((bitField0_ & 0x00000001) == 0x00000001)) {
                        noticeMessage_ = java.util.Collections.unmodifiableList(noticeMessage_);
                        bitField0_ = (bitField0_ & ~0x00000001);
                    }
                    result.noticeMessage_ = noticeMessage_;
                } else {
                    result.noticeMessage_ = noticeMessageBuilder_.build();
                }
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof MsgBean.AcceptBeGroupMessage) {
                    return mergeFrom((MsgBean.AcceptBeGroupMessage)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MsgBean.AcceptBeGroupMessage other) {
                if (other == MsgBean.AcceptBeGroupMessage.getDefaultInstance()) return this;
                if (noticeMessageBuilder_ == null) {
                    if (!other.noticeMessage_.isEmpty()) {
                        if (noticeMessage_.isEmpty()) {
                            noticeMessage_ = other.noticeMessage_;
                            bitField0_ = (bitField0_ & ~0x00000001);
                        } else {
                            ensureNoticeMessageIsMutable();
                            noticeMessage_.addAll(other.noticeMessage_);
                        }
                        onChanged();
                    }
                } else {
                    if (!other.noticeMessage_.isEmpty()) {
                        if (noticeMessageBuilder_.isEmpty()) {
                            noticeMessageBuilder_.dispose();
                            noticeMessageBuilder_ = null;
                            noticeMessage_ = other.noticeMessage_;
                            bitField0_ = (bitField0_ & ~0x00000001);
                            noticeMessageBuilder_ =
                                    com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                                            getNoticeMessageFieldBuilder() : null;
                        } else {
                            noticeMessageBuilder_.addAllMessages(other.noticeMessage_);
                        }
                    }
                }
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                MsgBean.AcceptBeGroupMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (MsgBean.AcceptBeGroupMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            private int bitField0_;

            private java.util.List<MsgBean.GroupNoticeMessage> noticeMessage_ =
                    java.util.Collections.emptyList();
            private void ensureNoticeMessageIsMutable() {
                if (!((bitField0_ & 0x00000001) == 0x00000001)) {
                    noticeMessage_ = new java.util.ArrayList<MsgBean.GroupNoticeMessage>(noticeMessage_);
                    bitField0_ |= 0x00000001;
                }
            }

            private com.google.protobuf.RepeatedFieldBuilderV3<
                    MsgBean.GroupNoticeMessage, MsgBean.GroupNoticeMessage.Builder, MsgBean.GroupNoticeMessageOrBuilder> noticeMessageBuilder_;

            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public java.util.List<MsgBean.GroupNoticeMessage> getNoticeMessageList() {
                if (noticeMessageBuilder_ == null) {
                    return java.util.Collections.unmodifiableList(noticeMessage_);
                } else {
                    return noticeMessageBuilder_.getMessageList();
                }
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public int getNoticeMessageCount() {
                if (noticeMessageBuilder_ == null) {
                    return noticeMessage_.size();
                } else {
                    return noticeMessageBuilder_.getCount();
                }
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public MsgBean.GroupNoticeMessage getNoticeMessage(int index) {
                if (noticeMessageBuilder_ == null) {
                    return noticeMessage_.get(index);
                } else {
                    return noticeMessageBuilder_.getMessage(index);
                }
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public Builder setNoticeMessage(
                    int index, MsgBean.GroupNoticeMessage value) {
                if (noticeMessageBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureNoticeMessageIsMutable();
                    noticeMessage_.set(index, value);
                    onChanged();
                } else {
                    noticeMessageBuilder_.setMessage(index, value);
                }
                return this;
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public Builder setNoticeMessage(
                    int index, MsgBean.GroupNoticeMessage.Builder builderForValue) {
                if (noticeMessageBuilder_ == null) {
                    ensureNoticeMessageIsMutable();
                    noticeMessage_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    noticeMessageBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public Builder addNoticeMessage(MsgBean.GroupNoticeMessage value) {
                if (noticeMessageBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureNoticeMessageIsMutable();
                    noticeMessage_.add(value);
                    onChanged();
                } else {
                    noticeMessageBuilder_.addMessage(value);
                }
                return this;
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public Builder addNoticeMessage(
                    int index, MsgBean.GroupNoticeMessage value) {
                if (noticeMessageBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureNoticeMessageIsMutable();
                    noticeMessage_.add(index, value);
                    onChanged();
                } else {
                    noticeMessageBuilder_.addMessage(index, value);
                }
                return this;
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public Builder addNoticeMessage(
                    MsgBean.GroupNoticeMessage.Builder builderForValue) {
                if (noticeMessageBuilder_ == null) {
                    ensureNoticeMessageIsMutable();
                    noticeMessage_.add(builderForValue.build());
                    onChanged();
                } else {
                    noticeMessageBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public Builder addNoticeMessage(
                    int index, MsgBean.GroupNoticeMessage.Builder builderForValue) {
                if (noticeMessageBuilder_ == null) {
                    ensureNoticeMessageIsMutable();
                    noticeMessage_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    noticeMessageBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public Builder addAllNoticeMessage(
                    java.lang.Iterable<? extends MsgBean.GroupNoticeMessage> values) {
                if (noticeMessageBuilder_ == null) {
                    ensureNoticeMessageIsMutable();
                    com.google.protobuf.AbstractMessageLite.Builder.addAll(
                            values, noticeMessage_);
                    onChanged();
                } else {
                    noticeMessageBuilder_.addAllMessages(values);
                }
                return this;
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public Builder clearNoticeMessage() {
                if (noticeMessageBuilder_ == null) {
                    noticeMessage_ = java.util.Collections.emptyList();
                    bitField0_ = (bitField0_ & ~0x00000001);
                    onChanged();
                } else {
                    noticeMessageBuilder_.clear();
                }
                return this;
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public Builder removeNoticeMessage(int index) {
                if (noticeMessageBuilder_ == null) {
                    ensureNoticeMessageIsMutable();
                    noticeMessage_.remove(index);
                    onChanged();
                } else {
                    noticeMessageBuilder_.remove(index);
                }
                return this;
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public MsgBean.GroupNoticeMessage.Builder getNoticeMessageBuilder(
                    int index) {
                return getNoticeMessageFieldBuilder().getBuilder(index);
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public MsgBean.GroupNoticeMessageOrBuilder getNoticeMessageOrBuilder(
                    int index) {
                if (noticeMessageBuilder_ == null) {
                    return noticeMessage_.get(index);  } else {
                    return noticeMessageBuilder_.getMessageOrBuilder(index);
                }
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public java.util.List<? extends MsgBean.GroupNoticeMessageOrBuilder>
            getNoticeMessageOrBuilderList() {
                if (noticeMessageBuilder_ != null) {
                    return noticeMessageBuilder_.getMessageOrBuilderList();
                } else {
                    return java.util.Collections.unmodifiableList(noticeMessage_);
                }
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public MsgBean.GroupNoticeMessage.Builder addNoticeMessageBuilder() {
                return getNoticeMessageFieldBuilder().addBuilder(
                        MsgBean.GroupNoticeMessage.getDefaultInstance());
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public MsgBean.GroupNoticeMessage.Builder addNoticeMessageBuilder(
                    int index) {
                return getNoticeMessageFieldBuilder().addBuilder(
                        index, MsgBean.GroupNoticeMessage.getDefaultInstance());
            }
            /**
             * <code>repeated .GroupNoticeMessage notice_message = 1;</code>
             */
            public java.util.List<MsgBean.GroupNoticeMessage.Builder>
            getNoticeMessageBuilderList() {
                return getNoticeMessageFieldBuilder().getBuilderList();
            }
            private com.google.protobuf.RepeatedFieldBuilderV3<
                    MsgBean.GroupNoticeMessage, MsgBean.GroupNoticeMessage.Builder, MsgBean.GroupNoticeMessageOrBuilder>
            getNoticeMessageFieldBuilder() {
                if (noticeMessageBuilder_ == null) {
                    noticeMessageBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
                            MsgBean.GroupNoticeMessage, MsgBean.GroupNoticeMessage.Builder, MsgBean.GroupNoticeMessageOrBuilder>(
                            noticeMessage_,
                            ((bitField0_ & 0x00000001) == 0x00000001),
                            getParentForChildren(),
                            isClean());
                    noticeMessage_ = null;
                }
                return noticeMessageBuilder_;
            }
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:AcceptBeGroupMessage)
        }

        // @@protoc_insertion_point(class_scope:AcceptBeGroupMessage)
        private static final MsgBean.AcceptBeGroupMessage DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new MsgBean.AcceptBeGroupMessage();
        }

        public static MsgBean.AcceptBeGroupMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<AcceptBeGroupMessage>
                PARSER = new com.google.protobuf.AbstractParser<AcceptBeGroupMessage>() {
            public AcceptBeGroupMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new AcceptBeGroupMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<AcceptBeGroupMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<AcceptBeGroupMessage> getParserForType() {
            return PARSER;
        }

        public MsgBean.AcceptBeGroupMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface RemoveGroupMemberMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:RemoveGroupMemberMessage)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <pre>
         * 群id
         * </pre>
         *
         * <code>string gid = 1;</code>
         */
        java.lang.String getGid();
        /**
         * <pre>
         * 群id
         * </pre>
         *
         * <code>string gid = 1;</code>
         */
        com.google.protobuf.ByteString
        getGidBytes();

        /**
         * <pre>
         * 群头像
         * </pre>
         *
         * <code>string avatar = 2;</code>
         */
        java.lang.String getAvatar();
        /**
         * <pre>
         * 群头像
         * </pre>
         *
         * <code>string avatar = 2;</code>
         */
        com.google.protobuf.ByteString
        getAvatarBytes();

        /**
         * <pre>
         * 群名
         * </pre>
         *
         * <code>string name = 3;</code>
         */
        java.lang.String getName();
        /**
         * <pre>
         * 群名
         * </pre>
         *
         * <code>string name = 3;</code>
         */
        com.google.protobuf.ByteString
        getNameBytes();
    }
    /**
     * <pre>
     * 删除群成员
     * </pre>
     *
     * Protobuf type {@code RemoveGroupMemberMessage}
     */
    public  static final class RemoveGroupMemberMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:RemoveGroupMemberMessage)
            RemoveGroupMemberMessageOrBuilder {
        // Use RemoveGroupMemberMessage.newBuilder() to construct.
        private RemoveGroupMemberMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }
        private RemoveGroupMemberMessage() {
            gid_ = "";
            avatar_ = "";
            name_ = "";
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }
        private RemoveGroupMemberMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            int mutable_bitField0_ = 0;
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 10: {
                            java.lang.String s = input.readStringRequireUtf8();

                            gid_ = s;
                            break;
                        }
                        case 18: {
                            java.lang.String s = input.readStringRequireUtf8();

                            avatar_ = s;
                            break;
                        }
                        case 26: {
                            java.lang.String s = input.readStringRequireUtf8();

                            name_ = s;
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                makeExtensionsImmutable();
            }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return MsgBean.internal_static_RemoveGroupMemberMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return MsgBean.internal_static_RemoveGroupMemberMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            MsgBean.RemoveGroupMemberMessage.class, MsgBean.RemoveGroupMemberMessage.Builder.class);
        }

        public static final int GID_FIELD_NUMBER = 1;
        private volatile java.lang.Object gid_;
        /**
         * <pre>
         * 群id
         * </pre>
         *
         * <code>string gid = 1;</code>
         */
        public java.lang.String getGid() {
            java.lang.Object ref = gid_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                gid_ = s;
                return s;
            }
        }
        /**
         * <pre>
         * 群id
         * </pre>
         *
         * <code>string gid = 1;</code>
         */
        public com.google.protobuf.ByteString
        getGidBytes() {
            java.lang.Object ref = gid_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                gid_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        public static final int AVATAR_FIELD_NUMBER = 2;
        private volatile java.lang.Object avatar_;
        /**
         * <pre>
         * 群头像
         * </pre>
         *
         * <code>string avatar = 2;</code>
         */
        public java.lang.String getAvatar() {
            java.lang.Object ref = avatar_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                avatar_ = s;
                return s;
            }
        }
        /**
         * <pre>
         * 群头像
         * </pre>
         *
         * <code>string avatar = 2;</code>
         */
        public com.google.protobuf.ByteString
        getAvatarBytes() {
            java.lang.Object ref = avatar_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                avatar_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        public static final int NAME_FIELD_NUMBER = 3;
        private volatile java.lang.Object name_;
        /**
         * <pre>
         * 群名
         * </pre>
         *
         * <code>string name = 3;</code>
         */
        public java.lang.String getName() {
            java.lang.Object ref = name_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                name_ = s;
                return s;
            }
        }
        /**
         * <pre>
         * 群名
         * </pre>
         *
         * <code>string name = 3;</code>
         */
        public com.google.protobuf.ByteString
        getNameBytes() {
            java.lang.Object ref = name_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                name_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            if (!getGidBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 1, gid_);
            }
            if (!getAvatarBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 2, avatar_);
            }
            if (!getNameBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 3, name_);
            }
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (!getGidBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, gid_);
            }
            if (!getAvatarBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, avatar_);
            }
            if (!getNameBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, name_);
            }
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MsgBean.RemoveGroupMemberMessage)) {
                return super.equals(obj);
            }
            MsgBean.RemoveGroupMemberMessage other = (MsgBean.RemoveGroupMemberMessage) obj;

            boolean result = true;
            result = result && getGid()
                    .equals(other.getGid());
            result = result && getAvatar()
                    .equals(other.getAvatar());
            result = result && getName()
                    .equals(other.getName());
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (37 * hash) + GID_FIELD_NUMBER;
            hash = (53 * hash) + getGid().hashCode();
            hash = (37 * hash) + AVATAR_FIELD_NUMBER;
            hash = (53 * hash) + getAvatar().hashCode();
            hash = (37 * hash) + NAME_FIELD_NUMBER;
            hash = (53 * hash) + getName().hashCode();
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static MsgBean.RemoveGroupMemberMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.RemoveGroupMemberMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.RemoveGroupMemberMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.RemoveGroupMemberMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.RemoveGroupMemberMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.RemoveGroupMemberMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.RemoveGroupMemberMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.RemoveGroupMemberMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.RemoveGroupMemberMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }
        public static MsgBean.RemoveGroupMemberMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.RemoveGroupMemberMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.RemoveGroupMemberMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MsgBean.RemoveGroupMemberMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        /**
         * <pre>
         * 删除群成员
         * </pre>
         *
         * Protobuf type {@code RemoveGroupMemberMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:RemoveGroupMemberMessage)
                MsgBean.RemoveGroupMemberMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_RemoveGroupMemberMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_RemoveGroupMemberMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.RemoveGroupMemberMessage.class, MsgBean.RemoveGroupMemberMessage.Builder.class);
            }

            // Construct using MsgBean.RemoveGroupMemberMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }
            public Builder clear() {
                super.clear();
                gid_ = "";

                avatar_ = "";

                name_ = "";

                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return MsgBean.internal_static_RemoveGroupMemberMessage_descriptor;
            }

            public MsgBean.RemoveGroupMemberMessage getDefaultInstanceForType() {
                return MsgBean.RemoveGroupMemberMessage.getDefaultInstance();
            }

            public MsgBean.RemoveGroupMemberMessage build() {
                MsgBean.RemoveGroupMemberMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public MsgBean.RemoveGroupMemberMessage buildPartial() {
                MsgBean.RemoveGroupMemberMessage result = new MsgBean.RemoveGroupMemberMessage(this);
                result.gid_ = gid_;
                result.avatar_ = avatar_;
                result.name_ = name_;
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof MsgBean.RemoveGroupMemberMessage) {
                    return mergeFrom((MsgBean.RemoveGroupMemberMessage)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MsgBean.RemoveGroupMemberMessage other) {
                if (other == MsgBean.RemoveGroupMemberMessage.getDefaultInstance()) return this;
                if (!other.getGid().isEmpty()) {
                    gid_ = other.gid_;
                    onChanged();
                }
                if (!other.getAvatar().isEmpty()) {
                    avatar_ = other.avatar_;
                    onChanged();
                }
                if (!other.getName().isEmpty()) {
                    name_ = other.name_;
                    onChanged();
                }
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                MsgBean.RemoveGroupMemberMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (MsgBean.RemoveGroupMemberMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private java.lang.Object gid_ = "";
            /**
             * <pre>
             * 群id
             * </pre>
             *
             * <code>string gid = 1;</code>
             */
            public java.lang.String getGid() {
                java.lang.Object ref = gid_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    gid_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }
            /**
             * <pre>
             * 群id
             * </pre>
             *
             * <code>string gid = 1;</code>
             */
            public com.google.protobuf.ByteString
            getGidBytes() {
                java.lang.Object ref = gid_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    gid_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }
            /**
             * <pre>
             * 群id
             * </pre>
             *
             * <code>string gid = 1;</code>
             */
            public Builder setGid(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                gid_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 群id
             * </pre>
             *
             * <code>string gid = 1;</code>
             */
            public Builder clearGid() {

                gid_ = getDefaultInstance().getGid();
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 群id
             * </pre>
             *
             * <code>string gid = 1;</code>
             */
            public Builder setGidBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                gid_ = value;
                onChanged();
                return this;
            }

            private java.lang.Object avatar_ = "";
            /**
             * <pre>
             * 群头像
             * </pre>
             *
             * <code>string avatar = 2;</code>
             */
            public java.lang.String getAvatar() {
                java.lang.Object ref = avatar_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    avatar_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }
            /**
             * <pre>
             * 群头像
             * </pre>
             *
             * <code>string avatar = 2;</code>
             */
            public com.google.protobuf.ByteString
            getAvatarBytes() {
                java.lang.Object ref = avatar_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    avatar_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }
            /**
             * <pre>
             * 群头像
             * </pre>
             *
             * <code>string avatar = 2;</code>
             */
            public Builder setAvatar(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                avatar_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 群头像
             * </pre>
             *
             * <code>string avatar = 2;</code>
             */
            public Builder clearAvatar() {

                avatar_ = getDefaultInstance().getAvatar();
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 群头像
             * </pre>
             *
             * <code>string avatar = 2;</code>
             */
            public Builder setAvatarBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                avatar_ = value;
                onChanged();
                return this;
            }

            private java.lang.Object name_ = "";
            /**
             * <pre>
             * 群名
             * </pre>
             *
             * <code>string name = 3;</code>
             */
            public java.lang.String getName() {
                java.lang.Object ref = name_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    name_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }
            /**
             * <pre>
             * 群名
             * </pre>
             *
             * <code>string name = 3;</code>
             */
            public com.google.protobuf.ByteString
            getNameBytes() {
                java.lang.Object ref = name_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    name_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }
            /**
             * <pre>
             * 群名
             * </pre>
             *
             * <code>string name = 3;</code>
             */
            public Builder setName(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                name_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 群名
             * </pre>
             *
             * <code>string name = 3;</code>
             */
            public Builder clearName() {

                name_ = getDefaultInstance().getName();
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 群名
             * </pre>
             *
             * <code>string name = 3;</code>
             */
            public Builder setNameBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                name_ = value;
                onChanged();
                return this;
            }
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:RemoveGroupMemberMessage)
        }

        // @@protoc_insertion_point(class_scope:RemoveGroupMemberMessage)
        private static final MsgBean.RemoveGroupMemberMessage DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new MsgBean.RemoveGroupMemberMessage();
        }

        public static MsgBean.RemoveGroupMemberMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<RemoveGroupMemberMessage>
                PARSER = new com.google.protobuf.AbstractParser<RemoveGroupMemberMessage>() {
            public RemoveGroupMemberMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new RemoveGroupMemberMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<RemoveGroupMemberMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<RemoveGroupMemberMessage> getParserForType() {
            return PARSER;
        }

        public MsgBean.RemoveGroupMemberMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface ChangeGroupMasterMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:ChangeGroupMasterMessage)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <pre>
         * 新群主uid
         * </pre>
         *
         * <code>uint64 uid = 1;</code>
         */
        long getUid();
    }
    /**
     * <pre>
     * 转让群
     * </pre>
     *
     * Protobuf type {@code ChangeGroupMasterMessage}
     */
    public  static final class ChangeGroupMasterMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:ChangeGroupMasterMessage)
            ChangeGroupMasterMessageOrBuilder {
        // Use ChangeGroupMasterMessage.newBuilder() to construct.
        private ChangeGroupMasterMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }
        private ChangeGroupMasterMessage() {
            uid_ = 0L;
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }
        private ChangeGroupMasterMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            int mutable_bitField0_ = 0;
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 8: {

                            uid_ = input.readUInt64();
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                makeExtensionsImmutable();
            }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return MsgBean.internal_static_ChangeGroupMasterMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return MsgBean.internal_static_ChangeGroupMasterMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            MsgBean.ChangeGroupMasterMessage.class, MsgBean.ChangeGroupMasterMessage.Builder.class);
        }

        public static final int UID_FIELD_NUMBER = 1;
        private long uid_;
        /**
         * <pre>
         * 新群主uid
         * </pre>
         *
         * <code>uint64 uid = 1;</code>
         */
        public long getUid() {
            return uid_;
        }

        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            if (uid_ != 0L) {
                output.writeUInt64(1, uid_);
            }
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (uid_ != 0L) {
                size += com.google.protobuf.CodedOutputStream
                        .computeUInt64Size(1, uid_);
            }
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MsgBean.ChangeGroupMasterMessage)) {
                return super.equals(obj);
            }
            MsgBean.ChangeGroupMasterMessage other = (MsgBean.ChangeGroupMasterMessage) obj;

            boolean result = true;
            result = result && (getUid()
                    == other.getUid());
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (37 * hash) + UID_FIELD_NUMBER;
            hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
                    getUid());
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static MsgBean.ChangeGroupMasterMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.ChangeGroupMasterMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.ChangeGroupMasterMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.ChangeGroupMasterMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.ChangeGroupMasterMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.ChangeGroupMasterMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.ChangeGroupMasterMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.ChangeGroupMasterMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.ChangeGroupMasterMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }
        public static MsgBean.ChangeGroupMasterMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.ChangeGroupMasterMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.ChangeGroupMasterMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MsgBean.ChangeGroupMasterMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        /**
         * <pre>
         * 转让群
         * </pre>
         *
         * Protobuf type {@code ChangeGroupMasterMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:ChangeGroupMasterMessage)
                MsgBean.ChangeGroupMasterMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_ChangeGroupMasterMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_ChangeGroupMasterMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.ChangeGroupMasterMessage.class, MsgBean.ChangeGroupMasterMessage.Builder.class);
            }

            // Construct using MsgBean.ChangeGroupMasterMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }
            public Builder clear() {
                super.clear();
                uid_ = 0L;

                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return MsgBean.internal_static_ChangeGroupMasterMessage_descriptor;
            }

            public MsgBean.ChangeGroupMasterMessage getDefaultInstanceForType() {
                return MsgBean.ChangeGroupMasterMessage.getDefaultInstance();
            }

            public MsgBean.ChangeGroupMasterMessage build() {
                MsgBean.ChangeGroupMasterMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public MsgBean.ChangeGroupMasterMessage buildPartial() {
                MsgBean.ChangeGroupMasterMessage result = new MsgBean.ChangeGroupMasterMessage(this);
                result.uid_ = uid_;
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof MsgBean.ChangeGroupMasterMessage) {
                    return mergeFrom((MsgBean.ChangeGroupMasterMessage)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MsgBean.ChangeGroupMasterMessage other) {
                if (other == MsgBean.ChangeGroupMasterMessage.getDefaultInstance()) return this;
                if (other.getUid() != 0L) {
                    setUid(other.getUid());
                }
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                MsgBean.ChangeGroupMasterMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (MsgBean.ChangeGroupMasterMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private long uid_ ;
            /**
             * <pre>
             * 新群主uid
             * </pre>
             *
             * <code>uint64 uid = 1;</code>
             */
            public long getUid() {
                return uid_;
            }
            /**
             * <pre>
             * 新群主uid
             * </pre>
             *
             * <code>uint64 uid = 1;</code>
             */
            public Builder setUid(long value) {

                uid_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 新群主uid
             * </pre>
             *
             * <code>uint64 uid = 1;</code>
             */
            public Builder clearUid() {

                uid_ = 0L;
                onChanged();
                return this;
            }
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:ChangeGroupMasterMessage)
        }

        // @@protoc_insertion_point(class_scope:ChangeGroupMasterMessage)
        private static final MsgBean.ChangeGroupMasterMessage DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new MsgBean.ChangeGroupMasterMessage();
        }

        public static MsgBean.ChangeGroupMasterMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<ChangeGroupMasterMessage>
                PARSER = new com.google.protobuf.AbstractParser<ChangeGroupMasterMessage>() {
            public ChangeGroupMasterMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new ChangeGroupMasterMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<ChangeGroupMasterMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<ChangeGroupMasterMessage> getParserForType() {
            return PARSER;
        }

        public MsgBean.ChangeGroupMasterMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface ChangeGroupNameMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:ChangeGroupNameMessage)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <pre>
         * 新的群名
         * </pre>
         *
         * <code>string name = 1;</code>
         */
        java.lang.String getName();
        /**
         * <pre>
         * 新的群名
         * </pre>
         *
         * <code>string name = 1;</code>
         */
        com.google.protobuf.ByteString
        getNameBytes();
    }
    /**
     * <pre>
     * 修改群名信息
     * </pre>
     *
     * Protobuf type {@code ChangeGroupNameMessage}
     */
    public  static final class ChangeGroupNameMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:ChangeGroupNameMessage)
            ChangeGroupNameMessageOrBuilder {
        // Use ChangeGroupNameMessage.newBuilder() to construct.
        private ChangeGroupNameMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }
        private ChangeGroupNameMessage() {
            name_ = "";
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }
        private ChangeGroupNameMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            int mutable_bitField0_ = 0;
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 10: {
                            java.lang.String s = input.readStringRequireUtf8();

                            name_ = s;
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                makeExtensionsImmutable();
            }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return MsgBean.internal_static_ChangeGroupNameMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return MsgBean.internal_static_ChangeGroupNameMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            MsgBean.ChangeGroupNameMessage.class, MsgBean.ChangeGroupNameMessage.Builder.class);
        }

        public static final int NAME_FIELD_NUMBER = 1;
        private volatile java.lang.Object name_;
        /**
         * <pre>
         * 新的群名
         * </pre>
         *
         * <code>string name = 1;</code>
         */
        public java.lang.String getName() {
            java.lang.Object ref = name_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                name_ = s;
                return s;
            }
        }
        /**
         * <pre>
         * 新的群名
         * </pre>
         *
         * <code>string name = 1;</code>
         */
        public com.google.protobuf.ByteString
        getNameBytes() {
            java.lang.Object ref = name_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                name_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            if (!getNameBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 1, name_);
            }
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (!getNameBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, name_);
            }
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MsgBean.ChangeGroupNameMessage)) {
                return super.equals(obj);
            }
            MsgBean.ChangeGroupNameMessage other = (MsgBean.ChangeGroupNameMessage) obj;

            boolean result = true;
            result = result && getName()
                    .equals(other.getName());
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (37 * hash) + NAME_FIELD_NUMBER;
            hash = (53 * hash) + getName().hashCode();
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static MsgBean.ChangeGroupNameMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.ChangeGroupNameMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.ChangeGroupNameMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.ChangeGroupNameMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.ChangeGroupNameMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.ChangeGroupNameMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.ChangeGroupNameMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.ChangeGroupNameMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.ChangeGroupNameMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }
        public static MsgBean.ChangeGroupNameMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.ChangeGroupNameMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.ChangeGroupNameMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MsgBean.ChangeGroupNameMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        /**
         * <pre>
         * 修改群名信息
         * </pre>
         *
         * Protobuf type {@code ChangeGroupNameMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:ChangeGroupNameMessage)
                MsgBean.ChangeGroupNameMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_ChangeGroupNameMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_ChangeGroupNameMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.ChangeGroupNameMessage.class, MsgBean.ChangeGroupNameMessage.Builder.class);
            }

            // Construct using MsgBean.ChangeGroupNameMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }
            public Builder clear() {
                super.clear();
                name_ = "";

                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return MsgBean.internal_static_ChangeGroupNameMessage_descriptor;
            }

            public MsgBean.ChangeGroupNameMessage getDefaultInstanceForType() {
                return MsgBean.ChangeGroupNameMessage.getDefaultInstance();
            }

            public MsgBean.ChangeGroupNameMessage build() {
                MsgBean.ChangeGroupNameMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public MsgBean.ChangeGroupNameMessage buildPartial() {
                MsgBean.ChangeGroupNameMessage result = new MsgBean.ChangeGroupNameMessage(this);
                result.name_ = name_;
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof MsgBean.ChangeGroupNameMessage) {
                    return mergeFrom((MsgBean.ChangeGroupNameMessage)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MsgBean.ChangeGroupNameMessage other) {
                if (other == MsgBean.ChangeGroupNameMessage.getDefaultInstance()) return this;
                if (!other.getName().isEmpty()) {
                    name_ = other.name_;
                    onChanged();
                }
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                MsgBean.ChangeGroupNameMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (MsgBean.ChangeGroupNameMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private java.lang.Object name_ = "";
            /**
             * <pre>
             * 新的群名
             * </pre>
             *
             * <code>string name = 1;</code>
             */
            public java.lang.String getName() {
                java.lang.Object ref = name_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    name_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }
            /**
             * <pre>
             * 新的群名
             * </pre>
             *
             * <code>string name = 1;</code>
             */
            public com.google.protobuf.ByteString
            getNameBytes() {
                java.lang.Object ref = name_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    name_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }
            /**
             * <pre>
             * 新的群名
             * </pre>
             *
             * <code>string name = 1;</code>
             */
            public Builder setName(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                name_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 新的群名
             * </pre>
             *
             * <code>string name = 1;</code>
             */
            public Builder clearName() {

                name_ = getDefaultInstance().getName();
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 新的群名
             * </pre>
             *
             * <code>string name = 1;</code>
             */
            public Builder setNameBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                name_ = value;
                onChanged();
                return this;
            }
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:ChangeGroupNameMessage)
        }

        // @@protoc_insertion_point(class_scope:ChangeGroupNameMessage)
        private static final MsgBean.ChangeGroupNameMessage DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new MsgBean.ChangeGroupNameMessage();
        }

        public static MsgBean.ChangeGroupNameMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<ChangeGroupNameMessage>
                PARSER = new com.google.protobuf.AbstractParser<ChangeGroupNameMessage>() {
            public ChangeGroupNameMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new ChangeGroupNameMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<ChangeGroupNameMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<ChangeGroupNameMessage> getParserForType() {
            return PARSER;
        }

        public MsgBean.ChangeGroupNameMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface ChangeGroupAnnouncementMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:ChangeGroupAnnouncementMessage)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <pre>
         * 新的群公告
         * </pre>
         *
         * <code>string announcement = 1;</code>
         */
        java.lang.String getAnnouncement();
        /**
         * <pre>
         * 新的群公告
         * </pre>
         *
         * <code>string announcement = 1;</code>
         */
        com.google.protobuf.ByteString
        getAnnouncementBytes();
    }
    /**
     * <pre>
     * 修改群公告信息
     * </pre>
     *
     * Protobuf type {@code ChangeGroupAnnouncementMessage}
     */
    public  static final class ChangeGroupAnnouncementMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:ChangeGroupAnnouncementMessage)
            ChangeGroupAnnouncementMessageOrBuilder {
        // Use ChangeGroupAnnouncementMessage.newBuilder() to construct.
        private ChangeGroupAnnouncementMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }
        private ChangeGroupAnnouncementMessage() {
            announcement_ = "";
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }
        private ChangeGroupAnnouncementMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            int mutable_bitField0_ = 0;
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 10: {
                            java.lang.String s = input.readStringRequireUtf8();

                            announcement_ = s;
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                makeExtensionsImmutable();
            }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return MsgBean.internal_static_ChangeGroupAnnouncementMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return MsgBean.internal_static_ChangeGroupAnnouncementMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            MsgBean.ChangeGroupAnnouncementMessage.class, MsgBean.ChangeGroupAnnouncementMessage.Builder.class);
        }

        public static final int ANNOUNCEMENT_FIELD_NUMBER = 1;
        private volatile java.lang.Object announcement_;
        /**
         * <pre>
         * 新的群公告
         * </pre>
         *
         * <code>string announcement = 1;</code>
         */
        public java.lang.String getAnnouncement() {
            java.lang.Object ref = announcement_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                announcement_ = s;
                return s;
            }
        }
        /**
         * <pre>
         * 新的群公告
         * </pre>
         *
         * <code>string announcement = 1;</code>
         */
        public com.google.protobuf.ByteString
        getAnnouncementBytes() {
            java.lang.Object ref = announcement_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                announcement_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            if (!getAnnouncementBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 1, announcement_);
            }
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (!getAnnouncementBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, announcement_);
            }
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MsgBean.ChangeGroupAnnouncementMessage)) {
                return super.equals(obj);
            }
            MsgBean.ChangeGroupAnnouncementMessage other = (MsgBean.ChangeGroupAnnouncementMessage) obj;

            boolean result = true;
            result = result && getAnnouncement()
                    .equals(other.getAnnouncement());
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (37 * hash) + ANNOUNCEMENT_FIELD_NUMBER;
            hash = (53 * hash) + getAnnouncement().hashCode();
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static MsgBean.ChangeGroupAnnouncementMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.ChangeGroupAnnouncementMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.ChangeGroupAnnouncementMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.ChangeGroupAnnouncementMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.ChangeGroupAnnouncementMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.ChangeGroupAnnouncementMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.ChangeGroupAnnouncementMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.ChangeGroupAnnouncementMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.ChangeGroupAnnouncementMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }
        public static MsgBean.ChangeGroupAnnouncementMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.ChangeGroupAnnouncementMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.ChangeGroupAnnouncementMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MsgBean.ChangeGroupAnnouncementMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        /**
         * <pre>
         * 修改群公告信息
         * </pre>
         *
         * Protobuf type {@code ChangeGroupAnnouncementMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:ChangeGroupAnnouncementMessage)
                MsgBean.ChangeGroupAnnouncementMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_ChangeGroupAnnouncementMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_ChangeGroupAnnouncementMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.ChangeGroupAnnouncementMessage.class, MsgBean.ChangeGroupAnnouncementMessage.Builder.class);
            }

            // Construct using MsgBean.ChangeGroupAnnouncementMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }
            public Builder clear() {
                super.clear();
                announcement_ = "";

                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return MsgBean.internal_static_ChangeGroupAnnouncementMessage_descriptor;
            }

            public MsgBean.ChangeGroupAnnouncementMessage getDefaultInstanceForType() {
                return MsgBean.ChangeGroupAnnouncementMessage.getDefaultInstance();
            }

            public MsgBean.ChangeGroupAnnouncementMessage build() {
                MsgBean.ChangeGroupAnnouncementMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public MsgBean.ChangeGroupAnnouncementMessage buildPartial() {
                MsgBean.ChangeGroupAnnouncementMessage result = new MsgBean.ChangeGroupAnnouncementMessage(this);
                result.announcement_ = announcement_;
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof MsgBean.ChangeGroupAnnouncementMessage) {
                    return mergeFrom((MsgBean.ChangeGroupAnnouncementMessage)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MsgBean.ChangeGroupAnnouncementMessage other) {
                if (other == MsgBean.ChangeGroupAnnouncementMessage.getDefaultInstance()) return this;
                if (!other.getAnnouncement().isEmpty()) {
                    announcement_ = other.announcement_;
                    onChanged();
                }
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                MsgBean.ChangeGroupAnnouncementMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (MsgBean.ChangeGroupAnnouncementMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private java.lang.Object announcement_ = "";
            /**
             * <pre>
             * 新的群公告
             * </pre>
             *
             * <code>string announcement = 1;</code>
             */
            public java.lang.String getAnnouncement() {
                java.lang.Object ref = announcement_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    announcement_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }
            /**
             * <pre>
             * 新的群公告
             * </pre>
             *
             * <code>string announcement = 1;</code>
             */
            public com.google.protobuf.ByteString
            getAnnouncementBytes() {
                java.lang.Object ref = announcement_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    announcement_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }
            /**
             * <pre>
             * 新的群公告
             * </pre>
             *
             * <code>string announcement = 1;</code>
             */
            public Builder setAnnouncement(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                announcement_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 新的群公告
             * </pre>
             *
             * <code>string announcement = 1;</code>
             */
            public Builder clearAnnouncement() {

                announcement_ = getDefaultInstance().getAnnouncement();
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 新的群公告
             * </pre>
             *
             * <code>string announcement = 1;</code>
             */
            public Builder setAnnouncementBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                announcement_ = value;
                onChanged();
                return this;
            }
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:ChangeGroupAnnouncementMessage)
        }

        // @@protoc_insertion_point(class_scope:ChangeGroupAnnouncementMessage)
        private static final MsgBean.ChangeGroupAnnouncementMessage DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new MsgBean.ChangeGroupAnnouncementMessage();
        }

        public static MsgBean.ChangeGroupAnnouncementMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<ChangeGroupAnnouncementMessage>
                PARSER = new com.google.protobuf.AbstractParser<ChangeGroupAnnouncementMessage>() {
            public ChangeGroupAnnouncementMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new ChangeGroupAnnouncementMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<ChangeGroupAnnouncementMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<ChangeGroupAnnouncementMessage> getParserForType() {
            return PARSER;
        }

        public MsgBean.ChangeGroupAnnouncementMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface DestroyGroupMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:DestroyGroupMessage)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <pre>
         * 所有群成员
         * </pre>
         *
         * <code>repeated uint64 uid = 1;</code>
         */
        java.util.List<java.lang.Long> getUidList();
        /**
         * <pre>
         * 所有群成员
         * </pre>
         *
         * <code>repeated uint64 uid = 1;</code>
         */
        int getUidCount();
        /**
         * <pre>
         * 所有群成员
         * </pre>
         *
         * <code>repeated uint64 uid = 1;</code>
         */
        long getUid(int index);

        /**
         * <pre>
         * 群头像
         * </pre>
         *
         * <code>string avatar = 2;</code>
         */
        java.lang.String getAvatar();
        /**
         * <pre>
         * 群头像
         * </pre>
         *
         * <code>string avatar = 2;</code>
         */
        com.google.protobuf.ByteString
        getAvatarBytes();

        /**
         * <pre>
         * 群名
         * </pre>
         *
         * <code>string name = 3;</code>
         */
        java.lang.String getName();
        /**
         * <pre>
         * 群名
         * </pre>
         *
         * <code>string name = 3;</code>
         */
        com.google.protobuf.ByteString
        getNameBytes();
    }
    /**
     * <pre>
     * 销毁群
     * </pre>
     *
     * Protobuf type {@code DestroyGroupMessage}
     */
    public  static final class DestroyGroupMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:DestroyGroupMessage)
            DestroyGroupMessageOrBuilder {
        // Use DestroyGroupMessage.newBuilder() to construct.
        private DestroyGroupMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }
        private DestroyGroupMessage() {
            uid_ = java.util.Collections.emptyList();
            avatar_ = "";
            name_ = "";
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }
        private DestroyGroupMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            int mutable_bitField0_ = 0;
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 8: {
                            if (!((mutable_bitField0_ & 0x00000001) == 0x00000001)) {
                                uid_ = new java.util.ArrayList<java.lang.Long>();
                                mutable_bitField0_ |= 0x00000001;
                            }
                            uid_.add(input.readUInt64());
                            break;
                        }
                        case 10: {
                            int length = input.readRawVarint32();
                            int limit = input.pushLimit(length);
                            if (!((mutable_bitField0_ & 0x00000001) == 0x00000001) && input.getBytesUntilLimit() > 0) {
                                uid_ = new java.util.ArrayList<java.lang.Long>();
                                mutable_bitField0_ |= 0x00000001;
                            }
                            while (input.getBytesUntilLimit() > 0) {
                                uid_.add(input.readUInt64());
                            }
                            input.popLimit(limit);
                            break;
                        }
                        case 18: {
                            java.lang.String s = input.readStringRequireUtf8();

                            avatar_ = s;
                            break;
                        }
                        case 26: {
                            java.lang.String s = input.readStringRequireUtf8();

                            name_ = s;
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                if (((mutable_bitField0_ & 0x00000001) == 0x00000001)) {
                    uid_ = java.util.Collections.unmodifiableList(uid_);
                }
                makeExtensionsImmutable();
            }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return MsgBean.internal_static_DestroyGroupMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return MsgBean.internal_static_DestroyGroupMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            MsgBean.DestroyGroupMessage.class, MsgBean.DestroyGroupMessage.Builder.class);
        }

        private int bitField0_;
        public static final int UID_FIELD_NUMBER = 1;
        private java.util.List<java.lang.Long> uid_;
        /**
         * <pre>
         * 所有群成员
         * </pre>
         *
         * <code>repeated uint64 uid = 1;</code>
         */
        public java.util.List<java.lang.Long>
        getUidList() {
            return uid_;
        }
        /**
         * <pre>
         * 所有群成员
         * </pre>
         *
         * <code>repeated uint64 uid = 1;</code>
         */
        public int getUidCount() {
            return uid_.size();
        }
        /**
         * <pre>
         * 所有群成员
         * </pre>
         *
         * <code>repeated uint64 uid = 1;</code>
         */
        public long getUid(int index) {
            return uid_.get(index);
        }
        private int uidMemoizedSerializedSize = -1;

        public static final int AVATAR_FIELD_NUMBER = 2;
        private volatile java.lang.Object avatar_;
        /**
         * <pre>
         * 群头像
         * </pre>
         *
         * <code>string avatar = 2;</code>
         */
        public java.lang.String getAvatar() {
            java.lang.Object ref = avatar_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                avatar_ = s;
                return s;
            }
        }
        /**
         * <pre>
         * 群头像
         * </pre>
         *
         * <code>string avatar = 2;</code>
         */
        public com.google.protobuf.ByteString
        getAvatarBytes() {
            java.lang.Object ref = avatar_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                avatar_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        public static final int NAME_FIELD_NUMBER = 3;
        private volatile java.lang.Object name_;
        /**
         * <pre>
         * 群名
         * </pre>
         *
         * <code>string name = 3;</code>
         */
        public java.lang.String getName() {
            java.lang.Object ref = name_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                name_ = s;
                return s;
            }
        }
        /**
         * <pre>
         * 群名
         * </pre>
         *
         * <code>string name = 3;</code>
         */
        public com.google.protobuf.ByteString
        getNameBytes() {
            java.lang.Object ref = name_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                name_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            getSerializedSize();
            if (getUidList().size() > 0) {
                output.writeUInt32NoTag(10);
                output.writeUInt32NoTag(uidMemoizedSerializedSize);
            }
            for (int i = 0; i < uid_.size(); i++) {
                output.writeUInt64NoTag(uid_.get(i));
            }
            if (!getAvatarBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 2, avatar_);
            }
            if (!getNameBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 3, name_);
            }
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            {
                int dataSize = 0;
                for (int i = 0; i < uid_.size(); i++) {
                    dataSize += com.google.protobuf.CodedOutputStream
                            .computeUInt64SizeNoTag(uid_.get(i));
                }
                size += dataSize;
                if (!getUidList().isEmpty()) {
                    size += 1;
                    size += com.google.protobuf.CodedOutputStream
                            .computeInt32SizeNoTag(dataSize);
                }
                uidMemoizedSerializedSize = dataSize;
            }
            if (!getAvatarBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, avatar_);
            }
            if (!getNameBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, name_);
            }
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MsgBean.DestroyGroupMessage)) {
                return super.equals(obj);
            }
            MsgBean.DestroyGroupMessage other = (MsgBean.DestroyGroupMessage) obj;

            boolean result = true;
            result = result && getUidList()
                    .equals(other.getUidList());
            result = result && getAvatar()
                    .equals(other.getAvatar());
            result = result && getName()
                    .equals(other.getName());
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            if (getUidCount() > 0) {
                hash = (37 * hash) + UID_FIELD_NUMBER;
                hash = (53 * hash) + getUidList().hashCode();
            }
            hash = (37 * hash) + AVATAR_FIELD_NUMBER;
            hash = (53 * hash) + getAvatar().hashCode();
            hash = (37 * hash) + NAME_FIELD_NUMBER;
            hash = (53 * hash) + getName().hashCode();
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static MsgBean.DestroyGroupMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.DestroyGroupMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.DestroyGroupMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.DestroyGroupMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.DestroyGroupMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.DestroyGroupMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.DestroyGroupMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.DestroyGroupMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.DestroyGroupMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }
        public static MsgBean.DestroyGroupMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.DestroyGroupMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.DestroyGroupMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MsgBean.DestroyGroupMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        /**
         * <pre>
         * 销毁群
         * </pre>
         *
         * Protobuf type {@code DestroyGroupMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:DestroyGroupMessage)
                MsgBean.DestroyGroupMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_DestroyGroupMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_DestroyGroupMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.DestroyGroupMessage.class, MsgBean.DestroyGroupMessage.Builder.class);
            }

            // Construct using MsgBean.DestroyGroupMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }
            public Builder clear() {
                super.clear();
                uid_ = java.util.Collections.emptyList();
                bitField0_ = (bitField0_ & ~0x00000001);
                avatar_ = "";

                name_ = "";

                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return MsgBean.internal_static_DestroyGroupMessage_descriptor;
            }

            public MsgBean.DestroyGroupMessage getDefaultInstanceForType() {
                return MsgBean.DestroyGroupMessage.getDefaultInstance();
            }

            public MsgBean.DestroyGroupMessage build() {
                MsgBean.DestroyGroupMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public MsgBean.DestroyGroupMessage buildPartial() {
                MsgBean.DestroyGroupMessage result = new MsgBean.DestroyGroupMessage(this);
                int from_bitField0_ = bitField0_;
                int to_bitField0_ = 0;
                if (((bitField0_ & 0x00000001) == 0x00000001)) {
                    uid_ = java.util.Collections.unmodifiableList(uid_);
                    bitField0_ = (bitField0_ & ~0x00000001);
                }
                result.uid_ = uid_;
                result.avatar_ = avatar_;
                result.name_ = name_;
                result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof MsgBean.DestroyGroupMessage) {
                    return mergeFrom((MsgBean.DestroyGroupMessage)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MsgBean.DestroyGroupMessage other) {
                if (other == MsgBean.DestroyGroupMessage.getDefaultInstance()) return this;
                if (!other.uid_.isEmpty()) {
                    if (uid_.isEmpty()) {
                        uid_ = other.uid_;
                        bitField0_ = (bitField0_ & ~0x00000001);
                    } else {
                        ensureUidIsMutable();
                        uid_.addAll(other.uid_);
                    }
                    onChanged();
                }
                if (!other.getAvatar().isEmpty()) {
                    avatar_ = other.avatar_;
                    onChanged();
                }
                if (!other.getName().isEmpty()) {
                    name_ = other.name_;
                    onChanged();
                }
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                MsgBean.DestroyGroupMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (MsgBean.DestroyGroupMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            private int bitField0_;

            private java.util.List<java.lang.Long> uid_ = java.util.Collections.emptyList();
            private void ensureUidIsMutable() {
                if (!((bitField0_ & 0x00000001) == 0x00000001)) {
                    uid_ = new java.util.ArrayList<java.lang.Long>(uid_);
                    bitField0_ |= 0x00000001;
                }
            }
            /**
             * <pre>
             * 所有群成员
             * </pre>
             *
             * <code>repeated uint64 uid = 1;</code>
             */
            public java.util.List<java.lang.Long>
            getUidList() {
                return java.util.Collections.unmodifiableList(uid_);
            }
            /**
             * <pre>
             * 所有群成员
             * </pre>
             *
             * <code>repeated uint64 uid = 1;</code>
             */
            public int getUidCount() {
                return uid_.size();
            }
            /**
             * <pre>
             * 所有群成员
             * </pre>
             *
             * <code>repeated uint64 uid = 1;</code>
             */
            public long getUid(int index) {
                return uid_.get(index);
            }
            /**
             * <pre>
             * 所有群成员
             * </pre>
             *
             * <code>repeated uint64 uid = 1;</code>
             */
            public Builder setUid(
                    int index, long value) {
                ensureUidIsMutable();
                uid_.set(index, value);
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 所有群成员
             * </pre>
             *
             * <code>repeated uint64 uid = 1;</code>
             */
            public Builder addUid(long value) {
                ensureUidIsMutable();
                uid_.add(value);
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 所有群成员
             * </pre>
             *
             * <code>repeated uint64 uid = 1;</code>
             */
            public Builder addAllUid(
                    java.lang.Iterable<? extends java.lang.Long> values) {
                ensureUidIsMutable();
                com.google.protobuf.AbstractMessageLite.Builder.addAll(
                        values, uid_);
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 所有群成员
             * </pre>
             *
             * <code>repeated uint64 uid = 1;</code>
             */
            public Builder clearUid() {
                uid_ = java.util.Collections.emptyList();
                bitField0_ = (bitField0_ & ~0x00000001);
                onChanged();
                return this;
            }

            private java.lang.Object avatar_ = "";
            /**
             * <pre>
             * 群头像
             * </pre>
             *
             * <code>string avatar = 2;</code>
             */
            public java.lang.String getAvatar() {
                java.lang.Object ref = avatar_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    avatar_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }
            /**
             * <pre>
             * 群头像
             * </pre>
             *
             * <code>string avatar = 2;</code>
             */
            public com.google.protobuf.ByteString
            getAvatarBytes() {
                java.lang.Object ref = avatar_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    avatar_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }
            /**
             * <pre>
             * 群头像
             * </pre>
             *
             * <code>string avatar = 2;</code>
             */
            public Builder setAvatar(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                avatar_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 群头像
             * </pre>
             *
             * <code>string avatar = 2;</code>
             */
            public Builder clearAvatar() {

                avatar_ = getDefaultInstance().getAvatar();
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 群头像
             * </pre>
             *
             * <code>string avatar = 2;</code>
             */
            public Builder setAvatarBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                avatar_ = value;
                onChanged();
                return this;
            }

            private java.lang.Object name_ = "";
            /**
             * <pre>
             * 群名
             * </pre>
             *
             * <code>string name = 3;</code>
             */
            public java.lang.String getName() {
                java.lang.Object ref = name_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    name_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }
            /**
             * <pre>
             * 群名
             * </pre>
             *
             * <code>string name = 3;</code>
             */
            public com.google.protobuf.ByteString
            getNameBytes() {
                java.lang.Object ref = name_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    name_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }
            /**
             * <pre>
             * 群名
             * </pre>
             *
             * <code>string name = 3;</code>
             */
            public Builder setName(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                name_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 群名
             * </pre>
             *
             * <code>string name = 3;</code>
             */
            public Builder clearName() {

                name_ = getDefaultInstance().getName();
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 群名
             * </pre>
             *
             * <code>string name = 3;</code>
             */
            public Builder setNameBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                name_ = value;
                onChanged();
                return this;
            }
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:DestroyGroupMessage)
        }

        // @@protoc_insertion_point(class_scope:DestroyGroupMessage)
        private static final MsgBean.DestroyGroupMessage DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new MsgBean.DestroyGroupMessage();
        }

        public static MsgBean.DestroyGroupMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<DestroyGroupMessage>
                PARSER = new com.google.protobuf.AbstractParser<DestroyGroupMessage>() {
            public DestroyGroupMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new DestroyGroupMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<DestroyGroupMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<DestroyGroupMessage> getParserForType() {
            return PARSER;
        }

        public MsgBean.DestroyGroupMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface ConflictMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:ConflictMessage)
            com.google.protobuf.MessageOrBuilder {
    }
    /**
     * <pre>
     * 登录冲突消息
     * </pre>
     *
     * Protobuf type {@code ConflictMessage}
     */
    public  static final class ConflictMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:ConflictMessage)
            ConflictMessageOrBuilder {
        // Use ConflictMessage.newBuilder() to construct.
        private ConflictMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }
        private ConflictMessage() {
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }
        private ConflictMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                makeExtensionsImmutable();
            }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return MsgBean.internal_static_ConflictMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return MsgBean.internal_static_ConflictMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            MsgBean.ConflictMessage.class, MsgBean.ConflictMessage.Builder.class);
        }

        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MsgBean.ConflictMessage)) {
                return super.equals(obj);
            }
            MsgBean.ConflictMessage other = (MsgBean.ConflictMessage) obj;

            boolean result = true;
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static MsgBean.ConflictMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.ConflictMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.ConflictMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.ConflictMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.ConflictMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.ConflictMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.ConflictMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.ConflictMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.ConflictMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }
        public static MsgBean.ConflictMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.ConflictMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.ConflictMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MsgBean.ConflictMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        /**
         * <pre>
         * 登录冲突消息
         * </pre>
         *
         * Protobuf type {@code ConflictMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:ConflictMessage)
                MsgBean.ConflictMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_ConflictMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_ConflictMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.ConflictMessage.class, MsgBean.ConflictMessage.Builder.class);
            }

            // Construct using MsgBean.ConflictMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }
            public Builder clear() {
                super.clear();
                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return MsgBean.internal_static_ConflictMessage_descriptor;
            }

            public MsgBean.ConflictMessage getDefaultInstanceForType() {
                return MsgBean.ConflictMessage.getDefaultInstance();
            }

            public MsgBean.ConflictMessage build() {
                MsgBean.ConflictMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public MsgBean.ConflictMessage buildPartial() {
                MsgBean.ConflictMessage result = new MsgBean.ConflictMessage(this);
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof MsgBean.ConflictMessage) {
                    return mergeFrom((MsgBean.ConflictMessage)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MsgBean.ConflictMessage other) {
                if (other == MsgBean.ConflictMessage.getDefaultInstance()) return this;
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                MsgBean.ConflictMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (MsgBean.ConflictMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:ConflictMessage)
        }

        // @@protoc_insertion_point(class_scope:ConflictMessage)
        private static final MsgBean.ConflictMessage DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new MsgBean.ConflictMessage();
        }

        public static MsgBean.ConflictMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<ConflictMessage>
                PARSER = new com.google.protobuf.AbstractParser<ConflictMessage>() {
            public ConflictMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new ConflictMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<ConflictMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<ConflictMessage> getParserForType() {
            return PARSER;
        }

        public MsgBean.ConflictMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface OutGroupMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:OutGroupMessage)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <pre>
         * 群id
         * </pre>
         *
         * <code>string gid = 1;</code>
         */
        java.lang.String getGid();
        /**
         * <pre>
         * 群id
         * </pre>
         *
         * <code>string gid = 1;</code>
         */
        com.google.protobuf.ByteString
        getGidBytes();
    }
    /**
     * <pre>
     * 退群
     * </pre>
     *
     * Protobuf type {@code OutGroupMessage}
     */
    public  static final class OutGroupMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:OutGroupMessage)
            OutGroupMessageOrBuilder {
        // Use OutGroupMessage.newBuilder() to construct.
        private OutGroupMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }
        private OutGroupMessage() {
            gid_ = "";
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }
        private OutGroupMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            int mutable_bitField0_ = 0;
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 10: {
                            java.lang.String s = input.readStringRequireUtf8();

                            gid_ = s;
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                makeExtensionsImmutable();
            }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return MsgBean.internal_static_OutGroupMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return MsgBean.internal_static_OutGroupMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            MsgBean.OutGroupMessage.class, MsgBean.OutGroupMessage.Builder.class);
        }

        public static final int GID_FIELD_NUMBER = 1;
        private volatile java.lang.Object gid_;
        /**
         * <pre>
         * 群id
         * </pre>
         *
         * <code>string gid = 1;</code>
         */
        public java.lang.String getGid() {
            java.lang.Object ref = gid_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                gid_ = s;
                return s;
            }
        }
        /**
         * <pre>
         * 群id
         * </pre>
         *
         * <code>string gid = 1;</code>
         */
        public com.google.protobuf.ByteString
        getGidBytes() {
            java.lang.Object ref = gid_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                gid_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            if (!getGidBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 1, gid_);
            }
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (!getGidBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, gid_);
            }
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MsgBean.OutGroupMessage)) {
                return super.equals(obj);
            }
            MsgBean.OutGroupMessage other = (MsgBean.OutGroupMessage) obj;

            boolean result = true;
            result = result && getGid()
                    .equals(other.getGid());
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (37 * hash) + GID_FIELD_NUMBER;
            hash = (53 * hash) + getGid().hashCode();
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static MsgBean.OutGroupMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.OutGroupMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.OutGroupMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.OutGroupMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.OutGroupMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.OutGroupMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.OutGroupMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.OutGroupMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.OutGroupMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }
        public static MsgBean.OutGroupMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.OutGroupMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.OutGroupMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MsgBean.OutGroupMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        /**
         * <pre>
         * 退群
         * </pre>
         *
         * Protobuf type {@code OutGroupMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:OutGroupMessage)
                MsgBean.OutGroupMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_OutGroupMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_OutGroupMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.OutGroupMessage.class, MsgBean.OutGroupMessage.Builder.class);
            }

            // Construct using MsgBean.OutGroupMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }
            public Builder clear() {
                super.clear();
                gid_ = "";

                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return MsgBean.internal_static_OutGroupMessage_descriptor;
            }

            public MsgBean.OutGroupMessage getDefaultInstanceForType() {
                return MsgBean.OutGroupMessage.getDefaultInstance();
            }

            public MsgBean.OutGroupMessage build() {
                MsgBean.OutGroupMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public MsgBean.OutGroupMessage buildPartial() {
                MsgBean.OutGroupMessage result = new MsgBean.OutGroupMessage(this);
                result.gid_ = gid_;
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof MsgBean.OutGroupMessage) {
                    return mergeFrom((MsgBean.OutGroupMessage)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MsgBean.OutGroupMessage other) {
                if (other == MsgBean.OutGroupMessage.getDefaultInstance()) return this;
                if (!other.getGid().isEmpty()) {
                    gid_ = other.gid_;
                    onChanged();
                }
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                MsgBean.OutGroupMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (MsgBean.OutGroupMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private java.lang.Object gid_ = "";
            /**
             * <pre>
             * 群id
             * </pre>
             *
             * <code>string gid = 1;</code>
             */
            public java.lang.String getGid() {
                java.lang.Object ref = gid_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    gid_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }
            /**
             * <pre>
             * 群id
             * </pre>
             *
             * <code>string gid = 1;</code>
             */
            public com.google.protobuf.ByteString
            getGidBytes() {
                java.lang.Object ref = gid_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    gid_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }
            /**
             * <pre>
             * 群id
             * </pre>
             *
             * <code>string gid = 1;</code>
             */
            public Builder setGid(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                gid_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 群id
             * </pre>
             *
             * <code>string gid = 1;</code>
             */
            public Builder clearGid() {

                gid_ = getDefaultInstance().getGid();
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 群id
             * </pre>
             *
             * <code>string gid = 1;</code>
             */
            public Builder setGidBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                gid_ = value;
                onChanged();
                return this;
            }
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:OutGroupMessage)
        }

        // @@protoc_insertion_point(class_scope:OutGroupMessage)
        private static final MsgBean.OutGroupMessage DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new MsgBean.OutGroupMessage();
        }

        public static MsgBean.OutGroupMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<OutGroupMessage>
                PARSER = new com.google.protobuf.AbstractParser<OutGroupMessage>() {
            public OutGroupMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new OutGroupMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<OutGroupMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<OutGroupMessage> getParserForType() {
            return PARSER;
        }

        public MsgBean.OutGroupMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface AckMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:AckMessage)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <pre>
         * 是否接受
         * </pre>
         *
         * <code>.RejectType reject_type = 1;</code>
         */
        int getRejectTypeValue();
        /**
         * <pre>
         * 是否接受
         * </pre>
         *
         * <code>.RejectType reject_type = 1;</code>
         */
        MsgBean.RejectType getRejectType();

        /**
         * <code>string request_id = 2;</code>
         */
        java.lang.String getRequestId();
        /**
         * <code>string request_id = 2;</code>
         */
        com.google.protobuf.ByteString
        getRequestIdBytes();

        /**
         * <pre>
         * 消息id
         * </pre>
         *
         * <code>repeated string msg_id = 3;</code>
         */
        java.util.List<java.lang.String>
        getMsgIdList();
        /**
         * <pre>
         * 消息id
         * </pre>
         *
         * <code>repeated string msg_id = 3;</code>
         */
        int getMsgIdCount();
        /**
         * <pre>
         * 消息id
         * </pre>
         *
         * <code>repeated string msg_id = 3;</code>
         */
        java.lang.String getMsgId(int index);
        /**
         * <pre>
         * 消息id
         * </pre>
         *
         * <code>repeated string msg_id = 3;</code>
         */
        com.google.protobuf.ByteString
        getMsgIdBytes(int index);

        /**
         * <pre>
         * 时间戳
         * </pre>
         *
         * <code>uint64 timestamp = 4;</code>
         */
        long getTimestamp();
    }
    /**
     * <pre>
     * ACK消息
     * </pre>
     *
     * Protobuf type {@code AckMessage}
     */
    public  static final class AckMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:AckMessage)
            AckMessageOrBuilder {
        // Use AckMessage.newBuilder() to construct.
        private AckMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }
        private AckMessage() {
            rejectType_ = 0;
            requestId_ = "";
            msgId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
            timestamp_ = 0L;
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }
        private AckMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            int mutable_bitField0_ = 0;
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 8: {
                            int rawValue = input.readEnum();

                            rejectType_ = rawValue;
                            break;
                        }
                        case 18: {
                            java.lang.String s = input.readStringRequireUtf8();

                            requestId_ = s;
                            break;
                        }
                        case 26: {
                            java.lang.String s = input.readStringRequireUtf8();
                            if (!((mutable_bitField0_ & 0x00000004) == 0x00000004)) {
                                msgId_ = new com.google.protobuf.LazyStringArrayList();
                                mutable_bitField0_ |= 0x00000004;
                            }
                            msgId_.add(s);
                            break;
                        }
                        case 32: {

                            timestamp_ = input.readUInt64();
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                if (((mutable_bitField0_ & 0x00000004) == 0x00000004)) {
                    msgId_ = msgId_.getUnmodifiableView();
                }
                makeExtensionsImmutable();
            }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return MsgBean.internal_static_AckMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return MsgBean.internal_static_AckMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            MsgBean.AckMessage.class, MsgBean.AckMessage.Builder.class);
        }

        private int bitField0_;
        public static final int REJECT_TYPE_FIELD_NUMBER = 1;
        private int rejectType_;
        /**
         * <pre>
         * 是否接受
         * </pre>
         *
         * <code>.RejectType reject_type = 1;</code>
         */
        public int getRejectTypeValue() {
            return rejectType_;
        }
        /**
         * <pre>
         * 是否接受
         * </pre>
         *
         * <code>.RejectType reject_type = 1;</code>
         */
        public MsgBean.RejectType getRejectType() {
            MsgBean.RejectType result = MsgBean.RejectType.valueOf(rejectType_);
            return result == null ? MsgBean.RejectType.UNRECOGNIZED : result;
        }

        public static final int REQUEST_ID_FIELD_NUMBER = 2;
        private volatile java.lang.Object requestId_;
        /**
         * <code>string request_id = 2;</code>
         */
        public java.lang.String getRequestId() {
            java.lang.Object ref = requestId_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                requestId_ = s;
                return s;
            }
        }
        /**
         * <code>string request_id = 2;</code>
         */
        public com.google.protobuf.ByteString
        getRequestIdBytes() {
            java.lang.Object ref = requestId_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                requestId_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        public static final int MSG_ID_FIELD_NUMBER = 3;
        private com.google.protobuf.LazyStringList msgId_;
        /**
         * <pre>
         * 消息id
         * </pre>
         *
         * <code>repeated string msg_id = 3;</code>
         */
        public com.google.protobuf.ProtocolStringList
        getMsgIdList() {
            return msgId_;
        }
        /**
         * <pre>
         * 消息id
         * </pre>
         *
         * <code>repeated string msg_id = 3;</code>
         */
        public int getMsgIdCount() {
            return msgId_.size();
        }
        /**
         * <pre>
         * 消息id
         * </pre>
         *
         * <code>repeated string msg_id = 3;</code>
         */
        public java.lang.String getMsgId(int index) {
            return msgId_.get(index);
        }
        /**
         * <pre>
         * 消息id
         * </pre>
         *
         * <code>repeated string msg_id = 3;</code>
         */
        public com.google.protobuf.ByteString
        getMsgIdBytes(int index) {
            return msgId_.getByteString(index);
        }

        public static final int TIMESTAMP_FIELD_NUMBER = 4;
        private long timestamp_;
        /**
         * <pre>
         * 时间戳
         * </pre>
         *
         * <code>uint64 timestamp = 4;</code>
         */
        public long getTimestamp() {
            return timestamp_;
        }

        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            if (rejectType_ != MsgBean.RejectType.ACCEPTED.getNumber()) {
                output.writeEnum(1, rejectType_);
            }
            if (!getRequestIdBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 2, requestId_);
            }
            for (int i = 0; i < msgId_.size(); i++) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 3, msgId_.getRaw(i));
            }
            if (timestamp_ != 0L) {
                output.writeUInt64(4, timestamp_);
            }
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (rejectType_ != MsgBean.RejectType.ACCEPTED.getNumber()) {
                size += com.google.protobuf.CodedOutputStream
                        .computeEnumSize(1, rejectType_);
            }
            if (!getRequestIdBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, requestId_);
            }
            {
                int dataSize = 0;
                for (int i = 0; i < msgId_.size(); i++) {
                    dataSize += computeStringSizeNoTag(msgId_.getRaw(i));
                }
                size += dataSize;
                size += 1 * getMsgIdList().size();
            }
            if (timestamp_ != 0L) {
                size += com.google.protobuf.CodedOutputStream
                        .computeUInt64Size(4, timestamp_);
            }
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MsgBean.AckMessage)) {
                return super.equals(obj);
            }
            MsgBean.AckMessage other = (MsgBean.AckMessage) obj;

            boolean result = true;
            result = result && rejectType_ == other.rejectType_;
            result = result && getRequestId()
                    .equals(other.getRequestId());
            result = result && getMsgIdList()
                    .equals(other.getMsgIdList());
            result = result && (getTimestamp()
                    == other.getTimestamp());
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (37 * hash) + REJECT_TYPE_FIELD_NUMBER;
            hash = (53 * hash) + rejectType_;
            hash = (37 * hash) + REQUEST_ID_FIELD_NUMBER;
            hash = (53 * hash) + getRequestId().hashCode();
            if (getMsgIdCount() > 0) {
                hash = (37 * hash) + MSG_ID_FIELD_NUMBER;
                hash = (53 * hash) + getMsgIdList().hashCode();
            }
            hash = (37 * hash) + TIMESTAMP_FIELD_NUMBER;
            hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
                    getTimestamp());
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static MsgBean.AckMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.AckMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.AckMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.AckMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.AckMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.AckMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.AckMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.AckMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.AckMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }
        public static MsgBean.AckMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.AckMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.AckMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MsgBean.AckMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        /**
         * <pre>
         * ACK消息
         * </pre>
         *
         * Protobuf type {@code AckMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:AckMessage)
                MsgBean.AckMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_AckMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_AckMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.AckMessage.class, MsgBean.AckMessage.Builder.class);
            }

            // Construct using MsgBean.AckMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }
            public Builder clear() {
                super.clear();
                rejectType_ = 0;

                requestId_ = "";

                msgId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
                bitField0_ = (bitField0_ & ~0x00000004);
                timestamp_ = 0L;

                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return MsgBean.internal_static_AckMessage_descriptor;
            }

            public MsgBean.AckMessage getDefaultInstanceForType() {
                return MsgBean.AckMessage.getDefaultInstance();
            }

            public MsgBean.AckMessage build() {
                MsgBean.AckMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public MsgBean.AckMessage buildPartial() {
                MsgBean.AckMessage result = new MsgBean.AckMessage(this);
                int from_bitField0_ = bitField0_;
                int to_bitField0_ = 0;
                result.rejectType_ = rejectType_;
                result.requestId_ = requestId_;
                if (((bitField0_ & 0x00000004) == 0x00000004)) {
                    msgId_ = msgId_.getUnmodifiableView();
                    bitField0_ = (bitField0_ & ~0x00000004);
                }
                result.msgId_ = msgId_;
                result.timestamp_ = timestamp_;
                result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof MsgBean.AckMessage) {
                    return mergeFrom((MsgBean.AckMessage)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MsgBean.AckMessage other) {
                if (other == MsgBean.AckMessage.getDefaultInstance()) return this;
                if (other.rejectType_ != 0) {
                    setRejectTypeValue(other.getRejectTypeValue());
                }
                if (!other.getRequestId().isEmpty()) {
                    requestId_ = other.requestId_;
                    onChanged();
                }
                if (!other.msgId_.isEmpty()) {
                    if (msgId_.isEmpty()) {
                        msgId_ = other.msgId_;
                        bitField0_ = (bitField0_ & ~0x00000004);
                    } else {
                        ensureMsgIdIsMutable();
                        msgId_.addAll(other.msgId_);
                    }
                    onChanged();
                }
                if (other.getTimestamp() != 0L) {
                    setTimestamp(other.getTimestamp());
                }
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                MsgBean.AckMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (MsgBean.AckMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            private int bitField0_;

            private int rejectType_ = 0;
            /**
             * <pre>
             * 是否接受
             * </pre>
             *
             * <code>.RejectType reject_type = 1;</code>
             */
            public int getRejectTypeValue() {
                return rejectType_;
            }
            /**
             * <pre>
             * 是否接受
             * </pre>
             *
             * <code>.RejectType reject_type = 1;</code>
             */
            public Builder setRejectTypeValue(int value) {
                rejectType_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 是否接受
             * </pre>
             *
             * <code>.RejectType reject_type = 1;</code>
             */
            public MsgBean.RejectType getRejectType() {
                MsgBean.RejectType result = MsgBean.RejectType.valueOf(rejectType_);
                return result == null ? MsgBean.RejectType.UNRECOGNIZED : result;
            }
            /**
             * <pre>
             * 是否接受
             * </pre>
             *
             * <code>.RejectType reject_type = 1;</code>
             */
            public Builder setRejectType(MsgBean.RejectType value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                rejectType_ = value.getNumber();
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 是否接受
             * </pre>
             *
             * <code>.RejectType reject_type = 1;</code>
             */
            public Builder clearRejectType() {

                rejectType_ = 0;
                onChanged();
                return this;
            }

            private java.lang.Object requestId_ = "";
            /**
             * <code>string request_id = 2;</code>
             */
            public java.lang.String getRequestId() {
                java.lang.Object ref = requestId_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    requestId_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }
            /**
             * <code>string request_id = 2;</code>
             */
            public com.google.protobuf.ByteString
            getRequestIdBytes() {
                java.lang.Object ref = requestId_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    requestId_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }
            /**
             * <code>string request_id = 2;</code>
             */
            public Builder setRequestId(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                requestId_ = value;
                onChanged();
                return this;
            }
            /**
             * <code>string request_id = 2;</code>
             */
            public Builder clearRequestId() {

                requestId_ = getDefaultInstance().getRequestId();
                onChanged();
                return this;
            }
            /**
             * <code>string request_id = 2;</code>
             */
            public Builder setRequestIdBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                requestId_ = value;
                onChanged();
                return this;
            }

            private com.google.protobuf.LazyStringList msgId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
            private void ensureMsgIdIsMutable() {
                if (!((bitField0_ & 0x00000004) == 0x00000004)) {
                    msgId_ = new com.google.protobuf.LazyStringArrayList(msgId_);
                    bitField0_ |= 0x00000004;
                }
            }
            /**
             * <pre>
             * 消息id
             * </pre>
             *
             * <code>repeated string msg_id = 3;</code>
             */
            public com.google.protobuf.ProtocolStringList
            getMsgIdList() {
                return msgId_.getUnmodifiableView();
            }
            /**
             * <pre>
             * 消息id
             * </pre>
             *
             * <code>repeated string msg_id = 3;</code>
             */
            public int getMsgIdCount() {
                return msgId_.size();
            }
            /**
             * <pre>
             * 消息id
             * </pre>
             *
             * <code>repeated string msg_id = 3;</code>
             */
            public java.lang.String getMsgId(int index) {
                return msgId_.get(index);
            }
            /**
             * <pre>
             * 消息id
             * </pre>
             *
             * <code>repeated string msg_id = 3;</code>
             */
            public com.google.protobuf.ByteString
            getMsgIdBytes(int index) {
                return msgId_.getByteString(index);
            }
            /**
             * <pre>
             * 消息id
             * </pre>
             *
             * <code>repeated string msg_id = 3;</code>
             */
            public Builder setMsgId(
                    int index, java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                ensureMsgIdIsMutable();
                msgId_.set(index, value);
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 消息id
             * </pre>
             *
             * <code>repeated string msg_id = 3;</code>
             */
            public Builder addMsgId(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                ensureMsgIdIsMutable();
                msgId_.add(value);
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 消息id
             * </pre>
             *
             * <code>repeated string msg_id = 3;</code>
             */
            public Builder addAllMsgId(
                    java.lang.Iterable<java.lang.String> values) {
                ensureMsgIdIsMutable();
                com.google.protobuf.AbstractMessageLite.Builder.addAll(
                        values, msgId_);
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 消息id
             * </pre>
             *
             * <code>repeated string msg_id = 3;</code>
             */
            public Builder clearMsgId() {
                msgId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
                bitField0_ = (bitField0_ & ~0x00000004);
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 消息id
             * </pre>
             *
             * <code>repeated string msg_id = 3;</code>
             */
            public Builder addMsgIdBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);
                ensureMsgIdIsMutable();
                msgId_.add(value);
                onChanged();
                return this;
            }

            private long timestamp_ ;
            /**
             * <pre>
             * 时间戳
             * </pre>
             *
             * <code>uint64 timestamp = 4;</code>
             */
            public long getTimestamp() {
                return timestamp_;
            }
            /**
             * <pre>
             * 时间戳
             * </pre>
             *
             * <code>uint64 timestamp = 4;</code>
             */
            public Builder setTimestamp(long value) {

                timestamp_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 时间戳
             * </pre>
             *
             * <code>uint64 timestamp = 4;</code>
             */
            public Builder clearTimestamp() {

                timestamp_ = 0L;
                onChanged();
                return this;
            }
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:AckMessage)
        }

        // @@protoc_insertion_point(class_scope:AckMessage)
        private static final MsgBean.AckMessage DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new MsgBean.AckMessage();
        }

        public static MsgBean.AckMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<AckMessage>
                PARSER = new com.google.protobuf.AbstractParser<AckMessage>() {
            public AckMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new AckMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<AckMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<AckMessage> getParserForType() {
            return PARSER;
        }

        public MsgBean.AckMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface AuthRequestMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:AuthRequestMessage)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <pre>
         * 访问令牌
         * </pre>
         *
         * <code>string access_token = 1;</code>
         */
        java.lang.String getAccessToken();
        /**
         * <pre>
         * 访问令牌
         * </pre>
         *
         * <code>string access_token = 1;</code>
         */
        com.google.protobuf.ByteString
        getAccessTokenBytes();
    }
    /**
     * <pre>
     * 连接认证请求
     * </pre>
     *
     * Protobuf type {@code AuthRequestMessage}
     */
    public  static final class AuthRequestMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:AuthRequestMessage)
            AuthRequestMessageOrBuilder {
        // Use AuthRequestMessage.newBuilder() to construct.
        private AuthRequestMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }
        private AuthRequestMessage() {
            accessToken_ = "";
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }
        private AuthRequestMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            int mutable_bitField0_ = 0;
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 10: {
                            java.lang.String s = input.readStringRequireUtf8();

                            accessToken_ = s;
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                makeExtensionsImmutable();
            }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return MsgBean.internal_static_AuthRequestMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return MsgBean.internal_static_AuthRequestMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            MsgBean.AuthRequestMessage.class, MsgBean.AuthRequestMessage.Builder.class);
        }

        public static final int ACCESS_TOKEN_FIELD_NUMBER = 1;
        private volatile java.lang.Object accessToken_;
        /**
         * <pre>
         * 访问令牌
         * </pre>
         *
         * <code>string access_token = 1;</code>
         */
        public java.lang.String getAccessToken() {
            java.lang.Object ref = accessToken_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                accessToken_ = s;
                return s;
            }
        }
        /**
         * <pre>
         * 访问令牌
         * </pre>
         *
         * <code>string access_token = 1;</code>
         */
        public com.google.protobuf.ByteString
        getAccessTokenBytes() {
            java.lang.Object ref = accessToken_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                accessToken_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            if (!getAccessTokenBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 1, accessToken_);
            }
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (!getAccessTokenBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, accessToken_);
            }
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MsgBean.AuthRequestMessage)) {
                return super.equals(obj);
            }
            MsgBean.AuthRequestMessage other = (MsgBean.AuthRequestMessage) obj;

            boolean result = true;
            result = result && getAccessToken()
                    .equals(other.getAccessToken());
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (37 * hash) + ACCESS_TOKEN_FIELD_NUMBER;
            hash = (53 * hash) + getAccessToken().hashCode();
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static MsgBean.AuthRequestMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.AuthRequestMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.AuthRequestMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.AuthRequestMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.AuthRequestMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.AuthRequestMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.AuthRequestMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.AuthRequestMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.AuthRequestMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }
        public static MsgBean.AuthRequestMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.AuthRequestMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.AuthRequestMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MsgBean.AuthRequestMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        /**
         * <pre>
         * 连接认证请求
         * </pre>
         *
         * Protobuf type {@code AuthRequestMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:AuthRequestMessage)
                MsgBean.AuthRequestMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_AuthRequestMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_AuthRequestMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.AuthRequestMessage.class, MsgBean.AuthRequestMessage.Builder.class);
            }

            // Construct using MsgBean.AuthRequestMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }
            public Builder clear() {
                super.clear();
                accessToken_ = "";

                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return MsgBean.internal_static_AuthRequestMessage_descriptor;
            }

            public MsgBean.AuthRequestMessage getDefaultInstanceForType() {
                return MsgBean.AuthRequestMessage.getDefaultInstance();
            }

            public MsgBean.AuthRequestMessage build() {
                MsgBean.AuthRequestMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public MsgBean.AuthRequestMessage buildPartial() {
                MsgBean.AuthRequestMessage result = new MsgBean.AuthRequestMessage(this);
                result.accessToken_ = accessToken_;
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof MsgBean.AuthRequestMessage) {
                    return mergeFrom((MsgBean.AuthRequestMessage)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MsgBean.AuthRequestMessage other) {
                if (other == MsgBean.AuthRequestMessage.getDefaultInstance()) return this;
                if (!other.getAccessToken().isEmpty()) {
                    accessToken_ = other.accessToken_;
                    onChanged();
                }
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                MsgBean.AuthRequestMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (MsgBean.AuthRequestMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private java.lang.Object accessToken_ = "";
            /**
             * <pre>
             * 访问令牌
             * </pre>
             *
             * <code>string access_token = 1;</code>
             */
            public java.lang.String getAccessToken() {
                java.lang.Object ref = accessToken_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    accessToken_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }
            /**
             * <pre>
             * 访问令牌
             * </pre>
             *
             * <code>string access_token = 1;</code>
             */
            public com.google.protobuf.ByteString
            getAccessTokenBytes() {
                java.lang.Object ref = accessToken_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    accessToken_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }
            /**
             * <pre>
             * 访问令牌
             * </pre>
             *
             * <code>string access_token = 1;</code>
             */
            public Builder setAccessToken(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                accessToken_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 访问令牌
             * </pre>
             *
             * <code>string access_token = 1;</code>
             */
            public Builder clearAccessToken() {

                accessToken_ = getDefaultInstance().getAccessToken();
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 访问令牌
             * </pre>
             *
             * <code>string access_token = 1;</code>
             */
            public Builder setAccessTokenBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                accessToken_ = value;
                onChanged();
                return this;
            }
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:AuthRequestMessage)
        }

        // @@protoc_insertion_point(class_scope:AuthRequestMessage)
        private static final MsgBean.AuthRequestMessage DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new MsgBean.AuthRequestMessage();
        }

        public static MsgBean.AuthRequestMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<AuthRequestMessage>
                PARSER = new com.google.protobuf.AbstractParser<AuthRequestMessage>() {
            public AuthRequestMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new AuthRequestMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<AuthRequestMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<AuthRequestMessage> getParserForType() {
            return PARSER;
        }

        public MsgBean.AuthRequestMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface AuthResponseMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:AuthResponseMessage)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <code>sint32 accepted = 1;</code>
         */
        int getAccepted();
    }
    /**
     * <pre>
     * 连接认证响应
     * </pre>
     *
     * Protobuf type {@code AuthResponseMessage}
     */
    public  static final class AuthResponseMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:AuthResponseMessage)
            AuthResponseMessageOrBuilder {
        // Use AuthResponseMessage.newBuilder() to construct.
        private AuthResponseMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }
        private AuthResponseMessage() {
            accepted_ = 0;
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }
        private AuthResponseMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            int mutable_bitField0_ = 0;
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 8: {

                            accepted_ = input.readSInt32();
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                makeExtensionsImmutable();
            }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return MsgBean.internal_static_AuthResponseMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return MsgBean.internal_static_AuthResponseMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            MsgBean.AuthResponseMessage.class, MsgBean.AuthResponseMessage.Builder.class);
        }

        public static final int ACCEPTED_FIELD_NUMBER = 1;
        private int accepted_;
        /**
         * <code>sint32 accepted = 1;</code>
         */
        public int getAccepted() {
            return accepted_;
        }

        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            if (accepted_ != 0) {
                output.writeSInt32(1, accepted_);
            }
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (accepted_ != 0) {
                size += com.google.protobuf.CodedOutputStream
                        .computeSInt32Size(1, accepted_);
            }
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MsgBean.AuthResponseMessage)) {
                return super.equals(obj);
            }
            MsgBean.AuthResponseMessage other = (MsgBean.AuthResponseMessage) obj;

            boolean result = true;
            result = result && (getAccepted()
                    == other.getAccepted());
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (37 * hash) + ACCEPTED_FIELD_NUMBER;
            hash = (53 * hash) + getAccepted();
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static MsgBean.AuthResponseMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.AuthResponseMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.AuthResponseMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.AuthResponseMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.AuthResponseMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.AuthResponseMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.AuthResponseMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.AuthResponseMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.AuthResponseMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }
        public static MsgBean.AuthResponseMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.AuthResponseMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.AuthResponseMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MsgBean.AuthResponseMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        /**
         * <pre>
         * 连接认证响应
         * </pre>
         *
         * Protobuf type {@code AuthResponseMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:AuthResponseMessage)
                MsgBean.AuthResponseMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_AuthResponseMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_AuthResponseMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.AuthResponseMessage.class, MsgBean.AuthResponseMessage.Builder.class);
            }

            // Construct using MsgBean.AuthResponseMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }
            public Builder clear() {
                super.clear();
                accepted_ = 0;

                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return MsgBean.internal_static_AuthResponseMessage_descriptor;
            }

            public MsgBean.AuthResponseMessage getDefaultInstanceForType() {
                return MsgBean.AuthResponseMessage.getDefaultInstance();
            }

            public MsgBean.AuthResponseMessage build() {
                MsgBean.AuthResponseMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public MsgBean.AuthResponseMessage buildPartial() {
                MsgBean.AuthResponseMessage result = new MsgBean.AuthResponseMessage(this);
                result.accepted_ = accepted_;
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof MsgBean.AuthResponseMessage) {
                    return mergeFrom((MsgBean.AuthResponseMessage)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MsgBean.AuthResponseMessage other) {
                if (other == MsgBean.AuthResponseMessage.getDefaultInstance()) return this;
                if (other.getAccepted() != 0) {
                    setAccepted(other.getAccepted());
                }
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                MsgBean.AuthResponseMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (MsgBean.AuthResponseMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private int accepted_ ;
            /**
             * <code>sint32 accepted = 1;</code>
             */
            public int getAccepted() {
                return accepted_;
            }
            /**
             * <code>sint32 accepted = 1;</code>
             */
            public Builder setAccepted(int value) {

                accepted_ = value;
                onChanged();
                return this;
            }
            /**
             * <code>sint32 accepted = 1;</code>
             */
            public Builder clearAccepted() {

                accepted_ = 0;
                onChanged();
                return this;
            }
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:AuthResponseMessage)
        }

        // @@protoc_insertion_point(class_scope:AuthResponseMessage)
        private static final MsgBean.AuthResponseMessage DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new MsgBean.AuthResponseMessage();
        }

        public static MsgBean.AuthResponseMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<AuthResponseMessage>
                PARSER = new com.google.protobuf.AbstractParser<AuthResponseMessage>() {
            public AuthResponseMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new AuthResponseMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<AuthResponseMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<AuthResponseMessage> getParserForType() {
            return PARSER;
        }

        public MsgBean.AuthResponseMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface UniversalMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:UniversalMessage)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <pre>
         * 请求id
         * </pre>
         *
         * <code>string request_id = 1;</code>
         */
        java.lang.String getRequestId();
        /**
         * <pre>
         * 请求id
         * </pre>
         *
         * <code>string request_id = 1;</code>
         */
        com.google.protobuf.ByteString
        getRequestIdBytes();

        /**
         * <code>uint64 to_uid = 2;</code>
         */
        long getToUid();

        /**
         * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
         */
        java.util.List<MsgBean.UniversalMessage.WrapMessage>
        getWrapMsgList();
        /**
         * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
         */
        MsgBean.UniversalMessage.WrapMessage getWrapMsg(int index);
        /**
         * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
         */
        int getWrapMsgCount();
        /**
         * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
         */
        java.util.List<? extends MsgBean.UniversalMessage.WrapMessageOrBuilder>
        getWrapMsgOrBuilderList();
        /**
         * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
         */
        MsgBean.UniversalMessage.WrapMessageOrBuilder getWrapMsgOrBuilder(
                int index);
    }
    /**
     * <pre>
     * 普通消息
     * </pre>
     *
     * Protobuf type {@code UniversalMessage}
     */
    public  static final class UniversalMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:UniversalMessage)
            UniversalMessageOrBuilder {
        // Use UniversalMessage.newBuilder() to construct.
        private UniversalMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }
        private UniversalMessage() {
            requestId_ = "";
            toUid_ = 0L;
            wrapMsg_ = java.util.Collections.emptyList();
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }
        private UniversalMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            int mutable_bitField0_ = 0;
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 10: {
                            java.lang.String s = input.readStringRequireUtf8();

                            requestId_ = s;
                            break;
                        }
                        case 16: {

                            toUid_ = input.readUInt64();
                            break;
                        }
                        case 80010: {
                            if (!((mutable_bitField0_ & 0x00000004) == 0x00000004)) {
                                wrapMsg_ = new java.util.ArrayList<MsgBean.UniversalMessage.WrapMessage>();
                                mutable_bitField0_ |= 0x00000004;
                            }
                            wrapMsg_.add(
                                    input.readMessage(MsgBean.UniversalMessage.WrapMessage.parser(), extensionRegistry));
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                if (((mutable_bitField0_ & 0x00000004) == 0x00000004)) {
                    wrapMsg_ = java.util.Collections.unmodifiableList(wrapMsg_);
                }
                makeExtensionsImmutable();
            }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return MsgBean.internal_static_UniversalMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return MsgBean.internal_static_UniversalMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            MsgBean.UniversalMessage.class, MsgBean.UniversalMessage.Builder.class);
        }

        public interface WrapMessageOrBuilder extends
                // @@protoc_insertion_point(interface_extends:UniversalMessage.WrapMessage)
                com.google.protobuf.MessageOrBuilder {

            /**
             * <pre>
             * 时间戳
             * </pre>
             *
             * <code>uint64 timestamp = 1;</code>
             */
            long getTimestamp();

            /**
             * <pre>
             * 消息类型
             * </pre>
             *
             * <code>.MessageType msg_type = 2;</code>
             */
            int getMsgTypeValue();
            /**
             * <pre>
             * 消息类型
             * </pre>
             *
             * <code>.MessageType msg_type = 2;</code>
             */
            MsgBean.MessageType getMsgType();

            /**
             * <pre>
             * 消息id
             * </pre>
             *
             * <code>string msg_id = 3;</code>
             */
            java.lang.String getMsgId();
            /**
             * <pre>
             * 消息id
             * </pre>
             *
             * <code>string msg_id = 3;</code>
             */
            com.google.protobuf.ByteString
            getMsgIdBytes();

            /**
             * <pre>
             * 消息来源
             * </pre>
             *
             * <code>uint64 from_uid = 4;</code>
             */
            long getFromUid();

            /**
             * <code>string gid = 5;</code>
             */
            java.lang.String getGid();
            /**
             * <code>string gid = 5;</code>
             */
            com.google.protobuf.ByteString
            getGidBytes();

            /**
             * <pre>
             *昵称
             * </pre>
             *
             * <code>string nickname = 6;</code>
             */
            java.lang.String getNickname();
            /**
             * <pre>
             *昵称
             * </pre>
             *
             * <code>string nickname = 6;</code>
             */
            com.google.protobuf.ByteString
            getNicknameBytes();

            /**
             * <pre>
             *头像
             * </pre>
             *
             * <code>string avatar = 7;</code>
             */
            java.lang.String getAvatar();
            /**
             * <pre>
             *头像
             * </pre>
             *
             * <code>string avatar = 7;</code>
             */
            com.google.protobuf.ByteString
            getAvatarBytes();

            /**
             * <code>.ChatMessage chat = 10000;</code>
             */
            MsgBean.ChatMessage getChat();
            /**
             * <code>.ChatMessage chat = 10000;</code>
             */
            MsgBean.ChatMessageOrBuilder getChatOrBuilder();

            /**
             * <code>.ImageMessage image = 10001;</code>
             */
            MsgBean.ImageMessage getImage();
            /**
             * <code>.ImageMessage image = 10001;</code>
             */
            MsgBean.ImageMessageOrBuilder getImageOrBuilder();

            /**
             * <code>.RedEnvelopeMessage red_envelope = 10002;</code>
             */
            MsgBean.RedEnvelopeMessage getRedEnvelope();
            /**
             * <code>.RedEnvelopeMessage red_envelope = 10002;</code>
             */
            MsgBean.RedEnvelopeMessageOrBuilder getRedEnvelopeOrBuilder();

            /**
             * <code>.ReceiveRedEnvelopeMessage receive_red_envelope = 10003;</code>
             */
            MsgBean.ReceiveRedEnvelopeMessage getReceiveRedEnvelope();
            /**
             * <code>.ReceiveRedEnvelopeMessage receive_red_envelope = 10003;</code>
             */
            MsgBean.ReceiveRedEnvelopeMessageOrBuilder getReceiveRedEnvelopeOrBuilder();

            /**
             * <code>.TransferMessage transfer = 10004;</code>
             */
            MsgBean.TransferMessage getTransfer();
            /**
             * <code>.TransferMessage transfer = 10004;</code>
             */
            MsgBean.TransferMessageOrBuilder getTransferOrBuilder();

            /**
             * <code>.StampMessage stamp = 10005;</code>
             */
            MsgBean.StampMessage getStamp();
            /**
             * <code>.StampMessage stamp = 10005;</code>
             */
            MsgBean.StampMessageOrBuilder getStampOrBuilder();

            /**
             * <code>.BusinessCardMessage business_card = 10006;</code>
             */
            MsgBean.BusinessCardMessage getBusinessCard();
            /**
             * <code>.BusinessCardMessage business_card = 10006;</code>
             */
            MsgBean.BusinessCardMessageOrBuilder getBusinessCardOrBuilder();

            /**
             * <code>.RequestFriendMessage request_friend = 10100;</code>
             */
            MsgBean.RequestFriendMessage getRequestFriend();
            /**
             * <code>.RequestFriendMessage request_friend = 10100;</code>
             */
            MsgBean.RequestFriendMessageOrBuilder getRequestFriendOrBuilder();

            /**
             * <code>.AcceptBeFriendsMessage accept_be_friends = 10101;</code>
             */
            MsgBean.AcceptBeFriendsMessage getAcceptBeFriends();
            /**
             * <code>.AcceptBeFriendsMessage accept_be_friends = 10101;</code>
             */
            MsgBean.AcceptBeFriendsMessageOrBuilder getAcceptBeFriendsOrBuilder();

            /**
             * <code>.RemoveFriendMessage remove_friend = 10102;</code>
             */
            MsgBean.RemoveFriendMessage getRemoveFriend();
            /**
             * <code>.RemoveFriendMessage remove_friend = 10102;</code>
             */
            MsgBean.RemoveFriendMessageOrBuilder getRemoveFriendOrBuilder();

            /**
             * <code>.RequestGroupMessage request_group = 10200;</code>
             */
            MsgBean.RequestGroupMessage getRequestGroup();
            /**
             * <code>.RequestGroupMessage request_group = 10200;</code>
             */
            MsgBean.RequestGroupMessageOrBuilder getRequestGroupOrBuilder();

            /**
             * <code>.AcceptBeGroupMessage accept_be_group = 10201;</code>
             */
            MsgBean.AcceptBeGroupMessage getAcceptBeGroup();
            /**
             * <code>.AcceptBeGroupMessage accept_be_group = 10201;</code>
             */
            MsgBean.AcceptBeGroupMessageOrBuilder getAcceptBeGroupOrBuilder();

            /**
             * <code>.RemoveGroupMemberMessage remove_group_member = 10202;</code>
             */
            MsgBean.RemoveGroupMemberMessage getRemoveGroupMember();
            /**
             * <code>.RemoveGroupMemberMessage remove_group_member = 10202;</code>
             */
            MsgBean.RemoveGroupMemberMessageOrBuilder getRemoveGroupMemberOrBuilder();

            /**
             * <code>.ChangeGroupMasterMessage change_group_master = 10203;</code>
             */
            MsgBean.ChangeGroupMasterMessage getChangeGroupMaster();
            /**
             * <code>.ChangeGroupMasterMessage change_group_master = 10203;</code>
             */
            MsgBean.ChangeGroupMasterMessageOrBuilder getChangeGroupMasterOrBuilder();

            /**
             * <code>.ChangeGroupNameMessage change_group_name = 10204;</code>
             */
            MsgBean.ChangeGroupNameMessage getChangeGroupName();
            /**
             * <code>.ChangeGroupNameMessage change_group_name = 10204;</code>
             */
            MsgBean.ChangeGroupNameMessageOrBuilder getChangeGroupNameOrBuilder();

            /**
             * <code>.ChangeGroupAnnouncementMessage change_group_announcement = 10205;</code>
             */
            MsgBean.ChangeGroupAnnouncementMessage getChangeGroupAnnouncement();
            /**
             * <code>.ChangeGroupAnnouncementMessage change_group_announcement = 10205;</code>
             */
            MsgBean.ChangeGroupAnnouncementMessageOrBuilder getChangeGroupAnnouncementOrBuilder();

            /**
             * <code>.DestroyGroupMessage destroy_group = 10206;</code>
             */
            MsgBean.DestroyGroupMessage getDestroyGroup();
            /**
             * <code>.DestroyGroupMessage destroy_group = 10206;</code>
             */
            MsgBean.DestroyGroupMessageOrBuilder getDestroyGroupOrBuilder();

            /**
             * <code>.OutGroupMessage out_group = 10210;</code>
             */
            MsgBean.OutGroupMessage getOutGroup();
            /**
             * <code>.OutGroupMessage out_group = 10210;</code>
             */
            MsgBean.OutGroupMessageOrBuilder getOutGroupOrBuilder();

            /**
             * <code>.ConflictMessage conflict = 10300;</code>
             */
            MsgBean.ConflictMessage getConflict();
            /**
             * <code>.ConflictMessage conflict = 10300;</code>
             */
            MsgBean.ConflictMessageOrBuilder getConflictOrBuilder();

            public MsgBean.UniversalMessage.WrapMessage.RealMsgCase getRealMsgCase();
        }
        /**
         * Protobuf type {@code UniversalMessage.WrapMessage}
         */
        public  static final class WrapMessage extends
                com.google.protobuf.GeneratedMessageV3 implements
                // @@protoc_insertion_point(message_implements:UniversalMessage.WrapMessage)
                WrapMessageOrBuilder {
            // Use WrapMessage.newBuilder() to construct.
            private WrapMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
                super(builder);
            }
            private WrapMessage() {
                timestamp_ = 0L;
                msgType_ = 0;
                msgId_ = "";
                fromUid_ = 0L;
                gid_ = "";
                nickname_ = "";
                avatar_ = "";
            }

            @java.lang.Override
            public final com.google.protobuf.UnknownFieldSet
            getUnknownFields() {
                return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
            }
            private WrapMessage(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                this();
                int mutable_bitField0_ = 0;
                try {
                    boolean done = false;
                    while (!done) {
                        int tag = input.readTag();
                        switch (tag) {
                            case 0:
                                done = true;
                                break;
                            default: {
                                if (!input.skipField(tag)) {
                                    done = true;
                                }
                                break;
                            }
                            case 8: {

                                timestamp_ = input.readUInt64();
                                break;
                            }
                            case 16: {
                                int rawValue = input.readEnum();

                                msgType_ = rawValue;
                                break;
                            }
                            case 26: {
                                java.lang.String s = input.readStringRequireUtf8();

                                msgId_ = s;
                                break;
                            }
                            case 32: {

                                fromUid_ = input.readUInt64();
                                break;
                            }
                            case 42: {
                                java.lang.String s = input.readStringRequireUtf8();

                                gid_ = s;
                                break;
                            }
                            case 50: {
                                java.lang.String s = input.readStringRequireUtf8();

                                nickname_ = s;
                                break;
                            }
                            case 58: {
                                java.lang.String s = input.readStringRequireUtf8();

                                avatar_ = s;
                                break;
                            }
                            case 80002: {
                                MsgBean.ChatMessage.Builder subBuilder = null;
                                if (realMsgCase_ == 10000) {
                                    subBuilder = ((MsgBean.ChatMessage) realMsg_).toBuilder();
                                }
                                realMsg_ =
                                        input.readMessage(MsgBean.ChatMessage.parser(), extensionRegistry);
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom((MsgBean.ChatMessage) realMsg_);
                                    realMsg_ = subBuilder.buildPartial();
                                }
                                realMsgCase_ = 10000;
                                break;
                            }
                            case 80010: {
                                MsgBean.ImageMessage.Builder subBuilder = null;
                                if (realMsgCase_ == 10001) {
                                    subBuilder = ((MsgBean.ImageMessage) realMsg_).toBuilder();
                                }
                                realMsg_ =
                                        input.readMessage(MsgBean.ImageMessage.parser(), extensionRegistry);
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom((MsgBean.ImageMessage) realMsg_);
                                    realMsg_ = subBuilder.buildPartial();
                                }
                                realMsgCase_ = 10001;
                                break;
                            }
                            case 80018: {
                                MsgBean.RedEnvelopeMessage.Builder subBuilder = null;
                                if (realMsgCase_ == 10002) {
                                    subBuilder = ((MsgBean.RedEnvelopeMessage) realMsg_).toBuilder();
                                }
                                realMsg_ =
                                        input.readMessage(MsgBean.RedEnvelopeMessage.parser(), extensionRegistry);
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom((MsgBean.RedEnvelopeMessage) realMsg_);
                                    realMsg_ = subBuilder.buildPartial();
                                }
                                realMsgCase_ = 10002;
                                break;
                            }
                            case 80026: {
                                MsgBean.ReceiveRedEnvelopeMessage.Builder subBuilder = null;
                                if (realMsgCase_ == 10003) {
                                    subBuilder = ((MsgBean.ReceiveRedEnvelopeMessage) realMsg_).toBuilder();
                                }
                                realMsg_ =
                                        input.readMessage(MsgBean.ReceiveRedEnvelopeMessage.parser(), extensionRegistry);
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom((MsgBean.ReceiveRedEnvelopeMessage) realMsg_);
                                    realMsg_ = subBuilder.buildPartial();
                                }
                                realMsgCase_ = 10003;
                                break;
                            }
                            case 80034: {
                                MsgBean.TransferMessage.Builder subBuilder = null;
                                if (realMsgCase_ == 10004) {
                                    subBuilder = ((MsgBean.TransferMessage) realMsg_).toBuilder();
                                }
                                realMsg_ =
                                        input.readMessage(MsgBean.TransferMessage.parser(), extensionRegistry);
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom((MsgBean.TransferMessage) realMsg_);
                                    realMsg_ = subBuilder.buildPartial();
                                }
                                realMsgCase_ = 10004;
                                break;
                            }
                            case 80042: {
                                MsgBean.StampMessage.Builder subBuilder = null;
                                if (realMsgCase_ == 10005) {
                                    subBuilder = ((MsgBean.StampMessage) realMsg_).toBuilder();
                                }
                                realMsg_ =
                                        input.readMessage(MsgBean.StampMessage.parser(), extensionRegistry);
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom((MsgBean.StampMessage) realMsg_);
                                    realMsg_ = subBuilder.buildPartial();
                                }
                                realMsgCase_ = 10005;
                                break;
                            }
                            case 80050: {
                                MsgBean.BusinessCardMessage.Builder subBuilder = null;
                                if (realMsgCase_ == 10006) {
                                    subBuilder = ((MsgBean.BusinessCardMessage) realMsg_).toBuilder();
                                }
                                realMsg_ =
                                        input.readMessage(MsgBean.BusinessCardMessage.parser(), extensionRegistry);
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom((MsgBean.BusinessCardMessage) realMsg_);
                                    realMsg_ = subBuilder.buildPartial();
                                }
                                realMsgCase_ = 10006;
                                break;
                            }
                            case 80802: {
                                MsgBean.RequestFriendMessage.Builder subBuilder = null;
                                if (realMsgCase_ == 10100) {
                                    subBuilder = ((MsgBean.RequestFriendMessage) realMsg_).toBuilder();
                                }
                                realMsg_ =
                                        input.readMessage(MsgBean.RequestFriendMessage.parser(), extensionRegistry);
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom((MsgBean.RequestFriendMessage) realMsg_);
                                    realMsg_ = subBuilder.buildPartial();
                                }
                                realMsgCase_ = 10100;
                                break;
                            }
                            case 80810: {
                                MsgBean.AcceptBeFriendsMessage.Builder subBuilder = null;
                                if (realMsgCase_ == 10101) {
                                    subBuilder = ((MsgBean.AcceptBeFriendsMessage) realMsg_).toBuilder();
                                }
                                realMsg_ =
                                        input.readMessage(MsgBean.AcceptBeFriendsMessage.parser(), extensionRegistry);
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom((MsgBean.AcceptBeFriendsMessage) realMsg_);
                                    realMsg_ = subBuilder.buildPartial();
                                }
                                realMsgCase_ = 10101;
                                break;
                            }
                            case 80818: {
                                MsgBean.RemoveFriendMessage.Builder subBuilder = null;
                                if (realMsgCase_ == 10102) {
                                    subBuilder = ((MsgBean.RemoveFriendMessage) realMsg_).toBuilder();
                                }
                                realMsg_ =
                                        input.readMessage(MsgBean.RemoveFriendMessage.parser(), extensionRegistry);
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom((MsgBean.RemoveFriendMessage) realMsg_);
                                    realMsg_ = subBuilder.buildPartial();
                                }
                                realMsgCase_ = 10102;
                                break;
                            }
                            case 81602: {
                                MsgBean.RequestGroupMessage.Builder subBuilder = null;
                                if (realMsgCase_ == 10200) {
                                    subBuilder = ((MsgBean.RequestGroupMessage) realMsg_).toBuilder();
                                }
                                realMsg_ =
                                        input.readMessage(MsgBean.RequestGroupMessage.parser(), extensionRegistry);
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom((MsgBean.RequestGroupMessage) realMsg_);
                                    realMsg_ = subBuilder.buildPartial();
                                }
                                realMsgCase_ = 10200;
                                break;
                            }
                            case 81610: {
                                MsgBean.AcceptBeGroupMessage.Builder subBuilder = null;
                                if (realMsgCase_ == 10201) {
                                    subBuilder = ((MsgBean.AcceptBeGroupMessage) realMsg_).toBuilder();
                                }
                                realMsg_ =
                                        input.readMessage(MsgBean.AcceptBeGroupMessage.parser(), extensionRegistry);
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom((MsgBean.AcceptBeGroupMessage) realMsg_);
                                    realMsg_ = subBuilder.buildPartial();
                                }
                                realMsgCase_ = 10201;
                                break;
                            }
                            case 81618: {
                                MsgBean.RemoveGroupMemberMessage.Builder subBuilder = null;
                                if (realMsgCase_ == 10202) {
                                    subBuilder = ((MsgBean.RemoveGroupMemberMessage) realMsg_).toBuilder();
                                }
                                realMsg_ =
                                        input.readMessage(MsgBean.RemoveGroupMemberMessage.parser(), extensionRegistry);
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom((MsgBean.RemoveGroupMemberMessage) realMsg_);
                                    realMsg_ = subBuilder.buildPartial();
                                }
                                realMsgCase_ = 10202;
                                break;
                            }
                            case 81626: {
                                MsgBean.ChangeGroupMasterMessage.Builder subBuilder = null;
                                if (realMsgCase_ == 10203) {
                                    subBuilder = ((MsgBean.ChangeGroupMasterMessage) realMsg_).toBuilder();
                                }
                                realMsg_ =
                                        input.readMessage(MsgBean.ChangeGroupMasterMessage.parser(), extensionRegistry);
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom((MsgBean.ChangeGroupMasterMessage) realMsg_);
                                    realMsg_ = subBuilder.buildPartial();
                                }
                                realMsgCase_ = 10203;
                                break;
                            }
                            case 81634: {
                                MsgBean.ChangeGroupNameMessage.Builder subBuilder = null;
                                if (realMsgCase_ == 10204) {
                                    subBuilder = ((MsgBean.ChangeGroupNameMessage) realMsg_).toBuilder();
                                }
                                realMsg_ =
                                        input.readMessage(MsgBean.ChangeGroupNameMessage.parser(), extensionRegistry);
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom((MsgBean.ChangeGroupNameMessage) realMsg_);
                                    realMsg_ = subBuilder.buildPartial();
                                }
                                realMsgCase_ = 10204;
                                break;
                            }
                            case 81642: {
                                MsgBean.ChangeGroupAnnouncementMessage.Builder subBuilder = null;
                                if (realMsgCase_ == 10205) {
                                    subBuilder = ((MsgBean.ChangeGroupAnnouncementMessage) realMsg_).toBuilder();
                                }
                                realMsg_ =
                                        input.readMessage(MsgBean.ChangeGroupAnnouncementMessage.parser(), extensionRegistry);
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom((MsgBean.ChangeGroupAnnouncementMessage) realMsg_);
                                    realMsg_ = subBuilder.buildPartial();
                                }
                                realMsgCase_ = 10205;
                                break;
                            }
                            case 81650: {
                                MsgBean.DestroyGroupMessage.Builder subBuilder = null;
                                if (realMsgCase_ == 10206) {
                                    subBuilder = ((MsgBean.DestroyGroupMessage) realMsg_).toBuilder();
                                }
                                realMsg_ =
                                        input.readMessage(MsgBean.DestroyGroupMessage.parser(), extensionRegistry);
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom((MsgBean.DestroyGroupMessage) realMsg_);
                                    realMsg_ = subBuilder.buildPartial();
                                }
                                realMsgCase_ = 10206;
                                break;
                            }
                            case 81682: {
                                MsgBean.OutGroupMessage.Builder subBuilder = null;
                                if (realMsgCase_ == 10210) {
                                    subBuilder = ((MsgBean.OutGroupMessage) realMsg_).toBuilder();
                                }
                                realMsg_ =
                                        input.readMessage(MsgBean.OutGroupMessage.parser(), extensionRegistry);
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom((MsgBean.OutGroupMessage) realMsg_);
                                    realMsg_ = subBuilder.buildPartial();
                                }
                                realMsgCase_ = 10210;
                                break;
                            }
                            case 82402: {
                                MsgBean.ConflictMessage.Builder subBuilder = null;
                                if (realMsgCase_ == 10300) {
                                    subBuilder = ((MsgBean.ConflictMessage) realMsg_).toBuilder();
                                }
                                realMsg_ =
                                        input.readMessage(MsgBean.ConflictMessage.parser(), extensionRegistry);
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom((MsgBean.ConflictMessage) realMsg_);
                                    realMsg_ = subBuilder.buildPartial();
                                }
                                realMsgCase_ = 10300;
                                break;
                            }
                        }
                    }
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                } catch (java.io.IOException e) {
                    throw new com.google.protobuf.InvalidProtocolBufferException(
                            e).setUnfinishedMessage(this);
                } finally {
                    makeExtensionsImmutable();
                }
            }
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_UniversalMessage_WrapMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_UniversalMessage_WrapMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.UniversalMessage.WrapMessage.class, MsgBean.UniversalMessage.WrapMessage.Builder.class);
            }

            private int realMsgCase_ = 0;
            private java.lang.Object realMsg_;
            public enum RealMsgCase
                    implements com.google.protobuf.Internal.EnumLite {
                CHAT(10000),
                IMAGE(10001),
                RED_ENVELOPE(10002),
                RECEIVE_RED_ENVELOPE(10003),
                TRANSFER(10004),
                STAMP(10005),
                BUSINESS_CARD(10006),
                REQUEST_FRIEND(10100),
                ACCEPT_BE_FRIENDS(10101),
                REMOVE_FRIEND(10102),
                REQUEST_GROUP(10200),
                ACCEPT_BE_GROUP(10201),
                REMOVE_GROUP_MEMBER(10202),
                CHANGE_GROUP_MASTER(10203),
                CHANGE_GROUP_NAME(10204),
                CHANGE_GROUP_ANNOUNCEMENT(10205),
                DESTROY_GROUP(10206),
                OUT_GROUP(10210),
                CONFLICT(10300),
                REALMSG_NOT_SET(0);
                private final int value;
                private RealMsgCase(int value) {
                    this.value = value;
                }
                /**
                 * @deprecated Use {@link #forNumber(int)} instead.
                 */
                @java.lang.Deprecated
                public static RealMsgCase valueOf(int value) {
                    return forNumber(value);
                }

                public static RealMsgCase forNumber(int value) {
                    switch (value) {
                        case 10000: return CHAT;
                        case 10001: return IMAGE;
                        case 10002: return RED_ENVELOPE;
                        case 10003: return RECEIVE_RED_ENVELOPE;
                        case 10004: return TRANSFER;
                        case 10005: return STAMP;
                        case 10006: return BUSINESS_CARD;
                        case 10100: return REQUEST_FRIEND;
                        case 10101: return ACCEPT_BE_FRIENDS;
                        case 10102: return REMOVE_FRIEND;
                        case 10200: return REQUEST_GROUP;
                        case 10201: return ACCEPT_BE_GROUP;
                        case 10202: return REMOVE_GROUP_MEMBER;
                        case 10203: return CHANGE_GROUP_MASTER;
                        case 10204: return CHANGE_GROUP_NAME;
                        case 10205: return CHANGE_GROUP_ANNOUNCEMENT;
                        case 10206: return DESTROY_GROUP;
                        case 10210: return OUT_GROUP;
                        case 10300: return CONFLICT;
                        case 0: return REALMSG_NOT_SET;
                        default: return null;
                    }
                }
                public int getNumber() {
                    return this.value;
                }
            };

            public RealMsgCase
            getRealMsgCase() {
                return RealMsgCase.forNumber(
                        realMsgCase_);
            }

            public static final int TIMESTAMP_FIELD_NUMBER = 1;
            private long timestamp_;
            /**
             * <pre>
             * 时间戳
             * </pre>
             *
             * <code>uint64 timestamp = 1;</code>
             */
            public long getTimestamp() {
                return timestamp_;
            }

            public static final int MSG_TYPE_FIELD_NUMBER = 2;
            private int msgType_;
            /**
             * <pre>
             * 消息类型
             * </pre>
             *
             * <code>.MessageType msg_type = 2;</code>
             */
            public int getMsgTypeValue() {
                return msgType_;
            }
            /**
             * <pre>
             * 消息类型
             * </pre>
             *
             * <code>.MessageType msg_type = 2;</code>
             */
            public MsgBean.MessageType getMsgType() {
                MsgBean.MessageType result = MsgBean.MessageType.valueOf(msgType_);
                return result == null ? MsgBean.MessageType.UNRECOGNIZED : result;
            }

            public static final int MSG_ID_FIELD_NUMBER = 3;
            private volatile java.lang.Object msgId_;
            /**
             * <pre>
             * 消息id
             * </pre>
             *
             * <code>string msg_id = 3;</code>
             */
            public java.lang.String getMsgId() {
                java.lang.Object ref = msgId_;
                if (ref instanceof java.lang.String) {
                    return (java.lang.String) ref;
                } else {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    msgId_ = s;
                    return s;
                }
            }
            /**
             * <pre>
             * 消息id
             * </pre>
             *
             * <code>string msg_id = 3;</code>
             */
            public com.google.protobuf.ByteString
            getMsgIdBytes() {
                java.lang.Object ref = msgId_;
                if (ref instanceof java.lang.String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    msgId_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }

            public static final int FROM_UID_FIELD_NUMBER = 4;
            private long fromUid_;
            /**
             * <pre>
             * 消息来源
             * </pre>
             *
             * <code>uint64 from_uid = 4;</code>
             */
            public long getFromUid() {
                return fromUid_;
            }

            public static final int GID_FIELD_NUMBER = 5;
            private volatile java.lang.Object gid_;
            /**
             * <code>string gid = 5;</code>
             */
            public java.lang.String getGid() {
                java.lang.Object ref = gid_;
                if (ref instanceof java.lang.String) {
                    return (java.lang.String) ref;
                } else {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    gid_ = s;
                    return s;
                }
            }
            /**
             * <code>string gid = 5;</code>
             */
            public com.google.protobuf.ByteString
            getGidBytes() {
                java.lang.Object ref = gid_;
                if (ref instanceof java.lang.String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    gid_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }

            public static final int NICKNAME_FIELD_NUMBER = 6;
            private volatile java.lang.Object nickname_;
            /**
             * <pre>
             *昵称
             * </pre>
             *
             * <code>string nickname = 6;</code>
             */
            public java.lang.String getNickname() {
                java.lang.Object ref = nickname_;
                if (ref instanceof java.lang.String) {
                    return (java.lang.String) ref;
                } else {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    nickname_ = s;
                    return s;
                }
            }
            /**
             * <pre>
             *昵称
             * </pre>
             *
             * <code>string nickname = 6;</code>
             */
            public com.google.protobuf.ByteString
            getNicknameBytes() {
                java.lang.Object ref = nickname_;
                if (ref instanceof java.lang.String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    nickname_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }

            public static final int AVATAR_FIELD_NUMBER = 7;
            private volatile java.lang.Object avatar_;
            /**
             * <pre>
             *头像
             * </pre>
             *
             * <code>string avatar = 7;</code>
             */
            public java.lang.String getAvatar() {
                java.lang.Object ref = avatar_;
                if (ref instanceof java.lang.String) {
                    return (java.lang.String) ref;
                } else {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    avatar_ = s;
                    return s;
                }
            }
            /**
             * <pre>
             *头像
             * </pre>
             *
             * <code>string avatar = 7;</code>
             */
            public com.google.protobuf.ByteString
            getAvatarBytes() {
                java.lang.Object ref = avatar_;
                if (ref instanceof java.lang.String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    avatar_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }

            public static final int CHAT_FIELD_NUMBER = 10000;
            /**
             * <code>.ChatMessage chat = 10000;</code>
             */
            public MsgBean.ChatMessage getChat() {
                if (realMsgCase_ == 10000) {
                    return (MsgBean.ChatMessage) realMsg_;
                }
                return MsgBean.ChatMessage.getDefaultInstance();
            }
            /**
             * <code>.ChatMessage chat = 10000;</code>
             */
            public MsgBean.ChatMessageOrBuilder getChatOrBuilder() {
                if (realMsgCase_ == 10000) {
                    return (MsgBean.ChatMessage) realMsg_;
                }
                return MsgBean.ChatMessage.getDefaultInstance();
            }

            public static final int IMAGE_FIELD_NUMBER = 10001;
            /**
             * <code>.ImageMessage image = 10001;</code>
             */
            public MsgBean.ImageMessage getImage() {
                if (realMsgCase_ == 10001) {
                    return (MsgBean.ImageMessage) realMsg_;
                }
                return MsgBean.ImageMessage.getDefaultInstance();
            }
            /**
             * <code>.ImageMessage image = 10001;</code>
             */
            public MsgBean.ImageMessageOrBuilder getImageOrBuilder() {
                if (realMsgCase_ == 10001) {
                    return (MsgBean.ImageMessage) realMsg_;
                }
                return MsgBean.ImageMessage.getDefaultInstance();
            }

            public static final int RED_ENVELOPE_FIELD_NUMBER = 10002;
            /**
             * <code>.RedEnvelopeMessage red_envelope = 10002;</code>
             */
            public MsgBean.RedEnvelopeMessage getRedEnvelope() {
                if (realMsgCase_ == 10002) {
                    return (MsgBean.RedEnvelopeMessage) realMsg_;
                }
                return MsgBean.RedEnvelopeMessage.getDefaultInstance();
            }
            /**
             * <code>.RedEnvelopeMessage red_envelope = 10002;</code>
             */
            public MsgBean.RedEnvelopeMessageOrBuilder getRedEnvelopeOrBuilder() {
                if (realMsgCase_ == 10002) {
                    return (MsgBean.RedEnvelopeMessage) realMsg_;
                }
                return MsgBean.RedEnvelopeMessage.getDefaultInstance();
            }

            public static final int RECEIVE_RED_ENVELOPE_FIELD_NUMBER = 10003;
            /**
             * <code>.ReceiveRedEnvelopeMessage receive_red_envelope = 10003;</code>
             */
            public MsgBean.ReceiveRedEnvelopeMessage getReceiveRedEnvelope() {
                if (realMsgCase_ == 10003) {
                    return (MsgBean.ReceiveRedEnvelopeMessage) realMsg_;
                }
                return MsgBean.ReceiveRedEnvelopeMessage.getDefaultInstance();
            }
            /**
             * <code>.ReceiveRedEnvelopeMessage receive_red_envelope = 10003;</code>
             */
            public MsgBean.ReceiveRedEnvelopeMessageOrBuilder getReceiveRedEnvelopeOrBuilder() {
                if (realMsgCase_ == 10003) {
                    return (MsgBean.ReceiveRedEnvelopeMessage) realMsg_;
                }
                return MsgBean.ReceiveRedEnvelopeMessage.getDefaultInstance();
            }

            public static final int TRANSFER_FIELD_NUMBER = 10004;
            /**
             * <code>.TransferMessage transfer = 10004;</code>
             */
            public MsgBean.TransferMessage getTransfer() {
                if (realMsgCase_ == 10004) {
                    return (MsgBean.TransferMessage) realMsg_;
                }
                return MsgBean.TransferMessage.getDefaultInstance();
            }
            /**
             * <code>.TransferMessage transfer = 10004;</code>
             */
            public MsgBean.TransferMessageOrBuilder getTransferOrBuilder() {
                if (realMsgCase_ == 10004) {
                    return (MsgBean.TransferMessage) realMsg_;
                }
                return MsgBean.TransferMessage.getDefaultInstance();
            }

            public static final int STAMP_FIELD_NUMBER = 10005;
            /**
             * <code>.StampMessage stamp = 10005;</code>
             */
            public MsgBean.StampMessage getStamp() {
                if (realMsgCase_ == 10005) {
                    return (MsgBean.StampMessage) realMsg_;
                }
                return MsgBean.StampMessage.getDefaultInstance();
            }
            /**
             * <code>.StampMessage stamp = 10005;</code>
             */
            public MsgBean.StampMessageOrBuilder getStampOrBuilder() {
                if (realMsgCase_ == 10005) {
                    return (MsgBean.StampMessage) realMsg_;
                }
                return MsgBean.StampMessage.getDefaultInstance();
            }

            public static final int BUSINESS_CARD_FIELD_NUMBER = 10006;
            /**
             * <code>.BusinessCardMessage business_card = 10006;</code>
             */
            public MsgBean.BusinessCardMessage getBusinessCard() {
                if (realMsgCase_ == 10006) {
                    return (MsgBean.BusinessCardMessage) realMsg_;
                }
                return MsgBean.BusinessCardMessage.getDefaultInstance();
            }
            /**
             * <code>.BusinessCardMessage business_card = 10006;</code>
             */
            public MsgBean.BusinessCardMessageOrBuilder getBusinessCardOrBuilder() {
                if (realMsgCase_ == 10006) {
                    return (MsgBean.BusinessCardMessage) realMsg_;
                }
                return MsgBean.BusinessCardMessage.getDefaultInstance();
            }

            public static final int REQUEST_FRIEND_FIELD_NUMBER = 10100;
            /**
             * <code>.RequestFriendMessage request_friend = 10100;</code>
             */
            public MsgBean.RequestFriendMessage getRequestFriend() {
                if (realMsgCase_ == 10100) {
                    return (MsgBean.RequestFriendMessage) realMsg_;
                }
                return MsgBean.RequestFriendMessage.getDefaultInstance();
            }
            /**
             * <code>.RequestFriendMessage request_friend = 10100;</code>
             */
            public MsgBean.RequestFriendMessageOrBuilder getRequestFriendOrBuilder() {
                if (realMsgCase_ == 10100) {
                    return (MsgBean.RequestFriendMessage) realMsg_;
                }
                return MsgBean.RequestFriendMessage.getDefaultInstance();
            }

            public static final int ACCEPT_BE_FRIENDS_FIELD_NUMBER = 10101;
            /**
             * <code>.AcceptBeFriendsMessage accept_be_friends = 10101;</code>
             */
            public MsgBean.AcceptBeFriendsMessage getAcceptBeFriends() {
                if (realMsgCase_ == 10101) {
                    return (MsgBean.AcceptBeFriendsMessage) realMsg_;
                }
                return MsgBean.AcceptBeFriendsMessage.getDefaultInstance();
            }
            /**
             * <code>.AcceptBeFriendsMessage accept_be_friends = 10101;</code>
             */
            public MsgBean.AcceptBeFriendsMessageOrBuilder getAcceptBeFriendsOrBuilder() {
                if (realMsgCase_ == 10101) {
                    return (MsgBean.AcceptBeFriendsMessage) realMsg_;
                }
                return MsgBean.AcceptBeFriendsMessage.getDefaultInstance();
            }

            public static final int REMOVE_FRIEND_FIELD_NUMBER = 10102;
            /**
             * <code>.RemoveFriendMessage remove_friend = 10102;</code>
             */
            public MsgBean.RemoveFriendMessage getRemoveFriend() {
                if (realMsgCase_ == 10102) {
                    return (MsgBean.RemoveFriendMessage) realMsg_;
                }
                return MsgBean.RemoveFriendMessage.getDefaultInstance();
            }
            /**
             * <code>.RemoveFriendMessage remove_friend = 10102;</code>
             */
            public MsgBean.RemoveFriendMessageOrBuilder getRemoveFriendOrBuilder() {
                if (realMsgCase_ == 10102) {
                    return (MsgBean.RemoveFriendMessage) realMsg_;
                }
                return MsgBean.RemoveFriendMessage.getDefaultInstance();
            }

            public static final int REQUEST_GROUP_FIELD_NUMBER = 10200;
            /**
             * <code>.RequestGroupMessage request_group = 10200;</code>
             */
            public MsgBean.RequestGroupMessage getRequestGroup() {
                if (realMsgCase_ == 10200) {
                    return (MsgBean.RequestGroupMessage) realMsg_;
                }
                return MsgBean.RequestGroupMessage.getDefaultInstance();
            }
            /**
             * <code>.RequestGroupMessage request_group = 10200;</code>
             */
            public MsgBean.RequestGroupMessageOrBuilder getRequestGroupOrBuilder() {
                if (realMsgCase_ == 10200) {
                    return (MsgBean.RequestGroupMessage) realMsg_;
                }
                return MsgBean.RequestGroupMessage.getDefaultInstance();
            }

            public static final int ACCEPT_BE_GROUP_FIELD_NUMBER = 10201;
            /**
             * <code>.AcceptBeGroupMessage accept_be_group = 10201;</code>
             */
            public MsgBean.AcceptBeGroupMessage getAcceptBeGroup() {
                if (realMsgCase_ == 10201) {
                    return (MsgBean.AcceptBeGroupMessage) realMsg_;
                }
                return MsgBean.AcceptBeGroupMessage.getDefaultInstance();
            }
            /**
             * <code>.AcceptBeGroupMessage accept_be_group = 10201;</code>
             */
            public MsgBean.AcceptBeGroupMessageOrBuilder getAcceptBeGroupOrBuilder() {
                if (realMsgCase_ == 10201) {
                    return (MsgBean.AcceptBeGroupMessage) realMsg_;
                }
                return MsgBean.AcceptBeGroupMessage.getDefaultInstance();
            }

            public static final int REMOVE_GROUP_MEMBER_FIELD_NUMBER = 10202;
            /**
             * <code>.RemoveGroupMemberMessage remove_group_member = 10202;</code>
             */
            public MsgBean.RemoveGroupMemberMessage getRemoveGroupMember() {
                if (realMsgCase_ == 10202) {
                    return (MsgBean.RemoveGroupMemberMessage) realMsg_;
                }
                return MsgBean.RemoveGroupMemberMessage.getDefaultInstance();
            }
            /**
             * <code>.RemoveGroupMemberMessage remove_group_member = 10202;</code>
             */
            public MsgBean.RemoveGroupMemberMessageOrBuilder getRemoveGroupMemberOrBuilder() {
                if (realMsgCase_ == 10202) {
                    return (MsgBean.RemoveGroupMemberMessage) realMsg_;
                }
                return MsgBean.RemoveGroupMemberMessage.getDefaultInstance();
            }

            public static final int CHANGE_GROUP_MASTER_FIELD_NUMBER = 10203;
            /**
             * <code>.ChangeGroupMasterMessage change_group_master = 10203;</code>
             */
            public MsgBean.ChangeGroupMasterMessage getChangeGroupMaster() {
                if (realMsgCase_ == 10203) {
                    return (MsgBean.ChangeGroupMasterMessage) realMsg_;
                }
                return MsgBean.ChangeGroupMasterMessage.getDefaultInstance();
            }
            /**
             * <code>.ChangeGroupMasterMessage change_group_master = 10203;</code>
             */
            public MsgBean.ChangeGroupMasterMessageOrBuilder getChangeGroupMasterOrBuilder() {
                if (realMsgCase_ == 10203) {
                    return (MsgBean.ChangeGroupMasterMessage) realMsg_;
                }
                return MsgBean.ChangeGroupMasterMessage.getDefaultInstance();
            }

            public static final int CHANGE_GROUP_NAME_FIELD_NUMBER = 10204;
            /**
             * <code>.ChangeGroupNameMessage change_group_name = 10204;</code>
             */
            public MsgBean.ChangeGroupNameMessage getChangeGroupName() {
                if (realMsgCase_ == 10204) {
                    return (MsgBean.ChangeGroupNameMessage) realMsg_;
                }
                return MsgBean.ChangeGroupNameMessage.getDefaultInstance();
            }
            /**
             * <code>.ChangeGroupNameMessage change_group_name = 10204;</code>
             */
            public MsgBean.ChangeGroupNameMessageOrBuilder getChangeGroupNameOrBuilder() {
                if (realMsgCase_ == 10204) {
                    return (MsgBean.ChangeGroupNameMessage) realMsg_;
                }
                return MsgBean.ChangeGroupNameMessage.getDefaultInstance();
            }

            public static final int CHANGE_GROUP_ANNOUNCEMENT_FIELD_NUMBER = 10205;
            /**
             * <code>.ChangeGroupAnnouncementMessage change_group_announcement = 10205;</code>
             */
            public MsgBean.ChangeGroupAnnouncementMessage getChangeGroupAnnouncement() {
                if (realMsgCase_ == 10205) {
                    return (MsgBean.ChangeGroupAnnouncementMessage) realMsg_;
                }
                return MsgBean.ChangeGroupAnnouncementMessage.getDefaultInstance();
            }
            /**
             * <code>.ChangeGroupAnnouncementMessage change_group_announcement = 10205;</code>
             */
            public MsgBean.ChangeGroupAnnouncementMessageOrBuilder getChangeGroupAnnouncementOrBuilder() {
                if (realMsgCase_ == 10205) {
                    return (MsgBean.ChangeGroupAnnouncementMessage) realMsg_;
                }
                return MsgBean.ChangeGroupAnnouncementMessage.getDefaultInstance();
            }

            public static final int DESTROY_GROUP_FIELD_NUMBER = 10206;
            /**
             * <code>.DestroyGroupMessage destroy_group = 10206;</code>
             */
            public MsgBean.DestroyGroupMessage getDestroyGroup() {
                if (realMsgCase_ == 10206) {
                    return (MsgBean.DestroyGroupMessage) realMsg_;
                }
                return MsgBean.DestroyGroupMessage.getDefaultInstance();
            }
            /**
             * <code>.DestroyGroupMessage destroy_group = 10206;</code>
             */
            public MsgBean.DestroyGroupMessageOrBuilder getDestroyGroupOrBuilder() {
                if (realMsgCase_ == 10206) {
                    return (MsgBean.DestroyGroupMessage) realMsg_;
                }
                return MsgBean.DestroyGroupMessage.getDefaultInstance();
            }

            public static final int OUT_GROUP_FIELD_NUMBER = 10210;
            /**
             * <code>.OutGroupMessage out_group = 10210;</code>
             */
            public MsgBean.OutGroupMessage getOutGroup() {
                if (realMsgCase_ == 10210) {
                    return (MsgBean.OutGroupMessage) realMsg_;
                }
                return MsgBean.OutGroupMessage.getDefaultInstance();
            }
            /**
             * <code>.OutGroupMessage out_group = 10210;</code>
             */
            public MsgBean.OutGroupMessageOrBuilder getOutGroupOrBuilder() {
                if (realMsgCase_ == 10210) {
                    return (MsgBean.OutGroupMessage) realMsg_;
                }
                return MsgBean.OutGroupMessage.getDefaultInstance();
            }

            public static final int CONFLICT_FIELD_NUMBER = 10300;
            /**
             * <code>.ConflictMessage conflict = 10300;</code>
             */
            public MsgBean.ConflictMessage getConflict() {
                if (realMsgCase_ == 10300) {
                    return (MsgBean.ConflictMessage) realMsg_;
                }
                return MsgBean.ConflictMessage.getDefaultInstance();
            }
            /**
             * <code>.ConflictMessage conflict = 10300;</code>
             */
            public MsgBean.ConflictMessageOrBuilder getConflictOrBuilder() {
                if (realMsgCase_ == 10300) {
                    return (MsgBean.ConflictMessage) realMsg_;
                }
                return MsgBean.ConflictMessage.getDefaultInstance();
            }

            private byte memoizedIsInitialized = -1;
            public final boolean isInitialized() {
                byte isInitialized = memoizedIsInitialized;
                if (isInitialized == 1) return true;
                if (isInitialized == 0) return false;

                memoizedIsInitialized = 1;
                return true;
            }

            public void writeTo(com.google.protobuf.CodedOutputStream output)
                    throws java.io.IOException {
                if (timestamp_ != 0L) {
                    output.writeUInt64(1, timestamp_);
                }
                if (msgType_ != MsgBean.MessageType.CHAT.getNumber()) {
                    output.writeEnum(2, msgType_);
                }
                if (!getMsgIdBytes().isEmpty()) {
                    com.google.protobuf.GeneratedMessageV3.writeString(output, 3, msgId_);
                }
                if (fromUid_ != 0L) {
                    output.writeUInt64(4, fromUid_);
                }
                if (!getGidBytes().isEmpty()) {
                    com.google.protobuf.GeneratedMessageV3.writeString(output, 5, gid_);
                }
                if (!getNicknameBytes().isEmpty()) {
                    com.google.protobuf.GeneratedMessageV3.writeString(output, 6, nickname_);
                }
                if (!getAvatarBytes().isEmpty()) {
                    com.google.protobuf.GeneratedMessageV3.writeString(output, 7, avatar_);
                }
                if (realMsgCase_ == 10000) {
                    output.writeMessage(10000, (MsgBean.ChatMessage) realMsg_);
                }
                if (realMsgCase_ == 10001) {
                    output.writeMessage(10001, (MsgBean.ImageMessage) realMsg_);
                }
                if (realMsgCase_ == 10002) {
                    output.writeMessage(10002, (MsgBean.RedEnvelopeMessage) realMsg_);
                }
                if (realMsgCase_ == 10003) {
                    output.writeMessage(10003, (MsgBean.ReceiveRedEnvelopeMessage) realMsg_);
                }
                if (realMsgCase_ == 10004) {
                    output.writeMessage(10004, (MsgBean.TransferMessage) realMsg_);
                }
                if (realMsgCase_ == 10005) {
                    output.writeMessage(10005, (MsgBean.StampMessage) realMsg_);
                }
                if (realMsgCase_ == 10006) {
                    output.writeMessage(10006, (MsgBean.BusinessCardMessage) realMsg_);
                }
                if (realMsgCase_ == 10100) {
                    output.writeMessage(10100, (MsgBean.RequestFriendMessage) realMsg_);
                }
                if (realMsgCase_ == 10101) {
                    output.writeMessage(10101, (MsgBean.AcceptBeFriendsMessage) realMsg_);
                }
                if (realMsgCase_ == 10102) {
                    output.writeMessage(10102, (MsgBean.RemoveFriendMessage) realMsg_);
                }
                if (realMsgCase_ == 10200) {
                    output.writeMessage(10200, (MsgBean.RequestGroupMessage) realMsg_);
                }
                if (realMsgCase_ == 10201) {
                    output.writeMessage(10201, (MsgBean.AcceptBeGroupMessage) realMsg_);
                }
                if (realMsgCase_ == 10202) {
                    output.writeMessage(10202, (MsgBean.RemoveGroupMemberMessage) realMsg_);
                }
                if (realMsgCase_ == 10203) {
                    output.writeMessage(10203, (MsgBean.ChangeGroupMasterMessage) realMsg_);
                }
                if (realMsgCase_ == 10204) {
                    output.writeMessage(10204, (MsgBean.ChangeGroupNameMessage) realMsg_);
                }
                if (realMsgCase_ == 10205) {
                    output.writeMessage(10205, (MsgBean.ChangeGroupAnnouncementMessage) realMsg_);
                }
                if (realMsgCase_ == 10206) {
                    output.writeMessage(10206, (MsgBean.DestroyGroupMessage) realMsg_);
                }
                if (realMsgCase_ == 10210) {
                    output.writeMessage(10210, (MsgBean.OutGroupMessage) realMsg_);
                }
                if (realMsgCase_ == 10300) {
                    output.writeMessage(10300, (MsgBean.ConflictMessage) realMsg_);
                }
            }

            public int getSerializedSize() {
                int size = memoizedSize;
                if (size != -1) return size;

                size = 0;
                if (timestamp_ != 0L) {
                    size += com.google.protobuf.CodedOutputStream
                            .computeUInt64Size(1, timestamp_);
                }
                if (msgType_ != MsgBean.MessageType.CHAT.getNumber()) {
                    size += com.google.protobuf.CodedOutputStream
                            .computeEnumSize(2, msgType_);
                }
                if (!getMsgIdBytes().isEmpty()) {
                    size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, msgId_);
                }
                if (fromUid_ != 0L) {
                    size += com.google.protobuf.CodedOutputStream
                            .computeUInt64Size(4, fromUid_);
                }
                if (!getGidBytes().isEmpty()) {
                    size += com.google.protobuf.GeneratedMessageV3.computeStringSize(5, gid_);
                }
                if (!getNicknameBytes().isEmpty()) {
                    size += com.google.protobuf.GeneratedMessageV3.computeStringSize(6, nickname_);
                }
                if (!getAvatarBytes().isEmpty()) {
                    size += com.google.protobuf.GeneratedMessageV3.computeStringSize(7, avatar_);
                }
                if (realMsgCase_ == 10000) {
                    size += com.google.protobuf.CodedOutputStream
                            .computeMessageSize(10000, (MsgBean.ChatMessage) realMsg_);
                }
                if (realMsgCase_ == 10001) {
                    size += com.google.protobuf.CodedOutputStream
                            .computeMessageSize(10001, (MsgBean.ImageMessage) realMsg_);
                }
                if (realMsgCase_ == 10002) {
                    size += com.google.protobuf.CodedOutputStream
                            .computeMessageSize(10002, (MsgBean.RedEnvelopeMessage) realMsg_);
                }
                if (realMsgCase_ == 10003) {
                    size += com.google.protobuf.CodedOutputStream
                            .computeMessageSize(10003, (MsgBean.ReceiveRedEnvelopeMessage) realMsg_);
                }
                if (realMsgCase_ == 10004) {
                    size += com.google.protobuf.CodedOutputStream
                            .computeMessageSize(10004, (MsgBean.TransferMessage) realMsg_);
                }
                if (realMsgCase_ == 10005) {
                    size += com.google.protobuf.CodedOutputStream
                            .computeMessageSize(10005, (MsgBean.StampMessage) realMsg_);
                }
                if (realMsgCase_ == 10006) {
                    size += com.google.protobuf.CodedOutputStream
                            .computeMessageSize(10006, (MsgBean.BusinessCardMessage) realMsg_);
                }
                if (realMsgCase_ == 10100) {
                    size += com.google.protobuf.CodedOutputStream
                            .computeMessageSize(10100, (MsgBean.RequestFriendMessage) realMsg_);
                }
                if (realMsgCase_ == 10101) {
                    size += com.google.protobuf.CodedOutputStream
                            .computeMessageSize(10101, (MsgBean.AcceptBeFriendsMessage) realMsg_);
                }
                if (realMsgCase_ == 10102) {
                    size += com.google.protobuf.CodedOutputStream
                            .computeMessageSize(10102, (MsgBean.RemoveFriendMessage) realMsg_);
                }
                if (realMsgCase_ == 10200) {
                    size += com.google.protobuf.CodedOutputStream
                            .computeMessageSize(10200, (MsgBean.RequestGroupMessage) realMsg_);
                }
                if (realMsgCase_ == 10201) {
                    size += com.google.protobuf.CodedOutputStream
                            .computeMessageSize(10201, (MsgBean.AcceptBeGroupMessage) realMsg_);
                }
                if (realMsgCase_ == 10202) {
                    size += com.google.protobuf.CodedOutputStream
                            .computeMessageSize(10202, (MsgBean.RemoveGroupMemberMessage) realMsg_);
                }
                if (realMsgCase_ == 10203) {
                    size += com.google.protobuf.CodedOutputStream
                            .computeMessageSize(10203, (MsgBean.ChangeGroupMasterMessage) realMsg_);
                }
                if (realMsgCase_ == 10204) {
                    size += com.google.protobuf.CodedOutputStream
                            .computeMessageSize(10204, (MsgBean.ChangeGroupNameMessage) realMsg_);
                }
                if (realMsgCase_ == 10205) {
                    size += com.google.protobuf.CodedOutputStream
                            .computeMessageSize(10205, (MsgBean.ChangeGroupAnnouncementMessage) realMsg_);
                }
                if (realMsgCase_ == 10206) {
                    size += com.google.protobuf.CodedOutputStream
                            .computeMessageSize(10206, (MsgBean.DestroyGroupMessage) realMsg_);
                }
                if (realMsgCase_ == 10210) {
                    size += com.google.protobuf.CodedOutputStream
                            .computeMessageSize(10210, (MsgBean.OutGroupMessage) realMsg_);
                }
                if (realMsgCase_ == 10300) {
                    size += com.google.protobuf.CodedOutputStream
                            .computeMessageSize(10300, (MsgBean.ConflictMessage) realMsg_);
                }
                memoizedSize = size;
                return size;
            }

            private static final long serialVersionUID = 0L;
            @java.lang.Override
            public boolean equals(final java.lang.Object obj) {
                if (obj == this) {
                    return true;
                }
                if (!(obj instanceof MsgBean.UniversalMessage.WrapMessage)) {
                    return super.equals(obj);
                }
                MsgBean.UniversalMessage.WrapMessage other = (MsgBean.UniversalMessage.WrapMessage) obj;

                boolean result = true;
                result = result && (getTimestamp()
                        == other.getTimestamp());
                result = result && msgType_ == other.msgType_;
                result = result && getMsgId()
                        .equals(other.getMsgId());
                result = result && (getFromUid()
                        == other.getFromUid());
                result = result && getGid()
                        .equals(other.getGid());
                result = result && getNickname()
                        .equals(other.getNickname());
                result = result && getAvatar()
                        .equals(other.getAvatar());
                result = result && getRealMsgCase().equals(
                        other.getRealMsgCase());
                if (!result) return false;
                switch (realMsgCase_) {
                    case 10000:
                        result = result && getChat()
                                .equals(other.getChat());
                        break;
                    case 10001:
                        result = result && getImage()
                                .equals(other.getImage());
                        break;
                    case 10002:
                        result = result && getRedEnvelope()
                                .equals(other.getRedEnvelope());
                        break;
                    case 10003:
                        result = result && getReceiveRedEnvelope()
                                .equals(other.getReceiveRedEnvelope());
                        break;
                    case 10004:
                        result = result && getTransfer()
                                .equals(other.getTransfer());
                        break;
                    case 10005:
                        result = result && getStamp()
                                .equals(other.getStamp());
                        break;
                    case 10006:
                        result = result && getBusinessCard()
                                .equals(other.getBusinessCard());
                        break;
                    case 10100:
                        result = result && getRequestFriend()
                                .equals(other.getRequestFriend());
                        break;
                    case 10101:
                        result = result && getAcceptBeFriends()
                                .equals(other.getAcceptBeFriends());
                        break;
                    case 10102:
                        result = result && getRemoveFriend()
                                .equals(other.getRemoveFriend());
                        break;
                    case 10200:
                        result = result && getRequestGroup()
                                .equals(other.getRequestGroup());
                        break;
                    case 10201:
                        result = result && getAcceptBeGroup()
                                .equals(other.getAcceptBeGroup());
                        break;
                    case 10202:
                        result = result && getRemoveGroupMember()
                                .equals(other.getRemoveGroupMember());
                        break;
                    case 10203:
                        result = result && getChangeGroupMaster()
                                .equals(other.getChangeGroupMaster());
                        break;
                    case 10204:
                        result = result && getChangeGroupName()
                                .equals(other.getChangeGroupName());
                        break;
                    case 10205:
                        result = result && getChangeGroupAnnouncement()
                                .equals(other.getChangeGroupAnnouncement());
                        break;
                    case 10206:
                        result = result && getDestroyGroup()
                                .equals(other.getDestroyGroup());
                        break;
                    case 10210:
                        result = result && getOutGroup()
                                .equals(other.getOutGroup());
                        break;
                    case 10300:
                        result = result && getConflict()
                                .equals(other.getConflict());
                        break;
                    case 0:
                    default:
                }
                return result;
            }

            @java.lang.Override
            public int hashCode() {
                if (memoizedHashCode != 0) {
                    return memoizedHashCode;
                }
                int hash = 41;
                hash = (19 * hash) + getDescriptor().hashCode();
                hash = (37 * hash) + TIMESTAMP_FIELD_NUMBER;
                hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
                        getTimestamp());
                hash = (37 * hash) + MSG_TYPE_FIELD_NUMBER;
                hash = (53 * hash) + msgType_;
                hash = (37 * hash) + MSG_ID_FIELD_NUMBER;
                hash = (53 * hash) + getMsgId().hashCode();
                hash = (37 * hash) + FROM_UID_FIELD_NUMBER;
                hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
                        getFromUid());
                hash = (37 * hash) + GID_FIELD_NUMBER;
                hash = (53 * hash) + getGid().hashCode();
                hash = (37 * hash) + NICKNAME_FIELD_NUMBER;
                hash = (53 * hash) + getNickname().hashCode();
                hash = (37 * hash) + AVATAR_FIELD_NUMBER;
                hash = (53 * hash) + getAvatar().hashCode();
                switch (realMsgCase_) {
                    case 10000:
                        hash = (37 * hash) + CHAT_FIELD_NUMBER;
                        hash = (53 * hash) + getChat().hashCode();
                        break;
                    case 10001:
                        hash = (37 * hash) + IMAGE_FIELD_NUMBER;
                        hash = (53 * hash) + getImage().hashCode();
                        break;
                    case 10002:
                        hash = (37 * hash) + RED_ENVELOPE_FIELD_NUMBER;
                        hash = (53 * hash) + getRedEnvelope().hashCode();
                        break;
                    case 10003:
                        hash = (37 * hash) + RECEIVE_RED_ENVELOPE_FIELD_NUMBER;
                        hash = (53 * hash) + getReceiveRedEnvelope().hashCode();
                        break;
                    case 10004:
                        hash = (37 * hash) + TRANSFER_FIELD_NUMBER;
                        hash = (53 * hash) + getTransfer().hashCode();
                        break;
                    case 10005:
                        hash = (37 * hash) + STAMP_FIELD_NUMBER;
                        hash = (53 * hash) + getStamp().hashCode();
                        break;
                    case 10006:
                        hash = (37 * hash) + BUSINESS_CARD_FIELD_NUMBER;
                        hash = (53 * hash) + getBusinessCard().hashCode();
                        break;
                    case 10100:
                        hash = (37 * hash) + REQUEST_FRIEND_FIELD_NUMBER;
                        hash = (53 * hash) + getRequestFriend().hashCode();
                        break;
                    case 10101:
                        hash = (37 * hash) + ACCEPT_BE_FRIENDS_FIELD_NUMBER;
                        hash = (53 * hash) + getAcceptBeFriends().hashCode();
                        break;
                    case 10102:
                        hash = (37 * hash) + REMOVE_FRIEND_FIELD_NUMBER;
                        hash = (53 * hash) + getRemoveFriend().hashCode();
                        break;
                    case 10200:
                        hash = (37 * hash) + REQUEST_GROUP_FIELD_NUMBER;
                        hash = (53 * hash) + getRequestGroup().hashCode();
                        break;
                    case 10201:
                        hash = (37 * hash) + ACCEPT_BE_GROUP_FIELD_NUMBER;
                        hash = (53 * hash) + getAcceptBeGroup().hashCode();
                        break;
                    case 10202:
                        hash = (37 * hash) + REMOVE_GROUP_MEMBER_FIELD_NUMBER;
                        hash = (53 * hash) + getRemoveGroupMember().hashCode();
                        break;
                    case 10203:
                        hash = (37 * hash) + CHANGE_GROUP_MASTER_FIELD_NUMBER;
                        hash = (53 * hash) + getChangeGroupMaster().hashCode();
                        break;
                    case 10204:
                        hash = (37 * hash) + CHANGE_GROUP_NAME_FIELD_NUMBER;
                        hash = (53 * hash) + getChangeGroupName().hashCode();
                        break;
                    case 10205:
                        hash = (37 * hash) + CHANGE_GROUP_ANNOUNCEMENT_FIELD_NUMBER;
                        hash = (53 * hash) + getChangeGroupAnnouncement().hashCode();
                        break;
                    case 10206:
                        hash = (37 * hash) + DESTROY_GROUP_FIELD_NUMBER;
                        hash = (53 * hash) + getDestroyGroup().hashCode();
                        break;
                    case 10210:
                        hash = (37 * hash) + OUT_GROUP_FIELD_NUMBER;
                        hash = (53 * hash) + getOutGroup().hashCode();
                        break;
                    case 10300:
                        hash = (37 * hash) + CONFLICT_FIELD_NUMBER;
                        hash = (53 * hash) + getConflict().hashCode();
                        break;
                    case 0:
                    default:
                }
                hash = (29 * hash) + unknownFields.hashCode();
                memoizedHashCode = hash;
                return hash;
            }

            public static MsgBean.UniversalMessage.WrapMessage parseFrom(
                    java.nio.ByteBuffer data)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return PARSER.parseFrom(data);
            }
            public static MsgBean.UniversalMessage.WrapMessage parseFrom(
                    java.nio.ByteBuffer data,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return PARSER.parseFrom(data, extensionRegistry);
            }
            public static MsgBean.UniversalMessage.WrapMessage parseFrom(
                    com.google.protobuf.ByteString data)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return PARSER.parseFrom(data);
            }
            public static MsgBean.UniversalMessage.WrapMessage parseFrom(
                    com.google.protobuf.ByteString data,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return PARSER.parseFrom(data, extensionRegistry);
            }
            public static MsgBean.UniversalMessage.WrapMessage parseFrom(byte[] data)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return PARSER.parseFrom(data);
            }
            public static MsgBean.UniversalMessage.WrapMessage parseFrom(
                    byte[] data,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return PARSER.parseFrom(data, extensionRegistry);
            }
            public static MsgBean.UniversalMessage.WrapMessage parseFrom(java.io.InputStream input)
                    throws java.io.IOException {
                return com.google.protobuf.GeneratedMessageV3
                        .parseWithIOException(PARSER, input);
            }
            public static MsgBean.UniversalMessage.WrapMessage parseFrom(
                    java.io.InputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                return com.google.protobuf.GeneratedMessageV3
                        .parseWithIOException(PARSER, input, extensionRegistry);
            }
            public static MsgBean.UniversalMessage.WrapMessage parseDelimitedFrom(java.io.InputStream input)
                    throws java.io.IOException {
                return com.google.protobuf.GeneratedMessageV3
                        .parseDelimitedWithIOException(PARSER, input);
            }
            public static MsgBean.UniversalMessage.WrapMessage parseDelimitedFrom(
                    java.io.InputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                return com.google.protobuf.GeneratedMessageV3
                        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
            }
            public static MsgBean.UniversalMessage.WrapMessage parseFrom(
                    com.google.protobuf.CodedInputStream input)
                    throws java.io.IOException {
                return com.google.protobuf.GeneratedMessageV3
                        .parseWithIOException(PARSER, input);
            }
            public static MsgBean.UniversalMessage.WrapMessage parseFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                return com.google.protobuf.GeneratedMessageV3
                        .parseWithIOException(PARSER, input, extensionRegistry);
            }

            public Builder newBuilderForType() { return newBuilder(); }
            public static Builder newBuilder() {
                return DEFAULT_INSTANCE.toBuilder();
            }
            public static Builder newBuilder(MsgBean.UniversalMessage.WrapMessage prototype) {
                return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
            }
            public Builder toBuilder() {
                return this == DEFAULT_INSTANCE
                        ? new Builder() : new Builder().mergeFrom(this);
            }

            @java.lang.Override
            protected Builder newBuilderForType(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                Builder builder = new Builder(parent);
                return builder;
            }
            /**
             * Protobuf type {@code UniversalMessage.WrapMessage}
             */
            public static final class Builder extends
                    com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                    // @@protoc_insertion_point(builder_implements:UniversalMessage.WrapMessage)
                    MsgBean.UniversalMessage.WrapMessageOrBuilder {
                public static final com.google.protobuf.Descriptors.Descriptor
                getDescriptor() {
                    return MsgBean.internal_static_UniversalMessage_WrapMessage_descriptor;
                }

                protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
                internalGetFieldAccessorTable() {
                    return MsgBean.internal_static_UniversalMessage_WrapMessage_fieldAccessorTable
                            .ensureFieldAccessorsInitialized(
                                    MsgBean.UniversalMessage.WrapMessage.class, MsgBean.UniversalMessage.WrapMessage.Builder.class);
                }

                // Construct using MsgBean.UniversalMessage.WrapMessage.newBuilder()
                private Builder() {
                    maybeForceBuilderInitialization();
                }

                private Builder(
                        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                    super(parent);
                    maybeForceBuilderInitialization();
                }
                private void maybeForceBuilderInitialization() {
                    if (com.google.protobuf.GeneratedMessageV3
                            .alwaysUseFieldBuilders) {
                    }
                }
                public Builder clear() {
                    super.clear();
                    timestamp_ = 0L;

                    msgType_ = 0;

                    msgId_ = "";

                    fromUid_ = 0L;

                    gid_ = "";

                    nickname_ = "";

                    avatar_ = "";

                    realMsgCase_ = 0;
                    realMsg_ = null;
                    return this;
                }

                public com.google.protobuf.Descriptors.Descriptor
                getDescriptorForType() {
                    return MsgBean.internal_static_UniversalMessage_WrapMessage_descriptor;
                }

                public MsgBean.UniversalMessage.WrapMessage getDefaultInstanceForType() {
                    return MsgBean.UniversalMessage.WrapMessage.getDefaultInstance();
                }

                public MsgBean.UniversalMessage.WrapMessage build() {
                    MsgBean.UniversalMessage.WrapMessage result = buildPartial();
                    if (!result.isInitialized()) {
                        throw newUninitializedMessageException(result);
                    }
                    return result;
                }

                public MsgBean.UniversalMessage.WrapMessage buildPartial() {
                    MsgBean.UniversalMessage.WrapMessage result = new MsgBean.UniversalMessage.WrapMessage(this);
                    result.timestamp_ = timestamp_;
                    result.msgType_ = msgType_;
                    result.msgId_ = msgId_;
                    result.fromUid_ = fromUid_;
                    result.gid_ = gid_;
                    result.nickname_ = nickname_;
                    result.avatar_ = avatar_;
                    if (realMsgCase_ == 10000) {
                        if (chatBuilder_ == null) {
                            result.realMsg_ = realMsg_;
                        } else {
                            result.realMsg_ = chatBuilder_.build();
                        }
                    }
                    if (realMsgCase_ == 10001) {
                        if (imageBuilder_ == null) {
                            result.realMsg_ = realMsg_;
                        } else {
                            result.realMsg_ = imageBuilder_.build();
                        }
                    }
                    if (realMsgCase_ == 10002) {
                        if (redEnvelopeBuilder_ == null) {
                            result.realMsg_ = realMsg_;
                        } else {
                            result.realMsg_ = redEnvelopeBuilder_.build();
                        }
                    }
                    if (realMsgCase_ == 10003) {
                        if (receiveRedEnvelopeBuilder_ == null) {
                            result.realMsg_ = realMsg_;
                        } else {
                            result.realMsg_ = receiveRedEnvelopeBuilder_.build();
                        }
                    }
                    if (realMsgCase_ == 10004) {
                        if (transferBuilder_ == null) {
                            result.realMsg_ = realMsg_;
                        } else {
                            result.realMsg_ = transferBuilder_.build();
                        }
                    }
                    if (realMsgCase_ == 10005) {
                        if (stampBuilder_ == null) {
                            result.realMsg_ = realMsg_;
                        } else {
                            result.realMsg_ = stampBuilder_.build();
                        }
                    }
                    if (realMsgCase_ == 10006) {
                        if (businessCardBuilder_ == null) {
                            result.realMsg_ = realMsg_;
                        } else {
                            result.realMsg_ = businessCardBuilder_.build();
                        }
                    }
                    if (realMsgCase_ == 10100) {
                        if (requestFriendBuilder_ == null) {
                            result.realMsg_ = realMsg_;
                        } else {
                            result.realMsg_ = requestFriendBuilder_.build();
                        }
                    }
                    if (realMsgCase_ == 10101) {
                        if (acceptBeFriendsBuilder_ == null) {
                            result.realMsg_ = realMsg_;
                        } else {
                            result.realMsg_ = acceptBeFriendsBuilder_.build();
                        }
                    }
                    if (realMsgCase_ == 10102) {
                        if (removeFriendBuilder_ == null) {
                            result.realMsg_ = realMsg_;
                        } else {
                            result.realMsg_ = removeFriendBuilder_.build();
                        }
                    }
                    if (realMsgCase_ == 10200) {
                        if (requestGroupBuilder_ == null) {
                            result.realMsg_ = realMsg_;
                        } else {
                            result.realMsg_ = requestGroupBuilder_.build();
                        }
                    }
                    if (realMsgCase_ == 10201) {
                        if (acceptBeGroupBuilder_ == null) {
                            result.realMsg_ = realMsg_;
                        } else {
                            result.realMsg_ = acceptBeGroupBuilder_.build();
                        }
                    }
                    if (realMsgCase_ == 10202) {
                        if (removeGroupMemberBuilder_ == null) {
                            result.realMsg_ = realMsg_;
                        } else {
                            result.realMsg_ = removeGroupMemberBuilder_.build();
                        }
                    }
                    if (realMsgCase_ == 10203) {
                        if (changeGroupMasterBuilder_ == null) {
                            result.realMsg_ = realMsg_;
                        } else {
                            result.realMsg_ = changeGroupMasterBuilder_.build();
                        }
                    }
                    if (realMsgCase_ == 10204) {
                        if (changeGroupNameBuilder_ == null) {
                            result.realMsg_ = realMsg_;
                        } else {
                            result.realMsg_ = changeGroupNameBuilder_.build();
                        }
                    }
                    if (realMsgCase_ == 10205) {
                        if (changeGroupAnnouncementBuilder_ == null) {
                            result.realMsg_ = realMsg_;
                        } else {
                            result.realMsg_ = changeGroupAnnouncementBuilder_.build();
                        }
                    }
                    if (realMsgCase_ == 10206) {
                        if (destroyGroupBuilder_ == null) {
                            result.realMsg_ = realMsg_;
                        } else {
                            result.realMsg_ = destroyGroupBuilder_.build();
                        }
                    }
                    if (realMsgCase_ == 10210) {
                        if (outGroupBuilder_ == null) {
                            result.realMsg_ = realMsg_;
                        } else {
                            result.realMsg_ = outGroupBuilder_.build();
                        }
                    }
                    if (realMsgCase_ == 10300) {
                        if (conflictBuilder_ == null) {
                            result.realMsg_ = realMsg_;
                        } else {
                            result.realMsg_ = conflictBuilder_.build();
                        }
                    }
                    result.realMsgCase_ = realMsgCase_;
                    onBuilt();
                    return result;
                }

                public Builder clone() {
                    return (Builder) super.clone();
                }
                public Builder setField(
                        com.google.protobuf.Descriptors.FieldDescriptor field,
                        Object value) {
                    return (Builder) super.setField(field, value);
                }
                public Builder clearField(
                        com.google.protobuf.Descriptors.FieldDescriptor field) {
                    return (Builder) super.clearField(field);
                }
                public Builder clearOneof(
                        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                    return (Builder) super.clearOneof(oneof);
                }
                public Builder setRepeatedField(
                        com.google.protobuf.Descriptors.FieldDescriptor field,
                        int index, Object value) {
                    return (Builder) super.setRepeatedField(field, index, value);
                }
                public Builder addRepeatedField(
                        com.google.protobuf.Descriptors.FieldDescriptor field,
                        Object value) {
                    return (Builder) super.addRepeatedField(field, value);
                }
                public Builder mergeFrom(com.google.protobuf.Message other) {
                    if (other instanceof MsgBean.UniversalMessage.WrapMessage) {
                        return mergeFrom((MsgBean.UniversalMessage.WrapMessage)other);
                    } else {
                        super.mergeFrom(other);
                        return this;
                    }
                }

                public Builder mergeFrom(MsgBean.UniversalMessage.WrapMessage other) {
                    if (other == MsgBean.UniversalMessage.WrapMessage.getDefaultInstance()) return this;
                    if (other.getTimestamp() != 0L) {
                        setTimestamp(other.getTimestamp());
                    }
                    if (other.msgType_ != 0) {
                        setMsgTypeValue(other.getMsgTypeValue());
                    }
                    if (!other.getMsgId().isEmpty()) {
                        msgId_ = other.msgId_;
                        onChanged();
                    }
                    if (other.getFromUid() != 0L) {
                        setFromUid(other.getFromUid());
                    }
                    if (!other.getGid().isEmpty()) {
                        gid_ = other.gid_;
                        onChanged();
                    }
                    if (!other.getNickname().isEmpty()) {
                        nickname_ = other.nickname_;
                        onChanged();
                    }
                    if (!other.getAvatar().isEmpty()) {
                        avatar_ = other.avatar_;
                        onChanged();
                    }
                    switch (other.getRealMsgCase()) {
                        case CHAT: {
                            mergeChat(other.getChat());
                            break;
                        }
                        case IMAGE: {
                            mergeImage(other.getImage());
                            break;
                        }
                        case RED_ENVELOPE: {
                            mergeRedEnvelope(other.getRedEnvelope());
                            break;
                        }
                        case RECEIVE_RED_ENVELOPE: {
                            mergeReceiveRedEnvelope(other.getReceiveRedEnvelope());
                            break;
                        }
                        case TRANSFER: {
                            mergeTransfer(other.getTransfer());
                            break;
                        }
                        case STAMP: {
                            mergeStamp(other.getStamp());
                            break;
                        }
                        case BUSINESS_CARD: {
                            mergeBusinessCard(other.getBusinessCard());
                            break;
                        }
                        case REQUEST_FRIEND: {
                            mergeRequestFriend(other.getRequestFriend());
                            break;
                        }
                        case ACCEPT_BE_FRIENDS: {
                            mergeAcceptBeFriends(other.getAcceptBeFriends());
                            break;
                        }
                        case REMOVE_FRIEND: {
                            mergeRemoveFriend(other.getRemoveFriend());
                            break;
                        }
                        case REQUEST_GROUP: {
                            mergeRequestGroup(other.getRequestGroup());
                            break;
                        }
                        case ACCEPT_BE_GROUP: {
                            mergeAcceptBeGroup(other.getAcceptBeGroup());
                            break;
                        }
                        case REMOVE_GROUP_MEMBER: {
                            mergeRemoveGroupMember(other.getRemoveGroupMember());
                            break;
                        }
                        case CHANGE_GROUP_MASTER: {
                            mergeChangeGroupMaster(other.getChangeGroupMaster());
                            break;
                        }
                        case CHANGE_GROUP_NAME: {
                            mergeChangeGroupName(other.getChangeGroupName());
                            break;
                        }
                        case CHANGE_GROUP_ANNOUNCEMENT: {
                            mergeChangeGroupAnnouncement(other.getChangeGroupAnnouncement());
                            break;
                        }
                        case DESTROY_GROUP: {
                            mergeDestroyGroup(other.getDestroyGroup());
                            break;
                        }
                        case OUT_GROUP: {
                            mergeOutGroup(other.getOutGroup());
                            break;
                        }
                        case CONFLICT: {
                            mergeConflict(other.getConflict());
                            break;
                        }
                        case REALMSG_NOT_SET: {
                            break;
                        }
                    }
                    onChanged();
                    return this;
                }

                public final boolean isInitialized() {
                    return true;
                }

                public Builder mergeFrom(
                        com.google.protobuf.CodedInputStream input,
                        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                        throws java.io.IOException {
                    MsgBean.UniversalMessage.WrapMessage parsedMessage = null;
                    try {
                        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                        parsedMessage = (MsgBean.UniversalMessage.WrapMessage) e.getUnfinishedMessage();
                        throw e.unwrapIOException();
                    } finally {
                        if (parsedMessage != null) {
                            mergeFrom(parsedMessage);
                        }
                    }
                    return this;
                }
                private int realMsgCase_ = 0;
                private java.lang.Object realMsg_;
                public RealMsgCase
                getRealMsgCase() {
                    return RealMsgCase.forNumber(
                            realMsgCase_);
                }

                public Builder clearRealMsg() {
                    realMsgCase_ = 0;
                    realMsg_ = null;
                    onChanged();
                    return this;
                }


                private long timestamp_ ;
                /**
                 * <pre>
                 * 时间戳
                 * </pre>
                 *
                 * <code>uint64 timestamp = 1;</code>
                 */
                public long getTimestamp() {
                    return timestamp_;
                }
                /**
                 * <pre>
                 * 时间戳
                 * </pre>
                 *
                 * <code>uint64 timestamp = 1;</code>
                 */
                public Builder setTimestamp(long value) {

                    timestamp_ = value;
                    onChanged();
                    return this;
                }
                /**
                 * <pre>
                 * 时间戳
                 * </pre>
                 *
                 * <code>uint64 timestamp = 1;</code>
                 */
                public Builder clearTimestamp() {

                    timestamp_ = 0L;
                    onChanged();
                    return this;
                }

                private int msgType_ = 0;
                /**
                 * <pre>
                 * 消息类型
                 * </pre>
                 *
                 * <code>.MessageType msg_type = 2;</code>
                 */
                public int getMsgTypeValue() {
                    return msgType_;
                }
                /**
                 * <pre>
                 * 消息类型
                 * </pre>
                 *
                 * <code>.MessageType msg_type = 2;</code>
                 */
                public Builder setMsgTypeValue(int value) {
                    msgType_ = value;
                    onChanged();
                    return this;
                }
                /**
                 * <pre>
                 * 消息类型
                 * </pre>
                 *
                 * <code>.MessageType msg_type = 2;</code>
                 */
                public MsgBean.MessageType getMsgType() {
                    MsgBean.MessageType result = MsgBean.MessageType.valueOf(msgType_);
                    return result == null ? MsgBean.MessageType.UNRECOGNIZED : result;
                }
                /**
                 * <pre>
                 * 消息类型
                 * </pre>
                 *
                 * <code>.MessageType msg_type = 2;</code>
                 */
                public Builder setMsgType(MsgBean.MessageType value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }

                    msgType_ = value.getNumber();
                    onChanged();
                    return this;
                }
                /**
                 * <pre>
                 * 消息类型
                 * </pre>
                 *
                 * <code>.MessageType msg_type = 2;</code>
                 */
                public Builder clearMsgType() {

                    msgType_ = 0;
                    onChanged();
                    return this;
                }

                private java.lang.Object msgId_ = "";
                /**
                 * <pre>
                 * 消息id
                 * </pre>
                 *
                 * <code>string msg_id = 3;</code>
                 */
                public java.lang.String getMsgId() {
                    java.lang.Object ref = msgId_;
                    if (!(ref instanceof java.lang.String)) {
                        com.google.protobuf.ByteString bs =
                                (com.google.protobuf.ByteString) ref;
                        java.lang.String s = bs.toStringUtf8();
                        msgId_ = s;
                        return s;
                    } else {
                        return (java.lang.String) ref;
                    }
                }
                /**
                 * <pre>
                 * 消息id
                 * </pre>
                 *
                 * <code>string msg_id = 3;</code>
                 */
                public com.google.protobuf.ByteString
                getMsgIdBytes() {
                    java.lang.Object ref = msgId_;
                    if (ref instanceof String) {
                        com.google.protobuf.ByteString b =
                                com.google.protobuf.ByteString.copyFromUtf8(
                                        (java.lang.String) ref);
                        msgId_ = b;
                        return b;
                    } else {
                        return (com.google.protobuf.ByteString) ref;
                    }
                }
                /**
                 * <pre>
                 * 消息id
                 * </pre>
                 *
                 * <code>string msg_id = 3;</code>
                 */
                public Builder setMsgId(
                        java.lang.String value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }

                    msgId_ = value;
                    onChanged();
                    return this;
                }
                /**
                 * <pre>
                 * 消息id
                 * </pre>
                 *
                 * <code>string msg_id = 3;</code>
                 */
                public Builder clearMsgId() {

                    msgId_ = getDefaultInstance().getMsgId();
                    onChanged();
                    return this;
                }
                /**
                 * <pre>
                 * 消息id
                 * </pre>
                 *
                 * <code>string msg_id = 3;</code>
                 */
                public Builder setMsgIdBytes(
                        com.google.protobuf.ByteString value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    checkByteStringIsUtf8(value);

                    msgId_ = value;
                    onChanged();
                    return this;
                }

                private long fromUid_ ;
                /**
                 * <pre>
                 * 消息来源
                 * </pre>
                 *
                 * <code>uint64 from_uid = 4;</code>
                 */
                public long getFromUid() {
                    return fromUid_;
                }
                /**
                 * <pre>
                 * 消息来源
                 * </pre>
                 *
                 * <code>uint64 from_uid = 4;</code>
                 */
                public Builder setFromUid(long value) {

                    fromUid_ = value;
                    onChanged();
                    return this;
                }
                /**
                 * <pre>
                 * 消息来源
                 * </pre>
                 *
                 * <code>uint64 from_uid = 4;</code>
                 */
                public Builder clearFromUid() {

                    fromUid_ = 0L;
                    onChanged();
                    return this;
                }

                private java.lang.Object gid_ = "";
                /**
                 * <code>string gid = 5;</code>
                 */
                public java.lang.String getGid() {
                    java.lang.Object ref = gid_;
                    if (!(ref instanceof java.lang.String)) {
                        com.google.protobuf.ByteString bs =
                                (com.google.protobuf.ByteString) ref;
                        java.lang.String s = bs.toStringUtf8();
                        gid_ = s;
                        return s;
                    } else {
                        return (java.lang.String) ref;
                    }
                }
                /**
                 * <code>string gid = 5;</code>
                 */
                public com.google.protobuf.ByteString
                getGidBytes() {
                    java.lang.Object ref = gid_;
                    if (ref instanceof String) {
                        com.google.protobuf.ByteString b =
                                com.google.protobuf.ByteString.copyFromUtf8(
                                        (java.lang.String) ref);
                        gid_ = b;
                        return b;
                    } else {
                        return (com.google.protobuf.ByteString) ref;
                    }
                }
                /**
                 * <code>string gid = 5;</code>
                 */
                public Builder setGid(
                        java.lang.String value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }

                    gid_ = value;
                    onChanged();
                    return this;
                }
                /**
                 * <code>string gid = 5;</code>
                 */
                public Builder clearGid() {

                    gid_ = getDefaultInstance().getGid();
                    onChanged();
                    return this;
                }
                /**
                 * <code>string gid = 5;</code>
                 */
                public Builder setGidBytes(
                        com.google.protobuf.ByteString value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    checkByteStringIsUtf8(value);

                    gid_ = value;
                    onChanged();
                    return this;
                }

                private java.lang.Object nickname_ = "";
                /**
                 * <pre>
                 *昵称
                 * </pre>
                 *
                 * <code>string nickname = 6;</code>
                 */
                public java.lang.String getNickname() {
                    java.lang.Object ref = nickname_;
                    if (!(ref instanceof java.lang.String)) {
                        com.google.protobuf.ByteString bs =
                                (com.google.protobuf.ByteString) ref;
                        java.lang.String s = bs.toStringUtf8();
                        nickname_ = s;
                        return s;
                    } else {
                        return (java.lang.String) ref;
                    }
                }
                /**
                 * <pre>
                 *昵称
                 * </pre>
                 *
                 * <code>string nickname = 6;</code>
                 */
                public com.google.protobuf.ByteString
                getNicknameBytes() {
                    java.lang.Object ref = nickname_;
                    if (ref instanceof String) {
                        com.google.protobuf.ByteString b =
                                com.google.protobuf.ByteString.copyFromUtf8(
                                        (java.lang.String) ref);
                        nickname_ = b;
                        return b;
                    } else {
                        return (com.google.protobuf.ByteString) ref;
                    }
                }
                /**
                 * <pre>
                 *昵称
                 * </pre>
                 *
                 * <code>string nickname = 6;</code>
                 */
                public Builder setNickname(
                        java.lang.String value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }

                    nickname_ = value;
                    onChanged();
                    return this;
                }
                /**
                 * <pre>
                 *昵称
                 * </pre>
                 *
                 * <code>string nickname = 6;</code>
                 */
                public Builder clearNickname() {

                    nickname_ = getDefaultInstance().getNickname();
                    onChanged();
                    return this;
                }
                /**
                 * <pre>
                 *昵称
                 * </pre>
                 *
                 * <code>string nickname = 6;</code>
                 */
                public Builder setNicknameBytes(
                        com.google.protobuf.ByteString value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    checkByteStringIsUtf8(value);

                    nickname_ = value;
                    onChanged();
                    return this;
                }

                private java.lang.Object avatar_ = "";
                /**
                 * <pre>
                 *头像
                 * </pre>
                 *
                 * <code>string avatar = 7;</code>
                 */
                public java.lang.String getAvatar() {
                    java.lang.Object ref = avatar_;
                    if (!(ref instanceof java.lang.String)) {
                        com.google.protobuf.ByteString bs =
                                (com.google.protobuf.ByteString) ref;
                        java.lang.String s = bs.toStringUtf8();
                        avatar_ = s;
                        return s;
                    } else {
                        return (java.lang.String) ref;
                    }
                }
                /**
                 * <pre>
                 *头像
                 * </pre>
                 *
                 * <code>string avatar = 7;</code>
                 */
                public com.google.protobuf.ByteString
                getAvatarBytes() {
                    java.lang.Object ref = avatar_;
                    if (ref instanceof String) {
                        com.google.protobuf.ByteString b =
                                com.google.protobuf.ByteString.copyFromUtf8(
                                        (java.lang.String) ref);
                        avatar_ = b;
                        return b;
                    } else {
                        return (com.google.protobuf.ByteString) ref;
                    }
                }
                /**
                 * <pre>
                 *头像
                 * </pre>
                 *
                 * <code>string avatar = 7;</code>
                 */
                public Builder setAvatar(
                        java.lang.String value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }

                    avatar_ = value;
                    onChanged();
                    return this;
                }
                /**
                 * <pre>
                 *头像
                 * </pre>
                 *
                 * <code>string avatar = 7;</code>
                 */
                public Builder clearAvatar() {

                    avatar_ = getDefaultInstance().getAvatar();
                    onChanged();
                    return this;
                }
                /**
                 * <pre>
                 *头像
                 * </pre>
                 *
                 * <code>string avatar = 7;</code>
                 */
                public Builder setAvatarBytes(
                        com.google.protobuf.ByteString value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    checkByteStringIsUtf8(value);

                    avatar_ = value;
                    onChanged();
                    return this;
                }

                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.ChatMessage, MsgBean.ChatMessage.Builder, MsgBean.ChatMessageOrBuilder> chatBuilder_;
                /**
                 * <code>.ChatMessage chat = 10000;</code>
                 */
                public MsgBean.ChatMessage getChat() {
                    if (chatBuilder_ == null) {
                        if (realMsgCase_ == 10000) {
                            return (MsgBean.ChatMessage) realMsg_;
                        }
                        return MsgBean.ChatMessage.getDefaultInstance();
                    } else {
                        if (realMsgCase_ == 10000) {
                            return chatBuilder_.getMessage();
                        }
                        return MsgBean.ChatMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.ChatMessage chat = 10000;</code>
                 */
                public Builder setChat(MsgBean.ChatMessage value) {
                    if (chatBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        realMsg_ = value;
                        onChanged();
                    } else {
                        chatBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10000;
                    return this;
                }
                /**
                 * <code>.ChatMessage chat = 10000;</code>
                 */
                public Builder setChat(
                        MsgBean.ChatMessage.Builder builderForValue) {
                    if (chatBuilder_ == null) {
                        realMsg_ = builderForValue.build();
                        onChanged();
                    } else {
                        chatBuilder_.setMessage(builderForValue.build());
                    }
                    realMsgCase_ = 10000;
                    return this;
                }
                /**
                 * <code>.ChatMessage chat = 10000;</code>
                 */
                public Builder mergeChat(MsgBean.ChatMessage value) {
                    if (chatBuilder_ == null) {
                        if (realMsgCase_ == 10000 &&
                                realMsg_ != MsgBean.ChatMessage.getDefaultInstance()) {
                            realMsg_ = MsgBean.ChatMessage.newBuilder((MsgBean.ChatMessage) realMsg_)
                                    .mergeFrom(value).buildPartial();
                        } else {
                            realMsg_ = value;
                        }
                        onChanged();
                    } else {
                        if (realMsgCase_ == 10000) {
                            chatBuilder_.mergeFrom(value);
                        }
                        chatBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10000;
                    return this;
                }
                /**
                 * <code>.ChatMessage chat = 10000;</code>
                 */
                public Builder clearChat() {
                    if (chatBuilder_ == null) {
                        if (realMsgCase_ == 10000) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                            onChanged();
                        }
                    } else {
                        if (realMsgCase_ == 10000) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                        }
                        chatBuilder_.clear();
                    }
                    return this;
                }
                /**
                 * <code>.ChatMessage chat = 10000;</code>
                 */
                public MsgBean.ChatMessage.Builder getChatBuilder() {
                    return getChatFieldBuilder().getBuilder();
                }
                /**
                 * <code>.ChatMessage chat = 10000;</code>
                 */
                public MsgBean.ChatMessageOrBuilder getChatOrBuilder() {
                    if ((realMsgCase_ == 10000) && (chatBuilder_ != null)) {
                        return chatBuilder_.getMessageOrBuilder();
                    } else {
                        if (realMsgCase_ == 10000) {
                            return (MsgBean.ChatMessage) realMsg_;
                        }
                        return MsgBean.ChatMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.ChatMessage chat = 10000;</code>
                 */
                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.ChatMessage, MsgBean.ChatMessage.Builder, MsgBean.ChatMessageOrBuilder>
                getChatFieldBuilder() {
                    if (chatBuilder_ == null) {
                        if (!(realMsgCase_ == 10000)) {
                            realMsg_ = MsgBean.ChatMessage.getDefaultInstance();
                        }
                        chatBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
                                MsgBean.ChatMessage, MsgBean.ChatMessage.Builder, MsgBean.ChatMessageOrBuilder>(
                                (MsgBean.ChatMessage) realMsg_,
                                getParentForChildren(),
                                isClean());
                        realMsg_ = null;
                    }
                    realMsgCase_ = 10000;
                    onChanged();;
                    return chatBuilder_;
                }

                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.ImageMessage, MsgBean.ImageMessage.Builder, MsgBean.ImageMessageOrBuilder> imageBuilder_;
                /**
                 * <code>.ImageMessage image = 10001;</code>
                 */
                public MsgBean.ImageMessage getImage() {
                    if (imageBuilder_ == null) {
                        if (realMsgCase_ == 10001) {
                            return (MsgBean.ImageMessage) realMsg_;
                        }
                        return MsgBean.ImageMessage.getDefaultInstance();
                    } else {
                        if (realMsgCase_ == 10001) {
                            return imageBuilder_.getMessage();
                        }
                        return MsgBean.ImageMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.ImageMessage image = 10001;</code>
                 */
                public Builder setImage(MsgBean.ImageMessage value) {
                    if (imageBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        realMsg_ = value;
                        onChanged();
                    } else {
                        imageBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10001;
                    return this;
                }
                /**
                 * <code>.ImageMessage image = 10001;</code>
                 */
                public Builder setImage(
                        MsgBean.ImageMessage.Builder builderForValue) {
                    if (imageBuilder_ == null) {
                        realMsg_ = builderForValue.build();
                        onChanged();
                    } else {
                        imageBuilder_.setMessage(builderForValue.build());
                    }
                    realMsgCase_ = 10001;
                    return this;
                }
                /**
                 * <code>.ImageMessage image = 10001;</code>
                 */
                public Builder mergeImage(MsgBean.ImageMessage value) {
                    if (imageBuilder_ == null) {
                        if (realMsgCase_ == 10001 &&
                                realMsg_ != MsgBean.ImageMessage.getDefaultInstance()) {
                            realMsg_ = MsgBean.ImageMessage.newBuilder((MsgBean.ImageMessage) realMsg_)
                                    .mergeFrom(value).buildPartial();
                        } else {
                            realMsg_ = value;
                        }
                        onChanged();
                    } else {
                        if (realMsgCase_ == 10001) {
                            imageBuilder_.mergeFrom(value);
                        }
                        imageBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10001;
                    return this;
                }
                /**
                 * <code>.ImageMessage image = 10001;</code>
                 */
                public Builder clearImage() {
                    if (imageBuilder_ == null) {
                        if (realMsgCase_ == 10001) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                            onChanged();
                        }
                    } else {
                        if (realMsgCase_ == 10001) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                        }
                        imageBuilder_.clear();
                    }
                    return this;
                }
                /**
                 * <code>.ImageMessage image = 10001;</code>
                 */
                public MsgBean.ImageMessage.Builder getImageBuilder() {
                    return getImageFieldBuilder().getBuilder();
                }
                /**
                 * <code>.ImageMessage image = 10001;</code>
                 */
                public MsgBean.ImageMessageOrBuilder getImageOrBuilder() {
                    if ((realMsgCase_ == 10001) && (imageBuilder_ != null)) {
                        return imageBuilder_.getMessageOrBuilder();
                    } else {
                        if (realMsgCase_ == 10001) {
                            return (MsgBean.ImageMessage) realMsg_;
                        }
                        return MsgBean.ImageMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.ImageMessage image = 10001;</code>
                 */
                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.ImageMessage, MsgBean.ImageMessage.Builder, MsgBean.ImageMessageOrBuilder>
                getImageFieldBuilder() {
                    if (imageBuilder_ == null) {
                        if (!(realMsgCase_ == 10001)) {
                            realMsg_ = MsgBean.ImageMessage.getDefaultInstance();
                        }
                        imageBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
                                MsgBean.ImageMessage, MsgBean.ImageMessage.Builder, MsgBean.ImageMessageOrBuilder>(
                                (MsgBean.ImageMessage) realMsg_,
                                getParentForChildren(),
                                isClean());
                        realMsg_ = null;
                    }
                    realMsgCase_ = 10001;
                    onChanged();;
                    return imageBuilder_;
                }

                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.RedEnvelopeMessage, MsgBean.RedEnvelopeMessage.Builder, MsgBean.RedEnvelopeMessageOrBuilder> redEnvelopeBuilder_;
                /**
                 * <code>.RedEnvelopeMessage red_envelope = 10002;</code>
                 */
                public MsgBean.RedEnvelopeMessage getRedEnvelope() {
                    if (redEnvelopeBuilder_ == null) {
                        if (realMsgCase_ == 10002) {
                            return (MsgBean.RedEnvelopeMessage) realMsg_;
                        }
                        return MsgBean.RedEnvelopeMessage.getDefaultInstance();
                    } else {
                        if (realMsgCase_ == 10002) {
                            return redEnvelopeBuilder_.getMessage();
                        }
                        return MsgBean.RedEnvelopeMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.RedEnvelopeMessage red_envelope = 10002;</code>
                 */
                public Builder setRedEnvelope(MsgBean.RedEnvelopeMessage value) {
                    if (redEnvelopeBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        realMsg_ = value;
                        onChanged();
                    } else {
                        redEnvelopeBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10002;
                    return this;
                }
                /**
                 * <code>.RedEnvelopeMessage red_envelope = 10002;</code>
                 */
                public Builder setRedEnvelope(
                        MsgBean.RedEnvelopeMessage.Builder builderForValue) {
                    if (redEnvelopeBuilder_ == null) {
                        realMsg_ = builderForValue.build();
                        onChanged();
                    } else {
                        redEnvelopeBuilder_.setMessage(builderForValue.build());
                    }
                    realMsgCase_ = 10002;
                    return this;
                }
                /**
                 * <code>.RedEnvelopeMessage red_envelope = 10002;</code>
                 */
                public Builder mergeRedEnvelope(MsgBean.RedEnvelopeMessage value) {
                    if (redEnvelopeBuilder_ == null) {
                        if (realMsgCase_ == 10002 &&
                                realMsg_ != MsgBean.RedEnvelopeMessage.getDefaultInstance()) {
                            realMsg_ = MsgBean.RedEnvelopeMessage.newBuilder((MsgBean.RedEnvelopeMessage) realMsg_)
                                    .mergeFrom(value).buildPartial();
                        } else {
                            realMsg_ = value;
                        }
                        onChanged();
                    } else {
                        if (realMsgCase_ == 10002) {
                            redEnvelopeBuilder_.mergeFrom(value);
                        }
                        redEnvelopeBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10002;
                    return this;
                }
                /**
                 * <code>.RedEnvelopeMessage red_envelope = 10002;</code>
                 */
                public Builder clearRedEnvelope() {
                    if (redEnvelopeBuilder_ == null) {
                        if (realMsgCase_ == 10002) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                            onChanged();
                        }
                    } else {
                        if (realMsgCase_ == 10002) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                        }
                        redEnvelopeBuilder_.clear();
                    }
                    return this;
                }
                /**
                 * <code>.RedEnvelopeMessage red_envelope = 10002;</code>
                 */
                public MsgBean.RedEnvelopeMessage.Builder getRedEnvelopeBuilder() {
                    return getRedEnvelopeFieldBuilder().getBuilder();
                }
                /**
                 * <code>.RedEnvelopeMessage red_envelope = 10002;</code>
                 */
                public MsgBean.RedEnvelopeMessageOrBuilder getRedEnvelopeOrBuilder() {
                    if ((realMsgCase_ == 10002) && (redEnvelopeBuilder_ != null)) {
                        return redEnvelopeBuilder_.getMessageOrBuilder();
                    } else {
                        if (realMsgCase_ == 10002) {
                            return (MsgBean.RedEnvelopeMessage) realMsg_;
                        }
                        return MsgBean.RedEnvelopeMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.RedEnvelopeMessage red_envelope = 10002;</code>
                 */
                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.RedEnvelopeMessage, MsgBean.RedEnvelopeMessage.Builder, MsgBean.RedEnvelopeMessageOrBuilder>
                getRedEnvelopeFieldBuilder() {
                    if (redEnvelopeBuilder_ == null) {
                        if (!(realMsgCase_ == 10002)) {
                            realMsg_ = MsgBean.RedEnvelopeMessage.getDefaultInstance();
                        }
                        redEnvelopeBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
                                MsgBean.RedEnvelopeMessage, MsgBean.RedEnvelopeMessage.Builder, MsgBean.RedEnvelopeMessageOrBuilder>(
                                (MsgBean.RedEnvelopeMessage) realMsg_,
                                getParentForChildren(),
                                isClean());
                        realMsg_ = null;
                    }
                    realMsgCase_ = 10002;
                    onChanged();;
                    return redEnvelopeBuilder_;
                }

                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.ReceiveRedEnvelopeMessage, MsgBean.ReceiveRedEnvelopeMessage.Builder, MsgBean.ReceiveRedEnvelopeMessageOrBuilder> receiveRedEnvelopeBuilder_;
                /**
                 * <code>.ReceiveRedEnvelopeMessage receive_red_envelope = 10003;</code>
                 */
                public MsgBean.ReceiveRedEnvelopeMessage getReceiveRedEnvelope() {
                    if (receiveRedEnvelopeBuilder_ == null) {
                        if (realMsgCase_ == 10003) {
                            return (MsgBean.ReceiveRedEnvelopeMessage) realMsg_;
                        }
                        return MsgBean.ReceiveRedEnvelopeMessage.getDefaultInstance();
                    } else {
                        if (realMsgCase_ == 10003) {
                            return receiveRedEnvelopeBuilder_.getMessage();
                        }
                        return MsgBean.ReceiveRedEnvelopeMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.ReceiveRedEnvelopeMessage receive_red_envelope = 10003;</code>
                 */
                public Builder setReceiveRedEnvelope(MsgBean.ReceiveRedEnvelopeMessage value) {
                    if (receiveRedEnvelopeBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        realMsg_ = value;
                        onChanged();
                    } else {
                        receiveRedEnvelopeBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10003;
                    return this;
                }
                /**
                 * <code>.ReceiveRedEnvelopeMessage receive_red_envelope = 10003;</code>
                 */
                public Builder setReceiveRedEnvelope(
                        MsgBean.ReceiveRedEnvelopeMessage.Builder builderForValue) {
                    if (receiveRedEnvelopeBuilder_ == null) {
                        realMsg_ = builderForValue.build();
                        onChanged();
                    } else {
                        receiveRedEnvelopeBuilder_.setMessage(builderForValue.build());
                    }
                    realMsgCase_ = 10003;
                    return this;
                }
                /**
                 * <code>.ReceiveRedEnvelopeMessage receive_red_envelope = 10003;</code>
                 */
                public Builder mergeReceiveRedEnvelope(MsgBean.ReceiveRedEnvelopeMessage value) {
                    if (receiveRedEnvelopeBuilder_ == null) {
                        if (realMsgCase_ == 10003 &&
                                realMsg_ != MsgBean.ReceiveRedEnvelopeMessage.getDefaultInstance()) {
                            realMsg_ = MsgBean.ReceiveRedEnvelopeMessage.newBuilder((MsgBean.ReceiveRedEnvelopeMessage) realMsg_)
                                    .mergeFrom(value).buildPartial();
                        } else {
                            realMsg_ = value;
                        }
                        onChanged();
                    } else {
                        if (realMsgCase_ == 10003) {
                            receiveRedEnvelopeBuilder_.mergeFrom(value);
                        }
                        receiveRedEnvelopeBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10003;
                    return this;
                }
                /**
                 * <code>.ReceiveRedEnvelopeMessage receive_red_envelope = 10003;</code>
                 */
                public Builder clearReceiveRedEnvelope() {
                    if (receiveRedEnvelopeBuilder_ == null) {
                        if (realMsgCase_ == 10003) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                            onChanged();
                        }
                    } else {
                        if (realMsgCase_ == 10003) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                        }
                        receiveRedEnvelopeBuilder_.clear();
                    }
                    return this;
                }
                /**
                 * <code>.ReceiveRedEnvelopeMessage receive_red_envelope = 10003;</code>
                 */
                public MsgBean.ReceiveRedEnvelopeMessage.Builder getReceiveRedEnvelopeBuilder() {
                    return getReceiveRedEnvelopeFieldBuilder().getBuilder();
                }
                /**
                 * <code>.ReceiveRedEnvelopeMessage receive_red_envelope = 10003;</code>
                 */
                public MsgBean.ReceiveRedEnvelopeMessageOrBuilder getReceiveRedEnvelopeOrBuilder() {
                    if ((realMsgCase_ == 10003) && (receiveRedEnvelopeBuilder_ != null)) {
                        return receiveRedEnvelopeBuilder_.getMessageOrBuilder();
                    } else {
                        if (realMsgCase_ == 10003) {
                            return (MsgBean.ReceiveRedEnvelopeMessage) realMsg_;
                        }
                        return MsgBean.ReceiveRedEnvelopeMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.ReceiveRedEnvelopeMessage receive_red_envelope = 10003;</code>
                 */
                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.ReceiveRedEnvelopeMessage, MsgBean.ReceiveRedEnvelopeMessage.Builder, MsgBean.ReceiveRedEnvelopeMessageOrBuilder>
                getReceiveRedEnvelopeFieldBuilder() {
                    if (receiveRedEnvelopeBuilder_ == null) {
                        if (!(realMsgCase_ == 10003)) {
                            realMsg_ = MsgBean.ReceiveRedEnvelopeMessage.getDefaultInstance();
                        }
                        receiveRedEnvelopeBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
                                MsgBean.ReceiveRedEnvelopeMessage, MsgBean.ReceiveRedEnvelopeMessage.Builder, MsgBean.ReceiveRedEnvelopeMessageOrBuilder>(
                                (MsgBean.ReceiveRedEnvelopeMessage) realMsg_,
                                getParentForChildren(),
                                isClean());
                        realMsg_ = null;
                    }
                    realMsgCase_ = 10003;
                    onChanged();;
                    return receiveRedEnvelopeBuilder_;
                }

                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.TransferMessage, MsgBean.TransferMessage.Builder, MsgBean.TransferMessageOrBuilder> transferBuilder_;
                /**
                 * <code>.TransferMessage transfer = 10004;</code>
                 */
                public MsgBean.TransferMessage getTransfer() {
                    if (transferBuilder_ == null) {
                        if (realMsgCase_ == 10004) {
                            return (MsgBean.TransferMessage) realMsg_;
                        }
                        return MsgBean.TransferMessage.getDefaultInstance();
                    } else {
                        if (realMsgCase_ == 10004) {
                            return transferBuilder_.getMessage();
                        }
                        return MsgBean.TransferMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.TransferMessage transfer = 10004;</code>
                 */
                public Builder setTransfer(MsgBean.TransferMessage value) {
                    if (transferBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        realMsg_ = value;
                        onChanged();
                    } else {
                        transferBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10004;
                    return this;
                }
                /**
                 * <code>.TransferMessage transfer = 10004;</code>
                 */
                public Builder setTransfer(
                        MsgBean.TransferMessage.Builder builderForValue) {
                    if (transferBuilder_ == null) {
                        realMsg_ = builderForValue.build();
                        onChanged();
                    } else {
                        transferBuilder_.setMessage(builderForValue.build());
                    }
                    realMsgCase_ = 10004;
                    return this;
                }
                /**
                 * <code>.TransferMessage transfer = 10004;</code>
                 */
                public Builder mergeTransfer(MsgBean.TransferMessage value) {
                    if (transferBuilder_ == null) {
                        if (realMsgCase_ == 10004 &&
                                realMsg_ != MsgBean.TransferMessage.getDefaultInstance()) {
                            realMsg_ = MsgBean.TransferMessage.newBuilder((MsgBean.TransferMessage) realMsg_)
                                    .mergeFrom(value).buildPartial();
                        } else {
                            realMsg_ = value;
                        }
                        onChanged();
                    } else {
                        if (realMsgCase_ == 10004) {
                            transferBuilder_.mergeFrom(value);
                        }
                        transferBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10004;
                    return this;
                }
                /**
                 * <code>.TransferMessage transfer = 10004;</code>
                 */
                public Builder clearTransfer() {
                    if (transferBuilder_ == null) {
                        if (realMsgCase_ == 10004) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                            onChanged();
                        }
                    } else {
                        if (realMsgCase_ == 10004) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                        }
                        transferBuilder_.clear();
                    }
                    return this;
                }
                /**
                 * <code>.TransferMessage transfer = 10004;</code>
                 */
                public MsgBean.TransferMessage.Builder getTransferBuilder() {
                    return getTransferFieldBuilder().getBuilder();
                }
                /**
                 * <code>.TransferMessage transfer = 10004;</code>
                 */
                public MsgBean.TransferMessageOrBuilder getTransferOrBuilder() {
                    if ((realMsgCase_ == 10004) && (transferBuilder_ != null)) {
                        return transferBuilder_.getMessageOrBuilder();
                    } else {
                        if (realMsgCase_ == 10004) {
                            return (MsgBean.TransferMessage) realMsg_;
                        }
                        return MsgBean.TransferMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.TransferMessage transfer = 10004;</code>
                 */
                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.TransferMessage, MsgBean.TransferMessage.Builder, MsgBean.TransferMessageOrBuilder>
                getTransferFieldBuilder() {
                    if (transferBuilder_ == null) {
                        if (!(realMsgCase_ == 10004)) {
                            realMsg_ = MsgBean.TransferMessage.getDefaultInstance();
                        }
                        transferBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
                                MsgBean.TransferMessage, MsgBean.TransferMessage.Builder, MsgBean.TransferMessageOrBuilder>(
                                (MsgBean.TransferMessage) realMsg_,
                                getParentForChildren(),
                                isClean());
                        realMsg_ = null;
                    }
                    realMsgCase_ = 10004;
                    onChanged();;
                    return transferBuilder_;
                }

                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.StampMessage, MsgBean.StampMessage.Builder, MsgBean.StampMessageOrBuilder> stampBuilder_;
                /**
                 * <code>.StampMessage stamp = 10005;</code>
                 */
                public MsgBean.StampMessage getStamp() {
                    if (stampBuilder_ == null) {
                        if (realMsgCase_ == 10005) {
                            return (MsgBean.StampMessage) realMsg_;
                        }
                        return MsgBean.StampMessage.getDefaultInstance();
                    } else {
                        if (realMsgCase_ == 10005) {
                            return stampBuilder_.getMessage();
                        }
                        return MsgBean.StampMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.StampMessage stamp = 10005;</code>
                 */
                public Builder setStamp(MsgBean.StampMessage value) {
                    if (stampBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        realMsg_ = value;
                        onChanged();
                    } else {
                        stampBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10005;
                    return this;
                }
                /**
                 * <code>.StampMessage stamp = 10005;</code>
                 */
                public Builder setStamp(
                        MsgBean.StampMessage.Builder builderForValue) {
                    if (stampBuilder_ == null) {
                        realMsg_ = builderForValue.build();
                        onChanged();
                    } else {
                        stampBuilder_.setMessage(builderForValue.build());
                    }
                    realMsgCase_ = 10005;
                    return this;
                }
                /**
                 * <code>.StampMessage stamp = 10005;</code>
                 */
                public Builder mergeStamp(MsgBean.StampMessage value) {
                    if (stampBuilder_ == null) {
                        if (realMsgCase_ == 10005 &&
                                realMsg_ != MsgBean.StampMessage.getDefaultInstance()) {
                            realMsg_ = MsgBean.StampMessage.newBuilder((MsgBean.StampMessage) realMsg_)
                                    .mergeFrom(value).buildPartial();
                        } else {
                            realMsg_ = value;
                        }
                        onChanged();
                    } else {
                        if (realMsgCase_ == 10005) {
                            stampBuilder_.mergeFrom(value);
                        }
                        stampBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10005;
                    return this;
                }
                /**
                 * <code>.StampMessage stamp = 10005;</code>
                 */
                public Builder clearStamp() {
                    if (stampBuilder_ == null) {
                        if (realMsgCase_ == 10005) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                            onChanged();
                        }
                    } else {
                        if (realMsgCase_ == 10005) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                        }
                        stampBuilder_.clear();
                    }
                    return this;
                }
                /**
                 * <code>.StampMessage stamp = 10005;</code>
                 */
                public MsgBean.StampMessage.Builder getStampBuilder() {
                    return getStampFieldBuilder().getBuilder();
                }
                /**
                 * <code>.StampMessage stamp = 10005;</code>
                 */
                public MsgBean.StampMessageOrBuilder getStampOrBuilder() {
                    if ((realMsgCase_ == 10005) && (stampBuilder_ != null)) {
                        return stampBuilder_.getMessageOrBuilder();
                    } else {
                        if (realMsgCase_ == 10005) {
                            return (MsgBean.StampMessage) realMsg_;
                        }
                        return MsgBean.StampMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.StampMessage stamp = 10005;</code>
                 */
                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.StampMessage, MsgBean.StampMessage.Builder, MsgBean.StampMessageOrBuilder>
                getStampFieldBuilder() {
                    if (stampBuilder_ == null) {
                        if (!(realMsgCase_ == 10005)) {
                            realMsg_ = MsgBean.StampMessage.getDefaultInstance();
                        }
                        stampBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
                                MsgBean.StampMessage, MsgBean.StampMessage.Builder, MsgBean.StampMessageOrBuilder>(
                                (MsgBean.StampMessage) realMsg_,
                                getParentForChildren(),
                                isClean());
                        realMsg_ = null;
                    }
                    realMsgCase_ = 10005;
                    onChanged();;
                    return stampBuilder_;
                }

                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.BusinessCardMessage, MsgBean.BusinessCardMessage.Builder, MsgBean.BusinessCardMessageOrBuilder> businessCardBuilder_;
                /**
                 * <code>.BusinessCardMessage business_card = 10006;</code>
                 */
                public MsgBean.BusinessCardMessage getBusinessCard() {
                    if (businessCardBuilder_ == null) {
                        if (realMsgCase_ == 10006) {
                            return (MsgBean.BusinessCardMessage) realMsg_;
                        }
                        return MsgBean.BusinessCardMessage.getDefaultInstance();
                    } else {
                        if (realMsgCase_ == 10006) {
                            return businessCardBuilder_.getMessage();
                        }
                        return MsgBean.BusinessCardMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.BusinessCardMessage business_card = 10006;</code>
                 */
                public Builder setBusinessCard(MsgBean.BusinessCardMessage value) {
                    if (businessCardBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        realMsg_ = value;
                        onChanged();
                    } else {
                        businessCardBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10006;
                    return this;
                }
                /**
                 * <code>.BusinessCardMessage business_card = 10006;</code>
                 */
                public Builder setBusinessCard(
                        MsgBean.BusinessCardMessage.Builder builderForValue) {
                    if (businessCardBuilder_ == null) {
                        realMsg_ = builderForValue.build();
                        onChanged();
                    } else {
                        businessCardBuilder_.setMessage(builderForValue.build());
                    }
                    realMsgCase_ = 10006;
                    return this;
                }
                /**
                 * <code>.BusinessCardMessage business_card = 10006;</code>
                 */
                public Builder mergeBusinessCard(MsgBean.BusinessCardMessage value) {
                    if (businessCardBuilder_ == null) {
                        if (realMsgCase_ == 10006 &&
                                realMsg_ != MsgBean.BusinessCardMessage.getDefaultInstance()) {
                            realMsg_ = MsgBean.BusinessCardMessage.newBuilder((MsgBean.BusinessCardMessage) realMsg_)
                                    .mergeFrom(value).buildPartial();
                        } else {
                            realMsg_ = value;
                        }
                        onChanged();
                    } else {
                        if (realMsgCase_ == 10006) {
                            businessCardBuilder_.mergeFrom(value);
                        }
                        businessCardBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10006;
                    return this;
                }
                /**
                 * <code>.BusinessCardMessage business_card = 10006;</code>
                 */
                public Builder clearBusinessCard() {
                    if (businessCardBuilder_ == null) {
                        if (realMsgCase_ == 10006) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                            onChanged();
                        }
                    } else {
                        if (realMsgCase_ == 10006) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                        }
                        businessCardBuilder_.clear();
                    }
                    return this;
                }
                /**
                 * <code>.BusinessCardMessage business_card = 10006;</code>
                 */
                public MsgBean.BusinessCardMessage.Builder getBusinessCardBuilder() {
                    return getBusinessCardFieldBuilder().getBuilder();
                }
                /**
                 * <code>.BusinessCardMessage business_card = 10006;</code>
                 */
                public MsgBean.BusinessCardMessageOrBuilder getBusinessCardOrBuilder() {
                    if ((realMsgCase_ == 10006) && (businessCardBuilder_ != null)) {
                        return businessCardBuilder_.getMessageOrBuilder();
                    } else {
                        if (realMsgCase_ == 10006) {
                            return (MsgBean.BusinessCardMessage) realMsg_;
                        }
                        return MsgBean.BusinessCardMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.BusinessCardMessage business_card = 10006;</code>
                 */
                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.BusinessCardMessage, MsgBean.BusinessCardMessage.Builder, MsgBean.BusinessCardMessageOrBuilder>
                getBusinessCardFieldBuilder() {
                    if (businessCardBuilder_ == null) {
                        if (!(realMsgCase_ == 10006)) {
                            realMsg_ = MsgBean.BusinessCardMessage.getDefaultInstance();
                        }
                        businessCardBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
                                MsgBean.BusinessCardMessage, MsgBean.BusinessCardMessage.Builder, MsgBean.BusinessCardMessageOrBuilder>(
                                (MsgBean.BusinessCardMessage) realMsg_,
                                getParentForChildren(),
                                isClean());
                        realMsg_ = null;
                    }
                    realMsgCase_ = 10006;
                    onChanged();;
                    return businessCardBuilder_;
                }

                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.RequestFriendMessage, MsgBean.RequestFriendMessage.Builder, MsgBean.RequestFriendMessageOrBuilder> requestFriendBuilder_;
                /**
                 * <code>.RequestFriendMessage request_friend = 10100;</code>
                 */
                public MsgBean.RequestFriendMessage getRequestFriend() {
                    if (requestFriendBuilder_ == null) {
                        if (realMsgCase_ == 10100) {
                            return (MsgBean.RequestFriendMessage) realMsg_;
                        }
                        return MsgBean.RequestFriendMessage.getDefaultInstance();
                    } else {
                        if (realMsgCase_ == 10100) {
                            return requestFriendBuilder_.getMessage();
                        }
                        return MsgBean.RequestFriendMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.RequestFriendMessage request_friend = 10100;</code>
                 */
                public Builder setRequestFriend(MsgBean.RequestFriendMessage value) {
                    if (requestFriendBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        realMsg_ = value;
                        onChanged();
                    } else {
                        requestFriendBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10100;
                    return this;
                }
                /**
                 * <code>.RequestFriendMessage request_friend = 10100;</code>
                 */
                public Builder setRequestFriend(
                        MsgBean.RequestFriendMessage.Builder builderForValue) {
                    if (requestFriendBuilder_ == null) {
                        realMsg_ = builderForValue.build();
                        onChanged();
                    } else {
                        requestFriendBuilder_.setMessage(builderForValue.build());
                    }
                    realMsgCase_ = 10100;
                    return this;
                }
                /**
                 * <code>.RequestFriendMessage request_friend = 10100;</code>
                 */
                public Builder mergeRequestFriend(MsgBean.RequestFriendMessage value) {
                    if (requestFriendBuilder_ == null) {
                        if (realMsgCase_ == 10100 &&
                                realMsg_ != MsgBean.RequestFriendMessage.getDefaultInstance()) {
                            realMsg_ = MsgBean.RequestFriendMessage.newBuilder((MsgBean.RequestFriendMessage) realMsg_)
                                    .mergeFrom(value).buildPartial();
                        } else {
                            realMsg_ = value;
                        }
                        onChanged();
                    } else {
                        if (realMsgCase_ == 10100) {
                            requestFriendBuilder_.mergeFrom(value);
                        }
                        requestFriendBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10100;
                    return this;
                }
                /**
                 * <code>.RequestFriendMessage request_friend = 10100;</code>
                 */
                public Builder clearRequestFriend() {
                    if (requestFriendBuilder_ == null) {
                        if (realMsgCase_ == 10100) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                            onChanged();
                        }
                    } else {
                        if (realMsgCase_ == 10100) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                        }
                        requestFriendBuilder_.clear();
                    }
                    return this;
                }
                /**
                 * <code>.RequestFriendMessage request_friend = 10100;</code>
                 */
                public MsgBean.RequestFriendMessage.Builder getRequestFriendBuilder() {
                    return getRequestFriendFieldBuilder().getBuilder();
                }
                /**
                 * <code>.RequestFriendMessage request_friend = 10100;</code>
                 */
                public MsgBean.RequestFriendMessageOrBuilder getRequestFriendOrBuilder() {
                    if ((realMsgCase_ == 10100) && (requestFriendBuilder_ != null)) {
                        return requestFriendBuilder_.getMessageOrBuilder();
                    } else {
                        if (realMsgCase_ == 10100) {
                            return (MsgBean.RequestFriendMessage) realMsg_;
                        }
                        return MsgBean.RequestFriendMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.RequestFriendMessage request_friend = 10100;</code>
                 */
                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.RequestFriendMessage, MsgBean.RequestFriendMessage.Builder, MsgBean.RequestFriendMessageOrBuilder>
                getRequestFriendFieldBuilder() {
                    if (requestFriendBuilder_ == null) {
                        if (!(realMsgCase_ == 10100)) {
                            realMsg_ = MsgBean.RequestFriendMessage.getDefaultInstance();
                        }
                        requestFriendBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
                                MsgBean.RequestFriendMessage, MsgBean.RequestFriendMessage.Builder, MsgBean.RequestFriendMessageOrBuilder>(
                                (MsgBean.RequestFriendMessage) realMsg_,
                                getParentForChildren(),
                                isClean());
                        realMsg_ = null;
                    }
                    realMsgCase_ = 10100;
                    onChanged();;
                    return requestFriendBuilder_;
                }

                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.AcceptBeFriendsMessage, MsgBean.AcceptBeFriendsMessage.Builder, MsgBean.AcceptBeFriendsMessageOrBuilder> acceptBeFriendsBuilder_;
                /**
                 * <code>.AcceptBeFriendsMessage accept_be_friends = 10101;</code>
                 */
                public MsgBean.AcceptBeFriendsMessage getAcceptBeFriends() {
                    if (acceptBeFriendsBuilder_ == null) {
                        if (realMsgCase_ == 10101) {
                            return (MsgBean.AcceptBeFriendsMessage) realMsg_;
                        }
                        return MsgBean.AcceptBeFriendsMessage.getDefaultInstance();
                    } else {
                        if (realMsgCase_ == 10101) {
                            return acceptBeFriendsBuilder_.getMessage();
                        }
                        return MsgBean.AcceptBeFriendsMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.AcceptBeFriendsMessage accept_be_friends = 10101;</code>
                 */
                public Builder setAcceptBeFriends(MsgBean.AcceptBeFriendsMessage value) {
                    if (acceptBeFriendsBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        realMsg_ = value;
                        onChanged();
                    } else {
                        acceptBeFriendsBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10101;
                    return this;
                }
                /**
                 * <code>.AcceptBeFriendsMessage accept_be_friends = 10101;</code>
                 */
                public Builder setAcceptBeFriends(
                        MsgBean.AcceptBeFriendsMessage.Builder builderForValue) {
                    if (acceptBeFriendsBuilder_ == null) {
                        realMsg_ = builderForValue.build();
                        onChanged();
                    } else {
                        acceptBeFriendsBuilder_.setMessage(builderForValue.build());
                    }
                    realMsgCase_ = 10101;
                    return this;
                }
                /**
                 * <code>.AcceptBeFriendsMessage accept_be_friends = 10101;</code>
                 */
                public Builder mergeAcceptBeFriends(MsgBean.AcceptBeFriendsMessage value) {
                    if (acceptBeFriendsBuilder_ == null) {
                        if (realMsgCase_ == 10101 &&
                                realMsg_ != MsgBean.AcceptBeFriendsMessage.getDefaultInstance()) {
                            realMsg_ = MsgBean.AcceptBeFriendsMessage.newBuilder((MsgBean.AcceptBeFriendsMessage) realMsg_)
                                    .mergeFrom(value).buildPartial();
                        } else {
                            realMsg_ = value;
                        }
                        onChanged();
                    } else {
                        if (realMsgCase_ == 10101) {
                            acceptBeFriendsBuilder_.mergeFrom(value);
                        }
                        acceptBeFriendsBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10101;
                    return this;
                }
                /**
                 * <code>.AcceptBeFriendsMessage accept_be_friends = 10101;</code>
                 */
                public Builder clearAcceptBeFriends() {
                    if (acceptBeFriendsBuilder_ == null) {
                        if (realMsgCase_ == 10101) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                            onChanged();
                        }
                    } else {
                        if (realMsgCase_ == 10101) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                        }
                        acceptBeFriendsBuilder_.clear();
                    }
                    return this;
                }
                /**
                 * <code>.AcceptBeFriendsMessage accept_be_friends = 10101;</code>
                 */
                public MsgBean.AcceptBeFriendsMessage.Builder getAcceptBeFriendsBuilder() {
                    return getAcceptBeFriendsFieldBuilder().getBuilder();
                }
                /**
                 * <code>.AcceptBeFriendsMessage accept_be_friends = 10101;</code>
                 */
                public MsgBean.AcceptBeFriendsMessageOrBuilder getAcceptBeFriendsOrBuilder() {
                    if ((realMsgCase_ == 10101) && (acceptBeFriendsBuilder_ != null)) {
                        return acceptBeFriendsBuilder_.getMessageOrBuilder();
                    } else {
                        if (realMsgCase_ == 10101) {
                            return (MsgBean.AcceptBeFriendsMessage) realMsg_;
                        }
                        return MsgBean.AcceptBeFriendsMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.AcceptBeFriendsMessage accept_be_friends = 10101;</code>
                 */
                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.AcceptBeFriendsMessage, MsgBean.AcceptBeFriendsMessage.Builder, MsgBean.AcceptBeFriendsMessageOrBuilder>
                getAcceptBeFriendsFieldBuilder() {
                    if (acceptBeFriendsBuilder_ == null) {
                        if (!(realMsgCase_ == 10101)) {
                            realMsg_ = MsgBean.AcceptBeFriendsMessage.getDefaultInstance();
                        }
                        acceptBeFriendsBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
                                MsgBean.AcceptBeFriendsMessage, MsgBean.AcceptBeFriendsMessage.Builder, MsgBean.AcceptBeFriendsMessageOrBuilder>(
                                (MsgBean.AcceptBeFriendsMessage) realMsg_,
                                getParentForChildren(),
                                isClean());
                        realMsg_ = null;
                    }
                    realMsgCase_ = 10101;
                    onChanged();;
                    return acceptBeFriendsBuilder_;
                }

                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.RemoveFriendMessage, MsgBean.RemoveFriendMessage.Builder, MsgBean.RemoveFriendMessageOrBuilder> removeFriendBuilder_;
                /**
                 * <code>.RemoveFriendMessage remove_friend = 10102;</code>
                 */
                public MsgBean.RemoveFriendMessage getRemoveFriend() {
                    if (removeFriendBuilder_ == null) {
                        if (realMsgCase_ == 10102) {
                            return (MsgBean.RemoveFriendMessage) realMsg_;
                        }
                        return MsgBean.RemoveFriendMessage.getDefaultInstance();
                    } else {
                        if (realMsgCase_ == 10102) {
                            return removeFriendBuilder_.getMessage();
                        }
                        return MsgBean.RemoveFriendMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.RemoveFriendMessage remove_friend = 10102;</code>
                 */
                public Builder setRemoveFriend(MsgBean.RemoveFriendMessage value) {
                    if (removeFriendBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        realMsg_ = value;
                        onChanged();
                    } else {
                        removeFriendBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10102;
                    return this;
                }
                /**
                 * <code>.RemoveFriendMessage remove_friend = 10102;</code>
                 */
                public Builder setRemoveFriend(
                        MsgBean.RemoveFriendMessage.Builder builderForValue) {
                    if (removeFriendBuilder_ == null) {
                        realMsg_ = builderForValue.build();
                        onChanged();
                    } else {
                        removeFriendBuilder_.setMessage(builderForValue.build());
                    }
                    realMsgCase_ = 10102;
                    return this;
                }
                /**
                 * <code>.RemoveFriendMessage remove_friend = 10102;</code>
                 */
                public Builder mergeRemoveFriend(MsgBean.RemoveFriendMessage value) {
                    if (removeFriendBuilder_ == null) {
                        if (realMsgCase_ == 10102 &&
                                realMsg_ != MsgBean.RemoveFriendMessage.getDefaultInstance()) {
                            realMsg_ = MsgBean.RemoveFriendMessage.newBuilder((MsgBean.RemoveFriendMessage) realMsg_)
                                    .mergeFrom(value).buildPartial();
                        } else {
                            realMsg_ = value;
                        }
                        onChanged();
                    } else {
                        if (realMsgCase_ == 10102) {
                            removeFriendBuilder_.mergeFrom(value);
                        }
                        removeFriendBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10102;
                    return this;
                }
                /**
                 * <code>.RemoveFriendMessage remove_friend = 10102;</code>
                 */
                public Builder clearRemoveFriend() {
                    if (removeFriendBuilder_ == null) {
                        if (realMsgCase_ == 10102) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                            onChanged();
                        }
                    } else {
                        if (realMsgCase_ == 10102) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                        }
                        removeFriendBuilder_.clear();
                    }
                    return this;
                }
                /**
                 * <code>.RemoveFriendMessage remove_friend = 10102;</code>
                 */
                public MsgBean.RemoveFriendMessage.Builder getRemoveFriendBuilder() {
                    return getRemoveFriendFieldBuilder().getBuilder();
                }
                /**
                 * <code>.RemoveFriendMessage remove_friend = 10102;</code>
                 */
                public MsgBean.RemoveFriendMessageOrBuilder getRemoveFriendOrBuilder() {
                    if ((realMsgCase_ == 10102) && (removeFriendBuilder_ != null)) {
                        return removeFriendBuilder_.getMessageOrBuilder();
                    } else {
                        if (realMsgCase_ == 10102) {
                            return (MsgBean.RemoveFriendMessage) realMsg_;
                        }
                        return MsgBean.RemoveFriendMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.RemoveFriendMessage remove_friend = 10102;</code>
                 */
                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.RemoveFriendMessage, MsgBean.RemoveFriendMessage.Builder, MsgBean.RemoveFriendMessageOrBuilder>
                getRemoveFriendFieldBuilder() {
                    if (removeFriendBuilder_ == null) {
                        if (!(realMsgCase_ == 10102)) {
                            realMsg_ = MsgBean.RemoveFriendMessage.getDefaultInstance();
                        }
                        removeFriendBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
                                MsgBean.RemoveFriendMessage, MsgBean.RemoveFriendMessage.Builder, MsgBean.RemoveFriendMessageOrBuilder>(
                                (MsgBean.RemoveFriendMessage) realMsg_,
                                getParentForChildren(),
                                isClean());
                        realMsg_ = null;
                    }
                    realMsgCase_ = 10102;
                    onChanged();;
                    return removeFriendBuilder_;
                }

                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.RequestGroupMessage, MsgBean.RequestGroupMessage.Builder, MsgBean.RequestGroupMessageOrBuilder> requestGroupBuilder_;
                /**
                 * <code>.RequestGroupMessage request_group = 10200;</code>
                 */
                public MsgBean.RequestGroupMessage getRequestGroup() {
                    if (requestGroupBuilder_ == null) {
                        if (realMsgCase_ == 10200) {
                            return (MsgBean.RequestGroupMessage) realMsg_;
                        }
                        return MsgBean.RequestGroupMessage.getDefaultInstance();
                    } else {
                        if (realMsgCase_ == 10200) {
                            return requestGroupBuilder_.getMessage();
                        }
                        return MsgBean.RequestGroupMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.RequestGroupMessage request_group = 10200;</code>
                 */
                public Builder setRequestGroup(MsgBean.RequestGroupMessage value) {
                    if (requestGroupBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        realMsg_ = value;
                        onChanged();
                    } else {
                        requestGroupBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10200;
                    return this;
                }
                /**
                 * <code>.RequestGroupMessage request_group = 10200;</code>
                 */
                public Builder setRequestGroup(
                        MsgBean.RequestGroupMessage.Builder builderForValue) {
                    if (requestGroupBuilder_ == null) {
                        realMsg_ = builderForValue.build();
                        onChanged();
                    } else {
                        requestGroupBuilder_.setMessage(builderForValue.build());
                    }
                    realMsgCase_ = 10200;
                    return this;
                }
                /**
                 * <code>.RequestGroupMessage request_group = 10200;</code>
                 */
                public Builder mergeRequestGroup(MsgBean.RequestGroupMessage value) {
                    if (requestGroupBuilder_ == null) {
                        if (realMsgCase_ == 10200 &&
                                realMsg_ != MsgBean.RequestGroupMessage.getDefaultInstance()) {
                            realMsg_ = MsgBean.RequestGroupMessage.newBuilder((MsgBean.RequestGroupMessage) realMsg_)
                                    .mergeFrom(value).buildPartial();
                        } else {
                            realMsg_ = value;
                        }
                        onChanged();
                    } else {
                        if (realMsgCase_ == 10200) {
                            requestGroupBuilder_.mergeFrom(value);
                        }
                        requestGroupBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10200;
                    return this;
                }
                /**
                 * <code>.RequestGroupMessage request_group = 10200;</code>
                 */
                public Builder clearRequestGroup() {
                    if (requestGroupBuilder_ == null) {
                        if (realMsgCase_ == 10200) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                            onChanged();
                        }
                    } else {
                        if (realMsgCase_ == 10200) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                        }
                        requestGroupBuilder_.clear();
                    }
                    return this;
                }
                /**
                 * <code>.RequestGroupMessage request_group = 10200;</code>
                 */
                public MsgBean.RequestGroupMessage.Builder getRequestGroupBuilder() {
                    return getRequestGroupFieldBuilder().getBuilder();
                }
                /**
                 * <code>.RequestGroupMessage request_group = 10200;</code>
                 */
                public MsgBean.RequestGroupMessageOrBuilder getRequestGroupOrBuilder() {
                    if ((realMsgCase_ == 10200) && (requestGroupBuilder_ != null)) {
                        return requestGroupBuilder_.getMessageOrBuilder();
                    } else {
                        if (realMsgCase_ == 10200) {
                            return (MsgBean.RequestGroupMessage) realMsg_;
                        }
                        return MsgBean.RequestGroupMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.RequestGroupMessage request_group = 10200;</code>
                 */
                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.RequestGroupMessage, MsgBean.RequestGroupMessage.Builder, MsgBean.RequestGroupMessageOrBuilder>
                getRequestGroupFieldBuilder() {
                    if (requestGroupBuilder_ == null) {
                        if (!(realMsgCase_ == 10200)) {
                            realMsg_ = MsgBean.RequestGroupMessage.getDefaultInstance();
                        }
                        requestGroupBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
                                MsgBean.RequestGroupMessage, MsgBean.RequestGroupMessage.Builder, MsgBean.RequestGroupMessageOrBuilder>(
                                (MsgBean.RequestGroupMessage) realMsg_,
                                getParentForChildren(),
                                isClean());
                        realMsg_ = null;
                    }
                    realMsgCase_ = 10200;
                    onChanged();;
                    return requestGroupBuilder_;
                }

                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.AcceptBeGroupMessage, MsgBean.AcceptBeGroupMessage.Builder, MsgBean.AcceptBeGroupMessageOrBuilder> acceptBeGroupBuilder_;
                /**
                 * <code>.AcceptBeGroupMessage accept_be_group = 10201;</code>
                 */
                public MsgBean.AcceptBeGroupMessage getAcceptBeGroup() {
                    if (acceptBeGroupBuilder_ == null) {
                        if (realMsgCase_ == 10201) {
                            return (MsgBean.AcceptBeGroupMessage) realMsg_;
                        }
                        return MsgBean.AcceptBeGroupMessage.getDefaultInstance();
                    } else {
                        if (realMsgCase_ == 10201) {
                            return acceptBeGroupBuilder_.getMessage();
                        }
                        return MsgBean.AcceptBeGroupMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.AcceptBeGroupMessage accept_be_group = 10201;</code>
                 */
                public Builder setAcceptBeGroup(MsgBean.AcceptBeGroupMessage value) {
                    if (acceptBeGroupBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        realMsg_ = value;
                        onChanged();
                    } else {
                        acceptBeGroupBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10201;
                    return this;
                }
                /**
                 * <code>.AcceptBeGroupMessage accept_be_group = 10201;</code>
                 */
                public Builder setAcceptBeGroup(
                        MsgBean.AcceptBeGroupMessage.Builder builderForValue) {
                    if (acceptBeGroupBuilder_ == null) {
                        realMsg_ = builderForValue.build();
                        onChanged();
                    } else {
                        acceptBeGroupBuilder_.setMessage(builderForValue.build());
                    }
                    realMsgCase_ = 10201;
                    return this;
                }
                /**
                 * <code>.AcceptBeGroupMessage accept_be_group = 10201;</code>
                 */
                public Builder mergeAcceptBeGroup(MsgBean.AcceptBeGroupMessage value) {
                    if (acceptBeGroupBuilder_ == null) {
                        if (realMsgCase_ == 10201 &&
                                realMsg_ != MsgBean.AcceptBeGroupMessage.getDefaultInstance()) {
                            realMsg_ = MsgBean.AcceptBeGroupMessage.newBuilder((MsgBean.AcceptBeGroupMessage) realMsg_)
                                    .mergeFrom(value).buildPartial();
                        } else {
                            realMsg_ = value;
                        }
                        onChanged();
                    } else {
                        if (realMsgCase_ == 10201) {
                            acceptBeGroupBuilder_.mergeFrom(value);
                        }
                        acceptBeGroupBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10201;
                    return this;
                }
                /**
                 * <code>.AcceptBeGroupMessage accept_be_group = 10201;</code>
                 */
                public Builder clearAcceptBeGroup() {
                    if (acceptBeGroupBuilder_ == null) {
                        if (realMsgCase_ == 10201) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                            onChanged();
                        }
                    } else {
                        if (realMsgCase_ == 10201) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                        }
                        acceptBeGroupBuilder_.clear();
                    }
                    return this;
                }
                /**
                 * <code>.AcceptBeGroupMessage accept_be_group = 10201;</code>
                 */
                public MsgBean.AcceptBeGroupMessage.Builder getAcceptBeGroupBuilder() {
                    return getAcceptBeGroupFieldBuilder().getBuilder();
                }
                /**
                 * <code>.AcceptBeGroupMessage accept_be_group = 10201;</code>
                 */
                public MsgBean.AcceptBeGroupMessageOrBuilder getAcceptBeGroupOrBuilder() {
                    if ((realMsgCase_ == 10201) && (acceptBeGroupBuilder_ != null)) {
                        return acceptBeGroupBuilder_.getMessageOrBuilder();
                    } else {
                        if (realMsgCase_ == 10201) {
                            return (MsgBean.AcceptBeGroupMessage) realMsg_;
                        }
                        return MsgBean.AcceptBeGroupMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.AcceptBeGroupMessage accept_be_group = 10201;</code>
                 */
                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.AcceptBeGroupMessage, MsgBean.AcceptBeGroupMessage.Builder, MsgBean.AcceptBeGroupMessageOrBuilder>
                getAcceptBeGroupFieldBuilder() {
                    if (acceptBeGroupBuilder_ == null) {
                        if (!(realMsgCase_ == 10201)) {
                            realMsg_ = MsgBean.AcceptBeGroupMessage.getDefaultInstance();
                        }
                        acceptBeGroupBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
                                MsgBean.AcceptBeGroupMessage, MsgBean.AcceptBeGroupMessage.Builder, MsgBean.AcceptBeGroupMessageOrBuilder>(
                                (MsgBean.AcceptBeGroupMessage) realMsg_,
                                getParentForChildren(),
                                isClean());
                        realMsg_ = null;
                    }
                    realMsgCase_ = 10201;
                    onChanged();;
                    return acceptBeGroupBuilder_;
                }

                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.RemoveGroupMemberMessage, MsgBean.RemoveGroupMemberMessage.Builder, MsgBean.RemoveGroupMemberMessageOrBuilder> removeGroupMemberBuilder_;
                /**
                 * <code>.RemoveGroupMemberMessage remove_group_member = 10202;</code>
                 */
                public MsgBean.RemoveGroupMemberMessage getRemoveGroupMember() {
                    if (removeGroupMemberBuilder_ == null) {
                        if (realMsgCase_ == 10202) {
                            return (MsgBean.RemoveGroupMemberMessage) realMsg_;
                        }
                        return MsgBean.RemoveGroupMemberMessage.getDefaultInstance();
                    } else {
                        if (realMsgCase_ == 10202) {
                            return removeGroupMemberBuilder_.getMessage();
                        }
                        return MsgBean.RemoveGroupMemberMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.RemoveGroupMemberMessage remove_group_member = 10202;</code>
                 */
                public Builder setRemoveGroupMember(MsgBean.RemoveGroupMemberMessage value) {
                    if (removeGroupMemberBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        realMsg_ = value;
                        onChanged();
                    } else {
                        removeGroupMemberBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10202;
                    return this;
                }
                /**
                 * <code>.RemoveGroupMemberMessage remove_group_member = 10202;</code>
                 */
                public Builder setRemoveGroupMember(
                        MsgBean.RemoveGroupMemberMessage.Builder builderForValue) {
                    if (removeGroupMemberBuilder_ == null) {
                        realMsg_ = builderForValue.build();
                        onChanged();
                    } else {
                        removeGroupMemberBuilder_.setMessage(builderForValue.build());
                    }
                    realMsgCase_ = 10202;
                    return this;
                }
                /**
                 * <code>.RemoveGroupMemberMessage remove_group_member = 10202;</code>
                 */
                public Builder mergeRemoveGroupMember(MsgBean.RemoveGroupMemberMessage value) {
                    if (removeGroupMemberBuilder_ == null) {
                        if (realMsgCase_ == 10202 &&
                                realMsg_ != MsgBean.RemoveGroupMemberMessage.getDefaultInstance()) {
                            realMsg_ = MsgBean.RemoveGroupMemberMessage.newBuilder((MsgBean.RemoveGroupMemberMessage) realMsg_)
                                    .mergeFrom(value).buildPartial();
                        } else {
                            realMsg_ = value;
                        }
                        onChanged();
                    } else {
                        if (realMsgCase_ == 10202) {
                            removeGroupMemberBuilder_.mergeFrom(value);
                        }
                        removeGroupMemberBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10202;
                    return this;
                }
                /**
                 * <code>.RemoveGroupMemberMessage remove_group_member = 10202;</code>
                 */
                public Builder clearRemoveGroupMember() {
                    if (removeGroupMemberBuilder_ == null) {
                        if (realMsgCase_ == 10202) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                            onChanged();
                        }
                    } else {
                        if (realMsgCase_ == 10202) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                        }
                        removeGroupMemberBuilder_.clear();
                    }
                    return this;
                }
                /**
                 * <code>.RemoveGroupMemberMessage remove_group_member = 10202;</code>
                 */
                public MsgBean.RemoveGroupMemberMessage.Builder getRemoveGroupMemberBuilder() {
                    return getRemoveGroupMemberFieldBuilder().getBuilder();
                }
                /**
                 * <code>.RemoveGroupMemberMessage remove_group_member = 10202;</code>
                 */
                public MsgBean.RemoveGroupMemberMessageOrBuilder getRemoveGroupMemberOrBuilder() {
                    if ((realMsgCase_ == 10202) && (removeGroupMemberBuilder_ != null)) {
                        return removeGroupMemberBuilder_.getMessageOrBuilder();
                    } else {
                        if (realMsgCase_ == 10202) {
                            return (MsgBean.RemoveGroupMemberMessage) realMsg_;
                        }
                        return MsgBean.RemoveGroupMemberMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.RemoveGroupMemberMessage remove_group_member = 10202;</code>
                 */
                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.RemoveGroupMemberMessage, MsgBean.RemoveGroupMemberMessage.Builder, MsgBean.RemoveGroupMemberMessageOrBuilder>
                getRemoveGroupMemberFieldBuilder() {
                    if (removeGroupMemberBuilder_ == null) {
                        if (!(realMsgCase_ == 10202)) {
                            realMsg_ = MsgBean.RemoveGroupMemberMessage.getDefaultInstance();
                        }
                        removeGroupMemberBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
                                MsgBean.RemoveGroupMemberMessage, MsgBean.RemoveGroupMemberMessage.Builder, MsgBean.RemoveGroupMemberMessageOrBuilder>(
                                (MsgBean.RemoveGroupMemberMessage) realMsg_,
                                getParentForChildren(),
                                isClean());
                        realMsg_ = null;
                    }
                    realMsgCase_ = 10202;
                    onChanged();;
                    return removeGroupMemberBuilder_;
                }

                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.ChangeGroupMasterMessage, MsgBean.ChangeGroupMasterMessage.Builder, MsgBean.ChangeGroupMasterMessageOrBuilder> changeGroupMasterBuilder_;
                /**
                 * <code>.ChangeGroupMasterMessage change_group_master = 10203;</code>
                 */
                public MsgBean.ChangeGroupMasterMessage getChangeGroupMaster() {
                    if (changeGroupMasterBuilder_ == null) {
                        if (realMsgCase_ == 10203) {
                            return (MsgBean.ChangeGroupMasterMessage) realMsg_;
                        }
                        return MsgBean.ChangeGroupMasterMessage.getDefaultInstance();
                    } else {
                        if (realMsgCase_ == 10203) {
                            return changeGroupMasterBuilder_.getMessage();
                        }
                        return MsgBean.ChangeGroupMasterMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.ChangeGroupMasterMessage change_group_master = 10203;</code>
                 */
                public Builder setChangeGroupMaster(MsgBean.ChangeGroupMasterMessage value) {
                    if (changeGroupMasterBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        realMsg_ = value;
                        onChanged();
                    } else {
                        changeGroupMasterBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10203;
                    return this;
                }
                /**
                 * <code>.ChangeGroupMasterMessage change_group_master = 10203;</code>
                 */
                public Builder setChangeGroupMaster(
                        MsgBean.ChangeGroupMasterMessage.Builder builderForValue) {
                    if (changeGroupMasterBuilder_ == null) {
                        realMsg_ = builderForValue.build();
                        onChanged();
                    } else {
                        changeGroupMasterBuilder_.setMessage(builderForValue.build());
                    }
                    realMsgCase_ = 10203;
                    return this;
                }
                /**
                 * <code>.ChangeGroupMasterMessage change_group_master = 10203;</code>
                 */
                public Builder mergeChangeGroupMaster(MsgBean.ChangeGroupMasterMessage value) {
                    if (changeGroupMasterBuilder_ == null) {
                        if (realMsgCase_ == 10203 &&
                                realMsg_ != MsgBean.ChangeGroupMasterMessage.getDefaultInstance()) {
                            realMsg_ = MsgBean.ChangeGroupMasterMessage.newBuilder((MsgBean.ChangeGroupMasterMessage) realMsg_)
                                    .mergeFrom(value).buildPartial();
                        } else {
                            realMsg_ = value;
                        }
                        onChanged();
                    } else {
                        if (realMsgCase_ == 10203) {
                            changeGroupMasterBuilder_.mergeFrom(value);
                        }
                        changeGroupMasterBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10203;
                    return this;
                }
                /**
                 * <code>.ChangeGroupMasterMessage change_group_master = 10203;</code>
                 */
                public Builder clearChangeGroupMaster() {
                    if (changeGroupMasterBuilder_ == null) {
                        if (realMsgCase_ == 10203) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                            onChanged();
                        }
                    } else {
                        if (realMsgCase_ == 10203) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                        }
                        changeGroupMasterBuilder_.clear();
                    }
                    return this;
                }
                /**
                 * <code>.ChangeGroupMasterMessage change_group_master = 10203;</code>
                 */
                public MsgBean.ChangeGroupMasterMessage.Builder getChangeGroupMasterBuilder() {
                    return getChangeGroupMasterFieldBuilder().getBuilder();
                }
                /**
                 * <code>.ChangeGroupMasterMessage change_group_master = 10203;</code>
                 */
                public MsgBean.ChangeGroupMasterMessageOrBuilder getChangeGroupMasterOrBuilder() {
                    if ((realMsgCase_ == 10203) && (changeGroupMasterBuilder_ != null)) {
                        return changeGroupMasterBuilder_.getMessageOrBuilder();
                    } else {
                        if (realMsgCase_ == 10203) {
                            return (MsgBean.ChangeGroupMasterMessage) realMsg_;
                        }
                        return MsgBean.ChangeGroupMasterMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.ChangeGroupMasterMessage change_group_master = 10203;</code>
                 */
                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.ChangeGroupMasterMessage, MsgBean.ChangeGroupMasterMessage.Builder, MsgBean.ChangeGroupMasterMessageOrBuilder>
                getChangeGroupMasterFieldBuilder() {
                    if (changeGroupMasterBuilder_ == null) {
                        if (!(realMsgCase_ == 10203)) {
                            realMsg_ = MsgBean.ChangeGroupMasterMessage.getDefaultInstance();
                        }
                        changeGroupMasterBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
                                MsgBean.ChangeGroupMasterMessage, MsgBean.ChangeGroupMasterMessage.Builder, MsgBean.ChangeGroupMasterMessageOrBuilder>(
                                (MsgBean.ChangeGroupMasterMessage) realMsg_,
                                getParentForChildren(),
                                isClean());
                        realMsg_ = null;
                    }
                    realMsgCase_ = 10203;
                    onChanged();;
                    return changeGroupMasterBuilder_;
                }

                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.ChangeGroupNameMessage, MsgBean.ChangeGroupNameMessage.Builder, MsgBean.ChangeGroupNameMessageOrBuilder> changeGroupNameBuilder_;
                /**
                 * <code>.ChangeGroupNameMessage change_group_name = 10204;</code>
                 */
                public MsgBean.ChangeGroupNameMessage getChangeGroupName() {
                    if (changeGroupNameBuilder_ == null) {
                        if (realMsgCase_ == 10204) {
                            return (MsgBean.ChangeGroupNameMessage) realMsg_;
                        }
                        return MsgBean.ChangeGroupNameMessage.getDefaultInstance();
                    } else {
                        if (realMsgCase_ == 10204) {
                            return changeGroupNameBuilder_.getMessage();
                        }
                        return MsgBean.ChangeGroupNameMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.ChangeGroupNameMessage change_group_name = 10204;</code>
                 */
                public Builder setChangeGroupName(MsgBean.ChangeGroupNameMessage value) {
                    if (changeGroupNameBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        realMsg_ = value;
                        onChanged();
                    } else {
                        changeGroupNameBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10204;
                    return this;
                }
                /**
                 * <code>.ChangeGroupNameMessage change_group_name = 10204;</code>
                 */
                public Builder setChangeGroupName(
                        MsgBean.ChangeGroupNameMessage.Builder builderForValue) {
                    if (changeGroupNameBuilder_ == null) {
                        realMsg_ = builderForValue.build();
                        onChanged();
                    } else {
                        changeGroupNameBuilder_.setMessage(builderForValue.build());
                    }
                    realMsgCase_ = 10204;
                    return this;
                }
                /**
                 * <code>.ChangeGroupNameMessage change_group_name = 10204;</code>
                 */
                public Builder mergeChangeGroupName(MsgBean.ChangeGroupNameMessage value) {
                    if (changeGroupNameBuilder_ == null) {
                        if (realMsgCase_ == 10204 &&
                                realMsg_ != MsgBean.ChangeGroupNameMessage.getDefaultInstance()) {
                            realMsg_ = MsgBean.ChangeGroupNameMessage.newBuilder((MsgBean.ChangeGroupNameMessage) realMsg_)
                                    .mergeFrom(value).buildPartial();
                        } else {
                            realMsg_ = value;
                        }
                        onChanged();
                    } else {
                        if (realMsgCase_ == 10204) {
                            changeGroupNameBuilder_.mergeFrom(value);
                        }
                        changeGroupNameBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10204;
                    return this;
                }
                /**
                 * <code>.ChangeGroupNameMessage change_group_name = 10204;</code>
                 */
                public Builder clearChangeGroupName() {
                    if (changeGroupNameBuilder_ == null) {
                        if (realMsgCase_ == 10204) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                            onChanged();
                        }
                    } else {
                        if (realMsgCase_ == 10204) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                        }
                        changeGroupNameBuilder_.clear();
                    }
                    return this;
                }
                /**
                 * <code>.ChangeGroupNameMessage change_group_name = 10204;</code>
                 */
                public MsgBean.ChangeGroupNameMessage.Builder getChangeGroupNameBuilder() {
                    return getChangeGroupNameFieldBuilder().getBuilder();
                }
                /**
                 * <code>.ChangeGroupNameMessage change_group_name = 10204;</code>
                 */
                public MsgBean.ChangeGroupNameMessageOrBuilder getChangeGroupNameOrBuilder() {
                    if ((realMsgCase_ == 10204) && (changeGroupNameBuilder_ != null)) {
                        return changeGroupNameBuilder_.getMessageOrBuilder();
                    } else {
                        if (realMsgCase_ == 10204) {
                            return (MsgBean.ChangeGroupNameMessage) realMsg_;
                        }
                        return MsgBean.ChangeGroupNameMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.ChangeGroupNameMessage change_group_name = 10204;</code>
                 */
                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.ChangeGroupNameMessage, MsgBean.ChangeGroupNameMessage.Builder, MsgBean.ChangeGroupNameMessageOrBuilder>
                getChangeGroupNameFieldBuilder() {
                    if (changeGroupNameBuilder_ == null) {
                        if (!(realMsgCase_ == 10204)) {
                            realMsg_ = MsgBean.ChangeGroupNameMessage.getDefaultInstance();
                        }
                        changeGroupNameBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
                                MsgBean.ChangeGroupNameMessage, MsgBean.ChangeGroupNameMessage.Builder, MsgBean.ChangeGroupNameMessageOrBuilder>(
                                (MsgBean.ChangeGroupNameMessage) realMsg_,
                                getParentForChildren(),
                                isClean());
                        realMsg_ = null;
                    }
                    realMsgCase_ = 10204;
                    onChanged();;
                    return changeGroupNameBuilder_;
                }

                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.ChangeGroupAnnouncementMessage, MsgBean.ChangeGroupAnnouncementMessage.Builder, MsgBean.ChangeGroupAnnouncementMessageOrBuilder> changeGroupAnnouncementBuilder_;
                /**
                 * <code>.ChangeGroupAnnouncementMessage change_group_announcement = 10205;</code>
                 */
                public MsgBean.ChangeGroupAnnouncementMessage getChangeGroupAnnouncement() {
                    if (changeGroupAnnouncementBuilder_ == null) {
                        if (realMsgCase_ == 10205) {
                            return (MsgBean.ChangeGroupAnnouncementMessage) realMsg_;
                        }
                        return MsgBean.ChangeGroupAnnouncementMessage.getDefaultInstance();
                    } else {
                        if (realMsgCase_ == 10205) {
                            return changeGroupAnnouncementBuilder_.getMessage();
                        }
                        return MsgBean.ChangeGroupAnnouncementMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.ChangeGroupAnnouncementMessage change_group_announcement = 10205;</code>
                 */
                public Builder setChangeGroupAnnouncement(MsgBean.ChangeGroupAnnouncementMessage value) {
                    if (changeGroupAnnouncementBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        realMsg_ = value;
                        onChanged();
                    } else {
                        changeGroupAnnouncementBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10205;
                    return this;
                }
                /**
                 * <code>.ChangeGroupAnnouncementMessage change_group_announcement = 10205;</code>
                 */
                public Builder setChangeGroupAnnouncement(
                        MsgBean.ChangeGroupAnnouncementMessage.Builder builderForValue) {
                    if (changeGroupAnnouncementBuilder_ == null) {
                        realMsg_ = builderForValue.build();
                        onChanged();
                    } else {
                        changeGroupAnnouncementBuilder_.setMessage(builderForValue.build());
                    }
                    realMsgCase_ = 10205;
                    return this;
                }
                /**
                 * <code>.ChangeGroupAnnouncementMessage change_group_announcement = 10205;</code>
                 */
                public Builder mergeChangeGroupAnnouncement(MsgBean.ChangeGroupAnnouncementMessage value) {
                    if (changeGroupAnnouncementBuilder_ == null) {
                        if (realMsgCase_ == 10205 &&
                                realMsg_ != MsgBean.ChangeGroupAnnouncementMessage.getDefaultInstance()) {
                            realMsg_ = MsgBean.ChangeGroupAnnouncementMessage.newBuilder((MsgBean.ChangeGroupAnnouncementMessage) realMsg_)
                                    .mergeFrom(value).buildPartial();
                        } else {
                            realMsg_ = value;
                        }
                        onChanged();
                    } else {
                        if (realMsgCase_ == 10205) {
                            changeGroupAnnouncementBuilder_.mergeFrom(value);
                        }
                        changeGroupAnnouncementBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10205;
                    return this;
                }
                /**
                 * <code>.ChangeGroupAnnouncementMessage change_group_announcement = 10205;</code>
                 */
                public Builder clearChangeGroupAnnouncement() {
                    if (changeGroupAnnouncementBuilder_ == null) {
                        if (realMsgCase_ == 10205) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                            onChanged();
                        }
                    } else {
                        if (realMsgCase_ == 10205) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                        }
                        changeGroupAnnouncementBuilder_.clear();
                    }
                    return this;
                }
                /**
                 * <code>.ChangeGroupAnnouncementMessage change_group_announcement = 10205;</code>
                 */
                public MsgBean.ChangeGroupAnnouncementMessage.Builder getChangeGroupAnnouncementBuilder() {
                    return getChangeGroupAnnouncementFieldBuilder().getBuilder();
                }
                /**
                 * <code>.ChangeGroupAnnouncementMessage change_group_announcement = 10205;</code>
                 */
                public MsgBean.ChangeGroupAnnouncementMessageOrBuilder getChangeGroupAnnouncementOrBuilder() {
                    if ((realMsgCase_ == 10205) && (changeGroupAnnouncementBuilder_ != null)) {
                        return changeGroupAnnouncementBuilder_.getMessageOrBuilder();
                    } else {
                        if (realMsgCase_ == 10205) {
                            return (MsgBean.ChangeGroupAnnouncementMessage) realMsg_;
                        }
                        return MsgBean.ChangeGroupAnnouncementMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.ChangeGroupAnnouncementMessage change_group_announcement = 10205;</code>
                 */
                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.ChangeGroupAnnouncementMessage, MsgBean.ChangeGroupAnnouncementMessage.Builder, MsgBean.ChangeGroupAnnouncementMessageOrBuilder>
                getChangeGroupAnnouncementFieldBuilder() {
                    if (changeGroupAnnouncementBuilder_ == null) {
                        if (!(realMsgCase_ == 10205)) {
                            realMsg_ = MsgBean.ChangeGroupAnnouncementMessage.getDefaultInstance();
                        }
                        changeGroupAnnouncementBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
                                MsgBean.ChangeGroupAnnouncementMessage, MsgBean.ChangeGroupAnnouncementMessage.Builder, MsgBean.ChangeGroupAnnouncementMessageOrBuilder>(
                                (MsgBean.ChangeGroupAnnouncementMessage) realMsg_,
                                getParentForChildren(),
                                isClean());
                        realMsg_ = null;
                    }
                    realMsgCase_ = 10205;
                    onChanged();;
                    return changeGroupAnnouncementBuilder_;
                }

                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.DestroyGroupMessage, MsgBean.DestroyGroupMessage.Builder, MsgBean.DestroyGroupMessageOrBuilder> destroyGroupBuilder_;
                /**
                 * <code>.DestroyGroupMessage destroy_group = 10206;</code>
                 */
                public MsgBean.DestroyGroupMessage getDestroyGroup() {
                    if (destroyGroupBuilder_ == null) {
                        if (realMsgCase_ == 10206) {
                            return (MsgBean.DestroyGroupMessage) realMsg_;
                        }
                        return MsgBean.DestroyGroupMessage.getDefaultInstance();
                    } else {
                        if (realMsgCase_ == 10206) {
                            return destroyGroupBuilder_.getMessage();
                        }
                        return MsgBean.DestroyGroupMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.DestroyGroupMessage destroy_group = 10206;</code>
                 */
                public Builder setDestroyGroup(MsgBean.DestroyGroupMessage value) {
                    if (destroyGroupBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        realMsg_ = value;
                        onChanged();
                    } else {
                        destroyGroupBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10206;
                    return this;
                }
                /**
                 * <code>.DestroyGroupMessage destroy_group = 10206;</code>
                 */
                public Builder setDestroyGroup(
                        MsgBean.DestroyGroupMessage.Builder builderForValue) {
                    if (destroyGroupBuilder_ == null) {
                        realMsg_ = builderForValue.build();
                        onChanged();
                    } else {
                        destroyGroupBuilder_.setMessage(builderForValue.build());
                    }
                    realMsgCase_ = 10206;
                    return this;
                }
                /**
                 * <code>.DestroyGroupMessage destroy_group = 10206;</code>
                 */
                public Builder mergeDestroyGroup(MsgBean.DestroyGroupMessage value) {
                    if (destroyGroupBuilder_ == null) {
                        if (realMsgCase_ == 10206 &&
                                realMsg_ != MsgBean.DestroyGroupMessage.getDefaultInstance()) {
                            realMsg_ = MsgBean.DestroyGroupMessage.newBuilder((MsgBean.DestroyGroupMessage) realMsg_)
                                    .mergeFrom(value).buildPartial();
                        } else {
                            realMsg_ = value;
                        }
                        onChanged();
                    } else {
                        if (realMsgCase_ == 10206) {
                            destroyGroupBuilder_.mergeFrom(value);
                        }
                        destroyGroupBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10206;
                    return this;
                }
                /**
                 * <code>.DestroyGroupMessage destroy_group = 10206;</code>
                 */
                public Builder clearDestroyGroup() {
                    if (destroyGroupBuilder_ == null) {
                        if (realMsgCase_ == 10206) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                            onChanged();
                        }
                    } else {
                        if (realMsgCase_ == 10206) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                        }
                        destroyGroupBuilder_.clear();
                    }
                    return this;
                }
                /**
                 * <code>.DestroyGroupMessage destroy_group = 10206;</code>
                 */
                public MsgBean.DestroyGroupMessage.Builder getDestroyGroupBuilder() {
                    return getDestroyGroupFieldBuilder().getBuilder();
                }
                /**
                 * <code>.DestroyGroupMessage destroy_group = 10206;</code>
                 */
                public MsgBean.DestroyGroupMessageOrBuilder getDestroyGroupOrBuilder() {
                    if ((realMsgCase_ == 10206) && (destroyGroupBuilder_ != null)) {
                        return destroyGroupBuilder_.getMessageOrBuilder();
                    } else {
                        if (realMsgCase_ == 10206) {
                            return (MsgBean.DestroyGroupMessage) realMsg_;
                        }
                        return MsgBean.DestroyGroupMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.DestroyGroupMessage destroy_group = 10206;</code>
                 */
                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.DestroyGroupMessage, MsgBean.DestroyGroupMessage.Builder, MsgBean.DestroyGroupMessageOrBuilder>
                getDestroyGroupFieldBuilder() {
                    if (destroyGroupBuilder_ == null) {
                        if (!(realMsgCase_ == 10206)) {
                            realMsg_ = MsgBean.DestroyGroupMessage.getDefaultInstance();
                        }
                        destroyGroupBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
                                MsgBean.DestroyGroupMessage, MsgBean.DestroyGroupMessage.Builder, MsgBean.DestroyGroupMessageOrBuilder>(
                                (MsgBean.DestroyGroupMessage) realMsg_,
                                getParentForChildren(),
                                isClean());
                        realMsg_ = null;
                    }
                    realMsgCase_ = 10206;
                    onChanged();;
                    return destroyGroupBuilder_;
                }

                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.OutGroupMessage, MsgBean.OutGroupMessage.Builder, MsgBean.OutGroupMessageOrBuilder> outGroupBuilder_;
                /**
                 * <code>.OutGroupMessage out_group = 10210;</code>
                 */
                public MsgBean.OutGroupMessage getOutGroup() {
                    if (outGroupBuilder_ == null) {
                        if (realMsgCase_ == 10210) {
                            return (MsgBean.OutGroupMessage) realMsg_;
                        }
                        return MsgBean.OutGroupMessage.getDefaultInstance();
                    } else {
                        if (realMsgCase_ == 10210) {
                            return outGroupBuilder_.getMessage();
                        }
                        return MsgBean.OutGroupMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.OutGroupMessage out_group = 10210;</code>
                 */
                public Builder setOutGroup(MsgBean.OutGroupMessage value) {
                    if (outGroupBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        realMsg_ = value;
                        onChanged();
                    } else {
                        outGroupBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10210;
                    return this;
                }
                /**
                 * <code>.OutGroupMessage out_group = 10210;</code>
                 */
                public Builder setOutGroup(
                        MsgBean.OutGroupMessage.Builder builderForValue) {
                    if (outGroupBuilder_ == null) {
                        realMsg_ = builderForValue.build();
                        onChanged();
                    } else {
                        outGroupBuilder_.setMessage(builderForValue.build());
                    }
                    realMsgCase_ = 10210;
                    return this;
                }
                /**
                 * <code>.OutGroupMessage out_group = 10210;</code>
                 */
                public Builder mergeOutGroup(MsgBean.OutGroupMessage value) {
                    if (outGroupBuilder_ == null) {
                        if (realMsgCase_ == 10210 &&
                                realMsg_ != MsgBean.OutGroupMessage.getDefaultInstance()) {
                            realMsg_ = MsgBean.OutGroupMessage.newBuilder((MsgBean.OutGroupMessage) realMsg_)
                                    .mergeFrom(value).buildPartial();
                        } else {
                            realMsg_ = value;
                        }
                        onChanged();
                    } else {
                        if (realMsgCase_ == 10210) {
                            outGroupBuilder_.mergeFrom(value);
                        }
                        outGroupBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10210;
                    return this;
                }
                /**
                 * <code>.OutGroupMessage out_group = 10210;</code>
                 */
                public Builder clearOutGroup() {
                    if (outGroupBuilder_ == null) {
                        if (realMsgCase_ == 10210) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                            onChanged();
                        }
                    } else {
                        if (realMsgCase_ == 10210) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                        }
                        outGroupBuilder_.clear();
                    }
                    return this;
                }
                /**
                 * <code>.OutGroupMessage out_group = 10210;</code>
                 */
                public MsgBean.OutGroupMessage.Builder getOutGroupBuilder() {
                    return getOutGroupFieldBuilder().getBuilder();
                }
                /**
                 * <code>.OutGroupMessage out_group = 10210;</code>
                 */
                public MsgBean.OutGroupMessageOrBuilder getOutGroupOrBuilder() {
                    if ((realMsgCase_ == 10210) && (outGroupBuilder_ != null)) {
                        return outGroupBuilder_.getMessageOrBuilder();
                    } else {
                        if (realMsgCase_ == 10210) {
                            return (MsgBean.OutGroupMessage) realMsg_;
                        }
                        return MsgBean.OutGroupMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.OutGroupMessage out_group = 10210;</code>
                 */
                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.OutGroupMessage, MsgBean.OutGroupMessage.Builder, MsgBean.OutGroupMessageOrBuilder>
                getOutGroupFieldBuilder() {
                    if (outGroupBuilder_ == null) {
                        if (!(realMsgCase_ == 10210)) {
                            realMsg_ = MsgBean.OutGroupMessage.getDefaultInstance();
                        }
                        outGroupBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
                                MsgBean.OutGroupMessage, MsgBean.OutGroupMessage.Builder, MsgBean.OutGroupMessageOrBuilder>(
                                (MsgBean.OutGroupMessage) realMsg_,
                                getParentForChildren(),
                                isClean());
                        realMsg_ = null;
                    }
                    realMsgCase_ = 10210;
                    onChanged();;
                    return outGroupBuilder_;
                }

                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.ConflictMessage, MsgBean.ConflictMessage.Builder, MsgBean.ConflictMessageOrBuilder> conflictBuilder_;
                /**
                 * <code>.ConflictMessage conflict = 10300;</code>
                 */
                public MsgBean.ConflictMessage getConflict() {
                    if (conflictBuilder_ == null) {
                        if (realMsgCase_ == 10300) {
                            return (MsgBean.ConflictMessage) realMsg_;
                        }
                        return MsgBean.ConflictMessage.getDefaultInstance();
                    } else {
                        if (realMsgCase_ == 10300) {
                            return conflictBuilder_.getMessage();
                        }
                        return MsgBean.ConflictMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.ConflictMessage conflict = 10300;</code>
                 */
                public Builder setConflict(MsgBean.ConflictMessage value) {
                    if (conflictBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        realMsg_ = value;
                        onChanged();
                    } else {
                        conflictBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10300;
                    return this;
                }
                /**
                 * <code>.ConflictMessage conflict = 10300;</code>
                 */
                public Builder setConflict(
                        MsgBean.ConflictMessage.Builder builderForValue) {
                    if (conflictBuilder_ == null) {
                        realMsg_ = builderForValue.build();
                        onChanged();
                    } else {
                        conflictBuilder_.setMessage(builderForValue.build());
                    }
                    realMsgCase_ = 10300;
                    return this;
                }
                /**
                 * <code>.ConflictMessage conflict = 10300;</code>
                 */
                public Builder mergeConflict(MsgBean.ConflictMessage value) {
                    if (conflictBuilder_ == null) {
                        if (realMsgCase_ == 10300 &&
                                realMsg_ != MsgBean.ConflictMessage.getDefaultInstance()) {
                            realMsg_ = MsgBean.ConflictMessage.newBuilder((MsgBean.ConflictMessage) realMsg_)
                                    .mergeFrom(value).buildPartial();
                        } else {
                            realMsg_ = value;
                        }
                        onChanged();
                    } else {
                        if (realMsgCase_ == 10300) {
                            conflictBuilder_.mergeFrom(value);
                        }
                        conflictBuilder_.setMessage(value);
                    }
                    realMsgCase_ = 10300;
                    return this;
                }
                /**
                 * <code>.ConflictMessage conflict = 10300;</code>
                 */
                public Builder clearConflict() {
                    if (conflictBuilder_ == null) {
                        if (realMsgCase_ == 10300) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                            onChanged();
                        }
                    } else {
                        if (realMsgCase_ == 10300) {
                            realMsgCase_ = 0;
                            realMsg_ = null;
                        }
                        conflictBuilder_.clear();
                    }
                    return this;
                }
                /**
                 * <code>.ConflictMessage conflict = 10300;</code>
                 */
                public MsgBean.ConflictMessage.Builder getConflictBuilder() {
                    return getConflictFieldBuilder().getBuilder();
                }
                /**
                 * <code>.ConflictMessage conflict = 10300;</code>
                 */
                public MsgBean.ConflictMessageOrBuilder getConflictOrBuilder() {
                    if ((realMsgCase_ == 10300) && (conflictBuilder_ != null)) {
                        return conflictBuilder_.getMessageOrBuilder();
                    } else {
                        if (realMsgCase_ == 10300) {
                            return (MsgBean.ConflictMessage) realMsg_;
                        }
                        return MsgBean.ConflictMessage.getDefaultInstance();
                    }
                }
                /**
                 * <code>.ConflictMessage conflict = 10300;</code>
                 */
                private com.google.protobuf.SingleFieldBuilderV3<
                        MsgBean.ConflictMessage, MsgBean.ConflictMessage.Builder, MsgBean.ConflictMessageOrBuilder>
                getConflictFieldBuilder() {
                    if (conflictBuilder_ == null) {
                        if (!(realMsgCase_ == 10300)) {
                            realMsg_ = MsgBean.ConflictMessage.getDefaultInstance();
                        }
                        conflictBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
                                MsgBean.ConflictMessage, MsgBean.ConflictMessage.Builder, MsgBean.ConflictMessageOrBuilder>(
                                (MsgBean.ConflictMessage) realMsg_,
                                getParentForChildren(),
                                isClean());
                        realMsg_ = null;
                    }
                    realMsgCase_ = 10300;
                    onChanged();;
                    return conflictBuilder_;
                }
                public final Builder setUnknownFields(
                        final com.google.protobuf.UnknownFieldSet unknownFields) {
                    return this;
                }

                public final Builder mergeUnknownFields(
                        final com.google.protobuf.UnknownFieldSet unknownFields) {
                    return this;
                }


                // @@protoc_insertion_point(builder_scope:UniversalMessage.WrapMessage)
            }

            // @@protoc_insertion_point(class_scope:UniversalMessage.WrapMessage)
            private static final MsgBean.UniversalMessage.WrapMessage DEFAULT_INSTANCE;
            static {
                DEFAULT_INSTANCE = new MsgBean.UniversalMessage.WrapMessage();
            }

            public static MsgBean.UniversalMessage.WrapMessage getDefaultInstance() {
                return DEFAULT_INSTANCE;
            }

            private static final com.google.protobuf.Parser<WrapMessage>
                    PARSER = new com.google.protobuf.AbstractParser<WrapMessage>() {
                public WrapMessage parsePartialFrom(
                        com.google.protobuf.CodedInputStream input,
                        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                        throws com.google.protobuf.InvalidProtocolBufferException {
                    return new WrapMessage(input, extensionRegistry);
                }
            };

            public static com.google.protobuf.Parser<WrapMessage> parser() {
                return PARSER;
            }

            @java.lang.Override
            public com.google.protobuf.Parser<WrapMessage> getParserForType() {
                return PARSER;
            }

            public MsgBean.UniversalMessage.WrapMessage getDefaultInstanceForType() {
                return DEFAULT_INSTANCE;
            }

        }

        private int bitField0_;
        public static final int REQUEST_ID_FIELD_NUMBER = 1;
        private volatile java.lang.Object requestId_;
        /**
         * <pre>
         * 请求id
         * </pre>
         *
         * <code>string request_id = 1;</code>
         */
        public java.lang.String getRequestId() {
            java.lang.Object ref = requestId_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                requestId_ = s;
                return s;
            }
        }
        /**
         * <pre>
         * 请求id
         * </pre>
         *
         * <code>string request_id = 1;</code>
         */
        public com.google.protobuf.ByteString
        getRequestIdBytes() {
            java.lang.Object ref = requestId_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                requestId_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        public static final int TO_UID_FIELD_NUMBER = 2;
        private long toUid_;
        /**
         * <code>uint64 to_uid = 2;</code>
         */
        public long getToUid() {
            return toUid_;
        }

        public static final int WRAPMSG_FIELD_NUMBER = 10001;
        private java.util.List<MsgBean.UniversalMessage.WrapMessage> wrapMsg_;
        /**
         * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
         */
        public java.util.List<MsgBean.UniversalMessage.WrapMessage> getWrapMsgList() {
            return wrapMsg_;
        }
        /**
         * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
         */
        public java.util.List<? extends MsgBean.UniversalMessage.WrapMessageOrBuilder>
        getWrapMsgOrBuilderList() {
            return wrapMsg_;
        }
        /**
         * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
         */
        public int getWrapMsgCount() {
            return wrapMsg_.size();
        }
        /**
         * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
         */
        public MsgBean.UniversalMessage.WrapMessage getWrapMsg(int index) {
            return wrapMsg_.get(index);
        }
        /**
         * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
         */
        public MsgBean.UniversalMessage.WrapMessageOrBuilder getWrapMsgOrBuilder(
                int index) {
            return wrapMsg_.get(index);
        }

        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            if (!getRequestIdBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 1, requestId_);
            }
            if (toUid_ != 0L) {
                output.writeUInt64(2, toUid_);
            }
            for (int i = 0; i < wrapMsg_.size(); i++) {
                output.writeMessage(10001, wrapMsg_.get(i));
            }
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (!getRequestIdBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, requestId_);
            }
            if (toUid_ != 0L) {
                size += com.google.protobuf.CodedOutputStream
                        .computeUInt64Size(2, toUid_);
            }
            for (int i = 0; i < wrapMsg_.size(); i++) {
                size += com.google.protobuf.CodedOutputStream
                        .computeMessageSize(10001, wrapMsg_.get(i));
            }
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MsgBean.UniversalMessage)) {
                return super.equals(obj);
            }
            MsgBean.UniversalMessage other = (MsgBean.UniversalMessage) obj;

            boolean result = true;
            result = result && getRequestId()
                    .equals(other.getRequestId());
            result = result && (getToUid()
                    == other.getToUid());
            result = result && getWrapMsgList()
                    .equals(other.getWrapMsgList());
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (37 * hash) + REQUEST_ID_FIELD_NUMBER;
            hash = (53 * hash) + getRequestId().hashCode();
            hash = (37 * hash) + TO_UID_FIELD_NUMBER;
            hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
                    getToUid());
            if (getWrapMsgCount() > 0) {
                hash = (37 * hash) + WRAPMSG_FIELD_NUMBER;
                hash = (53 * hash) + getWrapMsgList().hashCode();
            }
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static MsgBean.UniversalMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.UniversalMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.UniversalMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.UniversalMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.UniversalMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }
        public static MsgBean.UniversalMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MsgBean.UniversalMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.UniversalMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.UniversalMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }
        public static MsgBean.UniversalMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MsgBean.UniversalMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }
        public static MsgBean.UniversalMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MsgBean.UniversalMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        /**
         * <pre>
         * 普通消息
         * </pre>
         *
         * Protobuf type {@code UniversalMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:UniversalMessage)
                MsgBean.UniversalMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return MsgBean.internal_static_UniversalMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return MsgBean.internal_static_UniversalMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                MsgBean.UniversalMessage.class, MsgBean.UniversalMessage.Builder.class);
            }

            // Construct using MsgBean.UniversalMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                    getWrapMsgFieldBuilder();
                }
            }
            public Builder clear() {
                super.clear();
                requestId_ = "";

                toUid_ = 0L;

                if (wrapMsgBuilder_ == null) {
                    wrapMsg_ = java.util.Collections.emptyList();
                    bitField0_ = (bitField0_ & ~0x00000004);
                } else {
                    wrapMsgBuilder_.clear();
                }
                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return MsgBean.internal_static_UniversalMessage_descriptor;
            }

            public MsgBean.UniversalMessage getDefaultInstanceForType() {
                return MsgBean.UniversalMessage.getDefaultInstance();
            }

            public MsgBean.UniversalMessage build() {
                MsgBean.UniversalMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public MsgBean.UniversalMessage buildPartial() {
                MsgBean.UniversalMessage result = new MsgBean.UniversalMessage(this);
                int from_bitField0_ = bitField0_;
                int to_bitField0_ = 0;
                result.requestId_ = requestId_;
                result.toUid_ = toUid_;
                if (wrapMsgBuilder_ == null) {
                    if (((bitField0_ & 0x00000004) == 0x00000004)) {
                        wrapMsg_ = java.util.Collections.unmodifiableList(wrapMsg_);
                        bitField0_ = (bitField0_ & ~0x00000004);
                    }
                    result.wrapMsg_ = wrapMsg_;
                } else {
                    result.wrapMsg_ = wrapMsgBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof MsgBean.UniversalMessage) {
                    return mergeFrom((MsgBean.UniversalMessage)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MsgBean.UniversalMessage other) {
                if (other == MsgBean.UniversalMessage.getDefaultInstance()) return this;
                if (!other.getRequestId().isEmpty()) {
                    requestId_ = other.requestId_;
                    onChanged();
                }
                if (other.getToUid() != 0L) {
                    setToUid(other.getToUid());
                }
                if (wrapMsgBuilder_ == null) {
                    if (!other.wrapMsg_.isEmpty()) {
                        if (wrapMsg_.isEmpty()) {
                            wrapMsg_ = other.wrapMsg_;
                            bitField0_ = (bitField0_ & ~0x00000004);
                        } else {
                            ensureWrapMsgIsMutable();
                            wrapMsg_.addAll(other.wrapMsg_);
                        }
                        onChanged();
                    }
                } else {
                    if (!other.wrapMsg_.isEmpty()) {
                        if (wrapMsgBuilder_.isEmpty()) {
                            wrapMsgBuilder_.dispose();
                            wrapMsgBuilder_ = null;
                            wrapMsg_ = other.wrapMsg_;
                            bitField0_ = (bitField0_ & ~0x00000004);
                            wrapMsgBuilder_ =
                                    com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                                            getWrapMsgFieldBuilder() : null;
                        } else {
                            wrapMsgBuilder_.addAllMessages(other.wrapMsg_);
                        }
                    }
                }
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                MsgBean.UniversalMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (MsgBean.UniversalMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            private int bitField0_;

            private java.lang.Object requestId_ = "";
            /**
             * <pre>
             * 请求id
             * </pre>
             *
             * <code>string request_id = 1;</code>
             */
            public java.lang.String getRequestId() {
                java.lang.Object ref = requestId_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    requestId_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }
            /**
             * <pre>
             * 请求id
             * </pre>
             *
             * <code>string request_id = 1;</code>
             */
            public com.google.protobuf.ByteString
            getRequestIdBytes() {
                java.lang.Object ref = requestId_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    requestId_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }
            /**
             * <pre>
             * 请求id
             * </pre>
             *
             * <code>string request_id = 1;</code>
             */
            public Builder setRequestId(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                requestId_ = value;
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 请求id
             * </pre>
             *
             * <code>string request_id = 1;</code>
             */
            public Builder clearRequestId() {

                requestId_ = getDefaultInstance().getRequestId();
                onChanged();
                return this;
            }
            /**
             * <pre>
             * 请求id
             * </pre>
             *
             * <code>string request_id = 1;</code>
             */
            public Builder setRequestIdBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                requestId_ = value;
                onChanged();
                return this;
            }

            private long toUid_ ;
            /**
             * <code>uint64 to_uid = 2;</code>
             */
            public long getToUid() {
                return toUid_;
            }
            /**
             * <code>uint64 to_uid = 2;</code>
             */
            public Builder setToUid(long value) {

                toUid_ = value;
                onChanged();
                return this;
            }
            /**
             * <code>uint64 to_uid = 2;</code>
             */
            public Builder clearToUid() {

                toUid_ = 0L;
                onChanged();
                return this;
            }

            private java.util.List<MsgBean.UniversalMessage.WrapMessage> wrapMsg_ =
                    java.util.Collections.emptyList();
            private void ensureWrapMsgIsMutable() {
                if (!((bitField0_ & 0x00000004) == 0x00000004)) {
                    wrapMsg_ = new java.util.ArrayList<MsgBean.UniversalMessage.WrapMessage>(wrapMsg_);
                    bitField0_ |= 0x00000004;
                }
            }

            private com.google.protobuf.RepeatedFieldBuilderV3<
                    MsgBean.UniversalMessage.WrapMessage, MsgBean.UniversalMessage.WrapMessage.Builder, MsgBean.UniversalMessage.WrapMessageOrBuilder> wrapMsgBuilder_;

            /**
             * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
             */
            public java.util.List<MsgBean.UniversalMessage.WrapMessage> getWrapMsgList() {
                if (wrapMsgBuilder_ == null) {
                    return java.util.Collections.unmodifiableList(wrapMsg_);
                } else {
                    return wrapMsgBuilder_.getMessageList();
                }
            }
            /**
             * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
             */
            public int getWrapMsgCount() {
                if (wrapMsgBuilder_ == null) {
                    return wrapMsg_.size();
                } else {
                    return wrapMsgBuilder_.getCount();
                }
            }
            /**
             * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
             */
            public MsgBean.UniversalMessage.WrapMessage getWrapMsg(int index) {
                if (wrapMsgBuilder_ == null) {
                    return wrapMsg_.get(index);
                } else {
                    return wrapMsgBuilder_.getMessage(index);
                }
            }
            /**
             * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
             */
            public Builder setWrapMsg(
                    int index, MsgBean.UniversalMessage.WrapMessage value) {
                if (wrapMsgBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureWrapMsgIsMutable();
                    wrapMsg_.set(index, value);
                    onChanged();
                } else {
                    wrapMsgBuilder_.setMessage(index, value);
                }
                return this;
            }
            /**
             * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
             */
            public Builder setWrapMsg(
                    int index, MsgBean.UniversalMessage.WrapMessage.Builder builderForValue) {
                if (wrapMsgBuilder_ == null) {
                    ensureWrapMsgIsMutable();
                    wrapMsg_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    wrapMsgBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            /**
             * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
             */
            public Builder addWrapMsg(MsgBean.UniversalMessage.WrapMessage value) {
                if (wrapMsgBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureWrapMsgIsMutable();
                    wrapMsg_.add(value);
                    onChanged();
                } else {
                    wrapMsgBuilder_.addMessage(value);
                }
                return this;
            }
            /**
             * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
             */
            public Builder addWrapMsg(
                    int index, MsgBean.UniversalMessage.WrapMessage value) {
                if (wrapMsgBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureWrapMsgIsMutable();
                    wrapMsg_.add(index, value);
                    onChanged();
                } else {
                    wrapMsgBuilder_.addMessage(index, value);
                }
                return this;
            }
            /**
             * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
             */
            public Builder addWrapMsg(
                    MsgBean.UniversalMessage.WrapMessage.Builder builderForValue) {
                if (wrapMsgBuilder_ == null) {
                    ensureWrapMsgIsMutable();
                    wrapMsg_.add(builderForValue.build());
                    onChanged();
                } else {
                    wrapMsgBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            /**
             * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
             */
            public Builder addWrapMsg(
                    int index, MsgBean.UniversalMessage.WrapMessage.Builder builderForValue) {
                if (wrapMsgBuilder_ == null) {
                    ensureWrapMsgIsMutable();
                    wrapMsg_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    wrapMsgBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            /**
             * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
             */
            public Builder addAllWrapMsg(
                    java.lang.Iterable<? extends MsgBean.UniversalMessage.WrapMessage> values) {
                if (wrapMsgBuilder_ == null) {
                    ensureWrapMsgIsMutable();
                    com.google.protobuf.AbstractMessageLite.Builder.addAll(
                            values, wrapMsg_);
                    onChanged();
                } else {
                    wrapMsgBuilder_.addAllMessages(values);
                }
                return this;
            }
            /**
             * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
             */
            public Builder clearWrapMsg() {
                if (wrapMsgBuilder_ == null) {
                    wrapMsg_ = java.util.Collections.emptyList();
                    bitField0_ = (bitField0_ & ~0x00000004);
                    onChanged();
                } else {
                    wrapMsgBuilder_.clear();
                }
                return this;
            }
            /**
             * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
             */
            public Builder removeWrapMsg(int index) {
                if (wrapMsgBuilder_ == null) {
                    ensureWrapMsgIsMutable();
                    wrapMsg_.remove(index);
                    onChanged();
                } else {
                    wrapMsgBuilder_.remove(index);
                }
                return this;
            }
            /**
             * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
             */
            public MsgBean.UniversalMessage.WrapMessage.Builder getWrapMsgBuilder(
                    int index) {
                return getWrapMsgFieldBuilder().getBuilder(index);
            }
            /**
             * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
             */
            public MsgBean.UniversalMessage.WrapMessageOrBuilder getWrapMsgOrBuilder(
                    int index) {
                if (wrapMsgBuilder_ == null) {
                    return wrapMsg_.get(index);  } else {
                    return wrapMsgBuilder_.getMessageOrBuilder(index);
                }
            }
            /**
             * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
             */
            public java.util.List<? extends MsgBean.UniversalMessage.WrapMessageOrBuilder>
            getWrapMsgOrBuilderList() {
                if (wrapMsgBuilder_ != null) {
                    return wrapMsgBuilder_.getMessageOrBuilderList();
                } else {
                    return java.util.Collections.unmodifiableList(wrapMsg_);
                }
            }
            /**
             * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
             */
            public MsgBean.UniversalMessage.WrapMessage.Builder addWrapMsgBuilder() {
                return getWrapMsgFieldBuilder().addBuilder(
                        MsgBean.UniversalMessage.WrapMessage.getDefaultInstance());
            }
            /**
             * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
             */
            public MsgBean.UniversalMessage.WrapMessage.Builder addWrapMsgBuilder(
                    int index) {
                return getWrapMsgFieldBuilder().addBuilder(
                        index, MsgBean.UniversalMessage.WrapMessage.getDefaultInstance());
            }
            /**
             * <code>repeated .UniversalMessage.WrapMessage wrapMsg = 10001;</code>
             */
            public java.util.List<MsgBean.UniversalMessage.WrapMessage.Builder>
            getWrapMsgBuilderList() {
                return getWrapMsgFieldBuilder().getBuilderList();
            }
            private com.google.protobuf.RepeatedFieldBuilderV3<
                    MsgBean.UniversalMessage.WrapMessage, MsgBean.UniversalMessage.WrapMessage.Builder, MsgBean.UniversalMessage.WrapMessageOrBuilder>
            getWrapMsgFieldBuilder() {
                if (wrapMsgBuilder_ == null) {
                    wrapMsgBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
                            MsgBean.UniversalMessage.WrapMessage, MsgBean.UniversalMessage.WrapMessage.Builder, MsgBean.UniversalMessage.WrapMessageOrBuilder>(
                            wrapMsg_,
                            ((bitField0_ & 0x00000004) == 0x00000004),
                            getParentForChildren(),
                            isClean());
                    wrapMsg_ = null;
                }
                return wrapMsgBuilder_;
            }
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:UniversalMessage)
        }

        // @@protoc_insertion_point(class_scope:UniversalMessage)
        private static final MsgBean.UniversalMessage DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new MsgBean.UniversalMessage();
        }

        public static MsgBean.UniversalMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<UniversalMessage>
                PARSER = new com.google.protobuf.AbstractParser<UniversalMessage>() {
            public UniversalMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new UniversalMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<UniversalMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<UniversalMessage> getParserForType() {
            return PARSER;
        }

        public MsgBean.UniversalMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_ChatMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_ChatMessage_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_ImageMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_ImageMessage_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_RedEnvelopeMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_RedEnvelopeMessage_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_ReceiveRedEnvelopeMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_ReceiveRedEnvelopeMessage_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_TransferMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_TransferMessage_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_StampMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_StampMessage_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_BusinessCardMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_BusinessCardMessage_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_RequestFriendMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_RequestFriendMessage_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_AcceptBeFriendsMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_AcceptBeFriendsMessage_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_RemoveFriendMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_RemoveFriendMessage_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_GroupNoticeMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_GroupNoticeMessage_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_InviteJoinGroupMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_InviteJoinGroupMessage_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_RequestGroupMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_RequestGroupMessage_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_AcceptBeGroupMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_AcceptBeGroupMessage_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_RemoveGroupMemberMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_RemoveGroupMemberMessage_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_ChangeGroupMasterMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_ChangeGroupMasterMessage_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_ChangeGroupNameMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_ChangeGroupNameMessage_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_ChangeGroupAnnouncementMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_ChangeGroupAnnouncementMessage_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_DestroyGroupMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_DestroyGroupMessage_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_ConflictMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_ConflictMessage_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_OutGroupMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_OutGroupMessage_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_AckMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_AckMessage_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_AuthRequestMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_AuthRequestMessage_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_AuthResponseMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_AuthResponseMessage_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_UniversalMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_UniversalMessage_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_UniversalMessage_WrapMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_UniversalMessage_WrapMessage_fieldAccessorTable;

    public static com.google.protobuf.Descriptors.FileDescriptor
    getDescriptor() {
        return descriptor;
    }
    private static  com.google.protobuf.Descriptors.FileDescriptor
            descriptor;
    static {
        java.lang.String[] descriptorData = {
                "\n\rMsgBean.proto\"\032\n\013ChatMessage\022\013\n\003msg\030\001 " +
                        "\001(\t\"\033\n\014ImageMessage\022\013\n\003url\030\001 \001(\t\"\360\001\n\022Red" +
                        "EnvelopeMessage\022\n\n\002id\030\001 \001(\t\0224\n\007re_type\030\002" +
                        " \001(\0162#.RedEnvelopeMessage.RedEnvelopeTyp" +
                        "e\022\017\n\007comment\030\003 \001(\t\0223\n\005style\030\004 \001(\0162$.RedE" +
                        "nvelopeMessage.RedEnvelopeStyle\"(\n\017RedEn" +
                        "velopeType\022\n\n\006ALIPAY\020\000\022\t\n\005MFPAY\020\001\"(\n\020Red" +
                        "EnvelopeStyle\022\n\n\006NORMAL\020\000\022\010\n\004LUCK\020\001\"\'\n\031R" +
                        "eceiveRedEnvelopeMessage\022\n\n\002id\030\001 \001(\t\"J\n\017" +
                        "TransferMessage\022\n\n\002id\030\001 \001(\t\022\032\n\022transacti",
                "on_amount\030\002 \001(\005\022\017\n\007comment\030\003 \001(\t\"\037\n\014Stam" +
                        "pMessage\022\017\n\007comment\030\001 \001(\t\"U\n\023BusinessCar" +
                        "dMessage\022\013\n\003uid\030\001 \001(\004\022\016\n\006avatar\030\002 \001(\t\022\020\n" +
                        "\010nickname\030\003 \001(\t\022\017\n\007comment\030\004 \001(\t\"&\n\024Requ" +
                        "estFriendMessage\022\016\n\006say_hi\030\001 \001(\t\"\030\n\026Acce" +
                        "ptBeFriendsMessage\"\025\n\023RemoveFriendMessag" +
                        "e\"C\n\022GroupNoticeMessage\022\013\n\003uid\030\001 \001(\004\022\016\n\006" +
                        "avatar\030\002 \001(\t\022\020\n\010nickname\030\003 \001(\t\"E\n\026Invite" +
                        "JoinGroupMessage\022+\n\016notice_message\030\001 \001(\013" +
                        "2\023.GroupNoticeMessage\"B\n\023RequestGroupMes",
                "sage\022+\n\016notice_message\030\001 \003(\0132\023.GroupNoti" +
                        "ceMessage\"C\n\024AcceptBeGroupMessage\022+\n\016not" +
                        "ice_message\030\001 \003(\0132\023.GroupNoticeMessage\"E" +
                        "\n\030RemoveGroupMemberMessage\022\013\n\003gid\030\001 \001(\t\022" +
                        "\016\n\006avatar\030\002 \001(\t\022\014\n\004name\030\003 \001(\t\"\'\n\030ChangeG" +
                        "roupMasterMessage\022\013\n\003uid\030\001 \001(\004\"&\n\026Change" +
                        "GroupNameMessage\022\014\n\004name\030\001 \001(\t\"6\n\036Change" +
                        "GroupAnnouncementMessage\022\024\n\014announcement" +
                        "\030\001 \001(\t\"@\n\023DestroyGroupMessage\022\013\n\003uid\030\001 \003" +
                        "(\004\022\016\n\006avatar\030\002 \001(\t\022\014\n\004name\030\003 \001(\t\"\021\n\017Conf",
                "lictMessage\"\036\n\017OutGroupMessage\022\013\n\003gid\030\001 " +
                        "\001(\t\"e\n\nAckMessage\022 \n\013reject_type\030\001 \001(\0162\013" +
                        ".RejectType\022\022\n\nrequest_id\030\002 \001(\t\022\016\n\006msg_i" +
                        "d\030\003 \003(\t\022\021\n\ttimestamp\030\004 \001(\004\"*\n\022AuthReques" +
                        "tMessage\022\024\n\014access_token\030\001 \001(\t\"\'\n\023AuthRe" +
                        "sponseMessage\022\020\n\010accepted\030\001 \001(\021\"\231\t\n\020Univ" +
                        "ersalMessage\022\022\n\nrequest_id\030\001 \001(\t\022\016\n\006to_u" +
                        "id\030\002 \001(\004\022/\n\007wrapMsg\030\221N \003(\0132\035.UniversalMe" +
                        "ssage.WrapMessage\032\257\010\n\013WrapMessage\022\021\n\ttim" +
                        "estamp\030\001 \001(\004\022\036\n\010msg_type\030\002 \001(\0162\014.Message",
                "Type\022\016\n\006msg_id\030\003 \001(\t\022\020\n\010from_uid\030\004 \001(\004\022\013" +
                        "\n\003gid\030\005 \001(\t\022\020\n\010nickname\030\006 \001(\t\022\016\n\006avatar\030" +
                        "\007 \001(\t\022\035\n\004chat\030\220N \001(\0132\014.ChatMessageH\000\022\037\n\005" +
                        "image\030\221N \001(\0132\r.ImageMessageH\000\022,\n\014red_env" +
                        "elope\030\222N \001(\0132\023.RedEnvelopeMessageH\000\022;\n\024r" +
                        "eceive_red_envelope\030\223N \001(\0132\032.ReceiveRedE" +
                        "nvelopeMessageH\000\022%\n\010transfer\030\224N \001(\0132\020.Tr" +
                        "ansferMessageH\000\022\037\n\005stamp\030\225N \001(\0132\r.StampM" +
                        "essageH\000\022.\n\rbusiness_card\030\226N \001(\0132\024.Busin" +
                        "essCardMessageH\000\0220\n\016request_friend\030\364N \001(",
                "\0132\025.RequestFriendMessageH\000\0225\n\021accept_be_" +
                        "friends\030\365N \001(\0132\027.AcceptBeFriendsMessageH" +
                        "\000\022.\n\rremove_friend\030\366N \001(\0132\024.RemoveFriend" +
                        "MessageH\000\022.\n\rrequest_group\030\330O \001(\0132\024.Requ" +
                        "estGroupMessageH\000\0221\n\017accept_be_group\030\331O " +
                        "\001(\0132\025.AcceptBeGroupMessageH\000\0229\n\023remove_g" +
                        "roup_member\030\332O \001(\0132\031.RemoveGroupMemberMe" +
                        "ssageH\000\0229\n\023change_group_master\030\333O \001(\0132\031." +
                        "ChangeGroupMasterMessageH\000\0225\n\021change_gro" +
                        "up_name\030\334O \001(\0132\027.ChangeGroupNameMessageH",
                "\000\022E\n\031change_group_announcement\030\335O \001(\0132\037." +
                        "ChangeGroupAnnouncementMessageH\000\022.\n\rdest" +
                        "roy_group\030\336O \001(\0132\024.DestroyGroupMessageH\000" +
                        "\022&\n\tout_group\030\342O \001(\0132\020.OutGroupMessageH\000" +
                        "\022%\n\010conflict\030\274P \001(\0132\020.ConflictMessageH\000B" +
                        "\n\n\010real_msg*\373\002\n\013MessageType\022\010\n\004CHAT\020\000\022\t\n" +
                        "\005IMAGE\020\001\022\021\n\rRED_ENVELOPER\020\002\022\031\n\025RECEIVE_R" +
                        "ED_ENVELOPER\020\003\022\014\n\010TRANSFER\020\004\022\t\n\005STAMP\020\005\022" +
                        "\021\n\rBUSINESS_CARD\020\006\022\022\n\016REQUEST_FRIEND\020d\022\025" +
                        "\n\021ACCEPT_BE_FRIENDS\020e\022\021\n\rREMOVE_FRIEND\020f",
                "\022\021\n\rREQUEST_GROUP\020g\022\023\n\017ACCEPT_BE_GROUP\020h" +
                        "\022\027\n\023REMOVE_GROUP_MEMBER\020i\022\027\n\023CHANGE_GROU" +
                        "P_MASTER\020j\022\025\n\021CHANGE_GROUP_NAME\020k\022\035\n\031CHA" +
                        "NGE_GROUP_ANNOUNCEMENT\020l\022\021\n\rDESTROY_GROU" +
                        "P\020m\022\r\n\tOUT_GROUP\020n\022\r\n\010CONFLICT\020\310\001*b\n\nRej" +
                        "ectType\022\014\n\010ACCEPTED\020\000\022\037\n\033NOT_FRIENDS_OR_" +
                        "GROUP_MEMBER\020\001\022\014\n\010NO_SPACE\020\010\022\027\n\023SERVICE_" +
                        "UNAVAILABLE\020db\006proto3"
        };
        com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
                new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
                    public com.google.protobuf.ExtensionRegistry assignDescriptors(
                            com.google.protobuf.Descriptors.FileDescriptor root) {
                        descriptor = root;
                        return null;
                    }
                };
        com.google.protobuf.Descriptors.FileDescriptor
                .internalBuildGeneratedFileFrom(descriptorData,
                        new com.google.protobuf.Descriptors.FileDescriptor[] {
                        }, assigner);
        internal_static_ChatMessage_descriptor =
                getDescriptor().getMessageTypes().get(0);
        internal_static_ChatMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_ChatMessage_descriptor,
                new java.lang.String[] { "Msg", });
        internal_static_ImageMessage_descriptor =
                getDescriptor().getMessageTypes().get(1);
        internal_static_ImageMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_ImageMessage_descriptor,
                new java.lang.String[] { "Url", });
        internal_static_RedEnvelopeMessage_descriptor =
                getDescriptor().getMessageTypes().get(2);
        internal_static_RedEnvelopeMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_RedEnvelopeMessage_descriptor,
                new java.lang.String[] { "Id", "ReType", "Comment", "Style", });
        internal_static_ReceiveRedEnvelopeMessage_descriptor =
                getDescriptor().getMessageTypes().get(3);
        internal_static_ReceiveRedEnvelopeMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_ReceiveRedEnvelopeMessage_descriptor,
                new java.lang.String[] { "Id", });
        internal_static_TransferMessage_descriptor =
                getDescriptor().getMessageTypes().get(4);
        internal_static_TransferMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_TransferMessage_descriptor,
                new java.lang.String[] { "Id", "TransactionAmount", "Comment", });
        internal_static_StampMessage_descriptor =
                getDescriptor().getMessageTypes().get(5);
        internal_static_StampMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_StampMessage_descriptor,
                new java.lang.String[] { "Comment", });
        internal_static_BusinessCardMessage_descriptor =
                getDescriptor().getMessageTypes().get(6);
        internal_static_BusinessCardMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_BusinessCardMessage_descriptor,
                new java.lang.String[] { "Uid", "Avatar", "Nickname", "Comment", });
        internal_static_RequestFriendMessage_descriptor =
                getDescriptor().getMessageTypes().get(7);
        internal_static_RequestFriendMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_RequestFriendMessage_descriptor,
                new java.lang.String[] { "SayHi", });
        internal_static_AcceptBeFriendsMessage_descriptor =
                getDescriptor().getMessageTypes().get(8);
        internal_static_AcceptBeFriendsMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_AcceptBeFriendsMessage_descriptor,
                new java.lang.String[] { });
        internal_static_RemoveFriendMessage_descriptor =
                getDescriptor().getMessageTypes().get(9);
        internal_static_RemoveFriendMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_RemoveFriendMessage_descriptor,
                new java.lang.String[] { });
        internal_static_GroupNoticeMessage_descriptor =
                getDescriptor().getMessageTypes().get(10);
        internal_static_GroupNoticeMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_GroupNoticeMessage_descriptor,
                new java.lang.String[] { "Uid", "Avatar", "Nickname", });
        internal_static_InviteJoinGroupMessage_descriptor =
                getDescriptor().getMessageTypes().get(11);
        internal_static_InviteJoinGroupMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_InviteJoinGroupMessage_descriptor,
                new java.lang.String[] { "NoticeMessage", });
        internal_static_RequestGroupMessage_descriptor =
                getDescriptor().getMessageTypes().get(12);
        internal_static_RequestGroupMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_RequestGroupMessage_descriptor,
                new java.lang.String[] { "NoticeMessage", });
        internal_static_AcceptBeGroupMessage_descriptor =
                getDescriptor().getMessageTypes().get(13);
        internal_static_AcceptBeGroupMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_AcceptBeGroupMessage_descriptor,
                new java.lang.String[] { "NoticeMessage", });
        internal_static_RemoveGroupMemberMessage_descriptor =
                getDescriptor().getMessageTypes().get(14);
        internal_static_RemoveGroupMemberMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_RemoveGroupMemberMessage_descriptor,
                new java.lang.String[] { "Gid", "Avatar", "Name", });
        internal_static_ChangeGroupMasterMessage_descriptor =
                getDescriptor().getMessageTypes().get(15);
        internal_static_ChangeGroupMasterMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_ChangeGroupMasterMessage_descriptor,
                new java.lang.String[] { "Uid", });
        internal_static_ChangeGroupNameMessage_descriptor =
                getDescriptor().getMessageTypes().get(16);
        internal_static_ChangeGroupNameMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_ChangeGroupNameMessage_descriptor,
                new java.lang.String[] { "Name", });
        internal_static_ChangeGroupAnnouncementMessage_descriptor =
                getDescriptor().getMessageTypes().get(17);
        internal_static_ChangeGroupAnnouncementMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_ChangeGroupAnnouncementMessage_descriptor,
                new java.lang.String[] { "Announcement", });
        internal_static_DestroyGroupMessage_descriptor =
                getDescriptor().getMessageTypes().get(18);
        internal_static_DestroyGroupMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_DestroyGroupMessage_descriptor,
                new java.lang.String[] { "Uid", "Avatar", "Name", });
        internal_static_ConflictMessage_descriptor =
                getDescriptor().getMessageTypes().get(19);
        internal_static_ConflictMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_ConflictMessage_descriptor,
                new java.lang.String[] { });
        internal_static_OutGroupMessage_descriptor =
                getDescriptor().getMessageTypes().get(20);
        internal_static_OutGroupMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_OutGroupMessage_descriptor,
                new java.lang.String[] { "Gid", });
        internal_static_AckMessage_descriptor =
                getDescriptor().getMessageTypes().get(21);
        internal_static_AckMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_AckMessage_descriptor,
                new java.lang.String[] { "RejectType", "RequestId", "MsgId", "Timestamp", });
        internal_static_AuthRequestMessage_descriptor =
                getDescriptor().getMessageTypes().get(22);
        internal_static_AuthRequestMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_AuthRequestMessage_descriptor,
                new java.lang.String[] { "AccessToken", });
        internal_static_AuthResponseMessage_descriptor =
                getDescriptor().getMessageTypes().get(23);
        internal_static_AuthResponseMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_AuthResponseMessage_descriptor,
                new java.lang.String[] { "Accepted", });
        internal_static_UniversalMessage_descriptor =
                getDescriptor().getMessageTypes().get(24);
        internal_static_UniversalMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_UniversalMessage_descriptor,
                new java.lang.String[] { "RequestId", "ToUid", "WrapMsg", });
        internal_static_UniversalMessage_WrapMessage_descriptor =
                internal_static_UniversalMessage_descriptor.getNestedTypes().get(0);
        internal_static_UniversalMessage_WrapMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_UniversalMessage_WrapMessage_descriptor,
                new java.lang.String[] { "Timestamp", "MsgType", "MsgId", "FromUid", "Gid", "Nickname", "Avatar", "Chat", "Image", "RedEnvelope", "ReceiveRedEnvelope", "Transfer", "Stamp", "BusinessCard", "RequestFriend", "AcceptBeFriends", "RemoveFriend", "RequestGroup", "AcceptBeGroup", "RemoveGroupMember", "ChangeGroupMaster", "ChangeGroupName", "ChangeGroupAnnouncement", "DestroyGroup", "OutGroup", "Conflict", "RealMsg", });
    }

    // @@protoc_insertion_point(outer_class_scope)
}