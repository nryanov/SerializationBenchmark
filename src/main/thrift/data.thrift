namespace java thriftBenchmark.java
#@namespace scala thriftBenchmark.scala

typedef i32 int
typedef i64 long

struct MixedData {
 1:optional string f1,
 2:optional double f2,
 3:optional long f3,
 4:optional int f4,
 5:optional string f5,
 6:optional double f6,
 7:optional long f7,
 8:optional int f8,
 9:optional int f9,
 10:optional long f10,
 11:optional double f11,
 12:optional double f12,
 13:optional string f13,
 14:optional string f14,
 15:optional long f15,
 16:optional int f16,
 17:optional int f17,
 18:optional string f18,
 19:optional string f19,
 20:optional string f20,
}

struct OnlyStrings {
 1:optional string f1,
 2:optional string f2,
 3:optional string f3,
 4:optional string f4,
 5:optional string f5,
 6:optional string f6,
 7:optional string f7,
 8:optional string f8,
 9:optional string f9,
 10:optional string f10,
 11:optional string f11,
 12:optional string f12,
 13:optional string f13,
 14:optional string f14,
 15:optional string f15,
 16:optional string f16,
 17:optional string f17,
 18:optional string f18,
 19:optional string f19,
 20:optional string f20,
}

struct OnlyLongs {
 1:optional long f1,
 2:optional long f2,
 3:optional long f3,
 4:optional long f4,
 5:optional long f5,
 6:optional long f6,
 7:optional long f7,
 8:optional long f8,
 9:optional long f9,
 10:optional long f10,
 11:optional long f11,
 12:optional long f12,
 13:optional long f13,
 14:optional long f14,
 15:optional long f15,
 16:optional long f16,
 17:optional long f17,
 18:optional long f18,
 19:optional long f19,
 20:optional long f20,
}