/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.model.util.serializer.providers.bvalue;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.ballerinalang.model.types.BAnyType;
import org.ballerinalang.model.types.BAnydataType;
import org.ballerinalang.model.types.BArrayType;
import org.ballerinalang.model.types.BAttachedFunction;
import org.ballerinalang.model.types.BField;
import org.ballerinalang.model.types.BFunctionType;
import org.ballerinalang.model.types.BMapType;
import org.ballerinalang.model.types.BObjectType;
import org.ballerinalang.model.types.BRecordType;
import org.ballerinalang.model.types.BType;
import org.ballerinalang.model.types.BUnionType;
import org.ballerinalang.model.util.serializer.BPacket;
import org.ballerinalang.model.util.serializer.BValueDeserializer;
import org.ballerinalang.model.util.serializer.BValueSerializer;
import org.ballerinalang.model.util.serializer.SerializationBValueProvider;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.util.codegen.ObjectTypeInfo;
import org.ballerinalang.util.codegen.RecordTypeInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Contain SerializationBValueProviders for BType derived classes.
 *
 * @since 0.982.0
 */
public class BTypeBValueProviders {
    private static final String ELEM_TYPE = "elemType";
    private static final String PACKAGE_PATH = "packagePath";
    private static final String TYPE_NAME = "typeName";
    private static final String VALUE_CLASS = "valueClass";

    private BTypeBValueProviders() {
    }

    /**
     * Provide mapping between {@link BAnyType} and {@link BValue} representation of it.
     */
    public static class BAnyTypeBValueProvider implements SerializationBValueProvider<BAnyType> {

        @Override
        public Class<?> getType() {
            return BAnyType.class;
        }

        @Override
        public String typeName() {
            return getType().getName();
        }

        @Override
        public BPacket toBValue(BAnyType type, BValueSerializer serializer) {
            String packagePath = type.getPackagePath();
            String typeName = type.getName();
            BPacket packet = BPacket.from(typeName(), null);
            if (packagePath != null) {
                packet.put(PACKAGE_PATH, new BString(packagePath));
            }
            packet.put(TYPE_NAME, new BString(typeName));
            return packet;
        }

        @Override
        @SuppressWarnings("unchecked")
        public BAnyType toObject(BPacket packet, BValueDeserializer bValueDeserializer) {
            String typeName = packet.get(TYPE_NAME).stringValue();
            String pkgPath = null;
            BValue pkgP = packet.get(PACKAGE_PATH);
            if (pkgP != null) {
                pkgPath = pkgP.stringValue();
            }

            try {
                Constructor<BAnyType> constructor = BAnyType.class.getDeclaredConstructor(String.class, String.class);
                constructor.setAccessible(true);
                return constructor.newInstance(typeName, pkgPath);
            } catch (InstantiationException | IllegalAccessException |
                    InvocationTargetException | NoSuchMethodException e) {
                return null;
            }
        }
    }

    /**
     * Provide mapping between {@link BAnydataType} and {@link BValue} representation of it.
     */
    public static class BAnydataTypeBValueProvider implements SerializationBValueProvider<BAnydataType> {

        @Override
        public Class<?> getType() {
            return BAnydataType.class;
        }

        @Override
        public String typeName() {
            return getType().getName();
        }

        @Override
        public BPacket toBValue(BAnydataType type, BValueSerializer serializer) {
            String packagePath = type.getPackagePath();
            String typeName = type.getName();
            BPacket packet = BPacket.from(typeName(), null);
            if (packagePath != null) {
                packet.put(PACKAGE_PATH, new BString(packagePath));
            }
            packet.put(TYPE_NAME, new BString(typeName));
            return packet;
        }

        @Override
        @SuppressWarnings("unchecked")
        public BAnydataType toObject(BPacket packet, BValueDeserializer bValueDeserializer) {
            String typeName = packet.get(TYPE_NAME).stringValue();
            String pkgPath = null;
            BValue pkgP = packet.get(PACKAGE_PATH);
            if (pkgP != null) {
                pkgPath = pkgP.stringValue();
            }
            try {
                Constructor<BAnydataType> constructor =
                        BAnydataType.class.getDeclaredConstructor(String.class, String.class);
                constructor.setAccessible(true);
                return constructor.newInstance(typeName, pkgPath);
            } catch (InstantiationException | IllegalAccessException |
                    InvocationTargetException | NoSuchMethodException e) {
                return null;
            }
        }
    }

