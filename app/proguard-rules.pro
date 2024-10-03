# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# keeping the below libs packages to prevent jni lib issues after enabling Proguard
-keep class com.ingenico.pclutilities.** {*;}
-keep class com.ingenico.pclservice.** {*;}
-keep class com.ingenico.rba_sdk.** {*;}
-keep class com.ingenico.rbasdk_android_adapter.** {*;}
-keep class org.simpleframework.** {*;}

-dontwarn org.xmlpull.v1.**
-keepattributes SourceFile,LineNumberTable
-keepattributes LocalVariableTable, LocalVariableTypeTable
-keepattributes *Annotation*, Signature, Exception

# keeing the log4j
-dontwarn org.apache.logging.log4j.**
-keep class org.apache.logging.log4j.** { *; }

# keeping the default constructors of all classes to prevent issues due to Reflection
-keepclassmembers class * {
public <init>(...);
}

# keeing the data classes for request and response data.
-keep class com.vantiv.triposmobilesdk.responses.** {*;}
-keep class com.vantiv.triposmobilesdk.requests.** {*;}

# keeping the static class members of heartbeat service
-keepclassmembers class com.vantiv.triposmobilesdk.heartbeat.CheckConnectionHeartbeatService {
    public static ** Companion;
}

# Keeping the enums and all the data classes extending FlowData, config, request, express message, exception,
-keep class * extends java.lang.Enum { *; }
-keep public class * extends com.vantiv.triposmobilesdk.express.Base {*;}
-keep public class * extends com.vantiv.triposmobilesdk.flows.FlowData {*;}
-keep public class * extends com.vantiv.triposmobilesdk.ConfigurationValidation {*;}
-keep public class * extends com.vantiv.triposmobilesdk.express.Message {*;}
-keep public class * extends com.vantiv.triposmobilesdk.requests.FinancialRequestBase {*;}
-keep public class com.vantiv.triposmobilesdk.CardVerificationData {*;}
-keep public class com.vantiv.triposmobilesdk.FinancialCardData {*;}
-keep public class com.vantiv.triposmobilesdk.EmvData {*;}
-keep public class com.vantiv.triposmobilesdk.CardVerificationData {*;}
-keep class com.vantiv.triposmobilesdk.exceptions.** {*;}

##Keeping receipt data
-keep public class com.vantiv.triposmobilesdk.ReceiptData {*;}
-keep public class com.vantiv.triposmobilesdk.ReceiptFooter {*;}
-keep public class com.vantiv.triposmobilesdk.ReceiptHeader {*;}


# keeping the data classes for store and forward functionality
-keep class com.vantiv.triposmobilesdk.storeandforward.StoredTransactionData {*;}
-keep class com.vantiv.triposmobilesdk.storeandforward.StoredTransactionRecord {*;}
-keep class com.vantiv.triposmobilesdk.flows.ManuallyForwardFlowData$StoredExpressRequestSenderFlowStepData { *; }