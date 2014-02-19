/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#pragma version(1)
#pragma rs java_package_name(android.renderscript.cts)

// Don't edit this file!  It is auto-generated by frameworks/rs/api/gen_runtime.

rs_allocation gAllocInMinValue;
rs_allocation gAllocInMaxValue;

float __attribute__((kernel)) testClampFloatFloatFloatFloat(float inValue, unsigned int x) {
    float inMinValue = rsGetElementAt_float(gAllocInMinValue, x);
    float inMaxValue = rsGetElementAt_float(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

float2 __attribute__((kernel)) testClampFloat2Float2Float2Float2(float2 inValue, unsigned int x) {
    float2 inMinValue = rsGetElementAt_float2(gAllocInMinValue, x);
    float2 inMaxValue = rsGetElementAt_float2(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

float3 __attribute__((kernel)) testClampFloat3Float3Float3Float3(float3 inValue, unsigned int x) {
    float3 inMinValue = rsGetElementAt_float3(gAllocInMinValue, x);
    float3 inMaxValue = rsGetElementAt_float3(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

float4 __attribute__((kernel)) testClampFloat4Float4Float4Float4(float4 inValue, unsigned int x) {
    float4 inMinValue = rsGetElementAt_float4(gAllocInMinValue, x);
    float4 inMaxValue = rsGetElementAt_float4(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

float2 __attribute__((kernel)) testClampFloat2FloatFloatFloat2(float2 inValue, unsigned int x) {
    float inMinValue = rsGetElementAt_float(gAllocInMinValue, x);
    float inMaxValue = rsGetElementAt_float(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

float3 __attribute__((kernel)) testClampFloat3FloatFloatFloat3(float3 inValue, unsigned int x) {
    float inMinValue = rsGetElementAt_float(gAllocInMinValue, x);
    float inMaxValue = rsGetElementAt_float(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

float4 __attribute__((kernel)) testClampFloat4FloatFloatFloat4(float4 inValue, unsigned int x) {
    float inMinValue = rsGetElementAt_float(gAllocInMinValue, x);
    float inMaxValue = rsGetElementAt_float(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

char __attribute__((kernel)) testClampCharCharCharChar(char inValue, unsigned int x) {
    char inMinValue = rsGetElementAt_char(gAllocInMinValue, x);
    char inMaxValue = rsGetElementAt_char(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

char2 __attribute__((kernel)) testClampChar2Char2Char2Char2(char2 inValue, unsigned int x) {
    char2 inMinValue = rsGetElementAt_char2(gAllocInMinValue, x);
    char2 inMaxValue = rsGetElementAt_char2(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

char3 __attribute__((kernel)) testClampChar3Char3Char3Char3(char3 inValue, unsigned int x) {
    char3 inMinValue = rsGetElementAt_char3(gAllocInMinValue, x);
    char3 inMaxValue = rsGetElementAt_char3(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

char4 __attribute__((kernel)) testClampChar4Char4Char4Char4(char4 inValue, unsigned int x) {
    char4 inMinValue = rsGetElementAt_char4(gAllocInMinValue, x);
    char4 inMaxValue = rsGetElementAt_char4(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

uchar __attribute__((kernel)) testClampUcharUcharUcharUchar(uchar inValue, unsigned int x) {
    uchar inMinValue = rsGetElementAt_uchar(gAllocInMinValue, x);
    uchar inMaxValue = rsGetElementAt_uchar(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

uchar2 __attribute__((kernel)) testClampUchar2Uchar2Uchar2Uchar2(uchar2 inValue, unsigned int x) {
    uchar2 inMinValue = rsGetElementAt_uchar2(gAllocInMinValue, x);
    uchar2 inMaxValue = rsGetElementAt_uchar2(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

uchar3 __attribute__((kernel)) testClampUchar3Uchar3Uchar3Uchar3(uchar3 inValue, unsigned int x) {
    uchar3 inMinValue = rsGetElementAt_uchar3(gAllocInMinValue, x);
    uchar3 inMaxValue = rsGetElementAt_uchar3(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

uchar4 __attribute__((kernel)) testClampUchar4Uchar4Uchar4Uchar4(uchar4 inValue, unsigned int x) {
    uchar4 inMinValue = rsGetElementAt_uchar4(gAllocInMinValue, x);
    uchar4 inMaxValue = rsGetElementAt_uchar4(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

short __attribute__((kernel)) testClampShortShortShortShort(short inValue, unsigned int x) {
    short inMinValue = rsGetElementAt_short(gAllocInMinValue, x);
    short inMaxValue = rsGetElementAt_short(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

short2 __attribute__((kernel)) testClampShort2Short2Short2Short2(short2 inValue, unsigned int x) {
    short2 inMinValue = rsGetElementAt_short2(gAllocInMinValue, x);
    short2 inMaxValue = rsGetElementAt_short2(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

short3 __attribute__((kernel)) testClampShort3Short3Short3Short3(short3 inValue, unsigned int x) {
    short3 inMinValue = rsGetElementAt_short3(gAllocInMinValue, x);
    short3 inMaxValue = rsGetElementAt_short3(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

short4 __attribute__((kernel)) testClampShort4Short4Short4Short4(short4 inValue, unsigned int x) {
    short4 inMinValue = rsGetElementAt_short4(gAllocInMinValue, x);
    short4 inMaxValue = rsGetElementAt_short4(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

ushort __attribute__((kernel)) testClampUshortUshortUshortUshort(ushort inValue, unsigned int x) {
    ushort inMinValue = rsGetElementAt_ushort(gAllocInMinValue, x);
    ushort inMaxValue = rsGetElementAt_ushort(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

ushort2 __attribute__((kernel)) testClampUshort2Ushort2Ushort2Ushort2(ushort2 inValue, unsigned int x) {
    ushort2 inMinValue = rsGetElementAt_ushort2(gAllocInMinValue, x);
    ushort2 inMaxValue = rsGetElementAt_ushort2(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

ushort3 __attribute__((kernel)) testClampUshort3Ushort3Ushort3Ushort3(ushort3 inValue, unsigned int x) {
    ushort3 inMinValue = rsGetElementAt_ushort3(gAllocInMinValue, x);
    ushort3 inMaxValue = rsGetElementAt_ushort3(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

ushort4 __attribute__((kernel)) testClampUshort4Ushort4Ushort4Ushort4(ushort4 inValue, unsigned int x) {
    ushort4 inMinValue = rsGetElementAt_ushort4(gAllocInMinValue, x);
    ushort4 inMaxValue = rsGetElementAt_ushort4(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

int __attribute__((kernel)) testClampIntIntIntInt(int inValue, unsigned int x) {
    int inMinValue = rsGetElementAt_int(gAllocInMinValue, x);
    int inMaxValue = rsGetElementAt_int(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

int2 __attribute__((kernel)) testClampInt2Int2Int2Int2(int2 inValue, unsigned int x) {
    int2 inMinValue = rsGetElementAt_int2(gAllocInMinValue, x);
    int2 inMaxValue = rsGetElementAt_int2(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

int3 __attribute__((kernel)) testClampInt3Int3Int3Int3(int3 inValue, unsigned int x) {
    int3 inMinValue = rsGetElementAt_int3(gAllocInMinValue, x);
    int3 inMaxValue = rsGetElementAt_int3(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

int4 __attribute__((kernel)) testClampInt4Int4Int4Int4(int4 inValue, unsigned int x) {
    int4 inMinValue = rsGetElementAt_int4(gAllocInMinValue, x);
    int4 inMaxValue = rsGetElementAt_int4(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

uint __attribute__((kernel)) testClampUintUintUintUint(uint inValue, unsigned int x) {
    uint inMinValue = rsGetElementAt_uint(gAllocInMinValue, x);
    uint inMaxValue = rsGetElementAt_uint(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

uint2 __attribute__((kernel)) testClampUint2Uint2Uint2Uint2(uint2 inValue, unsigned int x) {
    uint2 inMinValue = rsGetElementAt_uint2(gAllocInMinValue, x);
    uint2 inMaxValue = rsGetElementAt_uint2(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

uint3 __attribute__((kernel)) testClampUint3Uint3Uint3Uint3(uint3 inValue, unsigned int x) {
    uint3 inMinValue = rsGetElementAt_uint3(gAllocInMinValue, x);
    uint3 inMaxValue = rsGetElementAt_uint3(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

uint4 __attribute__((kernel)) testClampUint4Uint4Uint4Uint4(uint4 inValue, unsigned int x) {
    uint4 inMinValue = rsGetElementAt_uint4(gAllocInMinValue, x);
    uint4 inMaxValue = rsGetElementAt_uint4(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

char2 __attribute__((kernel)) testClampChar2CharCharChar2(char2 inValue, unsigned int x) {
    char inMinValue = rsGetElementAt_char(gAllocInMinValue, x);
    char inMaxValue = rsGetElementAt_char(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

char3 __attribute__((kernel)) testClampChar3CharCharChar3(char3 inValue, unsigned int x) {
    char inMinValue = rsGetElementAt_char(gAllocInMinValue, x);
    char inMaxValue = rsGetElementAt_char(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

char4 __attribute__((kernel)) testClampChar4CharCharChar4(char4 inValue, unsigned int x) {
    char inMinValue = rsGetElementAt_char(gAllocInMinValue, x);
    char inMaxValue = rsGetElementAt_char(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

uchar2 __attribute__((kernel)) testClampUchar2UcharUcharUchar2(uchar2 inValue, unsigned int x) {
    uchar inMinValue = rsGetElementAt_uchar(gAllocInMinValue, x);
    uchar inMaxValue = rsGetElementAt_uchar(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

uchar3 __attribute__((kernel)) testClampUchar3UcharUcharUchar3(uchar3 inValue, unsigned int x) {
    uchar inMinValue = rsGetElementAt_uchar(gAllocInMinValue, x);
    uchar inMaxValue = rsGetElementAt_uchar(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

uchar4 __attribute__((kernel)) testClampUchar4UcharUcharUchar4(uchar4 inValue, unsigned int x) {
    uchar inMinValue = rsGetElementAt_uchar(gAllocInMinValue, x);
    uchar inMaxValue = rsGetElementAt_uchar(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

short2 __attribute__((kernel)) testClampShort2ShortShortShort2(short2 inValue, unsigned int x) {
    short inMinValue = rsGetElementAt_short(gAllocInMinValue, x);
    short inMaxValue = rsGetElementAt_short(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

short3 __attribute__((kernel)) testClampShort3ShortShortShort3(short3 inValue, unsigned int x) {
    short inMinValue = rsGetElementAt_short(gAllocInMinValue, x);
    short inMaxValue = rsGetElementAt_short(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

short4 __attribute__((kernel)) testClampShort4ShortShortShort4(short4 inValue, unsigned int x) {
    short inMinValue = rsGetElementAt_short(gAllocInMinValue, x);
    short inMaxValue = rsGetElementAt_short(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

ushort2 __attribute__((kernel)) testClampUshort2UshortUshortUshort2(ushort2 inValue, unsigned int x) {
    ushort inMinValue = rsGetElementAt_ushort(gAllocInMinValue, x);
    ushort inMaxValue = rsGetElementAt_ushort(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

ushort3 __attribute__((kernel)) testClampUshort3UshortUshortUshort3(ushort3 inValue, unsigned int x) {
    ushort inMinValue = rsGetElementAt_ushort(gAllocInMinValue, x);
    ushort inMaxValue = rsGetElementAt_ushort(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

ushort4 __attribute__((kernel)) testClampUshort4UshortUshortUshort4(ushort4 inValue, unsigned int x) {
    ushort inMinValue = rsGetElementAt_ushort(gAllocInMinValue, x);
    ushort inMaxValue = rsGetElementAt_ushort(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

int2 __attribute__((kernel)) testClampInt2IntIntInt2(int2 inValue, unsigned int x) {
    int inMinValue = rsGetElementAt_int(gAllocInMinValue, x);
    int inMaxValue = rsGetElementAt_int(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

int3 __attribute__((kernel)) testClampInt3IntIntInt3(int3 inValue, unsigned int x) {
    int inMinValue = rsGetElementAt_int(gAllocInMinValue, x);
    int inMaxValue = rsGetElementAt_int(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

int4 __attribute__((kernel)) testClampInt4IntIntInt4(int4 inValue, unsigned int x) {
    int inMinValue = rsGetElementAt_int(gAllocInMinValue, x);
    int inMaxValue = rsGetElementAt_int(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

uint2 __attribute__((kernel)) testClampUint2UintUintUint2(uint2 inValue, unsigned int x) {
    uint inMinValue = rsGetElementAt_uint(gAllocInMinValue, x);
    uint inMaxValue = rsGetElementAt_uint(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

uint3 __attribute__((kernel)) testClampUint3UintUintUint3(uint3 inValue, unsigned int x) {
    uint inMinValue = rsGetElementAt_uint(gAllocInMinValue, x);
    uint inMaxValue = rsGetElementAt_uint(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}

uint4 __attribute__((kernel)) testClampUint4UintUintUint4(uint4 inValue, unsigned int x) {
    uint inMinValue = rsGetElementAt_uint(gAllocInMinValue, x);
    uint inMaxValue = rsGetElementAt_uint(gAllocInMaxValue, x);
    return clamp(inValue, inMinValue, inMaxValue);
}