    /**
     * Provide mapping between {@link BArrayType} and {@link BValue} representation of it.
     */
    public static class BArrayTypeBValueProvider implements SerializationBValueProvider<BArrayType> {

        static final String DIMENSIONS = "dimensions";
        static final String SIZE = "size";

        @Override
        public Class<?> getType() {
            return BArrayType.class;
        }

        @Override
        public String typeName() {
            return getType().getName();
        }

        @Override
        public BPacket toBValue(BArrayType type, BValueSerializer serializer) {
            BType elementType = type.getElementType();
            int dimensions = type.getDimensions();
            int size = type.getSize();

            BPacket packet = BPacket.from(typeName(), null);
            packet.put(ELEM_TYPE, serializer.toBValue(elementType, null));
            packet.put(DIMENSIONS, new BInteger(dimensions));
            packet.put(SIZE, new BInteger(size));

            return packet;
        }

        @Override
        @SuppressWarnings("unchecked")
        public BArrayType toObject(BPacket packet, BValueDeserializer bValueDeserializer) {
            BType elemType = (BType) bValueDeserializer.deserialize(packet.get(ELEM_TYPE), BType.class);
            int size = (int) ((BInteger) packet.get(SIZE)).intValue();

            return new BArrayType(elemType, size);
        }
    }

    /**
     * Provide mapping between {@link BMapType} and {@link BValue} representation of it.
     */
    public static class BMapTypeBValueProvider implements SerializationBValueProvider<BMapType> {

        @Override
        public Class<?> getType() {
            return BMapType.class;
        }

        @Override
        public String typeName() {
            return getType().getName();
        }

        @Override
        public BPacket toBValue(BMapType type, BValueSerializer serializer) {
            BType elementType = type.getConstrainedType();
            BPacket packet = BPacket.from(typeName(), null);
            packet.put(ELEM_TYPE, serializer.toBValue(elementType, null));
            return packet;
        }

        @Override
        @SuppressWarnings("unchecked")
        public BMapType toObject(BPacket packet, BValueDeserializer bValueDeserializer) {
            BType elemType = (BType) bValueDeserializer.deserialize(packet.get(ELEM_TYPE), BType.class);
            BMapType bMapType = new BMapType(elemType);
            bValueDeserializer.addObjReference(packet.toBMap(), bMapType);
            return bMapType;
        }
    }

    /**
     * Provide mapping between {@link BObjectType} and {@link BValue} representation of it.
     */
    public static class BObjectTypeBValueProvider implements SerializationBValueProvider<BObjectType> {

        private static final String PACKAGE_PATH = "packagePath";
        private static final String TYPE_NAME = "typeName";
        private static final String FLAGS = "flags";
        private static final String FIELDS = "fields";

        private static final String ATTACHED_FUNCTIONS = "attachedFunctions";
        private static final String INITIALIZER = "initializer";
        private static final String DEFAULT_INIT = "defaultsValuesInitFunc";

        @Override
        public Class<?> getType() {
            return BObjectType.class;
        }

        @Override
        public String typeName() {
            return getType().getName();
        }

        @Override
        public BPacket toBValue(BObjectType objType, BValueSerializer serializer) {
            String packagePath = objType.getPackagePath();
            String typeName = objType.getName();
            int flags = objType.flags;
            BValue fields = serializer.toBValue(objType.getFields(), null);
            BValue attachedFunctions = serializer.toBValue(objType.getAttachedFunctions(), null);
            BValue initializer = serializer.toBValue(objType.initializer, null);
            BValue defaultsValuesInitFunc = serializer.toBValue(objType.defaultsValuesInitFunc, null);

            BPacket packet = BPacket.from(typeName(), null);
            packet.put(PACKAGE_PATH, new BString(packagePath));
            packet.put(TYPE_NAME, new BString(typeName));
            packet.put(FLAGS, new BInteger(flags));
            packet.put(FIELDS, fields);
            packet.put(ATTACHED_FUNCTIONS, attachedFunctions);
            packet.put(INITIALIZER, initializer);
            packet.put(DEFAULT_INIT, defaultsValuesInitFunc);

            return packet;
        }

        @SuppressWarnings("unchecked")
        @Override
        public BObjectType toObject(BPacket packet, BValueDeserializer bValueDeserializer) {
            String typeName = packet.get(TYPE_NAME).stringValue();
            String pkgPath = packet.get(PACKAGE_PATH).stringValue();
            int flags = (int) ((BInteger) packet.get(FLAGS)).intValue();

            ObjectTypeInfo objectTypeInfo = new ObjectTypeInfo();
            BObjectType bObjectType = new BObjectType(objectTypeInfo, typeName, pkgPath, flags);
            objectTypeInfo.setType(bObjectType);

            bValueDeserializer.addObjReference(packet.toBMap(), bObjectType);
            LinkedHashMap fields = (LinkedHashMap) bValueDeserializer
                    .deserialize(packet.get(FIELDS), LinkedHashMap.class);
            BAttachedFunction[] attachedFunctions = (BAttachedFunction[]) bValueDeserializer
                    .deserialize(packet.get(ATTACHED_FUNCTIONS), BAttachedFunction[].class);
            BAttachedFunction initializer = (BAttachedFunction) bValueDeserializer
                    .deserialize(packet.get(INITIALIZER), BAttachedFunction.class);
            BAttachedFunction defaultsValuesInitFunc = (BAttachedFunction) bValueDeserializer
                    .deserialize(packet.get(DEFAULT_INIT), BAttachedFunction.class);

            bObjectType.setFields(fields);
            bObjectType.setAttachedFunctions(attachedFunctions);
            bObjectType.initializer = initializer;
            bObjectType.defaultsValuesInitFunc = defaultsValuesInitFunc;
            return bObjectType;
        }
    }

    /**
     * Provide mapping between {@link BAttachedFunction} and {@link BValue} representation of it.
     */
    public static class BAttachedFunctionBValueProvider implements SerializationBValueProvider<BAttachedFunction> {

        private static final String FUNC_NAME = "funcName";
        private static final String FLAGS = "flags";

        @Override
        public Class<?> getType() {
            return BAttachedFunction.class;
        }

        @Override
        public String typeName() {
            return getType().getName();
        }

        @Override
        public BPacket toBValue(BAttachedFunction function, BValueSerializer serializer) {
            String funcName = function.funcName;
            int flags = function.flags;
            BType elementType = function.type;
            BValue fields = serializer.toBValue(elementType, null);

            BPacket packet = BPacket.from(typeName(), null);
            packet.put(FUNC_NAME, new BString(funcName));
            packet.put(FLAGS, new BInteger(flags));
            packet.put(ELEM_TYPE, fields);

            return packet;
        }

        @SuppressWarnings("unchecked")
        @Override
        public BAttachedFunction toObject(BPacket packet, BValueDeserializer bValueDeserializer) {
            String funcName = packet.get(FUNC_NAME).stringValue();
            int flags = (int) ((BInteger) packet.get(FLAGS)).intValue();
            BFunctionType elemType = (BFunctionType) bValueDeserializer.deserialize(packet.get(ELEM_TYPE), BType.class);

            BAttachedFunction function = new BAttachedFunction(funcName, elemType, flags);
            bValueDeserializer.addObjReference(packet.toBMap(), function);
            return function;
        }
    }

    /**
     * Provide mapping between {@link BRecordType} and {@link BValue} representation of it.
     */
    public static class BRecordTypeBValueProvider implements SerializationBValueProvider<BRecordType> {

        static final String REST_FIELD_SIGNATURE_CP_INDEX = "restFieldSignatureCPIndex";
        static final String REST_FIELD_TYPE_SIGNATURE = "restFieldTypeSignature";
        private static final String PACKAGE_PATH = "packagePath";
        private static final String TYPE_NAME = "typeName";
        private static final String FLAGS = "flags";
        private static final String REST_FIELD_TYPE = "restFieldType";
        private static final String CLOSED = "closed";

        @Override
        public Class<?> getType() {
            return BRecordType.class;
        }

        @Override
        public String typeName() {
            return getType().getName();
        }

        @Override
        public BPacket toBValue(BRecordType recType, BValueSerializer serializer) {
            String packagePath = recType.getPackagePath();
            String typeName = recType.getName();
            int flags = recType.flags;
            int restFieldSignatureCPIndex = recType.recordTypeInfo.getRestFieldSignatureCPIndex();
            String restFieldTypeSignature = recType.recordTypeInfo.getRestFieldTypeSignature();

            BPacket packet = BPacket.from(typeName(), serializer.toBValue(recType.getFields(), null));
            packet.putString(PACKAGE_PATH, packagePath);
            packet.putString(TYPE_NAME, typeName);
            packet.put(FLAGS, new BInteger(flags));
            packet.put(REST_FIELD_SIGNATURE_CP_INDEX, new BInteger(restFieldSignatureCPIndex));
            packet.putString(REST_FIELD_TYPE_SIGNATURE, restFieldTypeSignature);
            packet.put(CLOSED, new BBoolean(recType.sealed));
            packet.put(REST_FIELD_TYPE, serializer.toBValue(recType.restFieldType, null));
            return packet;
        }

        @SuppressWarnings("unchecked")
        @Override
        public BRecordType toObject(BPacket packet, BValueDeserializer bValueDeserializer) {
            String typeName = packet.get(TYPE_NAME).stringValue();
            String pkgPath = packet.get(PACKAGE_PATH).stringValue();
            int flags = (int) ((BInteger) packet.get(FLAGS)).intValue();
            int cpIndex = (int) ((BInteger) packet.get(REST_FIELD_SIGNATURE_CP_INDEX)).intValue();
            BBoolean closed = (BBoolean) packet.get(CLOSED);

            RecordTypeInfo recTypeInfo = new RecordTypeInfo();
            recTypeInfo.setRestFieldSignatureCPIndex(cpIndex);
            BValue restFieldTypeSig = packet.get(REST_FIELD_TYPE_SIGNATURE);
            if (restFieldTypeSig != null) {
                String typeSig = restFieldTypeSig.stringValue();
                recTypeInfo.setRestFieldTypeSignature(typeSig);
            }

            BRecordType bRecType = new BRecordType(recTypeInfo, typeName, pkgPath, flags);
            Map<String, BField> fields = (Map<String, BField>) bValueDeserializer.deserialize(packet.getValue(),
                                                                                              LinkedHashMap.class);
            bRecType.setFields(fields);

            recTypeInfo.setType(bRecType);
            bRecType.restFieldType = (BType) bValueDeserializer.deserialize(packet.get(REST_FIELD_TYPE), BType.class);
            bRecType.sealed = closed.booleanValue();

            return bRecType;
        }
    }

    /**
     * Provide mapping between {@link BUnionType} and {@link BValue} representation of it.
     */
    public static class BUnionTypeBValueProvider implements SerializationBValueProvider<BUnionType> {

        private static final String PACKAGE_PATH = "packagePath";
        private static final String TYPE_NAME = "typeName";
        private static final String IS_NULLABLE = "isNullable";
        private static final String MEMBER_TYPES = "memberTypes";

        @Override
        public Class<?> getType() {
            return BUnionType.class;
        }

        @Override
        public String typeName() {
            return getType().getName();
        }

        @Override
        public BPacket toBValue(BUnionType unionType, BValueSerializer serializer) {
            String packagePath = unionType.getPackagePath();
            String typeName = unionType.getName();
            boolean isNullable = unionType.isNullable();
            BValue memberTypes = serializer.toBValue(unionType.getMemberTypes(), null);

            BPacket packet = BPacket.from(typeName(), null);
            packet.put(PACKAGE_PATH, (packagePath != null) ? new BString(packagePath) : null);
            packet.put(TYPE_NAME, (typeName != null) ? new BString(typeName) : null);
            packet.put(IS_NULLABLE, new BBoolean(isNullable));
            packet.put(MEMBER_TYPES, memberTypes);

            return packet;
        }

        @SuppressWarnings("unchecked")
        @Override
        public BUnionType toObject(BPacket packet, BValueDeserializer bValueDeserializer) {
            String typeName = (packet.get(TYPE_NAME) != null) ? packet.get(TYPE_NAME).stringValue() : null;
            String pkgPath = (packet.get(PACKAGE_PATH) != null) ? packet.get(PACKAGE_PATH).stringValue() : null;
            boolean isNullable = ((BBoolean) packet.get(IS_NULLABLE)).booleanValue();
            List memberTypes = (ArrayList) bValueDeserializer.deserialize(packet.get(MEMBER_TYPES), List.class);

            try {
                BUnionType unionType = new BUnionType();
                FieldUtils.writeField(unionType, "typeName", typeName, true);
                FieldUtils.writeField(unionType, "pkgPath", pkgPath, true);
                FieldUtils.writeField(unionType, "nullable", isNullable, true);
                FieldUtils.writeField(unionType, "memberTypes", memberTypes, true);
                return unionType;
            } catch (IllegalAccessException e) {
                return null;
            }
        }
    }
}
